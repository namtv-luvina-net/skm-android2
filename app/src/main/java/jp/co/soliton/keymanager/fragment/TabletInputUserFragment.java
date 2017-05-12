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
import jp.co.soliton.keymanager.activity.CompleteConfirmApplyActivity;
import jp.co.soliton.keymanager.activity.ViewPagerInputActivity;
import jp.co.soliton.keymanager.customview.DialogApplyProgressBar;
import jp.co.soliton.keymanager.customview.DialogMessageTablet;
import jp.co.soliton.keymanager.dbalias.ElementApply;
import jp.co.soliton.keymanager.dbalias.ElementApplyManager;
import jp.co.soliton.keymanager.xmlparser.XmlDictionary;
import jp.co.soliton.keymanager.xmlparser.XmlPullParserAided;
import jp.co.soliton.keymanager.xmlparser.XmlStringData;

import java.util.List;

import static jp.co.soliton.keymanager.fragment.TabletBaseInputFragment.*;

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
	TabletBaseInputFragment tabletBaseInputFragment;
	public static Fragment newInstance(Context context, TabletBaseInputFragment tabletBaseInputFragment) {
		TabletInputUserFragment f = new TabletInputUserFragment();
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
		View view = inflater.inflate(R.layout.fragment_input_user_tablet, container, false);
		txtUserId = (EditText) view.findViewById(R.id.txtUserId);
		txtPassword = (EditText) view.findViewById(R.id.txtPassword);
		titleInput = (TextView) view.findViewById(R.id.titleInput);
		titleInput.setText(getString(R.string.input_id_and_password));
		return view;
	}

	@Override
	public void onAttach(Context context) {
		super.onAttach(context);
		if (context instanceof ViewPagerInputActivity) {
			if (tabletBaseInputFragment.progressDialog == null) {
				tabletBaseInputFragment.progressDialog = new DialogApplyProgressBar(getActivity());
			}
			if (elementMgr == null) {
				elementMgr = new ElementApplyManager(getActivity());
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
					if (!nullOrEmpty(txtUserId.getText().toString()) && !nullOrEmpty(txtPassword.getText().toString())) {
						nextAction();
						return true;
					}
				}
				return false;
			}
		});
		if (!nullOrEmpty(tabletBaseInputFragment.getInputApplyInfo().getUserId())) {
			txtPassword.requestFocus();
			InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.toggleSoftInput(InputMethodManager.SHOW_FORCED,0);
		}
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
	 * init value for controls
	 */
	private void initValueControl() {
		if (tabletBaseInputFragment == null || txtUserId == null || txtPassword == null) {
			return;
		}
		if (!nullOrEmpty(tabletBaseInputFragment.getInputApplyInfo().getUserId())) {
			txtUserId.setText(tabletBaseInputFragment.getInputApplyInfo().getUserId());
		}
		if (!nullOrEmpty(tabletBaseInputFragment.getInputApplyInfo().getPassword())) {
			txtPassword.setText(tabletBaseInputFragment.getInputApplyInfo().getPassword());
		}
		setStatusControl();
	}

	/**
	 * Set status for next back button
	 */
	private void setStatusControl() {
		if (tabletBaseInputFragment.getCurrentPage() != 3) {
			return;
		}
		if (nullOrEmpty(txtUserId.getText().toString()) || nullOrEmpty(txtPassword.getText().toString())) {
			tabletBaseInputFragment.disableNext();
		} else {
			tabletBaseInputFragment.enableNext();
		}
	}


	@Override
	public void nextAction() {
		if (!ValidateParams.isValidUserID(txtUserId.getText().toString().trim())) {
			tabletBaseInputFragment.showMessage(getString(R.string.user_id_is_invalid));
			return;
		}
		String userId = txtUserId.getText().toString().trim();
		String password = txtPassword.getText().toString();
		tabletBaseInputFragment.getInputApplyInfo().setUserId(userId);
		tabletBaseInputFragment.getInputApplyInfo().setPassword(password);
		tabletBaseInputFragment.getInputApplyInfo().savePref(getActivity());
		//make parameter|
		String place = tabletBaseInputFragment.getInputApplyInfo().getPlace();
		boolean ret = tabletBaseInputFragment.controlPagesInput.makeParameterLogon(userId, password, place,
				tabletBaseInputFragment.getInformCtrl() );
		if (!ret) {
			tabletBaseInputFragment.showMessage(getString(R.string.connect_failed));
			return;
		}
		tabletBaseInputFragment.progressDialog.show();
		// グレーアウト
//		setButtonRunnable(false);
		if (nullOrEmpty(tabletBaseInputFragment.getInformCtrl().GetURL())) {
			String url = String.format("%s:%s", tabletBaseInputFragment.getInputApplyInfo().getHost(), tabletBaseInputFragment.getInputApplyInfo().getSecurePort());
			tabletBaseInputFragment.getInformCtrl().SetURL(url);
		}
		tabletBaseInputFragment.getInformCtrl().SetCookie(null);
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
		tabletBaseInputFragment.progressDialog.dismiss();
		if (result) {
			//check action next
			InputApplyInfo inputApplyInfo = tabletBaseInputFragment.getInputApplyInfo();
			InformCtrl informCtrl = tabletBaseInputFragment.getInformCtrl();
			if (isEnroll) {
				//save element apply
				saveElementApply();
				inputApplyInfo.setPassword(null);
				inputApplyInfo.savePref(getActivity());
				String id = String.valueOf(elementMgr.getIdElementApply(inputApplyInfo.getHost(),
						inputApplyInfo.getUserId()));
				ElementApply element = elementMgr.getElementApply(id);
				tabletBaseInputFragment.gotoCompleteApply(informCtrl, element);
			} else {
				if (isSubmitted) {
					saveElementApply();
					Intent intent = new Intent(getActivity(), CompleteConfirmApplyActivity.class);
					getActivity().finish();
					intent.putExtra("STATUS_APPLY", ElementApply.STATUS_APPLY_PENDING);
					String id = String.valueOf(elementMgr.getIdElementApply(inputApplyInfo.getHost(), inputApplyInfo
							.getUserId()));
					ElementApply element = elementMgr.getElementApply(id);
					intent.putExtra("ELEMENT_APPLY", element);
					intent.putExtra(StringList.m_str_InformCtrl, informCtrl);
					startActivity(intent);
				} else {
					tabletBaseInputFragment.gotoPage(4);
				}
			}
		} else {
			//show error message
			int m_nErroType = tabletBaseInputFragment.getErroType();
			String strRtn = tabletBaseInputFragment.getInformCtrl().GetRtn();
			if (m_nErroType == ERR_FORBIDDEN) {
				String str_forbidden = getString(R.string.Forbidden);
				tabletBaseInputFragment.showMessage(strRtn.substring(str_forbidden.length()));
			} else if (m_nErroType == ERR_UNAUTHORIZED) {
				String str_unauth = getString(R.string.Unauthorized);
				tabletBaseInputFragment.showMessage(strRtn.substring(str_unauth
						.length()));
			} else if (m_nErroType == ERR_COLON) {
				String str_err = getString(R.string.ERR);
				tabletBaseInputFragment.showMessage(strRtn.substring(str_err.length()));
			} else if (m_nErroType == ERR_LOGIN_FAIL) {
				tabletBaseInputFragment.showMessage(getString(R.string.login_failed), new DialogMessageTablet.OnOkDismissMessageListener() {
					@Override
					public void onOkDismissMessage() {
						txtPassword.setText("");
						txtPassword.requestFocus();
						InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
						imm.toggleSoftInput(InputMethodManager.SHOW_FORCED,0);
					}
				});
			} else {
				tabletBaseInputFragment.showMessage(getString(R.string.connect_failed));
			}
		}
	}

	private void saveElementApply() {
		if (elementMgr == null) {
			elementMgr = new ElementApplyManager(getActivity());
		}
		String rtnserial;
		if (InputBasePageFragment.TARGET_WiFi.equals(tabletBaseInputFragment.getInputApplyInfo().getPlace())) {
			rtnserial = "WIFI" + XmlPullParserAided.GetUDID(getActivity());
		} else {
			rtnserial = "APP" + XmlPullParserAided.GetVpnApid(getActivity());
		}
		ElementApply elementApply = new ElementApply();
		InputApplyInfo inputApplyInfo = tabletBaseInputFragment.getInputApplyInfo();
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
			boolean ret = conn.RunHttpApplyLoginUrlConnection(tabletBaseInputFragment.getInformCtrl());

			if (ret == false) {
				logCtrlAsyncTask.loggerError("LogonApplyTask Network error");
				tabletBaseInputFragment.setErroType(ERR_NETWORK);
				return false;
			}
			// ログイン結果
			String strRtn = tabletBaseInputFragment.getInformCtrl().GetRtn();
			if (strRtn.startsWith(getText(R.string.Forbidden).toString())) {
				logCtrlAsyncTask.loggerError("LogonApplyTask Forbidden.");
				tabletBaseInputFragment.setErroType(ERR_FORBIDDEN);
				return false;
			} else if (strRtn.startsWith(getText(R.string.Unauthorized).toString())) {
				logCtrlAsyncTask.loggerError("LogonApplyTask Unauthorized.");
				tabletBaseInputFragment.setErroType(ERR_UNAUTHORIZED);
				return false;
			} else if (strRtn.startsWith(getText(R.string.ERR).toString())) {
				logCtrlAsyncTask.loggerError("LogonApplyTask ERR:");
				tabletBaseInputFragment.setErroType(ERR_COLON);
				return false;
			} else if (strRtn.startsWith("NG")) {
				logCtrlAsyncTask.loggerError("LogonApplyTask NG");
				tabletBaseInputFragment.setErroType(ERR_LOGIN_FAIL);
				return false;
			}
			// 取得したCookieをログイン時のCookieとして保持する.
			tabletBaseInputFragment.getInformCtrl().SetLoginCookie(tabletBaseInputFragment.getInformCtrl().GetCookie());
			///////////////////////////////////////////////////
			// 認証応答の解析(Enroll応答のときの対応を流用できるはず)
			///////////////////////////////////////////////////
			// 取得XMLのパーサー
			XmlPullParserAided m_p_aided = new XmlPullParserAided(getActivity(), strRtn, 2);
			// 最上位dictの階層は2になる

			ret = m_p_aided.TakeApartUserAuthenticationResponse(tabletBaseInputFragment.getInformCtrl());
			if (ret == false) {
				logCtrlAsyncTask.loggerError("LogonApplyTask-- TakeApartDevice false");
				tabletBaseInputFragment.setErroType(ERR_NETWORK);
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
						if (InputBasePageFragment.TARGET_WiFi.equals(tabletBaseInputFragment.getInputApplyInfo().getPlace())) {
							rtnserial = XmlPullParserAided.GetUDID(getActivity());
						} else {
							rtnserial = XmlPullParserAided.GetVpnApid(getActivity());
						}
						String sendmsg = m_p_aided.DeviceInfoText(rtnserial);
						tabletBaseInputFragment.getInformCtrl().SetMessage(sendmsg);
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
							tabletBaseInputFragment.getInputApplyInfo().setEmail(p_data.GetData());
						}
					}
				}
			}
			////////////////////////////////////////////////////////////////////////////
			// 大項目1. ログイン終了 =========>
			////////////////////////////////////////////////////////////////////////////
			tabletBaseInputFragment.setErroType(SUCCESSFUL);
			return ret;
		}

		@Override
		protected void onPostExecute(Boolean result) {
			super.onPostExecute(result);
			endConnection(result);
		}
	}
}