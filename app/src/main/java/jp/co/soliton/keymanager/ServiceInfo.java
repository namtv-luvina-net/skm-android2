package jp.co.soliton.keymanager;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.Context;

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
			for (RunningServiceInfo svc:runningService) {
				if (svc.service.getShortClassName().equals(serviceName/*.SacService*/)) {
					run = true;
					break;
				}
			}
		}
		
		return run;
	}
}