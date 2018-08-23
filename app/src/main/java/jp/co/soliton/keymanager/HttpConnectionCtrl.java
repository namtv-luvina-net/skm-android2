package jp.co.soliton.keymanager;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import org.apache.commons.io.IOUtils;
import org.bouncycastle.cms.CMSSignedDataParser;

import javax.net.ssl.*;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

//////////////////////////////////
// サーバとの通信周りを行う
//////////////////////////////////
public class HttpConnectionCtrl {
	private static final int TIME_OUT = 30000;
	private Context m_ctx;
	private String m_str_user_agtpro = "";

	// コンストラクタ
	//public HttpConnectionCtrl() {
	//	trustAllHosts();	// 証明書認証を回避するための手続き2-1
		
	//}

	// コンストラクタ
	public HttpConnectionCtrl(Context context) {
		m_ctx = context;
		SetUserAgentProfile();
		
	//	trustAllHosts();	// 証明書認証を回避するための手続き2-1

	}
	
	private void SetUserAgentProfile() {
		try {
			// Apllication version
			PackageInfo packageInfo = null;
			packageInfo = m_ctx.getPackageManager().getPackageInfo(m_ctx.getPackageName(), PackageManager.GET_META_DATA);
			String str_appver = packageInfo.versionName;
			// OS Version
			String str_osver = Build.VERSION.RELEASE;
			
			m_str_user_agtpro = m_ctx.getString(R.string.User_Agentprofile, str_osver, str_appver);
		} catch (Exception e) {
			LogCtrl.getInstance().error("HttpConnectionCtrl::SetUserAgentProfile::Exception: " + e.toString());
		}
	}
	
	public boolean RunHttpDeviceCertUrlConnection(InformCtrl Inf) {
		LogCtrl.getInstance().info("HttpConnectionCtrl: Request Profile");

		HttpURLConnection http = null;
		
		try {
			// HTTP 接続のopen
			URL url = new URL(Inf.GetSituationURL());	// 返信されたxml中のURLを使用する

			if (url.getProtocol().toLowerCase().equals("https")) {
				trustAllHosts();	// 証明書認証を回避するための手続き2-1
				HttpsURLConnection urlconn = (HttpsURLConnection)url.openConnection();
				urlconn.setHostnameVerifier(DO_NOT_VERIFY); 	// 証明書認証を回避するための手続き2-2                
				http = urlconn; 
			} else {
				http = (HttpURLConnection)url.openConnection();
			}
			
			// コネクションタイムアウトを設定 
            http.setConnectTimeout(TIME_OUT);
            
         // HTTPメソッド定義
			http.setRequestMethod("POST");
			http.setRequestProperty("User-Agent", m_str_user_agtpro /*m_ctx.getText(R.string.User_Agentprofile).toString()*/);
		//	http.setRequestProperty("Content-Type", "application/pkcs7-signature");
			http.setRequestProperty("Cookie", Inf.GetLoginCookie());
			http.setRequestProperty("Accept-Language",  m_ctx.getString(R.string.accept_language));

			return PostPacket(http, Inf, 512);

		} catch (MalformedURLException e) {
			LogCtrl.getInstance().error("HttpConnectionCtrl::RunHttpEnrollReturnUrlConnection::MalformedURLException: " + e.toString());
			return false;
		} catch (IOException e) {
			LogCtrl.getInstance().error("HttpConnectionCtrl::RunHttpEnrollReturnUrlConnection::IOException: " + e.toString());
			return false;
		}
	}
	
	// MDM通信
	// MDM関連の通信は、ヘッダ情報は不変で、URLは通信フォーマットの中に入っているものを使うので(api.phpみたいな追加はなし)
	// 関数は共通とする.
	public boolean RunHttpMDMConnection(InformCtrl Inf) {
		LogCtrl.getInstance().info("HttpConnectionCtrl: Request MDM");

		HttpURLConnection http = null;
		
		try {
			// HTTP 接続のopen
			URL url = new URL(Inf.GetURL());	// 返信されたxml中のURLを使用する

			if (url.getProtocol().toLowerCase().equals("https")) {
				trustAllHosts();	// 証明書認証を回避するための手続き2-1
				HttpsURLConnection urlconn = (HttpsURLConnection)url.openConnection();
				urlconn.setHostnameVerifier(DO_NOT_VERIFY); 	// 証明書認証を回避するための手続き2-2                
				http = urlconn; 
			} else {
				http = (HttpURLConnection)url.openConnection();
			}
			
			// コネクションタイムアウトを設定 
            http.setConnectTimeout(TIME_OUT);
            
         // HTTPメソッド定義
			http.setRequestMethod("POST");
			http.setRequestProperty("User-Agent", m_str_user_agtpro /*m_ctx.getText(R.string.User_Agentprofile).toString()*/);
		//	http.setRequestProperty("Content-Type", "application/pkcs7-signature");
		//	http.setRequestProperty("Cookie", Inf.GetLoginCookie());
			http.setRequestProperty("Accept-Language",  m_ctx.getString(R.string.accept_language));

			return PostPacket(http, Inf, 512);
            
		} catch (MalformedURLException e) {
			LogCtrl.getInstance().error("HttpConnectionCtrl::RunHttpMDMConnection::MalformedURLException: " + e.toString());
			return false;
		} catch (IOException e) {
			LogCtrl.getInstance().error("HttpConnectionCtrl::RunHttpMDMConnection::IOException: " + e.toString());
			return false;
		}
	}

