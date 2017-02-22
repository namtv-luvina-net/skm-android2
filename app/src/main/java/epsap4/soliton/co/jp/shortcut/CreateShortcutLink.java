package epsap4.soliton.co.jp.shortcut;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Log;
import android.util.Xml;

import org.xmlpull.v1.XmlSerializer;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import epsap4.soliton.co.jp.StringList;
import epsap4.soliton.co.jp.xmlparser.XmlDictionary;
import epsap4.soliton.co.jp.xmlparser.XmlPullParserAided;
import epsap4.soliton.co.jp.xmlparser.XmlStringData;

public class CreateShortcutLink {
	
	private Context m_ctx;
	private List<ShortcutItem> m_pShortItem;
	
	public CreateShortcutLink(Context context)
	{
		m_ctx = context;
		m_pShortItem = new ArrayList<ShortcutItem>();
	}
	
	public void SetShortcutList(XmlDictionary dict) {
		// ここで分解してパラメータを振り分ける
		ShortcutItem item_piece = new ShortcutItem();
		item_piece.SetAction(Intent.ACTION_VIEW);	// 現状、action.viewのみ対応なのでここでセットしてしまおう.
		
		List<XmlStringData> str_list;
		str_list = dict.GetArrayString();
		for(int i = 0; str_list.size() > i; i++){
			// config情報に従って、処理を行う.
			XmlStringData p_data = str_list.get(i);
			SetConfigrationChild(p_data, item_piece);
		}
		
		m_pShortItem.add(item_piece);
	}
	
	private void SetConfigrationChild(XmlStringData p_data, ShortcutItem item_piece) {
		String strKeyName = p_data.GetKeyName();	// キー名
		int    i_type = p_data.GetType();		// 要素タイプ(string:1, data=2, date=3, real=4, integer=5, true=6, false=7)
		String strData = p_data.GetData();		// 要素
		
		Log.i("CreateShortcutLink::SetConfigrationChild", "Start. " + strKeyName);

		boolean b_type = true;
		if(i_type == 7) b_type = false;
		
		if(strKeyName.equalsIgnoreCase(StringList.m_str_webclip_label)) {
			item_piece.SetShortcutName(strData);
		} else if(strKeyName.equalsIgnoreCase(StringList.m_str_URL)) {
			item_piece.SetUri(strData);
		} else if(strKeyName.equalsIgnoreCase(StringList.m_str_removal)) {
			item_piece.SetRemoval(b_type);
		} else if(strKeyName.equalsIgnoreCase(StringList.m_str_precomposed)) {
			item_piece.SetPrecomposed(b_type);
		} else if(strKeyName.equalsIgnoreCase(StringList.m_str_icon)) {
			item_piece.SetIcon(strData);
		}
	}
	
	public boolean CreateRun() {
		Log.i("CreateShortcutLink::CreateRun", "Start.");
		
		String currentdir = System.getProperty("user.dir");
		Log.i("CreateShortcutLink", "current dir=" + currentdir);
		
		// 設定内容をファイル保存
		WriteShortcutInfo();
		
		for(int i = 0; m_pShortItem.size() > i; i++){
			ShortcutItem shortitem = m_pShortItem.get(i);
			CreateRunChild(shortitem);
		}
		
		return true;
		
	}
	
