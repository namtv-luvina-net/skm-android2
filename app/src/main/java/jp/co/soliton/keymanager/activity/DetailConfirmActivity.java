package jp.co.soliton.keymanager.activity;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.TextView;
import jp.co.soliton.keymanager.InputApplyInfo;
import jp.co.soliton.keymanager.R;
import jp.co.soliton.keymanager.customview.DialogApplyConfirm;
import jp.co.soliton.keymanager.dbalias.ElementApply;
import jp.co.soliton.keymanager.dbalias.ElementApplyManager;

/**
 * Created by lexuanvinh on 02/27/2017.
 */

public class DetailConfirmActivity extends FragmentActivity {

    private ElementApplyManager elementMgr;
    private String id;
//	private boolean isTablet;
//	Fragment fragmentLeft, fragmentContent;
//	FragmentManager fragmentManager;

	private TextView tvHostName;
	private TextView tvUserId;
	private TextView tvDate;
	private TextView tvStatus;
	private TextView title;
	private TextView tvDeleteApply;
	private TextView tvConfirmApply;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//	    isTablet = getResources().getBoolean(R.bool.isTablet);
//	    fragmentManager = getSupportFragmentManager();
	    setContentView(R.layout.activity_detail_confirm);
//	    setOrientation();
	    id = getIntent().getStringExtra("ELEMENT_APPLY_ID");
	    elementMgr = new ElementApplyManager(getApplicationContext());
	    title = (TextView) findViewById(R.id.tvTitleHeader);
		tvHostName = (TextView) findViewById(R.id.tvHostName);
		tvUserId = (TextView) findViewById(R.id.tvUserId);
		tvDate = (TextView) findViewById(R.id.tvDate);
		tvStatus = (TextView) findViewById(R.id.tvStatus);
		tvDeleteApply = (TextView) findViewById(R.id.tvDeleteApply);
		tvConfirmApply = (TextView) findViewById(R.id.tvConfirmApply);
    }


//	@Override
//	public void onSaveInstanceState(Bundle savedInstanceState) {
//		super.onSaveInstanceState(savedInstanceState);
//		if (isTablet) {
//			getSupportFragmentManager().putFragment(savedInstanceState, "fragmentContent", fragmentContent);
//			getSupportFragmentManager().putFragment(savedInstanceState, "fragmentLeft", fragmentLeft);
//		}
//	}

//	private void setOrientation() {
//		if (!isTablet) {
//			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
//		} else {
//			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
//		}
//	}

//	private void createView(){
//		FragmentTransaction fragmentTransaction1 = fragmentManager.beginTransaction();
//		fragmentLeft = new LeftSideDetailConfirmTabletFragment();
//		fragmentTransaction1.replace(R.id.fragment_left_side_menu_tablet, fragmentLeft);
//		fragmentTransaction1.commit();
//
//		FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
//		fragmentTransaction.setCustomAnimations(R.anim.enter, R.anim.exit, R.anim.pop_enter, R.anim.pop_exit);
//		fragmentContent = new ContentDetailConfirmFragment();
//		fragmentTransaction.replace(R.id.fragment_content_menu_tablet, fragmentContent);
//		fragmentTransaction.commit();
//	}

//	public void updateDesLeftSide(String newDes) {
//		((LeftSideDetailConfirmTabletFragment)fragmentLeft).setTextDes(newDes);
//	}

    public void clickConfirmApply(View v) {
	    Intent intent;
//	    if (!isTablet) {
		    intent = new Intent(DetailConfirmActivity.this, InputPasswordActivity.class);
		    intent.putExtra("ELEMENT_APPLY_ID", id);
		    startActivity(intent);
//	    } else {
//		    intent = new Intent(DetailConfirmActivity.this, InputPasswordTabletActivity.class);
//		    intent.putExtra("ELEMENT_APPLY_ID", id);
//		    startActivity(intent);
//		    overridePendingTransition(0, 0);
//	    }
    }

    public void clickReApply(View v) {
        InputApplyInfo.deletePref(DetailConfirmActivity.this);
//	    if (isTablet) {
//		    Intent intent = new Intent(DetailConfirmActivity.this, ViewPagerInputTabletActivity.class);
//		    intent.putExtra("ELEMENT_APPLY_ID", id);
//		    startActivity(intent);
//		    overridePendingTransition(0, 0);
//	    } else {
			Intent intent = new Intent(DetailConfirmActivity.this, ViewPagerInputActivity.class);
		    intent.putExtra("ELEMENT_APPLY_ID", id);
		    startActivity(intent);
//	    }
    }

    public void clickDeleteApply(View v) {
//        if (isTablet) {
//	        deleteApplyTablet(v);
//        }else {
	        deleteApplyPhone(v);
//        }
    }