	public boolean RunHttpProbeHostCerConnection(InformCtrl Inf) {
		LogCtrl.getInstance().info("HttpConnectionCtrl: Try TLS Connection");

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
			http.setConnectTimeout(TIME_OUT);

			// HTTPメソッド定義
			http.setRequestMethod("POST");
			http.setRequestProperty("User-Agent", m_str_user_agtpro /*m_ctx.getText(R.string.User_Agentprofile).toString()*/);
			http.setRequestProperty("Cookie", Inf.GetLoginCookie());
			http.setRequestProperty("Accept-Language",  m_ctx.getString(R.string.accept_language));

			return PostProbeCertPacket(http, Inf, 512);
		} catch (Exception e) {
			LogCtrl.getInstance().error("HttpConnectionCtrl::RunHttpProbeHostCerConnection::Exception: " + e.toString());
			e.printStackTrace();
			return false;
		}
	}

	public boolean RunHttpDownloadCertificate(InformCtrl Inf) {
		LogCtrl.getInstance().info("HttpConnectionCtrl: Request CA Certificate");

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
			http.setConnectTimeout(TIME_OUT);

			// HTTPメソッド定義
			http.setRequestMethod("GET");
			http.setRequestProperty("User-Agent", m_str_user_agtpro /*m_ctx.getText(R.string.User_Agentprofile).toString()*/);
			http.setRequestProperty("Accept-Language",  m_ctx.getString(R.string.accept_language));

			return GetPacket(http, Inf, 512);
		} catch (MalformedURLException e) {
			LogCtrl.getInstance().error("HttpConnectionCtrl::RunHttpEnrollReturnUrlConnection::MalformedURLException: " + e.toString());
			return false;
		} catch (IOException e) {
			LogCtrl.getInstance().error("HttpConnectionCtrl::RunHttpEnrollReturnUrlConnection::IOException: " + e.toString());
			return false;
		}
	}

	// ログイン
	public boolean RunHttpApplyLoginUrlConnection(InformCtrl Inf) {
		LogCtrl.getInstance().info("HttpConnectionCtrl: Request Login");

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
			http.setConnectTimeout(TIME_OUT);

			// HTTPメソッド定義
			http.setRequestMethod("POST");
			http.setRequestProperty("User-Agent", m_str_user_agtpro /*m_ctx.getText(R.string.User_Agentprofile).toString()*/);
			//http.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
			http.setRequestProperty("Accept-Language",  m_ctx.getString(R.string.accept_language));

			// 送受信
			return PostPacket(http, Inf, 512);

		} catch (MalformedURLException e) {
			LogCtrl.getInstance().error("HttpConnectionCtrl::RunHttpApplyLoginUrlConnection::MalformedURLException: " + e.toString());
			return false;
		} catch (IOException e) {
			LogCtrl.getInstance().error("HttpConnectionCtrl::RunHttpApplyLoginUrlConnection::IOException: " + e.toString());
			return false;
		}
	}

	// ログイン
	public boolean RunHttpDropUrlConnection(InformCtrl Inf) {
		LogCtrl.getInstance().info("HttpConnectionCtrl: Request Withdrawal");

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
			http.setConnectTimeout(TIME_OUT);

			// HTTPメソッド定義
			http.setRequestMethod("POST");
			http.setRequestProperty("User-Agent", m_str_user_agtpro /*m_ctx.getText(R.string.User_Agentprofile).toString()*/);
			//http.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
			http.setRequestProperty("Cookie", Inf.GetLoginCookie());	// ログイン時に取得したCookieをそのまま使う.
			http.setRequestProperty("Accept-Language",  m_ctx.getString(R.string.accept_language));

			// 送受信
			return PostPacket(http, Inf, 512);

		} catch (MalformedURLException e) {
			LogCtrl.getInstance().error("HttpConnectionCtrl::RunHttpApplyCerUrlConnection::MalformedURLException: " + e.toString());
			return false;
		} catch (IOException e) {
			LogCtrl.getInstance().error("HttpConnectionCtrl::RunHttpApplyCerUrlConnection::IOException: " + e.toString());
			return false;
		}
	}

	// ログイン
	public boolean RunHttpApplyCerUrlConnection(InformCtrl Inf) {
		LogCtrl.getInstance().info("HttpConnectionCtrl: Request Application");

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
			http.setConnectTimeout(TIME_OUT);

			// HTTPメソッド定義
			http.setRequestMethod("POST");
			http.setRequestProperty("User-Agent", m_str_user_agtpro /*m_ctx.getText(R.string.User_Agentprofile).toString()*/);
			//http.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
			http.setRequestProperty("Cookie", Inf.GetLoginCookie());	// ログイン時に取得したCookieをそのまま使う.
			http.setRequestProperty("Accept-Language",  m_ctx.getString(R.string.accept_language));

			// 送受信
			return PostPacket(http, Inf, 512);

		} catch (MalformedURLException e) {
			LogCtrl.getInstance().error("HttpConnectionCtrl::RunHttpApplyCerUrlConnection::MalformedURLException: " + e.toString());
			return false;
		} catch (IOException e) {
			LogCtrl.getInstance().error("HttpConnectionCtrl::RunHttpApplyCerUrlConnection::IOException: " + e.toString());
			return false;
		}
	}

	private String getAllHeaderFieldValues(Map headers) {
		Iterator headerIt = headers.keySet().iterator();
		String header = "{\r\n";
		while(headerIt.hasNext()){
			String headerKey = (String)headerIt.next();
			if (headerKey == null) {
				continue;
			}
			final List valueList = (List)headers.get(headerKey);
			final StringBuilder values = new StringBuilder();
			for (Object value : valueList) {
				values.append(value + ", ");
			}
			header += "  " + headerKey + ": " + values + "\r\n";
		}
		header += "}";
		return header;
	}

	private boolean GetPacket(HttpURLConnection http, InformCtrl Inf, int i_packetsize) {
		LogCtrl.getInstance().info("HttpConnectionCtrl: Send Request GET");
		LogCtrl.getInstance().debug(http.getURL().toString());
		LogCtrl.getInstance().debug(getAllHeaderFieldValues(http.getRequestProperties()));

		try {
			// 送受信定義
			http.setDoInput(true);

			// 接続
			http.connect();

			// 入力ストリーム
			int input_ret = http.getResponseCode();		// response code を取得

			LogCtrl.getInstance().info("HttpConnectionCtrl: Receive Response " + Integer.toString(input_ret));
			LogCtrl.getInstance().debug(getAllHeaderFieldValues(http.getHeaderFields()));

			Inf.SetResponseCode(input_ret);
			if(input_ret != StringList.RES_200_OK) {
				http.disconnect();
				return false;
			}

			BufferedInputStream bis = new BufferedInputStream(decodeData(http.getInputStream()));
			byte[] buf_solo = new byte[i_packetsize];
			byte[] buf_all = new byte[65536];
			int i_size;
			int i_size_total= 0;

			while ((i_size = bis.read(buf_solo, 0, buf_solo.length)) != -1) {
				System.arraycopy(buf_solo, 0, buf_all, i_size_total, i_size);
				i_size_total += i_size;
			}

			if (i_size_total < 1) {
				LogCtrl.getInstance().warn("HttpConnectionCtrl: No Content");
				http.disconnect();
				return false;
			}

			String cert = new String(buf_all, "UTF-8");
//			String retCode = new String(buf_all, 0, i_size_total);
			Inf.SetRtn(cert);

			LogCtrl.getInstance().debug("\r\n" + new String(buf_all));

			bis.close();

			// Cookie取得
			String cookieValue = null;
			cookieValue = http.getHeaderField("Set-Cookie");
			Inf.SetCookie(cookieValue);

			http.disconnect();
		} catch (IOException e) {
			LogCtrl.getInstance().error("HttpConnectionCtrl::GetPacket::IOException: " + e.toString());
			return false;
		} catch (Exception e) {
			LogCtrl.getInstance().error("HttpConnectionCtrl::GetPacket::Other Exception: " + e.toString());
			e.printStackTrace();
			return false;
		}
		return true;
	}

	private InputStream decodeData(InputStream inputStream) {
		try {
			CMSSignedDataParser parser = new CMSSignedDataParser(inputStream);
			return parser.getSignedContent().getContentStream();
		} catch (Exception e) {
			e.printStackTrace();
			return new InputStream() {
				@Override
				public int read() {
					return -1;
				}
			};
		}
	}
	
	// パケットの送受信
	private boolean PostPacket(HttpURLConnection http, InformCtrl Inf, int i_packetsize) {
		LogCtrl.getInstance().info("HttpConnectionCtrl: Send Request POST");
		LogCtrl.getInstance().debug(http.getURL().toString());
		LogCtrl.getInstance().debug(getAllHeaderFieldValues(http.getRequestProperties()));
		try {
			// 送受信定義
			http.setDoInput(true);
			http.setDoOutput(true);
			// 接続
			http.connect();
			
			// 出力ストリーム
			ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
			PrintWriter out = new PrintWriter(byteStream);

			String message = Inf.GetMessage();

			if (message != null && message.length() > 0 && (SKMApplication.SKM_DEBUG == true || SKMApplication.SKM_TRACE == true)) {
				String logMessage = message;
				int index = message.indexOf("&Password=");
				if (index != -1) {
					int index2 = message.indexOf("&", index + 1);
					logMessage = message.substring(0, index + 10) + "***";
					logMessage += message.substring(index2, message.length());
				}
				LogCtrl.getInstance().debug(logMessage);
			}

			out.print(Inf.GetMessage());
			
			out.flush();
			
			byteStream.writeTo(http.getOutputStream());
			
			out.close();

			// 入力ストリーム
			int input_ret = http.getResponseCode();		// response code を取得

			LogCtrl.getInstance().info("HttpConnectionCtrl: Receive Response " + Integer.toString(input_ret));
			LogCtrl.getInstance().debug(getAllHeaderFieldValues(http.getHeaderFields()));

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
			}
*/		    
		    // 証明書verifyテストコード(終わり)
		    
		    // 返信が無ければ、エラーとする
			// パケットが無くてもエラーとならないときもあるため(MDMチェックインなど)、
			// この辺はInformontroll::GetResponseCodeと使い分ける
		    if (i_size_total < 1) {
				LogCtrl.getInstance().warn("HttpConnectionCtrl: No Content");
		    	http.disconnect();
		    	return false;
		    }
		    
//		    String retCode = new String(buf, 0, i_size);	// バッファの先頭(0)から最後尾(サイズ=i_size)までStringに変換
		    String retCode = new String(buf_all, 0, i_size_total);
			Inf.SetRtn(retCode);

			LogCtrl.getInstance().debug("\r\n" + new String(buf_all));
			
		    bis.close();

			// Cookie取得
			String cookieValue = null;
			cookieValue = http.getHeaderField("Set-Cookie");
			Inf.SetCookie(cookieValue);

			http.disconnect();
		} catch (IOException e) {
			LogCtrl.getInstance().error("HttpConnectionCtrl::PostPacket::IOException: " + e.toString());
			return false;
		} catch (Exception e) {
			LogCtrl.getInstance().error("HttpConnectionCtrl::PostPacket::Other Exception: " + e.toString());
			e.printStackTrace();
			return false;
		}
		return true;
	}

	private boolean PostProbeCertPacket(HttpURLConnection http, InformCtrl Inf, int i_packetsize) {
		try {
			LogCtrl.getInstance().info("HttpConnectionCtrl: Send Request POST Probe");
			LogCtrl.getInstance().debug(http.getURL().toString());
			LogCtrl.getInstance().debug(getAllHeaderFieldValues(http.getRequestProperties()));

			// 送受信定義
			http.setDoInput(true);
			http.setDoOutput(true);
			// 接続
			http.connect();

			// 入力ストリーム
			int input_ret = http.getResponseCode();        // response code を取得

			LogCtrl.getInstance().info("HttpConnectionCtrl: Receive Response " + Integer.toString(input_ret));
			LogCtrl.getInstance().debug(getAllHeaderFieldValues(http.getHeaderFields()));

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
				LogCtrl.getInstance().warn("HttpConnectionCtrl: No Content");
				http.disconnect();
				return false;
			}
			String retCode = new String(buf_all, 0, i_size_total);
			Inf.SetRtn(retCode);

			LogCtrl.getInstance().debug("\r\n" + new String(buf_all));

			bis.close();

			// Cookie取得
			String cookieValue = null;
			cookieValue = http.getHeaderField("Set-Cookie");
			Inf.SetCookie(cookieValue);

			http.disconnect();
		} catch (IOException e) {
			LogCtrl.getInstance().error("HttpConnectionCtrl::PostProbeCertPacket::IOException: " + e.toString());
			if (e instanceof SSLHandshakeException) {
				Inf.SetRtn(m_ctx.getString(R.string.not_installed_ca));
				return true;
			} else {
				return false;
			}
		} catch (Exception e) {
			LogCtrl.getInstance().error("HttpConnectionCtrl::PostProbeCertPacket::Other Exception: " + e.toString());
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

}