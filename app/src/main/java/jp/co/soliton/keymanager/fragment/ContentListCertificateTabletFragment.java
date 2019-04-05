package jp.co.soliton.keymanager.fragment;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
import jp.co.soliton.keymanager.R;
import jp.co.soliton.keymanager.activity.MenuAcivity;
import jp.co.soliton.keymanager.adapter.AdapterListCertificateTablet;
import jp.co.soliton.keymanager.dbalias.ElementApply;

import java.util.List;

/**
 * Created by nguyenducdat on 4/25/2017.
 */

public class ContentListCertificateTabletFragment extends Fragment implements AdapterListCertificateTablet.ItemListener {

	private View viewFragment;
	private List<ElementApply> listCertificate;
	private ListView listView;
	private TextView tvNew;
	private AdapterListCertificateTablet adapterListCertificateTablet;

	@Override
	public void clickApplyButton(String id) {
		((MenuAcivity)getActivity()).startUpdateFragmentFromListCertificate(id);
	}

	public static Fragment newInstance(List<ElementApply> listCertificate) {
		ContentListCertificateTabletFragment f = new ContentListCertificateTabletFragment();
		f.listCertificate = listCertificate;
		return f;
	}

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		viewFragment = inflater.inflate(R.layout.fragment_list_apply_update_tablet, container, false);
		listView = viewFragment.findViewById(R.id.listCertificate);
		tvNew = viewFragment.findViewById(R.id.tvNewApply);
		return viewFragment;
	}

	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		listCertificate = ((MenuAcivity)getActivity()).getListCertificate();
		adapterListCertificateTablet = new AdapterListCertificateTablet(getActivity(), listCertificate, this);
		listView.setAdapter(adapterListCertificateTablet);
	}

	@Override
	public void onResume() {
		super.onResume();
		listCertificate = ((MenuAcivity)getActivity()).getListCertificate();
		adapterListCertificateTablet.setListCertificate(listCertificate);
		adapterListCertificateTablet.notifyDataSetChanged();
		tvNew.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				((MenuAcivity)getActivity()).startApplyActivityFragment(TabletBaseInputFragment.START_FROM_LIST_CERTIFICATE);
			}
		});
		((MenuAcivity)getActivity()).updateLeftSideListCertAndReapply(0);
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		viewFragment = null;
	}
}
