package jp.co.soliton.keymanager.common;

import android.util.Log;
import jp.co.soliton.keymanager.ValidateParams;

/**
 * Created by nguyenducdat on 9/22/2017.
 */

public class EpsapVersion {

	public static final String VERSION_VALID_USE_APID = "2.2.0";

	public static boolean checkVersionValidUseApid(String versionCompare) {
		if (ValidateParams.nullOrEmpty(versionCompare)) {
			return false;
		}
		String[] vals1 = versionCompare.split("\\.");
		String[] vals2 = VERSION_VALID_USE_APID.split("\\.");
		int i = 0;
		// set index to first non-equal ordinal or length of shortest version string
		while (i < vals1.length && i < vals2.length && vals1[i].equals(vals2[i])) {
			i++;
		}
		// compare first non-equal ordinal number
		if (i < vals1.length && i < vals2.length) {
			int diff = Integer.valueOf(vals1[i]).compareTo(Integer.valueOf(vals2[i]));
			return (Integer.signum(diff) >= 0);
		}
		// the strings are equal or one string is a substring of the other
		// e.g. "1.2.3" = "1.2.3" or "1.2.3" < "1.2.3.4"
		return (Integer.signum(vals1.length - vals2.length) >= 0);
	}
}
