package jp.co.soliton.keymanager.adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import jp.co.soliton.keymanager.fragment.InputBasePageFragment;
import jp.co.soliton.keymanager.fragment.InputEmailPageFragment;
import jp.co.soliton.keymanager.fragment.InputHostPageFragment;
import jp.co.soliton.keymanager.fragment.InputUserPageFragment;
import jp.co.soliton.keymanager.fragment.InputPortPageFragment;
import jp.co.soliton.keymanager.fragment.InputReasonPageFragment;
import jp.co.soliton.keymanager.fragment.InputPlacePageFragment;

/**
 * Created by luongdolong on 2/3/2017.
 */

public class ViewPagerAdapter extends FragmentPagerAdapter {
    public static int totalPage = 6;

    private Context context;
    private Fragment [] listFragment = new Fragment[6];

    public ViewPagerAdapter(Context context, FragmentManager fm) {
        super(fm);
        this.context = context;
        listFragment[0] = InputHostPageFragment.newInstance(context);
        listFragment[1] = InputPortPageFragment.newInstance(context);
        listFragment[2] = InputPlacePageFragment.newInstance(context);
        listFragment[3] = InputUserPageFragment.newInstance(context);
        listFragment[4] = InputEmailPageFragment.newInstance(context);
        listFragment[5] = InputReasonPageFragment.newInstance(context);
    }

    @Override
    public Fragment getItem(int position) {
        return listFragment[position];
    }
    @Override
    public int getCount() {
        return totalPage;
    }
}
