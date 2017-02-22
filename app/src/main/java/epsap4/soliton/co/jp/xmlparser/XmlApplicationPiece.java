package epsap4.soliton.co.jp.xmlparser;

public class XmlApplicationPiece {

	// メンバ変数:入力
	private String m_uuid;			// UUID
	private String m_appname;		// App Name
	private String m_appver;		// App Version
	private String m_icon;			// icon
	private String m_apkname;		// Apk Name
	
	// コンストラクタ
	public XmlApplicationPiece (){}
		
	// 変数セット
	public void SetUUID(String id) { m_uuid = id;}
	public void SetAppName(String name) { m_appname = name;}
	public void SetAppVersion(String version) { m_appver = version;}
	public void SetIcon(String icon) { m_icon = icon;}
	public void SetApkName(String apk) { m_apkname = apk;}
		
	// 変数ゲット
	public String GetUUID() { return m_uuid;}
	public String GetAppName() { return m_appname;}
	public String GetAppVersion() { return m_appver;}
	public String GetIcon() { return m_icon;}
	public String GetApkName() {return m_apkname;}

}
