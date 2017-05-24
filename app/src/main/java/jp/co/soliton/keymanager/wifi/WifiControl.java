package jp.co.soliton.keymanager.wifi;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiConfiguration.*;
import android.net.wifi.WifiEnterpriseConfig;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.util.Log;
import android.util.Xml;
import jp.co.soliton.keymanager.R;
import jp.co.soliton.keymanager.StringList;
import jp.co.soliton.keymanager.xmlparser.XmlDictionary;
import jp.co.soliton.keymanager.xmlparser.XmlPullParserAided;
import jp.co.soliton.keymanager.xmlparser.XmlStringData;
import org.xmlpull.v1.XmlSerializer;

import java.io.*;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;

public class WifiControl {

	// EAP 種別
	public enum EAP_TYPE {
		PEAP,
		TLS,
		TTLS,
		LEAP,
	};

	public static final int TLS = 13;
	public static final int LEAP = 17;
	public static final int TTLS = 21;
	public static final int PEAP = 25;
	public static final int NONE = 0;

	public static final int INSTANT_WIFI = 100;
	public static final int SCEP_WIFI = 101;


	private static final String INT_PRIVATE_KEY = "private_key";
    private static final String INT_PHASE2 = "phase2";
    private static final String INT_PASSWORD = "password";
    private static final String INT_IDENTITY = "identity";
    private static final String INT_EAP = "eap";
    private static final String INT_CLIENT_CERT = "client_cert";
    private static final String INT_CA_CERT = "ca_cert";
    private static final String INT_ANONYMOUS_IDENTITY = "anonymous_identity";
    private static final String INT_PRIVATE_KEY_ID = "key_id";
    final String INT_ENTERPRISEFIELD_NAME = "android.net.wifi.WifiConfiguration$EnterpriseField";

    private Context context;
    private List<WifiItem> m_pWifiItem;
    private String m_strCaCert = "";
    private String m_strUserCert = "";

    // mdm system add.
//    boolean m_bHidden;
//    String m_strssid = "";
//    String m_stridentity = "";
//    String m_strwifipass = "";
//    String m_strenctype = "";
//    String m_strphase2auth = "";
//    int m_numEaptype = PEAP;

    // MDM system parameter Set Method.
//    public void SetHidden(boolean hide) {m_bHidden = hide;}
//    public void SetSSID(String ssid) {m_strssid = ssid;}
//    public void SetIdentity(String identity) {m_stridentity = identity;}
//    public void SetWifipass(String pass) {m_strwifipass = pass;}
//    public void SetEncType(String enc) {m_strenctype = enc;}
//    public void SetPhase2Auth(String phase2) {m_strphase2auth = phase2;}
//    public void SetEAPType(String eap) {
//    	int i_eap = Integer.parseInt(eap);
 //   	if(i_eap == TLS) m_numEaptype = TLS;
 //   	else if (i_eap == LEAP) m_numEaptype = LEAP;
//    	else if (i_eap == TTLS) m_numEaptype = TTLS;
//    	else m_numEaptype = PEAP;
//    }

    public WifiControl(Context con) {
    	context = con;
    	m_pWifiItem = new ArrayList<WifiItem>();
    //	readWepConfig();		// デバッグ用
    //	readEapConfig();
    }

    public void SetWifiList(XmlDictionary dict) {
    	WifiItem item_piece = new WifiItem();

    	List<XmlStringData> str_list;
		str_list = dict.GetArrayString();
		for(int i = 0; str_list.size() > i; i++){
			// config情報に従って、処理を行う.
			XmlStringData p_data = str_list.get(i);
			SetConfigrationChild(p_data, item_piece);
		}

		m_pWifiItem.add(item_piece);
    }

    public void SetCaCert(String ca) {m_strCaCert = ca;}
    public void SetUserCert(String us) {m_strUserCert = us;}

    private void SetConfigrationChild(XmlStringData p_data, WifiItem item_piece) {
		String strKeyName = p_data.GetKeyName();	// キー名
		int    i_type = p_data.GetType();		// 要素タイプ(string:1, data=2, date=3, real=4, integer=5, true=6, false=7)
		String strData = p_data.GetData();		// 要素

		Log.i("WifiControl::SetConfigrationChild", "Start. " + strKeyName);

		boolean b_type = true;
		if(i_type == 7) b_type = false;

		if(strKeyName.equalsIgnoreCase(StringList.m_str_ssid)) {	// SSID
			item_piece.SetSSID(strData);
		} else if(strKeyName.equalsIgnoreCase("HIDDEN_NETWORK")) {	// HIDDEN_NETWORK
			item_piece.SetHidden(b_type);
		} else if(strKeyName.equalsIgnoreCase(StringList.m_str_encrypttype)) {	// 暗号方式(WEP, WPA/WPA2)
			item_piece.SetEncType(strData);
		} else if(strKeyName.equalsIgnoreCase(StringList.m_str_WifiPassword)) {
			item_piece.SetWifipass(strData);
		} else if(strKeyName.equalsIgnoreCase(StringList.m_str_OuterIdentity)) {	// 外部ID
			item_piece.SetIdentity(strData);
		} else if(strKeyName.equalsIgnoreCase(StringList.m_str_TLSTrustedServerNames)) {	// 証明書
			;
		} else if(strKeyName.equalsIgnoreCase(StringList.m_str_AcceptEAPTypes)) {	// EAP type
			item_piece.SetEAPType(strData);
		} else if(strKeyName.equalsIgnoreCase(StringList.m_str_Phase2)) {	// EAP Phase2 Authentication
			item_piece.SetPhase2Auth(strData);
		} else if(strKeyName.equalsIgnoreCase(StringList.m_str_UserName)) {	// UserName
			item_piece.SetUserName(strData);
		} else if(strKeyName.equalsIgnoreCase(StringList.m_str_UserPassword)) {
			item_piece.SetUserPass(strData);
		}

	}

    public boolean Disconnect() {
    	return disconnect();
    }

    private boolean disconnect() {
    	boolean bRet = false;

    	WifiManager wifiManager = (WifiManager)context.getSystemService(Context.WIFI_SERVICE);
    	bRet = wifiManager.disconnect();

    	return bRet;
    }

    /*
    public boolean Connect(EAP_TYPE eapType, String ssid, String identity, String password, String usercert) {
    	boolean bRet = false;
    	WifiConfiguration wifiConfig = null;

    	// Create Configuration
    	wifiConfig = createEapConfig(eapType, ssid, identity, password, usercert);

    	// Delete Configuration
    	deleteConfig(ssid);

    	// Connect
    	if (wifiConfig != null) {
    		bRet = connect(wifiConfig);
    	}

    	return bRet;
    }*/


    ///// yoshim add.
    // Connect
    public boolean PublicConnect(int nProfileType) {
    	Log.i("WifiControl", "PublicConnect start.");

    	boolean bRet = false;
    	// Wifi情報がないときは抜ける.
    	if(m_pWifiItem.size() < 1) { return true; }

    	// 設定内容をファイル保存
    	if(nProfileType == INSTANT_WIFI) {
    		WriteWifiInfo(StringList.m_strWifiOutputFile);
    	} else if(nProfileType == SCEP_WIFI) {
    		WriteWifiInfo(StringList.m_strScepWifiOutputFile);
    	}

    	for(int i = 0; m_pWifiItem.size() > i; i++){
			WifiItem wifiitem = m_pWifiItem.get(i);
			bRet = PublicConnectChild(wifiitem);
			if(bRet == false) return bRet;
		}
    /*
    	WifiConfiguration wifiConfig = null;

    	// SSIDが設定されていないときは、そのまま進めるとwifiの設定がおかしくなるので抜ける。
    	if(m_strssid.length() < 1) { return true;}

    	// 設定内容をファイル保存
    	WriteWifiInfo();

    	wifiConfig = this.createEapConfig2();

    	// Delete Configuration :: createEapConfig2で作成したWifiConfigと同じSSIDが既に存在するときは、先に削除しておく
    	deleteConfig(m_strssid);

    	// Connect
    	if (wifiConfig != null) {
    		bRet = connect(wifiConfig);
    	}
    */
    	Log.i("WifiControl", "PublicConnect end.");

    	return bRet;
    }

