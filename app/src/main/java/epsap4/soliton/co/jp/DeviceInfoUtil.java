package epsap4.soliton.co.jp;

import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.telephony.TelephonyManager;
import android.util.Log;

public class DeviceInfoUtil {

	public static String GetWifiMacAdress(WifiManager wifiManager) {
		String strmac = "";
		try {
//	        WifiManager wifiManager = (WifiManager) ctx.getSystemService(Context.WIFI_SERVICE);
	        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
	        strmac = wifiInfo.getMacAddress();
	        if (strmac.length() > 0)
	        	Log.i("Wi-FI Mac = ", strmac);
        } catch (Exception e) {
	        	Log.e("Wi-FI MacAdress = ", "Error");
        }
		
		return strmac;
	}
	
	public static String GetIccid(TelephonyManager telManager) {
//		TelephonyManager telManager = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
        //String strSimCountry = telManager.getSimCountryIso();
        //Log.i("SIM COUNTRY = ", strSimCountry);
	        
        String strSerialNumber =  telManager.getSimSerialNumber();
        return strSerialNumber;
	}
	
	public static String GetImei(TelephonyManager telManager) {
		String strDevice = telManager.getDeviceId();
		return strDevice;
	}
	
	public static String GetFakeUDID(TelephonyManager telManager) {
		String struuid = "";
		struuid = telManager.getDeviceId();
		struuid += struuid;
		struuid += struuid;
		struuid = struuid.substring(0, 40);
		
		return struuid;
	}
}
