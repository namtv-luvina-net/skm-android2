package jp.co.soliton.keymanager.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
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
        Intent intent = getIntent();
        elementApply = (ElementApply)intent.getSerializableExtra("ELEMENT_APPLY");
        txtCN = (TextView) findViewById(R.id.txtCN);
        txtSN = (TextView) findViewById(R.id.txtSN);
        txtEpDate = (TextView) findViewById(R.id.txtEpDate);
    }

    @Override
    protected void onResume() {
        super.onResume();
        txtCN.setText(elementApply.getcNValue());
        txtSN.setText(elementApply.getsNValue());
        txtEpDate.setText(elementApply.getExpirationDate());
    }
}
