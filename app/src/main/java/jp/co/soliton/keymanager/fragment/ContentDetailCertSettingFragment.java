package jp.co.soliton.keymanager.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import jp.co.soliton.keymanager.ItemChildDetailCertSetting;
import jp.co.soliton.keymanager.R;
import jp.co.soliton.keymanager.adapter.AdapterSettingDetailCertificate;
import jp.co.soliton.keymanager.common.InfoDetailCertificateSetting;
import jp.co.soliton.keymanager.dbalias.ElementApply;
import jp.co.soliton.keymanager.dbalias.ElementApplyManager;

import java.util.List;

import static jp.co.soliton.keymanager.activity.SettingTabletActivity.RATIO_SCALE_WIDTH;

/**
 * Created by nguyenducdat on 4/25/2017.
 */

public class ContentDetailCertSettingFragment extends Fragment {

	private View viewFragment;
	private TextView tvTitleHeader;
	private TextView textViewBack;
	private LinearLayout moreOption;
	private ExpandableListView expandableListView;
	private List<String> listDataHeader;
	private List<List<ItemChildDetailCertSetting>> listDataChild;
	private AdapterSettingDetailCertificate adapterSettingDetailCertificate;
	private ElementApplyManager elementMgr;
	private String id;
	ElementApply elementApply;

	public static Fragment newInstance(String id) {
		ContentDetailCertSettingFragment f = new ContentDetailCertSettingFragment();
		f.id = id;
		return f;
	}

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		elementMgr = new ElementApplyManager(getActivity());
		elementApply = elementMgr.getElementApply(id);
		viewFragment = inflater.inflate(R.layout.fragment_setting_detail_certificate, container, false);
		tvTitleHeader = (TextView) viewFragment.findViewById(R.id.tvTitleHeader);
		tvTitleHeader.setText(elementApply.getcNValue());
		textViewBack = (TextView) viewFragment.findViewById(R.id.textViewBack);
		moreOption = (LinearLayout) viewFragment.findViewById(R.id.more_option);
		expandableListView = (ExpandableListView) viewFragment.findViewById(R.id.expand_detail_cert);
		adapterSettingDetailCertificate = new AdapterSettingDetailCertificate(getActivity(), true);
		expandableListView.setAdapter(adapterSettingDetailCertificate);
		expandableListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
			@Override
			public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
				return true;
			}
		});
		prepareData();
		expandableListView.setClickable(false);
		return viewFragment;
	}

	@Override
	public void onResume() {
		super.onResume();
		updateTitle();
		prepareData();
		textViewBack.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				getActivity().onBackPressed();
			}
		});
		moreOption.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {

			}
		});
	}

	private void prepareData() {
		listDataHeader  = InfoDetailCertificateSetting.prepareHeader(getActivity());
		listDataChild = InfoDetailCertificateSetting.prepareChild(getActivity(), elementApply);
		adapterSettingDetailCertificate.setListDataHeader(listDataHeader);
		adapterSettingDetailCertificate.setListDataChild(listDataChild);
		adapterSettingDetailCertificate.notifyDataSetChanged();
	}

	private void updateTitle() {
		tvTitleHeader.measure(0, 0);
		textViewBack.measure(0, 0);
		viewFragment.measure(0, 0);
		DisplayMetrics displayMetrics = new DisplayMetrics();
		getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
		int width = (int) (displayMetrics.widthPixels * RATIO_SCALE_WIDTH);

		if (tvTitleHeader.getMeasuredWidth() > width - (textViewBack.getMeasuredWidth() * 2)) {
			textViewBack.setText("");
			RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
					RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
			params.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
			params.addRule(RelativeLayout.RIGHT_OF, textViewBack.getId());
			params.addRule(RelativeLayout.LEFT_OF, moreOption.getId());
			tvTitleHeader.setLayoutParams(params);
		}
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		viewFragment = null;
	}
}
