package jp.co.soliton.keymanager.xmlparser;

import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.util.Xml;
import jp.co.soliton.keymanager.*;
import jp.co.soliton.keymanager.fragment.InputPortPageFragment;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;
import org.xmlpull.v1.XmlSerializer;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.NetworkInterface;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class XmlPullParserAided /*extends Activity*/{
	// 変数
	private String m_xmldata_string;	// 取得データ原文
	private int DepthOfDict;			// dictの階層 (最上位dictは2)

	/*List<XmlDictionary>*/XmlDictionary m_xmlDictionary;
	XmlDictionary m_xmlScepDictionary;
	XmlDictionary m_xmlPassDictionary;
	XmlDictionary m_xmlAppDictionary;
	XmlDictionary m_xmlMdmDictionary;
	XmlKeyWord m_xmlKeyword;
	List<XmlProfilePiece> m_xmlProfile;
	List<XmlApplicationPiece> m_xmlApppiecceList;	//v1.2.2
	List<XmlDictionary> m_xmlwifiDictList;		// 複数Wi-Fi対応
	List<XmlDictionary> m_xmlShortcutDictList;	// 複数ショートカット対応
	XmlDictionary m_xmlRmvPwdDictionary;	// プロファイル削除パスワード
	List<String> m_StrListScepSubject;		// SCEP:証明書のSubject

	private Context m_ctx;

	// コンストラクタ
	public XmlPullParserAided(Context ctx, String data, int depth) {
		m_xmldata_string = data;
		DepthOfDict = depth;
		//m_xmlDictionary = new ArrayList<XmlDictionary>();
		m_xmlProfile = new ArrayList<XmlProfilePiece>();
		m_xmlApppiecceList = new ArrayList<XmlApplicationPiece>();		//v1.2.2
		m_xmlwifiDictList = new ArrayList<XmlDictionary>();
		m_xmlShortcutDictList = new ArrayList<XmlDictionary>();
		m_xmlDictionary = new XmlDictionary(DepthOfDict + 1, "", m_xmldata_string);	// 最上位のdictはkeyなし
		m_StrListScepSubject = new ArrayList<String>();

		m_ctx = ctx;

		m_xmlKeyword = new XmlKeyWord();
	}

	// 分解
	public boolean TakeApart() {

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

					//
					if (tagName.equals(m_ctx.getText(R.string.Property_key).toString())){
						//////////////////////////////////
						// keyの処理
						//////////////////////////////////
						//次の要素へ進む
						parser.next();

						//要素がTEXTだったら内容を取り出す
						if(parser.getEventType() == XmlPullParser.TEXT){
							keyName = parser.getText();		// key Nameにセット
						}
					} else if(tagName.equals(m_ctx.getText(R.string.Property_dict).toString())){
						//////////////////////////////////
						// dictの処理
						//////////////////////////////////

						m_xmlDictionary.setDict(keyName);
					} else if (tagName.equals(m_ctx.getText(R.string.Property_array).toString())) {
						//////////////////////////////////
						// arrayの処理
						//////////////////////////////////
						//bArray = true;	// フラグを立てるだけ
						m_xmlDictionary.setArray(keyName);
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

						m_xmlDictionary.setParam(keyName, typeName, strdata);
					} // else

				}

				//break;

			}

		} catch (XmlPullParserException e) {
			LogCtrl.getInstance().error("EnrollActivity::XmlPullParserException: " + e.toString());
			return false;
		} catch (IOException e) {
			LogCtrl.getInstance().error("EnrollActivity::IOException: " + e.toString());
			return false;
		}

		return true;
	}

	// 分解
	public boolean TakeApartUserAuthenticationResponse(InformCtrl inf) {

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
			boolean bArray = false;
			boolean bMailAdd = false;
			boolean bDescription = false;

			//XML文章の終わりまでループして解析する
			for(int eventType = parser.getEventType(); eventType !=
				XmlPullParser.END_DOCUMENT; eventType = parser.next()){
				String tagName;
				String tagText;

				if(parser.getDepth() < DepthOfDict + 1) {
					continue;
				}

				switch(eventType){
				//TAGの始まり
				case XmlPullParser.START_TAG:
					//TAGの名前を取得する
					tagName = parser.getName();

					//
					if (tagName.equals(m_ctx.getText(R.string.Property_key).toString())){
						//////////////////////////////////
						// keyの処理
						//////////////////////////////////
						bArray = false;
						//次の要素へ進む
						parser.next();

						//要素がTEXTだったら内容を取り出す
						if(parser.getEventType() == XmlPullParser.TEXT){
							keyName = parser.getText();		// key Nameにセット
						}

						// フラグMailAddress, DescriptionがONだったらkeyをそれぞれに変更
						if(bMailAdd == true) {
							keyName = StringList.m_str_mailaddress;
						} else if(bDescription == true) {
							keyName = StringList.m_str_description;
						}

						// keyが[MailAddress],または[Description]だったらフラグをONにする。
						if(keyName.equalsIgnoreCase(StringList.m_str_mailaddress)) {
							bMailAdd = true;
						} else if (keyName.equalsIgnoreCase(StringList.m_str_description)) {
							bDescription = true;
						}


					} else if(tagName.equals(m_ctx.getText(R.string.Property_dict).toString())){
						//////////////////////////////////
						// dictの処理
						//////////////////////////////////
						bArray = false;

						//m_xmlDictionary.setDict(keyName);
					} else if (tagName.equals(m_ctx.getText(R.string.Property_array).toString())) {
						//////////////////////////////////
						// arrayの処理
						//////////////////////////////////
						bArray = true;	// フラグを立てるだけ
						//m_xmlDictionary.setArray(keyName);
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

						// キーワードにセットしていく
						if(keyName.equalsIgnoreCase(StringList.m_str_challenge)) {	// 一応 大文字・小文字の区別を行わない
							m_xmlKeyword.SetChallenge(strdata);
							LogCtrl.getInstance().debug("XML Parser: Profile Challenge=" + strdata);
						} else if(keyName.equalsIgnoreCase("URL")) {
							m_xmlKeyword.SetSituationURL(strdata);
							inf.SetSituationURL(strdata);
							LogCtrl.getInstance().info("XML Parser: Response contained profile configuration");
							LogCtrl.getInstance().debug("XML Parser: Profile URL=" + strdata);
						} else if(bArray == true) {
							m_xmlKeyword.SetArrayString(strdata);
						} else if(keyName.equalsIgnoreCase(StringList.m_str_mailaddress)) {
							bMailAdd = false;
							m_xmlDictionary.setParam(keyName, typeName, strdata);
							LogCtrl.getInstance().info("XML Parser: Response contained an mail address");
							LogCtrl.getInstance().debug(strdata);
						} else if (keyName.equalsIgnoreCase(StringList.m_str_description)) {
							bDescription = false;
							m_xmlDictionary.setParam(keyName, typeName, strdata);
							LogCtrl.getInstance().info("XML Parser: Response contained a description");
							LogCtrl.getInstance().debug(strdata);
						} else {
							m_xmlDictionary.setParam(keyName, typeName, strdata);
						}
					} // else

				}

				//break;

			}

		} catch (XmlPullParserException e) {
			LogCtrl.getInstance().error("EnrollActivity::XmlPullParserException: " + e.toString());
			return false;
		} catch (IOException e) {
			LogCtrl.getInstance().error("EnrollActivity::IOException: " + e.toString());
			return false;
		}


		return true;
	}

	// SCEP Info
	public boolean TakeApartScepInfoResponse(InformCtrl inf) {

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
//			boolean bArray = false;
//			boolean bSubject = false;

			//XML文章の終わりまでループして解析する
			for(int eventType = parser.getEventType(); eventType !=
				XmlPullParser.END_DOCUMENT; eventType = parser.next()){
				String tagName;
				String tagText;

				if(parser.getDepth() < DepthOfDict + 1) {
					continue;
				}

				switch(eventType){
				//TAGの始まり
				case XmlPullParser.START_TAG:
					//TAGの名前を取得する
					tagName = parser.getName();

					//
					if (tagName.equals(m_ctx.getText(R.string.Property_key).toString())){
						//////////////////////////////////
						// keyの処理
						//////////////////////////////////
					//	bArray = false;
						//次の要素へ進む
						parser.next();

						//要素がTEXTだったら内容を取り出す
						if(parser.getEventType() == XmlPullParser.TEXT){
							keyName = parser.getText();		// key Nameにセット
						}

						// keyが[MailAddress],または[Description]だったらフラグをONにする。
					//	if(keyName.equalsIgnoreCase(StringList.m_str_subject)) {
					//		bSubject = true;
					//	} else bSubject = false;


					} else if(tagName.equals(m_ctx.getText(R.string.Property_dict).toString())){
						//////////////////////////////////
						// dictの処理
						//////////////////////////////////
					//	bArray = false;

						//m_xmlDictionary.setDict(keyName);
					} else if (tagName.equals(m_ctx.getText(R.string.Property_array).toString())) {
						//////////////////////////////////
						// arrayの処理
						//////////////////////////////////
					//	bArray = true;	// フラグを立てるだけ
						//m_xmlDictionary.setArray(keyName);
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

						// キーワードにセットしていく
						if(keyName.equalsIgnoreCase(StringList.m_str_scep_url)) {
							m_xmlKeyword.SetSituationURL(strdata);
							inf.SetSituationURL(strdata);
							LogCtrl.getInstance().info("XMLParser: Profile contained SCEP configuration");
							LogCtrl.getInstance().debug("XMLParser: SCEP URL=" + strdata);
						} else if(keyName.equalsIgnoreCase(StringList.m_str_subject)) {
							m_StrListScepSubject.add(strdata);
						} else {
							m_xmlDictionary.setParam(keyName, typeName, strdata);
						}
					} // else

				}

				//break;

			}

		} catch (XmlPullParserException e) {
			LogCtrl.getInstance().error("EnrollActivity::XmlPullParserException: " + e.toString());
			return false;
		} catch (IOException e) {
			LogCtrl.getInstance().error("EnrollActivity::IOException: " + e.toString());
			return false;
		}


		return true;
	}

	/////////////////////////////
	// Enroll完了後、制御をparseする
	// (dictやarrayは無視する？
	/////////////////////////////
	public boolean TakeApartControll(/*InformCtrl inf*/) {

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

				if(parser.getDepth() < DepthOfDict + 1) {
					continue;
				}

				switch(eventType){
				//TAGの始まり
				case XmlPullParser.START_TAG:
					//TAGの名前を取得する
					tagName = parser.getName();

					//
					if (tagName.equals(m_ctx.getText(R.string.Property_key).toString())){
						//////////////////////////////////
						// keyの処理
						//////////////////////////////////
						//次の要素へ進む
						parser.next();

						//要素がTEXTだったら内容を取り出す
						if(parser.getEventType() == XmlPullParser.TEXT){
							keyName = parser.getText();		// key Nameにセット
						}
					} else if(tagName.equals(m_ctx.getText(R.string.Property_dict).toString())){
						//////////////////////////////////
						// dictの処理
						//////////////////////////////////

						//m_xmlDictionary.setDict(keyName);
					} else if (tagName.equals(m_ctx.getText(R.string.Property_array).toString())) {
						//////////////////////////////////
						// arrayの処理
						//////////////////////////////////
						//bArray = true;	// フラグを立てるだけ
						//m_xmlDictionary.setArray(keyName);
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

						m_xmlDictionary.setParam(keyName, typeName, strdata);
					} // else

				}

				//break;

			}

		} catch (XmlPullParserException e) {
			LogCtrl.getInstance().error("EnrollActivity::XmlPullParserException: " + e.toString());
			return false;
		} catch (IOException e) {
			LogCtrl.getInstance().error("EnrollActivity::IOException: " + e.toString());
			return false;
		}
		return true;
	}

	/////////////////////////////
	// EPS-ap Profile解析
	// EPS-ap 1.2
	/////////////////////////////
	public boolean TakeApartProfile(/*InformCtrl inf*/) {

		//XMLパーサーを生成する
		XmlPullParserFactory factory;
		try {
			XmlDictionary xmldict = new XmlDictionary(DepthOfDict + 1, "", m_xmldata_string);

			factory = XmlPullParserFactory.newInstance();
			XmlPullParser parser = factory.newPullParser();

			//XMLパーサに解析したい内容を設定する
			parser.setInput(new StringReader(m_xmldata_string));

			String keyName = "";	// 初期化
			String typeName;
			String strdata;

			int dict_count = 0;	// 初期値0
			//XML文章の終わりまでループして解析する
			for(int eventType = parser.getEventType(); eventType !=
				XmlPullParser.END_DOCUMENT; eventType = parser.next()){
				String tagName;
				String tagText;

				if(parser.getDepth() < DepthOfDict + 1) {
					continue;
				}

				switch(eventType){
				//TAGの始まり
				case XmlPullParser.START_TAG:
					//TAGの名前を取得する
					tagName = parser.getName();

					//
					if (tagName.equals(m_ctx.getText(R.string.Property_key).toString())){
						//////////////////////////////////
						// keyの処理
						//////////////////////////////////
						//次の要素へ進む
						parser.next();

						//要素がTEXTだったら内容を取り出す
						if(parser.getEventType() == XmlPullParser.TEXT){
							keyName = parser.getText();		// key Nameにセット
						}
					} else if(tagName.equals(m_ctx.getText(R.string.Property_dict).toString())){
						//////////////////////////////////
						// dictの処理
						//////////////////////////////////
						dict_count++;

						//m_xmlDictionary.setDict(keyName);
					} else if (tagName.equals(m_ctx.getText(R.string.Property_array).toString())) {
						//////////////////////////////////
						// arrayの処理
						//////////////////////////////////
						//bArray = true;	// フラグを立てるだけ
						//m_xmlDictionary.setArray(keyName);
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

						if(keyName.equals(StringList.m_str_payloadtype)) {
							// keyがPayloadTypeのとき
							xmldict.SetPayloadType(strdata);
						} else {
							xmldict.setParam(keyName, typeName, strdata);
						}
					}
					break;
				case XmlPullParser.END_TAG:
					//TAGの名前を取得する
					tagName = parser.getName();

					if(tagName.equals(m_ctx.getText(R.string.Property_dict).toString())) {
						dict_count--;
						if(dict_count == 0) {
							SetPayloadDictionary(xmldict);
							xmldict = new XmlDictionary(DepthOfDict + 1, "", m_xmldata_string);
						}
					}

					break;
				}
			}
		} catch (XmlPullParserException e) {
			LogCtrl.getInstance().error("EnrollActivity::XmlPullParserException: " + e.toString());
			return false;
		} catch (IOException e) {
			LogCtrl.getInstance().error("EnrollActivity::IOException: " + e.toString());
			return false;
		}

		return true;
	}

	public boolean TakeApartProfileList() {

		//XMLパーサーを生成する
		XmlPullParserFactory factory;
		try {
			XmlProfilePiece xmlpiece = new XmlProfilePiece();

			factory = XmlPullParserFactory.newInstance();
			XmlPullParser parser = factory.newPullParser();

			//XMLパーサに解析したい内容を設定する
			parser.setInput(new StringReader(m_xmldata_string));

			String keyName = "";	// 初期化
			String typeName;
			String strdata;

			int dict_count = 0;	// 初期値0
			//XML文章の終わりまでループして解析する
			for(int eventType = parser.getEventType(); eventType !=
				XmlPullParser.END_DOCUMENT; eventType = parser.next()){
				String tagName;
				String tagText;

				if(parser.getDepth() < DepthOfDict + 1) {
					continue;
				}

				switch(eventType){
				//TAGの始まり
				case XmlPullParser.START_TAG:
					//TAGの名前を取得する
					tagName = parser.getName();

					//
					if (tagName.equals(m_ctx.getText(R.string.Property_key).toString())){
						//////////////////////////////////
						// keyの処理
						//////////////////////////////////
						//次の要素へ進む
						parser.next();

						//要素がTEXTだったら内容を取り出す
						if(parser.getEventType() == XmlPullParser.TEXT){
							keyName = parser.getText();		// key Nameにセット
						}
					} else if(tagName.equals(m_ctx.getText(R.string.Property_dict).toString())){
						//////////////////////////////////
						// dictの処理
						//////////////////////////////////
						dict_count++;

						//m_xmlDictionary.setDict(keyName);
					} else if (tagName.equals(m_ctx.getText(R.string.Property_array).toString())) {
						//////////////////////////////////
						// arrayの処理
						//////////////////////////////////
						//bArray = true;	// フラグを立てるだけ
						//m_xmlDictionary.setArray(keyName);
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

						if(keyName.equals(StringList.m_str_profileid)) {
							// keyがProfileIDのとき
							xmlpiece.SetID(strdata);
						} else if (keyName.equals(StringList.m_str_payloaddisplayname)){
							// keyがPayloadDisplayNameのとき
							xmlpiece.SetProfileName(strdata);
							if (!ValidateParams.nullOrEmpty(strdata)) {
								InputPortPageFragment.payloadDisplayName = strdata;
								LogCtrl.getInstance().info("XML Parser: CA Certificate Profile contained a payload display name");
								LogCtrl.getInstance().debug("XML Parser: Payload Display Name=" + strdata);
							}

						} else {
							m_xmlDictionary.setParam(keyName, typeName, strdata);
						}
					}
					break;
				case XmlPullParser.END_TAG:
					//TAGの名前を取得する
					tagName = parser.getName();

					if(tagName.equals(m_ctx.getText(R.string.Property_dict).toString())) {
						dict_count--;
						if(dict_count == 0) {
							if(xmlpiece.GetId() != null) m_xmlProfile.add(xmlpiece);	// 連絡先が追加されたことによる、条件追加
							xmlpiece = new XmlProfilePiece();
						}
					}

					break;
				}
			}
			List<XmlStringData> listPayloadContent = GetDictionary().GetArrayString();
			String cacert = listPayloadContent.get(listPayloadContent.size() - 1).GetData();
			cacert = cacert.replace("\n", "");
			cacert = cacert.replace(" ", "");
			cacert = cacert.replaceAll("\\p{C}", ""); // CONTROL CODE
			cacert = cacert.replaceAll("�", ""); // � = REPLACEMENT CHARACTOR
			listPayloadContent.get(listPayloadContent.size() - 1).SetData(cacert);
		} catch (XmlPullParserException e) {
			LogCtrl.getInstance().error("EnrollActivity::XmlPullParserException: " + e.toString());
			return false;
		} catch (IOException e) {
			LogCtrl.getInstance().error("EnrollActivity::IOException: " + e.toString());
			return false;
		}

		return true;
	}

	public boolean TakeApartMdmCommand(InformCtrl inf) {

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
			boolean bArray = false;
			boolean bMailAdd = false;
			boolean bDescription = false;

			//XML文章の終わりまでループして解析する
			for(int eventType = parser.getEventType(); eventType !=
				XmlPullParser.END_DOCUMENT; eventType = parser.next()){
				String tagName;
				String tagText;

				if(parser.getDepth() < DepthOfDict + 1) {
					continue;
				}

				switch(eventType){
				//TAGの始まり
				case XmlPullParser.START_TAG:
					//TAGの名前を取得する
					tagName = parser.getName();

					//
					if (tagName.equals(m_ctx.getText(R.string.Property_key).toString())){
						//////////////////////////////////
						// keyの処理
						//////////////////////////////////
						bArray = false;
						//次の要素へ進む
						parser.next();

						//要素がTEXTだったら内容を取り出す
						if(parser.getEventType() == XmlPullParser.TEXT){
							keyName = parser.getText();		// key Nameにセット
						}

						// フラグMailAddress, DescriptionがONだったらkeyをそれぞれに変更
						if(bMailAdd == true) {
							keyName = StringList.m_str_mailaddress;
						} else if(bDescription == true) {
							keyName = StringList.m_str_description;
						}

						// keyが[MailAddress],または[Description]だったらフラグをONにする。
						if(keyName.equalsIgnoreCase(StringList.m_str_mailaddress)) {
							bMailAdd = true;
						} else if (keyName.equalsIgnoreCase(StringList.m_str_description)) {
							bDescription = true;
						}


					} else if(tagName.equals(m_ctx.getText(R.string.Property_dict).toString())){
						//////////////////////////////////
						// dictの処理
						//////////////////////////////////
						bArray = false;

						//m_xmlDictionary.setDict(keyName);
					} else if (tagName.equals(m_ctx.getText(R.string.Property_array).toString())) {
						//////////////////////////////////
						// arrayの処理
						//////////////////////////////////
						bArray = true;	// フラグを立てるだけ
						//m_xmlDictionary.setArray(keyName);
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

						// キーワードにセットしていく
						if(keyName.equalsIgnoreCase(StringList.m_str_cmduuid)) {	// 一応 大文字・小文字の区別を行わない
							m_xmlKeyword.SetCmdUUID(strdata);
						} else if(keyName.equalsIgnoreCase(StringList.m_str_RequestType)) {
							m_xmlKeyword.SetReqtype(strdata);
						//	inf.SetSituationURL(strdata);
						} else if(bArray == true) {
							m_xmlKeyword.SetArrayString(strdata);
						} else {
							m_xmlDictionary.setParam(keyName, typeName, strdata);
						}
					} // else

				}

				//break;

			}

		} catch (XmlPullParserException e) {
			LogCtrl.getInstance().error("EnrollActivity::XmlPullParserException: " + e.toString());
			return false;
		} catch (IOException e) {
			LogCtrl.getInstance().error("EnrollActivity::IOException: " + e.toString());
			return false;
		}


		return true;
	}

	private void SetPayloadDictionary(XmlDictionary xmldict) {
		if(xmldict.GetPaylodaType().equals(StringList.m_str_scep_profile)) {
			m_xmlScepDictionary = xmldict;
		} else if(xmldict.GetPaylodaType().equals(StringList.m_str_wifi_profile)) {
			m_xmlwifiDictList.add(xmldict);
		} else if(xmldict.GetPaylodaType().equals(StringList.m_str_pass_profile)) {
			m_xmlPassDictionary = xmldict;
		} else if(xmldict.GetPaylodaType().equals(StringList.m_str_appli_profile)) {
			m_xmlAppDictionary = xmldict;
		} else if(xmldict.GetPaylodaType().equals(StringList.m_str_webclip_profile)) {
			m_xmlShortcutDictList.add(xmldict);
		} else if(xmldict.GetPaylodaType().equals(StringList.m_str_removalpwd_profile)) {
			m_xmlRmvPwdDictionary = xmldict;
		} else if(xmldict.GetPaylodaType().equals(StringList.m_str_mdm_profile)) {
			m_xmlMdmDictionary = xmldict;
		} else {
			m_xmlDictionary = xmldict;
		}

	}

	// Enroll要求メッセージ送信後の要求を構築する.
	// Device登録応答...
	public String DeviceInfoText(String apid_position){
		String retmsg = "";

		XmlSerializer serializer = Xml.newSerializer();
	    StringWriter writer = new StringWriter();
	    try {
	        serializer.setOutput(writer);	// XmlSerializerとStringWriterの関連付け..
	        serializer.startDocument("UTF-8", true);
	        serializer.startTag("", "plist");
	        serializer.attribute("", "version", "1.0");
	        serializer.startTag("", m_ctx.getText(R.string.Property_dict).toString());

	        // arrayの項目を取得
	        List<String> strArraylist = m_xmlKeyword.GetArrayString();
	        for(int i = 0; strArraylist.size() > i; i++) {
	        	String parameter = strArraylist.get(i);

	        	// Serverから送信されたarrayの項目を、返信のkeyに設定する
	        	SetParameter4Output(serializer, parameter, RtnIdEtc(parameter, apid_position));
	        }
	        /*for (Message msg: messages){
	            serializer.startTag("", "message");
	            serializer.attribute("", "date", msg.getDate());
	            serializer.startTag("", "title");
	            serializer.text(msg.getTitle());
	            serializer.endTag("", "title");
	            serializer.startTag("", "url");
	            serializer.text(msg.getLink().toExternalForm());
	            serializer.endTag("", "url");
	            serializer.startTag("", "body");
	            serializer.text(msg.getDescription());
	            serializer.endTag("", "body");
	            serializer.endTag("", "message");
	        }*/
	        // CHALLENGEを設定する
	        SetParameter4Output(serializer, "CHALLENGE", m_xmlKeyword.GetChallenge());

	        serializer.endTag("", m_ctx.getText(R.string.Property_dict).toString());
	        serializer.endTag("", "plist");
	        serializer.endDocument();
	        return writer.toString();
	    } catch (IOException e){

		}

		return retmsg;
	}

	// Device情報xml構築
	// DeviceInfoTextとは違い、Device情報要求を受けずにDevice情報を構築する
	// 要求がないため、CHALLENGEは設定しない
