package epsap4.soliton.co.jp;

import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;

import java.io.File;
import java.util.List;

import epsap4.soliton.co.jp.scep.Requester;
import epsap4.soliton.co.jp.shortcut.CreateShortcutLink;
import epsap4.soliton.co.jp.wifi.WifiControl;
import epsap4.soliton.co.jp.xmlparser.XmlDictionary;
import epsap4.soliton.co.jp.xmlparser.XmlPullParserAided;
import epsap4.soliton.co.jp.xmlparser.XmlStringData;


///////////////////////////////////////////
// Configration 振り分け機能
///////////////////////////////////////////
public class ConfigrationDivision {
	
	private Context m_ctx;
	XmlDictionary p_dict;	// XmlDictionaryクラス
	XmlDictionary p_Scepdict;
	XmlDictionary p_passdict;
	XmlDictionary p_Appdict;
	List<XmlDictionary> p_wifidictlist;
	List<XmlDictionary> p_Shortcutdictlist;
	XmlDictionary p_remove_password;
	enum dict_num {
		num_dict,
		num_scepdict ,
		num_wifipict,
		num_passdict,
		num_appdict,
		num_shortdict,
	};
	
	DevicePolicyManager m_DPM;
    ComponentName m_DeviceAdmin;
    WifiControl m_wifi;
    RestrictionsControl m_resriction;
    RestrictionsFlgs m_restflgs;
    CreateShortcutLink m_c_link;
 //   ScepAsyncTask m_scep_asynctask;
    
    private Requester scepRequester = null;
    
    boolean m_bforcePIN = false;	// 強制パスコード(Config設定後に行うので、フラグを持っておく)
	
	public ConfigrationDivision(Context ctx, XmlPullParserAided aided, DevicePolicyManager dpm, ComponentName cmpname) {
		
		m_ctx = ctx;		
		LogCtrl.Logger(LogCtrl.m_strInfo, "ConfigrationDivision "+ "Constractor start.", m_ctx);
		
		// XmlDictionary
		p_dict = aided.GetDictionary();		// 4.0より各機能ごとのDictionaryを取得する
		p_Scepdict = aided.GetScepDictionary();	// Scep
		p_passdict = aided.GetPassDictionary();	// passcode
		p_Appdict = aided.GetAppDictionary();	// aided
		p_wifidictlist = aided.GetWifiDictList();
		p_Shortcutdictlist = aided.GetWebClipDictList();
		p_remove_password = aided.GetRmvPwdDictionary();
		
		m_DPM = dpm;
		m_DeviceAdmin = cmpname;
		
		// Wi-Fi
		m_wifi = new WifiControl(m_ctx);
		// 機能制限
		m_resriction = new RestrictionsControl(m_ctx);
		m_restflgs = new RestrictionsFlgs();
		// ショートカットリンク
		m_c_link = new CreateShortcutLink(m_ctx);
		// SCEP
//		m_scep_asynctask = new ScepAsyncTask((Activity) m_ctx);
		
	}
	
	///////////////////////////////////////////////
	// 機能：Config情報に従って、Passcode,Wi-Fi, Restrictionsの設定を実施する.
	///////////////////////////////////////////////
	public boolean RunConfigration() {
		
		// 初期化
		InitConfigration();
		
		// profileのデータを設定する
		SetConfigration();
		
		// WI-FIは、RunConfigrationChildで値を設定後、まとめて処理
		if(m_wifi.PublicConnect(WifiControl.INSTANT_WIFI) == false) {
			return false;
		}
		
		// ショートカット
		if(m_c_link.CreateRun() == false) {
			return false;
		}
		
		// scep debug
	    //	scepRequester = getScepRequester();
	    //	m_scep_asynctask.execute(scepRequester);
		
		// forcePIN(強制パスコード)がtrueだったとき、パスコードアクティビティを実行する
		if(m_bforcePIN == true) {
			Intent intent = new Intent(DevicePolicyManager.ACTION_SET_NEW_PASSWORD);
			m_ctx.startActivity(intent);
		}
		
		// Restrictionsもまとめて行うことにする.
		// (もしかしたらRestriction情報出力ファイルを利用して他情報も出力するかもしれないので最後に持ってくる)
		m_restflgs.WriteRestrictionsInfo(m_ctx);
		//m_restflgs.ReadAndSetRestictionsInfo(m_ctx);
		m_resriction.SrartMoniter(m_restflgs);
		
		return true;
	}
	
