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
import jp.co.soliton.keymanager.activity.ViewPagerInputActivity;

/**
 * Created by luongdolong on 2/3/2017.
 *
 * Page input reason for apply
 */

public class InputReasonPageFragment extends InputBasePageFragment {
    private EditText txtReason;
    private Button btnSkipReason;

    public static Fragment newInstance(Context context) {
        InputReasonPageFragment f = new InputReasonPageFragment();
        return f;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup root = (ViewGroup) inflater.inflate(R.layout.fragment_input_reason, null);
        txtReason = (EditText) root.findViewById(R.id.txtReason);
        btnSkipReason = (Button) root.findViewById(R.id.btnSkipReason);
        initValueControl();
        return root;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof ViewPagerInputActivity) {
            this.pagerInputActivity = (ViewPagerInputActivity) context;
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //Action for edit text
        txtReason.addTextChangedListener(new TextWatcher() {
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

        txtReason.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
	                txtReason.setText(txtReason.getText().toString().trim());
                    hideKeyboard(v, getContext());
	                updateStatusSkipButton();
                } else {
	                btnSkipReason.setVisibility(View.INVISIBLE);
                }
            }
        });
    }

	@Override
	public void clearFocusEditText() {
		super.clearFocusEditText();
		updateStatusSkipButton();
	}

	private void updateStatusSkipButton() {
		if (txtReason.getText().toString().trim().length() == 0) {
			btnSkipReason.setVisibility(View.VISIBLE);
		} else {
			btnSkipReason.setVisibility(View.INVISIBLE);
		}
	}

    @Override
    public void onResume() {
        super.onResume();
        //Action skip input reason
        btnSkipReason.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pagerInputActivity.getInputApplyInfo().setReason(null);
                pagerInputActivity.getInputApplyInfo().savePref(pagerInputActivity);
                pagerInputActivity.gotoConfirmApply();
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
     * Move to confirm apply screen
     */
    @Override
    public void nextAction() {
        pagerInputActivity.getInputApplyInfo().setReason(txtReason.getText().toString().trim());
        pagerInputActivity.getInputApplyInfo().savePref(pagerInputActivity);

        pagerInputActivity.gotoConfirmApply();
    }

    /**
     * init value control
     */
    private void initValueControl() {
        if (pagerInputActivity == null) {
            return;
        }
        if (!nullOrEmpty(pagerInputActivity.getInputApplyInfo().getReason())) {
            txtReason.setText(pagerInputActivity.getInputApplyInfo().getReason());
        }
	    updateStatusSkipButton();
        setStatusControl();
    }

    /**
     * Set status for next/back button
     */
    private void setStatusControl() {
        if (pagerInputActivity.getCurrentPage() != 5) {
            return;
        }
        if (nullOrEmpty(txtReason.getText().toString())) {
            pagerInputActivity.setActiveBackNext(true, false);
        } else {
            pagerInputActivity.setActiveBackNext(true, true);
        }
    }
}
