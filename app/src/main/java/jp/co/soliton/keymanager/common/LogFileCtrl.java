package jp.co.soliton.keymanager.common;

import android.content.Context;

import java.io.File;
import java.util.ArrayList;

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
			if (isFileNameLogValid(fileName)) {
				String dateCreatedFile = getDateCreatedFile(fileName);
				if (!DateUtils.isDateValid(dateCreatedFile)) {
					File dir = context.getFilesDir();
					File file = new File(dir, fileName);
					boolean deleted = file.delete();
				}
			}
		}
	}

	public static ArrayList<String> getListLogFile(Context context) {
		File files = context.getFilesDir();
		ArrayList<String> listFileName = new ArrayList<>();
		for (String fileName : files.list()) {
			if (isFileNameLogValid(fileName)) {
				String dateCreatedFile = getDateCreatedFile(fileName);
				if (DateUtils.isDateValid(dateCreatedFile)) {
					listFileName.add(context.getFilesDir().getPath() + File.separator + fileName);
				} else {
					File f = new File(files, fileName);
					boolean deleted = f.delete();
				}
			}
		}
		return listFileName;
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
