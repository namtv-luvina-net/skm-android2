package jp.co.soliton.keymanager;

import java.io.Serializable;

///////////////////////////////////////
// ログイン情報や、サーバとの送受信メッセージの管理を行う
///////////////////////////////////////
public class InformCtrl implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	// メンバ変数:出力
	private String m_strUserID;		// ユーザID
	private String m_strPassword;	// パスワード
	private String m_strURL;		// サーバURL
	private String m_strSituationURL;	// xmlに指定されるURL
	private String m_message;		// 送信メッセージ
	private String m_strAPID;		// APID
	
	// メンバ変数:入力
	private String m_rtn;			// サーバからの応答メッセージ
	private int m_ResponseCode;		// サーバからのresponse code
	private String m_strCookie;		// Cookie
	private String m_strLoginCookie;	// Login時の返信Cookie(保持)
	private String m_strMailAddress;	// 申請要求のMail Address
	private String m_strDescription;	// 申請要求のDescription
	private int m_nSubmitted;			// 申請の有無
	private byte[] m_bytes;
	
	// URL Scheme自動接続
	private boolean m_bAutoConnect = false;	// autoconnect
	
	// コンストラクタ
	public InformCtrl (){}
	
	// 変数セット
	public void SetUserID(String userid) { m_strUserID = userid;}
	public void SetPassword(String password) { m_strPassword = password;}
	public void SetURL(String url) { m_strURL = url;}
	public void SetMessage(String message) { m_message = message;}
	public void SetRtn(String rtn) { m_rtn = rtn;}
	public void SetResponseCode(int code) { m_ResponseCode = code; }
	public void SetCookie(String cookie) { m_strCookie = cookie;}
	public void SetLoginCookie(String cookie) { m_strLoginCookie = cookie;}
	public void SetRtnBytes(byte[] buf) { m_bytes = buf;}
	public void SetSituationURL(String url) { m_strSituationURL = url;}
	public void SetMailAddress(String addr) { m_strMailAddress = addr; }
	public void SetDescription(String desc) { m_strDescription = desc; }
	public void SetSubmitted(int submit) { m_nSubmitted = submit; }
	public void SetAutoConnect(boolean connect) { m_bAutoConnect = connect; }
	public void SetAPID(String apid) {m_strAPID = apid;}
	
	// 変数ゲット
	public String GetUserID() { return m_strUserID;}
	public String GetPassword() { return m_strPassword;}
	public String GetURL() { return m_strURL;}
	public String GetMessage() { return m_message;}
	public String GetRtn() { return m_rtn;}
	public int GetResponseCode() { return m_ResponseCode; }
	public String GetCookie() { return m_strCookie;}
	public String GetLoginCookie() { return m_strLoginCookie;}
	public byte[] GetRtnBytes() { return m_bytes;}
	public String GetSituationURL() { return m_strSituationURL;}
	public String GetMailAddress() { return m_strMailAddress; }
	public String GetDescription() { return m_strDescription; }
	public int GetSubmitted() { return m_nSubmitted; }
	public boolean GetAutoConnect() { return m_bAutoConnect; }
	public String GetAPID() { return m_strAPID;}
}