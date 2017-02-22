package epsap4.soliton.co.jp.xmlparser;

///////////////////////////////////////
// プロファイルリスト情報の一個単位
///////////////////////////////////////
public class XmlProfilePiece {
	/**
	 * 
	 */
	
	// メンバ変数:入力
	private String m_profileid;		// ProfileID
	private String m_profilename;	// Profile Name
	
	// コンストラクタ
	public XmlProfilePiece (){}
	
	// 変数セット
	public void SetID(String id) { m_profileid = id;}
	public void SetProfileName(String name) { m_profilename = name;}
	
	// 変数ゲット
	public String GetId() { return m_profileid;}
	public String GetProfileName() { return m_profilename;}

}