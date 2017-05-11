package jp.co.soliton.keymanager.adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.Log;
import jp.co.soliton.keymanager.fragment.*;

/**
 * Created by luongdolong on 2/3/2017.
 */

public class ViewPagerTabletAdapter extends FragmentPagerAdapter {
    public static final int TOTAL_PAGES = 7;

    private Context context;
    private Fragment[] listFragment;

    public ViewPagerTabletAdapter(Context context, FragmentManager fm, TabletBaseInputFragment tabletBaseInputFragment) {
        super(fm);
	    Log.d("ViewPagerTabletAdapter:datnd", "ViewPagerTabletAdapter: .");
	    this.context = context;
	    listFragment = new Fragment[TOTAL_PAGES];
	    listFragment[0] = TabletInputHostFragment.newInstance(context, tabletBaseInputFragment);
        listFragment[1] = TabletInputPortFragment.newInstance(context, tabletBaseInputFragment);
        listFragment[2] = TabletInputPlaceFragment.newInstance(context, tabletBaseInputFragment);
        listFragment[3] = TabletInputUserFragment.newInstance(context, tabletBaseInputFragment);
        listFragment[4] = TabletInputEmailFragment.newInstance(context, tabletBaseInputFragment);
        listFragment[5] = TabletInputReasonFragment.newInstance(context, tabletBaseInputFragment);
        listFragment[6] = TabletInputConfirmFragment.newInstance(context, tabletBaseInputFragment);
    }

    @Override
    public Fragment getItem(int position) {
        return listFragment[position];
    }
    @Override
    public int getCount() {
        return TOTAL_PAGES;
    }
}
