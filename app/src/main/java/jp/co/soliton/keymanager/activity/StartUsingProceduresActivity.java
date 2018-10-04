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

import static jp.co.soliton.keymanager.asynctask.StartUsingProceduresControl.CERT_STORE_TO_KEY_CHAIN;
import static jp.co.soliton.keymanager.asynctask.StartUsingProceduresControl.KEY_PAIR_TO_KEY_CHAIN;

/**
 * Created by luongdolong on 2/3/2017.
 *
 * Activity for menu apply screen
 */

public class StartUsingProceduresActivity extends Activity {

	private static InformCtrl m_InformCtrl;
	private ElementApply element;
	private StartUsingProceduresControl startUsingProceduresControl;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_start_using_procedures);
		Intent intent = getIntent();
		m_InformCtrl = (InformCtrl) intent.getSerializableExtra(StringList.m_str_InformCtrl);
		element = (ElementApply) intent.getSerializableExtra("ELEMENT_APPLY");
		startUsingProceduresControl = StartUsingProceduresControl.newInstance(this,
				m_InformCtrl, element);
		startUsingProceduresControl.startDeviceCertTask();
	}

	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == KEY_PAIR_TO_KEY_CHAIN) {
			LogCtrl.getInstance().info("onActivityResult KEY_PAIR_TO_KEY_CHAIN");
			startUsingProceduresControl.startCertToKeyChainTask();
		}
		if (requestCode == CERT_STORE_TO_KEY_CHAIN + 1) {
			if (resultCode == RESULT_OK) {
				LogCtrl.getInstance().info("Proc: CA Certificate Installation Successful");
				StartUsingProceduresControl.getInstance(this).startCertificateEnrollTask();
			} else {
				LogCtrl.getInstance().warn("Proc: CA Certificate Installation Cancelled");
				goToListApply();
			}
		}
		if (requestCode == StartUsingProceduresControl.m_nEnrollRtnCode) {
			StartUsingProceduresControl.getInstance(this).afterIntallCert();
			// After CertificateEnrollTask
			if (resultCode != 0) {
				LogCtrl.getInstance().info("Proc: Certificate Installation Successful");
				ElementApplyManager mgr = ElementApplyManager.getInstance(getApplicationContext());
				ElementApply elementApplyNew = StartUsingProceduresControl.getInstance(this).getElement();
				ElementApply elementApplyInDatabase = mgr.getElementApply(String.valueOf(elementApplyNew.getId()));
				AlarmReceiver alarm = new AlarmReceiver();
				alarm.updateNotificationIfNeed(getApplicationContext(), elementApplyNew, elementApplyInDatabase);

				mgr.updateElementCertificate(elementApplyNew);

				Intent intent = new Intent(getApplicationContext(), CompleteUsingProceduresActivity.class);
				intent.putExtra("ELEMENT_APPLY", elementApplyNew);
				finish();
				startActivity(intent);
			} else {
				LogCtrl.getInstance().warn("Proc: Certificate Installation Cancelled");
				goToListApply();
			}
		} else if (requestCode == StartUsingProceduresControl.m_nMDM_RequestCode) {
			if (resultCode == RESULT_OK) {
				StartUsingProceduresControl.getInstance(this).resultWithRequestCodeMDM();
			} else {
				finish();
			}
		}
	}

	private void goToListApply() {
		StringList.GO_TO_LIST_APPLY = "1";
		Intent intent = new Intent(getApplicationContext(), MenuAcivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(intent);
	}
}
