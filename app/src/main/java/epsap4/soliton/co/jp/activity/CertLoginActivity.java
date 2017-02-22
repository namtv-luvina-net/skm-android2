package epsap4.soliton.co.jp.activity;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.security.KeyChain;
import android.security.KeyChainAliasCallback;
import android.security.KeyChainException;
import android.util.Log;
import android.util.Xml;
import android.view.View;
import android.view.WindowManager.LayoutParams;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;


import org.xmlpull.v1.XmlSerializer;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.net.URLEncoder;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.SecureRandom;
import java.security.cert.CertStore;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;

import epsap4.soliton.co.jp.AddressInfo;
import epsap4.soliton.co.jp.ConfigrationProcess;
import epsap4.soliton.co.jp.HttpConnectionCtrl;
import epsap4.soliton.co.jp.InformCtrl;
import epsap4.soliton.co.jp.LogCtrl;
import epsap4.soliton.co.jp.activity.ProfileActivity.dict_num;
import epsap4.soliton.co.jp.R;
import epsap4.soliton.co.jp.StringList;

import epsap4.soliton.co.jp.dbalias.DatabaseHandler;
import epsap4.soliton.co.jp.dbalias.ItemAlias;
import epsap4.soliton.co.jp.instapp.InstallAppListActivity;
import epsap4.soliton.co.jp.mdm.MDMCheckinActivity;
import epsap4.soliton.co.jp.mdm.MDMControl;
import epsap4.soliton.co.jp.mdm.MDMFlgs;
import epsap4.soliton.co.jp.notification.AlarmReceiver;
import epsap4.soliton.co.jp.scep.Requester;
import epsap4.soliton.co.jp.scep.RequesterException;
import epsap4.soliton.co.jp.scep.cert.CertificateUtility;
import epsap4.soliton.co.jp.scep.pkimessage.CertRep;
import epsap4.soliton.co.jp.scep.pkimessage.PkiStatus;
import epsap4.soliton.co.jp.wifi.WifiControl;
import epsap4.soliton.co.jp.xmlparser.XmlDictionary;
import epsap4.soliton.co.jp.xmlparser.XmlPullParserAided;
import epsap4.soliton.co.jp.xmlparser.XmlStringData;

import org.bouncycastle.jce.PKCS10CertificationRequest;

