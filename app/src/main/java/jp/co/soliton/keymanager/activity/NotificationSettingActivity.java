package jp.co.soliton.keymanager.activity;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.*;
import jp.co.soliton.keymanager.LogCtrl;
import jp.co.soliton.keymanager.R;
import jp.co.soliton.keymanager.StringList;
import jp.co.soliton.keymanager.alarm.AlarmReceiver;
import jp.co.soliton.keymanager.common.CommonUtils;
import jp.co.soliton.keymanager.common.DateUtils;
import jp.co.soliton.keymanager.common.SoftKeyboardCtrl;
import jp.co.soliton.keymanager.customview.DialogApplyMessage;
import jp.co.soliton.keymanager.customview.ExpiredTimeNotifyBottomSheetDialog;
import jp.co.soliton.keymanager.dbalias.ElementApply;
import jp.co.soliton.keymanager.dbalias.ElementApplyManager;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by luongdolong on 3/31/2017.
 */

public class NotificationSettingActivity extends BaseSettingPhoneActivity implements CompoundButton.OnCheckedChangeListener {
    public static final String KEY_NOTIF_MODE = "KEY_NOTIF_MODE";
    public enum NotifModeEnum {
        ALL, ONE;
    }
    private Switch swNotifFlag;
    private Switch swNotifBeforeFlag;
    private TextView tvNotifBefore;

