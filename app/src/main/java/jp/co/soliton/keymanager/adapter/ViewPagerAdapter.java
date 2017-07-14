package jp.co.soliton.keymanager.adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import jp.co.soliton.keymanager.fragment.*;

import java.util.ArrayList;

/**
 * Created by luongdolong on 2/3/2017.
 */

public class ViewPagerAdapter extends FragmentPagerAdapter {
    private Context context;
    private ArrayList<Fragment> listFragment = new ArrayList<>();
	private ArrayList<String> titles = new ArrayList<>();

    public ViewPagerAdapter(Context context, FragmentManager fm) {
        super(fm);
        this.context = context;
    }

    public void init() {
	    addFragment(InputHostPageFragment.newInstance(context), InputHostPageFragment.class.getName());
	    addFragment(InputPortPageFragment.newInstance(context), InputPortPageFragment.class.getName());
	    addFragment(InputPlacePageFragment.newInstance(context), InputPlacePageFragment.class.getName());
	    addFragment(InputUserPageFragment.newInstance(context), InputUserPageFragment.class.getName());
	    addFragment(InputEmailPageFragment.newInstance(context), InputEmailPageFragment.class.getName());
	    addFragment(InputReasonPageFragment.newInstance(context) , InputReasonPageFragment.class.getName());
    }

	public void addFragment(Fragment fragment, String title) {
		listFragment.add(fragment);
		titles.add(title);
	}

	public ArrayList<String> getTitles() {
		return titles;
	}

	@Override
    public Fragment getItem(int position) {
        return listFragment.get(position);
    }
    @Override
    public int getCount() {
        return listFragment.size();
    }
}
