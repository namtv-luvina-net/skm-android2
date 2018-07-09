package jp.co.soliton.keymanager.activity;

import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import jp.co.soliton.keymanager.R;
import jp.co.soliton.keymanager.common.CommonUtils;

/**
 * Created by nguyenducdat on 6/19/2017.
 */

public class BaseSettingPhoneActivity extends FragmentActivity {

	protected TextView textViewBack;
	protected TextView tvTitleHeader;
	protected Button btnMenuDetailSetting;

	@Override
	protected void onStart() {
		super.onStart();
		tvTitleHeader = findViewById(R.id.tvTitleHeader);
		textViewBack = findViewById(R.id.textViewBack);
		btnMenuDetailSetting = findViewById(R.id.btnMenuDetailSetting);
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
		CommonUtils.updateHeader(this, textViewBack, tvTitleHeader);
	}

	protected void setTextBtnBack() {
		textViewBack.setText(getString(R.string.label_setting));
	}

	protected void setTextTitle() {
	}

	protected void initBtnMenuDetailSetting() {	}
}
