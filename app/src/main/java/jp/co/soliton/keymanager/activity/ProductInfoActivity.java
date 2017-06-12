package jp.co.soliton.keymanager.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import jp.co.soliton.keymanager.BuildConfig;
import jp.co.soliton.keymanager.R;
import jp.co.soliton.keymanager.asynctask.ProcessInfoAndZipTask;
import jp.co.soliton.keymanager.common.EmailCtrl;

/**
 * Created by luongdolong on 3/31/2017.
 */

public class ProductInfoActivity extends Activity {
    private Button btnLogSendMail;
	ProgressDialog progressDialog;
	private Button btnSettingProductInfo;
	private TextView textViewBack;
	private TextView tvTitleHeader;
	private TextView spaceRightTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_info);
        btnLogSendMail = (Button) findViewById(R.id.btnLogSendMail);
	    textViewBack = (TextView) findViewById(R.id.textViewBack);
	    tvTitleHeader = (TextView) findViewById(R.id.tvTitleHeader);
	    spaceRightTitle = (TextView) findViewById(R.id.spaceRightTitle);
	    btnSettingProductInfo = (Button) findViewById(R.id.btnSettingProductInfo);
	    progressDialog = new ProgressDialog(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        setupControl();
    }

    public void btnBackClick(View v) {
        finish();
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
	    updateTitle();
    }

	private void updateTitle() {
		tvTitleHeader.measure(0, 0);
		textViewBack.measure(0, 0);
		DisplayMetrics displayMetrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
		int width = displayMetrics.widthPixels;

		if (tvTitleHeader.getMeasuredWidth() > width - (textViewBack.getMeasuredWidth() * 2)) {
			textViewBack.setText("");
			RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
					RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
			params.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
			params.addRule(RelativeLayout.RIGHT_OF, textViewBack.getId());
			params.addRule(RelativeLayout.LEFT_OF, spaceRightTitle.getId());
			tvTitleHeader.setLayoutParams(params);
		}
	}
}
