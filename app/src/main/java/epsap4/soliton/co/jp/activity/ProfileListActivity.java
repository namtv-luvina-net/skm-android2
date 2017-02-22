package epsap4.soliton.co.jp.activity;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.util.Xml;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import org.xmlpull.v1.XmlSerializer;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import epsap4.soliton.co.jp.AddressInfo;
import epsap4.soliton.co.jp.FileAdapter;
import epsap4.soliton.co.jp.HttpConnectionCtrl;
import epsap4.soliton.co.jp.InformCtrl;
import epsap4.soliton.co.jp.ListItem;
import epsap4.soliton.co.jp.LogCtrl;
import epsap4.soliton.co.jp.R;
import epsap4.soliton.co.jp.StringList;
import epsap4.soliton.co.jp.xmlparser.XmlDictionary;
import epsap4.soliton.co.jp.xmlparser.XmlProfilePiece;
import epsap4.soliton.co.jp.xmlparser.XmlPullParserAided;
import epsap4.soliton.co.jp.xmlparser.XmlStringData;


public class ProfileListActivity extends ListActivity
	/*implements View.OnClickListener AdapterView.OnItemClickListener*/ 
	implements Runnable,View.OnClickListener{
	/** Called when the activity is first created. */
	
	private Button m_rtnButton;	// 戻るボタン
	private ListView m_lstInf;
	private Button m_MailButton;
	private Button m_PhoneButton;
	
	private ProgressDialog progressDialog;
	
	private static InformCtrl m_InformCtrl;
	private XmlPullParserAided m_p_aided = null;
	
	private FileAdapter adapter;
	private TextView m_strempty;
	private boolean m_b_empty = false;		// リストの有無
	private boolean m_b_scep = false;		// SCEP Profileの実行権
	private int m_i_iconProfile = R.mipmap.profile;
	private int m_i_iconCertificate = R.mipmap.certificate;
	
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
        Thread thread = new Thread(this);	// 自分クラスをスレッドの引数に渡して...
        thread.start();						// run()が実行される
		
		/*NotificationManager nm;
		nm = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
	
		PendingIntent intent = PendingIntent.getActivity(this, 0, 
				new Intent(this, hqz.soliton.so.jp.HiQZen4AndroidActivity.class), 0);
		SKMNotification notification =
			new SKMNotification(R.drawable.mogura, "dyrectry", System.currentTimeMillis());
		notification.setLatestEventInfo(this, "contentTitle", "contentText", intent);
	
		nm.cancel(0);
		nm.notify(0, notification);
		*/
		// ここでUIとソースの結びつけ ← ListActivity の場合。UIとソースの結びつけは不要
	//	m_rtnButton = (Button)findViewById(R.id.rtnButton);
		//m_lstInf = (ListView)findViewById(R.id.);
		
		// アダプターを設定します        
		//m_lstInf.setAdapter(adapter);
	    
		// コールバック登録
		//m_rtnButton.setOnClickListener(this);
        
     
	}
	
	private void setUItoMember() {
		// staticの値を初期化
		AddressInfo.SetMailAddress("");
    	AddressInfo.SetPhoneNumber("");
    	
		// Button
		m_MailButton = (Button)findViewById(R.id.Button_Mail);
		m_PhoneButton = (Button)findViewById(R.id.Button_Phone);
		
		m_MailButton.setSingleLine();

		m_MailButton.setOnClickListener(this);
		m_PhoneButton.setOnClickListener(this);
		
		m_MailButton.setVisibility(View.GONE);
		m_PhoneButton.setVisibility(View.GONE);

	}
	public void run() {

		//////////////////////////////////////////////
		// サーバーにプロファイルリストの要求       //
		// 1. HttpConnectionCtrlでコネクション確立  //
		// 2. リスト要求-返信情報からリスト(ListItem)の構築   //
		//////////////////////////////////////////////
		boolean ret = HttpGetProfileList();
		if (ret == false) {
			LogCtrl.Logger(LogCtrl.m_strError, "ProfileListActivity::onCreate-- " + "ProfileList Error.", this);
			CertRequestActivity.endProgress(progressDialog);
			return;
		}
		
		// 接続先EPS-apの情報を保存
		WriteEPSapInfo();
		
		//String[] items = {"1つ目","2つ目","3つ目"};
		List<ListItem> files_member = new ArrayList<ListItem>();
		
		// Xmlから取得したファイル情報をメンバに設定する.
		SetXmlParserInfo(files_member);
		
		if(files_member.isEmpty()) m_b_empty = true;

		adapter = new FileAdapter(this, R.layout.unique/*android.R.layout.simple_expandable_list_item_1*/, files_member/*, items*/);
		//setListAdapter(adapter);
		handler.sendEmptyMessage(0);

	}
	
	 private Handler handler = new Handler() {
		 public void handleMessage(Message msg) {
			 // 処理終了時の動作をここに記述。

			if(m_b_empty == true) m_strempty.setText(R.string.list_no_data/*"データが存在しません。"*/);
			// http://stackoverflow.com/questions/3047999/call-setlistadapter-from-inside-a-thread
			// リストの設定
			setListAdapter(adapter);	// run()に置くとErrorが発生してしまうので
			// 連絡先の設定
			if(AddressInfo.GetMailAddress().length() > 0) {
				m_MailButton.setVisibility(View.VISIBLE);
				String strmsg = getText(R.string.MailRequest).toString() + AddressInfo.GetMailAddress();
				m_MailButton.setText(strmsg);
			}
			if (AddressInfo.GetPhoneNumber().length() > 0) {
				m_PhoneButton.setVisibility(View.VISIBLE);
				String strmsg = getText(R.string.PhoneRequest).toString() + AddressInfo.GetPhoneNumber();
				m_PhoneButton.setText(strmsg);
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
	    
	private boolean HttpGetProfileList() {

		String message = "";	// メッセージはひとまず空.
		m_InformCtrl.SetMessage(message);
		
		// RunHttpGetProfileListで要求/取得を行う
		HttpConnectionCtrl conn = new HttpConnectionCtrl(this);
		boolean ret = conn.RunHttpGetProfileList(m_InformCtrl);
		
		if (ret == false) {
			LogCtrl.Logger(LogCtrl.m_strError, "ProfileListActivity::HttpGetProfileList" + "Get Profile Error1.", this);
			setResult(StringList.RESULT_HTTP_CON_ERR);
			finish();
			return ret;
		}
		
		//<== debug
		//String data = ReadAndSetWifiInfo();
		//==>
		
		// 取得XMLのパーサー
		m_p_aided = new XmlPullParserAided(this, m_InformCtrl.GetRtn()/*data*/, 2);	// 最上位dictの階層は2になる
		ret = m_p_aided.TakeApartProfileList();
		if (ret == false) {
			LogCtrl.Logger(LogCtrl.m_strError, "ProfileListActivity::HttpGetProfileList" + "Get Profile Error2.", this);
			return ret;
		}
		
		return ret;
	}
	
	public void onClick(View view) {
		Log.i("ProfileListActivity::onClick", "NULL");
		if(view == m_MailButton) {
			Log.i(StringList.m_str_SKMTag, "ProfileListActivity::onClick-- "+ "Mail");
			AddressInfo.Runmailer(this);
			// アクティビティの終了
		//	finish();
		} else if(view == m_PhoneButton) {
			Log.i(StringList.m_str_SKMTag, "ProfileListActivity::onClick--" + "Phone");
			AddressInfo.RunTelephone(this);
		}
	}

	public void onItemClick(View view) {
		Log.i("ProfileListActivity::onItemClick", "NULL");
	}

	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		 Log.i("FileAdapter::onClick", "NULL");
	 }

	@Override
	// クリックしたリストアイテムの座標から値を取得し、配下のリストアイテムを表示するため、
	// AppListActivityをネストする
	protected void onListItemClick(ListView listView, View v, int position, long id) {
		super.onListItemClick(listView, v, position, id);  
		
		ListItem item =(ListItem) listView.getItemAtPosition(position);
		String str_profilename = item.getText();
		String str_id = item.getIDText();
		LogCtrl.Logger(LogCtrl.m_strVerbose, "AppListActivity::onListItemClick "+ String.format("Selected: %s", str_profilename), this);
		LogCtrl.Logger(LogCtrl.m_strVerbose, "AppListActivity::onListItemClick "+ String.format("ID: %s", str_id), this);
		
		if (item.getIconResource() == m_i_iconProfile) {
			// ディレクトリを選択した場合はさらに配下のリストビューIntentを開く
			LogCtrl.Logger(LogCtrl.m_strVerbose, "AppListActivity::onListItemClick-- " + "Folder Icon", this);
			
			// 次のビューのintentを構成する
//			Intent AppIntent = 
//				new Intent(this, ProfileListActivity.class);
			// 次のビューのリストを新しいintentに引き渡す
//			AppIntent.putExtra("ListItem", item);
//			AppIntent.setAction(Intent.ACTION_VIEW);
			// 新しいビューを開始
//	        startActivity(AppIntent); 
	        
	        // リストから選択されたプロファイルの情報を元に個別のプロファイルアクティビティを作成する
	        Intent AppIntent = new Intent(this, ProfileActivity.class);
	        // ビューのリストを新しいintentに引き渡す.HTTP通信もそちらで行う。
			AppIntent.putExtra(StringList.m_str_ListItem, item);
			AppIntent.putExtra(StringList.m_str_InformCtrl, m_InformCtrl);
			
			startActivityForResult(AppIntent, 0);
	
		} else if(item.getIconResource() == m_i_iconCertificate) {
			// selected certificate icon 
			LogCtrl.Logger(LogCtrl.m_strVerbose, "AppListActivity::onListItemClick-- " + "Certificate Icon", this);
			
			Intent AppIntent;
//			if (ReadLoginUserInfo() == true) {
				AppIntent = new Intent(this, CertLoginActivity.class);
//			} else {
//				AppIntent = new Intent(this, CertGuidanceActivity.class);
//			}
			
			AppIntent.putExtra(StringList.m_str_InformCtrl, m_InformCtrl);
			startActivityForResult(AppIntent, 0);
			
		} else {
			// ファイルを選択し場合、保存箇所を選ばせ、保存後実行する
			Log.v("AppListActivity::onListItemClick", "File Icon");
			
			// Debug用テストデータ作成
			String testdata = "yoshim test data";
			OutputStream out=null;
			try{
				out = openFileOutput("/data/data/epsap3.soliton.so.jp/test.txt", Context.MODE_PRIVATE);
				out.write(testdata.getBytes(), 0, testdata.getBytes().length);
				
				out.close();
			} catch (Exception e) {
				Log.e("AppListActivity::onListItemClick", "Exception");
				e.printStackTrace();
			}
			
			// DL後→保存→実行
			OpenFile("/data/data/epsap3.soliton.so.jp/test.txt"/*"/system/etc/security/cacerts.bks"*/);
		}
	}  
	
	
		
	// 子Activityからアプリを終了する方法
	// 参照:http://ymgcsng.blogspot.jp/2010/12/activity.html
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
	    if (resultCode == StringList.RESULT_CLOSE) {
	        setResult(StringList.RESULT_CLOSE);
	        finish();
	    }
	}
	
	@Override
    protected void onPause() { 
		super.onPause();
		CertRequestActivity.endProgress(progressDialog);
	}

	//ParseされたプロファイルリストをListActivity表示用のListItemに割り当てる
	protected void SetXmlParserInfo(List<ListItem> files_list) {
		// <= プロファイルリスト
		// parseされたprofilelistを取得
		List<XmlProfilePiece> xml_piece = m_p_aided.GetProfilePieceList();
		LogCtrl.Logger(LogCtrl.m_strInfo, 
				"ProfileListActivity::SetXmlParserInfo ListSize = " + Integer.toString(xml_piece.size()), this);
		for(int i = 0; xml_piece.size() > i; i++){
			XmlProfilePiece one_piece = xml_piece.get(i);
			String name = one_piece.GetProfileName();
			String id_num = one_piece.GetId();

			files_list.add(new ListItem(m_i_iconProfile, name, id_num));	// idは隠しパラメータ
		}
		// プロファイルリスト ==>
		
		// <== 証明書取得の有無及び、メール、TEL
		//** 「証明書インポート」リストアイテムを最下層に追加する(隠しパラメータのidは不要) **//
		XmlDictionary xmldict = m_p_aided.GetDictionary();
		if(xmldict != null) {
			List<XmlStringData> str_list;
			str_list = xmldict.GetArrayString();			
			for(int i = 0; str_list.size() > i; i++){
				// config情報に従って、処理を行う.
				XmlStringData p_data = str_list.get(i);
				SetConfigrationChild(p_data);
			}
			
		}
		
		// 「証明書導入」の有無
		if(m_b_scep == true/*false*/) {
			files_list.add(new ListItem(m_i_iconCertificate, this.getText(R.string.Certificate_import).toString()));
		}
		// 証明書取得の有無及び、メール、TEL ==>
		
		/////////////////////////////////////////////////////////////////////////////////////////////////
		// インスタントプロファイルなし & 証明書導入ありの場合、CertLoginActivityに直接遷移する
		// na-prj #13859 条件追加：URL Scheme呼び出しでautoconnect = trueのとき
		/////////////////////////////////////////////////////////////////////////////////////////////////
		if(((xml_piece.size() == 0) && (m_b_scep == true))
				|| ((m_InformCtrl.GetAutoConnect() == true)  && (m_b_scep == true))) {
			Intent AppIntent;

			AppIntent = new Intent(this, CertLoginActivity.class);
			AppIntent.putExtra(StringList.m_str_InformCtrl, m_InformCtrl);
			startActivityForResult(AppIntent, 0);
			
			finish();
		}

//		files_list.add(new ListItem(R.drawable.ic_menu_archive, "プロファイル 001"));
//		files_list.add(new ListItem(R.drawable.ic_menu_compose, "プロファイル 002"));
//		files_list.add(new ListItem(R.drawable.ic_menu_archive, "Wi-Fi設定プロファイル"));
		
		
		// リストアイテムのネスト (テストデータ)
//		Intent intent = getIntent();
//		ListItem origin_item = (ListItem)intent.getSerializableExtra("ListItem");
		// 最初のリスト呼び出しのときは空なのでnullチェックを行わないと異常終了してしまう
//		if(origin_item != null) {
//			Log.i("AppListActivity::SetXmlParserInfo", "TRACE1");
//			files_list.add(origin_item);
//		}
			
		
	}
	
	private void SetConfigrationChild(XmlStringData p_data) {
		String strKeyName = p_data.GetKeyName();	// キー名
		int    i_type = p_data.GetType();		// 要素タイプ(string:1, data=2, date=3, real=4, integer=5, true=6, false=7)
		String strData = p_data.GetData();		// 要素
		
		
		boolean b_type = true;
		if(i_type == 7) b_type = false;
		
		if(strKeyName.equalsIgnoreCase(StringList.m_str_scepprofile)) {
			m_b_scep = b_type;
		} else if (strKeyName.equalsIgnoreCase(StringList.m_str_pflist_addr)) {
			AddressInfo.SetMailAddress(strData);
		} else if (strKeyName.equalsIgnoreCase(StringList.m_str_pflist_phone)) {
			AddressInfo.SetPhoneNumber(strData);
		}
		
	}

	// ファイル出力
	public void WriteEPSapInfo() {
		String retmsg = "";
		
		XmlSerializer serializer = Xml.newSerializer();
		StringWriter writer = new StringWriter();
		try {
			serializer.setOutput(writer);	// XmlSerializerとStringWriterの関連付け..
			serializer.startDocument("UTF-8", true);
			serializer.startTag("", "plist");
			serializer.attribute("", "version", "1.0");
			serializer.startTag("", "dict");

			// EPS-ap URL
			XmlPullParserAided.SetParameter4Output(serializer, StringList.m_strEPSapURL, m_InformCtrl.GetURL());
		        
			serializer.endTag("", "dict");
			serializer.endTag("", "plist");
			serializer.endDocument();
		        
			// アウトプットをストリング型へ変換する
			retmsg = writer.toString();

		} catch (IOException e){
			LogCtrl.Logger(LogCtrl.m_strError, "WriteRestrictionsInfo::IOException -- " + e.toString(), this);
		}
		    
		Log.i("ProfileListActivity::WriteEPSapInfo", retmsg);
		byte[] byArrData = retmsg.getBytes();
		OutputStream outputStreamObj=null;
			
		try {
			//Context ctx = new Context();
			//Contextから出力ストリーム取得
			outputStreamObj=openFileOutput(StringList.m_strEPSapSrvOutputFile, Context.MODE_PRIVATE);
			//出力ストリームにデータを出力
			outputStreamObj.write(byArrData, 0, byArrData.length);
		} catch (FileNotFoundException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		} catch (IOException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}
			
	}
		
	// ファイルを開く
	protected void OpenFile(String FileName) {
		File filename = new File(FileName);
		if (filename.exists()) {
			Intent intent = new Intent(Intent.ACTION_VIEW);
			intent.setDataAndType(Uri.parse("file://" + filename.getPath()), "text/plain");
			startActivity(intent);
		} else {
			Log.i(StringList.m_str_SKMTag, "AppListActivity::OpenFile-- "+ "No File:" + FileName);
		}
	}

/////////////////////////////////
// debug
/////////
// ファイル読み込み
	public String ReadAndSetWifiInfo() {
 		byte[] byArrData_read = null;
 		int iSize;
 		byte[] byArrTempData=new byte[8192];
 		InputStream inputStreamObj=null;
 		ByteArrayOutputStream byteArrayOutputStreamObj=null;

 		try {
 			//Contextから入力ストリームの取得
 			inputStreamObj=openFileInput(StringList.m_str_debug_profile);
 			//
 			byteArrayOutputStreamObj=new ByteArrayOutputStream();
 			//ファイルからbyte配列に読み込み、さらにそれをByteArrayOutputStreamに追加していく
 			while (true) {
 				iSize=inputStreamObj.read(byArrTempData);
 				if (iSize<=0) break;
 				byteArrayOutputStreamObj.write(byArrTempData,0,iSize);
 			}
 			//ByteArrayOutputStreamからbyte配列に変換
 			byArrData_read = byteArrayOutputStreamObj.toByteArray();
 		} catch (Exception e) {
 			Log.d("ReadAndSetRestictionsInfo", e.getMessage());
 		} finally{
 			try {
 				if (inputStreamObj!=null) inputStreamObj.close();
 				if (byteArrayOutputStreamObj!=null) byteArrayOutputStreamObj.close();
 			} catch (Exception e2) {
 			Log.d("ReadAndSetRestictionsInfo", e2.getMessage());
 			}
 				
 		}
 			
 		String read_string = new String(byArrData_read);
 			
 		Log.d("*****Re-Read*****", read_string);
 			
 		return read_string;
 	}
}