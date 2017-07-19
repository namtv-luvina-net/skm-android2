package jp.co.soliton.keymanager.mdm;

import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.telephony.TelephonyManager;
import android.util.Xml;
import jp.co.soliton.keymanager.LogCtrl;
import jp.co.soliton.keymanager.StringList;
import jp.co.soliton.keymanager.xmlparser.XmlDictionary;
import jp.co.soliton.keymanager.xmlparser.XmlKeyWord;
import jp.co.soliton.keymanager.xmlparser.XmlPullParserAided;
import jp.co.soliton.keymanager.xmlparser.XmlStringData;
import org.xmlpull.v1.XmlSerializer;

import java.io.*;
import java.util.List;

public class MDMFlgs implements Serializable{
	private static final long serialVersionUID = 4L;
	
	//DevicePolicyManager m_DPM;
    //ComponentName m_DeviceAdmin;
	
	private String m_strSelectAlias;
	private String m_strEpsapURL;
	
	// AccessRights
	private int m_n_devicelock = 4;
	private int m_n_devaiceerace = 8;
	private int m_n_deviceinf = 16;
	private int m_n_network = 32;
	private int m_n_inst_appinf = 256;
	
	// プロファイルメンバー
	private String m_str_topic;		// Topic
	private String m_str_serverurl;	// ServerURL
	private String m_str_checkein;	// CheckInURL
	private int m_n_accessright;	// AccessRight
	private boolean m_b_checkout = false;	// CheckOutWhenRemoved
	
	private String m_str_UDID;	//UDID プロファイルメンバーではないが、通信で結構使用するのでメンバ変数保存する
	
	private boolean m_b_wipe = false;
	
	public void SetAlias(String alias) { m_strSelectAlias = alias; }
	public void SetEpsapUrl(String url) { m_strEpsapURL = url; }
	
	public void SetTopic(String topic) { m_str_topic = topic; }
	public void SetServerurl(String url) { m_str_serverurl = url; }
	public void SetCheckin(String url) { m_str_checkein = url; }
	public void SetAccessRight(int right) { m_n_accessright = right; }
	public void SetUDID(String udid) { m_str_UDID = udid;}
	public void SetCheckOut(boolean co) { m_b_checkout = co;}
	
	public String GetTopic() { return m_str_topic;}
	public String GetServerurl() { return m_str_serverurl;}
	public String GetCheckin() { return m_str_checkein;}
	public int GetAccessRight() { return m_n_accessright;}

	public int getM_n_devicelock() {
		return m_n_devicelock;
	}

	public int getM_n_devaiceerace() {
		return m_n_devaiceerace;
	}

	public int getM_n_deviceinf() {
		return m_n_deviceinf;
	}

	public int getM_n_network() {
		return m_n_network;
	}

	public int getM_n_inst_appinf() {
		return m_n_inst_appinf;
	}

	public String GetUDID() { return m_str_UDID;}
	public boolean GetCheckOut() { return m_b_checkout;}
	public boolean GetWipe() { return m_b_wipe; }
	
//	public DevicePolicyManager GetDPM() { return m_DPM;}
//	public ComponentName GetComponent() { return m_DeviceAdmin;}

	public MDMFlgs() {
		// TODO 自動生成されたコンストラクター・スタブ
	//	m_DPM = dpm;
	//	m_DeviceAdmin = cmpname;
	}
	
	public String CheckinoutMsg(boolean bChkin) {
		String retmsg = "";
		
		XmlSerializer serializer = Xml.newSerializer();
		StringWriter writer = new StringWriter();
		try {
			serializer.setOutput(writer);	// XmlSerializerとStringWriterの関連付け..
			serializer.startDocument("UTF-8", true);
			serializer.startTag("", "plist");
			serializer.attribute("", "version", "1.0");
			serializer.startTag("", "dict");
	
			// MessageType
			String msgtype;
			if(bChkin == true) {
				// チェックイン
				msgtype = StringList.m_strCheckIn;
			} else {
				msgtype = StringList.m_strCheckOut;
			}
			XmlPullParserAided.SetParameter4Output(serializer, StringList.m_strMessageType, msgtype);
			// Topic
			XmlPullParserAided.SetParameter4Output(serializer, StringList.m_str_topic, m_str_topic);
			// UDID
			XmlPullParserAided.SetParameter4Output(serializer, StringList.m_str_udid, m_str_UDID);
		        
			serializer.endTag("", "dict");
			serializer.endTag("", "plist");
			serializer.endDocument();
			        
			// アウトプットをストリング型へ変換する
			retmsg = writer.toString();

		} catch (IOException e){
			LogCtrl.getInstance().error("MDMFlags::CheckinoutMsg:IOException " + e.toString());
		}
		
		return retmsg;
	}
	