public class CertLoginActivity extends Activity
        implements View.OnClickListener, Runnable
        , KeyChainAliasCallback {

    // UIの変数設定
    private Button m_ButtonLogin;
    private Button m_ButtonUserAuth;
    private Button m_ButtonOpenGuide;
    private Button m_ButtonUdidsaw;
    private Button m_ButtonAppList;
    private EditText m_EditUserID;
    private EditText m_EditPassword;
    private EditText m_EditURL;
    private EditText m_EditUDID;
    private TextView m_TextErrorLogin;
    private TextView m_TextUserID;
    private TextView m_TextPassword;
    private TextView m_TextAPID;
    private View m_separator02;
    private TextView m_TextApptitle;
    private Spinner m_Spi_certificate;
    private TextView m_TextCertType;
    private EditText m_EditVpnAPID;
    private TextView m_TextWifiApid;
    private TextView m_TextVpnApid;

    private static InformCtrl m_InformCtrl;
    private /*static*/ InformCtrl m_InformCtrl4EPS = new InformCtrl();
    private XmlPullParserAided m_p_aided = null;
    private XmlPullParserAided m_p_aided_profile = null;
    private ProgressDialog progressDialog;

    private int m_nApplicationList = 75;
    private int m_nMDM_RequestCode = 70;
    private int m_nGuidePageRequestCode = 65;
    private int m_nCertReq_RequestCode = 60;
    private int m_nEnrollRtnCode = 55;

    int m_nErroType;

    boolean m_bprofile_state = true;    // プロファイル情報の正常性
    private String m_strAPIDWifi = "";    // APID (Wi-Fi)		# 21391
    private String m_strAPIDVPN = "";    // APID (VPNとアプリ)# 21391

    int m_nConnectionActionType;
    private static int CONN_LOGIN = 10;
    private static int CONN_SCEP = 11;
    private static int CONN_DROP = 12;
    private static int CONN_USRAUTH = 13;
    private static int CONN_MDMCHKOUT = 14;

    // SCEP
    private Requester scepRequester = null;
    private CertStore cACertificateStore = null;
    private String m_strSelectArias = "";
    private String m_strServerURL = "";
    private String m_strSubject = "";
    private String m_strChallenge = "";
    private String m_strCertArias = "";
    private String m_strKeyType = "";
    private String m_strSubjectAltName = "";

    // 連絡先
    private Button m_MailButton;
    private Button m_PhoneButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {

//	    	this.setTitle(R.string.ApplicationTitle);
        super.onCreate(savedInstanceState);

        this.getWindow().setSoftInputMode(LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        setContentView(R.layout.cert_login);

        // 情報管理クラスの取得
        Intent intent = getIntent();
        m_InformCtrl = (InformCtrl) intent.getSerializableExtra(StringList.m_str_InformCtrl);

        setUItoMember();

        // 通信中ダイアログを表示させる。
//		 progressDialog = new ProgressDialog(this);
//		 progressDialog.setTitle("通信中");
//		 progressDialog.setMessage("データ取得中・・・");
//		 progressDialog.setIndeterminate(false);
//		 progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
//		 progressDialog.show();

        // サーバーとの通信をスレッドで行う
//		 Thread thread = new Thread(this);	// 自分クラスをスレッドの引数に渡して...
//		 thread.start();						// run()が実行される

        //	http_connection();

        // Debug
        //PrintViewKeyStore();
        //	 MDMThread testthread = new MDMThread();
        //	 testthread.start();

    }

    private void setUItoMember() {
        m_EditUserID = (EditText) findViewById(R.id.EditUserID);    // ユーザIDエディット
        m_EditPassword = (EditText) findViewById(R.id.EditPassword);    // パスワードエディット

        m_ButtonUserAuth = (Button) findViewById(R.id.button_user_auth);
        m_ButtonUserAuth.setOnClickListener(this);

        m_ButtonLogin = (Button) findViewById(R.id.button_start_cert);
        m_ButtonLogin.setOnClickListener(this);

        m_ButtonOpenGuide = (Button) findViewById(R.id.button_open_guide);
        m_ButtonOpenGuide.setOnClickListener(this);

//		 m_Seterr = (TextView)findViewById(R.id.profile_set_err);
//		 m_Seterr.setTextColor(Color.rgb(255,20,20));

//		 m_ButtonEndPscode = (Button)findViewById(R.id.ButtonEndpasscode);
//		 m_ButtonStartPscode = (Button)findViewById(R.id.ButtonStartpasscode);
//		 m_ButtonEndPscode.setOnClickListener(this);
//		 m_ButtonStartPscode.setOnClickListener(this);

        m_TextErrorLogin = (TextView) findViewById(R.id.ErrorLoginMessage);    // エラーメッセージ
        m_TextErrorLogin.setTextColor(Color.rgb(255, 20, 20));
        m_TextErrorLogin.setVisibility(View.GONE);                            // 初期設定では非表示

        m_TextUserID = (TextView) findViewById(R.id.UserID);
        m_TextPassword = (TextView) findViewById(R.id.Password);

        m_ButtonUdidsaw = (Button) findViewById(R.id.button_UDID_saw);
        m_ButtonUdidsaw.setOnClickListener(this);

        m_EditUDID = (EditText) findViewById(R.id.EditUDID);
        m_EditUDID.setVisibility(View.GONE);

        m_EditVpnAPID = (EditText) findViewById(R.id.EditVPNAPID);
        m_EditVpnAPID.setVisibility(View.GONE);

        m_TextWifiApid = (TextView) findViewById(R.id.apid_wifi_comment);
        m_TextWifiApid.setVisibility(View.GONE);

        m_TextVpnApid = (TextView) findViewById(R.id.apid_vpn_comment);
        m_TextVpnApid.setVisibility(View.GONE);

        m_TextAPID = (TextView) findViewById(R.id.apid_comment);

        m_separator02 = (View) findViewById(R.id.Separator02);
        m_TextApptitle = (TextView) findViewById(R.id.APPInstTitle);
        m_ButtonAppList = (Button) findViewById(R.id.button_application_list);
        m_ButtonAppList.setOnClickListener(this);

        m_Spi_certificate = (Spinner) findViewById(R.id.Spinner_certificate);
        m_TextCertType = (TextView) findViewById(R.id.CertTypeMsg);
        // OSバージョン確認 4.3未満の時はSpinnerは[Wi-Fi]固定で選択させないようにする
        double d_android_version = ConfigrationProcess.getAndroidOsVersion();
        if (d_android_version < 4.3) m_Spi_certificate.setEnabled(false);

        // 連絡先
        m_MailButton = (Button) findViewById(R.id.Button_Mail);
        m_PhoneButton = (Button) findViewById(R.id.Button_Phone);
        m_MailButton.setSingleLine();
        m_MailButton.setOnClickListener(this);
        m_PhoneButton.setOnClickListener(this);
        if (AddressInfo.GetMailAddress().length() > 0) {
            m_MailButton.setVisibility(View.VISIBLE);
            String strmsg = getText(R.string.MailRequest).toString() + AddressInfo.GetMailAddress();
            m_MailButton.setText(strmsg);
        } else m_MailButton.setVisibility(View.GONE);
        if (AddressInfo.GetPhoneNumber().length() > 0) {
            m_PhoneButton.setVisibility(View.VISIBLE);
            String strmsg = getText(R.string.PhoneRequest).toString() + AddressInfo.GetPhoneNumber();
            m_PhoneButton.setText(strmsg);
        } else m_PhoneButton.setVisibility(View.GONE);

        // Login User Info
        ReadAndSetLoginUserInfo();

        // 証明書申請画面の表示設定
        SetCertificateVisible(View.GONE);

    }

    private void SetUserAuthVisible(int visible) {

        m_TextUserID.setVisibility(visible);
        m_TextPassword.setVisibility(visible);
        m_EditUserID.setVisibility(visible);
        m_EditPassword.setVisibility(visible);
        // ユーザー認証ボタン
        m_ButtonUserAuth.setVisibility(visible);
        m_Spi_certificate.setVisibility(visible);
        m_TextCertType.setVisibility(visible);

    }

    private void SetCertificateVisible(int visible) {
        m_ButtonLogin.setVisibility(visible);
        m_ButtonOpenGuide.setVisibility(visible);
        m_ButtonUdidsaw.setVisibility(visible);
        m_TextAPID.setVisibility(visible);
        m_separator02.setVisibility(visible);
        m_TextApptitle.setVisibility(/*visible*/View.GONE);
        m_ButtonAppList.setVisibility(/*visible*/View.GONE);
    }

    @Override
    public void run() {
        // TODO 自動生成されたメソッド・スタブ
        if (m_nConnectionActionType == CONN_LOGIN) {
            http_user_login();
        } else if (m_nConnectionActionType == CONN_SCEP) {
            // SCEP
            http_scep();
        } else if (m_nConnectionActionType == CONN_DROP) {
            http_cert_drop();
        } else if (m_nConnectionActionType == CONN_USRAUTH) {
            // ユーザー認証
            http_user_auth();
        } else if (m_nConnectionActionType == CONN_MDMCHKOUT) {
            // MDM checkout
            OldMdmCheckOut();
        }
        handler.sendEmptyMessage(0);
    }

    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            // 処理終了時の動作をここに記述。
            //	 SetEditMember();
            // プログレスダイアログ終了
            try {
                CertRequestActivity.endProgress(progressDialog);
                //progressDialog.dismiss();
            } catch (Exception e) {
            }

            if (m_nErroType == CertRequestActivity.ERR_FORBIDDEN) {
                String str_forbidden = getString(R.string.Forbidden);
                m_TextErrorLogin.setVisibility(View.VISIBLE);
                m_TextErrorLogin.setText(R.string.Forbidden);
                //	 m_TextErrorLogin.setText(m_InformCtrl.GetRtn().substring(str_forbidden.length()-1));

                SetButtonRunnable(true);    // ボタン活性化

				 /*--- Enroll失敗時は申請取り消しを送信する  ---*/
                m_nConnectionActionType = CONN_DROP;

                m_nErroType = CertRequestActivity.SUCCESSFUL;

                // サーバーとの通信をスレッドで行う
                Thread thread = new Thread(CertLoginActivity.this);
                thread.start();                        // run()が実行される

            } else if (m_nErroType == CertRequestActivity.ERR_UNAUTHORIZED) {
                String str_unauth = getString(R.string.Unauthorized);
                m_TextErrorLogin.setVisibility(View.VISIBLE);
                m_TextErrorLogin.setText(R.string.Unauthorized);
                //	 m_TextErrorLogin.setText(m_InformCtrl.GetRtn().substring(str_unauth.length()));

                SetButtonRunnable(true);    // ボタン活性化

            } else if (m_nErroType == CertRequestActivity.ERR_NETWORK) {
                m_TextErrorLogin.setVisibility(View.VISIBLE);
                m_TextErrorLogin.setText(R.string.LoginErrorMessage);

                SetButtonRunnable(true);    // ボタン活性化
            } else if (m_nErroType == CertRequestActivity.ERR_COLON) {
                String str_err = getString(R.string.ERR);
                m_TextErrorLogin.setVisibility(View.VISIBLE);
                m_TextErrorLogin.setText(m_InformCtrl.GetRtn().substring(str_err.length()));

                SetButtonRunnable(true);    // ボタン活性化

            }

            // ログイン後,認証確認成功
            if ((m_nConnectionActionType == CONN_LOGIN) &&
                    (m_nErroType == CertRequestActivity.AUTHENTICATION_SUCCESSFUL)) {
                try {
                    progressDialog.show();
                } catch (Exception e) {
                }
                // 証明書のインストール
                scepRequester = getScepRequester();
                new CACertificateInstallTask().execute(scepRequester);
            }

            if ((m_nConnectionActionType == CONN_USRAUTH) &&
                    (m_nErroType == CertRequestActivity.SUCCESSFUL)) {
                SetUserAuthVisible(View.GONE);
                SetCertificateVisible(View.VISIBLE);
                SetButtonRunnable(true);    // ボタン活性化
            }

        }
    };

    private void http_user_login() {

        // ログインボタン
        LogCtrl.Logger(LogCtrl.m_strInfo, "CertLoginAcrivity::onClick  " + "Push LoginButton", this);
        //	LogCtrl.Logger(LogCtrl.m_strInfo, "CertLoginAcrivity::onClick  " + m_EditUserID.getText().toString(), this);
        //	LogCtrl.Logger(LogCtrl.m_strInfo, "CertLoginAcrivity::onClick  " + m_EditPassword.getText().toString(), this);

        //	printAlias();

//* ひとまずコメント
        String str_userid = m_EditUserID.getText().toString();
        String str_passwd = m_EditPassword.getText().toString();
        String str_url = m_InformCtrl.GetURL();

//		if (str_url.startsWith("http") == true) {
        // URLがhttpの場合、認証を開始しても正常な応答メッセージが返ってこないため、解析で落ちてしまうことがあるため、
        // 認証前にURLチェックを行う
//			Log.e("CertLoginAcrivity::onClick", "Not HTTPS!!");
        //	m_TextErrorLogin.setText(R.string.LoginErrorMessage);
//			m_nErroType = CertRequestActivity.ERR_NETWORK;
//			return;
//		}

        String rtnserial = "";
        if (m_Spi_certificate.getSelectedItemPosition() == 0)    // 0:Wifi, 1:VPN&Application
            rtnserial = m_strAPIDWifi;//XmlPullParserAided.GetUDID(this);//GetSerialCode();//telManager.getDeviceId();
        else rtnserial = m_strAPIDVPN;//XmlPullParserAided.GetVpnApid(this);

        // ログインメッセージ
        String message;
        try {
            message = "Action=logon" + "&" + StringList.m_strUserID + URLEncoder.encode(str_userid, "UTF-8") +
                    "&" + StringList.m_strPassword + URLEncoder.encode(str_passwd, "UTF-8") +
                    "&" + StringList.m_strSerial + rtnserial;
            Log.i(StringList.m_str_SKMTag, "http_user_login:: " + "LoginMsg=" + message);
            //	LogCtrl.Logger(LogCtrl.m_strInfo, "CertLoginAcrivity-- " + "LoginMsg=" + message, this);
            LogCtrl.Logger(LogCtrl.m_strInfo, "http_user_login-- " + "USER ID=" + str_userid, this);
            LogCtrl.Logger(LogCtrl.m_strInfo, "http_user_login-- " + "URL=" + str_url, this);
        } catch (UnsupportedEncodingException e) {
            // TODO 自動生成された catch ブロック
            //	e.printStackTrace();
            LogCtrl.Logger(LogCtrl.m_strError, "CertLoginAcrivity::onClick UnsupportedEncodingException " + e.toString(), this);
            m_nErroType = CertRequestActivity.ERR_NETWORK;
            return;
        }

        // 入力データを情報管理クラスへセットする
        m_InformCtrl.SetUserID(str_userid);
        m_InformCtrl.SetPassword(str_passwd);
        m_InformCtrl.SetURL(str_url);
        m_InformCtrl.SetMessage(message);
        m_InformCtrl.SetAPID(rtnserial);

        ////////////////////////////////////////////////////////////////////////////
        // 大項目1. ログイン開始 <=========
        ////////////////////////////////////////////////////////////////////////////
        HttpConnectionCtrl conn = new HttpConnectionCtrl(this);
        boolean ret = conn.RunHttpLoginUrlConnection(m_InformCtrl);
        if (ret == false) {
            LogCtrl.Logger(LogCtrl.m_strError, "CertLoginAcrivity::onClick  " + "Login Error.", this);
            //	m_TextErrorLogin.setText(R.string.LoginErrorMessage);
            m_nErroType = CertRequestActivity.ERR_NETWORK;
            return;
        }

        Log.i(StringList.m_str_SKMTag, "CertLoginAcrivity::onClick:RTN = " + m_InformCtrl.GetRtn());
        Log.i(StringList.m_str_SKMTag, "CertLoginAcrivity:Gookie= " + m_InformCtrl.GetCookie());
        //RunHttpLoginDefaultHttpClient();
        //RunHttpLoginUrlConnection();

        // ログイン結果
        if (m_InformCtrl.GetRtn().startsWith(getText(R.string.Forbidden).toString())) {
            LogCtrl.Logger(LogCtrl.m_strError, "CertLoginAcrivity::onClick  " + "Forbidden.", this);
            m_nErroType = CertRequestActivity.ERR_FORBIDDEN;
            return;
        } else if (m_InformCtrl.GetRtn().startsWith(getText(R.string.Unauthorized).toString())) {
            LogCtrl.Logger(LogCtrl.m_strError, "CertLoginAcrivity::onClick  " + "Unauthorized.", this);
            m_nErroType = CertRequestActivity.ERR_UNAUTHORIZED;
            return;
        } else if (m_InformCtrl.GetRtn().startsWith(getText(R.string.ERR).toString())) {
            LogCtrl.Logger(LogCtrl.m_strError, "CertLoginAcrivity::onClick  " + "ERR:", this);
            m_nErroType = CertRequestActivity.ERR_COLON;
            return;
        }

        // 取得したCookieをログイン時のCookieとして保持する.
        m_InformCtrl.SetLoginCookie(m_InformCtrl.GetCookie());

        ///////////////////////////////////////////////////
        // 認証応答の解析(Enroll応答のときの対応を流用できるはず)
        ///////////////////////////////////////////////////
        // 取得XMLのパーサー
        m_p_aided = new XmlPullParserAided(this, m_InformCtrl.GetRtn(), 2);    // 最上位dictの階層は2になる

        ret = m_p_aided.TakeApartUserAuthenticationResponse(m_InformCtrl);
        if (ret == false) {
            LogCtrl.Logger(LogCtrl.m_strError, "CertLoginAcrivity::onClick  " + "TakeApartDevice false", this);
            //	m_ErrorMessage.setText(R.string.EnrollErrorMessage);
            m_nErroType = CertRequestActivity.ERR_NETWORK;
            return;
        }
        ////////////////////////////////////////////////////////////////////////////
        // 大項目1. ログイン終了 =========>
        ////////////////////////////////////////////////////////////////////////////

        ///////////////////////////////////////////////////
        // 1. 承認済みのときはなら次はDeviceInfoメッセージを構築して送信
        // 2. 申請されていないときは、申請フォームを表示(別Activity)
        // 3. 申請済みで承認がされていないときは、申請フォームを表示(2と別Activityにするかor2と同Activityで表示に手を加える)
        ///////////////////////////////////////////////////
        XmlDictionary xml_dict = m_p_aided.GetDictionary();
        boolean b_issubmit = false;
        if (xml_dict != null) {
            b_issubmit = IsSubmitFrag(xml_dict);
        }

        if (b_issubmit == true) {
            m_nErroType = CertRequestActivity.SUCCESSFUL;
            return;
        }

        ///////////////////////////////////////////////////
        // 1. 承認済みルート↓
        ///////////////////////////////////////////////////
        ////////////////////////////////////////////////////////////////////////////
        // 大項目2. デバイス登録開始 <=========
        ////////////////////////////////////////////////////////////////////////////
        String sendmsg = m_p_aided.DeviceInfoText(rtnserial);

        Log.i(StringList.m_str_SKMTag, "Enroll RTN MESSAGE= " + sendmsg);

        m_InformCtrl.SetMessage(sendmsg);

        // EnrollActivityではRunHttpConfigrationUrlConnectionを呼んでいるが、API名の変更が必要かも.
        ret = conn.RunHttpDeviceCertUrlConnection(m_InformCtrl);
        if (ret == false) {
            LogCtrl.Logger(LogCtrl.m_strError, "CertLoginAcrivity::onClick  " + "Device Certificate Error.", this);
            m_nErroType = CertRequestActivity.ERR_NETWORK;
            return;
        }

        if (m_InformCtrl.GetRtn().startsWith(getText(R.string.Forbidden).toString())) {
            LogCtrl.Logger(LogCtrl.m_strError, "CertLoginAcrivity::onClick " + "Forbidden.", this);
            m_nErroType = CertRequestActivity.ERR_FORBIDDEN;
            return;
        } else if (m_InformCtrl.GetRtn().startsWith(getText(R.string.Unauthorized).toString())) {
            LogCtrl.Logger(LogCtrl.m_strError, "CertLoginAcrivity::onClick  " + "Unauthorized.", this);
            m_nErroType = CertRequestActivity.ERR_UNAUTHORIZED;
            return;
        } else if (m_InformCtrl.GetRtn().startsWith(getText(R.string.ERR).toString())) {
            LogCtrl.Logger(LogCtrl.m_strError, "CertLoginAcrivity::onClick  " + "ERR:", this);
            m_nErroType = CertRequestActivity.ERR_COLON;
            return;
        }

        // 取得したCookieをログイン時のCookieとして保持する.
        m_InformCtrl.SetLoginCookie(m_InformCtrl.GetCookie());

        ///////////////////////////////////////////////////
        // Device登録応答の解析
        // 1. Scep情報の解析
        // 2. プロパティ情報(Wi-Fiなど)の解析
        ///////////////////////////////////////////////////
        // 取得XMLのパーサー
        m_p_aided = new XmlPullParserAided(this, m_InformCtrl.GetRtn(), 2);    // 最上位dictの階層は2になる

        ret = m_p_aided.TakeApartScepInfoResponse(m_InformCtrl);
        if (ret == false) {
            LogCtrl.Logger(LogCtrl.m_strError, "CertLoginAcrivity::onClick  " + "TakeApartScepInfoResponse false", this);
            //	m_ErrorMessage.setText(R.string.EnrollErrorMessage);
            m_nErroType = CertRequestActivity.ERR_NETWORK;
            return;
        }

        ////////////////////////////////////////////////////////////////////////////
        // 大項目2. デバイス登録開始 =========>
        ////////////////////////////////////////////////////////////////////////////

        ////////////////////////////////////////////////////////////////////////////
        // 大項目3. SCEP開始 <=========
        ////////////////////////////////////////////////////////////////////////////

        // XmlPullParserAided::TakeApartScepInfoResponseで解析したパラメータとInformCtrlに設定したパラメータから
        // 証明書要求の項目を構成する.
        SetScepItem();

        // Wi-Fi情報が入ってくるため、Device登録応答を再度解析する
        // XmlPullParserAidedのメンバをTakeApartProfileで上書きしてしまう恐れがあるため、SetScepItemの後に実行する
        m_p_aided_profile = m_p_aided;
        ret = m_p_aided_profile.TakeApartProfile();
        if (ret == false) {
            LogCtrl.Logger(LogCtrl.m_strError, "CertLoginAcrivity::onClick  " + "TakeApartProfile false", this);
            //	m_ErrorMessage.setText(R.string.EnrollErrorMessage);
            m_nErroType = CertRequestActivity.ERR_NETWORK;
            return;
        }

        // EPSとのSCEP通信開始 コメント
        //	scepRequester = getScepRequester();
        //	new CACertificateInstallTask().execute(scepRequester);

        m_nErroType = CertRequestActivity.AUTHENTICATION_SUCCESSFUL;
        return;

    }

    private void http_user_auth() {
        // ログインボタン
        LogCtrl.Logger(LogCtrl.m_strInfo, "http_user_auth-- " + "Push UserAuthenticationButton", this);
        Log.i(StringList.m_str_SKMTag, "http_user_auth-- " + m_EditUserID.getText().toString());
        //	Log.i(StringList.m_str_SKMTag, "http_user_auth-- "+ m_EditPassword.getText().toString());

        //	printAlias();

        //* ひとまずコメント
        String str_userid = m_EditUserID.getText().toString();
        String str_passwd = m_EditPassword.getText().toString();
        String str_url = m_InformCtrl.GetURL();

//				if (str_url.startsWith("http") == true) {
        // URLがhttpの場合、認証を開始しても正常な応答メッセージが返ってこないため、解析で落ちてしまうことがあるため、
        // 認証前にURLチェックを行う
//					Log.e("CertLoginAcrivity::onClick", "Not HTTPS!!");
        //	m_TextErrorLogin.setText(R.string.LoginErrorMessage);
//					m_nErroType = CertRequestActivity.ERR_NETWORK;
//					return;
//				}

        // m_Spi_certificate.getSelectedItemPosition() :: Spinnerのポジション取得 0～
        // (String)m_Spi_certificate.getSelectedItem() :: Spinnerの選択項目の文字列取得
        String rtnserial = "";
        if (m_Spi_certificate.getSelectedItemPosition() == 0)
            rtnserial = m_strAPIDWifi;//XmlPullParserAided.GetUDID(this);			// #21391
        else rtnserial = m_strAPIDVPN;//XmlPullParserAided.GetVpnApid(this);		// #21391

        // ログインメッセージ
        // URLEncodeが必須 <http://wada811.blog.fc2.com/?tag=URL%E3%82%A8%E3%83%B3%E3%82%B3%E3%83%BC%E3%83%89>参照
        String message;
        try {
            message = "Action=logon" + "&" + StringList.m_strUserID + URLEncoder.encode(str_userid, "UTF-8") +
                    "&" + StringList.m_strPassword + URLEncoder.encode(str_passwd, "UTF-8") +
                    "&" + StringList.m_strSerial + rtnserial;
            // + "&" + "LOGON=1";
            Log.i(StringList.m_str_SKMTag, "http_user_auth:: " + "LoginMsg=" + message);
            //	LogCtrl.Logger(LogCtrl.m_strInfo, "http_user_auth::  "+ "LoginMsg=" + message, this);
            LogCtrl.Logger(LogCtrl.m_strInfo, "http_user_auth-- " + "USER ID=" + str_userid, this);
            LogCtrl.Logger(LogCtrl.m_strInfo, "http_user_auth::  " + "URL=" + str_url, this);
        } catch (UnsupportedEncodingException e) {
            // TODO 自動生成された catch ブロック
            e.printStackTrace();
            m_nErroType = CertRequestActivity.ERR_NETWORK;
            return;
        }

        // 入力データを情報管理クラスへセットする
        m_InformCtrl.SetUserID(str_userid);
        m_InformCtrl.SetPassword(str_passwd);
        m_InformCtrl.SetURL(str_url);
        m_InformCtrl.SetMessage(message);

        ////////////////////////////////////////////////////////////////////////////
        // 大項目1. ログイン開始 <=========
        ////////////////////////////////////////////////////////////////////////////
        HttpConnectionCtrl conn = new HttpConnectionCtrl(this);
        boolean ret = conn.RunHttpLoginUrlConnection(m_InformCtrl);
        if (ret == false) {
            LogCtrl.Logger(LogCtrl.m_strError, "http_user_auth  " + "Login Error.", this);
            //	m_TextErrorLogin.setText(R.string.LoginErrorMessage);
            m_nErroType = CertRequestActivity.ERR_NETWORK;
            return;
        }

        Log.i(StringList.m_str_SKMTag, "http_user_auth:RTN  " + m_InformCtrl.GetRtn());
        Log.i(StringList.m_str_SKMTag, "http_user_auth:Gookie:" + m_InformCtrl.GetCookie());
        //RunHttpLoginDefaultHttpClient();
        //RunHttpLoginUrlConnection();

        // ログイン結果
        if (m_InformCtrl.GetRtn().startsWith(getText(R.string.Forbidden).toString())) {
            LogCtrl.Logger(LogCtrl.m_strError, "http_user_auth  " + "Forbidden.", this);
            m_nErroType = CertRequestActivity.ERR_FORBIDDEN;
            return;
        } else if (m_InformCtrl.GetRtn().startsWith(getText(R.string.Unauthorized).toString())) {
            LogCtrl.Logger(LogCtrl.m_strError, "http_user_auth  " + "Unauthorized.", this);
            m_nErroType = CertRequestActivity.ERR_UNAUTHORIZED;
            return;
        } else if (m_InformCtrl.GetRtn().startsWith(getText(R.string.ERR).toString())) {
            LogCtrl.Logger(LogCtrl.m_strError, "CertLoginAcrivity::onClick  " + "ERR:", this);
            m_nErroType = CertRequestActivity.ERR_COLON;
            return;
        }

        // 取得したCookieをログイン時のCookieとして保持する.
        m_InformCtrl.SetLoginCookie(m_InformCtrl.GetCookie());

        ///////////////////////////////////////////////////
        // 認証応答の解析(Enroll応答のときの対応を流用できるはず)
        ///////////////////////////////////////////////////
        // 取得XMLのパーサー
        m_p_aided = new XmlPullParserAided(this, m_InformCtrl.GetRtn(), 2);    // 最上位dictの階層は2になる

        ret = m_p_aided.TakeApartUserAuthenticationResponse(m_InformCtrl);
        if (ret == false) {
            LogCtrl.Logger(LogCtrl.m_strError, "http_user_auth-- " + "TakeApartDevice false", this);
            //	m_ErrorMessage.setText(R.string.EnrollErrorMessage);
            m_nErroType = CertRequestActivity.ERR_NETWORK;
            return;
        }
        ////////////////////////////////////////////////////////////////////////////
        // 大項目1. ログイン終了 =========>
        ////////////////////////////////////////////////////////////////////////////

        m_nErroType = CertRequestActivity.SUCCESSFUL;

        return;
    }

    private void http_scep() {
        //	printAlias();

        String message = "";    // メッセージはひとまず空.
        m_InformCtrl.SetMessage(message);

        HttpConnectionCtrl conn = new HttpConnectionCtrl(this);
        boolean ret = conn.RunHttpGetScepProfile(m_InformCtrl, m_strSelectArias);

        if (ret == false) {
            LogCtrl.Logger(LogCtrl.m_strError, "CertLoginActivity::http_scep " + "Get Profile Error1.", this);
            return;
        }

        //////////////////////////////////////////////////////////////
        // 最後にサービスを起動して、定期的に問い合わせを行うようにする
        /////////////////////////////////////////////////////////////
        // alias情報等を保存する
/*    	MDMFlgs mdmf = new MDMFlgs();
        mdmf.SetAlias(m_strSelectArias);
    	mdmf.SetEpsapUrl(m_InformCtrl.GetURL());
    	mdmf.WriteScepMdmInfo(this);

    	MDMControl mdmctrl = new MDMControl(this);
    	mdmctrl.SrartService(mdmf);*/
    }

    // 証明書の申請取り消し
    private void http_cert_drop() {
        m_InformCtrl.SetMessage("Action=drop");
        //	Log.e("CertLoginActivity::http_cert_drop", m_InformCtrl.GetLoginCookie());
        LogCtrl.Logger(LogCtrl.m_strError, "CertLoginActivity::http_cert_drop " + m_InformCtrl.GetURL(), this);
        //	LogCtrl.Logger(LogCtrl.m_strError, "CertLoginActivity::http_cert_drop "+ m_InformCtrl.GetPassword(), this);
        //	Log.e("CertLoginActivity::http_cert_drop", m_InformCtrl.GetPassword());
        LogCtrl.Logger(LogCtrl.m_strError, "CertLoginActivity::http_cert_drop " + m_InformCtrl.GetUserID(), this);

        // 申請
        HttpConnectionCtrl conn = new HttpConnectionCtrl(this);
        boolean ret = conn.RunHttpApplyUrlConnection(m_InformCtrl);        // 専用のRunHttpを作成する


    }

    private boolean SetScepItem() {
        // URL
        m_strServerURL = m_InformCtrl.GetSituationURL();
        // Subject
        List<String> list = m_p_aided.GetSubjectList();
        String subject_string = "";
        for (int n = 0; list.size() > n; n++) {
            if (subject_string.length() == 0) {
                subject_string = list.get(n);
                subject_string += "=";
            } else if ((n % 2) == 0) {
                subject_string += ", ";
                subject_string += list.get(n);
                subject_string += "=";
            } else if ((n % 2) == 1) {
                // na-prj #8321対応
                // サブジェクトに含まれている","を取り除いて文字列を再形成する
                // http://www.javadrive.jp/start/string_class/index5.html
                //	String[] str_split = list.get(n).split(",", 0);
                //	for(int m = 0; str_split.length > m; m++) {
                //		Log.d("CertLoginActivity::SetScepItem", str_split[m]);
                //		subject_string += str_split[m];
                //	}
                subject_string += "\"";
                subject_string += list.get(n);
                subject_string += "\"";
            }

        }
        m_strSubject = subject_string;
        // v1.2.1以降---SubjectにmailAddressを追加する場合があるので確認は最後に...

        // Challenge, Name, KeyType
        XmlDictionary dict = m_p_aided.GetDictionary();
        List<XmlStringData> str_list;
        if (dict != null) {
            str_list = dict.GetArrayString();
            for (int i = 0; str_list.size() > i; i++) {
                // config情報に従って、処理を行う.
                XmlStringData p_data = str_list.get(i);
                SetEditMemberChild(p_data);
            }
        }

        LogCtrl.Logger(LogCtrl.m_strDebug, "CertLoginAcrivity::SetScepItem " + "Subject: " + m_strSubject, this);

        return true;
    }

    private void SetEditMemberChild(XmlStringData data) {
        String strKeyName = data.GetKeyName();    // キー名
        int i_type = data.GetType();        // 要素タイプ(string:1, data=2, date=3, real=4, integer=5, true=6, false=7)
        String strData = data.GetData();        // 要素
        LogCtrl.Logger(LogCtrl.m_strInfo, "CertLoginAcrivity::SetEditMemberChild "
                + "Key= " + strKeyName + " , Data= " + strData, this);

//		ConfigrationProcess p_cnf = new ConfigrationProcess(m_ctx, m_DPM, m_DeviceAdmin);

        boolean b_type = true;
        if (i_type == 7) b_type = false;

        // Chalenge
        if (strKeyName.equalsIgnoreCase(StringList.m_str_scep_challenge)) {    // Challenge
            m_strChallenge = strData;
        } else if (strKeyName.equalsIgnoreCase(StringList.m_str_CaIdent)) {    // Name(Arias)
            m_strCertArias = strData;
        } else if (strKeyName.equalsIgnoreCase(StringList.m_str_scep_keytype)) {    // Key Type
            m_strKeyType = strData;
        } else if (strKeyName.equalsIgnoreCase(StringList.m_str_scep_rfc822Name)) {    // rfc822Name
            // rfc822NameはmailAddressとしてSubjectに追加.
        /*	if(m_strSubject.length() == 0) {
				m_strSubject = StringList.m_str_scep_mailaddr;
			} else {
				m_strSubject += ", ";
				m_strSubject += StringList.m_str_scep_mailaddr;
			}
			m_strSubject += "=";
			m_strSubject += "\"";
			m_strSubject += strData;
			m_strSubject += "\"";*/

            // チケット #8907 メールアドレスをSubjectAltNameとして設定しておく
            m_strSubjectAltName = strData;
        }
    }

    private boolean IsSubmitFrag(XmlDictionary xml_dict) {
        boolean bret = false;
        boolean is_submitted;
        List<XmlStringData> str_list;
        str_list = xml_dict.GetArrayString();
        for (int i = 0; str_list.size() > i; i++) {
            // config情報に従って、処理を行う.
            XmlStringData p_data = str_list.get(i);
            if (p_data.GetKeyName().equalsIgnoreCase(StringList.m_str_issubmitted) == true) {
                bret = true;            // IsSubmittedキーは存在したよ.
                m_InformCtrl.SetSubmitted(p_data.GetType());
            } else if (p_data.GetKeyName().equalsIgnoreCase(StringList.m_str_mailaddress)) {
                // InformCtrlに追加
                m_InformCtrl.SetMailAddress(p_data.GetData());
            } else if (p_data.GetKeyName().equalsIgnoreCase(StringList.m_str_description)) {
                // InformCtrlに追加
                m_InformCtrl.SetDescription(p_data.GetData());
            }

        }

        if (bret == true) {
            // CertRequestActivity実行. InformCtrlをputExtraする
            // リストから選択されたプロファイルの情報を元に個別のプロファイルアクティビティを作成する
            Intent AppIntent = new Intent(this, CertRequestActivity.class);
            // ビューのリストを新しいintentに引き渡す.HTTP通信もそちらで行う。
            AppIntent.putExtra(StringList.m_str_InformCtrl, m_InformCtrl);

            startActivityForResult(AppIntent, m_nCertReq_RequestCode);
        }

        return bret;
    }

    // 子Activityからアプリを終了する方法
    // 参照:http://ymgcsng.blogspot.jp/2010/12/activity.html
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        LogCtrl.Logger(LogCtrl.m_strInfo, "onActivityResult start " + "REC CODE = " + Integer.toString(requestCode), this);
        if (requestCode == m_nCertReq_RequestCode) {
            // 申請アクティビティ終了後
            Log.i("onActivityResult", "REC CODE = " + Integer.toString(requestCode));

            SetButtonRunnable(true);    // ボタン活性化
        } else if (requestCode == 10) {
            // After CertificateInstallTask
            if (resultCode == RESULT_OK) {
                new CertificateEnrollTask().execute(scepRequester);

                //	 Thread thread = new Thread(this);	// 自分クラスをスレッドの引数に渡して...
                //        thread.start();
            } else {
                LogCtrl.Logger(LogCtrl.m_strError, "onActivityResult After CertificateInstallTask Result error. ", this);
                try {
                    CertRequestActivity.endProgress(progressDialog);
                    //progressDialog.dismiss();
                } catch (Exception e) {
                }
                CancelScreenOrientation();
                SetButtonRunnable(true);    // ボタン活性化
            }
        } else if (requestCode == m_nEnrollRtnCode) {
           
            // After CertificateEnrollTask
            Log.i("CertLoginActivity", "REC CODE = " + Integer.toString(requestCode));

            //certificateの選択
            // OSバージョン確認
            double d_android_version = ConfigrationProcess.getAndroidOsVersion();
            if (d_android_version >= 4.3) {
                Log.i("CertLoginActivity", "OS Version " + d_android_version);//Integer.toString(requestCode));

                //	chooseCert();	// 20130208 V1.0.1ではアプリが証明書を参照することがないのでコメント
            }

            progressDialog.cancel();//dismiss();
           
            CancelScreenOrientation();

            SetButtonRunnable(true);    // ボタン活性化

            ///////////////////////////////////////////////////////////////
            // na-prj 11588案件：パラメータの事前チェック
            ///////////////////////////////////////////////////////////////
	    /*	List<XmlDictionary> wifi_list = m_p_aided_profile.GetWifiDictList();
	    	ConsistenceParameter(wifi_list, dict_num.num_wifipict);
	    	if (m_bprofile_state == false) {
	    		LogCtrl.Logger(LogCtrl.m_strError, "ConsistenceParameter parameter error", this);
	    		// エラーメッセージを表示して、抜ける
	    		m_TextErrorLogin.setTextColor(Color.rgb(255,20,20));
		    	m_TextErrorLogin.setVisibility(View.VISIBLE);
				m_TextErrorLogin.setText(R.string.profile_err_profileset);
				return;
	    	}*/

            // ここでWi-Fiの設定を行おう
            // ※現時点(1.X)ではWiFiのみだが、将来的には総合項目を扱う



			SetScepWifi();

            m_TextErrorLogin.setTextColor(Color.rgb(0, 120, 90));
            m_TextErrorLogin.setVisibility(View.VISIBLE);
            m_TextErrorLogin.setText(R.string.CertificateInstall);


            // "PayloadType" = "com.apple.mdm"が存在した場合、MDMチェックイン画面へ移行する
            CallMDMActivity();
        } else if (requestCode == m_nGuidePageRequestCode) {
            if (resultCode == StringList.RESULT_GUIDE_CLOSE) finish();
        } else if (requestCode == m_nApplicationList) {
            if (resultCode == StringList.RESULT_HTTP_CON_ERR) {
                m_TextErrorLogin.setTextColor(Color.rgb(255, 20, 20));
                m_TextErrorLogin.setVisibility(View.VISIBLE);
                m_TextErrorLogin.setText(R.string.LoginErrorMessage);
            }
        }

    }

    public Requester getScepRequester() {
        if (scepRequester == null) {
            setScepRequester(new Requester());
        }
        return scepRequester;
    }

    public void setScepRequester(Requester scepRequester) {
        this.scepRequester = scepRequester;
    }

    private void chooseCert() {
        Log.d("CertLoginActivity", "chooseCert()");
        KeyChain.choosePrivateKeyAlias(this, this, // Callback
                new String[]{}, // Any key types.
                null, // Any issuers.
                "localhost", // Any host
                -1, // Any port
                /*"epsap"*//*m_strCertArias*/m_EditUserID.getText().toString());    // ユーザー証明書名を指定
        // 実行後、aliasにコールバックが返る
    }

    private void SetScepWifi() {
        //	PrintViewKeyStore2();

        // 1. WifiControlインスタンス取得
        WifiControl wifi = new WifiControl(this);

        // 2. GetWifiDictList取得
        List<XmlDictionary> wifi_list = m_p_aided_profile.GetWifiDictList();

        ////////////////////////////////////////////
        // この辺でパラメータの整合性チェックが入る
        ////////////////////////////////////////////

        // 3. ループしてWifiControl::SetWifiListを実行
        if (!wifi_list.isEmpty()) {
            for (int i = 0; wifi_list.size() > i; i++) {
                XmlDictionary one_piece = wifi_list.get(i);
                wifi.SetWifiList(one_piece);
            }
        }

        // 3.2 CA_CertとUser_Certを設定する.
        wifi.SetCaCert(m_strCertArias);
        wifi.SetUserCert(m_EditUserID.getText().toString());

        // 4. WifiControl::PublicConnect(WifiControl.SCEP_WIFI)を実行
        if (wifi.PublicConnect(WifiControl.SCEP_WIFI) == false) {
            return;
        }
    }

    // MDMのチェックインの呼び出し
    private void CallMDMActivity() {
        LogCtrl.Logger(LogCtrl.m_strDebug, "CertLoginActivity " + "CallMDMActivity()", this);

        // 古い情報をチェックアウト (この段階では設定ファイルは古い情報のまま)
        m_nConnectionActionType = CONN_MDMCHKOUT;
        Thread thread = new Thread(this);    // 自分クラスをスレッドの引数に渡して...
        thread.start();                        // run()が実行される

        // GetMdmDictionary取得
        XmlDictionary mdm_dict = m_p_aided_profile.GetMdmDictionary();

        if (mdm_dict == null) return;

        Intent AppIntent = new Intent(this, MDMCheckinActivity.class);
        AppIntent.putExtra(StringList.m_str_InformCtrl, m_InformCtrl);
        startActivityForResult(AppIntent, m_nMDM_RequestCode);

        // 1. MDMインスタンス取得
/*		MDMControl mdmctrl = new MDMControl(this);

		// 2. GetMDMDictionary
		XmlDictionary mdm_dict = m_p_aided_profile.GetMdmDictionary();
		if (mdm_dict == null) {
			Log.d("CertLoginActivity", "SetMDM() No profile");
			return;
		}

		// 3. MDMFlgsにセット(MDMControlにMDMFlgs変数を持たせてそちらにやってもらう
		mdmctrl.SetMDMmember(mdm_dict);

		// 4. チェックイン(HTTP(S))
		boolean bret = mdmctrl.CheckInOut(true);

		// 5. OKならスレッド起動...定期通信
		if(bret == true) {
			mdmctrl.SrartService();
		}*/

    }

    private void OldMdmCheckOut() {
        String filedir = "/data/data/" + getPackageName() + "/files/";

        java.io.File filename_mdm = new java.io.File(filedir + StringList.m_strMdmOutputFile);
        if (filename_mdm.exists()) {
            LogCtrl.Logger(LogCtrl.m_strInfo, "MDMCheckinActivity " + "OldMdmCheckOut()", this);
            MDMFlgs mdm = new MDMFlgs();
            boolean bRet = mdm.ReadAndSetScepMdmInfo(this);
            if (mdm.GetCheckOut() == true) {
                MDMControl.CheckOut(mdm, this);
            }

            MDMControl mdmctrl = new MDMControl(this, mdm.GetUDID());    // この時点でサービスを止める
            filename_mdm.delete();
        }
    }

    // パラメータの整合性チェック
    private void ConsistenceParameter(List<XmlDictionary> dictlist, dict_num dic) {
        LogCtrl.Logger(LogCtrl.m_strInfo, "CertLoginActivity ConsistenceParameter Start.", this);
        List<XmlStringData> str_list;


        int Count = dictlist.size();
        for (int current = 0; Count > current; current++) {
            XmlDictionary one_piece = dictlist.get(current);    // カレントのプロパティ(初期値0)
            str_list = one_piece.GetArrayString();
            for (int i = 0; str_list.size() > i; i++) {
                // config情報に従って、処理を行う.
                XmlStringData p_data = str_list.get(i);
                ConsistenceParameterChild(p_data, dic);
            }
        }


    }

    private void ConsistenceParameterChild(XmlStringData data, dict_num dic) {
        LogCtrl.Logger(LogCtrl.m_strInfo, "CertLoginActivity ConsistenceParameterChild Start.", this);
        String strKeyName = data.GetKeyName();    // キー名
        int i_type = data.GetType();        // 要素タイプ(string:1, data=2, date=3, real=4, integer=5, true=6, false=7)
        String strData = data.GetData();        // 要素

//		ConfigrationProcess p_cnf = new ConfigrationProcess(m_ctx, m_DPM, m_DeviceAdmin);

        if (m_bprofile_state == false) {
            // 既にパラメータエラーが発生しているときは抜ける
            LogCtrl.Logger(LogCtrl.m_strError, "ConsistenceParameterChild  Error", this);
            return;
        }

        boolean b_type = true;
        if (i_type == 7) b_type = false;

        switch (dic) {
            case num_dict:

                break;
            case num_scepdict:
                break;

            case num_wifipict:
                // Wi-Fi
                if (strKeyName.equalsIgnoreCase(StringList.m_str_ssid)) {    // SSID
                    m_bprofile_state = ProfileActivity.consistence_ssid(strData);
                } else if (strKeyName.equalsIgnoreCase("HIDDEN_NETWORK")) {    // HIDDEN_NETWORK

                } else if (strKeyName.equalsIgnoreCase(StringList.m_str_encrypttype)) {    // 暗号方式(WEP, WPA/WPA2)
                    m_bprofile_state = ProfileActivity.consistence_EncType(strData);
                } else if (strKeyName.equalsIgnoreCase(StringList.m_str_WifiPassword)) {
                    m_bprofile_state = ProfileActivity.consistence_Wifipass(strData);
                } else if (strKeyName.equalsIgnoreCase(StringList.m_str_OuterIdentity)) {    // 外部ID
//				m_wifi.SetIdentity(strData);
                } else if (strKeyName.equalsIgnoreCase(StringList.m_str_TLSTrustedServerNames)) {    // 証明書
                    ;
                } else if (strKeyName.equalsIgnoreCase(StringList.m_str_AcceptEAPTypes)) {    // EAP type
                    m_bprofile_state = ProfileActivity.consistence_EAPTypes(strData);
//				m_wifi.SetEAPTypes(strData);
                } else if (strKeyName.equalsIgnoreCase(StringList.m_str_Phase2)) {    // EAP Phase2 Authentication
//				m_wifi.SetPhase2Auth(strData);
                }
                break;
            case num_passdict:
                // Passcode
                break;
            case num_appdict:
                // Restrictions

                break;
            case num_shortdict:
                // Shortcut
                if (strKeyName.equalsIgnoreCase(StringList.m_str_webclip_label)) {
//				Shortcut(strData);
                } else if (strKeyName.equalsIgnoreCase(StringList.m_str_URL)) {
                    m_bprofile_state = ProfileActivity.consistence_URI(strData);
                } else if (strKeyName.equalsIgnoreCase(StringList.m_str_removal)) {
//				m_c_link.SetRemoval(b_type);
                } else if (strKeyName.equalsIgnoreCase(StringList.m_str_precomposed)) {
//				m_restflgs.SetSafari(b_type);
                } else if (strKeyName.equalsIgnoreCase(StringList.m_str_icon)) {

                }
                break;
        }
    }

    @Override
    public void onClick(View arg0) {
        // TODO 自動生成されたメソッド・スタブ
        Log.i("CertLoginAcrivity::onClick", "Start.");
        // TODO 自動生成されたメソッド・スタブ

        if (arg0 == m_ButtonLogin) {
            m_TextErrorLogin.setVisibility(View.GONE);

            m_nConnectionActionType = CONN_LOGIN;

            // Activityの向きを一時的に固定する.
//			SetScreenOrientation();

            // 通信中ダイアログを表示させる。
            progressDialog = new ProgressDialog(this);
            progressDialog.setTitle(R.string.progress_title);
            progressDialog.setMessage(getText(R.string.progress_message).toString());
            progressDialog.setIndeterminate(false);
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog.show();

            // ログイン情報を保存
            WriteLoginUserInfo();

            // グレーアウト
            SetButtonRunnable(false);

            // サーバーとの通信をスレッドで行う
            Thread thread = new Thread(this);    // 自分クラスをスレッドの引数に渡して...
            thread.start();                        // run()が実行される


        } else if (arg0 == m_ButtonUdidsaw) {
            //	Toast.makeText(this, Integer.toString(m_Spi_certificate.getSelectedItemPosition()), Toast.LENGTH_SHORT).show();
            //	Toast.makeText(this, (String)m_Spi_certificate.getSelectedItem(), Toast.LENGTH_SHORT).show();

            // 可視化
            m_EditUDID.setVisibility(View.VISIBLE);
            m_EditVpnAPID.setVisibility(View.VISIBLE);
            m_TextVpnApid.setVisibility(View.VISIBLE);
            m_TextWifiApid.setVisibility(View.VISIBLE);

            // クリップボードへのコピー
            // https://sites.google.com/a/techdoctranslator.com/jp/android/guide/copy-paste
            ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            String strUDID = GetUDID();
            String strVpnAPID = GetVpnApid();
            String strclip = strUDID + " " + strVpnAPID;
            ClipData clip = ClipData.newPlainText("simple text", strclip);
            clipboard.setPrimaryClip(clip);

            // EditBoxの文字列設定
            m_EditUDID.setText(strUDID);
            m_EditVpnAPID.setText(strVpnAPID);


        } else if (arg0 == m_ButtonOpenGuide) {
            Intent AppIntent = new Intent(this, CertGuidanceActivity.class/* InstallAppListActivity.class*/);
            AppIntent.putExtra(StringList.m_str_InformCtrl, m_InformCtrl);
            startActivityForResult(AppIntent, m_nGuidePageRequestCode);
        } else if (arg0 == m_MailButton) {
            Log.i("CertLoginActivity::onClick", "Mail");
            AddressInfo.Runmailer(this);
        } else if (arg0 == m_PhoneButton) {
            Log.i("CertLoginActivity::onClick", "Phone");
            AddressInfo.RunTelephone(this);
        } else if (arg0 == m_ButtonUserAuth) {
            Log.i("CertLoginActivity::onClick", "m_ButtonUserAuth");
            m_TextErrorLogin.setVisibility(View.GONE);

            m_nConnectionActionType = CONN_USRAUTH;

            // 通信中ダイアログを表示させる。
            progressDialog = new ProgressDialog(this);
            progressDialog.setTitle(R.string.progress_title);
            progressDialog.setMessage(getText(R.string.progress_message).toString());
            progressDialog.setIndeterminate(false);
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog.show();

            if (m_Spi_certificate.getSelectedItemPosition() == 0) {
                if (m_strAPIDWifi.length() == 0) m_strAPIDWifi = XmlPullParserAided.GetUDID(this);
            } else {
                if (m_strAPIDVPN.length() == 0) m_strAPIDVPN = XmlPullParserAided.GetVpnApid(this);
            }

            // ログイン情報を保存
            WriteLoginUserInfo();

            // グレーアウト
            SetButtonRunnable(false);

            // サーバーとの通信をスレッドで行う
            Thread thread = new Thread(this);    // 自分クラスをスレッドの引数に渡して...
            thread.start();                        // run()が実行される
        } else if (arg0 == m_ButtonAppList) {
            Intent AppIntent = new Intent(this, InstallAppListActivity.class);
            AppIntent.putExtra(StringList.m_str_InformCtrl, m_InformCtrl);
            startActivityForResult(AppIntent, m_nApplicationList);
        }
    }

    private String GetUDID() {
        if (m_strAPIDWifi.length() > 0) return m_strAPIDWifi;
        else return XmlPullParserAided.GetUDID(this);

    }

    private String GetVpnApid() {
        if (m_strAPIDVPN.length() > 0) return m_strAPIDVPN;
        else return XmlPullParserAided.GetVpnApid(this);
    }

