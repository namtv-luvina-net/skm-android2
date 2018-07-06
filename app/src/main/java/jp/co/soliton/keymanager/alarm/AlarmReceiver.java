package jp.co.soliton.keymanager.alarm;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import jp.co.soliton.keymanager.R;
import jp.co.soliton.keymanager.StringList;
import jp.co.soliton.keymanager.activity.AlarmReapplyActivity;
import jp.co.soliton.keymanager.activity.MenuAcivity;
import jp.co.soliton.keymanager.common.DateUtils;
import jp.co.soliton.keymanager.dbalias.ElementApply;
import jp.co.soliton.keymanager.dbalias.ElementApplyManager;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class AlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
	    boolean isTablet = context.getResources().getBoolean(R.bool.isTablet);
        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "YOUR TAG");
        //Acquire the lock
        wl.acquire();

        //You can do the processing here update the widget/remote views.
        Bundle extras = intent.getExtras();

	    String id = extras.getString(StringList.ELEMENT_APPLY_ID, "");
		ElementApplyManager mgr = ElementApplyManager.getInstance(context);
		ElementApply element = mgr.getElementApply(id);

		NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context
				.NOTIFICATION_SERVICE);
		String NOTIFICATION_CHANNEL_ID = "jp.co.soliton.keymanager.ID";
		String NOTIFICATION_CHANNEL_NAME = "jp.co.soliton.keymanager.NAME";
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
			NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID,
					NOTIFICATION_CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH);
			// Configure the notification channel.
			notificationChannel.setDescription("Channel description");
			notificationChannel.enableLights(true);
			notificationChannel.setLightColor(Color.RED);
			notificationChannel.setVibrationPattern(new long[]{0, 1000, 500, 1000});
			notificationChannel.enableVibration(true);
			notificationManager.createNotificationChannel(notificationChannel);
		}

		Bitmap bmLarge = getLargeIcon(context);

		NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID);

		mBuilder.setLargeIcon(bmLarge)
				.setSmallIcon(getNotificationIcon())
				.setColor(context.getResources().getColor(R.color.product_icon_notification))
				.setContentTitle(context.getString(R.string.notif_title))
				.setContentText(element.getcNValue());
		Intent resultIntent;
		if (isTablet) {
			resultIntent = new Intent(context, MenuAcivity.class);
		} else {
			resultIntent = new Intent(context, AlarmReapplyActivity.class);
		}
		resultIntent.putExtra(StringList.ELEMENT_APPLY_ID, id);
		TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
		if (!isTablet) {
			stackBuilder.addParentStack(AlarmReapplyActivity.class);
		}
		stackBuilder.addNextIntent(resultIntent);
		PendingIntent resultPendingIntent =
				stackBuilder.getPendingIntent(
						Integer.parseInt(id),
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

    private void cancelNotification(Context context) {
        NotificationManager notificationManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancelAll();
    }

    public void setupNotification(Context context) {
        cancelNotification(context);
        ElementApplyManager elementMgr = ElementApplyManager.getInstance(context);
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
		AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
		Intent intent = new Intent(context, AlarmReceiver.class);
		intent.removeExtra(StringList.ELEMENT_APPLY_ID);
		intent.putExtra(StringList.ELEMENT_APPLY_ID, value);
		PendingIntent pendingIntent = PendingIntent.getBroadcast(context, requestCode, intent, PendingIntent
				.FLAG_UPDATE_CURRENT);
		am.cancel(pendingIntent);
		am.set(AlarmManager.RTC_WAKEUP, time, pendingIntent);
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