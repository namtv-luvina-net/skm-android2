package epsap4.soliton.co.jp.shortcut;

import android.util.Base64;
import android.util.Log;

public class ShortcutItem {
	private String m_action;	// アクション
	private String m_uriparse = "";	// URI
	private String m_shrtname = "";	// ショートカット名
	private boolean m_Isremove = true;	// 削除許可
	private boolean m_IsPrecomposed = true;	// 作成済み
	private byte[] m_iconbytes;
	
	public void SetAction(String action) {m_action = action;}
	public void SetUri(String uri) {m_uriparse = uri;}
	public void SetShortcutName(String name) { m_shrtname = name;}
	public void SetRemoval(boolean isremove) { m_Isremove = isremove;}
	public void SetPrecomposed(boolean isprecomposed) { m_IsPrecomposed = isprecomposed;}
	public void SetIcon(String str_icon) {
		Log.i("CreateShortcutLink::SetIcon", "Start." + str_icon);
		m_iconbytes = Base64.decode(str_icon, 0);//str_icon.getBytes(/*"UTF-8"*/);
	}
	
	public String GetAction() { return m_action; }
	public String GetUri() { return m_uriparse; }
	public String GetShortcutName() { return m_shrtname; }
	public boolean GetRemoval() { return m_Isremove; }
	public boolean GetPrecomposed() { return m_IsPrecomposed; }
	public byte[] GetIcon() { return m_iconbytes; }
}
