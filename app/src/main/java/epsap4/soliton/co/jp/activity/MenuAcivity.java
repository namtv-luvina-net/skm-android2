package epsap4.soliton.co.jp.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import epsap4.soliton.co.jp.InputApplyInfo;
import epsap4.soliton.co.jp.R;

/**
 * Created by luongdolong on 2/3/2017.
 *
 * Activity for menu apply screen
 */

public class MenuAcivity extends Activity {
    private LinearLayout zoneMenuCtr;
    private Button btnMenuStart;
    private Button btnMenuAPID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        btnMenuStart = (Button) findViewById(R.id.btnMenuStart);
        btnMenuAPID = (Button) findViewById(R.id.btnMenuAPID);
        zoneMenuCtr = (LinearLayout) findViewById(R.id.zoneMenuCtr);
    }


    @Override
    protected void onResume() {
        super.onResume();
        setupControl();
    }

    /**
     * Set action for menu control
     */
    private void setupControl() {
        btnMenuStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputApplyInfo.deletePref(MenuAcivity.this);
                Intent intent = new Intent(MenuAcivity.this, ViewPagerInputActivity.class);
                startActivity(intent);
            }
        });

        btnMenuAPID.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MenuAcivity.this, APIDActivity.class);
                startActivity(intent);
            }
        });
    }
}
