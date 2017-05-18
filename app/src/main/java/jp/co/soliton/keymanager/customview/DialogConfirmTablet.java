package jp.co.soliton.keymanager.customview;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;
import jp.co.soliton.keymanager.R;

/**
 * Created by lexuanvinh on 3/1/2017.
 */

public class DialogConfirmTablet extends Dialog {
    private TextView btnOK;
    private TextView btnCancel;
    private TextView txtDlgApplyMsg;
    private TextView txtDlgApplyTitle;
    private LinearLayout zoneDlgApplyMsg;
    private Context context;

    public DialogConfirmTablet(Context context) {
        super(context);
        this.context = context;
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_confirm_tablet);
        btnOK = (TextView) findViewById(R.id.btnDlgApplyOK);
        btnCancel = (TextView) findViewById(R.id.btnDlgApplyCancel);
        txtDlgApplyMsg = (TextView) findViewById(R.id.txtDlgApplyMsg);
        txtDlgApplyTitle = (TextView) findViewById(R.id.txtDlgApplyTitle);
        zoneDlgApplyMsg = (LinearLayout)findViewById(R.id.zoneDlgApplyMsg);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
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

    public void setTextDisplay(String title, String message, String btnCancel, String btnOK) {
        txtDlgApplyTitle.setText(title);
        txtDlgApplyMsg.setText(message);
        this.btnOK.setText(btnOK);
        this.btnCancel.setText(btnCancel);
    }

    public void setOnClickOK(View.OnClickListener listener) {
        btnOK.setOnClickListener(listener);
    }

    public void setOnClickCancel(View.OnClickListener listener) {
        btnCancel.setOnClickListener(listener);
    }

}
