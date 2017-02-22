package epsap4.soliton.co.jp;

public class StringList {
	public final static int RESULT_CLOSE = 666;
	public final static int RESULT_HTTP_CON_ERR = 667;
	public final static int RESULT_GUIDE_CLOSE = 668;
	public final static int RESULT_CERT_CLOSE = 669;
	public final static int REQUEST_CERT_ENROLL = 25;

	public final static int RES_200_OK = 200;
	public final static int RES_401_Unauthorized = 401;
	public final static int RES_403_Forbidden = 403;


//	public StringList() {};




	//<======= URL Scheme
	public final static String m_str_schemehost = "host";
	public final static String m_str_schemeuser = "user";
	public final static String m_str_schemepass = "pass";
	public final static String m_str_schemeauto = "autoconnect";

	/*--- POSTパラメータ ---*/
	public final static String m_strUserID = "UserID=";
	public final static String m_strPassword = "Password=";
	public final static String m_strSerial = "Serial=";


	// Restriction情報を保存するファイル
	public final static String m_strfalse = "false";
	public final static String m_strtrue = "true";
	public final static String m_strRestrictionFileName = "MDMRestriction.txt";

	// PayloadType KEY
	public final static String m_str_payloadtype = "PayloadType";

	//*---- RemovePassword ----*/
	// PayloadType
	public final static String m_str_removalpwd_profile = "com.apple.profileRemovalPassword";
	// removal password
	public final static String m_str_remove_pwd = "RemovalPassword";

	//*---- Restiction -----*/
	// PayloadType
	public final static String m_str_appli_profile = "com.apple.applicationaccess";
	// カメラ
	public final static String m_strCamera = "allowCamera";
	// iTunes(=Anroid Market)
	public final static String m_striTunes = "allowiTunes";
	// YouTube
	public final static String m_strYoutube = "allowYouTube";
	// Safari
	public final static String m_strSafari = "allowSafari";

	//*---- Passcode ------*/
	// PayloadType
	public final static String m_str_pass_profile = "com.apple.mobiledevice.passwordpolicy";
	// パスコードの単純性
	public final static String m_str_allowSimple = "allowSimple";
	// パスコードの英文字
	public final static String m_str_requireAlphanumeric = "requireAlphanumeric";
	// 最小パスコード長
	public final static String m_str_minLength = "minLength";
	// 複合パスコード設定
	public final static String m_str_minComplexChars = "minComplexChars";
	// パスコードの有効期限
	public final static String m_str_maxPINAgeInDays = "maxPINAgeInDays";
	// パスコード履歴
	public final static String m_str_pinHistory = "pinHistory";
	// パスコード入力失敗回数上限
	public final static String m_str_maxFailedAttempts = "maxFailedAttempts";
	// 強制PIN設定
	public final static String m_str_forcePIN = "forcePIN";

	/////////////////////////////////
	//<== *---- Wi-Fi ------*/
	// PayloadType
	public final static String m_str_wifi_profile = "com.apple.wifi.managed";
	// 出力ファイル(インスタントプロファイル)
	public final static String m_strWifiOutputFile = "WifiOutput.txt";
	// 出力ファイル(SCEP対応)
	public final static String m_strScepWifiOutputFile = "ScepWifiOutput.txt";
	// SSID
	public final static String m_str_ssid = "SSID_STR";
	// 暗号方式(WEP, WPA/WPA2)
	public final static String m_str_encrypttype = "EncryptionType";
	// パスワード1
	public final static String m_str_WifiPassword = "Password";
	// パスワード2
	public final static String m_str_UserPassword = /*"Password";*/"UserPassword";
	// ユーザー名
	public final static String m_str_UserName = "UserName";
	// 外部ID
	public final static String m_str_OuterIdentity = "OuterIdentity";
	// 証明書
	public final static String m_str_TLSTrustedServerNames = "TLSTrustedServerNames";
	// EAP Type
	public final static String m_str_AcceptEAPTypes = "AcceptEAPTypes";
	// Phase2
	public final static String m_str_Phase2 = "TTLSInnerAuthentication";
	// *---- Wi-Fi ------*/ ==>
	///////////////////////////////////

	/////////////////////////////////
	//<==*---- Shortcut(WebClip) ------*/
	// 出力ファイル
	public final static String m_strShortcutOutputFile = "ShortcutOutput.txt";
	// PayloadType
	public final static String m_str_webclip_profile = "com.apple.webClip.managed";
	// Label
	public final static String m_str_webclip_label = "Label";
	// URL
	public final static String m_str_URL = "URL";
	// 取り除き
	public final static String m_str_removal = "IsRemovable";
	// 作成済みアイコン
	public final static String m_str_precomposed = "Precomposed";
	// Icon
	public final static String m_str_icon = "Icon";
	// アクション ひとまず"android.intent.action.VIEWのみなので必要なし
	// *---- Shortcut(WebClip) ------*/ ==>
	///////////////////////////////////

