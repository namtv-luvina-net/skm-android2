package jp.co.soliton.keymanager.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
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
import jp.co.soliton.keymanager.activity.DetailConfirmActivity;
import jp.co.soliton.keymanager.activity.ListCertificateActivity;
import jp.co.soliton.keymanager.activity.ListConfirmActivity;
import jp.co.soliton.keymanager.activity.MenuAcivity;
import jp.co.soliton.keymanager.dbalias.ElementApply;
import jp.co.soliton.keymanager.dbalias.ElementApplyManager;
import jp.co.soliton.keymanager.manager.APIDManager;

import java.util.List;

/**
 * Created by nguyenducdat on 4/25/2017.
 */

public class ContentMenuTabletFragment extends Fragment {

	RelativeLayout rlMenuStart;
	RelativeLayout rlMenuAPID;
	RelativeLayout rlMenuConfirmApply;
	ElementApplyManager elementMgr;
	TextView contentVPN;
	TextView contentWifi;
	TextView titleWifi;
	TextView titleVPN;
	private APIDManager apidManager;
	Activity activity;

	public static Fragment newInstance() {
		ContentMenuTabletFragment f = new ContentMenuTabletFragment();
		return f;
	}

	@Override
	public void onAttach(Context context) {
		super.onAttach(context);
		this.activity = (Activity) context;
	}

	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		elementMgr = new ElementApplyManager(activity);
	}

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_content_menu_tablet, container, false);
		rlMenuStart = (RelativeLayout) view.findViewById(R.id.rl_menu_start);
		rlMenuAPID = (RelativeLayout) view.findViewById(R.id.rl_menu_apid);
		rlMenuConfirmApply = (RelativeLayout) view.findViewById(R.id.rl_menu_confirm_apply);
		contentVPN = (TextView) view.findViewById(R.id.content_vpn);
		contentWifi = (TextView) view.findViewById(R.id.content_wifi);
		titleWifi = (TextView) view.findViewById(R.id.title_wifi);
		titleVPN = (TextView) view.findViewById(R.id.title_vpn);
		apidManager = new APIDManager(activity);
		return view;
	}

	@Override
	public void onResume() {
		super.onResume();
		updateViewTitle();
		updateMenuConfirm();
		setupControl();
		String strVpnID = apidManager.getStrVpnID();
		String strUDID = apidManager.getStrUDID();
		contentVPN.setText(strVpnID);
		contentWifi.setText(strUDID);
	}

	private void updateViewTitle() {
		titleWifi.measure(0, 0);
		titleVPN.measure(0, 0);
		int widthWifi = titleWifi.getMeasuredWidth();
		int widthVpn = titleVPN.getMeasuredWidth();
		if (widthWifi < widthVpn) {
			RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) titleWifi.getLayoutParams();
			params.width = widthVpn;
			titleWifi.setLayoutParams(params);
		} else {
			RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) titleVPN.getLayoutParams();
			params.width = widthWifi;
			titleVPN.setLayoutParams(params);
		}
	}

	private void setupControl() {
			rlMenuStart.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					if (elementMgr.hasCertificate()) {
						Intent intent = new Intent(activity, ListCertificateActivity.class);
						startActivity(intent);
					} else {
						((MenuAcivity)activity).setFocusMenuTablet(false);
						InputApplyInfo.deletePref(getActivity());
						((MenuAcivity)getActivity()).startActivityStartApply();
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
        final int totalApply = elementMgr.getCountElementApply();
        if (totalApply <= 0) {
            rlMenuConfirmApply.setVisibility(View.GONE);
        } else {
	        rlMenuConfirmApply.setVisibility(View.VISIBLE);
	        rlMenuConfirmApply.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
	                if (totalApply == 1) {
                        List<ElementApply> listElementApply = elementMgr.getAllElementApply();
                        Intent intent = new Intent(activity, DetailConfirmActivity.class);
                        intent.putExtra("ELEMENT_APPLY_ID", String.valueOf(listElementApply.get(0).getId()));
                        startActivity(intent);
                    } else {
                        Intent intent = new Intent(activity, ListConfirmActivity.class);
                        startActivity(intent);
	                    activity.overridePendingTransition(0, 0);
                    }
                }
            });
        }
	}
}
