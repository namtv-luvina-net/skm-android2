package jp.co.soliton.keymanager.fragment;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
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
import jp.co.soliton.keymanager.R;
import jp.co.soliton.keymanager.asynctask.ConnectApplyTask;
import jp.co.soliton.keymanager.asynctask.DownloadCertificateTask;
import jp.co.soliton.keymanager.common.CommonUtils;
import jp.co.soliton.keymanager.common.SoftKeyboardCtrl;

import static jp.co.soliton.keymanager.common.ErrorNetwork.*;

/**
 * Created by nguyenducdat on 4/25/2017.
 */

public class TabletInputPortFragment extends TabletInputFragment {

	RelativeLayout rootViewInputPort;
	EditText edtPort;
	TextView txtGuideDownloadCaCertificate;
	TextView titleInput;

	TabletAbtractInputFragment tabletAbtractInputFragment;

	public static Fragment newInstance(Context context, TabletAbtractInputFragment tabletAbtractInputFragment) {
		TabletInputPortFragment f = new TabletInputPortFragment();
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
		View view = inflater.inflate(R.layout.fragment_input_port_tablet, container, false);
		rootViewInputPort = view.findViewById(R.id.rootViewInputPort);
		titleInput = view.findViewById(R.id.titleInput);
		titleInput.setText(getString(R.string.download_ca_certificate));
		edtPort = view.findViewById(R.id.edit_port);
		txtGuideDownloadCaCertificate = view.findViewById(R.id.txt_des_download_ca);
		if (tabletAbtractInputFragment.sdk_int_version < Build.VERSION_CODES.JELLY_BEAN_MR2) {
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
					SoftKeyboardCtrl.hideKeyboard(v, getContext());
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
		if (tabletAbtractInputFragment == null || edtPort == null) {
			return;
		}
		if (!nullOrEmpty(tabletAbtractInputFragment.getInputApplyInfo().getPort())) {
			edtPort.setText(tabletAbtractInputFragment.getInputApplyInfo().getPort());
		}
		setStatusControl();
	}

	/**
	 * Set status control next/back
	 */
	private void setStatusControl() {
		if (tabletAbtractInputFragment.getCurrentPage() != 1) {
			return;
		}
		if (nullOrEmpty(edtPort.getText().toString())) {
			tabletAbtractInputFragment.disableNext();
		} else {
			tabletAbtractInputFragment.enableNext();
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
			tabletAbtractInputFragment.getInputApplyInfo().setPort(edtPort.getText().toString().trim());
			tabletAbtractInputFragment.getInputApplyInfo().savePref(getActivity());
			tabletAbtractInputFragment.getProgressDialog().show();
			if (tabletAbtractInputFragment.getInformCtrl() == null) {
				tabletAbtractInputFragment.setInformCtrl(new InformCtrl());
			}
			String url = String.format("%s:%s", tabletAbtractInputFragment.getInputApplyInfo().getHost(), edtPort.getText()
					.toString().trim());

			tabletAbtractInputFragment.getInformCtrl().SetURL(url);
			new DownloadCertificateTask(getActivity(), tabletAbtractInputFragment.getInformCtrl(), tabletAbtractInputFragment
					.getErroType(), new
					DownloadCertificateTask.EndConnection() {
						@Override
						public void endConnect(Boolean result, InformCtrl informCtrl, int errorType) {
							tabletAbtractInputFragment.setInformCtrl(informCtrl);
							tabletAbtractInputFragment.setErroType(errorType);
							endConnection(result);
						}
					}).execute();
	}

	private void endConnection(boolean result) {
		tabletAbtractInputFragment.getProgressDialog().dismiss();
		if (result) {
			//Download certificate
			String strDownloadCert = tabletAbtractInputFragment.controlPagesInput.downloadCert(tabletAbtractInputFragment
					.getInformCtrl().GetRtn());
			if (strDownloadCert.length() > 0) {
				tabletAbtractInputFragment.showMessage(strDownloadCert);
			}
		} else {
			//Show error message
			int m_nErroType = tabletAbtractInputFragment.getErroType();
			String strRtn = tabletAbtractInputFragment.getInformCtrl().GetRtn();
			if (m_nErroType == ERR_FORBIDDEN) {
				String str_forbidden = getString(R.string.Forbidden);
				tabletAbtractInputFragment.showMessage(strRtn.substring(str_forbidden.length
						()));
			} else if (m_nErroType == ERR_UNAUTHORIZED) {
				String str_unauth = getString(R.string.Unauthorized);
				tabletAbtractInputFragment.showMessage(strRtn.substring(str_unauth.length()));
			} else if (m_nErroType == ERR_COLON) {
				String str_err = getString(R.string.ERR);
				tabletAbtractInputFragment.showMessage(strRtn.substring(str_err.length()));
			} else {
				tabletAbtractInputFragment.showMessage(getString(R.string.connect_failed));
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
			if (tabletAbtractInputFragment.controlPagesInput.getCertArray().size() > 0)
			{
				final Handler handler = new Handler();
				handler.postDelayed(new Runnable() {
					@Override
					public void run() {
						tabletAbtractInputFragment.controlPagesInput.installCACert();
					}
				}, 500);
			}
			else
			{
				if (tabletAbtractInputFragment.sdk_int_version >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
					tabletAbtractInputFragment.getProgressDialog().show();
					String host = tabletAbtractInputFragment.getHostName();
					String port = tabletAbtractInputFragment.getPortName();
					String url = String.format("%s:%s", host, port);
					tabletAbtractInputFragment.getInformCtrl().SetURL(url);
					new ConnectApplyTask(getActivity(), tabletAbtractInputFragment.getInformCtrl(), tabletAbtractInputFragment
							.getErroType(), new ConnectApplyTask.EndConnection() {
						@Override
						public void endConnect(Boolean result, InformCtrl informCtrl, int errorType) {
							tabletAbtractInputFragment.getProgressDialog().dismiss();
							tabletAbtractInputFragment.setInformCtrl(informCtrl);
							tabletAbtractInputFragment.setErroType(errorType);
							checkCertificateInstalled(result);
						}
					}).execute();
				} else {
					tabletAbtractInputFragment.gotoPage(2);
				}
			}

		}
	}

	private void checkCertificateInstalled(boolean result) {
		if (result) {
			if (tabletAbtractInputFragment.getErroType() == SUCCESSFUL) {
				tabletAbtractInputFragment.hideInputPort(true);
				tabletAbtractInputFragment.gotoPage(2);
			}
		}
	}
}