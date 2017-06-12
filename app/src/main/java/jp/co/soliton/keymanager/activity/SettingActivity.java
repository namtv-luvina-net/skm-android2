package jp.co.soliton.keymanager.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import jp.co.soliton.keymanager.R;

/**
 * Created by luongdolong on 3/31/2017.
 */

public class SettingActivity  extends Activity {
    private TextView titleScreen;
    private RelativeLayout menuSettingCertList;
    private RelativeLayout menuSettingNotif;
    private RelativeLayout menuSettingProduct;
    private RelativeLayout menuSettingLibrary;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        titleScreen = (TextView) findViewById(R.id.tvTitleHeader);
        menuSettingCertList = (RelativeLayout) findViewById(R.id.menuSettingCertList);
        menuSettingNotif = (RelativeLayout) findViewById(R.id.menuSettingNotif);
        menuSettingProduct = (RelativeLayout) findViewById(R.id.menuSettingProduct);
        menuSettingLibrary = (RelativeLayout) findViewById(R.id.menuSettingLibrary);
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
        titleScreen.setText(getString(R.string.label_setting));
        menuSettingCertList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SettingActivity.this, SettingListCertificateActivity.class);
                startActivity(intent);
            }
        });
        menuSettingNotif.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SettingActivity.this, NotificationSettingActivity.class);
                intent.putExtra(NotificationSettingActivity.KEY_NOTIF_MODE, NotificationSettingActivity.NotifModeEnum.ALL);
                startActivity(intent);
            }
        });
        menuSettingProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SettingActivity.this, ProductInfoActivity.class);
                startActivity(intent);
            }
        });
        menuSettingLibrary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SettingActivity.this, LibraryInfoActivity.class);
                startActivity(intent);
            }
        });
    }
}
