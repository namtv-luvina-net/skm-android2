package jp.co.soliton.keymanager.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import jp.co.soliton.keymanager.BuildConfig;
import jp.co.soliton.keymanager.LogCtrl;
import jp.co.soliton.keymanager.R;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_info);
        btnLogSendMail = (Button) findViewById(R.id.btnLogSendMail);
	    btnSettingProductInfo = (Button) findViewById(R.id.btnSettingProductInfo);
	    btnPrivacyPolicy = findViewById(R.id.btnPrivacyPolicy);
	    progressDialog = new ProgressDialog(this);
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
