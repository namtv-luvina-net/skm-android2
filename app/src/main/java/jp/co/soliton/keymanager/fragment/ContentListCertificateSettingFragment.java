package jp.co.soliton.keymanager.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
import jp.co.soliton.keymanager.R;
import jp.co.soliton.keymanager.adapter.AdapterSettingListCertificate;
import jp.co.soliton.keymanager.dbalias.ElementApply;
import jp.co.soliton.keymanager.dbalias.ElementApplyManager;

import java.util.List;

/**
 * Created by nguyenducdat on 4/25/2017.
 */

public class ContentListCertificateSettingFragment extends TabletBaseSettingFragment {

	private ListView list;
	private AdapterSettingListCertificate adapterListCertificate;
	private ElementApplyManager elementMgr;
	private List<ElementApply> listCertificate;
	private TextView tvNoCertInstalled;

	public static Fragment newInstance() {
		ContentListCertificateSettingFragment f = new ContentListCertificateSettingFragment();
		return f;
	}

	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		elementMgr = new ElementApplyManager(getActivity());
		listCertificate = elementMgr.getAllCertificate();
		adapterListCertificate = new AdapterSettingListCertificate(getActivity(), listCertificate, true);
		list.setAdapter(adapterListCertificate);
	}

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		viewFragment = inflater.inflate(R.layout.fragment_setting_list_certificate, container, false);
		tvNoCertInstalled = (TextView) viewFragment.findViewById(R.id.tvNoCertInstalled);
		textViewBack = (TextView) viewFragment.findViewById(R.id.textViewBack);
		list = (ListView) viewFragment.findViewById(R.id.listSettingCert);
		tvTitleHeader = (TextView) viewFragment.findViewById(R.id.tvTitleHeader);
		tvTitleHeader.setText(getString(R.string.list_cert));
		return viewFragment;
	}

	/**
	 * Update List Certificate
	 */
	@Override
	public void onResume() {
		super.onResume();
		listCertificate = elementMgr.getAllCertificate();
		if (listCertificate == null || listCertificate.isEmpty()) {
			tvNoCertInstalled.setVisibility(View.VISIBLE);
		} else {
			tvNoCertInstalled.setVisibility(View.GONE);
			adapterListCertificate.setListElementApply(listCertificate);
			adapterListCertificate.notifyDataSetChanged();
		}
	}
}