	public String TokenUpdateMsg() {
		String retmsg = "";
		
		XmlSerializer serializer = Xml.newSerializer();
		StringWriter writer = new StringWriter();
		try {
			serializer.setOutput(writer);	// XmlSerializerとStringWriterの関連付け..
			serializer.startDocument("UTF-8", true);
			serializer.startTag("", "plist");
			serializer.attribute("", "version", "1.0");
			serializer.startTag("", "dict");
	
			// MessageType
			XmlPullParserAided.SetParameter4Output(serializer, StringList.m_strMessageType, StringList.m_strTokenUp);
			// Topic
			XmlPullParserAided.SetParameter4Output(serializer, StringList.m_str_topic, m_str_topic);
			// UDID
			XmlPullParserAided.SetParameter4Output(serializer, StringList.m_str_udid, m_str_UDID);
			// PushMagic
			XmlPullParserAided.SetParameter4Output(serializer, StringList.m_strPM, m_str_UDID);
			// Token
			XmlPullParserAided.SetDataParameter4Output(serializer, StringList.m_strToken, m_str_UDID);
			// UnlockToken
			XmlPullParserAided.SetDataParameter4Output(serializer, StringList.m_strUT, m_str_UDID);
		        
			serializer.endTag("", "dict");
			serializer.endTag("", "plist");
			serializer.endDocument();
			        
			// アウトプットをストリング型へ変換する
			retmsg = writer.toString();

		} catch (IOException e){
			LogCtrl.getInstance().error("MDMFlags::TokenUpdateMsg:IOException " + e.toString());
		}
		
		return retmsg;
	}
	
	public String StatusMsg(String str_status) {
		String retmsg = "";
		
		XmlSerializer serializer = Xml.newSerializer();
		StringWriter writer = new StringWriter();
		try {
			serializer.setOutput(writer);	// XmlSerializerとStringWriterの関連付け..
			serializer.startDocument("UTF-8", true);
			serializer.startTag("", "plist");
			serializer.attribute("", "version", "1.0");
			serializer.startTag("", "dict");
	
			// Status - Idle
			XmlPullParserAided.SetParameter4Output(serializer, StringList.m_strStatus, str_status/*StringList.m_strIdle*/);
			// UDID
			XmlPullParserAided.SetParameter4Output(serializer, StringList.m_str_udid, m_str_UDID);
		        
			serializer.endTag("", "dict");
			serializer.endTag("", "plist");
			serializer.endDocument();
			        
			// アウトプットをストリング型へ変換する
			retmsg = writer.toString();

		} catch (IOException e){
			LogCtrl.getInstance().error("MDMFlags::StatusMsg:IOException " + e.toString());
		}
		
		return retmsg;
	}
	