    private NotifModeEnum mode;
    private String idCert;
    private ElementApplyManager elementMgr;
    private static final int MAX_BEFORE_DATE = 120;
    private static final int MIN_BEFORE_DATE = 1;
	String numDateNotifBefore = "";
	int notifyBeforeCurrent = 1;
	private TextView btnSettingProductInfo;
	private RelativeLayout rlExpired;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification_setting);
        if (getIntent().hasExtra(KEY_NOTIF_MODE)) {
            mode = (NotifModeEnum) getIntent().getSerializableExtra(KEY_NOTIF_MODE);
        } else {
            mode = NotifModeEnum.ALL;
        }
        if (NotifModeEnum.ONE == mode) {
            idCert = getIntent().getStringExtra(StringList.ELEMENT_APPLY_ID);
        }
        swNotifFlag = findViewById(R.id.swNotifFlag);
        swNotifBeforeFlag = findViewById(R.id.swNotifBeforeFlag);
        tvNotifBefore = findViewById(R.id.tvNotifBefore);
	    btnSettingProductInfo = findViewById(R.id.btnSettingProductInfo);
	    rlExpired = findViewById(R.id.rl_expired);
        elementMgr = ElementApplyManager.getInstance(this);
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

    @Override
    protected void onResume() {
        super.onResume();
	    btnSettingProductInfo.setText(getString(R.string.label_expired) +" (" + getString(R.string.label_day_before) +")");
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

	@Override
	protected void setTextTitle() {
		tvTitleHeader.setText(getString(R.string.notif_setting));
	}

	public void btnSaveNotifClick() {
        if (!isValidateInput()) {
            return;
        }
        if (NotifModeEnum.ONE == mode) {
	        elementMgr.updateNotifSettingElement(swNotifFlag.isChecked(), swNotifBeforeFlag.isChecked(),
                    CommonUtils.toInt(getTextNotifBefore()), idCert);
        } else {
            CommonUtils.putPref(getApplicationContext(), StringList.KEY_NOTIF_ENABLE_FLAG,
                    new Boolean(swNotifFlag.isChecked()));
            CommonUtils.putPref(getApplicationContext(), StringList.KEY_NOTIF_ENABLE_BEFORE_FLAG,
                    new Boolean(swNotifBeforeFlag.isChecked()));
            CommonUtils.putPref(getApplicationContext(), StringList.KEY_NOTIF_ENABLE_BEFORE,
                    new Integer(CommonUtils.toInt(getTextNotifBefore())));

            //elementMgr.updateNotifSetting(swNotifFlag.isChecked(), swNotifBeforeFlag.isChecked(),
            //                        CommonUtils.toInt(tvNotifBefore.getText().toString().trim()));
        }
        AlarmReceiver alarm = new AlarmReceiver();
        alarm.setupNotification(getApplicationContext());
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
                }else {
	                swNotifFlag.setEnabled(true);
	                swNotifBeforeFlag.setEnabled(true);
	                updateEnableViewExpired(swNotifBeforeFlag.isChecked());
                }
            } catch (Exception ex) {
		        LogCtrl.getInstance().error(ex.toString());
            }
        } else {
            swNotifFlag.setChecked(CommonUtils.getPrefBoolean(getApplicationContext(), StringList.KEY_NOTIF_ENABLE_FLAG));
            swNotifBeforeFlag.setChecked(CommonUtils.getPrefBoolean(getApplicationContext(), StringList.KEY_NOTIF_ENABLE_BEFORE_FLAG));
	        notifyBeforeCurrent = CommonUtils.getPrefIntegerWithDefaultValue(getApplicationContext(), StringList
			        .KEY_NOTIF_ENABLE_BEFORE, 14);
            tvNotifBefore.setText(String.valueOf(notifyBeforeCurrent));

	        updateEnableViewExpired(swNotifBeforeFlag.isChecked());
        }
        findViewById(R.id.rootView).setOnClickListener(new View.OnClickListener() {
	        @Override
	        public void onClick(View v) {
		        View currentFocus = getCurrentFocus();
		        if (currentFocus instanceof EditText) {
			        SoftKeyboardCtrl.hideKeyboard(NotificationSettingActivity.this);
			        currentFocus.clearFocus();
		        }
	        }
        });

        rlExpired.setOnClickListener(new View.OnClickListener() {
	        @Override
	        public void onClick(View view) {
		        showPickerChangeTimeNotify();
	        }
        });
		tvNotifBefore.setOnClickListener(new View.OnClickListener() {
	        @Override
	        public void onClick(View view) {
		        showPickerChangeTimeNotify();
	        }
        });
    }

	private void showPickerChangeTimeNotify() {
		ExpiredTimeNotifyBottomSheetDialog bottomSheetDialog = ExpiredTimeNotifyBottomSheetDialog.newInstance(notifyBeforeCurrent);
		bottomSheetDialog.setListener(new ExpiredTimeNotifyBottomSheetDialog.TimeSpecificListener() {
			@Override
			public void saveTimeDownload(int timeDownload) {
				notifyBeforeCurrent = timeDownload;
				tvNotifBefore.setText(String.valueOf(timeDownload));
				btnSaveNotifClick();
			}
		});
		bottomSheetDialog.show(getSupportFragmentManager(), "ExpiredTimeNotifyBottomSheetDialog");
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

	private boolean isValidateInput() {
        if (!swNotifBeforeFlag.isChecked()) {
            return true;
        }
        if (CommonUtils.isEmpty(getTextNotifBefore()) || !CommonUtils.isNumber(getTextNotifBefore())) {
            showMessage(makeMsgNotRangeExpiry(), getString(R.string.error), new DialogApplyMessage.OnOkDismissMessageListener() {
                @Override
                public void onOkDismissMessage() {
                    tvNotifBefore.setText(numDateNotifBefore);
	                btnSaveNotifClick();
                }
            });
            return false;
        }
        if (CommonUtils.toInt(getTextNotifBefore()) <= 0) {
            showMessage(makeMsgNotRangeExpiry(), getString(R.string.error), new DialogApplyMessage.OnOkDismissMessageListener() {
                @Override
                public void onOkDismissMessage() {
                    tvNotifBefore.setText(String.valueOf(MIN_BEFORE_DATE));
	                btnSaveNotifClick();
                }
            });
            return false;
        }
        if (CommonUtils.toInt(getTextNotifBefore()) > MAX_BEFORE_DATE) {
            showMessage(makeMsgNotRangeExpiry(), getString(R.string.error), new DialogApplyMessage.OnOkDismissMessageListener() {
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

	@NonNull
	private String getTextNotifBefore() {
		return tvNotifBefore.getText().toString().trim();
	}

	/**
     * Show message
     *
     * @param message
     */
    private void showMessage(String message, String titleDialog, DialogApplyMessage.OnOkDismissMessageListener listener) {
        DialogApplyMessage dlgMessage = new DialogApplyMessage(this, message);
        dlgMessage.setOnOkDismissMessageListener(listener);
        dlgMessage.setTitleDialog(titleDialog);
        dlgMessage.show();
    }

    private String makeMsgNotRangeExpiry() {
        return String.format(getString(R.string.error_input_before_expiry), MAX_BEFORE_DATE);
    }
}
