package jp.co.soliton.keymanager.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.*;
import android.widget.*;
import jp.co.soliton.keymanager.LogCtrl;
import jp.co.soliton.keymanager.R;
import jp.co.soliton.keymanager.StringList;
import jp.co.soliton.keymanager.activity.SettingTabletActivity;
import jp.co.soliton.keymanager.alarm.AlarmReceiver;
import jp.co.soliton.keymanager.common.CommonUtils;
import jp.co.soliton.keymanager.common.DateUtils;
import jp.co.soliton.keymanager.common.SoftKeyboardCtrl;
import jp.co.soliton.keymanager.common.DaysBeforeNotifEditText;
import jp.co.soliton.keymanager.customview.DialogMessageTablet;
import jp.co.soliton.keymanager.dbalias.ElementApply;
import jp.co.soliton.keymanager.dbalias.ElementApplyManager;

import java.util.Calendar;
import java.util.Date;

import static jp.co.soliton.keymanager.activity.SettingTabletActivity.STATUS_NOTIFICATION_ALL;
import static jp.co.soliton.keymanager.activity.SettingTabletActivity.STATUS_NOTIFICATION_ONE;

/**
 * Created by nguyenducdat on 4/25/2017.
 */

public class ContentNotificationSettingFragment extends TabletBaseSettingFragment implements CompoundButton
		.OnCheckedChangeListener, SoftKeyboardCtrl.DetectsListenner{

	public enum NotifModeEnum {
		ALL, ONE;
	}
	private static final int MAX_BEFORE_DATE = 120;
	private static final int MIN_BEFORE_DATE = 1;
	private Switch swNotifFlag;
	private Switch swNotifBeforeFlag;
	private DaysBeforeNotifEditText tvNotifBefore;
	private Button btnDayBeforeMinus;
	private Button btnDayBeforePlus;

	private NotifModeEnum mode;
	private String idCert;
	private ElementApplyManager elementMgr;
	private String numDateNotifBefore = "";
	private boolean isShowingKeyboard;

	/**
	 * For Notification All
	 * @return
	 */
	public static Fragment newInstance() {
		ContentNotificationSettingFragment f = new ContentNotificationSettingFragment();
		f.mode = NotifModeEnum.ALL;
		return f;
	}

	/**
	 * For Notificate One
	 * @param idCert
	 * @return
	 */
	public static Fragment newInstance(String idCert) {
		ContentNotificationSettingFragment f = new ContentNotificationSettingFragment();
		f.idCert = idCert;
		f.mode = NotifModeEnum.ONE;
		return f;
	}

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		viewFragment = inflater.inflate(R.layout.fragment_notification_setting_tablet, container, false);
		tvTitleHeader = (TextView) viewFragment.findViewById(R.id.tvTitleHeader);
		tvTitleHeader.setText(getString(R.string.notif_setting));
		textViewBack = (TextView) viewFragment.findViewById(R.id.textViewBack);
		swNotifFlag = (Switch) viewFragment.findViewById(R.id.swNotifFlag);
		swNotifBeforeFlag = (Switch) viewFragment.findViewById(R.id.swNotifBeforeFlag);
		tvNotifBefore = (DaysBeforeNotifEditText) viewFragment.findViewById(R.id.tvNotifBefore);
		btnDayBeforeMinus = (Button) viewFragment.findViewById(R.id.btnDayBeforeMinus);
		btnDayBeforePlus = (Button) viewFragment.findViewById(R.id.btnDayBeforePlus);
		elementMgr = new ElementApplyManager(getActivity());
		viewFragment.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				return SoftKeyboardCtrl.hideKeyboardIfTouchOutEditText(getActivity(), event);
			}
		});
		SoftKeyboardCtrl.addListenner(viewFragment, this);
		return viewFragment;
	}

	@Override
	public void onResume() {
		super.onResume();
		setupControl();
		swNotifFlag.setOnCheckedChangeListener(this);
		swNotifBeforeFlag.setOnCheckedChangeListener(this);
		tvNotifBefore.setOnKeyboardHidden(new DaysBeforeNotifEditText.OnKeyboardHidden() {
			@Override
			public void onKeyboardHidden() {
				View v = getActivity().getCurrentFocus();
				if (v != null && v instanceof EditText && !v.getClass().getName().startsWith("android.webkit.")) {
					v.clearFocus();
				}
			}
		});
	}

	@Override
	protected void setTextBtnBack() {
		if (NotifModeEnum.ONE == mode) {
			textViewBack.setText(R.string.back);
		}else {
			textViewBack.setText(R.string.label_setting);
		}
	}

	public void setupControl() {
		if (NotifModeEnum.ONE == mode) {
			ElementApply element = elementMgr.getElementApply(idCert);
			swNotifFlag.setChecked(element.isNotiEnableFlag());
			swNotifBeforeFlag.setChecked(element.isNotiEnableBeforeFlag());
			tvNotifBefore.setText(String.valueOf(element.getNotiEnableBefore()));
			try {
				Calendar cal = Calendar.getInstance();
				Date expirationDate = DateUtils.convertSringToDateSystemTime(element.getExpirationDate());
				cal.setTime(expirationDate);
				if (Calendar.getInstance().getTime().after(cal.getTime())) {
					swNotifFlag.setEnabled(false);
					swNotifBeforeFlag.setEnabled(false);
					updateEnableViewExpired(false);
				}else {
					swNotifFlag.setEnabled(true);
					swNotifBeforeFlag.setEnabled(true);
					updateEnableViewExpired(swNotifBeforeFlag.isChecked());
				}
			} catch (Exception ex) {
				LogCtrl.getInstance(getActivity()).loggerError(ex.toString());
			}
		} else {
			swNotifFlag.setChecked(CommonUtils.getPrefBoolean(getActivity(), StringList.KEY_NOTIF_ENABLE_FLAG));
			swNotifBeforeFlag.setChecked(CommonUtils.getPrefBoolean(getActivity(), StringList.KEY_NOTIF_ENABLE_BEFORE_FLAG));
			tvNotifBefore.setText(String.valueOf(CommonUtils.getPrefIntegerWithDefaultValue(getActivity(), StringList
					.KEY_NOTIF_ENABLE_BEFORE, 14)));

			updateEnableViewExpired(swNotifBeforeFlag.isChecked());
		}

		btnDayBeforePlus.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (Integer.parseInt(getTextNotifBefore()) == MAX_BEFORE_DATE) {
					return;
				}
				tvNotifBefore.setText(String.valueOf(CommonUtils.toInt(getTextNotifBefore()) + 1));
				btnSaveNotifClick();
			}
		});
		btnDayBeforeMinus.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (Integer.parseInt(getTextNotifBefore()) == MIN_BEFORE_DATE) {
					return;
				}
				tvNotifBefore.setText(String.valueOf(CommonUtils.toInt(getTextNotifBefore()) - 1));
				btnSaveNotifClick();
			}
		});

		tvNotifBefore.setOnFocusChangeListener(new View.OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (hasFocus) {
					numDateNotifBefore = tvNotifBefore.getText().toString();
				} else {
					SoftKeyboardCtrl.hideKeyboard(getActivity());
					if (!numDateNotifBefore.equalsIgnoreCase(tvNotifBefore.getText().toString())) {
						btnSaveNotifClick();
					}
				}
			}
		});
		tvNotifBefore.setOnKeyListener(new View.OnKeyListener() {
			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if (event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
					tvNotifBefore.clearFocus();
				}
				return false;
			}
		});
	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		if (buttonView == swNotifBeforeFlag) {
			final int oldValue = CommonUtils.toInt(tvNotifBefore.getText().toString());
			if (!isChecked) {
				if (CommonUtils.isEmpty(getTextNotifBefore()) ||
						!CommonUtils.isNumber(getTextNotifBefore())) {
					tvNotifBefore.setText(oldValue);
				}
				if (CommonUtils.toInt(getTextNotifBefore()) <= 0 ||
						CommonUtils.toInt(getTextNotifBefore()) > MAX_BEFORE_DATE) {
					tvNotifBefore.setText(String.valueOf(oldValue));
				}
			}
			updateEnableViewExpired(isChecked);
		}
		btnSaveNotifClick();
	}

	public void btnSaveNotifClick() {
		int currentStatus = ((SettingTabletActivity)getActivity()).getCurrentStatus();
		if (currentStatus != STATUS_NOTIFICATION_ONE && currentStatus != STATUS_NOTIFICATION_ALL) {
			return;
		}
		if (!isValidateInput()) {
			return;
		}
		if (NotifModeEnum.ONE == mode) {
			elementMgr.updateNotifSettingElement(swNotifFlag.isChecked(), swNotifBeforeFlag.isChecked(),
					CommonUtils.toInt(getTextNotifBefore()), idCert);
		} else {
			CommonUtils.putPref(getActivity(), StringList.KEY_NOTIF_ENABLE_FLAG,
					new Boolean(swNotifFlag.isChecked()));
			CommonUtils.putPref(getActivity(), StringList.KEY_NOTIF_ENABLE_BEFORE_FLAG,
					new Boolean(swNotifBeforeFlag.isChecked()));
			CommonUtils.putPref(getActivity(), StringList.KEY_NOTIF_ENABLE_BEFORE,
					new Integer(CommonUtils.toInt(getTextNotifBefore())));
		}
		AlarmReceiver alarm = new AlarmReceiver();
		alarm.setupNotification(getActivity());
	}

	private boolean isValidateInput() {
		if (!swNotifBeforeFlag.isChecked()) {
			return true;
		}
		if (CommonUtils.isEmpty(getTextNotifBefore()) || !CommonUtils.isNumber(getTextNotifBefore())) {
			showMessage(makeMsgNotRangeExpiry(), getString(R.string.error), new DialogMessageTablet.OnOkDismissMessageListener() {
				@Override
				public void onOkDismissMessage() {
					tvNotifBefore.setText(numDateNotifBefore);
					btnSaveNotifClick();
				}
			});
			return false;
		}
		if (CommonUtils.toInt(getTextNotifBefore()) <= 0) {
			showMessage(makeMsgNotRangeExpiry(), getString(R.string.error), new DialogMessageTablet.OnOkDismissMessageListener() {
				@Override
				public void onOkDismissMessage() {
					tvNotifBefore.setText(String.valueOf(MIN_BEFORE_DATE));
					btnSaveNotifClick();
				}
			});
			return false;
		}
		if (CommonUtils.toInt(getTextNotifBefore()) > MAX_BEFORE_DATE) {
			showMessage(makeMsgNotRangeExpiry(), getString(R.string.error), new DialogMessageTablet.OnOkDismissMessageListener() {
				@Override
				public void onOkDismissMessage() {
					tvNotifBefore.setText(String.valueOf(MAX_BEFORE_DATE));
					btnSaveNotifClick();
				}
			});
			return false;
		}
		return true;
	}

	private String makeMsgNotRangeExpiry() {
		return String.format(getString(R.string.error_input_before_expiry), MAX_BEFORE_DATE);
	}

	/**
	 * Show message
	 *
	 * @param message
	 */
	private void showMessage(String message, String titleDialog, DialogMessageTablet.OnOkDismissMessageListener listener) {
		DialogMessageTablet dlgMessage = new DialogMessageTablet(getActivity(), message);
		dlgMessage.setOnOkDismissMessageListener(listener);
		dlgMessage.setTitleDialog(titleDialog);
		dlgMessage.show();
	}

	private void updateEnableViewExpired(boolean isChecked) {
		tvNotifBefore.setEnabled(isChecked);
		btnDayBeforeMinus.setEnabled(isChecked);
		btnDayBeforePlus.setEnabled(isChecked);
	}

	@NonNull
	private String getTextNotifBefore() {
		return tvNotifBefore.getText().toString().trim();
	}

	@Override
	public void onSoftKeyboardShown(boolean isShowing) {
		if (!isShowing) {
			if (isShowingKeyboard) {
				View v = getActivity().getCurrentFocus();
				if (v != null && v instanceof EditText && !v.getClass().getName().startsWith("android.webkit.")) {
					v.clearFocus();
				}
				isShowingKeyboard = false;
			}
		} else {
			isShowingKeyboard = true;
		}
	}
}
