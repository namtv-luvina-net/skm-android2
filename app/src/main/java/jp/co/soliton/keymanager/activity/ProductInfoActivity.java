package jp.co.soliton.keymanager.activity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import jp.co.soliton.keymanager.BuildConfig;
import jp.co.soliton.keymanager.R;
import jp.co.soliton.keymanager.asynctask.ProcessInfoAndZipTask;
import jp.co.soliton.keymanager.common.EmailCtrl;

/**
 * Created by luongdolong on 3/31/2017.
 */

public class ProductInfoActivity extends BaseSettingPhoneActivity {
    private Button btnLogSendMail;
	ProgressDialog progressDialog;
	private Button btnSettingProductInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_info);
        btnLogSendMail = (Button) findViewById(R.id.btnLogSendMail);
	    btnSettingProductInfo = (Button) findViewById(R.id.btnSettingProductInfo);
	    progressDialog = new ProgressDialog(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
	    tvTitleHeader.setText(getString(R.string.label_product_setting));
        setupControl();
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
	            progressDialog.show();
	            progressDialog.setMessage(getApplicationContext().getString(R.string.creating_diagnostic_infomation));
	            new ProcessInfoAndZipTask(ProductInfoActivity.this, new ProcessInfoAndZipTask.EndProcessInfoTask() {
		            @Override
		            public void endConnection(ProcessInfoAndZipTask.ContentZip contentZip) {
			            progressDialog.dismiss();
			            EmailCtrl.sentEmailInfo(ProductInfoActivity.this, contentZip);
		            }
	            }).execute();
            }
        });
    }
}
