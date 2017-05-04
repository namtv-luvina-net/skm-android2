package jp.co.soliton.keymanager.manager;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import jp.co.soliton.keymanager.R;
import jp.co.soliton.keymanager.fragment.FooterInputTabletFragment;
import jp.co.soliton.keymanager.fragment.LeftSideInputTabletFragment;
import jp.co.soliton.keymanager.fragment.TabletBaseInputFragment;

/**
 * Created by luongdolong on 2/8/2017.
 *
 * Processing base input page
 */

public class TabletInputFragmentManager{

	FragmentManager fragmentManager;
	FooterInputTabletFragment footerInputTabletFragment;
	TabletBaseInputFragment tabletBaseInputFragment;

	public TabletInputFragmentManager(FragmentManager fragmentManager) {
		this.fragmentManager = fragmentManager;
	}

	public void startActivityStartApply() {
		FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
		tabletBaseInputFragment = (TabletBaseInputFragment) TabletBaseInputFragment.newInstance();
		fragmentTransaction.setCustomAnimations(R.anim.enter, R.anim.exit, R.anim.pop_enter, R.anim.pop_exit);
		fragmentTransaction.replace(R.id.fragment_content_menu_tablet, tabletBaseInputFragment);
		fragmentTransaction.commit();

		FragmentTransaction fragmentTransaction1 = fragmentManager.beginTransaction();
		fragmentTransaction1.replace(R.id.fragment_left_side_menu_tablet, LeftSideInputTabletFragment.newInstance());
		fragmentTransaction1.commit();
	}
}