	/////////////////////////////////
	//<==*---- Scep -----*/
	// PayloadType
	public final static String m_str_scep_profile = "com.apple.security.scep";
	// CA IDENT (CA 証明書)
	public final static String m_str_CaIdent = "Name";
	// URL
	public final static String m_str_scep_url = "URL";
	// Subject
	public final static String m_str_subject = "Subject";
	// Challenge
	public final static String m_str_scep_challenge = "Challenge";
	// Key Type
	public final static String m_str_scep_keytype = "Key Type";
	// rfc822Name
	public final static String m_str_scep_rfc822Name = "rfc822Name";
	// mailAddress
	public final static String m_str_scep_mailaddr = "EmailAddress";
	// *---- Scep ------*/ ==>
	///////////////////////////////////

	/////////////////////////////////
	//<== *---- MDM ------*/
	// PayloadType
	public final static String m_str_mdm_profile = "com.apple.mdm";
	// 出力ファイル
	public final static String m_strMdmOutputFile = "MdmOutput.txt";
	// サーバーURL
	public final static String m_str_mdm_server = "ServerURL";
	// チェックインURL
	public final static String m_str_mdm_checkin = "CheckInURL";
	// Topic
	public final static String m_str_topic = "Topic";
	// AccessRights
	public final static String m_str_AccessRights = "AccessRights";
	// チェックアウト
	public final static String m_str_CheckOutRemoved = "CheckOutWhenRemoved";
	// *---- MDM ------*/ ==>
	///////////////////////////////////

	//*---- Device Info -----*/
	// Challenge
	public final static String m_str_challenge = "Challenge";
	// UDID
	public final static String m_str_udid = "UDID";
	// IMEI
	public final static String m_str_imei = "IMEI";
	// ICCID
	public final static String m_str_iccid = "ICCID";
	// PRODUCT
	public final static String m_str_product = "PRODUCT";
	// VERSION
	public final static String m_str_version = "VERSION";
	// MAC_ADDRESS_EN0
	public final static String m_str_macaddr = "MAC_ADDRESS_EN0";
	//*---- 申請 -----*/
	// 申請済み
	public final static String m_str_issubmitted = "IsSubmitted";
	// MailAddress
	public final static String m_str_mailaddress = "MailAddress";
	public final static String m_str_description = "Description";


	//*---- ProfileList -----*/
	// ProfileID
	public final static String m_str_profileid = "ProfileID";
	// PayloadDisplayName
	public final static String m_str_payloaddisplayname = "PayloadDisplayName";
	// 証明書取得
	public final static String m_str_scepprofile = "HasSCEPProfile";
	//connected
	public final static String m_str_isConnected = "IsConnected";
	//connected
	public final static String m_str_isEnroll = "PayloadIdentifier";
	// 連絡先メールアドレス
	public final static String m_str_pflist_addr = "MailAddress";
	// 連絡先TEL
	public final static String m_str_pflist_phone = "PhoneNumber";

	//*---- EPS-ap Server Info -----*/
	// 出力ファイル
	public final static String m_strEPSapSrvOutputFile = "EpsapServerInf.txt";
	// URL
	public final static String m_strEPSapURL = "EPSapURL";

	//*---- Login User Info ----*/
	// 出力ファイル
	public final static String m_strLoginUserOutputFile = "LoginUserInf.txt";
	// ユーザーID
	public final static String m_str_User_id = "LoginUserID";
	// パスワード
	public final static String m_str_LoginUser_Pass = "LoginUserPW";
	// APID Wi-Fi #21391
	public final static String m_str_Apid_Wifi = "ApidWiFi";
	// APID VPNとアプリ #21391
	public final static String m_str_Apid_VPN = "ApidVPN";

