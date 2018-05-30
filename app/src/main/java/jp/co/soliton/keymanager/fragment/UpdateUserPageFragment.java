package jp.co.soliton.keymanager.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import jp.co.soliton.keymanager.*;
import jp.co.soliton.keymanager.activity.CompleteApplyActivity;
import jp.co.soliton.keymanager.activity.CompleteConfirmApplyActivity;
import jp.co.soliton.keymanager.activity.ViewPagerUpdateActivity;
import jp.co.soliton.keymanager.common.CommonUtils;
import jp.co.soliton.keymanager.common.EpsapVersion;
import jp.co.soliton.keymanager.common.SoftKeyboardCtrl;
import jp.co.soliton.keymanager.customview.DialogApplyMessage;
import jp.co.soliton.keymanager.customview.DialogApplyProgressBar;
import jp.co.soliton.keymanager.dbalias.ElementApply;
import jp.co.soliton.keymanager.dbalias.ElementApplyManager;
import jp.co.soliton.keymanager.xmlparser.XmlDictionary;
import jp.co.soliton.keymanager.xmlparser.XmlPullParserAided;
import jp.co.soliton.keymanager.xmlparser.XmlStringData;

import java.net.URLEncoder;
import java.util.List;

import static jp.co.soliton.keymanager.common.ErrorNetwork.*;
import static jp.co.soliton.keymanager.manager.APIDManager.*;

/**
 * Created by luongdolong on 2/3/2017.
 *
 * Page input account and execute logon to server
 */

public class UpdateUserPageFragment extends ReapplyBasePageFragment {
    private EditText txtPassword;
    private TextView txtUserId;
    private boolean isEnroll;
    private boolean challenge;
    private ElementApplyManager elementMgr;
    private boolean isSubmitted;
    private boolean firstTime;

