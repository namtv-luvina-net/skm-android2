package jp.co.soliton.keymanager;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.util.Log;
import android.webkit.CookieManager;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertPathValidatorException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLHandshakeException;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import jp.co.soliton.keymanager.SSLUtils.KeyChainKeyManager;

//////////////////////////////////
// サーバとの通信周りを行う
//////////////////////////////////
public class HttpConnectionCtrl {
	
	private Context m_ctx;
	private String m_str_user_agtpro = "";
	private LogCtrl logCtrl;

	// コンストラクタ
	//public HttpConnectionCtrl() {
	//	trustAllHosts();	// 証明書認証を回避するための手続き2-1
		
	//}
	
	// コンストラクタ
	public HttpConnectionCtrl(Context context) {
		m_ctx = context;
		
		SetUserAgentProfile();
		
	//	trustAllHosts();	// 証明書認証を回避するための手続き2-1
		
		logCtrl = LogCtrl.getInstance(context);
	}
	
	private void SetUserAgentProfile() {
		logCtrl.loggerInfo("HttpConnectionCtrl:SetUserAgentProfile");
		try {
			// Apllication version
			PackageInfo packageInfo = null;
			packageInfo = m_ctx.getPackageManager().getPackageInfo(m_ctx.getPackageName(), PackageManager.GET_META_DATA);
			String str_appver = packageInfo.versionName;
			// OS Version
			String str_osver = Build.VERSION.RELEASE;
			
			m_str_user_agtpro = m_ctx.getString(R.string.User_Agentprofile, str_osver, str_appver);
			Log.d("UserAgent: ", m_str_user_agtpro);
			//LogCtrl.Logger(LogCtrl.m_strDebug, "UserAgent: " + m_str_user_agtpro, m_ctx);
		} catch (Exception e) {
			logCtrl.loggerError("HttpConnectionCtrl::SetUserAgentProfile::Exception : " + e.toString());
			e.printStackTrace();
			logCtrl.loggerError(e.toString());
		}
	}
	
	// プロファイルリスト取得
	// EPS-ap 1.2.x以降
	public boolean RunHttpGetProfileList(InformCtrl Inf) {
		logCtrl.loggerInfo("HttpConnectionCtrl:RunHttpGetProfileList");
		Log.i("HttpConnectionCtrl::RunHttpGetProfileList ", "start");
		
		HttpURLConnection http = null;
		
		try { 
			String strURL;
			
			if (Inf.GetURL().startsWith("https") == true
					|| Inf.GetURL().startsWith("http") == true) {
				strURL = Inf.GetURL();
			} else {
				strURL = "https://" + Inf.GetURL();
			}
			
			// HTTP 接続のopen
			URL url = new URL(/*"http://" + Inf.GetURL()*/strURL + "/api.php");
			
			if (url.getProtocol().toLowerCase().equals("https")) {
				trustAllHosts();	// 証明書認証を回避するための手続き2-1
				HttpsURLConnection urlconn = (HttpsURLConnection)url.openConnection();
				urlconn.setHostnameVerifier(DO_NOT_VERIFY); 	// 証明書認証を回避するための手続き2-2                
				http = urlconn; 
			} else {
				http = (HttpURLConnection)url.openConnection();
			}
			
			// コネクションタイムアウトを設定 
            http.setConnectTimeout(5000); 
            
			// HTTPメソッド定義
			http.setRequestMethod("POST");
			http.setRequestProperty("User-Agent", m_str_user_agtpro /*m_ctx.getText(R.string.User_Agentprofile).toString()*/);
			http.setRequestProperty("Accept-Language",  m_ctx.getString(R.string.accept_language));
			//http.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
			
			// 送受信
			return PostPacket(http, Inf, /*2048*/256);

		} catch (MalformedURLException e) {
			// FIXME
			Log.e("HttpConnectionCtrl::RunHttpLoginUrlConnection::MalformedURLException", e.toString());
			logCtrl.loggerError("HttpConnectionCtrl::RunHttpLoginUrlConnection::MalformedURLException" + e.toString());
			return false;
		} catch (IOException e) {
			// FIXME
			Log.e("HttpConnectionCtrl::RunHttpLoginUrlConnection::IOException", e.toString());
			logCtrl.loggerError("HttpConnectionCtrl::RunHttpLoginUrlConnection::IOException : " + e.toString());
			return false;
		}
	}
	
	// プロファイル取得
	public boolean RunHttpGetProfileMember(InformCtrl Inf, String str_profile_number) {
		logCtrl.loggerInfo("RunHttpGetProfileMember");
		Log.i("HttpConnectionCtrl::RunHttpGetProfileMember ", "start");
		
		HttpURLConnection http = null;
		
		try { 
			String strURL;

			if (Inf.GetURL().startsWith("https") == true
					|| Inf.GetURL().startsWith("http") == true) {
				strURL = Inf.GetURL();
			} else {
				strURL = "https://" + Inf.GetURL();
			}
			
			// HTTP 接続のopen
			URL url = new URL(/*"http://" + Inf.GetURL()*/strURL + "/api.php");
			
			if (url.getProtocol().toLowerCase().equals("https")) {
				trustAllHosts();	// 証明書認証を回避するための手続き2-1
				HttpsURLConnection urlconn = (HttpsURLConnection)url.openConnection();
				urlconn.setHostnameVerifier(DO_NOT_VERIFY); 	// 証明書認証を回避するための手続き2-2                
				http = urlconn; 
			} else {
				http = (HttpURLConnection)url.openConnection();
			}
			
			// コネクションタイムアウトを設定 
            http.setConnectTimeout(5000); 
            
			// HTTPメソッド定義
			http.setRequestMethod("POST");
			Log.i("HttpConnectionCtrl::RunHttpGetProfileMember ", "ProfileID=" + str_profile_number);
			http.setRequestProperty("ProfileID", str_profile_number);
			http.setRequestProperty("User-Agent", m_str_user_agtpro /*m_ctx.getText(R.string.User_Agentprofile).toString()*/);
			http.setRequestProperty("Accept-Language",  m_ctx.getString(R.string.accept_language));
			//http.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
			
			// 送受信
			return PostPacket(http, Inf, /*8192*/512);

		} catch (MalformedURLException e) {
			// FIXME
			Log.e("HttpConnectionCtrl::RunHttpLoginUrlConnection::MalformedURLException", e.toString());
			logCtrl.loggerError("HttpConnectionCtrl::RunHttpLoginUrlConnection::MalformedURLException" + e.toString());
			return false;
		} catch (IOException e) {
			// FIXME
			Log.e("HttpConnectionCtrl::RunHttpLoginUrlConnection::IOException", e.toString());
			logCtrl.loggerError("HttpConnectionCtrl::RunHttpLoginUrlConnection::IOException" + e.toString());
			return false;
		}
	}
	
