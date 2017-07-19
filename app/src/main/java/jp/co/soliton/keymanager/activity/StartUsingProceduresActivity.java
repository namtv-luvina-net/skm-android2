package jp.co.soliton.keymanager.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import jp.co.soliton.keymanager.InformCtrl;
import jp.co.soliton.keymanager.LogCtrl;
import jp.co.soliton.keymanager.R;
import jp.co.soliton.keymanager.StringList;
import jp.co.soliton.keymanager.alarm.AlarmReceiver;
import jp.co.soliton.keymanager.asynctask.StartUsingProceduresControl;
import jp.co.soliton.keymanager.dbalias.ElementApply;
import jp.co.soliton.keymanager.dbalias.ElementApplyManager;

/**
 * Created by luongdolong on 2/3/2017.
 *
 * Activity for menu apply screen
 */

public class StartUsingProceduresActivity extends Activity {

    private static InformCtrl m_InformCtrl;
    private ElementApply element;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_using_procedures);
        Intent intent = getIntent();
        m_InformCtrl = (InformCtrl)intent.getSerializableExtra(StringList.m_str_InformCtrl);
        element = (ElementApply)intent.getSerializableExtra("ELEMENT_APPLY");
	    StartUsingProceduresControl startUsingProceduresControl = StartUsingProceduresControl.newInstance(this,
			    m_InformCtrl, element);
	    startUsingProceduresControl.startDeviceCertTask();
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
	    if (requestCode == StartUsingProceduresControl.m_nEnrollRtnCode) {
		    StartUsingProceduresControl.getInstance(this).afterIntallCert();
		    // After CertificateEnrollTask
		    if (resultCode != 0) {
				LogCtrl.getInstance().info("Proc: Certificate Installation Successful");
			    ElementApplyManager mgr = new ElementApplyManager(getApplicationContext());
			    mgr.updateElementCertificate(StartUsingProceduresControl.getInstance(this).getElement());
			    AlarmReceiver alarm = new AlarmReceiver();
			    alarm.setupNotification(getApplicationContext());
			    Intent intent = new Intent(getApplicationContext(), CompleteUsingProceduresActivity.class);
			    intent.putExtra("ELEMENT_APPLY", StartUsingProceduresControl.getInstance(this).getElement());
			    finish();
			    startActivity(intent);
		    } else {
				LogCtrl.getInstance().warn("Proc: Certificate Installation Cancelled");
			    StringList.GO_TO_LIST_APPLY = "1";
			    Intent intent = new Intent(getApplicationContext(), MenuAcivity.class);
			    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			    startActivity(intent);
		    }
	    } else if (requestCode == StartUsingProceduresControl.m_nMDM_RequestCode) {
		    if (resultCode == RESULT_OK) {
			    StartUsingProceduresControl.getInstance(this).resultWithRequestCodeMDM();
		    } else {
			    finish();
		    }
	    } else if (requestCode == ViewPagerInputActivity.REQUEST_CODE_INSTALL_CERTIFICATION_VIEWPAGER_INPUT) {
		    if (resultCode == Activity.RESULT_OK) {
				LogCtrl.getInstance().info("Proc: CA Certificate Installation Successful");
			    StartUsingProceduresControl.getInstance(this).startCertificateEnrollTask();
		    }
		    else {
				LogCtrl.getInstance().warn("Proc: CA Certificate Installation Cancelled");
				StringList.GO_TO_LIST_APPLY = "1";
				Intent intent = new Intent(getApplicationContext(), MenuAcivity.class);
				intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(intent);
			}
	    }
    }
}
