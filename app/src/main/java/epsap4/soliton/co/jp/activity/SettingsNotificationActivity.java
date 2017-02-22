package epsap4.soliton.co.jp.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SwitchCompat;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.TextView;

import epsap4.soliton.co.jp.R;
import epsap4.soliton.co.jp.SKMPreferences;

/**
 * Created by daoanhtung on 1/3/2017.
 */

public class SettingsNotificationActivity extends AppCompatActivity {

    //UI param
    private SwitchCompat switchExperidNotification, switchBeforeExperidNotification;
    private TextView textViewNotificationSetting;
    private TextView textViewbeforefinaldate;
    private TextView textViewSubtract;
    private TextView textViewAdd;
    private TextView textViewSave;

    private int numberDay;
    private Boolean flagFinalDate = false;
    private Boolean flagBeforeFinalDate = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings_notification);
        switchExperidNotification = (SwitchCompat) findViewById(R.id.switchExperidNotification);
        switchBeforeExperidNotification = (SwitchCompat) findViewById(R.id.switchBeforeExperidNotification);
        textViewNotificationSetting = (TextView) findViewById(R.id.textViewNotificationSetting);
        textViewSave = (TextView) findViewById(R.id.textViewSave);
        textViewbeforefinaldate = (TextView) findViewById(R.id.textViewbeforefinaldate);
        textViewAdd = (TextView) findViewById(R.id.textview_add);
        textViewSubtract = (TextView) findViewById(R.id.textview_subtract);
        setUIMember();
        setOnClickListener();
    }

    /**
     * set switch, textview display on screen when create Activity
     */
    private void setUIMember() {
        numberDay = SKMPreferences.getNumberBeforeFinalDate(SettingsNotificationActivity.this);
        flagFinalDate = SKMPreferences.getStatusAllNotificationFinalDate(SettingsNotificationActivity.this);
        flagBeforeFinalDate = SKMPreferences.getStatusAllNotificationBeforeFinalDate(SettingsNotificationActivity.this);
        if (SKMPreferences.getStatusAllNotificationFinalDate(SettingsNotificationActivity.this)) {
            switchExperidNotification.setChecked(true);
        } else {
            switchExperidNotification.setChecked(false);
        }
        if (SKMPreferences.getStatusAllNotificationBeforeFinalDate(SettingsNotificationActivity.this)) {
            switchBeforeExperidNotification.setChecked(true);
        } else {
            switchBeforeExperidNotification.setChecked(false);
        }
        textViewbeforefinaldate.setText(String.valueOf(SKMPreferences.getNumberBeforeFinalDate(SettingsNotificationActivity.this)) + " " + getString(R.string.label_day_before));
    }

    /**
     * This method returns onclick
     */
    private void setOnClickListener() {
        savePropetyNotificationAll();
        switchExperidNotification.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    flagFinalDate = true;
                } else {
                    flagFinalDate = false;
                }
            }
        });
        switchBeforeExperidNotification.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    flagBeforeFinalDate = true;
                } else {
                    flagBeforeFinalDate = false;
                }
            }
        });
        textViewNotificationSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SettingsNotificationActivity.this, SettingActivity.class);
                startActivity(intent);
                finish();
            }
        });
        textViewAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (flagBeforeFinalDate) {
                    if (numberDay < 120) {
                        numberDay = numberDay + 1;
                        textViewbeforefinaldate.setText(String.valueOf(numberDay) + " " + getString(R.string.label_day_before));
                    }
                }
            }
        });
        textViewSubtract.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (flagBeforeFinalDate) {
                    if (numberDay > 1) {
                        numberDay = numberDay - 1;
                        textViewbeforefinaldate.setText(String.valueOf(numberDay) + " " + getString(R.string.label_day_before));
                    }
                }
            }
        });
    }

    /**
     * This method save property SKMNotification: status, day before
     */
    private void savePropetyNotificationAll() {
        textViewSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (flagFinalDate) {
                    SKMPreferences.setStatusAllNotificationFinalDate(SettingsNotificationActivity.this, true);
                } else {
                    SKMPreferences.setStatusAllNotificationFinalDate(SettingsNotificationActivity.this, false);
                }
                if (flagBeforeFinalDate) {
                    SKMPreferences.setStatusAllNotificationBeforeFinalDate(SettingsNotificationActivity.this, true);
                } else {
                    SKMPreferences.setStatusAllNotificationBeforeFinalDate(SettingsNotificationActivity.this, false);
                }
                SKMPreferences.setNumberBeforeFinalDate(SettingsNotificationActivity.this, numberDay);
                finish();
            }
        });
    }

}