	// SCEP
	public boolean RunHttpGetScepProfile(InformCtrl Inf, String strArias) {
		logCtrl.loggerInfo("RunHttpGetScepProfile");
		Log.i("HttpConnectionCtrl::RunHttpGetScepProfile ", "start");
		
		HttpURLConnection http = null;
		
		try { 
			String strURL;
			
			if (Inf.GetURL().startsWith("https") == true) {
				strURL = Inf.GetURL();
			} else if (Inf.GetURL().startsWith("http") == true) {
				strURL = Inf.GetURL().substring(4);	// "http"を削除
				strURL = "https" + strURL;
			} else {
				strURL = "https://" + Inf.GetURL();
			}
			
			// HTTP 接続のopen
			URL url = new URL(/*"http://" + Inf.GetURL()*/strURL + "");
			
			if (url.getProtocol().toLowerCase().equals("https")) {
				CACertAccess(this.m_ctx, strArias);
			//	trustAllHosts();	// 証明書認証を回避するための手続き2-1
				HttpsURLConnection urlconn = (HttpsURLConnection)url.openConnection();
				urlconn.setHostnameVerifier(DO_NOT_VERIFY); 	// 証明書認証を回避するための手続き2-2   				
				http = urlconn; 
			} else {
				http = (HttpURLConnection)url.openConnection();
			}
			
			// コネクションタイムアウトを設定 
            http.setConnectTimeout(5000); 
            
			// HTTPメソッド定義
			http.setRequestMethod("POST");
			http.setRequestProperty("User-Agent", m_str_user_agtpro /*m_ctx.getText(R.string.User_Agentprofile).toString()*/);
			//http.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
			http.setRequestProperty("Accept-Language",  m_ctx.getString(R.string.accept_language));
			
			// 送受信
			return PostPacket(http, Inf, /*2048*/256);

		} catch (MalformedURLException e) {
			// FIXME
			Log.e("HttpConnectionCtrl::RunHttpLoginUrlConnection::MalformedURLException", e.toString());
			logCtrl.loggerError("HttpConnectionCtrl::RunHttpLoginUrlConnection::MalformedURLException" + e.toString());
			return false;
		} catch (IOException e) {
			// FIXME
			Log.e("HttpConnectionCtrl::RunHttpLoginUrlConnection::IOException", e.toString());
			logCtrl.loggerError("HttpConnectionCtrl::RunHttpLoginUrlConnection::IOException" + e.toString());
			return false;
		}
	}
	
	// ログイン
	public boolean RunHttpLoginUrlConnection(InformCtrl Inf) {
		logCtrl.loggerInfo("RunHttpLoginUrlConnection");
		Log.i("HttpConnectionCtrl::RunHttpLoginUrlConnection ", "start");
		
		HttpURLConnection http = null;
		
		//CookieManager manager = CookieManager.getInstance();
		
		try { 
			String strURL;
			
			if (Inf.GetURL().startsWith("https") == true
					|| Inf.GetURL().startsWith("http") == true) {
				strURL = Inf.GetURL();
			} else {
				strURL = "https://" + Inf.GetURL();
			}
			
			// HTTP 接続のopen
			URL url = new URL(/*Inf.GetURL()*/strURL + "/api.php");
			
			if (url.getProtocol().toLowerCase().equals("https")) {
				trustAllHosts();	// 証明書認証を回避するための手続き2-1
				HttpsURLConnection urlconn = (HttpsURLConnection)url.openConnection();
				urlconn.setHostnameVerifier(DO_NOT_VERIFY); 	// 証明書認証を回避するための手続き2-2                
				http = urlconn; 
			} else {
				http = (HttpURLConnection)url.openConnection();
			}
			
			// コネクションタイムアウトを設定 
            http.setConnectTimeout(5000); 
            
			// HTTPメソッド定義
			http.setRequestMethod("POST");
			http.setRequestProperty("User-Agent", m_str_user_agtpro /*m_ctx.getText(R.string.User_Agentprofile).toString()*/);
			//http.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
			http.setRequestProperty("Accept-Language",  m_ctx.getString(R.string.accept_language));
			
			// 送受信
			return PostPacket(http, Inf, 512);

		} catch (MalformedURLException e) {
			// FIXME
			Log.e("HttpConnectionCtrl::RunHttpLoginUrlConnection::MalformedURLException", e.toString());
			logCtrl.loggerError("HttpConnectionCtrl::RunHttpLoginUrlConnection::MalformedURLException" + e.toString());
			return false;
		} catch (IOException e) {
			// FIXME
			Log.e("HttpConnectionCtrl::RunHttpLoginUrlConnection::IOException", e.toString());
			logCtrl.loggerError("HttpConnectionCtrl::RunHttpLoginUrlConnection::IOException" + e.toString());
			return false;
		}

	}

	// ログアウト
	public boolean RunHttpLogoutUrlConnection(InformCtrl Inf) {
		logCtrl.loggerInfo("RunHttpLogoutUrlConnection");
		HttpURLConnection http = null;
		
		try { 			
			String strURL;
			
			if (Inf.GetURL().startsWith("https") == true
					|| Inf.GetURL().startsWith("http") == true) {
				strURL = Inf.GetURL();
			} else {
				strURL = "https://" + Inf.GetURL();
			}
			// HTTP 接続のopen
			URL url = new URL(/*Inf.GetURL()*/strURL + "/logoff.php");
			
			CookieManager manager = CookieManager.getInstance();
			//CookieManager.getInstance().setCookie(Inf.GetURL(), Inf.GetCookie());
			//CookieSyncManager.getInstance().sync();
			
			if (url.getProtocol().toLowerCase().equals("https")) {
				trustAllHosts();	// 証明書認証を回避するための手続き2-1
				HttpsURLConnection urlconn = (HttpsURLConnection)url.openConnection();
				urlconn.setHostnameVerifier(DO_NOT_VERIFY); 	// 証明書認証を回避するための手続き2-2                
				http = urlconn; 
			} else {
				http = (HttpURLConnection)url.openConnection();
			}
			
			// コネクションタイムアウトを設定 
            http.setConnectTimeout(5000); 
            
         // HTTPメソッド定義
			http.setRequestMethod("POST");
			http.setRequestProperty("User-Agent", m_str_user_agtpro /*m_ctx.getText(R.string.User_Agentprofile).toString()*/);
			http.setRequestProperty("Cookie", Inf.GetLoginCookie());	// ログイン時に取得したCookieをそのまま使う.
			http.setRequestProperty("Accept-Language",  m_ctx.getString(R.string.accept_language));

			Inf.SetMessage("");	// ログアウトではメッセージは空にする.
			
			return PostPacket(http, Inf, 512);
			
            
		} catch (MalformedURLException e) {
			// FIXME
			Log.e("HttpConnectionCtrl::RunHttpLogoutUrlConnection::MalformedURLException", e.toString());
			logCtrl.loggerError("HttpConnectionCtrl::RunHttpLogoutUrlConnection::MalformedURLException" + e.toString());
			return false;
		} catch (IOException e) {
			// FIXME
			Log.e("HttpConnectionCtrl::RunHttpLogoutUrlConnection::IOException", e.toString());
			logCtrl.loggerError("HttpConnectionCtrl::RunHttpLogoutUrlConnection::IOException" + e.toString());
			return false;
		}

	}
	
