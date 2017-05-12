package jp.co.soliton.keymanager.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import jp.co.soliton.keymanager.*;
import jp.co.soliton.keymanager.activity.CompleteApplyActivity;
import jp.co.soliton.keymanager.activity.ConfirmApplyActivity;
import jp.co.soliton.keymanager.activity.ViewPagerInputActivity;
import jp.co.soliton.keymanager.customview.DialogApplyMessage;
import jp.co.soliton.keymanager.customview.DialogApplyProgressBar;
import jp.co.soliton.keymanager.customview.DialogMenuCertDetail;
import jp.co.soliton.keymanager.dbalias.ElementApply;
import jp.co.soliton.keymanager.dbalias.ElementApplyManager;
import jp.co.soliton.keymanager.xmlparser.XmlDictionary;
import jp.co.soliton.keymanager.xmlparser.XmlPullParserAided;
import jp.co.soliton.keymanager.xmlparser.XmlStringData;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;

import static jp.co.soliton.keymanager.common.ControlPagesInput.*;
import static jp.co.soliton.keymanager.fragment.TabletBaseInputFragment.SUCCESSFUL;

/**
 * Created by nguyenducdat on 4/25/2017.
 */

public class TabletInputConfirmFragment extends TabletInputFragment {

	private int errorCount;
	private boolean reTry;
	private HashMap<String, Boolean> mapKey = new HashMap<>();
	private HttpConnectionCtrl conn;
	private XmlPullParserAided m_p_aided;
	TextView txtHostName;
	TextView txtPortName;
	TextView txtUserId;
	TextView txtStore;
	TextView txtEmail;
	TextView txtReason;
	TextView titleInput;
	private InputApplyInfo inputApplyInfo;
	private InformCtrl m_InformCtrl;
	private ElementApplyManager elementMgr;
	private String update_apply;

	TabletBaseInputFragment tabletBaseInputFragment;
	public static Fragment newInstance(Context context, TabletBaseInputFragment tabletBaseInputFragment) {
		TabletInputConfirmFragment f = new TabletInputConfirmFragment();
		f.tabletBaseInputFragment = tabletBaseInputFragment;
		return f;
	}

