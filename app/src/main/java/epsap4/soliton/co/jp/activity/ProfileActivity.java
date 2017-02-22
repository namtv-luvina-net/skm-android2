package epsap4.soliton.co.jp.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.util.List;

import epsap4.soliton.co.jp.AddressInfo;
import epsap4.soliton.co.jp.ConfigrationDivision;
import epsap4.soliton.co.jp.ConfigrationProcess;
import epsap4.soliton.co.jp.EpsapAdminReceiver;
import epsap4.soliton.co.jp.HttpConnectionCtrl;
import epsap4.soliton.co.jp.InformCtrl;
import epsap4.soliton.co.jp.ListItem;
import epsap4.soliton.co.jp.LogCtrl;
import epsap4.soliton.co.jp.R;
import epsap4.soliton.co.jp.RestrictionsControl;
import epsap4.soliton.co.jp.StringList;
import epsap4.soliton.co.jp.shortcut.CreateShortcutLink;
import epsap4.soliton.co.jp.wifi.WifiControl;
import epsap4.soliton.co.jp.xmlparser.XmlDictionary;
import epsap4.soliton.co.jp.xmlparser.XmlPullParserAided;
import epsap4.soliton.co.jp.xmlparser.XmlStringData;

////////////////////////////////
//Profileアクティビティ
////////////////////////////////
public class ProfileActivity extends Activity
	implements View.OnClickListener, Runnable {

	public static final int RESULT_ENABLE = 1;

	// 適用ボタン
	private Button m_ButtonSet;
	private TextView m_Seterr;
	private TextView m_Csterr;

	// パスコードコントロール適用ボタン
	private Button m_ButtonEndPscode;
	private Button m_ButtonStartPscode;
	private TextView m_textDevice01;
	private TextView m_textDevice02;
	// UI passcode
	private EditText m_editPINAge;
	private EditText m_editPINHistory;
	private EditText m_editPassQuality;
	private EditText m_editMinPassLength;
	private EditText m_editFailedAttmt;

	private TextView m_PINAgeerr;
	private TextView m_Historyerr;
	private TextView m_FailerAtmterr;
	// UI Wi-Fi
	private EditText m_editssid;
	private EditText m_editEnc;
	private EditText m_editEapstyle;
	private EditText m_editPhase2;
	private EditText m_editEapId;
	private EditText m_editEapPass;
	private Button m_ButtonWifiNext;
	private Button m_ButtonWifiBack;

	private TextView m_textwifi;
	private TextView m_textssid;
	private TextView m_textenc;
	private TextView m_textpass;
	private TextView m_ssiderr;
	private TextView m_Encerr;
	private TextView m_Eapstyleerr;
	private TextView m_Phase2err;
	private TextView m_EapPasserr;

	private View m_SepWifi;
	private FrameLayout m_fmwifi01;
	private FrameLayout m_fmwifi02;
	private FrameLayout m_fmwifi06;

	// UI Shortcut
	private EditText m_editShortcutName;
	private EditText m_editUri;
	private Button m_ButtonShortNext;
	private Button m_ButtonShortBack;

	private TextView m_textShortcut;
	private TextView m_textUri;
	private TextView m_Urierr;
	private TextView m_strLabel;
	private View m_SepShort;
	private FrameLayout m_fmshort01;
	private FrameLayout m_fmshort02;

	// UI AppAccess
	private TextView m_textApp;
	private EditText m_editAppAccess;
	private String m_strAppAccess = "";
	private View m_SepApp;

	// 連絡先
	private Button m_MailButton;
	private Button m_PhoneButton;

	private static InformCtrl m_InformCtrl;
	private XmlPullParserAided m_p_aided = null;
	XmlDictionary m_pdictionary;
	XmlDictionary p_Scepdict;
	XmlDictionary p_passdict;
	XmlDictionary p_Appdict;
	List<XmlDictionary> p_Shortcutdictlist;
	List<XmlDictionary> p_Wifidictlist;
	enum dict_num {
		num_dict,
		num_scepdict ,
		num_wifipict,
		num_passdict,
		num_appdict,
		num_shortdict,
	};
	List<XmlStringData> m_str_list;			// class XmlDictionaryのメンバ変数List<XmlStringData>を何らかの形で取得しなければならない.
											// GetArrayString()で取得可能。XmlDictionaryのインスタンスの取得が必要

	DevicePolicyManager m_DPM;
    ComponentName m_DeviceAdmin;
    ConfigrationDivision m_p_conf;
    int m_nErroType;

    boolean m_bforcePIN = false;	// 強制パスコード
    private boolean m_bSetButtonEnable = false;
    private boolean m_bApplicationControl = true;
    private ProgressDialog progressDialog;
    int m_nShortcutCount = 0;		// ショートカットプロパティの総数
    int m_nShortcutCurrentNum = 0;	// 現在表示中のショートカットプロパティの番号
    int m_nWifiCount = 0;			// WiFiプロパティの総数
    int m_nWifiCurrentNum = 0;		// 現在表示中のWiFiプロパティの番号
    boolean m_bprofile_state = true;	// プロファイル情報の正常性

	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {

    	this.setTitle(R.string.ApplicationTitle);

    	m_DPM = (DevicePolicyManager)getSystemService(Context.DEVICE_POLICY_SERVICE);
        m_DeviceAdmin = new ComponentName(ProfileActivity.this, EpsapAdminReceiver.class);

    	super.onCreate(savedInstanceState);

    	setContentView(R.layout.profile);

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

    //	http_connection();

    }

    @Override
    protected void onPause() {
		super.onPause();
		CertRequestActivity.endProgress(progressDialog);
	}

    @Override
	public void run() {
		// TODO 自動生成されたメソッド・スタブ
    	http_connection();
    	handler.sendEmptyMessage(0);
	}

    private Handler handler = new Handler() {
		 public void handleMessage(Message msg) {
			 // 処理終了時の動作をここに記述。
			 if(m_nErroType == CertRequestActivity.ERR_NETWORK) SetEditNullMember();
			 else SetEditMember();
			 // プログレスダイアログ終了
			 CertRequestActivity.endProgress(progressDialog);
		 }
	 };

    //////////////////////////////////////////////////
    // サーバー通信を行い,プロファイル情報を取得する
    private void http_connection() {
    	// ProfileListActivityからPutされたListItemとInformCtrlを取得する
    	Intent intent = getIntent();
    	ListItem origin_item = (ListItem)intent.getSerializableExtra(StringList.m_str_ListItem);
    	m_InformCtrl = (InformCtrl)intent.getSerializableExtra(StringList.m_str_InformCtrl);

    	// 先ず、プロファイル名を取得する
    	String str_profilename = origin_item.getText();
    	Log.v("ProfileActivity::http_connection", String.format("Selected: %s", str_profilename));
    	// 続いて、プロファイルIDを取得
    	String str_id = origin_item.getIDText();
		Log.v("AppListActivity::onListItemClick", String.format("ID: %s", str_id));

		// メッセージ
		String message = "ProfileID=" + str_id;
		m_InformCtrl.SetMessage(message);
    	// ProfileActivity内でHTTP通信を行うか、ここで通信を行いHTML解析まで行ってからXmlDictionaryを引数に渡すか...
    	HttpConnectionCtrl conn = new HttpConnectionCtrl(this);
    	boolean ret = conn.RunHttpGetProfileMember(m_InformCtrl, str_id);

    	if (ret == false) {
			LogCtrl.Logger(LogCtrl.m_strError, "ProfileActivity::http_connection Get Profile Error1.", this);
			m_nErroType = CertRequestActivity.ERR_NETWORK;
			return ;
		}

    	// 取得XMLのパーサー
    	m_p_aided = new XmlPullParserAided(this, m_InformCtrl.GetRtn()/*data*/, 2);	// 最上位dictの階層は2になる
    	ret = m_p_aided.TakeApartProfile();
    	if (ret == false) {
    		LogCtrl.Logger(LogCtrl.m_strError, "ProfileActivity::http_connection Get Profile Error2.", this);
    		m_nErroType = CertRequestActivity.ERR_NETWORK;
    		return;
    	}

    	// XmlDictionary
    	m_pdictionary = m_p_aided.GetDictionary();		// 4.0より各機能ごとのDictionaryを取得する
    	p_Scepdict = m_p_aided.GetScepDictionary();	// Scep
    	p_passdict = m_p_aided.GetPassDictionary();	// passcode
    	p_Appdict = m_p_aided.GetAppDictionary();	// aided
    	// webclipとwifiはList<XmlDictionary>として取得
    	p_Shortcutdictlist = m_p_aided.GetWebClipDictList();	// webclip
    	p_Wifidictlist = m_p_aided.GetWifiDictList();			// wifi

    	m_nErroType = CertRequestActivity.SUCCESSFUL;

 //   	SetEditMember();

    	// プロファイル名をキーワードにprofileの詳細を取得して、XmlPullParserAidedで解析後、XmlDictionaryを元にConfigrationDivision
    	// で行っているようなことをする。
    	// Debug用 <===
 //   	maxPINAgeInDays("6");
 //   	pinHistory("数字以外の文字列");
 //   	ssid("ssid全角文字含みttt");
 //   	EncType("WPA");
 //   	EAPTypes("13");
 //   	Phase2Auth("MSCHAPV2");
 //   	requireAlphanumeric(true);
 //   	URI("https://www.yahoo.co.jp");
    	// Debug用 ===>


 /*   	if(m_str_list == null) {
    		return;
    	}

    	for(int i = 0; m_str_list.size() > i; i++){
			// config情報に従って、処理を行う.
			XmlStringData p_data = m_str_list.get(i);
			SetEditMember(p_data);
		}*/

    }

    private void setUItoMember() {
    	m_ButtonSet = (Button)findViewById(R.id.ButtonProfileSet);
    	m_ButtonSet.setOnClickListener(this);
    	m_Seterr = (TextView)findViewById(R.id.profile_set_err);
    	m_Seterr.setTextColor(Color.rgb(255,20,20));
    	m_Csterr = (TextView)findViewById(R.id.profile_const_err);
    	m_Csterr.setTextColor(Color.rgb(255,20,20));
    	m_textDevice01 = (TextView)findViewById(R.id.textView_deviceadmin01);
    	m_textDevice02 = (TextView)findViewById(R.id.textView_deviceadmin02);

    	m_ButtonEndPscode = (Button)findViewById(R.id.ButtonEndpasscode);
    	m_ButtonStartPscode = (Button)findViewById(R.id.ButtonStartpasscode);
    	m_ButtonEndPscode.setOnClickListener(this);
    	m_ButtonStartPscode.setOnClickListener(this);

    	//===== Passcode
 //   	m_editPINAge = (EditText)findViewById(R.id.EditPinAgeDays);
 //   	m_editPINHistory = (EditText)findViewById(R.id.EditPinHistory);
 //   	m_editPassQuality = (EditText)findViewById(R.id.EditallowSimple);
 //   	m_editMinPassLength = (EditText)findViewById(R.id.EditMinPassLength);
 //   	m_editFailedAttmt = (EditText)findViewById(R.id.EditFailedAttempt);

 //   	m_PINAgeerr = (TextView)findViewById(R.id.PinAgeDays_err);
 //   	m_PINAgeerr.setTextColor(Color.rgb(255,20,20));
 //   	m_Historyerr = (TextView)findViewById(R.id.PinHistory_err);
 //   	m_Historyerr.setTextColor(Color.rgb(255,20,20));
 //   	m_FailerAtmterr = (TextView)findViewById(R.id.FailedAttempt_err);
 //   	m_FailerAtmterr.setTextColor(Color.rgb(255,20,20));

    	//===== SSID
    	m_editssid = (EditText)findViewById(R.id.EditSSID);
    	m_editEnc = (EditText)findViewById(R.id.EditEncType);
//    	m_editEapstyle = (EditText)findViewById(R.id.EditEapStyle);
//    	m_editPhase2 = (EditText)findViewById(R.id.EditPhase2);
//    	m_editEapId = (EditText)findViewById(R.id.EditEapId);
    	m_editEapPass = (EditText)findViewById(R.id.EditEappass);

    	m_textwifi = (TextView)findViewById(R.id.textViewWifi);
    	m_textssid = (TextView)findViewById(R.id.SSID);
    	m_textenc = (TextView)findViewById(R.id.SecPriority);
    	m_textpass = (TextView)findViewById(R.id.Eappass);
    	m_ssiderr = (TextView)findViewById(R.id.SSID_err);
    	m_ssiderr.setTextColor(Color.rgb(255,20,20));
    	m_Encerr = (TextView)findViewById(R.id.SecPriorityerr);
    	m_Encerr.setTextColor(Color.rgb(255,20,20));
//    	m_Eapstyleerr = (TextView)findViewById(R.id.EapStyleerr);
//    	m_Eapstyleerr.setTextColor(Color.rgb(255,20,20));
//    	m_Phase2err = (TextView)findViewById(R.id.Phase2err);
//    	m_Phase2err.setTextColor(Color.rgb(255,20,20));
    	m_EapPasserr = (TextView)findViewById(R.id.Eappasserr);
    	m_EapPasserr.setTextColor(Color.rgb(255,20,20));
    	m_SepWifi = (View)findViewById(R.id.SeparatorWifi);
    	m_fmwifi01 = (FrameLayout)findViewById(R.id.FmWifi01);
    	m_fmwifi02 = (FrameLayout)findViewById(R.id.FmWifi02);
    	m_fmwifi06 = (FrameLayout)findViewById(R.id.FmWifi06);
    	m_ButtonWifiNext = (Button)findViewById(R.id.Button_WifiNext);
    	m_ButtonWifiNext.setOnClickListener(this);
    	m_ButtonWifiBack = (Button)findViewById(R.id.Button_WifiBack);
    	m_ButtonWifiBack.setOnClickListener(this);

    	//====== Shortcut
    	m_editShortcutName = (EditText)findViewById(R.id.EditShortcutName);
    	m_editUri = (EditText)findViewById(R.id.EditUri);
    	m_Urierr = (TextView)findViewById(R.id.Urierr);
    	m_Urierr.setTextColor(Color.rgb(255, 20, 20));
    	m_textShortcut = (TextView)findViewById(R.id.textViewShowtcut);
    	m_strLabel = (TextView)findViewById(R.id.ShortcutName);
    	m_textUri = (TextView)findViewById(R.id.Uri);
    	m_SepShort = (View)findViewById(R.id.SeparatorShortcut);
    	m_fmshort01 = (FrameLayout)findViewById(R.id.FmShort01);
    	m_fmshort02 = (FrameLayout)findViewById(R.id.FmShort02);
    	m_ButtonShortNext = (Button)findViewById(R.id.Button_ShortNext);
    	m_ButtonShortNext.setOnClickListener(this);
    	m_ButtonShortBack = (Button)findViewById(R.id.Button_ShortBack);
    	m_ButtonShortBack.setOnClickListener(this);

    	//======= Application Access
    	m_editAppAccess = (EditText)findViewById(R.id.EditAppAccess);
    	m_textApp = (TextView)findViewById(R.id.textViewAppli);
    	m_SepApp = (View)findViewById(R.id.SeparatorApp);


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

    private void SetEditMember() {
        //☆ 各XmlDirectory にnull判定が必要 ☆//
    	List<XmlStringData> str_list;

    	// <key, type, data>リストを取得
 /*   	if(m_pdictionary != null) {
    		str_list = m_pdictionary.GetArrayString();
    		for(int i = 0; str_list.size() > i; i++){
    			// config情報に従って、処理を行う.
    			XmlStringData p_data = str_list.get(i);
    			SetEditMemberChild(p_data, dict_num.num_dict);
    		}
    	}
 */
    	// Scep
 /*   	if(p_Scepdict != null) {
    		str_list = p_Scepdict.GetArrayString();
    		for(int i = 0; str_list.size() > i; i++){
    			// config情報に従って、処理を行う.
    			XmlStringData p_data = str_list.get(i);
    			SetEditMemberChild(p_data, dict_num.num_scepdict);
    		}
    	}
*/
    	// Wi-Fi
    	if(!p_Wifidictlist.isEmpty()) {
    		m_bSetButtonEnable = true;
    		m_nWifiCount = p_Wifidictlist.size();
    		XmlDictionary one_piece = p_Wifidictlist.get(m_nWifiCurrentNum);	// カレントのプロパティ(初期値0)
    		str_list = one_piece.GetArrayString();
    		for(int i = 0; str_list.size() > i; i++){
    			// config情報に従って、処理を行う.
    			XmlStringData p_data = str_list.get(i);
    			SetEditMemberChild(p_data, dict_num.num_wifipict);
    		}
    		//////////////////////////////////////////////////////////
    		// ここで全リストメンバのパラメータ整合性チェックを入れる
    		///////////////////////////////////////////////////////////
    		ConsistenceParameter(p_Wifidictlist, dict_num.num_wifipict);
    	} else {
    		HideViewMember(dict_num.num_wifipict);
    	}

    	// Passcode
/*    	if(p_passdict != null) {
    		str_list = p_passdict.GetArrayString();
    		for(int i = 0; str_list.size() > i; i++){
    			// config情報に従って、処理を行う.
    			XmlStringData p_data = str_list.get(i);
    			SetEditMemberChild(p_data, dict_num.num_passdict);
    		}
    	}
*/
    	// Application
    	if(p_Appdict != null) {
    		m_bSetButtonEnable = true;
    		str_list = p_Appdict.GetArrayString();
    		for(int i = 0; str_list.size() > i; i++){
    			// config情報に従って、処理を行う.
    			XmlStringData p_data = str_list.get(i);
    			SetEditMemberChild(p_data, dict_num.num_appdict);
    		}
    	} else {
    		HideViewMember(dict_num.num_appdict);
    	}

    	// Shortcut
    	if(!p_Shortcutdictlist.isEmpty()) {
    		m_bSetButtonEnable = true;
    		m_nShortcutCount = p_Shortcutdictlist.size();	// ショートカットプロパティの総数を取得
    		XmlDictionary one_piece = p_Shortcutdictlist.get(m_nShortcutCurrentNum);	// カレントのプロパティ(初期値0)
    		str_list = one_piece.GetArrayString();
    		for(int i = 0; str_list.size() > i; i++){
    			// config情報に従って、処理を行う.
    			XmlStringData p_data = str_list.get(i);
    			SetEditMemberChild(p_data, dict_num.num_shortdict);
    		}
			//////////////////////////////////////////////////////////
			// ここで全リストメンバのパラメータ整合性チェックを入れる
			///////////////////////////////////////////////////////////
			ConsistenceParameter(p_Shortcutdictlist, dict_num.num_shortdict);
    	} else {
    		HideViewMember(dict_num.num_shortdict);
    	}

    	// プロファイルに設定可能な情報がない場合、警告メッセージを表示する
    	if(m_bSetButtonEnable == false) {
    		// "※ 有効なプロファイルの情報が存在しません。"
    		m_Seterr.setText(R.string.profile_err_noprofile);
    	}

    	updateUi();
    }

    private void SetEditNullMember() {
    	HideViewMember(dict_num.num_wifipict);
    	HideViewMember(dict_num.num_appdict);
    	HideViewMember(dict_num.num_shortdict);
    	// プロファイルに設定可能な情報がない場合、警告メッセージを表示する
    	// "※ プロファイル情報が取得できませんでした。"
   		m_Seterr.setText(R.string.profile_err_getprofile);

    	updateUi();
    }

    private void SetEditMemberChild(XmlStringData data, dict_num dic) {
    	Log.i("ProfileActivity", "SetEditMember Start.");
    	String strKeyName = data.GetKeyName();	// キー名
		int    i_type = data.GetType();		// 要素タイプ(string:1, data=2, date=3, real=4, integer=5, true=6, false=7)
		String strData = data.GetData();		// 要素

//		ConfigrationProcess p_cnf = new ConfigrationProcess(m_ctx, m_DPM, m_DeviceAdmin);

		boolean b_type = true;
		if(i_type == 7) b_type = false;

		switch(dic) {
		case num_dict:

			break;
		case num_scepdict:
			break;

		case num_wifipict:
			// Wi-Fi
			if(strKeyName.equalsIgnoreCase(StringList.m_str_ssid)) {	// SSID
				ssid(strData);
			} else if(strKeyName.equalsIgnoreCase("HIDDEN_NETWORK")) {	// HIDDEN_NETWORK
//				m_wifi.SetHidden(b_type);
			} else if(strKeyName.equalsIgnoreCase(StringList.m_str_encrypttype)) {	// 暗号方式(WEP, WPA/WPA2)
				EncType(strData);
			} else if(strKeyName.equalsIgnoreCase(StringList.m_str_WifiPassword)) {
				Wifipass(strData);
//				m_wifi.SetWifipass(strData);
			} else if(strKeyName.equalsIgnoreCase(StringList.m_str_OuterIdentity)) {	// 外部ID
//				m_wifi.SetIdentity(strData);
			} else if(strKeyName.equalsIgnoreCase(StringList.m_str_TLSTrustedServerNames)) {	// 証明書
				;
			} else if(strKeyName.equalsIgnoreCase(StringList.m_str_AcceptEAPTypes)) {	// EAP type
				EAPTypes(strData);
//				m_wifi.SetEAPTypes(strData);
			} else if(strKeyName.equalsIgnoreCase(StringList.m_str_Phase2)) {	// EAP Phase2 Authentication
//				m_wifi.SetPhase2Auth(strData);
			}
			break;
		case num_passdict:
			// Passcode
			if(strKeyName.equalsIgnoreCase(StringList.m_str_allowSimple)) {
				allowSimple(b_type);
			} else if(strKeyName.equalsIgnoreCase(StringList.m_str_requireAlphanumeric)) {
				requireAlphanumeric(b_type);
			} else if(strKeyName.equalsIgnoreCase(StringList.m_str_minLength)) {
				minLength(strData);
			} else if(strKeyName.equalsIgnoreCase(StringList.m_str_minComplexChars)) {
//				p_cnf.minComplexChars(strData);
			} else if(strKeyName.equalsIgnoreCase(StringList.m_str_maxPINAgeInDays)) {
				maxPINAgeInDays(strData);
			} else if(strKeyName.equalsIgnoreCase("maxInactivity")) {
//				p_cnf.maxInactivity(strData);
			} else if(strKeyName.equalsIgnoreCase(StringList.m_str_pinHistory)) {
				pinHistory(strData);
			} else if(strKeyName.equalsIgnoreCase("maxGracePeriod")) {
//				p_cnf.maxGracePeriod(strData);
			} else if(strKeyName.equalsIgnoreCase(StringList.m_str_maxFailedAttempts)) {
				maxFailedAttempts(strData);
			} else if(strKeyName.equalsIgnoreCase(StringList.m_str_forcePIN)) {
				if (i_type == 6) {
					// true
					m_bforcePIN = true;
				}
			}
		case num_appdict:
			// Restrictions
			if(strKeyName.equalsIgnoreCase(StringList.m_strCamera)) {
				SetCamera(b_type);
			} else if(strKeyName.equalsIgnoreCase(StringList.m_strYoutube)) {
//				SetYouTube(b_type);
			} else if(strKeyName.equalsIgnoreCase(StringList.m_striTunes)) {
//				SetiTunes(b_type);
			} else if(strKeyName.equalsIgnoreCase(StringList.m_strSafari)) {
//				m_restflgs.SetSafari(b_type);
			}
			break;
		case num_shortdict:
			// Shortcut
			if(strKeyName.equalsIgnoreCase(StringList.m_str_webclip_label)) {
				Shortcut(strData);
			} else if(strKeyName.equalsIgnoreCase(StringList.m_str_URL)) {
				URI(strData);
			} else if(strKeyName.equalsIgnoreCase(StringList.m_str_removal)) {
//				m_c_link.SetRemoval(b_type);
			} else if(strKeyName.equalsIgnoreCase(StringList.m_str_precomposed)) {
//				m_restflgs.SetSafari(b_type);
			} else if(strKeyName.equalsIgnoreCase(StringList.m_str_icon)) {

			}
			break;
		}



    }

    // パラメータの整合性チェック
    private void ConsistenceParameter(List<XmlDictionary> dictlist, dict_num dic) {
    	Log.i("ProfileActivity", "ConsistenceParameter Start.");
    	List<XmlStringData> str_list;


    	int Count = dictlist.size();
    	for(int current = 0; Count > current; current++) {
    		XmlDictionary one_piece = dictlist.get(current);	// カレントのプロパティ(初期値0)
    		str_list = one_piece.GetArrayString();
    		for(int i = 0; str_list.size() > i; i++){
    			// config情報に従って、処理を行う.
    			XmlStringData p_data = str_list.get(i);
    			ConsistenceParameterChild(p_data, dic);
    		}
    	}

    	if (m_bprofile_state == false) {
    		// "プロファイル情報に不正があるため、プロファイルの適用が行えません。"
    		m_Csterr.setText(R.string.profile_err_consistence);
    	}


    }

    private void ConsistenceParameterChild(XmlStringData data, dict_num dic) {
    	Log.i("ProfileActivity", "ConsistenceParameterChild Start.");
    	String strKeyName = data.GetKeyName();	// キー名
		int    i_type = data.GetType();		// 要素タイプ(string:1, data=2, date=3, real=4, integer=5, true=6, false=7)
		String strData = data.GetData();		// 要素

//		ConfigrationProcess p_cnf = new ConfigrationProcess(m_ctx, m_DPM, m_DeviceAdmin);

		if (m_bprofile_state == false) {
			// 既にパラメータエラーが発生しているときは抜ける
			LogCtrl.Logger(LogCtrl.m_strError, "ConsistenceParameterChild  Error", this);
			return;
		}

		boolean b_type = true;
		if(i_type == 7) b_type = false;

		switch(dic) {
		case num_dict:

			break;
		case num_scepdict:
			break;

		case num_wifipict:
			// Wi-Fi
			if(strKeyName.equalsIgnoreCase(StringList.m_str_ssid)) {	// SSID
				m_bprofile_state = consistence_ssid(strData);
			} else if(strKeyName.equalsIgnoreCase("HIDDEN_NETWORK")) {	// HIDDEN_NETWORK

			} else if(strKeyName.equalsIgnoreCase(StringList.m_str_encrypttype)) {	// 暗号方式(WEP, WPA/WPA2)
				m_bprofile_state = consistence_EncType(strData);
			} else if(strKeyName.equalsIgnoreCase(StringList.m_str_WifiPassword)) {
				m_bprofile_state = consistence_Wifipass(strData);
			} else if(strKeyName.equalsIgnoreCase(StringList.m_str_OuterIdentity)) {	// 外部ID
//				m_wifi.SetIdentity(strData);
			} else if(strKeyName.equalsIgnoreCase(StringList.m_str_TLSTrustedServerNames)) {	// 証明書
				;
			} else if(strKeyName.equalsIgnoreCase(StringList.m_str_AcceptEAPTypes)) {	// EAP type
				m_bprofile_state = consistence_EAPTypes(strData);
//				m_wifi.SetEAPTypes(strData);
			} else if(strKeyName.equalsIgnoreCase(StringList.m_str_Phase2)) {	// EAP Phase2 Authentication
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
			if(strKeyName.equalsIgnoreCase(StringList.m_str_webclip_label)) {
//				Shortcut(strData);
			} else if(strKeyName.equalsIgnoreCase(StringList.m_str_URL)) {
			//	m_bprofile_state = consistence_URI(strData);
			} else if(strKeyName.equalsIgnoreCase(StringList.m_str_removal)) {
//				m_c_link.SetRemoval(b_type);
			} else if(strKeyName.equalsIgnoreCase(StringList.m_str_precomposed)) {
//				m_restflgs.SetSafari(b_type);
			} else if(strKeyName.equalsIgnoreCase(StringList.m_str_icon)) {

			}
			break;
		}
    }

    // Profile情報のない、項目を画面から消す
    // View.INVISIBLE:非表示, View.VISIBLE:表示, View.GONE:ないものとする
    private void HideViewMember(dict_num dic){
    	switch(dic) {
		case num_dict:

			break;
		case num_scepdict:
			break;

		case num_wifipict:
			// Wi-Fi
			m_editssid.setVisibility(View.GONE);
			m_editEnc.setVisibility(View.GONE);
			//m_editEapstyle.setVisibility(View.GONE);
			//m_editPhase2.setVisibility(View.GONE);
			//m_editEapId.setVisibility(View.GONE);
			m_editEapPass.setVisibility(View.GONE);
			m_ssiderr.setVisibility(View.GONE);
			m_Encerr.setVisibility(View.GONE);
			//m_Eapstyleerr.setVisibility(View.GONE);
			//m_Phase2err.setVisibility(View.GONE);
			m_EapPasserr.setVisibility(View.GONE);
			m_SepWifi.setVisibility(View.GONE);
			m_textwifi.setVisibility(View.GONE);
			m_textssid.setVisibility(View.GONE);
	    	m_textenc.setVisibility(View.GONE);
	    	m_textpass.setVisibility(View.GONE);
			m_fmwifi01.setVisibility(View.GONE);
			m_fmwifi02.setVisibility(View.GONE);
			m_fmwifi06.setVisibility(View.GONE);
			m_ButtonWifiNext.setVisibility(View.GONE);
			m_ButtonWifiBack.setVisibility(View.GONE);

			break;
		case num_passdict:
			// Passcode


    		break;
		case num_appdict:
			// Restrictions
			m_editAppAccess.setVisibility(View.GONE);
	    	m_textApp.setVisibility(View.GONE);
	    	m_SepApp.setVisibility(View.GONE);

	    	// device admin
	    	m_ButtonStartPscode.setVisibility(View.GONE);
	    	m_ButtonEndPscode.setVisibility(View.GONE);
	    	m_textDevice01.setVisibility(View.GONE);
	    	m_textDevice02.setVisibility(View.GONE);

	    	m_bApplicationControl = false;

			break;
		case num_shortdict:
			// Shortcut
			m_editShortcutName.setVisibility(View.GONE);
	    	m_editUri.setVisibility(View.GONE);
	    	m_Urierr.setVisibility(View.GONE);
	    	m_textShortcut.setVisibility(View.GONE);
	    	m_strLabel.setVisibility(View.GONE);
	    	m_textUri.setVisibility(View.GONE);
	    	m_SepShort.setVisibility(View.GONE);
	    	m_fmshort01.setVisibility(View.GONE);
	    	m_fmshort02.setVisibility(View.GONE);
	    	m_ButtonShortNext.setVisibility(View.GONE);
	    	m_ButtonShortBack.setVisibility(View.GONE);

			break;
		}
    }

    private void SetCamera(boolean b_run) {
    	if(b_run == false) {
    		if(m_strAppAccess.length() == 0)
    			m_strAppAccess = getText(R.string.profile_appctrl_msgctrl).toString();//"制限あり：";

    		m_strAppAccess += getText(R.string.profile_appctrl_camera).toString();//"カメラ, ";
    		m_editAppAccess.setText(m_strAppAccess);
    	}
    }

    private void SetYouTube(boolean b_run) {
    	if(b_run == false) {
    		if(m_strAppAccess.length() == 0)
    			m_strAppAccess = getText(R.string.profile_appctrl_msgctrl).toString();//"制限あり：";

    		m_strAppAccess += "Youtube, ";
    		m_editAppAccess.setText(m_strAppAccess);
    	}
    }

    private void SetiTunes(boolean b_run) {
    	if(b_run == false) {
    		if(m_strAppAccess.length() == 0)
    			m_strAppAccess = getText(R.string.profile_appctrl_msgctrl).toString();//"制限あり：";

    		m_strAppAccess += "Playストア, ";
    		m_editAppAccess.setText(m_strAppAccess);
    	}
    }

    ////<= Passcode //////
    // パスコードの有効期限
    private void maxPINAgeInDays(String days) {
    	Log.i("ProfileActivity", "maxPINAgeInDays");
		try {
			if(days.length() < 1) {
				m_PINAgeerr.setText(R.string.profile_err_passcode_expiry);
			}

			// OSバージョン確認
			double d_android_version = ConfigrationProcess.getAndroidOsVersion();
			Log.i("RunConfigrationChild Version= ", Double.toString(d_android_version));
			if(d_android_version < 3.0) return;

    		long i_maxpin = Long.parseLong(days);

    		if(i_maxpin < 0 || i_maxpin > 730) {
    			m_PINAgeerr.setText(R.string.profile_err_passcode_area);
    		}

    		m_editPINAge.setText(days + getText(R.string.profile_string_day).toString());

    		//SharedPreferences prefs = getSamplePreferences(this);
    		//final long pwExpiration = prefs.getLong(PREF_PASSWORD_EXPIRATION_TIMEOUT, 0L);
    		long l_maxpin = i_maxpin * 86400 * 1000;	// ★ 入力日数 * 1日あたりの秒数 * ミリセカンド

    		Log.i("DeviceAdmin maxpin= ", Long.toString(l_maxpin));

		} catch (Exception e) {
			//e.printStackTrace();
			Log.e("maxPINAgeInDays error:", e.toString());
			m_PINAgeerr.setText(R.string.profile_err_different_form);
		}
    }

    // パスコード履歴
    private void pinHistory(String history) {
		Log.i("ProfileActivity", "pinHistory");
		try {
			// パスコードの設定を行わないプロファイルも存在するはずなので、履歴の未設定はアプリ側では行わない
		//	if(history.length() < 1) {
		//		return;
		//	}

			// OSバージョン確認
			double d_android_version = ConfigrationProcess.getAndroidOsVersion();
			Log.i("RunConfigrationChild Version= ", Double.toString(d_android_version));
			if(d_android_version < 3.0) return;

			int i_his = Integer.parseInt(history);

			if(i_his < 0 || i_his > 50) {
				m_Historyerr.setText(R.string.profile_err_passcode_record);
			}

			m_editPINHistory.setText(history);


    		Log.i("DeviceAdmin pinHistory2= ", Integer.toString(i_his));
		} catch (Exception e) {
			e.printStackTrace();
			m_Historyerr.setText(R.string.profile_err_different_form);
		}
	}

    // パスコードの単純性
    private void allowSimple(boolean type) {
    	Log.i("ProfileActivity", "allowSimple");
		try {
			if (type == true) {
				// true
				m_editPassQuality.setText(R.string.profile_string_able);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
    }

    // パスコードに英数字を入れる
    private void requireAlphanumeric(boolean type) {
    	Log.i("ProfileActivity", "requireAlphanumeric");
		try {
			if (type == true) {
				// true
				m_editPassQuality.setText(R.string.profile_string_alphanumeric);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
    }

    // 最小パスコード長
    private void minLength(String length) {
		Log.i("ProfileActivity", "minLength");
		try {
			if(length.length() < 1) {
				m_editMinPassLength.setText("0");
				return;
			}

		//	int i_length = Integer.parseInt(length);


    		m_editMinPassLength.setText(length);
		} catch (Exception e) {
			e.printStackTrace();
			m_editMinPassLength.setText(R.string.profile_err_different_form);
		}
	}

    // パスコード入力失敗回数上限
    public void maxFailedAttempts(String number) {
		Log.i("ProfileActivity", "maxFailedAttempts");
		try {
			if(number.length() < 1) {
				m_FailerAtmterr.setText(R.string.profile_err_passcode_failure);
			}

    		m_editFailedAttmt.setText(number + getText(R.string.profile_string_count).toString());

		} catch (Exception e) {
			e.printStackTrace();
			m_editFailedAttmt.setText(R.string.profile_err_different_form);
		}
	}

    //// Passcode =>//////

    // SSID
    private void ssid(String str_ssid) {
    	Log.i("ProfileActivity", "ssid");

    	m_ssiderr.setText("");	// 一旦空白セット

    	int ssid_length = /*str_ssid.length()*/str_ssid.getBytes().length;	// 文字数ではなくbyte数で判別する

    	if(ssid_length < 1 || ssid_length > 32) {
    		m_ssiderr.setText(R.string.profile_err_ssid_len);
    	//	m_bprofile_state = false;
    	}
    	for(int index = 0; index < str_ssid.length(); index++) {
    		String tmp_char = "" + str_ssid.charAt(index);

    		if(tmp_char.getBytes().length >= 2) {
    		//	m_ssiderr.setText("ssidにマルチバイト文字は使用できません");
    		//	break;
    		} else if(tmp_char.getBytes().length == 1) {
    			Log.i("ProfileActivity::ssid 1byte", tmp_char);
    		}
    	}
    	if(str_ssid.indexOf("\"") != -1) {
    		m_ssiderr.setText(R.string.profile_err_ssidcode);
    	//	m_bprofile_state = false;
    	}

    	m_editssid.setText(str_ssid);

    }

    static public boolean consistence_ssid(String str_ssid) {
    	boolean rtn_bool = true;
    	int ssid_length = /*str_ssid.length()*/str_ssid.getBytes().length;	// 文字数ではなくbyte数で判別する

    	if(ssid_length < 1 || ssid_length > 32) {
    		rtn_bool = false;
    	}

    	if(str_ssid.indexOf("\"") != -1) {
    		rtn_bool = false;
    	}
    	return rtn_bool;
    }

    // 暗号方式
    private void EncType(String str_enc) {
    	Log.i("ProfileActivity", "EncType");

    	if(str_enc.compareToIgnoreCase("WEP") == 0) {
    		m_Encerr.setText(R.string.profile_err_wep);
    		m_bprofile_state = false;
    	}
    	m_editEnc.setText(str_enc);

    }

    static public boolean consistence_EncType(String str_enc) {
    	boolean rtn_bool = true;

    	if(str_enc.compareToIgnoreCase("WEP") == 0) {
    		rtn_bool = false;
    	}

    	return rtn_bool;

    }

    // Wi-Fiパスワード
    private void Wifipass(String str_password) {
    	Log.i("ProfileActivity", "SetWifipass");

    	if(str_password.length() < 1) {
    		m_EapPasserr.setText(R.string.profile_err_wifipass);
			return;
		}

    	for(int index = 0; index < str_password.length(); index++) {
    		String tmp_char = "" + str_password.charAt(index);

    		if(tmp_char.getBytes().length >= 2) {
    			m_EapPasserr.setText(R.string.profile_err_wifipass_multi);
    			m_bprofile_state = false;
    			break;
    		} else if(tmp_char.getBytes().length == 1) {
    			Log.i("ProfileActivity::ssid 1byte", tmp_char);
    		}
    	}

    	m_editEapPass.setText(str_password);

    }

    static public boolean consistence_Wifipass(String str_password) {
    	boolean rtn_bool = true;


    	for(int index = 0; index < str_password.length(); index++) {
    		String tmp_char = "" + str_password.charAt(index);

    		if(tmp_char.getBytes().length >= 2) {
    			rtn_bool = false;
    		}
    	}

    	return rtn_bool;

    }

    // EAP Type
    private void EAPTypes(String strData) {
    	Log.i("ProfileActivity", "EAPTypes");

    	int i_eap = Integer.parseInt(strData);
    	if(i_eap == WifiControl.TLS) {
    		m_Eapstyleerr.setText(R.string.profile_err_tls);
    		m_editEapstyle.setText("TLS");
    		m_bprofile_state = false;
    	} else if (i_eap == WifiControl.LEAP) {
    		m_editEapstyle.setText("LEAP");
    	} else if (i_eap == WifiControl.TTLS) {
    		m_editEapstyle.setText("TTLS");
    	} else if(i_eap == WifiControl.PEAP) {
    		m_editEapstyle.setText("PEAP");
    	}
    }

    static public boolean consistence_EAPTypes(String strData) {
    	boolean rtn_bool = true;
    	int i_eap = Integer.parseInt(strData);
    	if(i_eap == WifiControl.TLS) {

    		rtn_bool = false;
    	}
    	return rtn_bool;
    }

    // Phase2
    private void Phase2Auth(String str_phase2) {
    	Log.i("ProfileActivity", "Phase2Auth");

    	// TTLSが指定されているときは空欄ではだめ.
    	// このことをどのように判断するか

    	if(str_phase2.compareToIgnoreCase("CHAP") == 0) {
    		m_Phase2err.setText(R.string.profile_err_chap);
    	}
    	m_editPhase2.setText(str_phase2);

    }

    // URI
    private void URI(String str_uri) {
    	Log.i("ProfileActivity", "URI");

    	String str_scheme = Uri.parse(str_uri).getScheme();  // schemeを取得できる。Debug用
    	if(str_scheme == null) {
    		m_Urierr.setText(R.string.profile_err_uri);
    		m_bprofile_state = false;
    	} else if(str_scheme.equals("http") == false
    			&& str_scheme.equals("https") == false
    			/*&& str_scheme.equals("mailto") == false*/) {
    	//	m_Urierr.setText("URIに指定できるのはhttpまたはhttpsまたはmailtoのみです");
    		m_Urierr.setText(R.string.profile_err_uri);
    		m_bprofile_state = false;
    	}
    	m_editUri.setText(str_uri);

    }

    static public boolean consistence_URI(String str_uri) {
    	boolean rtn_bool = true;
    	String str_scheme = Uri.parse(str_uri).getScheme();  // schemeを取得できる。Debug用
    	if(str_scheme.equals("http") == false
    			&& str_scheme.equals("https") == false
    			/*&& str_scheme.equals("mailto") == false*/) {
    	//	m_Urierr.setText("URIに指定できるのはhttpまたはhttpsまたはmailtoのみです");
    		rtn_bool = false;
    	}
    	return rtn_bool;

    }

    // Shortcut
    private void Shortcut(String str_shortname) {
    	Log.i("ProfileActivity", "Shortcut");
    	m_editShortcutName.setText(str_shortname);
    }

	@Override
	public void onClick(View v) {
		Log.i("ProfileActivity::onClick", "start");
		// TODO 自動生成されたメソッド・スタブ
		if(v == m_ButtonSet) {
			// プロファイルの適用
			// ConfigrationDivision::RunConfigration--RunConfigrationChildのような処理をここで行わなければならない.
			// XmlDictionary m_pdictionaryの中身はhttp_connection()で設定する
			// XmlDictionaryは各機能(Wifi,passcode,application,scep...shortcutは未定)毎に存在する。

			// <==== デバッグ用1
/*			String rtn_str = ReadAndSetWifiInfo();

			XmlPullParserAided m_p_aided = new XmlPullParserAided(this, rtn_str, 2);	// 最上位dictの階層は2になる
			boolean ret = m_p_aided.TakeApartProfile();
			if (ret == false) {
				Log.e("EnrollActivity::onClick", "Enroll xml analyze");
				return;
			}
*/			// デバッグ用1 ====>

			// <==== 実体
			/*ConfigrationDivision p_conf*/m_p_conf = new ConfigrationDivision(this, m_p_aided, m_DPM, m_DeviceAdmin);
			boolean bRet = m_p_conf.RunConfigration();

			if(bRet) {
				//this.moveTaskToBack(true);  // アプリの中断(Homeボタンを押したときと同じような感じ
				setResult(StringList.RESULT_CLOSE);
				finish();
			} else {
				String filedir = "/data/data/" + getPackageName() + "/files/";

				//<=== ショートカットの削除
				File filename = new File(filedir + StringList.m_strShortcutOutputFile);
				Log.i("ProfileResetActivity::onClick", "Filename = " + filename);

				if(filename.exists()) {
					CreateShortcutLink c_link = new CreateShortcutLink(this);
					c_link.ReadAndSetShortcutInfo();
					c_link.RemoveRun();
					filename.delete();
				}
				// ショートカットの削除 ===>

				//<=== Wifiの削除
				// Wi-Fi
				File filename2 = new File(filedir + StringList.m_strWifiOutputFile);

				if(filename2.exists()) {
					WifiControl wifi = new WifiControl(this);
					wifi.ReadAndSetWifiInfo(StringList.m_strWifiOutputFile);
					wifi.deleteConfig();
					filename2.delete();
				}
				// Wifiの削除 ===>

				//<=== 機能制限の削除
				File filename3 = new File(filedir + StringList.m_strRestrictionFileName);
				if(filename3.exists()) {
					RestrictionsControl m_resriction = new RestrictionsControl(this);	// この時点でサービスを止める

					filename3.delete();

				};

				// エラーメッセージ
				m_Seterr.setText(R.string.profile_err_profileset);
				m_ButtonSet.setEnabled(false);		// グレーアウトして連打させないようにする
				m_ButtonEndPscode.setEnabled(false);
				m_ButtonStartPscode.setEnabled(false);
				m_ButtonWifiNext.setEnabled(false);
				m_ButtonWifiBack.setEnabled(false);
				m_ButtonShortNext.setEnabled(false);
				m_ButtonShortBack.setEnabled(false);

			}
			// 実体 ====>

			// <==== デバッグ用2
			// 機能制限
//			RestrictionsControl m_resriction = new RestrictionsControl(this);
//			RestrictionsFlgs m_restflgs = new RestrictionsFlgs();

//			m_restflgs.SetYouTube(false);
//			m_restflgs.SetCamera(false);

//			m_restflgs.WriteRestrictionsInfo(this);
//			m_resriction.SrartMoniter(m_restflgs);

			// Wifi
/*			WifiControl m_wifi = new WifiControl(this);
			m_wifi.SetSSID("EPS-ap WPA2");
			m_wifi.SetEncType("WPA");
			m_wifi.SetWifipass("password");

			m_wifi.PublicConnect();
*/
			// ショートカット
//			CreateShortcutLink m_c_link = new CreateShortcutLink(this);
//			m_c_link.SetAction(Intent.ACTION_VIEW);
//			m_c_link.SetShortcutName(m_editShortcutName.getText().toString());
//			m_c_link.SetUri(m_editUri.getText().toString());
//			m_c_link.CreateRun();
			// デバッグ用2 ====>

			Log.i("ProfileActivity::onClick", "m_ButtonSet");
		} else if (v == m_ButtonEndPscode) {
			if(isDeviceAdmin() == true) {
				Log.i("EPS-ip", "AdminReceiver end.");
				m_DPM.removeActiveAdmin(m_DeviceAdmin);
				//updateUi();
				// DeviceAdminのremove実行後、すぐにisDeviceAdminを呼んでもtrueになってしまうときがあるので,
				// ここで直接、ボタンのグレーアウトを切り替える
				m_ButtonEndPscode.setEnabled(false);
			   	m_ButtonStartPscode.setEnabled(true);
			   	m_ButtonSet.setEnabled(false);
			}
		} else if (v == m_ButtonStartPscode) {
			if(isDeviceAdmin() == false) {
				Log.i("EPS-ip", "AdminReceiver start.");
				addDeviceAdmin();
				updateUi();
				//m_ButtonEndPscode.setEnabled(true);
			   	//m_ButtonStartPscode.setEnabled(false);
			   	//m_ButtonSet.setEnabled(true);
			}
		} else if(v == m_ButtonShortNext) {
		    m_nShortcutCurrentNum++;
		    if(m_nShortcutCurrentNum < m_nShortcutCount) {
		    	XmlDictionary one_piece = p_Shortcutdictlist.get(m_nShortcutCurrentNum);	// カレントのプロパティ(初期値0)
		    	List<XmlStringData> str_list = one_piece.GetArrayString();
	    		for(int i = 0; str_list.size() > i; i++){
	    			// config情報に従って、処理を行う.
	    			XmlStringData p_data = str_list.get(i);
	    			SetEditMemberChild(p_data, dict_num.num_shortdict);
	    		}
		    } else m_nShortcutCurrentNum--;
		    updateUi();
		} else if(v == m_ButtonShortBack) {
			m_nShortcutCurrentNum--;
			if(m_nShortcutCurrentNum > -1) {
		    	XmlDictionary one_piece = p_Shortcutdictlist.get(m_nShortcutCurrentNum);	// カレントのプロパティ(初期値0)
		    	List<XmlStringData> str_list = one_piece.GetArrayString();
	    		for(int i = 0; str_list.size() > i; i++){
	    			// config情報に従って、処理を行う.
	    			XmlStringData p_data = str_list.get(i);
	    			SetEditMemberChild(p_data, dict_num.num_shortdict);
	    		}
		    } else m_nShortcutCurrentNum++;
			updateUi();
		} else if(v == m_ButtonWifiNext) {
			m_nWifiCurrentNum++;
			m_editEapPass.setText("");	// パスワードが設定されていないとき、前頁のものが残ってしまうため一旦空白リセット
		    if(m_nWifiCurrentNum < m_nWifiCount) {
		    	XmlDictionary one_piece = p_Wifidictlist.get(m_nWifiCurrentNum);	// カレントのプロパティ(初期値0)
		    	List<XmlStringData> str_list = one_piece.GetArrayString();
	    		for(int i = 0; str_list.size() > i; i++){
	    			// config情報に従って、処理を行う.
	    			XmlStringData p_data = str_list.get(i);
	    			SetEditMemberChild(p_data, dict_num.num_wifipict);
	    		}
		    } else m_nWifiCurrentNum--;
		    updateUi();
		} else if(v == m_ButtonWifiBack) {
			m_nWifiCurrentNum--;
			m_editEapPass.setText("");	// パスワードが設定されていないとき、前頁のものが残ってしまうため一旦空白リセット
			if(m_nWifiCurrentNum > -1) {
		    	XmlDictionary one_piece = p_Wifidictlist.get(m_nWifiCurrentNum);	// カレントのプロパティ(初期値0)
		    	List<XmlStringData> str_list = one_piece.GetArrayString();
	    		for(int i = 0; str_list.size() > i; i++){
	    			// config情報に従って、処理を行う.
	    			XmlStringData p_data = str_list.get(i);
	    			SetEditMemberChild(p_data, dict_num.num_wifipict);
	    		}
		    } else m_nWifiCurrentNum++;
			updateUi();
		} else if(v == m_MailButton) {
			Log.i("ProfileActivity::onClick", "Mail");
			AddressInfo.Runmailer(this);
			// アクティビティの終了
		//	finish();
		} else if(v == m_PhoneButton) {
			Log.i("ProfileActivity::onClick", "Phone");
			AddressInfo.RunTelephone(this);
		}
	}


	// UIのグレーアウト状況
	private void updateUi() {
		if (m_Seterr.getText().toString().length() > 0) return;	// エラーメッセージが表示されているときはボタンの状態を全く変更しない

	   	boolean running = isDeviceAdmin();
	   	m_ButtonEndPscode.setEnabled(running);
	   	m_ButtonStartPscode.setEnabled(!running);

	   	if(m_bApplicationControl == true) {
	   		m_ButtonSet.setEnabled(m_bSetButtonEnable & running & m_bprofile_state);
	   	} else {
	   		m_ButtonSet.setEnabled(/*true*/m_bSetButtonEnable & m_bprofile_state);
	   	}

	   	// Wi-Fiプロパティボタン
	   	if(m_nWifiCurrentNum <= 0) {
	   		m_ButtonWifiBack.setEnabled(false);
	   	} else {
	   		m_ButtonWifiBack.setEnabled(true);
	   	}

	   	if(m_nWifiCurrentNum >= m_nWifiCount - 1) {
	   		m_ButtonWifiNext.setEnabled(false);
	   	} else {
	   		m_ButtonWifiNext.setEnabled(true);
	   	}

	   	// ショートカットプロパティボタン
	   	if(m_nShortcutCurrentNum <= 0) {
	   		m_ButtonShortBack.setEnabled(false);
	   	} else {
	   		m_ButtonShortBack.setEnabled(true);
	   	}

	   	if(m_nShortcutCurrentNum >= m_nShortcutCount - 1) {
	   		m_ButtonShortNext.setEnabled(false);
	   	} else {
	   		m_ButtonShortNext.setEnabled(true);
	   	}
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
		startActivityForResult(intent, RESULT_ENABLE);
	}

	public boolean isDeviceAdmin() {
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
		case 10:	// SCEPの証明書インストールからのrequestcode
			Log.i("ProfileActivity", "onActivityResult Requestcode 10.");
/*
			m_p_conf.RunScepEnrollment();
			finish();
*/	//		ConfigrationDivision p_conf = new ConfigrationDivision(this, m_p_aided, m_DPM, m_DeviceAdmin);

			break;
		case StringList.REQUEST_CERT_ENROLL:		// Enroll
			break;
		}

		super.onActivityResult(requestCode, resultCode, data);
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