	// 初期化
	private void InitConfigration() {
		String filedir = "/data/data/" + m_ctx.getPackageName() + "/files/";
		
		//<=== ショートカットの削除
		File filename = new File(filedir + StringList.m_strShortcutOutputFile);
		LogCtrl.Logger(LogCtrl.m_strInfo, "ProfileResetActivity::onClick "+ "Filename = " + filename, m_ctx);
		
		if(filename.exists()) {
			CreateShortcutLink c_link = new CreateShortcutLink(m_ctx);
		//	c_link.SetAction(Intent.ACTION_VIEW);
			c_link.ReadAndSetShortcutInfo();
			c_link.RemoveRun();
			filename.delete();
		}
		// ショートカットの削除 ===>
		
		//<=== Wifiの削除
		// Wi-Fi
/*		File filename2 = new File(filedir + StringList.m_strWifiOutputFile);
		
		if(filename2.exists()) {
			WifiControl wifi = new WifiControl(m_ctx);
			wifi.ReadAndSetWifiInfo();
			wifi.deleteConfig();
			filename2.delete();
		}*/
		// Wifiの削除 ===>
		
		//<=== 機能制限の削除
		File filename3 = new File(filedir + StringList.m_strRestrictionFileName);
		if(filename3.exists()) {
			RestrictionsControl m_resriction = new RestrictionsControl(m_ctx);	// この時点でサービスを止める
			
			filename3.delete();
			
		}

		if(m_DPM.isAdminActive(m_DeviceAdmin) == true)
			m_DPM.setCameraDisabled(m_DeviceAdmin, false);	// Camera起動を一旦許可する
	}
	
	private void SetConfigration() {
		//☆ 各XmlDirectory にnull判定が必要 ☆//
		List<XmlStringData> str_list;
		
		// <key, type, data>リストを取得
		if(p_dict != null) {
			str_list = p_dict.GetArrayString();
			for(int i = 0; str_list.size() > i; i++){
				// config情報に従って、処理を行う.
				XmlStringData p_data = str_list.get(i);
				SetConfigrationChild(p_data, dict_num.num_dict);
			}
		}
		
		// Scep
		if(p_Scepdict != null) {
			str_list = p_Scepdict.GetArrayString();
			for(int i = 0; str_list.size() > i; i++){
				// config情報に従って、処理を行う.
				XmlStringData p_data = str_list.get(i);
				SetConfigrationChild(p_data, dict_num.num_scepdict);
			}
		}
		
		// Wi-Fi
		if(!p_wifidictlist.isEmpty()) {
			for(int i = 0; p_wifidictlist.size() > i; i++){
				XmlDictionary one_piece = p_wifidictlist.get(i);
				m_wifi.SetWifiList(one_piece);
			}
		}
		
		// Passcode
		if(p_passdict != null) {
			str_list = p_passdict.GetArrayString();
			for(int i = 0; str_list.size() > i; i++){
				// config情報に従って、処理を行う.
				XmlStringData p_data = str_list.get(i);
				SetConfigrationChild(p_data, dict_num.num_passdict);
			}
		}
		
		// Application
		if(p_Appdict != null) {
			str_list = p_Appdict.GetArrayString();
			for(int i = 0; str_list.size() > i; i++){
				// config情報に従って、処理を行う.
				XmlStringData p_data = str_list.get(i);
				SetConfigrationChild(p_data, dict_num.num_appdict);
			}
		}
		
		// Shortcut
		if(!p_Shortcutdictlist.isEmpty()) {
			for(int i = 0; p_Shortcutdictlist.size() > i; i++){
				XmlDictionary one_piece = p_Shortcutdictlist.get(i);
				m_c_link.SetShortcutList(one_piece);
			}
		}
		
		// profile delete pasword
		if(p_remove_password != null) {
			// もうここで保存してしまってよい...
		}
	}
	
