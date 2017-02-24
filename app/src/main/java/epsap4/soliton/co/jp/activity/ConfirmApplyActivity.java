package epsap4.soliton.co.jp.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;

import epsap4.soliton.co.jp.HttpConnectionCtrl;
import epsap4.soliton.co.jp.InformCtrl;
import epsap4.soliton.co.jp.InputApplyInfo;
import epsap4.soliton.co.jp.R;
import epsap4.soliton.co.jp.StringList;
import epsap4.soliton.co.jp.customview.DialogApplyMessage;
import epsap4.soliton.co.jp.customview.DialogApplyProgressBar;
import epsap4.soliton.co.jp.dbalias.DatabaseHandler;
import epsap4.soliton.co.jp.dbalias.ElementApply;
import epsap4.soliton.co.jp.fragment.InputBasePageFragment;
import epsap4.soliton.co.jp.xmlparser.XmlDictionary;
import epsap4.soliton.co.jp.xmlparser.XmlPullParserAided;
import epsap4.soliton.co.jp.xmlparser.XmlStringData;

/**
 * Created by luongdolong on 2/7/2017.
 * Acivity for screen confirm apply
 */

public class ConfirmApplyActivity extends Activity {

    private final static int ERR_FORBIDDEN = 20;
    private final static int ERR_UNAUTHORIZED = 21;
    private final static int SUCCESSFUL = 22;
    private final static int ERR_NETWORK = 23;
    private final static int ERR_COLON = 24;
    private final static int ERR_ESPAP_NOT_CONNECT = 25;
    private final static int RET_ESP_AP_OK = 26;
    private final static int ERR_LOGIN_FAIL = 27;
    private final static int ERR_ESP_AP_STOP = 28;
    private final static int ERR_SESSION_TIMEOUT = 29;

    private Button btnBackInput;
    private Button btnApply;
    private TextView txtConfirmHostname;
    private TextView txtConfirmPortnumber;
    private TextView txtConfirmUserId;
    private TextView txtConfirmTargetPlace;
    private TextView txtConfirmEmail;
    private TextView txtConfirmReason;
    protected DialogApplyProgressBar progressDialog;
    private DatabaseHandler databaseHandler;

