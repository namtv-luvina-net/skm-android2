package jp.co.soliton.keymanager.alarm;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import jp.co.soliton.keymanager.NotificationLogCtrl;
import jp.co.soliton.keymanager.dbalias.ElementApply;
import jp.co.soliton.keymanager.dbalias.ElementApplyManager;

import java.util.List;

public class AutoStartUp extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
	    NotificationLogCtrl.getInstance().reboot();
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

}