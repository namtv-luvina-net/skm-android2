package jp.co.soliton.keymanager.activity;

import android.Manifest;
import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import jp.co.soliton.keymanager.LogCtrl;
import jp.co.soliton.keymanager.R;
import jp.co.soliton.keymanager.StringList;
import jp.co.soliton.keymanager.fragment.ContentAPIDTabletFragment;
import jp.co.soliton.keymanager.fragment.ContentMenuTabletFragment;
import jp.co.soliton.keymanager.fragment.LeftSideAPIDTabletFragment;
import jp.co.soliton.keymanager.fragment.LeftSideMenuTabletFragment;
import jp.co.soliton.keymanager.xmlparser.XmlDictionary;
import jp.co.soliton.keymanager.xmlparser.XmlPullParserAided;
import jp.co.soliton.keymanager.xmlparser.XmlStringData;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.List;

/**
 * Created by luongdolong on 2/3/2017.
 *
 * Activity for menu apply screen
 */

public class MenuAcivity extends Activity {
    private int PERMISSIONS_REQUEST_READ_PHONE_STATE = 10;
	FragmentManager fragmentManager;
	private boolean isTablet;
	private LogCtrl logCtrl;
	String strUDID;
	private String strVpnID;
	private String m_strAPIDWifi = "";	// APID Wi-Fi #21391
	private String m_strAPIDVPN = "";	// APID VPN #21391
	private boolean isFocusMenuTablet;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
	    setOrientation();
	    setContentView(R.layout.activity_menu);
	    logCtrl = LogCtrl.getInstance(this);
	    fragmentManager = getFragmentManager();
	    getSAPID();
	    createView();
    }

	private void createView() {
		if (isTablet) {
			isFocusMenuTablet = true;
			FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
			fragmentTransaction.add(R.id.fragment_left_side_menu_tablet, new LeftSideMenuTabletFragment());
			fragmentTransaction.add(R.id.fragment_content_menu_tablet, new ContentMenuTabletFragment());
			fragmentTransaction.commit();
		}
	}

	private void setOrientation() {
		isTablet = getResources().getBoolean(R.bool.isTablet);
		if (isTablet) {
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
		} else {
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		}
	}

	@Override
	public void onBackPressed() {
		if (!isFocusMenuTablet && isTablet) {
			goToMenu();
		} else {
			super.onBackPressed();
		}
	}

	public void goToMenu() {
		isFocusMenuTablet = true;
		FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
		fragmentTransaction.setCustomAnimations(R.anim.pop_enter, R.anim.pop_exit, R.anim.exit, R.anim.enter);
		fragmentTransaction.replace(R.id.fragment_content_menu_tablet, new ContentMenuTabletFragment());
		fragmentTransaction.commit();

		fragmentTransaction = fragmentManager.beginTransaction();
		fragmentTransaction.replace(R.id.fragment_left_side_menu_tablet, new LeftSideMenuTabletFragment());
		fragmentTransaction.commit();
	}

	public void startSettingActivity() {
		Intent intent = new Intent(this, SettingActivity.class);
		startActivity(intent);
	}

	public void startActivityAPID(){
		if (isTablet) {
			isFocusMenuTablet = false;
			FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
			fragmentTransaction.setCustomAnimations(R.anim.enter, R.anim.exit, R.anim.pop_enter, R.anim.pop_exit);
			fragmentTransaction.replace(R.id.fragment_content_menu_tablet, new ContentAPIDTabletFragment());
			fragmentTransaction.commit();

			FragmentTransaction fragmentTransaction1 = fragmentManager.beginTransaction();
			fragmentTransaction1.replace(R.id.fragment_left_side_menu_tablet, new LeftSideAPIDTabletFragment());
			fragmentTransaction1.commit();
		}else {
			Intent intent = new Intent(this, APIDActivity.class);
			intent.putExtra("m_strAPIDVPN", strVpnID);
			intent.putExtra("m_strAPIDWifi", strUDID);
			startActivity(intent);
		}
	}

	@Override
    protected void onResume() {
        super.onResume();
        if (StringList.GO_TO_LIST_APPLY.equals("1")) {
            StringList.GO_TO_LIST_APPLY = "0";
            Intent intent = new Intent(MenuAcivity.this, ListConfirmActivity.class);
            startActivity(intent);
        }
        if(android.os.Build.VERSION.SDK_INT >= 23) {
            NewPermissionSet();
        }
    }

    private void NewPermissionSet() {
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_PHONE_STATE},
                    PERMISSIONS_REQUEST_READ_PHONE_STATE);
        }
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
			logCtrl.loggerDebug("ReadAndSetLoginUserInfo: "+ e.getMessage());
			bRet = false;
		} finally{
			try {
				if (inputStreamObj!=null) inputStreamObj.close();
				if (byteArrayOutputStreamObj!=null) byteArrayOutputStreamObj.close();
			} catch (Exception e2) {
				logCtrl.loggerDebug("ReadAndSetLoginUserInfo e2: " + e2.getMessage());
				bRet = false;
			}
		}

		if(bRet == false) return bRet;

		String read_string = new String(byArrData_read);
		android.util.Log.d(StringList.m_str_SKMTag, "*****Re-Read***** " + read_string);

		// 新しくXmlPullParserAidedを作成する.
		XmlPullParserAided p_aided = new XmlPullParserAided(this, read_string, 2);
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
			logCtrl.loggerDebug("LoginUserOutputFile Wifi APID=" + strData);
		} else if(strKeyName.equalsIgnoreCase(StringList.m_str_Apid_VPN)) {
			m_strAPIDVPN = strData;
			logCtrl.loggerDebug("LoginUserOutputFile VPN APID=" + strData);
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

}