	private boolean CreateRunChild(ShortcutItem item_piece) {
		if(item_piece.GetShortcutName().length() < 1) {
			// ショートカットが定義されていないときは抜ける
			Log.i("CreateShortcutLink::CreateRunChild", "+=+= No Shortcut. =+=+");
			return true;
		}
		
		try {
			// ショートカットに持たせるインテントの内容
			Intent shortcutIntent = new Intent(item_piece.GetAction()/*Intent.ACTION_VIEW*/);
			//String str_scheme = Uri.parse(m_uriparse).getScheme();  // schemeを取得できる。Debug用
			shortcutIntent.setData(Uri.parse(item_piece.GetUri()/*"http://www.yahoo.co.jp"*/));
	
			// ショートカットをHOMEに作成する
			Intent intent = new Intent();
			intent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, shortcutIntent);
			intent.putExtra(Intent.EXTRA_SHORTCUT_NAME, item_piece.GetShortcutName());
			if(item_piece.GetPrecomposed()) intent.putExtra("duplicate", false);		// 複製禁止
			
			// アイコンをリソースから設定する
			//Parcelable iconResource = Intent.ShortcutIconResource.fromContext(m_ctx, R.drawable.ic_launcher);
			//intent.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE, iconResource);
			
			// アイコンを外部画像で設定する
			Log.i("CreateShortcutLink::CreateRunChild", "TRACE1.");
			byte[] iconbytes = item_piece.GetIcon();
			if(iconbytes != null){
			// http://stackoverflow.com/questions/4802457/intent-putextraintent-extra-shortcut-icon-bmp-image-is-off-center
			Bitmap theBitmap = //BitmapFactory.decodeFile("/data/data/epsap3.soliton.co.jp/img1203_a49.jpg" "/mnt/sdcard/Download/20110519hari14.jpg");
					BitmapFactory.decodeByteArray(iconbytes, 0, iconbytes.length);
			Log.i("CreateShortcutLink::CreateRunChild", "TRACE2.");
			if(theBitmap != null) {
				int yyy = theBitmap.getHeight();
				int xxx = theBitmap.getWidth();
				if(yyy > xxx) {
					xxx = 72 * xxx / yyy;
					yyy = 72;
				}
				else {
					yyy = 72 * yyy / xxx;
					xxx = 72;
				}
				Log.i("CreateShortcutLink::CreateRun", "TRACE3.");
				Log.i("SIZE X", Integer.toString(xxx));
				Log.i("SIZE Y", Integer.toString(yyy));
				Bitmap scaledBitmap = Bitmap.createScaledBitmap(theBitmap, xxx, yyy, true);
		//		Bitmap scaledBitmap = Bitmap.createBitmap(theBitmap, 0, 0, 1, 1);
				intent.putExtra(Intent.EXTRA_SHORTCUT_ICON, scaledBitmap);
			}
			}

			// ショートカット作成を設定して実行する
			intent.setAction("com.android.launcher.action.INSTALL_SHORTCUT");
			m_ctx.sendBroadcast(intent);
			return true;
		} catch (Exception e) {
            //e.printStackTrace();
            Log.e("CreateShortcutLink::CreateRunChild", e.toString());
            return false;            
        }
	}
	
	public void RemoveRun() {
		Log.i("CreateShortcutLink::RemoveRun", "Start.");
		
		for(int i = 0; m_pShortItem.size() > i; i++){
			ShortcutItem shortitem = m_pShortItem.get(i);
			RemoveRunChild(shortitem);
		}

	}
	
	private void RemoveRunChild(ShortcutItem item_piece) {
		if(item_piece.GetShortcutName().length() < 1) {
			// ショートカットが定義されていないときは抜ける
			Log.i("CreateShortcutLink::RemoveRunChild", "+=+= No Shortcut. =+=+");
			return;
		}
		
		if(item_piece.GetRemoval() == false) {
			// 削除許可が下りていない時は抜ける
			Log.i("CreateShortcutLink::RemoveRunChild", "+=+= IsRemoval NO. =+=+");
			return;
		}
		
		Intent shortcutIntent = new Intent(item_piece.GetAction());
		shortcutIntent.setData(Uri.parse(item_piece.GetUri()));
		shortcutIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		shortcutIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		
		Intent intent = new Intent();
		intent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, shortcutIntent);
		intent.putExtra(Intent.EXTRA_SHORTCUT_NAME, item_piece.GetShortcutName());
