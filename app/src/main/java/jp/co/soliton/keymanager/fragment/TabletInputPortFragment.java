package jp.co.soliton.keymanager.fragment;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import jp.co.soliton.keymanager.InformCtrl;
import jp.co.soliton.keymanager.LogCtrl;
import jp.co.soliton.keymanager.R;
import jp.co.soliton.keymanager.asynctask.ConnectApplyTask;
import jp.co.soliton.keymanager.asynctask.DownloadCertificateTask;

import static jp.co.soliton.keymanager.fragment.TabletBaseInputFragment.*;

/**
 * Created by nguyenducdat on 4/25/2017.
 */

public class TabletInputPortFragment extends TabletInputFragment {

	RelativeLayout rootViewInputPort;
	EditText edtPort;
	TextView txtGuideDownloadCaCertificate;
	TextView titleInput;
	private LogCtrl logCtrl;

	TabletBaseInputFragment tabletBaseInputFragment;

	public static Fragment newInstance(Context context, TabletBaseInputFragment tabletBaseInputFragment) {
		TabletInputPortFragment f = new TabletInputPortFragment();
		f.tabletBaseInputFragment = tabletBaseInputFragment;
		f.logCtrl = LogCtrl.getInstance(context);
		return f;
	}

	@Override
	public void onSaveInstanceState(Bundle savedInstanceState) {
		super.onSaveInstanceState(savedInstanceState);
		getActivity().getSupportFragmentManager().putFragment(savedInstanceState, TAG_TABLET_BASE_INPUT_FRAGMENT, tabletBaseInputFragment);
	}

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		if (savedInstanceState != null) {
			tabletBaseInputFragment = (TabletBaseInputFragment) getActivity().getSupportFragmentManager().getFragment(savedInstanceState,
					TAG_TABLET_BASE_INPUT_FRAGMENT);
		}
		View view = inflater.inflate(R.layout.fragment_input_port_tablet, container, false);
		rootViewInputPort = (RelativeLayout) view.findViewById(R.id.rootViewInputPort);
		titleInput = (TextView) view.findViewById(R.id.titleInput);
		titleInput.setText(getString(R.string.download_ca_certificate));
		edtPort = (EditText) view.findViewById(R.id.edit_port);
		txtGuideDownloadCaCertificate = (TextView) view.findViewById(R.id.txt_des_download_ca);
		if (tabletBaseInputFragment.sdk_int_version < Build.VERSION_CODES.JELLY_BEAN_MR2) {
			txtGuideDownloadCaCertificate.setText(getString(R.string.download_ca_description42));
		} else {
			txtGuideDownloadCaCertificate.setText(Html.fromHtml(getString(R.string.download_ca_description43)));
		}
		initValueControl();
		return view;
	}

	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		edtPort.addTextChangedListener(new TextWatcher() {
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

		edtPort.setOnFocusChangeListener(new View.OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (!hasFocus) {
					hideKeyboard(v, getContext());
				}
			}
		});
		edtPort.setOnKeyListener(new View.OnKeyListener() {
			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if (event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
					if (!nullOrEmpty(edtPort.getText().toString())) {
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

	/**
	 * Init value for control
	 */
	private void initValueControl() {
		if (tabletBaseInputFragment == null || edtPort == null) {
			return;
		}
		if (!nullOrEmpty(tabletBaseInputFragment.getInputApplyInfo().getPort())) {
			edtPort.setText(tabletBaseInputFragment.getInputApplyInfo().getPort());
		}
		setStatusControl();
	}

	/**
	 * Set status control next/back
	 */
	private void setStatusControl() {
		if (tabletBaseInputFragment.getCurrentPage() != 1) {
			return;
		}
		if (nullOrEmpty(edtPort.getText().toString())) {
			tabletBaseInputFragment.disableNext();
		} else {
			tabletBaseInputFragment.enableNext();
		}
	}

	public void hideScreen(boolean hide) {
		if (rootViewInputPort == null) {
			return;
		}
		rootViewInputPort.setVisibility(hide ? View.INVISIBLE : View.VISIBLE);
	}

	@Override
	public void nextAction() {
			logCtrl.loggerInfo("InputPortPageFragment--nextAction--");
			tabletBaseInputFragment.getInputApplyInfo().setPort(edtPort.getText().toString().trim());
			tabletBaseInputFragment.getInputApplyInfo().savePref(getActivity());
			tabletBaseInputFragment.getProgressDialog().show();
			if (tabletBaseInputFragment.getInformCtrl() == null) {
				tabletBaseInputFragment.setInformCtrl(new InformCtrl());
			}
			String url = String.format("%s:%s", tabletBaseInputFragment.getInputApplyInfo().getHost(), edtPort.getText()
					.toString().trim());

			tabletBaseInputFragment.getInformCtrl().SetURL(url);
			new DownloadCertificateTask(getActivity(), tabletBaseInputFragment.getInformCtrl(), tabletBaseInputFragment
					.getErroType(), new
					DownloadCertificateTask.EndConnection() {
						@Override
						public void endConnect(Boolean result, InformCtrl informCtrl, int errorType) {
							tabletBaseInputFragment.setInformCtrl(informCtrl);
							tabletBaseInputFragment.setErroType(errorType);
							endConnection(result);
						}
					}).execute();
	}

	private void endConnection(boolean result) {
		tabletBaseInputFragment.getProgressDialog().dismiss();
		if (result) {
			//Download certificate
			String strDownloadCert = tabletBaseInputFragment.controlPagesInput.downloadCert(tabletBaseInputFragment
					.getInformCtrl().GetRtn());
			if (strDownloadCert.length() > 0) {
				tabletBaseInputFragment.showMessage(strDownloadCert);
			}
		} else {
			//Show error message
			int m_nErroType = tabletBaseInputFragment.getErroType();
			String strRtn = tabletBaseInputFragment.getInformCtrl().GetRtn();
			if (m_nErroType == ERR_FORBIDDEN) {
				String str_forbidden = getString(R.string.Forbidden);
				tabletBaseInputFragment.showMessage(strRtn.substring(str_forbidden.length
						()));
			} else if (m_nErroType == ERR_UNAUTHORIZED) {
				String str_unauth = getString(R.string.Unauthorized);
				tabletBaseInputFragment.showMessage(strRtn.substring(str_unauth.length()));
			} else if (m_nErroType == ERR_COLON) {
				String str_err = getString(R.string.ERR);
				tabletBaseInputFragment.showMessage(strRtn.substring(str_err.length()));
			} else {
				tabletBaseInputFragment.showMessage(getString(R.string.connect_failed));
			}
		}
	}

	/**
	 * Finish install certificate
	 *
	 * @param resultCode
	 */
	public void finishInstallCertificate(int resultCode) {
		if (resultCode == Activity.RESULT_OK) {
			if (tabletBaseInputFragment.sdk_int_version >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
				tabletBaseInputFragment.getProgressDialog().show();
				String host = tabletBaseInputFragment.getHostName();
				String port = tabletBaseInputFragment.getPortName();
				String url = String.format("%s:%s", host, port);
				tabletBaseInputFragment.getInformCtrl().SetURL(url);
				new ConnectApplyTask(getActivity(), tabletBaseInputFragment.getInformCtrl(), tabletBaseInputFragment
						.getErroType(), new ConnectApplyTask.EndConnection() {
					@Override
					public void endConnect(Boolean result, InformCtrl informCtrl, int errorType) {
						tabletBaseInputFragment.getProgressDialog().dismiss();
						tabletBaseInputFragment.setInformCtrl(informCtrl);
						tabletBaseInputFragment.setErroType(errorType);
						checkCertificateInstalled(result);
					}
				}).execute();
			} else {
				tabletBaseInputFragment.gotoPage(2);
			}
		}
	}

	private void checkCertificateInstalled(boolean result) {
		if (result) {
			if (tabletBaseInputFragment.getErroType() == SUCCESSFUL) {
				tabletBaseInputFragment.hideInputPort(true);
				tabletBaseInputFragment.gotoPage(2);
			}
		}
	}
}