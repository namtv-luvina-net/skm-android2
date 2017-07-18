package jp.co.soliton.keymanager;

import android.os.Environment;
import android.util.Log;

import jp.co.soliton.keymanager.common.DateUtils;
import jp.co.soliton.keymanager.common.LogFileCtrl;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

public class LogCtrl {

	private static final String Info = "INFO";
	private static final String Warn = "WARN";
	private static final String Error = "ERRR";
	private static final String Debug = "DEBG";

	private static LogCtrl instance;
	private static String nameLogFile;

	public static LogCtrl getInstance(){
		if (instance == null) {
			synchronized (LogCtrl.class) {
				if (instance == null) {
					instance = new LogCtrl();
				}
			}
		}
		return instance;
	}

	private LogCtrl() {
	}

	public void info(String msg){
		Logger(Info, msg);
	}

	public void warn(String msg){
		Logger(Warn, msg);
	}

	public void error(String msg){
		Logger(Error, msg);
	}

	public void debug(String msg){
		if (SKMApplication.SKM_DEBUG == true || SKMApplication.SKM_TRACE == true) {
			Logger(Debug, msg);
		}
	}

	private synchronized void Logger(String msgtype, String msg) {

		OutputLogCat(msgtype, msg);

		if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED) == false) {
			// SDカード領域が存在しないときは抜ける
			return;
		}

		String logName = LogFileCtrl.getLogName();
		if (nameLogFile == null || nameLogFile.equalsIgnoreCase(logName) == false) {
			LogFileCtrl.deleteOldLogFile(SKMApplication.getAppContext());
		}
		nameLogFile = logName;

		String dateString = DateUtils.getCurrentDateSystem2();
		String log_path = SKMApplication.getAppContext().getFilesDir().getPath() + File.separator + nameLogFile;
		String log_str = dateString + " [" + msgtype + "] " + msg + "\n";

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

	private static void OutputLogCat(String msgtype, String msg) {
		if (SKMApplication.SKM_DEBUG == true) {
			if(msgtype.equalsIgnoreCase(Info) == true) {
				Log.i(StringList.m_str_SKMTag, msg);
			} else if(msgtype.equalsIgnoreCase(Warn) == true) {
				Log.w(StringList.m_str_SKMTag, msg);
			} else if(msgtype.equalsIgnoreCase(Error) == true) {
				Log.e(StringList.m_str_SKMTag, msg);
			} else if(msgtype.equalsIgnoreCase(Debug) == true) {
				Log.d(StringList.m_str_SKMTag, msg);
			}
		}
	}
}
