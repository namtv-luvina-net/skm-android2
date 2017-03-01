package jp.co.soliton.keymanager.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import java.util.List;

import jp.co.soliton.keymanager.InputApplyInfo;
import jp.co.soliton.keymanager.R;
import jp.co.soliton.keymanager.dbalias.DatabaseHandler;
import jp.co.soliton.keymanager.dbalias.ElementApply;

/**
 * Created by luongdolong on 2/3/2017.
 *
 * Activity for menu apply screen
 */

public class MenuAcivity extends Activity {
    private LinearLayout zoneMenuCtr;
    private Button btnMenuStart;
    private Button btnMenuAPID;
    private Button btnMenuConfirmApply;
    private int totalApply;
    private DatabaseHandler db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        btnMenuStart = (Button) findViewById(R.id.btnMenuStart);
        btnMenuAPID = (Button) findViewById(R.id.btnMenuAPID);
        btnMenuConfirmApply = (Button) findViewById(R.id.btnMenuConfirmApply);
        zoneMenuCtr = (LinearLayout) findViewById(R.id.zoneMenuCtr);
        db = new DatabaseHandler(getApplicationContext());
    }


    @Override
    protected void onResume() {
        super.onResume();
        totalApply = db.getCountElementApply();
        if (totalApply <= 0) {
            btnMenuConfirmApply.setVisibility(View.GONE);
        } else {
            btnMenuConfirmApply.setVisibility(View.VISIBLE);
            btnMenuConfirmApply.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (totalApply == 1) {
                        List<ElementApply> listElementApply = db.getAllElementApply();
                        Intent intent = new Intent(MenuAcivity.this, DetailConfirmActivity.class);
                        intent.putExtra("ELEMENT_APPLY_ID", String.valueOf(listElementApply.get(0).getId()));
                        startActivity(intent);
                    } else {
                        Intent intent = new Intent(MenuAcivity.this, ListConfirmActivity.class);
                        startActivity(intent);
                    }
                }
            });
        }
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
