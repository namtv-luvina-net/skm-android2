package jp.co.soliton.keymanager.common;

import android.content.Context;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by nguyenducdat on 4/11/2017.
 */

public class LogFileCtrl {

	public static final String PREFIX_LOG = "skm_";
	public static final String SUFFIX_LOG = ".log";
	public static final Integer MAXIMUM_LOG_FILES = 3;

	public static void deleteOldLogFile(Context context) {
		getListLogFile(context);
	}

	public static ArrayList<String> getListLogFile(Context context) {
		File files = context.getFilesDir();
		ArrayList<String> listTmp = new ArrayList<>();
		for (String fileName : files.list())
		{
			if (isFileNameLogValid(fileName)) {
				String dateCreatedFile = getDateCreatedFile(fileName);
				if (!DateUtils.isDateValid(dateCreatedFile)) {
					deleteFile(context, fileName);
				} else {
					listTmp.add(fileName);
				}
			}
		}
		if (listTmp.size() > MAXIMUM_LOG_FILES) {
			Collections.sort(listTmp);
			Collections.reverse(listTmp);
			while (listTmp.size() > MAXIMUM_LOG_FILES) {
				deleteFile(context, listTmp.get(MAXIMUM_LOG_FILES));
				listTmp.remove(MAXIMUM_LOG_FILES);
			}
		}
		ArrayList<String> listFileName = new ArrayList<>();
		for (int i = 0; i < listTmp.size() ; i++) {
			listFileName.add(context.getFilesDir().getPath() + File.separator + listTmp.get(i));
		}
		return listFileName;
	}

	private static void deleteFile(Context context, String child) {
		File dir = context.getFilesDir();
		File file = new File(dir, child);
		boolean deleted = file.delete();
	}

	private static boolean isFileNameLogValid(String fileName) {
		return fileName.startsWith(PREFIX_LOG) && fileName.endsWith(SUFFIX_LOG);
	}

	public static String getDateCreatedFile(String fileName) {
		return fileName.substring(PREFIX_LOG.length(), fileName.length() - SUFFIX_LOG.length());
	}

	public static String getLogName() {
		String logName = PREFIX_LOG + DateUtils.getCurrentDateLog() + SUFFIX_LOG;
		return logName;
	}
}
