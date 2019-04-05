package jp.co.soliton.keymanager.fragment;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import jp.co.soliton.keymanager.R;

import static jp.co.soliton.keymanager.manager.APIDManager.TARGET_VPN;
import static jp.co.soliton.keymanager.manager.APIDManager.TARGET_WiFi;

/**
 * Created by nguyenducdat on 4/25/2017.
 */

public class TabletInputPlaceFragment extends TabletInputFragment {

	private TextView titleInput;
	private RelativeLayout btnTargetVPN;
	private RelativeLayout btnTargetWiFi;
	private RelativeLayout zoneInputPlace;
	TabletAbtractInputFragment tabletAbtractInputFragment;
	public static Fragment newInstance(Context context, TabletAbtractInputFragment tabletAbtractInputFragment) {
		TabletInputPlaceFragment f = new TabletInputPlaceFragment();
		f.tabletAbtractInputFragment = tabletAbtractInputFragment;
		return f;
	}

	@Override
	public void onSaveInstanceState(Bundle savedInstanceState) {
		super.onSaveInstanceState(savedInstanceState);
		getActivity().getSupportFragmentManager().putFragment(savedInstanceState, TAG_TABLET_BASE_INPUT_FRAGMENT, tabletAbtractInputFragment);
	}

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		if (savedInstanceState != null) {
			tabletAbtractInputFragment = (TabletBaseInputFragment) getActivity().getSupportFragmentManager().getFragment(savedInstanceState,
					TAG_TABLET_BASE_INPUT_FRAGMENT);
		}
		View view = inflater.inflate(R.layout.fragment_input_store_tablet, container, false);
		zoneInputPlace = view.findViewById(R.id.zoneInputPlace);
		btnTargetVPN = view.findViewById(R.id.btnTargetVPN);
		btnTargetWiFi = view.findViewById(R.id.btnTargetWifi);
		titleInput = view.findViewById(R.id.titleInput);
		titleInput.setText(getString(R.string.target_place));

		if (tabletAbtractInputFragment.sdk_int_version < Build.VERSION_CODES.JELLY_BEAN_MR2){
			tabletAbtractInputFragment.getInputApplyInfo().setPlace(TARGET_VPN);
			tabletAbtractInputFragment.getInputApplyInfo().savePref(getActivity());
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
				tabletAbtractInputFragment.getInputApplyInfo().setPlace(TARGET_VPN);
				tabletAbtractInputFragment.gotoPage(3);
			}
		});
		btnTargetWiFi.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				tabletAbtractInputFragment.getInputApplyInfo().setPlace(TARGET_WiFi);
				tabletAbtractInputFragment.gotoPage(3);
			}
		});
	}

	@Override
	public void nextAction() {
	}
}