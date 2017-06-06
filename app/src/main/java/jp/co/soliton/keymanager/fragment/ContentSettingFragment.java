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
import jp.co.soliton.keymanager.activity.*;

import static jp.co.soliton.keymanager.common.TypeScrollFragment.SCROLL_TO_LEFT;

/**
 * Created by nguyenducdat on 4/25/2017.
 */

public class ContentSettingFragment extends Fragment {

	private Activity activity;
	private TextView titleScreen;
	private TextView textViewBack;
	private RelativeLayout menuSettingCertList;
	private RelativeLayout menuSettingNotif;
	private RelativeLayout menuSettingProduct;
	private RelativeLayout menuSettingLibrary;
	private View viewFragment;

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
		titleScreen = (TextView) viewFragment.findViewById(R.id.tvTitleHeader);
		textViewBack = (TextView) viewFragment.findViewById(R.id.textViewBack);
		menuSettingCertList = (RelativeLayout) viewFragment.findViewById(R.id.menuSettingCertList);
		menuSettingNotif = (RelativeLayout) viewFragment.findViewById(R.id.menuSettingNotif);
		menuSettingProduct = (RelativeLayout) viewFragment.findViewById(R.id.menuSettingProduct);
		menuSettingLibrary = (RelativeLayout) viewFragment.findViewById(R.id.menuSettingLibrary);
		return viewFragment;
	}

	@Override
	public void onResume() {
		super.onResume();
		setupControl();
	}

	public void setupControl() {
		titleScreen.setText(getString(R.string.label_settings));
		menuSettingCertList.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
//				Intent intent = new Intent(getActivity(), SettingListCertificateActivity.class);
//				startActivity(intent);
				((SettingTabletActivity)getActivity()).gotoListCertificatesSetting(SCROLL_TO_LEFT);
			}
		});
		menuSettingNotif.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
//				Intent intent = new Intent(getActivity(), NotificationSettingActivity.class);
//				intent.putExtra(NotificationSettingActivity.KEY_NOTIF_MODE, NotificationSettingActivity.NotifModeEnum.ALL);
//				startActivity(intent);
				((SettingTabletActivity)getActivity()).gotoNotificationAllSetting(SCROLL_TO_LEFT);
			}
		});
		menuSettingProduct.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
//				Intent intent = new Intent(getActivity(), ProductInfoActivity.class);
//				startActivity(intent);
				((SettingTabletActivity)getActivity()).gotoProductInfo(SCROLL_TO_LEFT);
			}
		});
		menuSettingLibrary.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
//				Intent intent = new Intent(getActivity(), LibraryInfoActivity.class);
//				startActivity(intent);
				((SettingTabletActivity)getActivity()).gotoLibrary(SCROLL_TO_LEFT);
			}
		});

		textViewBack.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				getActivity().finish();
			}
		});
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		viewFragment = null;
	}
}
