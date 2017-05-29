package jp.co.soliton.keymanager.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import jp.co.soliton.keymanager.InputApplyInfo;
import jp.co.soliton.keymanager.R;
import jp.co.soliton.keymanager.activity.MenuAcivity;

/**
 * Created by nguyenducdat on 4/25/2017.
 */

public class LeftSideListCertAndReapplyTabletFragment extends Fragment {

	TextView[] listTextTitle;
	TextView tvBack;
	int currentPositionHighlight = 0;

	public static Fragment newInstance() {
		LeftSideListCertAndReapplyTabletFragment f = new LeftSideListCertAndReapplyTabletFragment();
		return f;
	}

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_leftside_list_certificate_and_reapply, container, false);
		tvBack = (TextView) view.findViewById(R.id.tvBack);
		listTextTitle = new TextView[5];
		listTextTitle[0] = (TextView) view.findViewById(R.id.tvListCertificate);
		listTextTitle[1] = (TextView) view.findViewById(R.id.tvInputPassword);
		listTextTitle[2] = (TextView) view.findViewById(R.id.tvInputEmail);
		listTextTitle[3] = (TextView) view.findViewById(R.id.tvInputReason);
		listTextTitle[4] = (TextView) view.findViewById(R.id.tvConfirmApply);
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

	@Override
	public void onResume() {
		super.onResume();
		tvBack.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				((MenuAcivity)getActivity()).gotoMenuTablet();
			}
		});
		highlightItem(currentPositionHighlight);
	}
}
