package epsap4.soliton.co.jp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import epsap4.soliton.co.jp.activity.BlockActivity;

//import android.util.Log;

/////////////////////////////////////////////
//Restrictionブロックを受信する.
//受信後、BlockActivityを起動
/////////////////////////////////////////////

/** BroadcastReceiver Class **/
public class BlockReceiver extends BroadcastReceiver {
	@Override
	public void onReceive(Context context, Intent intent) {
		String action = intent.getAction();
		if (action == null) {
			return;
		}
		
		if (action.equals("epsap3.soliton.co.jp.block.ViewAction.VIEW")) {
			Log.i("MDM Client BlockReceiver", "epsap3.soliton.co.jp.block.ViewAction.VIEW");
			// Block Activity
			Intent ViewActivity = new Intent(context, BlockActivity.class);
			ViewActivity.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_TASK_ON_HOME);
			context.startActivity(ViewActivity);
		}

	}
};