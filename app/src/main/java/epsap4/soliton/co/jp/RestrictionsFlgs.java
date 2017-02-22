package epsap4.soliton.co.jp;

import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.util.Log;
import android.util.Xml;

import org.xmlpull.v1.XmlSerializer;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.io.StringWriter;
import java.util.List;

import epsap4.soliton.co.jp.xmlparser.XmlDictionary;
import epsap4.soliton.co.jp.xmlparser.XmlPullParserAided;
import epsap4.soliton.co.jp.xmlparser.XmlStringData;

public class RestrictionsFlgs implements Serializable {
	private static final long serialVersionUID = 2L;
	
	private final String m_strfalse = StringList.m_strfalse;//"false";
	private final String m_strFineName = StringList.m_strRestrictionFileName;//"MDMRestriction.txt";
	
	// カメラ
	private final String m_strCamera = StringList.m_strCamera;//"allowCamera";
	private boolean m_bCamera = true;
	
	// iTunes(=Anroid Market)
	private final String m_striTunes = StringList.m_striTunes;//"allowiTunes";
	private boolean m_biTunes = true;
	
	// YouTube
	private final String m_strYoutube = StringList.m_strYoutube;//"allowYouTube";
	private boolean m_bYouTube = true;
	
	// Safari
	private final String m_strSafari = StringList.m_strSafari;//"allowSafari";
	private boolean m_bSafari = true;
	
//	public RestrictionsFlgs(Context ctx) { m_ctx = ctx; }
	
	// falseだったらフラグをfalseセット
	public void SetCamera(boolean b_type, DevicePolicyManager dpm, ComponentName deviceadmin) {
		// setCameraDisabled は
		// true:カメラ起動不可, false:カメラ起動可 となり
		// EPS-apプロファイルの true:カメラ起動可, false:カメラ起動不可 と処理が逆になるためフラグを逆にセット
	    dpm.setCameraDisabled(deviceadmin, !b_type);	
		m_bCamera = b_type; 
	}
	public void SetiTunes(boolean b_type) { m_biTunes = b_type; }
	public void SetYouTube(boolean b_type) { m_bYouTube = b_type; }
	public void SetSafari(boolean b_type) { m_bSafari = b_type; }
	
	public boolean GetCamera() { return m_bCamera; }
	public boolean GetiTunes() { return m_biTunes; }
	public boolean GetYouTube() { return m_bYouTube; }
	public boolean GetSafari() { return m_bSafari; }
	
	
	// ファイル出力
	public void WriteRestrictionsInfo(Context ctx) {
		String retmsg = "";
		
		XmlSerializer serializer = Xml.newSerializer();
	    StringWriter writer = new StringWriter();
	    try {
	        serializer.setOutput(writer);	// XmlSerializerとStringWriterの関連付け..
	        serializer.startDocument("UTF-8", true);
	        serializer.startTag("", "plist");
	        serializer.attribute("", "version", "1.0");
	        serializer.startTag("", "dict");

	        // camera
	        SetParameter4Output(serializer, m_strCamera, m_bCamera);
	        // iTunes(=Anroid Market)
	        SetParameter4Output(serializer, m_striTunes, m_biTunes);
	        // YouTube
	        SetParameter4Output(serializer, m_strYoutube, m_bYouTube);
	        // Safari
	        SetParameter4Output(serializer, m_strSafari, m_bSafari);
	        
	        serializer.endTag("", "dict");
	        serializer.endTag("", "plist");
	        serializer.endDocument();
	        
	        // アウトプットをストリング型へ変換する
	        retmsg = writer.toString();

	    } catch (IOException e){
			Log.e("WriteRestrictionsInfo::IOException ", e.toString());
		}
	    
	    Log.i("RestrictionsFlgs::WriteRestrictionsInfo", retmsg);
	    byte[] byArrData = retmsg.getBytes();
		OutputStream outputStreamObj=null;
		
		try {
			//Context ctx = new Context();
			//Contextから出力ストリーム取得
			outputStreamObj=ctx.openFileOutput(m_strFineName, Context.MODE_PRIVATE);
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
	
	private void SetParameter4Output(XmlSerializer serializer, String keyname, boolean flg) {
		try {
			// falseの項目だけファイルに書き込む
			if(flg == /*true*/false) {
	        	serializer.startTag("", "key");
		        serializer.text(keyname);
		        serializer.endTag("", "key");
		        serializer.startTag("", m_strfalse);
		        serializer.endTag("", m_strfalse);
	        } 
		} catch (IOException e){
			Log.e("SetParameter::IOException ", e.toString());
		}
		    
	}
	
	// ファイル読み込み&フラグセット
	public boolean ReadAndSetRestictionsInfo(Context ctx) {
		byte[] byArrData_read = null;
		int iSize;
		byte[] byArrTempData=new byte[4096];
		InputStream inputStreamObj=null;
		ByteArrayOutputStream byteArrayOutputStreamObj=null;
		
		boolean bRet = true;

		try {
			//Contextから入力ストリームの取得
			inputStreamObj=ctx.openFileInput(m_strFineName);
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
			Log.d("ReadAndSetRestictionsInfo", e.getMessage());
			bRet = false;
		} finally{
			try {
				if (inputStreamObj!=null) inputStreamObj.close();
				if (byteArrayOutputStreamObj!=null) byteArrayOutputStreamObj.close();
			} catch (Exception e2) {
				Log.d("ReadAndSetRestictionsInfo", e2.getMessage());
				bRet = false;
			}
			
		}
		
		if(bRet == false) return bRet;
		
		String read_string = new String(byArrData_read);
		Log.d("*****Re-Read*****", read_string);
		
		// 新しくXmlPullParserAidedを作成する.
		XmlPullParserAided p_aided = new XmlPullParserAided(ctx, read_string, 2);
		p_aided.TakeApartControll();		// ここで分解する
		XmlDictionary p_dict = p_aided.GetDictionary();		// XmlPullParserAidedクラスで分類され、XmlDictionaryに振るいわけされた要素を取得
		
		// <key, type, data>リストを取得
		List<XmlStringData> str_list = p_dict.GetArrayString();
		for(int i = 0; str_list.size() > i; i++){
			// config情報に従って、処理を行う.
			XmlStringData p_data = str_list.get(i);
			SetParametorFromFile(p_data);
		}
		
		return bRet;
	}
	
	
	private void SetParametorFromFile(XmlStringData p_data) {
	
		String strKeyName = p_data.GetKeyName();	// キー名
		int    i_type = p_data.GetType();		// 要素タイプ(string:1, data=2, date=3, real=4, integer=5, true=6, false=7)
		
		// MDMRestriction.txtに存在した項目はfalse確定なので...
		if(strKeyName.equalsIgnoreCase(m_strCamera)) {
			m_bCamera = false;
		} else if(strKeyName.equalsIgnoreCase(m_striTunes)) {
			m_biTunes = false;
		} else if(strKeyName.equalsIgnoreCase(m_strYoutube)) {
			m_bYouTube = false;
		} else if(strKeyName.equalsIgnoreCase(m_strSafari)) {
			m_bSafari = false;
		}
	}
}