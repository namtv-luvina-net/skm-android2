package jp.co.soliton.keymanager.fragment;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import jp.co.soliton.keymanager.InformCtrl;
import jp.co.soliton.keymanager.activity.ViewPagerInputActivity;
import jp.co.soliton.keymanager.customview.DialogApplyMessage;
import jp.co.soliton.keymanager.customview.DialogApplyProgressBar;

/**
 * Created by luongdolong on 2/8/2017.
 *
 * Processing base input page
 */

public class InputBasePageFragment extends Fragment {
    public final static int ERR_FORBIDDEN    = 20;
    public final static int ERR_UNAUTHORIZED = 21;
    public final static int SUCCESSFUL       = 22;
    public final static int ERR_NETWORK      = 23;
    public final static int ERR_COLON        = 24;
    public final static int NOT_INSTALL_CA   = 25;
    public final static int ERR_LOGIN_FAIL = 27;

    public final static String TARGET_VPN  = "0";
    public final static String TARGET_WiFi = "1";

    protected ViewPagerInputActivity pagerInputActivity;
    protected DialogApplyProgressBar progressDialog;
    protected InformCtrl m_InformCtrl;
    protected int m_nErroType;

    /**
     * Check null or empty string value
     *
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
     *
     * @param view
     * @param context
     */
    protected void hideKeyboard(View view, Context context) {
        InputMethodManager inputMethodManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    /**
     * Set runnable button back next
     *
     * @param enable
     */
    protected void setButtonRunnable(boolean enable) {
        pagerInputActivity.setActiveBackNext(enable, enable);
    }

    /**
     * Show message
     *
     * @param message
     */
    protected void showMessage(String message) {
        DialogApplyMessage dlgMessage = new DialogApplyMessage(getContext(), message);
        dlgMessage.show();
    }

    /**
     * Next action when click next button in every page
     */
    public void nextAction() {
    }
}
