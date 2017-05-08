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
import jp.co.soliton.keymanager.ValidateParams;

/**
 * Created by nguyenducdat on 4/25/2017.
 */

public class TabletInputEmailFragment extends TabletInputFragment {
	private EditText txtEmail;
	private TextView titleInput;
	TabletBaseInputFragment tabletBaseInputFragment;
	public static Fragment newInstance(Context context, TabletBaseInputFragment tabletBaseInputFragment) {
		TabletInputEmailFragment f = new TabletInputEmailFragment();
		f.tabletBaseInputFragment = tabletBaseInputFragment;
		return f;
	}

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		Log.d("datnd", "onCreateView: TabletInputEmailFragment");
		View view = inflater.inflate(R.layout.fragment_input_email_tablet, container, false);
		txtEmail = (EditText) view.findViewById(R.id.txtEmail);
		titleInput = (TextView) view.findViewById(R.id.titleInput);
		titleInput.setText(getString(R.string.set_notification_destination_email_address));
		return view;
	}

	@Override
	public void onResume() {
		super.onResume();
		Log.d("datnd", "onResume: TabletInputEmailFragment");
//		initValueControl();
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
					String email = txtEmail.getText().toString().trim();
					txtEmail.setText(email);
					hideKeyboard(v, getContext());
					updateStatusSkipButton();
				} else {
					tabletBaseInputFragment.goneSkip();
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
		if (!nullOrEmpty(tabletBaseInputFragment.getInputApplyInfo().getEmail())) {
			txtEmail.setText(tabletBaseInputFragment.getInputApplyInfo().getEmail());
		}
		setStatusControl();
		updateStatusSkipButton();
	}

	/**
	 * Set status for next/back button
	 */
	private void setStatusControl() {
		if (tabletBaseInputFragment.getCurrentPage() != 4) {
			return;
		}
		if (nullOrEmpty(txtEmail.getText().toString())) {
			tabletBaseInputFragment.disableNext();
		} else {
			tabletBaseInputFragment.enableNext();
		}
	}

	private void updateStatusSkipButton() {
		Log.d("datnd", "updateStatusSkipButton: update skip = " + txtEmail.getText().toString().trim().length());
		if (txtEmail.getText().toString().trim().length() == 0) {
			tabletBaseInputFragment.visibleSkip();
		} else {
			tabletBaseInputFragment.goneSkip();
		}
	}

	@Override
	public void nextAction() {
		Log.d("datnd", "nextAction: set email = " + txtEmail.getText().toString().trim());
		tabletBaseInputFragment.getInputApplyInfo().setEmail(txtEmail.getText().toString().trim());
		tabletBaseInputFragment.getInputApplyInfo().savePref(getActivity());
		if (!ValidateParams.isValidEmail(txtEmail.getText().toString().trim())) {
			tabletBaseInputFragment.showMessage(getString(R.string.apply_mail_error));
			return;
		}
		tabletBaseInputFragment.gotoPage(5);
	}

	@Override
	protected void clickSkipButton() {
		super.clickSkipButton();
		tabletBaseInputFragment.getInputApplyInfo().setEmail(null);
		tabletBaseInputFragment.getInputApplyInfo().savePref(getActivity());
		tabletBaseInputFragment.gotoPage(5);
	}
}