    public boolean PublicConnectChild(WifiItem wifiitem) {
    	boolean bRet = false;
    	WifiConfiguration wifiConfig = null;
    	boolean bConnect = true;

    	// OSバージョン確認
    	int sdk_int_version = Build.VERSION.SDK_INT;

    	if(wifiitem.GetEAPType() == NONE) {
    		Log.d("WifiControl::PublicConnectChild", "EAPType NONE. ");
    		wifiConfig = this.createPskConfig(wifiitem);
    		if(sdk_int_version > Build.VERSION_CODES.LOLLIPOP_MR1) bConnect = false;
    	} else if (sdk_int_version < Build.VERSION_CODES.JELLY_BEAN_MR2){
    		Log.d("WifiControl::PublicConnectChild", "EAPType = " + wifiitem.GetEAPType());
    		wifiConfig =this.createEapConfigEAP(wifiitem);
    	} else {
    		Log.d("WifiControl::PublicConnectChild over 4.3", "EAPType = " + wifiitem.GetEAPType());
    		// 4.3以降のEAPはとりあえず、Wi-Fiセット不可
    	//	wifiConfig =this.createEapConfigEAPover43(wifiitem);
    		wifiConfig =this.createEapConfigEAP(wifiitem);
    		bConnect = false;
//    		EAPWifi43Info();
    	}

    	// Delete Configuration :: createEapConfig2で作成したWifiConfigと同じSSIDが既に存在するときは、先に削除しておく
    	deleteConfig(wifiitem.GetSSID());

    	// Connect
    	if (wifiConfig != null) {
    		bRet = connect(wifiConfig, bConnect);
    	}

    	Log.i("WifiControl", "PublicConnectChild end.");

    	return bRet;
    }

    // Android 4.3以降のEAPのプロパティが存在するときの確認ダイアログを表示
    private void EAPWifi43Info() {
		AlertDialog.Builder dlg;
		dlg = new AlertDialog.Builder(context);
		dlg.setTitle(context.getText(R.string.Dialog_title_wifiinfo).toString());
		dlg.setMessage(context.getText(R.string.Dialog_msg_wifiinfo).toString());
		dlg.setPositiveButton("YES", new DialogInterface.OnClickListener() {
			 public void onClick(DialogInterface dialog, int id) {
			 // 確認だけなので動作は何も行わない
			    }
		});


		dlg.show();
	}

    public boolean deleteConfig(String ssid) {
    	boolean bRet = false;

    	WifiManager wifiManager = (WifiManager)context.getSystemService(Context.WIFI_SERVICE);

    	List<WifiConfiguration> configs = wifiManager.getConfiguredNetworks();
		for (WifiConfiguration config : configs) {
		//	Log.d("Wifi", "Config:" + config.toString());

			if (config.SSID.equals("\"" + ssid + "\"")) {
				if (wifiManager.removeNetwork(config.networkId)) {
					Log.d("Wifi", "Remove network success. " + config.SSID);
					bRet = true;
				}
				else {
					Log.w("Wifi", "Remove network failed. " + config.SSID);
				}
			}
		}

    	return bRet;
    }

    /* プロファイル削除におけるAPの削除 */
    public boolean deleteConfig() {
    	boolean bRet = false;

    	// 対象のSSIDのAPを削除
    	for(int i = 0; m_pWifiItem.size() > i; i++){
			WifiItem wifiitem = m_pWifiItem.get(i);
			bRet = deleteConfig(wifiitem.GetSSID());
		}
    //	bRet = deleteConfig(m_strssid);

    	// 以前使用していたAPに接続できるように、一旦Wi-Fi OFF、その後Wi-Fi ONに設定する
    	WifiManager wifiManager = (WifiManager)context.getSystemService(Context.WIFI_SERVICE);
    	wifiManager.saveConfiguration();	// APを削除した後のWi-FiのConfigを一旦保存しなければならない. 障害DB 235
		wifiManager.setWifiEnabled(false);	// Wi-Fi OFF
		wifiManager.setWifiEnabled(true);	// Wi-Fi ON

    	return bRet;//deleteConfig(m_strssid);
    }

    private boolean connect(WifiConfiguration wifiConfig, boolean bConnect) {
    	boolean bRet = false;

        WifiManager wifiManager = (WifiManager)context.getSystemService(Context.WIFI_SERVICE);

        if (!wifiManager.isWifiEnabled()) {
        	Log.d("Wifi", "Set WifiEnabled.");
        	wifiManager.setWifiEnabled(true);
        }
        //Log.i("WifiControl::connect", "trace1");
        int ID = wifiManager.addNetwork(wifiConfig);
        Log.d("Wifi", "Network ID = " + ID);
        if(bConnect == true)	// #23021
        	wifiManager.enableNetwork(wifiConfig.networkId, false);
        //Log.i("WifiControl::connect", "trace2");
        wifiManager.saveConfiguration();
        //Log.i("WifiControl::connect", "trace3");
        wifiManager.updateNetwork(wifiConfig);
        //Log.i("WifiControl::connect", "trace4");
        if(bConnect == true)
        	bRet = wifiManager.enableNetwork(ID, true);	// ここで接続にいく.従って設定のみ行いたい場合は実行しない
        else bRet = true;	// 接続を行わないときは戻り値をtrueにする
        Log.d("Wifi", "Enable network is " + bRet);

    	return bRet;
    }

    public boolean WaitConnect(String ssid, int timeout_sec) {
    	boolean connected = false;
    	WifiManager wifiManager = (WifiManager)context.getSystemService(Context.WIFI_SERVICE);
    	WifiInfo wifiInfo;

    	int i = 1;
    	while (!connected) {
    		wifiInfo = wifiManager.getConnectionInfo();
    		//if (wifiInfo.getSSID().equals(ssid)) {
    			switch (wifiInfo.getSupplicantState()) {
    			case ASSOCIATED:	// - Association completed.
    				Log.d("Wifi", ssid + ":ASSOCIATED");
    				break;
    			case ASSOCIATING:	// - Trying to associate with an access point.
    				Log.d("Wifi", ssid + ":ASSOCIATING");
    				break;
    			case COMPLETED:		// - All authentication completed.
    				Log.d("Wifi", ssid + ":COMPLETED");
    				connected = true;
    				break;
    			case DISCONNECTED:	// - This state indicates that client is not associated, but is likely to start looking for an access point.
    				Log.d("Wifi", ssid + ":DISCONNECTED");
    				break;
    			case DORMANT:		// - An Android-added state that is reported when a client issues an explicit DISCONNECT command.
    				Log.d("Wifi", ssid + ":DORMANT");
    				break;
    			case FOUR_WAY_HANDSHAKE:	//  - WPA 4-Way Key Handshake in progress.
    				Log.d("Wifi", ssid + ":FOUR_WAY_HANDSHAKE");
    				break;
    			case GROUP_HANDSHAKE:	// - WPA Group Key Handshake in progress.
    				Log.d("Wifi", ssid + ":GROUP_HANDSHAKE");
    				break;
    			case INACTIVE:		// - Inactive state.
    				Log.d("Wifi", ssid + ":INACTIVE");
    				break;
    			case INVALID:		// - A pseudo-state that should normally never be seen. SCANNING - Scanning for a network.
    				Log.d("Wifi", ssid + ":INVALID");
    				break;
    			case UNINITIALIZED:	// - No connection.
    				Log.d("Wifi", ssid + ":UNINITIALIZED");
    				break;
    			}

    		//}

    		if ((i*3000) > (timeout_sec*1000)) {
    			// timeout
    			Log.w("Wifi", ssid + ":Timeout");
    			break;
    		}

    		try {
    			Thread.sleep(3000);
    		}
    		catch (InterruptedException ex) {
    			// ex
    		}
    		i++;
    	}

    	return connected;
    }

