package epsap4.soliton.co.jp.xmlparser;

// <key>単位でのクラスライブラリ
public class XmlStringData {	
	String strKeyName;	// キー名
	int    i_type;		// 要素タイプ(string:1, data=2, date=3, real=4, integer=5, true=6, false=7)
	String strData;		// 要素毎の文字列(integerなども文字列で保存する)
	
	public XmlStringData() {}
	
	public void SetKeyName(String keyname) { strKeyName = keyname;}
	public void Settype(int type) { i_type = type;}
	public void SetData(String data) { strData = data;}
	
	public String GetKeyName() { return strKeyName;}
	public int GetType() { return i_type;}
	public String GetData() { return strData;}
}