//		if(item_piece.GetPrecomposed()) intent.putExtra("duplicate", false);		// 複製禁止 ← ここをfalseにセットするとアイコンが消えない
				
		// ショートカット削除を設定して実行する
		intent.setAction("com.android.launcher.action.UNINSTALL_SHORTCUT");
		m_ctx.sendBroadcast(intent);
	}
	
	// ファイル出力
	public void WriteShortcutInfo() {
		String retmsg = "";
	
		XmlSerializer serializer = Xml.newSerializer();
		StringWriter writer = new StringWriter();
		try {
			serializer.setOutput(writer);	// XmlSerializerとStringWriterの関連付け..
			serializer.startDocument("UTF-8", true);
			serializer.startTag("", "plist");
			serializer.attribute("", "version", "1.0");
			for(int i = 0; m_pShortItem.size() > i; i++){
				ShortcutItem shortitem = m_pShortItem.get(i);

				serializer.startTag("", "dict");
		       
				/* ショートカット (ショートカット名, URI) **アクションはSchemeから判断すればよいので保存せず */
				// PayloadType
				XmlPullParserAided.SetParameter4Output(serializer, StringList.m_str_payloadtype, StringList.m_str_webclip_profile);
				// ショートカット名
				XmlPullParserAided.SetParameter4Output(serializer, StringList.m_str_webclip_label, shortitem.GetShortcutName());
				// URI
				XmlPullParserAided.SetParameter4Output(serializer, StringList.m_str_URL, shortitem.GetUri());
				// 削除許可
				SetParameter4Output(serializer, StringList.m_str_removal, shortitem.GetRemoval());
				// 複製
				SetParameter4Output(serializer, StringList.m_str_precomposed, shortitem.GetPrecomposed());
		       
				serializer.endTag("", "dict");
			}
			serializer.endTag("", "plist");
			serializer.endDocument();
	       
			// アウトプットをストリング型へ変換する
			retmsg = writer.toString();
		} catch (IOException e){
			Log.e("WriteRestrictionsInfo::IOException ", e.toString());
		}
	    
		Log.i("CreateShortcutLink::WriteShortcutInfo", retmsg);
		byte[] byArrData = retmsg.getBytes();
		OutputStream outputStreamObj=null;
	
		try {
			//Context ctx = new Context();
			//Contextから出力ストリーム取得
			outputStreamObj=m_ctx.openFileOutput(StringList.m_strShortcutOutputFile, Context.MODE_PRIVATE);
			//出力ストリームにデータを出力
			outputStreamObj.write(byArrData, 0, byArrData.length);
		} catch (FileNotFoundException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		} catch (IOException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}
	}
		
	// ファイル読み込み
	public void ReadAndSetShortcutInfo() {
		byte[] byArrData_read = null;
		int iSize;
		byte[] byArrTempData=new byte[4096];
		InputStream inputStreamObj=null;
		ByteArrayOutputStream byteArrayOutputStreamObj=null;

		try {
			//Contextから入力ストリームの取得
			inputStreamObj=m_ctx.openFileInput(StringList.m_strShortcutOutputFile);
			//
			byteArrayOutputStreamObj=new ByteArrayOutputStream();
			//ファイルからbyte配列に読み込み、さらにそれをByteArrayOutputStreamに追加していく
			while (true) {
				iSize=inputStreamObj.read(byArrTempData);
				if (iSize<=0) break;
				byteArrayOutputStreamObj.write(byArrTempData,0,iSize);
			}
			//ByteArrayOutputStreamからbyte配列に変換
			byArrData_read = byteArrayOutputStreamObj.toByteArray();
		} catch (Exception e) {
			Log.d("ReadAndSetShortcutInfo", e.getMessage());
		} finally{
			try {
				if (inputStreamObj!=null) inputStreamObj.close();
				if (byteArrayOutputStreamObj!=null) byteArrayOutputStreamObj.close();
			} catch (Exception e2) {
				Log.d("ReadAndSetShortcutInfo", e2.getMessage());
			}
			
		}
		
		String read_string = new String(byArrData_read);
		
		Log.d("*****Re-Read*****", read_string);
		
		// 新しくXmlPullParserAidedを作成する.
		XmlPullParserAided p_aided = new XmlPullParserAided(m_ctx, read_string, 1);
		p_aided.TakeApartProfile();		// ここで分解する
		List<XmlDictionary> p_dict = p_aided.GetWebClipDictList();
		
		// <key, type, data>リストを取得
		if(!p_dict.isEmpty()) {
			for(int i = 0; p_dict.size() > i; i++){
				XmlDictionary one_piece = p_dict.get(i);
				SetShortcutList(one_piece);
			}
		}

	}
			
	
	private void SetParameter4Output(XmlSerializer serializer, String keyname, boolean flg) {
		try {
			// falseの項目だけファイルに書き込む
			if(flg == /*true*/false) {
	        	serializer.startTag("", "key");
		        serializer.text(keyname);
		        serializer.endTag("", "key");
		        serializer.startTag("", StringList.m_strfalse);
		        serializer.endTag("", StringList.m_strfalse);
	        } 
		} catch (IOException e){
			Log.e("SetParameter::IOException ", e.toString());
		}
		    
	}
		
}