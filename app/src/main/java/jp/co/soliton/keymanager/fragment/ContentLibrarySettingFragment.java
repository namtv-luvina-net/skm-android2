package jp.co.soliton.keymanager.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import jp.co.soliton.keymanager.R;

/**
 * Created by nguyenducdat on 4/25/2017.
 */

public class ContentLibrarySettingFragment extends TabletBaseSettingFragment {

	public static Fragment newInstance() {
		ContentLibrarySettingFragment f = new ContentLibrarySettingFragment();
		return f;
	}

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		viewFragment = inflater.inflate(R.layout.fragment_library_info, container, false);
		textViewBack = viewFragment.findViewById(R.id.textViewBack);
		tvTitleHeader = viewFragment.findViewById(R.id.tvTitleHeader);
		tvTitleHeader.setText(getString(R.string.label_library_setting));
		return viewFragment;
	}
}
