package jp.co.soliton.keymanager.adapter;

import android.content.Context;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import jp.co.soliton.keymanager.fragment.UpdateEmailPageFragment;
import jp.co.soliton.keymanager.fragment.UpdateReasonPageFragment;
import jp.co.soliton.keymanager.fragment.UpdateUserPageFragment;

/**
 * Created by luongdolong on 2/3/2017.
 */

public class ViewPagerUpdateAdapter extends FragmentPagerAdapter {
    public static int totalPage = 3;

    private Context context;
    private Fragment [] listFragment = new Fragment[3];

    public ViewPagerUpdateAdapter(Context context, FragmentManager fm) {
        super(fm);
        this.context = context;
        listFragment[0] = UpdateUserPageFragment.newInstance(context);
        listFragment[1] = UpdateEmailPageFragment.newInstance(context);
        listFragment[2] = UpdateReasonPageFragment.newInstance(context);
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
