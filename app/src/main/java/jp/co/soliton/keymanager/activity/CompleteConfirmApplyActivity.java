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
    private ElementApply element;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_complete_confirm_apply);
        Intent it = getIntent();
        status = it.getIntExtra("STATUS_APPLY", -1);
        m_InformCtrl = (InformCtrl)it.getSerializableExtra(StringList.m_str_InformCtrl);
        element = (ElementApply)it.getSerializableExtra("ELEMENT_APPLY");
        if (status == ElementApply.STATUS_APPLY_APPROVED) {
            //
        } else if (status == ElementApply.STATUS_APPLY_PENDING) {
            LinearLayout layout = (LinearLayout) findViewById(R.id.layoutComplete);
            layout.setVisibility(View.GONE);
            showMessage(getString(R.string.message_pending), getString(R.string.approval_confirmation), new DialogApplyMessage.OnOkDismissMessageListener() {
                @Override
                public void onOkDismissMessage() {
                    Intent intent = new Intent(getApplicationContext(), MenuAcivity.class);
                    StringList.GO_TO_LIST_APPLY = "1";
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                }
            });
        } else if (status == ElementApply.STATUS_APPLY_REJECT) {
            LinearLayout layout = (LinearLayout) findViewById(R.id.layoutComplete);
            layout.setVisibility(View.GONE);
            showMessage(getString(R.string.message_reject), getString(R.string.approval_confirmation), new DialogApplyMessage.OnOkDismissMessageListener() {
                @Override
                public void onOkDismissMessage() {
                    finish();
                }
            });
        } else if (status == ElementApply.STATUS_APPLY_CANCEL) {
            LinearLayout layout = (LinearLayout) findViewById(R.id.layoutComplete);
            layout.setVisibility(View.GONE);
            showMessage(getString(R.string.message_cancel), getString(R.string.title_cancel), new DialogApplyMessage.OnOkDismissMessageListener() {
                @Override
                public void onOkDismissMessage() {
                    finish();
                }
            });
        } else {
            Intent intent = new Intent(getApplicationContext(), MenuAcivity.class);
            StringList.GO_TO_LIST_APPLY = "1";
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    public void clickStart(View v) {
        Intent intent = new Intent(getApplicationContext(), StartUsingProceduresActivity.class);
        intent.putExtra(StringList.m_str_InformCtrl, m_InformCtrl);
        intent.putExtra("ELEMENT_APPLY", element);
        CompleteConfirmApplyActivity.this.finish();
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
