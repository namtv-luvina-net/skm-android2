/**
 * 
 */
package epsap4.soliton.co.jp;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

/**
 * @author ymori
 *
 */
public class AddressInfo {

	/**
	 * 
	 */
	
	private static String m_strMail = "";
	private static String m_strPhone = "";
	
	public AddressInfo() {
		// TODO 自動生成されたコンストラクター・スタブ
	}

	public static void SetMailAddress(String address) {m_strMail = address;}
	public static void SetPhoneNumber(String phone) {m_strPhone = phone;}
	
	public static String GetMailAddress() { return m_strMail; }
	public static String GetPhoneNumber() { return m_strPhone; }
	
	public static void Runmailer(Context ctx) {
		Uri uri= Uri.parse("mailto:" + m_strMail);
		Intent intent=new Intent(Intent.ACTION_SENDTO, uri);
	//	intent.putExtra(Intent.EXTRA_SUBJECT,"タイトル");
	//	intent.putExtra(Intent.EXTRA_TEXT,"ボディのテキスト");
	//	intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		ctx.startActivity(intent);
	}
	
	public static void RunTelephone(Context ctx) {
	//	ダイアラーへ

		Intent di = new Intent(Intent.ACTION_VIEW, Uri.parse("tel:" + m_strPhone));
		ctx.startActivity(di);
	
		//	通話開始へ
	//	Uri uri=Uri.parse("tel:117");
	//	Intent intent=new Intent(Intent.ACTION_CALL,uri);
	//	startActivity(intent)
	}
}
