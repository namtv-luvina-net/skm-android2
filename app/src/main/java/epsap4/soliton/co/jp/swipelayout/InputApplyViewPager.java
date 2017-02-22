package epsap4.soliton.co.jp.swipelayout;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * Created by luongdolong on 2/7/2017.
 */

public class InputApplyViewPager extends ViewPager{
    private boolean enabled;
    public InputApplyViewPager(Context context) {
        super(context);
    }

    public InputApplyViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.enabled = true;
    }
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (this.enabled) {
            return super.onTouchEvent(event);
        }
        return false;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        if (this.enabled) {
            return super.onInterceptTouchEvent(event);
        }
        return false;
    }

    // To enable/disable swipe
    public void setPagingEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}
