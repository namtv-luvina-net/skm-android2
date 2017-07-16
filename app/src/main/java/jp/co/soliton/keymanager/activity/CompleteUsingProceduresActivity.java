package jp.co.soliton.keymanager.activity;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.TextView;

import jp.co.soliton.keymanager.LogCtrl;
import jp.co.soliton.keymanager.R;
import jp.co.soliton.keymanager.dbalias.ElementApply;
import jp.co.soliton.keymanager.fragment.ContentCompleteUsingProceduresFragment;

/**
 * Created by luongdolong on 2/3/2017.
 *
 * Activity for menu apply screen
 */

public class CompleteUsingProceduresActivity extends FragmentActivity {
    private ElementApply elementApply;
	private TextView txtCN;
	private TextView txtSN;
	private TextView txtEpDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_complete_using_procedures);
	    txtCN = (TextView) findViewById(R.id.txtCN);
	    txtSN = (TextView) findViewById(R.id.txtSN);
	    txtEpDate = (TextView) findViewById(R.id.txtEpDate);
        Intent intent = getIntent();
        elementApply = (ElementApply)intent.getSerializableExtra("ELEMENT_APPLY");
    }

	public ElementApply getElementApply() {
		return elementApply;
	}

	@Override
	protected void onResume() {
		super.onResume();

		String cn = elementApply.getcNValue();
		String sn = elementApply.getsNValue();
		String ex = elementApply.getExpirationDate().split(" ")[0];
		txtCN.setText(cn);
		txtSN.setText(sn);
		txtEpDate.setText(ex);

		LogCtrl.getInstance().info("Proc: Complete Processing");
		LogCtrl.getInstance().debug("CN=" + cn + ", S/N=" + sn + ", Expiration=" + ex);
	}

	@Override
    public void onBackPressed() {
        backToTop(null);
    }

    public void backToTop(View v) {
	    final Intent intent = new Intent(getApplicationContext(), MenuAcivity.class);
	    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
	    startActivity(intent);
    }
}
