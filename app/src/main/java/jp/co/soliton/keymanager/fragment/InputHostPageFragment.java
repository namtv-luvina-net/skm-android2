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
import jp.co.soliton.keymanager.InformCtrl;
import jp.co.soliton.keymanager.R;
import jp.co.soliton.keymanager.activity.ViewPagerInputActivity;
import jp.co.soliton.keymanager.asynctask.ConnectApplyTask;
import jp.co.soliton.keymanager.common.CommonUtils;
import jp.co.soliton.keymanager.customview.DialogApplyProgressBar;

import static jp.co.soliton.keymanager.common.ErrorNetwork.*;

/**
 * Created by luongdolong on 2/3/2017.
 * Page input host and secure port
 */

public class InputHostPageFragment extends InputBasePageFragment {
    private EditText txtHostname;
    private EditText txtSecurePort;

    public static Fragment newInstance(Context context) {
        InputHostPageFragment f = new InputHostPageFragment();
        return f;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup root = (ViewGroup) inflater.inflate(R.layout.fragment_input_host, null);
        txtHostname = root.findViewById(R.id.txtHostname);
        txtSecurePort = root.findViewById(R.id.txtSecurePort);
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
        txtHostname.addTextChangedListener(new TextWatcher() {
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
        txtSecurePort.addTextChangedListener(new TextWatcher() {
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
	    txtHostname.setOnKeyListener(new View.OnKeyListener() {
		    @Override
		    public boolean onKey(View v, int keyCode, KeyEvent event) {
			    if (event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
				    txtSecurePort.requestFocus();
			    }
			    return false;
		    }
	    });
        txtSecurePort.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                    if (!nullOrEmpty(txtHostname.getText().toString()) && !nullOrEmpty(txtSecurePort.getText().toString())) {
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
     * Start connect to server
     */
    @Override
    public void nextAction() {
	    String host = CommonUtils.removeHttp(txtHostname.getText().toString().trim());
	    String port = txtSecurePort.getText().toString().trim();
        pagerInputActivity.getInputApplyInfo().setHost(host);
        pagerInputActivity.getInputApplyInfo().setSecurePort(port);
        pagerInputActivity.getInputApplyInfo().savePref(pagerInputActivity);
        progressDialog.show();
        // グレーアウト
        setButtonRunnable(false);
        if (m_InformCtrl == null) {
            m_InformCtrl = new InformCtrl();
        }
	    pagerInputActivity.setHostName(host);
	    pagerInputActivity.setPortName(port);
	    String url = String.format("%s:%s", host, port);
        m_InformCtrl.SetURL(url);
	    new ConnectApplyTask(pagerInputActivity, m_InformCtrl, m_nErroType, new ConnectApplyTask.EndConnection() {
		    @Override
		    public void endConnect(Boolean result, InformCtrl informCtrl, int errorType) {
			    m_InformCtrl = informCtrl;
			    m_nErroType = errorType;
			    endConnection(result);
		    }
	    }).execute();
    }

    /**
     * Processing result after connect to server
     * @param result
     */
    private void endConnection(boolean result) {
        progressDialog.dismiss();
        setButtonRunnable(true);
        if (result) {
            if (m_nErroType == NOT_INSTALL_CA) {
                pagerInputActivity.hideInputPort(false);
                pagerInputActivity.gotoPage(1);
            } else {
                pagerInputActivity.hideInputPort(true);
                pagerInputActivity.gotoPage(2);
            }
        } else {
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
    }

    /**
     * Init value for controls
     */
    private void initValueControl() {
        if (pagerInputActivity == null) {
            return;
        }
        if (!nullOrEmpty(pagerInputActivity.getInputApplyInfo().getHost())) {
            txtHostname.setText(pagerInputActivity.getInputApplyInfo().getHost());
        }
        if (!nullOrEmpty(pagerInputActivity.getInputApplyInfo().getSecurePort())) {
            txtSecurePort.setText(pagerInputActivity.getInputApplyInfo().getSecurePort());
        }
        txtSecurePort.setSelection(txtSecurePort.getText().length());
        setStatusControl();
    }

    /**
     * Set status control next/back
     */
    private void setStatusControl() {
        if (pagerInputActivity.getCurrentPage() != 0) {
            return;
        }
        if (nullOrEmpty(txtHostname.getText().toString()) || nullOrEmpty(txtSecurePort.getText().toString())) {
            pagerInputActivity.setActiveBackNext(true, false);
        } else {
            pagerInputActivity.setActiveBackNext(true, true);
        }
    }

}
