package jp.co.soliton.keymanager.fragment;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import jp.co.soliton.keymanager.R;
import jp.co.soliton.keymanager.StringList;
import jp.co.soliton.keymanager.activity.MenuAcivity;
import jp.co.soliton.keymanager.dbalias.ElementApply;
import jp.co.soliton.keymanager.manager.APIDManager;

import java.util.List;

import static jp.co.soliton.keymanager.common.TypeScrollFragment.SCROLL_TO_LEFT;

/**
 * Created by nguyenducdat on 4/25/2017.
 */

public class ContentMenuTabletFragment extends Fragment {

	RelativeLayout rlMenuStart;
	RelativeLayout rlMenuAPID;
	RelativeLayout rlMenuConfirmApply;
	TextView contentVPN;
	TextView contentWifi;
	TextView titleWifi;
	TextView titleVPN;
	private APIDManager apidManager;
	Activity activity;
	private View viewFragment;

	public static Fragment newInstance() {
		ContentMenuTabletFragment f = new ContentMenuTabletFragment();
		return f;
	}

	@Override
	public void onAttach(Context context) {
		super.onAttach(context);
		this.activity = (Activity) context;
	}

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		viewFragment = inflater.inflate(R.layout.fragment_content_menu_tablet, container, false);
		rlMenuStart = viewFragment.findViewById(R.id.rl_menu_start);
		rlMenuAPID = viewFragment.findViewById(R.id.rl_menu_apid);
		rlMenuConfirmApply = viewFragment.findViewById(R.id.rl_menu_confirm_apply);
		contentVPN = viewFragment.findViewById(R.id.content_vpn);
		contentWifi = viewFragment.findViewById(R.id.content_wifi);
		titleWifi = viewFragment.findViewById(R.id.title_wifi);
		titleVPN = viewFragment.findViewById(R.id.title_vpn);
		apidManager = new APIDManager(activity);
		return viewFragment;
	}

	@Override
	public void onResume() {
		super.onResume();
		updateMenuConfirm();
		setupControl();
		String strVpnID = apidManager.getStrVpnID();
		String strUDID = apidManager.getStrUDID();
		contentVPN.setText(strVpnID);
		contentWifi.setText(strUDID);
	}

	private void setupControl() {
			rlMenuStart.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					if (((MenuAcivity)getActivity()).getListCertificate().isEmpty()) {
						((MenuAcivity) activity).startApplyActivityFragment(TabletBaseInputFragment.START_FROM_MENU);
					} else {
						((MenuAcivity) activity).startListApplyUpdateFragment(SCROLL_TO_LEFT);
					}
				}
			});

			rlMenuAPID.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					((MenuAcivity)activity).setFocusMenuTablet(false);
					((MenuAcivity)getActivity()).startActivityAPID();
				}
			});
	}

	private void updateMenuConfirm() {
		final List<ElementApply> listElementApply = ((MenuAcivity)activity).getListElementApply();
        if (listElementApply.isEmpty()) {
            rlMenuConfirmApply.setVisibility(View.GONE);
        } else {
	        rlMenuConfirmApply.setVisibility(View.VISIBLE);
	        rlMenuConfirmApply.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
	                if (listElementApply.size() == 1) {
		                StringList.ID_DETAIL_CURRENT = String.valueOf(listElementApply.get(0).getId());
		                ((MenuAcivity)activity).startDetailConfirmApplyFragment(SCROLL_TO_LEFT);
	                } else {
		                ((MenuAcivity)activity).startListConfirmApplyFragment(SCROLL_TO_LEFT);
	                }
                }
            });
        }
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		viewFragment = null;
	}
}
