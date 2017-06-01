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

public class LeftSideInputTabletFragment extends Fragment {

	private View viewFragment;
	private TextView[] listTextTitle;
	private TextView tvBack;
	private int currentPositionHighlight = 0;
	private int startFrom = 0;

	public static Fragment newInstance(int startFrom) {
		LeftSideInputTabletFragment f = new LeftSideInputTabletFragment();
		f.startFrom = startFrom;
		return f;
	}

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		viewFragment = inflater.inflate(R.layout.fragment_left_side_input_tablet, container, false);
		tvBack = (TextView) viewFragment.findViewById(R.id.tvBack);
		listTextTitle = new TextView[7];
		listTextTitle[0] = (TextView) viewFragment.findViewById(R.id.tv_host_name_and_port_number);
		listTextTitle[1] = (TextView) viewFragment.findViewById(R.id.tv_download_ca_certificate);
		listTextTitle[2] = (TextView) viewFragment.findViewById(R.id.tv_select_store);
		listTextTitle[3] = (TextView) viewFragment.findViewById(R.id.tv_input_id_and_password);
		listTextTitle[4] = (TextView) viewFragment.findViewById(R.id.tv_input_email);
		listTextTitle[5] = (TextView) viewFragment.findViewById(R.id.tv_input_reason);
		listTextTitle[6] = (TextView) viewFragment.findViewById(R.id.tv_confirm_input);
		return viewFragment;
	}

	public void highlightItem(int possition, int startFrom) {
		if (currentPositionHighlight != possition) {
			currentPositionHighlight = possition;
		}
		if (this.startFrom != startFrom) {
			this.startFrom = startFrom;
		}
		if (tvBack != null) {
			if (possition == 0 && startFrom == TabletBaseInputFragment.START_FROM_MENU) {
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
				InputApplyInfo.deletePref(getActivity());
				((MenuAcivity)getActivity()).gotoMenuTablet();
			}
		});
		highlightItem(currentPositionHighlight, startFrom);
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		viewFragment = null;
	}
}
