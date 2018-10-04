package jp.co.soliton.keymanager;

import android.util.Log;
import jp.co.soliton.keymanager.common.DateUtils;

import java.io.*;

public class NotificationLogCtrl {

	private static final String Add = "ADD";
	private static final String Remove = "REMOVE";
	private static final String Reboot = "REBOOT";

	private static NotificationLogCtrl instance;
	private static final String nameNotificationLogFile = "notification.log";

	public static NotificationLogCtrl getInstance(){
		if (instance == null) {
			synchronized (NotificationLogCtrl.class) {
				if (instance == null) {
					instance = new NotificationLogCtrl();
				}
			}
		}
		return instance;
	}

	private NotificationLogCtrl() {
	}


	public String getNotificationLogFile() {
		String log_path = SKMApplication.getAppContext().getFilesDir().getPath() + File.separator + nameNotificationLogFile;
		File file = new File(log_path);
		if (file.exists()) {
			return file.getAbsolutePath();
		}
		return null;
	}

	public void reboot(){
		Logger(Reboot, "Recreate Alarm");
	}

	public void add(String msg){
		Logger(Add, msg);
	}

	public void remove(String msg){
		Logger(Remove, msg);
	}

	private synchronized void Logger(String msgtype, String msg) {
		String dateString = DateUtils.getCurrentDateSystem2();
		String log_path = SKMApplication.getAppContext().getFilesDir().getPath() + File.separator + nameNotificationLogFile;
		String log_str = dateString + " [" + msgtype + "] " + msg + "\n";
		OutputLogCat(log_str);

		OutputStream out;
	    try {
	        out = new BufferedOutputStream(new FileOutputStream(log_path, true));
	        PrintWriter writer = new PrintWriter(new OutputStreamWriter(out, "UTF-8"));
	        writer.append(log_str);
	        writer.close();
	    } catch (IOException e) {
	        e.printStackTrace();
	    }
	}

	private static void OutputLogCat(String msg) {
		if (SKMApplication.SKM_DEBUG) {
			Log.i(StringList.m_str_SKMTag, msg);
		}
	}
}
