package jp.co.soliton.keymanager.common;

import android.content.Context;
import android.util.Log;

import java.io.*;

/**
 * Created by nguyenducdat on 4/11/2017.
 */

public class LogFileCtrl {

	public static final String PREFIX_LOG = "skm_";
	public static final String SUFFIX_LOG = ".log";

	public static void deleteOldLogFile(Context context) {
		File files = context.getFilesDir();
		for (String fileName : files.list())
		{
			if (fileName.startsWith(PREFIX_LOG) && fileName.endsWith(SUFFIX_LOG)) {
				String dateCreatedFile = getDateCreatedFile(fileName);
				if (!DateUtils.isDateValid(dateCreatedFile)) {
					File dir = context.getFilesDir();
					File file = new File(dir, fileName);
					boolean deleted = file.delete();
					Log.i("LogFileCtrl", "deleteOldLogFile: " + deleted);
				}
			}
		}
	}

	public static String getDateCreatedFile(String fileName) {
		return fileName.substring(PREFIX_LOG.length(), fileName.length() - SUFFIX_LOG.length());
	}

	public static String getLogName() {
		String logName = PREFIX_LOG + DateUtils.getCurrentDate() + SUFFIX_LOG;
		return logName;
	}
}