//	private void deleteApplyTablet(View v) {
//	    final DialogConfirmTablet dialog = new DialogConfirmTablet(this);
//	    dialog.setTextDisplay(getString(R.string.dialog_delete_title), getString(R.string.dialog_delete_msg)
//			    , getString(R.string.label_dialog_Cancle), getString(R.string.label_dialog_delete_cert));
//	    dialog.setOnClickOK(new View.OnClickListener() {
//		    @Override
//		    public void onClick(View v) {
//			    confirmClickOKBtnDelete(dialog);
//		    }
//	    });
//	    dialog.show();
//    }

	private void deleteApplyPhone(View v) {
	    final DialogApplyConfirm dialog = new DialogApplyConfirm(this);
	    dialog.setTextDisplay(getString(R.string.dialog_delete_title), getString(R.string.dialog_delete_msg)
			    , getString(R.string.label_dialog_Cancle), getString(R.string.label_dialog_delete_cert));
	    dialog.setOnClickOK(new View.OnClickListener() {
		    @Override
		    public void onClick(View v) {
			    confirmClickOKBtnDelete(dialog);
		    }
	    });
	    dialog.show();
    }

    private void confirmClickOKBtnDelete(Dialog dialog) {
	    dialog.dismiss();
	    elementMgr.deleteElementApply(id);
	    final Activity activity = this;
//	    if (isTablet) {
//		    removeFragmentContentTabletToRight();
//		    final Handler handler = new Handler();
//		    handler.postDelayed(new Runnable() {
//			    @Override
//			    public void run() {
//				    Intent intent = new Intent(activity, MenuAcivity.class);
//				    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//				    startActivity(intent);
//				    activity.overridePendingTransition(0, 0);
//			    }
//		    }, getResources().getInteger(android.R.integer.config_mediumAnimTime));
//	    } else {
		    Intent intent = new Intent(activity, MenuAcivity.class);
		    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		    startActivity(intent);
//	    }
    }

    public void clickWithdrawApply(View v) {
//	    if (isTablet) {
//		    Intent intent = new Intent(DetailConfirmActivity.this, InputPasswordTabletActivity.class);
//		    intent.putExtra("ELEMENT_APPLY_ID", id);
//		    intent.putExtra("CANCEL_APPLY", "1");
//		    startActivity(intent);
//		    overridePendingTransition(0, 0);
//	    } else {
		    Intent intent = new Intent(DetailConfirmActivity.this, InputPasswordActivity.class);
		    intent.putExtra("ELEMENT_APPLY_ID", id);
		    intent.putExtra("CANCEL_APPLY", "1");
		    startActivity(intent);
//	    }
    }

	public String getId() {
		return id;
	}

//	@Override
//	public void onBackPressed() {
//		btnBackClick(null);
//	}

//	public void btnBackClick(View v) {
//		if (isTablet) {
//			removeFragmentContentTabletToRight();
//			final Activity activity = this;
//			final Handler handler = new Handler();
//			handler.postDelayed(new Runnable() {
//				@Override
//				public void run() {
//					activity.finish();
//					activity.overridePendingTransition(0, 0);
//				}
//			}, getResources().getInteger(android.R.integer.config_shortAnimTime));
//		} else {
//			finish();
//		}
//	}

//	private void recreateFragmentContentWithDelay(){
//		final Handler handler = new Handler();
//		handler.postDelayed(new Runnable() {
//			@Override
//			public void run() {
//				FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
//				fragmentTransaction.setCustomAnimations(R.anim.pop_enter, R.anim.pop_exit, R.anim.enter, R.anim.exit);
//				fragmentTransaction.replace(R.id.fragment_content_menu_tablet, fragmentContent);
//				fragmentTransaction.replace(R.id.fragment_left_side_menu_tablet, fragmentLeft);
//				fragmentTransaction.commit();
//			}
//		}, getResources().getInteger(android.R.integer.config_longAnimTime));
//	}
//
//	private void removeFragmentContentTabletToLeft(){
//		if (isTablet) {
//			removeFragmentLeft();
//			FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
//			fragmentTransaction.setCustomAnimations(R.anim.enter, R.anim.exit, R.anim.pop_enter, R.anim.pop_exit);
//			fragmentTransaction.remove(fragmentContent);
//			fragmentTransaction.commit();
//		}
//	}
//
//	private void removeFragmentContentTabletToRight(){
//		if (isTablet) {
//			FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
//			fragmentTransaction.setCustomAnimations(R.anim.pop_enter, R.anim.pop_exit, R.anim.enter, R.anim.exit);
//			fragmentTransaction.remove(fragmentContent);
//			fragmentTransaction.commit();
//		}
//	}
//
//	private void removeFragmentLeft(){
//		if (isTablet) {
//			FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
//			fragmentTransaction.remove(fragmentLeft);
//			fragmentTransaction.commit();
//		}
//	}

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
	    setupDisplay();
    }


	private void setupDisplay() {
		title.setText(getString(R.string.approval_confirmation));
		ElementApply detail = elementMgr.getElementApply(id);
		if (detail.getHost() != null) {
			tvHostName.setText(detail.getHost());
		}
		if (detail.getUserId() != null) {
			tvUserId.setText(detail.getUserId());
		}
		if (detail.getUpdateDate() != null) {
			String updateDate = detail.getUpdateDate().split(" ")[0];
			tvDate.setText(updateDate.replace("-", "/"));
		}
		if (detail.getStatus() == ElementApply.STATUS_APPLY_CANCEL) {
			tvStatus.setText(getText(R.string.stt_cancel));
		} else if (detail.getStatus() == ElementApply.STATUS_APPLY_PENDING) {
			tvStatus.setText(getText(R.string.stt_waiting_approval));
		} else if (detail.getStatus() == ElementApply.STATUS_APPLY_REJECT) {
			tvStatus.setText(getText(R.string.stt_rejected));
		}

		if (detail.getStatus() == ElementApply.STATUS_APPLY_PENDING) {
			tvConfirmApply.setText(getString(R.string.confirm_apply_status));
			tvConfirmApply.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
//					if (!isTablet) {
//						((DetailConfirmActivity) getActivity()).clickConfirmApply(v);
//					} else {
						clickConfirmApply(v);
//					}
				}
			});
			tvDeleteApply.setText(getString(R.string.withdrawal_apply));
			tvDeleteApply.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					clickWithdrawApply(v);
				}
			});
		} else {
			tvConfirmApply.setText(getString(R.string.re_apply));
			tvConfirmApply.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					clickReApply(v);
				}
			});

			tvDeleteApply.setText(getString(R.string.delete_apply));
			tvDeleteApply.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					clickDeleteApply(v);
				}
			});
		}
	}
}
