package jp.co.soliton.keymanager.asynctask;

import android.app.Activity;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.security.KeyChain;
import android.security.KeyChainAliasCallback;
import android.util.Log;
import jp.co.soliton.keymanager.*;
import jp.co.soliton.keymanager.activity.MenuAcivity;
import jp.co.soliton.keymanager.activity.ViewPagerInputActivity;
import jp.co.soliton.keymanager.common.CommonUtils;
import jp.co.soliton.keymanager.customview.DialogApplyMessage;
import jp.co.soliton.keymanager.customview.DialogMessageTablet;
import jp.co.soliton.keymanager.dbalias.ElementApply;
import jp.co.soliton.keymanager.fragment.InputPortPageFragment;
import jp.co.soliton.keymanager.mdm.MDMControl;
import jp.co.soliton.keymanager.mdm.MDMFlgs;
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
	private LogCtrl logCtrl;
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
		logCtrl = LogCtrl.getInstance(activity);
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
//		SetMDM();
//		SetScepItem();
//		SetScepWifi();
		Log.d("StartUsingProceduresControl:datnd", "resultWithRequestCodeMDM: ");
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
			LogCtrl logCtrlAsyncTask = LogCtrl.getInstance(activity);
			HttpConnectionCtrl conn = new HttpConnectionCtrl(activity);
			boolean ret = conn.RunHttpDeviceCertUrlConnection(m_InformCtrl);
			if (ret == false) {
				logCtrlAsyncTask.loggerError("GetDeviceCertTask Network error");
				m_nErroType = ERR_NETWORK;
				return false;
			}
			// ログイン結果
			if (m_InformCtrl.GetRtn().startsWith(activity.getText(R.string.Forbidden).toString())) {
				logCtrlAsyncTask.loggerError("GetDeviceCertTask Forbidden.");
				m_nErroType = ERR_FORBIDDEN;
				return false;
			} else if (m_InformCtrl.GetRtn().startsWith(activity.getText(R.string.Unauthorized).toString())) {
				logCtrlAsyncTask.loggerError("GetDeviceCertTask Unauthorized.");
				m_nErroType = ERR_UNAUTHORIZED;
				return false;
			} else if (m_InformCtrl.GetRtn().startsWith(activity.getText(R.string.ERR).toString())) {
				logCtrlAsyncTask.loggerError("GetDeviceCertTask ERR:");
				m_nErroType = ERR_COLON;
				return false;
			} else if (m_InformCtrl.GetRtn().startsWith("NG")) {
				logCtrlAsyncTask.loggerError("GetDeviceCertTask NG:");
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
				logCtrlAsyncTask.loggerError("LogonApplyTask-- TakeApartDevice false");
				m_nErroType = ERR_NETWORK;
				return false;
			}

			SetScepItem();

			m_p_aided_profile = m_p_aided;
			ret = m_p_aided_profile.TakeApartProfile();
			if (ret == false) {
				logCtrlAsyncTask.loggerError("CertLoginAcrivity::onClick TakeApartProfile false");
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
				Log.d("CertLoginActivity", "SetMDM() No profile");
//				SetScepWifi();
				DownloadCACertificate();
			} else {
				Log.d("CertLoginActivity", "SetMDM() Has profile");
				CallMDMCheckIn();
			}

		} else {
			//show error message
			if (m_nErroType == ERR_FORBIDDEN) {
				String str_forbidden = activity.getString(R.string.Forbidden);
				showMessage(m_InformCtrl.GetRtn().substring(str_forbidden.length()));
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
		Log.d("CertLoginActivity", "printAlias():: " + alias);
	}

	public void startCertificateEnrollTask(){
		new CertificateEnrollTask().execute(scepRequester);
	}

	private class CertificateEnrollTask extends AsyncTask<Requester, Integer, Boolean> {

		@Override
		protected Boolean doInBackground(Requester... params) {
			LogCtrl logCtrlAsyncTask = LogCtrl.getInstance(activity);
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

				Log.d("CertificateEnrollTask", "privatekey - " + rSAKeyPair.getPrivate());

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
					CertificateUtility.keyPairToKeyChain(
							activity,
							rSAKeyPair);
					CertificateUtility.certificateToKeyChain(
							activity,
							certRep.getCertificate(),
							m_InformCtrl.GetUserID()/*"epsap"m_strCertArias*/, m_nEnrollRtnCode/*0*/);

					element.setsNValue(certRep.getCertificate().getSerialNumber().toString());
					String str = certRep.getCertificate().getSubjectDN().toString();
					String[] arr = str.split(",");
					for(int i = 0; i < arr.length; i++) {
						if(arr[i].toString().startsWith("CN=")) {
							element.setcNValue(arr[i].toString().replace("CN=","").trim());
						}
					}
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
					element.setNotiEnableBeforeFlag(CommonUtils.getPrefBoolean(activity, StringList.KEY_NOTIF_ENABLE_FLAG));
					element.setNotiEnableBefore(CommonUtils.getPrefInteger(activity, StringList.KEY_NOTIF_ENABLE_BEFORE));
				} else {
					return false;
				}
			} catch (RequesterException e) {
				logCtrlAsyncTask.loggerError("CertificateEnrollTask RequesterException::" + e
						.toString());
				//	e.printStackTrace();
				return false;
			} catch (NoSuchAlgorithmException e) {
				logCtrlAsyncTask.loggerError("CertificateEnrollTask NoSuchAlgorithmException::" + e.toString());
				//	e.printStackTrace();
				return false;
			} catch (NoSuchProviderException e) {
				logCtrlAsyncTask.loggerError("CertificateEnrollTask NoSuchProviderException::" + e.toString());
				//	e.printStackTrace();
				return false;
			} catch (Exception e) {
				logCtrlAsyncTask.loggerError("CertificateEnrollTask Exception::" + e.toString());
				//	e.printStackTrace();
				return false;
			}
			return true;
		}

		@Override
		// プログレス処理
		protected void onProgressUpdate(Integer... values) {
			Log.d("CertificateEnrollTask", "onProgressUpdate - " + "values");
		}

		@Override
		// メインスレッドに反映させる処理
		protected void onPostExecute(Boolean result) {
			Log.d("CertificateEnrollTask", "onPostExecute - " + "result");
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
		System.out.println("_________________---------____________________");
		for(int n = 0; list.size() > n; n++) {
			System.out.println(list.get(n));
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
		logCtrl.loggerDebug("CertLoginAcrivity::SetScepItem Subject: " + m_strSubject);
		return true;
	}

	private void SetEditMemberChild(XmlStringData data) {
		String strKeyName = data.GetKeyName();	// キー名
		int    i_type = data.GetType();		// 要素タイプ(string:1, data=2, date=3, real=4, integer=5, true=6, false=7)
		String strData = data.GetData();		// 要素
		logCtrl.loggerInfo("CertLoginAcrivity::SetEditMemberChild Key= " +  strKeyName + " , Data= " + strData);

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
		logCtrl.loggerDebug("CertLoginActivity CallMDMActivity()");

		// 古い情報をチェックアウト (この段階では設定ファイルは古い情報のまま)
		OldMdmCheckOut();

		m_DPM = (DevicePolicyManager) activity.getSystemService(Context.DEVICE_POLICY_SERVICE);
		m_DeviceAdmin = new ComponentName(activity, EpsapAdminReceiver.class);
		String apid;
		if (element.getTarger().startsWith("WIFI")) {
			apid = XmlPullParserAided.GetUDID(activity);
		} else {
			apid = XmlPullParserAided.GetVpnApid(activity);
		}
		m_InformCtrl.SetAPID(apid);
		Log.d("StartUsingProceduresControl:datnd", "CallMDMCheckIn: ========================================= ");
		mdmctrl = new MDMControl(activity, m_InformCtrl.GetAPID());

		if (isDeviceAdmin() == false) {
			addDeviceAdmin();
		} else {
//			SetMDM();
//			SetScepItem();
//			SetScepWifi();
//			DownloadCACertificate();
			resultWithRequestCodeMDM();
		}
	}

	private void OldMdmCheckOut() {
		Thread t = new Thread(new Runnable() {
			@Override
			public void run() {
				String filedir = "/data/data/" + activity.getPackageName() + "/files/";

				java.io.File filename_mdm = new java.io.File(filedir + StringList.m_strMdmOutputFile);
				if(filename_mdm.exists()) {
					logCtrl.loggerInfo("MDMCheckinActivity OldMdmCheckOut()");
					MDMFlgs mdm = new MDMFlgs();
					boolean bRet = mdm.ReadAndSetScepMdmInfo(activity);
					if(mdm.GetCheckOut() == true) {
						MDMControl.CheckOut(mdm, activity);
					}
					Log.d("StartUsingProceduresControl:datnd", "run: ===================================== OldMdmCheckOut");
					MDMControl mdmctrl = new MDMControl(activity, mdm.GetUDID());	// この時点でサービスを止める
					filename_mdm.delete();
				}
			}
		});
		t.start();

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

				Log.d("StartUsingProceduresControl", "SetMDM()");
				// 2. GetMDMDictionary
				XmlDictionary mdm_dict = m_p_aided_profile.GetMdmDictionary();
				if (mdm_dict == null) {
					Log.d("StartUsingProceduresControl", "SetMDM() No profile");
					return;
				}

				// 3. MDMFlgsにセット(MDMControlにMDMFlgs変数を持たせてそちらにやってもらう
				mdmctrl.SetMDMmember(mdm_dict);

				// 4. チェックイン(HTTP(S)) (新しいMDM設定情報もここでファイル保存する)
				boolean bret = mdmctrl.CheckIn(true);

				// 5. OKならスレッド起動...定期通信
				if(bret == false) {
					//	mdmctrl.startService();
					Log.e("StartUsingProceduresControl::SetMDM", "Checkin err");
					return;
				}

				bret = mdmctrl.TokenUpdate();
				if(bret == false) {
					//	mdmctrl.startService();
					Log.e("StartUsingProceduresControl::SetMDM", "TokenUpdate err");
					return;
				}
			}
		});
		t.start();
	}

	private void addDeviceAdmin() {
		Log.i("ProfileActivity", "addDeviceAdmin");
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
	private class DownloadCACertificateTask extends AsyncTask<Void, Void, Boolean> {
		@Override
		protected Boolean doInBackground(Void... params) {
			LogCtrl logCtrlAsyncTask = LogCtrl.getInstance(activity);
			HttpConnectionCtrl conn = new HttpConnectionCtrl(activity);
			//send request to server
			boolean ret = conn.RunHttpDownloadCertificate(m_InformCtrlCA);
			//parse result return
			if (ret == false) {
				logCtrlAsyncTask.loggerError("DownloadCACertificateTask Network error");
				m_nErroType = ERR_NETWORK;
				return false;
			}
			// ログイン結果
			if (m_InformCtrlCA.GetRtn().startsWith(activity.getText(R.string.Forbidden).toString())) {
				logCtrlAsyncTask.loggerError("DownloadCACertificateTask Forbidden.");
				m_nErroType = ERR_FORBIDDEN;
				return false;
			} else if (m_InformCtrlCA.GetRtn().startsWith(activity.getText(R.string.Unauthorized).toString())) {
				logCtrlAsyncTask.loggerError("DownloadCACertificateTask Unauthorized.");
				m_nErroType = ERR_UNAUTHORIZED;
				return false;
			} else if (m_InformCtrlCA.GetRtn().startsWith(activity.getText(R.string.ERR).toString())) {
				logCtrlAsyncTask.loggerError("DownloadCACertificateTask ERR:");
				m_nErroType = ERR_COLON;
				return false;
			}
			m_nErroType = SUCCESSFUL;

			return ret;
		}

		@Override
		protected void onPostExecute(Boolean result) {
			super.onPostExecute(result);
			if (result) {
				installCACert();
			} else {
				endConnection(result);
			}
		}
	}

	private void DownloadCACertificate(){
		if (element.getTarger().startsWith("WIFI")) {
			m_InformCtrlCA = new InformCtrl();
			String url = String.format("%s:%s", element.getHost(), element.getPort());
			m_InformCtrlCA.SetURL(url);
			//Open thread download cert
			new DownloadCACertificateTask().execute();
		} else {
			new CertificateEnrollTask().execute(scepRequester);
		}
	}

	/**
	 * Download and install certificate
	 */
	private void installCACert() {
		//Extract certificate from .mobileconfig file
		String cacert = m_InformCtrlCA.GetRtn();
		cacert = cacert.substring(cacert.indexOf("<?xml"));
		cacert = cacert.substring(0, cacert.indexOf("</plist>") + 8);
		XmlPullParserAided m_p_aided = new XmlPullParserAided(activity, cacert, 2);	// 最上位dictの階層は2になる
		boolean ret = m_p_aided.TakeApartProfileList();
		if (!ret) {
			showMessage(activity.getString(R.string.error_install_certificate));
			return;
		}
		List<XmlStringData> listPayloadContent = m_p_aided.GetDictionary().GetArrayString();
		cacert = listPayloadContent.get(listPayloadContent.size() - 1).GetData();
		cacert = String.format("%s\n%s\n%s", "-----BEGIN CERTIFICATE-----", cacert, "-----END CERTIFICATE-----");
		//Install certificate
		Intent intent = KeyChain.createInstallIntent();
		try {
			javax.security.cert.X509Certificate x509 = javax.security.cert.X509Certificate.getInstance(cacert.getBytes());
			intent.putExtra(KeyChain.EXTRA_CERTIFICATE, x509.getEncoded());
			intent.putExtra(KeyChain.EXTRA_NAME, InputPortPageFragment.payloadDisplayName);
			activity.startActivityForResult(intent, ViewPagerInputActivity.REQUEST_CODE_INSTALL_CERTIFICATION_VIEWPAGER_INPUT);
		} catch (Exception e) {
			logCtrl.loggerInfo("StartUsingProceduresActivity:installCACert : " + activity.getString(R.string
					.error_install_certificate));
			showMessage(activity.getString(R.string.error_install_certificate));
		}
	}


}