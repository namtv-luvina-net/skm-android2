package jp.co.soliton.keymanager.xmlparser;

import android.util.Log;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import jp.co.soliton.keymanager.LogCtrl;

// <array>単位でのクラスライブラリ
public class XmlArrayData {
	private String m_xmldata_string;	// 取得データ原文
	
	String m_strKey;			// array下のネストのときはkeyは存在しない
	List<XmlStringData> m_stringData;
	List<XmlDictionary> m_xmlDictionary;	// 子dict配列
	List<XmlArrayData> m_Arrays;
	
	private int DepthOfDict;			// dictの階層
	
	public XmlArrayData(int depth, String key, String origin_xml) {
		m_strKey = key;
		DepthOfDict = depth;
		m_xmldata_string = origin_xml;
		
		m_stringData = new ArrayList<XmlStringData>();
		m_xmlDictionary = new ArrayList<XmlDictionary>();
		m_Arrays = new ArrayList<XmlArrayData>();
	}
	
	public void SetParam(String strtype, String strdata) {
		XmlStringData p_strdata = new XmlStringData();
		p_strdata.SetKeyName("");
		p_strdata.SetData(strdata);
		
		if(strtype.equals("string")) {
			p_strdata.Settype(1);
		} else if(strtype.equals("data")) {
			p_strdata.Settype(2);
		} else if(strtype.equals("date")) {
			p_strdata.Settype(3);
		} else if(strtype.equals("real")) {
			p_strdata.Settype(4);
		} else if(strtype.equals("integer")) {
			p_strdata.Settype(5);
		} else if(strtype.equals("true")) {
			p_strdata.Settype(6);
		} else if(strtype.equals("false")) {
			p_strdata.Settype(7);
		}
		
		m_stringData.add(p_strdata);
	}
	
	public void SetDict() {
		// key
		XmlDictionary new_xml_dict = new XmlDictionary(DepthOfDict + 1, "", m_xmldata_string);
		
		//XMLパーサーを生成する
		XmlPullParserFactory factory;
		try {
			factory = XmlPullParserFactory.newInstance();
			XmlPullParser parser = factory.newPullParser();
			
			//XMLパーサに解析したい内容を設定する
			parser.setInput(new StringReader(m_xmldata_string));
			
			String keyName = "";	// 初期化
			String typeName;
			String strdata;
			
			//XML文章の終わりまでループして解析する
			for(int eventType = parser.getEventType(); eventType !=
				XmlPullParser.END_DOCUMENT; eventType = parser.next()){
				String tagName;
				String tagText;
				

				if(parser.getDepth() != DepthOfDict + 1) {
					continue;
				}
				
				switch(eventType){
				//TAGの始まり
				case XmlPullParser.START_TAG:
					//TAGの名前を取得する
					tagName = parser.getName();

					if (tagName.equals("key")){
						//////////////////////////////////
						// keyの処理
						//////////////////////////////////
						//次の要素へ進む
						parser.next();
	
						//要素がTEXTだったら内容を取り出す
						if(parser.getEventType() == XmlPullParser.TEXT){
							keyName = parser.getText();		// key Nameにセット
						}
					} else if(tagName.equals("dict")){
						//////////////////////////////////
						// dictの処理
						//////////////////////////////////
						
						new_xml_dict.setDict(keyName);
					} else if (tagName.equals("array")) {
						//////////////////////////////////
						// arrayの処理
						//////////////////////////////////
						//bArray = true;	// フラグを立てるだけ
						new_xml_dict.setArray(keyName);
					} else {
						//////////////////////////////////
						// それ以外のstarttag要素
						//////////////////////////////////
						typeName = tagName;		// 型名を取得
						
						//次の要素へ進む
						parser.next();
	
						//要素がTEXTだったら内容を取り出す
						if(parser.getEventType() == XmlPullParser.TEXT){
							strdata = parser.getText();		// セット
						} else strdata = "";
						
						new_xml_dict.setParam(keyName, typeName, strdata);
					} // else
					
				}

				//break;
				
			}
			
		} catch (XmlPullParserException e) {
			LogCtrl.getInstance().error("XmlArrayData::XmlPullParserException: " + e.toString());
		} catch (IOException e) {
			LogCtrl.getInstance().error("XmlArrayData::IOException: " + e.toString());
		}
		m_xmlDictionary.add(new_xml_dict);
	}
	
	public void SetArray() {
		
		// key に対応したArrayが設定されていなかったら、XmlArrayDataを作成して追加
		XmlArrayData new_xml_array = new XmlArrayData(DepthOfDict + 1, "", m_xmldata_string);
		//XMLパーサーを生成する
		XmlPullParserFactory factory;
		try {
			factory = XmlPullParserFactory.newInstance();
			XmlPullParser parser = factory.newPullParser();
			
			//XMLパーサに解析したい内容を設定する
			parser.setInput(new StringReader(m_xmldata_string));
			String keyName = "";	// 初期化
			String typeName;
			String strdata;
			
			//XML文章の終わりまでループして解析する
			for(int eventType = parser.getEventType(); eventType !=
				XmlPullParser.END_DOCUMENT; eventType = parser.next()){
				String tagName;
				
				if(parser.getDepth() != DepthOfDict + 1) {
					continue;
				}
				
				switch(eventType){
				//TAGの始まり
				case XmlPullParser.START_TAG:
					
					//TAGの名前を取得する
					tagName = parser.getName();
					
					if(tagName.equals("dict")){
						//////////////////////////////////
						// dictの処理
						//////////////////////////////////
						
						new_xml_array.SetDict();	// キーネームはなし
					} else if (tagName.equals("array")) {
						//////////////////////////////////
						// arrayの処理
						//////////////////////////////////
						//bArray = true;	// フラグを立てるだけ
						new_xml_array.SetArray();
					} else {
						//////////////////////////////////
						// それ以外のstarttag要素
						//////////////////////////////////
						typeName = tagName;		// 型名を取得
						
						//次の要素へ進む
						parser.next();
	
						//要素がTEXTだったら内容を取り出す
						if(parser.getEventType() == XmlPullParser.TEXT){
							strdata = parser.getText();		// セット
						} else strdata = "";
						
						new_xml_array.SetParam(typeName, strdata);
					} // else
				}
			}
		} catch (XmlPullParserException e) {
			LogCtrl.getInstance().error("XmlArrayData::XmlPullParserException: " + e.toString());
		} catch (IOException e) {
			LogCtrl.getInstance().error("XmlArrayData::IOException: " + e.toString());
		}
		
		m_Arrays.add(new_xml_array);
	}
	
	public String GetKeyString() {return m_strKey;}
	
}
