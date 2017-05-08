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
import android.widget.EditText;
import android.widget.TextView;
import jp.co.soliton.keymanager.R;

/**
 * Created by nguyenducdat on 4/25/2017.
 */

public class TabletInputReasonFragment extends TabletInputFragment {
	EditText txtReason;
	TextView titleInput;
	TabletBaseInputFragment tabletBaseInputFragment;
	public static Fragment newInstance(Context context, TabletBaseInputFragment tabletBaseInputFragment) {
		TabletInputReasonFragment f = new TabletInputReasonFragment();
		f.tabletBaseInputFragment = tabletBaseInputFragment;
		return f;
	}

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_input_reason_tablet, container, false);
		txtReason = (EditText) view.findViewById(R.id.txtReason);
		titleInput = (TextView) view.findViewById(R.id.titleInput);
		titleInput.setText(getString(R.string.input_reason_for_apply));
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
					txtReason.setText(txtReason.getText().toString().trim());
					hideKeyboard(v, getContext());
					updateStatusSkipButton();
				} else {
					tabletBaseInputFragment.goneSkip();
				}
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
	 * Init value for controls
	 */
	private void initValueControl() {
		if (tabletBaseInputFragment == null) {
			return;
		}
		if (!nullOrEmpty(tabletBaseInputFragment.getInputApplyInfo().getReason())) {
			txtReason.setText(tabletBaseInputFragment.getInputApplyInfo().getReason());
		}
		setStatusControl();
		updateStatusSkipButton();
	}

	/**
	 * Set status for next/back button
	 */
	private void setStatusControl() {
		if (tabletBaseInputFragment.getCurrentPage() != 5) {
			return;
		}
		if (nullOrEmpty(txtReason.getText().toString())) {
			tabletBaseInputFragment.disableNext();
		} else {
			tabletBaseInputFragment.enableNext();
		}
	}

	private void updateStatusSkipButton() {
		if (txtReason.getText().toString().trim().length() == 0) {
			tabletBaseInputFragment.visibleSkip();
		} else {
			tabletBaseInputFragment.goneSkip();
		}
	}
	@Override
	public void nextAction() {
		tabletBaseInputFragment.getInputApplyInfo().setReason(txtReason.getText().toString().trim());
		tabletBaseInputFragment.getInputApplyInfo().savePref(getActivity());
		tabletBaseInputFragment.gotoPage(6);
	}

	@Override
	protected void clickSkipButton() {
		super.clickSkipButton();
		tabletBaseInputFragment.getInputApplyInfo().setReason(null);
		tabletBaseInputFragment.getInputApplyInfo().savePref(getActivity());
		tabletBaseInputFragment.gotoPage(6);
	}
}