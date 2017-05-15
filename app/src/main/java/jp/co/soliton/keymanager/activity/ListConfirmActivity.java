package jp.co.soliton.keymanager.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import jp.co.soliton.keymanager.R;
import jp.co.soliton.keymanager.dbalias.ElementApply;
import jp.co.soliton.keymanager.dbalias.ElementApplyManager;
import jp.co.soliton.keymanager.fragment.ContentListConfirmTabletFragment;
import jp.co.soliton.keymanager.fragment.LeftSideAPIDTabletFragment;

import java.util.List;

/**
 * Created by lexuanvinh on 02/27/2017.
 */

public class ListConfirmActivity extends FragmentActivity {

    private ElementApplyManager elementMgr;
    private List<ElementApply> listElementApply;
	private boolean isTablet;
	Fragment fragmentLeft, fragmentContent;
	FragmentManager fragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
	    fragmentManager = getSupportFragmentManager();
	    setOrientation();
        setContentView(R.layout.activity_list_confirm);
        elementMgr = new ElementApplyManager(getApplicationContext());
    }

	private void setOrientation() {
		isTablet = getResources().getBoolean(R.bool.isTablet);
		if (!isTablet) {
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		} else {
			FragmentTransaction fragmentTransaction1 = fragmentManager.beginTransaction();
			fragmentLeft = new LeftSideAPIDTabletFragment();
			fragmentTransaction1.replace(R.id.fragment_left_side_menu_tablet, fragmentLeft);
			fragmentTransaction1.commit();

			FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
			fragmentTransaction.setCustomAnimations(R.anim.enter, R.anim.exit, R.anim.pop_enter, R.anim.pop_exit);
			fragmentContent= new ContentListConfirmTabletFragment();
			fragmentTransaction.replace(R.id.fragment_content_menu_tablet, fragmentContent);
			fragmentTransaction.commit();
		}
	}

	@Override
	public void onBackPressed() {
		btnBackClick(null);
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

    /**
     * Update List Certificate, Certificate delete screen DetailCertActivity
     */
    @Override
    protected void onResume() {
        super.onResume();
        listElementApply = elementMgr.getAllElementApply();
        if(listElementApply.size() == 1) {
            Intent intent = new Intent(ListConfirmActivity.this, DetailConfirmActivity.class);
            intent.putExtra("ELEMENT_APPLY_ID", String.valueOf(listElementApply.get(0).getId()));
            finish();
            startActivity(intent);
	        overridePendingTransition(0, 0);
        } else if(listElementApply.size() == 0) {
            finish();
        }
    }

	public List<ElementApply> getListElementApply() {
		return listElementApply;
	}
}
