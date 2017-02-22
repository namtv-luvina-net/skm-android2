package epsap4.soliton.co.jp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/** BroadcastReceiver Class **/
/////////////////////////////////////////////
//端末起動時に受信する.
//受信後、SacServiceを起動する
/////////////////////////////////////////////
public class BootReceiver extends BroadcastReceiver {
	@Override
	public void onReceive(Context context, Intent intent) {
		
		String action = intent.getAction();
		if (action == null) {
			return;
		}
		
		if (action.equals(Intent.ACTION_BOOT_COMPLETED)) {
        	// Boot completed
        	Log.i("MDM Client BootReceiver", "Intent.ACTION_BOOT_COMPLETED !!!");
        	//Intent serviceIntent = new Intent(context, SacService.class );
    		//context.startService(serviceIntent);
        	
        	// Restrictions情報ファイルを読み込む.
        	RestrictionsFlgs restriction = new RestrictionsFlgs();
        	boolean bRet = restriction.ReadAndSetRestictionsInfo(context);
        	
        	if (bRet == false) {
        		Log.i("MDM Client BootReceiver", "No Restrictions Return");
        		return;
        	}
        	
        	// 読み込んだ情報をRestiction制御クラスに引き渡して、SacServiceを実行
        	RestrictionsControl rest_ctrl = new RestrictionsControl(context);
        	rest_ctrl.SrartMoniter(restriction);
        }
		
		else if(action.equals(Intent.ACTION_SCREEN_ON)) {
			Log.i("MDM Client BootReceiver", "Intent.ACTION_SCREEN_ON !!!");
			
			// Restrictions情報ファイルを読み込む.
        	RestrictionsFlgs restriction = new RestrictionsFlgs();
        	boolean bRet = restriction.ReadAndSetRestictionsInfo(context);
        	
        	if (bRet == false) {
        		Log.i("MDM Client BootReceiver", "No Restrictions Return");
        		return;
        	}
        	
        	// 読み込んだ情報をRestiction制御クラスに引き渡して、SacServiceを実行
        	RestrictionsControl rest_ctrl = new RestrictionsControl(context);
        	rest_ctrl.SrartMoniter(restriction);
		} else if(action.equals(Intent.ACTION_SCREEN_OFF)) {
			Log.i("MDM Client BootReceiver", "Intent.ACTION_SCREEN_OFF !!!");
			// SacServiceを落としてしまうと、再起動しないので駄目だ..
		//	RestrictionsControl rest_ctrl = new RestrictionsControl(context);
		}
	}
};