	/////////////////////////////////////////////////////
	// <== MDM メッセージ  Android->Eps-ap
	//*--- チェックイン/チェックアウト ---*/
	public final static String m_strMessageType = "MessageType";
	public final static String m_strCheckIn = "Authenticate";
	public final static String m_strTokenUp = "TokenUpdate";
	// Topic, UDIDは使いまわす
	public final static String m_strCheckOut = "CheckOut";
	//*--- DeviceInf ---*/
	public final static String m_strQR = "QueryResponses";
	//*--- Status ---*/
	public final static String m_strStatus = "Status";
	public final static String m_strIdle = "Idle";
	public final static String m_strAc = "Acknowledged";
	public final static String m_strError = "Error";
	public final static String m_strCFError = "CommandFormatError";	// 使用せず
	//*-- TokenUpdate --*/
	public final static String m_strPM = "PushMagic";
	public final static String m_strToken = "Token";
	public final static String m_strUT = "UnlockToken";
	//*--- ErrorMsg -*/
	public final static String m_strErrorChain = "ErrorChain";
	public final static String m_strErrorCode = "ErrorCode";
	// MDM メッセージ Android->Eps-ap ==>
	//////////////////////////////////////////////////////////

	/////////////////////////////////////////////////////
	// <== MDM メッセージ  Eps-ap->Android
	//*--- 共通 ---*/
	// CommandUUID
	public final static String m_str_cmduuid = "CommandUUID";
	// RequestType
	public final static String m_str_RequestType = "RequestType";

	//*--- InstalledApplicationList(インストールアプリケーション情報) ---*/
	// RequestType
	public final static String m_str_RType_AppList = "InstalledApplicationList";

	//*--- DeviceInformation(デバイス情報) ---*/
	// RequestType
	public final static String m_str_RType_DevInf = "DeviceInformation";

	//*--- DeviceLock(デバイスロック) ---*/
	// RequestType
	public final static String m_str_RType_DevLock = "DeviceLock";

	//*--- EraseDevice(ワイプ) ---*/
	// RequestType
	public final static String m_str_RType_Wipe = "EraseDevice";

	// MDM メッセージ Eps-ap->Android ==>
	//////////////////////////////////////////////////////////

	/////////////////////////////////////////////////////
	// <== 配布アプリケーション情報
	// UUID
	public final static String m_str_uuid = "UUID";
	// アプリの名称
	public final static String m_str_app_name = "applicationname";
	// バージョン情報
	public final static String m_str_app_version = "Version";
	// アイコン画像
	public final static String m_str_app_icon = "IconImg";
	// apk名称
	public final static String m_str_app_apk = "ApkName";
	// 配布アプリケーション情報 ==>
	//////////////////////////////////////////////////////////

	//*--- SCEP-MDM Info ---*/
	// 出力ファイル
	public final static String m_strScepMdmOutputFile = "ScepMdmInf.txt";
	// EPS-ap URLは[EPS-ap Server Info]のを流用

	// Alias
	public final static String m_strAlias = "certalias";

	// 通信フォーマットのキーワード. #res\values\string.xmlに保存すると、参照するときにいちいちContextを持ってこないといけないので
	// すっごい面倒くさい。よってここに記述する. 必要に応じて随時追加
	public final static String m_str_dict = "dict";
	public final static String m_str_array = "array";
	public final static String m_str_key = "key";
	public final static String m_str_string = "string";
	public final static String m_str_integer = "integer";

	// Serializable
	public final static String m_str_InformCtrl = "Inform";		// InformCtrlクラス
	public final static String m_str_ListItem = "ListItem";		// ListItemクラス
	public final static String m_str_MdmFlgs = "mdmlabel";		// MDMFlgsクラス


	// Debug
	// Test XML Data
	public final static String m_str_xml_testdata = "";
	// 出力ファイル
	public final static String m_str_debug_profile = "profile.txt";

	public final static String m_str_SKMTag = "Soliton KM";

	//SKM version 2.0
	/*--- Get Cert ---*/
	public final static String m_str_alias_skm = "alias SKM";
	// SKMNotification
	//Name of preference name store SKM
	public final static String m_str_store_preference="storeprepreferenceSKM";
	//Name of preference name that saves the status all notification final date
	public final static String m_str_notification_final_date_pref = "allnotificationfinaldate";
	//Name of preference name that saves the status all notification
	public final static String m_str_notification_before_final_date_pref = "allnotificationbeforefinaldate";
	//Name of preference name that saves the status all notification before final date
	public final static String m_str_number_before_final_date_pref = "numberbeforefinaldate";
	//path common name of Certificate
	public static final String m_str_common_name_certificate ="CN=";
	//path country name of Certificate
	public static final String m_str_country_name_certificate ="C=";
	//path locality name of Certificate
	public static final String m_str_locality_name_certificate ="L=";
	//path organization name of Certificate
	public static final String m_str_organization_name_certificate ="O=";
	//path organization unit name of Certificate
	public static final String m_str_organization_unit_name_certificate ="OU=";
	//path street name of Certificate
	public static final String m_str_street_name_certificate ="ST=";
	//path email name of Certificate
	public static final String m_str_email_name_certificate ="E=";

}