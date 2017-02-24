package epsap4.soliton.co.jp.fragment;

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
import android.widget.EditText;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;

import epsap4.soliton.co.jp.HttpConnectionCtrl;
import epsap4.soliton.co.jp.LogCtrl;
import epsap4.soliton.co.jp.R;
import epsap4.soliton.co.jp.StringList;
import epsap4.soliton.co.jp.ValidateParams;
import epsap4.soliton.co.jp.activity.CompleteApplyActivity;
import epsap4.soliton.co.jp.activity.ViewPagerInputActivity;
import epsap4.soliton.co.jp.customview.DialogApplyProgressBar;
import epsap4.soliton.co.jp.dbalias.DatabaseHandler;
import epsap4.soliton.co.jp.dbalias.ElementApply;
import epsap4.soliton.co.jp.xmlparser.XmlDictionary;
import epsap4.soliton.co.jp.xmlparser.XmlPullParserAided;
import epsap4.soliton.co.jp.xmlparser.XmlStringData;

/**
 * Created by luongdolong on 2/3/2017.
 *
 * Page input account and execute logon to server
 */

public class InputUserPageFragment extends InputBasePageFragment {
    private EditText txtUserId;
    private EditText txtPassword;
    private boolean isEnroll;
    private boolean challenge;
    private DatabaseHandler databaseHandler;

