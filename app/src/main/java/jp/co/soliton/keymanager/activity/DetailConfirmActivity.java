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

import jp.co.soliton.keymanager.InputApplyInfo;
import jp.co.soliton.keymanager.R;
import jp.co.soliton.keymanager.customview.DialogApplyConfirm;
import jp.co.soliton.keymanager.dbalias.ElementApplyManager;
import jp.co.soliton.keymanager.fragment.ContentDetailConfirmFragment;
import jp.co.soliton.keymanager.fragment.LeftSideAPIDTabletFragment;

/**
 * Created by lexuanvinh on 02/27/2017.
 */

public class DetailConfirmActivity extends FragmentActivity {

    private ElementApplyManager elementMgr;
    private String id;
	private boolean isTablet;
	Fragment fragmentLeft, fragmentContent;
	FragmentManager fragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
	    fragmentManager = getSupportFragmentManager();
	    setOrientation();
	    setContentView(R.layout.activity_detail_confirm);
	    id = getIntent().getStringExtra("ELEMENT_APPLY_ID");
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
			fragmentContent= new ContentDetailConfirmFragment();
			fragmentTransaction.replace(R.id.fragment_content_menu_tablet, fragmentContent);
			fragmentTransaction.commit();
		}
	}

    public void clickConfirmApply(View v) {
        Intent intent = new Intent(DetailConfirmActivity.this, InputPasswordActivity.class);
        intent.putExtra("ELEMENT_APPLY_ID", id);
        startActivity(intent);
    }

    public void clickReApply(View v) {
        InputApplyInfo.deletePref(DetailConfirmActivity.this);
        Intent intent = new Intent(DetailConfirmActivity.this, ViewPagerInputActivity.class);
        intent.putExtra("ELEMENT_APPLY_ID", id);
        startActivity(intent);
    }

    public void clickDeleteApply(View v) {
        final DialogApplyConfirm dialog = new DialogApplyConfirm(this);
        dialog.setTextDisplay(getString(R.string.dialog_delete_title), getString(R.string.dialog_delete_msg)
                , getString(R.string.label_dialog_Cancle), getString(R.string.label_dialog_delete_cert));
        dialog.setOnClickOK(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                elementMgr.deleteElementApply(id);
                Intent intent = new Intent(getApplicationContext(), MenuAcivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });
        dialog.show();
    }

    public void clickWithdrawApply(View v) {
        Intent intent = new Intent(DetailConfirmActivity.this, InputPasswordActivity.class);
        intent.putExtra("ELEMENT_APPLY_ID", id);
        intent.putExtra("CANCEL_APPLY", "1");
        startActivity(intent);
    }

	public String getId() {
		return id;
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
        int totalApply = elementMgr.getCountElementApply();
        if (totalApply <= 0) {
            finish();
        }
    }
}
