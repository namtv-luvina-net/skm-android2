package jp.co.soliton.keymanager.common;

import android.app.Activity;
import android.content.Context;
import android.graphics.Rect;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import static android.content.Context.INPUT_METHOD_SERVICE;

/**
 * Created by nguyenducdat on 4/19/2017.
 */

public class SoftKeyboardCtrl {
	public interface DetectsListenner  {
		void onSoftKeyboardShown (boolean isShowing);
	}

	public static void addListenner(final View view, final DetectsListenner detectsListenner){
		view.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
			@Override
			public void onGlobalLayout() {
				Rect r = new Rect();
				view.getWindowVisibleDisplayFrame(r);
				int screenHeight = view.getRootView().getHeight();
				// r.bottom is the position above soft keypad or device button.
				// if keypad is shown, the r.bottom is smaller than that before.
				int keypadHeight = screenHeight - r.bottom;
				if (keypadHeight > screenHeight * 0.15) { // 0.15 ratio is perhaps enough to determine keypad height.
					detectsListenner.onSoftKeyboardShown(true);
				} else {
					detectsListenner.onSoftKeyboardShown(false);
				}
			}
		});
	}

	public static void hideKeyboard(Activity activity) {
		if (activity != null && activity.getWindow() != null && activity.getWindow().getDecorView() != null) {
			InputMethodManager imm = (InputMethodManager)activity.getSystemService(INPUT_METHOD_SERVICE);
			imm.hideSoftInputFromWindow(activity.getWindow().getDecorView().getWindowToken(), 0);
		}
	}

	/**
	 * Hide keyboard in edit text controls
	 * @param view
	 * @param context
	 */
	public static void hideKeyboard(View view, Context context) {
		InputMethodManager inputMethodManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
		inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
	}

	public static boolean hideKeyboardIfTouchOutEditText(Activity activity, MotionEvent ev) {
		View v = activity.getCurrentFocus();
		if (v != null && (ev.getAction() == MotionEvent.ACTION_UP || ev.getAction() == MotionEvent.ACTION_MOVE) &&
				v instanceof EditText && !v.getClass().getName().startsWith("android.webkit.")) {
			int scrcoords[] = new int[2];
			v.getLocationOnScreen(scrcoords);
			float x = ev.getRawX() + v.getLeft() - scrcoords[0];
			float y = ev.getRawY() + v.getTop() - scrcoords[1];
			if (x < v.getLeft() || x > v.getRight() || y < v.getTop() || y > v.getBottom()) {
				SoftKeyboardCtrl.hideKeyboard(activity);
				v.clearFocus();
			}
		}
		return true;
	}
}
