package jp.co.soliton.keymanager.activity;

import android.os.Bundle;
import jp.co.soliton.keymanager.R;

/**
 * Created by luongdolong on 3/31/2017.
 */

public class LibraryInfoActivity extends BaseSettingPhoneActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_library_info);
    }

	@Override
	protected void setTextBtnBack() {
		textViewBack.setText(getString(R.string.label_setting));
	}

	@Override
	protected void setTextTitle() {
		tvTitleHeader.setText(getString(R.string.label_library_setting));
	}
}
