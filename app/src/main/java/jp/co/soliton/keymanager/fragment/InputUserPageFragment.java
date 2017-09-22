package jp.co.soliton.keymanager.fragment;

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
import jp.co.soliton.keymanager.*;
import jp.co.soliton.keymanager.activity.CompleteApplyActivity;
import jp.co.soliton.keymanager.activity.CompleteConfirmApplyActivity;
import jp.co.soliton.keymanager.activity.ViewPagerInputActivity;
import jp.co.soliton.keymanager.common.EpsapVersion;
import jp.co.soliton.keymanager.common.SoftKeyboardCtrl;
import jp.co.soliton.keymanager.customview.DialogApplyMessage;
import jp.co.soliton.keymanager.customview.DialogApplyProgressBar;
import jp.co.soliton.keymanager.dbalias.ElementApply;
import jp.co.soliton.keymanager.dbalias.ElementApplyManager;
import jp.co.soliton.keymanager.manager.APIDManager;
import jp.co.soliton.keymanager.xmlparser.XmlDictionary;
import jp.co.soliton.keymanager.xmlparser.XmlPullParserAided;
import jp.co.soliton.keymanager.xmlparser.XmlStringData;

import java.net.URLEncoder;
import java.util.List;

import static jp.co.soliton.keymanager.common.ErrorNetwork.*;
import static jp.co.soliton.keymanager.manager.APIDManager.TARGET_VPN;
import static jp.co.soliton.keymanager.manager.APIDManager.TARGET_WiFi;

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
    private ElementApplyManager elementMgr;
    private boolean isSubmitted;
	private boolean firstTime;

    public static Fragment newInstance(Context context) {
        InputUserPageFragment f = new InputUserPageFragment();
	    f.firstTime = true;
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
            if (elementMgr == null) {
                elementMgr = new ElementApplyManager(pagerInputActivity);
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
	    txtUserId.setOnKeyListener(new View.OnKeyListener() {
		    @Override
		    public boolean onKey(View v, int keyCode, KeyEvent event) {
			    if (event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
				    txtPassword.requestFocus();
			    }
			    return false;
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
        if (TARGET_WiFi.equals(pagerInputActivity.getInputApplyInfo().getPlace())) {
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
	        LogCtrl.getInstance().error("InputUserPageFragment:makeParameterLogon: " + ex.toString());
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
	            InputApplyInfo inputApplyInfo = pagerInputActivity.getInputApplyInfo();
	            inputApplyInfo.setPassword(null);
	            inputApplyInfo.savePref(pagerInputActivity);
                Intent intent = new Intent(pagerInputActivity, CompleteApplyActivity.class);
                intent.putExtra(StringList.BACK_AUTO, true);
                intent.putExtra(StringList.m_str_InformCtrl, pagerInputActivity.getInformCtrl());
	            String id;
	            String versionEpsapServer = inputApplyInfo.getVersionEpsap();
	            if (ValidateParams.nullOrEmpty(versionEpsapServer)) {
		            id = String.valueOf(elementMgr.getIdElementApply(inputApplyInfo.getHost(), inputApplyInfo.getUserId()));
	            } else {
		            if (EpsapVersion.checkVersionValidUseApid(versionEpsapServer)) {
			            String target;
			            if (TARGET_WiFi.equals(inputApplyInfo.getPlace())) {
				            target = "WIFI" + XmlPullParserAided.GetUDID(getActivity());
			            } else {
				            target = "APP" + XmlPullParserAided.GetVpnApid(getActivity());
			            }
			            id = String.valueOf(elementMgr.getIdElementApply(inputApplyInfo.getHost(), inputApplyInfo
							            .getUserId(), target));
		            } else {
			            id = String.valueOf(elementMgr.getIdElementApply(inputApplyInfo.getHost(), inputApplyInfo.getUserId()));
		            }
	            }
                ElementApply element = elementMgr.getElementApply(id);
                intent.putExtra("ELEMENT_APPLY", element);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                pagerInputActivity.finish();
            } else {
                if (isSubmitted) {
	                Log.d("InputUserPageFragment", "datnd:endConnection: isSubmitted");
	                saveElementApply();
	                InputApplyInfo inputApplyInfo = pagerInputActivity.getInputApplyInfo();
                    Intent intent = new Intent(pagerInputActivity, CompleteConfirmApplyActivity.class);
                    pagerInputActivity.finish();
                    intent.putExtra("STATUS_APPLY", ElementApply.STATUS_APPLY_PENDING);
	                String id;
	                String versionEpsapServer = inputApplyInfo.getVersionEpsap();
	                if (ValidateParams.nullOrEmpty(versionEpsapServer)) {
		                id = String.valueOf(elementMgr.getIdElementApply(inputApplyInfo.getHost(), inputApplyInfo.getUserId()));
	                } else {
		                if (EpsapVersion.checkVersionValidUseApid(versionEpsapServer)) {
			                String target;
			                if (TARGET_WiFi.equals(inputApplyInfo.getPlace())) {
				                target = "WIFI" + XmlPullParserAided.GetUDID(getActivity());
			                } else {
				                target = "APP" + XmlPullParserAided.GetVpnApid(getActivity());
			                }
			                id = String.valueOf(elementMgr.getIdElementApply(inputApplyInfo.getHost(), inputApplyInfo
					                .getUserId(), target));
		                } else {
			                id = String.valueOf(elementMgr.getIdElementApply(inputApplyInfo.getHost(), inputApplyInfo.getUserId()));
		                }
	                }
                    ElementApply element = elementMgr.getElementApply(id);
                    intent.putExtra("ELEMENT_APPLY", element);
                    intent.putExtra(StringList.m_str_InformCtrl, pagerInputActivity.getInformCtrl());
                    startActivity(intent);
                } else {
	                pagerInputActivity.gotoPage(4);
                }
            }
        } else {
            //show error message
            if (m_nErroType == ERR_FORBIDDEN) {
                String str_forbidden = getString(R.string.Forbidden);
                showMessage(pagerInputActivity.getInformCtrl().GetRtn().substring(str_forbidden.length()));
            } else if (m_nErroType == ERR_UNAUTHORIZED) {
                String str_unauth = getString(R.string.Unauthorized);
                showMessage(pagerInputActivity.getInformCtrl().GetRtn().substring(str_unauth.length()));
            } else if (m_nErroType == ERR_COLON) {
                String str_err = getString(R.string.ERR);
                showMessage(pagerInputActivity.getInformCtrl().GetRtn().substring(str_err.length()), new DialogApplyMessage.OnOkDismissMessageListener() {
	                @Override
	                public void onOkDismissMessage() {
		                txtPassword.setText("");
		                txtUserId.requestFocus();
		                SoftKeyboardCtrl.showKeyboard(getActivity());
	                }
                });
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
        if (elementMgr == null) {
            elementMgr = new ElementApplyManager(pagerInputActivity);
        }
        String rtnserial;
        if (TARGET_WiFi.equals(pagerInputActivity.getInputApplyInfo().getPlace())) {
            rtnserial = "WIFI" + XmlPullParserAided.GetUDID(pagerInputActivity);
        } else {
            rtnserial = "APP" + XmlPullParserAided.GetVpnApid(pagerInputActivity);
        }
        ElementApply elementApply = new ElementApply();
        elementApply.setHost(pagerInputActivity.getInputApplyInfo().getHost());
        elementApply.setPort(pagerInputActivity.getInputApplyInfo().getPort());
        elementApply.setPortSSL(pagerInputActivity.getInputApplyInfo().getSecurePort());
        elementApply.setUserId(pagerInputActivity.getInputApplyInfo().getUserId());
        elementApply.setPassword(pagerInputActivity.getInputApplyInfo().getPassword());
        elementApply.setVersionEpsAp(pagerInputActivity.getInputApplyInfo().getVersionEpsap());
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

            LogCtrl.getInstance().info("Apply: Login");

            HttpConnectionCtrl conn = new HttpConnectionCtrl(pagerInputActivity);
            boolean ret = conn.RunHttpApplyLoginUrlConnection(pagerInputActivity.getInformCtrl());

            if (ret == false) {
                LogCtrl.getInstance().error("Apply Login: Network error");
                m_nErroType = ERR_NETWORK;
                return false;
            }

            String retStr = pagerInputActivity.getInformCtrl().GetRtn();
            // ログイン結果
            if (retStr.startsWith(getText(R.string.Forbidden).toString())) {
                LogCtrl.getInstance().error("Apply Login: Permission error (Forbidden)");
                m_nErroType = ERR_FORBIDDEN;
                return false;
            } else if (retStr.startsWith(getText(R.string.Unauthorized).toString())) {
                LogCtrl.getInstance().error("Apply Login: Permission error (Unauthorized)");
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
            pagerInputActivity.getInformCtrl().SetLoginCookie(pagerInputActivity.getInformCtrl().GetCookie());
            ///////////////////////////////////////////////////
            // 認証応答の解析(Enroll応答のときの対応を流用できるはず)
            ///////////////////////////////////////////////////
            // 取得XMLのパーサー
            XmlPullParserAided m_p_aided = new XmlPullParserAided(pagerInputActivity, pagerInputActivity.getInformCtrl().GetRtn(), 2);    // 最上位dictの階層は2になる

            ret = m_p_aided.TakeApartUserAuthenticationResponse(pagerInputActivity.getInformCtrl());
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
                        if (TARGET_WiFi.equals(pagerInputActivity.getInputApplyInfo().getPlace())) {
                            rtnserial = XmlPullParserAided.GetUDID(pagerInputActivity);
                        } else {
                            rtnserial = XmlPullParserAided.GetVpnApid(pagerInputActivity);
                        }
                        String sendmsg = m_p_aided.DeviceInfoText(rtnserial);
                        pagerInputActivity.getInformCtrl().SetMessage(sendmsg);
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
	                    String userIdInApplyInfo = pagerInputActivity.getInputApplyInfo().getUserId();
	                    if (!userIdInApplyInfo.equals(currentUserId) || firstTime) {
		                    if (!ValidateParams.nullOrEmpty(p_data.GetData())) {
			                    pagerInputActivity.getInputApplyInfo().setEmail(p_data.GetData());
		                    } else {
			                    pagerInputActivity.getInputApplyInfo().setEmail("");
		                    }
		                    pagerInputActivity.getInputApplyInfo().setReason("");
		                    pagerInputActivity.getInputApplyInfo().setUserId(currentUserId);
		                    pagerInputActivity.getInputApplyInfo().savePref(pagerInputActivity);
	                    }
                    }
	                if (StringList.m_str_ver_epsap.equalsIgnoreCase(p_data.GetKeyName())) {
		                if (!ValidateParams.nullOrEmpty(p_data.GetData())) {
			                pagerInputActivity.getInputApplyInfo().setVersionEpsap(p_data.GetData());
			                pagerInputActivity.getInputApplyInfo().savePref(pagerInputActivity);
		                }
	                }
                }
	            if (ValidateParams.nullOrEmpty(pagerInputActivity.getInputApplyInfo().getVersionEpsap())) {
		            pagerInputActivity.getInputApplyInfo().setVersionEpsap("");
		            pagerInputActivity.getInputApplyInfo().savePref(pagerInputActivity);
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
	        if (result) {
		        if (firstTime) {
			        firstTime = false;
		        }
		        pagerInputActivity.getInputApplyInfo().setUserId(txtUserId.getText().toString().trim());
		        pagerInputActivity.getInputApplyInfo().setPassword(txtPassword.getText().toString().trim());
		        pagerInputActivity.getInputApplyInfo().savePref(pagerInputActivity);
	        }
	        endConnection(result);
        }
    }
}
