package jp.co.soliton.keymanager.customview;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import jp.co.soliton.keymanager.R;

/**
 * Created by luongdolong on 2/8/2017.
 */

public class DialogApplyMessage extends Dialog {
    private Button btnOK;
    private TextView txtDlgApplyMsg;
    private TextView txtTitle;

    private OnOkDismissMessageListener listener;

    public interface OnOkDismissMessageListener {
        void onOkDismissMessage();
    }

    public void setOnOkDismissMessageListener(OnOkDismissMessageListener listener) {
        this.listener = listener;
    }

	public DialogApplyMessage(Context context, String message) {
		super(context);
		constructorDialog(message);
	}

	private void constructorDialog(String message) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.dialog_apply_message);
		btnOK = findViewById(R.id.btnDlgApplyOK);
		txtDlgApplyMsg = findViewById(R.id.txtDlgApplyMsg);
		txtDlgApplyMsg.setText(message);
		txtTitle = findViewById(R.id.txtTitle);
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
		lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
		lp.gravity = Gravity.CENTER;
		getWindow().setAttributes(lp);
	}

	public void setTitleDialog(String title) {
		if (title == null || title.length() == 0) {
			txtTitle.setVisibility(View.GONE);
		} else {
			txtTitle.setVisibility(View.VISIBLE);
			txtTitle.setText(title);
		}
	}
}
