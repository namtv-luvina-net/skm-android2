package epsap4.soliton.co.jp.activity;

import android.app.Activity;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import epsap4.soliton.co.jp.ConfigrationDivision;
import epsap4.soliton.co.jp.EpsapAdminReceiver;
import epsap4.soliton.co.jp.HttpConnectionCtrl;
import epsap4.soliton.co.jp.InformCtrl;
import epsap4.soliton.co.jp.R;
import epsap4.soliton.co.jp.xmlparser.XmlPullParserAided;

////////////////////////////////
// Enrollアクティビティ
////////////////////////////////
public class EnrollActivity extends Activity
	implements View.OnClickListener  {
	
	static final int RESULT_ENABLE = 1;
	
	// 変数
	private Button m_ButtonLogout;
	private Button m_ButtonEnrollment;
	private TextView m_ErrorMessage;
	private Button m_ButtonEndPscode;
	private Button m_ButtonStartPscode;
	
	private static InformCtrl m_InformCtrl;
	private XmlPullParserAided m_p_aided = null;
	
	DevicePolicyManager m_DPM;
    ComponentName m_DeviceAdmin;
	
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	
    	this.setTitle(R.string.ApplicationTitle);
    	
    	m_DPM = (DevicePolicyManager)getSystemService(Context.DEVICE_POLICY_SERVICE);
        m_DeviceAdmin = new ComponentName(EnrollActivity.this, EpsapAdminReceiver.class);
        
    	super.onCreate(savedInstanceState);
    	
    	setContentView(R.layout.enroll);
    	
    	// 変数割り当てとコールバックの登録
    	setUItoMember();
    }

    private void setUItoMember() {
    	// 変数割り当て
    	m_ButtonLogout = (Button)findViewById(R.id.ButtonLogout);
    	m_ButtonEnrollment = (Button)findViewById(R.id.ButtonEnrollment);
    	m_ButtonEndPscode = (Button)findViewById(R.id.ButtonEndpasscode);
    	m_ButtonStartPscode = (Button)findViewById(R.id.ButtonStartpasscode);
    	
    	m_ErrorMessage = (TextView)findViewById(R.id.ErrorEnrollMessage);
    	m_ErrorMessage.setTextColor(Color.rgb(255,20,20));
    	
    	// コールバック
    	m_ButtonLogout.setOnClickListener(this);
    	m_ButtonEnrollment.setOnClickListener(this);
    	m_ButtonEndPscode.setOnClickListener(this);
    	m_ButtonStartPscode.setOnClickListener(this);
    	
    	// 情報管理クラスの取得
    	Intent intent = getIntent();
    	m_InformCtrl = (InformCtrl)intent.getSerializableExtra("Inform");
    	
    	Log.i("EnrollActivity::setUItoMember", m_InformCtrl.GetCookie());
    	Log.i("EnrollActivity::setUItoMember", m_InformCtrl.GetMessage());
    	
    	

    }
    
	@Override
	public void onClick(View clickParameter) {
		Log.i("EnrollActivity::onClick", "Start.");
		
		if(clickParameter == m_ButtonLogout) {
			Log.i("EnrollActivity::onClick", "Logout Click.");
			
			//String message = m_InformCtrl.GetCookie();
			//m_InformCtrl.SetMessage(message);
			
			HttpConnectionCtrl conn = new HttpConnectionCtrl(this);
			boolean ret = conn.RunHttpLogoutUrlConnection(m_InformCtrl);
			if (ret == false) {
				Log.e("EnrollActivity::onClick", "Logout Error.");
				m_ErrorMessage.setText(R.string.LogoutErrorMessage);
				return;
			}
			
			// 返信コードチェック
			// ログイン結果
			if(m_InformCtrl.GetRtn().equalsIgnoreCase(/*"NG"*/getText(R.string.NGkey).toString())) {
				// "NG"ならログアウト失敗
				Log.e("MdmServiceActivity::onClick", "Login NG.");
				m_ErrorMessage.setText(R.string.LogoutErrorMessage);
				return;
			}
			
			finish();	// 問題なければダイアログ終了
			//return;
		} else if(clickParameter == m_ButtonEnrollment) {
			Log.i("EnrollActivity::onClick", "Enroll Click.");
			
			///////////////////////////////////////////////////
			// Enroll要求
			///////////////////////////////////////////////////
			String str_userid = m_InformCtrl.GetUserID();
			String str_passwd = m_InformCtrl.GetPassword();
			
			// メッセージ
			String message = "USER_ID=" + str_userid + "&" + "PASSWORD=" + str_passwd;
			
			m_InformCtrl.SetMessage(message);
			
			HttpConnectionCtrl conn = new HttpConnectionCtrl(this);
			boolean ret = conn.RunHttpEnrollUrlConnection(m_InformCtrl);
			if (ret == false) {
				Log.e("EnrollActivity::onClick", "Enroll Error1.");
				m_ErrorMessage.setText(R.string.EnrollErrorMessage);
				return;
			}
			
			// enroll結果
			if(m_InformCtrl.GetRtn().equalsIgnoreCase(/*"NG"*/getText(R.string.NGkey).toString())) {
				// "NG"ならログイン失敗
				Log.e("EnrollActivity::onClick", "Enroll NG.");
				m_ErrorMessage.setText(R.string.EnrollErrorMessage);
				return;
			}
			
			///////////////////////////////////////////////////
			// Enroll応答の解析
			///////////////////////////////////////////////////
			// 取得XMLのパーサー
			m_p_aided = new XmlPullParserAided(this, m_InformCtrl.GetRtn(), 2);	// 最上位dictの階層は2になる
			ret = m_p_aided.TakeApart();
			if (ret == false) {
				Log.e("EnrollActivity::onClick", "Enroll xml analyze");
				m_ErrorMessage.setText(R.string.EnrollErrorMessage);
				return;
			}

			ret = m_p_aided.TakeApartUserAuthenticationResponse(m_InformCtrl);
			if (ret == false) {
				Log.e("EnrollActivity::onClick", "TakeApart2 false");
				m_ErrorMessage.setText(R.string.EnrollErrorMessage);
				return;
			}
			
			///////////////////////////////////////////////////
			// 解析がOKなら次はConfigrationメッセージを構築して送信
			///////////////////////////////////////////////////
			String sendmsg = "dummy";//m_p_aided.DeviceInfoText();
			
			Log.i("Enroll RTN MESSAGE", sendmsg);
			
			m_InformCtrl.SetMessage(sendmsg);
			
			ret = conn.RunHttpDeviceCertUrlConnection(m_InformCtrl);
			if (ret == false) {
				Log.e("EnrollActivity::onClick", "Configration Error.");
				m_ErrorMessage.setText(R.string.ConfigrationErrorMessage);
				return;
			}

			///////////////////////////////////////////////////
			// サーバからのConfigration応答の解析
			///////////////////////////////////////////////////
			// 新しくXmlPullParserAidedを作成する.
			m_p_aided = new XmlPullParserAided(this, m_InformCtrl.GetRtn(), 2);	// 最上位dictの階層は2になる
			
			/*ret = m_p_aided.TakeApart();
			if (ret == false) {
				Log.e("EnrollActivity::onClick", "Enroll xml analyze");
				m_ErrorMessage.setText(R.string.EnrollErrorMessage);
				return;
			}*/

			ret = m_p_aided.TakeApartControll(/*m_InformCtrl*/);
			if (ret == false) {
				Log.e("EnrollActivity::onClick", "TakeApartControll false");
				m_ErrorMessage.setText(R.string.ConfigrationErrorMessage);
				return;
			}
			
			////// Configrationに従って、Androidの制御を行う
			
			addDeviceAdmin();
			
			//XmlDictionary p_dict = m_p_aided.GetDictionary();		// XmlPullParserAidedクラスで分類され、XmlDictionaryに振るいわけされた要素を取得
			ConfigrationDivision p_conf = new ConfigrationDivision(this, m_p_aided, m_DPM, m_DeviceAdmin);
			p_conf.RunConfigration();
		} else if (clickParameter == m_ButtonEndPscode) {
			if(isDeviceAdmin() == true) {
				Log.i("EPS-ip", "AdminReceiver end.");
				m_DPM.removeActiveAdmin(m_DeviceAdmin);
				updateUi();
			}
		} else if (clickParameter == m_ButtonStartPscode) {
			if(isDeviceAdmin() == false) {
				Log.i("EPS-ip", "AdminReceiver start.");
				addDeviceAdmin();
				updateUi();
			}
		}
	}
	
	// xmlパーサーの取得
	public XmlPullParserAided getXmlParser () { return m_p_aided; }

	// UIのグレーアウト状況
	private void updateUi() {
    	boolean running = isDeviceAdmin();
    	m_ButtonEndPscode.setEnabled(running);
    	m_ButtonStartPscode.setEnabled(!running);
    	
    	m_ButtonEnrollment.setEnabled(running);
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
    	Log.i("EnrollActivity", "addDeviceAdmin");
    	// Launch the activity to have the user enable our admin.
        Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
        intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, m_DeviceAdmin);
        intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION,
                "Additional text explaining why this needs to be added.");
        startActivityForResult(intent, RESULT_ENABLE);
    }
    
    private boolean isDeviceAdmin() {
    	return m_DPM.isAdminActive(m_DeviceAdmin);
    }
    
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case RESULT_ENABLE:
                if (resultCode == Activity.RESULT_OK) {
                    Log.i("DeviceAdmin", "Admin enabled!");
                } else {
                    Log.i("DeviceAdmin", "Admin enable FAILED!");
                }
                return;
        }

        super.onActivityResult(requestCode, resultCode, data);
    }
}