	@Override
	public void onSaveInstanceState(Bundle savedInstanceState) {
		super.onSaveInstanceState(savedInstanceState);
		getActivity().getSupportFragmentManager().putFragment(savedInstanceState, "tabletBaseInputFragment", tabletBaseInputFragment);
	}

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		if (savedInstanceState != null) {
			tabletBaseInputFragment = (TabletBaseInputFragment) getActivity().getSupportFragmentManager().getFragment(savedInstanceState,
					"tabletBaseInputFragment");
		}
		View view = inflater.inflate(R.layout.fragment_input_confirm_tablet, container, false);
		txtHostName = (TextView) view.findViewById(R.id.txtHostName);
		txtPortName = (TextView) view.findViewById(R.id.txtPortName);
		txtUserId = (TextView) view.findViewById(R.id.txtUserId);
		txtStore = (TextView) view.findViewById(R.id.txtStore);
		txtEmail = (TextView) view.findViewById(R.id.txtEmail);
		txtReason = (TextView) view.findViewById(R.id.txtReason);
		titleInput = (TextView) view.findViewById(R.id.titleInput);
		titleInput.setText(getString(R.string.confirm_content));
		return view;
	}

	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		inputApplyInfo = InputApplyInfo.getPref(getActivity());
		m_InformCtrl = tabletBaseInputFragment.getInformCtrl();
		if (tabletBaseInputFragment.progressDialog == null) {
			tabletBaseInputFragment.progressDialog = new DialogApplyProgressBar(getActivity());
		}
		if (elementMgr == null) {
			elementMgr = new ElementApplyManager(getActivity());
		}
		conn = new HttpConnectionCtrl(getActivity());
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
	 * Set action for controls and init value for items
	 */
	private void initValueControl() {
		inputApplyInfo = InputApplyInfo.getPref(getActivity());
		txtHostName.setText(inputApplyInfo.getHost());
		txtPortName.setText(inputApplyInfo.getSecurePort());
		if (InputBasePageFragment.TARGET_VPN.equals(inputApplyInfo.getPlace())) {
			txtStore.setText(getString(R.string.main_apid_vpn));
		} else {
			txtStore.setText(getString(R.string.main_apid_wifi));
		}
		txtUserId.setText(inputApplyInfo.getUserId());
		txtEmail.setText(inputApplyInfo.getEmail());
		txtReason.setText(inputApplyInfo.getReason());
		tabletBaseInputFragment.enableNext();
	}

	@Override
	public void nextAction() {
		processingApply();
	}

	/**
	 * Execute action apply
	 */
	private void processingApply() {
		tabletBaseInputFragment.progressDialog.show();
		tabletBaseInputFragment.setErroType(SUCCESSFUL);
		errorCount = 0;
		reTry = false;
		//open thread processing apply
		makeParameterApply();
		new ProcessApplyTask().execute();
	}

	/**
	 * Make parameter for request apply to server
	 *
	 * @return
	 */
	private void makeParameterApply() {
		String rtnserial;
		if (InputBasePageFragment.TARGET_WiFi.equals(inputApplyInfo.getPlace())) {
			rtnserial = XmlPullParserAided.GetUDID(getActivity());
		} else {
			rtnserial = XmlPullParserAided.GetVpnApid(getActivity());
		}
		// ログインメッセージ
		String message = "";
		String message_ma = "&" + "MailAddress=";	// #26556
		String message_dc = "&" + "Description=";	// #26556

		if (!nullOrEmpty(inputApplyInfo.getEmail())) {
			try {
				message_ma = message_ma + URLEncoder.encode(inputApplyInfo.getEmail(), "UTF-8");
			} catch (UnsupportedEncodingException ex) {
				LogCtrl.getInstance(getActivity()).loggerInfo("CompleteApplyActivity:makeParameterApply:Email:: " + "Message=" + ex
						.getMessage());
				Log.i(StringList.m_str_SKMTag, "apply:: " + "Message=" + ex.getMessage());
			}
		}
		if (!nullOrEmpty(inputApplyInfo.getReason())) {
			try {
				message_dc = message_dc + URLEncoder.encode(inputApplyInfo.getReason(), "UTF-8");
			} catch (UnsupportedEncodingException ex) {
				LogCtrl.getInstance(getActivity()).loggerInfo("CompleteApplyActivity:makeParameterApply:Reason:: " + "Message=" + ex
						.getMessage());
				Log.i(StringList.m_str_SKMTag, "apply:: " + "Message=" + ex.getMessage());
			}
		}
		message = "Action=apply" + message_ma + message_dc + "&" + StringList.m_strSerial + rtnserial;

		// 入力データを情報管理クラスへセットする
		m_InformCtrl.SetMessage(message);
	}

	/**
	 * Processing result after request to server
	 * @param result
	 */
	private void endConnection(boolean result) {
		tabletBaseInputFragment.progressDialog.dismiss();
		//request with result error
		int m_nErroType = tabletBaseInputFragment.getErroType();
		if (!result) {
			if (reTry) {
				new ProcessApplyTask().execute();
				return;
			}
			//show message error
			if (m_nErroType == ERR_ESPAP_NOT_CONNECT) {
				tabletBaseInputFragment.showMessage(getString(R.string.connect_not_epsap));
			}
			if (m_nErroType == ERR_NETWORK) {
				tabletBaseInputFragment.showMessage(getString(R.string.connect_failed));
			}
			if (m_nErroType == ERR_ESP_AP_STOP) {
				tabletBaseInputFragment.showMessage(getString(R.string.connect_failed));
			}
			if (m_nErroType == ERR_SESSION_TIMEOUT) {
				tabletBaseInputFragment.showMessage(getString(R.string.session_timeout));
			}
			if (m_nErroType == ERR_FORBIDDEN) {
				tabletBaseInputFragment.showMessage(getString(R.string.devicecheck_error));
			}
			if (m_nErroType == ERR_UNAUTHORIZED) {
				String str_unauth = getString(R.string.Unauthorized);
				tabletBaseInputFragment.showMessage(m_InformCtrl.GetRtn().substring(str_unauth.length()));
			}
			if (m_nErroType == ERR_COLON) {
				String str_err = getString(R.string.ERR);
				tabletBaseInputFragment.showMessage(m_InformCtrl.GetRtn().substring(str_err.length()));
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
			tabletBaseInputFragment.showMessage(getString(R.string.login_failed));
			return;
		}
		if (mapKey.containsKey(StringList.m_str_scepprofile) && !mapKey.get(StringList.m_str_scepprofile)) {
			DialogApplyMessage dlgMessage = new DialogApplyMessage(getActivity(), getString(R.string.registration_setting_invalid));
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
	 * Processing logic after apply successful
	 */
	private void applyFinish() {
		this.inputApplyInfo.setPassword(null);
		this.inputApplyInfo.savePref(getActivity());
		tabletBaseInputFragment.gotoCompleteApply();
	}

	
	private void saveElementApply() {
		if (!ValidateParams.nullOrEmpty(update_apply)) {
			elementMgr.updateStatus(ElementApply.STATUS_APPLY_CLOSED, update_apply);
		}

		String rtnserial;
		if (InputBasePageFragment.TARGET_WiFi.equals(inputApplyInfo.getPlace())) {
			rtnserial = "WIFI" + XmlPullParserAided.GetUDID(getActivity());
		} else {
			rtnserial = "APP" + XmlPullParserAided.GetVpnApid(getActivity());
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
		protected Boolean doInBackground(Void... params) {
			LogCtrl logCtrlAsyncTask = LogCtrl.getInstance(getActivity());
			boolean ret;
			//Call to server
			ret = conn.RunHttpApplyCerUrlConnection(m_InformCtrl);
			//Parse result
			if (!ret) {
				if (errorCount > 10) {
					tabletBaseInputFragment.setErroType(ERR_ESPAP_NOT_CONNECT);
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
				tabletBaseInputFragment.setErroType(ERR_NETWORK);
				return false;
			}
			if (m_InformCtrl.GetRtn().startsWith("OK")) {
				tabletBaseInputFragment.setErroType(RET_ESP_AP_OK);
				return true;
			}
			if (m_InformCtrl.GetRtn().startsWith("NG")) {
				logCtrlAsyncTask.loggerError("ConfirmApplyActivity:ProcessApplyTask:doInBackground NG");
				tabletBaseInputFragment.setErroType(ERR_LOGIN_FAIL);
				return false;
			}
			if (m_InformCtrl.GetRtn().startsWith("EPS-ap Service is stopped.")) {
				logCtrlAsyncTask.loggerError("ConfirmApplyActivity:ProcessApplyTask:doInBackground EPS-ap Service is stopped.");
				tabletBaseInputFragment.setErroType(ERR_ESP_AP_STOP);
				return false;
			}
			if (m_InformCtrl.GetRtn().startsWith("No session")) {
				logCtrlAsyncTask.loggerError("ConfirmApplyActivity:ProcessApplyTask:doInBackground No session.");
				tabletBaseInputFragment.setErroType(ERR_SESSION_TIMEOUT);
				return false;
			}
			if (m_InformCtrl.GetRtn().startsWith(getText(R.string.Forbidden).toString())) {
				logCtrlAsyncTask.loggerError("ConfirmApplyActivity:ProcessApplyTask:doInBackground Forbidden.");
				tabletBaseInputFragment.setErroType(ERR_FORBIDDEN);
				return false;
			}
			if (m_InformCtrl.GetRtn().startsWith(getText(R.string.Unauthorized).toString())) {
				logCtrlAsyncTask.loggerError("ConfirmApplyActivity:ProcessApplyTask:doInBackground Unauthorized.");
				tabletBaseInputFragment.setErroType(ERR_UNAUTHORIZED);
				return false;
			}
			if (m_InformCtrl.GetRtn().length() > 4 && m_InformCtrl.GetRtn().startsWith(getString(R.string.ERR).toString())) {
				logCtrlAsyncTask.loggerError("ConfirmApplyActivity:ProcessApplyTask:doInBackground ERR:");
				tabletBaseInputFragment.setErroType(ERR_COLON);
				return false;
			}
			// 取得XMLのパーサー
			m_p_aided = new XmlPullParserAided(getActivity(), m_InformCtrl.GetRtn(), 2);    // 最上位dictの階層は2になる
			ret = m_p_aided.TakeApartUserAuthenticationResponse(m_InformCtrl);
			if (ret == false) {
				logCtrlAsyncTask.loggerError("ConfirmApplyActivity:ProcessApplyTask:doInBackground TakeApartDevice false");
				reTry = false;
				tabletBaseInputFragment.setErroType(ERR_NETWORK);
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