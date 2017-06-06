package jp.co.soliton.keymanager.fragment;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import jp.co.soliton.keymanager.BuildConfig;
import jp.co.soliton.keymanager.R;
import jp.co.soliton.keymanager.asynctask.ProcessInfoAndZipTask;
import jp.co.soliton.keymanager.common.EmailCtrl;

/**
 * Created by nguyenducdat on 4/25/2017.
 */

public class ContentLibrarySettingFragment extends Fragment {

	private View viewFragment;
	private TextView textViewBack;
	private TextView tvTitleHeader;

	public static Fragment newInstance() {
		ContentLibrarySettingFragment f = new ContentLibrarySettingFragment();
		return f;
	}

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		viewFragment = inflater.inflate(R.layout.fragment_library_info, container, false);
		textViewBack = (TextView) viewFragment.findViewById(R.id.textViewBack);
		textViewBack.setText(getString(R.string.label_settings));
		tvTitleHeader = (TextView) viewFragment.findViewById(R.id.tvTitleHeader);
		tvTitleHeader.setText(getString(R.string.label_library_setting));
		return viewFragment;
	}

	@Override
	public void onResume() {
		super.onResume();
		textViewBack.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				getActivity().onBackPressed();
			}
		});
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		viewFragment = null;
	}
}
