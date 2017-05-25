package jp.co.soliton.keymanager.alarm;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.PowerManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;

import jp.co.soliton.keymanager.R;
import jp.co.soliton.keymanager.StringList;
import jp.co.soliton.keymanager.activity.AlarmReapplyActivity;
import jp.co.soliton.keymanager.common.DateUtils;
import jp.co.soliton.keymanager.dbalias.ElementApply;
import jp.co.soliton.keymanager.dbalias.ElementApplyManager;

public class AlarmReceiver extends BroadcastReceiver {

    final public static String ONE_TIME = "onetime";
    @Override
    public void onReceive(Context context, Intent intent) {
        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "YOUR TAG");
        //Acquire the lock
        wl.acquire();

        //You can do the processing here update the widget/remote views.
        Bundle extras = intent.getExtras();

        String id = extras.getString(StringList.ELEMENT_APPLY_ID, "");

        ElementApplyManager mgr = new ElementApplyManager(context);
        ElementApply element = mgr.getElementApply(id);
	    Bitmap bmLarge = getLargeIcon(context);
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(context)
		                .setLargeIcon(bmLarge)
                        .setSmallIcon(getNotificationIcon())
		                .setColor(context.getResources().getColor(R.color.product_icon_notification))
                        .setContentTitle(context.getString(R.string.notif_title))
                        .setContentText(element.getcNValue());
        Intent resultIntent = new Intent(context, AlarmReapplyActivity.class);
        resultIntent.putExtra(StringList.ELEMENT_APPLY_ID, id);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addParentStack(AlarmReapplyActivity.class);
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(
                        0,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        mBuilder.setContentIntent(resultPendingIntent);
        mBuilder.setAutoCancel(true);
        NotificationManager mNotificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        final int _id = (int) System.currentTimeMillis();
        mNotificationManager.notify(_id, mBuilder.build());

        //Release the lock
        wl.release();
    }

	private int getNotificationIcon() {
		if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
			return R.mipmap.ic_notification5;
		}
		return R.mipmap.ic_notification;
	}

    public void setOnetimeTimer(Context context, String elementId){

        try {
            AlarmManager am=(AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
            Intent intent = new Intent(context, AlarmReceiver.class);
            intent.putExtra(StringList.ELEMENT_APPLY_ID, elementId);
            final int _id = (int) System.currentTimeMillis();
            PendingIntent pi = PendingIntent.getBroadcast(context, _id, intent, PendingIntent.FLAG_ONE_SHOT);
            ElementApplyManager mgr = new ElementApplyManager(context);
            ElementApply element = mgr.getElementApply(elementId);
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
            Date expirationDate = formatter.parse(element.getExpirationDate());

            am.set(AlarmManager.RTC_WAKEUP, expirationDate.getTime(), pi);
//            am.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + 10000, pi);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void cancelNotification(Context context) {
        NotificationManager notificationManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancelAll();
    }

    public void setupNotification(Context context) {
        cancelNotification(context);
        ElementApplyManager elementMgr = new ElementApplyManager(context);
        List<ElementApply> lsElement = elementMgr.getAllCertificate();
        Calendar cal = Calendar.getInstance();
        int index = 0;
        int startIndex = 1000;
        for (ElementApply el : lsElement) {
            if (!el.isNotiEnableFlag() && !el.isNotiEnableBeforeFlag()) {
                continue;
            }
            Date expirationDate = DateUtils.convertSringToDateSystemTime(el.getExpirationDate());
            cal.setTime(expirationDate);
            if (el.isNotiEnableFlag() && expirationDate.after(Calendar.getInstance().getTime())) {
                setAlarm(context, String.valueOf(el.getId()), startIndex + index, expirationDate.getTime());
                index++;
            }
            int before = el.getNotiEnableBefore();
            cal.add(Calendar.DAY_OF_MONTH, before * -1);
            if (el.isNotiEnableBeforeFlag() && cal.getTime().after(Calendar.getInstance().getTime())) {
                setAlarm(context, String.valueOf(el.getId()), startIndex + index, cal.getTime().getTime());
                index++;
            }
        }
    }

	private void setAlarm(Context context, String value, int requestCode, long time) {
		AlarmManager am =(AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
		Intent intent = new Intent(context, AlarmReceiver.class);
		intent.removeExtra(StringList.ELEMENT_APPLY_ID);
		intent.putExtra(StringList.ELEMENT_APPLY_ID, value);
		PendingIntent pi = PendingIntent.getBroadcast(context, requestCode, intent, PendingIntent.FLAG_UPDATE_CURRENT);
		am.set(AlarmManager.RTC_WAKEUP, time, pi);
	}

	public Bitmap getLargeIcon(Context context) {
		Bitmap largeIcon = BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_notification);
		int width = (int) context.getResources().getDimension(android.R.dimen.notification_large_icon_width);
		int height = (int) context.getResources().getDimension(android.R.dimen.notification_large_icon_height);
		if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.LOLLIPOP) {
			width = (int) (width * 0.67f);
			height = (int) (height * 0.67f);
		}
		largeIcon = Bitmap.createScaledBitmap(largeIcon, width, height, false);
		return largeIcon;
	}
}