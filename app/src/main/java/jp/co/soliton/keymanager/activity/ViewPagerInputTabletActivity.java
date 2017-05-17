package jp.co.soliton.keymanager.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import jp.co.soliton.keymanager.InformCtrl;
import jp.co.soliton.keymanager.InputApplyInfo;
import jp.co.soliton.keymanager.R;
import jp.co.soliton.keymanager.StringList;
import jp.co.soliton.keymanager.dbalias.ElementApply;
import jp.co.soliton.keymanager.fragment.LeftSideInputTabletFragment;
import jp.co.soliton.keymanager.fragment.TabletBaseInputFragment;
import jp.co.soliton.keymanager.fragment.TabletInputSuccessFragment;

/**
 * Created by nguyenducdat on 05/16/2017.
 */

public class ViewPagerInputTabletActivity extends FragmentActivity {

	public static final int STATUS_SUCCESS = 1;
	private boolean isTablet;
	Fragment fragmentLeft, fragmentContent;
	FragmentManager fragmentManager;
	String idConfirmApply;
	int status = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
	    fragmentManager = getSupportFragmentManager();
	    setOrientation();
        setContentView(R.layout.activity_list_confirm);
		idConfirmApply = getIntent().getStringExtra("ELEMENT_APPLY_ID");
    }

	public String getIdConfirmApply() {
		return idConfirmApply;
	}

	private void setOrientation() {
		isTablet = getResources().getBoolean(R.bool.isTablet);
		if (isTablet) {
			FragmentTransaction fragmentTransaction1 = fragmentManager.beginTransaction();
			fragmentLeft = new LeftSideInputTabletFragment();
			fragmentTransaction1.replace(R.id.fragment_left_side_menu_tablet, fragmentLeft);
			fragmentTransaction1.commit();

			FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
			fragmentTransaction.setCustomAnimations(R.anim.enter, R.anim.exit, R.anim.pop_enter, R.anim.pop_exit);
			fragmentContent= TabletBaseInputFragment.newInstance();
			fragmentTransaction.replace(R.id.fragment_content_menu_tablet, fragmentContent);
			fragmentTransaction.commit();
		}
	}

	@Override
	public void onBackPressed() {
		if (status == STATUS_SUCCESS){
			gotoMenu();
		}else {
			((TabletBaseInputFragment) fragmentContent).clickBackButton();
		}
	}

	public void btnBackClick(View v) {
		if (isTablet) {
			FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
			fragmentTransaction.setCustomAnimations(R.anim.pop_enter, R.anim.pop_exit, R.anim.enter, R.anim.exit);
			fragmentTransaction.remove(fragmentContent);
			fragmentTransaction.commit();
			final Activity activity = this;
			final Handler handler = new Handler();
			handler.postDelayed(new Runnable() {
				@Override
				public void run() {
					activity.finish();
					activity.overridePendingTransition(0, 0);
				}
			}, getResources().getInteger(android.R.integer.config_shortAnimTime));
		} else {
			finish();
		}
    }

	public void updateLeftSideInput(int possition) {
		if (fragmentLeft == null) {
			return;
		}
		((LeftSideInputTabletFragment)fragmentLeft).highlightItem(possition);
	}

	public void goApplyCompleted(){
		TabletInputSuccessFragment tabletInputSuccessFragment = (TabletInputSuccessFragment) TabletInputSuccessFragment
				.newInstance();
		gotoApplyCompleteFragment(tabletInputSuccessFragment);
	}

	public void goApplyCompleted(InformCtrl m_InformCtrl, ElementApply element){
		TabletInputSuccessFragment tabletInputSuccessFragment = (TabletInputSuccessFragment) TabletInputSuccessFragment
				.newInstance(m_InformCtrl, element);
		gotoApplyCompleteFragment(tabletInputSuccessFragment);
	}

	private void gotoApplyCompleteFragment(TabletInputSuccessFragment tabletInputSuccessFragment){
		status = STATUS_SUCCESS;
		FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
		fragmentTransaction.setCustomAnimations(R.anim.enter, R.anim.exit, R.anim.pop_enter, R.anim.pop_exit);
		fragmentTransaction.replace(R.id.fragment_content_menu_tablet, tabletInputSuccessFragment);
		fragmentTransaction.commit();
		((LeftSideInputTabletFragment)fragmentLeft).hideContent();
	}

	public void startUsingProceduresActivity(final InformCtrl m_InformCtrl, final ElementApply element) {
		FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
		fragmentTransaction.setCustomAnimations(R.anim.pop_enter, R.anim.pop_exit, R.anim.enter, R.anim.exit);
		fragmentTransaction.remove(fragmentContent);
		fragmentTransaction.commit();
		final Handler handler = new Handler();
		handler.postDelayed(new Runnable() {
			@Override
			public void run() {
				Intent intent = new Intent(ViewPagerInputTabletActivity.this, StartUsingProceduresActivity.class);
				intent.putExtra(StringList.m_str_InformCtrl, m_InformCtrl);
				intent.putExtra("ELEMENT_APPLY", element);
				startActivity(intent);
				overridePendingTransition(0, 0);
				finish();
			}
		}, getResources().getInteger(android.R.integer.config_shortAnimTime));
	}

	public void gotoMenu() {
		FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
		fragmentTransaction.setCustomAnimations(R.anim.pop_enter, R.anim.pop_exit, R.anim.enter, R.anim.exit);
		fragmentTransaction.remove(fragmentContent);
		fragmentTransaction.commit();
		final Handler handler = new Handler();
		handler.postDelayed(new Runnable() {
			@Override
			public void run() {
				InputApplyInfo.deletePref(ViewPagerInputTabletActivity.this);
				Intent intent = new Intent(ViewPagerInputTabletActivity.this, MenuAcivity.class);
				intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(intent);
				overridePendingTransition(0, 0);
				finish();
			}
		}, getResources().getInteger(android.R.integer.config_shortAnimTime));
	}


    @Override
    protected void onResume() {
        super.onResume();
    }
}
