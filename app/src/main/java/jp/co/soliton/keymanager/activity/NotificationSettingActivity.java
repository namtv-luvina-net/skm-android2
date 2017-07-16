package jp.co.soliton.keymanager.activity;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.KeyEvent;
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
    private Button btnDayBeforeMinus;
    private Button btnDayBeforePlus;

    private NotifModeEnum mode;
    private String idCert;
    private ElementApplyManager elementMgr;
    private static final int MAX_BEFORE_DATE = 120;
    private static final int MIN_BEFORE_DATE = 1;
	String numDateNotifBefore = "";

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
        swNotifFlag = (Switch) findViewById(R.id.swNotifFlag);
        swNotifBeforeFlag = (Switch) findViewById(R.id.swNotifBeforeFlag);
        tvNotifBefore = (TextView) findViewById(R.id.tvNotifBefore);
        btnDayBeforeMinus = (Button) findViewById(R.id.btnDayBeforeMinus);
        btnDayBeforePlus = (Button) findViewById(R.id.btnDayBeforePlus);
        elementMgr = new ElementApplyManager(this);
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
	    tvTitleHeader.setText(getString(R.string.notif_setting));
        setupControl();
	    swNotifFlag.setOnCheckedChangeListener(this);
	    swNotifBeforeFlag.setOnCheckedChangeListener(this);
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
            textViewBack.setText(R.string.back);
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
		        LogCtrl.getInstance().error(ex.toString());
            }
        } else {
            textViewBack.setText(R.string.label_setting);
            swNotifFlag.setChecked(CommonUtils.getPrefBoolean(getApplicationContext(), StringList.KEY_NOTIF_ENABLE_FLAG));
            swNotifBeforeFlag.setChecked(CommonUtils.getPrefBoolean(getApplicationContext(), StringList.KEY_NOTIF_ENABLE_BEFORE_FLAG));
            tvNotifBefore.setText(String.valueOf(CommonUtils.getPrefIntegerWithDefaultValue(getApplicationContext(), StringList
		            .KEY_NOTIF_ENABLE_BEFORE, 14)));

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
				    SoftKeyboardCtrl.hideKeyboard(NotificationSettingActivity.this);
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

	private void updateEnableViewExpired(boolean isChecked) {
		tvNotifBefore.setEnabled(isChecked);
		btnDayBeforeMinus.setEnabled(isChecked);
		btnDayBeforePlus.setEnabled(isChecked);
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
