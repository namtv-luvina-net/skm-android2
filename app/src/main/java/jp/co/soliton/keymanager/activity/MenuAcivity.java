package jp.co.soliton.keymanager.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import jp.co.soliton.keymanager.InputApplyInfo;
import jp.co.soliton.keymanager.LogCtrl;
import jp.co.soliton.keymanager.R;
import jp.co.soliton.keymanager.StringList;
import jp.co.soliton.keymanager.manager.TabletContentFragmentManager;

import static jp.co.soliton.keymanager.common.ControlPagesInput.REQUEST_CODE_INSTALL_CERTIFICATION;
import static jp.co.soliton.keymanager.manager.TabletContentFragmentManager.COMPLETE_STATUS;
import static jp.co.soliton.keymanager.manager.TabletContentFragmentManager.INPUT_APPLY_STATUS;

/**
 * Created by luongdolong on 2/3/2017.
 *
 * Activity for menu apply screen
 */

public class MenuAcivity extends FragmentActivity {
    private int PERMISSIONS_REQUEST_READ_PHONE_STATE = 10;
	private boolean isTablet;
	private LogCtrl logCtrl;
	private boolean isFocusMenuTablet;
	TabletContentFragmentManager tabletContentFragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
	    Log.d("MenuAcivity:datnd", "onCreate: ");
	    setOrientation();
	    setContentView(R.layout.activity_menu);
	    logCtrl = LogCtrl.getInstance(this);
	    tabletContentFragmentManager = new TabletContentFragmentManager(this, getSupportFragmentManager());
	    if (savedInstanceState == null) {
		    createView();
	    } else {
		    int currentStatus = savedInstanceState.getInt("currentStatus");
		    int currentPage = savedInstanceState.getInt("currentPage");
		    Log.d("MenuAcivity:datnd", "onCreate: " + currentStatus +" - " + currentPage);
		    tabletContentFragmentManager.gotoLastState(currentStatus);
//		    if (currentStatus == INPUT_APPLY_STATUS && currentPage >= 0){
//			    tabletContentFragmentManager.gotoPageInputApply(currentPage);
//		    }
	    }
    }

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
		int currentStatus = savedInstanceState.getInt("currentStatus");
		int currentPage = savedInstanceState.getInt("currentPage");
		tabletContentFragmentManager.gotoLastState(currentStatus);
		Log.d("MenuAcivity:datnd", "onRestoreInstanceState: ");
	}

	@Override
	public void onSaveInstanceState(Bundle savedInstanceState) {
		Log.d("MenuAcivity:datnd", "onSaveInstanceState: ");
		tabletContentFragmentManager.removeAllFragment();
		super.onSaveInstanceState(savedInstanceState);
		int currentStatus = tabletContentFragmentManager.currentStatus;
		savedInstanceState.putInt("currentStatus", currentStatus);
		savedInstanceState.putInt("currentPage", tabletContentFragmentManager.getCurrentPageInputApply());
	}
//
//	@Override
//	public void onConfigurationChanged(Configuration newConfig) {
//		super.onConfigurationChanged(newConfig);
//		Log.d("MenuAcivity:datnd", "onConfigurationChanged: " + newConfig.toString());
//	}

	@Override
	protected void onDestroy() {
		Log.d("MenuAcivity:datnd", "onDestroy: ");
		super.onDestroy();
	}

	private void createView() {
		Log.d("MenuAcivity:datnd", "createView: ");
		if (isTablet) {
			isFocusMenuTablet = true;
			tabletContentFragmentManager.gotoMenu();
		}
	}

	private void setOrientation() {
		isTablet = getResources().getBoolean(R.bool.isTablet);
		if (!isTablet) {
//			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
//		} else {
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		}
	}

	@Override
	public void onBackPressed() {
		if (!isFocusMenuTablet && isTablet) {
			if (tabletContentFragmentManager.currentStatus == INPUT_APPLY_STATUS) {
				tabletContentFragmentManager.pressBackInputApply();
			} else if (tabletContentFragmentManager.currentStatus == COMPLETE_STATUS) {
				InputApplyInfo.deletePref(this);
				Intent intent = new Intent(MenuAcivity.this, MenuAcivity.class);
				intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(intent);
				finish();
			}else {
				tabletContentFragmentManager.gotoMenu();
			}
		} else {
			super.onBackPressed();
		}
	}

	public void goToMenu() {
		isFocusMenuTablet = true;
		tabletContentFragmentManager.gotoMenu();
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
			tabletContentFragmentManager.finishInstallCertificate(resultCode);
		}
	}
}
