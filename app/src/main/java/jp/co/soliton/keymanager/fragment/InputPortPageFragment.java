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
import android.widget.LinearLayout;
import android.widget.TextView;
import jp.co.soliton.keymanager.InformCtrl;
import jp.co.soliton.keymanager.LogCtrl;
import jp.co.soliton.keymanager.R;
import jp.co.soliton.keymanager.activity.ViewPagerInputActivity;
import jp.co.soliton.keymanager.asynctask.ConnectApplyTask;
import jp.co.soliton.keymanager.asynctask.DownloadCertificateTask;
import jp.co.soliton.keymanager.common.ControlPagesInput;
import jp.co.soliton.keymanager.common.SoftKeyboardCtrl;
import jp.co.soliton.keymanager.customview.DialogApplyProgressBar;

import static jp.co.soliton.keymanager.common.ErrorNetwork.*;

/**
 * Created by luongdolong on 2/3/2017.
 *
 * Page input port and Processing download certificate
 */

public class InputPortPageFragment extends InputBasePageFragment {
    private EditText txtPort;
    private TextView zoneInputPortTitle;
	private TextView txtGuideDownloadCaCertificate;
    private LinearLayout zoneInputPort;
    public static String payloadDisplayName = "EACert";
	private LogCtrl logCtrl;

    public static Fragment newInstance(Context context) {
        InputPortPageFragment f = new InputPortPageFragment();
	    f.logCtrl = LogCtrl.getInstance(context);
        return f;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup root = (ViewGroup) inflater.inflate(R.layout.fragment_input_port, null);
        txtPort = (EditText) root.findViewById(R.id.txtPort);
        zoneInputPortTitle = (TextView) root.findViewById(R.id.zoneInputPortTitle);
        txtGuideDownloadCaCertificate = (TextView) root.findViewById(R.id.tv_guide_download_ca_certificate);
	    if (pagerInputActivity.sdk_int_version < Build.VERSION_CODES.JELLY_BEAN_MR2) {
		    txtGuideDownloadCaCertificate.setText(getString(R.string.download_ca_description42));
	    }else {
		    txtGuideDownloadCaCertificate.setText(Html.fromHtml(getString(R.string.download_ca_description43)));
	    }
        zoneInputPort = (LinearLayout) root.findViewById(R.id.zoneInputPort);
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
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //Set action for edit text
        txtPort.addTextChangedListener(new TextWatcher() {
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

        txtPort.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    SoftKeyboardCtrl.hideKeyboard(v, getContext());
                }
            }
        });
        txtPort.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                    if (!nullOrEmpty(txtPort.getText().toString())) {
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
     * Start download certificate
     */
    @Override
    public void nextAction() {
	    logCtrl.loggerInfo("InputPortPageFragment--nextAction--");
        pagerInputActivity.getInputApplyInfo().setPort(txtPort.getText().toString().trim());
        pagerInputActivity.getInputApplyInfo().savePref(pagerInputActivity);
        progressDialog.show();
        // グレーアウト
        setButtonRunnable(false);
        if (m_InformCtrl == null) {
            m_InformCtrl = new InformCtrl();
        }
        String url = String.format("%s:%s", pagerInputActivity.getInputApplyInfo().getHost(), txtPort.getText().toString().trim());
        m_InformCtrl.SetURL(url);
        //Open thread download cert
        new DownloadCertificateTask(getActivity(), m_InformCtrl, m_nErroType, new DownloadCertificateTask.EndConnection() {
	        @Override
	        public void endConnect(Boolean result, InformCtrl informCtrl, int errorType) {
		        m_InformCtrl = informCtrl;
		        m_nErroType = errorType;
                endConnection(result);
	        }
        }).execute();
    }

    /**
     * Finish install certificate
     * @param resultCode
     */
    public void finishInstallCertificate(int resultCode) {
        if (resultCode == Activity.RESULT_OK) {
	        if (pagerInputActivity.sdk_int_version >= Build.VERSION_CODES.JELLY_BEAN_MR2){
		        progressDialog.show();
		        String url = String.format("%s:%s", pagerInputActivity.getHostName(), pagerInputActivity.getPortName());
		        m_InformCtrl.SetURL(url);
		        new ConnectApplyTask(pagerInputActivity, m_InformCtrl, m_nErroType, new ConnectApplyTask.EndConnection() {
			        @Override
			        public void endConnect(Boolean result, InformCtrl informCtrl, int errorType) {
				        progressDialog.dismiss();
				        m_InformCtrl = informCtrl;
				        m_nErroType = errorType;
				        checkCertificateInstalled(result);
			        }
		        }).execute();
	        }else {
		        pagerInputActivity.gotoPage(2);
	        }
        }
    }

    public void hideScreen(boolean hide) {
        if (zoneInputPort == null || zoneInputPortTitle == null) {
            return;
        }
        zoneInputPort.setVisibility(hide ? View.INVISIBLE : View.VISIBLE);
        zoneInputPortTitle.setVisibility(hide ? View.INVISIBLE : View.VISIBLE);
    }

    private void checkCertificateInstalled(boolean result) {
	    if (result) {
		    if (m_nErroType == SUCCESSFUL) {
			    pagerInputActivity.hideInputPort(true);
			    pagerInputActivity.gotoPage(2);
		    }
	    }
    }

    private void endConnection(boolean result) {
        progressDialog.dismiss();
        if (result) {
            //Download certificate
//            downloadCert();
	        ControlPagesInput controlPagesInput = new ControlPagesInput(getActivity());
	        String strDownloadCert = controlPagesInput.downloadCert(m_InformCtrl.GetRtn());
	        if (strDownloadCert.length() > 0) {
		        showMessage(strDownloadCert);
	        }
        } else {
            //Show error message
            if (m_nErroType == ERR_FORBIDDEN) {
                String str_forbidden = getString(R.string.Forbidden);
                showMessage(m_InformCtrl.GetRtn().substring(str_forbidden.length()));
            } else if (m_nErroType == ERR_UNAUTHORIZED) {
                String str_unauth = getString(R.string.Unauthorized);
                showMessage(m_InformCtrl.GetRtn().substring(str_unauth.length()));
            } else if (m_nErroType == ERR_COLON) {
                String str_err = getString(R.string.ERR);
                showMessage(m_InformCtrl.GetRtn().substring(str_err.length()));
            } else {
                showMessage(getString(R.string.connect_failed));
            }
        }
        setButtonRunnable(true);
    }

    /**
     * Init value for control
     */
    private void initValueControl() {
        if (pagerInputActivity == null) {
            return;
        }
        if (!nullOrEmpty(pagerInputActivity.getInputApplyInfo().getPort())) {
            txtPort.setText(pagerInputActivity.getInputApplyInfo().getPort());
        }
        setStatusControl();
    }

    /**
     * Set status control next/back
     */
    private void setStatusControl() {
        if (pagerInputActivity.getCurrentPage() != 1) {
            return;
        }
        if (nullOrEmpty(txtPort.getText().toString())) {
            pagerInputActivity.setActiveBackNext(true, false);
        } else {
            pagerInputActivity.setActiveBackNext(true, true);
        }
    }
}
