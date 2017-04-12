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
	public static final String STRING_DATE_FORMAT = "yyyyMMdd";
	public static final int NUM_DATE_VALID = 3;

	public static String getCurrentDate() {
		Date currentDate = Calendar.getInstance().getTime();
		return convertDateToString(currentDate);
	}

	public static String convertDateToString(Date date) {
		String strDate = new SimpleDateFormat(STRING_DATE_FORMAT).format(date).toString();
		System.out.println(strDate);
		return strDate;
	}

	public static Date convertSringToDate(String strDate){
		DateFormat dateFormat = new SimpleDateFormat(STRING_DATE_FORMAT);
		Date date = new Date();
		try {
			date = dateFormat.parse(strDate);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return date;
	}

	public static boolean isDateValid(String strDateToCompare){
		try {
			Date currentDate = Calendar.getInstance().getTime();
			Date dateCompare = convertSringToDate(strDateToCompare);
			if (currentDate.before(dateCompare)) {
				return false;
			}
			long diff = Math.abs(currentDate.getTime() - dateCompare.getTime());
			long diffDays = diff / (24 * 60 * 60 * 1000);
			System.out.println("diffDate = " + diffDays);
			if (diffDays < NUM_DATE_VALID) {
				return true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
}
