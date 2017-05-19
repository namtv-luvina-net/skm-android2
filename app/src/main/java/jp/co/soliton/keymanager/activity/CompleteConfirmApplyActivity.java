package jp.co.soliton.keymanager.activity;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.FrameLayout;
import jp.co.soliton.keymanager.InformCtrl;
import jp.co.soliton.keymanager.R;
import jp.co.soliton.keymanager.StringList;
import jp.co.soliton.keymanager.customview.DialogApplyMessage;
import jp.co.soliton.keymanager.customview.DialogMessageTablet;
import jp.co.soliton.keymanager.dbalias.ElementApply;
import jp.co.soliton.keymanager.fragment.ContentCompleteConfirmApplyFragment;

/**
 * Created by luongdolong on 2/7/2017.
 *
 * Activity for complete apply screen
 */

public class CompleteConfirmApplyActivity extends FragmentActivity {

    private int status;
    private InformCtrl m_InformCtrl;
    private ElementApply element;
	private boolean isTablet;
	Fragment fragmentLeft, fragmentContent;
	FragmentManager fragmentManager;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
	    fragmentManager = getSupportFragmentManager();
        setContentView(R.layout.activity_complete_confirm_apply);
	    setOrientation();
	    Intent it = getIntent();
        status = it.getIntExtra("STATUS_APPLY", -1);
        m_InformCtrl = (InformCtrl)it.getSerializableExtra(StringList.m_str_InformCtrl);
        element = (ElementApply)it.getSerializableExtra("ELEMENT_APPLY");
    }

	public int getStatus() {
		return status;
	}

	private void setOrientation() {
		isTablet = getResources().getBoolean(R.bool.isTablet);
		if (!isTablet) {
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		} else {
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
			FrameLayout frameLayoutLeft = (FrameLayout) findViewById(R.id.fragment_left_side_menu_tablet);
			if(android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.JELLY_BEAN) {
				frameLayoutLeft.setBackgroundDrawable( getResources().getDrawable(R.drawable.left_panel_background) );
			} else {
				frameLayoutLeft.setBackground( getResources().getDrawable(R.drawable.left_panel_background));
			}

			FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
			fragmentTransaction.setCustomAnimations(R.anim.enter, R.anim.exit, R.anim.pop_enter, R.anim.pop_exit);
			fragmentContent= new ContentCompleteConfirmApplyFragment();
			fragmentTransaction.replace(R.id.fragment_content_menu_tablet, fragmentContent);
			fragmentTransaction.commit();
		}
	}

	@Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(getApplicationContext(), MenuAcivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
	    overridePendingTransition(0, 0);
    }

    public void clickStart(View v) {
        Intent intent = new Intent(getApplicationContext(), StartUsingProceduresActivity.class);
        intent.putExtra(StringList.m_str_InformCtrl, m_InformCtrl);
        intent.putExtra("ELEMENT_APPLY", element);
        CompleteConfirmApplyActivity.this.finish();
        startActivity(intent);
	    overridePendingTransition(0, 0);
    }


	public void gotoMenu() {
		if (isTablet) {
			removeFragmentContentTablet();
			final Handler handler = new Handler();
			handler.postDelayed(new Runnable() {
				@Override
				public void run() {
					Intent intent = new Intent(CompleteConfirmApplyActivity.this, MenuAcivity.class);
					StringList.GO_TO_LIST_APPLY = "1";
					intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					startActivity(intent);
					overridePendingTransition(0, 0);
				}
			}, getResources().getInteger(android.R.integer.config_shortAnimTime));
		} else {
			Intent intent = new Intent(CompleteConfirmApplyActivity.this, MenuAcivity.class);
			StringList.GO_TO_LIST_APPLY = "1";
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intent);
		}
	}

	private void removeFragmentContentTablet() {
		FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
		fragmentTransaction.setCustomAnimations(R.anim.pop_enter, R.anim.pop_exit, R.anim.enter, R.anim.exit);
		fragmentTransaction.remove(fragmentContent);
		fragmentTransaction.commit();
	}


	public void showMessageWithdrawn() {
		if (!isTablet) {
			showMessagePhone(getString(R.string.message_cancel), getString(R.string.title_cancel), new DialogApplyMessage
					.OnOkDismissMessageListener() {
				@Override
				public void onOkDismissMessage() {
					finish();
				}
			});
		} else {
			showMessageTablet(getString(R.string.message_cancel), getString(R.string.title_cancel), new DialogMessageTablet
					.OnOkDismissMessageListener() {
				@Override
				public void onOkDismissMessage() {
					finishActivityTablet();
				}
			});
		}
	}

	public void showMessageRejected() {
		if (!isTablet) {
			showMessagePhone(getString(R.string.message_reject), getString(R.string.approval_confirmation), new DialogApplyMessage.OnOkDismissMessageListener() {
				@Override
				public void onOkDismissMessage() {
					finish();
				}
			});
		} else {
			showMessageTablet(getString(R.string.message_reject), getString(R.string.approval_confirmation), new
					DialogMessageTablet.OnOkDismissMessageListener() {
				@Override
				public void onOkDismissMessage() {
					finishActivityTablet();
				}
			});
		}
	}

	private void finishActivityTablet() {
		removeFragmentContentTablet();
		final Handler handler = new Handler();
		handler.postDelayed(new Runnable() {
			@Override
			public void run() {
				finish();
				overridePendingTransition(0, 0);
			}
		}, getResources().getInteger(android.R.integer.config_shortAnimTime));
	}

	public void showMessagePending() {
		if (!isTablet) {
			showMessagePhone(getString(R.string.message_pending), getString(R.string
					.approval_confirmation), new DialogApplyMessage.OnOkDismissMessageListener() {
				@Override
				public void onOkDismissMessage() {
					Intent intent = new Intent(CompleteConfirmApplyActivity.this, MenuAcivity.class);
					StringList.GO_TO_LIST_APPLY = "1";
					intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					startActivity(intent);
				}
			});
		} else {
			showMessageTablet(getString(R.string.message_pending), getString(R.string
					.approval_confirmation), new DialogMessageTablet.OnOkDismissMessageListener() {
				@Override
				public void onOkDismissMessage() {
							Intent intent = new Intent(CompleteConfirmApplyActivity.this, MenuAcivity.class);
							StringList.GO_TO_LIST_APPLY = "1";
							intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
							startActivity(intent);
							overridePendingTransition(0, 0);
				}
			});
		}
	}

	private void showMessagePhone(String message, String titleDialog, DialogApplyMessage.OnOkDismissMessageListener
			listener) {
		DialogApplyMessage dlgMessage = new DialogApplyMessage(CompleteConfirmApplyActivity.this, message);
		dlgMessage.setOnOkDismissMessageListener(listener);
		dlgMessage.setTitleDialog(titleDialog);
		dlgMessage.show();
	}

	private void showMessageTablet(String message, String titleDialog, DialogMessageTablet.OnOkDismissMessageListener
			listener) {
		DialogMessageTablet dlgMessage = new DialogMessageTablet(CompleteConfirmApplyActivity.this, message);
		dlgMessage.setOnOkDismissMessageListener(listener);
		dlgMessage.setTitleDialog(titleDialog);
		dlgMessage.show();
	}
}
