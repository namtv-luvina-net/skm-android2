package jp.co.soliton.keymanager.fragment;

import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import jp.co.soliton.keymanager.R;

import static jp.co.soliton.keymanager.activity.SettingTabletActivity.RATIO_SCALE_WIDTH;

/**
 * Created by nguyenducdat on 4/25/2017.
 */

public class TabletBaseSettingFragment extends Fragment {

	protected View viewFragment;
	protected TextView textViewBack;
	protected TextView tvTitleHeader;
	protected Button moreOption;

	@Override
	public void onResume() {
		super.onResume();
		setTextBtnBack();
		textViewBack.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				getActivity().onBackPressed();
			}
		});
		updateTitle();
	}

	protected void setTextBtnBack() {
		textViewBack.setText(getString(R.string.label_setting));
	}

	private void updateTitle() {
		tvTitleHeader.measure(0, 0);
		textViewBack.measure(0, 0);
		DisplayMetrics displayMetrics = new DisplayMetrics();
		getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
		int width = (int) (displayMetrics.widthPixels * RATIO_SCALE_WIDTH);

		RelativeLayout.LayoutParams paramBackBtn = (RelativeLayout.LayoutParams) textViewBack.getLayoutParams();
		if (tvTitleHeader.getMeasuredWidth() > width - ((textViewBack.getMeasuredWidth() + paramBackBtn.leftMargin) * 2)) {
			textViewBack.setText("");
			textViewBack.measure(0, 0);

			int tmp = textViewBack.getMeasuredWidth() + + paramBackBtn.leftMargin;
			RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) tvTitleHeader.getLayoutParams();
			params.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
			params.setMargins(tmp, params.topMargin, tmp, params.bottomMargin);
			tvTitleHeader.setLayoutParams(params);
		}
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		viewFragment = null;
	}
}
