package jp.co.soliton.keymanager.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.net.URLEncoder;
import java.util.List;

import jp.co.soliton.keymanager.HttpConnectionCtrl;
import jp.co.soliton.keymanager.InformCtrl;
import jp.co.soliton.keymanager.LogCtrl;
import jp.co.soliton.keymanager.R;
import jp.co.soliton.keymanager.StringList;
import jp.co.soliton.keymanager.ValidateParams;
import jp.co.soliton.keymanager.customview.DialogApplyConfirm;
import jp.co.soliton.keymanager.customview.DialogApplyMessage;
import jp.co.soliton.keymanager.customview.DialogApplyProgressBar;
import jp.co.soliton.keymanager.dbalias.ElementApply;
import jp.co.soliton.keymanager.dbalias.ElementApplyManager;
import jp.co.soliton.keymanager.fragment.InputBasePageFragment;
import jp.co.soliton.keymanager.fragment.InputUserPageFragment;
import jp.co.soliton.keymanager.xmlparser.XmlDictionary;
import jp.co.soliton.keymanager.xmlparser.XmlPullParserAided;
import jp.co.soliton.keymanager.xmlparser.XmlStringData;

/**
 * Created by luongdolong on 2/3/2017.
 *
 * Activity for menu apply screen
 */

public class InputPasswordActivity extends Activity {
    private String id;
    private String cancelApply;
    private int status;
    private ElementApplyManager elementMgr;
    private TextView txtUserId;
    private EditText txtPassword;
    private Button btnInputNext;
    private DialogApplyProgressBar progressDialog;
    private int m_nErroType;
    private InformCtrl m_InformCtrl;
    private ElementApply element;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input_password);
        id = getIntent().getStringExtra("ELEMENT_APPLY_ID");
        cancelApply = getIntent().getStringExtra("CANCEL_APPLY");
        txtUserId = (TextView) findViewById(R.id.txtUserId);
        txtPassword = (EditText) findViewById(R.id.txtPassword);
        btnInputNext = (Button) findViewById(R.id.btnInputNext);
        elementMgr = new ElementApplyManager(this);
        m_InformCtrl = new InformCtrl();
        if (progressDialog == null) {
            progressDialog = new DialogApplyProgressBar(this);
        }
    }

    public void clickBack(View v) {
        finish();
    }

    public void clickNext(View v) {
        String url = String.format("%s:%s", element.getHost(), element.getPortSSL());
        m_InformCtrl.SetURL(url);

        //make parameter
        boolean ret = makeParameterLogon();
        if (!ret) {
            showMessage(getString(R.string.connect_failed));
            return;
        }
        progressDialog.show();
        m_InformCtrl.SetCookie(null);
        //open thread logon to server
        new LogonApplyTask().execute();
    }

    /**
     * Make parameter for logon to server
     * @return
     */
    private boolean makeParameterLogon() {
        String strUserid = txtUserId.getText().toString().trim();
        String strPasswd = txtPassword.getText().toString();
        String rtnserial = element.getTarger();
        // ログインメッセージ
        // URLEncodeが必須 <http://wada811.blog.fc2.com/?tag=URL%E3%82%A8%E3%83%B3%E3%82%B3%E3%83%BC%E3%83%89>参照
        String message;
        try {
            message = "Action=logon" + "&" + StringList.m_strUserID + URLEncoder.encode(strUserid, "UTF-8") +
                    "&" + StringList.m_strPassword + URLEncoder.encode(strPasswd, "UTF-8") +
                    "&" + StringList.m_strSerial + rtnserial;
        } catch (Exception ex) {
            Log.i(StringList.m_str_SKMTag, "logon:: " + "Message=" + ex.getMessage());
            return false;
        }
        // 入力データを情報管理クラスへセットする
        m_InformCtrl.SetUserID(strUserid);
        m_InformCtrl.SetPassword(strPasswd);
        m_InformCtrl.SetMessage(message);
        return true;
    }

    /**
     * Make parameter for logon to server
     * @return
     */
    private boolean makeParameterDrop() {
        // ログインメッセージ
        // URLEncodeが必須 <http://wada811.blog.fc2.com/?tag=URL%E3%82%A8%E3%83%B3%E3%82%B3%E3%83%BC%E3%83%89>参照
        String message;
        try {
            message = "Action=drop";
        } catch (Exception ex) {
            Log.i(StringList.m_str_SKMTag, "logon:: " + "Message=" + ex.getMessage());
            return false;
        }
        // 入力データを情報管理クラスへセットする
        m_InformCtrl.SetMessage(message);
        return true;
    }

    private void setupControl() {
        if (!ValidateParams.nullOrEmpty(id)) {
            element = elementMgr.getElementApply(id);
            txtUserId.setText(element.getUserId());
        }
        setEnableNextButton();
        txtPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                setEnableNextButton();
            }
        });
        txtPassword.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                    if (!ValidateParams.nullOrEmpty(txtPassword.getText().toString())) {
                        clickNext(v);
                        return true;
                    }
                }
                return false;
            }
        });
    }

    private void setEnableNextButton() {
        if (ValidateParams.nullOrEmpty(txtPassword.getText().toString())) {
            btnInputNext.setEnabled(false);
            btnInputNext.setTextColor(getResources().getColor(R.color.text_button_inactive));
        } else {
            btnInputNext.setEnabled(true);
            btnInputNext.setTextColor(getResources().getColor(R.color.text_color_active));
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        setupControl();
    }

    /**
     * Task processing logon
     */
    private class LogonApplyTask extends AsyncTask<Void, Void, Boolean> {
        @Override
        protected Boolean doInBackground(Void... params) {
            ////////////////////////////////////////////////////////////////////////////
            // 大項目1. ログイン開始 <=========
            ////////////////////////////////////////////////////////////////////////////
            HttpConnectionCtrl conn = new HttpConnectionCtrl(getApplicationContext());
            boolean ret = conn.RunHttpApplyLoginUrlConnection(m_InformCtrl);

            if (ret == false) {
                LogCtrl.Logger(LogCtrl.m_strError, "LogonApplyTask " + "Network error", getApplicationContext());
                m_nErroType = InputBasePageFragment.ERR_NETWORK;
                return false;
            }
            // ログイン結果
            if (m_InformCtrl.GetRtn().startsWith(getText(R.string.Forbidden).toString())) {
                LogCtrl.Logger(LogCtrl.m_strError, "LogonApplyTask  " + " Forbidden.", getApplicationContext());
                m_nErroType = InputBasePageFragment.ERR_FORBIDDEN;
                return false;
            } else if (m_InformCtrl.GetRtn().startsWith(getText(R.string.Unauthorized).toString())) {
                LogCtrl.Logger(LogCtrl.m_strError, "LogonApplyTask  " + "Unauthorized.", getApplicationContext());
                m_nErroType = InputBasePageFragment.ERR_UNAUTHORIZED;
                return false;
            } else if (m_InformCtrl.GetRtn().startsWith(getText(R.string.ERR).toString())) {
                LogCtrl.Logger(LogCtrl.m_strError, "LogonApplyTask  " + "ERR:", getApplicationContext());
                m_nErroType = InputBasePageFragment.ERR_COLON;
                return false;
            } else if (m_InformCtrl.GetRtn().startsWith("NG")) {
                m_nErroType = InputBasePageFragment.ERR_LOGIN_FAIL;
                return false;
            }
            // 取得したCookieをログイン時のCookieとして保持する.
            m_InformCtrl.SetLoginCookie(m_InformCtrl.GetCookie());
            ///////////////////////////////////////////////////
            // 認証応答の解析(Enroll応答のときの対応を流用できるはず)
            ///////////////////////////////////////////////////
            // 取得XMLのパーサー
            XmlPullParserAided m_p_aided = new XmlPullParserAided(getApplicationContext(), m_InformCtrl.GetRtn(), 2);    // 最上位dictの階層は2になる

            ret = m_p_aided.TakeApartUserAuthenticationResponse(m_InformCtrl);
            if (ret == false) {
                LogCtrl.Logger(LogCtrl.m_strError, "LogonApplyTask-- " + "TakeApartDevice false", getApplicationContext());
                m_nErroType = InputBasePageFragment.ERR_NETWORK;
                return false;
            }
            status = ElementApply.STATUS_APPLY_PENDING;
            //parse xml return from server
            XmlDictionary xmldict = m_p_aided.GetDictionary();
            if(xmldict != null) {
                List<XmlStringData> str_list;
                str_list = xmldict.GetArrayString();
                for(int i = 0; str_list.size() > i; i++){
                    // config情報に従って、処理を行う.
                    XmlStringData p_data = str_list.get(i);
                    // 要素タイプ(string:1, data=2, date=3, real=4, integer=5, true=6, false=7)
                    if(StringList.m_str_issubmitted.equalsIgnoreCase(p_data.GetKeyName()) ) {
                        if (6 == p_data.GetType()) {
                            status = ElementApply.STATUS_APPLY_PENDING;
                        } else {
                            status = ElementApply.STATUS_APPLY_REJECT;
                        }
                    }
                    if (StringList.m_str_isEnroll.equalsIgnoreCase(p_data.GetKeyName())) {
                        status = ElementApply.STATUS_APPLY_APPROVED;
                    }
                }
            }
            if (status == ElementApply.STATUS_APPLY_APPROVED) {
                String sendmsg = m_p_aided.DeviceInfoText(element.getTarger());
                m_InformCtrl.SetMessage(sendmsg);
            }
            ////////////////////////////////////////////////////////////////////////////
            // 大項目1. ログイン終了 =========>
            ////////////////////////////////////////////////////////////////////////////
            m_nErroType = InputBasePageFragment.SUCCESSFUL;
            return ret;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);
            endConnection(result);
        }
    }

    /**
     * Task processing logon
     */
    private class DropApplyTask extends AsyncTask<Void, Void, Boolean> {
        @Override
        protected Boolean doInBackground(Void... params) {
            ////////////////////////////////////////////////////////////////////////////
            // 大項目1. ログイン開始 <=========
            ////////////////////////////////////////////////////////////////////////////
            HttpConnectionCtrl conn = new HttpConnectionCtrl(getApplicationContext());
            boolean ret = conn.RunHttpDropUrlConnection(m_InformCtrl);
            cancelApply = "";

            if (ret == false) {
                LogCtrl.Logger(LogCtrl.m_strError, "LogonApplyTask " + "Network error", getApplicationContext());
                m_nErroType = InputBasePageFragment.ERR_NETWORK;
                return false;
            }
            // ログイン結果
            if (m_InformCtrl.GetRtn().startsWith(getText(R.string.Forbidden).toString())) {
                LogCtrl.Logger(LogCtrl.m_strError, "LogonApplyTask  " + " Forbidden.", getApplicationContext());
                m_nErroType = InputBasePageFragment.ERR_FORBIDDEN;
                return false;
            } else if (m_InformCtrl.GetRtn().startsWith(getText(R.string.Unauthorized).toString())) {
                LogCtrl.Logger(LogCtrl.m_strError, "LogonApplyTask  " + "Unauthorized.", getApplicationContext());
                m_nErroType = InputBasePageFragment.ERR_UNAUTHORIZED;
                return false;
            } else if (m_InformCtrl.GetRtn().startsWith(getText(R.string.ERR).toString())) {
                LogCtrl.Logger(LogCtrl.m_strError, "LogonApplyTask  " + "ERR:", getApplicationContext());
                m_nErroType = InputBasePageFragment.ERR_COLON;
                return false;
            } else if (m_InformCtrl.GetRtn().startsWith("NG")) {
                m_nErroType = InputBasePageFragment.ERR_LOGIN_FAIL;
                return false;
            }
            // 取得したCookieをログイン時のCookieとして保持する.
            m_InformCtrl.SetLoginCookie(m_InformCtrl.GetCookie());
            if (m_InformCtrl.GetRtn().startsWith("OK")) {
                status = ElementApply.STATUS_APPLY_CANCEL;
            }
            m_nErroType = InputBasePageFragment.SUCCESSFUL;
            return ret;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);
            endConnection(result);
        }
    }

    /**
     * Processing result from server return back
     *
     * @param result
     */
    private void endConnection(boolean result) {
        progressDialog.dismiss();
        if (result) {
            if (!ValidateParams.nullOrEmpty(cancelApply) && cancelApply.equals("1") && status == ElementApply.STATUS_APPLY_PENDING) {
                final DialogApplyConfirm dialog = new DialogApplyConfirm(this);
                dialog.setTextDisplay(getString(R.string.dialog_withdraw_title), getString(R.string.dialog_withdraw_msg)
                        , getString(R.string.label_dialog_Cancle), getString(R.string.dialog_btn_withdraw));
                dialog.setOnClickOK(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                        String url = String.format("%s:%s", element.getHost(), element.getPortSSL());
                        m_InformCtrl.SetURL(url);

                        //make parameter
                        boolean ret = makeParameterDrop();
                        if (!ret) {
                            showMessage(getString(R.string.connect_failed));
                            return;
                        }
                        progressDialog.show();
                        //open thread logon to server
                        new DropApplyTask().execute();
                    }
                });
                dialog.show();
            } else {
                elementMgr.updateStatus(status, id);
                Intent intent = new Intent(InputPasswordActivity.this, CompleteConfirmApplyActivity.class);
                intent.putExtra("STATUS_APPLY", status);
                intent.putExtra("ELEMENT_APPLY", element);
                intent.putExtra(StringList.m_str_InformCtrl, m_InformCtrl);
                finish();
                startActivity(intent);
            }
        } else {
            //show error message
            if (m_nErroType == InputBasePageFragment.ERR_FORBIDDEN) {
                String str_forbidden = getString(R.string.Forbidden);
                showMessage(m_InformCtrl.GetRtn().substring(str_forbidden.length()));
            } else if (m_nErroType == InputBasePageFragment.ERR_UNAUTHORIZED) {
                String str_unauth = getString(R.string.Unauthorized);
                showMessage(m_InformCtrl.GetRtn().substring(str_unauth.length()));
            } else if (m_nErroType == InputBasePageFragment.ERR_COLON) {
                String str_err = getString(R.string.ERR);
                showMessage(m_InformCtrl.GetRtn().substring(str_err.length()));
            } else if (m_nErroType == InputBasePageFragment.ERR_LOGIN_FAIL) {
                showMessage(getString(R.string.login_failed), new DialogApplyMessage.OnOkDismissMessageListener() {
                    @Override
                    public void onOkDismissMessage() {
                        txtPassword.setText("");
                        InputMethodManager imm = (InputMethodManager) getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED,0);
                    }
                });
            } else {
                showMessage(getString(R.string.connect_failed));
            }
        }
    }

    /**
     * Show message
     *
     * @param message
     */
    protected void showMessage(String message) {
        DialogApplyMessage dlgMessage = new DialogApplyMessage(this, message);
        dlgMessage.show();
    }

    /**
     * Show message
     *
     * @param message
     */
    protected void showMessage(String message, DialogApplyMessage.OnOkDismissMessageListener listener) {
        DialogApplyMessage dlgMessage = new DialogApplyMessage(this, message);
        dlgMessage.setOnOkDismissMessageListener(listener);
        dlgMessage.show();
    }
}
