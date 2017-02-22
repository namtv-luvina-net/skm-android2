package epsap4.soliton.co.jp.notification;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.NotificationCompat;

import java.util.Calendar;

import epsap4.soliton.co.jp.R;
import epsap4.soliton.co.jp.StringList;
import epsap4.soliton.co.jp.activity.ListCertActivity;
import epsap4.soliton.co.jp.dbalias.DatabaseHandler;
import epsap4.soliton.co.jp.dbalias.ItemAlias;


/**
 * Created by daoanhtung on 1/4/2017.
 */

public class AlarmReceiver extends BroadcastReceiver {
    // Param in AlarmReceiver
    private NotificationCompat.Builder notBuilder;
    private DatabaseHandler databaseHandler;
    private ItemAlias itemAlias;

    @Override
    public void onReceive(Context context, Intent intent) {
        int id = intent.getIntExtra(StringList.m_str_alias_skm, 1);
        databaseHandler = new DatabaseHandler(context);
        itemAlias = databaseHandler.getAlias(id);
        setNotification(context);
    }

    /**
     * This method display notification on pannel
     *
     * @param context
     */
    private void setNotification(Context context) {
        if (itemAlias != null) {
            notBuilder = new NotificationCompat.Builder(context);
            // when user click notification, notification will remove panel
            notBuilder.setAutoCancel(true);
            notBuilder.setSmallIcon(R.mipmap.type04_72px);
            checkNotification(context);
            // Create Intent
            Intent intentnotification = new Intent(context, ListCertActivity.class);
            // PendingIntent.getActivity(..) will start new Activity,
            // when user click notification will start ListCertActivity of app
            PendingIntent pendingIntent = PendingIntent.getActivity(context, itemAlias.getiD(),
                    intentnotification, PendingIntent.FLAG_UPDATE_CURRENT);
            notBuilder.setContentIntent(pendingIntent);
            NotificationManager notificationService =
                    (NotificationManager) context.getSystemService(context.NOTIFICATION_SERVICE);
            // Build notification and send system
            Notification notification = notBuilder.build();
            notificationService.notify(itemAlias.getiD(), notification);

        }
    }

    /**
     * This method check notification is experid notìication or before experid notification
     *
     * @param context
     */
    private void checkNotification(Context context) {
        if (itemAlias.getFinalDate() < System.currentTimeMillis()) {
            notBuilder.setContentTitle(context.getString(R.string.label_date_final) + ": " + itemAlias.getNameSubjectDN());
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(itemAlias.getFinalDate());
            String datefinal = SKMNotification.format.format(calendar.getTime());
            notBuilder.setContentText(datefinal);
        } else {
            notBuilder.setContentTitle(context.getString(R.string.msg_notification_before) + ": " + itemAlias.getNameSubjectDN());
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(itemAlias.getFinalDate() - itemAlias.getBeforeFinalDate());
            String beoforedatefinal = SKMNotification.format.format(calendar.getTime());
            int numberday = (int) (itemAlias.getBeforeFinalDate() / (SKMNotification.DAY_MILLIS));
            notBuilder.setContentText(beoforedatefinal + "  " + context.getString(R.string.label_day_before) + ": " + String.valueOf(numberday));
            if(itemAlias.getStatusNotificationFinalDate()==1){
            SKMNotification.setRepeatingAlarm(context, itemAlias, itemAlias.getFinalDate());
            }
        }
    }
}
