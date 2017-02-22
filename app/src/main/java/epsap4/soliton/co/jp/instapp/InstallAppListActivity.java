/**
 * 
 */
package epsap4.soliton.co.jp.instapp;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import epsap4.soliton.co.jp.AddressInfo;
import epsap4.soliton.co.jp.activity.CertRequestActivity;
import epsap4.soliton.co.jp.HttpConnectionCtrl;
import epsap4.soliton.co.jp.InformCtrl;
import epsap4.soliton.co.jp.ListItem;
import epsap4.soliton.co.jp.R;
import epsap4.soliton.co.jp.StringList;
import epsap4.soliton.co.jp.xmlparser.XmlApplicationPiece;
import epsap4.soliton.co.jp.xmlparser.XmlPullParserAided;

/**
 * @author ymori
 *
 */
public class InstallAppListActivity extends ListActivity
	implements Runnable,View.OnClickListener{

	private Button m_MailButton;
	private Button m_PhoneButton;
	
	private ProgressDialog progressDialog;
	
	private static InformCtrl m_InformCtrl;
	private XmlPullParserAided m_p_aided = null;
	private String m_str_uuid;
	private String m_str_apk;
	
	private InstallAppAdapter adapter;
	private TextView m_strempty;
	private boolean m_b_empty = false;		// リストの有無
	
	int m_nConnectionActionType;
	private static int CONN_LIST_GET = 10;
	private static int CONN_ITEM = 11;
	
	/**
	 * 
	 */
	public InstallAppListActivity() {
		
	}

	public void onCreate(Bundle applistbundle) {
		this.setTitle(R.string.ApplicationTitle);
		super.onCreate(applistbundle);
		
		// ActionBar
	//	getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
		
		setContentView(R.layout.selectlist);
		
		m_strempty = (TextView)findViewById(R.id.android_empty);
		m_strempty.setText("");
		
		// 情報管理クラスの取得
    	Intent intent = getIntent();
    	m_InformCtrl = (InformCtrl)intent.getSerializableExtra(StringList.m_str_InformCtrl);
    	
    	setUItoMember();    	
    	
    	// 通信中ダイアログを表示させる。
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle(R.string.progress_title);
        progressDialog.setMessage(getText(R.string.progress_message).toString());
        progressDialog.setIndeterminate(false);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.show();
        
        // サーバーとの通信をスレッドで行う
        m_nConnectionActionType = CONN_LIST_GET;
        Thread thread = new Thread(this);	// 自分クラスをスレッドの引数に渡して...
        thread.start();						// run()が実行される
		
		
     
	}
	
	private void setUItoMember() {
		// 連絡先
		m_MailButton = (Button)findViewById(R.id.Button_Mail);
		m_PhoneButton = (Button)findViewById(R.id.Button_Phone);
		m_MailButton.setSingleLine();		// 行末省略...
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

	@Override
	public void onClick(View v) {
		// TODO 自動生成されたメソッド・スタブ
		if(v == m_MailButton) {
			Log.i("InstallAppListActivity::onClick", "Mail");
			AddressInfo.Runmailer(this);
			// アクティビティの終了
		//	finish();
		} else if(v == m_PhoneButton) {
			Log.i("InstallAppListActivity::onClick", "Phone");
			AddressInfo.RunTelephone(this);
		}
	}
	
	@Override
	// クリックしたリストアイテムの座標から値を取得し、配下のリストアイテムを表示するため、
	// AppListActivityをネストする
	protected void onListItemClick(ListView listView, View v, int position, long id) {
		super.onListItemClick(listView, v, position, id);  
		
		// UUIDをメッセージに使用するのでクラス変数登録する.
		ListItem item =(ListItem) listView.getItemAtPosition(position);
		String str_profilename = item.getText();
		m_str_uuid = item.getUUIDText();
		m_str_apk = item.getApk();
		Log.v("InstallAppListActivity::onListItemClick", String.format("Selected: %s", str_profilename));
		Log.v("InstallAppListActivity::onListItemClick", String.format("ID: %s", m_str_uuid));
		
		
		 
		 progressDialog = new ProgressDialog(this);
		 progressDialog.setTitle(R.string.progress_title);
		 progressDialog.setMessage(getText(R.string.progress_message).toString());
		 progressDialog.setIndeterminate(false);
		 progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		 progressDialog.show();
	        
		 // サーバーとの通信をスレッドで行う
		 m_nConnectionActionType = CONN_ITEM;
		 Thread thread = new Thread(this);	// 自分クラスをスレッドの引数に渡して...
		 thread.start();		
	}

	@Override
	public void run() {
		// TODO 自動生成されたメソッド・スタブ
		if (m_nConnectionActionType == CONN_LIST_GET) {
			// アプリケーションリスト取得
			Log.v("InstallAppListActivity::run", "run_HttpGetAppList call");
			run_HttpGetAppList();
		} else if(m_nConnectionActionType == CONN_ITEM) {
			// アプリケーションアイテムを選択
			run_HttpInstApplication();
		}
		
		handler.sendEmptyMessage(0);
	}
	
	private Handler handler = new Handler() {
		 public void handleMessage(Message msg) {
			 // 処理終了時の動作をここに記述。

			 if (m_nConnectionActionType == CONN_LIST_GET) {
				 if(m_b_empty == true) m_strempty.setText(R.string.list_no_data);
				 // http://stackoverflow.com/questions/3047999/call-setlistadapter-from-inside-a-thread
				 // リストの設定
				 setListAdapter(adapter);	// run()に置くとErrorが発生してしまうので
			 } else if(m_nConnectionActionType == CONN_ITEM) {
				 // ダウンロードしたapkを削除する
				 DeleteDLFile();
			 }
			
			// プログレスダイアログ終了
			CertRequestActivity.endProgress(progressDialog);			 
			 
			// ↓ Debug
/*			AddressInfo.SetMailAddress("yoshinobu.mori@soliton.co.jp");
			m_MailButton.setVisibility(View.VISIBLE);
			String strmsg = getText(R.string.MailRequest).toString() + "yoshinobu.mori@soliton.co.jp";
			m_MailButton.setText(strmsg);
			
			AddressInfo.SetPhoneNumber("090-XXXX-XXXX");
			m_PhoneButton.setVisibility(View.VISIBLE);
			String strmsg2 = getText(R.string.PhoneRequest).toString() + "090-XXXX-XXXX";
			m_PhoneButton.setText(strmsg2);
*/
		 }
	 };
	 
	 // EPS-apへアプリケーションリストの要求を行い、リストアダプタを形成する
	 private void run_HttpGetAppList() {
		 boolean ret = HttpGetAppList();
		 if (ret == false) {
			 
			 CertRequestActivity.endProgress(progressDialog);
			 return;
		 }
			
		 //String[] items = {"1つ目","2つ目","3つ目"};
		 List<ListItem> files_member = new ArrayList<ListItem>();
			
		 SetXmlParserInfo(files_member);
			
		 if(files_member.isEmpty()) m_b_empty = true;
		 
		 adapter = new InstallAppAdapter(this, R.layout.unique/*android.R.layout.simple_expandable_list_item_1*/, files_member/*, items*/);
		 
	 }
	 
	 // EPS-apとのアプリケーションリスト要求/応答/解析を行う
	 private boolean HttpGetAppList() {

		String message = "Action=AppList";
		m_InformCtrl.SetMessage(message);
			
		// RunHttp～でCookieをセットすることを忘れないように。  RunHttpDeviceCertUrlConnection参照
		// CertLoginActivity::http_user_auth()でSetLoginCookieを呼んでCookieをセットしている
		
		// RunHttpGetProfileListで要求/取得を行う
		HttpConnectionCtrl conn = new HttpConnectionCtrl(this);
		boolean ret = conn.RunHttpGetApplicationList(m_InformCtrl);
			
		if (ret == false) {
		//	Log.e("ProfileListActivity::HttpGetProfileList", "Get Profile Error1.");
			 setResult(StringList.RESULT_HTTP_CON_ERR);
			finish();
			return ret;
		}
			
			//<== debug
			//String data = ReadAndSetWifiInfo();
			//==>
			
		// 取得XMLのパーサー
		m_p_aided = new XmlPullParserAided(this, m_InformCtrl.GetRtn()/*data*/, 2);	// 最上位dictの階層は2になる
		ret = m_p_aided.TakeApartApplicationList();
		if (ret == false) {
			Log.e("InstallAppListActivity::HttpGetAppList", "Get AppList error.");
			return ret;
		}
			
		return ret;
	}
	 
	 // アプリケーションアイテムを要求/応答/インストーラの実行
	 private void run_HttpInstApplication() {
		 String message = "Action=getApp" + "&" + StringList.m_str_uuid + "=" + m_str_uuid;
		 m_InformCtrl.SetMessage(message);
		 
		 HttpConnectionCtrl conn = new HttpConnectionCtrl(this);
		 boolean ret = conn.RunHttpAppInstConnection(m_InformCtrl, m_str_apk);
	 }
	 
	 protected void SetXmlParserInfo(List<ListItem> files_list) {
		 // parseされたアプリリストを取得. XmlAppPieceクラスを新規に作成(XmlProfilePieceを参考にする)
		 List<XmlApplicationPiece> xmlApppiecceList = m_p_aided.GetApplicationPieceList();
		 
		 // ListItemをnewしてsetIcon, setText, getUUIDTextを実行して値をセットする
		 for(int i = 0; xmlApppiecceList.size() > i; i++){
			 XmlApplicationPiece one_piece = xmlApppiecceList.get(i);
			 String name = one_piece.GetAppName() + "/" + one_piece.GetAppVersion();
			 String uuid = one_piece.GetUUID();
			 String iconstr = one_piece.GetIcon();
			 String apk = one_piece.GetApkName();
			
			 ListItem listitem = new ListItem();
			 listitem.setText(name);
			 listitem.setUUIDIDText(uuid);
			 listitem.setIcon(iconstr);
			 listitem.setApk(apk);
			 files_list.add(listitem);
		}
	 }
	 
	 // ダウンロードしたapkを削除する
	 private void DeleteDLFile() {
		// SDカードの設定
		 if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED) == false) {
			 // SDカード領域が存在しないときは抜ける
			 return;
		 }
		 
		 String log_path = getExternalFilesDir(null).getPath() + "/" + m_str_apk;
		 
		 File filename = new File(log_path);
		 if(filename.exists()) {
			filename.delete();
				
		}
	 }
	 
}
