package jp.co.soliton.keymanager.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import jp.co.soliton.keymanager.R;
import jp.co.soliton.keymanager.dbalias.ElementApply;

/**
 * Created by luongdolong on 2/3/2017.
 *
 * Activity for menu apply screen
 */

public class CompleteUsingProceduresActivity extends Activity {
    private ElementApply elementApply;
    private TextView txtCN;
    private TextView txtSN;
    private TextView txtEpDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_complete_using_procedures);
	    setOrientation();
        Intent intent = getIntent();
        elementApply = (ElementApply)intent.getSerializableExtra("ELEMENT_APPLY");
        txtCN = (TextView) findViewById(R.id.txtCN);
        txtSN = (TextView) findViewById(R.id.txtSN);
        txtEpDate = (TextView) findViewById(R.id.txtEpDate);
    }

	private void setOrientation() {
		boolean isTablet = getResources().getBoolean(R.bool.isTablet);
		if (!isTablet) {
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		}
	}

    @Override
    protected void onResume() {
        super.onResume();
        txtCN.setText(elementApply.getcNValue());
        txtSN.setText(elementApply.getsNValue());
        txtEpDate.setText(elementApply.getExpirationDate().split(" ")[0]);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(getApplicationContext(), MenuAcivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
	    overridePendingTransition(0, 0);
    }

    public void backToTop(View v) {
        Intent intent = new Intent(getApplicationContext(), MenuAcivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
	    overridePendingTransition(0, 0);
    }
}
