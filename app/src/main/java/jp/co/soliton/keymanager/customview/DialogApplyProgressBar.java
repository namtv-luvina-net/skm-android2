package jp.co.soliton.keymanager.customview;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Window;
import jp.co.soliton.keymanager.R;

/**
 * Created by luongdolong on 2/8/2017.
 */

public class DialogApplyProgressBar extends Dialog {

    /**
     * Constructor
     *
     * @param context This is context of view
     */
    public DialogApplyProgressBar(Context context) {
        super(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setCancelable(false);

        getWindow().setBackgroundDrawableResource(
                android.R.color.transparent);
        setContentView(R.layout.dialog_apply_progress);
    }
}
