package jp.co.soliton.keymanager.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import jp.co.soliton.keymanager.InformCtrl;
import jp.co.soliton.keymanager.R;
import jp.co.soliton.keymanager.asynctask.ConnectApplyTask;
import jp.co.soliton.keymanager.common.CommonUtils;

import static jp.co.soliton.keymanager.common.ErrorNetwork.*;

/**
 * Created by nguyenducdat on 4/25/2017.
 */

public class TabletInputHostFragment extends TabletInputFragment {

	TextView titleInput;
	EditText editTextHost;
	EditText editTextSecurePort;
	TabletAbtractInputFragment tabletAbtractInputFragment;

	public static Fragment newInstance(Context context, TabletAbtractInputFragment tabletAbtractInputFragment) {
		TabletInputHostFragment f = new TabletInputHostFragment();
		f.tabletAbtractInputFragment = tabletAbtractInputFragment;
		return f;
	}

	@Override
	public void onSaveInstanceState(Bundle savedInstanceState) {
		super.onSaveInstanceState(savedInstanceState);
		getActivity().getSupportFragmentManager().putFragment(savedInstanceState, TAG_TABLET_BASE_INPUT_FRAGMENT, tabletAbtractInputFragment);
	}

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		if (savedInstanceState != null) {
			tabletAbtractInputFragment = (TabletBaseInputFragment) getActivity().getSupportFragmentManager().getFragment(savedInstanceState,
					TAG_TABLET_BASE_INPUT_FRAGMENT);
		}
		View view = inflater.inflate(R.layout.fragment_input_host_tablet, null);
		editTextHost = (EditText) view.findViewById(R.id.edit_host);
		editTextSecurePort = (EditText) view.findViewById(R.id.edit_port);
		titleInput = (TextView) view.findViewById(R.id.titleInput);
		titleInput.setText(getString(R.string.host_name_and_port_number));
		return view;
	}

	@Override
	public void onResume() {
		super.onResume();
		addTextChangedListenerForTextView();
		setStatusControl();
		initValueEditText();
	}

	private void addTextChangedListenerForTextView() {
		//Set action for edit text
		editTextHost.addTextChangedListener(new TextWatcher() {
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
		editTextSecurePort.addTextChangedListener(new TextWatcher() {
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

		editTextHost.setOnKeyListener(new View.OnKeyListener() {
			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if (event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
					editTextSecurePort.requestFocus();
				}
				return false;
			}
		});
		editTextSecurePort.setOnKeyListener(new View.OnKeyListener() {
			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if (event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
					if (!nullOrEmpty(editTextHost.getText().toString()) && !nullOrEmpty(editTextSecurePort.getText().toString())) {
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
			setStatusControl();
			initValueEditText();
		}
	}

	@Override
	public void setUserVisibleHint(boolean isVisibleToUser) {
		super.setUserVisibleHint(isVisibleToUser);
		if (isVisibleToUser) {
			setStatusControl();
			initValueEditText();
		}
	}

	/**
	 * Set status control next/back
	 */
	private void setStatusControl() {
		if (tabletAbtractInputFragment == null || editTextHost == null || editTextSecurePort == null) {
			return;
		}
		if (nullOrEmpty(editTextHost.getText().toString()) || nullOrEmpty(editTextSecurePort.getText().toString())) {
			tabletAbtractInputFragment.disableNext();
		} else {
			tabletAbtractInputFragment.enableNext();
		}
	}

	private void initValueEditText() {
		if (editTextHost == null || editTextSecurePort == null) {
			return;
		}
		String host = tabletAbtractInputFragment.getInputApplyInfo().getHost();
		if (!nullOrEmpty(host)) {
			editTextHost.setText(host);
		}
		String securePort = tabletAbtractInputFragment.getInputApplyInfo().getSecurePort();
		if (!nullOrEmpty(securePort)) {
			editTextSecurePort.setText(securePort);
		}
	}

	@Override
	public void nextAction() {
		String host = editTextHost.getText().toString().trim();
		String port = editTextSecurePort.getText().toString().trim();
		tabletAbtractInputFragment.getInputApplyInfo().setHost(host);
		tabletAbtractInputFragment.getInputApplyInfo().setSecurePort(port);
		tabletAbtractInputFragment.getInputApplyInfo().savePref(getActivity());
		tabletAbtractInputFragment.getProgressDialog().show();
		if (tabletAbtractInputFragment.getInformCtrl() == null) {
			tabletAbtractInputFragment.setInformCtrl(new InformCtrl());
		}

		tabletAbtractInputFragment.setHostName(host);
		tabletAbtractInputFragment.setPortName(port);
		String url = String.format("%s:%s", CommonUtils.removeHttp(host), port);
		tabletAbtractInputFragment.getInformCtrl().SetURL(url);
		new ConnectApplyTask(getActivity(), tabletAbtractInputFragment.getInformCtrl(), tabletAbtractInputFragment.getErroType()
				, new ConnectApplyTask.EndConnection() {
			@Override
			public void endConnect(Boolean result, InformCtrl informCtrl, int errorType) {
				tabletAbtractInputFragment.setInformCtrl(informCtrl);
				tabletAbtractInputFragment.setErroType(errorType);
				endConnection(result);
			}
		}).execute();
	}

	/**
	 * Processing result after connect to server
	 * @param result
	 */
	private void endConnection(boolean result) {
		tabletAbtractInputFragment.getProgressDialog().dismiss();
		int m_nErroType = tabletAbtractInputFragment.getErroType();
		if (result) {
			if (m_nErroType == NOT_INSTALL_CA) {
				tabletAbtractInputFragment.hideInputPort(false);
				tabletAbtractInputFragment.gotoPage(1);
			} else {
				tabletAbtractInputFragment.hideInputPort(true);
				tabletAbtractInputFragment.gotoPage(2);
			}
		} else {
			if (m_nErroType == ERR_FORBIDDEN) {
				String str_forbidden = getString(R.string.Forbidden);
				tabletAbtractInputFragment.showMessage(tabletAbtractInputFragment.getInformCtrl().GetRtn().substring(str_forbidden.length
						()));
			} else if (m_nErroType == ERR_UNAUTHORIZED) {
				String str_unauth = getString(R.string.Unauthorized);
				tabletAbtractInputFragment.showMessage(tabletAbtractInputFragment.getInformCtrl().GetRtn().substring(str_unauth.length()));
			} else if (m_nErroType == ERR_COLON) {
				String str_err = getString(R.string.ERR);
				tabletAbtractInputFragment.showMessage(tabletAbtractInputFragment.getInformCtrl().GetRtn().substring(str_err.length()));
			} else {
				tabletAbtractInputFragment.showMessage(getString(R.string.connect_failed));
			}
		}
	}

}