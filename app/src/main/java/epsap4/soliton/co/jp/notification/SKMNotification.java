package epsap4.soliton.co.jp.notification;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import java.text.SimpleDateFormat;

import epsap4.soliton.co.jp.StringList;
import epsap4.soliton.co.jp.dbalias.ItemAlias;

/**
 * Created by daoanhtung on 1/17/2017.
 */

public class SKMNotification {
    // Param in SKMNotification
    public static SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
    public static final int DAY_MILLIS = 24 * 60 * 60 * 1000;

    /**
     * @param context
     * @param itemAlias
     */
    public static void updateStatusNotification(Context context, ItemAlias itemAlias) {
        if (itemAlias.getStatusNotificationFinalDate() == 0 && itemAlias.getStatusNotificationBeforeFinalDate() == 0) {
            cancleAlarm(context, itemAlias);
        }
        if (itemAlias.getFinalDate() > System.currentTimeMillis()) {
            if (itemAlias.getFinalDate() - itemAlias.getBeforeFinalDate() > System.currentTimeMillis()) {
                if (itemAlias.getStatusNotificationBeforeFinalDate() == 1) {
                    setRepeatingAlarm(context, itemAlias, itemAlias.getFinalDate() - itemAlias.getBeforeFinalDate());
                } else {
                    if (itemAlias.getStatusNotificationFinalDate() == 1) {
                        setRepeatingAlarm(context, itemAlias, itemAlias.getFinalDate());
                    }
                }
            } else {
                if (itemAlias.getStatusNotificationFinalDate() == 1) {
                    setRepeatingAlarm(context, itemAlias, itemAlias.getFinalDate());
                }
            }
        }
    }

    /**
     * This method Cancle Alarm
     *
     * @param context
     * @param itemAlias
     */
    public static void cancleAlarm(Context context, ItemAlias itemAlias) {
        AlarmManager alarmMgr = (AlarmManager) context.getSystemService(context.ALARM_SERVICE);
        Intent intent = new Intent(context, AlarmReceiver.class);
        PendingIntent alarmIntent = PendingIntent.getBroadcast(context, itemAlias.getiD(), intent, 0);
        alarmMgr.cancel(alarmIntent);
    }

    /**
     * This method set Repeating Alarm by time Mills for SKMNotification
     *
     * @param context
     * @param itemAlias
     * @param triggerAtMillis
     */
    public static void setRepeatingAlarm(Context context, ItemAlias itemAlias, long triggerAtMillis) {
        AlarmManager alarmMgr = (AlarmManager) context.getSystemService(context.ALARM_SERVICE);
        Intent intent = new Intent(context, AlarmReceiver.class);
        intent.putExtra(StringList.m_str_alias_skm, itemAlias.getiD());
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, itemAlias.getiD(), intent, 0);
        alarmMgr.setRepeating(AlarmManager.RTC_WAKEUP, triggerAtMillis, 0, pendingIntent);
    }
}
