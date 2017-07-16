package jp.co.soliton.keymanager.mdm;

import android.app.admin.DevicePolicyManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import jp.co.soliton.keymanager.LogCtrl;

public class MDMBootReceiver extends BroadcastReceiver {

	DevicePolicyManager m_DPM;
    ComponentName m_DeviceAdmin;
    
//	public MDMBootReceiver(DevicePolicyManager dpm, ComponentName cmpname) {
		// TODO 自動生成されたコンストラクター・スタブ
//		m_DPM = dpm;
//		m_DeviceAdmin = cmpname;
//	}

	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO 自動生成されたメソッド・スタブ
		String action = intent.getAction();
		if (action == null) {
			return;
		}
		
		
		if (action.equals(Intent.ACTION_BOOT_COMPLETED) 
				|| action.equals(Intent.ACTION_SCREEN_ON)) {
        	// Boot completed
			LogCtrl.getInstance().info("MDMBootReceiver: onReceive ACTION_BOOT_COMPLETED or ACTION_SCREEN_ON");
        	//Intent serviceIntent = new Intent(context, SacService.class );
    		//context.startService(serviceIntent);
        	
        	// MDM情報ファイルを読み込む.
        	MDMFlgs mdm = new MDMFlgs();
        	boolean bRet = mdm.ReadAndSetScepMdmInfo(context);
        	
        	if (bRet == false) {
        		return;
        	}

        	LogCtrl.getInstance().info("MDMBootReceiver: onReceive Start MDM Service");
        	
        	// 読み込んだ情報をMDM制御クラスに引き渡して、SacServiceを実行
        	MDMControl MDM_ctrl = new MDMControl(context, mdm.GetUDID());
        	MDM_ctrl.startService(mdm);
		

		} else if(action.equals(Intent.ACTION_SCREEN_OFF)) {
			// SacServiceを落としてしまうと、再起動しないので駄目だ..
		//	RestrictionsControl rest_ctrl = new RestrictionsControl(context);
		}

	}

}
