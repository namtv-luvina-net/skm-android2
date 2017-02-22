package epsap4.soliton.co.jp;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by daoanhtung on 1/13/2017.
 */

public class SKMPreferences {

    /**
     * This method sets the status of the all notification final date to the application preference
     *
     * @param context
     * @param status
     */
    public static void setStatusAllNotificationFinalDate(Context context, boolean status) {
        SharedPreferences pref = context.getSharedPreferences(StringList.m_str_store_preference,
                context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putBoolean(StringList.m_str_notification_final_date_pref, status);
        editor.commit();
    }

    /**
     * This method returns the status of the all notification final date from the application
     *
     * @param context
     * @return
     */
    public static boolean getStatusAllNotificationFinalDate(Context context) {
        SharedPreferences pref = context.getSharedPreferences(StringList.m_str_store_preference,
                context.MODE_PRIVATE);
        return pref.getBoolean(StringList.m_str_notification_final_date_pref, true);
    }

    /**
     * This method sets the status of the all notification before final date to the application preference
     *
     * @param context
     * @param status
     */
    public static void setStatusAllNotificationBeforeFinalDate(Context context, boolean status) {
        SharedPreferences pref = context.getSharedPreferences(StringList.m_str_store_preference,
                context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putBoolean(StringList.m_str_notification_before_final_date_pref, status);
        editor.commit();
    }

    /**
     * This method returns the status of the all notification before final date from the application
     *
     * @param context
     * @return
     */
    public static boolean getStatusAllNotificationBeforeFinalDate(Context context) {
        SharedPreferences pref = context.getSharedPreferences(StringList.m_str_store_preference,
                context.MODE_PRIVATE);
        return pref.getBoolean(StringList.m_str_notification_before_final_date_pref, true);
    }

    /**
     * This method sets the number day of the all before final date to the application preference
     *
     * @param context
     * @param days
     */
    public static void setNumberBeforeFinalDate(Context context, int days) {
        SharedPreferences pref = context.getSharedPreferences(StringList.m_str_store_preference,
                context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putInt(StringList.m_str_number_before_final_date_pref, days);
        editor.commit();
    }

    /**
     * This method returns the number day of the all before final date from the application
     *
     * @param context
     * @return
     */
    public static int getNumberBeforeFinalDate(Context context) {
        SharedPreferences pref = context.getSharedPreferences(StringList.m_str_store_preference,
                context.MODE_PRIVATE);
        return pref.getInt(StringList.m_str_number_before_final_date_pref, 7);
    }
}