	public String CmdRepliesMsg(XmlPullParserAided aided, Context context, DevicePolicyManager dpm
			, ComponentName DeviceAdmin){
		String retmsg = "";
		m_b_wipe = false;	// 毎回、必ずfalse初期化する
		
		XmlKeyWord xmlKeyword = aided.GetXmlKeyWord();
		String reqtype = xmlKeyword.GetReqtype();
		boolean dp_running = dpm.isAdminActive(DeviceAdmin);
		
		if(reqtype.equalsIgnoreCase(StringList.m_str_RType_AppList)) {
			// InstalledApplicationList
			if((m_n_inst_appinf & m_n_accessright) == m_n_inst_appinf)
				retmsg = InstalledApplicationListMsg(aided, context);
			else
				retmsg = ErroMsg(aided, 12007);
		} else if(reqtype.equalsIgnoreCase(StringList.m_str_RType_DevInf)) {
			// DeviceInformation
			if((m_n_deviceinf & m_n_accessright) == m_n_deviceinf)
				retmsg = DeviceInfoListMsg(aided, context);
			else if((m_n_network & m_n_accessright) == m_n_network)
				retmsg = DeviceInfoListMsg(aided, context);
			else 
				retmsg = ErroMsg(aided, 12007);
		} else if(reqtype.equalsIgnoreCase(StringList.m_str_RType_DevLock)) {
			if((m_n_devicelock & m_n_accessright) == m_n_devicelock) {
				// DeviceLock
				if(dp_running == true) {
					RunLock(dpm);
					retmsg = AckMsg(aided);
				} else retmsg = ErroMsg(aided, 12029);
			} else retmsg = ErroMsg(aided, 12007);
		} else if(reqtype.equalsIgnoreCase(StringList.m_str_RType_Wipe)) {
			if((m_n_devaiceerace & m_n_accessright) == m_n_devaiceerace) {
				// EraseDevice
				if(dp_running == true) {
				//	RunWipe(dpm);
					m_b_wipe = true;
					retmsg = AckMsg(aided);
				} else retmsg = ErroMsg(aided, 12029);
			} else retmsg = ErroMsg(aided, 12007);
		} else {
			retmsg = ErroMsg(aided, 12021);	// Invalid request type
		}
		
		return retmsg;
	}
	
	public String AckMsg(XmlPullParserAided aided) {
		String retmsg = "";
		
		XmlSerializer serializer = Xml.newSerializer();
	    StringWriter writer = new StringWriter();
	    
	    try {
	        serializer.setOutput(writer);	// XmlSerializerとStringWriterの関連付け..
	        serializer.startDocument("UTF-8", true);
	        serializer.startTag("", "plist");
	        serializer.attribute("", "version", "1.0");
	        serializer.startTag("", StringList.m_str_dict);
	        
	        // CommandUUID <key>-<string>
	        XmlPullParserAided.SetParameter4Output(serializer, StringList.m_str_cmduuid, aided.GetXmlKeyWord().GetCmdUUID());
	        
		    // Status 
	        XmlPullParserAided.SetParameter4Output(serializer, StringList.m_strStatus, StringList.m_strAc);
	        // APIDを設定する
	        XmlPullParserAided.SetParameter4Output(serializer, StringList.m_str_udid, m_str_UDID);
	        
	        serializer.endTag("", StringList.m_str_dict);
	        serializer.endTag("", "plist");
	        serializer.endDocument();
	        return writer.toString();
	    } catch (IOException e){
			
		}
		return retmsg;
	}
	
	// インストールアプリ一覧
	public String InstalledApplicationListMsg(XmlPullParserAided aided, Context context) {
		String retmsg = "";
		
		XmlSerializer serializer = Xml.newSerializer();
	    StringWriter writer = new StringWriter();
	    
	    try {
	        serializer.setOutput(writer);	// XmlSerializerとStringWriterの関連付け..
	        serializer.startDocument("UTF-8", true);
	        serializer.startTag("", "plist");
	        serializer.attribute("", "version", "1.0");
	        serializer.startTag("", StringList.m_str_dict);
	        
	        // CommandUUID <key>-<string>
	        XmlPullParserAided.SetParameter4Output(serializer, StringList.m_str_cmduuid, aided.GetXmlKeyWord().GetCmdUUID());
	        
	        // InstalledApplicationList <key>のみ
	        serializer.startTag("", StringList.m_str_key);
	        serializer.text("InstalledApplicationList");
	        serializer.endTag("", StringList.m_str_key);
	        
	        serializer.startTag("", StringList.m_str_array);
	        
	        // arrayの項目を設定
	        // インストールアプリケーションリストは独自で構築する
	        // パッケージマネージャーの作成
		    PackageManager packageManager = context.getPackageManager();
		       // インストール済みのアプリケーション一覧の取得
		    List<ApplicationInfo> applicationInfo = packageManager.getInstalledApplications(PackageManager.GET_META_DATA);
		    for (ApplicationInfo info : applicationInfo) {
		    	SetApplicationInfo(serializer, packageManager, info);
		    }
	        
		    serializer.endTag("", StringList.m_str_array);
		    // Status 
	        XmlPullParserAided.SetParameter4Output(serializer, StringList.m_strStatus, StringList.m_strAc);
	        // APIDを設定する
	        XmlPullParserAided.SetParameter4Output(serializer, StringList.m_str_udid, m_str_UDID);
	        
	        serializer.endTag("", StringList.m_str_dict);
	        serializer.endTag("", "plist");
	        serializer.endDocument();
	        return writer.toString();
	    } catch (IOException e){
			
		}
		return retmsg;
	}
	
