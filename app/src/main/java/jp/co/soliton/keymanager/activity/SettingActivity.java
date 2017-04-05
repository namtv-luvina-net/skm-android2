package jp.co.soliton.keymanager.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import jp.co.soliton.keymanager.R;

/**
 * Created by luongdolong on 3/31/2017.
 */

public class SettingActivity  extends Activity {
    private TextView titleScreen;
    private Button btnSettingProductInfo;
    private Button btnSettingNotification;
    private Button btnSettingListCert;
    private Button btnSettingLibrary;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        titleScreen = (TextView) findViewById(R.id.tvTitleHeader);
        btnSettingProductInfo = (Button) findViewById(R.id.btnSettingProductInfo);
        btnSettingNotification = (Button) findViewById(R.id.btnSettingNotification);
        btnSettingListCert = (Button) findViewById(R.id.btnSettingListCert);
        btnSettingLibrary = (Button) findViewById(R.id.btnSettingLibrary);
    }

    @Override
    protected void onResume() {
        super.onResume();
        setupControl();
    }

    public void btnBackClick(View v) {
        finish();
    }

    public void setupControl() {
        titleScreen.setText(getString(R.string.label_settings));
        btnSettingProductInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SettingActivity.this, ProductInfoActivity.class);
                startActivity(intent);
            }
        });
        btnSettingNotification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SettingActivity.this, NotificationSettingActivity.class);
                intent.putExtra(NotificationSettingActivity.KEY_NOTIF_MODE, NotificationSettingActivity.NotifModeEnum.ALL);
                startActivity(intent);
            }
        });
        btnSettingListCert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SettingActivity.this, SettingListCertificateActivity.class);
                startActivity(intent);
            }
        });
        btnSettingLibrary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SettingActivity.this, LibraryInfoActivity.class);
                startActivity(intent);
            }
        });
    }
}
