package epsap4.soliton.co.jp;

import java.io.Serializable;

public class ScepInfo implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 3L;
	
	private String m_str_casert_url;		//== CA証明書のインストールアクセス先 ===//
	private String m_str_credentialname;	//== KeyChain.EXTRA_NAMEに設定する任意の資格名 ===//
	private String m_str_commonname;		//== CNに設定する文字列 ===//
	private String m_str_country;			//== Cに設定する文字列(JP固定で良いか？) ===//
	
	// コンストラクタ
	public ScepInfo() {};
	
	public void SetCaCertUrl(String url) {m_str_casert_url = url;}
	public void SetCredentName(String credential) {m_str_credentialname = credential;}
	public void SetCommonName(String commonname) {m_str_commonname = commonname;}
	public void SetCountry(String country) {m_str_country = country;}
	
	public String GetCaCertUrl() {return m_str_casert_url;}
	public String GetCredentName() {return m_str_credentialname;}
	public String GetCommonName() {return m_str_commonname;}
	public String GetCountry() {return m_str_country;}
}