package jp.co.soliton.keymanager.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.*;
import android.support.v4.content.ContextCompat;
import android.view.View;
import jp.co.soliton.keymanager.*;
import jp.co.soliton.keymanager.alarm.AlarmReceiver;
import jp.co.soliton.keymanager.asynctask.StartUsingProceduresControl;
import jp.co.soliton.keymanager.customview.DialogConfirmTablet;
import jp.co.soliton.keymanager.dbalias.ElementApply;
import jp.co.soliton.keymanager.dbalias.ElementApplyManager;
import jp.co.soliton.keymanager.fragment.*;

import java.util.ArrayList;
import java.util.List;

import static jp.co.soliton.keymanager.common.ControlPagesInput.REQUEST_CODE_INSTALL_CERTIFICATION_CONTROL_PAGES_INPUT;
import static jp.co.soliton.keymanager.common.StatusFragmentTablet.*;
import static jp.co.soliton.keymanager.common.TypeScrollFragment.*;

/**
 * Created by luongdolong on 2/3/2017.
 *
 * Activity for menu apply screen
 */

public class MenuAcivity extends FragmentActivity {

    private int PERMISSIONS_REQUEST_READ_PHONE_STATE = 10;
	private boolean isTablet;
	private boolean isFocusMenuTablet;
	private ElementApplyManager elementMgr;