    private InputApplyInfo inputApplyInfo;
    private InformCtrl m_InformCtrl;
    private HttpConnectionCtrl conn;
    private XmlPullParserAided m_p_aided;
    private int m_nErroType;
    private int errorCount;
    private boolean reTry;
    private HashMap<String, Boolean> mapKey = new HashMap<>();

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_apply);
        btnBackInput = (Button) findViewById(R.id.btnConfirmBack);
        btnApply = (Button) findViewById(R.id.btnConfirmApply);
        txtConfirmHostname = (TextView) findViewById(R.id.txtConfirmHostname);
        txtConfirmPortnumber = (TextView) findViewById(R.id.txtConfirmPortnumber);
        txtConfirmUserId = (TextView) findViewById(R.id.txtConfirmUserId);
        txtConfirmTargetPlace = (TextView) findViewById(R.id.txtConfirmTargetPlace);
        txtConfirmEmail = (TextView) findViewById(R.id.txtConfirmEmail);
        txtConfirmReason = (TextView) findViewById(R.id.txtConfirmReason);

        inputApplyInfo = InputApplyInfo.getPref(this);
        Intent intent = getIntent();
        m_InformCtrl = (InformCtrl)intent.getSerializableExtra(StringList.m_str_InformCtrl);
        conn = new HttpConnectionCtrl(this);
        if (progressDialog == null) {
            progressDialog = new DialogApplyProgressBar(this);
        }
        if (databaseHandler == null) {
            databaseHandler = new DatabaseHandler(this);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        setupControl();
    }

    /**
     * Set action for controls and init value for items
     */
    private void setupControl() {
        btnBackInput.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent returnIntent = new Intent();
                setResult(Activity.RESULT_OK, returnIntent);
                finish();
            }
        });
        btnApply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                processingApply();
            }
        });
        initValueControl();
    }

    /**
     * Init value for items in screen
     */
    private void initValueControl() {
        txtConfirmHostname.setText(inputApplyInfo.getHost());
        txtConfirmPortnumber.setText(inputApplyInfo.getSecurePort());
        if (InputBasePageFragment.TARGET_VPN.equals(inputApplyInfo.getPlace())) {
            txtConfirmTargetPlace.setText(getString(R.string.main_apid_vpn));
        } else {
            txtConfirmTargetPlace.setText(getString(R.string.main_apid_wifi));
        }
        txtConfirmUserId.setText(inputApplyInfo.getUserId());
        txtConfirmEmail.setText(inputApplyInfo.getEmail());
        txtConfirmReason.setText(inputApplyInfo.getReason());
    }

    /**
     * Execute action apply
     */
    private void processingApply() {
        btnApply.setEnabled(false);
        m_nErroType = SUCCESSFUL;
        errorCount = 0;
        reTry = false;
        //open thread processing apply
        makeParameterApply();
        new ProcessApplyTask().execute();
    }

    /**
     * Processing logic after apply successful
     */
    private void applyFinish() {
        this.inputApplyInfo.setPassword(null);
        this.inputApplyInfo.savePref(this);
        Intent intent = new Intent(ConfirmApplyActivity.this, CompleteApplyActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivityForResult(intent, ViewPagerInputActivity.REQUEST_CODE_APPLY_COMPLETE);
        finish();
    }

    /**
     * Processing result after request to server
     * @param result
     */
    private void endConnection(boolean result) {
        progressDialog.dismiss();
        //request with result error
        if (!result) {
            if (reTry) {
                new ProcessApplyTask().execute();
                return;
            }
            btnApply.setEnabled(true);
            //show message error
            if (m_nErroType == ERR_ESPAP_NOT_CONNECT) {
                showMessage(getString(R.string.connect_not_epsap));
            }
            if (m_nErroType == ERR_NETWORK) {
                showMessage(getString(R.string.connect_failed));
            }
            if (m_nErroType == ERR_ESP_AP_STOP) {
                showMessage(getString(R.string.connect_failed));
            }
            if (m_nErroType == ERR_SESSION_TIMEOUT) {
                showMessage(getString(R.string.session_timeout));
            }
            if (m_nErroType == ERR_FORBIDDEN) {
                showMessage(getString(R.string.devicecheck_error));
            }
            if (m_nErroType == ERR_UNAUTHORIZED) {
                String str_unauth = getString(R.string.Unauthorized);
                showMessage(m_InformCtrl.GetRtn().substring(str_unauth.length()));
            }
            if (m_nErroType == ERR_COLON) {
                String str_err = getString(R.string.ERR);
                showMessage(m_InformCtrl.GetRtn().substring(str_err.length()));
            }
        } else {
            if (m_nErroType == RET_ESP_AP_OK) {
                saveElementApply();
                applyFinish();
                return;
            }
            //parse result for next action
            parseResult();
        }
    }

    /**
     * Parse result from server return
     */
    private void parseResult() {
        if (mapKey.containsKey(StringList.m_str_isConnected) && !mapKey.get(StringList.m_str_isConnected)) {
            btnApply.setEnabled(true);
            showMessage(getString(R.string.login_failed));
            return;
        }
        if (mapKey.containsKey(StringList.m_str_scepprofile) && !mapKey.get(StringList.m_str_scepprofile)) {
            DialogApplyMessage dlgMessage = new DialogApplyMessage(this, getString(R.string.registration_setting_invalid));
            dlgMessage.setOnOkDismissMessageListener(new DialogApplyMessage.OnOkDismissMessageListener() {
                @Override
                public void onOkDismissMessage() {
                    applyFinish();
                }
            });
            dlgMessage.show();
            return;
        }
        if (mapKey.containsKey(StringList.m_str_issubmitted) && mapKey.get(StringList.m_str_issubmitted)) {
            saveElementApply();
            applyFinish();
            return;
        }
        if (mapKey.containsKey(StringList.m_str_isEnroll) && mapKey.get(StringList.m_str_isEnroll)) {
            saveElementApply();
            applyFinish();
            return;
        }

        applyFinish();
    }

    /**
     * Make parameter for request apply to server
     *
     * @return
     */
    private void makeParameterApply() {
        String rtnserial;
        if (InputBasePageFragment.TARGET_WiFi.equals(inputApplyInfo.getPlace())) {
            rtnserial = XmlPullParserAided.GetUDID(this);
        } else {
            rtnserial = XmlPullParserAided.GetVpnApid(this);
        }
        // ログインメッセージ
        String message = "";
        String message_ma = "&" + "MailAddress=";	// #26556
        String message_dc = "&" + "Description=";	// #26556

        if (!nullOrEmpty(inputApplyInfo.getEmail())) {
            try {
                message_ma = message_ma + URLEncoder.encode(inputApplyInfo.getEmail(), "UTF-8");
            } catch (UnsupportedEncodingException ex) {
                Log.i(StringList.m_str_SKMTag, "apply:: " + "Message=" + ex.getMessage());
            }
        }
        if (!nullOrEmpty(inputApplyInfo.getReason())) {
            try {
                message_dc = message_dc + URLEncoder.encode(inputApplyInfo.getReason(), "UTF-8");
            } catch (UnsupportedEncodingException ex) {
                Log.i(StringList.m_str_SKMTag, "apply:: " + "Message=" + ex.getMessage());
            }
        }
        message = "Action=apply" + message_ma + message_dc + "&" + StringList.m_strSerial + rtnserial;

        // 入力データを情報管理クラスへセットする
        m_InformCtrl.SetMessage(message);
    }

    /**
     * Show message error
     * @param message
     */
    private void showMessage(String message) {
        DialogApplyMessage dlgMessage = new DialogApplyMessage(this, message);
        dlgMessage.show();
    }

    /**
     * Check String is null or empty
     * @param value
     * @return
     */
    private boolean nullOrEmpty(String value) {
        if (value == null) {
            return true;
        }
        return value.trim().isEmpty();
    }

    private void saveElementApply() {
        String rtnserial;
        if (InputBasePageFragment.TARGET_WiFi.equals(inputApplyInfo.getPlace())) {
            rtnserial = "WIFI" + XmlPullParserAided.GetUDID(this);
        } else {
            rtnserial = "APP" + XmlPullParserAided.GetVpnApid(this);
        }
        ElementApply elementApply = new ElementApply();
        elementApply.setHost(inputApplyInfo.getHost());
        elementApply.setUserId(inputApplyInfo.getUserId());
        elementApply.setPassword(inputApplyInfo.getPassword());
        elementApply.setEmail(inputApplyInfo.getEmail());
        elementApply.setReason(inputApplyInfo.getReason());
        elementApply.setTarger(rtnserial);
        elementApply.setStatus(ElementApply.STATUS_APPLY_PENDING);
        if (mapKey.containsKey(StringList.m_str_scep_challenge)) {
            elementApply.setChallenge(mapKey.get(StringList.m_str_scep_challenge));
        } else {
            elementApply.setChallenge(false);
        }
        databaseHandler.addElementApply(elementApply);
    }

    /**
     * Task send request to server and receive result return
     */
    private class ProcessApplyTask extends AsyncTask<Void, Void, Boolean> {
        @Override
        protected void onPreExecute() {
            progressDialog.show();
        }
        @Override
        protected Boolean doInBackground(Void... params) {
            boolean ret;
            //Call to server
            ret = conn.RunHttpApplyCerUrlConnection(m_InformCtrl);
            //Parse result
            if (!ret) {
                if (errorCount > 10) {
                    m_nErroType = ERR_ESPAP_NOT_CONNECT;
                    reTry = false;
                } else {
                    reTry = true;
                    errorCount++;
                }
                return false;
            }
            reTry = false;
            //Check status of certificate
            if (nullOrEmpty(m_InformCtrl.GetRtn())) {
                m_nErroType = ERR_NETWORK;
                return false;
            }
            if (m_InformCtrl.GetRtn().startsWith("OK")) {
                m_nErroType = RET_ESP_AP_OK;
                return true;
            }
            if (m_InformCtrl.GetRtn().startsWith("NG")) {
                m_nErroType = ERR_LOGIN_FAIL;
                return false;
            }
            if (m_InformCtrl.GetRtn().startsWith("EPS-ap Service is stopped.")) {
                m_nErroType = ERR_ESP_AP_STOP;
                return false;
            }
            if (m_InformCtrl.GetRtn().startsWith("No session")) {
                m_nErroType = ERR_SESSION_TIMEOUT;
                return false;
            }
            if (m_InformCtrl.GetRtn().startsWith(getText(R.string.Forbidden).toString())) {
                m_nErroType = ERR_FORBIDDEN;
                return false;
            }
            if (m_InformCtrl.GetRtn().startsWith(getText(R.string.Unauthorized).toString())) {
                m_nErroType = ERR_UNAUTHORIZED;
                return false;
            }
            if (m_InformCtrl.GetRtn().length() > 4 && m_InformCtrl.GetRtn().startsWith(getString(R.string.ERR).toString())) {
                m_nErroType = ERR_COLON;
                return false;
            }
            // 取得XMLのパーサー
            m_p_aided = new XmlPullParserAided(ConfirmApplyActivity.this, m_InformCtrl.GetRtn(), 2);    // 最上位dictの階層は2になる
            ret = m_p_aided.TakeApartUserAuthenticationResponse(m_InformCtrl);
            if (ret == false) {
                reTry = false;
                m_nErroType = ERR_NETWORK;
                return false;
            }
            parseXML();
            return ret;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);
            endConnection(result);
        }

        /**
         * parse XML file from result
         */
        private void parseXML() {
            XmlDictionary xmldict = m_p_aided.GetDictionary();
            mapKey.clear();
            if(xmldict != null) {
                List<XmlStringData> str_list;
                str_list = xmldict.GetArrayString();
                for(int i = 0; str_list.size() > i; i++){
                    // config情報に従って、処理を行う.
                    XmlStringData p_data = str_list.get(i);
                    // 要素タイプ(string:1, data=2, date=3, real=4, integer=5, true=6, false=7)
                    if(StringList.m_str_isConnected.equalsIgnoreCase(p_data.GetKeyName()) ) {
                        mapKey.put(StringList.m_str_isConnected, 6 == p_data.GetType());
                    }
                    if(StringList.m_str_scepprofile.equalsIgnoreCase(p_data.GetKeyName()) ) {
                        mapKey.put(StringList.m_str_scepprofile, 6 == p_data.GetType());
                    }
                    if(StringList.m_str_issubmitted.equalsIgnoreCase(p_data.GetKeyName()) ) {
                        mapKey.put(StringList.m_str_issubmitted, 6 == p_data.GetType());
                    }
                    if(StringList.m_str_isEnroll.equalsIgnoreCase(p_data.GetKeyName()) ) {
                        mapKey.put(StringList.m_str_isEnroll, true);
                    }
                    if (StringList.m_str_scep_challenge.equalsIgnoreCase(p_data.GetKeyName())) {
                        mapKey.put(StringList.m_str_scep_challenge, 6 == p_data.GetType());
                    }
                }
            }
        }
    }
}
