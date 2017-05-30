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

	TextView tvTitle;
	TextView tvBack;
	TextView tvDes;

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_left_side_list_confirm_tablet, container, false);
		tvTitle = (TextView) view.findViewById(R.id.tvTitle);
		tvTitle.setText(getString(R.string.approval_confirmation));
		tvBack = (TextView) view.findViewById(R.id.tvBack);
		tvDes = (TextView) view.findViewById(R.id.tv_description);
		return view;
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
}
