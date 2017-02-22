package epsap4.soliton.co.jp.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import epsap4.soliton.co.jp.InputApplyInfo;
import epsap4.soliton.co.jp.R;

/**
 * Created by luongdolong on 2/7/2017.
 *
 * Activity for complete apply screen
 */

public class CompleteApplyActivity extends Activity {
    public static final String BACK_AUTO = "backAuto";
    private Button btnBackTop;


    @Override
    public void finishActivity(int requestCode) {
        super.finishActivity(requestCode);
    }

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_complete_apply);
        btnBackTop = (Button) findViewById(R.id.btnBackToTop);
        Intent intent = getIntent();
        if (intent.getBooleanExtra(BACK_AUTO, false)) {
            btnBackTop.setText(getString(R.string.back_to_top_auto));
        } else {
            btnBackTop.setText(getString(R.string.back_to_top));
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        setupControl();
    }

    /**
     * Set action for control
     */
    private void setupControl() {
        btnBackTop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputApplyInfo.deletePref(CompleteApplyActivity.this);
                finish();
            }
        });
    }
}
