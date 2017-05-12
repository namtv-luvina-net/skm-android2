package jp.co.soliton.keymanager.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import jp.co.soliton.keymanager.InformCtrl;
import jp.co.soliton.keymanager.InputApplyInfo;
import jp.co.soliton.keymanager.R;
import jp.co.soliton.keymanager.activity.MenuAcivity;
import jp.co.soliton.keymanager.dbalias.ElementApply;

/**
 * Created by nguyenducdat on 4/25/2017.
 */

public class LeftSideInputTabletFragment extends Fragment {

	RelativeLayout rootViewContent;
	TextView[] listTextTitle;
	TextView tvBack;
	int currentPositionHighlight = 0;

	public static Fragment newInstance() {
		LeftSideInputTabletFragment f = new LeftSideInputTabletFragment();
		return f;
	}

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_left_side_input_tablet, container, false);
		tvBack = (TextView) view.findViewById(R.id.tvBack);
		rootViewContent = (RelativeLayout) view.findViewById(R.id.rootViewContent);
		listTextTitle = new TextView[7];
		listTextTitle[0] = (TextView) view.findViewById(R.id.tv_host_name_and_port_number);
		listTextTitle[1] = (TextView) view.findViewById(R.id.tv_download_ca_certificate);
		listTextTitle[2] = (TextView) view.findViewById(R.id.tv_select_store);
		listTextTitle[3] = (TextView) view.findViewById(R.id.tv_input_id_and_password);
		listTextTitle[4] = (TextView) view.findViewById(R.id.tv_input_email);
		listTextTitle[5] = (TextView) view.findViewById(R.id.tv_input_reason);
		listTextTitle[6] = (TextView) view.findViewById(R.id.tv_confirm_input);
		return view;
	}

	public void highlightItem(int possition) {
		if (currentPositionHighlight != possition) {
			currentPositionHighlight = possition;
		}
		if (tvBack != null) {
			if (possition == 0) {
				tvBack.setVisibility(View.VISIBLE);
			} else {
				tvBack.setVisibility(View.INVISIBLE);
			}
		}
		if (listTextTitle == null) {
			return;
		}
		for (int i = 0 ; i < listTextTitle.length; i++) {
			if (i == possition) {
				listTextTitle[i].setTextColor(getResources().getColor(R.color.color_title_menu));
			}else {
				listTextTitle[i].setTextColor(getResources().getColor(R.color.color_title_menu_50));
			}
		}
	}

	public void hideContent() {
		rootViewContent.setVisibility(View.GONE);
	}
	public void showContent() {
		rootViewContent.setVisibility(View.VISIBLE);
	}

	@Override
	public void onResume() {
		super.onResume();
		tvBack.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				InputApplyInfo.deletePref(getActivity());
				((MenuAcivity)getActivity()).goToMenu();
			}
		});
		highlightItem(currentPositionHighlight);
	}
}
