package jp.co.soliton.keymanager.fragment;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import jp.co.soliton.keymanager.R;

/**
 * Created by nguyenducdat on 4/25/2017.
 */

public class LeftSideInputPasswordTabletFragment extends Fragment {

	private TextView tvValueHost;
	private TextView tvValueUserId;
	private TextView tvValueApplyDate;
	private TextView tvValueStatus;
	private TextView tvStorage;
	private String[] listData;
	private View viewFragment;

	public static Fragment newInstance(String[] listData) {
		LeftSideInputPasswordTabletFragment f = new LeftSideInputPasswordTabletFragment();
		f.listData = listData;
		return f;
	}


	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		viewFragment = inflater.inflate(R.layout.fragment_left_side_input_password_tablet, container, false);
		tvValueHost = viewFragment.findViewById(R.id.tv_value_host);
		tvValueUserId = viewFragment.findViewById(R.id.tv_value_user_id);
		tvValueApplyDate = viewFragment.findViewById(R.id.tv_value_apply_date);
		tvValueStatus = viewFragment.findViewById(R.id.tv_value_status);
		tvStorage = viewFragment.findViewById(R.id.tv_storage);
		updateLeftsideInputPasswordTablet();
		return viewFragment;
	}

	private void updateLeftsideInputPasswordTablet() {
		if (listData == null || listData.length != 5) return;
		setTvValueHost(listData[0]);
		setTvValueUserId(listData[1]);
		setTvStorage(listData[2]);
		setTvValueApplyDate(listData[3]);
		setTvValueStatus(listData[4]);
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
	public void setTvStorage(String str) {
		tvStorage.setText(str);
	}

	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		viewFragment = null;
	}
}
