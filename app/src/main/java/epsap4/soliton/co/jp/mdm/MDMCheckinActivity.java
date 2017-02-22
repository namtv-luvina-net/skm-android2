package epsap4.soliton.co.jp.mdm;

import android.app.Activity;
import android.app.ProgressDialog;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import epsap4.soliton.co.jp.activity.CertRequestActivity;
import epsap4.soliton.co.jp.EpsapAdminReceiver;
import epsap4.soliton.co.jp.InformCtrl;
import epsap4.soliton.co.jp.activity.ProfileActivity;
import epsap4.soliton.co.jp.R;
import epsap4.soliton.co.jp.StringList;
import epsap4.soliton.co.jp.xmlparser.XmlDictionary;
import epsap4.soliton.co.jp.xmlparser.XmlPullParserAided;

public class MDMCheckinActivity extends Activity
	implements View.OnClickListener, Runnable {

	// パスコードコントロール適用ボタン
	private Button m_ButtonEndPscode;
	private Button m_ButtonStartPscode;
	// チェックインボタン
	private Button m_ButtonCheckin;
	// MDM開始
	private Button m_ButtonMDMRun;

	DevicePolicyManager m_DPM;
    ComponentName m_DeviceAdmin;
    private ProgressDialog progressDialog;
    MDMControl mdmctrl;

    private static InformCtrl m_InformCtrl;
    private XmlPullParserAided m_p_aided;

	public MDMCheckinActivity() {
		// TODO 自動生成されたコンストラクター・スタブ
	}

	 @Override
	 public void onCreate(Bundle savedInstanceState) {

		 this.setTitle(R.string.ApplicationTitle);

		 // 情報管理クラスの取得
		 Intent intent = getIntent();
		 m_InformCtrl = (InformCtrl)intent.getSerializableExtra(StringList.m_str_InformCtrl);

		 m_DPM = (DevicePolicyManager)getSystemService(Context.DEVICE_POLICY_SERVICE);
		 m_DeviceAdmin = new ComponentName(MDMCheckinActivity.this, EpsapAdminReceiver.class);
		 mdmctrl = new MDMControl(this, m_InformCtrl.GetAPID());

		 super.onCreate(savedInstanceState);

		 setContentView(R.layout.mdm_checkin);


    	m_p_aided = new XmlPullParserAided(this, m_InformCtrl.GetRtn(), 2);	// 最上位dictの階層は2になる
    	Log.i("MDMCheckinActivity:GetRtn", m_InformCtrl.GetRtn());
    	boolean ret = m_p_aided.TakeApartProfile();
    	if (ret == false) {
    		Log.e("MDMCheckinActivity::onCreate", "TakeApartProfile false");

    	}

    	setUItoMember();


	 }

	 private void setUItoMember() {

		 m_ButtonEndPscode = (Button)findViewById(R.id.ButtonEndpasscode);
		 m_ButtonStartPscode = (Button)findViewById(R.id.ButtonStartpasscode);
		 m_ButtonEndPscode.setOnClickListener(this);
		 m_ButtonStartPscode.setOnClickListener(this);

		 m_ButtonCheckin = (Button)findViewById(R.id.ButtonCheckin);
		 m_ButtonCheckin.setOnClickListener(this);

		 m_ButtonMDMRun = (Button)findViewById(R.id.ButtonMDMStart);
		 m_ButtonMDMRun.setOnClickListener(this);
		 m_ButtonMDMRun.setVisibility(View.GONE);

		 updateUi();
	 }

	@Override
	public void run() {
		// TODO 自動生成されたメソッド・スタブ
		SetMDM();

		handler.sendEmptyMessage(0);
	}

	private Handler handler = new Handler() {
		 public void handleMessage(Message msg) {
			// プログレスダイアログ終了
			 try{
				 mdmctrl.SrartService();
				 CertRequestActivity.endProgress(progressDialog);
			 //progressDialog.dismiss();
			 }catch(Exception e){}
			 updateUi();
			 finish();
		 }
	};

	@Override
	public void onClick(View v) {
		// TODO 自動生成されたメソッド・スタブ
		Log.i("MDMCheckinActivity::onClick", "start");
		// TODO 自動生成されたメソッド・スタブ
		if(v == m_ButtonCheckin) {
			// 連打を防ぐためグレーアウト
	        SetButtonRunnable(false);

	        // 通信中ダイアログを表示させる。
	        progressDialog = new ProgressDialog(this);
	        progressDialog.setTitle(R.string.progress_title);
	        progressDialog.setMessage(getText(R.string.progress_message).toString());
	        progressDialog.setIndeterminate(false);
	        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
	        progressDialog.show();

	        // サーバーとの通信をスレッドで行う
	        Thread thread = new Thread(this);	// 自分クラスをスレッドの引数に渡して...
	        thread.start();						// run()が実行される

		} else if(v == m_ButtonEndPscode) {
			if(isDeviceAdmin() == true) {
				Log.i("SKM", "AdminReceiver end.");
				m_DPM.removeActiveAdmin(m_DeviceAdmin);
				//updateUi();
				// DeviceAdminのremove実行後、すぐにisDeviceAdminを呼んでもtrueになってしまうときがあるので,
				// ここで直接、ボタンのグレーアウトを切り替える
				m_ButtonEndPscode.setEnabled(false);
			   	m_ButtonStartPscode.setEnabled(true);
			   	m_ButtonCheckin.setEnabled(false);
			   	m_ButtonMDMRun.setEnabled(false);
			}
		} else if(v == m_ButtonStartPscode) {
			if(isDeviceAdmin() == false) {
				Log.i("SKM", "AdminReceiver start.");
				addDeviceAdmin();
				updateUi();
			}
		} else if(v == m_ButtonMDMRun) {
			mdmctrl.SrartService();
		}

	}

	// MDMのチェックインおよび、定期通信サービススレッドの起動
	// HTTP通信を行うため、スレッドから呼び出されること
	private void SetMDM() {
		Log.d("MDMCheckinActivity", "SetMDM()");
		// 1. MDMインスタンス取得
		//*MDMControl*/ mdmctrl = new MDMControl(this, m_DPM, m_DeviceAdmin);

		// 2. GetMDMDictionary
		XmlDictionary mdm_dict = m_p_aided.GetMdmDictionary();
		if (mdm_dict == null) {
			Log.d("CertLoginActivity", "SetMDM() No profile");
			return;
		}

		// 3. MDMFlgsにセット(MDMControlにMDMFlgs変数を持たせてそちらにやってもらう
		mdmctrl.SetMDMmember(mdm_dict);

		// 4. チェックイン(HTTP(S)) (新しいMDM設定情報もここでファイル保存する)
		boolean bret = mdmctrl.CheckIn(true);

		// 5. OKならスレッド起動...定期通信
		if(bret == false) {
		//	mdmctrl.SrartService();
			Log.e("MDMCheckinActivity::SetMDM", "Checkin err");
			return;
		}

		bret = mdmctrl.TokenUpdate();
		if(bret == false) {
			//	mdmctrl.SrartService();
			Log.e("MDMCheckinActivity::SetMDM", "TokenUpdate err");
			return;
		}


	}

	// UIのグレーアウト状況
	private void updateUi() {
		boolean running = isDeviceAdmin();
		m_ButtonEndPscode.setEnabled(running);
		m_ButtonStartPscode.setEnabled(!running);
		m_ButtonCheckin.setEnabled(running);
		m_ButtonMDMRun.setEnabled(running);

	}

	// ボタン活性化
	public void SetButtonRunnable(boolean enable) {
		m_ButtonEndPscode.setEnabled(enable);	//
		m_ButtonStartPscode.setEnabled(enable);	//
		m_ButtonCheckin.setEnabled(enable);	//
		m_ButtonMDMRun.setEnabled(enable);
	}

	//////////////////////////////////////////
	/// DadminReceiver クラス関連          ///
	//////////////////////////////////////////
	public void onResume() {
		//	if (isDeviceAdmin() == false) {
		//		Log.i("EnrollActivity", "onResume false");
		//		addDeviceAdmin();
		//	}
		updateUi();
		super.onResume();
	}

	private void addDeviceAdmin() {
		Log.i("ProfileActivity", "addDeviceAdmin");
		// Launch the activity to have the user enable our admin.
		Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
		intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, m_DeviceAdmin);
		intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION,
		"Additional text explaining why this needs to be added.");
		startActivityForResult(intent, ProfileActivity.RESULT_ENABLE);
	}

	private boolean isDeviceAdmin() {
		return m_DPM.isAdminActive(m_DeviceAdmin);
	}

}
