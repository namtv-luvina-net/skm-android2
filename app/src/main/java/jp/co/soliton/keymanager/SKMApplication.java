package jp.co.soliton.keymanager;

import android.app.Activity;
import android.app.Application;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import jp.co.soliton.keymanager.common.CommonUtils;

/**
 * Created by luongdolong on 4/5/2017.
 */

public class SKMApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        if (!CommonUtils.getPrefBoolean(getApplicationContext(), StringList.KEY_OPENED_APP)) {
            CommonUtils.putPref(getApplicationContext(), StringList.KEY_OPENED_APP, new Boolean(true));
            CommonUtils.putPref(getApplicationContext(), StringList.KEY_NOTIF_ENABLE_FLAG, new Boolean(true));
            CommonUtils.putPref(getApplicationContext(), StringList.KEY_NOTIF_ENABLE_BEFORE_FLAG, new Boolean(true));
            CommonUtils.putPref(getApplicationContext(), StringList.KEY_NOTIF_ENABLE_BEFORE, new Integer(14));
        }
	    LogCtrl.getInstance(this).createNameLogFile();
	    registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacks() {
		    @Override
		    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
			    if (!getResources().getBoolean(R.bool.isTablet)) {
				    activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
			    } else {
				    activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
			    }
		    }

		    @Override
		    public void onActivityStarted(Activity activity) {

		    }

		    @Override
		    public void onActivityResumed(Activity activity) {

		    }

		    @Override
		    public void onActivityPaused(Activity activity) {

		    }

		    @Override
		    public void onActivityStopped(Activity activity) {

		    }

		    @Override
		    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

		    }

		    @Override
		    public void onActivityDestroyed(Activity activity) {

		    }
	    });
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
    }
}

