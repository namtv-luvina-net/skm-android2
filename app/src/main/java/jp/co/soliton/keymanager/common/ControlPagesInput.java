package jp.co.soliton.keymanager.common;

import android.app.Activity;
import android.content.Intent;
import android.security.KeyChain;

import org.bouncycastle.asn1.x500.RDN;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x500.style.BCStyle;
import org.bouncycastle.cert.jcajce.JcaX509CertificateHolder;

import jp.co.soliton.keymanager.InformCtrl;
import jp.co.soliton.keymanager.LogCtrl;
import jp.co.soliton.keymanager.R;
import jp.co.soliton.keymanager.StringList;
import jp.co.soliton.keymanager.fragment.InputPortPageFragment;
import jp.co.soliton.keymanager.xmlparser.XmlPullParserAided;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.net.URLEncoder;
import java.util.List;

import static jp.co.soliton.keymanager.manager.APIDManager.TARGET_WiFi;

/**
 * Created by nguyenducdat on 5/5/2017.
 */

public class ControlPagesInput {
	public static int REQUEST_CODE_INSTALL_CERTIFICATION_CONTROL_PAGES_INPUT = 4955;

	private Activity activity;

	public List<String> getCertArray() {
		return certArray;
	}

	private List<String> certArray;

	public ControlPagesInput(Activity activity) {
		this.activity = activity;
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
			LogCtrl.getInstance().error("Apply: Download CA Certificate Error: " + activity.getString(R.string
					.error_install_certificate));
			return activity.getString(R.string.error_install_certificate);
		}
		certArray = m_p_aided.GetCacertArray();
		return installCACert();
	}

	public String installCACert()
	{
		if (certArray.size() > 0)
		{
			String cacert = certArray.get(0);
			certArray.remove(0);
			Intent intent = KeyChain.createInstallIntent();
			try {
				LogCtrl.getInstance().info("Apply: Install CA Certificate " + Integer.toString(cacert.length()));
				LogCtrl.getInstance().debug(cacert);

				CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509", "BC");
				InputStream inputStrem = new ByteArrayInputStream(cacert.getBytes());
				X509Certificate x509 = (X509Certificate) certificateFactory.generateCertificate(inputStrem);
				X500Name x500name = new JcaX509CertificateHolder(x509).getSubject();
				RDN cn = x500name.getRDNs(BCStyle.CN)[0];
				String cnCertificate = cn.getFirst().getValue().toString();
				intent.putExtra(KeyChain.EXTRA_CERTIFICATE, x509.getEncoded());
				intent.putExtra(KeyChain.EXTRA_NAME, cnCertificate);
				activity.startActivityForResult(intent, REQUEST_CODE_INSTALL_CERTIFICATION_CONTROL_PAGES_INPUT);
			} catch (Exception e) {
				LogCtrl.getInstance().error("Apply: Install CA Certificate Error: " +  activity.getString(R.string.error_install_certificate));
				return activity.getString(R.string.error_install_certificate);
			}
		}
		return "";
	}

	/**
	 * Make parameter for logon to server
	 * @return
	 */
	public boolean makeParameterLogon(String strUserid, String strPasswd, String place, InformCtrl informCtrl) {
		String rtnserial = "";
		if (TARGET_WiFi.equals(place)) {
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
			LogCtrl.getInstance().error("Apply: Make parameter error: " + ex.getMessage());
			return false;
		}
		// 入力データを情報管理クラスへセットする
		informCtrl.SetUserID(strUserid);
		informCtrl.SetPassword(strPasswd);
		informCtrl.SetMessage(message);
		return true;
	}
}
