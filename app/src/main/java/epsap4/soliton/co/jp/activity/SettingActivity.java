package epsap4.soliton.co.jp.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import epsap4.soliton.co.jp.R;

/**
 * Created by daoanhtung on 1/4/2017.
 */

public class SettingActivity extends AppCompatActivity {
    //UI param
    private Button buttonNotificationSettings;
    private Button buttonNotificationScheduled;
    private TextView textViewCloseSetting;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        buttonNotificationSettings = (Button) findViewById(R.id.buttonNotificationSettings);
        buttonNotificationScheduled = (Button) findViewById(R.id.buttonNotificationScheduled);
        textViewCloseSetting = (TextView) findViewById(R.id.textViewCloseSetting);
        setOnClickListener();
    }

    /**
     * This method returns onclick buttonNotificationSettings, buttonNotificationScheduled, textViewSetting
     *
     * @return
     */
    private void setOnClickListener() {
        buttonNotificationSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SettingActivity.this, SettingsNotificationActivity.class);
                startActivity(intent);
            }
        });
        buttonNotificationScheduled.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SettingActivity.this, ScheduledNoificationActivity.class);
                startActivity(intent);
                //finish();
            }
        });
        textViewCloseSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SettingActivity.this, ListCertActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
}
