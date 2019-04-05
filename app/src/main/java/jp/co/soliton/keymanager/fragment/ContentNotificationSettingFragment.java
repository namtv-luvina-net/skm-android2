package jp.co.soliton.keymanager.fragment;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import jp.co.soliton.keymanager.LogCtrl;
import jp.co.soliton.keymanager.R;
import jp.co.soliton.keymanager.StringList;
import jp.co.soliton.keymanager.activity.SettingTabletActivity;
import jp.co.soliton.keymanager.alarm.AlarmReceiver;
import jp.co.soliton.keymanager.common.CommonUtils;
import jp.co.soliton.keymanager.common.DateUtils;
import jp.co.soliton.keymanager.common.SoftKeyboardCtrl;
import jp.co.soliton.keymanager.customview.DialogPicker;
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
		.OnCheckedChangeListener {

	public enum NotifModeEnum {
		ALL, ONE
	}

	private static final int MAX_BEFORE_DATE = 120;
	private static final int MIN_BEFORE_DATE = 1;
	private Switch swNotifFlag;
	private Switch swNotifBeforeFlag;
	private TextView tvNotifBefore;

	private NotifModeEnum mode;
	private String idCert;
	private ElementApplyManager elementMgr;
	int notifyBeforeCurrent = 1;
	private TextView btnSettingProductInfo;
	private RelativeLayout rlExpired;
	private AlarmReceiver alarm;

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
		tvTitleHeader = viewFragment.findViewById(R.id.tvTitleHeader);
		tvTitleHeader.setText(getString(R.string.notif_setting));
		textViewBack = viewFragment.findViewById(R.id.textViewBack);
		swNotifFlag = viewFragment.findViewById(R.id.swNotifFlag);
		swNotifBeforeFlag = viewFragment.findViewById(R.id.swNotifBeforeFlag);
		tvNotifBefore = viewFragment.findViewById(R.id.tvNotifBefore);
		rlExpired = viewFragment.findViewById(R.id.rl_expired);
		btnSettingProductInfo = viewFragment.findViewById(R.id.btnSettingProductInfo);
		elementMgr = ElementApplyManager.getInstance(getActivity());
		viewFragment.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				return SoftKeyboardCtrl.hideKeyboardIfTouchOutEditText(getActivity(), event);
			}
		});
		return viewFragment;
	}

	@Override
	public void onResume() {
		super.onResume();
		btnSettingProductInfo.setText(getString(R.string.label_expired) + " (" + getString(R.string.label_day_before) +
				")");
		setupControl();
		swNotifFlag.setOnCheckedChangeListener(this);
		swNotifBeforeFlag.setOnCheckedChangeListener(this);
	}

	@Override
	protected void setTextBtnBack() {
		if (NotifModeEnum.ONE == mode) {
			textViewBack.setText(R.string.back);
		} else {
			textViewBack.setText(R.string.label_setting);
		}
	}

	public void setupControl() {
		if (NotifModeEnum.ONE == mode) {
			ElementApply element = elementMgr.getElementApply(idCert);
			swNotifFlag.setChecked(element.isNotiEnableFlag());
			swNotifBeforeFlag.setChecked(element.isNotiEnableBeforeFlag());
			notifyBeforeCurrent = element.getNotiEnableBefore();
			tvNotifBefore.setText(String.valueOf(notifyBeforeCurrent));
			try {
				Calendar cal = Calendar.getInstance();
				Date expirationDate = DateUtils.convertSringToDateSystemTime(element.getExpirationDate());
				cal.setTime(expirationDate);
				if (Calendar.getInstance().getTime().after(cal.getTime())) {
					swNotifFlag.setEnabled(false);
					swNotifBeforeFlag.setEnabled(false);
					updateEnableViewExpired(false);
				} else {
					swNotifFlag.setEnabled(true);
					swNotifBeforeFlag.setEnabled(true);
					updateEnableViewExpired(swNotifBeforeFlag.isChecked());
				}
			} catch (Exception ex) {
				LogCtrl.getInstance().error(ex.toString());
			}
		} else {
			swNotifFlag.setChecked(CommonUtils.getPrefBoolean(getActivity(), StringList.KEY_NOTIF_ENABLE_FLAG));
			swNotifBeforeFlag.setChecked(CommonUtils.getPrefBoolean(getActivity(), StringList
					.KEY_NOTIF_ENABLE_BEFORE_FLAG));
			notifyBeforeCurrent = CommonUtils.getPrefIntegerWithDefaultValue(getActivity(), StringList
					.KEY_NOTIF_ENABLE_BEFORE, 14);
			tvNotifBefore.setText(String.valueOf(notifyBeforeCurrent));
			updateEnableViewExpired(swNotifBeforeFlag.isChecked());
		}

		rlExpired.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				showPickerChangeTimeNotify();
			}
		});
	}

	private void showPickerChangeTimeNotify() {
		DialogPicker dialogPicker = new DialogPicker(getActivity(), notifyBeforeCurrent, new DialogPicker
				.ClickListener() {
			@Override
			public void clickApply(int newValue) {
				notifyBeforeCurrent = newValue;
				tvNotifBefore.setText(String.valueOf(newValue));
				btnSaveNotifClick();
			}
		});
		dialogPicker.show();
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
		updateNotification(buttonView, isChecked);
	}

	private void updateNotification(CompoundButton buttonView, boolean isChecked) {
		if (NotifModeEnum.ONE != mode) {
			return;
		}
		if (alarm == null) {
			alarm = new AlarmReceiver();
		}
		if (buttonView == swNotifBeforeFlag) {
			if (!isChecked) {
				alarm.removeAlarmBefore(getActivity().getApplicationContext(), idCert);
			} else {
				ElementApply elementApply = elementMgr.getElementApply(idCert);
				alarm.addAlarmBeforeIfNeed(getActivity().getApplicationContext(), elementApply);
			}
		} else {
			if (!isChecked) {
				alarm.removeAlarmExpired(getActivity().getApplicationContext(), idCert);
			} else {
				ElementApply elementApply = elementMgr.getElementApply(idCert);
				alarm.addAlarmExpiredIfNeed(getActivity().getApplicationContext(), elementApply);
			}
		}
	}

	public void btnSaveNotifClick() {
		int currentStatus = ((SettingTabletActivity) getActivity()).getCurrentStatus();
		if (currentStatus != STATUS_NOTIFICATION_ONE && currentStatus != STATUS_NOTIFICATION_ALL) {
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
	}

	private void updateEnableViewExpired(boolean isChecked) {
		rlExpired.setEnabled(isChecked);
		tvNotifBefore.setEnabled(isChecked);
		if (isChecked) {
			btnSettingProductInfo.setTextColor(getResources().getColor(R.color.color_black));
			tvNotifBefore.setTextColor(getResources().getColor(R.color.color_black));
		} else {
			btnSettingProductInfo.setTextColor(getResources().getColor(R.color.color_title_menu_50));
			tvNotifBefore.setTextColor(getResources().getColor(R.color.color_title_menu_50));
		}
	}

	@NonNull
	private String getTextNotifBefore() {
		return tvNotifBefore.getText().toString().trim();
	}
}
