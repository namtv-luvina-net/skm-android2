package jp.co.soliton.keymanager.activity;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import jp.co.soliton.keymanager.InformCtrl;
import jp.co.soliton.keymanager.R;
import jp.co.soliton.keymanager.StringList;
import jp.co.soliton.keymanager.customview.DialogApplyMessage;
import jp.co.soliton.keymanager.dbalias.ElementApply;
import jp.co.soliton.keymanager.fragment.ContentCompleteConfirmApplyFragment;
import jp.co.soliton.keymanager.fragment.LeftSideAPIDTabletFragment;

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
	    setOrientation();
        setContentView(R.layout.activity_complete_confirm_apply);
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
			FragmentTransaction fragmentTransaction1 = fragmentManager.beginTransaction();
			fragmentLeft = new LeftSideAPIDTabletFragment();
			fragmentTransaction1.replace(R.id.fragment_left_side_menu_tablet, fragmentLeft);
			fragmentTransaction1.commit();

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

    /**
     * Show message
     *
     * @param message
     */
    public void showMessage(String message, String titleDialog, DialogApplyMessage.OnOkDismissMessageListener listener) {
        DialogApplyMessage dlgMessage = new DialogApplyMessage(this, message);
        dlgMessage.setOnOkDismissMessageListener(listener);
        dlgMessage.setTitleDialog(titleDialog);
        dlgMessage.show();
    }
}
