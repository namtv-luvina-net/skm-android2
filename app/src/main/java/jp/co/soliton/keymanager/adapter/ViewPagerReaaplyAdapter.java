package jp.co.soliton.keymanager.adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import jp.co.soliton.keymanager.fragment.InputEmailPageFragment;
import jp.co.soliton.keymanager.fragment.InputHostPageFragment;
import jp.co.soliton.keymanager.fragment.InputPlacePageFragment;
import jp.co.soliton.keymanager.fragment.InputPortPageFragment;
import jp.co.soliton.keymanager.fragment.InputReasonPageFragment;
import jp.co.soliton.keymanager.fragment.InputUserPageFragment;
import jp.co.soliton.keymanager.fragment.ReapplyEmailPageFragment;
import jp.co.soliton.keymanager.fragment.ReapplyReasonPageFragment;
import jp.co.soliton.keymanager.fragment.ReapplyUserPageFragment;

/**
 * Created by luongdolong on 2/3/2017.
 */

public class ViewPagerReaaplyAdapter extends FragmentPagerAdapter {
    public static int totalPage = 6;

    private Context context;
    private Fragment [] listFragment = new Fragment[6];

    public ViewPagerReaaplyAdapter(Context context, FragmentManager fm) {
        super(fm);
        this.context = context;
        listFragment[0] = ReapplyUserPageFragment.newInstance(context);
        listFragment[1] = ReapplyEmailPageFragment.newInstance(context);
        listFragment[2] = ReapplyReasonPageFragment.newInstance(context);
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
