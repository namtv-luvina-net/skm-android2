package jp.co.soliton.keymanager.fragment;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import jp.co.soliton.keymanager.R;
import jp.co.soliton.keymanager.activity.SettingTabletActivity;
import jp.co.soliton.keymanager.mdm.MDMFlgs;

import static jp.co.soliton.keymanager.common.TypeScrollFragment.SCROLL_TO_LEFT;

/**
 * Created by nguyenducdat on 4/25/2017.
 */

public class ContentSettingFragment extends TabletBaseSettingFragment {

	private Activity activity;
	private RelativeLayout menuSettingCertList;
	private RelativeLayout menuSettingNotif;
	private RelativeLayout menuSettingProduct;
	private RelativeLayout menuSettingLibrary;
	private RelativeLayout menuSettingMDM;
	private LinearLayout layoutMDM;
	private MDMFlgs mdm;

	public static Fragment newInstance() {
		ContentSettingFragment f = new ContentSettingFragment();
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
		viewFragment = inflater.inflate(R.layout.fragment_setting_tablet, container, false);
		tvTitleHeader = viewFragment.findViewById(R.id.tvTitleHeader);
		moreOption = viewFragment.findViewById(R.id.more_option);
		textViewBack = viewFragment.findViewById(R.id.textViewBack);
		menuSettingCertList = viewFragment.findViewById(R.id.menuSettingCertList);
		menuSettingNotif = viewFragment.findViewById(R.id.menuSettingNotif);
		menuSettingProduct = viewFragment.findViewById(R.id.menuSettingProduct);
		menuSettingLibrary = viewFragment.findViewById(R.id.menuSettingLibrary);
		menuSettingMDM = viewFragment.findViewById(R.id.menuSettingMDM);
		layoutMDM = viewFragment.findViewById(R.id.ll_mdm);
		mdm = new MDMFlgs();
		return viewFragment;
	}

	@Override
	protected void setTextBtnBack() {
		textViewBack.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
		RelativeLayout.LayoutParams llp = (RelativeLayout.LayoutParams) textViewBack.getLayoutParams();
		llp.setMargins((int) (20*getActivity().getResources().getDisplayMetrics().density), llp.topMargin, llp
				.rightMargin, llp.bottomMargin);
		textViewBack.setLayoutParams(llp);
		textViewBack.setText(R.string.close);
	}

	@Override
	public void onResume() {
		super.onResume();
		setupControl();
		boolean bRet = mdm.ReadAndSetScepMdmInfo(getActivity());
		if (!bRet) {
			layoutMDM.setVisibility(View.GONE);
		} else {
			layoutMDM.setVisibility(View.VISIBLE);
		}
	}

	public void setupControl() {
		tvTitleHeader.setText(getString(R.string.label_setting));
		menuSettingCertList.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				((SettingTabletActivity)getActivity()).gotoListCertificatesSetting(SCROLL_TO_LEFT);
			}
		});
		menuSettingNotif.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				((SettingTabletActivity)getActivity()).gotoNotificationAllSetting(SCROLL_TO_LEFT);
			}
		});
		menuSettingProduct.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				((SettingTabletActivity)getActivity()).gotoProductInfo(SCROLL_TO_LEFT);
			}
		});
		menuSettingLibrary.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				((SettingTabletActivity)getActivity()).gotoLibrary(SCROLL_TO_LEFT);
			}
		});
		menuSettingMDM.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				((SettingTabletActivity)getActivity()).gotoMDM(mdm, SCROLL_TO_LEFT);
			}
		});
	}
}
