package jp.co.soliton.keymanager.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import jp.co.soliton.keymanager.R;
import jp.co.soliton.keymanager.common.SoftKeyboardCtrl;

/**
 * Created by nguyenducdat on 4/25/2017.
 */

public class TabletInputReasonFragment extends TabletInputFragment {
	EditText txtReason;
	TextView titleInput;
	TabletAbtractInputFragment tabletAbtractInputFragment;
	public static Fragment newInstance(Context context, TabletAbtractInputFragment tabletAbtractInputFragment) {
		TabletInputReasonFragment f = new TabletInputReasonFragment();
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
			tabletAbtractInputFragment = (TabletAbtractInputFragment) getActivity().getSupportFragmentManager().getFragment(savedInstanceState,
					TAG_TABLET_BASE_INPUT_FRAGMENT);
		}
		View view = inflater.inflate(R.layout.fragment_input_reason_tablet, container, false);
		txtReason = view.findViewById(R.id.txtReason);
		titleInput = view.findViewById(R.id.titleInput);
		titleInput.setText(getString(R.string.reason_for_apply));
		return view;
	}

	@Override
	public void onResume() {
		super.onResume();
		addListenerForEditText();
	}

	private void addListenerForEditText() {
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
					String reason = txtReason.getText().toString().trim();
					tabletAbtractInputFragment.getInputApplyInfo().setReason(reason);
					tabletAbtractInputFragment.getInputApplyInfo().savePref(getActivity());
					txtReason.setText(reason);
					SoftKeyboardCtrl.hideKeyboard(v, getContext());
					updateStatusSkipButton();
				} else {
					tabletAbtractInputFragment.goneSkip();
				}
			}
		});
	}

	@Override
	public void onPageSelected() {
		initValueControl();
	}

	/**
	 * Init value for controls
	 */
	private void initValueControl() {
		if (tabletAbtractInputFragment == null) {
			return;
		}
		if (!nullOrEmpty(tabletAbtractInputFragment.getInputApplyInfo().getReason())) {
			txtReason.setText(tabletAbtractInputFragment.getInputApplyInfo().getReason());
		} else {
			txtReason.setText("");
		}
		setStatusControl();
		updateStatusSkipButton();
	}

	/**
	 * Set status for next/back button
	 */
	private void setStatusControl() {
		if (nullOrEmpty(txtReason.getText().toString())) {
			tabletAbtractInputFragment.disableNext();
		} else {
			tabletAbtractInputFragment.enableNext();
		}
	}

	private void updateStatusSkipButton() {
		if (txtReason.getText().toString().trim().length() == 0) {
			tabletAbtractInputFragment.visibleSkip();
		} else {
			tabletAbtractInputFragment.goneSkip();
		}
	}
	@Override
	public void nextAction() {
		tabletAbtractInputFragment.getInputApplyInfo().setReason(txtReason.getText().toString().trim());
		tabletAbtractInputFragment.getInputApplyInfo().savePref(getActivity());
		tabletAbtractInputFragment.gotoNextPage();
	}

	@Override
	protected void clickSkipButton() {
		super.clickSkipButton();
		tabletAbtractInputFragment.getInputApplyInfo().setReason(null);
		tabletAbtractInputFragment.getInputApplyInfo().savePref(getActivity());
		tabletAbtractInputFragment.gotoNextPage();
	}
}