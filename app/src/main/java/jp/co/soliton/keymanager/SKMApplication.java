package jp.co.soliton.keymanager;

import android.app.Activity;
import android.app.Application;
import android.app.NotificationManager;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.os.Bundle;
import jp.co.soliton.keymanager.alarm.AlarmReceiver;
import jp.co.soliton.keymanager.common.CommonUtils;
import jp.co.soliton.keymanager.dbalias.ElementApply;
import jp.co.soliton.keymanager.dbalias.ElementApplyManager;

import java.util.List;

/**
 * Created by luongdolong on 4/5/2017.
 */

public class SKMApplication extends Application {

	public static final boolean SKM_DEBUG = BuildConfig.BUILD_TYPE.equals("debug");
	public static final boolean SKM_TRACE = BuildConfig.BUILD_TYPE.equals("trace");

	private static Context context;

	public static Context getAppContext() {
		return SKMApplication.context;
	}

    @Override
    public void onCreate() {
        super.onCreate();
		SKMApplication.context = getApplicationContext();
        if (!CommonUtils.getPrefBoolean(SKMApplication.context, StringList.KEY_OPENED_APP)) {
            CommonUtils.putPref(SKMApplication.context, StringList.KEY_OPENED_APP, new Boolean(true));
            CommonUtils.putPref(SKMApplication.context, StringList.KEY_NOTIF_ENABLE_FLAG, new Boolean(true));
            CommonUtils.putPref(SKMApplication.context, StringList.KEY_NOTIF_ENABLE_BEFORE_FLAG, new Boolean(true));
            CommonUtils.putPref(SKMApplication.context, StringList.KEY_NOTIF_ENABLE_BEFORE, new Integer(14));
        } else if (!CommonUtils.getPrefBoolean(SKMApplication.context, StringList.UPDATE_METHOD_SETUP_NOTIFY)) {
	        CommonUtils.putPref(SKMApplication.context, StringList.UPDATE_METHOD_SETUP_NOTIFY, new Boolean(true));
	        changeLogicSetUpNotification();
        }
		String appVer = context.getResources().getString(R.string.app_name) + " " + context.getResources().getString(R
				.string.main_versionname) + BuildConfig.VERSION_NAME + "." + BuildConfig.BUILD_NUM + (BuildConfig
				.BUILD_TYPE.equals("debug") ? "d" : BuildConfig.BUILD_TYPE.equals("trace") ? "t" : "");
	    LogCtrl.getInstance().info(appVer);

	    registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacks() {
		    @Override
		    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
				LogCtrl.getInstance().debug("Application: " + activity.getLocalClassName() + " Created");
			    if (!getResources().getBoolean(R.bool.isTablet)) {
				    activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
			    } else {
					if (Build.VERSION.SDK_INT == 26 && activity.getLocalClassName().contains("SettingTabletActivity")) {
				        activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
				    } else {
				        activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
				    }
			    }
		    }

		    @Override
		    public void onActivityStarted(Activity activity) {
				LogCtrl.getInstance().debug("Application: " + activity.getLocalClassName() + " Started");
		    }

		    @Override
		    public void onActivityResumed(Activity activity) {
				LogCtrl.getInstance().debug("Application: " + activity.getLocalClassName() + " Resumed");
		    }

		    @Override
		    public void onActivityPaused(Activity activity) {
				LogCtrl.getInstance().debug("Application: " + activity.getLocalClassName() + " Paused");
		    }

		    @Override
		    public void onActivityStopped(Activity activity) {
				LogCtrl.getInstance().debug("Application: " + activity.getLocalClassName() + " Stopped");
		    }

		    @Override
		    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

		    }

		    @Override
		    public void onActivityDestroyed(Activity activity) {
				LogCtrl.getInstance().debug("Application: " + activity.getLocalClassName() + " Destroyed");
		    }
	    });
    }

	private void changeLogicSetUpNotification() {
		NotificationManager notificationManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
		notificationManager.cancelAll();
		ElementApplyManager elementMgr = ElementApplyManager.getInstance(context);
		List<ElementApply> lsElement = elementMgr.getAllCertificate();
		AlarmReceiver alarm = new AlarmReceiver();
		for (ElementApply el : lsElement) {
			alarm.addAlarmExpiredIfNeed(context, el);
			alarm.addAlarmBeforeIfNeed(context, el);
		}
	}

	@Override
    public void onTerminate() {
        super.onTerminate();
    }
}

