package epsap4.soliton.co.jp;


import android.content.Context;
import android.content.Intent;
import android.util.Log;


public class RestrictionsControl {
	
	private Context context;
	
	public RestrictionsControl(Context con) {
    	context = con;
     	
    	// SacServiceが起動していたら一旦止める.
		boolean running = ServiceInfo.IsRunning(context, ".SacService");
		if(running) {
			Log.i("RestrictionControll", "SacService Stop!!");
			Intent intent = new Intent(context, SacService.class);
	    	
			context.stopService(intent);
		}
    }
	
	public void SrartMoniter(RestrictionsFlgs flgs) {
		// SacServiceのIntentを作成してサービス開始.
		// ★懸案：各フラグをどうやってLogMonitorまで引き継ぐか？
		
		Log.i("RestrictionControll", "SrartMoniter start.");
		
		Intent intent = new Intent(context, SacService.class);
		
		intent.putExtra("restrictionlabel", /*this.getClass()*/flgs);
		
		if (context.startService(intent) != null) {
        	//bRet = true;
        }
		
	}
	

}