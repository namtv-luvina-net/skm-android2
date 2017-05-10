package jp.co.soliton.keymanager;

import java.util.UUID;

import android.app.Activity;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Build;
import android.util.Log;

///////////////////////////////////////////
//Configration 設定機能
///////////////////////////////////////////
public class ConfigrationProcess {
	
	private Context m_ctx;
	DevicePolicyManager m_DPM;
    ComponentName m_DeviceAdmin;
	LogCtrl logCtrl;
    
    
	public ConfigrationProcess(Context ctx, DevicePolicyManager dpm, ComponentName cmpname) {
		m_ctx = ctx;
		logCtrl = LogCtrl.getInstance(ctx);
		
		// initialize Device policy manager, device administrator
    	m_DPM = dpm;
    	m_DeviceAdmin = cmpname;
	}
	
    
	// Passcode Policy Payload
	public void allowSimple(boolean type) {
		Log.i("ConfigrationProcess", "allowSimple");
		try {
			if (type == true) {
				// true	    		
	    		int i_qual = m_DPM.getPasswordQuality(m_DeviceAdmin);
	    		Log.i("DeviceAdmin passwordquality= ", Integer.toString(i_qual));
	    		
	    		m_DPM.setPasswordQuality(m_DeviceAdmin, DevicePolicyManager.PASSWORD_QUALITY_UNSPECIFIED);
	    		
	    		i_qual = m_DPM.getPasswordQuality(m_DeviceAdmin);
	    		Log.i("DeviceAdmin passwordquality2= ", Integer.toString(i_qual));
			}
		} catch (Exception e) {
			logCtrl.loggerError("ConfigrationProcess::allowSimple::Exception : " + e.toString());
			e.printStackTrace();
		}
	}
	
	public void requireAlphanumeric(boolean type) {
		Log.i("ConfigrationProcess", "requireAlphanumeric");
		try {
			if (type == true) {
				// true
	    		
	    		int i_qual = m_DPM.getPasswordQuality(m_DeviceAdmin);
	    		Log.i("DeviceAdmin passwordquality= ", Integer.toString(i_qual));
	    		
	    		m_DPM.setPasswordQuality(m_DeviceAdmin, DevicePolicyManager.PASSWORD_QUALITY_ALPHANUMERIC);
	    		
	    		i_qual = m_DPM.getPasswordQuality(m_DeviceAdmin);
	    		Log.i("DeviceAdmin passwordquality2= ", Integer.toString(i_qual));
			}
		} catch (Exception e) {
			logCtrl.loggerError("ConfigrationProcess::requireAlphanumeric::Exception : " + e.toString());
			e.printStackTrace();
		}
	}
	
	public void minLength(String length) {
		Log.i("ConfigrationProcess", "minLength");
		try {
			if(length.length() < 1) return;
    		
			int i_length = Integer.parseInt(length);
    		
    		int i_qual =m_DPM.getPasswordMinimumLength(m_DeviceAdmin);
    		Log.i("DeviceAdmin minLength= ", Integer.toString(i_qual));
    		
    		m_DPM.setPasswordMinimumLength(m_DeviceAdmin, i_length);

    		i_qual =m_DPM.getPasswordMinimumLength(m_DeviceAdmin);
    		Log.i("DeviceAdmin minLength2= ", Integer.toString(i_qual));
		} catch (Exception e) {
			logCtrl.loggerError("ConfigrationProcess::minLength::Exception : " + e.toString());
			e.printStackTrace();
		}
	}
	
	public void minComplexChars(String mincmp) {
		Log.i("ConfigrationProcess", "minComplexChars");
		try {
			if(mincmp.length() < 1) return;
    		
    		// ★★現状、対応されているAPIが存在しない★★
		} catch (Exception e) {
			logCtrl.loggerError("ConfigrationProcess::minComplexChars::Exception : " + e.toString());
			e.printStackTrace();
		}
	}
	
	public void maxPINAgeInDays(String days) {
		Log.i("ConfigrationProcess", "maxPINAgeInDays");
		try {
			if(days.length() < 1) return;
			
			// OSバージョン確認
			int sdk_int_version = Build.VERSION.SDK_INT;
			Log.i("RunConfigrationChild Version= ", Integer.toString(sdk_int_version));
			if(sdk_int_version < Build.VERSION_CODES.HONEYCOMB) return;
    		
    		long i_maxpin = Long.parseLong(days);
    		
    		//SharedPreferences prefs = getSamplePreferences(this);
    		//final long pwExpiration = prefs.getLong(PREF_PASSWORD_EXPIRATION_TIMEOUT, 0L);
    		long l_maxpin = i_maxpin * 86400 * 1000;	// ★ 入力日数 * 1日あたりの秒数 * ミリセカンド
    		
    		Log.i("DeviceAdmin maxpin= ", Long.toString(l_maxpin));

    		m_DPM.setPasswordExpirationTimeout(m_DeviceAdmin, l_maxpin);	// ★対象のAPIはAPIレベルが11なので、Android2.2では動かない.
    		//i_maxpin =mDPM.getPasswordMinimumLength(mDeviceAdmin);
    		
    		l_maxpin = m_DPM.getPasswordExpirationTimeout(m_DeviceAdmin);

    		Log.i("DeviceAdmin passwordquality2= ", Long.toString(l_maxpin));
		} catch (Exception e) {
			//e.printStackTrace();
			logCtrl.loggerError("ConfigrationProcess::maxPINAgeInDays::Exception : " + e.toString());
			Log.e("maxPINAgeInDays error:", e.toString());
		}
	}
	
