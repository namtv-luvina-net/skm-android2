package jp.co.soliton.keymanager.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import jp.co.soliton.keymanager.*;
import jp.co.soliton.keymanager.common.CommonUtils;
import jp.co.soliton.keymanager.common.SoftKeyboardCtrl;
import jp.co.soliton.keymanager.customview.DialogApplyConfirm;
import jp.co.soliton.keymanager.customview.DialogApplyMessage;
import jp.co.soliton.keymanager.customview.DialogApplyProgressBar;
import jp.co.soliton.keymanager.dbalias.ElementApply;
import jp.co.soliton.keymanager.dbalias.ElementApplyManager;
import jp.co.soliton.keymanager.xmlparser.XmlDictionary;
import jp.co.soliton.keymanager.xmlparser.XmlPullParserAided;
import jp.co.soliton.keymanager.xmlparser.XmlStringData;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;

import static jp.co.soliton.keymanager.common.ErrorNetwork.*;
import static jp.co.soliton.keymanager.manager.APIDManager.PREFIX_APID_VPN;
import static jp.co.soliton.keymanager.manager.APIDManager.PREFIX_APID_WIFI;

/**
 * Created by luongdolong on 2/3/2017.
 *
 * Activity for menu apply screen
 */

public class InputPasswordActivity extends Activity implements SoftKeyboardCtrl.DetectsListenner{
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
	private boolean isShowingKeyboard = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input_password);
        id = getIntent().getStringExtra(StringList.ELEMENT_APPLY_ID);
        cancelApply = getIntent().getStringExtra(StringList.CANCEL_APPLY);
        txtUserId = (TextView) findViewById(R.id.txtUserId);
        txtPassword = (EditText) findViewById(R.id.txtPassword);
        btnInputNext = (Button) findViewById(R.id.btnInputNext);
        elementMgr = ElementApplyManager.getInstance(this);
        m_InformCtrl = new InformCtrl();
        if (progressDialog == null) {
            progressDialog = new DialogApplyProgressBar(this);
        }
	    SoftKeyboardCtrl.addListenner(findViewById(R.id.activityRoot), this);
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
        //openDatabase thread logon to server
        new LogonApplyTask().execute();
    }

    /**
     * Make parameter for logon to server
     * @return
     */
    private boolean makeParameterLogon() {
        String strUserid = txtUserId.getText().toString().trim();
        String strPasswd = txtPassword.getText().toString();
        String rtnserial = element.getTarget().replace(PREFIX_APID_WIFI, "").replace(PREFIX_APID_VPN, "");
	    String str_url = m_InformCtrl.GetURL();
        // ログインメッセージ
        // URLEncodeが必須 <http://wada811.blog.fc2.com/?tag=URL%E3%82%A8%E3%83%B3%E3%82%B3%E3%83%BC%E3%83%89>参照
        String message;
	    try {
		    message = "Action=logon" + "&" + StringList.m_strUserID + URLEncoder.encode(strUserid, "UTF-8") +
		            "&" + StringList.m_strPassword + URLEncoder.encode(strPasswd, "UTF-8") +
		            "&" + StringList.m_strSerial + rtnserial;

        } catch (UnsupportedEncodingException ex) {
	        LogCtrl.getInstance().error("InputPasswordActivity::makeParameterLogon:UnsupportedEncodingException: "+ ex.toString());
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
	        LogCtrl.getInstance().error("InputPasswordActivity::makeParameterDrop:Exception: " + ex.toString());
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

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        View v = getCurrentFocus();

        if (v != null &&
                (ev.getAction() == MotionEvent.ACTION_UP || ev.getAction() == MotionEvent.ACTION_MOVE) &&
                v instanceof EditText &&
                !v.getClass().getName().startsWith("android.webkit.")) {
            int scrcoords[] = new int[2];
            v.getLocationOnScreen(scrcoords);
            float x = ev.getRawX() + v.getLeft() - scrcoords[0];
            float y = ev.getRawY() + v.getTop() - scrcoords[1];

            if (x < v.getLeft() || x > v.getRight() || y < v.getTop() || y > v.getBottom())
                SoftKeyboardCtrl.hideKeyboard(this);
        }
        return super.dispatchTouchEvent(ev);
    }

	@Override
	public void onSoftKeyboardShown(boolean isShowing) {
		if (!isShowing) {
			if (isShowingKeyboard) {
				View v = getCurrentFocus();
				if (v != null && v instanceof EditText) {
					v.clearFocus();
				}
				isShowingKeyboard = false;
			}
		} else {
			isShowingKeyboard = true;
		}
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

            LogCtrl.getInstance().info("Apply: Login");

            HttpConnectionCtrl conn = new HttpConnectionCtrl(getApplicationContext());
            boolean ret = conn.RunHttpApplyLoginUrlConnection(m_InformCtrl);
            if (ret == false) {
                LogCtrl.getInstance().error("Apply Login: Connection error");
                m_nErroType = ERR_NETWORK;
                return false;
            }

            String retStr = m_InformCtrl.GetRtn();

            // ログイン結果
            if (retStr.startsWith(getText(R.string.Forbidden).toString())) {
                LogCtrl.getInstance().error("Apply Login: Receive " + retStr);
                m_nErroType = ERR_FORBIDDEN;
                return false;
            } else if (retStr.startsWith(getText(R.string.Unauthorized).toString())) {
                LogCtrl.getInstance().error("Apply Login: Receive " + retStr);
                m_nErroType = ERR_UNAUTHORIZED;
                return false;
            } else if (retStr.startsWith(getText(R.string.ERR).toString())) {
                LogCtrl.getInstance().error("Apply Login: Receive " + retStr);
                m_nErroType = ERR_COLON;
                return false;
            } else if (retStr.startsWith("NG")) {
                LogCtrl.getInstance().error("Apply Login: Receive " + retStr);
                m_nErroType = ERR_LOGIN_FAIL;
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
                m_nErroType = ERR_NETWORK;
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
                String sendmsg = m_p_aided.DeviceInfoText(element.getTarget().replace(PREFIX_APID_WIFI, "").replace
		                (PREFIX_APID_VPN, ""));
                m_InformCtrl.SetMessage(sendmsg);
            }
            ////////////////////////////////////////////////////////////////////////////
            // 大項目1. ログイン終了 =========>
            ////////////////////////////////////////////////////////////////////////////
            m_nErroType = SUCCESSFUL;
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
                LogCtrl.getInstance().error("Withdraw: Connection error");
                m_nErroType = ERR_NETWORK;
                return false;
            }

            String retStr = m_InformCtrl.GetRtn();

            // ログイン結果
            if (retStr.startsWith(getText(R.string.Forbidden).toString())) {
                LogCtrl.getInstance().error("Withdraw: Receive " + retStr);
                m_nErroType = ERR_FORBIDDEN;
                return false;
            } else if (retStr.startsWith(getText(R.string.Unauthorized).toString())) {
                LogCtrl.getInstance().error("Withdraw: Receive " + retStr);
                m_nErroType = ERR_UNAUTHORIZED;
                return false;
            } else if (retStr.startsWith(getText(R.string.ERR).toString())) {
                LogCtrl.getInstance().error("Withdraw: Receive " + retStr);
                m_nErroType = ERR_COLON;
                return false;
            } else if (retStr.startsWith("NG")) {
                LogCtrl.getInstance().error("Withdraw: Receive " + retStr);
                m_nErroType = ERR_LOGIN_FAIL;
                return false;
            }
            // 取得したCookieをログイン時のCookieとして保持する.
            m_InformCtrl.SetLoginCookie(m_InformCtrl.GetCookie());
            if (m_InformCtrl.GetRtn().startsWith("OK")) {
                status = ElementApply.STATUS_APPLY_CANCEL;
            }
            m_nErroType = SUCCESSFUL;
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
            if (!ValidateParams.nullOrEmpty(cancelApply) && cancelApply.equals("1") && status != ElementApply.STATUS_APPLY_REJECT) {
                final DialogApplyConfirm dialog = new DialogApplyConfirm(this);
                dialog.setTextDisplay(getString(R.string.dialog_withdraw_title), getString(R.string.dialog_withdraw_msg)
                        , getString(R.string.label_dialog_cancel), getString(R.string.dialog_btn_withdraw));
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
                        //openDatabase thread logon to server
                        new DropApplyTask().execute();
                    }
                });
                dialog.setOnClickCancel(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                        finish();
                    }
                });
                dialog.show();
            } else {
                if (status != ElementApply.STATUS_APPLY_APPROVED) {
                    elementMgr.updateStatus(status, id);
                }

                Intent intent = new Intent(InputPasswordActivity.this, CompleteConfirmApplyActivity.class);
                intent.putExtra("STATUS_APPLY", status);
                intent.putExtra("ELEMENT_APPLY", element);
                intent.putExtra(StringList.m_str_InformCtrl, m_InformCtrl);
                finish();
                startActivity(intent);
            }
        } else {
            //show error message
            if (m_nErroType == ERR_FORBIDDEN) {
                String str_forbidden = getString(R.string.Forbidden);
                showMessage(m_InformCtrl.GetRtn().substring(str_forbidden.length()));
            } else if (m_nErroType == ERR_UNAUTHORIZED) {
                String str_unauth = getString(R.string.Unauthorized);
                showMessage(m_InformCtrl.GetRtn().substring(str_unauth.length()));
            } else if (m_nErroType == ERR_COLON) {
                String str_err = getString(R.string.ERR);
                showMessage(m_InformCtrl.GetRtn().substring(str_err.length()));
            } else if (m_nErroType == ERR_LOGIN_FAIL) {
                showMessage(getString(R.string.login_failed), new DialogApplyMessage.OnOkDismissMessageListener() {
                    @Override
                    public void onOkDismissMessage() {
                        txtPassword.setText("");
	                    txtPassword.requestFocus();
                        SoftKeyboardCtrl.showKeyboard(InputPasswordActivity.this);
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
