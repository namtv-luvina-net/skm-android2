package jp.co.soliton.keymanager.manager;

import android.content.Context;
import jp.co.soliton.keymanager.LogCtrl;
import jp.co.soliton.keymanager.StringList;
import jp.co.soliton.keymanager.xmlparser.XmlDictionary;
import jp.co.soliton.keymanager.xmlparser.XmlPullParserAided;
import jp.co.soliton.keymanager.xmlparser.XmlStringData;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.List;

/**
 * Created by nguyenducdat on 5/4/2017.
 */

public class APIDManager {
	public final static String TARGET_VPN  = "0";
	public final static String TARGET_WiFi = "1";
	public final static String PREFIX_APID_WIFI = "WIFI";
	public final static String PREFIX_APID_VPN = "APP";


	String strUDID;
	private String strVpnID;
	private String m_strAPIDWifi = "";	// APID Wi-Fi #21391
	private String m_strAPIDVPN = "";	// APID VPN #21391
	Context context;

	public APIDManager(Context context) {
		this.context = context;
		getSAPID();
	}

	public String getStrUDID() {
		return strUDID;
	}

	public String getStrVpnID() {
		return strVpnID;
	}

	public void getSAPID() {
		ReadAndSetLoginUserInfo();
		strUDID = GetUDID().trim();
		strVpnID = GetVpnApid().trim();
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
			inputStreamObj=context.openFileInput(StringList.m_strLoginUserOutputFile);
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
			bRet = false;
		} finally{
			try {
				if (inputStreamObj!=null) inputStreamObj.close();
				if (byteArrayOutputStreamObj!=null) byteArrayOutputStreamObj.close();
			} catch (Exception e2) {
				bRet = false;
			}
		}

		if(bRet == false) return bRet;

		String read_string = new String(byArrData_read);

		// 新しくXmlPullParserAidedを作成する.
		XmlPullParserAided p_aided = new XmlPullParserAided(context, read_string, 2);
		p_aided.TakeApartControll();
		XmlDictionary p_dict = p_aided.GetDictionary();

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
		String strKeyName = p_data.GetKeyName();
		String strData = p_data.GetData();
		//
		if(strKeyName.equalsIgnoreCase(StringList.m_str_Apid_Wifi)) {
			m_strAPIDWifi = strData;
			LogCtrl.getInstance().debug("APID: Wifi=" + strData);
		} else if(strKeyName.equalsIgnoreCase(StringList.m_str_Apid_VPN)) {
			m_strAPIDVPN = strData;
			LogCtrl.getInstance().debug("APID: VPN=" + strData);
		}
	}

	private String GetUDID() {
		if(m_strAPIDWifi.length() > 0) return m_strAPIDWifi;
		else return XmlPullParserAided.GetUDID(context);
	}

	private String GetVpnApid() {
		if(m_strAPIDVPN.length() > 0) return m_strAPIDVPN;
		else return XmlPullParserAided.GetVpnApid(context);
	}

}
