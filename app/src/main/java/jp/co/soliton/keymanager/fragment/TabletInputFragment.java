package jp.co.soliton.keymanager.fragment;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

/**
 * Created by nguyenducdat on 5/4/2017.
 */

public abstract class TabletInputFragment extends Fragment {

	public static final String TAG_TABLET_BASE_INPUT_FRAGMENT = "tabletBaseInputFragment";

	/**
	 * Check null or empty string value
	 * @param value
	 * @return
	 */
	protected boolean nullOrEmpty(String value) {
		if (value == null) {
			return true;
		}
		return value.trim().isEmpty();
	}

	/**
	 * Hide keyboard in edit text controls
	 * @param view
	 * @param context
	 */
	protected void hideKeyboard(View view, Context context) {
		InputMethodManager inputMethodManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
		inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
	}

	/**
	 * Next action when click next button in every page
	 */
	public abstract void nextAction();
	protected void clickSkipButton(){}
}