	// enroll
	public boolean RunHttpEnrollUrlConnection(InformCtrl Inf) {
		logCtrl.loggerInfo("RunHttpEnrollUrlConnection");
		HttpURLConnection http = null;
		
		try { 			
			
			// HTTP 接続のopen
			URL url = new URL(Inf.GetURL() + "/enroll.php");

			
			if (url.getProtocol().toLowerCase().equals("https")) {
				trustAllHosts();	// 証明書認証を回避するための手続き2-1
				HttpsURLConnection urlconn = (HttpsURLConnection)url.openConnection();
				urlconn.setHostnameVerifier(DO_NOT_VERIFY); 	// 証明書認証を回避するための手続き2-2                
				http = urlconn; 
			} else {
				http = (HttpURLConnection)url.openConnection();
			}
			
			// コネクションタイムアウトを設定 
            http.setConnectTimeout(5000); 
            
         // HTTPメソッド定義
			http.setRequestMethod("POST");
			http.setRequestProperty("User-Agent", m_str_user_agtpro /*m_ctx.getText(R.string.User_Agentprofile).toString()*/);
			http.setRequestProperty("Cookie", Inf.GetLoginCookie());	// ログイン時に取得したCookieをそのまま使う.
			http.setRequestProperty("Accept-Language",  m_ctx.getString(R.string.accept_language));

			//Inf.SetMessage("");	// ログアウトではメッセージは空にする.
			
			return PostPacket(http, Inf, 512);
			
            
		} catch (MalformedURLException e) {
			// FIXME
			Log.e("HttpConnectionCtrl::RunHttpEnrollUrlConnection::MalformedURLException", e.toString());
			logCtrl.loggerError("HttpConnectionCtrl::RunHttpEnrollUrlConnection::MalformedURLException" + e.toString());
			return false;
		} catch (IOException e) {
			// FIXME
			Log.e("HttpConnectionCtrl::RunHttpEnrollUrlConnection::IOException", e.toString());
			logCtrl.loggerError("HttpConnectionCtrl::RunHttpEnrollUrlConnection::IOException" + e.toString());
			return false;
		}
	}
	
	public boolean RunHttpDeviceCertUrlConnection(InformCtrl Inf) {
		logCtrl.loggerInfo("RunHttpDeviceCertUrlConnection");
		Log.i("HttpConnectionCtrl::RunHttpDeviceCertUrlConnection ", "start");
		HttpURLConnection http = null;
		
		try {
			// HTTP 接続のopen
			URL url = new URL(Inf.GetSituationURL());	// 返信されたxml中のURLを使用する
			Log.i("HttpConnectionCtrl::RunHttpDeviceCertUrlConnection ", Inf.GetSituationURL());

			
			if (url.getProtocol().toLowerCase().equals("https")) {
				trustAllHosts();	// 証明書認証を回避するための手続き2-1
				HttpsURLConnection urlconn = (HttpsURLConnection)url.openConnection();
				urlconn.setHostnameVerifier(DO_NOT_VERIFY); 	// 証明書認証を回避するための手続き2-2                
				http = urlconn; 
			} else {
				http = (HttpURLConnection)url.openConnection();
			}
			
			// コネクションタイムアウトを設定 
            http.setConnectTimeout(5000); 
            
         // HTTPメソッド定義
			http.setRequestMethod("POST");
			http.setRequestProperty("User-Agent", m_str_user_agtpro /*m_ctx.getText(R.string.User_Agentprofile).toString()*/);
		//	http.setRequestProperty("Content-Type", "application/pkcs7-signature");
			http.setRequestProperty("Cookie", Inf.GetLoginCookie());
			http.setRequestProperty("Accept-Language",  m_ctx.getString(R.string.accept_language));

			return PostPacket(http, Inf, 512);
			
            
		} catch (MalformedURLException e) {
			// FIXME
			Log.e("HttpConnectionCtrl::RunHttpEnrollReturnUrlConnection::MalformedURLException", e.toString());
			logCtrl.loggerError("HttpConnectionCtrl::RunHttpEnrollReturnUrlConnection::MalformedURLException" + e.toString());
			return false;
		} catch (IOException e) {
			// FIXME
			logCtrl.loggerError("HttpConnectionCtrl::RunHttpEnrollReturnUrlConnection::IOException" + e.toString());
			return false;
		}
	}
	
	public boolean RunHttpApplyUrlConnection(InformCtrl Inf) {
		logCtrl.loggerInfo("RunHttpApplyUrlConnection");
		HttpURLConnection http = null;
		
		try {
			String strURL;
			
			if (Inf.GetURL().startsWith("https") == true
					|| Inf.GetURL().startsWith("http") == true) {
				strURL = Inf.GetURL();
			} else {
				strURL = "https://" + Inf.GetURL();
			}
			
			// HTTP 接続のopen
			URL url = new URL(/*Inf.GetURL()*/strURL + "/api.php");
			
			if (url.getProtocol().toLowerCase().equals("https")) {
				trustAllHosts();	// 証明書認証を回避するための手続き2-1
				HttpsURLConnection urlconn = (HttpsURLConnection)url.openConnection();
				urlconn.setHostnameVerifier(DO_NOT_VERIFY); 	// 証明書認証を回避するための手続き2-2                
				http = urlconn; 
			} else {
				http = (HttpURLConnection)url.openConnection();
			}
			
			// コネクションタイムアウトを設定 
            http.setConnectTimeout(5000); 
            
         // HTTPメソッド定義
			http.setRequestMethod("POST");
			http.setRequestProperty("User-Agent", m_str_user_agtpro /*m_ctx.getText(R.string.User_Agentprofile).toString()*/);
		//	http.setRequestProperty("Content-Type", "application/pkcs7-signature");
			http.setRequestProperty("Cookie", Inf.GetLoginCookie());
			http.setRequestProperty("Accept-Language",  m_ctx.getString(R.string.accept_language));

			return PostPacket(http, Inf, 512);
			
            
		} catch (MalformedURLException e) {
			// FIXME
			Log.e("HttpConnectionCtrl::RunHttpEnrollReturnUrlConnection::MalformedURLException", e.toString());
			logCtrl.loggerError("HttpConnectionCtrl::RunHttpEnrollReturnUrlConnection::MalformedURLException" + e.toString());
			return false;
		} catch (IOException e) {
			// FIXME
			Log.e("HttpConnectionCtrl::RunHttpEnrollReturnUrlConnection::IOException", e.toString());
			logCtrl.loggerError("HttpConnectionCtrl::RunHttpEnrollReturnUrlConnection::IOException" + e.toString());
			return false;
		}
	}
	
