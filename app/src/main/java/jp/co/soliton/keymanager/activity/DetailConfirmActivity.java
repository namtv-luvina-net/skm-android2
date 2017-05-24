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
	    setContentView(R.layout.activity_detail_confirm);
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

	public String getId() {
		return id;
	}

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
					clickConfirmApply(v);
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

	public void clickConfirmApply(View v) {
		Intent intent;
		intent = new Intent(DetailConfirmActivity.this, InputPasswordActivity.class);
		intent.putExtra("ELEMENT_APPLY_ID", id);
		startActivity(intent);
	}

	public void clickReApply(View v) {
		InputApplyInfo.deletePref(DetailConfirmActivity.this);
		Intent intent = new Intent(DetailConfirmActivity.this, ViewPagerInputActivity.class);
		intent.putExtra("ELEMENT_APPLY_ID", id);
		startActivity(intent);
	}

	public void clickDeleteApply(View v) {
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
		Intent intent = new Intent(activity, MenuAcivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(intent);
	}

	public void clickWithdrawApply(View v) {
		Intent intent = new Intent(DetailConfirmActivity.this, InputPasswordActivity.class);
		intent.putExtra("ELEMENT_APPLY_ID", id);
		intent.putExtra("CANCEL_APPLY", "1");
		startActivity(intent);
	}

	@Override
	public void onBackPressed() {
		btnBackClick(null);
	}

	public void btnBackClick(View v) {
		finish();
	}
}