	private void SetConfigrationChild(XmlStringData p_data, dict_num dic) {
		String strKeyName = p_data.GetKeyName();	// キー名
		int    i_type = p_data.GetType();		// 要素タイプ(string:1, data=2, date=3, real=4, integer=5, true=6, false=7)
		String strData = p_data.GetData();		// 要素
		
	//	Log.i("ConfigrationDivision::RunConfigrationChild key=", strKeyName);
	//	Log.i("ConfigrationDivision::RunConfigrationChild type=", Integer.toString(i_type));
	//	Log.i("ConfigrationDivision::RunConfigrationChild data=", strData);
		
		boolean b_type = true;
		if(i_type == 7) b_type = false;
		
		switch(dic) {
		case num_dict:
			
			break;
		case num_scepdict:
/*			if(strKeyName.equalsIgnoreCase(StringList.m_str_scep_url)) {
				m_scep_asynctask.SetScepUrl(strData);
			} else if(strKeyName.equalsIgnoreCase(StringList.m_str_CaIdent)) {
				m_scep_asynctask.SetScepCaID(strData);
			} else if(strKeyName.equalsIgnoreCase(StringList.m_str_common_name_certificate)) {
				m_scep_asynctask.SetCommonName(strData);
			} else if(strKeyName.equalsIgnoreCase(StringList.m_str_country_name_certificate)) {
				m_scep_asynctask.SetCountry(strData);
			}*/
			break;
		case num_wifipict:
			// Wi-Fi
			// List処理へ移行
			break;
		case num_passdict:
			ConfigrationProcess p_cnf = new ConfigrationProcess(m_ctx, m_DPM, m_DeviceAdmin);
			
			// Passcode
			if(strKeyName.equalsIgnoreCase(StringList.m_str_allowSimple)) {
				p_cnf.allowSimple(b_type);
			} else if(strKeyName.equalsIgnoreCase(StringList.m_str_requireAlphanumeric)) {
				p_cnf.requireAlphanumeric(b_type);
			} else if(strKeyName.equalsIgnoreCase(StringList.m_str_minLength)) {
				p_cnf.minLength(strData);
			} else if(strKeyName.equalsIgnoreCase(StringList.m_str_minComplexChars)) {
				p_cnf.minComplexChars(strData);
			} else if(strKeyName.equalsIgnoreCase(StringList.m_str_maxPINAgeInDays)) {
				p_cnf.maxPINAgeInDays(strData);
			} else if(strKeyName.equalsIgnoreCase("maxInactivity")) {
				p_cnf.maxInactivity(strData);
			} else if(strKeyName.equalsIgnoreCase(StringList.m_str_pinHistory)) {
				p_cnf.pinHistory(strData);
			} else if(strKeyName.equalsIgnoreCase("maxGracePeriod")) {
				p_cnf.maxGracePeriod(strData);
			} else if(strKeyName.equalsIgnoreCase(StringList.m_str_maxFailedAttempts)) {
				p_cnf.maxFailedAttempts(strData);
			} else if(strKeyName.equalsIgnoreCase(StringList.m_str_forcePIN)) {
				if (i_type == 6) {
					// true	    		
					m_bforcePIN = true;
				}
			}
			break;
		case num_appdict:
			// Restrictions
			if(strKeyName.equalsIgnoreCase(StringList.m_strCamera)) {
				m_restflgs.SetCamera(b_type, m_DPM, m_DeviceAdmin);
			} else if(strKeyName.equalsIgnoreCase(StringList.m_strYoutube)) {
				m_restflgs.SetYouTube(b_type);
			} else if(strKeyName.equalsIgnoreCase(StringList.m_striTunes)) {
				m_restflgs.SetiTunes(b_type);
			} else if(strKeyName.equalsIgnoreCase(StringList.m_strSafari)) {
				m_restflgs.SetSafari(b_type);
			}
			break;
		case num_shortdict:
			// Shortcut
			// List処理へ移行
			break;
		}
		
		
	}
	
	// Scep Enroll
	public boolean RunScepEnrollment() {
		// profileのデータを設定する
		//SetConfigration();
		
		// インストール時に取得したパラメータ群(CA Certificateなど)をGetする
/*		CertStore store = m_scep_asynctask.GetCertStore();
		String url = m_scep_asynctask.GetScepUrl();
		String id = m_scep_asynctask.GetScepCaID();
		
		// AsyncTaskは一回しか実行できないので、再度インスタンスを作成する
		m_scep_asynctask = new ScepAsyncTask((Activity) m_ctx);
		m_scep_asynctask.SetCertStrore(store);		// CA Certificateをセットする
		m_scep_asynctask.SetScepUrl(url);
		m_scep_asynctask.SetScepCaID(id);
		m_scep_asynctask.SetScepAsyncTaskNumber(ScepAsyncTask.i_CertificateEnroll);	// Enroll
		
		// Requesterに乗せて実行する
		scepRequester = getScepRequester();
	   	m_scep_asynctask.execute(scepRequester);*/
		
		return true;
	}
		
	////////////////////////////////////
	//=== Certificate Process      ===//
	////////////////////////////////////
	public Requester getScepRequester() {
		// Requester作成済みならそれを使用する.未作成なら作成
		if (scepRequester == null) {
			setScepRequester(new Requester());
		}
		return scepRequester;
	}
	
	public void setScepRequester(Requester scepRequester) {
		this.scepRequester = scepRequester;
	}
}