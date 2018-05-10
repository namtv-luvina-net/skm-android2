package jp.co.soliton.keymanager.customview;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.NumberPicker;
import android.widget.TextView;
import jp.co.soliton.keymanager.R;

public class DialogPicker extends Dialog {
	private TextView btnOK;
	private TextView btnCancel;
	private NumberPicker numberPicker;

	public interface ClickListener {
		void clickApply(int newValue);
	}

	public DialogPicker(Context context, int notifyBeforeCurrent, final ClickListener clickListener) {
		super(context);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.dialog_picker);
		btnOK = findViewById(R.id.btnDlgApplyOK);
		btnCancel = findViewById(R.id.btnDlgApplyCancel);
		numberPicker = findViewById(R.id.number_picker);
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
		initPicker(notifyBeforeCurrent);
		btnOK.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				clickListener.clickApply(numberPicker.getValue());
				dismiss();
			}
		});
		btnCancel.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				dismiss();
			}
		});
	}

	private void initPicker(int notifyBeforeCurrent) {
		String[] data = new String[120];
		for (int i = 0; i < data.length; i++) {
			data[i] = String.valueOf(i + 1);
		}
		numberPicker.setMinValue(1);
		numberPicker.setMaxValue(data.length);
		numberPicker.setDisplayedValues(data);
		numberPicker.setValue(notifyBeforeCurrent);
		numberPicker.setWrapSelectorWheel(true);
	}
}
