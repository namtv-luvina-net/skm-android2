package jp.co.soliton.keymanager.fragment;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
import jp.co.soliton.keymanager.R;
import jp.co.soliton.keymanager.activity.MenuAcivity;
import jp.co.soliton.keymanager.adapter.AdapterListApplyUpdateTablet;
import jp.co.soliton.keymanager.dbalias.ElementApply;

import java.util.List;

/**
 * Created by nguyenducdat on 4/25/2017.
 */

public class ContentListApplyUpdateTabletFragment extends Fragment implements AdapterListApplyUpdateTablet.ItemListener {

	private List<ElementApply> listCertificate;
	private ListView listView;
	private TextView tvNew;
	private AdapterListApplyUpdateTablet adapterListApplyUpdateTablet;

	@Override
	public void clickApplyButton(String id) {
		((MenuAcivity)getActivity()).startUpdateFragment(id);
	}

	public static Fragment newInstance(List<ElementApply> listCertificate) {
		ContentListApplyUpdateTabletFragment f = new ContentListApplyUpdateTabletFragment();
		f.listCertificate = listCertificate;
		return f;
	}

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_list_apply_update_tablet, container, false);
		listView = (ListView) view.findViewById(R.id.listCertificate);
		tvNew = (TextView) view.findViewById(R.id.tvNewApply);
		return view;
	}

	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		listCertificate = ((MenuAcivity)getActivity()).getListCertificate();
		adapterListApplyUpdateTablet = new AdapterListApplyUpdateTablet(getActivity(), listCertificate, this);
		listView.setAdapter(adapterListApplyUpdateTablet);
	}

	@Override
	public void onResume() {
		super.onResume();
		listCertificate = ((MenuAcivity)getActivity()).getListCertificate();
		adapterListApplyUpdateTablet.setListCertificate(listCertificate);
		adapterListApplyUpdateTablet.notifyDataSetChanged();
		tvNew.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				((MenuAcivity)getActivity()).startApplyActivityFragment();
			}
		});
		((MenuAcivity)getActivity()).updateLeftSideListCertAndReapply(0);
	}


}
