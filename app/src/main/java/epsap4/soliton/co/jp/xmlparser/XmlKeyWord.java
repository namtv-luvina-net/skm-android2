package epsap4.soliton.co.jp.xmlparser;

import java.util.ArrayList;
import java.util.List;

public class XmlKeyWord {
	/*--- Device Info ---*/
	private String m_strChallenge;	// CHALLENGE
	private String m_strSituationURL;	// xmlに指定されるURL
	
	/*--- MDM Info ---*/
	private String m_strCmdUUID;	// CommandUUID
	private String m_strReqtype;	// RequestType
	private List<String> m_stringlist;
	
	public XmlKeyWord() {
		m_stringlist = new ArrayList<String>();
	}
	
	public void SetChallenge(String word) { m_strChallenge = word; }
	public void SetArrayString(String word) { m_stringlist.add(word);}
	public void SetSituationURL(String url) { m_strSituationURL = url;}
	public void SetCmdUUID(String uuid) { m_strCmdUUID = uuid;}
	public void SetReqtype(String req) { m_strReqtype = req;}
	
	public String GetChallenge() { return m_strChallenge; }
	public List<String> GetArrayString() { return m_stringlist; }
	public String GetSituationURL() { return m_strSituationURL;}
	public String GetCmdUUID() { return m_strCmdUUID;}
	public String GetReqtype() { return m_strReqtype;}
}