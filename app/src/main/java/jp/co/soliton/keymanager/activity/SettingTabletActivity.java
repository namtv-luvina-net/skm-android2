package jp.co.soliton.keymanager.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import jp.co.soliton.keymanager.R;
import jp.co.soliton.keymanager.fragment.*;

import static jp.co.soliton.keymanager.common.TypeScrollFragment.*;

/**
 * Created by luongdolong on 3/31/2017.
 */

public class SettingTabletActivity extends FragmentActivity {
	public static final int STATUS_MENU = 1;
	public static final int STATUS_LIST_CERTS = 2;
	public static final int STATUS_DETAIL_CERT = 3;
	public static final int STATUS_NOTIFICATION_ALL = 4;
	public static final int STATUS_PRODUCT = 5;
	public static final int STATUS_LIBRARY = 6;
	public static final int STATUS_NOTIFICATION_ONE = 7;
	public static final float RATIO_SCALE_WIDTH = 0.6f;
	private int currentStatus;
	private String currentIdDetail = "";
	Fragment fragmentContent;
	private FragmentManager fragmentManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
	    fragmentManager = getSupportFragmentManager();
	    setContentView(R.layout.activity_setting_tablet);
	    gotoMenuSetting(NOT_SCROLL);
    }

    public void gotoMenuSetting(int typeScroll) {
	    currentStatus = STATUS_MENU;
	    fragmentContent = ContentSettingFragment.newInstance();
		changeFragmentContent(typeScroll);
    }

    public void gotoListCertificatesSetting(int typeScroll) {
	    currentStatus = STATUS_LIST_CERTS;
	    fragmentContent = ContentListCertificateSettingFragment.newInstance();
		changeFragmentContent(typeScroll);
    }

    public void gotoDetailCertificatesSetting(String id, int typeScroll) {
	    if (!id.equals(currentIdDetail)) {
		    currentIdDetail = id;
	    }
	    currentStatus = STATUS_DETAIL_CERT;
	    fragmentContent = ContentDetailCertSettingFragment.newInstance(id);
		changeFragmentContent(typeScroll);
    }

    public void gotoNotificationAllSetting(int typeScroll) {
	    currentStatus = STATUS_NOTIFICATION_ALL;
	    fragmentContent = ContentNotificationSettingFragment.newInstance();
		changeFragmentContent(typeScroll);
    }

    public void gotoNotificationOneSetting(int typeScroll) {
	    currentStatus = STATUS_NOTIFICATION_ONE;
	    fragmentContent = ContentNotificationSettingFragment.newInstance(currentIdDetail);
		changeFragmentContent(typeScroll);
    }

    public void gotoProductInfo(int typeScroll) {
	    currentStatus = STATUS_PRODUCT;
	    fragmentContent = ContentProductInfoSettingFragment.newInstance();
		changeFragmentContent(typeScroll);
    }
    public void gotoLibrary(int typeScroll) {
	    currentStatus = STATUS_LIBRARY;
	    fragmentContent = ContentLibrarySettingFragment.newInstance();
		changeFragmentContent(typeScroll);
    }

	@Override
	public void onBackPressed() {
		if (currentStatus != STATUS_NOTIFICATION_ONE && currentIdDetail.length() > 0) {
			currentIdDetail = "";
		}
		if (currentStatus == STATUS_NOTIFICATION_ALL || currentStatus == STATUS_PRODUCT || currentStatus ==
				STATUS_LIBRARY || currentStatus == STATUS_LIST_CERTS) {
			gotoMenuSetting(SCROLL_TO_RIGHT);
		} else if (currentStatus == STATUS_DETAIL_CERT) {
			gotoListCertificatesSetting(SCROLL_TO_RIGHT);
		} else if (currentStatus == STATUS_NOTIFICATION_ONE) {
			gotoDetailCertificatesSetting(currentIdDetail, SCROLL_TO_RIGHT);
		} else {
			super.onBackPressed();
		}
	}

	public int getCurrentStatus() {
		return currentStatus;
	}

	private void changeFragmentContent(int typeScroll) {
		switch (typeScroll) {
			case SCROLL_TO_RIGHT:
				changeFragmentContentTabletGoToRight();
				break;
			case SCROLL_TO_LEFT:
				changeFragmentContentTabletGoToLeft();
				break;
			case NOT_SCROLL:
				changeFragmentContentTabletNotScroll();
				break;
			default:
				changeFragmentContentTabletNotScroll();
				break;
		}
	}

	private void changeFragmentContentTabletGoToLeft(){
		FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
		fragmentTransaction.setCustomAnimations(R.anim.enter, R.anim.exit, R.anim.pop_enter, R.anim.pop_exit);
		fragmentTransaction.replace(R.id.fragment_content_tablet, fragmentContent);
		fragmentTransaction.commit();
	}

	private void changeFragmentContentTabletNotScroll(){
		FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
		fragmentTransaction.replace(R.id.fragment_content_tablet, fragmentContent);
		fragmentTransaction.commit();
	}

	private void changeFragmentContentTabletGoToRight(){
		FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
		fragmentTransaction.setCustomAnimations(R.anim.pop_enter, R.anim.pop_exit, R.anim.enter, R.anim.exit);
		fragmentTransaction.replace(R.id.fragment_content_tablet, fragmentContent);
		fragmentTransaction.commit();
	}
}
