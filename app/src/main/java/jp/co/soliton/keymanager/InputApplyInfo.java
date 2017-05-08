package jp.co.soliton.keymanager;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

/**
 * Created by luongdolong on 2/7/2017.
 */

public class InputApplyInfo {
    public final static String INPUT_APPLY_HOST        = "input_apply_host";
    public final static String INPUT_APPLY_SECURE_PORT = "input_apply_secure_port";
    public final static String INPUT_APPLY_PORT        = "input_apply_port";
    public final static String INPUT_APPLY_PLACE       = "input_apply_place";
    public final static String INPUT_APPLY_USER_ID     = "input_apply_user_id";
    public final static String INPUT_APPLY_PASSWORD    = "input_apply_password";
    public final static String INPUT_APPLY_EMAIL       = "input_apply_email";
    public final static String INPUT_APPLY_REASON      = "input_apply_reason";

    private String host;
    private String securePort;
    private String port;
    private String place;
    private String userId;
    private String password;
    private String email;
    private String reason;

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getPort() {
	    return port;
    }

    public void setPort(String port) {
	    this.port = port;
    }

    public String getSecurePort() {
        return securePort;
    }

    public void setSecurePort(String securePort) {
        this.securePort = securePort;
    }

    public String getPlace() {
        return place;
    }

    public void setPlace(String place) {
        this.place = place;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public void savePref(Context context) {
        SharedPreferences pref = context.getSharedPreferences(StringList.m_str_store_preference,
                context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        if (host != null && !host.isEmpty()) {
            editor.putString(INPUT_APPLY_HOST, host);
        } else {
            editor.remove(INPUT_APPLY_HOST);
        }
        if (securePort != null && !securePort.isEmpty()) {
            editor.putString(INPUT_APPLY_SECURE_PORT, securePort);
        } else {
            editor.remove(INPUT_APPLY_SECURE_PORT);
        }
        if (port != null && !port.isEmpty()) {
            editor.putString(INPUT_APPLY_PORT, port);
        } else {
            editor.remove(INPUT_APPLY_PORT);
        }
        if (place != null && !place.isEmpty()) {
            editor.putString(INPUT_APPLY_PLACE, place);
        } else {
            editor.remove(INPUT_APPLY_PLACE);
        }
        if (userId != null && !userId.isEmpty()) {
            editor.putString(INPUT_APPLY_USER_ID, userId);
        } else {
            editor.remove(INPUT_APPLY_USER_ID);
        }
        if (password != null && !password.isEmpty()) {
            editor.putString(INPUT_APPLY_PASSWORD, password);
        } else {
            editor.remove(INPUT_APPLY_PASSWORD);
        }
        if (email != null && !email.isEmpty()) {
            editor.putString(INPUT_APPLY_EMAIL, email);
        } else {
            editor.remove(INPUT_APPLY_EMAIL);
        }
        if (reason != null && !reason.isEmpty()) {
            editor.putString(INPUT_APPLY_REASON, reason);
        } else {
            editor.remove(INPUT_APPLY_REASON);
        }
        editor.commit();
    }

    public static InputApplyInfo getPref(Context context) {
        SharedPreferences pref = context.getSharedPreferences(StringList.m_str_store_preference,
                context.MODE_PRIVATE);
        InputApplyInfo inputApplyInfo = new InputApplyInfo();
        inputApplyInfo.setHost(pref.getString(INPUT_APPLY_HOST, ""));
        inputApplyInfo.setSecurePort(pref.getString(INPUT_APPLY_SECURE_PORT, ""));
        inputApplyInfo.setPort(pref.getString(INPUT_APPLY_PORT, ""));
        inputApplyInfo.setPlace(pref.getString(INPUT_APPLY_PLACE, ""));
        inputApplyInfo.setUserId(pref.getString(INPUT_APPLY_USER_ID, ""));
        inputApplyInfo.setPassword(pref.getString(INPUT_APPLY_PASSWORD, ""));
        inputApplyInfo.setEmail(pref.getString(INPUT_APPLY_EMAIL, ""));
        inputApplyInfo.setReason(pref.getString(INPUT_APPLY_REASON, ""));
        return inputApplyInfo;
    }

    public static void deletePref(Context context) {
        SharedPreferences pref = context.getSharedPreferences(StringList.m_str_store_preference,
                context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        if (!pref.getString(INPUT_APPLY_HOST, "").isEmpty()) {
            editor.remove(INPUT_APPLY_HOST);
        }
        if (!pref.getString(INPUT_APPLY_SECURE_PORT, "").isEmpty()) {
            editor.remove(INPUT_APPLY_SECURE_PORT);
        }
        if (!pref.getString(INPUT_APPLY_PORT, "").isEmpty()) {
            editor.remove(INPUT_APPLY_PORT);
        }
        if (!pref.getString(INPUT_APPLY_PLACE, "").isEmpty()) {
            editor.remove(INPUT_APPLY_PLACE);
        }
        if (!pref.getString(INPUT_APPLY_USER_ID, "").isEmpty()) {
            editor.remove(INPUT_APPLY_USER_ID);
        }
        if (!pref.getString(INPUT_APPLY_PASSWORD, "").isEmpty()) {
            editor.remove(INPUT_APPLY_PASSWORD);
        }
        if (!pref.getString(INPUT_APPLY_EMAIL, "").isEmpty()) {
            editor.remove(INPUT_APPLY_EMAIL);
        }
        if (!pref.getString(INPUT_APPLY_REASON, "").isEmpty()) {
            editor.remove(INPUT_APPLY_REASON);
        }
        editor.commit();
    }
}