    public static Fragment newInstance(Context context) {
        InputUserPageFragment f = new InputUserPageFragment();
        return f;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup root = (ViewGroup) inflater.inflate(R.layout.fragment_input_user, null);
        txtUserId = (EditText) root.findViewById(R.id.txtUserId);
        txtPassword = (EditText) root.findViewById(R.id.txtPassword);
        initValueControl();
        return root;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof ViewPagerInputActivity) {
            this.pagerInputActivity = (ViewPagerInputActivity) context;
            if (progressDialog == null) {
                progressDialog = new DialogApplyProgressBar(pagerInputActivity);
            }
            if (databaseHandler == null) {
                databaseHandler = new DatabaseHandler(pagerInputActivity);
            }
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //Execute action for edit text
        txtUserId.addTextChangedListener(new TextWatcher() {
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
        txtUserId.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    hideKeyboard(v, getContext());
                }
            }
        });
        txtPassword.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    hideKeyboard(v, getContext());
                }
            }
        });
        txtPassword.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                    if (!nullOrEmpty(txtUserId.getText().toString()) && !nullOrEmpty(txtPassword.getText().toString())) {
                        nextAction();
                        return true;
                    }
                }
                return false;
            }
        });
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
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            initValueControl();
        }
    }

    /**
     * Start logon to server
     */
    @Override
    public void nextAction() {
        if (!ValidateParams.isValidUserID(txtUserId.getText().toString().trim())) {
            showMessage(getString(R.string.user_id_is_invalid));
            return;
        }
        pagerInputActivity.getInputApplyInfo().setUserId(txtUserId.getText().toString().trim());
        pagerInputActivity.getInputApplyInfo().setPassword(txtPassword.getText().toString());
        pagerInputActivity.getInputApplyInfo().savePref(pagerInputActivity);
        //make parameter
        boolean ret = makeParameterLogon();
        if (!ret) {
            showMessage(getString(R.string.connect_failed));
            return;
        }
        progressDialog.show();
        // グレーアウト
        setButtonRunnable(false);
        if (nullOrEmpty(pagerInputActivity.getInformCtrl().GetURL())) {
            String url = String.format("%s:%s", pagerInputActivity.getInputApplyInfo().getHost(), pagerInputActivity.getInputApplyInfo().getSecurePort());
            pagerInputActivity.getInformCtrl().SetURL(url);
        }
        pagerInputActivity.getInformCtrl().SetCookie(null);
        isEnroll = false;
        challenge = false;
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
        String rtnserial = "";
        if (InputBasePageFragment.TARGET_WiFi.equals(pagerInputActivity.getInputApplyInfo().getPlace())) {
            rtnserial = XmlPullParserAided.GetUDID(pagerInputActivity);
        } else {
            rtnserial = XmlPullParserAided.GetVpnApid(pagerInputActivity);
        }
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
        pagerInputActivity.getInformCtrl().SetUserID(strUserid);
        pagerInputActivity.getInformCtrl().SetPassword(strPasswd);
        pagerInputActivity.getInformCtrl().SetMessage(message);
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
                pagerInputActivity.getInputApplyInfo().setPassword(null);
                pagerInputActivity.getInputApplyInfo().savePref(pagerInputActivity);
                Intent intent = new Intent(pagerInputActivity, CompleteApplyActivity.class);
                intent.putExtra(CompleteApplyActivity.BACK_AUTO, false);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                pagerInputActivity.finish();
            } else {
                pagerInputActivity.gotoPage(4);
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
                showMessage(getString(R.string.login_failed));
            } else {
                showMessage(getString(R.string.connect_failed));
            }
        }
    }

    /**
     * init value for controls
     */
    private void initValueControl() {
        if (pagerInputActivity == null) {
            return;
        }
        if (!nullOrEmpty(pagerInputActivity.getInputApplyInfo().getUserId())) {
            txtUserId.setText(pagerInputActivity.getInputApplyInfo().getUserId());
        }
        if (!nullOrEmpty(pagerInputActivity.getInputApplyInfo().getPassword())) {
            txtPassword.setText(pagerInputActivity.getInputApplyInfo().getPassword());
        }
        setStatusControl();
    }

    /**
     * Set status for next back button
     */
    private void setStatusControl() {
        if (pagerInputActivity.getCurrentPage() != 3) {
            return;
        }
        if (nullOrEmpty(txtUserId.getText().toString()) || nullOrEmpty(txtPassword.getText().toString())) {
            pagerInputActivity.setActiveBackNext(true, false);
        } else {
            pagerInputActivity.setActiveBackNext(true, true);
        }
    }

    private void saveElementApply() {
        if (databaseHandler == null) {
            databaseHandler = new DatabaseHandler(pagerInputActivity);
        }
        String rtnserial;
        if (InputBasePageFragment.TARGET_WiFi.equals(pagerInputActivity.getInputApplyInfo().getPlace())) {
            rtnserial = "WIFI" + XmlPullParserAided.GetUDID(pagerInputActivity);
        } else {
            rtnserial = "APP" + XmlPullParserAided.GetVpnApid(pagerInputActivity);
        }
        ElementApply elementApply = new ElementApply();
        elementApply.setHost(pagerInputActivity.getInputApplyInfo().getHost());
        elementApply.setUserId(pagerInputActivity.getInputApplyInfo().getUserId());
        elementApply.setPassword(pagerInputActivity.getInputApplyInfo().getPassword());
        elementApply.setEmail("");
        elementApply.setReason("");
        elementApply.setTarger(rtnserial);
        elementApply.setStatus(ElementApply.STATUS_APPLY_PENDING);
        elementApply.setChallenge(challenge);
        databaseHandler.addElementApply(elementApply);
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
            HttpConnectionCtrl conn = new HttpConnectionCtrl(pagerInputActivity);
            boolean ret = conn.RunHttpApplyLoginUrlConnection(pagerInputActivity.getInformCtrl());

            if (ret == false) {
                LogCtrl.Logger(LogCtrl.m_strError, "LogonApplyTask " + "Network error", pagerInputActivity);
                m_nErroType = ERR_NETWORK;
                return false;
            }
            // ログイン結果
            if (pagerInputActivity.getInformCtrl().GetRtn().startsWith(getText(R.string.Forbidden).toString())) {
                LogCtrl.Logger(LogCtrl.m_strError, "LogonApplyTask  " + " Forbidden.", pagerInputActivity);
                m_nErroType = ERR_FORBIDDEN;
                return false;
            } else if (pagerInputActivity.getInformCtrl().GetRtn().startsWith(getText(R.string.Unauthorized).toString())) {
                LogCtrl.Logger(LogCtrl.m_strError, "LogonApplyTask  " + "Unauthorized.", pagerInputActivity);
                m_nErroType = ERR_UNAUTHORIZED;
                return false;
            } else if (pagerInputActivity.getInformCtrl().GetRtn().startsWith(getText(R.string.ERR).toString())) {
                LogCtrl.Logger(LogCtrl.m_strError, "LogonApplyTask  " + "ERR:", pagerInputActivity);
                m_nErroType = ERR_COLON;
                return false;
            } else if (pagerInputActivity.getInformCtrl().GetRtn().startsWith("NG")) {
                m_nErroType = ERR_LOGIN_FAIL;
                return false;
            }
            // 取得したCookieをログイン時のCookieとして保持する.
            pagerInputActivity.getInformCtrl().SetLoginCookie(pagerInputActivity.getInformCtrl().GetCookie());
            ///////////////////////////////////////////////////
            // 認証応答の解析(Enroll応答のときの対応を流用できるはず)
            ///////////////////////////////////////////////////
            // 取得XMLのパーサー
            XmlPullParserAided m_p_aided = new XmlPullParserAided(pagerInputActivity, pagerInputActivity.getInformCtrl().GetRtn(), 2);    // 最上位dictの階層は2になる

            ret = m_p_aided.TakeApartUserAuthenticationResponse(pagerInputActivity.getInformCtrl());
            if (ret == false) {
                LogCtrl.Logger(LogCtrl.m_strError, "LogonApplyTask-- " + "TakeApartDevice false", pagerInputActivity);
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
                    }
                    if(StringList.m_str_issubmitted.equalsIgnoreCase(p_data.GetKeyName()) ) {
                        if (6 == p_data.GetType()) {
                            isEnroll = true;
                        }
                    }
                    if (StringList.m_str_scep_challenge.equalsIgnoreCase(p_data.GetKeyName())) {
                        challenge = (6 == p_data.GetType());
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
