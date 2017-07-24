package jp.co.soliton.keymanager.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import jp.co.soliton.keymanager.*;
import jp.co.soliton.keymanager.customview.AutoResizeTextView;
import jp.co.soliton.keymanager.customview.DialogApplyMessage;
import jp.co.soliton.keymanager.customview.DialogApplyProgressBar;
import jp.co.soliton.keymanager.dbalias.ElementApply;
import jp.co.soliton.keymanager.dbalias.ElementApplyManager;
import jp.co.soliton.keymanager.fragment.InputBasePageFragment;
import jp.co.soliton.keymanager.xmlparser.XmlDictionary;
import jp.co.soliton.keymanager.xmlparser.XmlPullParserAided;
import jp.co.soliton.keymanager.xmlparser.XmlStringData;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;

import static jp.co.soliton.keymanager.common.ErrorNetwork.*;

/**
 * Created by luongdolong on 2/7/2017.
 * Acivity for screen confirm apply
 */

public class ConfirmApplyActivity extends Activity {

    private Button btnBackInput;
    private Button btnApply;
    private TextView txtConfirmHostname;
    private TextView txtConfirmPortnumber;
    private TextView txtConfirmUserId;
    private TextView txtConfirmTargetPlace;
    private TextView txtConfirmEmail;
    private TextView txtConfirmReason;
    protected DialogApplyProgressBar progressDialog;
    private ElementApplyManager elementMgr;

    private InputApplyInfo inputApplyInfo;
    private InformCtrl m_InformCtrl;
    private HttpConnectionCtrl conn;
    private XmlPullParserAided m_p_aided;
    private int m_nErroType;
    private int errorCount;
    private boolean reTry;
    private HashMap<String, Boolean> mapKey = new HashMap<>();
    private AutoResizeTextView titleEmail;
    private String update_apply;

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
        titleEmail = (AutoResizeTextView) findViewById(R.id.titleEmail);
        if (ValidateParams.isJPLanguage()) {
            titleEmail.setMaxLines(1);
        } else {
            titleEmail.setMaxLines(3);
        }

        int sdk_int_version = Build.VERSION.SDK_INT;
        if (sdk_int_version < Build.VERSION_CODES.JELLY_BEAN_MR2){
            View hrStore = findViewById(R.id.hrStore);
            LinearLayout titleStore = (LinearLayout) findViewById(R.id.titleStore);
            LinearLayout valueStore = (LinearLayout) findViewById(R.id.valueStore);
            hrStore.setVisibility(View.GONE);
            titleStore.setVisibility(View.GONE);
            valueStore.setVisibility(View.GONE);
            LinearLayout.LayoutParams llp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT
                    , LinearLayout.LayoutParams.WRAP_CONTENT);
            llp.setMargins(0, 0, 0, 20);

            txtConfirmUserId.setLayoutParams(llp);
        }

        inputApplyInfo = InputApplyInfo.getPref(this);
        Intent intent = getIntent();
        m_InformCtrl = (InformCtrl)intent.getSerializableExtra(StringList.m_str_InformCtrl);
        update_apply = intent.getStringExtra(StringList.UPDATE_APPLY);
        conn = new HttpConnectionCtrl(this);
        if (progressDialog == null) {
            progressDialog = new DialogApplyProgressBar(this);
        }
        if (elementMgr == null) {
            elementMgr = new ElementApplyManager(this);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        setupControl();
    }

    @Override
    public void onBackPressed() {
        Intent returnIntent = new Intent();
        setResult(Activity.RESULT_OK, returnIntent);
        finish();
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
        String storeString;
        String rtnserial;
        if (InputBasePageFragment.TARGET_WiFi.equals(inputApplyInfo.getPlace())) {
            storeString = "Wi-Fi";
            rtnserial = XmlPullParserAided.GetUDID(this);
        } else {
            storeString = "VPN and apps";
            rtnserial = XmlPullParserAided.GetVpnApid(this);
        }
        LogCtrl.getInstance().info("Apply: Store=" + storeString + " (" + rtnserial + ")");

        // ログインメッセージ
        String message = "";
        String message_ma = "&" + "MailAddress=";	// #26556
        String message_dc = "&" + "Description=";	// #26556

        if (!nullOrEmpty(inputApplyInfo.getEmail())) {
            try {
                message_ma = message_ma + URLEncoder.encode(inputApplyInfo.getEmail(), "UTF-8");
            } catch (UnsupportedEncodingException ex) {
	            LogCtrl.getInstance().error("CompleteApplyActivity:makeParameterApply:Email:: " + "Message=" + ex
			            .getMessage());
            }
        }
        if (!nullOrEmpty(inputApplyInfo.getReason())) {
            try {
                message_dc = message_dc + URLEncoder.encode(inputApplyInfo.getReason(), "UTF-8");
            } catch (UnsupportedEncodingException ex) {
	            LogCtrl.getInstance().error("CompleteApplyActivity:makeParameterApply:Reason:: " + "Message=" + ex
			            .getMessage());
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
	    if (!ValidateParams.nullOrEmpty(update_apply)) {
            elementMgr.updateStatus(ElementApply.STATUS_APPLY_CLOSED, update_apply);
        }

        String rtnserial;
        if (InputBasePageFragment.TARGET_WiFi.equals(inputApplyInfo.getPlace())) {
            rtnserial = "WIFI" + XmlPullParserAided.GetUDID(this);
        } else {
            rtnserial = "APP" + XmlPullParserAided.GetVpnApid(this);
        }
        ElementApply elementApply = new ElementApply();
        elementApply.setHost(inputApplyInfo.getHost());
        elementApply.setPort(inputApplyInfo.getPort());
        elementApply.setPortSSL(inputApplyInfo.getSecurePort());
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
        elementMgr.saveElementApply(elementApply);
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
                LogCtrl.getInstance().error("Apply: Receive NG");
                m_nErroType = ERR_LOGIN_FAIL;
                return false;
            }
            if (m_InformCtrl.GetRtn().startsWith("EPS-ap Service is stopped.")) {
                LogCtrl.getInstance().error("Apply: EPS-ap Service is stopped");
                m_nErroType = ERR_ESP_AP_STOP;
                return false;
            }
            if (m_InformCtrl.GetRtn().startsWith("No session")) {
                LogCtrl.getInstance().error("Apply: No session");
                m_nErroType = ERR_SESSION_TIMEOUT;
                return false;
            }
            if (m_InformCtrl.GetRtn().startsWith(getText(R.string.Forbidden).toString())) {
                LogCtrl.getInstance().error("Apply: Permission error (Forbidden)");
                m_nErroType = ERR_FORBIDDEN;
                return false;
            }
            if (m_InformCtrl.GetRtn().startsWith(getText(R.string.Unauthorized).toString())) {
                LogCtrl.getInstance().error("Apply: Permission error (Unauthorized)");
                m_nErroType = ERR_UNAUTHORIZED;
                return false;
            }
            if (m_InformCtrl.GetRtn().length() > 4 && m_InformCtrl.GetRtn().startsWith(getString(R.string.ERR).toString())) {
                LogCtrl.getInstance().error("Apply: Receive ERR");
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