	// 1インストールアプリ単位のリプライ構築
	private void SetApplicationInfo(XmlSerializer serializer, PackageManager PM, ApplicationInfo info) {
		 try {
			serializer.startTag("", StringList.m_str_dict);
			
			// Name : アプリ名
			XmlPullParserAided.SetParameter4Output(serializer, "Name", (String)PM.getApplicationLabel(info));
			
			
			PackageInfo PI = PM.getPackageInfo(info.packageName, PackageManager.GET_META_DATA);
			// Identifier : パッケージ名
			XmlPullParserAided.SetParameter4Output(serializer, "Identifier", info.packageName);
			
			// Version : バージョン名
			XmlPullParserAided.SetParameter4Output(serializer, "Version", PI.versionName);
			
			// ShortVersion : バージョンコード
			XmlPullParserAided.SetParameter4Output(serializer, "ShortVersion", Integer.toString(PI.versionCode));
			
			serializer.endTag("", StringList.m_str_dict);
		} catch (IllegalArgumentException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		} catch (IllegalStateException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		} catch (IOException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		} catch (NameNotFoundException e) {
            e.printStackTrace();
        }
		 
		
		 
	}
	
	// デバイス情報一覧
	public String DeviceInfoListMsg(XmlPullParserAided aided, Context context) {
		String retmsg = "";
			
		XmlSerializer serializer = Xml.newSerializer();
	    StringWriter writer = new StringWriter();
	    try {
	        serializer.setOutput(writer);	// XmlSerializerとStringWriterの関連付け..
	        serializer.startDocument("UTF-8", true);
	        serializer.startTag("", "plist");
	        serializer.attribute("", "version", "1.0");
	        serializer.startTag("", StringList.m_str_dict);
	        
	        // CommandUUID <key>-<string>
	        XmlPullParserAided.SetParameter4Output(serializer, StringList.m_str_cmduuid, aided.GetXmlKeyWord().GetCmdUUID());
	        
	        // QuaryResponses <key>のみ
	        serializer.startTag("", StringList.m_str_key);
	        serializer.text(StringList.m_strQR);
	        serializer.endTag("", StringList.m_str_key);
	        
	        // Device情報では、ここでもう一つdictを挟む
	        serializer.startTag("", StringList.m_str_dict);
	        
	        // arrayの項目を取得
	        List<String> strArraylist = aided.GetXmlKeyWord().GetArrayString();
	        for(int i = 0; strArraylist.size() > i; i++) {
	        	String parameter = strArraylist.get(i);
	        	
	        	// Serverから送信されたarrayの項目を、返信のkeyに設定する
	        	String info_parameter = RtnIdEtc(parameter, context);
				if(info_parameter != null && info_parameter.length() > 0)
	        	XmlPullParserAided.SetParameter4Output(serializer, parameter, /*RtnIdEtc(parameter, context)*/info_parameter);
	        }
	        
	        
	        serializer.endTag("", StringList.m_str_dict);
	        
	        // Status 
	        XmlPullParserAided.SetParameter4Output(serializer, StringList.m_strStatus, StringList.m_strAc);
	        // APIDを設定する
	        XmlPullParserAided.SetParameter4Output(serializer, StringList.m_str_udid, m_str_UDID);
	        
	        serializer.endTag("", StringList.m_str_dict);
	        serializer.endTag("", "plist");
	        serializer.endDocument();
	        return writer.toString();
	    } catch (IOException e){
			
		}
	    
		return retmsg;
	}
	
	// 即時Lock
	public void RunLock(DevicePolicyManager dpm) {
		dpm.lockNow();
	}
	
	// 即時Wipe
	static public void RunWipe(DevicePolicyManager dpm) {
		dpm.wipeData(0);
	}
	