	// MDM通信
	// MDM関連の通信は、ヘッダ情報は不変で、URLは通信フォーマットの中に入っているものを使うので(api.phpみたいな追加はなし)
	// 関数は共通とする.
	public boolean RunHttpMDMConnection(InformCtrl Inf) {
		logCtrl.loggerInfo("RunHttpMDMConnection");
		Log.i("HttpConnectionCtrl::RunHttpMDMConnection ", "start");
		HttpURLConnection http = null;
		
		try {
			// HTTP 接続のopen
			URL url = new URL(Inf.GetURL());	// 返信されたxml中のURLを使用する
			Log.i("HttpConnectionCtrl::RunHttpMDMConnection ", Inf.GetURL());

			
			if (url.getProtocol().toLowerCase().equals("https")) {
				trustAllHosts();	// 証明書認証を回避するための手続き2-1
				HttpsURLConnection urlconn = (HttpsURLConnection)url.openConnection();
				urlconn.setHostnameVerifier(DO_NOT_VERIFY); 	// 証明書認証を回避するための手続き2-2                
				http = urlconn; 
			} else {
				http = (HttpURLConnection)url.openConnection();
			}
			
			// コネクションタイムアウトを設定 
            http.setConnectTimeout(5000); 
            
         // HTTPメソッド定義
			http.setRequestMethod("POST");
			http.setRequestProperty("User-Agent", m_str_user_agtpro /*m_ctx.getText(R.string.User_Agentprofile).toString()*/);
		//	http.setRequestProperty("Content-Type", "application/pkcs7-signature");
		//	http.setRequestProperty("Cookie", Inf.GetLoginCookie());
			http.setRequestProperty("Accept-Language",  m_ctx.getString(R.string.accept_language));

			return PostPacket(http, Inf, 512);
			
            
		} catch (MalformedURLException e) {
			// FIXME
			Log.e("HttpConnectionCtrl::RunHttpMDMConnection::MalformedURLException", e.toString());
			logCtrl.loggerError("HttpConnectionCtrl::RunHttpMDMConnection::MalformedURLException" + e.toString());
			return false;
		} catch (IOException e) {
			// FIXME
			Log.e("HttpConnectionCtrl::RunHttpMDMConnection::IOException", e.toString());
			logCtrl.loggerError("HttpConnectionCtrl::RunHttpMDMConnection::IOException" + e.toString());
			return false;
		}
	}
	
	// アプリケーションリスト取得
	public boolean RunHttpGetApplicationList(InformCtrl Inf) {
		logCtrl.loggerInfo("RunHttpGetApplicationList");
		Log.i("HttpConnectionCtrl::RunHttpGetProfileList ", "start");
		
		HttpURLConnection http = null;
		
		try { 
			String strURL;
			
			if (Inf.GetURL().startsWith("https") == true
					|| Inf.GetURL().startsWith("http") == true) {
				strURL = Inf.GetURL();
			} else {
				strURL = "https://" + Inf.GetURL();
			}
			
			// HTTP 接続のopen
			URL url = new URL(/*"http://" + Inf.GetURL()*/strURL + "/api.php");
			
			if (url.getProtocol().toLowerCase().equals("https")) {
				trustAllHosts();	// 証明書認証を回避するための手続き2-1
				HttpsURLConnection urlconn = (HttpsURLConnection)url.openConnection();
				urlconn.setHostnameVerifier(DO_NOT_VERIFY); 	// 証明書認証を回避するための手続き2-2                
				http = urlconn; 
			} else {
				http = (HttpURLConnection)url.openConnection();
			}
			
			// コネクションタイムアウトを設定 
            http.setConnectTimeout(5000); 
            
			// HTTPメソッド定義
			http.setRequestMethod("POST");
			http.setRequestProperty("User-Agent", m_str_user_agtpro /*m_ctx.getText(R.string.User_Agentprofile).toString()*/);
			//http.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
			http.setRequestProperty("Cookie", Inf.GetLoginCookie());
			http.setRequestProperty("Accept-Language",  m_ctx.getString(R.string.accept_language));
			
			// 送受信
			return PostPacket(http, Inf, /*2048*/256);

		} catch (MalformedURLException e) {
			// FIXME
			Log.e("HttpConnectionCtrl::RunHttpLoginUrlConnection::MalformedURLException", e.toString());
			logCtrl.loggerError("HttpConnectionCtrl::RunHttpLoginUrlConnection::MalformedURLException" + e.toString());
			return false;
		} catch (IOException e) {
			// FIXME
			Log.e("HttpConnectionCtrl::RunHttpLoginUrlConnection::IOException", e.toString());
			logCtrl.loggerError("HttpConnectionCtrl::RunHttpLoginUrlConnection::IOException" + e.toString());
			return false;
		}
	}
	
	// アプリケーションインストール
	public boolean RunHttpAppInstConnection(InformCtrl Inf, String str_apk) {
		logCtrl.loggerInfo("RunHttpAppInstConnection");
		HttpURLConnection http = null;
		
		try {
			String strURL;
			
			if (Inf.GetURL().startsWith("https") == true
					|| Inf.GetURL().startsWith("http") == true) {
				strURL = Inf.GetURL();
			} else {
				strURL = "https://" + Inf.GetURL();
			}
			
			// HTTP 接続のopen
			URL url = new URL(/*Inf.GetURL()*/strURL + "/api.php");
			
			if (url.getProtocol().toLowerCase().equals("https")) {
				trustAllHosts();	// 証明書認証を回避するための手続き2-1
				HttpsURLConnection urlconn = (HttpsURLConnection)url.openConnection();
				urlconn.setHostnameVerifier(DO_NOT_VERIFY); 	// 証明書認証を回避するための手続き2-2                
				http = urlconn; 
			} else {
				http = (HttpURLConnection)url.openConnection();
			}
			
			// コネクションタイムアウトを設定 
            http.setConnectTimeout(5000); 
            
         // HTTPメソッド定義
			http.setRequestMethod("POST");
			http.setRequestProperty("User-Agent", m_str_user_agtpro /*m_ctx.getText(R.string.User_Agentprofile).toString()*/);
			http.setRequestProperty("Cookie", Inf.GetLoginCookie());
			http.setRequestProperty("Accept-Language",  m_ctx.getString(R.string.accept_language));

			return ApkDlandInstall(http, Inf, str_apk);		// ここでインストーラを実行する. 
			
            
		} catch (MalformedURLException e) {
			// FIXME
			Log.e("HttpConnectionCtrl::RunHttpEnrollReturnUrlConnection::MalformedURLException", e.toString());
			logCtrl.loggerError("HttpConnectionCtrl::RunHttpEnrollReturnUrlConnection::MalformedURLException" + e.toString());
			return false;
		} catch (IOException e) {
			// FIXME
			Log.e("HttpConnectionCtrl::RunHttpEnrollReturnUrlConnection::IOException", e.toString());
			logCtrl.loggerError("HttpConnectionCtrl::RunHttpEnrollReturnUrlConnection::IOException" + e.toString());
			return false;
		}
	}

