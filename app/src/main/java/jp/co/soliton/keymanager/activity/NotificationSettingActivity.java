package jp.co.soliton.keymanager.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import jp.co.soliton.keymanager.LogCtrl;
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
    private TextView tvDetailNotifSave;
    private TextView textViewBack;
    private Switch swNotifFlag;
    private Switch swNotifBeforeFlag;
    private TextView tvNotifBefore;
    private Button btnDayBeforeMinus;
    private Button btnDayBeforePlus;

    private NotifModeEnum mode;
    private String idCert;
    private ElementApplyManager elementMgr;
    private int maxBeforeDate;

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
        tvDetailNotifSave = (TextView) findViewById(R.id.tvDetailNotifSave);
        textViewBack = (TextView) findViewById(R.id.textViewBack);
        swNotifFlag = (Switch) findViewById(R.id.swNotifFlag);
        swNotifBeforeFlag = (Switch) findViewById(R.id.swNotifBeforeFlag);
        tvNotifBefore = (TextView) findViewById(R.id.tvNotifBefore);
        btnDayBeforeMinus = (Button) findViewById(R.id.btnDayBeforeMinus);
        btnDayBeforePlus = (Button) findViewById(R.id.btnDayBeforePlus);
        maxBeforeDate = 120;
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

            //elementMgr.updateNotifSetting(swNotifFlag.isChecked(), swNotifBeforeFlag.isChecked(),
            //                        CommonUtils.toInt(tvNotifBefore.getText().toString().trim()));
        }
        AlarmReceiver alarm = new AlarmReceiver();
        alarm.setupNotification(getApplicationContext());
        finish();
    }

    public void setupControl() {

        if (NotifModeEnum.ONE == mode) {
            ElementApply element = elementMgr.getElementApply(idCert);
            textViewBack.setText(R.string.label_dialog_Cancle);
            swNotifFlag.setChecked(element.isNotiEnableFlag());
            swNotifBeforeFlag.setChecked(element.isNotiEnableBeforeFlag());
            tvNotifBefore.setText(String.valueOf(element.getNotiEnableBefore()));
            try {
                SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                Date expirationDate = formatter.parse(element.getExpirationDate());
                if (Calendar.getInstance().getTime().after(expirationDate)) {
                    swNotifFlag.setEnabled(false);
                    swNotifBeforeFlag.setEnabled(false);
                    tvNotifBefore.setEnabled(false);
                    btnDayBeforeMinus.setEnabled(false);
                    btnDayBeforePlus.setEnabled(false);
                    tvDetailNotifSave.setClickable(false);
                } else {
                    tvNotifBefore.setEnabled(swNotifBeforeFlag.isChecked());
                    btnDayBeforeMinus.setEnabled(swNotifBeforeFlag.isChecked());
                    btnDayBeforePlus.setEnabled(swNotifBeforeFlag.isChecked());
                    tvDetailNotifSave.setClickable(true);
                }
                //Comparing dates
                long difference = expirationDate.getTime() - Calendar.getInstance().getTime().getTime();
                long differenceDates = difference / (24 * 60 * 60 * 1000);
                if (difference > 0){
                    differenceDates++;
                }
                if (differenceDates < 120) {
                    maxBeforeDate = (int)differenceDates;
                }
            } catch (Exception ex) {
                LogCtrl.Logger(LogCtrl.m_strError, ex.toString(), getApplicationContext());
            }
        } else {
            textViewBack.setText(R.string.label_settings);
            swNotifFlag.setChecked(CommonUtils.getPrefBoolean(getApplicationContext(), StringList.KEY_NOTIF_ENABLE_FLAG));
            swNotifBeforeFlag.setChecked(CommonUtils.getPrefBoolean(getApplicationContext(), StringList.KEY_NOTIF_ENABLE_BEFORE_FLAG));
            tvNotifBefore.setText(String.valueOf(CommonUtils.getPrefInteger(getApplicationContext(), StringList.KEY_NOTIF_ENABLE_BEFORE)));

            tvNotifBefore.setEnabled(swNotifBeforeFlag.isChecked());
            btnDayBeforeMinus.setEnabled(swNotifBeforeFlag.isChecked());
            btnDayBeforePlus.setEnabled(swNotifBeforeFlag.isChecked());
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
                            CommonUtils.toInt(tvNotifBefore.getText().toString().trim()) > maxBeforeDate) {
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
                } else if (CommonUtils.toInt(tvNotifBefore.getText().toString().trim()) < maxBeforeDate){
                    tvNotifBefore.setText(String.valueOf(CommonUtils.toInt(tvNotifBefore.getText().toString().trim()) + 1));
                }
            }
        });
        btnDayBeforeMinus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (CommonUtils.isEmpty(tvNotifBefore.getText().toString().trim()) || !CommonUtils.isNumber(tvNotifBefore.getText().toString().trim())) {
                    tvNotifBefore.setText(String.valueOf(maxBeforeDate));
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
            showMessage(makeMsgNotRangeExpiry(), getString(R.string.error), new DialogApplyMessage.OnOkDismissMessageListener() {
                @Override
                public void onOkDismissMessage() {
                    tvNotifBefore.setText("1");
                }
            });
            return false;
        }
        if (CommonUtils.toInt(tvNotifBefore.getText().toString().trim()) <= 0) {
            showMessage(makeMsgNotRangeExpiry(), getString(R.string.error), new DialogApplyMessage.OnOkDismissMessageListener() {
                @Override
                public void onOkDismissMessage() {
                    tvNotifBefore.setText("1");
                }
            });
            return false;
        }
        if (CommonUtils.toInt(tvNotifBefore.getText().toString().trim()) > maxBeforeDate) {
            showMessage(makeMsgNotRangeExpiry(), getString(R.string.error), new DialogApplyMessage.OnOkDismissMessageListener() {
                @Override
                public void onOkDismissMessage() {
                    tvNotifBefore.setText(String.valueOf(maxBeforeDate));
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

    private String makeMsgNotRangeExpiry() {
        return String.format(getString(R.string.error_input_before_expiry), maxBeforeDate);
    }
}