	// エラーメッセージ
	private String ErroMsg(XmlPullParserAided aided, int eCode) {
		String retmsg = "";
		
		XmlSerializer serializer = Xml.newSerializer();
	    StringWriter writer = new StringWriter();
	    try {
	        serializer.setOutput(writer);	// XmlSerializerとStringWriterの関連付け..
	        serializer.startDocument("UTF-8", true);
	        serializer.startTag("", "plist");
	        serializer.attribute("", "version", "1.0");
	        serializer.startTag("", StringList.m_str_dict);
	        
	        // CommandUUID <key>-<string>
	        XmlPullParserAided.SetParameter4Output(serializer, StringList.m_str_cmduuid, aided.GetXmlKeyWord().GetCmdUUID());
	        
	        // QuaryResponses <key>のみ
	        serializer.startTag("", StringList.m_str_key);
	        serializer.text(StringList.m_strErrorChain);
	        serializer.endTag("", StringList.m_str_key);
	        
	        // <array>-<dict>でエラー詳細を挟む
	        serializer.startTag("", StringList.m_str_array);      
	        serializer.startTag("", StringList.m_str_dict);
	        
	        // エラー詳細
	        serializer.startTag("", StringList.m_str_key);
	        serializer.text(StringList.m_strErrorCode);
	        serializer.endTag("", StringList.m_str_key);
	       
	        serializer.startTag("", "Number"/*StringList.m_str_integer*/);
	        serializer.text(Integer.toString(eCode));
	        serializer.endTag("", "Number"/*StringList.m_str_integer*/);
	        
	        // <==debug
/*	        serializer.startTag("", StringList.m_str_key);
	        serializer.text("LocalizedDescription");
	        serializer.endTag("", StringList.m_str_key);
	        
	        serializer.startTag("", StringList.m_str_string);
	        serializer.text("エラーだ");
	        serializer.endTag("", StringList.m_str_string);*/
	        
	        // debug  ==>
	        
	        serializer.endTag("", StringList.m_str_dict);
	        serializer.endTag("", StringList.m_str_array);
	        
	        // Status 
	        XmlPullParserAided.SetParameter4Output(serializer, StringList.m_strStatus, StringList.m_strError);
	        // APIDを設定する
	        XmlPullParserAided.SetParameter4Output(serializer, StringList.m_str_udid, m_str_UDID);
	        
	        serializer.endTag("", StringList.m_str_dict);
	        serializer.endTag("", "plist");
	        serializer.endDocument();
	        return writer.toString();
	    } catch (IOException e){
			
		}
	    
		return retmsg;
	}
	
	private String RtnIdEtc(String word, Context context) {
		String rtnstr = "";
		try {
			// Wi-Fi
			WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
	        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
	        
	        // terephone情報
	        TelephonyManager telManager = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
	        
			if(word.equals("IMEI")) {
				rtnstr = telManager.getDeviceId();
				if(rtnstr == null) {
					rtnstr = "";
				}
			} else if(word.equals("OSVersion")) {
				//int sdkInt = Integer.parseInt(Build.VERSION.SDK);
				rtnstr = Build.VERSION.RELEASE;
			} else if (word.equals("BuildVersion")) {
				rtnstr = Build.VERSION.INCREMENTAL;
			} else if(word.equals("ModelName")) {
				rtnstr = Build.MODEL;
			} else if(word.equals("ICCID")) {
				rtnstr = telManager.getSimSerialNumber();
			} else if(word.equals("WiFiMAC")) {
				rtnstr = XmlPullParserAided.GetMacAddress();//wifiInfo.getMacAddress();
			} else if(word.equals("PhoneNumber")) {
				try {
				rtnstr = telManager.getLine1Number();
				} catch(Exception e) {
					rtnstr = null;
				}
				
				if(rtnstr == null) {
					rtnstr = "None";
				}
			} 
		} catch(Exception e) {
			LogCtrl.getInstance().error("MDMFlags::RtnIdEtc:IOException " + e.toString());
			rtnstr = "";
		}
		
		return rtnstr;
	}
		
