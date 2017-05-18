package jp.co.soliton.keymanager.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.*;
import android.support.v4.content.ContextCompat;
import jp.co.soliton.keymanager.InputApplyInfo;
import jp.co.soliton.keymanager.R;
import jp.co.soliton.keymanager.StringList;
import jp.co.soliton.keymanager.fragment.ContentAPIDTabletFragment;
import jp.co.soliton.keymanager.fragment.ContentMenuTabletFragment;
import jp.co.soliton.keymanager.fragment.LeftSideAPIDTabletFragment;
import jp.co.soliton.keymanager.fragment.LeftSideMenuTabletFragment;

/**
 * Created by luongdolong on 2/3/2017.
 *
 * Activity for menu apply screen
 */

public class MenuAcivity extends FragmentActivity {
	public static final int RESET_STATUS = 0;
	public static final int APID_STATUS = 1;
	public static final int COMPLETE_STATUS = 3;

    private int PERMISSIONS_REQUEST_READ_PHONE_STATE = 10;
	private boolean isTablet;
	private boolean isFocusMenuTablet;

	public int currentStatus;
	FragmentManager fragmentManager;
	Fragment fragmentLeft, fragmentContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
	    setContentView(R.layout.activity_menu);
	    setOrientation();
	    fragmentManager = getSupportFragmentManager();
	    if (savedInstanceState == null) {
		    createView();
	    } else if (isTablet) {
		    fragmentContent = getSupportFragmentManager().getFragment(savedInstanceState, "fragmentContent");
		    fragmentLeft = getSupportFragmentManager().getFragment(savedInstanceState, "fragmentLeft");
		    currentStatus = savedInstanceState.getInt("currentStatus");
	    }
    }

	@Override
	public void onSaveInstanceState(Bundle savedInstanceState) {
		super.onSaveInstanceState(savedInstanceState);
		if (isTablet) {
			savedInstanceState.putInt("currentStatus", currentStatus);
			getSupportFragmentManager().putFragment(savedInstanceState, "fragmentContent", fragmentContent);
			getSupportFragmentManager().putFragment(savedInstanceState, "fragmentLeft", fragmentLeft);
		}
	}

	private void createView() {
		if (isTablet) {
			isFocusMenuTablet = true;
			gotoMenu();
		}
	}

	private void setOrientation() {
		isTablet = getResources().getBoolean(R.bool.isTablet);
		if (!isTablet) {
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		} else {
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
		}
	}

	@Override
	public void onBackPressed() {
		if (!isFocusMenuTablet && isTablet) {
			if (currentStatus == COMPLETE_STATUS) {
				InputApplyInfo.deletePref(this);
				Intent intent = new Intent(MenuAcivity.this, MenuAcivity.class);
				intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(intent);
				finish();
			}else {
				gotoMenu();
			}
		} else {
			super.onBackPressed();
		}
	}

	public void goToMenu() {
		isFocusMenuTablet = true;
		gotoMenu();
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

	public void gotoMenu() {
		isFocusMenuTablet = true;
		currentStatus = RESET_STATUS;
		FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
		fragmentTransaction.setCustomAnimations(R.anim.pop_enter, R.anim.pop_exit, R.anim.exit, R.anim.enter);
		fragmentContent = ContentMenuTabletFragment.newInstance();
		fragmentTransaction.replace(R.id.fragment_content_menu_tablet, fragmentContent);
		fragmentTransaction.commit();

		fragmentTransaction = fragmentManager.beginTransaction();
		fragmentLeft = new LeftSideMenuTabletFragment();
		fragmentTransaction.replace(R.id.fragment_left_side_menu_tablet, fragmentLeft);
		fragmentTransaction.commit();
	}

	public void startActivityAPID() {
		currentStatus = APID_STATUS;
		FragmentTransaction fragmentTransaction1 = fragmentManager.beginTransaction();
		fragmentLeft = new LeftSideAPIDTabletFragment();
		fragmentTransaction1.replace(R.id.fragment_left_side_menu_tablet, fragmentLeft);
		fragmentTransaction1.commit();

		FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
		fragmentTransaction.setCustomAnimations(R.anim.enter, R.anim.exit, R.anim.pop_enter, R.anim.pop_exit);
		fragmentContent= new ContentAPIDTabletFragment();
		fragmentTransaction.replace(R.id.fragment_content_menu_tablet, fragmentContent);
		fragmentTransaction.commit();
	}
}
