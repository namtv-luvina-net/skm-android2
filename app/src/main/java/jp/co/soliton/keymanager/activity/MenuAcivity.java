package jp.co.soliton.keymanager.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import java.util.List;

import jp.co.soliton.keymanager.InputApplyInfo;
import jp.co.soliton.keymanager.R;
import jp.co.soliton.keymanager.dbalias.ElementApply;
import jp.co.soliton.keymanager.dbalias.ElementApplyManager;

/**
 * Created by luongdolong on 2/3/2017.
 *
 * Activity for menu apply screen
 */

public class MenuAcivity extends Activity {
    public static String GO_TO_LIST_APPLY = "0";
    private int PERMISSIONS_REQUEST_READ_PHONE_STATE = 10;

    private LinearLayout zoneMenuCtr;
    private Button btnMenuStart;
    private Button btnMenuAPID;
    private Button btnMenuConfirmApply;
    private int totalApply;
    private ElementApplyManager elementMgr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        btnMenuStart = (Button) findViewById(R.id.btnMenuStart);
        btnMenuAPID = (Button) findViewById(R.id.btnMenuAPID);
        btnMenuConfirmApply = (Button) findViewById(R.id.btnMenuConfirmApply);
        zoneMenuCtr = (LinearLayout) findViewById(R.id.zoneMenuCtr);
        elementMgr = new ElementApplyManager(getApplicationContext());
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (MenuAcivity.GO_TO_LIST_APPLY.equals("1")) {
            MenuAcivity.GO_TO_LIST_APPLY = "0";
            Intent intent = new Intent(MenuAcivity.this, ListConfirmActivity.class);
            startActivity(intent);
        }
        totalApply = elementMgr.getCountElementApply();
        if (totalApply <= 0) {
            btnMenuConfirmApply.setVisibility(View.GONE);
        } else {
            btnMenuConfirmApply.setVisibility(View.VISIBLE);
            btnMenuConfirmApply.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (totalApply == 1) {
                        List<ElementApply> listElementApply = elementMgr.getAllElementApply();
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

        if(android.os.Build.VERSION.SDK_INT >= 23) {
            NewPermissionSet();
        }
    }

    /**
     * Set action for menu control
     */
    private void setupControl() {
        btnMenuStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (elementMgr.hasReApplyCertificate()) {
                    Intent intent = new Intent(MenuAcivity.this, ListCertificateActivity.class);
                    startActivity(intent);
                } else {
                    InputApplyInfo.deletePref(MenuAcivity.this);
                    Intent intent = new Intent(MenuAcivity.this, ViewPagerInputActivity.class);
                    startActivity(intent);
                }
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

    private void NewPermissionSet() {
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_PHONE_STATE},
                    PERMISSIONS_REQUEST_READ_PHONE_STATE);
        }
    }
}
