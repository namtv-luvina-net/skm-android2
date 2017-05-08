package jp.co.soliton.keymanager.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import jp.co.soliton.keymanager.LogCtrl;
import jp.co.soliton.keymanager.R;
import jp.co.soliton.keymanager.StringList;
import jp.co.soliton.keymanager.fragment.ContentMenuTabletFragment;
import jp.co.soliton.keymanager.fragment.LeftSideMenuTabletFragment;

import static jp.co.soliton.keymanager.common.ControlPagesInput.REQUEST_CODE_INSTALL_CERTIFICATION;
import static jp.co.soliton.keymanager.fragment.ContentMenuTabletFragment.INPUT_APPLY_STATUS;

/**
 * Created by luongdolong on 2/3/2017.
 *
 * Activity for menu apply screen
 */

public class MenuAcivity extends FragmentActivity {
    private int PERMISSIONS_REQUEST_READ_PHONE_STATE = 10;
	FragmentManager fragmentManager;
	private boolean isTablet;
	private LogCtrl logCtrl;
	private boolean isFocusMenuTablet;
	ContentMenuTabletFragment contentMenuTabletFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
	    setOrientation();
	    setContentView(R.layout.activity_menu);
	    logCtrl = LogCtrl.getInstance(this);
	    fragmentManager = getSupportFragmentManager();
	    createView();
    }

	private void createView() {
		if (isTablet) {
			isFocusMenuTablet = true;
			FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
			fragmentTransaction.add(R.id.fragment_left_side_menu_tablet, new LeftSideMenuTabletFragment());
			contentMenuTabletFragment = new ContentMenuTabletFragment();
			fragmentTransaction.add(R.id.fragment_content_menu_tablet, contentMenuTabletFragment);
			fragmentTransaction.commit();
		}
	}

	private void setOrientation() {
		isTablet = getResources().getBoolean(R.bool.isTablet);
		if (isTablet) {
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
		} else {
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		}
	}

	@Override
	public void onBackPressed() {
		if (!isFocusMenuTablet && isTablet) {
			if (contentMenuTabletFragment.currentStatus == INPUT_APPLY_STATUS) {
				contentMenuTabletFragment.pressBackInputApply();
			}else {
				goToMenu();
			}
		} else {
			super.onBackPressed();
		}
	}

	public void goToMenu() {
		isFocusMenuTablet = true;
		FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
		fragmentTransaction.setCustomAnimations(R.anim.pop_enter, R.anim.pop_exit, R.anim.exit, R.anim.enter);
		contentMenuTabletFragment = new ContentMenuTabletFragment();
		fragmentTransaction.replace(R.id.fragment_content_menu_tablet, contentMenuTabletFragment);
		fragmentTransaction.commit();

		fragmentTransaction = fragmentManager.beginTransaction();
		fragmentTransaction.replace(R.id.fragment_left_side_menu_tablet, new LeftSideMenuTabletFragment());
		fragmentTransaction.commit();
	}

	public boolean isFocusMenuTablet() {
		return isFocusMenuTablet;
	}

	public void setFocusMenuTablet(boolean focusMenuTablet) {
		isFocusMenuTablet = focusMenuTablet;
	}

	@Override
    protected void onResume() {
        super.onResume();
        if (StringList.GO_TO_LIST_APPLY.equals("1")) {
            StringList.GO_TO_LIST_APPLY = "0";
            Intent intent = new Intent(MenuAcivity.this, ListConfirmActivity.class);
            startActivity(intent);
        }
        if(android.os.Build.VERSION.SDK_INT >= 23) {
            NewPermissionSet();
        }
    }

    private void NewPermissionSet() {
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_PHONE_STATE},
                    PERMISSIONS_REQUEST_READ_PHONE_STATE);
        }
    }

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		LogCtrl.getInstance(this).loggerInfo("ViewPagerInputActivity:onActivityResult  requestCode = " + requestCode + ". " +
				"resultCode = " + requestCode);
		if (requestCode == REQUEST_CODE_INSTALL_CERTIFICATION) {
			contentMenuTabletFragment.finishInstallCertificate(resultCode);
		}
	}
}