	public int currentStatus;
	private FragmentManager fragmentManager;
	private Fragment fragmentLeft, fragmentContent;
	private List<ElementApply> listElementApply = new ArrayList<>();
	private List<ElementApply> listCertificate = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
	    isTablet = getResources().getBoolean(R.bool.isTablet);
	    setContentView(R.layout.activity_menu);
	    elementMgr = new ElementApplyManager(this);
	    fragmentManager = getSupportFragmentManager();
	    String id_update = getIdUpdate();
	    checkGoToConfirmIfNeed();
	    if (!ValidateParams.nullOrEmpty(id_update)) {
			startNotifUpdateFragment(id_update, NOT_SCROLL);
	    }else {
		    if (isTablet) {
			    if (savedInstanceState == null) {
				    gotoMenuTablet();
			    } else {
				    fragmentContent = getSupportFragmentManager().getFragment(savedInstanceState, "fragmentContent");
				    if (savedInstanceState.containsKey("fragmentLeft")) {
					    fragmentLeft = getSupportFragmentManager().getFragment(savedInstanceState, "fragmentLeft");
				    }
				    currentStatus = savedInstanceState.getInt("currentStatus");
			    }
		    }
	    }
    }

    private String getIdUpdate() {
	    try{
		    if (isTablet) {
			    return getIntent().getStringExtra(StringList.ELEMENT_APPLY_ID);
		    }
	    }catch (Exception e) {
		    e.printStackTrace();
	    }
	    return "";
    }

	public ElementApplyManager getElementMgr() {
		return elementMgr;
	}

	@Override
	public void onSaveInstanceState(Bundle savedInstanceState) {
		super.onSaveInstanceState(savedInstanceState);
		if (isTablet) {
			savedInstanceState.putInt("currentStatus", currentStatus);
			getSupportFragmentManager().putFragment(savedInstanceState, "fragmentContent", fragmentContent);
			if (fragmentLeft != null) {
				getSupportFragmentManager().putFragment(savedInstanceState, "fragmentLeft", fragmentLeft);
			}
		}
	}

	@Override
	public void onBackPressed() {
		if (!isFocusMenuTablet && isTablet) {
			if (currentStatus == COMPLETE_STATUS) {
				InputApplyInfo.deletePref(this);
				gotoMenuTablet();
			}else if (currentStatus == START_APPLY_STATUS) {
				((TabletBaseInputFragment)fragmentContent).clickBackButton();
			}else if (currentStatus == START_UPDATE_STATUS) {
				((TabletBaseUpdateFragment)fragmentContent).clickBackButton();
			}else if (currentStatus == REAPPLY_STATUS) {
				((TabletBaseInputFragment)fragmentContent).clickBackButton();
			}else if (currentStatus == DETAIL_CONFIRM_APPLY_STATUS) {
				onBackPressedFromDetailCetificate();
			}else if (currentStatus == CONFIRM_APPLY_STATUS || currentStatus == WITHDRAW_APPLY_STATUS) {
				startDetailConfirmApplyFragment(SCROLL_TO_RIGHT);
			}else if (currentStatus == START_USING_PROCEDURES_STATUS) {
				if (getListElementApply().size() == 1) {
					StringList.ID_DETAIL_CURRENT = String.valueOf(listElementApply.get(0).getId());
					startDetailConfirmApplyFragment(SCROLL_TO_RIGHT);
				} else {
					startListConfirmApplyFragment(SCROLL_TO_RIGHT);
				}
			}else if (currentStatus == NOTIF_UPDATE_STATUS) {
				finish();
			} else {
				gotoMenuTablet();
			}
		} else {
			finish();
		}
	}

	public void onBackPressedFromDetailCetificate() {
		updateListElementApply();
		if (listElementApply.size() > 1) {
			startListConfirmApplyFragment(SCROLL_TO_RIGHT);
		} else {
			gotoMenuTablet();
		}
	}

	public void setFocusMenuTablet(boolean focusMenuTablet) {
		isFocusMenuTablet = focusMenuTablet;
	}

	@Override
    protected void onResume() {
        super.onResume();
		checkGoToConfirmIfNeed();
		updateListElementApply();
		if(android.os.Build.VERSION.SDK_INT >= 23) {
            NewPermissionSet();
        }
    }

    private void updateListElementApply() {
	    listElementApply = elementMgr.getAllElementApply();
	    ElementApply.sortListConfirmApply(listElementApply);
    }

    private void updateListCertificate() {
	    listCertificate = elementMgr.getAllCertificate();
	    ElementApply.sortListApplyUpdate(listCertificate);
    }

	public List<ElementApply> getListElementApply() {
		updateListElementApply();
		return listElementApply;
	}

	public List<ElementApply> getListCertificate() {
		updateListCertificate();
		return listCertificate;
	}

	private void checkGoToConfirmIfNeed() {
		if (StringList.GO_TO_LIST_APPLY.equals("1")) {
		    StringList.GO_TO_LIST_APPLY = "0";
		    gotoConfirmActivity();
		}
	}

	public void gotoConfirmActivity() {
		updateListElementApply();
	    if (listElementApply.size() == 1) {
		    if (!isTablet) {
			    Intent intent = new Intent(MenuAcivity.this, DetailConfirmActivity.class);
			    intent.putExtra("ELEMENT_APPLY_ID", String.valueOf(listElementApply.get(0).getId()));
			    startActivity(intent);
		    } else {
			    startDetailConfirmApplyFragment(SCROLL_TO_LEFT);
		    }
	    } else {
		    if (!isTablet) {
			    Intent intent = new Intent(MenuAcivity.this, ListConfirmActivity.class);
			    startActivity(intent);
		    } else {
			    startListConfirmApplyFragment(SCROLL_TO_LEFT);
		    }
	    }
    }

    private void NewPermissionSet() {
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_PHONE_STATE},
                    PERMISSIONS_REQUEST_READ_PHONE_STATE);
        }
    }

	public void gotoMenuTablet() {
		StringList.ID_DETAIL_CURRENT = "";
		isFocusMenuTablet = true;
		currentStatus = RESET_STATUS;
		fragmentContent = ContentMenuTabletFragment.newInstance();
		changeFragmentContent(SCROLL_TO_RIGHT);

		fragmentLeft = new LeftSideMenuTabletFragment();
		changeFragmentLeftTablet();
	}

	public void startNotifUpdateFragment(String id, int typeScroll) {
		isFocusMenuTablet = false;
		currentStatus = NOTIF_UPDATE_STATUS;
		hideFragmentLeft();
		fragmentContent= ContentUpdateFromNotificationFragment.newInstance(id);
		changeFragmentContent(typeScroll);
	}

	public void startActivityAPID() {
		isFocusMenuTablet = false;
		currentStatus = APID_STATUS;
		fragmentLeft = new LeftSideAPIDTabletFragment();
		changeFragmentLeftTablet();

		fragmentContent= new ContentAPIDTabletFragment();
		changeFragmentContent(SCROLL_TO_LEFT);
	}

	public void startApplyActivityFragment(int startFrom) {
		InputApplyInfo.deletePref(this);
		isFocusMenuTablet = false;
		currentStatus = START_APPLY_STATUS;
		fragmentLeft = LeftSideInputTabletFragment.newInstance(startFrom);
		changeFragmentLeftTablet();
		fragmentContent= TabletBaseInputFragment.newInstanceStartApply(startFrom);
		changeFragmentContent(SCROLL_TO_LEFT);
	}

	public void startUpdateFragmentFromListCertificate(String idConfirmApply){
		fragmentContent= TabletBaseUpdateFragment.newInstance(idConfirmApply, false);
		startUpdateFragment();
	}

	public void startUpdateFragmentFromNotification(String idConfirmApply){
		fragmentContent= TabletBaseUpdateFragment.newInstance(idConfirmApply, true);
		startUpdateFragment();
	}

	private void startUpdateFragment() {
		InputApplyInfo.deletePref(this);
		isFocusMenuTablet = false;
		currentStatus = START_UPDATE_STATUS;
		if (fragmentLeft == null || !(fragmentLeft instanceof LeftSideListCertAndReapplyTabletFragment) || !fragmentLeft.isVisible()) {
			fragmentLeft = new LeftSideListCertAndReapplyTabletFragment();
			changeFragmentLeftTablet();
		}
		changeFragmentContent(SCROLL_TO_LEFT);
	}

	public void updateLeftSideListCertAndReapply(int possition) {
		if (fragmentLeft == null || !(fragmentLeft instanceof LeftSideListCertAndReapplyTabletFragment)) {
			return;
		}
		((LeftSideListCertAndReapplyTabletFragment)fragmentLeft).highlightItem(possition);
	}

	public void updateLeftSideInput(int possition, int startFrom) {
		if (fragmentLeft == null || !(fragmentLeft instanceof LeftSideInputTabletFragment)) {
			return;
		}
		((LeftSideInputTabletFragment)fragmentLeft).highlightItem(possition, startFrom);
	}

	public void goApplyCompleted(){
		fragmentContent = TabletInputSuccessFragment.newInstance();
		gotoApplyCompleteFragment();
	}

	public void goApplyCompleted(InformCtrl m_InformCtrl, ElementApply element){
		fragmentContent = TabletInputSuccessFragment.newInstance(m_InformCtrl, element);
		gotoApplyCompleteFragment();
	}

	private void gotoApplyCompleteFragment(){
		currentStatus = APPLY_SUCCESS_STATUS;
		hideFragmentLeft();
		changeFragmentContent(SCROLL_TO_LEFT);
	}

	public void startListApplyUpdateFragment(int typeScroll) {
		isFocusMenuTablet = false;
		currentStatus = LIST_APPLY_UPDATE_STATUS;
		if (fragmentLeft == null || !(fragmentLeft instanceof LeftSideListCertAndReapplyTabletFragment)) {
			fragmentLeft = new LeftSideListCertAndReapplyTabletFragment();
			changeFragmentLeftTablet();
		}
		fragmentContent= ContentListCertificateTabletFragment.newInstance(listCertificate);
		changeFragmentContent(typeScroll);
	}

	public void startListConfirmApplyFragment(int typeScroll) {
		isFocusMenuTablet = false;
		currentStatus = LIST_CONFIRM_APPLY_STATUS;
		StringList.ID_DETAIL_CURRENT = "";
		fragmentLeft = new LeftSideListConfirmTabletFragment();
		changeFragmentLeftTablet();

		fragmentContent= ContentListConfirmTabletFragment.newInstance(listElementApply);
		changeFragmentContent(typeScroll);
	}

	public void startDetailConfirmApplyFragment(int typeScroll) {
		isFocusMenuTablet = false;
		currentStatus = DETAIL_CONFIRM_APPLY_STATUS;
		fragmentLeft = new LeftSideDetailConfirmTabletFragment();
		changeFragmentLeftTablet();

		fragmentContent = ContentDetailConfirmFragment.newInstance();
		changeFragmentContent(typeScroll);
	}

	public void updateDesLeftSideDetailConfirm(String newDes) {
		if (fragmentLeft == null || currentStatus != DETAIL_CONFIRM_APPLY_STATUS) {
			return;
		}
		((LeftSideDetailConfirmTabletFragment)fragmentLeft).setTextDes(newDes);
	}

	public void clickConfirmApply(String[] listData) {
		currentStatus = CONFIRM_APPLY_STATUS;
		fragmentLeft = LeftSideInputPasswordTabletFragment.newInstance(listData);
		changeFragmentLeftTablet();
		fragmentContent= ContentInputPasswordTabletFragment.newInstance(false);
		changeFragmentContent(SCROLL_TO_LEFT);
	}

	public void clickWithdrawApply(String[] listData) {
		currentStatus = WITHDRAW_APPLY_STATUS;
		fragmentLeft = LeftSideInputPasswordTabletFragment.newInstance(listData);
		changeFragmentLeftTablet();

		fragmentContent= ContentInputPasswordTabletFragment.newInstance(true);
		changeFragmentContent(SCROLL_TO_LEFT);
	}

	public void clickReApply() {
		InputApplyInfo.deletePref(this);
		isFocusMenuTablet = false;
		currentStatus = REAPPLY_STATUS;
		fragmentLeft = LeftSideInputTabletFragment.newInstance(TabletBaseInputFragment.START_FROM_MENU);
		changeFragmentLeftTablet();
		fragmentContent= TabletBaseInputFragment.newInstanceReApply(StringList.ID_DETAIL_CURRENT);
		changeFragmentContent(SCROLL_TO_LEFT);
	}

	public void clickDeleteApplyTablet() {
	    final DialogConfirmTablet dialog = new DialogConfirmTablet(this);
	    dialog.setTextDisplay(getString(R.string.dialog_delete_title), getString(R.string.dialog_delete_msg)
			    , getString(R.string.label_dialog_Cancle), getString(R.string.label_dialog_delete_cert));
	    dialog.setOnClickOK(new View.OnClickListener() {
		    @Override
		    public void onClick(View v) {
			    dialog.dismiss();
			    elementMgr.deleteElementApply(StringList.ID_DETAIL_CURRENT);
			    gotoMenuTablet();
		    }
	    });
	    dialog.show();
	}

	public void gotoCompleteConfirmApplyFragment(int status, ElementApply element, InformCtrl m_InformCtrl) {
		hideFragmentLeft();
		currentStatus  = COMPLETE_CONFIRM_STATUS;
		fragmentContent= ContentCompleteConfirmApplyFragment.newInstance(status, element, m_InformCtrl);
		if (status == ElementApply.STATUS_APPLY_PENDING || status == ElementApply.STATUS_APPLY_REJECT  || status
				== ElementApply.STATUS_APPLY_CANCEL ) {
			changeFragmentContent(NOT_SCROLL);
		} else if (status == ElementApply.STATUS_APPLY_APPROVED){
			changeFragmentContent(SCROLL_TO_LEFT);
		} else {
			changeFragmentContent(SCROLL_TO_RIGHT);
		}
	}

	public void startUsingProceduresFragment(InformCtrl m_InformCtrl, ElementApply element) {
		currentStatus = START_USING_PROCEDURES_STATUS;
		StringList.ID_DETAIL_CURRENT = String.valueOf(element.getId());
		hideFragmentLeft();
		fragmentContent = ContentStartUsingProceduresFragment.newInstance();
		changeFragmentContent(SCROLL_TO_LEFT);
		StartUsingProceduresControl startUsingProceduresControl = StartUsingProceduresControl.newInstance(this,
				m_InformCtrl, element);
		startUsingProceduresControl.startDeviceCertTask();
	}

	private void completeUsingProceduresFragment(ElementApply elementApply) {
		currentStatus = COMPLETE_STATUS;
		hideFragmentLeft();
		fragmentContent = ContentCompleteUsingProceduresFragment.newInstance(elementApply);
		changeFragmentContent(SCROLL_TO_LEFT);
	}

	private void hideFragmentLeft() {
		if (fragmentLeft == null) {
			return;
		}
		FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
		fragmentTransaction.hide(fragmentLeft);
		fragmentTransaction.commit();
	}

	private void changeFragmentLeftTablet(){
		FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
		fragmentTransaction.replace(R.id.fragment_leftside_tablet, fragmentLeft);
		fragmentTransaction.commit();
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

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == REQUEST_CODE_INSTALL_CERTIFICATION_CONTROL_PAGES_INPUT) {
			((TabletBaseInputFragment)fragmentContent).finishInstallCertificate(resultCode);
		}

		if (requestCode == StartUsingProceduresControl.m_nEnrollRtnCode) {
			// After CertificateEnrollTask
			StartUsingProceduresControl.getInstance(this).afterIntallCert();
			if (resultCode != 0) {
				ElementApplyManager mgr = new ElementApplyManager(getApplicationContext());
				mgr.updateElementCertificate(StartUsingProceduresControl.getInstance(this).getElement());
				AlarmReceiver alarm = new AlarmReceiver();
				alarm.setupNotification(getApplicationContext());
				completeUsingProceduresFragment(StartUsingProceduresControl.getInstance(this).getElement());
			} else {
				if (getListElementApply().size() == 1) {
					StringList.ID_DETAIL_CURRENT = String.valueOf(listElementApply.get(0).getId());
					startDetailConfirmApplyFragment(SCROLL_TO_RIGHT);
				} else {
					startListConfirmApplyFragment(SCROLL_TO_RIGHT);
				}
			}
		} else if (requestCode == StartUsingProceduresControl.m_nMDM_RequestCode) {
			if (resultCode == RESULT_OK) {
				StartUsingProceduresControl.getInstance(this).resultWithRequestCodeMDM();
			} else {
				finish();
			}
		} else if (requestCode == ViewPagerInputActivity.REQUEST_CODE_INSTALL_CERTIFICATION_VIEWPAGER_INPUT) {
			if (resultCode == Activity.RESULT_OK) {
				StartUsingProceduresControl.getInstance(this).startCertificateEnrollTask();
			} else {
				LogCtrl.getInstance().warn("Proc: CA Certificate Installation Cancelled");
				if (getListElementApply().size() == 1) {
					StringList.ID_DETAIL_CURRENT = String.valueOf(listElementApply.get(0).getId());
					startDetailConfirmApplyFragment(SCROLL_TO_RIGHT);
				} else {
					startListConfirmApplyFragment(SCROLL_TO_RIGHT);
				}
			}
		}
	}
}
