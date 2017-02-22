package epsap4.soliton.co.jp.activity;

import android.app.AlarmManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SwitchCompat;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.TextView;

import epsap4.soliton.co.jp.R;
import epsap4.soliton.co.jp.StringList;
import epsap4.soliton.co.jp.dbalias.DatabaseHandler;
import epsap4.soliton.co.jp.dbalias.ItemAlias;
import epsap4.soliton.co.jp.notification.SKMNotification;

/**
 * Created by daoanhtung on 1/4/2017.
 */

public class SettingNotificationOneCertActivity extends AppCompatActivity {
    //UI param
    private TextView textViewNotificationSetting;
    private TextView textViewbeforefinaldate;
    private TextView textViewSubtract;
    private TextView textViewAdd;
    private TextView textViewSave;
    private SwitchCompat switchExperidNotification;
    private SwitchCompat switchBeforeExperidNotification;

    private ItemAlias itemAlias = null;
    //DB
    private DatabaseHandler databaseHandler;
    private Long numberDay;
    private Boolean flagFinalDate = false;
    private Boolean flagBeforeFinalDate = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings_notification);
        Intent intent = getIntent();
        if (intent != null) {
            itemAlias = (ItemAlias) intent.getSerializableExtra(StringList.m_str_alias_skm);
            databaseHandler = new DatabaseHandler(SettingNotificationOneCertActivity.this);
        }
        textViewNotificationSetting = (TextView) findViewById(R.id.textViewNotificationSetting);
        textViewNotificationSetting.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
        textViewNotificationSetting.setText(getString(R.string.label_dialog_Cancle));
        textViewbeforefinaldate = (TextView) findViewById(R.id.textViewbeforefinaldate);
        textViewSubtract = (TextView) findViewById(R.id.textview_subtract);
        textViewAdd = (TextView) findViewById(R.id.textview_add);
        textViewSave = (TextView) findViewById(R.id.textViewSave);
        switchExperidNotification = (SwitchCompat) findViewById(R.id.switchExperidNotification);
        switchBeforeExperidNotification = (SwitchCompat) findViewById(R.id.switchBeforeExperidNotification);
        setUIMember();
        if (itemAlias!=null) {
            setOnClickListener();
        }
    }

    /**
     * set switch, textview display on screen when create Activity
     */
    private void setUIMember() {
        if (itemAlias != null) {
            if (itemAlias.getStatusNotificationFinalDate() == 1) {
                switchExperidNotification.setChecked(true);
            } else {
                switchExperidNotification.setChecked(false);
            }
            if (itemAlias.getStatusNotificationBeforeFinalDate() == 1) {
                switchBeforeExperidNotification.setChecked(true);
            } else {
                switchBeforeExperidNotification.setChecked(false);
            }
            numberDay = (itemAlias.getBeforeFinalDate() / SKMNotification.DAY_MILLIS);
            textViewbeforefinaldate.setText(String.valueOf(numberDay) + " " + getString(R.string.label_day_before));

            if (itemAlias.getStatusNotificationFinalDate() == 1) {
                flagFinalDate = true;
            } else {
                flagFinalDate = false;
            }
            if (itemAlias.getStatusNotificationBeforeFinalDate() == 1) {
                flagBeforeFinalDate = true;
            } else {
                flagBeforeFinalDate = false;
            }
        }
    }

    /**
     * This method returns onclick
     */
    private void setOnClickListener() {
        textViewNotificationSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        switchExperidNotification.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (itemAlias != null) {
                    if (isChecked) {
                        flagFinalDate = true;
                    } else {
                        flagFinalDate = false;
                    }
                }
            }
        });
        if (itemAlias.getFinalDate() > System.currentTimeMillis()) {
            savePropetyNotificationAll();
            switchExperidNotification.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (itemAlias != null) {
                        if (isChecked) {
                            flagFinalDate = true;
                        } else {
                            flagFinalDate = false;
                        }
                    }
                }
            });
            switchBeforeExperidNotification.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (itemAlias != null) {
                        if (isChecked) {
                            flagBeforeFinalDate = true;
                        } else {
                            flagBeforeFinalDate = false;
                        }
                    }
                }
            });
            textViewSubtract.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (itemAlias != null) {
                        if (flagBeforeFinalDate) {
                            if (numberDay > 1) {
                                numberDay = numberDay - 1;
                                textViewbeforefinaldate.setText(String.valueOf(numberDay) + " " + getString(R.string.label_day_before));
                            }
                        }
                    }
                }
            });
            textViewAdd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (itemAlias != null) {
                        if (flagBeforeFinalDate) {
                            if (numberDay < 120) {
                                numberDay = numberDay + 1;
                                textViewbeforefinaldate.setText(String.valueOf(numberDay) + " " + getString(R.string.label_day_before));
                            }
                        }
                    }
                }
            });
        } else {
            // Disable Certificate experied
            switchExperidNotification.setChecked(false);
            switchBeforeExperidNotification.setChecked(false);
            switchExperidNotification.setEnabled(false);
            switchBeforeExperidNotification.setEnabled(false);

        }
    }
    /**
     * This method save property SKMNotification: status, day before
     */
    private void savePropetyNotificationAll() {
        textViewSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (flagFinalDate) {
                    itemAlias.setStatusNotificationFinalDate(1);
                } else {
                    itemAlias.setStatusNotificationFinalDate(0);
                }
                if (flagBeforeFinalDate) {
                    itemAlias.setStatusNotificationBeforeFinalDate(1);
                } else {
                    itemAlias.setStatusNotificationBeforeFinalDate(0);
                }
                Long timeDay = Long.valueOf(numberDay * SKMNotification.DAY_MILLIS);
                itemAlias.setBeforeFinalDate(timeDay);
                //update the status of the notification and before day to DB,
                databaseHandler.updateAlias(itemAlias);
                SKMNotification.updateStatusNotification(SettingNotificationOneCertActivity.this, itemAlias);
                finish();
            }
        });
    }
}
