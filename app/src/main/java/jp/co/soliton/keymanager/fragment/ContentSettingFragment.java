package jp.co.soliton.keymanager.fragment;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import jp.co.soliton.keymanager.R;
import jp.co.soliton.keymanager.activity.SettingTabletActivity;

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
		tvTitleHeader = (TextView) viewFragment.findViewById(R.id.tvTitleHeader);
		moreOption = (Button) viewFragment.findViewById(R.id.more_option);
		textViewBack = (TextView) viewFragment.findViewById(R.id.textViewBack);
		menuSettingCertList = (RelativeLayout) viewFragment.findViewById(R.id.menuSettingCertList);
		menuSettingNotif = (RelativeLayout) viewFragment.findViewById(R.id.menuSettingNotif);
		menuSettingProduct = (RelativeLayout) viewFragment.findViewById(R.id.menuSettingProduct);
		menuSettingLibrary = (RelativeLayout) viewFragment.findViewById(R.id.menuSettingLibrary);
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
	}
}
