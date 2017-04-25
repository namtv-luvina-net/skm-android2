package jp.co.soliton.keymanager.fragment;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import jp.co.soliton.keymanager.R;
import jp.co.soliton.keymanager.activity.SettingActivity;

/**
 * Created by nguyenducdat on 4/25/2017.
 */

public class LeftSideMenuTabletFragment extends Fragment {

	Button btnSetting;

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_left_side_menu_tablet, container, false);
	}

	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		btnSetting = (Button) getActivity().findViewById(R.id.btnSetting);
		btnSetting.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getActivity(), SettingActivity.class);
				startActivity(intent);
			}
		});
	}
}