    private WifiConfiguration createEapConfig(EAP_TYPE eapType, String ssid, String username, String password, String usercert) {
    	String ENTERPRISE_EAP_PEAP = "PEAP";
    	String ENTERPRISE_EAP_TLS = "TLS";
        String ENTERPRISE_CLIENT_CERT = "";//"keystore://USRCERT_CertificateName";
        String ENTERPRISE_PRIV_KEY = "";//"keystore://USRPKEY_CertificateName";
        String ENTERPRISE_PHASE2 = "";
        String ENTERPRISE_ANON_IDENT = "";
        String ENTERPRISE_CA_CERT = "";

        WifiConfiguration wifiConfig = new WifiConfiguration();

        /*AP Name*/
        wifiConfig.SSID = "\"" + ssid + "\"";

        /*Priority*/
        wifiConfig.priority = 40;

        /*Enable Hidden SSID*/
 //       wifiConfig.hiddenSSID = true;

        /*Key Mgmnt*/
        wifiConfig.allowedKeyManagement.clear();
        wifiConfig.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.IEEE8021X);
        wifiConfig.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_EAP);

        /*Group Ciphers*/
        wifiConfig.allowedGroupCiphers.clear();
        wifiConfig.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
        wifiConfig.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
        wifiConfig.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP104);
        wifiConfig.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);

        /*Pairwise ciphers*/
        wifiConfig.allowedPairwiseCiphers.clear();
        wifiConfig.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
        wifiConfig.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);

        /*Protocols*/
        wifiConfig.allowedProtocols.clear();
        wifiConfig.allowedProtocols.set(WifiConfiguration.Protocol.RSN);
        wifiConfig.allowedProtocols.set(WifiConfiguration.Protocol.WPA);

        // Enterprise Settings
        // Reflection magic here too, need access to non-public APIs
        try {
            // Let the magic start
            Class[] wcClasses = WifiConfiguration.class.getClasses();
            // null for overzealous java compiler
            Class wcEnterpriseField = null;

            for (Class wcClass : wcClasses)
                if (wcClass.getName().equals(INT_ENTERPRISEFIELD_NAME))
                {
                    wcEnterpriseField = wcClass;
                    break;
                }
            boolean noEnterpriseFieldType = false;
            if(wcEnterpriseField == null)
                noEnterpriseFieldType = true; // Cupcake/Donut access enterprise settings directly

            Field wcefAnonymousId = null, wcefCaCert = null, wcefClientCert = null, wcefEap = null, wcefIdentity = null, wcefPassword = null, wcefPhase2 = null, wcefPrivateKey = null;
            Field[] wcefFields = WifiConfiguration.class.getFields();
            // Dispatching Field vars
            for (Field wcefField : wcefFields)
            {
                if (wcefField.getName().equals(INT_ANONYMOUS_IDENTITY))
                    wcefAnonymousId = wcefField;
                else if (wcefField.getName().equals(INT_CA_CERT))
                    wcefCaCert = wcefField;
                else if (wcefField.getName().equals(INT_CLIENT_CERT))
                    wcefClientCert = wcefField;
                else if (wcefField.getName().equals(INT_EAP))
                    wcefEap = wcefField;
                else if (wcefField.getName().equals(INT_IDENTITY))
                    wcefIdentity = wcefField;
                else if (wcefField.getName().equals(INT_PASSWORD))
                    wcefPassword = wcefField;
                else if (wcefField.getName().equals(INT_PHASE2))
                    wcefPhase2 = wcefField;
                else if (wcefField.getName().equals(INT_PRIVATE_KEY))
                    wcefPrivateKey = wcefField;
            }

            Method wcefSetValue = null;
            if(!noEnterpriseFieldType){
            for(Method m: wcEnterpriseField.getMethods())
                //System.out.println(m.getName());
                if(m.getName().trim().equals("setValue"))
                    wcefSetValue = m;
            }

            /*EAP Method*/
            if (!noEnterpriseFieldType) {
            	if (eapType == EAP_TYPE.PEAP) {
            		wcefSetValue.invoke(wcefEap.get(wifiConfig), ENTERPRISE_EAP_PEAP);
            	}
            	else {
            		wcefSetValue.invoke(wcefEap.get(wifiConfig), ENTERPRISE_EAP_TLS);
            		ENTERPRISE_CLIENT_CERT = "keystore://USRCERT_" + usercert;
            		ENTERPRISE_PRIV_KEY = "keystore://USRPKEY_" + usercert;
            		ENTERPRISE_CA_CERT = "keystore://CACERT_" /* + cacert*/;	// CA証明書
            	}
            }

            /*EAP Phase 2 Authentication*/
            if(!noEnterpriseFieldType)
                wcefSetValue.invoke(wcefPhase2.get(wifiConfig), ENTERPRISE_PHASE2);

            /*EAP Anonymous Identity*/
            if(!noEnterpriseFieldType)
                wcefSetValue.invoke(wcefAnonymousId.get(wifiConfig), ENTERPRISE_ANON_IDENT);

            /*EAP CA Certificate*/
            if(!noEnterpriseFieldType)
                wcefSetValue.invoke(wcefCaCert.get(wifiConfig), ENTERPRISE_CA_CERT);

            /*EAP Private key*/
            if(!noEnterpriseFieldType)
                wcefSetValue.invoke(wcefPrivateKey.get(wifiConfig), ENTERPRISE_PRIV_KEY);

            /*EAP Identity*/
            if(!noEnterpriseFieldType)
                wcefSetValue.invoke(wcefIdentity.get(wifiConfig), username);

            /*EAP Password*/
            if(!noEnterpriseFieldType)
                wcefSetValue.invoke(wcefPassword.get(wifiConfig), password);

            /*EAp Client certificate*/
            if(!noEnterpriseFieldType)
                wcefSetValue.invoke(wcefClientCert.get(wifiConfig), ENTERPRISE_CLIENT_CERT);

        }
        catch (Exception e) {
            e.printStackTrace();
            return null;
        }

        return wifiConfig;
    }


    // http://tokotoko.gr.jp/modules/boss/index.php?caldate=2010-12-08
    private WifiConfiguration createPskConfig(WifiItem wifiitem) {
    	String ENTERPRISE_EAP_PEAP = "PEAP";
    	String ENTERPRISE_EAP_TLS = "TLS";
    	String ENTERPRISE_EAP_TTLS = "TTLS";
    	String ENTERPRISE_EAP_LEAP = "LEAP";
        String ENTERPRISE_CLIENT_CERT = "";//"keystore://USRCERT_CertificateName";
        String ENTERPRISE_PRIV_KEY = "";//"keystore://USRPKEY_CertificateName";
    	String ENTERPRISE_PHASE2 = "";
    	String ENTERPRISE_ANON_IDENT = "";
    	String ENTERPRISE_CA_CERT = "";


    	WifiConfiguration wifiConfig = new WifiConfiguration();
    	/*AP Name*/
        wifiConfig.SSID = "\"" + wifiitem.GetSSID() + "\"";

        /*Priority*/
        wifiConfig.priority = 40;

        /*Enable Hidden SSID*/
        wifiConfig.hiddenSSID = wifiitem.GetHidden();//Hidden;

        /*Key Mgmnt*/
        wifiConfig.allowedKeyManagement.clear();
        if(wifiitem.GetEncType().equalsIgnoreCase("WPA")) {
	        // IEEE 802.1X using EAP authentication and (optionally) dynamically generated WEP keys.
        	// na-prj ticket10891 Android4.4.x以降,IEEE8021Xを指定しているとWPA/WPA2 PSKのSSID設定に失敗する
	     //   wifiConfig.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.IEEE8021X);

	        // <==== WPA/PSKかWPA/EAPのどちらを選択するかで振り分ける必要がある.
	        // WPA using EAP authentication.
	        //wifiConfig.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_EAP);
	        // WPA pre-shared key (requires preSharedKey to be specified).
	        wifiConfig.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
	        // WPA/PSKかWPA/EAPのどちらを選択するかで振り分ける必要がある. ====>
	        /*pre-SharedKey(PSK) MDMで追加*/
	        wifiConfig.preSharedKey = "\"" + wifiitem.GetWifipass() + "\"";

        } else if(wifiitem.GetEncType().equalsIgnoreCase("WEP")) {
	        // WPA is not used; plaintext or static WEP could be used.
	        wifiConfig.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
        } else if(wifiitem.GetEncType().equalsIgnoreCase("NONE")) {
	        // WPA is not used; plaintext or static WEP could be used.
	        wifiConfig.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
        }

        /*Group Ciphers*/
//        if(m_strenctype.equalsIgnoreCase("Any")) {
	        wifiConfig.allowedGroupCiphers.clear();
	        wifiConfig.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
	        wifiConfig.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
	        wifiConfig.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP104);
	        wifiConfig.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);
 /*       } else if(m_strenctype.equalsIgnoreCase("WEP")) {
        	wifiConfig.allowedGroupCiphers.clear();
	        wifiConfig.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP104);
	        wifiConfig.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);
        } else if(m_strenctype.equalsIgnoreCase("WPA")) {
        	// WPA_PSKには必要ないみたいだが...
        	wifiConfig.allowedGroupCiphers.clear();
	        wifiConfig.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
	        wifiConfig.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
        }*/

        /*Pairwise ciphers*/
        wifiConfig.allowedPairwiseCiphers.clear();
        wifiConfig.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
        wifiConfig.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);

        /*Protocols*/
        wifiConfig.allowedProtocols.clear();
        wifiConfig.allowedProtocols.set(WifiConfiguration.Protocol.RSN);	// WPA2
        wifiConfig.allowedProtocols.set(WifiConfiguration.Protocol.WPA);	// WPA

        // Enterprise Settings
        // Reflection magic here too, need access to non-public APIs
        try {
            // Let the magic start
            Class[] wcClasses = WifiConfiguration.class.getClasses();
            // null for overzealous java compiler
            Class wcEnterpriseField = null;

            for (Class wcClass : wcClasses)
                if (wcClass.getName().equals(INT_ENTERPRISEFIELD_NAME))
                {
                    wcEnterpriseField = wcClass;
                    break;
                }
            boolean noEnterpriseFieldType = false;
            if(wcEnterpriseField == null)
                noEnterpriseFieldType = true; // Cupcake/Donut access enterprise settings directly

            Field wcefAnonymousId = null, wcefCaCert = null, wcefClientCert = null, wcefEap = null, wcefIdentity = null, wcefPassword = null, wcefPhase2 = null, wcefPrivateKey = null;
            Field[] wcefFields = WifiConfiguration.class.getFields();
            // Dispatching Field vars
            for (Field wcefField : wcefFields)
            {
                if (wcefField.getName().equals(INT_ANONYMOUS_IDENTITY))
                    wcefAnonymousId = wcefField;
                else if (wcefField.getName().equals(INT_CA_CERT))
                    wcefCaCert = wcefField;
                else if (wcefField.getName().equals(INT_CLIENT_CERT))
                    wcefClientCert = wcefField;
                else if (wcefField.getName().equals(INT_EAP))
                    wcefEap = wcefField;
                else if (wcefField.getName().equals(INT_IDENTITY))
                    wcefIdentity = wcefField;
                else if (wcefField.getName().equals(INT_PASSWORD))
                    wcefPassword = wcefField;
                else if (wcefField.getName().equals(INT_PHASE2))
                    wcefPhase2 = wcefField;
                else if (wcefField.getName().equals(INT_PRIVATE_KEY))
                    wcefPrivateKey = wcefField;
            }

            Method wcefSetValue = null;
            if(!noEnterpriseFieldType){
            for(Method m: wcEnterpriseField.getMethods())
                //System.out.println(m.getName());
                if(m.getName().trim().equals("setValue"))
                    wcefSetValue = m;
            }

            /*EAP Method*/
 /*           if (!noEnterpriseFieldType) {
            	if (m_numEaptype == PEAP) {
            		Log.i("WifiControl", "createEapConfig2 Selected PEAP.");
            		wcefSetValue.invoke(wcefEap.get(wifiConfig), ENTERPRISE_EAP_PEAP);
            	}
	           	else if(m_numEaptype == TLS){
	           		Log.i("WifiControl", "createEapConfig2 Selected TLS.");
	           		wcefSetValue.invoke(wcefEap.get(wifiConfig), ENTERPRISE_EAP_TLS);
	           	//	ENTERPRISE_CLIENT_CERT = "keystore://USRCERT_" + usercert;	// user certificateを渡す手段がない
	           	//	ENTERPRISE_PRIV_KEY = "keystore://USRPKEY_" + usercert;
	           	}
	           	else if(m_numEaptype == TTLS){
	           		Log.i("WifiControl", "createEapConfig2 Selected TTLS.");
	           		wcefSetValue.invoke(wcefEap.get(wifiConfig), ENTERPRISE_EAP_TTLS);
	           	}
	           	else if(m_numEaptype == LEAP){
	           		Log.i("WifiControl", "createEapConfig2 Selected LEAP.");
	           		wcefSetValue.invoke(wcefEap.get(wifiConfig), ENTERPRISE_EAP_LEAP);
	           	}

 //           	if (eapType == EAP_TYPE.PEAP) {
 //           		wcefSetValue.invoke(wcefEap.get(wifiConfig), ENTERPRISE_EAP_PEAP);
 //           	}
 //           	else {
 //           		wcefSetValue.invoke(wcefEap.get(wifiConfig), ENTERPRISE_EAP_TLS);
 //           		ENTERPRISE_CLIENT_CERT = "keystore://USRCERT_" + usercert;
 //           		ENTERPRISE_PRIV_KEY = "keystore://USRPKEY_" + usercert;
 //           	}
            }
*/
            /*EAP Phase 2 Authentication*/
            // 例：phase2="auth=PAP" 形式
/*            ENTERPRISE_PHASE2 = "auth=" + m_strphase2auth;
            if(!noEnterpriseFieldType)
                wcefSetValue.invoke(wcefPhase2.get(wifiConfig), ENTERPRISE_PHASE2);
*/
            /*EAP Anonymous Identity*/
/*            if(!noEnterpriseFieldType)
                wcefSetValue.invoke(wcefAnonymousId.get(wifiConfig), ENTERPRISE_ANON_IDENT);
*/
            /*EAP CA Certificate*/
/*            if(!noEnterpriseFieldType)
                wcefSetValue.invoke(wcefCaCert.get(wifiConfig), ENTERPRISE_CA_CERT);
*/
            /*EAP Private key*/
/*           if(!noEnterpriseFieldType)
                wcefSetValue.invoke(wcefPrivateKey.get(wifiConfig), ENTERPRISE_PRIV_KEY);
*/
            /*EAP Identity*/
/*            if(!noEnterpriseFieldType)
                wcefSetValue.invoke(wcefIdentity.get(wifiConfig), m_stridentity);
*/
            /*EAP Password*/
/*            if(!noEnterpriseFieldType)
                wcefSetValue.invoke(wcefPassword.get(wifiConfig), m_strwifipass);
*/
            /*EAp Client certificate*/
/*            if(!noEnterpriseFieldType)
                wcefSetValue.invoke(wcefClientCert.get(wifiConfig), ENTERPRISE_CLIENT_CERT);
*/
        }
        catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    	return wifiConfig;
    }

    private WifiConfiguration createEapConfigEAP(WifiItem wifiitem) {
    	String ENTERPRISE_EAP_PEAP = "PEAP";
    	String ENTERPRISE_EAP_TLS = "TLS";
    	String ENTERPRISE_EAP_TTLS = "TTLS";
    	String ENTERPRISE_EAP_LEAP = "LEAP";
        String ENTERPRISE_CLIENT_CERT = "";//"keystore://USRCERT_CertificateName";
        String ENTERPRISE_PRIV_KEY = "";//"keystore://USRPKEY_CertificateName";
    	String ENTERPRISE_PHASE2 = "";
    	String ENTERPRISE_ANON_IDENT = "";
    	String ENTERPRISE_CA_CERT = "";
    	String ENTERPRISE_PRIV_KEY_ID = "";


    	WifiConfiguration wifiConfig = new WifiConfiguration();
    	/*AP Name*/
        wifiConfig.SSID = "\"" + wifiitem.GetSSID() + "\"";

        /*Priority*/
        wifiConfig.priority = 40;

        /*Enable Hidden SSID*/
        wifiConfig.hiddenSSID = wifiitem.GetHidden();//Hidden;

        /*Key Mgmnt*/
        wifiConfig.allowedKeyManagement.clear();
        if(wifiitem.GetEncType().equalsIgnoreCase("WPA")) {
	        // IEEE 802.1X using EAP authentication and (optionally) dynamically generated WEP keys.
        	// na-prj ticket10891 対応でEAP側も修正しておく
	        //wifiConfig.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.IEEE8021X);

	        // <==== WPA/PSKかWPA/EAPのどちらを選択するかで振り分ける必要がある.
	        // WPA using EAP authentication.
	        wifiConfig.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_EAP);
	        // WPA pre-shared key (requires preSharedKey to be specified).
	        //wifiConfig.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
	        // WPA/PSKかWPA/EAPのどちらを選択するかで振り分ける必要がある. ====>
	        /*pre-SharedKey(PSK) MDMで追加*/
	        //wifiConfig.preSharedKey = "\"" + wifiitem.GetWifipass() + "\"";

        } else if(wifiitem.GetEncType().equalsIgnoreCase("WEP")) {
	        // WPA is not used; plaintext or static WEP could be used.
	        wifiConfig.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
        } else if(wifiitem.GetEncType().equalsIgnoreCase("NONE")) {
	        // WPA is not used; plaintext or static WEP could be used.
	        wifiConfig.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
        }

        /*Group Ciphers*/
