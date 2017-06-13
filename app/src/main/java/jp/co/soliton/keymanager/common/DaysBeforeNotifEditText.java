package jp.co.soliton.keymanager.common;

import android.content.Context;
import android.support.v7.widget.AppCompatEditText;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.inputmethod.InputMethodManager;

/**
 * Created by luongdolong on 5/5/2016.
 */
public class DaysBeforeNotifEditText extends AppCompatEditText {
    OnKeyboardHidden mOnKeyboardHidden;

    public DaysBeforeNotifEditText(Context context)
    {
        super(context);
        init();
    }

    public DaysBeforeNotifEditText(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        init();
    }

    public DaysBeforeNotifEditText(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
        init();
    }

    private void init()
    { }

    public interface OnKeyboardHidden {
        public void onKeyboardHidden();
    }

    public void setOnKeyboardHidden(OnKeyboardHidden action) {
        mOnKeyboardHidden = action;
    }

    @Override
    public boolean onKeyPreIme(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP) {
            // User has pressed Back key. So hide the keyboard
            InputMethodManager imm = (InputMethodManager) getContext()
                    .getSystemService(Context.INPUT_METHOD_SERVICE);

            imm.hideSoftInputFromWindow(this.getWindowToken(), 0);
	        if (mOnKeyboardHidden != null) {
		        mOnKeyboardHidden.onKeyboardHidden();
	        }
        }
        return super.dispatchKeyEvent(event);
    }
}
