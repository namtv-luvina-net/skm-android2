package jp.co.soliton.keymanager;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import jp.co.soliton.keymanager.common.CommonUtils;

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
				    activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
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

    @Override
    public void onTerminate() {
        super.onTerminate();
    }
}

