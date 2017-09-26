package jp.co.soliton.keymanager.asynctask;

import android.app.Activity;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.security.KeyChainAliasCallback;
import jp.co.soliton.keymanager.*;
import jp.co.soliton.keymanager.activity.MenuAcivity;
import jp.co.soliton.keymanager.common.CommonUtils;
import jp.co.soliton.keymanager.customview.DialogApplyMessage;
import jp.co.soliton.keymanager.customview.DialogMessageTablet;
import jp.co.soliton.keymanager.dbalias.ElementApply;
import jp.co.soliton.keymanager.dbalias.ElementApplyManager;
import jp.co.soliton.keymanager.mdm.MDMControl;
import jp.co.soliton.keymanager.scep.Requester;
import jp.co.soliton.keymanager.scep.RequesterException;
import jp.co.soliton.keymanager.scep.cert.CertificateUtility;
import jp.co.soliton.keymanager.scep.cert.X509Utils;
import jp.co.soliton.keymanager.scep.pkimessage.CertRep;
import jp.co.soliton.keymanager.scep.pkimessage.PkiStatus;
import jp.co.soliton.keymanager.wifi.WifiControl;
import jp.co.soliton.keymanager.xmlparser.XmlDictionary;
import jp.co.soliton.keymanager.xmlparser.XmlPullParserAided;
import jp.co.soliton.keymanager.xmlparser.XmlStringData;
import org.bouncycastle.jce.PKCS10CertificationRequest;

import java.math.BigInteger;
import java.security.*;
import java.security.cert.CertStore;
import java.security.cert.X509Certificate;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static jp.co.soliton.keymanager.common.ErrorNetwork.*;
import static jp.co.soliton.keymanager.common.TypeScrollFragment.SCROLL_TO_RIGHT;
import static jp.co.soliton.keymanager.manager.APIDManager.PREFIX_APID_WIFI;

/**
 * Created by nguyenducdat on 4/28/2017.
 */

public class StartUsingProceduresControl implements KeyChainAliasCallback {

	private Activity activity;
	public static int m_nMDM_RequestCode = 70;
	public static int m_nEnrollRtnCode = 55;

	private String m_strKeyType = "";
	private String m_strSubject = "";
	private String m_strChallenge = "";
	private String m_strSubjectAltName = "";
	private String m_strServerURL = "";
	private CertStore cACertificateStore = null;
	private Requester scepRequester = null;
	private XmlPullParserAided m_p_aided = null;
	private XmlPullParserAided m_p_aided_profile = null;
	private static InformCtrl m_InformCtrl;
	private static InformCtrl m_InformCtrlCA;
	private int m_nErroType;
	private ElementApply element;
	private String m_strCertArias;
	private DevicePolicyManager m_DPM;
	private MDMControl mdmctrl;
	private ComponentName m_DeviceAdmin;
	private static StartUsingProceduresControl instance;
	private boolean isTablet;

	public static StartUsingProceduresControl newInstance(Activity activity, InformCtrl m_InformCtrl, ElementApply element){
		instance = new StartUsingProceduresControl(activity, m_InformCtrl, element);
		return instance;
	}

	public static StartUsingProceduresControl getInstance(Activity activity){
		if (instance != null) {
			return instance;
		} else {
			return new StartUsingProceduresControl(activity, null, null);
		}
	}

	private StartUsingProceduresControl(Activity activity, InformCtrl m_InformCtrl, ElementApply element) {
		this.activity = activity;
		isTablet = activity.getResources().getBoolean(R.bool.isTablet);
		this.m_InformCtrl = m_InformCtrl;
		this.element = element;
		scepRequester = getScepRequester();
	}
	public ElementApply getElement() {
		return element;
	}

	public void startDeviceCertTask(){
		new GetDeviceCertTask().execute();
	}

	public Requester getScepRequester() {
		if (scepRequester == null) {
			setScepRequester(new Requester());
		}
		return scepRequester;
	}

	public void setScepRequester(Requester scepRequester) {
		this.scepRequester = scepRequester;
	}

	public void resultWithRequestCodeMDM() {
		SetMDM();
		handler.sendEmptyMessage(0);
		DownloadCACertificate();
	}

