package jp.co.soliton.keymanager.activity;

import android.app.Activity;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import jp.co.soliton.keymanager.R;

/**
 * Created by nguyenducdat on 6/19/2017.
 */

public class BaseSettingPhoneActivity extends Activity {

	protected TextView textViewBack;
	protected TextView tvTitleHeader;
	protected Button btnMenuDetailSetting;

	@Override
	protected void onStart() {
		super.onStart();
		tvTitleHeader = (TextView) findViewById(R.id.tvTitleHeader);
		textViewBack = (TextView) findViewById(R.id.textViewBack);
		btnMenuDetailSetting = (Button) findViewById(R.id.btnMenuDetailSetting);
	}

	public void btnBackClick(View v) {
		finish();
	}

	@Override
	protected void onResume() {
		super.onResume();
		setTextBtnBack();
		setTextTitle();
		initBtnMenuDetailSetting();
		updateHeader();
	}

	protected void setTextBtnBack() {
		textViewBack.setText(getString(R.string.label_setting));
	}

	protected void setTextTitle() {
	}

	protected void initBtnMenuDetailSetting() {	}

	private void updateHeader() {
		tvTitleHeader.measure(0, 0);
		textViewBack.measure(0, 0);
		DisplayMetrics displayMetrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
		int width = displayMetrics.widthPixels;
		if (tvTitleHeader.getMeasuredWidth() > width - (textViewBack.getMeasuredWidth() * 2)) {
			textViewBack.setText("");
			textViewBack.measure(0, 0);
			int tmp = textViewBack.getMeasuredWidth() * 2;
			RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) tvTitleHeader.getLayoutParams();
			params.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
			params.setMargins(tmp, params.topMargin, tmp, params.bottomMargin);
			tvTitleHeader.setLayoutParams(params);
		}
	}
}
