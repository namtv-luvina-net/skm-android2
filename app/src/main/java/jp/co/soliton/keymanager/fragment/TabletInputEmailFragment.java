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
import android.widget.TextView;
import jp.co.soliton.keymanager.R;
import jp.co.soliton.keymanager.ValidateParams;
import jp.co.soliton.keymanager.common.SoftKeyboardCtrl;

/**
 * Created by nguyenducdat on 4/25/2017.
 */

public class TabletInputEmailFragment extends TabletInputFragment {
	private EditText txtEmail;
	private TextView titleInput;
	TabletAbtractInputFragment tabletAbtractInputFragment;
	public static Fragment newInstance(Context context, TabletAbtractInputFragment tabletAbtractInputFragment) {
		TabletInputEmailFragment f = new TabletInputEmailFragment();
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
		viewFragment = inflater.inflate(R.layout.fragment_input_email_tablet, container, false);
		txtEmail = (EditText) viewFragment.findViewById(R.id.txtEmail);
		titleInput = (TextView) viewFragment.findViewById(R.id.titleInput);
		titleInput.setText(getString(R.string.set_notification_destination_email_address));
		return viewFragment;
	}

	@Override
	public void onResume() {
		super.onResume();
		addListenerForEditText();
	}

	private void addListenerForEditText() {
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
					tabletAbtractInputFragment.getInputApplyInfo().setEmail(txtEmail.getText().toString().trim());
					tabletAbtractInputFragment.getInputApplyInfo().savePref(getActivity());
					String email = txtEmail.getText().toString().trim();
					txtEmail.setText(email);
					SoftKeyboardCtrl.hideKeyboard(v, getContext());
					updateStatusSkipButton();
				} else {
					tabletAbtractInputFragment.goneSkip();
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
		if (!nullOrEmpty(tabletAbtractInputFragment.getInputApplyInfo().getEmail())) {
			txtEmail.setText(tabletAbtractInputFragment.getInputApplyInfo().getEmail());
		}else {
			txtEmail.setText("");
		}
		setStatusControl();
		updateStatusSkipButton();
	}

	/**
	 * Set status for next/back button
	 */
	private void setStatusControl() {
		if (nullOrEmpty(txtEmail.getText().toString())) {
			tabletAbtractInputFragment.disableNext();
		} else {
			tabletAbtractInputFragment.enableNext();
		}
	}

	private void updateStatusSkipButton() {
		if (txtEmail.getText().toString().trim().length() == 0) {
			tabletAbtractInputFragment.visibleSkip();
		} else {
			tabletAbtractInputFragment.goneSkip();
		}
	}

	@Override
	public void nextAction() {
		tabletAbtractInputFragment.getInputApplyInfo().setEmail(txtEmail.getText().toString().trim());
		tabletAbtractInputFragment.getInputApplyInfo().savePref(getActivity());
		if (!ValidateParams.isValidEmail(txtEmail.getText().toString().trim())) {
			tabletAbtractInputFragment.showMessage(getString(R.string.apply_mail_error));
			return;
		}
		tabletAbtractInputFragment.gotoNextPage();
	}

	@Override
	protected void clickSkipButton() {
		super.clickSkipButton();
		tabletAbtractInputFragment.getInputApplyInfo().setEmail(null);
		tabletAbtractInputFragment.getInputApplyInfo().savePref(getActivity());
		tabletAbtractInputFragment.gotoNextPage();
	}
}