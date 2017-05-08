package jp.co.soliton.keymanager.manager;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import jp.co.soliton.keymanager.InformCtrl;
import jp.co.soliton.keymanager.R;
import jp.co.soliton.keymanager.dbalias.ElementApply;
import jp.co.soliton.keymanager.fragment.*;

import static jp.co.soliton.keymanager.fragment.ContentMenuTabletFragment.COMPLETE_STATUS;

/**
 * Created by luongdolong on 2/8/2017.
 *
 * Processing base input page
 */

public class TabletInputFragmentManager{

	FragmentManager fragmentManager;
	TabletBaseInputFragment tabletBaseInputFragment;
	LeftSideInputTabletFragment leftSideInputTabletFragment;
//	MenuAcivity menuAcivity;
	ContentMenuTabletFragment contentMenuTabletFragment;

	public TabletInputFragmentManager(ContentMenuTabletFragment contentMenuTabletFragment, FragmentManager fragmentManager) {
		this.fragmentManager = fragmentManager;
		this.contentMenuTabletFragment = contentMenuTabletFragment;
	}

	public void gotoMenu() {
		contentMenuTabletFragment.goToMenu();
	}

	public void pressBackInputApply() {
		tabletBaseInputFragment.clickBackButton();
	}

	public void startActivityStartApply() {
		FragmentTransaction fragmentTransaction1 = fragmentManager.beginTransaction();
		leftSideInputTabletFragment = (LeftSideInputTabletFragment) LeftSideInputTabletFragment.newInstance();
		fragmentTransaction1.replace(R.id.fragment_left_side_menu_tablet, leftSideInputTabletFragment);
		fragmentTransaction1.commit();

		FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
		tabletBaseInputFragment = (TabletBaseInputFragment) TabletBaseInputFragment.newInstance(this);
		fragmentTransaction.setCustomAnimations(R.anim.enter, R.anim.exit, R.anim.pop_enter, R.anim.pop_exit);
		fragmentTransaction.replace(R.id.fragment_content_menu_tablet, tabletBaseInputFragment);
		fragmentTransaction.commit();
	}

	public void startActivityAPID() {
		FragmentTransaction fragmentTransaction1 = fragmentManager.beginTransaction();
		fragmentTransaction1.replace(R.id.fragment_left_side_menu_tablet, new LeftSideAPIDTabletFragment());
		fragmentTransaction1.commit();

		FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
		fragmentTransaction.setCustomAnimations(R.anim.enter, R.anim.exit, R.anim.pop_enter, R.anim.pop_exit);
		fragmentTransaction.replace(R.id.fragment_content_menu_tablet, new ContentAPIDTabletFragment());
		fragmentTransaction.commit();
	}

	public void goApplyCompleted(){
		contentMenuTabletFragment.currentStatus = COMPLETE_STATUS;
		FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
		fragmentTransaction.setCustomAnimations(R.anim.enter, R.anim.exit, R.anim.pop_enter, R.anim.pop_exit);
		fragmentTransaction.replace(R.id.fragment_content_menu_tablet, TabletInputSuccessFragment.newInstance());
		fragmentTransaction.commit();
		leftSideInputTabletFragment.hideContent();
	}
	public void goApplyCompleted(InformCtrl m_InformCtrl, ElementApply element){
		contentMenuTabletFragment.currentStatus = COMPLETE_STATUS;
		FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
		fragmentTransaction.setCustomAnimations(R.anim.enter, R.anim.exit, R.anim.pop_enter, R.anim.pop_exit);
		fragmentTransaction.replace(R.id.fragment_content_menu_tablet, TabletInputSuccessFragment.newInstance(m_InformCtrl, element));
		fragmentTransaction.commit();
		leftSideInputTabletFragment.hideContent();
	}

	public void updateLeftSideInput(int possition) {
		if (leftSideInputTabletFragment == null) {
			return;
		}
		leftSideInputTabletFragment.highlightItem(possition);
	}

	/**
	 * Finish install certificate
	 * @param resultCode
	 */
	public void finishInstallCertificate(int resultCode) {
		tabletBaseInputFragment.finishInstallCertificate(resultCode);
	}
}
