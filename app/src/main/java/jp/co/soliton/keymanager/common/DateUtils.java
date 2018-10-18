package jp.co.soliton.keymanager.common;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by nguyenducdat on 4/11/2017.
 */

public class DateUtils {
	public static final String STRING_DATE_FORMAT_FOR_LOG = "yyyyMMdd";
	public static final String STRING_DATE_FORMAT_FOR_ZIP = "yyyyMMddHHmmss";
	public static final String STRING_DATE_FORMAT_SYSTEM_TIME = "yyyy/MM/dd HH:mm:ss";
	public static final String STRING_DATE_FORMAT_SYSTEM_TIME1 = "yyyy-MM-dd HH:mm:ss";
	public static final String STRING_DATE_FORMAT_SYSTEM_TIME2 = "yyyy/MM/dd HH:mm:ss.SSS";

	public static String getCurrentDateLog() {
		Date currentDate = Calendar.getInstance().getTime();
		return convertDateToString(STRING_DATE_FORMAT_FOR_LOG, currentDate);
	}

	public static String getCurrentDateZip() {
		Date currentDate = Calendar.getInstance().getTime();
		return convertDateToString(STRING_DATE_FORMAT_FOR_ZIP, currentDate);
	}

	public static String getCurrentDateSystem() {
		Date currentDate = Calendar.getInstance().getTime();
		return convertDateToString(STRING_DATE_FORMAT_SYSTEM_TIME, currentDate);
	}

	public static String getCurrentDateSystem2() {
		Date currentDate = Calendar.getInstance().getTime();
		return convertDateToString(STRING_DATE_FORMAT_SYSTEM_TIME2, currentDate);
	}

	public static String convertDateToString(String type, Date date) {
		String strDate = new SimpleDateFormat(type).format(date);
		return strDate;
	}

	public static boolean isDateValid(String strDateToCompare){
		try {
			Date currentDate = Calendar.getInstance().getTime();
			Date dateCompare = convertSringToDate(STRING_DATE_FORMAT_FOR_LOG, strDateToCompare);
			return !currentDate.before(dateCompare);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	public static Date convertSringToDateSystemTime(String strDate){
		return convertSringToDate(STRING_DATE_FORMAT_SYSTEM_TIME, strDate);
	}

	public static Date convertSringToDateLog(String strDate){
		return convertSringToDate(STRING_DATE_FORMAT_FOR_LOG, strDate);
	}

	public static Date convertSringToDateZip(String strDate){
		return convertSringToDate(STRING_DATE_FORMAT_FOR_ZIP, strDate);
	}

	public static Date convertSringToDate(String type, String strDate){
		DateFormat dateFormat = new SimpleDateFormat(type);
		Date date = new Date();
		try {
			date = dateFormat.parse(strDate);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return date;
	}
}
