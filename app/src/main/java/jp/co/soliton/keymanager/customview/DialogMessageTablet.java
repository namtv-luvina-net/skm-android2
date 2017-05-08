package jp.co.soliton.keymanager.customview;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import jp.co.soliton.keymanager.R;

/**
 * Created by luongdolong on 2/8/2017.
 */

public class DialogMessageTablet extends Dialog {
    private Button btnOK;
    private TextView txtDlgApplyMsg;
    private TextView txtTitle;
    private RelativeLayout zoneDlgApplyMsg;
    private Context context;

    private OnOkDismissMessageListener listener;

    public interface OnOkDismissMessageListener {
        void onOkDismissMessage();
    }

    public void setOnOkDismissMessageListener(OnOkDismissMessageListener listener) {
        this.listener = listener;
    }

    public DialogMessageTablet(Context context, String message) {
        super(context);
        this.context = context;
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_message_tablet);
        btnOK = (Button) findViewById(R.id.btnDlgApplyOK);
        txtDlgApplyMsg = (TextView) findViewById(R.id.txtDlgApplyMsg);
        txtDlgApplyMsg.setText(message);
        txtTitle = (TextView) findViewById(R.id.txtTitle);
        zoneDlgApplyMsg = (RelativeLayout)findViewById(R.id.zoneDlgApplyMsg);
        btnOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                if (listener != null) {
                    listener.onOkDismissMessage();
                }
            }
        });
        setCancelable(false);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(getWindow().getAttributes());
        lp.width = (int)(context.getResources().getDisplayMetrics().widthPixels*0.8f);
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.gravity = Gravity.CENTER;

        getWindow().setAttributes(lp);
    }

    public void setTitleDialog(String title) {
        txtTitle.setText(title);
        txtTitle.setTypeface(null, Typeface.BOLD);
    }

    @Override
    public void onWindowFocusChanged (boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        adjustControl();
    }

    private void adjustControl() {
        DisplayMetrics displaymetrics = new DisplayMetrics();
        ((Activity)context).getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        final float scale = displaymetrics.density;
        zoneDlgApplyMsg.getLayoutParams().height = (int)((140 + 1) * scale + txtDlgApplyMsg.getMeasuredHeight());
    }
}
