package epsap4.soliton.co.jp.activity;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.graphics.Color;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.StatFs;
import android.security.KeyChain;
import android.security.KeyChainException;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.Xml;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;

import org.xmlpull.v1.XmlSerializer;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.StringWriter;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import epsap4.soliton.co.jp.EpsapAdminReceiver;
import epsap4.soliton.co.jp.InformCtrl;
import epsap4.soliton.co.jp.LogCtrl;
import epsap4.soliton.co.jp.R;
import epsap4.soliton.co.jp.RestrictionsControl;
import epsap4.soliton.co.jp.StringList;
import epsap4.soliton.co.jp.mdm.MDMControl;
import epsap4.soliton.co.jp.mdm.MDMFlgs;
import epsap4.soliton.co.jp.shortcut.CreateShortcutLink;
import epsap4.soliton.co.jp.wifi.WifiControl;
import epsap4.soliton.co.jp.xmlparser.XmlDictionary;
import epsap4.soliton.co.jp.xmlparser.XmlPullParserAided;
import epsap4.soliton.co.jp.xmlparser.XmlStringData;

public class EPS_ap400Activity extends Activity
		implements View.OnClickListener, Runnable {

	// UIの変数設定
	private Button m_ButtonLogin;
	private EditText m_EditUserID;
	private EditText m_EditPassword;
	private EditText m_EditURL;
	private TextView m_TextErrorLogin;
	private TextView m_TextGoSupport;

	private EditText m_EditShorcut;		// ショートカット(ショートカット名)
	private EditText m_EditUri;			// ショートカット(URI)
	private Button m_ButtonShortcut;	// ショートカット追加ボタン
	private Button m_ButtonRmvShortcut;	// ショートカット削除ボタン
	private Button m_ButtonProfile;		// プロファイル
	private Button m_ButtonEnroll;		// Enroll

	private Button m_ButtonDeleteProfile;	// プロファイル削除
	private TextView m_TextDelProfile;
	private FrameLayout m_fmpro02;

	private /*static*/ InformCtrl m_InformCtrl = new InformCtrl();

	// Callback parameter
	private int PERMISSIONS_REQUEST_READ_PHONE_STATE = 10;
	private static final String KEYCHAIN_PREF_ALIAS = "alias";

	public String test_str = "";

	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	// ★★ Android3.0よりStrictModeってのがデフォルトでONになっているので，
    	// メインスレッド上からネットワーク通信を行うとandroid.os.NetworkOnMainThreadExceptionが発生する
    	// よってOFFにするため，以下の一文を必要があります
 //   	StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().permitAll().build());

    	this.setTitle(R.string.ApplicationTitle);
        super.onCreate(savedInstanceState);
        this.getWindow().setSoftInputMode(LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        setContentView(R.layout.main);

        setUItoMember();

        if(android.os.Build.VERSION.SDK_INT >= 23) {
        	NewPermissionSet();
        }

        Log.i(StringList.m_str_SKMTag, "EPS_ap400Activity::onCreate  "+ printDate());

        // URL Scheme呼び出し
        // autoconnectが設定されていたら、証明書取得画面へ
        boolean bAutoConnect = IsURLScheme();

        if(bAutoConnect == true) {
	        m_InformCtrl.SetURL(m_EditURL.getText().toString());
	        m_InformCtrl.SetAutoConnect(true);


	        Intent AppIntent;
	        AppIntent = new Intent(this, ProfileListActivity.class);
	        AppIntent.putExtra(StringList.m_str_InformCtrl, m_InformCtrl);

	        //  Activityの呼び出し
	        startActivityForResult(AppIntent, 0);
        }

        m_InformCtrl.SetAutoConnect(false);

        // <=== debug
        // Android id get for Android6.0 over
        //String hoge = Settings.Secure.getString(getContentResolver(),Settings.Secure.ANDROID_ID);
        //android.util.Log.i("EPS_ap400Activity", "Android ID=" + hoge);
        // SSID get
        //WifiManager manager = (WifiManager)getSystemService(WIFI_SERVICE);
        //WifiInfo info = manager.getConnectionInfo();
        //android.util.Log.i("EPS_ap400Activity", "Current SSID=" + info.getSSID());

        // mime-type get
        //MimeTypeMap map = MimeTypeMap.getSingleton();
        //String typestr = map.getMimeTypeFromExtension("pptx");
        //android.util.Log.i("EPS_ap400Activity", "mime type=" + typestr);

  //      AppIsInstalled("epsap4.soliton.co.jp"/*"com.noshufou.android.su"*/);
  //      try {
//			Process process = Runtime.getRuntime().exec("su -v");
//		} catch (IOException e) {
			// TODO 自動生成された catch ブロック
//			e.printStackTrace();
//		}

        // Debug

     //  GetDeviceInfo();		// デバイス情報の取得
        //String aaa = getMacAddr();
        //android.util.Log.i("EPS_ap400Activity", "MAC ADDR=" + aaa);
      //  GetInstallationApp();	// インストールリスト
    //    GetLauncherApp();		// ランチャーから起動できるアプリ一覧
    //    GetDpis();				// 解像度など
     //   Log.i(StringList.m_str_SKMTag, "EPS_ap400Activity::onCreate vpn "+ XmlPullParserAided.GetVpnApid(this));
    }

    // UIのメンバ変数割り当てとコールバック登録
    private void setUItoMember(/*Button views, int id*/) {

    	// 変数割り当て
//   	m_ButtonLogin = (Button)findViewById(R.id.ButtonLogin);	// Loginボタン

    	m_EditUserID = (EditText)findViewById(R.id.EditUserID);	// ユーザIDエディット
    	m_EditPassword = (EditText)findViewById(R.id.EditPassword);	// パスワードエディット
    	m_EditURL = (EditText)findViewById(R.id.EditServerURL);		// サーバURL
    	m_EditURL.requestFocus();		// 当アクティビティが開いたときの、初期フォーカスとして設定する

    	m_TextErrorLogin = (TextView)findViewById(R.id.ErrorLoginMessage);	// エラーメッセージ
    	m_TextErrorLogin.setTextColor(Color.rgb(255,20,20));
    	m_TextErrorLogin.setVisibility(View.GONE);							// 初期設定では非表示

    	// ショートカット
//    	m_EditShorcut = (EditText)findViewById(R.id.editShortcut);
//    	m_EditUri = (EditText)findViewById(R.id.editUri);
//    	m_ButtonShortcut = (Button)findViewById(R.id.button_create_short);
//    	m_ButtonRmvShortcut = (Button)findViewById(R.id.button_remove_short);
    	m_ButtonProfile = (Button)findViewById(R.id.button_profile_list);

//    	m_ButtonEnroll = (Button)findViewById(R.id.button_enroll);

    	// コールバック関数登録
//    	m_ButtonLogin.setOnClickListener(this);
//    	m_ButtonShortcut.setOnClickListener(this);
//    	m_ButtonRmvShortcut.setOnClickListener(this);
    	m_ButtonProfile.setOnClickListener(this);
//    	m_ButtonEnroll.setOnClickListener(this);



		 // バージョン v1.2.2 バージョン名のみ. バージョンコードも対応可
		 m_TextGoSupport = (TextView)findViewById(R.id.SupportText);	// バージョン名
		 PackageInfo packageInfo = null;
		 try {
			 packageInfo = getPackageManager().getPackageInfo(getPackageName(), PackageManager.GET_META_DATA);
		 } catch (NameNotFoundException e) {
			 // TODO Auto-generated catch block
			 e.printStackTrace();
		 }

		 String strVersionName = getText(R.string.main_versionname).toString() + packageInfo.versionName;

//		 m_TextVersionName.setText(strVersionName);
		 Log.i("EPS-ap_4 versionCode", Integer.toString(packageInfo.versionCode));
		 Log.i("EPS-ap_4 versionName", packageInfo.versionName);
		 m_TextGoSupport.setOnClickListener(this);

    	// EPS-ap Server 情報の取得
		//getCertificateChain(KEYCHAIN_PREF_ALIAS);
    	ReadAndSetEPSapInfo();

    	// [プロファイル削除]ボタンの設定
    	setUIProfileDel();
    }
	private X509Certificate[] getCertificateChain(String alias) {
		try {
			return KeyChain.getCertificateChain(this, alias);
		} catch (KeyChainException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return null;
	}
    // [プロファイル削除]ボタン
 	private void setUIProfileDel() {

 		m_ButtonDeleteProfile = (Button)findViewById(R.id.button_profile_del);
 		m_TextDelProfile = (TextView)findViewById(R.id.profile_del_comment);
 		m_fmpro02 = (FrameLayout)findViewById(R.id.FmProfile02);

 		m_ButtonDeleteProfile.setOnClickListener(this);

 		// プロファイルがインストール済みなら、プロファイル削除ボタンを表示する。
 		File filename = new File("/data/data/" + getPackageName() + "/files/" + StringList.m_strShortcutOutputFile);
 		File filename2 = new File("/data/data/" + getPackageName() + "/files/" + StringList.m_strWifiOutputFile);
 		File filename3 = new File("/data/data/" + getPackageName() + "/files/" + StringList.m_strRestrictionFileName);
 		File filename4 = new File("/data/data/" + getPackageName() + "/files/" + StringList.m_strScepWifiOutputFile);
 		File filename5 = new File("/data/data/" + getPackageName() + "/files/" + StringList.m_strMdmOutputFile);
 		if(filename.exists() || filename2.exists() || filename3.exists() || filename4.exists()
 				|| filename5.exists()) {
 			;
 		} else {
 			m_ButtonDeleteProfile.setVisibility(View.GONE);
 			m_TextDelProfile.setVisibility(View.GONE);
 			m_fmpro02.setVisibility(View.GONE);
 		}

 		return;
 	}

 	// URLスキーマ起動されたときのクエリの解析とパラメータの設定
 	private boolean IsURLScheme() {
 		boolean b_auto = false;

 		// URL scheme起動時のパラメータ取得
        Intent intent = getIntent();
        String action = intent.getAction();
        if (Intent.ACTION_VIEW.equals(action)) {
        	b_auto = true;
            Uri uri = intent.getData();
            if (uri != null) {
            	String p = uri.getQuery();
            	Log.i(StringList.m_str_SKMTag, "EPS_ap400Activity::IsURLScheme Query: "+ p);

            	String param_host = uri.getQueryParameter(StringList.m_str_schemehost);
            	String param_user = uri.getQueryParameter(StringList.m_str_schemeuser);
            	String param_pass = uri.getQueryParameter(StringList.m_str_schemepass);
            	String param_auto = uri.getQueryParameter(StringList.m_str_schemeauto);
            	Log.i(StringList.m_str_SKMTag, "EPS_ap400Activity::IsURLScheme Host: "+ param_host);
            	Log.i(StringList.m_str_SKMTag, "EPS_ap400Activity::IsURLScheme User: "+ param_user);
            	Log.i(StringList.m_str_SKMTag, "EPS_ap400Activity::IsURLScheme Password: "+ param_pass);

            	m_EditURL.setText(param_host);	// ホスト名をURLエディットボックスに入力
            	WriteLoginUserInfo(param_user, param_pass);

            	if(param_host == null) b_auto = false;
            	else if(param_user == null) b_auto = false;
            	else if(param_pass == null) b_auto = false;
            	else if(param_auto == null) b_auto = false;
            	else if((param_auto.length() > 0) && (param_auto.equalsIgnoreCase("true"))) b_auto = true;

            }

        }

 		return b_auto;
 	}

    // #21391
	private void NewPermissionSet() {
		if(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
			ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_PHONE_STATE},
					PERMISSIONS_REQUEST_READ_PHONE_STATE);
		}
	}

    // requestPermissionsのコールバック
    public void onRequestPermissionsResult(int requestCode,
										   String permissions[], int[] grantResults) {
        if(requestCode == PERMISSIONS_REQUEST_READ_PHONE_STATE) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // 許可するを選択した場合の処理を実装。
            	LogCtrl.Logger(LogCtrl.m_strInfo, "EPS_ap400Activity::onRequestPermissionsResult "+ "PERMISSION_GRANTED", this);
            } else {
            	PhoneStatePermissionsWarn();
            	LogCtrl.Logger(LogCtrl.m_strInfo, "EPS_ap400Activity::onRequestPermissionsResult "+ "PERMISSION_DENIED", this);
            //	finish();
                // 許可しないを選択した場合を実装
            }
            return;
        }
    }

    // 許可しないを選択したときの確認メッセージ
    private void PhoneStatePermissionsWarn() {
		AlertDialog.Builder dlg;
		dlg = new AlertDialog.Builder(this);
	//	dlg.setTitle(getText(R.string.Dialog_title_permissions).toString());
		dlg.setMessage(getText(R.string.Dialog_msg_telpermissions).toString());
		dlg.setPositiveButton("YES", new DialogInterface.OnClickListener() {
			 public void onClick(DialogInterface dialog, int id) {
			 // 確認後アプリを終了
				 finish();
			    }
		});


		dlg.show();
	}

 // ファイル出力
 	private void WriteLoginUserInfo(String struser, String strpass) {
 		String retmsg = "";

 		XmlSerializer serializer = Xml.newSerializer();
 		StringWriter writer = new StringWriter();
 		try {
 			serializer.setOutput(writer);	// XmlSerializerとStringWriterの関連付け..
 			serializer.startDocument("UTF-8", true);
 			serializer.startTag("", "plist");
 			serializer.attribute("", "version", "1.0");
 			serializer.startTag("", "dict");

 			// EPS-ap UserID
 			XmlPullParserAided.SetParameter4Output(serializer, StringList.m_str_User_id, struser);
 			// EPS-ap Password
 			XmlPullParserAided.SetParameter4Output(serializer, StringList.m_str_LoginUser_Pass, strpass);

 			serializer.endTag("", "dict");
 			serializer.endTag("", "plist");
 			serializer.endDocument();

 			// アウトプットをストリング型へ変換する
 			retmsg = writer.toString();

 		} catch (IOException e){
 			Log.e("CertLoginActivity::IOException ", e.toString());
 		}

 		Log.i("CertLoginActivity::WriteLoginUserInfo", retmsg);
 		byte[] byArrData = retmsg.getBytes();
 		OutputStream outputStreamObj=null;

 		try {
 			//Context ctx = new Context();
 			//Contextから出力ストリーム取得
 			outputStreamObj=openFileOutput(StringList.m_strLoginUserOutputFile, Context.MODE_PRIVATE);
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

	@Override
	public void onClick(View clickParameter) {
		// TODO 自動生成されたメソッド・スタブ
		Log.i(StringList.m_str_SKMTag, "EPS_ap400Activity::onClick--" + "Start.");
		// TODO 自動生成されたメソッド・スタブ

		if(clickParameter == m_ButtonLogin) {
			m_TextErrorLogin.setText("");

			// ログインボタン
			Log.i("EPS_ap300Activity::onClick", "Push LoginButton");
			Log.i("EPS_ap300Activity::onClick", m_EditUserID.getText().toString());
			Log.i("EPS_ap300Activity::onClick", m_EditPassword.getText().toString());

/* ひとまずコメント
			String str_userid = m_EditUserID.getText().toString();
			String str_passwd = m_EditPassword.getText().toString();
			String str_url = m_EditURL.getText().toString();

			// ログインメッセージ
			String message = "USER_ID=" + str_userid + "&" + "PASSWORD=" + str_passwd + "&" + "LOGON=1";
			Log.i("EPS_ap300Activity::", "LoginMsg=" + message);

			// 入力データを情報管理クラスへセットする
			m_InformCtrl.SetUserID(str_userid);
			m_InformCtrl.SetPassword(str_passwd);
			m_InformCtrl.SetURL(str_url);
			m_InformCtrl.SetMessage(message);

			// ログイン
			HttpConnectionCtrl conn = new HttpConnectionCtrl(this);
			boolean ret = conn.RunHttpLoginUrlConnection(m_InformCtrl);
			if (ret == false) {
				Log.e("EPS_ap300Activity::onClick", "Login Error.");
				m_TextErrorLogin.setText(R.string.LoginErrorMessage);
				return;
			}

			Log.i("EPS_ap300Activity::onClick:RTN", m_InformCtrl.GetRtn());
			Log.i("EPS_ap300Activity:onClick:Cookie:", m_InformCtrl.GetCookie());
			//RunHttpLoginDefaultHttpClient();
			//RunHttpLoginUrlConnection();

			// ログイン結果
//			if(m_InformCtrl.GetRtn().equalsIgnoreCase(getText(R.string.NGkey).toString())) {
				// "NG"ならログイン失敗
//				Log.e("MdmServiceActivity::onClick", "Login NG.");
//				m_TextErrorLogin.setText(R.string.LoginErrorMessage);
//				return;
//			}

			// 取得したCookieをログイン時のCookieとして保持する.
			m_InformCtrl.SetLoginCookie(m_InformCtrl.GetCookie());
ひとまずコメント*/

			File filename = new File("/data/data/" + getPackageName() + "/files/" + StringList.m_strShortcutOutputFile);
			File filename2 = new File("/data/data/" + getPackageName() + "/files/" + StringList.m_strWifiOutputFile);
			File filename3 = new File("/data/data/" + getPackageName() + "/files/" + StringList.m_strRestrictionFileName);
			Intent AppIntent;
			if(filename.exists() || filename2.exists() || filename3.exists()) {

				// 設定ファイルが存在したときには、プロファイルリセットアクティビティに遷移
				AppIntent = new Intent(this, /*EnrollActivity.class*/ProfileResetActivity.class);
			} else {
				AppIntent = new Intent(this, ProfileListActivity.class);
			}

			AppIntent.putExtra(StringList.m_str_InformCtrl, m_InformCtrl);

			startActivityForResult(AppIntent, 0);	// EnrollActivity開始
		} else if(clickParameter == m_ButtonProfile) {
			LogCtrl.Logger(LogCtrl.m_strInfo, "EPS_ap400Activity::onClick "+ "Profile List", this);

			if(android.os.Build.VERSION.SDK_INT >= 23) {
				NewPermissionSet();
			}

			// 接続エラーメッセージを非表示
			m_TextErrorLogin.setVisibility(View.GONE);

/*			ParcelFileDescriptor pfd = null;

			 VpnService vpn_instance = new VpnService();
			 vpn_instance.onRevoke();
			 Intent intent = VpnService.prepare(this);
			 if(intent != null) {
				 Log.i("EPS_ap400Activity::onClick", "VPNService");
				 startActivityForResult(intent, 1);
			 } else {
				 Log.i("EPS_ap400Activity::onClick", "VPNService Null Null");
			 }
*/
			// 接続先サーバーのURL
			String str_url = m_EditURL.getText().toString();
			m_InformCtrl.SetURL(str_url);
			// ユーザーID
//			String str_id = m_EditUserID.getText().toString();
//			m_InformCtrl.SetUserID(str_id);
			// Password
//			String str_password = m_EditPassword.getText().toString();
//			m_InformCtrl.SetPassword(str_password);


    		// 2. Intentへのパラメータ設定
    		/*dlg2 = new AlertDialog.Builder(this);
            dlg2.setTitle("TEST");
            dlg2.setMessage("TRACE2");
            dlg2.setPositiveButton("OK", null);
            dlg2.show();*/

 //   		File filename = new File("/data/data/" + getPackageName() + "/files/" + StringList.m_strShortcutOutputFile);
//			File filename2 = new File("/data/data/" + getPackageName() + "/files/" + StringList.m_strWifiOutputFile);
//			File filename3 = new File("/data/data/" + getPackageName() + "/files/" + StringList.m_strRestrictionFileName);
			Intent AppIntent;
//			if(filename.exists() || filename2.exists() || filename3.exists()) {

				// 設定ファイルが存在したときには、プロファイルリセットアクティビティに遷移
//				AppIntent = new Intent(this, /*EnrollActivity.class*/ProfileResetActivity.class);
//			} else {
				AppIntent = new Intent(this, ProfileListActivity.class);
//			}

			AppIntent.putExtra(StringList.m_str_InformCtrl, m_InformCtrl);

    		// 3. Activityの呼び出し
    		startActivityForResult(AppIntent, 0);

		} else if (clickParameter == m_ButtonEnroll) {
			Log.i("EPS_ap300Activity::onClick", "Enroll");
			Log.i("EPS_ap300Activity::onClick", "m_ButtonRmvShortcut " + test_str);

			String rtn_str = ReadAndSetWifiInfo();

			XmlPullParserAided m_p_aided = new XmlPullParserAided(this, rtn_str, 2);	// 最上位dictの階層は2になる
			boolean ret = m_p_aided.TakeApartProfile();
			if (ret == false) {
				Log.e("EnrollActivity::onClick", "Enroll xml analyze");
				return;
			}


		}  else if(clickParameter == m_ButtonDeleteProfile) {
			// ダイアログ表示
			Dialog4DeleteProfile();
		} else if(clickParameter == m_TextGoSupport) {
			// TextViewもsetOnClickListener設定できる
			Log.i("EnrollActivity::onClick", "m_TextGoSupport");
			Intent AppIntent;
			AppIntent = new Intent(this, SupportActivity.class);
			startActivityForResult(AppIntent, 0);
		}
	}


	private void Dialog4DeleteProfile() {
		AlertDialog.Builder dlg;
		dlg = new AlertDialog.Builder(this);
		dlg.setTitle(getText(R.string.Dialog_title_delpro).toString());
		dlg.setMessage(getText(R.string.Dialog_msg_delpro).toString());
		dlg.setPositiveButton("YES", new DialogInterface.OnClickListener() {
		     public void onClick(DialogInterface dialog, int id) {
		    	// プロファイルの削除
				DeleteProfile();

				// DevicePolicyの削除
				DeleteDevicePolicy();

				m_ButtonDeleteProfile.setVisibility(View.GONE);
				m_TextDelProfile.setVisibility(View.GONE);
				m_fmpro02.setVisibility(View.GONE);
		    }
		});
		dlg.setNegativeButton("NO", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				dialog.cancel();
			}
		});

		dlg.show();
	}

	public void DeleteProfile() {
		String filedir = "/data/data/" + getPackageName() + "/files/";
		LogCtrl.Logger(LogCtrl.m_strInfo, "EPS_ap400Activity::DeleteProfile filedir=" + filedir, this);

		//<=== ショートカットの削除
		File filename = new File(filedir + StringList.m_strShortcutOutputFile);

		if(filename.exists()) {
			CreateShortcutLink c_link = new CreateShortcutLink(this);
			c_link.ReadAndSetShortcutInfo();
			c_link.RemoveRun();
			filename.delete();
		}
		// ショートカットの削除 ===>

		//<=== Wifiの削除
		// Wi-Fi(インスタントプロファイル)
		File filename2 = new File(filedir + StringList.m_strWifiOutputFile);

		if(filename2.exists()) {
			WifiControl wifi = new WifiControl(this);
			wifi.ReadAndSetWifiInfo(StringList.m_strWifiOutputFile);
			wifi.deleteConfig();
			filename2.delete();
		}

		//<=== 機能制限の削除
		File filename3 = new File(filedir + StringList.m_strRestrictionFileName);
		if(filename3.exists()) {
			RestrictionsControl m_resriction = new RestrictionsControl(this);	// この時点でサービスを止める

			filename3.delete();

		}
		// 機能制限の削除 ===>

		//<=== MDM
		File filename_mdm = new File(filedir + StringList.m_strMdmOutputFile);

		if(filename_mdm.exists()) {
			// チェックアウト後削除.チェックアウトのためにThread化しないと...
			 Thread thread = new Thread(this);	// 自分クラスをスレッドの引数に渡して...
	        thread.start();						// run()が実行される
		//	filename_mdm.delete();
		}
		// MDMの削除 ===>

		// #19854 Wi-Fiの削除は最後に行う. sleepをかませる
		// Wi-Fi(SCEP)
		File filename4 = new File(filedir + StringList.m_strScepWifiOutputFile);

		if(filename4.exists()) {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO 自動生成された catch ブロック
				e.printStackTrace();
			}
			WifiControl wifi = new WifiControl(this);
			wifi.ReadAndSetWifiInfo(StringList.m_strScepWifiOutputFile);
			wifi.deleteConfig();
			filename4.delete();
		}
		// Wifiの削除 ===>

	}

	private void DeleteDevicePolicy() {
		// device policyの終了. camera制御もdevice policy になったため、ここでしっかりremoveする
		DevicePolicyManager m_DPM;
		ComponentName m_DeviceAdmin;
		m_DPM = (DevicePolicyManager)getSystemService(Context.DEVICE_POLICY_SERVICE);
		m_DeviceAdmin = new ComponentName(EPS_ap400Activity.this, EpsapAdminReceiver.class);
		m_DPM.removeActiveAdmin(m_DeviceAdmin);
	}

	// 子Activityからアプリを終了する方法
	// 参照:http://ymgcsng.blogspot.jp/2010/12/activity.html
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
	    if (resultCode == StringList.RESULT_CLOSE) {
	        setResult(StringList.RESULT_CLOSE);
	        finish();
	    } else if(resultCode == StringList.RESULT_HTTP_CON_ERR) {
	    	// 接続エラーメッセージの可視化
	    	m_TextErrorLogin.setVisibility(View.VISIBLE);
	    	m_TextErrorLogin.setText(R.string.LoginErrorMessage);
	    }
	}

	// ファイル読み込み&フラグセット
	public boolean ReadAndSetEPSapInfo() {
		byte[] byArrData_read = null;
		int iSize;
		byte[] byArrTempData=new byte[4096];
		InputStream inputStreamObj=null;
		ByteArrayOutputStream byteArrayOutputStreamObj=null;

		boolean bRet = true;

		try {
			//Contextから入力ストリームの取得
			inputStreamObj=openFileInput(StringList.m_strEPSapSrvOutputFile);
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
			Log.d("ReadAndSetEPSapInfo", e.getMessage());
			bRet = false;
		} finally{
			try {
				if (inputStreamObj!=null) inputStreamObj.close();
				if (byteArrayOutputStreamObj!=null) byteArrayOutputStreamObj.close();
			} catch (Exception e2) {
				Log.d("ReadAndSetEPSapInfo", e2.getMessage());
				bRet = false;
			}

		}

		if(bRet == false) return bRet;

		String read_string = new String(byArrData_read);
		Log.d("*****Re-Read*****", read_string);

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
		if(strKeyName.equalsIgnoreCase(StringList.m_strEPSapURL)) {
			m_EditURL.setText(strData);
		}
	}

	private void MDMChkOut() {
		MDMFlgs mdm = new MDMFlgs();
    	boolean bRet = mdm.ReadAndSetScepMdmInfo(this);
    	if(mdm.GetCheckOut() == true) {
    		MDMControl.CheckOut(mdm, this);
    	}

    	MDMControl mdmctrl = new MDMControl(this, mdm.GetUDID());	// この時点でサービスを止める
    	String filedir = "/data/data/" + getPackageName() + "/files/";
    	File filename_mdm = new File(filedir + StringList.m_strMdmOutputFile);
    	filename_mdm.delete();
	}

	@Override
	public void run() {
		// TODO 自動生成されたメソッド・スタブ
		MDMChkOut();
		handler.sendEmptyMessage(0);
	}

	private Handler handler = new Handler() {
		 public void handleMessage(Message msg) {
			// プログレスダイアログ終了

		 }
	};


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
	 			Log.d("ReadAndSetWifiInfo", e.getMessage());
	 		} finally{
	 			try {
	 				if (inputStreamObj!=null) inputStreamObj.close();
	 				if (byteArrayOutputStreamObj!=null) byteArrayOutputStreamObj.close();
	 			} catch (Exception e2) {
	 			Log.d("ReadAndSetWifiInfo", e2.getMessage());
	 			}

	 		}

	 		String read_string = new String(byArrData_read);

	 		Log.d("*****Re-Read*****", read_string);

	 		return read_string;
	 	}

	 	private boolean AppIsInstalled(String PackageName) {
	 		PackageManager pm = getPackageManager();
	 		try {
	 			ApplicationInfo ai = pm.getApplicationInfo(PackageName, 0);
	 		} catch (NameNotFoundException e) {
	 			Log.i("EPS_ap400Activity::AppIsInstalled", "FALSE");

	 			return false;
	 		}
	 		Log.i("EPS_ap400Activity::AppIsInstalled", "TRUE");

	 		return true;
	 	}
	 private void GetDeviceInfo() {



		// Wi-Fi情報 エミュレータでは落ちる
        try {
	        WifiManager wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
	        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
	        String sss = "";
	        sss = wifiInfo.getMacAddress();
	        if (sss.length() > 0)
	        	Log.i("Wi-FI Mac = ", sss);
        } catch (Exception e) {
	        	Log.e("Wi-FI MacAdress = ", "Error");
        }

        Log.i("Package Name::", getPackageName());
        // terephone情報
        TelephonyManager telManager = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
        //String strSimCountry = telManager.getSimCountryIso();
        //Log.i("SIM COUNTRY = ", strSimCountry);

 //       String strSerialNumber =  telManager.getSimSerialNumber();
 //       Log.i("SERIAL NUMBER = ", strSerialNumber);	// Android tab3.1で異常終了

 //       Log.i("TELEPHONE NUMBER = ", telManager.getLine1Number());
        String rtnstr;
        try {
        	rtnstr = telManager.getLine1Number();
        } catch(Exception e) {
        	Log.e("TELNUMBER = ", e.toString());
        	rtnstr = null;
        }
		if (rtnstr == null)
			rtnstr = "None";
        Log.i("PhoneNumber = ", rtnstr);

        String strDevice;
        try {
        strDevice = telManager.getDeviceId();
        } catch(Exception e) {
        	Log.e("IMEI = ", e.toString());
        	strDevice = null;
        }
        if(strDevice == null) Log.i("Device ID=", "NULL だよ");
        else Log.i("Device ID=", strDevice);
//        Log.i("Device ID = ", telManager.getDeviceId());
//        Log.i("SIM OPERATOR = ", telManager.getSimOperator());
//        Log.i("SIM OPERATOR Name= ", telManager.getSimOperatorName());
        //Log.i("Software Version = ", telManager.getDeviceSoftwareVersion());	// エミュレータで異常終了
        //Log.i("VoiceMail Number = ", telManager.getVoiceMailNumber());		// 実機で異常終了
        Log.i("NETWORK OPERATOR=", telManager.getNetworkOperator());
        Log.i("NETWORK OPERATOR NAME=", telManager.getNetworkOperatorName());
 //       Log.i("SUBSCRIBER ID=", telManager.getSubscriberId());
        Log.i("MODEL NUMBER= ", Build.MODEL);
        Log.i("OSVersion", Build.VERSION.RELEASE);
        Log.i("Build Vertion", Build.VERSION.INCREMENTAL);


        // 本体容量
        File path = Environment.getDataDirectory();
        StatFs stat = new StatFs(path.getAbsolutePath());
        long availMemSize = stat.getAvailableBlocks() * stat.getBlockSize();
        long allMemSize = stat.getBlockCount() *  stat.getBlockSize();
        String unit = "";
        if (availMemSize > 1024) {
            availMemSize /= 1024;
            unit = "KB";
            if (availMemSize > 1024) {
                availMemSize /= 1024;
                unit = "MB";
            }
        }
        Log.i("Available Capacity", availMemSize + unit);	// 空き容量

        if (allMemSize > 1024) {
        	allMemSize /= 1024;
            unit = "KB";
            if (allMemSize > 1024) {
            	allMemSize /= 1024;
                unit = "MB";
            }
        }
        Log.i("All Capacity", allMemSize + unit);	// 全体容量

        // SDカード容量
        File pathSD = Environment.getExternalStorageDirectory();
        StatFs statSD = new StatFs(pathSD.getAbsolutePath());
        long availMemSizeSD = statSD.getAvailableBlocks() * statSD.getBlockSize();
        long allMemSizeSD = statSD.getBlockCount() *  statSD.getBlockSize();

        if (availMemSizeSD > 1024) {
        	availMemSizeSD /= 1024;
            unit = "KB";
            if (availMemSizeSD > 1024) {
            	availMemSizeSD /= 1024;
                unit = "MB";
            }
        }
        Log.i("Available CapacitySD", availMemSizeSD + unit);

        if (allMemSizeSD > 1024) {
        	allMemSizeSD /= 1024;
            unit = "KB";
            if (allMemSizeSD > 1024) {
            	allMemSizeSD /= 1024;
                unit = "MB";
            }
        }
        Log.i("All CapacitySD", allMemSizeSD + unit);

 //       String strUDID = telManager.getDeviceId();
 //       strUDID += strUDID;
 //       strUDID += strUDID;
 //       strUDID = strUDID.substring(0, 40);
//		if (strUDID.length() > 0)
//        	Log.i("UDID = ", strUDID);

 //       Intent intent = getIntent();
 //       String mResult = "";
 //       BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
 //       mResult += "Name : " + device.getName() + "\n";
 //       mResult += "Device Class : " + device.getBluetoothClass().getDeviceClass() + "\n";
 //       mResult += "MAC Address : " + device.getAddress() + "\n";
 //       mResult += "State : " + device.getBondState() + "\n";
 //       Log.i("Bluetooth info=", mResult);
	 }


	 // インストールされているアプリ一覧
	// http://9ensan.com/blog/smartphone/android/android-install-list/
	 private void GetInstallationApp() {
		// リスト作成
	    ArrayList<String> appList = new ArrayList<String>();
	        // パッケージマネージャーの作成
	    PackageManager packageManager = getPackageManager();
	        // インストール済みのアプリケーション一覧の取得
	    List<ApplicationInfo> applicationInfo = packageManager.getInstalledApplications(PackageManager.GET_META_DATA);
	    for (ApplicationInfo info : applicationInfo) {
	    	// プリインストールされているものを飛ばすときは次に一文を追加する.
	    //	if ((info.flags & ApplicationInfo.FLAG_SYSTEM) == ApplicationInfo.FLAG_SYSTEM) continue;

	        appList.add((String)packageManager.getApplicationLabel(info));
	        Log.i("GetInstallationApp", (String)packageManager.getApplicationLabel(info));

	        try {
                PackageInfo info2 = packageManager.getPackageInfo(info.packageName, PackageManager.GET_META_DATA);
                Log.i("GetInstallationApp package", info2.packageName + ":"+info2.versionName+":"+info2.versionCode);
            } catch (NameNotFoundException e) {
                e.printStackTrace();
            }
	    }
	    // リスト表示設定
	 //   ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, appList);
	    //setListAdapter(adapter);
	 }

	// ランチャーから起動できるアプリ一覧
	 // http://9ensan.com/blog/smartphone/android/android-install-list/
	 private void GetLauncherApp() {
		// リスト作成
	     ArrayList<String> appList = new ArrayList<String>();
	     // パッケージマネージャーの作成
	     PackageManager packageManager = getPackageManager();
	     // ランチャーから起動出来るアプリケーションの一覧
	     Intent intent = new Intent(Intent.ACTION_MAIN, null);
	     intent.addCategory(Intent.CATEGORY_LAUNCHER);
	     List<ResolveInfo> appInfo = packageManager.queryIntentActivities(intent, 0);
	     // アプリケーション名の取得
	     if (appInfo != null) {
	         for (ResolveInfo info : appInfo) {
	             appList.add( (String)info.loadLabel(packageManager));
	             Log.i("GetLauncherApp",  (String)info.loadLabel(packageManager));
	         }
	     }
	     // リスト表示設定
	  //   ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, appList);
	     //setListAdapter(adapter);
	 }

	 // http://pentan.info/android/app/display_width_height.html
	 private void GetDpis() {
		 WindowManager windowManager = getWindowManager();
		 Display display = windowManager.getDefaultDisplay();
		 DisplayMetrics displayMetrics = new DisplayMetrics();
		 display.getMetrics(displayMetrics);

		 Log.v("widthPixels",    String.valueOf(displayMetrics.widthPixels));
		 Log.v("heightPixels",   String.valueOf(displayMetrics.heightPixels));
		 Log.v("xdpi",           String.valueOf(displayMetrics.xdpi));
		 Log.v("ydpi",           String.valueOf(displayMetrics.ydpi));
		 Log.v("density",        String.valueOf(displayMetrics.density));
		 Log.v("scaledDensity",  String.valueOf(displayMetrics.scaledDensity));

		 //Log.v("width",          String.valueOf(display.getWidth()));       // 非推奨
		 //Log.v("height",         String.valueOf(display.getHeight()));      // 非推奨
		 //Log.v("orientation",    String.valueOf(display.getOrientation())); // 非推奨
		 Log.v("refreshRate",    String.valueOf(display.getRefreshRate()));
		 //Log.v("pixelFormat",    String.valueOf(display.getPixelFormat()));
		 Log.v("rotation",       String.valueOf(display.getRotation()));
	 }

	 private void print_logcat() {
		 ArrayList<String> command = new ArrayList<String>();
		command.add("logcat");
		command.add(StringList.m_str_SKMTag + ":I *:S");	// "ActivityManager"tagメンバを取得する.
		 try {
			 Process process = Runtime.getRuntime().exec(command.toArray(new String[command.size()]));
		//		BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()), 1024);

		//	    Process process = Runtime.getRuntime().exec("logcat");
			    BufferedReader reader = new BufferedReader(
			        new InputStreamReader(process.getInputStream()));
			//    String line = reader.readLine();
			    String line = "***read start***";
			    //1出力毎にループ
			    int count = 0;
			    while((line != null) && (count < 1)) {
			    	line = reader.readLine();
			    	test_str += line;
			    	count++;
			    }
			} catch (Exception e) {}
	 }

	 private String printDate() {
		// Dateクラスによる現在時表示
		 Date date = new Date();

		// デフォルトのCalendarオブジェクト
		 Calendar cal = Calendar.getInstance();

		 String tmp = cal.get(Calendar.YEAR) + "/"
		            + (cal.get(Calendar.MONTH) + 1) + "/" + cal.get(Calendar.DATE)
		            + " " + cal.get(Calendar.HOUR_OF_DAY) + ":"
		            + cal.get(Calendar.MINUTE) + ":" + cal.get(Calendar.SECOND);

		 return tmp;

	 }




}