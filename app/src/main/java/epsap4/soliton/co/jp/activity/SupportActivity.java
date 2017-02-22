/**
 *
 */
package epsap4.soliton.co.jp.activity;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.WindowManager.LayoutParams;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import epsap4.soliton.co.jp.LogCtrl;
import epsap4.soliton.co.jp.R;
import epsap4.soliton.co.jp.StringList;
import epsap4.soliton.co.jp.xmlparser.XmlDictionary;
import epsap4.soliton.co.jp.xmlparser.XmlPullParserAided;
import epsap4.soliton.co.jp.xmlparser.XmlStringData;
import jp.hishidama.zip.ZipCloak;

/**
 * @author ymori
 *
 */
public class SupportActivity extends Activity
	implements View.OnClickListener{

	/**
	 *
	 */
	private TextView m_TextVersioninfo;
	private EditText m_EditAPID;		// APID
	private EditText m_EditVpnAPID;
	private TextView m_TextWifiApid;
	private TextView m_TextVpnApid;

	private Button m_ButtonUdidsaw;		// apid
	private Button m_ButtonMailSend;	// メールを送信
	private Button m_ButtonLogGet;		// 診断情報取得
	private Button m_ButtonLogDel;		// 診断情報削除

	private String m_strAPIDWifi = "";	// APID Wi-Fi #21391
	private String m_strAPIDVPN = "";	// APID VPN #21391

	@Override
	 public void onCreate(Bundle savedInstanceState) {

//	    	this.setTitle(R.string.ApplicationTitle);
		 super.onCreate(savedInstanceState);

		 this.getWindow().setSoftInputMode(LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

		 setContentView(R.layout.support);

		// 情報管理クラスの取得

		 setUItoMember();


	 }

	private void setUItoMember() {
		// 製品情報
		m_TextVersioninfo = (TextView)findViewById(R.id.support_apkverion);	// バージョン名
		PackageInfo packageInfo = null;
		try {
			packageInfo = getPackageManager().getPackageInfo(getPackageName(), PackageManager.GET_META_DATA);
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		String strVersionName = getText(R.string.app_name) + " " + getText(R.string.main_versionname).toString() + packageInfo.versionName;

		m_TextVersioninfo.setText(strVersionName);
		LogCtrl.Logger(LogCtrl.m_strInfo, "EPS-ap_4 versionCode :: " + Integer.toString(packageInfo.versionCode), this);
		LogCtrl.Logger(LogCtrl.m_strInfo, "EPS-ap_4 versionName :: " + packageInfo.versionName, this);


		// UDID
		m_ButtonUdidsaw = (Button)findViewById(R.id.button_UDID_saw);
		m_ButtonUdidsaw.setOnClickListener(this);

		m_EditAPID = (EditText)findViewById(R.id.EditUDID);
		m_EditAPID.setVisibility(View.GONE);

		m_EditVpnAPID = (EditText)findViewById(R.id.EditVPNAPID);
		m_EditVpnAPID.setVisibility(View.GONE);

		m_TextWifiApid =(TextView)findViewById(R.id.apid_wifi_comment);
		m_TextWifiApid.setVisibility(View.GONE);

		m_TextVpnApid =(TextView)findViewById(R.id.apid_vpn_comment);
		m_TextVpnApid.setVisibility(View.GONE);

		 m_ButtonMailSend = (Button)findViewById(R.id.button_mail_send);
		 m_ButtonMailSend.setOnClickListener(this);
		 m_ButtonMailSend.setVisibility(View.GONE);

		 // 診断情報
		 m_ButtonLogGet = (Button)findViewById(R.id.button_log_send);
		 m_ButtonLogGet.setOnClickListener(this);

		 m_ButtonLogDel = (Button)findViewById(R.id.button_log_delete);
		 m_ButtonLogDel.setOnClickListener(this);

		 ReadAndSetLoginUserInfo();
	}

	private void Runmailer() {
		Uri uri= Uri.parse("mailto:");
		Intent intent=new Intent(Intent.ACTION_SENDTO, uri);
		intent.putExtra(Intent.EXTRA_SUBJECT, getText(R.string.main_apid_mailtitle).toString());
		// メッセージ本文
		String msg = getText(R.string.main_apid_wifi).toString() + ": " +
				m_EditAPID.getText().toString() + "\n\n"
				+ getText(R.string.main_apid_vpn).toString() + ": "
				+ m_EditVpnAPID.getText().toString() + "\n\n"
				+ getText(R.string.main_apid_mailmsg).toString();
		intent.putExtra(Intent.EXTRA_TEXT, msg);
	//	intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

		startActivity(intent);
	}

	private void RunmailerLogs() {
		///=== SDカード領域確認 ===///
		if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED) == false) {
			// SDカード領域が存在しないときは抜ける
			LogCtrl.Logger(LogCtrl.m_strInfo, "RunMailerLogs NO SD card.", this);
			Toast.makeText(this, R.string.diag_log_nosdcard, Toast.LENGTH_SHORT).show();
			return;
		}

		///=== 外部出力設定 ===///
	//	Uri uri=Uri.parse("mailto:");
		Intent intent=//new Intent(Intent.ACTION_SENDTO, uri);		// URI指定
					  new Intent(Intent.ACTION_SEND);				// URI指定なし(選択) na-prj 11444
		intent.putExtra(Intent.EXTRA_SUBJECT, getText(R.string.main_log_mailtitle).toString());
		//intent.setClassName("com.google.android.gm", "com.google.android.gm.ComposeActivityGmail");		// G-Mail指定

		// メッセージ本文
		String msg =getText(R.string.main_apid_mailmsg).toString();
		intent.putExtra(Intent.EXTRA_TEXT, msg);


		// <=== skminfo.txtの作成
		// # 26472
		String info_path = /*getExternalFilesDir(null)*/getFilesDir().getPath() + "/" + LogCtrl.m_strinfo_txt;
		LogCtrl.CreateInfoText(this);
		// skminfo.txtの作成 ===>

		// skmlog.csvのパス
		// # 26472
		String log_path = /*Environment.getExternalStorageDirectory().getPath() + "/" + LogCtrl.m_strlog_csv;*/
				/*getExternalFilesDir(null)*/getFilesDir().getPath() + "/" + LogCtrl.m_strlog_csv;

		// zipに入れるファイルの入力パスと出力パスの設定
		String inputFiles[] = new String[2];
		String outputFiles[] = new String[2];
		inputFiles[0] = log_path;
		inputFiles[1] = info_path;
		outputFiles[0] = LogCtrl.m_strlog_csv;
		outputFiles[1] = LogCtrl.m_strinfo_txt;

		File filename4 = new File(log_path);

		if(filename4.exists()) {
			String zip_path = getExternalFilesDir(null).getPath() + "/" + LogCtrl.m_strlog_zip;
			String zippass_path = getExternalFilesDir(null).getPath() + "/" + LogCtrl.m_strlog_zippass;
			intent.putExtra(Intent.EXTRA_STREAM, Uri.parse("file:"+/*log_path*/ zip_path/* zippass_path*/));	// na-prj 11444
		//	intent.setType("text/comma-separated-values");
			intent.setType("application/octet-stream");		// na-prj 11444
			CreateLogZip(zip_path, zippass_path, inputFiles, outputFiles);		// 診断情報をzip化
			startActivity(/*intent*/Intent.createChooser(intent, null));	// na-prj 11444
		} else Toast.makeText(this, R.string.diag_logdel_nolog, Toast.LENGTH_SHORT).show();

	}

	private void RundeleteLogs() {
		if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED) == false) {
			// SDカード領域が存在しないときは抜ける
			LogCtrl.Logger(LogCtrl.m_strInfo, "RunMailerLogs NO SD card.", this);
			Toast.makeText(this, R.string.diag_log_nosdcard, Toast.LENGTH_SHORT).show();
			return;
		}

		// #27327-27329  # 26472による不具合.書き込みファイルパスをgetFilesDirで指定しているので、削除も同様にする
		String log_path = /*getExternalFilesDir(null)*/getFilesDir().getPath() + "/" + LogCtrl.m_strlog_csv;
		String zip_pass = getExternalFilesDir(null).getPath() + "/" + LogCtrl.m_strlog_zip;
		String zippass_path = getExternalFilesDir(null).getPath() + "/" + LogCtrl.m_strlog_zippass;

		File filename4 = new File(log_path);
		File filezip = new File(zip_pass);
		File filezippass = new File(zippass_path);

		if(filename4.exists()) {
			filename4.delete();
			Toast.makeText(this, R.string.diag_logdel_msg, Toast.LENGTH_SHORT).show();
		} else Toast.makeText(this, R.string.diag_logdel_nolog, Toast.LENGTH_SHORT).show();

		if(filezip.exists()) filezip.delete();
		if(filezippass.exists()) filezippass.delete();
	}

	private void CreateLogZip(String src_zippath, String dst_zippath, String inputFiles[], String outputFiles[]) {
	//	 String log_path = getExternalFilesDir(null).getPath() + "/" + LogCtrl.m_strlog_csv;

		// 入力ストリーム
		 InputStream is = null;

		 // ZIP形式の出力ストリーム
		 ZipOutputStream zos = null;

		 // 入力対象のJPEGファイル
//		 String inputFiles[] = new String[1];
//		 inputFiles[0] = log_path;
	//	 inputFiles[1] = externalStoragePath+"/Sample/Sample2.jpg";

		// 入出力用のバッファを作成
		 byte[] buf = new byte[1024];

		 // ZipOutputStreamオブジェクトの作成
		 try {
		     zos = new ZipOutputStream(new FileOutputStream(src_zippath));
		 } catch (FileNotFoundException e2) {
		     e2.printStackTrace();
		 }

		 try {
		     for (int i=0; i<inputFiles.length; i++) {
		         // 入力ストリームのオブジェクトを作成
				is = new FileInputStream(inputFiles[i]);

		     // ZIPエントリを作成。名前は入力ファイルのファイル名
		     ZipEntry ze = new ZipEntry("LogCtrl/" + /*LogCtrl.m_strlog_csv*/outputFiles[i]);

		     // 作成したZIPエントリを登録
		     zos.putNextEntry(ze);

		     // 入力ストリームからZIP形式の出力ストリームへ書き出す
		     int len=0;
		     while ((len = is.read(buf)) != -1) {
		         zos.write(buf, 0, len);
		     }

		     // 入力ストリームを閉じる
		     is.close();

		     // エントリをクローズする
		     zos.closeEntry();
		 }

		 // 出力ストリームを閉じる
		 zos.close();

		 // zipをさらにパスワード付きzipへ変換
		 File src = new File(src_zippath);
		 File dst = new File(dst_zippath);
		 String password = LogCtrl.m_str_zippassword;

		 ZipCloak zip = new ZipCloak(src);
		 zip.encrypt(dst, password.getBytes("MS932"));

		 }catch (FileNotFoundException e) {

		 } catch (IOException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}

	 }

	@Override
	public void onClick(View v) {
		// TODO 自動生成されたメソッド・スタブ
		if(v == m_ButtonUdidsaw) {
			m_EditAPID.setVisibility(View.VISIBLE);
			m_EditVpnAPID.setVisibility(View.VISIBLE);
			m_ButtonMailSend.setVisibility(View.VISIBLE);
			m_TextVpnApid.setVisibility(View.VISIBLE);
			m_TextWifiApid.setVisibility(View.VISIBLE);

			// クリップボードへのコピー
			// https://sites.google.com/a/techdoctranslator.com/jp/android/guide/copy-paste
			ClipboardManager clipboard = (ClipboardManager)getSystemService(Context.CLIPBOARD_SERVICE);
			String strUDID = GetUDID();
			String strVpnAPID = GetVpnApid();
			String strclip = strUDID + " " + strVpnAPID;
			ClipData clip = ClipData.newPlainText("simple text", strclip);
			clipboard.setPrimaryClip(clip);

			m_EditAPID.setText(strUDID);
			m_EditVpnAPID.setText(strVpnAPID);


		} else if(v == m_ButtonMailSend) {
			Log.i("EnrollActivity::onClick", "m_ButtonMailSend");
		//	print_logcat();
			Runmailer();
		//	RunmailerLogs();
		} else if(v == m_ButtonLogGet) {
			RunmailerLogs();
		} else if(v == m_ButtonLogDel) {
			RundeleteLogs();
		}
	}

	private String GetUDID() {
		if(m_strAPIDWifi.length() > 0) return m_strAPIDWifi;
		else return XmlPullParserAided.GetUDID(this);

	}

	private String GetVpnApid() {
		if(m_strAPIDVPN.length() > 0) return m_strAPIDVPN;
		else return XmlPullParserAided.GetVpnApid(this);
	}

	// ファイル読み込み&フラグセット
	public boolean ReadAndSetLoginUserInfo() {
		byte[] byArrData_read = null;
		int iSize;
		byte[] byArrTempData=new byte[4096];
		InputStream inputStreamObj=null;
		ByteArrayOutputStream byteArrayOutputStreamObj=null;

		boolean bRet = true;

		try {
			//Contextから入力ストリームの取得
			inputStreamObj=openFileInput(StringList.m_strLoginUserOutputFile);
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
			LogCtrl.Logger(LogCtrl.m_strDebug, "ReadAndSetLoginUserInfo: "+ e.getMessage(), this);
			bRet = false;
		} finally{
			try {
				if (inputStreamObj!=null) inputStreamObj.close();
				if (byteArrayOutputStreamObj!=null) byteArrayOutputStreamObj.close();
			} catch (Exception e2) {
				LogCtrl.Logger(LogCtrl.m_strDebug, "ReadAndSetLoginUserInfo e2: " + e2.getMessage(), this);
				bRet = false;
			}

		}

		if(bRet == false) return bRet;

		String read_string = new String(byArrData_read);
		//LogCtrl.Logger(LogCtrl.m_strDebug, "*****Re-Read***** " + read_string, this);
		android.util.Log.d(StringList.m_str_SKMTag, "*****Re-Read***** " + read_string);

		// 新しくXmlPullParserAidedを作成する.
		XmlPullParserAided p_aided = new XmlPullParserAided(this, read_string, 2);
		p_aided.TakeApartControll();		// ここで分解する
		XmlDictionary p_dict = p_aided.GetDictionary();		// XmlPullParserAidedクラスで分類され、XmlDictionaryに振るいわけされた要素を取得

		// <key, type, data>リストを取得
		List<XmlStringData> str_list = p_dict.GetArrayString();
		for(int i = 0; str_list.size() > i; i++){
			// config情報に従って、処理を行う.
			XmlStringData p_data = str_list.get(i);
			SetParametorFromFile(p_data);
		}

		return bRet;
	}


	private void SetParametorFromFile(XmlStringData p_data) {

		String strKeyName = p_data.GetKeyName();	// キー名
		int    i_type = p_data.GetType();		// 要素タイプ(string:1, data=2, date=3, real=4, integer=5, true=6, false=7)
		String strData = p_data.GetData();		// 要素
		//
		if(strKeyName.equalsIgnoreCase(StringList.m_str_Apid_Wifi)) { // # 21391
			m_strAPIDWifi = strData;
		} else if(strKeyName.equalsIgnoreCase(StringList.m_str_Apid_VPN)) { // # 21391
			m_strAPIDVPN = strData;
		}

	}

}
