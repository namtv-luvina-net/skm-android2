package jp.co.soliton.keymanager.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import jp.co.soliton.keymanager.ConfigrationProcess;
import jp.co.soliton.keymanager.InputApplyInfo;
import jp.co.soliton.keymanager.R;
import jp.co.soliton.keymanager.adapter.ViewPagerTabletAdapter;
import jp.co.soliton.keymanager.swipelayout.InputApplyViewPager;

/**
 * Created by nguyenducdat on 5/4/2017.
 */

public class TabletBaseInputFragment extends Fragment {

	InputApplyViewPager viewPager;
	ViewPagerTabletAdapter adapter;
	private TextView btnSkip;
	private Button btnNext;
	private Button btnBack;
	public double d_android_version;

	public static Fragment newInstance() {
		TabletBaseInputFragment f = new TabletBaseInputFragment();
		return f;
	}

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_base_input_tablet, container, false);
		d_android_version = ConfigrationProcess.getAndroidOsVersion();
		btnSkip = (TextView) view.findViewById(R.id.btnSkip);
		btnBack = (Button) view.findViewById(R.id.btnBack);
		btnNext = (Button) view.findViewById(R.id.btnNext);
		viewPager = (InputApplyViewPager) view.findViewById(R.id.viewPager);
		adapter = new ViewPagerTabletAdapter(getActivity(), getChildFragmentManager());
		viewPager.setAdapter(adapter);
		viewPager.setPagingEnabled(false);
		viewPager.setCurrentItem(0);
		setTab();
		return view;
	}

	/**
	 * Action change tab page
	 */
	private void setTab(){
		viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener(){
			@Override
			public void onPageScrollStateChanged(int position) {}
			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {}
			@Override
			public void onPageSelected(int position) {
				
			}
		});
	}

	@Override
	public void onResume() {
		super.onResume();
		setChangePage();
	}

	/**
	 * Action next back page input
	 */
	private void setChangePage() {
		btnBack.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				int current;
				if (d_android_version < 4.3 && viewPager.getCurrentItem() == 3){
					viewPager.setCurrentItem(2, true);
				}
				if (viewPager.getCurrentItem() == 2) {
					hideInputPort(true);
					current = viewPager.getCurrentItem() - 2;
				} else {
					current = viewPager.getCurrentItem() - 1;
				}
				if (current < 0) {
					InputApplyInfo.deletePref(getActivity());
					getActivity().finish();
				} else {
					viewPager.setCurrentItem(current, true);
				}
				setStatusBackNext(current);
			}
		});
		btnNext.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				int current = viewPager.getCurrentItem();
				if (current < (adapter.getCount())) {
					((TabletInputFragment)adapter.getItem(current)).nextAction();
				}
			}
		});
	}

	/**
	 * Set status next back button
	 * @param current
	 */
	public void setStatusBackNext(int current) {
		btnNext.setVisibility(current == 2 ? View.INVISIBLE : View.VISIBLE);
		if (current == 1) {
			btnNext.setText(R.string.download);
		} else {
			btnNext.setText(R.string.next);
		}
	}

	public void hideInputPort(boolean hide) {
		((InputPortPageFragment) adapter.getItem(1)).hideScreen(hide);
	}

	public void goneSkip() {
		btnSkip.setVisibility(View.GONE);
	}
	public void visibleSkip() {
		btnSkip.setVisibility(View.VISIBLE);
	}

	public void invisibleBack() {
		btnBack.setVisibility(View.INVISIBLE);
	}

	public void visibleBack() {
		btnBack.setVisibility(View.VISIBLE);
	}

	public void disableNext() {
		btnNext.setEnabled(false);
		if(android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.JELLY_BEAN) {
			btnNext.setBackgroundDrawable( getResources().getDrawable(R.drawable.background_btn_disable) );
		} else {
			btnNext.setBackground( getResources().getDrawable(R.drawable.background_btn_disable));
		}
	}

	public void enableNext() {
		btnNext.setEnabled(true);
		if(android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.JELLY_BEAN) {
			btnNext.setBackgroundDrawable( getResources().getDrawable(R.drawable.background_btn_ctrl_apid) );
		} else {
			btnNext.setBackground( getResources().getDrawable(R.drawable.background_btn_ctrl_apid));
		}
	}

	public void invisibleNext() {
		btnNext.setVisibility(View.INVISIBLE);
	}

	public void visibleNext() {
		btnNext.setVisibility(View.VISIBLE);
	}
}
