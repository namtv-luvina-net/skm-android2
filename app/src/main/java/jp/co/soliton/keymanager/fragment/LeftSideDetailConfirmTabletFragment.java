package jp.co.soliton.keymanager.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import jp.co.soliton.keymanager.R;
import jp.co.soliton.keymanager.activity.DetailConfirmActivity;
import jp.co.soliton.keymanager.activity.MenuAcivity;

/**
 * Created by nguyenducdat on 4/25/2017.
 */

public class LeftSideDetailConfirmTabletFragment extends Fragment {

	private TextView tvTitle;
	private TextView tvBack;
	private TextView tvDes;
	private View viewFragment;

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		viewFragment = inflater.inflate(R.layout.fragment_left_side_list_confirm_tablet, container, false);
		tvTitle = (TextView) viewFragment.findViewById(R.id.tvTitle);
		tvTitle.setText(getString(R.string.approval_confirmation));
		tvBack = (TextView) viewFragment.findViewById(R.id.tvBack);
		tvDes = (TextView) viewFragment.findViewById(R.id.tv_description);
		return viewFragment;
	}

	public void setTextDes(String str) {
		tvDes.setText(str);
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
				((MenuAcivity)getActivity()).onBackPressedFromDetailCetificate();
			}
		});
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		viewFragment = null;
	}
}
