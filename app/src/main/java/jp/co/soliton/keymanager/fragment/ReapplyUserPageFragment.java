package jp.co.soliton.keymanager.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import java.net.URLEncoder;
import java.util.List;

import jp.co.soliton.keymanager.HttpConnectionCtrl;
import jp.co.soliton.keymanager.LogCtrl;
import jp.co.soliton.keymanager.R;
import jp.co.soliton.keymanager.StringList;
import jp.co.soliton.keymanager.ValidateParams;
import jp.co.soliton.keymanager.activity.CompleteApplyActivity;
import jp.co.soliton.keymanager.activity.CompleteConfirmApplyActivity;
import jp.co.soliton.keymanager.activity.ViewPagerReapplyActivity;
import jp.co.soliton.keymanager.customview.DialogApplyMessage;
import jp.co.soliton.keymanager.customview.DialogApplyProgressBar;
import jp.co.soliton.keymanager.dbalias.ElementApply;
import jp.co.soliton.keymanager.dbalias.ElementApplyManager;
import jp.co.soliton.keymanager.xmlparser.XmlDictionary;
import jp.co.soliton.keymanager.xmlparser.XmlPullParserAided;
import jp.co.soliton.keymanager.xmlparser.XmlStringData;

/**
 * Created by luongdolong on 2/3/2017.
 *
 * Page input account and execute logon to server
 */

public class ReapplyUserPageFragment extends ReapplyBasePageFragment {
    private EditText txtPassword;
    private TextView txtUserId;
    private boolean isEnroll;
    private boolean challenge;
    private ElementApplyManager elementMgr;
    private boolean isSubmitted;

