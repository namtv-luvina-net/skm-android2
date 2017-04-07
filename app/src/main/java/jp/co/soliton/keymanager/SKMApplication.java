package jp.co.soliton.keymanager;

import android.app.Application;

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
            CommonUtils.putPref(getApplicationContext(), StringList.KEY_NOTIF_ENABLE_BEFORE, new Integer(7));
        }
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
    }
}