//	@Override
//    protected void onPause() {
//		super.onPause();
//		progressDialog.dismiss();
//	}

    private class CACertificateInstallTask extends AsyncTask<Requester, Integer, Boolean> {
        @Override
        protected Boolean doInBackground(Requester... params) {
            try {
                // EPS Server URL
                // 現在は取得方法不明...20130130
                m_InformCtrl4EPS.SetURL(m_strServerURL);

                Requester requester = params[0];
                // Get CA Certificate
                cACertificateStore =
                        requester.getCACertificate(m_strServerURL/*"http://10.30.127.44/ca/NaScepEPSap.cgi"*/);
                CertificateUtility.certStoreToKeyChain(
                        CertLoginActivity.this,
                        cACertificateStore,
						/*"epspCA"*/m_strCertArias);
            } catch (RequesterException e) {
                LogCtrl.Logger(LogCtrl.m_strError, "CACertificateInstallTask::doInBackground RequesterException::" + e.toString(), CertLoginActivity.this);
                e.printStackTrace();
                progressDialog.cancel();
                CancelScreenOrientation();
                return false;
            }
            return true;
        }

        @Override
        // プログレス処理
        protected void onProgressUpdate(Integer... values) {
            Log.d("CACertificateInstallTask", "onProgressUpdate - " + "values");
        }

        @Override
        // メインスレッドに反映させる処理
        protected void onPostExecute(Boolean result) {
            Log.d("CACertificateInstallTask", "onPostExecute - " + "result");

//			progressDialog.cancel();
            // Debug用 ↓
            //m_teststr.test_str = "hogehoge";
            // doInBackgroundからの戻り値

            Log.d("CACertificateInstallTask", "onPostExecute - " + "result");

            // Debug用 ↓
            //m_teststr.test_str = "hogehoge";
            // doInBackgroundからの戻り値
            if (result == false) {
                m_TextErrorLogin.setTextColor(Color.rgb(255, 20, 20));
                m_TextErrorLogin.setVisibility(View.VISIBLE);
                m_TextErrorLogin.setText(R.string.EnrollErrorMessage);

                SetButtonRunnable(true);    // ボタン活性化

				/*--- Enroll失敗時は申請取り消しを送信する  ---*/
                m_nConnectionActionType = CONN_DROP;

                // サーバーとの通信をスレッドで行う
                Thread thread = new Thread(CertLoginActivity.this);    // て...
                thread.start();                        // run()が実行される

            }
        }
    }

    private class CertificateEnrollTask extends AsyncTask<Requester, Integer, Boolean> {

//		private Activity m_act;

//		CertificateEnrollTask(Activity act) {
//			m_act = act;
//		}

        @Override
        protected Boolean doInBackground(Requester... params) {
            try {
                // EPS Server URL
                //String str_url = m_EditURL.getText().toString();

                Requester requester = params[0];
                // Generate Key Pair
                KeyPairGenerator rSAKeyPairGenerator = KeyPairGenerator.getInstance(m_strKeyType/*"RSA"*/, "BC");
                rSAKeyPairGenerator.initialize(2048, new SecureRandom());
                KeyPair rSAKeyPair = rSAKeyPairGenerator.generateKeyPair();

				// Generate self-signed certificate
				X509Certificate selfSignedCertificate;
				final Date notBefore = new Date(System.currentTimeMillis() - (5 * 60000));
				final Date notAfter = new Date(System.currentTimeMillis() + (1 * 3600000));
				final BigInteger serial = BigInteger.valueOf(System.currentTimeMillis());

                Log.d("CertificateEnrollTask", "privatekey - " + rSAKeyPair.getPrivate());

                selfSignedCertificate =
                        CertificateUtility.generateSelfSignedCertificate(
                                m_strSubject,//"CN=NetAttest EPS Root CA,OU=RDD,O=Soliton Systems K.K.,L=Shinjuku,ST=Tokyo,C=JP",
                                serial,
                                notBefore,
                                notAfter,
                                rSAKeyPair,
                                "SHA1WithRSA");

                // Make PKCS #10
                PKCS10CertificationRequest certificateSigningRequest;
                certificateSigningRequest =
                        CertificateUtility.generateCertificateSigningRequest(
                                m_strSubject,//"CN=NetAttest EPS Root CA,OU=RDD,O=Soliton Systems K.K.,L=Shinjuku,ST=Tokyo,C=JP",
                                m_strChallenge,//"Challenge",
                                rSAKeyPair,
                                "SHA1WithRSA",
                                m_strSubjectAltName);
                CertRep certRep = requester.certificateEnrollment(
                        m_strServerURL/*"https://10.30.127.44/ca/NaScepEPSap.cgi"*/,
                        certificateSigningRequest,
                        selfSignedCertificate,
                        rSAKeyPair.getPrivate(),
                        cACertificateStore);
                if (certRep.getPkiStatus().getStatus() == PkiStatus.Status.SUCCESS) {
                    CertificateUtility.keyPairToKeyChain(
                            CertLoginActivity.this,
                            rSAKeyPair);
                    
                    CertificateUtility.certificateToKeyChain(
                            CertLoginActivity.this,
                            certRep.getCertificate(),
                            m_EditUserID.getText().toString()/*"epsap"m_strCertArias*/, m_nEnrollRtnCode/*0*/);

                } else {
                    CertRequestActivity.endProgress(progressDialog);

                    CancelScreenOrientation();
                    return false;
                }
            } catch (RequesterException e) {
                LogCtrl.Logger(LogCtrl.m_strError, "CertificateEnrollTask RequesterException::" + e.toString(), CertLoginActivity.this);
                //	e.printStackTrace();
                CertRequestActivity.endProgress(progressDialog);
                CancelScreenOrientation();
                return false;
            } catch (NoSuchAlgorithmException e) {
                LogCtrl.Logger(LogCtrl.m_strError, "CertificateEnrollTask NoSuchAlgorithmException::" + e.toString(), CertLoginActivity.this);
                //	e.printStackTrace();
                CertRequestActivity.endProgress(progressDialog);
                CancelScreenOrientation();
                return false;
            } catch (NoSuchProviderException e) {
                LogCtrl.Logger(LogCtrl.m_strError, "CertificateEnrollTask NoSuchProviderException::" + e.toString(), CertLoginActivity.this);
                //	e.printStackTrace();
                CertRequestActivity.endProgress(progressDialog);
                CancelScreenOrientation();
                return false;
            } catch (Exception e) {
                LogCtrl.Logger(LogCtrl.m_strError, "CertificateEnrollTask Exception::" + e.toString(), CertLoginActivity.this);
                //	e.printStackTrace();
                CertRequestActivity.endProgress(progressDialog);
                CancelScreenOrientation();
                return false;
            }
            return true;
        }

        @Override
        // プログレス処理
        protected void onProgressUpdate(Integer... values) {
            Log.d("CertificateEnrollTask", "onProgressUpdate - " + "values");
        }

        @Override
        // メインスレッドに反映させる処理
        protected void onPostExecute(Boolean result) {
            Log.d("CertificateEnrollTask", "onPostExecute - " + "result");

            // Debug用 ↓
            //m_teststr.test_str = "hogehoge";
            // doInBackgroundからの戻り値
            if (result == false) {
                m_TextErrorLogin.setTextColor(Color.rgb(255, 20, 20));
                m_TextErrorLogin.setVisibility(View.VISIBLE);
                m_TextErrorLogin.setText(R.string.EnrollErrorMessage);

                SetButtonRunnable(true);    // ボタン活性化

				/*--- Enroll失敗時は申請取り消しを送信する  ---*/
                m_nConnectionActionType = CONN_DROP;

                // サーバーとの通信をスレッドで行う
                Thread thread = new Thread(CertLoginActivity.this);    // て...
                thread.start();                        // run()が実行される

            }

        }
    }


    @Override
    public void alias(String alias) {
        // TODO 自動生成されたメソッド・スタブ

        PrintViewKeyStore2();

        if (alias != null) {
            m_strSelectArias = alias;

            // ここでWi-Fiの設定を行おう
            SetScepWifi();

            Log.d("CertLoginActivity", "printAlias():: " + m_strSelectArias);
            try {
                PrivateKey privateKey = KeyChain.getPrivateKey(CertLoginActivity.this, m_strSelectArias);
                Log.d("CertLoginActivity", privateKey.getFormat() + ":" + privateKey);

                X509Certificate[] certs = KeyChain.getCertificateChain(CertLoginActivity.this, m_strSelectArias);
                final StringBuffer sb = new StringBuffer();
                for (X509Certificate cert : certs) {
                    sb.append(cert.getIssuerDN());
                    sb.append("\n");
                    sb.append(cert.getNotAfter());
                    sb.append("\n");
                }
                Log.d("CertLoginActivity", "X509Certificate:" + sb.toString());

                X509Certificate hogehoge = getX509Cert( sb.toString());

                // 通信前にConnectionTypeをセットする
                m_nConnectionActionType = CONN_SCEP;

                // 通信中ダイアログを表示させる。 ← ここでよびだすと例外が発生する(threadの関係上).
                //	        progressDialog = new ProgressDialog(this);
                //	        progressDialog.setTitle("通信中");
                //	        progressDialog.setMessage("データ取得中・・・");
                //	        progressDialog.setIndeterminate(false);
                //	        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                //	        progressDialog.show();

                // SCEP関連のプロパティのやり取りをEPSと行う
                // サーバーとの通信をスレッドで行う
                Thread thread = new Thread(this);	// 自分クラスをスレッドの引数に渡して...
                thread.start();						// run()が実行される
            } catch (KeyChainException e) {
                // TODO 自動生成された catch ブロック
                e.printStackTrace();
            } catch (InterruptedException e) {
                // TODO 自動生成された catch ブロック
                e.printStackTrace();
            }
            //           setAlias(alias); // Set the alias in the application preference
            //           disableKeyChainButton();
            //           printInfo();

        } else {
            //           Log.d(TAG, "User hit Disallow");
        }

    }

    // ファイル出力
    public void WriteLoginUserInfo() {
        String retmsg = "";

        XmlSerializer serializer = Xml.newSerializer();
        StringWriter writer = new StringWriter();
        try {
            serializer.setOutput(writer);    // XmlSerializerとStringWriterの関連付け..
            serializer.startDocument("UTF-8", true);
            serializer.startTag("", "plist");
            serializer.attribute("", "version", "1.0");
            serializer.startTag("", "dict");

            // EPS-ap UserID
            XmlPullParserAided.SetParameter4Output(serializer, StringList.m_str_User_id, m_EditUserID.getText().toString());
            // EPS-ap Password
            XmlPullParserAided.SetParameter4Output(serializer, StringList.m_str_LoginUser_Pass, m_EditPassword.getText().toString());
            // APID Wi-Fi # 21391
            XmlPullParserAided.SetParameter4Output(serializer, StringList.m_str_Apid_Wifi, m_strAPIDWifi);
            // APID VPN # 21391
            XmlPullParserAided.SetParameter4Output(serializer, StringList.m_str_Apid_VPN, m_strAPIDVPN);

            serializer.endTag("", "dict");
            serializer.endTag("", "plist");
            serializer.endDocument();

            // アウトプットをストリング型へ変換する
            retmsg = writer.toString();

        } catch (IOException e) {
            Log.e("CertLoginActivity::IOException ", e.toString());
        }

        Log.i("CertLoginActivity::WriteLoginUserInfo", retmsg);
        byte[] byArrData = retmsg.getBytes();
        OutputStream outputStreamObj = null;

        try {
            //Context ctx = new Context();
            //Contextから出力ストリーム取得
            outputStreamObj = openFileOutput(StringList.m_strLoginUserOutputFile, Context.MODE_PRIVATE);
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

    // ファイル読み込み&フラグセット
    public boolean ReadAndSetLoginUserInfo() {
        byte[] byArrData_read = null;
        int iSize;
        byte[] byArrTempData = new byte[4096];
        InputStream inputStreamObj = null;
        ByteArrayOutputStream byteArrayOutputStreamObj = null;

        boolean bRet = true;

        try {
            //Contextから入力ストリームの取得
            inputStreamObj = openFileInput(StringList.m_strLoginUserOutputFile);
            //
            byteArrayOutputStreamObj = new ByteArrayOutputStream();
            //ファイルからbyte配列に読み込み、さらにそれをByteArrayOutputStreamに追加していく
            while (true) {
                iSize = inputStreamObj.read(byArrTempData);
                if (iSize <= 0) break;
                byteArrayOutputStreamObj.write(byArrTempData, 0, iSize);
            }
            //ByteArrayOutputStreamからbyte配列に変換
            byArrData_read = byteArrayOutputStreamObj.toByteArray();
        } catch (Exception e) {
            LogCtrl.Logger(LogCtrl.m_strDebug, "ReadAndSetLoginUserInfo: " + e.getMessage(), this);
            bRet = false;
        } finally {
            try {
                if (inputStreamObj != null) inputStreamObj.close();
                if (byteArrayOutputStreamObj != null) byteArrayOutputStreamObj.close();
            } catch (Exception e2) {
                LogCtrl.Logger(LogCtrl.m_strDebug, "ReadAndSetLoginUserInfo e2: " + e2.getMessage(), this);
                bRet = false;
            }

        }

        if (bRet == false) return bRet;

        String read_string = new String(byArrData_read);
        //LogCtrl.Logger(LogCtrl.m_strDebug, "*****Re-Read***** " + read_string, this);
        //android.util.Log.d(StringList.m_str_SKMTag, "*****Re-Read***** " + read_string);

        // 新しくXmlPullParserAidedを作成する.
        XmlPullParserAided p_aided = new XmlPullParserAided(this, read_string, 2);
        p_aided.TakeApartControll();        // ここで分解する
        XmlDictionary p_dict = p_aided.GetDictionary();        // XmlPullParserAidedクラスで分類され、XmlDictionaryに振るいわけされた要素を取得

        // <key, type, data>リストを取得
        List<XmlStringData> str_list = p_dict.GetArrayString();
        for (int i = 0; str_list.size() > i; i++) {
            // config情報に従って、処理を行う.
            XmlStringData p_data = str_list.get(i);
            SetParametorFromFile(p_data);
        }

        return bRet;
    }


    private void SetParametorFromFile(XmlStringData p_data) {

        String strKeyName = p_data.GetKeyName();    // キー名
        int i_type = p_data.GetType();        // 要素タイプ(string:1, data=2, date=3, real=4, integer=5, true=6, false=7)
        String strData = p_data.GetData();        // 要素
        //
        if (strKeyName.equalsIgnoreCase(StringList.m_str_User_id)) {
            m_EditUserID.setText(strData);
            LogCtrl.Logger(LogCtrl.m_strDebug, "LoginUserOutputFile user=" + strData, this);
        } else if (strKeyName.equalsIgnoreCase(StringList.m_str_LoginUser_Pass)) {
            m_EditPassword.setText(strData);
        } else if (strKeyName.equalsIgnoreCase(StringList.m_str_Apid_Wifi)) { // # 21391
            m_strAPIDWifi = strData;
            LogCtrl.Logger(LogCtrl.m_strDebug, "LoginUserOutputFile Wifi APID=" + strData, this);
        } else if (strKeyName.equalsIgnoreCase(StringList.m_str_Apid_VPN)) { // # 21391
            m_strAPIDVPN = strData;
            LogCtrl.Logger(LogCtrl.m_strDebug, "LoginUserOutputFile VPN APID=" + strData, this);
        }

    }

    //画面固定
    private void SetScreenOrientation() {
        Configuration config = getResources().getConfiguration();
//	    if (config.orientation == Configuration.ORIENTATION_LANDSCAPE) {
//	        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
//	    } else if (config.orientation == Configuration.ORIENTATION_PORTRAIT) {
//	        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
//	    }
    }

    // 画面固定解除
    private void CancelScreenOrientation() {
        //setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
    }

    // ボタン活性化
    public void SetButtonRunnable(boolean enable) {
        m_ButtonUserAuth.setEnabled(enable);    // ユーザー認証
        m_ButtonLogin.setEnabled(enable);    // 開始ボタン
        m_ButtonOpenGuide.setEnabled(enable);    // ガイド
        m_ButtonUdidsaw.setEnabled(enable);    // APID
        m_ButtonAppList.setEnabled(enable);
    }

    // Debug用
    private void PrintViewKeyStore() {
        Log.d("CertLoginActivity::PrintViewKeyStore", "Start");
        KeyStore ks;
        try {
            ks = KeyStore.getInstance("AndroidCAStore");
            ks.load(null, null);

            Enumeration e;
            e = ks.aliases();

            while (e.hasMoreElements()) {
                X509Certificate c = (X509Certificate) e.nextElement();
                // ここでリストに挿入など証明書をごにょごにょする。

                StringBuffer sb = new StringBuffer();
                sb.append(c.getIssuerDN());
                sb.append("\n");
                Log.d("CertLoginActivity::PrintViewKeyStore", "X509Certificate:" + sb.toString());
            }

        } catch (KeyStoreException e1) {
            // TODO 自動生成された catch ブロック
            e1.printStackTrace();
        } catch (NoSuchAlgorithmException e1) {
            // TODO 自動生成された catch ブロック
            e1.printStackTrace();
        } catch (CertificateException e1) {
            // TODO 自動生成された catch ブロック
            e1.printStackTrace();
        } catch (IOException e1) {
            // TODO 自動生成された catch ブロック
            e1.printStackTrace();
        }
    }

    private void PrintViewKeyStore2() {
        Log.d("CertLoginActivity::PrintViewKeyStore2", "Start");

        try {
            KeyStore ks = KeyStore.getInstance("AndroidCAStore");
            ks.load(null, null);
            Enumeration aliases = ks.aliases();
            while (aliases.hasMoreElements()) {
                String alias = (String) aliases.nextElement();
                Log.d("CertLoginActivity::PrintViewKeyStore2", "aliase: " + alias);
                if (alias.indexOf("user") == -1) {
                    continue;
                }

                X509Certificate cert = (X509Certificate)
                        ks.getCertificate(alias);
                Log.d("CertLoginActivity::PrintViewKeyStore2", "Subject DN: " +
                        cert.getSubjectDN().getName());
                Log.d("CertLoginActivity::PrintViewKeyStore2", "Issuer DN: " +
                        cert.getIssuerDN().getName());


                Date _date = new Date();
                cert.checkValidity(_date);
            }


        } catch (KeyStoreException e1) {
            // TODO 自動生成された catch ブロック
            e1.printStackTrace();
        } catch (NoSuchAlgorithmException e1) {
            // TODO 自動生成された catch ブロック
            e1.printStackTrace();
        } catch (CertificateException e1) {
            // TODO 自動生成された catch ブロック
            e1.printStackTrace();
        } catch (IOException e1) {
            // TODO 自動生成された catch ブロック
            e1.printStackTrace();
        }
    }

    // ユーザー証明書のaliasから情報を取得
    // ☆ main threadから実行するとKeyChain.getPrivateKeyで落ちるのでsub threadから実行する
    private void printAlias() {
        m_strSelectArias = m_EditUserID.getText().toString();
        Log.d("CertLoginActivity", "printAlias():: " + m_strSelectArias);
        try {
            PrivateKey privateKey = KeyChain.getPrivateKey(CertLoginActivity.this, m_strSelectArias);
            Log.d("CertLoginActivity::printAlias", privateKey.getFormat() + ":" + privateKey);

            X509Certificate[] certs = KeyChain.getCertificateChain(CertLoginActivity.this, m_strSelectArias);
            final StringBuffer sb = new StringBuffer();
            for (X509Certificate cert : certs) {
                sb.append(cert.getIssuerDN());
                sb.append("\n");
            }
            Log.d("CertLoginActivity::printAlias", "X509Certificate:" + sb.toString());

            // 通信前にConnectionTypeをセットする
//			m_nConnectionActionType = CONN_SCEP;


            // SCEP関連のプロパティのやり取りをEPSと行う
            // サーバーとの通信をスレッドで行う
//			Thread thread = new Thread(this);	// 自分クラスをスレッドの引数に渡して...
//			thread.start();						// run()が実行される
        } catch (KeyChainException e) {
            // TODO 自動生成された catch ブロック
            e.printStackTrace();
        } catch (InterruptedException e) {
            // TODO 自動生成された catch ブロック
            e.printStackTrace();
        }
    }


    // https://code.google.com/p/android/issues/detail?id=59200
    public X509Certificate getX509Cert(String inpem) {
        InputStream is = new ByteArrayInputStream(inpem.getBytes());

        BufferedInputStream bis = new BufferedInputStream(is);

        CertificateFactory cf = null;
        try {
            cf = CertificateFactory.getInstance("X.509");
        } catch (java.security.cert.CertificateException e) {
            e.printStackTrace();
        }

        X509Certificate cert = null;

        try {
            while (bis.available() > 0) {
                cert = (X509Certificate) cf.generateCertificate(/*bis*/is);
            }
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } catch (java.security.cert.CertificateException e) {
            e.printStackTrace();
        }

        try {
            bis.close();
            is.close();
        } catch (IOException e) {
            // If this fails, it isn't the end of the world.
            e.printStackTrace();
        }

        return cert;
    }

    /////////////////////////////////////////////////////////////////////////
    // ユーザー認証/証明書取得時のPOSTに設定されるシリアル(Serial)を取得する
    // 20150312 XmlPullParserAided::GetUDID()を使用してAPIDをシリアルに変更もあり
    /////////////////////////////////////////////////////////////////////////
/*	 private String GetSerialCode() {
		 String rtnSerial = "";
		 WifiManager wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
		 WifiInfo wifiInfo = wifiManager.getConnectionInfo();
		 TelephonyManager telManager = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);

		 rtnSerial = telManager.getDeviceId();
		 if(rtnSerial == null) {
			 rtnSerial = "";
			 String strmac = wifiInfo.getMacAddress();
			 for(int n = 0; strmac.length() > (3 * n); n++) {
				 rtnSerial += strmac.substring(3 * n, 3 * n + 2);
			 }
		 }

		 return rtnSerial;
	 }*/
}
