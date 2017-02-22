package epsap4.soliton.co.jp.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.WindowManager.LayoutParams;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import epsap4.soliton.co.jp.AddressInfo;
import epsap4.soliton.co.jp.HttpConnectionCtrl;
import epsap4.soliton.co.jp.InformCtrl;
import epsap4.soliton.co.jp.LogCtrl;
import epsap4.soliton.co.jp.R;
import epsap4.soliton.co.jp.StringList;

public class CertRequestActivity extends Activity
	implements View.OnClickListener, Runnable {
	
	// UIの変数設定
	private Button m_ButtonApply;		// 「申請」ボタン
	private Button m_ButtonApplyCng;	// 「申請変更」ボタン
	private Button m_ButtonApplyBack;	// 「申請取り消し」ボタン
	
	private EditText m_EditAddress01;	// MailAddress
	private EditText m_EditMemo;		// Memo
	
	private TextView m_TextApply;		// 未申請
	private TextView m_TextApproval;	// 未承認
	private TextView m_TextApplyErr;
	
	// 連絡先
	private Button m_MailButton;
	private Button m_PhoneButton;
	
	private static InformCtrl m_InformCtrl;
	private ProgressDialog progressDialog;
	
	int m_nFragActionType;
	private static int POST_APPLY = 10;
	private static int POST_MODIFY = 11;
	private static int POST_DROP = 12;
	
	int m_nErroType;
	public static int ERR_FORBIDDEN = 20;
	public static int ERR_UNAUTHORIZED = 21;
	public static int SUCCESSFUL = 22;
	public static int ERR_NETWORK = 23;
	public static int AUTHENTICATION_SUCCESSFUL =24;
	public static int ERR_COLON = 25;
	
	public CertRequestActivity() {
		// TODO 自動生成されたコンストラクター・スタブ
	}
	
	 @Override
	public void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		
		this.getWindow().setSoftInputMode(LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		
		setContentView(R.layout.no_cert);
		 
		// 情報管理クラスの取得
		Intent intent = getIntent();
		m_InformCtrl = (InformCtrl)intent.getSerializableExtra(StringList.m_str_InformCtrl);
	    	
		setUItoMember();
	    	

	}
	 
	private void setUItoMember() {
		// Button
		m_ButtonApply = (Button)findViewById(R.id.ButtonApply);
		m_ButtonApplyCng = (Button)findViewById(R.id.ButtonApplyChange);
		m_ButtonApplyBack = (Button)findViewById(R.id.ButtonApplyBack);

		m_ButtonApply.setOnClickListener(this);
		m_ButtonApplyCng.setOnClickListener(this);
		m_ButtonApplyBack.setOnClickListener(this);
		
		// EditText
		m_EditAddress01 = (EditText)findViewById(R.id.EditAddress01);
		m_EditAddress01.setText(m_InformCtrl.GetMailAddress());
		m_EditMemo = (EditText)findViewById(R.id.EditMemo);
		m_EditMemo.setText(m_InformCtrl.GetDescription());
		
		// TextView
		m_TextApply = (TextView)findViewById(R.id.textViewNoApply);
		m_TextApproval = (TextView)findViewById(R.id.textViewNoApproval);
		
		m_TextApplyErr = (TextView)findViewById(R.id.apply_set_err);	// エラーメッセージ
		m_TextApplyErr.setTextColor(Color.rgb(255,20,20));
		m_TextApplyErr.setVisibility(View.GONE);							// 初期設定では非表示
		
		if (m_InformCtrl.GetSubmitted() == 6) { // true
			m_ButtonApply.setVisibility(View.GONE);
			m_TextApply.setVisibility(View.GONE);
		} else {	// false
			m_ButtonApplyCng.setVisibility(View.GONE);
			m_ButtonApplyBack.setVisibility(View.GONE);
			m_TextApproval.setVisibility(View.GONE);
		}
		
		// 連絡先
		m_MailButton = (Button)findViewById(R.id.Button_Mail);
		m_PhoneButton = (Button)findViewById(R.id.Button_Phone);
		m_MailButton.setSingleLine();
		m_MailButton.setOnClickListener(this);
		m_PhoneButton.setOnClickListener(this);
		if(AddressInfo.GetMailAddress().length() > 0) {
			m_MailButton.setVisibility(View.VISIBLE);
			String strmsg = getText(R.string.MailRequest).toString() + AddressInfo.GetMailAddress();
			m_MailButton.setText(strmsg);
		} else m_MailButton.setVisibility(View.GONE);
		if (AddressInfo.GetPhoneNumber().length() > 0) {
			m_PhoneButton.setVisibility(View.VISIBLE);
			String strmsg = getText(R.string.PhoneRequest).toString() + AddressInfo.GetPhoneNumber();
			m_PhoneButton.setText(strmsg);
		} else m_PhoneButton.setVisibility(View.GONE);
	}
	
	private void HideViewMember() {
		if (m_nErroType != SUCCESSFUL) return;
		
		if ((m_nFragActionType == POST_APPLY) || (m_nFragActionType == POST_MODIFY)) {
			m_ButtonApplyCng.setVisibility(View.VISIBLE);
			m_ButtonApplyBack.setVisibility(View.VISIBLE);
			m_TextApproval.setVisibility(View.VISIBLE);
			
			m_ButtonApply.setVisibility(View.GONE);
			m_TextApply.setVisibility(View.GONE);
		} else if (m_nFragActionType == POST_DROP) {
			m_ButtonApply.setVisibility(View.VISIBLE);
			m_TextApply.setVisibility(View.VISIBLE);
			
			m_ButtonApplyCng.setVisibility(View.GONE);
			m_ButtonApplyBack.setVisibility(View.GONE);
			m_TextApproval.setVisibility(View.GONE);
		}
	}

	@Override
	public void run() {
		// TODO 自動生成されたメソッド・スタブ
		http_request();
		handler.sendEmptyMessage(0);
	}
    
    private Handler handler = new Handler() {
		 public void handleMessage(Message msg) {
			 // 処理終了時の動作をここに記述。
		//	 SetEditMember();
			 // プログレスダイアログ終了
			 progressDialog.dismiss();
			 if (m_nErroType == ERR_FORBIDDEN) {
				 String str_forbidden = getString(R.string.Forbidden);
				 m_TextApplyErr.setVisibility(View.VISIBLE);
			///	 m_TextApplyErr.setText(R.string.Forbidden);
				 m_TextApplyErr.setText(m_InformCtrl.GetRtn().substring(str_forbidden.length()));
			 } else if (m_nErroType == ERR_UNAUTHORIZED) {
				 String str_unauth = getString(R.string.Unauthorized);
				 m_TextApplyErr.setVisibility(View.VISIBLE);
			//	 m_TextApplyErr.setText(R.string.Unauthorized);
				 m_TextApplyErr.setText(m_InformCtrl.GetRtn().substring(str_unauth.length()));
			 } else if (m_nErroType == ERR_NETWORK) {
				 m_TextApplyErr.setVisibility(View.VISIBLE);
				 m_TextApplyErr.setText(R.string.LoginErrorMessage);
			 } else if (m_nErroType == ERR_COLON) {
				 String str_err = getString(R.string.ERR);
				 m_TextApplyErr.setVisibility(View.VISIBLE);
				 m_TextApplyErr.setText(m_InformCtrl.GetRtn().substring(str_err.length()));
			 } else finish();

			 HideViewMember();


		 }
	};

	private void http_request() {
		// ログインボタン
		LogCtrl.Logger(LogCtrl.m_strInfo, "CertRequestActivity::http_request", this);
				
				
		// ログインメッセージ
		String message = "";
		String message_ma = "&" + "MailAddress=";	// #26556
		String message_dc = "&" + "Description=";	// #26556
		if (m_EditAddress01.getText().toString().length() > 0) {
			try {
				message_ma = message_ma + URLEncoder.encode(m_EditAddress01.getText().toString(), "UTF-8");
			} catch (UnsupportedEncodingException e) {
				// TODO 自動生成された catch ブロック
				e.printStackTrace();
			}
			//m_EditAddress01.getText().toString();//m_InformCtrl.GetMailAddress();
		}
		if (m_EditMemo.getText().toString().length() > 0) {
			try {
				message_dc = message_dc + URLEncoder.encode(m_EditMemo.getText().toString(), "UTF-8");
			} catch (UnsupportedEncodingException e) {
				// TODO 自動生成された catch ブロック
				e.printStackTrace();
			}// m_EditMemo.getText().toString();//m_InformCtrl.GetDescription();
		}
		
		if (m_nFragActionType == POST_APPLY) {
			message = "Action=apply" + message_ma + message_dc;
		} else if (m_nFragActionType == POST_MODIFY) {
			message = "Action=modify" + message_ma + message_dc;
		} else if (m_nFragActionType == POST_DROP) {
			message = "Action=drop";
		}
		LogCtrl.Logger(LogCtrl.m_strInfo, "CertRequestActivity:: "+ "LoginMsg=" + message, this);
				
		// 入力データを情報管理クラスへセットする
		m_InformCtrl.SetMessage(message);
				
		// 申請
		HttpConnectionCtrl conn = new HttpConnectionCtrl(this);
		boolean ret = conn.RunHttpApplyUrlConnection(m_InformCtrl);		// 専用のRunHttpを作成する
		if (ret == false) {
			LogCtrl.Logger(LogCtrl.m_strError, "CertRequestActivity::onClick "+ "Login Error.", this);
//			m_TextErrorLogin.setText(R.string.LoginErrorMessage);
			m_nErroType = ERR_NETWORK;
			return;
		}
				
		LogCtrl.Logger(LogCtrl.m_strInfo, "CertLoginAcrivity::onClick:RTN "+ m_InformCtrl.GetRtn(), this);
//		Log.i("CertLoginAcrivity:Gookie:", m_InformCtrl.GetCookie());
		//RunHttpLoginDefaultHttpClient();
		//RunHttpLoginUrlConnection();
		
		if(m_InformCtrl.GetRtn().startsWith(getText(R.string.Forbidden).toString())) {
			LogCtrl.Logger(LogCtrl.m_strError, "CertRequestActivity::onClick "+ "Forbidden.", this);
			m_nErroType = CertRequestActivity.ERR_FORBIDDEN;
			return;
		} else if (m_InformCtrl.GetRtn().startsWith(getText(R.string.Unauthorized).toString())) {
			LogCtrl.Logger(LogCtrl.m_strError, "CertRequestActivity::onClick "+ "Unauthorized.", this);
			m_nErroType = CertRequestActivity.ERR_UNAUTHORIZED;
			return;
		} else if (m_InformCtrl.GetRtn().startsWith(getText(R.string.ERR).toString())) {
			LogCtrl.Logger(LogCtrl.m_strError, "CertLoginAcrivity::onClick  "+ "ERR:", this);
			m_nErroType = CertRequestActivity.ERR_COLON;
			return;
		}
		
		m_nErroType = SUCCESSFUL;

	}
	
	@Override
	public void onClick(View arg0) {
		// TODO 自動生成されたメソッド・スタブ
		Log.i("CertRequestActivity::onClick", "Start.");
		// TODO 自動生成されたメソッド・スタブ
		
		if(arg0 == m_ButtonApply) {			        
	        // 申請フラグON
	        m_nFragActionType = POST_APPLY;
			
		} else if (arg0 == m_ButtonApplyCng) {	        
	        // 申請変更フラグON
	        m_nFragActionType = POST_MODIFY;
	        		
		} else if (arg0 == m_ButtonApplyBack) {       
	        // 申請取り消しフラグON
	        m_nFragActionType = POST_DROP;
		} else if(arg0 == m_MailButton) {
			Log.i("CertRequestActivity::onClick", "Mail");
			AddressInfo.Runmailer(this);
		} else if(arg0 == m_PhoneButton) {
			Log.i("CertRequestActivity::onClick", "Phone");
			AddressInfo.RunTelephone(this);
		} else return;
		 
		m_TextApplyErr.setVisibility(View.GONE);
		
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
	}

	@Override
    protected void onPause() { 
		super.onPause();
		endProgress(progressDialog);		
	}
	
	static public void endProgress(ProgressDialog dlg) {
		if(dlg != null) {
			dlg.dismiss();
		}
	}
}
