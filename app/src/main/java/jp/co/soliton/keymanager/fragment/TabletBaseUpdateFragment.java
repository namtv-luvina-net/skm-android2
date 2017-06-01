package jp.co.soliton.keymanager.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.View;
import jp.co.soliton.keymanager.InputApplyInfo;
import jp.co.soliton.keymanager.R;
import jp.co.soliton.keymanager.ValidateParams;
import jp.co.soliton.keymanager.activity.MenuAcivity;
import jp.co.soliton.keymanager.adapter.ViewPagerTabletAdapter;
import jp.co.soliton.keymanager.dbalias.ElementApply;
import jp.co.soliton.keymanager.dbalias.ElementApplyManager;

import static jp.co.soliton.keymanager.common.TypeScrollFragment.SCROLL_TO_RIGHT;

public class TabletBaseUpdateFragment extends TabletAbtractInputFragment {

	private boolean isUpdateFromNotification;
	public static Fragment newInstance(String idConfirmApply, boolean isUpdateFromNotification) {
		TabletBaseUpdateFragment f = new TabletBaseUpdateFragment();
		f.idConfirmApply = idConfirmApply;
		f.isUpdateFromNotification = isUpdateFromNotification;
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
	public void updateLeftSide() {
		((MenuAcivity)getActivity()).updateLeftSideListCertAndReapply(getCurrentPage() + 1);
	}

	@Override
	protected void updateButtonFooterStatus(int position) {
		if (position != 1 && position != 2) {
			btnSkip.setVisibility(View.GONE);
		} else {
			((TabletInputFragment)adapter.getItem(position)).onPageSelected();
		}
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
		int current = viewPager.getCurrentItem() - 1;
		if (current < 0) {
			InputApplyInfo.deletePref(getActivity());
			if (isUpdateFromNotification) {
				((MenuAcivity) getActivity()).startNotifUpdateFragment(idConfirmApply, SCROLL_TO_RIGHT);
			} else {
				((MenuAcivity) getActivity()).startListApplyUpdateFragment(SCROLL_TO_RIGHT);
			}
		} else {
			viewPager.setCurrentItem(current);
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
					viewPager.setCurrentItem(nextPageIndex);
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
		if (current == 3) {
			btnNext.setText(R.string.apply);
		} else {
			btnNext.setText(R.string.next);
		}
	}

	@Override
	public void hideInputPort(boolean hide) {
	}
}