	public void WriteScepMdmInfo(Context ctx) {
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
		//	XmlPullParserAided.SetParameter4Output(serializer, StringList.m_strEPSapURL, m_strEpsapURL);			
			// Alias
		//	XmlPullParserAided.SetParameter4Output(serializer, StringList.m_strAlias, m_strSelectAlias);	
			// Server URL
			XmlPullParserAided.SetParameter4Output(serializer, StringList.m_str_mdm_server, m_str_serverurl);
			// CheckIn URL
			XmlPullParserAided.SetParameter4Output(serializer, StringList.m_str_mdm_checkin, m_str_checkein);
			// Topic
			XmlPullParserAided.SetParameter4Output(serializer, StringList.m_str_topic, m_str_topic);
			// AccessRight
			XmlPullParserAided.SetIntParameter4Output(serializer, StringList.m_str_AccessRights, Integer.toString(m_n_accessright));
		    // UDID
			XmlPullParserAided.SetParameter4Output(serializer, StringList.m_str_udid, m_str_UDID);
			// CheckOut
			XmlPullParserAided.SetBoolParameter4Output(serializer, StringList.m_str_CheckOutRemoved, m_b_checkout);
			
			serializer.endTag("", "dict");
			serializer.endTag("", "plist");
			serializer.endDocument();
			        
			// アウトプットをストリング型へ変換する
			retmsg = writer.toString();

		} catch (IOException e){
			LogCtrl.getInstance().error("MDMFlags::WriteScepMdmInfo:IOException: " + e.toString());
		}
			    
		byte[] byArrData = retmsg.getBytes();
		OutputStream outputStreamObj=null;
				
		try {
			//Context ctx = new Context();
			//Contextから出力ストリーム取得
			outputStreamObj=ctx.openFileOutput(StringList.m_strMdmOutputFile, Context.MODE_PRIVATE);
			//出力ストリームにデータを出力
			outputStreamObj.write(byArrData, 0, byArrData.length);
		} catch (FileNotFoundException e) {
			LogCtrl.getInstance().error("MDMFlags::WriteScepMdmInfo:FileNotFoundException: " + e.toString());
		} catch (IOException e) {
			LogCtrl.getInstance().error("MDMFlags::WriteScepMdmInfo:IOException: " + e.toString());
		}
	}
	
	public boolean ReadAndSetScepMdmInfo(Context ctx) {
		byte[] byArrData_read = null;
		int iSize;
		byte[] byArrTempData=new byte[4096];
		InputStream inputStreamObj=null;
		ByteArrayOutputStream byteArrayOutputStreamObj=null;
					
		boolean bRet = true;
		
		try {
			//Contextから入力ストリームの取得
			inputStreamObj=ctx.openFileInput(StringList.m_strMdmOutputFile);
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
			LogCtrl.getInstance().error("MDMFlags::ReadAndSetScepMdmInfo:Exception: " + e.toString());
			bRet = false;
		} finally{
			try {
				if (inputStreamObj!=null) inputStreamObj.close();
				if (byteArrayOutputStreamObj!=null) byteArrayOutputStreamObj.close();
			} catch (Exception e2) {
				LogCtrl.getInstance().error("MDMFlags::ReadAndSetScepMdmInfo:Exception: " + e2.toString());
				bRet = false;
			}
					
		}
			
		if(bRet == false) return bRet;
			
		String read_string = new String(byArrData_read);

		// 新しくXmlPullParserAidedを作成する.
		XmlPullParserAided p_aided = new XmlPullParserAided(ctx, read_string, 2);
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
		
		boolean b_type = true;
		if(i_type == 7) b_type = false;
		// 
		if(strKeyName.equalsIgnoreCase(StringList.m_strEPSapURL)) {
			m_strEpsapURL = strData;
		} else if(strKeyName.equalsIgnoreCase(StringList.m_strAlias)) {
			m_strSelectAlias = strData;
		} else if(strKeyName.equalsIgnoreCase(StringList.m_str_mdm_server)) {
			m_str_serverurl = strData;
		} else if(strKeyName.equalsIgnoreCase(StringList.m_str_mdm_checkin)) {
			m_str_checkein = strData;
		} else if(strKeyName.equalsIgnoreCase(StringList.m_str_topic)) {
			m_str_topic = strData;
		} else if(strKeyName.equalsIgnoreCase(StringList.m_str_AccessRights)) {
			m_n_accessright = Integer.parseInt(strData);
		} else if(strKeyName.equalsIgnoreCase(StringList.m_str_udid)) {
			SetUDID(strData);
		} else if(strKeyName.equalsIgnoreCase(StringList.m_str_CheckOutRemoved)) {
			m_b_checkout = b_type;
		}
	}

}
