package jp.co.soliton.keymanager.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.security.KeyChain;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import jp.co.soliton.keymanager.HttpConnectionCtrl;
import jp.co.soliton.keymanager.InformCtrl;
import jp.co.soliton.keymanager.LogCtrl;
import jp.co.soliton.keymanager.R;
import jp.co.soliton.keymanager.activity.ViewPagerInputActivity;
import jp.co.soliton.keymanager.customview.DialogApplyProgressBar;
import jp.co.soliton.keymanager.xmlparser.XmlPullParserAided;
import jp.co.soliton.keymanager.xmlparser.XmlStringData;

import javax.security.cert.X509Certificate;
import java.util.List;

/**
 * Created by luongdolong on 2/3/2017.
 *
 * Page input port and Processing download certificate
 */

public class InputPortPageFragment extends InputBasePageFragment {
    private EditText txtPort;
    private TextView zoneInputPortTitle;
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
                    hideKeyboard(v, getContext());
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
        new DownloadCertificateTask().execute();
    }

    /**
     * Finish install certificate
     * @param resultCode
     */
    public void finishInstallCertificate(int resultCode) {
        if (resultCode == Activity.RESULT_OK) {
            pagerInputActivity.gotoPage(2);
        }
    }

    public void hideScreen(boolean hide) {
        if (zoneInputPort == null || zoneInputPortTitle == null) {
            return;
        }
        zoneInputPort.setVisibility(hide ? View.INVISIBLE : View.VISIBLE);
        zoneInputPortTitle.setVisibility(hide ? View.INVISIBLE : View.VISIBLE);
    }

    /**
     * Processing after connect to server
     *
     * @param result
     */
    private void endConnection(boolean result) {
        progressDialog.dismiss();
        if (result) {
            //Download certificate
            downloadCert();
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
     * Download and install certificate
     */
    private void downloadCert() {
        //Extract certificate from .mobileconfig file
        String cacert = m_InformCtrl.GetRtn();
        cacert = cacert.substring(cacert.indexOf("<?xml"));
        cacert = cacert.substring(0, cacert.indexOf("</plist>") + 8);
        XmlPullParserAided m_p_aided = new XmlPullParserAided(pagerInputActivity, cacert, 2);	// 最上位dictの階層は2になる
        boolean ret = m_p_aided.TakeApartProfileList();
        if (!ret) {
	        logCtrl.loggerError("InputPortPageFragment:downloadCert1: " + getString(R.string.error_install_certificate));
            showMessage(getString(R.string.error_install_certificate));
            return;
        }
        List<XmlStringData> listPayloadContent = m_p_aided.GetDictionary().GetArrayString();
        cacert = listPayloadContent.get(listPayloadContent.size() - 1).GetData();
        cacert = String.format("%s\n%s\n%s", "-----BEGIN CERTIFICATE-----", cacert, "-----END CERTIFICATE-----");
        //Install certificate
        Intent intent = KeyChain.createInstallIntent();
        try {
            X509Certificate x509 = X509Certificate.getInstance(cacert.getBytes());
            intent.putExtra(KeyChain.EXTRA_CERTIFICATE, x509.getEncoded());
            intent.putExtra(KeyChain.EXTRA_NAME, InputPortPageFragment.payloadDisplayName);
            pagerInputActivity.startActivityForResult(intent, ViewPagerInputActivity.REQUEST_CODE_INSTALL_CERTIFICATION);
        } catch (Exception e) {
	        logCtrl.loggerError("InputPortPageFragment:downloadCert2: " + getString(R.string.error_install_certificate));
            showMessage(getString(R.string.error_install_certificate));
        }
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

    /**
     * Task download certificate
     */
    private class DownloadCertificateTask extends AsyncTask<Void, Void, Boolean> {
        @Override
        protected Boolean doInBackground(Void... params) {
	        LogCtrl logCtrlAsyncTask = LogCtrl.getInstance(pagerInputActivity);
            HttpConnectionCtrl conn = new HttpConnectionCtrl(pagerInputActivity);
            //send request to server
            boolean ret = conn.RunHttpDownloadCertificate(m_InformCtrl);
            //parse result return
            if (ret == false) {
	            logCtrlAsyncTask.loggerError("DownloadCertificateTask Network error");
                m_nErroType = ERR_NETWORK;
                return false;
            }
            // ログイン結果
            if (m_InformCtrl.GetRtn().startsWith(getText(R.string.Forbidden).toString())) {
	            logCtrlAsyncTask.loggerError("DownloadCertificateTask Forbidden.");
                m_nErroType = ERR_FORBIDDEN;
                return false;
            } else if (m_InformCtrl.GetRtn().startsWith(getText(R.string.Unauthorized).toString())) {
	            logCtrlAsyncTask.loggerError("DownloadCertificateTask Unauthorized.");
                m_nErroType = ERR_UNAUTHORIZED;
                return false;
            } else if (m_InformCtrl.GetRtn().startsWith(getText(R.string.ERR).toString())) {
	            logCtrlAsyncTask.loggerError("DownloadCertificateTask ERR:");
                m_nErroType = ERR_COLON;
                return false;
            }
            m_nErroType = SUCCESSFUL;

            return ret;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);
            endConnection(result);
        }
    }
}