	public void afterIntallCert() {
		SetScepWifi();
	}

	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			// プログレスダイアログ終了
			try{
				mdmctrl.startService();
				//progressDialog.dismiss();
			}catch(Exception e){
				e.printStackTrace();
			}
		}
	};

	/**
	 * Task processing GetDeviceCertTask
	 */
	private class GetDeviceCertTask extends AsyncTask<Void, Void, Boolean> {
		protected Boolean doInBackground(Void... params) {
			////////////////////////////////////////////////////////////////////////////
			// 大項目1. ログイン開始 <=========
			////////////////////////////////////////////////////////////////////////////

			LogCtrl.getInstance().info("Proc: Get Profile");

			HttpConnectionCtrl conn = new HttpConnectionCtrl(activity);
			boolean ret = conn.RunHttpDeviceCertUrlConnection(m_InformCtrl);
			if (ret == false) {
				LogCtrl.getInstance().error("Proc: Connection error");
				m_nErroType = ERR_NETWORK;
				return false;
			}

			String retStr = m_InformCtrl.GetRtn();

			// ログイン結果
			if (retStr.startsWith(activity.getText(R.string.Forbidden).toString())) {
				LogCtrl.getInstance().error("Proc: Receive " + retStr);
				m_nErroType = ERR_FORBIDDEN;
				return false;
			} else if (retStr.startsWith(activity.getText(R.string.Unauthorized).toString())) {
				LogCtrl.getInstance().error("Proc: Receive " + retStr);
				m_nErroType = ERR_UNAUTHORIZED;
				return false;
			} else if (retStr.startsWith(activity.getText(R.string.ERR).toString())) {
				LogCtrl.getInstance().error("Proc: Receive " + retStr);
				m_nErroType = ERR_COLON;
				return false;
			} else if (retStr.startsWith("NG")) {
				LogCtrl.getInstance().error("Proc: Receive " + retStr);
				m_nErroType = ERR_LOGIN_FAIL;
				return false;
			}
			// 取得したCookieをログイン時のCookieとして保持する.
			m_InformCtrl.SetLoginCookie(m_InformCtrl.GetCookie());
			///////////////////////////////////////////////////
			// 認証応答の解析(Enroll応答のときの対応を流用できるはず)
			///////////////////////////////////////////////////
			// 取得XMLのパーサー
			m_p_aided = new XmlPullParserAided(activity, m_InformCtrl.GetRtn(), 2);    // 最上位dictの階層は2になる

			ret = m_p_aided.TakeApartScepInfoResponse(m_InformCtrl);
			if (ret == false) {
				LogCtrl.getInstance().error("Proc: XML Parser Error");
				m_nErroType = ERR_NETWORK;
				return false;
			}

			LogCtrl.getInstance().info("Proc: Receive Profile");

			SetScepItem();

			m_p_aided_profile = m_p_aided;
			ret = m_p_aided_profile.TakeApartProfile();
			if (ret == false) {
				//	m_ErrorMessage.setText(R.string.EnrollErrorMessage);
				m_nErroType = ERR_NETWORK;
				return false;
			}
			////////////////////////////////////////////////////////////////////////////
			// 大項目1. ログイン終了 =========>
			////////////////////////////////////////////////////////////////////////////
			m_nErroType = SUCCESSFUL;
			return ret;
		}

		@Override
		protected void onPostExecute(Boolean result) {
			super.onPostExecute(result);
			endConnection(result);
		}
	}

	/**
	 * Processing result from server return back
	 *
	 * @param result
	 */
	private void endConnection(boolean result) {
		if (result) {
			// 2. GetMDMDictionary
			XmlDictionary mdm_dict = m_p_aided_profile.GetMdmDictionary();
			if (mdm_dict == null) {
				LogCtrl.getInstance().info("Proc: Profile doesn't contained MDM configuration");
				DownloadCACertificate();
			} else {
				LogCtrl.getInstance().info("Proc: Profile contained MDM configuration");
				CallMDMCheckIn();
			}

		} else {
			//show error message
			if (m_nErroType == ERR_FORBIDDEN) {
				showMessage(activity.getString(R.string.handler_failed));
			} else if (m_nErroType == ERR_UNAUTHORIZED) {
				String str_unauth = activity.getString(R.string.Unauthorized);
				showMessage(m_InformCtrl.GetRtn().substring(str_unauth.length()));
			} else if (m_nErroType == ERR_COLON) {
				String str_err = activity.getString(R.string.ERR);
				showMessage(m_InformCtrl.GetRtn().substring(str_err.length()));
			} else if (m_nErroType == ERR_LOGIN_FAIL) {
				showMessage(activity.getString(R.string.login_failed));
			} else {
				showMessage(activity.getString(R.string.connect_failed));
			}
		}
	}

	/**
	 * Show message
	 *
	 * @param message
	 */
	protected void showMessage(String message) {
		if (!isTablet) {
			DialogApplyMessage dlgMessage = new DialogApplyMessage(activity, message);
			dlgMessage.setOnOkDismissMessageListener(new DialogApplyMessage.OnOkDismissMessageListener() {
				@Override
				public void onOkDismissMessage() {
					StringList.GO_TO_LIST_APPLY = "1";
					Intent intent = new Intent(activity, MenuAcivity.class);
					intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					activity.startActivity(intent);
				}
			});
			dlgMessage.show();
		} else {
			DialogMessageTablet dlgMessage = new DialogMessageTablet(activity, message);
			dlgMessage.setOnOkDismissMessageListener(new DialogMessageTablet.OnOkDismissMessageListener() {
				@Override
				public void onOkDismissMessage() {
					if (activity instanceof MenuAcivity) {
						int sizeListElementApply = ((MenuAcivity)activity).getListElementApply().size();
						if (sizeListElementApply == 1) {
							((MenuAcivity) activity).startDetailConfirmApplyFragment(SCROLL_TO_RIGHT);
						} else {
							((MenuAcivity) activity).startListConfirmApplyFragment(SCROLL_TO_RIGHT);
						}
					} else {
						StringList.GO_TO_LIST_APPLY = "1";
						Intent intent = new Intent(activity, MenuAcivity.class);
						intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
						activity.startActivity(intent);
						activity.overridePendingTransition(0, 0);
					}
				}
			});
			dlgMessage.show();
		}
	}


	@Override
	public void alias(String alias) {
	}

	public void startCertificateEnrollTask(){
		new CertificateEnrollTask().execute(scepRequester);
	}

	private class CertificateEnrollTask extends AsyncTask<Requester, Integer, Boolean> {

		String strError = "";
		@Override
		protected Boolean doInBackground(Requester... params) {
			try {
				Requester requester = params[0];
				// Generate Key Pair
				KeyPairGenerator rSAKeyPairGenerator = KeyPairGenerator.getInstance(m_strKeyType/*"RSA"*/, "BC");
				rSAKeyPairGenerator.initialize(2048, new SecureRandom());
				KeyPair rSAKeyPair = rSAKeyPairGenerator.generateKeyPair();

				// Generate self-signed certificate
				X509Certificate selfSignedCertificate;
				final Date notBefore = new Date(System.currentTimeMillis() - (5 * 60000));
				final Date notAfter = new Date(System.currentTimeMillis() + (1 * 3600000));
				final BigInteger serial = BigInteger.valueOf(System.currentTimeMillis());

				selfSignedCertificate =
						CertificateUtility.generateSelfSignedCertificate(
								m_strSubject,//"CN=NetAttest EPS Root CA,OU=RDD,O=Soliton Systems K.K.,L=Shinjuku,ST=Tokyo,C=JP",
								serial,
								notBefore,
								notAfter,
								rSAKeyPair,
								"SHA1WithRSA");

				// Make PKCS #10
				PKCS10CertificationRequest certificateSigningRequest;
				certificateSigningRequest =
						CertificateUtility.generateCertificateSigningRequest(
								m_strSubject,//"CN=NetAttest EPS Root CA,OU=RDD,O=Soliton Systems K.K.,L=Shinjuku,ST=Tokyo,C=JP",
								m_strChallenge,//"Challenge",
								rSAKeyPair,
								"SHA1WithRSA",
								m_strSubjectAltName);
				cACertificateStore =
						requester.getCACertificate(m_strServerURL/*"http://10.30.127.44/ca/NaScepEPSap.cgi"*/);
				CertRep certRep = requester.certificateEnrollment(
						m_strServerURL/*"https://10.30.127.44/ca/NaScepEPSap.cgi"*/,
						certificateSigningRequest,
						selfSignedCertificate,
						rSAKeyPair.getPrivate(),
						cACertificateStore);
				if (certRep.getPkiStatus().getStatus() == PkiStatus.Status.SUCCESS) {
					CertificateUtility.keyPairToKeyChain(activity, rSAKeyPair);

					element.setsNValue(certRep.getCertificate().getSerialNumber().toString());
					String str = certRep.getCertificate().getSubjectDN().toString();
					String[] arr = str.split(",");
					for(int i = 0; i < arr.length; i++) {
						if(arr[i].toString().startsWith("CN=")) {
							element.setcNValue(arr[i].toString().replace("CN=","").trim());
						}
					}
					CertificateUtility.certificateToKeyChain(
							activity,
							certRep.getCertificate(),
							element.getcNValue(),
							m_nEnrollRtnCode/*0*/);
					SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.getDefault());
					element.setExpirationDate(dateFormat.format(certRep.getCertificate().getNotAfter()));
					//retrieve data certificate
					for (int i = 0; i < arr.length; i++) {
						if (arr[i] == null || arr[i].length() <= 0) {
							continue;
						}
						if(arr[i].toString().startsWith("C=")) {
							element.setSubjectCountryName(arr[i].toString().replace("C=","").trim());
						}
						if(arr[i].toString().startsWith("ST=")) {
							element.setSubjectStateOrProvinceName(arr[i].toString().replace("ST=","").trim());
						}
						if(arr[i].toString().startsWith("L=")) {
							element.setSubjectLocalityName(arr[i].toString().replace("L=","").trim());
						}
						if(arr[i].toString().startsWith("O=")) {
							element.setSubjectOrganizationName(arr[i].toString().replace("O=","").trim());
						}
						if(arr[i].toString().startsWith("CN=")) {
							element.setSubjectCommonName(arr[i].toString().replace("CN=","").trim());
						}
						if(arr[i].toString().startsWith("E=")) {
							element.setSubjectEmailAddress (arr[i].toString().replace("E=","").trim());
						}
					}
					str = certRep.getCertificate().getIssuerDN().toString();
					String [] arrIssuer = str.split(",");
					for (int i = 0; i < arrIssuer.length; i++) {
						if (arrIssuer[i] == null || arrIssuer[i].length() <= 0) {
							continue;
						}
						if(arrIssuer[i].toString().startsWith("C=")) {
							element.setIssuerCountryName(arrIssuer[i].toString().replace("C=","").trim());
						}
						if(arrIssuer[i].toString().startsWith("ST=")) {
							element.setIssuerStateOrProvinceName(arrIssuer[i].toString().replace("ST=","").trim());
						}
						if(arrIssuer[i].toString().startsWith("L=")) {
							element.setIssuerLocalityName(arrIssuer[i].toString().replace("L=","").trim());
						}
						if(arrIssuer[i].toString().startsWith("O=")) {
							element.setIssuerOrganizationName(arrIssuer[i].toString().replace("O=","").trim());
						}
						if(arrIssuer[i].toString().startsWith("CN=")) {
							element.setIssuerCommonName(arrIssuer[i].toString().replace("CN=","").trim());
						}
						if(arrIssuer[i].toString().startsWith("E=")) {
							element.setIssuerEmailAdress(arrIssuer[i].toString().replace("E=","").trim());
						}
						if(arrIssuer[i].toString().startsWith("OU=")) {
							element.setIssuerOrganizationUnitName(arrIssuer[i].toString().replace("OU=","").trim());
						}
					}
					element.setVersion(String.valueOf(certRep.getCertificate().getVersion()));
					element.setSerialNumber(certRep.getCertificate().getSerialNumber().toString());
					element.setSignatureAlogrithm(certRep.getCertificate().getSigAlgName());
					if (certRep.getCertificate().getNotBefore() != null) {
						element.setNotValidBefore(dateFormat.format(certRep.getCertificate().getNotBefore()));
					}
					if (certRep.getCertificate().getNotAfter() != null) {
						element.setNotValidAfter(dateFormat.format(certRep.getCertificate().getNotAfter()));
					}
					element.setPublicKeyAlogrithm(certRep.getCertificate().getPublicKey().getAlgorithm());
					element.setPublicKeyData(bytesToHex(certRep.getCertificate().getPublicKey().getEncoded()));
					element.setPublicSignature(bytesToHex(certRep.getCertificate().getSignature()));
					element.setCertificateAuthority(String.valueOf(certRep.getCertificate().getBasicConstraints()));
					StringBuilder builder = new StringBuilder();
					String [] keyUsage = {"digitalSignature", "nonRepudiation", "keyEncipherment", "dataEncipherment", "keyAgreement", "keyCertSign", "cRLSign", "encipherOnly", "decipherOnly"};
					int maxLength = Math.min(keyUsage.length, certRep.getCertificate().getKeyUsage().length);
					for (int i = 0; i < maxLength; i++) {
						if (!certRep.getCertificate().getKeyUsage()[i]) {
							continue;
						}
						if (builder.length() <= 0) {
							builder.append(keyUsage[i]);
						} else {
							builder.append("," + keyUsage[i]);
						}
					}
					element.setUsage(builder.toString());
					element.setSubjectKeyIdentifier(bytesToHex(X509Utils.getSubjectKeyIdentifier(certRep.getCertificate())));
					element.setAuthorityKeyIdentifier(bytesToHex(X509Utils.getAuthorityKeyIdentifier(certRep.getCertificate())));
					element.setClrDistributionPointUri(X509Utils.getCRLURL(certRep.getCertificate()));
					List<String> ocspUrlList = X509Utils.getAIALocations(certRep.getCertificate());
					if (ocspUrlList != null && !ocspUrlList.isEmpty()) {
						element.setCertificateAuthorityUri(ocspUrlList.get(0));
					}
					element.setPurpose(X509Utils.getPurpose(certRep.getCertificate()));
					element.setNotiEnableFlag(CommonUtils.getPrefBoolean(activity, StringList.KEY_NOTIF_ENABLE_FLAG));
					element.setNotiEnableBeforeFlag(CommonUtils.getPrefBoolean(activity, StringList.KEY_NOTIF_ENABLE_BEFORE_FLAG));
					element.setNotiEnableBefore(CommonUtils.getPrefInteger(activity, StringList.KEY_NOTIF_ENABLE_BEFORE));
				} else {
					return false;
				}
			} catch (RequesterException e) {
				if (e.toString().contains("No CA Certificcate")) {
					strError = activity.getString(R.string.failed_to_get_ca);
					element.setStatus(ElementApply.STATUS_APPLY_FAILURE);
					ElementApplyManager mgr = new ElementApplyManager(activity);
					mgr.updateStatus(ElementApply.STATUS_APPLY_FAILURE, String.valueOf(element.getId()));
				}
				LogCtrl.getInstance().error("CertificateEnrollTask RequesterException: " + e.toString());
				return false;
			} catch (NoSuchAlgorithmException e) {
				LogCtrl.getInstance().error("CertificateEnrollTask NoSuchAlgorithmException: " + e.toString());
				return false;
			} catch (NoSuchProviderException e) {
				LogCtrl.getInstance().error("CertificateEnrollTask NoSuchProviderException: " + e.toString());
				return false;
			} catch (Exception e) {
				LogCtrl.getInstance().error("CertificateEnrollTask Exception :" + e.toString());
				return false;
			}
			return true;
		}

		@Override
		// プログレス処理
		protected void onProgressUpdate(Integer... values) {
		}

		@Override
		// メインスレッドに反映させる処理
		protected void onPostExecute(Boolean result) {
			if (!result) {
				if (strError.length() == 0) {
					strError = activity.getString(R.string.EnrollErrorMessage);
				}
				showMessage(strError);
			}
		}

		public String bytesToHex(byte[] in) {
			final StringBuilder builder = new StringBuilder();
			for(byte b : in) {
				if (builder.length() > 0) {
					builder.append(String.format(":%02x", b));
				} else {
					builder.append(String.format("%02x", b));
				}
			}
			return builder.toString();
		}
	}


	private boolean SetScepItem() {
		// URL
		m_strServerURL = m_InformCtrl.GetSituationURL();
		// Subject
		List<String> list = m_p_aided.GetSubjectList();
		String subject_string = "";
		for(int n = 0; list.size() > n; n++) {
			if(subject_string.length() == 0) {
				subject_string = list.get(n);
				subject_string += "=";
			} else if ((n%2) == 0) {
				subject_string += ", ";
				subject_string += list.get(n);
				subject_string += "=";
			} else if ((n%2) == 1) {
				subject_string += "\"";
				subject_string += list.get(n);
				subject_string += "\"";
			}
		}
		m_strSubject = subject_string;
		// v1.2.1以降---SubjectにmailAddressを追加する場合があるので確認は最後に...

		// Challenge, Name, KeyType
		XmlDictionary dict = m_p_aided.GetDictionary();
		List<XmlStringData> str_list;
		if(dict != null) {
			str_list = dict.GetArrayString();
			for(int i = 0; str_list.size() > i; i++){
				// config情報に従って、処理を行う.
				XmlStringData p_data = str_list.get(i);
				SetEditMemberChild(p_data);
			}
		}
		LogCtrl.getInstance().info("Proc: SCEP Subject " + Integer.toString(m_strSubject.length()));
		LogCtrl.getInstance().debug(m_strSubject);
		return true;
	}

	private void SetEditMemberChild(XmlStringData data) {
		String strKeyName = data.GetKeyName();	// キー名
		int    i_type = data.GetType();		// 要素タイプ(string:1, data=2, date=3, real=4, integer=5, true=6, false=7)
		String strData = data.GetData();		// 要素

		// Chalenge
		if(strKeyName.equalsIgnoreCase(StringList.m_str_scep_challenge)) {	// Challenge
			m_strChallenge = strData;
		} else if(strKeyName.equalsIgnoreCase(StringList.m_str_CaIdent)) {	// Name(Arias)
			m_strCertArias = strData;
		} else if(strKeyName.equalsIgnoreCase(StringList.m_str_scep_keytype)) {	// Key Type
			m_strKeyType = strData;
		} else if(strKeyName.equalsIgnoreCase(StringList.m_str_scep_rfc822Name)) {	// rfc822Name
			// チケット #8907 メールアドレスをSubjectAltNameとして設定しておく
			m_strSubjectAltName = strData;
		}
	}

	private void SetScepWifi() {
		// 1. WifiControlインスタンス取得
		WifiControl wifi = new WifiControl(activity);

		// 2. GetWifiDictList取得
		List<XmlDictionary> wifi_list = m_p_aided_profile.GetWifiDictList();

		// 3. ループしてWifiControl::SetWifiListを実行
		if(!wifi_list.isEmpty()) {
			for(int i = 0; wifi_list.size() > i; i++){
				XmlDictionary one_piece = wifi_list.get(i);
				wifi.SetWifiList(one_piece);
			}
		}

		// 3.2 CA_CertとUser_Certを設定する.
		wifi.SetCaCert(m_strCertArias);
		wifi.SetUserCert(m_InformCtrl.GetUserID());

		// 4. WifiControl::PublicConnect(WifiControl.SCEP_WIFI)を実行
		if(wifi.PublicConnect(WifiControl.SCEP_WIFI) == false) {
			return;
		}
	}


	// MDMのチェックインの呼び出し
	private void CallMDMCheckIn() {

		m_DPM = (DevicePolicyManager) activity.getSystemService(Context.DEVICE_POLICY_SERVICE);
		m_DeviceAdmin = new ComponentName(activity, EpsapAdminReceiver.class);
		String apid;
		if (element.getTarget().startsWith(PREFIX_APID_WIFI)) {
			apid = XmlPullParserAided.GetUDID(activity);
		} else {
			apid = XmlPullParserAided.GetVpnApid(activity);
		}
		m_InformCtrl.SetAPID(apid);
		mdmctrl = new MDMControl(activity, m_InformCtrl.GetAPID());
		// 古い情報をチェックアウト (この段階では設定ファイルは古い情報のまま)
		MDMControl.CheckOutMdmTask checkOutMdmTask = new MDMControl.CheckOutMdmTask(activity, new MDMControl.CheckOutListener() {
			@Override
			public void checkOutComplete() {
				if (isDeviceAdmin() == false) {
					addDeviceAdmin();
				} else {
					resultWithRequestCodeMDM();
				}
			}
		});
		checkOutMdmTask.execute();
	}

	// MDMのチェックインおよび、定期通信サービススレッドの起動
	// HTTP通信を行うため、スレッドから呼び出されること
	public void SetMDM() {
		Thread t = new Thread(new Runnable() {
			@Override
			public void run() {
				if (element == null) {
					return;
				}

				LogCtrl.getInstance().info("Proc: Start MDM configuration");

				// 2. GetMDMDictionary
				XmlDictionary mdm_dict = m_p_aided_profile.GetMdmDictionary();
				if (mdm_dict == null) {
					LogCtrl.getInstance().error("Proc: No MDM profile");
					return;
				}

				// 3. MDMFlgsにセット(MDMControlにMDMFlgs変数を持たせてそちらにやってもらう
				mdmctrl.SetMDMmember(mdm_dict);

				// 4. チェックイン(HTTP(S)) (新しいMDM設定情報もここでファイル保存する)
				boolean bret = mdmctrl.CheckIn(true);

				// 5. OKならスレッド起動...定期通信
				if(bret == false) {
					//	mdmctrl.startService();
					LogCtrl.getInstance().error("Proc: Check-in failed");
					return;
				}

				bret = mdmctrl.TokenUpdate();
				if(bret == false) {
					//	mdmctrl.startService();
					LogCtrl.getInstance().error("Proc: Token udpate failed");
					return;
				}
			}
		});
		t.start();
	}

	private void addDeviceAdmin() {
		LogCtrl.getInstance().info("Proc: Show DeviceAdmin activity");
		// Launch the activity to have the user enable our admin.
		Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
		intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, m_DeviceAdmin);
		intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION,
				"Additional text explaining why this needs to be added.");
		activity.startActivityForResult(intent, m_nMDM_RequestCode);
	}

	private boolean isDeviceAdmin() {
		return m_DPM.isAdminActive(m_DeviceAdmin);
	}


	/**
	 * Task download certificate
	 */
	private class DownloadCACertificateTask extends AsyncTask<Requester, Void, Boolean> {
		@Override
		protected Boolean doInBackground(Requester... params) {
			boolean ret;
			try {
				Requester requester = params[0];
				m_InformCtrlCA.SetURL(m_strServerURL);
				// Get CA Certificate
				cACertificateStore = requester.getCACertificate(m_strServerURL/*"http://10.30.127.44/ca/NaScepEPSap.cgi"*/);
				CertificateUtility.certStoreToKeyChain(activity, cACertificateStore,/*"epspCA"*/m_strCertArias);
				m_nErroType = SUCCESSFUL;
				ret = true;
			} catch (RequesterException e) {
				e.printStackTrace();
				ret = false;
			}
			return ret;
		}

		@Override
		protected void onPostExecute(Boolean result) {
			super.onPostExecute(result);
			if (!result) {
				endConnection(result);
			}
		}
	}

	private void DownloadCACertificate(){
		if (element.getTarget().startsWith(PREFIX_APID_WIFI)) {
			m_InformCtrlCA = new InformCtrl();
			String url = String.format("%s:%s", element.getHost(), element.getPort());
			m_InformCtrlCA.SetURL(url);
			//Open thread download cert
			new DownloadCACertificateTask().execute(scepRequester);
		} else {
			new CertificateEnrollTask().execute(scepRequester);
		}
	}
}