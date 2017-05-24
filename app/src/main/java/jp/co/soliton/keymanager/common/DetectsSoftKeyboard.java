package jp.co.soliton.keymanager.common;

import android.graphics.Rect;
import android.view.View;
import android.view.ViewTreeObserver;

/**
 * Created by nguyenducdat on 4/19/2017.
 */

public class DetectsSoftKeyboard {
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
}
