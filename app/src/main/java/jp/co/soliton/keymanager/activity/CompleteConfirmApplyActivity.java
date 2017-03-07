package jp.co.soliton.keymanager.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import jp.co.soliton.keymanager.InformCtrl;
import jp.co.soliton.keymanager.InputApplyInfo;
import jp.co.soliton.keymanager.R;
import jp.co.soliton.keymanager.StringList;
import jp.co.soliton.keymanager.customview.DialogApplyMessage;
import jp.co.soliton.keymanager.dbalias.ElementApply;

/**
 * Created by luongdolong on 2/7/2017.
 *
 * Activity for complete apply screen
 */

public class CompleteConfirmApplyActivity extends Activity {

    private int status;
    private InformCtrl m_InformCtrl;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_complete_confirm_apply);
        Intent intent = getIntent();
        status = intent.getIntExtra("STATUS_APPLY", -1);
        m_InformCtrl = (InformCtrl)intent.getSerializableExtra(StringList.m_str_InformCtrl);
        if (status == ElementApply.STATUS_APPLY_APPROVED) {
            //
        } else if (status == ElementApply.STATUS_APPLY_PENDING) {
            LinearLayout layout = (LinearLayout) findViewById(R.id.layoutComplete);
            layout.setVisibility(View.GONE);
            showMessage(getString(R.string.message_pending), getString(R.string.apply_confirmation), new DialogApplyMessage.OnOkDismissMessageListener() {
                @Override
                public void onOkDismissMessage() {
                    CompleteConfirmApplyActivity.this.finish();
                }
            });
        } else if (status == ElementApply.STATUS_APPLY_REJECT) {
            LinearLayout layout = (LinearLayout) findViewById(R.id.layoutComplete);
            layout.setVisibility(View.GONE);
            showMessage(getString(R.string.message_reject), getString(R.string.apply_confirmation), new DialogApplyMessage.OnOkDismissMessageListener() {
                @Override
                public void onOkDismissMessage() {
                    CompleteConfirmApplyActivity.this.finish();
                }
            });
        } else if (status == ElementApply.STATUS_APPLY_CANCEL) {
            LinearLayout layout = (LinearLayout) findViewById(R.id.layoutComplete);
            layout.setVisibility(View.GONE);
            showMessage(getString(R.string.message_cancel), getString(R.string.apply_confirmation), new DialogApplyMessage.OnOkDismissMessageListener() {
                @Override
                public void onOkDismissMessage() {
                    CompleteConfirmApplyActivity.this.finish();
                }
            });
        } else {
            CompleteConfirmApplyActivity.this.finish();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    public void clickStart(View v) {
        Intent intent = new Intent(getApplicationContext(), StartUsingProceduresActivity.class);
        intent.putExtra(StringList.m_str_InformCtrl, m_InformCtrl);
        startActivity(intent);
    }

    /**
     * Show message
     *
     * @param message
     */
    protected void showMessage(String message, String titleDialog, DialogApplyMessage.OnOkDismissMessageListener listener) {
        DialogApplyMessage dlgMessage = new DialogApplyMessage(this, message);
        dlgMessage.setOnOkDismissMessageListener(listener);
        dlgMessage.setTitleDialog(titleDialog);
        dlgMessage.show();
    }
}
