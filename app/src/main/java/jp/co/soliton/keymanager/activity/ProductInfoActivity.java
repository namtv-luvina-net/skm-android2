package jp.co.soliton.keymanager.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import jp.co.soliton.keymanager.BuildConfig;
import jp.co.soliton.keymanager.LogCtrl;
import jp.co.soliton.keymanager.R;
import jp.co.soliton.keymanager.StringList;
import jp.co.soliton.keymanager.asynctask.ProcessInfoAndZipTask;
import jp.co.soliton.keymanager.common.EmailCtrl;
import jp.co.soliton.keymanager.customview.DialogApplyConfirm;

/**
 * Created by luongdolong on 3/31/2017.
 */

public class ProductInfoActivity extends BaseSettingPhoneActivity {
    private Button btnLogSendMail;
	private ProgressDialog progressDialog;
	private Button btnSettingProductInfo;
	private Button btnPrivacyPolicy;

	private Switch traceModeSwitch;
	private RelativeLayout traceModeItem;
	public static final int MAX_CLICK_COUNT = 7;
	private int clickCount;
	private Toast toast;

	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_info);
        btnLogSendMail = findViewById(R.id.btnLogSendMail);
	    btnSettingProductInfo = findViewById(R.id.btnSettingProductInfo);
	    btnPrivacyPolicy = findViewById(R.id.btnPrivacyPolicy);
	    progressDialog = new ProgressDialog(this);
		traceModeSwitch =findViewById(R.id.sw_trace_mode);
		traceModeItem = findViewById(R.id.traceLogsItem);
	}

    @Override
    protected void onResume() {
        super.onResume();
        setupControl();
    }

	@Override
	protected void setTextTitle() {
		tvTitleHeader.setText(getString(R.string.label_product_setting));
	}

	public void setupControl() {
	    String nameApp = getString(R.string.app_name);
	    String version = getString(R.string.version);
	    String verApp = BuildConfig.VERSION_NAME;
	    String productInfo = nameApp + "\n" + version +" " +verApp;
	    btnSettingProductInfo.setText(productInfo);

		Context context = getApplicationContext();
		SharedPreferences sharedPref = context.getSharedPreferences(StringList.m_str_store_preference,
				MODE_PRIVATE);
		boolean isTraceMode = sharedPref.getBoolean(StringList.TRACE_LOG_KEY, false);
		clickCount = isTraceMode ? 0 : MAX_CLICK_COUNT;
		traceModeItem.setVisibility(isTraceMode ? View.VISIBLE : View.GONE);

		btnSettingProductInfo.setOnClickListener(new View.OnClickListener() {
		    @Override
		    public void onClick(View v) {
			    handleTapProductItem();
		    }
	    });

        btnLogSendMail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
	            confirmPrivacyPolicy();

            }
        });

		btnPrivacyPolicy.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				openUrlPrivacyPolicy();
			}
		});

		traceModeSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
				traceModeItem.setVisibility(View.GONE);
				traceModeSwitch.setChecked(true);
				clickCount = MAX_CLICK_COUNT;
				Context context = getApplicationContext();
				SharedPreferences sharedPref = context.getSharedPreferences(StringList.m_str_store_preference,
						MODE_PRIVATE);
				SharedPreferences.Editor editor = sharedPref.edit();
				editor.putBoolean(StringList.TRACE_LOG_KEY, false);
				editor.commit();
				LogCtrl.getInstance().updateTraceMode();
			}
		});
    }

	private void handleTapProductItem() {
		clickCount--;
		if (clickCount < MAX_CLICK_COUNT - 2) {
			if (toast != null) {
				toast.cancel();
			}
			if (clickCount > 0) {
				toast = Toast.makeText(this, String.format(getString(R.string.trace_mode_notify), clickCount), Toast
						.LENGTH_SHORT);
			} else {
				toast = Toast.makeText(this, getString(R.string.trace_mode_enable), Toast.LENGTH_SHORT);
				if (clickCount == 0) {
					Context context = getApplicationContext();
					SharedPreferences sharedPref = context.getSharedPreferences(StringList.m_str_store_preference,
							MODE_PRIVATE);
					SharedPreferences.Editor editor = sharedPref.edit();
					editor.putBoolean(StringList.TRACE_LOG_KEY, true);
					editor.commit();
					traceModeItem.setVisibility(View.VISIBLE);
					LogCtrl.getInstance().updateTraceMode();
				}
			}
			toast.show();
		}
	}

	private void confirmPrivacyPolicy() {
		final DialogApplyConfirm dialog = new DialogApplyConfirm(this);
		String message = getResources().getString(R.string.content_privacy_policy);
		dialog.setOnClickOK(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				dialog.dismiss();
				sendDiagnostics();
			}
		});
		dialog.setOnClickCancel(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				dialog.dismiss();
			}
		});
		dialog.setTextDisplay("", message, "", "");
		dialog.show();
	}

	private void sendDiagnostics() {
		progressDialog.show();
		progressDialog.setMessage(getApplicationContext().getString(R.string.creating_diagnostic_infomation));
		new ProcessInfoAndZipTask(ProductInfoActivity.this, new ProcessInfoAndZipTask.EndProcessInfoTask() {
			@Override
			public void endConnection(ProcessInfoAndZipTask.ContentZip contentZip) {
				progressDialog.dismiss();
				LogCtrl.getInstance().info("Diag: Zip archiving successful");
				EmailCtrl.sentEmailInfo(ProductInfoActivity.this, contentZip);
			}
		}).execute();
	}

	private void openUrlPrivacyPolicy() {
		Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(getResources().getString(R.string
				.url_privacy_policy)));
		if (browserIntent.resolveActivity(getPackageManager()) != null) {
			startActivity(browserIntent);
		} else {
			Toast.makeText(ProductInfoActivity.this, getString(R.string.browse_is_not_installed), Toast.LENGTH_SHORT).show();
		}
	}
}
