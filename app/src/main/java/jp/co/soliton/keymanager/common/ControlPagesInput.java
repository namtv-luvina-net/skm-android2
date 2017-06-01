package jp.co.soliton.keymanager.common;

import android.app.Activity;
import android.content.Intent;
import android.security.KeyChain;
import android.util.Log;
import jp.co.soliton.keymanager.InformCtrl;
import jp.co.soliton.keymanager.LogCtrl;
import jp.co.soliton.keymanager.R;
import jp.co.soliton.keymanager.StringList;
import jp.co.soliton.keymanager.fragment.InputBasePageFragment;
import jp.co.soliton.keymanager.fragment.InputPortPageFragment;
import jp.co.soliton.keymanager.xmlparser.XmlPullParserAided;
import jp.co.soliton.keymanager.xmlparser.XmlStringData;

import javax.security.cert.X509Certificate;
import java.net.URLEncoder;
import java.util.List;

/**
 * Created by nguyenducdat on 5/5/2017.
 */

public class ControlPagesInput {
	public static int REQUEST_CODE_INSTALL_CERTIFICATION_CONTROL_PAGES_INPUT = 4955;

	private Activity activity;
	private LogCtrl logCtrl;

	public ControlPagesInput(Activity activity) {
		this.activity = activity;
		logCtrl = LogCtrl.getInstance(activity);
	}

	/**
	 * Download and install certificate
	 */
	public String downloadCert(String cacert) {
		//Extract certificate from .mobileconfig file
		cacert = cacert.substring(cacert.indexOf("<?xml"));
		cacert = cacert.substring(0, cacert.indexOf("</plist>") + 8);
		XmlPullParserAided m_p_aided = new XmlPullParserAided(activity, cacert, 2);	// 最上位dictの階層は2になる
		boolean ret = m_p_aided.TakeApartProfileList();
		if (!ret) {
			logCtrl.loggerError("InputPortPageFragment:downloadCert1: " + activity.getString(R.string
					.error_install_certificate));
			return activity.getString(R.string.error_install_certificate);
		}
		List<XmlStringData> listPayloadContent = m_p_aided.GetDictionary().GetArrayString();
		cacert = listPayloadContent.get(listPayloadContent.size() - 1).GetData();
		cacert = String.format("%s\n%s\n%s", "-----BEGIN CERTIFICATE-----", cacert, "-----END CERTIFICATE-----");
		//Install certificate
		Intent intent = KeyChain.createInstallIntent();
		try {
			X509Certificate x509 = X509Certificate.getInstance(cacert.getBytes());
			intent.putExtra(KeyChain.EXTRA_CERTIFICATE, x509.getEncoded());
			intent.putExtra(KeyChain.EXTRA_NAME, InputPortPageFragment.payloadDisplayName);
			activity.startActivityForResult(intent, REQUEST_CODE_INSTALL_CERTIFICATION_CONTROL_PAGES_INPUT);
		} catch (Exception e) {
			logCtrl.loggerError("InputPortPageFragment:downloadCert2: " + activity.getString(R.string
					.error_install_certificate));
			return activity.getString(R.string.error_install_certificate);
		}
		return "";
	}

	/**
	 * Make parameter for logon to server
	 * @return
	 */
	public boolean makeParameterLogon(String strUserid, String strPasswd, String place, InformCtrl informCtrl) {
		String rtnserial = "";
		if (InputBasePageFragment.TARGET_WiFi.equals(place)) {
			rtnserial = XmlPullParserAided.GetUDID(activity);
		} else {
			rtnserial = XmlPullParserAided.GetVpnApid(activity);
		}
		// ログインメッセージ
		// URLEncodeが必須 <http://wada811.blog.fc2.com/?tag=URL%E3%82%A8%E3%83%B3%E3%82%B3%E3%83%BC%E3%83%89>参照
		String message;
		try {
			message = "Action=logon" + "&" + StringList.m_strUserID + URLEncoder.encode(strUserid, "UTF-8") +
					"&" + StringList.m_strPassword + URLEncoder.encode(strPasswd, "UTF-8") +
					"&" + StringList.m_strSerial + rtnserial;
		} catch (Exception ex) {
			LogCtrl.getInstance(activity).loggerInfo("InputUserPageFragment:makeParameterLogon: " + ex.toString());
			Log.i(StringList.m_str_SKMTag, "logon:: " + "Message=" + ex.getMessage());
			return false;
		}
		// 入力データを情報管理クラスへセットする
		informCtrl.SetUserID(strUserid);
		informCtrl.SetPassword(strPasswd);
		informCtrl.SetMessage(message);
		return true;
	}
}