	public boolean RunHttpProbeHostCerConnection(InformCtrl Inf) {
		logCtrl.loggerInfo("RunHttpProbeHostCerConnection");
		HttpURLConnection http = null;

		try {
			String strURL;

			if (Inf.GetURL().startsWith("https") == true
					|| Inf.GetURL().startsWith("http") == true) {
				strURL = Inf.GetURL();
			} else {
				strURL = "https://" + Inf.GetURL();
			}

			// HTTP 接続のopen
			URL url = new URL(/*Inf.GetURL()*/strURL + "/api.php");

			if (url.getProtocol().toLowerCase().equals("https")) {
				HttpsURLConnection urlconn = (HttpsURLConnection)url.openConnection();
				http = urlconn;
			} else {
				http = (HttpURLConnection)url.openConnection();
			}

			// コネクションタイムアウトを設定
			http.setConnectTimeout(5000);

			// HTTPメソッド定義
			http.setRequestMethod("POST");
			http.setRequestProperty("User-Agent", m_str_user_agtpro /*m_ctx.getText(R.string.User_Agentprofile).toString()*/);
			http.setRequestProperty("Cookie", Inf.GetLoginCookie());
			http.setRequestProperty("Accept-Language",  m_ctx.getString(R.string.accept_language));

			return PostProbeCertPacket(http, Inf, 512);
		} catch (MalformedURLException e) {
			// FIXME
			Log.e("HttpConnectionCtrl::RunHttpProbeHostCerConnection::MalformedURLException", e.toString());
			logCtrl.loggerError("HttpConnectionCtrl::RunHttpProbeHostCerConnection::MalformedURLException" + e.toString());
			return false;
		} catch (IOException e) {
			// FIXME
			Log.e("HttpConnectionCtrl::RunHttpProbeHostCerConnection::IOException", e.toString());
			logCtrl.loggerError("HttpConnectionCtrl::RunHttpProbeHostCerConnection::IOException" + e.toString());
			return false;
		}
	}

	public boolean RunHttpDownloadCertificate(InformCtrl Inf) {
		logCtrl.loggerInfo("RunHttpDownloadCertificate");
		HttpURLConnection http = null;

		try {
			String strURL;

			if (Inf.GetURL().startsWith("https") == true
					|| Inf.GetURL().startsWith("http") == true) {
				strURL = Inf.GetURL();
			} else {
				strURL = "http://" + Inf.GetURL();
			}

			// HTTP 接続のopen
			URL url = new URL(/*Inf.GetURL()*/strURL + "/cacert.php");

			if (url.getProtocol().toLowerCase().equals("https")) {
				//trustAllHosts();	// 証明書認証を回避するための手続き2-1
				HttpsURLConnection urlconn = (HttpsURLConnection)url.openConnection();
				http = urlconn;
			} else {
				http = (HttpURLConnection)url.openConnection();
			}

			// コネクションタイムアウトを設定
			http.setConnectTimeout(5000);

			// HTTPメソッド定義
			http.setRequestMethod("GET");
			http.setRequestProperty("User-Agent", m_str_user_agtpro /*m_ctx.getText(R.string.User_Agentprofile).toString()*/);
			http.setRequestProperty("Accept-Language",  m_ctx.getString(R.string.accept_language));

			return PostPacket(http, Inf, 512);
		} catch (MalformedURLException e) {
			// FIXME
			Log.e("HttpConnectionCtrl::RunHttpEnrollReturnUrlConnection::MalformedURLException", e.toString());
			logCtrl.loggerError("HttpConnectionCtrl::RunHttpEnrollReturnUrlConnection::MalformedURLException" + e.toString
					());
			return false;
		} catch (IOException e) {
			// FIXME
			Log.e("HttpConnectionCtrl::RunHttpEnrollReturnUrlConnection::IOException", e.toString());
			logCtrl.loggerError("HttpConnectionCtrl::RunHttpEnrollReturnUrlConnection::IOException" + e.toString());
			return false;
		}
	}

	// ログイン
	public boolean RunHttpApplyLoginUrlConnection(InformCtrl Inf) {
		logCtrl.loggerInfo("RunHttpApplyLoginUrlConnection");
		Log.i("HttpConnectionCtrl::RunHttpApplyLoginUrlConnection ", "start");

		HttpURLConnection http = null;

		//CookieManager manager = CookieManager.getInstance();

		try {
			String strURL;

			if (Inf.GetURL().startsWith("https") == true
					|| Inf.GetURL().startsWith("http") == true) {
				strURL = Inf.GetURL();
			} else {
				strURL = "https://" + Inf.GetURL();
			}

			// HTTP 接続のopen
			URL url = new URL(/*Inf.GetURL()*/strURL + "/api.php");

			if (url.getProtocol().toLowerCase().equals("https")) {
				HttpsURLConnection urlconn = (HttpsURLConnection)url.openConnection();
				http = urlconn;
			} else {
				http = (HttpURLConnection)url.openConnection();
			}

			// コネクションタイムアウトを設定
			http.setConnectTimeout(5000);

			// HTTPメソッド定義
			http.setRequestMethod("POST");
			http.setRequestProperty("User-Agent", m_str_user_agtpro /*m_ctx.getText(R.string.User_Agentprofile).toString()*/);
			//http.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
			http.setRequestProperty("Accept-Language",  m_ctx.getString(R.string.accept_language));

			// 送受信
			return PostPacket(http, Inf, 512);

		} catch (MalformedURLException e) {
			// FIXME
			Log.e("HttpConnectionCtrl::RunHttpApplyLoginUrlConnection::MalformedURLException", e.toString());
			logCtrl.loggerError("HttpConnectionCtrl::RunHttpApplyLoginUrlConnection::MalformedURLException" + e.toString());
			return false;
		} catch (IOException e) {
			// FIXME
			Log.e("HttpConnectionCtrl::RunHttpApplyLoginUrlConnection::IOException", e.toString());
			logCtrl.loggerError("HttpConnectionCtrl::RunHttpApplyLoginUrlConnection::IOException" + e.toString());
			return false;
		}
	}

	// ログイン
	public boolean RunHttpDropUrlConnection(InformCtrl Inf) {
		logCtrl.loggerInfo("RunHttpDropUrlConnection");
		Log.i("HttpConnectionCtrl::RunHttpDropUrlConnection ", "start");

		HttpURLConnection http = null;

		//CookieManager manager = CookieManager.getInstance();

		try {
			String strURL;

			if (Inf.GetURL().startsWith("https") == true
					|| Inf.GetURL().startsWith("http") == true) {
				strURL = Inf.GetURL();
			} else {
				strURL = "https://" + Inf.GetURL();
			}

			// HTTP 接続のopen
			URL url = new URL(/*Inf.GetURL()*/strURL + "/api.php");

			if (url.getProtocol().toLowerCase().equals("https")) {
				HttpsURLConnection urlconn = (HttpsURLConnection)url.openConnection();
				http = urlconn;
			} else {
				http = (HttpURLConnection)url.openConnection();
			}

			// コネクションタイムアウトを設定
			http.setConnectTimeout(5000);

			// HTTPメソッド定義
			http.setRequestMethod("POST");
			http.setRequestProperty("User-Agent", m_str_user_agtpro /*m_ctx.getText(R.string.User_Agentprofile).toString()*/);
			//http.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
			http.setRequestProperty("Cookie", Inf.GetLoginCookie());	// ログイン時に取得したCookieをそのまま使う.
			http.setRequestProperty("Accept-Language",  m_ctx.getString(R.string.accept_language));

			// 送受信
			return PostPacket(http, Inf, 512);

		} catch (MalformedURLException e) {
			// FIXME
			Log.e("HttpConnectionCtrl::RunHttpApplyCerUrlConnection::MalformedURLException", e.toString());
			logCtrl.loggerError("HttpConnectionCtrl::RunHttpApplyCerUrlConnection::MalformedURLException" + e.toString());
			return false;
		} catch (IOException e) {
			// FIXME
			Log.e("HttpConnectionCtrl::RunHttpApplyCerUrlConnection::IOException", e.toString());
			logCtrl.loggerError("HttpConnectionCtrl::RunHttpApplyCerUrlConnection::IOException" + e.toString());
			return false;
		}
	}

