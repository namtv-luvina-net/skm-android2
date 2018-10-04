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
import jp.co.soliton.keymanager.NotificationLogCtrl;
import jp.co.soliton.keymanager.R;
import jp.co.soliton.keymanager.StringList;
import jp.co.soliton.keymanager.activity.AlarmReapplyActivity;
import jp.co.soliton.keymanager.activity.MenuAcivity;
import jp.co.soliton.keymanager.common.DateUtils;
import jp.co.soliton.keymanager.dbalias.ElementApply;
import jp.co.soliton.keymanager.dbalias.ElementApplyManager;

import java.util.Calendar;
import java.util.Date;

import static jp.co.soliton.keymanager.common.DateUtils.STRING_DATE_FORMAT_SYSTEM_TIME;

public class AlarmReceiver extends BroadcastReceiver {

	public static final String REQUEST_CODE = "REQUEST_CODE";

	@Override
	public void onReceive(Context context, Intent intent) {
		boolean isTablet = context.getResources().getBoolean(R.bool.isTablet);
		PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
		PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "soliton:key_manager");
		//Acquire the lock
		wl.acquire();

		//You can do the processing here update the widget/remote views.
		Bundle extras = intent.getExtras();

		String id = extras.getString(StringList.ELEMENT_APPLY_ID, "");
		int requestCode = extras.getInt(REQUEST_CODE, 0);
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
						requestCode,
						PendingIntent.FLAG_UPDATE_CURRENT
				);
		mBuilder.setContentIntent(resultPendingIntent);
		mBuilder.setAutoCancel(true);
		NotificationManager mNotificationManager =
				(NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		mNotificationManager.notify(requestCode, mBuilder.build());
		//Release the lock
		wl.release();
	}

	private int getNotificationIcon() {
		if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
			return R.mipmap.ic_notification5;
		}
		return R.mipmap.ic_notification;
	}

	public void updateNotificationIfNeed(Context context, ElementApply newElementApply, ElementApply databaseElementApply) {
		if (!newElementApply.isNotiEnableFlag()) {
			if (databaseElementApply.isNotiEnableFlag() && databaseElementApply.getNotValidAfter() != null) {
				removeAlarmExpired(context, String.valueOf(databaseElementApply.getId()));
			}
		} else {
			if (databaseElementApply.isNotiEnableFlag() && databaseElementApply.getNotValidAfter() != null) {
				removeAlarmExpired(context, String.valueOf(databaseElementApply.getId()));
			}
			addAlarmExpiredIfNeed(context, newElementApply);
		}

		if (!newElementApply.isNotiEnableBeforeFlag()) {
			if (databaseElementApply.isNotiEnableBeforeFlag() && databaseElementApply.getNotValidAfter() != null) {
				removeAlarmBefore(context, String.valueOf(databaseElementApply.getId()));
			}
		} else {
			if (databaseElementApply.isNotiEnableBeforeFlag() && databaseElementApply.getNotValidAfter() != null) {
				removeAlarmBefore(context, String.valueOf(databaseElementApply.getId()));
			}
			addAlarmBeforeIfNeed(context, newElementApply);
		}
	}

	public void addAlarmExpiredIfNeed(Context context, ElementApply el) {
		Calendar cal = Calendar.getInstance();
		Date expirationDate = DateUtils.convertSringToDateSystemTime(el.getExpirationDate());
		cal.setTime(expirationDate);
		if (el.isNotiEnableFlag() && expirationDate.after(Calendar.getInstance().getTime())) {
			NotificationLogCtrl.getInstance().add(getInfoElementApplyToLogExpired(el));
			setAlarm(context, String.valueOf(el.getId()), getRequestCodeExpired(el.getId()), expirationDate.getTime());
		}
	}

	public void addAlarmBeforeIfNeed(Context context, ElementApply el) {
		Calendar cal = Calendar.getInstance();
		Date expirationDate = DateUtils.convertSringToDateSystemTime(el.getExpirationDate());
		cal.setTime(expirationDate);
		int before = el.getNotiEnableBefore();
		cal.add(Calendar.DAY_OF_MONTH, before * -1);
		if (el.isNotiEnableBeforeFlag() && cal.getTime().after(Calendar.getInstance().getTime())) {
			NotificationLogCtrl.getInstance().add(getInfoElementApplyToLogBefore(el));
			setAlarm(context, String.valueOf(el.getId()), getRequestCodeBeforeExpired(el.getId()), cal.getTime().getTime());
		}
	}

	public int getRequestCodeExpired(int idElement) {
		return idElement * 2;
	}

	public int getRequestCodeBeforeExpired(int idElement) {
		return idElement * 2 + 1;
	}

	private void setAlarm(Context context, String idElement, int requestCode, long time) {
		AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
		Intent intent = new Intent(context, AlarmReceiver.class);
		intent.putExtra(StringList.ELEMENT_APPLY_ID, idElement);
		intent.putExtra(REQUEST_CODE, requestCode);
		PendingIntent pendingIntent = PendingIntent.getBroadcast(context, requestCode, intent, PendingIntent
				.FLAG_UPDATE_CURRENT);
		if (pendingIntent != null) {
			am.cancel(pendingIntent);
			pendingIntent = PendingIntent.getBroadcast(context, requestCode, intent, PendingIntent
					.FLAG_UPDATE_CURRENT);
		}

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
			am.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, time, pendingIntent);
		} else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
			am.setExact(AlarmManager.RTC_WAKEUP, time, pendingIntent);
		} else {
			am.set(AlarmManager.RTC_WAKEUP, time, pendingIntent);
		}
	}

	public void removeAlarmExpired(Context context, String idElement) {
		NotificationLogCtrl.getInstance().remove(getInfoElementApplyToLogExpired(ElementApplyManager.getInstance(context)
				.getElementApply(idElement)));
		removeAlarm(context, idElement, getRequestCodeExpired(Integer.parseInt(idElement)));
	}

	public void removeAlarmBefore(Context context, String idElement) {
		NotificationLogCtrl.getInstance().remove(getInfoElementApplyToLogBefore(ElementApplyManager.getInstance(context)
				.getElementApply(idElement)));
		removeAlarm(context, idElement, getRequestCodeBeforeExpired(Integer.parseInt(idElement)));
	}

	private void removeAlarm(Context context, String idElement, int requestCode) {
		AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
		Intent intent = new Intent(context, AlarmReceiver.class);
		intent.putExtra(StringList.ELEMENT_APPLY_ID, idElement);
		PendingIntent pendingIntent = PendingIntent.getBroadcast(context, requestCode, intent, PendingIntent
				.FLAG_UPDATE_CURRENT);
		am.cancel(pendingIntent);
	}

	private String getInfoElementApplyToLogBefore(ElementApply el) {
		Calendar calendar = Calendar.getInstance();
		Date expirationDate = DateUtils.convertSringToDateSystemTime(el.getExpirationDate());
		calendar.setTime(expirationDate);
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append(DateUtils.convertDateToString(STRING_DATE_FORMAT_SYSTEM_TIME, calendar.getTime()) + " ");
		stringBuilder.append(el.getcNValue() + " ");
		stringBuilder.append(el.getSerialNumber() + " ");
		stringBuilder.append("[DaysBefore] ");
		int before = el.getNotiEnableBefore();
		calendar.add(Calendar.DAY_OF_MONTH, before * -1);
		stringBuilder.append(DateUtils.convertDateToString(STRING_DATE_FORMAT_SYSTEM_TIME, calendar.getTime()) + " ");
		stringBuilder.append("\n");
		return stringBuilder.toString();
	}

	private String getInfoElementApplyToLogExpired(ElementApply el) {
		Calendar calendar = Calendar.getInstance();
		Date expirationDate = DateUtils.convertSringToDateSystemTime(el.getExpirationDate());
		calendar.setTime(expirationDate);
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append(DateUtils.convertDateToString(STRING_DATE_FORMAT_SYSTEM_TIME, calendar.getTime()) + " ");
		stringBuilder.append(el.getcNValue() + " ");
		stringBuilder.append(el.getSerialNumber() + " ");
		stringBuilder.append("[Expired] ");
		stringBuilder.append(DateUtils.convertDateToString(STRING_DATE_FORMAT_SYSTEM_TIME, calendar.getTime()) + " ");
		stringBuilder.append("\n");
		return stringBuilder.toString();
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