package jp.co.soliton.keymanager.fragment;

import android.content.Context;
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
import android.widget.EditText;
import android.widget.TextView;
import jp.co.soliton.keymanager.InformCtrl;
import jp.co.soliton.keymanager.R;
import jp.co.soliton.keymanager.asynctask.ConnectApplyTask;
import jp.co.soliton.keymanager.customview.DialogApplyProgressBar;

import static jp.co.soliton.keymanager.fragment.TabletBaseInputFragment.*;

/**
 * Created by nguyenducdat on 4/25/2017.
 */

public class TabletInputHostFragment extends TabletInputFragment {

	TextView titleInput;
	EditText editTextHost;
	EditText editTextSecurePort;
//	TabletBaseInputFragment tabletBaseInputFragment;

	public static Fragment newInstance(Context context, TabletBaseInputFragment tabletBaseInputFragment) {
		Log.d("TabletInputHostFragment:datnd", "newInstance: ");
		TabletInputHostFragment f = new TabletInputHostFragment();
		Log.d("TabletInputHostFragment:datnd", "newInstance: ");
		f.tabletBaseInputFragment = tabletBaseInputFragment;
		return f;
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putSerializable("tabletBaseInputFragment", tabletBaseInputFragment);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		Log.d("TabletInputHostFragment:datnd", "onDestroy: ");
	}

	@Override
	public void onAttach(Context context) {
		super.onAttach(context);
		Log.d("TabletInputHostFragment:datnd", "onAttach: ");
		if (tabletBaseInputFragment != null && tabletBaseInputFragment.progressDialog == null) {
			tabletBaseInputFragment.progressDialog = new DialogApplyProgressBar(getActivity());
		}
	}

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		Log.d("TabletInputHostFragment:datnd", "onCreateView: ");
		View view = inflater.inflate(R.layout.fragment_input_host_tablet, null);
		if (savedInstanceState != null) {
			tabletBaseInputFragment = (TabletBaseInputFragment) savedInstanceState.getSerializable("tabletBaseInputFragment");
		}
		editTextHost = (EditText) view.findViewById(R.id.edit_host);
		editTextSecurePort = (EditText) view.findViewById(R.id.edit_port);
		titleInput = (TextView) view.findViewById(R.id.titleInput);
		titleInput.setText(getString(R.string.input_host_name_and_port_number));
		return view;
	}

	@Override
	public void onResume() {
		super.onResume();
		Log.d("TabletInputHostFragment:datnd", "onResume: ");
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
		editTextHost.setOnFocusChangeListener(new View.OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (!hasFocus) {
					hideKeyboard(v, getContext());
				}else {
					InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
					imm.showSoftInput(editTextSecurePort, InputMethodManager.SHOW_IMPLICIT);
				}
			}
		});
		editTextSecurePort.setOnFocusChangeListener(new View.OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (!hasFocus) {
					hideKeyboard(v, getContext());
				} else {
					InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
					imm.showSoftInput(editTextSecurePort, InputMethodManager.SHOW_IMPLICIT);
				}
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
		if (tabletBaseInputFragment == null || editTextHost == null || editTextSecurePort == null) {
			return;
		}
		if (nullOrEmpty(editTextHost.getText().toString()) || nullOrEmpty(editTextSecurePort.getText().toString())) {
			tabletBaseInputFragment.disableNext();
		} else {
			tabletBaseInputFragment.enableNext();
		}
	}

	private void initValueEditText() {
		if (editTextHost == null || editTextSecurePort == null) {
			return;
		}
		String host = tabletBaseInputFragment.getInputApplyInfo().getHost();
		if (!nullOrEmpty(host)) {
			editTextHost.setText(host);
		}
		String securePort = tabletBaseInputFragment.getInputApplyInfo().getHost();
		if (!nullOrEmpty(securePort)) {
			editTextSecurePort.setText(securePort);
		}
	}

	@Override
	public void nextAction() {
		String host = editTextHost.getText().toString().trim();
		String port = editTextSecurePort.getText().toString().trim();
		tabletBaseInputFragment.getInputApplyInfo().setHost(host);
		tabletBaseInputFragment.getInputApplyInfo().setSecurePort(port);
		tabletBaseInputFragment.getInputApplyInfo().savePref(getActivity());
		tabletBaseInputFragment.progressDialog.show();
		if (tabletBaseInputFragment.getInformCtrl() == null) {
			tabletBaseInputFragment.setInformCtrl(new InformCtrl());
		}

		tabletBaseInputFragment.setHostName(host);
		tabletBaseInputFragment.setPortName(port);
		String url = String.format("%s:%s", host, port);
		tabletBaseInputFragment.getInformCtrl().SetURL(url);
		new ConnectApplyTask(getActivity(), tabletBaseInputFragment.getInformCtrl(), tabletBaseInputFragment.getErroType()
				, new ConnectApplyTask.EndConnection() {
			@Override
			public void endConnect(Boolean result, InformCtrl informCtrl, int errorType) {
				tabletBaseInputFragment.setInformCtrl(informCtrl);
				tabletBaseInputFragment.setErroType(errorType);
				endConnection(result);
			}
		}).execute();
	}

	/**
	 * Processing result after connect to server
	 * @param result
	 */
	private void endConnection(boolean result) {
		tabletBaseInputFragment.progressDialog.dismiss();
		int m_nErroType = tabletBaseInputFragment.getErroType();
		if (result) {
			if (m_nErroType == NOT_INSTALL_CA) {
				tabletBaseInputFragment.hideInputPort(false);
				tabletBaseInputFragment.gotoPage(1);
			} else {
				tabletBaseInputFragment.hideInputPort(true);
				tabletBaseInputFragment.gotoPage(2);
			}
		} else {
			if (m_nErroType == ERR_FORBIDDEN) {
				String str_forbidden = getString(R.string.Forbidden);
				tabletBaseInputFragment.showMessage(tabletBaseInputFragment.getInformCtrl().GetRtn().substring(str_forbidden.length
						()));
			} else if (m_nErroType == ERR_UNAUTHORIZED) {
				String str_unauth = getString(R.string.Unauthorized);
				tabletBaseInputFragment.showMessage(tabletBaseInputFragment.getInformCtrl().GetRtn().substring(str_unauth.length()));
			} else if (m_nErroType == ERR_COLON) {
				String str_err = getString(R.string.ERR);
				tabletBaseInputFragment.showMessage(tabletBaseInputFragment.getInformCtrl().GetRtn().substring(str_err.length()));
			} else {
				tabletBaseInputFragment.showMessage(getString(R.string.connect_failed));
			}
		}
	}

}