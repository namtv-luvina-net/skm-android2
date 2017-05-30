package jp.co.soliton.keymanager.fragment;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.View;
import jp.co.soliton.keymanager.InformCtrl;
import jp.co.soliton.keymanager.InputApplyInfo;
import jp.co.soliton.keymanager.R;
import jp.co.soliton.keymanager.ValidateParams;
import jp.co.soliton.keymanager.activity.MenuAcivity;
import jp.co.soliton.keymanager.adapter.ViewPagerTabletAdapter;
import jp.co.soliton.keymanager.dbalias.ElementApply;
import jp.co.soliton.keymanager.dbalias.ElementApplyManager;

/**
 * Created by nguyenducdat on 5/4/2017.
 */

public class TabletBaseInputFragment extends TabletAbtractInputFragment {
	public final static int STATUS_START_APPLY = 1;
	public final static int STATUS_RE_APPLY = 2;
	public final static int START_FROM_MENU = 1;
	public final static int START_FROM_LIST_CERTIFICATE = 2;
	private int startFrom;

	private int currentStatus;

	public static Fragment newInstanceStartApply(int startFrom) {
		TabletBaseInputFragment f = new TabletBaseInputFragment();
		f.currentStatus = STATUS_START_APPLY;
		f.startFrom = startFrom;
		return f;
	}

	public static Fragment newInstanceReApply(String idConfirmApply) {
		TabletBaseInputFragment f = new TabletBaseInputFragment();
		f.idConfirmApply = idConfirmApply;
		f.currentStatus = STATUS_RE_APPLY;
		return f;
	}

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.adapter = new ViewPagerTabletAdapter(activity, getChildFragmentManager(), this);
	}

	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		checkHasIdApply();
	}

	private void checkHasIdApply() {
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
			viewPager.setCurrentItem(3);
		}
	}

	@Override
	public void gotoNextPage() {
		viewPager.postDelayed(new Runnable() {

			@Override
			public void run() {
				int nextPageIndex = viewPager.getCurrentItem() + 1;
				goneSkip();
				if (sdk_int_version < Build.VERSION_CODES.JELLY_BEAN_MR2 && nextPageIndex == 2){
					nextPageIndex++;
				}
				if (nextPageIndex >= 0 && nextPageIndex < adapter.getCount()) {
					viewPager.setCurrentItem(nextPageIndex, true);
					setStatusBackNext(nextPageIndex);
				}
			}
		}, 100);
	}

	public void finishInstallCertificate(int resultCode) {
		((TabletInputPortFragment)adapter.getItem(viewPager.getCurrentItem())).finishInstallCertificate(resultCode);
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
		((MenuAcivity)getActivity()).updateLeftSideInput(getCurrentPage(), startFrom);
	}

	@Override
	protected void updateButtonFooterStatus(int position) {
		btnBack.setVisibility((position == 0 && startFrom == START_FROM_MENU) ? View.INVISIBLE : View.VISIBLE);
		btnNext.setVisibility(position == 2 ? View.INVISIBLE : View.VISIBLE);
		if (position != 4 && position != 5) {
			btnSkip.setVisibility(View.GONE);
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
			if (currentStatus == STATUS_START_APPLY) {
				if (((MenuAcivity)getActivity()).getListCertificate().isEmpty()) {
					((MenuAcivity) getActivity()).gotoMenuTablet();
				} else {
					((MenuAcivity) getActivity()).startListApplyUpdateFragment(MenuAcivity.SCROLL_TO_RIGHT);
				}
			} else if (currentStatus == STATUS_RE_APPLY) {
				((MenuAcivity) getActivity()).startDetailConfirmApplyFragment(MenuAcivity.SCROLL_TO_RIGHT);
			}
		} else {
			viewPager.setCurrentItem(current, true);
		}
		setStatusBackNext(current);
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
		if (current == 1) {
			btnNext.setText(R.string.download);
		} else if (current == 6) {
			btnNext.setText(R.string.apply);
		} else {
			btnNext.setText(R.string.next);
		}
	}

	@Override
	public void hideInputPort(boolean hide) {
		((TabletInputPortFragment) adapter.getItem(1)).hideScreen(hide);
	}
}
