package jp.co.soliton.keymanager.wifi;

public class WifiItem {
	private boolean m_bHidden;
	private String m_strssid = "";
	private String m_stridentity = "";
	private String m_strwifipass = "";
	private String m_strenctype = "";
	//https://android.googlesource.com/platform/frameworks/base/+/2b378cde411b551464f0040e935692073cfb119f/wifi/java/android/net/wifi/WifiEnterpriseConfig.java
	private String m_strphase2auth = "NULL";	// 初期値NULL
	private String m_strusern = "";
	private String m_struserp = "";
	private int m_numEaptype = WifiControl.NONE;
    
    // MDM system parameter Set Method.
    public void SetHidden(boolean hide) {m_bHidden = hide;}
    public void SetSSID(String ssid) {m_strssid = ssid;}
    public void SetIdentity(String identity) {m_stridentity = identity;}
    public void SetWifipass(String pass) {m_strwifipass = pass;}
    public void SetEncType(String enc) {m_strenctype = enc;}
    public void SetPhase2Auth(String phase2) {m_strphase2auth = phase2;}
    public void SetEAPType(String eap) {
    	int i_eap = Integer.parseInt(eap);
    	if(i_eap == WifiControl.TLS) m_numEaptype = WifiControl.TLS;
    	else if (i_eap == WifiControl.LEAP) m_numEaptype = WifiControl.LEAP;
    	else if (i_eap == WifiControl.TTLS) m_numEaptype = WifiControl.TTLS;
    	else if (i_eap == WifiControl.PEAP) m_numEaptype = WifiControl.PEAP;
    	else m_numEaptype = WifiControl.NONE;
    }
    public void SetUserName(String user) {m_strusern = user;}
    public void SetUserPass(String pass) {m_struserp = pass;}
    
    public String GetSSID() { return m_strssid;}	// SSID
    public boolean GetHidden() { return m_bHidden; }
    public String GetIdentity() { return m_stridentity; }
    public String GetWifipass() { return m_strwifipass; }
    public String GetEncType() { return m_strenctype; }
    public String GetPhase2Auth() { return m_strphase2auth; }
    public int GetEAPType() { return m_numEaptype; }
    public String GetUserName() { return m_strusern;}
    public String GetUserPass() { return m_struserp;}
}
