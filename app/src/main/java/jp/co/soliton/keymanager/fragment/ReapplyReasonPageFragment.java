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
import jp.co.soliton.keymanager.activity.ViewPagerReapplyActivity;

/**
 * Created by luongdolong on 2/3/2017.
 *
 * Page input reason for apply
 */

public class ReapplyReasonPageFragment extends ReapplyBasePageFragment {
    private EditText txtReason;
    private Button btnSkipReason;

    public static Fragment newInstance(Context context) {
        ReapplyReasonPageFragment f = new ReapplyReasonPageFragment();
        return f;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup root = (ViewGroup) inflater.inflate(R.layout.fragment_reapply_reason, null);
        txtReason = (EditText) root.findViewById(R.id.txtReason);
        btnSkipReason = (Button) root.findViewById(R.id.btnSkipReason);
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
                    hideKeyboard(v, getContext());
                }
            }
        });
        txtReason.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                    if (!nullOrEmpty(txtReason.getText().toString())) {
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
        //Action skip input reason
        btnSkipReason.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pagerReapplyActivity.getInputApplyInfo().setReason(null);
                pagerReapplyActivity.getInputApplyInfo().savePref(pagerReapplyActivity);
                pagerReapplyActivity.gotoConfirmApply();
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
     * Move to confirm apply screen
     */
    @Override
    public void nextAction() {
        pagerReapplyActivity.getInputApplyInfo().setReason(txtReason.getText().toString().trim());
        pagerReapplyActivity.getInputApplyInfo().savePref(pagerReapplyActivity);

        pagerReapplyActivity.gotoConfirmApply();
    }

    /**
     * init value control
     */
    private void initValueControl() {
        if (pagerReapplyActivity == null) {
            return;
        }
        if (!nullOrEmpty(pagerReapplyActivity.getInputApplyInfo().getReason())) {
            txtReason.setText(pagerReapplyActivity.getInputApplyInfo().getReason());
        }
        setStatusControl();
    }

    /**
     * Set status for next/back button
     */
    private void setStatusControl() {
        if (pagerReapplyActivity.getCurrentPage() != 2) {
            return;
        }
        if (nullOrEmpty(txtReason.getText().toString())) {
            pagerReapplyActivity.setActiveBackNext(true, false);
        } else {
            pagerReapplyActivity.setActiveBackNext(true, true);
        }
    }
}
