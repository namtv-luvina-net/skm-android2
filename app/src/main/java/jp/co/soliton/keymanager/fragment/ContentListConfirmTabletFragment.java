package jp.co.soliton.keymanager.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import jp.co.soliton.keymanager.InputApplyInfo;
import jp.co.soliton.keymanager.R;
import jp.co.soliton.keymanager.activity.DetailConfirmActivity;
import jp.co.soliton.keymanager.activity.ListCertificateActivity;
import jp.co.soliton.keymanager.activity.ListConfirmActivity;
import jp.co.soliton.keymanager.activity.MenuAcivity;
import jp.co.soliton.keymanager.adapter.AdapterListConfirmApply;
import jp.co.soliton.keymanager.adapter.AdapterListConfirmTabletApply;
import jp.co.soliton.keymanager.dbalias.ElementApply;
import jp.co.soliton.keymanager.dbalias.ElementApplyManager;
import jp.co.soliton.keymanager.manager.APIDManager;

import java.util.List;

/**
 * Created by nguyenducdat on 4/25/2017.
 */

public class ContentListConfirmTabletFragment extends ListFragment {

	Activity activity;
	FragmentManager fragmentManager;
	ElementApplyManager elementMgr;
	private List<ElementApply> listElementApply;
	AdapterListConfirmTabletApply adapterListConfirmTabletApply;

	public static Fragment newInstance(FragmentManager fragmentManager) {
		ContentListConfirmTabletFragment f = new ContentListConfirmTabletFragment();
		f.fragmentManager = fragmentManager;
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

	@Override
	public void onResume() {
		super.onResume();
		setListShown(true);
		getListView().setDivider(null);
		getListView().setDividerHeight(0);
		listElementApply = ((ListConfirmActivity)getActivity()).getListElementApply();
		adapterListConfirmTabletApply = new AdapterListConfirmTabletApply(getActivity(), listElementApply);
		getListView().setAdapter(adapterListConfirmTabletApply);
		getListView().setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Intent intent = new Intent(getActivity(), DetailConfirmActivity.class);
				intent.putExtra("ELEMENT_APPLY_ID", String.valueOf(listElementApply.get(position).getId()));
				startActivity(intent);
				getActivity().overridePendingTransition(0, 0);
			}
		});
	}


}
