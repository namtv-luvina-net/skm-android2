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
import android.widget.Button;
import android.widget.EditText;
import jp.co.soliton.keymanager.R;
import jp.co.soliton.keymanager.ValidateParams;
import jp.co.soliton.keymanager.activity.ViewPagerReapplyActivity;
import jp.co.soliton.keymanager.common.SoftKeyboardCtrl;
import jp.co.soliton.keymanager.customview.AutoResizeTextView;

/**
 * Created by luongdolong on 2/3/2017.
 * Page input email for apply
 */

public class ReapplyEmailPageFragment extends ReapplyBasePageFragment {
    private EditText txtEmail;
    private Button btnSkipEmail;
    private AutoResizeTextView titleEmail;
    public static Fragment newInstance(Context context) {
        ReapplyEmailPageFragment f = new ReapplyEmailPageFragment();
        return f;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup root = (ViewGroup) inflater.inflate(R.layout.fragment_input_email, null);
        txtEmail = (EditText) root.findViewById(R.id.txtEmail);
        btnSkipEmail = (Button) root.findViewById(R.id.btnSkipEmail);
        titleEmail = (AutoResizeTextView) root.findViewById(R.id.titleEmail);
        if (ValidateParams.isJPLanguage()) {
            titleEmail.setMaxLines(1);
        } else {
            titleEmail.setMaxLines(3);
        }
        initValueControl();
        return root;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof ViewPagerReapplyActivity) {
            this.pagerReapplyActivity = (ViewPagerReapplyActivity) context;
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //Set action for edit text
        txtEmail.addTextChangedListener(new TextWatcher() {
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

        txtEmail.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
	            if (!hasFocus) {
		            pagerReapplyActivity.getInputApplyInfo().setEmail(txtEmail.getText().toString().trim());
		            pagerReapplyActivity.getInputApplyInfo().savePref(pagerReapplyActivity);
		            txtEmail.setText(txtEmail.getText().toString().trim());
		            SoftKeyboardCtrl.hideKeyboard(v, getContext());
		            updateStatusSkipButton();
	            } else {
		            btnSkipEmail.setVisibility(View.INVISIBLE);
	            }
            }
        });
        txtEmail.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
	                txtEmail.clearFocus();
                    if (!nullOrEmpty(txtEmail.getText().toString())) {
                        nextAction();
                        return true;
                    }
                }
                return false;
            }
        });
    }

	@Override
	public void clearFocusEditText() {
		super.clearFocusEditText();
		updateStatusSkipButton();
	}

	private void updateStatusSkipButton() {
		if (txtEmail.getText().toString().trim().length() == 0) {
			btnSkipEmail.setVisibility(View.VISIBLE);
		} else {
			btnSkipEmail.setVisibility(View.INVISIBLE);
		}
	}

    @Override
    public void onResume() {
        super.onResume();
        //Action skip input email
        btnSkipEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pagerReapplyActivity.getInputApplyInfo().setEmail(null);
                pagerReapplyActivity.getInputApplyInfo().savePref(pagerReapplyActivity);
                pagerReapplyActivity.gotoPage(2);
            }
        });
	    updateStatusSkipButton();
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
     * Next action move to input reason screen
     */
    @Override
    public void nextAction() {
        pagerReapplyActivity.getInputApplyInfo().setEmail(txtEmail.getText().toString().trim());
        pagerReapplyActivity.getInputApplyInfo().savePref(pagerReapplyActivity);
        if (!ValidateParams.isValidEmail(txtEmail.getText().toString().trim())) {
            showMessage(getString(R.string.apply_mail_error));
            return;
        }
        pagerReapplyActivity.gotoPage(2);
    }

    /**
     * Init value for controls
     */
    private void initValueControl() {
        if (pagerReapplyActivity == null) {
            return;
        }
	    if (!nullOrEmpty(pagerReapplyActivity.getInputApplyInfo().getEmail())) {
		    txtEmail.setText(pagerReapplyActivity.getInputApplyInfo().getEmail());
	    } else {
		    txtEmail.setText("");
	    }
	    updateStatusSkipButton();
        setStatusControl();
    }

    /**
     * Set status for next/back button
     */
    private void setStatusControl() {
        if (pagerReapplyActivity.getCurrentPage() != 1) {
            return;
        }
        if (nullOrEmpty(txtEmail.getText().toString())) {
            pagerReapplyActivity.setActiveBackNext(true, false);
        } else {
            pagerReapplyActivity.setActiveBackNext(true, true);
        }
    }

}