/*	public String PreDeviceInfoText() {
		String retmsg = "";

		XmlSerializer serializer = Xml.newSerializer();
	    StringWriter writer = new StringWriter();
	    try {
	        serializer.setOutput(writer);	// XmlSerializerとStringWriterの関連付け..
	        serializer.startDocument("UTF-8", true);
	        serializer.startTag("", "plist");
	        serializer.attribute("", "version", "1.0");
	        serializer.startTag("", m_ctx.getText(R.string.Property_dict).toString());

	        //<==== Device Info


	        // Device Info ====>

	        serializer.endTag("", m_ctx.getText(R.string.Property_dict).toString());
	        serializer.endTag("", "plist");
	        serializer.endDocument();
	        return writer.toString();
	    } catch (IOException e){

		}

		return retmsg;
	}*/

	private String RtnIdEtc(String word, String apid_position) {
		String rtnstr = "";
		try {
			// Wi-Fi
			WifiManager wifiManager = (WifiManager) m_ctx.getSystemService(Context.WIFI_SERVICE);
	        WifiInfo wifiInfo = wifiManager.getConnectionInfo();

	        // terephone情報
	        TelephonyManager telManager = (TelephonyManager)m_ctx.getSystemService(Context.TELEPHONY_SERVICE);

			if(word.equals("UDID")) {
				// UDIDを取得
				rtnstr = apid_position;
			//	if(apid_position == 0)	// 0:Wifi, 1:VPN&Application
			//		rtnstr = GetUDID(m_ctx);
			//	else rtnstr = GetVpnApid(m_ctx);
			} else if(word.equals("MAC_ADDRESS_EN0")) {
				rtnstr = GetMacAddress();//wifiInfo.getMacAddress();
			} else if(word.equals("IMEI")) {
				rtnstr = telManager.getDeviceId();
				if(rtnstr == null) {
					rtnstr = "0000000000000000";
				}
			} else if(word.equals("ICCID")) {
				rtnstr = telManager.getSimSerialNumber();
			} else if(word.equals("VERSION")) {
				//int sdkInt = Integer.parseInt(Build.VERSION.SDK);
				rtnstr = Build.VERSION.RELEASE;
			} else if(word.equals("PRODUCT")) {
				rtnstr = "Android";
			}
		} catch(Exception e) {
			e.printStackTrace();
			LogCtrl.getInstance().error("EnrollActivity::IOException: " + e.toString());
			rtnstr = "";
		}

		return rtnstr;
	}

	// Macアドレスから疑似UDIDを作成
	private static String SetMacToUdid(String strmac) {
		String strmac_parts = "";
		for(int n = 0; strmac.length() > (3 * n); n++) {
			strmac_parts += strmac.substring(3 * n, 3 * n + 2);
		}
		strmac_parts += strmac_parts;
		strmac_parts += strmac_parts;
		strmac_parts += strmac_parts;
		strmac_parts = strmac_parts.substring(0, 40);
		return strmac_parts;
	}

	// #25238
	// AndroidIDからAPIDの基礎文字列を生成
	private static String GetAndroididToApid(Context ctx) {
		 String str_id = Settings.Secure.getString(ctx.getContentResolver(), Settings.Secure.ANDROID_ID);

		 while(str_id.length() < 40) {
			 str_id += str_id;
		 }
		 str_id = str_id.substring(0, 40);

		 return str_id;
	}

	// #25238
	// AndroidIDから文字列を反転させsha1ハッシュを生成
	private static String GetAndroididToApidSHA1(Context ctx) {
		String str_id = GetAndroididToApid(ctx);

		StringBuffer sb = new StringBuffer(str_id);
		String dst = sb.reverse().toString();

		dst = encodeSHA1(dst);

		return dst;
	}

	// VPNとアプリのAPID
	// 前20文字:通常のAPID, 後20文字:アカウントから作成したAPID
	public static String GetVpnApid(Context ctx) {
		String rtnstr = "";

		String strapid = GetUDID(ctx);
		strapid = strapid.substring(0, 20);
		String straccount = GetAndroididToApidSHA1(ctx);//GetAccountToApid(ctx);
		straccount = straccount.substring(0, 20);

		rtnstr = strapid + straccount;

		return rtnstr;
	}

	// 通常(Wi-Fi)のAPID
	public static String GetUDID(Context ctx) {
		String rtnstr = "";
		WifiManager wifiManager = (WifiManager) ctx.getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
		TelephonyManager telManager = (TelephonyManager)ctx.getSystemService(Context.TELEPHONY_SERVICE);

		// UDIDを取得
		try {
			rtnstr = telManager.getDeviceId();
		} catch(Exception e) {
			rtnstr = null;
		}
		if(rtnstr == null) {
			String macaddress = GetMacAddress();
			if(android.os.Build.VERSION.SDK_INT < 23)	// #25238 Android6.0未満はMacアドレスから生成、それ以上はAndroidID～
				rtnstr = SetMacToUdid(/*wifiInfo.getMacAddress()*/macaddress);
			else rtnstr = GetAndroididToApid(ctx);
		} else {
			rtnstr += rtnstr;
			rtnstr += rtnstr;
			rtnstr = rtnstr.substring(0, 40);
		}
		if (rtnstr.length() > 0) {
        	rtnstr = encodeSHA1(rtnstr);	// Sha-1エンコード
		}

		return rtnstr;
	}

	// #26914
	// http://robinhenniges.com/en/android6-get-mac-address-programmatically
	public static String GetMacAddress() {

		String retstr = "";
		String strtmp = "";
		try {
            List<NetworkInterface> all = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface nif : all) {
                if (!nif.getName().equalsIgnoreCase("wlan0")) continue;

                byte[] macBytes = nif.getHardwareAddress();
                if (macBytes == null) {
                    return "";
                }

                StringBuilder res1 = new StringBuilder();
                for (byte b : macBytes) {
                	strtmp = Integer.toHexString(b & 0xFF) + ":";
                	while(strtmp.length() < 3) {
                		// 例: 0b:の場合、b:となってしまうので、補足する	#27128
                		strtmp = "0" + strtmp;
                	}
                    res1.append(/*Integer.toHexString(b & 0xFF) + ":"*/strtmp);
                }

                if (res1.length() > 0) {
                    res1.deleteCharAt(res1.length() - 1);
                }
                retstr = res1.toString();
            }
        } catch (Exception ex) {
			LogCtrl.getInstance().error("XMLPullParserAided::GetMacAddress:IOException: " + ex.toString());
        }
		return retstr;
	}

	// Sha-1エンコード
	// http://alumican.ddo.jp/common/index.php?%A5%B3%A1%BC%A5%C7%A5%A3%A5%F3%A5%B0%2FAndroid%2Fsha1
	public static String encodeSHA1(String s) /*throws NoSuchAlgorithmException*/ {
	   	MessageDigest md;
	   	String result = "";
	   	try {
			md = MessageDigest.getInstance("SHA1");
			md.update(s.getBytes(),0,s.getBytes().length);
		   	byte[] digest  = md.digest();
		   	for (int i=0; digest.length>i; i++) {
		   		result += String.format("%02x",digest[i]);
		   	}

		} catch (NoSuchAlgorithmException e) {
			LogCtrl.getInstance().error("XMLPullParserAided::encodeSHA1:NoSuchAlgorithmException: " + e.toString());
		}

	   	return result;
   }

	static public void SetParameter4Output(XmlSerializer serializer, String keyname, String parameter) {
		try {
			serializer.startTag("", "key");
			serializer.text(keyname);
			serializer.endTag("", "key");
			serializer.startTag("", "string");
			if(parameter != null) serializer.text(parameter);
			serializer.endTag("", "string");
		} catch (IOException e){
			LogCtrl.getInstance().error("XMLPullParserAided::SetParameter4Output:IOException: " + e.toString());
		}

	}

	static public void SetIntParameter4Output(XmlSerializer serializer, String keyname, String parameter) {
		try {
			serializer.startTag("", "key");
			serializer.text(keyname);
			serializer.endTag("", "key");
			serializer.startTag("", "integer");
			serializer.text(parameter);
			serializer.endTag("", "integer");
		} catch (IOException e){
			Log.e("SetParameter::IOException ", e.toString());
		}

	}

	static public void SetDataParameter4Output(XmlSerializer serializer, String keyname, String parameter) {
		try {
			serializer.startTag("", "key");
			serializer.text(keyname);
			serializer.endTag("", "key");
			serializer.startTag("", "data");
			serializer.text(parameter);
			serializer.endTag("", "data");
		} catch (IOException e){
			LogCtrl.getInstance().error("XMLPullParserAided::SetDataParameter4Output:IOException: " + e.toString());
		}

	}

	static public void SetBoolParameter4Output(XmlSerializer serializer, String keyname, boolean flg) {
		try {
        	serializer.startTag("", "key");
	        serializer.text(keyname);
	        serializer.endTag("", "key");
			if(flg == /*true*/false) {
		        serializer.startTag("", StringList.m_strfalse);
		        serializer.endTag("", StringList.m_strfalse);
	        } else {
	        	serializer.startTag("", StringList.m_strtrue);
		        serializer.endTag("", StringList.m_strtrue);
	        }
		} catch (IOException e){
			LogCtrl.getInstance().error("XMLPullParserAided::SetBoolParameter4Output:IOException: " + e.toString());
		}

	}

	public XmlDictionary GetDictionary() { return this.m_xmlDictionary;}
	public XmlDictionary GetScepDictionary() { return this.m_xmlScepDictionary;}
	public XmlDictionary GetPassDictionary() { return this.m_xmlPassDictionary;}
	public XmlDictionary GetAppDictionary() { return this.m_xmlAppDictionary;}
	public XmlDictionary GetMdmDictionary() { return this.m_xmlMdmDictionary;}
	public List<XmlProfilePiece> GetProfilePieceList() { return this.m_xmlProfile;}
	public List<XmlApplicationPiece> GetApplicationPieceList() { return this.m_xmlApppiecceList;}	//v1.2.2
	public List<XmlDictionary> GetWifiDictList() { return this.m_xmlwifiDictList;}
	public List<XmlDictionary> GetWebClipDictList() { return this.m_xmlShortcutDictList;}
	public XmlDictionary GetRmvPwdDictionary() { return this.m_xmlRmvPwdDictionary;}
	public List<String> GetSubjectList() { return this.m_StrListScepSubject;}
	public XmlKeyWord GetXmlKeyWord() { return this.m_xmlKeyword;}

}