	public boolean RunHttpApplyAPSapConnection(InformCtrl Inf) {
		logCtrl.loggerInfo("RunHttpApplyAPSapConnection");
		Log.i("HttpConnectionCtrl::RunHttpApplyAPSapConnection ", "start");

		HttpURLConnection http;
		try {
			String strURL;

			if (Inf.GetURL().startsWith("https") == true
					|| Inf.GetURL().startsWith("http") == true) {
				strURL = Inf.GetURL();
			} else {
				strURL = "https://" + Inf.GetURL();
			}

			// HTTP 接続のopen
			URL url = new URL(/*Inf.GetURL()*/strURL + "/api.php");

			if (url.getProtocol().toLowerCase().equals("https")) {
				HttpsURLConnection urlconn = (HttpsURLConnection)url.openConnection();
				http = urlconn;
			} else {
				http = (HttpURLConnection)url.openConnection();
			}

			// コネクションタイムアウトを設定
			http.setConnectTimeout(5000);

			// HTTPメソッド定義
			http.setRequestMethod("POST");
			http.setRequestProperty("User-Agent", m_str_user_agtpro);
			//http.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
			http.setRequestProperty("Accept-Language",  m_ctx.getString(R.string.accept_language));

			// 送受信
			return PostPacket(http, Inf, 512);

		} catch (MalformedURLException e) {
			// FIXME
			Log.e("HttpConnectionCtrl::RunHttpApplyAPSapConnection::MalformedURLException", e.toString());
			logCtrl.loggerError("HttpConnectionCtrl::RunHttpApplyAPSapConnection::MalformedURLException" + e.toString());
			return false;
		} catch (IOException e) {
			// FIXME
			Log.e("HttpConnectionCtrl::RunHttpApplyAPSapConnection::IOException", e.toString());
			logCtrl.loggerError("HttpConnectionCtrl::RunHttpApplyAPSapConnection::IOException" + e.toString());
			return false;
		}
	}

	// ログイン
	public boolean RunHttpApplyCerUrlConnection(InformCtrl Inf) {
		logCtrl.loggerInfo("RunHttpApplyCerUrlConnection");
		Log.i("HttpConnectionCtrl::RunHttpApplyCerUrlConnection ", "start");

		HttpURLConnection http = null;

		//CookieManager manager = CookieManager.getInstance();

		try {
			String strURL;

			if (Inf.GetURL().startsWith("https") == true
					|| Inf.GetURL().startsWith("http") == true) {
				strURL = Inf.GetURL();
			} else {
				strURL = "https://" + Inf.GetURL();
			}

			// HTTP 接続のopen
			URL url = new URL(/*Inf.GetURL()*/strURL + "/api.php");

			if (url.getProtocol().toLowerCase().equals("https")) {
				HttpsURLConnection urlconn = (HttpsURLConnection)url.openConnection();
				http = urlconn;
			} else {
				http = (HttpURLConnection)url.openConnection();
			}

			// コネクションタイムアウトを設定
			http.setConnectTimeout(5000);

			// HTTPメソッド定義
			http.setRequestMethod("POST");
			http.setRequestProperty("User-Agent", m_str_user_agtpro /*m_ctx.getText(R.string.User_Agentprofile).toString()*/);
			//http.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
			http.setRequestProperty("Cookie", Inf.GetLoginCookie());	// ログイン時に取得したCookieをそのまま使う.
			http.setRequestProperty("Accept-Language",  m_ctx.getString(R.string.accept_language));

			// 送受信
			return PostPacket(http, Inf, 512);

		} catch (MalformedURLException e) {
			// FIXME
			Log.e("HttpConnectionCtrl::RunHttpApplyCerUrlConnection::MalformedURLException", e.toString());
			logCtrl.loggerError("HttpConnectionCtrl::RunHttpApplyCerUrlConnection::MalformedURLException" + e.toString());
			return false;
		} catch (IOException e) {
			// FIXME
			Log.e("HttpConnectionCtrl::RunHttpApplyCerUrlConnection::IOException", e.toString());
			logCtrl.loggerError("HttpConnectionCtrl::RunHttpApplyCerUrlConnection::IOException" + e.toString());
			return false;
		}
	}

	private boolean PostPacketKayCyain(HttpURLConnection http, InformCtrl Inf, int i_packetsize) {
		logCtrl.loggerInfo("PostPacketKayCyain");
		return true;
	}
	
	private boolean ApkDlandInstall(HttpURLConnection http, InformCtrl Inf, String str_apk) {
		logCtrl.loggerInfo("ApkDlandInstall");
		try {

			// 送受信定義
			http.setDoInput(true);
			http.setDoOutput(true);
			// 接続
			http.connect();
			
			// 出力ストリーム
			ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
			PrintWriter out = new PrintWriter(byteStream);
			out.print(Inf.GetMessage());
						
			out.flush();
						
			byteStream.writeTo(http.getOutputStream());
			
			out.close();
			
			// 入力ストリーム
			int input_ret = http.getResponseCode();		// response code を取得
			logCtrl.loggerInfo("HttpConnectionCtrl::ApkDlandInstall Get ResponseCode" + Integer.toString(input_ret));
			Inf.SetResponseCode(input_ret);
			if(input_ret != StringList.RES_200_OK) {
				http.disconnect();
				return false;
			}
			
			// SDカードの設定
			if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED) == false) {
				// SDカード領域が存在しないときは抜ける
				logCtrl.loggerError("HttpConnectionCtrl::ApkDlandInstall: "+m_ctx.getString(R.string.instapp_nosdcard));
				Toast.makeText(m_ctx, R.string.instapp_nosdcard, Toast.LENGTH_SHORT).show();
				return false;
			}
			
			String log_path = m_ctx.getExternalFilesDir(null).getPath() + "/" + str_apk;
			FileOutputStream fos = new FileOutputStream(log_path);
			
			// ダウンロード開始            
            InputStream is = http.getInputStream();
            byte[] buffer = new byte[1024];
            int len = 0;
            while ((len = is.read(buffer)) != -1) {
                fos.write(buffer, 0, len);
            }
            fos.close();
            is.close();
            
