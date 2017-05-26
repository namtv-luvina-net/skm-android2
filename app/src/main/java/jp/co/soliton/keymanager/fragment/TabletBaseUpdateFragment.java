package jp.co.soliton.keymanager.fragment;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.View;
import jp.co.soliton.keymanager.InformCtrl;
import jp.co.soliton.keymanager.InputApplyInfo;
import jp.co.soliton.keymanager.R;
import jp.co.soliton.keymanager.ValidateParams;
import jp.co.soliton.keymanager.activity.MenuAcivity;
import jp.co.soliton.keymanager.adapter.ViewPagerTabletAdapter;
import jp.co.soliton.keymanager.dbalias.ElementApply;
import jp.co.soliton.keymanager.dbalias.ElementApplyManager;

public class TabletBaseUpdateFragment extends TabletAbtractInputFragment {

	public static Fragment newInstance(String idConfirmApply) {
		TabletBaseUpdateFragment f = new TabletBaseUpdateFragment();
		f.idConfirmApply = idConfirmApply;
		return f;
	}

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.adapter = new ViewPagerTabletAdapter(activity, getChildFragmentManager(), this, idConfirmApply);
	}

	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		getIdApply();
	}

	private void getIdApply() {
		if(!ValidateParams.nullOrEmpty(idConfirmApply)) {
			ElementApplyManager elementMgr = new ElementApplyManager(getActivity());
			ElementApply detail = elementMgr.getElementApply(idConfirmApply);
			if (detail == null) {
				Log.d("TabletBaseUpdateFragment:datnd", "getIdApply: " + idConfirmApply +" bi null");
			}
			getInputApplyInfo().setHost(detail.getHost());
			getInputApplyInfo().setPort(detail.getPort());
			getInputApplyInfo().setSecurePort(detail.getPortSSL());
			if (detail.getTarger().startsWith("WIFI")) {
				getInputApplyInfo().setPlace(InputBasePageFragment.TARGET_WiFi);
			} else {
				getInputApplyInfo().setPlace(InputBasePageFragment.TARGET_VPN);
			}
			getInputApplyInfo().setUserId(detail.getUserId());
			getInputApplyInfo().savePref(getActivity());
		}
	}


	@Override
	public void gotoCompleteApply() {
		((MenuAcivity)getActivity()).goApplyCompleted();
	}

	@Override
	public void gotoCompleteApply(InformCtrl m_InformCtrl, ElementApply element) {
		((MenuAcivity)getActivity()).goApplyCompleted(m_InformCtrl, element);
	}

	@Override
	public void updateLeftSide() {
	}

	@Override
	protected void updateButtonFooterStatus(int position) {
//		gotoCompleteApply
	}

	public void clickBackButton(){
		btnBack.performClick();
	}

	@Override
	public void clickButtonSkip() {
		((TabletInputFragment)adapter.getItem(viewPager.getCurrentItem())).clickSkipButton();
	}

	@Override
	public void clickButtonBack() {
		int current;
		if (sdk_int_version < Build.VERSION_CODES.JELLY_BEAN_MR2 && viewPager.getCurrentItem() == 3){
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
			((MenuAcivity) getActivity()).startListApplyUpdateFragment(MenuAcivity.SCROLL_TO_RIGHT);
		} else {
			viewPager.setCurrentItem(current, true);
		}
		setStatusBackNext(current);
	}

	@Override
	public void gotoNextPage() {
		viewPager.postDelayed(new Runnable() {

			@Override
			public void run() {
				int nextPageIndex = viewPager.getCurrentItem() + 1;
				goneSkip();
				if (nextPageIndex >= 0 && nextPageIndex < adapter.getCount()) {
					viewPager.setCurrentItem(nextPageIndex, true);
					setStatusBackNext(nextPageIndex);
				}
			}
		}, 100);
	}

	@Override
	public void clickButtonNext() {
		int current = viewPager.getCurrentItem();
		if (current < adapter.getCount()) {
			((TabletInputFragment)adapter.getItem(current)).nextAction();
		}
	}

	/**
	 * Set status next back button
	 * @param current
	 */
	@Override
	public void setStatusBackNext(int current) {
//		if (current == 1) {
//			btnNext.setText(R.string.download);
//		} else if (current == 6) {
//			btnNext.setText(R.string.apply);
//		} else {
//			btnNext.setText(R.string.next);
//		}
	}

	@Override
	public void hideInputPort(boolean hide) {
	}
}
