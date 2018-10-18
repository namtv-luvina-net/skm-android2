package jp.co.soliton.keymanager.fragment;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import jp.co.soliton.keymanager.InputApplyInfo;
import jp.co.soliton.keymanager.R;
import jp.co.soliton.keymanager.activity.*;
import jp.co.soliton.keymanager.dbalias.ElementApplyManager;
import jp.co.soliton.keymanager.manager.APIDManager;

/**
 * Created by nguyenducdat on 4/25/2017.
 */

public class ContentMenuPhoneFragment extends Fragment {

	private Button btnMenuStart;
	private Button btnMenuAPID;
	private Button btnMenuConfirmApply;
	private Button btnSetting;
	private ElementApplyManager elementMgr;
	private APIDManager apidManager;

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		elementMgr = ElementApplyManager.getInstance(getActivity());
	}

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View  view = inflater.inflate(R.layout.fragment_menu_phone, container, false);
		btnMenuStart = view.findViewById(R.id.btnMenuStart);
		btnMenuAPID = view.findViewById(R.id.btnMenuAPID);
		btnMenuConfirmApply = view.findViewById(R.id.btnMenuConfirmApply);
		btnSetting = view.findViewById(R.id.btnSetting);
		return view;
	}

	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		elementMgr = ElementApplyManager.getInstance(getActivity());
	}

	@Override
	public void onResume() {
		super.onResume();
		updateMenuConfirm();
		setupControl();
	}

	private void updateMenuConfirm() {
		final int totalApply = elementMgr.getCountElementApply();
		if (totalApply <= 0) {
			btnMenuConfirmApply.setVisibility(View.GONE);
		} else {
			btnMenuConfirmApply.setVisibility(View.VISIBLE);
			btnMenuConfirmApply.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					((MenuAcivity)getActivity()).gotoConfirmActivity();
				}
			});
		}
	}

	private void setupControl() {
		btnMenuStart.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (elementMgr.hasCertificate()) {
					Intent intent = new Intent(getActivity(), ListCertificateActivity.class);
					startActivity(intent);
				} else {
					InputApplyInfo.deletePref(getActivity());
					Intent intent = new Intent(getActivity(), ViewPagerInputActivity.class);
					startActivity(intent);
				}
			}
		});

		btnMenuAPID.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (apidManager == null) {
					apidManager = new APIDManager(getActivity());
				}
				Intent intent = new Intent(getActivity(), APIDActivity.class);
				intent.putExtra("m_strAPIDVPN", apidManager.getStrVpnID());
				intent.putExtra("m_strAPIDWifi", apidManager.getStrUDID());
				startActivity(intent);
			}
		});

		btnSetting.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getActivity(), SettingActivity.class);
				startActivity(intent);
			}
		});

	}
}
