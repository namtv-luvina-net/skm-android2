package jp.co.soliton.keymanager.adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.view.ViewGroup;
import jp.co.soliton.keymanager.fragment.*;

/**
 * Created by luongdolong on 2/3/2017.
 */

public class ViewPagerTabletAdapter extends FragmentStatePagerAdapter {
    public static final int TOTAL_PAGES_INPUT_APPLY = 7;
    public static final int TOTAL_PAGES_UPDATE = 4;
	private String idConfirmApply;

    private Fragment[] listFragment;

	public ViewPagerTabletAdapter(Context context, FragmentManager fm, TabletAbtractInputFragment tabletAbtractInputFragment,
	                              String idConfirmApply) {
        super(fm);
		this.idConfirmApply = idConfirmApply;
		createViewPagerUpdate(context, fm, tabletAbtractInputFragment);
    }

	public ViewPagerTabletAdapter(Context context, FragmentManager fm, TabletAbtractInputFragment tabletAbtractInputFragment) {
        super(fm);
		createViewPagerInputApply(context, fm, tabletAbtractInputFragment);
    }

	private void createViewPagerUpdate(Context context, FragmentManager fm, TabletAbtractInputFragment tabletBaseInputFragment) {
		listFragment = new Fragment[TOTAL_PAGES_UPDATE];
		listFragment[0] = TabletInputUserFragment.newInstance(context, tabletBaseInputFragment, idConfirmApply);
		listFragment[1] = TabletInputEmailFragment.newInstance(context, tabletBaseInputFragment);
		listFragment[2] = TabletInputReasonFragment.newInstance(context, tabletBaseInputFragment);
		listFragment[3] = TabletInputConfirmFragment.newInstance(context, tabletBaseInputFragment, idConfirmApply);
	}

	private void createViewPagerInputApply(Context context, FragmentManager fm, TabletAbtractInputFragment
			tabletBaseInputFragment) {
		listFragment = new Fragment[TOTAL_PAGES_INPUT_APPLY];
		listFragment[0] = TabletInputHostFragment.newInstance(context, tabletBaseInputFragment);
		listFragment[1] = TabletInputPortFragment.newInstance(context, tabletBaseInputFragment);
		listFragment[2] = TabletInputPlaceFragment.newInstance(context, tabletBaseInputFragment);
		listFragment[3] = TabletInputUserFragment.newInstance(context, tabletBaseInputFragment);
		listFragment[4] = TabletInputEmailFragment.newInstance(context, tabletBaseInputFragment);
		listFragment[5] = TabletInputReasonFragment.newInstance(context, tabletBaseInputFragment);
		listFragment[6] = TabletInputConfirmFragment.newInstance(context, tabletBaseInputFragment, idConfirmApply);
	}

	@Override
	public Object instantiateItem(ViewGroup container, int position) {
		Fragment createdFragment = (Fragment) super.instantiateItem(container, position);
		listFragment[position] = createdFragment;
		return createdFragment;
	}

    @Override
    public Fragment getItem(int position) {
        return listFragment[position];
    }

    @Override
    public int getCount() {
        if (idConfirmApply != null) {
	        return TOTAL_PAGES_UPDATE;
        } else {
	        return TOTAL_PAGES_INPUT_APPLY;
        }
    }
}
