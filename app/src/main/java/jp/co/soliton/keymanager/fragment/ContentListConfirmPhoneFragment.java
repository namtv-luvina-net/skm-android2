package jp.co.soliton.keymanager.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import jp.co.soliton.keymanager.InputApplyInfo;
import jp.co.soliton.keymanager.R;
import jp.co.soliton.keymanager.activity.*;
import jp.co.soliton.keymanager.adapter.AdapterListConfirmApply;
import jp.co.soliton.keymanager.dbalias.ElementApply;
import jp.co.soliton.keymanager.dbalias.ElementApplyManager;
import jp.co.soliton.keymanager.manager.APIDManager;

import java.util.List;

/**
 * Created by nguyenducdat on 4/25/2017.
 */

public class ContentListConfirmPhoneFragment extends Fragment {

	private ListView list;
	private AdapterListConfirmApply adapterListConfirmApply;
	private List<ElementApply> listElementApply;
	private TextView title;

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View  view = inflater.inflate(R.layout.fragment_list_confirm_phone, container, false);
		title = (TextView) view.findViewById(R.id.tvTitleHeader);
		title.setText(getString(R.string.list_application));
		list = (ListView) view.findViewById(R.id.listConfirm);
		return view;
	}

	@Override
	public void onResume() {
		super.onResume();
		listElementApply = ((ListConfirmActivity)getActivity()).getListElementApply();
		adapterListConfirmApply = new AdapterListConfirmApply(getActivity(), listElementApply);
		list.setAdapter(adapterListConfirmApply);

		list.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Intent intent = new Intent(getActivity(), DetailConfirmActivity.class);
				intent.putExtra("ELEMENT_APPLY_ID", String.valueOf(listElementApply.get(position).getId()));
				startActivity(intent);
			}
		});
	}

}
