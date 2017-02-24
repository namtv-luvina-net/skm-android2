package epsap4.soliton.co.jp;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ValidateParams {
    public static Boolean isValidEmail(String email) {
        Pattern ps = Pattern.compile("^[-0-9a-zA-Z'!#$%&*+/=?^_`{|}~.]*[-0-9a-zA-Z'!#$%&*+/=?^_`{|}~]@[-0-9a-zA-Z'!#$%&*+/=?^_`{|}~.]+$");
        Matcher ms = ps.matcher(email.trim());
        boolean matched = ms.matches();
        if(matched) {
            String[] domain = email.split("@");
            String[] domainArray = domain[1].split("\\.", -1);
            for (int i = 0; i < domainArray.length; i++) {
                if (domainArray[i].length() == 0) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    public static Boolean isValidUserID(String userId) {
        String validateChar = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ -_@!$%&^~.";
        String validationStartChar = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
        for (int i = 0; i < userId.length(); i++) {
            char c = userId.charAt(i);

            if (i == 0 && !validationStartChar.contains(String.valueOf(c))) {
                return false;
            } else if (!validateChar.contains(String.valueOf(c))) {
                return false;
            }
        }
        if (userId.contains("  ")) {
            return false;
        }
        return true;
    }
}