package jp.co.soliton.keymanager.customview;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.Window;
import android.widget.RelativeLayout;
import android.widget.TextView;

import jp.co.soliton.keymanager.R;

/**
 * Created by lexuanvinh on 3/1/2017.
 */

public class DialogApplyConfirm extends Dialog {
    private TextView btnOK;
    private TextView btnCancel;
    private TextView txtDlgApplyMsg;
    private TextView txtDlgApplyTitle;
    private RelativeLayout zoneDlgApplyMsg;
    private Context context;

    public DialogApplyConfirm(Context context) {
        super(context);
        this.context = context;
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_apply_confirm);
        btnOK = (TextView) findViewById(R.id.btnDlgApplyOK);
        btnCancel = (TextView) findViewById(R.id.btnDlgApplyCancel);
        txtDlgApplyMsg = (TextView) findViewById(R.id.txtDlgApplyMsg);
        txtDlgApplyTitle = (TextView) findViewById(R.id.txtDlgApplyTitle);
        zoneDlgApplyMsg = (RelativeLayout)findViewById(R.id.zoneDlgApplyMsg);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        setCancelable(false);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
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