//        if(m_strenctype.equalsIgnoreCase("Any")) {
	        wifiConfig.allowedGroupCiphers.clear();
	        wifiConfig.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
	        wifiConfig.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
	        wifiConfig.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP104);
	        wifiConfig.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);
 /*       } else if(m_strenctype.equalsIgnoreCase("WEP")) {
        	wifiConfig.allowedGroupCiphers.clear();
	        wifiConfig.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP104);
	        wifiConfig.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);
        } else if(m_strenctype.equalsIgnoreCase("WPA")) {
        	// WPA_PSKには必要ないみたいだが...
        	wifiConfig.allowedGroupCiphers.clear();
	        wifiConfig.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
	        wifiConfig.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
        }*/

        /*Pairwise ciphers*/
        wifiConfig.allowedPairwiseCiphers.clear();
        wifiConfig.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
        wifiConfig.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);

        /*Protocols*/
        wifiConfig.allowedProtocols.clear();
        wifiConfig.allowedProtocols.set(WifiConfiguration.Protocol.RSN);	// WPA2
        wifiConfig.allowedProtocols.set(WifiConfiguration.Protocol.WPA);	// WPA

        // Enterprise Settings
        // Reflection magic here too, need access to non-public APIs
        try {
            // Let the magic start
            Class[] wcClasses = WifiConfiguration.class.getClasses();
            // null for overzealous java compiler
            Class wcEnterpriseField = null;

            for (Class wcClass : wcClasses)
                if (wcClass.getName().equals(INT_ENTERPRISEFIELD_NAME))
                {
                    wcEnterpriseField = wcClass;
                    break;
                }
            boolean noEnterpriseFieldType = false;
            if(wcEnterpriseField == null)
                noEnterpriseFieldType = true; // Cupcake/Donut access enterprise settings directly

            Field wcefAnonymousId = null, wcefCaCert = null, wcefClientCert = null, wcefEap = null, wcefIdentity = null, wcefPassword = null, wcefPhase2 = null, wcefPrivateKey = null
            		,wcefPrivateKeyID = null;
            Field[] wcefFields = WifiConfiguration.class.getFields();
            // Dispatching Field vars
            for (Field wcefField : wcefFields)
            {
            	Log.d("WifiControl : Field", wcefField.getName());
                if (wcefField.getName().equals(INT_ANONYMOUS_IDENTITY))
                    wcefAnonymousId = wcefField;
                else if (wcefField.getName().equals(INT_CA_CERT))
                    wcefCaCert = wcefField;
                else if (wcefField.getName().equals(INT_CLIENT_CERT))
                    wcefClientCert = wcefField;
                else if (wcefField.getName().equals(INT_EAP))
                    wcefEap = wcefField;
                else if (wcefField.getName().equals(INT_IDENTITY))
                    wcefIdentity = wcefField;
                else if (wcefField.getName().equals(INT_PASSWORD))
                    wcefPassword = wcefField;
                else if (wcefField.getName().equals(INT_PHASE2))
                    wcefPhase2 = wcefField;
                else if (wcefField.getName().equals(INT_PRIVATE_KEY))
                    wcefPrivateKey = wcefField;
                else if (wcefField.getName().equals(INT_PRIVATE_KEY_ID))
                	wcefPrivateKeyID = wcefField;
            }

            Method wcefSetValue = null;
            if(!noEnterpriseFieldType){
            for(Method m: wcEnterpriseField.getMethods())
                //System.out.println(m.getName());
                if(m.getName().trim().equals("setValue"))
                    wcefSetValue = m;
            }

            /*EAP Method*/
            if (!noEnterpriseFieldType) {
            	if (wifiitem.GetEAPType() == PEAP) {
            		Log.i("WifiControl", "createEapConfigEAP Selected PEAP.");
            		wcefSetValue.invoke(wcefEap.get(wifiConfig), ENTERPRISE_EAP_PEAP);
            	}
	           	else if(wifiitem.GetEAPType()  == TLS){
	           		Log.i("WifiControl", "createEapConfigEAP Selected TLS.");
	           		Log.i("WifiControl", "CA CERT = " + m_strCaCert);
	           		Log.i("WifiControl", "USER CERT = " + m_strUserCert);
	           		wcefSetValue.invoke(wcefEap.get(wifiConfig), ENTERPRISE_EAP_TLS);
	           		ENTERPRISE_CLIENT_CERT = "keystore://USRCERT_" + m_strUserCert;	// user certificateを渡す手段がない
            		ENTERPRISE_PRIV_KEY = "keystore://USRPKEY_" + m_strUserCert;//m_strUserCert;
            		ENTERPRISE_CA_CERT = "keystore://CACERT_"  + m_strCaCert;	// CA証明書
            		ENTERPRISE_PRIV_KEY_ID = "USRPKEY_" + m_strUserCert;		// ユーザー証明書のキーID
	           	}
	           	else if(wifiitem.GetEAPType() == TTLS){
	           		Log.i("WifiControl", "createEapConfig2 Selected TTLS.");
	           		wcefSetValue.invoke(wcefEap.get(wifiConfig), ENTERPRISE_EAP_TTLS);
	           	}
	           	else if(wifiitem.GetEAPType() == LEAP){
	           		Log.i("WifiControl", "createEapConfig2 Selected LEAP.");
	           		wcefSetValue.invoke(wcefEap.get(wifiConfig), ENTERPRISE_EAP_LEAP);
	           	}

 //           	if (eapType == EAP_TYPE.PEAP) {
 //           		wcefSetValue.invoke(wcefEap.get(wifiConfig), ENTERPRISE_EAP_PEAP);
 //           	}
 //           	else {
 //           		wcefSetValue.invoke(wcefEap.get(wifiConfig), ENTERPRISE_EAP_TLS);
 //           		ENTERPRISE_CLIENT_CERT = "keystore://USRCERT_" + usercert;
 //           		ENTERPRISE_PRIV_KEY = "keystore://USRPKEY_" + usercert;
 //           	}*/
            }

            /*EAP Phase 2 Authentication*/
            // 例：phase2="auth=PAP" 形式
            ENTERPRISE_PHASE2 = "auth=" + wifiitem.GetPhase2Auth();
            if((!noEnterpriseFieldType) && (wifiitem.GetPhase2Auth().length() > 0))
                wcefSetValue.invoke(wcefPhase2.get(wifiConfig), ENTERPRISE_PHASE2);

            /*EAP Anonymous Identity*/
/*            if(!noEnterpriseFieldType)
                wcefSetValue.invoke(wcefAnonymousId.get(wifiConfig), ENTERPRISE_ANON_IDENT);
*/
            /*EAP CA Certificate*/
            if(!noEnterpriseFieldType)
                wcefSetValue.invoke(wcefCaCert.get(wifiConfig), ENTERPRISE_CA_CERT);

            /*EAP Private key*/
           if((!noEnterpriseFieldType) && (wcefPrivateKey != null))
                wcefSetValue.invoke(wcefPrivateKey.get(wifiConfig), ENTERPRISE_PRIV_KEY);

           if((!noEnterpriseFieldType) && (wcefPrivateKeyID != null))
        	   wcefSetValue.invoke(wcefPrivateKeyID.get(wifiConfig), ENTERPRISE_PRIV_KEY_ID);

            /*EAP Identity*/
            if(!noEnterpriseFieldType) {
                wcefSetValue.invoke(wcefIdentity.get(wifiConfig), /*wifiitem.GetIdentity()*/wifiitem.GetUserName());
                Log.i("WifiControl", "EAP UserName::" + wifiitem.GetUserName());
            }

            /*EAP Password*/
            if(!noEnterpriseFieldType) {
                wcefSetValue.invoke(wcefPassword.get(wifiConfig), wifiitem.GetUserPass());
                Log.i("WifiControl", "EAP Password::" + wifiitem.GetUserPass());
            }

            /*EAp Client certificate*/
            if(!noEnterpriseFieldType)
                wcefSetValue.invoke(wcefClientCert.get(wifiConfig), ENTERPRISE_CLIENT_CERT);

        }
        catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    	return wifiConfig;
    }

    @TargetApi(18)
	private WifiConfiguration createEapConfigEAPover43(WifiItem wifiitem) {
    	String ENTERPRISE_EAP_PEAP = "PEAP";
    	String ENTERPRISE_EAP_TLS = "TLS";
    	String ENTERPRISE_EAP_TTLS = "TTLS";
    	String ENTERPRISE_EAP_LEAP = "LEAP";
        String ENTERPRISE_CLIENT_CERT = "";//"keystore://USRCERT_CertificateName";
        String ENTERPRISE_PRIV_KEY = "";//"keystore://USRPKEY_CertificateName";
    	String ENTERPRISE_PHASE2 = "";
    	String ENTERPRISE_ANON_IDENT = "";
    	String ENTERPRISE_CA_CERT = "";
    	String ENTERPRISE_PRIV_KEY_ID = "";
    	X509Certificate ENTERPRISE_CERTIFICATE_CA_CERT = null;


    	WifiConfiguration wifiConfig = new WifiConfiguration();
    	WifiEnterpriseConfig mEnterpriseConfig = new WifiEnterpriseConfig();

    	/*AP Name*/
        wifiConfig.SSID = "\"" + wifiitem.GetSSID() + "\"";

        /*Priority*/
        wifiConfig.priority = 40;

        /*Enable Hidden SSID*/
        wifiConfig.hiddenSSID = wifiitem.GetHidden();//Hidden;

        /*Key Mgmnt*/
        wifiConfig.allowedKeyManagement.clear();
        if(wifiitem.GetEncType().equalsIgnoreCase("WPA")) {
	        // IEEE 802.1X using EAP authentication and (optionally) dynamically generated WEP keys.
	        wifiConfig.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.IEEE8021X);

	        // <==== WPA/PSKかWPA/EAPのどちらを選択するかで振り分ける必要がある.
	        // WPA using EAP authentication.
	        wifiConfig.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_EAP);
	        // WPA pre-shared key (requires preSharedKey to be specified).
	        //wifiConfig.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
	        // WPA/PSKかWPA/EAPのどちらを選択するかで振り分ける必要がある. ====>
	        /*pre-SharedKey(PSK) MDMで追加*/
	        //wifiConfig.preSharedKey = "\"" + wifiitem.GetWifipass() + "\"";

        } else if(wifiitem.GetEncType().equalsIgnoreCase("WEP")) {
	        // WPA is not used; plaintext or static WEP could be used.
	        wifiConfig.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
        } else if(wifiitem.GetEncType().equalsIgnoreCase("NONE")) {
	        // WPA is not used; plaintext or static WEP could be used.
	        wifiConfig.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
        }

        /*Group Ciphers*/