	public void maxInactivity(String minites) {
		Log.i("ConfigrationProcess", "maxInactivity");
		try {
			if(minites.length() < 1) return;
    		
			long i_maxinac = Long.parseLong(minites);
    		
    		long i_qual =m_DPM.getMaximumTimeToLock(m_DeviceAdmin);
    		Log.i("DeviceAdmin maxInactivity= ", Long.toString(i_qual));
    		
    		m_DPM.setMaximumTimeToLock(m_DeviceAdmin, i_maxinac * 60000);	// ×60sec

    		Log.i("DeviceAdmin maxInactivity2= ", Long.toString(i_maxinac * 60000));
    		
    		//m_DPM.lockNow();		// 即時デバイスロック(デバッグ用)
		} catch (Exception e) {
			logCtrl.loggerError("ConfigrationProcess::maxInactivity::Exception : " + e.toString());
			e.printStackTrace();
		}
	}
	
	public void pinHistory(String history) {
		Log.i("ConfigrationProcess", "pinHistory");
		try {
			if(history.length() < 1) return;
			
			// OSバージョン確認
			int sdk_int_version = Build.VERSION.SDK_INT;
			Log.i("RunConfigrationChild Version= ", Integer.toString(sdk_int_version));
			if(sdk_int_version < Build.VERSION_CODES.HONEYCOMB) return;
    		
			int i_his = Integer.parseInt(history);
    		
    		int i_qual =m_DPM.getPasswordHistoryLength(m_DeviceAdmin);
    		Log.i("DeviceAdmin pinHistory= ", Integer.toString(i_qual));
    		
    		m_DPM.setPasswordHistoryLength(m_DeviceAdmin, i_his);	// ×60sec

    		i_qual =m_DPM.getPasswordHistoryLength(m_DeviceAdmin);
    		Log.i("DeviceAdmin pinHistory2= ", Integer.toString(i_qual));
		} catch (Exception e) {
			logCtrl.loggerError("ConfigrationProcess::pinHistory::Exception : " + e.toString());
			e.printStackTrace();
		}
	}
	
	public void maxGracePeriod(String maxgrace) {
		Log.i("ConfigrationProcess", "maxGracePeriod");
		try {
			if(maxgrace.length() < 1) return;
    		
    		// ★★現状、対応されているAPIが存在しない★★
		} catch (Exception e) {
			logCtrl.loggerError("ConfigrationProcess::maxGracePeriod::Exception : " + e.toString());
			e.printStackTrace();
		}
	}
	
	public void maxFailedAttempts(String number) {
		Log.i("ConfigrationProcess", "maxFailedAttempts");
		try {
			if(number.length() < 1) return;
    		
			int i_maxFaild = Integer.parseInt(number);
    		
    		m_DPM.setMaximumFailedPasswordsForWipe(m_DeviceAdmin, i_maxFaild);
    		
    		int i_qual = m_DPM.getMaximumFailedPasswordsForWipe(m_DeviceAdmin);
    		Log.i("DeviceAdmin maxFailedAttempts2= ", Integer.toString(i_qual));
		} catch (Exception e) {
			logCtrl.loggerError("ConfigrationProcess::maxFailedAttempts::Exception : " + e.toString());
			e.printStackTrace();
		}
	}
	
	// Passcode ここまで...=======>
	
	public void PayloadCertificateUUID(String str_id) {
		Log.i("ConfigrationProcess", "PayloadCertificateUUID");
		try {
			if(str_id.length() < 1) return;
    		
			// Get UUID
	        SharedPreferences pref =((Activity)m_ctx).getPreferences(Context.MODE_WORLD_READABLE|Context.MODE_WORLD_WRITEABLE);
	    	String tag = "uuid";//"uuid+m_app_name";
	        String uuuid = pref.getString(tag, null);	//get
	        
	        Log.v("onCreate", "get_uuid " + uuuid ); 
	        
	        // Set UUID
	        UUID uie = UUID.fromString(str_id);		// (1) この手順は必要ないが、後学のために残しておく
	        
	    	Editor e = pref.edit();
	    	e.putString(tag, uie.toString());		// (2) (1)の手順を行ったため、再度Stringに戻す
	    	e.commit();
	    	
	    	// 再度取得
	    	uuuid = pref.getString(tag, null);	//get
	    	
	        Log.v("onCreate", "get_uuid2 " + uuuid );
			
		} catch (Exception e) {
			logCtrl.loggerError("ConfigrationProcess::PayloadCertificateUUID::Exception : " + e.toString());
			e.printStackTrace();
		}
	}
	
	// 即時Wipe
	public void RunWipe() {
		m_DPM.wipeData(0);
	}
	
	// 即時Lock
	public void RunLock() {
		m_DPM.lockNow();
	}
}