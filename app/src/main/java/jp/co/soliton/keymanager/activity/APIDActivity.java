package jp.co.soliton.keymanager.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.List;

import jp.co.soliton.keymanager.LogCtrl;
import jp.co.soliton.keymanager.R;
import jp.co.soliton.keymanager.StringList;
import jp.co.soliton.keymanager.xmlparser.XmlDictionary;
import jp.co.soliton.keymanager.xmlparser.XmlPullParserAided;
import jp.co.soliton.keymanager.xmlparser.XmlStringData;

/**
 * Created by vinhlx on 2/16/2017.
 */

public class APIDActivity extends Activity implements View.OnClickListener {

    private LinearLayout layoutShareAPID;
    private TextView tvVPNID;
    private TextView tvWIFIID;
    private String m_strAPIDWifi = "";	// APID Wi-Fi #21391
    private String m_strAPIDVPN = "";	// APID VPN #21391
    private String m_strAPID = "";	// APID VPN #21391

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_apid);
        this.layoutShareAPID = (LinearLayout)findViewById(R.id.layoutShareAPID);
        layoutShareAPID.setOnClickListener(this);
        ReadAndSetLoginUserInfo();

        String strUDID = GetUDID().trim();
        String strVpnID = GetVpnApid().trim();
        this.m_strAPID = strUDID + " " + strVpnID;

        this.tvVPNID = (TextView)findViewById(R.id.tvVPNID);
        this.tvWIFIID = (TextView)findViewById(R.id.tvWIFIID);
        tvVPNID.setText(strVpnID);
        tvWIFIID.setText(strUDID);
    }

    private String GetUDID() {
        if(m_strAPIDWifi.length() > 0) return m_strAPIDWifi;
        else return XmlPullParserAided.GetUDID(this);
    }

    private String GetVpnApid() {
        if(m_strAPIDVPN.length() > 0) return m_strAPIDVPN;
        else return XmlPullParserAided.GetVpnApid(this);
    }

    private void SetParametorFromFile(XmlStringData p_data) {

        String strKeyName = p_data.GetKeyName();
        String strData = p_data.GetData();
        //
        if(strKeyName.equalsIgnoreCase(StringList.m_str_Apid_Wifi)) {
            m_strAPIDWifi = strData;
        } else if(strKeyName.equalsIgnoreCase(StringList.m_str_Apid_VPN)) {
            m_strAPIDVPN = strData;
        }
    }

    // ファイル読み込み&フラグセット
    public boolean ReadAndSetLoginUserInfo() {
        byte[] byArrData_read = null;
        int iSize;
        byte[] byArrTempData=new byte[4096];
        InputStream inputStreamObj=null;
        ByteArrayOutputStream byteArrayOutputStreamObj=null;

        boolean bRet = true;
		LogCtrl logCtrl = LogCtrl.getInstance(this);
        try {
            //Contextから入力ストリームの取得
            inputStreamObj=openFileInput(StringList.m_strLoginUserOutputFile);
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
            logCtrl.loggerDebug("ReadAndSetLoginUserInfo: "+ e.getMessage());
            bRet = false;
        } finally{
            try {
                if (inputStreamObj!=null) inputStreamObj.close();
                if (byteArrayOutputStreamObj!=null) byteArrayOutputStreamObj.close();
            } catch (Exception e2) {
                logCtrl.loggerDebug("ReadAndSetLoginUserInfo e2: " + e2.getMessage());
                bRet = false;
            }

        }

        if(bRet == false) return bRet;

        String read_string = new String(byArrData_read);
        android.util.Log.d(StringList.m_str_SKMTag, "*****Re-Read***** " + read_string);

        // 新しくXmlPullParserAidedを作成する.
        XmlPullParserAided p_aided = new XmlPullParserAided(this, read_string, 2);
        p_aided.TakeApartControll();
        XmlDictionary p_dict = p_aided.GetDictionary();

        // <key, type, data>リストを取得
        List<XmlStringData> str_list = p_dict.GetArrayString();
        for(int i = 0; str_list.size() > i; i++){
            // config情報に従って、処理を行う.
            XmlStringData p_data = str_list.get(i);
            SetParametorFromFile(p_data);
        }

        return bRet;
    }

    private void setClipboard() {
        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = android.content.ClipData.newPlainText("Text", this.m_strAPID);
        clipboard.setPrimaryClip(clip);
    }

    private void sendMail() {
        Intent email = new Intent(android.content.Intent.ACTION_SEND);
        email.setType("message/rfc822");
        email.putExtra(Intent.EXTRA_EMAIL, new String[] { "" });
        email.putExtra(Intent.EXTRA_SUBJECT, getText(R.string.apid_subject_email).toString());
        email.putExtra(Intent.EXTRA_TEXT, this.m_strAPID);
        try {
            startActivity(Intent.createChooser(email, ""));
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(this, "There are no email clients installed.", Toast.LENGTH_SHORT).show();
        }
    }

    public void btnBackClick(View v) {
        finish();
    }

    @Override
    public void onClick(View v) {
        CharSequence menus[] = new CharSequence[] {getText(R.string.copy_apid).toString(), getText(R.string.send_apid).toString()};

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getText(R.string.select_apid).toString());
        builder.setItems(menus, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(which == 0) {
                    setClipboard();
                } else {
                    sendMail();
                }
            }
        });
        builder.show();
    }
}
