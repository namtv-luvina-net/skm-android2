package jp.co.soliton.keymanager.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import jp.co.soliton.keymanager.R;

import static jp.co.soliton.keymanager.R.id.zoneInputPlace;
import static jp.co.soliton.keymanager.fragment.TabletBaseInputFragment.TARGET_VPN;
import static jp.co.soliton.keymanager.fragment.TabletBaseInputFragment.TARGET_WiFi;

/**
 * Created by nguyenducdat on 4/25/2017.
 */

public class TabletInputPlaceFragment extends TabletInputFragment {

	private TextView titleInput;
	private RelativeLayout btnTargetVPN;
	private RelativeLayout btnTargetWiFi;
	private RelativeLayout zoneInputPlace;
	TabletBaseInputFragment tabletBaseInputFragment;
	public static Fragment newInstance(Context context, TabletBaseInputFragment tabletBaseInputFragment) {
		TabletInputPlaceFragment f = new TabletInputPlaceFragment();
		f.tabletBaseInputFragment = tabletBaseInputFragment;
		return f;
	}

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_input_store_tablet, container, false);
		zoneInputPlace = (RelativeLayout) view.findViewById(R.id.zoneInputPlace);
		btnTargetVPN = (RelativeLayout) view.findViewById(R.id.btnTargetVPN);
		btnTargetWiFi = (RelativeLayout) view.findViewById(R.id.btnTargetWifi);
		titleInput = (TextView) view.findViewById(R.id.titleInput);
		titleInput.setText(getString(R.string.target_place));
		if (tabletBaseInputFragment.d_android_version < 4.3){
			tabletBaseInputFragment.getInputApplyInfo().setPlace(TARGET_VPN);
			tabletBaseInputFragment.getInputApplyInfo().savePref(getActivity());
			zoneInputPlace.setVisibility(View.GONE);
		}
		return view;
	}

	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		//Execute action
		btnTargetVPN.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				tabletBaseInputFragment.getInputApplyInfo().setPlace(TARGET_VPN);
				tabletBaseInputFragment.gotoPage(3);
			}
		});
		btnTargetWiFi.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				tabletBaseInputFragment.getInputApplyInfo().setPlace(TARGET_WiFi);
				tabletBaseInputFragment.gotoPage(3);
			}
		});
	}

	@Override
	public void nextAction() {
	}
}