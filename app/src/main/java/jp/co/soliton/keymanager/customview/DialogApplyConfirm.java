package jp.co.soliton.keymanager.customview;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
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
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.dialog_apply_confirm);
		btnOK = findViewById(R.id.btnDlgApplyOK);
		btnCancel = findViewById(R.id.btnDlgApplyCancel);
		txtDlgApplyMsg = findViewById(R.id.txtDlgApplyMsg);
		txtDlgApplyTitle = findViewById(R.id.txtDlgApplyTitle);
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
		lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
		lp.gravity = Gravity.CENTER;

		getWindow().setAttributes(lp);
	}

	public void setTextDisplay(String title, String message, String btnCancel, String btnOK) {
		if (title == null || title.length() == 0) {
			txtDlgApplyTitle.setVisibility(View.GONE);
		} else {
			txtDlgApplyTitle.setVisibility(View.VISIBLE);
			txtDlgApplyTitle.setText(title);
		}
		txtDlgApplyMsg.setText(message);
		if (btnOK != null && btnOK.length() > 0) {
			this.btnOK.setText(btnOK);
		}
		if (btnCancel != null && btnCancel.length() > 0) {
			this.btnCancel.setText(btnCancel);
		}
	}

    public void setOnClickOK(View.OnClickListener listener) {
        btnOK.setOnClickListener(listener);
        this.dismiss();
    }

    public void setOnClickCancel(View.OnClickListener listener) {
        btnCancel.setOnClickListener(listener);
	    this.dismiss();
    }

}