    public static Fragment newInstance(Context context) {
        UpdateUserPageFragment f = new UpdateUserPageFragment();
	    f.firstTime = true;
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
        if (context instanceof ViewPagerUpdateActivity) {
            this.pagerReapplyActivity = (ViewPagerUpdateActivity) context;
            if (progressDialog == null) {
                progressDialog = new DialogApplyProgressBar(pagerReapplyActivity);
            }
            if (elementMgr == null) {
                elementMgr = ElementApplyManager.getInstance(pagerReapplyActivity);
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
                    SoftKeyboardCtrl.hideKeyboard(v, getContext());
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
            SoftKeyboardCtrl.hideKeyboard(pagerReapplyActivity);
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            initValueControl();
            SoftKeyboardCtrl.hideKeyboard(pagerReapplyActivity);
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
        //openDatabase thread logon to server
        new LogonApplyTask().execute();
    }

    /**
     * Make parameter for logon to server
     * @return
     */
    private boolean makeParameterLogon() {
        String strPasswd = txtPassword.getText().toString();
        String rtnserial = "";
        if (TARGET_WiFi.equals(pagerReapplyActivity.getInputApplyInfo().getPlace())) {
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
	        LogCtrl.getInstance().error("UpdateUserPageFragment:makeParameterLogon: " + ex.toString());
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
	            InputApplyInfo inputApplyInfo = pagerReapplyActivity.getInputApplyInfo();
	            inputApplyInfo.setPassword(null);
	            inputApplyInfo.savePref(pagerReapplyActivity);
//	            String host = (inputApplyInfo.getHost());
//	            String userId = (inputApplyInfo.getUserId());
	            String id;
	            String versionEpsapServer = inputApplyInfo.getVersionEpsap();
	            if (ValidateParams.nullOrEmpty(versionEpsapServer)) {
		            id = String.valueOf(elementMgr.getIdElementApply(inputApplyInfo.getHost(), inputApplyInfo.getUserId()));
	            } else {
		            if (EpsapVersion.checkVersionValidUseApid(versionEpsapServer)) {
			            String target;
			            if (TARGET_WiFi.equals(inputApplyInfo.getPlace())) {
				            target = PREFIX_APID_WIFI + XmlPullParserAided.GetUDID(getActivity());
			            } else {
				            target = PREFIX_APID_VPN + XmlPullParserAided.GetVpnApid(getActivity());
			            }
			            id = String.valueOf(elementMgr.getIdElementApply(inputApplyInfo.getHost(), inputApplyInfo
					            .getUserId(), target));
		            } else {
			            id = String.valueOf(elementMgr.getIdElementApply(inputApplyInfo.getHost(), inputApplyInfo.getUserId()));
		            }
	            }
//	            pagerReapplyActivity.idConfirmApply = String.valueOf(elementMgr.getIdElementApply(host, userId));
                pagerReapplyActivity.idConfirmApply = id;
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
	                    txtPassword.requestFocus();
                        SoftKeyboardCtrl.showKeyboard(getActivity());
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
            elementMgr = ElementApplyManager.getInstance(pagerReapplyActivity);
        }
        elementMgr.updateStatus(ElementApply.STATUS_APPLY_CLOSED, pagerReapplyActivity.idConfirmApply);
        String rtnserial;
        if (TARGET_WiFi.equals(pagerReapplyActivity.getInputApplyInfo().getPlace())) {
            rtnserial = PREFIX_APID_WIFI + XmlPullParserAided.GetUDID(pagerReapplyActivity);
        } else {
            rtnserial = PREFIX_APID_VPN + XmlPullParserAided.GetVpnApid(pagerReapplyActivity);
        }
        ElementApply elementApply = new ElementApply();
	    InputApplyInfo inputApplyInfo = pagerReapplyActivity.getInputApplyInfo();
        elementApply.setHost(inputApplyInfo.getHost());
        elementApply.setPort(inputApplyInfo.getPort());
        elementApply.setPortSSL(inputApplyInfo.getSecurePort());
        elementApply.setUserId(inputApplyInfo.getUserId());
        elementApply.setPassword(inputApplyInfo.getPassword());
        elementApply.setEmail("");
        elementApply.setReason("");
        elementApply.setTarger(rtnserial);
        elementApply.setVersionEpsAp(inputApplyInfo.getVersionEpsap());
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

            LogCtrl.getInstance().info("Apply: Login");

            HttpConnectionCtrl conn = new HttpConnectionCtrl(pagerReapplyActivity);
            boolean ret = conn.RunHttpApplyLoginUrlConnection(pagerReapplyActivity.getInformCtrl());

            if (ret == false) {
                LogCtrl.getInstance().error("Apply Login: Connection error");
                m_nErroType = ERR_NETWORK;
                return false;
            }

            String retStr = pagerReapplyActivity.getInformCtrl().GetRtn();

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
            pagerReapplyActivity.getInformCtrl().SetLoginCookie(pagerReapplyActivity.getInformCtrl().GetCookie());
            ///////////////////////////////////////////////////
            // 認証応答の解析(Enroll応答のときの対応を流用できるはず)
            ///////////////////////////////////////////////////
            // 取得XMLのパーサー
            XmlPullParserAided m_p_aided = new XmlPullParserAided(pagerReapplyActivity, pagerReapplyActivity.getInformCtrl().GetRtn(), 2);    // 最上位dictの階層は2になる

            ret = m_p_aided.TakeApartUserAuthenticationResponse(pagerReapplyActivity.getInformCtrl());
            if (ret == false) {
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
                        if (TARGET_WiFi.equals(pagerReapplyActivity.getInputApplyInfo().getPlace())) {
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
	                    String currentUserId = txtUserId.getText().toString().trim();
	                    String userIdInApplyInfo = pagerReapplyActivity.getInputApplyInfo().getUserId();
	                    if (!userIdInApplyInfo.equals(currentUserId) || firstTime) {
			                    if (!ValidateParams.nullOrEmpty(p_data.GetData())) {
				                    pagerReapplyActivity.getInputApplyInfo().setEmail(p_data.GetData());
			                    } else {
				                    pagerReapplyActivity.getInputApplyInfo().setEmail("");
			                    }
			                    pagerReapplyActivity.getInputApplyInfo().setReason("");
			                    pagerReapplyActivity.getInputApplyInfo().setUserId(currentUserId);
			                    pagerReapplyActivity.getInputApplyInfo().savePref(pagerReapplyActivity);
		                    }
                    }
	                if (StringList.m_str_ver_epsap.equalsIgnoreCase(p_data.GetKeyName())) {
		                if (!ValidateParams.nullOrEmpty(p_data.GetData())) {
			                pagerReapplyActivity.getInputApplyInfo().setVersionEpsap(p_data.GetData());
			                pagerReapplyActivity.getInputApplyInfo().savePref(pagerReapplyActivity);
		                }
	                }
                }
	            if (ValidateParams.nullOrEmpty(pagerReapplyActivity.getInputApplyInfo().getVersionEpsap())) {
		            pagerReapplyActivity.getInputApplyInfo().setVersionEpsap("");
		            pagerReapplyActivity.getInputApplyInfo().savePref(pagerReapplyActivity);
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
