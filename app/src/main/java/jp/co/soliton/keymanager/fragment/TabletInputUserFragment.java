package jp.co.soliton.keymanager.fragment;

import android.content.Context;
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
import jp.co.soliton.keymanager.customview.DialogMessageTablet;
import jp.co.soliton.keymanager.dbalias.ElementApply;
import jp.co.soliton.keymanager.dbalias.ElementApplyManager;
import jp.co.soliton.keymanager.xmlparser.XmlDictionary;
import jp.co.soliton.keymanager.xmlparser.XmlPullParserAided;
import jp.co.soliton.keymanager.xmlparser.XmlStringData;

import java.util.List;

import static jp.co.soliton.keymanager.common.ErrorNetwork.*;

/**
 * Created by nguyenducdat on 4/25/2017.
 */

public class TabletInputUserFragment extends TabletInputFragment {
	private EditText txtUserId;
	private EditText txtPassword;
	private TextView titleInput;
	private boolean isEnroll;
	private boolean challenge;
	private ElementApplyManager elementMgr;
	private boolean isSubmitted;
	TabletAbtractInputFragment tabletAbtractInputFragment;
	private String id_update;

	public static Fragment newInstance(Context context, TabletAbtractInputFragment tabletAbtractInputFragment, String id) {
		TabletInputUserFragment f = new TabletInputUserFragment();
		f.tabletAbtractInputFragment = tabletAbtractInputFragment;
		f.id_update = id;
		return f;
	}

	public static Fragment newInstance(Context context, TabletAbtractInputFragment tabletAbtractInputFragment) {
		TabletInputUserFragment f = new TabletInputUserFragment();
		f.tabletAbtractInputFragment = tabletAbtractInputFragment;
		return f;
	}

