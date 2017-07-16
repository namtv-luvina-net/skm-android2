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
import jp.co.soliton.keymanager.LogCtrl;
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
	private View layoutComplete;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_complete_confirm_apply);
	    layoutComplete = findViewById(R.id.layoutComplete);
	    Intent it = getIntent();
        status = it.getIntExtra("STATUS_APPLY", -1);
        m_InformCtrl = (InformCtrl)it.getSerializableExtra(StringList.m_str_InformCtrl);
        element = (ElementApply)it.getSerializableExtra("ELEMENT_APPLY");
    }

	public int getStatus() {
		return status;
	}

	@Override
    public void onResume() {
        super.onResume();
		if (status == ElementApply.STATUS_APPLY_APPROVED) {
			//
		} else if (status == ElementApply.STATUS_APPLY_PENDING) {
			LogCtrl.getInstance().info("Apply: Application is still pending");
			layoutComplete.setVisibility(View.GONE);
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
		} else if (status == ElementApply.STATUS_APPLY_REJECT) {
			LogCtrl.getInstance().info("Apply: Application has rejected");
			layoutComplete.setVisibility(View.GONE);
			showMessagePhone(getString(R.string.message_reject), getString(R.string.approval_confirmation), new DialogApplyMessage.OnOkDismissMessageListener() {
				@Override
				public void onOkDismissMessage() {
					finish();
				}
			});
		} else if (status == ElementApply.STATUS_APPLY_CANCEL) {
			LogCtrl.getInstance().info("Apply: Application has withdrawn");
			layoutComplete.setVisibility(View.GONE);
			showMessagePhone(getString(R.string.message_cancel), getString(R.string.title_cancel), new DialogApplyMessage
					.OnOkDismissMessageListener() {
				@Override
				public void onOkDismissMessage() {
					finish();
				}
			});
		} else {
			Intent intent = new Intent(CompleteConfirmApplyActivity.this, MenuAcivity.class);
			StringList.GO_TO_LIST_APPLY = "1";
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intent);
		}
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(getApplicationContext(), MenuAcivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    public void clickStart(View v) {
        Intent intent = new Intent(getApplicationContext(), StartUsingProceduresActivity.class);
        intent.putExtra(StringList.m_str_InformCtrl, m_InformCtrl);
        intent.putExtra("ELEMENT_APPLY", element);
        CompleteConfirmApplyActivity.this.finish();
        startActivity(intent);
    }

	private void showMessagePhone(String message, String titleDialog, DialogApplyMessage.OnOkDismissMessageListener
			listener) {
		DialogApplyMessage dlgMessage = new DialogApplyMessage(CompleteConfirmApplyActivity.this, message);
		dlgMessage.setOnOkDismissMessageListener(listener);
		dlgMessage.setTitleDialog(titleDialog);
		dlgMessage.show();
	}
}
