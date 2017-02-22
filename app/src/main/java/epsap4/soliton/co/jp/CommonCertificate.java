package epsap4.soliton.co.jp;

import android.content.Context;
import android.security.KeyChain;

import java.security.cert.X509Certificate;

/**
 * Created by daoanhtung on 1/18/2017.
 */

public class CommonCertificate {

    /**
     * This method get part name of Certificate
     *
     * @param SubjectDN
     * @param path
     * @return
     */
    public static String getPartName(String SubjectDN, String path) {
        if (!SubjectDN.equals("")) {
            String[] parts = SubjectDN.split(",");
            for (int i = 0; i < parts.length; i++) {
                if (parts[i].contains(path)) {
                    return parts[i].replace(path, "");
                }
            }
        }
        return "";
    }

    /**
     * This method returns X509Certificate[]
     *
     * @param context
     * @param alias
     * @return X509Certificate[]
     */
    public static X509Certificate[] getCertificateChain(Context context, String alias) {
        try {
            return KeyChain.getCertificateChain(context, alias);
        } catch (Exception e) {
            LogCtrl.Logger(LogCtrl.m_strError, e.toString(), context);
        }
        return null;
    }

}
