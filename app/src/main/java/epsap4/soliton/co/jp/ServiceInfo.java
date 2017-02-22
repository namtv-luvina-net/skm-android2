package epsap4.soliton.co.jp;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.Context;
import android.util.Log;

import java.util.List;

public class ServiceInfo {
	static public boolean IsRunning(Context context, String serviceName) {
		boolean run = false;
		
		if ((context == null) || (serviceName == null)) {
			return false;
		}
		
		ActivityManager activityManager = (ActivityManager)context.getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningServiceInfo> runningService = activityManager.getRunningServices(100);
		if (runningService != null) {
			Log.i("SAC", "*** start check service");
			for (RunningServiceInfo svc:runningService) {
				Log.i("SAC", svc.service.getShortClassName());
				if (svc.service.getShortClassName().equals(serviceName/*.SacService*/)) {
					Log.d("SAC", "Found!!! -> " + serviceName);
					run = true;
					break;
				}
			}
			Log.i("SAC", "*** stop check service");
		}
		
		return run;
	}
}