package jp.co.soliton.keymanager.fragment;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import jp.co.soliton.keymanager.R;
import jp.co.soliton.keymanager.activity.MenuAcivity;

/**
 * Created by nguyenducdat on 4/25/2017.
 */

public class LeftSideListConfirmTabletFragment extends Fragment {
	private View viewFragment;
	private TextView tvTitle;
	private TextView tvBack;
	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		viewFragment = inflater.inflate(R.layout.fragment_left_side_list_confirm_tablet, container, false);
		tvTitle = viewFragment.findViewById(R.id.tvTitle);
		tvTitle.setText(getString(R.string.list_application));
		tvBack = viewFragment.findViewById(R.id.tvBack);
		return viewFragment;
	}

	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
	}

	@Override
	public void onResume() {
		super.onResume();
		tvBack.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				((MenuAcivity)getActivity()).gotoMenuTablet();
			}
		});
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		viewFragment = null;
	}
}