            // Intent生成
            Intent intent = new Intent(Intent.ACTION_VIEW);
            // MIME type設定
            intent.setDataAndType(Uri.fromFile(new File(log_path)), "application/vnd.android.package-archive");
            // Intent発行
            m_ctx.startActivity(intent); 
			
			
		} catch (IOException e) {
			logCtrl.loggerError("HttpConnectionCtrl::ApkDlandInstall::IOException "+ e.toString());
			return false;
		}
		return true;
	}
	
	// パケットの送受信
	private boolean PostPacket(HttpURLConnection http, InformCtrl Inf, int i_packetsize) {
		logCtrl.loggerInfo("PostPacket");
		try {

			// 送受信定義
			http.setDoInput(true);
			http.setDoOutput(true);
			// 接続
			http.connect();
			
			// 出力ストリーム
			ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
			PrintWriter out = new PrintWriter(byteStream);
			out.print(Inf.GetMessage());
			
			out.flush();
			
			byteStream.writeTo(http.getOutputStream());
			
			out.close();

			// 入力ストリーム
			int input_ret = http.getResponseCode();		// response code を取得
			Log.i("HttpConnectionCtrl::PostPacket Get ResponseCode", Integer.toString(input_ret));
			Inf.SetResponseCode(input_ret);
			if(input_ret != StringList.RES_200_OK) {
				http.disconnect();
				return false;
			}
		
			BufferedInputStream bis = new BufferedInputStream( http.getInputStream() );
			//InputStream bis = http.getInputStream();	// BufferedInputStreamとInputStreamどちらでもいい感じ.
			byte[] buf_solo = new byte[i_packetsize];
			byte[] buf_all = new byte[65536];//{(byte)0xF8, (byte)0x9F};
//		    int i_size = bis.read(buf, 0, buf.length);// = bis.read(buf);		// 入力ストリームbisからバッファbufへ格納。サイズを取得
			int i_size;
			int i_size_total= 0;

			while ((i_size = bis.read(buf_solo, 0, buf_solo.length)) != -1) {
				// buf_soloで取得したbyte配列をbuf_allに追加していく.
				System.arraycopy(buf_solo, 0, buf_all, i_size_total, i_size);
				i_size_total += i_size;				
			}

		    // 証明書verifyテストコード
/*		    X509Certificate mime;// = X509Certificate.getInstance(buf);
		    try {
				CertificateFactory cf = CertificateFactory.getInstance("X.509");
				CertPath cp = cf.generateCertPath(bis, "PKCS7");
				
				List certs = cp.getCertificates();
				
				Iterator i = certs.iterator();
				while(i.hasNext()) {
					mime = (X509Certificate)i.next();
				}
			} catch (CertificateException e) {
				// TODO 自動生成された catch ブロック
				e.printStackTrace();
				Log.e("HttpConnectionCtrl::PostPacket::CertificateException", e.toString());
			}
*/		    
		    // 証明書verifyテストコード(終わり)
		    
		    // 返信が無ければ、エラーとする
			// パケットが無くてもエラーとならないときもあるため(MDMチェックインなど)、
			// この辺はInformontroll::GetResponseCodeと使い分ける
		    if (i_size_total < 1) {
		    	Log.e("HttpConnectionCtrl::PostPacket ", "Size 0.");
		    //	LogCtrl.Logger(LogCtrl.m_strError, "HttpConnectionCtrl::PostPacket "+ "Size 0.", m_ctx);
		    	http.disconnect();
		    	return false;
		    }
		    
//		    String retCode = new String(buf, 0, i_size);	// バッファの先頭(0)から最後尾(サイズ=i_size)までStringに変換
		    String retCode = new String(buf_all, 0, i_size_total);
		    Inf.SetRtn(retCode);
//			☆☆☆ 取得したXML文のログ ☆☆☆ 大量にログ出力される場合もあるので、デバッグ時以外コメント
    		System.out.println("syutokushita mojiretsu-ha? " + retCode);
//			//Log.i("MdmServiceActivity::RunHttpLoginUrlConnection BufferedInputStream", new String(buf));
			
		    bis.close();
			
			Log.i("HttpConnectionCtrl::PostPacket", "TRACE2");
			
			// Cookie取得
			String cookieValue = null;
			cookieValue = http.getHeaderField("Set-Cookie");
			Inf.SetCookie(cookieValue);
				
			System.out.println( "cookieValue1: " + cookieValue);
	
			Log.i("HttpConnectionCtrl::PostPacket", "TRACE3");
			
			http.disconnect();
		} catch (IOException e) {
			// FIXME
			Log.e("HttpConnectionCtrl::PostPacket::IOException ", e.toString());
			logCtrl.loggerError("HttpConnectionCtrl::PostPacket::IOException " + e.toString());
		//	LogCtrl.Logger(LogCtrl.m_strError, "HttpConnectionCtrl::PostPacket::IOException "+ e.toString(), m_ctx);
			return false;
		} /*catch (javax.security.cert.CertificateException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}*/ catch (Exception e) {
			Log.e("HttpConnectionCtrl::PostPacket::Other Exception ", e.toString());
			logCtrl.loggerError("HttpConnectionCtrl::PostPacket::Other Exception " + e.toString());
		//	LogCtrl.Logger(LogCtrl.m_strError, "HttpConnectionCtrl::PostPacket::Other Exception "+ e.toString(), m_ctx);
			e.printStackTrace();
			return false;
		}
		return true;
	}

	private boolean PostProbeCertPacket(HttpURLConnection http, InformCtrl Inf, int i_packetsize) {
		logCtrl.loggerInfo("PostProbeCertPacket");
		try {
			// 送受信定義
			http.setDoInput(true);
			http.setDoOutput(true);
			// 接続
			http.connect();

			// 出力ストリーム
			ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
			PrintWriter out = new PrintWriter(byteStream);
			out.print(Inf.GetMessage());

			out.flush();

			byteStream.writeTo(http.getOutputStream());

			out.close();

			// 入力ストリーム
			int input_ret = http.getResponseCode();        // response code を取得
			Log.i("HttpConnectionCtrl::PostProbeCertPacket Get ResponseCode", Integer.toString(input_ret));
			Inf.SetResponseCode(input_ret);
			if (input_ret != StringList.RES_200_OK) {
				http.disconnect();
				return false;
			}

			BufferedInputStream bis = new BufferedInputStream(http.getInputStream());
			byte[] buf_solo = new byte[i_packetsize];
			byte[] buf_all = new byte[65536];//{(byte)0xF8, (byte)0x9F};
			int i_size;
			int i_size_total = 0;

			while ((i_size = bis.read(buf_solo, 0, buf_solo.length)) != -1) {
				// buf_soloで取得したbyte配列をbuf_allに追加していく.
				System.arraycopy(buf_solo, 0, buf_all, i_size_total, i_size);
				i_size_total += i_size;
			}
			// この辺はInformontroll::GetResponseCodeと使い分ける
			if (i_size_total < 1) {
				Log.e("HttpConnectionCtrl::PostProbeCertPacket ", "Size 0.");
				//	LogCtrl.Logger(LogCtrl.m_strError, "HttpConnectionCtrl::PostPacket "+ "Size 0.", m_ctx);
				http.disconnect();
				return false;
			}
			String retCode = new String(buf_all, 0, i_size_total);
			Inf.SetRtn(retCode);
//			☆☆☆ 取得したXML文のログ ☆☆☆ 大量にログ出力される場合もあるので、デバッグ時以外コメント
			System.out.println("syutokushita mojiretsu-ha? " + retCode);
//			//Log.i("MdmServiceActivity::RunHttpLoginUrlConnection BufferedInputStream", new String(buf));

			bis.close();

			Log.i("HttpConnectionCtrl::PostProbeCertPacket", "TRACE2");

			// Cookie取得
			String cookieValue = null;
			cookieValue = http.getHeaderField("Set-Cookie");
			Inf.SetCookie(cookieValue);

			System.out.println("cookieValue1: " + cookieValue);

			Log.i("HttpConnectionCtrl::PostProbeCertPacket", "TRACE3");
			http.disconnect();
		} catch (IOException e) {
			Log.e("HttpConnectionCtrl::PostProbeCertPacket::IOException ", e.toString());
			logCtrl.loggerError("HttpConnectionCtrl::PostProbeCertPacket::IOException " + e.toString());
			if (e instanceof SSLHandshakeException) {
				Inf.SetRtn(m_ctx.getString(R.string.not_installed_ca));
				return true;
			} else {
				return false;
			}
		} catch (Exception e) {
			Log.e("HttpConnectionCtrl::PostProbeCertPacket::Other Exception ", e.toString());
			logCtrl.loggerError("HttpConnectionCtrl::PostProbeCertPacket::Other Exception " + e.toString());
			return false;
		}
		return true;
	}
	
	// http://stackoverflow.com/questions/995514/https-connection-android
	// always verify the host - dont check for certificate 
	final static HostnameVerifier DO_NOT_VERIFY = new HostnameVerifier() {
		public boolean verify(String hostname, SSLSession session) {
			return true;         
		}
	};  
	
	/**  * Trust every server - dont check for any certificate  */ 
	private static void trustAllHosts() {         
		// Create a trust manager that does not validate certificate chains         
		TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() {
			public java.security.cert.X509Certificate[] getAcceptedIssuers() {
				return new java.security.cert.X509Certificate[] {};
			}

			public void checkClientTrusted(X509Certificate[] chain,
					String authType) throws CertificateException {
			}
			public void checkServerTrusted(X509Certificate[] chain,
					String authType) throws CertificateException {

			}

			} };

		// Install the all-trusting trust manager
		try {
			SSLContext sc = SSLContext.getInstance("TLS"/*"SSL"*/);
			sc.init(null, trustAllCerts, new java.security.SecureRandom());
			HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
		} catch (Exception e) {
			e.printStackTrace();
		} 
	} 
	
	private static void CACertAccess(Context context, String strArias) {
		LogCtrl.getInstance(context).loggerInfo("CACertAccess");
//		System.setProperty("javax.net.ssl.trustStore", /*"C:\\tmp\\cer\\cacerts"*/"epsapCA");
//		System.setProperty("javax.net.ssl.trustStorePassword", "password");
//		System.setProperty("javax.net.ssl.keyStore", /*"C:\\tmp\\cer\\testcert.p12"*/"epsap");
//		System.setProperty("javax.net.ssl.keyStorePassword", "password");

	/*	System.setProperty("javax.net.ssl.trustStore", "myTrustStore");
		System.setProperty("javax.net.ssl.trustStorePassword", "changeit");*/
		System.setProperty("javax.net.ssl.keyStoreType", "pkcs12");
		System.setProperty("javax.net.ssl.keyStore", "new.p12");
		System.setProperty("javax.net.ssl.keyStorePassword", "password");
		
		
		// サーバ側の証明書の処理
	/*KeyStore trust_store;*/
		try {
			/*trust_store = KeyStore.getInstance("BKS");
		
		char[] trust_pass = System.getProperty("javax.net.ssl.trustStorePassword").toCharArray();
		trust_store.load(new FileInputStream(System.getProperty("javax.net.ssl.trustStore")), trust_pass);
		TrustManagerFactory tmf = TrustManagerFactory.getInstance("SunX509");
		tmf.init(trust_store);
*/
		// クライアント側の証明書の処理
	KeyStore key_store = KeyStore.getInstance("PKCS12");


	char[] key_pass = System.getProperty("javax.net.ssl.keyStorePassword").toCharArray();
		key_store.load(new FileInputStream(System.getProperty("javax.net.ssl.keyStore")), key_pass);
			boolean ret = key_store.containsAlias("epsap");
			if(ret == true) {
				Log.i("HttpConnection::CACertAccess", "epsap exist!!");
			} else Log.i("HttpConnection::CACertAccess", "epsap No exist!!");
			if (key_store.containsAlias("epsapCA") == true) {
				Log.i("HttpConnection::CACertAccess", "epsapCA exist!!");
			} else Log.i("HttpConnection::CACertAccess", "epsapCA No exist!!");
		KeyManagerFactory kmf = KeyManagerFactory.getInstance("SunX509");
	kmf.init(key_store, key_pass);

		// <== SSLUtils対応 
		KeyManager keyManager =
		        KeyChainKeyManager.fromAlias(context, strArias/*"epsap"*/);
		KeyManager[] keyManagerList = new KeyManager[] { keyManager };
		// SSLUtils対応 ==>
		
		// SSLContextの初期化
		SSLContext sslcontext= SSLContext.getInstance("SSL");
		sslcontext.init(kmf.getKeyManagers(),null, new SecureRandom());
		HttpsURLConnection.setDefaultSSLSocketFactory(sslcontext.getSocketFactory());
		//httpsurlconnection.setSSLSocketFactory(sslcontext.getSocketFactory());
//		} catch (KeyStoreException e) {
			// TODO 自動生成された catch ブロック
//			e.printStackTrace();
//		} catch (NoSuchAlgorithmException e) {
			// TODO 自動生成された catch ブロック
//			e.printStackTrace();
//		} catch (CertificateException e) {
			// TODO 自動生成された catch ブロック
//			e.printStackTrace();
//		} catch (FileNotFoundException e) {
			// TODO 自動生成された catch ブロック
//			e.printStackTrace();
//		} catch (IOException e) {
			// TODO 自動生成された catch ブロック
//			e.printStackTrace();
//		} catch (UnrecoverableKeyException e) {
			// TODO 自動生成された catch ブロック
//			e.printStackTrace();
//		} catch (KeyManagementException e) {
			// TODO 自動生成された catch ブロック
//			e.printStackTrace();
//		} catch (KeyStoreException e) {
//			e.printStackTrace();
//		} catch (IOException e) {
//			e.printStackTrace();
		} catch (Exception e) {
			LogCtrl.getInstance(context).loggerError("HttpConnectionCtrl::CACertAccess : " + e.toString());
			e.printStackTrace();
		}
	}
	

}