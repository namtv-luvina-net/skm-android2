package jp.co.soliton.keymanager.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.*;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import jp.co.soliton.keymanager.*;
import jp.co.soliton.keymanager.dbalias.ElementApply;
import jp.co.soliton.keymanager.fragment.*;

import static jp.co.soliton.keymanager.common.ControlPagesInput.REQUEST_CODE_INSTALL_CERTIFICATION;

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
//	TabletContentFragmentManager tabletContentFragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
	    Log.d("MenuAcivity:datnd", "onCreate: ");
	    setOrientation();
	    setContentView(R.layout.activity_menu);
	    logCtrl = LogCtrl.getInstance(this);
	    fragmentManager = getSupportFragmentManager();
	    if (savedInstanceState == null) {
//		    tabletContentFragmentManager = new TabletContentFragmentManager(getSupportFragmentManager());
		    createView();
	    } else {
		    int currentStatus = savedInstanceState.getInt("currentStatus");
		    int currentPage = savedInstanceState.getInt("currentPage");
		    Log.d("MenuAcivity:datnd", "onCreate: " + currentStatus +" - " + currentPage);
//		    gotoLastState(currentStatus);
//		    tabletContentFragmentManager.gotoLastState(currentStatus);
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
//		tabletContentFragmentManager = (TabletContentFragmentManager) savedInstanceState.getSerializable("tabletContentFragmentManager");
//		tabletContentFragmentManager.gotoLastState(currentStatus);
		Log.d("MenuAcivity:datnd", "onRestoreInstanceState: ");
	}

	@Override
	public void onSaveInstanceState(Bundle savedInstanceState) {
		Log.d("MenuAcivity:datnd", "onSaveInstanceState: ");
		super.onSaveInstanceState(savedInstanceState);
//		int currentStatus = tabletContentFragmentManager.currentStatus;
//		savedInstanceState.putSerializable("tabletContentFragmentManager", tabletContentFragmentManager);
		savedInstanceState.putInt("currentStatus", currentStatus);
		savedInstanceState.putInt("currentPage", getCurrentPageInputApply());
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
			gotoMenu();
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
			if (currentStatus == INPUT_APPLY_STATUS) {
				pressBackInputApply();
			} else if (currentStatus == COMPLETE_STATUS) {
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
			finishInstallCertificate(resultCode);
		}
	}


	//======================================================================================================================
	public static final int NOT_VALID = -1;
	public static final int RESET_STATUS = 0;
	public static final int APID_STATUS = 1;
	public static final int INPUT_APPLY_STATUS = 2;
	public static final int COMPLETE_STATUS = 3;
	public int currentStatus;
	FragmentManager fragmentManager;
	Fragment fragmentLeft, fragmentContent;

	public void gotoLastState(int currentStatus) {
		switch (currentStatus) {
			case RESET_STATUS:
				gotoMenu();
				break;
			case APID_STATUS:
				startActivityAPID();
				break;
			case INPUT_APPLY_STATUS:
				startActivityStartApply();
				break;
			case COMPLETE_STATUS:
				goApplyCompleted();
				break;
			default:
				break;
		}
	}

	public void gotoMenu() {
		Log.d("TabletContentFragmentManager:datnd", "gotoMenu: ");
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

	public void pressBackInputApply() {
		((TabletBaseInputFragment)fragmentContent).clickBackButton();
	}

	public void startActivityStartApply() {
		Log.d("TabletContentFragmentManager:datnd", "startActivityStartApply: ");
		currentStatus = INPUT_APPLY_STATUS;
		FragmentTransaction fragmentTransaction1 = fragmentManager.beginTransaction();
		fragmentLeft = LeftSideInputTabletFragment.newInstance();
		fragmentTransaction1.replace(R.id.fragment_left_side_menu_tablet, fragmentLeft);
		fragmentTransaction1.commit();

		FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
		fragmentContent = TabletBaseInputFragment.newInstance();
		FooterInputTabletFragment footerInputTabletFragment = (FooterInputTabletFragment) FooterInputTabletFragment.newInstance();


		fragmentTransaction.setCustomAnimations(R.anim.enter, R.anim.exit, R.anim.pop_enter, R.anim.pop_exit);
		fragmentTransaction.replace(R.id.fragment_content_menu_tablet, fragmentContent);
		fragmentTransaction.replace(R.id.fragment_content_footer_tablet, footerInputTabletFragment);
		fragmentTransaction.commit();
	}


	private FooterInputTabletFragment.InterfaceForFooter createIntefaceForFooter(final TabletBaseInputFragment tabletBaseInputFragment){
		return new FooterInputTabletFragment.InterfaceForFooter() {
			@Override
			public void clickNext() {
				tabletBaseInputFragment.clickNext();
			}

			@Override
			public void clickBack() {
				tabletBaseInputFragment.clickBack();
			}

			@Override
			public void clickSkip() {
				tabletBaseInputFragment.clickSkip();
			}
		};
	}

	private FooterInputTabletFragment.InterfaceForContent createIntefaceForContent(final FooterInputTabletFragment
			                                                                               footerInputTabletFragment) {
		return new FooterInputTabletFragment.InterfaceForContent(){

			@Override
			public void updateButtonFooterStatus(int position) {
				footerInputTabletFragment.updateButtonFooterStatus(position);
			}

			@Override
			public void clickBackButton() {
				footerInputTabletFragment.clickBackButton();
			}

			@Override
			public void setStatusBackNext(int position) {
				footerInputTabletFragment.setStatusBackNext(position);
			}

			@Override
			public void goneSkipButton() {
				footerInputTabletFragment.goneSkipButton();
			}

			@Override
			public void visibleSkipButton() {
				footerInputTabletFragment.visibleSkipButton();
			}

			@Override
			public void disableNextButton() {
				footerInputTabletFragment.disableNextButton();
			}

			@Override
			public void enableNextButton() {
				footerInputTabletFragment.enableNextButton();
			}
		};
	}

	public void gotoPageInputApply(int page) {
		((TabletBaseInputFragment)fragmentContent).gotoPage(page);
	}

	public int getCurrentPageInputApply() {
		if (currentStatus == INPUT_APPLY_STATUS) {
			return ((TabletBaseInputFragment)fragmentContent).getCurrentPage();
		}
		return NOT_VALID;
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

	public void goApplyCompleted(){
		currentStatus = COMPLETE_STATUS;
		FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
		fragmentTransaction.setCustomAnimations(R.anim.enter, R.anim.exit, R.anim.pop_enter, R.anim.pop_exit);
		fragmentTransaction.replace(R.id.fragment_content_menu_tablet, TabletInputSuccessFragment.newInstance());
		fragmentTransaction.commit();
		((LeftSideInputTabletFragment)fragmentLeft).hideContent();
	}
	public void goApplyCompleted(InformCtrl m_InformCtrl, ElementApply element){
		currentStatus = COMPLETE_STATUS;
		FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
		fragmentTransaction.setCustomAnimations(R.anim.enter, R.anim.exit, R.anim.pop_enter, R.anim.pop_exit);
		fragmentTransaction.replace(R.id.fragment_content_menu_tablet, TabletInputSuccessFragment.newInstance(m_InformCtrl, element));
		fragmentTransaction.commit();
		((LeftSideInputTabletFragment)fragmentLeft).hideContent();
	}

	public void updateLeftSideInput(int possition) {
		if (fragmentLeft == null) {
			return;
		}
		((LeftSideInputTabletFragment)fragmentLeft).highlightItem(possition);
	}

	/**
	 * Finish install certificate
	 * @param resultCode
	 */
	public void finishInstallCertificate(int resultCode) {
		((TabletBaseInputFragment)fragmentContent).finishInstallCertificate(resultCode);
	}

}
