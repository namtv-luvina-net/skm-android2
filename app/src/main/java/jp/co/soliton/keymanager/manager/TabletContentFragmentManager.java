package jp.co.soliton.keymanager.manager;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import jp.co.soliton.keymanager.InformCtrl;
import jp.co.soliton.keymanager.InputApplyInfo;
import jp.co.soliton.keymanager.R;
import jp.co.soliton.keymanager.activity.MenuAcivity;
import jp.co.soliton.keymanager.dbalias.ElementApply;
import jp.co.soliton.keymanager.fragment.*;

import java.io.Serializable;

/**
 * Created by luongdolong on 2/8/2017.
 *
 * Processing base input page
 */

public class TabletContentFragmentManager implements Serializable{
	public static final int NOT_VALID = -1;
	public static final int RESET_STATUS = 0;
	public static final int APID_STATUS = 1;
	public static final int INPUT_APPLY_STATUS = 2;
	public static final int COMPLETE_STATUS = 3;
	public int currentStatus;

	FragmentManager fragmentManager;
	Fragment fragmentLeft, fragmentContent;

	public TabletContentFragmentManager(FragmentManager fragmentManager) {
		Log.d("TabletContentFragmentManager:datnd", "TabletContentFragmentManager: tao moi ========================================= ");
		this.fragmentManager = fragmentManager;
	}

	public void gotoLastState(int currentStatus) {
		switch (currentStatus) {
			case RESET_STATUS:
				gotoMenu();
				break;
			case APID_STATUS:
				startActivityAPID();
				break;
			case INPUT_APPLY_STATUS:
				startActivityStartApply();
				break;
			case COMPLETE_STATUS:
				goApplyCompleted();
				break;
			default:
				break;
		}
	}

	public void gotoMenu() {
		Log.d("TabletContentFragmentManager:datnd", "gotoMenu: ");
		currentStatus = RESET_STATUS;
		FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
		fragmentTransaction.setCustomAnimations(R.anim.pop_enter, R.anim.pop_exit, R.anim.exit, R.anim.enter);
		fragmentContent = ContentMenuTabletFragment.newInstance(this);
		fragmentTransaction.replace(R.id.fragment_content_menu_tablet, fragmentContent);
		fragmentTransaction.commit();

		fragmentTransaction = fragmentManager.beginTransaction();
		fragmentLeft = new LeftSideMenuTabletFragment();
		fragmentTransaction.replace(R.id.fragment_left_side_menu_tablet, fragmentLeft);
		fragmentTransaction.commit();
	}

	public void pressBackInputApply() {
		((TabletBaseInputFragment)fragmentContent).clickBackButton();
	}

	public void startActivityStartApply() {
		Log.d("TabletContentFragmentManager:datnd", "startActivityStartApply: ");
		currentStatus = INPUT_APPLY_STATUS;
		FragmentTransaction fragmentTransaction1 = fragmentManager.beginTransaction();
		fragmentLeft = LeftSideInputTabletFragment.newInstance();
		fragmentTransaction1.replace(R.id.fragment_left_side_menu_tablet, fragmentLeft);
		fragmentTransaction1.commit();

		FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
		fragmentContent = TabletBaseInputFragment.newInstance(this);
		fragmentTransaction.setCustomAnimations(R.anim.enter, R.anim.exit, R.anim.pop_enter, R.anim.pop_exit);
		fragmentTransaction.replace(R.id.fragment_content_menu_tablet, fragmentContent);
		fragmentTransaction.commit();
	}

	public void gotoPageInputApply(int page) {
		((TabletBaseInputFragment)fragmentContent).gotoPage(page);
	}

	public int getCurrentPageInputApply() {
		if (currentStatus == INPUT_APPLY_STATUS) {
			return ((TabletBaseInputFragment)fragmentContent).getCurrentPage();
		}
		return NOT_VALID;
	}

	public void startActivityAPID() {
		currentStatus = APID_STATUS;
		FragmentTransaction fragmentTransaction1 = fragmentManager.beginTransaction();
		fragmentLeft = new LeftSideAPIDTabletFragment();
		fragmentTransaction1.replace(R.id.fragment_left_side_menu_tablet, fragmentLeft);
		fragmentTransaction1.commit();

		FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
		fragmentTransaction.setCustomAnimations(R.anim.enter, R.anim.exit, R.anim.pop_enter, R.anim.pop_exit);
		fragmentContent= new ContentAPIDTabletFragment();
		fragmentTransaction.replace(R.id.fragment_content_menu_tablet, fragmentContent);
		fragmentTransaction.commit();
	}

	public void goApplyCompleted(){
		currentStatus = COMPLETE_STATUS;
		FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
		fragmentTransaction.setCustomAnimations(R.anim.enter, R.anim.exit, R.anim.pop_enter, R.anim.pop_exit);
		fragmentTransaction.replace(R.id.fragment_content_menu_tablet, TabletInputSuccessFragment.newInstance());
		fragmentTransaction.commit();
		((LeftSideInputTabletFragment)fragmentLeft).hideContent();
	}
	public void goApplyCompleted(InformCtrl m_InformCtrl, ElementApply element){
		currentStatus = COMPLETE_STATUS;
		FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
		fragmentTransaction.setCustomAnimations(R.anim.enter, R.anim.exit, R.anim.pop_enter, R.anim.pop_exit);
		fragmentTransaction.replace(R.id.fragment_content_menu_tablet, TabletInputSuccessFragment.newInstance(m_InformCtrl, element));
		fragmentTransaction.commit();
		((LeftSideInputTabletFragment)fragmentLeft).hideContent();
	}

	public void removeAllFragment() {
		FragmentTransaction fragmentTransaction1 = fragmentManager.beginTransaction();
		fragmentTransaction1.remove(fragmentLeft);
		fragmentTransaction1.remove(fragmentContent);
		fragmentTransaction1.commit();
	}

	public void updateLeftSideInput(int possition) {
		if (fragmentLeft == null) {
			return;
		}
		((LeftSideInputTabletFragment)fragmentLeft).highlightItem(possition);
	}

	/**
	 * Finish install certificate
	 * @param resultCode
	 */
	public void finishInstallCertificate(int resultCode) {
		((TabletBaseInputFragment)fragmentContent).finishInstallCertificate(resultCode);
	}
}
