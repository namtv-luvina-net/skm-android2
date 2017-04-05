package jp.co.soliton.keymanager.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import jp.co.soliton.keymanager.R;
import jp.co.soliton.keymanager.StringList;
import jp.co.soliton.keymanager.alarm.AlarmReceiver;
import jp.co.soliton.keymanager.common.CommonUtils;
import jp.co.soliton.keymanager.customview.DialogApplyMessage;
import jp.co.soliton.keymanager.dbalias.ElementApply;
import jp.co.soliton.keymanager.dbalias.ElementApplyManager;

/**
 * Created by luongdolong on 3/31/2017.
 */

public class NotificationSettingActivity extends Activity {
    public static final String KEY_NOTIF_MODE = "KEY_NOTIF_MODE";
    public enum NotifModeEnum {
        ALL, ONE;
    }
    private TextView textViewBack;
    private Switch swNotifFlag;
    private Switch swNotifBeforeFlag;
    private TextView tvNotifBefore;
    private Button btnDayBeforeMinus;
    private Button btnDayBeforePlus;

    private NotifModeEnum mode;
    private String idCert;
    private ElementApplyManager elementMgr;

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
        textViewBack = (TextView) findViewById(R.id.textViewBack);
        swNotifFlag = (Switch) findViewById(R.id.swNotifFlag);
        swNotifBeforeFlag = (Switch) findViewById(R.id.swNotifBeforeFlag);
        tvNotifBefore = (TextView) findViewById(R.id.tvNotifBefore);
        btnDayBeforeMinus = (Button) findViewById(R.id.btnDayBeforeMinus);
        btnDayBeforePlus = (Button) findViewById(R.id.btnDayBeforePlus);

        elementMgr = new ElementApplyManager(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        setupControl();
    }

    public void btnBackClick(View v) {
        finish();
    }

    public void btnSaveNotifClick(View v) {
        if (!isValidateInput()) {
            return;
        }
        if (NotifModeEnum.ONE == mode) {
            elementMgr.updateNotifSettingElement(swNotifFlag.isChecked(), swNotifBeforeFlag.isChecked(),
                    CommonUtils.toInt(tvNotifBefore.getText().toString().trim()), idCert);
        } else {
            CommonUtils.putPref(getApplicationContext(), StringList.KEY_NOTIF_ENABLE_FLAG,
                    new Boolean(swNotifFlag.isChecked()));
            CommonUtils.putPref(getApplicationContext(), StringList.KEY_NOTIF_ENABLE_BEFORE_FLAG,
                    new Boolean(swNotifBeforeFlag.isChecked()));
            CommonUtils.putPref(getApplicationContext(), StringList.KEY_NOTIF_ENABLE_BEFORE,
                    new Integer(CommonUtils.toInt(tvNotifBefore.getText().toString().trim())));

            elementMgr.updateNotifSetting(swNotifFlag.isChecked(), swNotifBeforeFlag.isChecked(),
                                    CommonUtils.toInt(tvNotifBefore.getText().toString().trim()));
        }
        AlarmReceiver alarm = new AlarmReceiver();
        alarm.setupNotification(getApplicationContext());
        finish();
    }

    public void setupControl() {

        if (NotifModeEnum.ONE == mode) {
            ElementApply element = elementMgr.getElementApply(idCert);
            textViewBack.setText(R.string.back);
            swNotifFlag.setChecked(element.isNotiEnableFlag());
            swNotifBeforeFlag.setChecked(element.isNotiEnableBeforeFlag());
            tvNotifBefore.setText(String.valueOf(element.getNotiEnableBefore()));
        } else {
            textViewBack.setText(R.string.label_settings);
            swNotifFlag.setChecked(CommonUtils.getPrefBoolean(getApplicationContext(), StringList.KEY_NOTIF_ENABLE_FLAG));
            swNotifBeforeFlag.setChecked(CommonUtils.getPrefBoolean(getApplicationContext(), StringList.KEY_NOTIF_ENABLE_BEFORE_FLAG));
            tvNotifBefore.setText(String.valueOf(CommonUtils.getPrefInteger(getApplicationContext(), StringList.KEY_NOTIF_ENABLE_BEFORE)));
        }
        final int oldValue = CommonUtils.toInt(tvNotifBefore.getText().toString());
        swNotifBeforeFlag.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (!isChecked) {
                    if (CommonUtils.isEmpty(tvNotifBefore.getText().toString().trim()) ||
                            !CommonUtils.isNumber(tvNotifBefore.getText().toString().trim())) {
                        tvNotifBefore.setText(oldValue);
                    }
                    if (CommonUtils.toInt(tvNotifBefore.getText().toString().trim()) <= 0 ||
                            CommonUtils.toInt(tvNotifBefore.getText().toString().trim()) > 120) {
                        tvNotifBefore.setText(String.valueOf(oldValue));
                    }
                }
                tvNotifBefore.setEnabled(isChecked);
                btnDayBeforeMinus.setEnabled(isChecked);
                btnDayBeforePlus.setEnabled(isChecked);
            }
        });
        btnDayBeforePlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (CommonUtils.isEmpty(tvNotifBefore.getText().toString().trim()) || !CommonUtils.isNumber(tvNotifBefore.getText().toString().trim())) {
                    tvNotifBefore.setText("1");
                } else if (CommonUtils.toInt(tvNotifBefore.getText().toString().trim()) < 120){
                    tvNotifBefore.setText(String.valueOf(CommonUtils.toInt(tvNotifBefore.getText().toString().trim()) + 1));
                }
            }
        });
        btnDayBeforeMinus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (CommonUtils.isEmpty(tvNotifBefore.getText().toString().trim()) || !CommonUtils.isNumber(tvNotifBefore.getText().toString().trim())) {
                    tvNotifBefore.setText("120");
                } else if (CommonUtils.toInt(tvNotifBefore.getText().toString().trim()) > 1){
                    tvNotifBefore.setText(String.valueOf(CommonUtils.toInt(tvNotifBefore.getText().toString().trim()) - 1));
                }
            }
        });
    }

    private boolean isValidateInput() {
        if (!swNotifBeforeFlag.isChecked()) {
            return true;
        }
        if (CommonUtils.isEmpty(tvNotifBefore.getText().toString().trim()) || !CommonUtils.isNumber(tvNotifBefore.getText().toString().trim())) {
            showMessage(getString(R.string.error_input_before_expiry), getString(R.string.error), new DialogApplyMessage.OnOkDismissMessageListener() {
                @Override
                public void onOkDismissMessage() {
                    tvNotifBefore.setText("1");
                }
            });
            return false;
        }
        if (CommonUtils.toInt(tvNotifBefore.getText().toString().trim()) <= 0) {
            showMessage(getString(R.string.error_input_before_expiry), getString(R.string.error), new DialogApplyMessage.OnOkDismissMessageListener() {
                @Override
                public void onOkDismissMessage() {
                    tvNotifBefore.setText("1");
                }
            });
            return false;
        }
        if (CommonUtils.toInt(tvNotifBefore.getText().toString().trim()) > 120) {
            showMessage(getString(R.string.error_input_before_expiry), getString(R.string.error), new DialogApplyMessage.OnOkDismissMessageListener() {
                @Override
                public void onOkDismissMessage() {
                    tvNotifBefore.setText("120");
                }
            });
            return false;
        }
        return true;
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
}