	@Override
	public void onSaveInstanceState(Bundle savedInstanceState) {
		super.onSaveInstanceState(savedInstanceState);
		getActivity().getSupportFragmentManager().putFragment(savedInstanceState, TAG_TABLET_BASE_INPUT_FRAGMENT,
				tabletAbtractInputFragment);
	}

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		if (savedInstanceState != null) {
			tabletAbtractInputFragment = (TabletAbtractInputFragment) getActivity().getSupportFragmentManager().getFragment
					(savedInstanceState, TAG_TABLET_BASE_INPUT_FRAGMENT);
		}
		View view = inflater.inflate(R.layout.fragment_input_user_tablet, container, false);
		txtUserId = (EditText) view.findViewById(R.id.txtUserId);
		txtPassword = (EditText) view.findViewById(R.id.txtPassword);
		titleInput = (TextView) view.findViewById(R.id.titleInput);
		if (ValidateParams.nullOrEmpty(id_update)) {
			titleInput.setText(getString(R.string.input_user_id_and_password));
		}else {
			titleInput.setText(getString(R.string.input_password));
		}
		return view;
	}

	@Override
	public void onAttach(Context context) {
		super.onAttach(context);
		if (elementMgr == null) {
			elementMgr = new ElementApplyManager(getActivity());
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

	@Override
	public void onResume() {
		super.onResume();
		initValueControl();
	}

	/**
	 * init value for controls
	 */
	private void initValueControl() {
		if (tabletAbtractInputFragment == null || txtUserId == null || txtPassword == null) {
			return;
		}
		if (!ValidateParams.nullOrEmpty(id_update)) {
			txtUserId.setTag(txtUserId.getKeyListener());
			txtUserId.setKeyListener(null);
			txtUserId.setBackgroundColor(android.R.color.transparent);
		}
		if (!nullOrEmpty(tabletAbtractInputFragment.getInputApplyInfo().getUserId())) {
			txtUserId.setText(tabletAbtractInputFragment.getInputApplyInfo().getUserId());
		}
		if (!nullOrEmpty(tabletAbtractInputFragment.getInputApplyInfo().getPassword())) {
			txtPassword.setText(tabletAbtractInputFragment.getInputApplyInfo().getPassword());
		}
		setStatusControl();
	}

	/**
	 * Set status for next back button
	 */
	private void setStatusControl() {
		if (nullOrEmpty(txtUserId.getText().toString()) || nullOrEmpty(txtPassword.getText().toString())) {
			tabletAbtractInputFragment.disableNext();
		} else {
			tabletAbtractInputFragment.enableNext();
		}
	}


	@Override
	public void nextAction() {
		if (!ValidateParams.isValidUserID(txtUserId.getText().toString().trim())) {
			tabletAbtractInputFragment.showMessage(getString(R.string.user_id_is_invalid));
			return;
		}
		String userId = txtUserId.getText().toString().trim();
		String password = txtPassword.getText().toString();
		tabletAbtractInputFragment.getInputApplyInfo().setUserId(userId);
		tabletAbtractInputFragment.getInputApplyInfo().setPassword(password);
		tabletAbtractInputFragment.getInputApplyInfo().savePref(getActivity());
		//make parameter|
		String place = tabletAbtractInputFragment.getInputApplyInfo().getPlace();
		boolean ret = tabletAbtractInputFragment.controlPagesInput.makeParameterLogon(userId, password, place,
				tabletAbtractInputFragment.getInformCtrl());
		if (!ret) {
			tabletAbtractInputFragment.showMessage(getString(R.string.connect_failed));
			return;
		}
		tabletAbtractInputFragment.getProgressDialog().show();
		// グレーアウト
//		setButtonRunnable(false);
		if (nullOrEmpty(tabletAbtractInputFragment.getInformCtrl().GetURL())) {
			String url = String.format("%s:%s", tabletAbtractInputFragment.getInputApplyInfo().getHost(),
					tabletAbtractInputFragment.getInputApplyInfo().getSecurePort());
			tabletAbtractInputFragment.getInformCtrl().SetURL(url);
		}
		tabletAbtractInputFragment.getInformCtrl().SetCookie(null);
		isEnroll = false;
		challenge = false;
		//open thread logon to server
		new LogonApplyTask().execute();
	}

	/**
	 * Processing result from server return back
	 *
	 * @param result
	 */
	private void endConnection(boolean result) {
		tabletAbtractInputFragment.getProgressDialog().dismiss();
		if (result) {
			//check action next
			InputApplyInfo inputApplyInfo = tabletAbtractInputFragment.getInputApplyInfo();
			InformCtrl informCtrl = tabletAbtractInputFragment.getInformCtrl();
			if (isEnroll) {
				//save element apply
				saveElementApply();
				inputApplyInfo.setPassword(null);
				inputApplyInfo.savePref(getActivity());
				ElementApply element;
				if (ValidateParams.nullOrEmpty(id_update)) {
					String id = String.valueOf(elementMgr.getIdElementApply(inputApplyInfo.getHost(),
							inputApplyInfo.getUserId()));
					element = elementMgr.getElementApply(id);
				} else {
					String host = (inputApplyInfo.getHost());
					String userId = (inputApplyInfo.getUserId());
					id_update = String.valueOf(elementMgr.getIdElementApply(host, userId));
					element = elementMgr.getElementApply(id_update);
				}
				tabletAbtractInputFragment.gotoCompleteApply(informCtrl, element);
			} else {
				if (isSubmitted) {
					saveElementApply();
					ElementApply element;
					if (ValidateParams.nullOrEmpty(id_update)) {
						String id = String.valueOf(elementMgr.getIdElementApply(inputApplyInfo.getHost(),
								inputApplyInfo.getUserId()));
						element = elementMgr.getElementApply(id);
					} else {
						element = elementMgr.getElementApply(id_update);
					}
					tabletAbtractInputFragment.gotoCompleteConfirmApplyFragment(ElementApply.STATUS_APPLY_PENDING,
							element, informCtrl);
				} else {
					tabletAbtractInputFragment.gotoNextPage();
				}
			}
		} else {
			//show error message
			int m_nErroType = tabletAbtractInputFragment.getErroType();
			String strRtn = tabletAbtractInputFragment.getInformCtrl().GetRtn();
			if (m_nErroType == ERR_FORBIDDEN) {
				String str_forbidden = getString(R.string.Forbidden);
				tabletAbtractInputFragment.showMessage(strRtn.substring(str_forbidden.length()));
			} else if (m_nErroType == ERR_UNAUTHORIZED) {
				String str_unauth = getString(R.string.Unauthorized);
				tabletAbtractInputFragment.showMessage(strRtn.substring(str_unauth
						.length()));
			} else if (m_nErroType == ERR_COLON) {
				String str_err = getString(R.string.ERR);
				tabletAbtractInputFragment.showMessage(strRtn.substring(str_err.length()));
			} else if (m_nErroType == ERR_LOGIN_FAIL) {
				tabletAbtractInputFragment.showMessage(getString(R.string.login_failed), new DialogMessageTablet
						.OnOkDismissMessageListener() {
					@Override
					public void onOkDismissMessage() {
						txtPassword.setText("");
						txtPassword.requestFocus();
						InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context
								.INPUT_METHOD_SERVICE);
						imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
					}
				});
			} else {
				tabletAbtractInputFragment.showMessage(getString(R.string.connect_failed));
			}
		}
	}

	private void saveElementApply() {
		if (elementMgr == null) {
			elementMgr = new ElementApplyManager(getActivity());
		}
		if (!ValidateParams.nullOrEmpty(id_update)) {
			elementMgr.updateStatus(ElementApply.STATUS_APPLY_CLOSED, id_update);
		}
		String rtnserial;
		if (InputBasePageFragment.TARGET_WiFi.equals(tabletAbtractInputFragment.getInputApplyInfo().getPlace())) {
			rtnserial = "WIFI" + XmlPullParserAided.GetUDID(getActivity());
		} else {
			rtnserial = "APP" + XmlPullParserAided.GetVpnApid(getActivity());
		}
		ElementApply elementApply = new ElementApply();
		InputApplyInfo inputApplyInfo = tabletAbtractInputFragment.getInputApplyInfo();
		elementApply.setHost(inputApplyInfo.getHost());
		elementApply.setPort(inputApplyInfo.getPort());
		elementApply.setPortSSL(inputApplyInfo.getSecurePort());
		elementApply.setUserId(inputApplyInfo.getUserId());
		elementApply.setPassword(inputApplyInfo.getPassword());
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
			LogCtrl logCtrlAsyncTask = LogCtrl.getInstance(getActivity());
			HttpConnectionCtrl conn = new HttpConnectionCtrl(getActivity());
			boolean ret = conn.RunHttpApplyLoginUrlConnection(tabletAbtractInputFragment.getInformCtrl());

			if (ret == false) {
				logCtrlAsyncTask.loggerError("LogonApplyTask Network error");
				tabletAbtractInputFragment.setErroType(ERR_NETWORK);
				return false;
			}
			// ログイン結果
			String strRtn = tabletAbtractInputFragment.getInformCtrl().GetRtn();
			if (strRtn.startsWith(getText(R.string.Forbidden).toString())) {
				logCtrlAsyncTask.loggerError("LogonApplyTask Forbidden.");
				tabletAbtractInputFragment.setErroType(ERR_FORBIDDEN);
				return false;
			} else if (strRtn.startsWith(getText(R.string.Unauthorized).toString())) {
				logCtrlAsyncTask.loggerError("LogonApplyTask Unauthorized.");
				tabletAbtractInputFragment.setErroType(ERR_UNAUTHORIZED);
				return false;
			} else if (strRtn.startsWith(getText(R.string.ERR).toString())) {
				logCtrlAsyncTask.loggerError("LogonApplyTask ERR:");
				tabletAbtractInputFragment.setErroType(ERR_COLON);
				return false;
			} else if (strRtn.startsWith("NG")) {
				logCtrlAsyncTask.loggerError("LogonApplyTask NG");
				tabletAbtractInputFragment.setErroType(ERR_LOGIN_FAIL);
				return false;
			}
			// 取得したCookieをログイン時のCookieとして保持する.
			tabletAbtractInputFragment.getInformCtrl().SetLoginCookie(tabletAbtractInputFragment.getInformCtrl().GetCookie());
			///////////////////////////////////////////////////
			// 認証応答の解析(Enroll応答のときの対応を流用できるはず)
			///////////////////////////////////////////////////
			// 取得XMLのパーサー
			XmlPullParserAided m_p_aided = new XmlPullParserAided(getActivity(), strRtn, 2);
			// 最上位dictの階層は2になる

			ret = m_p_aided.TakeApartUserAuthenticationResponse(tabletAbtractInputFragment.getInformCtrl());
			if (ret == false) {
				logCtrlAsyncTask.loggerError("LogonApplyTask-- TakeApartDevice false");
				tabletAbtractInputFragment.setErroType(ERR_NETWORK);
				return false;
			}
			//parse xml return from server
			XmlDictionary xmldict = m_p_aided.GetDictionary();
			if (xmldict != null) {
				List<XmlStringData> str_list;
				str_list = xmldict.GetArrayString();
				for (int i = 0; str_list.size() > i; i++) {
					// config情報に従って、処理を行う.
					XmlStringData p_data = str_list.get(i);
					// 要素タイプ(string:1, data=2, date=3, real=4, integer=5, true=6, false=7)
					if (StringList.m_str_isEnroll.equalsIgnoreCase(p_data.GetKeyName())) {
						isEnroll = true;
						String rtnserial = "";
						if (InputBasePageFragment.TARGET_WiFi.equals(tabletAbtractInputFragment.getInputApplyInfo().getPlace()
						)) {
							rtnserial = XmlPullParserAided.GetUDID(getActivity());
						} else {
							rtnserial = XmlPullParserAided.GetVpnApid(getActivity());
						}
						String sendmsg = m_p_aided.DeviceInfoText(rtnserial);
						tabletAbtractInputFragment.getInformCtrl().SetMessage(sendmsg);
					}
					if (StringList.m_str_issubmitted.equalsIgnoreCase(p_data.GetKeyName())) {
						if (6 == p_data.GetType()) {
							isSubmitted = true;
						}
					}
					if (StringList.m_str_scep_challenge.equalsIgnoreCase(p_data.GetKeyName())) {
						challenge = (6 == p_data.GetType());
					}
					if (StringList.m_str_mailaddress.equalsIgnoreCase(p_data.GetKeyName())) {
						if (!ValidateParams.nullOrEmpty(p_data.GetData())) {
							tabletAbtractInputFragment.getInputApplyInfo().setEmail(p_data.GetData());
						} else {
							tabletAbtractInputFragment.getInputApplyInfo().setEmail("");
						}
					}
				}
			}
			////////////////////////////////////////////////////////////////////////////
			// 大項目1. ログイン終了 =========>
			////////////////////////////////////////////////////////////////////////////
			tabletAbtractInputFragment.setErroType(SUCCESSFUL);
			return ret;
		}

		@Override
		protected void onPostExecute(Boolean result) {
			super.onPostExecute(result);
			endConnection(result);
		}
	}
}