//        if(m_strenctype.equalsIgnoreCase("Any")) {
	        wifiConfig.allowedGroupCiphers.clear();
	        wifiConfig.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
	        wifiConfig.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
	        wifiConfig.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP104);
	        wifiConfig.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);
 /*       } else if(m_strenctype.equalsIgnoreCase("WEP")) {
        	wifiConfig.allowedGroupCiphers.clear();
	        wifiConfig.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP104);
	        wifiConfig.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);
        } else if(m_strenctype.equalsIgnoreCase("WPA")) {
        	// WPA_PSKには必要ないみたいだが...
        	wifiConfig.allowedGroupCiphers.clear();
	        wifiConfig.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
	        wifiConfig.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
        }*/

        /*Pairwise ciphers*/
        wifiConfig.allowedPairwiseCiphers.clear();
        wifiConfig.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
        wifiConfig.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);

        /*Protocols*/
        wifiConfig.allowedProtocols.clear();
        wifiConfig.allowedProtocols.set(WifiConfiguration.Protocol.RSN);	// WPA2
        wifiConfig.allowedProtocols.set(WifiConfiguration.Protocol.WPA);	// WPA

        // Enterprise Settings
        // Reflection magic here too, need access to non-public APIs
        try {
            // Let the magic start
            Class[] wcClasses = WifiConfiguration.class.getClasses();
            // null for overzealous java compiler
            Class wcEnterpriseField = null;

            for (Class wcClass : wcClasses)
                if (wcClass.getName().equals(INT_ENTERPRISEFIELD_NAME))
                {
                    wcEnterpriseField = wcClass;
                    break;
                }
            boolean noEnterpriseFieldType = false;
            if(wcEnterpriseField == null)
                noEnterpriseFieldType = true; // Cupcake/Donut access enterprise settings directly

            Field wcefAnonymousId = null, wcefCaCert = null, wcefClientCert = null, wcefEap = null, wcefIdentity = null, wcefPassword = null, wcefPhase2 = null, wcefPrivateKey = null
            		,wcefPrivateKeyID = null;
            Field[] wcefFields = WifiConfiguration.class.getFields();
            // Dispatching Field vars
            for (Field wcefField : wcefFields)
            {
            	Log.d("WifiControl : Field", wcefField.getName());
                if (wcefField.getName().equals(INT_ANONYMOUS_IDENTITY))
                    wcefAnonymousId = wcefField;
                else if (wcefField.getName().equals(INT_CA_CERT))
                    wcefCaCert = wcefField;
                else if (wcefField.getName().equals(INT_CLIENT_CERT))
                    wcefClientCert = wcefField;
                else if (wcefField.getName().equals(INT_EAP))
                    wcefEap = wcefField;
                else if (wcefField.getName().equals(INT_IDENTITY))
                    wcefIdentity = wcefField;
                else if (wcefField.getName().equals(INT_PASSWORD))
                    wcefPassword = wcefField;
                else if (wcefField.getName().equals(INT_PHASE2))
                    wcefPhase2 = wcefField;
                else if (wcefField.getName().equals(INT_PRIVATE_KEY))
                    wcefPrivateKey = wcefField;
                else if (wcefField.getName().equals(INT_PRIVATE_KEY_ID))
                	wcefPrivateKeyID = wcefField;
            }

            Method wcefSetValue = null;
            if(!noEnterpriseFieldType){
            for(Method m: wcEnterpriseField.getMethods())
                //System.out.println(m.getName());
                if(m.getName().trim().equals("setValue"))
                    wcefSetValue = m;
            }

            /*EAP Method*/
           	if (wifiitem.GetEAPType() == PEAP) {
           		Log.i("WifiControl", "createEapConfigEAP Selected PEAP.");
           		mEnterpriseConfig.setEapMethod(WifiEnterpriseConfig.Eap.PEAP);
           	}
           	else if(wifiitem.GetEAPType()  == TLS){
           		Log.i("WifiControl", "createEapConfigEAP Selected TLS.");
           		Log.i("WifiControl", "CA CERT = " + m_strCaCert);
           		Log.i("WifiControl", "USER CERT = " + m_strUserCert);
           		mEnterpriseConfig.setEapMethod(WifiEnterpriseConfig.Eap.TLS);


           		ENTERPRISE_CLIENT_CERT = "keystore://USRCERT_" + m_strUserCert;	// user certificateを渡す手段がない
           		ENTERPRISE_PRIV_KEY = "keystore://USRPKEY_" + m_strUserCert;//m_strUserCert;
           		ENTERPRISE_CA_CERT = "keystore://CACERT_"  + m_strCaCert;	// CA証明書
           		ENTERPRISE_PRIV_KEY_ID = "USRPKEY_" + m_strUserCert;		// ユーザー証明書のキーID
           	}
           	else if(wifiitem.GetEAPType() == TTLS){
           		Log.i("WifiControl", "createEapConfig2 Selected TTLS.");
           		mEnterpriseConfig.setEapMethod(WifiEnterpriseConfig.Eap.TTLS);
           	}
           	else if(wifiitem.GetEAPType() == LEAP){
           		Log.i("WifiControl", "createEapConfig2 Selected LEAP.");
           		wcefSetValue.invoke(wcefEap.get(wifiConfig), ENTERPRISE_EAP_LEAP);
           	}



            /*EAP Phase 2 Authentication*/
            // 例：phase2="auth=PAP" 形式
            int n_phase2 = WifiEnterpriseConfig.Phase2.NONE;
            ENTERPRISE_PHASE2 = wifiitem.GetPhase2Auth();

            if (ENTERPRISE_PHASE2.equalsIgnoreCase("PAP")) n_phase2 = WifiEnterpriseConfig.Phase2.PAP;
            else if (ENTERPRISE_PHASE2.equalsIgnoreCase("MSCHAP")) n_phase2 = WifiEnterpriseConfig.Phase2.MSCHAP;
            else if (ENTERPRISE_PHASE2.equalsIgnoreCase("MSCHAPV2")) n_phase2 = WifiEnterpriseConfig.Phase2.MSCHAPV2;
            else if (ENTERPRISE_PHASE2.equalsIgnoreCase("GTC")) n_phase2 = WifiEnterpriseConfig.Phase2.GTC;

            mEnterpriseConfig.setPhase2Method(n_phase2);



            /*EAP Identity*/
           mEnterpriseConfig.setIdentity(wifiitem.GetUserName());

            /*EAP Password*/
           mEnterpriseConfig.setPassword(wifiitem.GetUserPass());

            /*EAP Anonymous Identity*/
/*            if(!noEnterpriseFieldType)
                wcefSetValue.invoke(wcefAnonymousId.get(wifiConfig), ENTERPRISE_ANON_IDENT);
*/

           ////////////////////////////////////////////////////////////
           // <=  Certificate 設定
           /////////////////////////
           try {
	   			KeyStore ks = KeyStore.getInstance("AndroidCAStore");
	   			ks.load(null, null);
	   			Enumeration aliases = ks.aliases();
	   			while (aliases.hasMoreElements()) {
	   			    String alias = (String) aliases.nextElement();
	   			    Log.d("CertLoginActivity::PrintViewKeyStore2", "aliase: " + alias);
	   			    if(alias.indexOf("user") == -1) {
	   			    	continue;
	   			    }

	   			    X509Certificate cert = (X509Certificate) ks.getCertificate(alias);
	   			    if(cert.getSubjectDN().getName().indexOf("Soliton Systems") != -1) {
	   			    	ENTERPRISE_CERTIFICATE_CA_CERT = cert;
	   			    Log.d("CertLoginActivity::PrintViewKeyStore2", "Subject DN: " +
	   			    		cert.getSubjectDN().getName());
	   			    Log.d("CertLoginActivity::PrintViewKeyStore2", "Issuer DN: " +
	   			    		cert.getIssuerDN().getName());
	   			    }
	   			}

	   		}catch (KeyStoreException e1) {
	   			// TODO 自動生成された catch ブロック
	   			e1.printStackTrace();
	   		}catch (NoSuchAlgorithmException e1) {
	   			// TODO 自動生成された catch ブロック
	   			e1.printStackTrace();
	   		} catch (CertificateException e1) {
	   			// TODO 自動生成された catch ブロック
	   			e1.printStackTrace();
	   		} catch (IOException e1) {
	   			// TODO 自動生成された catch ブロック
	   			e1.printStackTrace();
	   		}

            /*EAP CA Certificate*/
           if ( ENTERPRISE_CERTIFICATE_CA_CERT != null)
        	   mEnterpriseConfig.setCaCertificate(ENTERPRISE_CERTIFICATE_CA_CERT);

            if(!noEnterpriseFieldType)
                wcefSetValue.invoke(wcefCaCert.get(wifiConfig), ENTERPRISE_CA_CERT);

            /*EAP Private key*/
           if((!noEnterpriseFieldType) && (wcefPrivateKey != null))
                wcefSetValue.invoke(wcefPrivateKey.get(wifiConfig), ENTERPRISE_PRIV_KEY);

           if((!noEnterpriseFieldType) && (wcefPrivateKeyID != null))
        	   wcefSetValue.invoke(wcefPrivateKeyID.get(wifiConfig), ENTERPRISE_PRIV_KEY_ID);


            /*EAp Client certificate*/
            if(!noEnterpriseFieldType)
                wcefSetValue.invoke(wcefClientCert.get(wifiConfig), ENTERPRISE_CLIENT_CERT);


            wifiConfig.enterpriseConfig = mEnterpriseConfig;



        }
        catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    	return wifiConfig;
    }

    // ファイル出力
 	public void WriteWifiInfo(String str_outputfile) {
 		String retmsg = "";

 		XmlSerializer serializer = Xml.newSerializer();
 	    StringWriter writer = new StringWriter();
 	    try {
 	        serializer.setOutput(writer);	// XmlSerializerとStringWriterの関連付け..
 	        serializer.startDocument("UTF-8", true);
 	        serializer.startTag("", "plist");
 	        serializer.attribute("", "version", "1.0");
	 	    for(int i = 0; m_pWifiItem.size() > i; i++){
				WifiItem wifiitem = m_pWifiItem.get(i);

				serializer.startTag("", "dict");
				// PayloadType
				SetParameter4Output(serializer, StringList.m_str_payloadtype, StringList.m_str_wifi_profile);
				/* Wifi情報はプロファイル削除で使用するので ssidのみで大丈夫なはず */
	 	        // SSID名
	 	        SetParameter4Output(serializer, StringList.m_str_ssid, wifiitem.GetSSID());

				serializer.endTag("", "dict");
			}

 	        serializer.endTag("", "plist");
 	        serializer.endDocument();

 	        // アウトプットをストリング型へ変換する
 	        retmsg = writer.toString();
 	    } catch (IOException e){
 			Log.e("WriteRestrictionsInfo::IOException ", e.toString());
 		}


 	    byte[] byArrData = retmsg.getBytes();
 		OutputStream outputStreamObj=null;

 		try {
 			//Context ctx = new Context();
 			//Contextから出力ストリーム取得
 			outputStreamObj=context.openFileOutput(/*StringList.m_strWifiOutputFile*/str_outputfile, Context.MODE_PRIVATE);
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

 	// ファイル読み込み
 	public void ReadAndSetWifiInfo(String str_outputfile) {
 		byte[] byArrData_read = null;
 		int iSize;
 		byte[] byArrTempData=new byte[4096];
 		InputStream inputStreamObj=null;
 		ByteArrayOutputStream byteArrayOutputStreamObj=null;

 		try {
 			//Contextから入力ストリームの取得
 			inputStreamObj=context.openFileInput(str_outputfile/*StringList.m_strWifiOutputFile*/);
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

 		// 新しくXmlPullParserAidedを作成する.
 		XmlPullParserAided p_aided = new XmlPullParserAided(context, read_string, 1);
 		p_aided.TakeApartProfile();		// ここで分解する
 		List<XmlDictionary> p_dict = p_aided.GetWifiDictList();

		// <key, type, data>リストを取得
		if(!p_dict.isEmpty()) {
			for(int i = 0; p_dict.size() > i; i++){
				XmlDictionary one_piece = p_dict.get(i);
				SetWifiList(one_piece);
			}
		}

 	}

 	private void SetParameter4Output(XmlSerializer serializer, String keyname, String parameter) {
 		try {
         	serializer.startTag("", "key");
 	        serializer.text(keyname);
 	        serializer.endTag("", "key");
 	        serializer.startTag("", "string");
 	        serializer.text(parameter);
 	        serializer.endTag("", "string");
 		} catch (IOException e){
 			Log.e("SetParameter::IOException ", e.toString());
 		}

 	}

 	 // デバッグ用
    void readWepConfig()
    {
        WifiManager wifi = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        List<WifiConfiguration> item = wifi.getConfiguredNetworks();
        int i = item.size();
        Log.d("WifiPreference", "NO OF CONFIG " + i );
        Iterator<WifiConfiguration> iter =  item.iterator();
        for(int num=0; num < i; num++) {
	        WifiConfiguration config = item.get(num/*0*/);
	        Log.d("WifiPreference", "========== ");
	        Log.d("WifiPreference", "SSID: " + config.SSID);
	        Log.d("WifiPreference", "PASSWORD: " + config.preSharedKey);

	        Log.d("WifiPreference", "ALLOWED ALGORITHMS");
	        Log.d("WifiPreference", "LEAP: " + config.allowedAuthAlgorithms.get(AuthAlgorithm.LEAP));
	        Log.d("WifiPreference", "OPEN: " + config.allowedAuthAlgorithms.get(AuthAlgorithm.OPEN));
	        Log.d("WifiPreference", "SHARED: " + config.allowedAuthAlgorithms.get(AuthAlgorithm.SHARED));
	        Log.d("WifiPreference", "GROUP CIPHERS: ");
	        Log.d("WifiPreference", "CCMP: " + config.allowedGroupCiphers.get(GroupCipher.CCMP));
	        Log.d("WifiPreference", "TKIP: " + config.allowedGroupCiphers.get(GroupCipher.TKIP));
	        Log.d("WifiPreference", "WEP104: " + config.allowedGroupCiphers.get(GroupCipher.WEP104));
	        Log.d("WifiPreference", "WEP40: " + config.allowedGroupCiphers.get(GroupCipher.WEP40));

	        Log.d("WifiPreference", "KEYMGMT");
	        Log.d("WifiPreference", "IEEE8021X: " + config.allowedKeyManagement.get(KeyMgmt.IEEE8021X));
	        Log.d("WifiPreference", "NONE: " + config.allowedKeyManagement.get(KeyMgmt.NONE));
	        Log.d("WifiPreference", "WPA_EAP: " + config.allowedKeyManagement.get(KeyMgmt.WPA_EAP));
	        Log.d("WifiPreference", "WPA_PSK: " + config.allowedKeyManagement.get(KeyMgmt.WPA_PSK));

	        Log.d("WifiPreference", "PairWiseCipher");
	        Log.d("WifiPreference", "CCMP: " + config.allowedPairwiseCiphers.get(PairwiseCipher.CCMP));
	        Log.d("WifiPreference", "NONE: " + config.allowedPairwiseCiphers.get(PairwiseCipher.NONE));
	        Log.d("WifiPreference", "TKIP: " + config.allowedPairwiseCiphers.get(PairwiseCipher.TKIP));

	        Log.d("WifiPreference", "Protocols");
	        Log.d("WifiPreference", "RSN: " + config.allowedProtocols.get(Protocol.RSN));
	        Log.d("WifiPreference", "WPA: " + config.allowedProtocols.get(Protocol.WPA));

	        Log.d("WifiPreference", "WEP Key Strings");
	        String[] wepKeys = config.wepKeys;
	        Log.d("WifiPreference", "WEP KEY 0" + wepKeys[0]);
	        Log.d("WifiPreference", "WEP KEY 1" + wepKeys[1]);
	        Log.d("WifiPreference", "WEP KEY 2" + wepKeys[2]);
	        Log.d("WifiPreference", "WEP KEY 3" + wepKeys[3]);
        }


    }

    void readEapConfig()
    {

        /*Get All WIfi configurations*/
    	WifiManager wifi = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        List<WifiConfiguration> configList = wifi.getConfiguredNetworks();
        int l = configList.size();
        Log.d("WifiPreference", "NO OF CONFIG " + l );

        /*Now we need to search appropriate configuration i.e. with name SSID_Name*/
        for(int i = 0;i<configList.size();i++)
        {

            	Log.d("Reflection:Found a devroam", "ddd");
                /*We found the appropriate config now read all config details*/
                Iterator<WifiConfiguration> iter =  configList.iterator();
                WifiConfiguration config = configList.get(i);

                /*I dont think these fields have anything to do with EAP config but still will
                 * print these to be on safe side*/
                Log.d("WifiPreference", "========== ");
    	        Log.d("WifiPreference", "SSID: " + config.SSID);
    	        Log.d("WifiPreference", "PASSWORD: " + config.preSharedKey);

    	        Log.d("WifiPreference", "ALLOWED ALGORITHMS");
    	        Log.d("WifiPreference", "LEAP: " + config.allowedAuthAlgorithms.get(AuthAlgorithm.LEAP));
    	        Log.d("WifiPreference", "OPEN: " + config.allowedAuthAlgorithms.get(AuthAlgorithm.OPEN));
    	        Log.d("WifiPreference", "SHARED: " + config.allowedAuthAlgorithms.get(AuthAlgorithm.SHARED));
    	        Log.d("WifiPreference", "GROUP CIPHERS: ");
    	        Log.d("WifiPreference", "CCMP: " + config.allowedGroupCiphers.get(GroupCipher.CCMP));
    	        Log.d("WifiPreference", "TKIP: " + config.allowedGroupCiphers.get(GroupCipher.TKIP));
    	        Log.d("WifiPreference", "WEP104: " + config.allowedGroupCiphers.get(GroupCipher.WEP104));
    	        Log.d("WifiPreference", "WEP40: " + config.allowedGroupCiphers.get(GroupCipher.WEP40));

    	        Log.d("WifiPreference", "KEYMGMT");
    	        Log.d("WifiPreference", "IEEE8021X: " + config.allowedKeyManagement.get(KeyMgmt.IEEE8021X));
    	        Log.d("WifiPreference", "NONE: " + config.allowedKeyManagement.get(KeyMgmt.NONE));
    	        Log.d("WifiPreference", "WPA_EAP: " + config.allowedKeyManagement.get(KeyMgmt.WPA_EAP));
    	        Log.d("WifiPreference", "WPA_PSK: " + config.allowedKeyManagement.get(KeyMgmt.WPA_PSK));

    	        Log.d("WifiPreference", "PairWiseCipher");
    	        Log.d("WifiPreference", "CCMP: " + config.allowedPairwiseCiphers.get(PairwiseCipher.CCMP));
    	        Log.d("WifiPreference", "NONE: " + config.allowedPairwiseCiphers.get(PairwiseCipher.NONE));
    	        Log.d("WifiPreference", "TKIP: " + config.allowedPairwiseCiphers.get(PairwiseCipher.TKIP));

    	        Log.d("WifiPreference", "Protocols");
    	        Log.d("WifiPreference", "RSN: " + config.allowedProtocols.get(Protocol.RSN));
    	        Log.d("WifiPreference", "WPA: " + config.allowedProtocols.get(Protocol.WPA));

    	        Log.d("WifiPreference", "WEP Key Strings");
    	        String[] wepKeys = config.wepKeys;
    	        Log.d("WifiPreference", "WEP KEY 0" + wepKeys[0]);
    	        Log.d("WifiPreference", "WEP KEY 1" + wepKeys[1]);
    	        Log.d("WifiPreference", "WEP KEY 2" + wepKeys[2]);
    	        Log.d("WifiPreference", "WEP KEY 3" + wepKeys[3]);

                /*reflection magic*/
                /*These are the fields we are really interested in*/
                try
                {
                    // Let the magic start
                    Class[] wcClasses = WifiConfiguration.class.getClasses();
                    // null for overzealous java compiler
                    Class wcEnterpriseField = null;

                    for (Class wcClass : wcClasses)
                        if (wcClass.getName().equals(INT_ENTERPRISEFIELD_NAME))
                        {
                            wcEnterpriseField = wcClass;
                            break;
                        }

                    boolean noEnterpriseFieldType = false;

                    if(wcEnterpriseField == null) noEnterpriseFieldType = true; // Cupcake/Donut access enterprise settings directly

                    Field wcefAnonymousId = null, wcefCaCert = null, wcefClientCert = null, wcefEap = null, wcefIdentity = null, wcefPassword = null, wcefPhase2 = null, wcefPrivateKey = null;
                    Field[] wcefFields = WifiConfiguration.class.getFields();

                    // Dispatching Field vars
                    for (Field wcefField : wcefFields)
                    {
                        if (wcefField.getName().trim().equals(INT_ANONYMOUS_IDENTITY)) wcefAnonymousId = wcefField;
                        else if (wcefField.getName().trim().equals(INT_CA_CERT)) wcefCaCert = wcefField;
                        else if (wcefField.getName().trim().equals(INT_CLIENT_CERT)) wcefClientCert = wcefField;
                        else if (wcefField.getName().trim().equals(INT_EAP)) wcefEap = wcefField;
                        else if (wcefField.getName().trim().equals(INT_IDENTITY)) wcefIdentity = wcefField;
                        else if (wcefField.getName().trim().equals(INT_PASSWORD)) wcefPassword = wcefField;
                        else if (wcefField.getName().trim().equals(INT_PHASE2)) wcefPhase2 = wcefField;
                        else if (wcefField.getName().trim().equals(INT_PRIVATE_KEY)) wcefPrivateKey = wcefField;
                    }

                Method wcefSetValue = null;
                if(!noEnterpriseFieldType)
                {
                	for(Method m: wcEnterpriseField.getMethods())
                		//System.out.println(m.getName());
                		if(m.getName().trim().equals("value")){
                			wcefSetValue = m;
                			break;
                		}
                }

                /*EAP Method*/
                String result = null;
                Object obj = null;
                if(!noEnterpriseFieldType)
                {
                    obj = wcefSetValue.invoke(wcefEap.get(config), null);
                    String retval = (String)obj;
                    Log.d("[EAP METHOD]", retval);
                }

                /*phase 2*/
                if(!noEnterpriseFieldType)
                {
                    result = (String) wcefSetValue.invoke(wcefPhase2.get(config), null);
                    Log.d("[EAP PHASE 2 AUTHENTICATION]" ,result);
                }

                /*Anonymous Identity*/
                if(!noEnterpriseFieldType)
                {
                    result = (String) wcefSetValue.invoke(wcefAnonymousId.get(config),null);
                    Log.d("[EAP ANONYMOUS IDENTITY]" , result);
                }

                /*CA certificate*/
                if(!noEnterpriseFieldType)
                {
                    result = (String) wcefSetValue.invoke(wcefCaCert.get(config), null);
                    Log.d("[EAP CA CERTIFICATE]" , result);
                }

                /*private key*/
                if(!noEnterpriseFieldType)
                {
                    result = (String) wcefSetValue.invoke(wcefPrivateKey.get(config),null);
                    Log.d("[EAP PRIVATE KEY]" , result);
                }

                /*Identity*/
                if(!noEnterpriseFieldType)
                {
                    result = (String) wcefSetValue.invoke(wcefIdentity.get(config), null);
                    Log.d("[EAP IDENTITY]" , result);
                }

                /*Password*/
                if(!noEnterpriseFieldType)
                {
                    result = (String) wcefSetValue.invoke(wcefPassword.get(config), null);
                    Log.d("[EAP PASSWORD]" , result);
                }

                /*client certificate*/
                if(!noEnterpriseFieldType)
                {
                    result = (String) wcefSetValue.invoke(wcefClientCert.get(config), null);
                    Log.d("[EAP CLIENT CERT]" , result);
                }


                }
                catch(Exception e)
                {
                	Log.e("Exception in read reflection:","");
                    e.printStackTrace();
                }


            }
        }
};