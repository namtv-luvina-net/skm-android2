package jp.co.soliton.keymanager;

import android.app.admin.DeviceAdminReceiver;
import android.content.Context;
//import android.app.admin.DevicePolicyManager;
import android.content.Intent;
//import android.content.ComponentName;
import android.util.Log;
import android.widget.Toast;

public class EpsapAdminReceiver extends DeviceAdminReceiver {

    @Override
    public void onEnabled(Context context, Intent intent) {
    	Log.i("EpsapAdminReceiver", "onEnabled");
        showToast(context, context.getText(R.string.admin_receiver_enabled)/*"KeyManager Device Admin: enabled"*/);
    }

    @Override
    public CharSequence onDisableRequested(Context context, Intent intent) {
    	Log.i("EpsapAdminReceiver", "onDisableRequested");
        return context.getText(R.string.admin_receiver_disabled_request);//"This is an optional message to warn the user about disabling.";
    }

    @Override
    public void onDisabled(Context context, Intent intent) {
    	Log.i("EpsapAdminReceiver", "onDisabled");
        showToast(context, context.getText(R.string.admin_receiver_disabled)/*"Sample Device Admin: disabled"*/);
    }

    @Override
    public void onPasswordChanged(Context context, Intent intent) {
    	Log.i("EpsapAdminReceiver", "onPasswordChanged");
        showToast(context, context.getText(R.string.admin_receiver_pwchange)/*"Sample Device Admin: pw changed"*/);
    }

    void showToast(Context context, CharSequence msg) {
    	Log.i("EpsapAdminReceiver", "showToast");
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
    }
}