    public static Fragment newInstance(Context context) {
        ReapplyUserPageFragment f = new ReapplyUserPageFragment();
        return f;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup root = (ViewGroup) inflater.inflate(R.layout.fragment_reapply_user, null);
        txtPassword = (EditText) root.findViewById(R.id.txtPassword);
        txtUserId = (TextView) root.findViewById(R.id.txtUserId);
        initValueControl();
        return root;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof ViewPagerReapplyActivity) {
            this.pagerReapplyActivity = (ViewPagerReapplyActivity) context;
            if (progressDialog == null) {
                progressDialog = new DialogApplyProgressBar(pagerReapplyActivity);
            }
            if (elementMgr == null) {
                elementMgr = new ElementApplyManager(pagerReapplyActivity);
            }
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //Execute action for edit text
        txtPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                setStatusControl();
            }
        });

        txtPassword.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    hideKeyboard(v, getContext());
                } else {
                    InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.showSoftInput(txtPassword, InputMethodManager.SHOW_IMPLICIT);
                }
            }
        });
        txtPassword.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                    if (!nullOrEmpty(txtPassword.getText().toString())) {
                        nextAction();
                        return true;
                    }
                }
                return false;
            }
        });
        if (!nullOrEmpty(pagerReapplyActivity.getInputApplyInfo().getUserId())) {
            //txtPassword.requestFocus();
            //InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            //imm.toggleSoftInput(InputMethodManager.SHOW_FORCED,0);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void setMenuVisibility(final boolean visible) {
        super.setMenuVisibility(visible);
        if (visible) {
            initValueControl();
            hideKeyboard(pagerReapplyActivity);
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            initValueControl();
            hideKeyboard(pagerReapplyActivity);
        }
    }

    /**
     * Start logon to server
     */
    @Override
    public void nextAction() {
        pagerReapplyActivity.getInputApplyInfo().setPassword(txtPassword.getText().toString());
        pagerReapplyActivity.getInputApplyInfo().savePref(pagerReapplyActivity);
        //make parameter
        boolean ret = makeParameterLogon();
        if (!ret) {
            showMessage(getString(R.string.connect_failed));
            return;
        }
        progressDialog.show();
        // グレーアウト
        setButtonRunnable(false);
        if (nullOrEmpty(pagerReapplyActivity.getInformCtrl().GetURL())) {
            String url = String.format("%s:%s", pagerReapplyActivity.getInputApplyInfo().getHost(),
                    pagerReapplyActivity.getInputApplyInfo().getSecurePort());
            pagerReapplyActivity.getInformCtrl().SetURL(url);
        }
        pagerReapplyActivity.getInformCtrl().SetCookie(null);
        isEnroll = false;
        challenge = false;
        //open thread logon to server
        new LogonApplyTask().execute();
    }

    private void hideKeyboard(Activity activity) {
        if (activity != null && activity.getWindow() != null && activity.getWindow().getDecorView() != null) {
            InputMethodManager imm = (InputMethodManager)activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(activity.getWindow().getDecorView().getWindowToken(), 0);
        }
    }

    /**
     * Make parameter for logon to server
     * @return
     */
    private boolean makeParameterLogon() {
        String strPasswd = txtPassword.getText().toString();
        String rtnserial = "";
        if (InputBasePageFragment.TARGET_WiFi.equals(pagerReapplyActivity.getInputApplyInfo().getPlace())) {
            rtnserial = XmlPullParserAided.GetUDID(pagerReapplyActivity);
        } else {
            rtnserial = XmlPullParserAided.GetVpnApid(pagerReapplyActivity);
        }
        // ログインメッセージ
        // URLEncodeが必須 <http://wada811.blog.fc2.com/?tag=URL%E3%82%A8%E3%83%B3%E3%82%B3%E3%83%BC%E3%83%89>参照
        String message;
        try {
            message = "Action=logon" + "&" + StringList.m_strUserID +
                    URLEncoder.encode(pagerReapplyActivity.getInputApplyInfo().getUserId(), "UTF-8") +
                    "&" + StringList.m_strPassword + URLEncoder.encode(strPasswd, "UTF-8") +
                    "&" + StringList.m_strSerial + rtnserial;
        } catch (Exception ex) {
            Log.i(StringList.m_str_SKMTag, "logon:: " + "Message=" + ex.getMessage());
            return false;
        }
        // 入力データを情報管理クラスへセットする
        pagerReapplyActivity.getInformCtrl().SetUserID(pagerReapplyActivity.getInputApplyInfo().getUserId());
        pagerReapplyActivity.getInformCtrl().SetPassword(strPasswd);
        pagerReapplyActivity.getInformCtrl().SetMessage(message);
        return true;
    }

    /**
     * Processing result from server return back
     *
     * @param result
     */
    private void endConnection(boolean result) {
        progressDialog.dismiss();
        setButtonRunnable(true);
        if (result) {
            //check action next
            if (isEnroll) {
                //save element apply
                saveElementApply();
                pagerReapplyActivity.getInputApplyInfo().setPassword(null);
                pagerReapplyActivity.getInputApplyInfo().savePref(pagerReapplyActivity);
                Intent intent = new Intent(pagerReapplyActivity, CompleteApplyActivity.class);
                intent.putExtra(StringList.BACK_AUTO, true);
                intent.putExtra(StringList.m_str_InformCtrl, pagerReapplyActivity.getInformCtrl());
                ElementApply element = elementMgr.getElementApply(pagerReapplyActivity.idConfirmApply);
                intent.putExtra("ELEMENT_APPLY", element);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                pagerReapplyActivity.finish();
            } else {
                if (isSubmitted) {
                    saveElementApply();
                    Intent intent = new Intent(pagerReapplyActivity, CompleteConfirmApplyActivity.class);
                    pagerReapplyActivity.finish();
                    intent.putExtra("STATUS_APPLY", ElementApply.STATUS_APPLY_PENDING);
                    ElementApply element = elementMgr.getElementApply(pagerReapplyActivity.idConfirmApply);
                    intent.putExtra("ELEMENT_APPLY", element);
                    intent.putExtra(StringList.m_str_InformCtrl, pagerReapplyActivity.getInformCtrl());
                    startActivity(intent);
                } else {
                    pagerReapplyActivity.gotoPage(1);
                }
            }
        } else {
            //show error message
            if (m_nErroType == ERR_FORBIDDEN) {
                String str_forbidden = getString(R.string.Forbidden);
                showMessage(pagerReapplyActivity.getInformCtrl().GetRtn().substring(str_forbidden.length()));
            } else if (m_nErroType == ERR_UNAUTHORIZED) {
                String str_unauth = getString(R.string.Unauthorized);
                showMessage(pagerReapplyActivity.getInformCtrl().GetRtn().substring(str_unauth.length()));
            } else if (m_nErroType == ERR_COLON) {
                String str_err = getString(R.string.ERR);
                showMessage(pagerReapplyActivity.getInformCtrl().GetRtn().substring(str_err.length()));
            } else if (m_nErroType == ERR_LOGIN_FAIL) {
                showMessage(getString(R.string.login_failed), new DialogApplyMessage.OnOkDismissMessageListener() {
                    @Override
                    public void onOkDismissMessage() {
                        txtPassword.setText("");
                        InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED,0);
                    }
                });
            } else {
                showMessage(getString(R.string.connect_failed));
            }
        }
    }

    /**
     * init value for controls
     */
    private void initValueControl() {
        if (pagerReapplyActivity == null) {
            return;
        }
        if (!nullOrEmpty(pagerReapplyActivity.getInputApplyInfo().getUserId())) {
            txtUserId.setText(pagerReapplyActivity.getInputApplyInfo().getUserId());
        }
        if (!nullOrEmpty(pagerReapplyActivity.getInputApplyInfo().getPassword())) {
            txtPassword.setText(pagerReapplyActivity.getInputApplyInfo().getPassword());
        }
        setStatusControl();
    }

    /**
     * Set status for next back button
     */
    private void setStatusControl() {
        if (pagerReapplyActivity.getCurrentPage() != 0) {
            return;
        }
        if (nullOrEmpty(txtPassword.getText().toString())) {
            pagerReapplyActivity.setActiveBackNext(true, false);
        } else {
            pagerReapplyActivity.setActiveBackNext(true, true);
        }
        pagerReapplyActivity.gotoPage(0);
    }

    private void saveElementApply() {
        if (elementMgr == null) {
            elementMgr = new ElementApplyManager(pagerReapplyActivity);
        }
        elementMgr.updateStatus(ElementApply.STATUS_APPLY_CLOSED, pagerReapplyActivity.idConfirmApply);
        String rtnserial;
        if (InputBasePageFragment.TARGET_WiFi.equals(pagerReapplyActivity.getInputApplyInfo().getPlace())) {
            rtnserial = "WIFI" + XmlPullParserAided.GetUDID(pagerReapplyActivity);
        } else {
            rtnserial = "APP" + XmlPullParserAided.GetVpnApid(pagerReapplyActivity);
        }
        ElementApply elementApply = new ElementApply();
        elementApply.setHost(pagerReapplyActivity.getInputApplyInfo().getHost());
        elementApply.setPort(pagerReapplyActivity.getInputApplyInfo().getPort());
        elementApply.setPortSSL(pagerReapplyActivity.getInputApplyInfo().getSecurePort());
        elementApply.setUserId(pagerReapplyActivity.getInputApplyInfo().getUserId());
        elementApply.setPassword(pagerReapplyActivity.getInputApplyInfo().getPassword());
        elementApply.setEmail("");
        elementApply.setReason("");
        elementApply.setTarger(rtnserial);
        elementApply.setStatus(ElementApply.STATUS_APPLY_PENDING);
        elementApply.setChallenge(challenge);
        elementMgr.saveElementApply(elementApply);
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
            HttpConnectionCtrl conn = new HttpConnectionCtrl(pagerReapplyActivity);
            boolean ret = conn.RunHttpApplyLoginUrlConnection(pagerReapplyActivity.getInformCtrl());

            if (ret == false) {
                LogCtrl.Logger(LogCtrl.m_strError, "LogonApplyTask " + "Network error", pagerReapplyActivity);
                m_nErroType = ERR_NETWORK;
                return false;
            }
            // ログイン結果
            if (pagerReapplyActivity.getInformCtrl().GetRtn().startsWith(getText(R.string.Forbidden).toString())) {
                LogCtrl.Logger(LogCtrl.m_strError, "LogonApplyTask  " + " Forbidden.", pagerReapplyActivity);
                m_nErroType = ERR_FORBIDDEN;
                return false;
            } else if (pagerReapplyActivity.getInformCtrl().GetRtn().startsWith(getText(R.string.Unauthorized).toString())) {
                LogCtrl.Logger(LogCtrl.m_strError, "LogonApplyTask  " + "Unauthorized.", pagerReapplyActivity);
                m_nErroType = ERR_UNAUTHORIZED;
                return false;
            } else if (pagerReapplyActivity.getInformCtrl().GetRtn().startsWith(getText(R.string.ERR).toString())) {
                LogCtrl.Logger(LogCtrl.m_strError, "LogonApplyTask  " + "ERR:", pagerReapplyActivity);
                m_nErroType = ERR_COLON;
                return false;
            } else if (pagerReapplyActivity.getInformCtrl().GetRtn().startsWith("NG")) {
                m_nErroType = ERR_LOGIN_FAIL;
                return false;
            }
            // 取得したCookieをログイン時のCookieとして保持する.
            pagerReapplyActivity.getInformCtrl().SetLoginCookie(pagerReapplyActivity.getInformCtrl().GetCookie());
            ///////////////////////////////////////////////////
            // 認証応答の解析(Enroll応答のときの対応を流用できるはず)
            ///////////////////////////////////////////////////
            // 取得XMLのパーサー
            XmlPullParserAided m_p_aided = new XmlPullParserAided(pagerReapplyActivity, pagerReapplyActivity.getInformCtrl().GetRtn(), 2);    // 最上位dictの階層は2になる

            ret = m_p_aided.TakeApartUserAuthenticationResponse(pagerReapplyActivity.getInformCtrl());
            if (ret == false) {
                LogCtrl.Logger(LogCtrl.m_strError, "LogonApplyTask-- " + "TakeApartDevice false", pagerReapplyActivity);
                m_nErroType = ERR_NETWORK;
                return false;
            }
            //parse xml return from server
            XmlDictionary xmldict = m_p_aided.GetDictionary();
            if(xmldict != null) {
                List<XmlStringData> str_list;
                str_list = xmldict.GetArrayString();
                for(int i = 0; str_list.size() > i; i++){
                    // config情報に従って、処理を行う.
                    XmlStringData p_data = str_list.get(i);
                    // 要素タイプ(string:1, data=2, date=3, real=4, integer=5, true=6, false=7)
                    if(StringList.m_str_isEnroll.equalsIgnoreCase(p_data.GetKeyName()) ) {
                        isEnroll = true;
                        String rtnserial = "";
                        if (InputBasePageFragment.TARGET_WiFi.equals(pagerReapplyActivity.getInputApplyInfo().getPlace())) {
                            rtnserial = XmlPullParserAided.GetUDID(pagerReapplyActivity);
                        } else {
                            rtnserial = XmlPullParserAided.GetVpnApid(pagerReapplyActivity);
                        }
                        String sendmsg = m_p_aided.DeviceInfoText(rtnserial);
                        pagerReapplyActivity.getInformCtrl().SetMessage(sendmsg);
                    }
                    if(StringList.m_str_issubmitted.equalsIgnoreCase(p_data.GetKeyName()) ) {
                        if (6 == p_data.GetType()) {
                            isSubmitted = true;
                        }
                    }
                    if (StringList.m_str_scep_challenge.equalsIgnoreCase(p_data.GetKeyName())) {
                        challenge = (6 == p_data.GetType());
                    }
                    if (StringList.m_str_mailaddress.equalsIgnoreCase(p_data.GetKeyName())) {
                        if (!ValidateParams.nullOrEmpty(p_data.GetData())) {
                            pagerReapplyActivity.getInputApplyInfo().setEmail(p_data.GetData());
                        }
                    }
                }
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
}
