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

public class LeftSideInputPasswordTabletFragment extends Fragment {

	TextView tvValueHost;
	TextView tvValueUserId;
	TextView tvValueApplyDate;
	TextView tvValueStatus;

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_left_side_input_password_tablet, container, false);
		tvValueHost = (TextView) view.findViewById(R.id.tv_value_host);
		tvValueUserId = (TextView) view.findViewById(R.id.tv_value_user_id);
		tvValueApplyDate = (TextView) view.findViewById(R.id.tv_value_apply_date);
		tvValueStatus = (TextView) view.findViewById(R.id.tv_value_status);
		return view;
	}

	public void setTvValueHost(String str) {
		tvValueHost.setText(str);
	}
	public void setTvValueUserId(String str) {
		tvValueUserId.setText(str);
	}
	public void setTvValueApplyDate(String str) {
		tvValueApplyDate.setText(str);
	}
	public void setTvValueStatus(String str) {
		tvValueStatus.setText(str);
	}

	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
	}

}
