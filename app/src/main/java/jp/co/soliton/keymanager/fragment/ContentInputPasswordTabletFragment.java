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
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import jp.co.soliton.keymanager.*;
import jp.co.soliton.keymanager.activity.CompleteConfirmApplyActivity;
import jp.co.soliton.keymanager.activity.InputPasswordTabletActivity;
import jp.co.soliton.keymanager.common.DetectsSoftKeyboard;
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

/**
 * Created by nguyenducdat on 4/25/2017.
 */

public class ContentInputPasswordTabletFragment extends Fragment implements DetectsSoftKeyboard.DetectsListenner{

	Activity activity;
	private TextView txtUserId;
	private EditText txtPassword;
	private Button btnNext;
	private Button btnBack;
	private int m_nErroType;
	private DialogApplyProgressBar progressDialog;
	private InformCtrl m_InformCtrl;
	private ElementApplyManager elementMgr;
	private ElementApply element;
	LogCtrl logCtrl;
	private int status;
	boolean isShowingKeyboard;

	public static Fragment newInstance() {
		ContentInputPasswordTabletFragment f = new ContentInputPasswordTabletFragment();
		return f;
	}

	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		elementMgr = new ElementApplyManager(getActivity());
		m_InformCtrl = new InformCtrl();
		if (progressDialog == null) {
			progressDialog = new DialogApplyProgressBar(getActivity());
		}
		logCtrl = logCtrl.getInstance(getActivity());
	}

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_input_password_tablet, container, false);
		txtUserId = (TextView) view.findViewById(R.id.txtUserId);
		txtPassword = (EditText) view.findViewById(R.id.txtPassword);
		btnNext = (Button) view.findViewById(R.id.btnNext);
		btnBack = (Button) view.findViewById(R.id.btnBack);
		DetectsSoftKeyboard.addListenner(view, this);
		return view;
	}

	@Override
	public void onAttach(Context context) {
		super.onAttach(context);
		this.activity = (Activity) context;
	}

	@Override
	public void onResume() {
		super.onResume();
		setupControl();
	}

	public void updateUserId(String userId) {
		txtUserId.setText(userId);
	}

	private void setupControl() {
		String id = ((InputPasswordTabletActivity)getActivity()).getId();
		if (!ValidateParams.nullOrEmpty(id)) {
			element = elementMgr.getElementApply(id);
			updateUserId(element.getUserId());
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
		btnNext.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				clickNext(v);
			}
		});
		btnBack.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				clickBack(v);
			}
		});
	}

	private void setEnableNextButton() {
		if (ValidateParams.nullOrEmpty(txtPassword.getText().toString())) {
			disableNext();
		} else {
			enableNext();
		}
	}

	private void disableNext() {
		btnNext.setEnabled(false);
		if(android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.JELLY_BEAN) {
			btnNext.setBackgroundDrawable( getResources().getDrawable(R.drawable.background_btn_disable) );
		} else {
			btnNext.setBackground( getResources().getDrawable(R.drawable.background_btn_disable));
		}
	}

	private void enableNext() {
		btnNext.setEnabled(true);
		if(android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.JELLY_BEAN) {
			btnNext.setBackgroundDrawable( getResources().getDrawable(R.drawable.background_btn_ctrl_apid) );
		} else {
			btnNext.setBackground( getResources().getDrawable(R.drawable.background_btn_ctrl_apid));
		}
	}

	private void clickNext(View v) {
		logCtrl.loggerInfo("CertLoginAcrivity::onClick  " + "Push LoginButton");
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

	private void clickBack(View v){
		((InputPasswordTabletActivity)getActivity()).btnBackClick(v);
	}

	/**
	 * Make parameter for logon to server
	 * @return
	 */
	private boolean makeParameterLogon() {
		String strUserid = txtUserId.getText().toString().trim();
		String strPasswd = txtPassword.getText().toString();
		String rtnserial = element.getTarger().replace("WIFI", "").replace("APP", "");
		String str_url = m_InformCtrl.GetURL();
		// ログインメッセージ
		// URLEncodeが必須 <http://wada811.blog.fc2.com/?tag=URL%E3%82%A8%E3%83%B3%E3%82%B3%E3%83%BC%E3%83%89>参照
		String message;
		try {
			message = "Action=logon" + "&" + StringList.m_strUserID + URLEncoder.encode(strUserid, "UTF-8") +
					"&" + StringList.m_strPassword + URLEncoder.encode(strPasswd, "UTF-8") +
					"&" + StringList.m_strSerial + rtnserial;

			logCtrl.loggerInfo("http_user_login-- " + "USER ID=" + strUserid);
			logCtrl.loggerInfo("http_user_login-- " + "URL=" + str_url);
		} catch (UnsupportedEncodingException ex) {
			logCtrl.loggerError("InputPasswordActivity::makeParameterLogon UnsupportedEncodingException "+ ex.toString());
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
			logCtrl.loggerError("InputPasswordActivity::makeParameterDrop1 "+ m_InformCtrl.GetURL());
			logCtrl.loggerError("InputPasswordActivity::makeParameterDrop2"+ m_InformCtrl.GetUserID());
		} catch (Exception ex) {
			logCtrl.loggerError("InputPasswordActivity::makeParameterDrop3 " + ex.toString());
			Log.i(StringList.m_str_SKMTag, "logon:: " + "Message=" + ex.getMessage());
			return false;
		}
		// 入力データを情報管理クラスへセットする
		m_InformCtrl.SetMessage(message);
		return true;
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
			HttpConnectionCtrl conn = new HttpConnectionCtrl(getActivity());
			boolean ret = conn.RunHttpApplyLoginUrlConnection(m_InformCtrl);
			LogCtrl logCtrlAsyncTask = LogCtrl.getInstance(getActivity());
			if (ret == false) {
				logCtrlAsyncTask.loggerError("LogonApplyTask Network error");
				m_nErroType = InputBasePageFragment.ERR_NETWORK;
				return false;
			}
			// ログイン結果
			if (m_InformCtrl.GetRtn().startsWith(getText(R.string.Forbidden).toString())) {
				logCtrlAsyncTask.loggerError("LogonApplyTask Forbidden.");
				m_nErroType = InputBasePageFragment.ERR_FORBIDDEN;
				return false;
			} else if (m_InformCtrl.GetRtn().startsWith(getText(R.string.Unauthorized).toString())) {
				logCtrlAsyncTask.loggerError("LogonApplyTask Unauthorized.");
				m_nErroType = InputBasePageFragment.ERR_UNAUTHORIZED;
				return false;
			} else if (m_InformCtrl.GetRtn().startsWith(getText(R.string.ERR).toString())) {
				logCtrlAsyncTask.loggerError("LogonApplyTask ERR:");
				m_nErroType = InputBasePageFragment.ERR_COLON;
				return false;
			} else if (m_InformCtrl.GetRtn().startsWith("NG")) {
				logCtrlAsyncTask.loggerError("LogonApplyTask NG");
				m_nErroType = InputBasePageFragment.ERR_LOGIN_FAIL;
				return false;
			}
			// 取得したCookieをログイン時のCookieとして保持する.
			m_InformCtrl.SetLoginCookie(m_InformCtrl.GetCookie());
			///////////////////////////////////////////////////
			// 認証応答の解析(Enroll応答のときの対応を流用できるはず)
			///////////////////////////////////////////////////
			// 取得XMLのパーサー
			XmlPullParserAided m_p_aided = new XmlPullParserAided(getActivity(), m_InformCtrl.GetRtn(), 2);    // 最上位dictの階層は2になる

			ret = m_p_aided.TakeApartUserAuthenticationResponse(m_InformCtrl);
			if (ret == false) {
				logCtrlAsyncTask.loggerError("LogonApplyTask-- TakeApartDevice false");
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
				String sendmsg = m_p_aided.DeviceInfoText(element.getTarger().replace("WIFI", "").replace("APP", ""));
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
			LogCtrl logCtrlAsyncTask = LogCtrl.getInstance(getActivity());
			HttpConnectionCtrl conn = new HttpConnectionCtrl(getActivity());
			boolean ret = conn.RunHttpDropUrlConnection(m_InformCtrl);
			((InputPasswordTabletActivity)getActivity()).setCancelApply("");
			if (ret == false) {
				logCtrlAsyncTask.loggerError("DropApplyTask Network error");
				m_nErroType = InputBasePageFragment.ERR_NETWORK;
				return false;
			}
			// ログイン結果
			if (m_InformCtrl.GetRtn().startsWith(getText(R.string.Forbidden).toString())) {
				logCtrlAsyncTask.loggerError("DropApplyTask Forbidden.");
				m_nErroType = InputBasePageFragment.ERR_FORBIDDEN;
				return false;
			} else if (m_InformCtrl.GetRtn().startsWith(getText(R.string.Unauthorized).toString())) {
				logCtrlAsyncTask.loggerError("DropApplyTask Unauthorized.");
				m_nErroType = InputBasePageFragment.ERR_UNAUTHORIZED;
				return false;
			} else if (m_InformCtrl.GetRtn().startsWith(getText(R.string.ERR).toString())) {
				logCtrlAsyncTask.loggerError("DropApplyTask ERR:");
				m_nErroType = InputBasePageFragment.ERR_COLON;
				return false;
			} else if (m_InformCtrl.GetRtn().startsWith("NG")) {
				logCtrlAsyncTask.loggerError("DropApplyTask NG");
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
			String cancelApply = ((InputPasswordTabletActivity)getActivity()).getCancelApply();
			if (!ValidateParams.nullOrEmpty(cancelApply) && cancelApply.equals("1") && status != ElementApply.STATUS_APPLY_REJECT) {
				final DialogApplyConfirm dialog = new DialogApplyConfirm(getActivity());
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
				dialog.setOnClickCancel(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						dialog.dismiss();
						getActivity().finish();
					}
				});
				dialog.show();
			} else {
				if (status != ElementApply.STATUS_APPLY_APPROVED) {
					elementMgr.updateStatus(status, ((InputPasswordTabletActivity)getActivity()).getId());
				}
				Intent intent = new Intent(getActivity(), CompleteConfirmApplyActivity.class);
				intent.putExtra("STATUS_APPLY", status);
				intent.putExtra("ELEMENT_APPLY", element);
				intent.putExtra(StringList.m_str_InformCtrl, m_InformCtrl);
				getActivity().finish();
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
						InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
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
		DialogApplyMessage dlgMessage = new DialogApplyMessage(getActivity(), message);
		dlgMessage.show();
	}

	/**
	 * Show message
	 *
	 * @param message
	 */
	protected void showMessage(String message, DialogApplyMessage.OnOkDismissMessageListener listener) {
		DialogApplyMessage dlgMessage = new DialogApplyMessage(getActivity(), message);
		dlgMessage.setOnOkDismissMessageListener(listener);
		dlgMessage.show();
	}


	@Override
	public void onSoftKeyboardShown(boolean isShowing) {
		if (!isShowing) {
			if (isShowingKeyboard) {
				View v = getActivity().getCurrentFocus();
				if (v != null && v instanceof EditText) {
					v.clearFocus();
				}
				isShowingKeyboard = false;
			}
		} else {
			isShowingKeyboard = true;
		}
	}
}
