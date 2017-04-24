package jp.co.soliton.keymanager.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.FileProvider;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import jp.co.soliton.keymanager.BuildConfig;
import jp.co.soliton.keymanager.LogCtrl;
import jp.co.soliton.keymanager.R;
import jp.co.soliton.keymanager.common.*;
import jp.co.soliton.keymanager.xmlparser.XmlPullParserAided;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

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
	            new ProcessInfoAndZipTask().execute();
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

	private void endConnectionAndSentEmail(ContentZip contentZip) {
		Uri contentUri = FileProvider.getUriForFile(this, "jp.co.soliton.keymanager", contentZip.file);
		Intent intent = new Intent(Intent.ACTION_SEND, contentUri);
		intent.setType("application/octet-stream");
		intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
		intent.putExtra(Intent.EXTRA_SUBJECT, getResources().getString(R.string.main_log_mailtitle));
		intent.putExtra(Intent.EXTRA_TEXT, contentZip.contentMail);
		intent.putExtra(Intent.EXTRA_STREAM, contentUri);
		startActivity(Intent.createChooser(intent, null));
	}

	private String buildBodyMailDiagnostics() {
        try {
            // Version
            PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            String version = pInfo.versionName;
            // APID
            String wifi = XmlPullParserAided.GetUDID(this);
            String vpn = XmlPullParserAided.GetVpnApid(this);
            // System Time
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
            String time = formatter.format(Calendar.getInstance().getTime());
            // Model
            String manufacturer = Build.MANUFACTURER;
            String model = Build.MODEL;
            String platform;
            if (model.startsWith(manufacturer)) {
                platform = CommonUtils.capitalize(model);
            } else {
                platform = CommonUtils.capitalize(manufacturer) + " " + model;
            }
            // Version
            String systemVersion = android.os.Build.VERSION.RELEASE;
            String buildNumber = Build.DISPLAY;
            // Carrier name
            TelephonyManager manager = (TelephonyManager)getApplicationContext().getSystemService(Context.TELEPHONY_SERVICE);
            String carrierName = manager.getNetworkOperatorName();
            if (CommonUtils.isEmpty(carrierName)) {
                carrierName = "-";
            }
            //File system
            String availableInternalMemory = CommonUtils.getAvailableInternalMemorySize();
            String totalInternalMemory = CommonUtils.getTotalInternalMemorySize();
            String availableExternalMemory = CommonUtils.getAvailableExternalMemorySize();
            String totalExternalMemory = CommonUtils.getTotalExternalMemorySize();
            String result = String.format("Soliton KeyManager Version: %s \n" +
                                         "Wi-Fi: %s\n" +
                                         "VPN and apps: %s\n" +
                                         "System Time: %s\n" +
                                         "Hardware Model: %s\n" +
                                         "OS Version: %s (%s)\n" +
                                         "Carrier Name: %s\n" +
                                         "Total Internal Memory %s\n" +
                                         "Available Internal Memory %s\n" +
                                         "Total External Memory %s\n" +
                                         "Available External Memory %s\n"
                                         , version, wifi, vpn, time, platform, systemVersion, buildNumber, carrierName,
                                        totalInternalMemory, availableInternalMemory, totalExternalMemory, availableExternalMemory);
            return result;
        } catch (Exception ex) {
	        LogCtrl.getInstance(this).loggerInfo("ProductInfoActivity:buildBodyMailDiagnostics:" + ex.getMessage());
            return "";
        }
    }

    private String GetUDID() {
        return XmlPullParserAided.GetUDID(this);
    }

    private String GetVpnApid() {
        return XmlPullParserAided.GetVpnApid(this);
    }

	private class ContentZip {
		public String contentMail;
		public File file;
	}

	private class ProcessInfoAndZipTask extends AsyncTask<Void, Void, ContentZip> {

		String patternNameZipFile = "skm_and%1s_diag_%2s.zip";

		@Override
		protected ContentZip doInBackground(Void... params) {
			ContentZip contentZip = new ContentZip();
			InfoDevice infoDevice = InfoDevice.getInstance(ProductInfoActivity.this);
			contentZip.contentMail = infoDevice.createFileInfo();
			File zipFile = createFileZip(infoDevice);
			contentZip.file = zipFile;
			return contentZip;
		}

		private File createFileZip(InfoDevice infoDevice) {
			File outputDir = getApplicationContext().getExternalCacheDir();
			clearOldCacheFiles(outputDir);
			String nameFileZip = String.format(patternNameZipFile, getVersionName(), DateUtils.getCurrentDateZip());
			File outputFile = new File(outputDir, nameFileZip);
			try {
				outputFile.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
			ArrayList<String> listFileToZip = LogFileCtrl.getListLogFile(getApplicationContext());
			listFileToZip.add(infoDevice.getPathFileInfo());
			new Compress(listFileToZip, outputFile.getAbsolutePath()).zip();
			return outputFile;
		}

		private void clearOldCacheFiles(File files) {
			for (String fileName : files.list()) {
				if (isZipFile(fileName)) {
					File file = new File(files, fileName);
					file.delete();
				}
			}
		}

		private boolean isZipFile(String fileName) {
			return fileName.startsWith("skm_and") && fileName.endsWith(".zip");
		}

		private String getVersionName() {
			String version = BuildConfig.VERSION_NAME + BuildConfig.BUILD_NUM;
			version = version.replace(".", "");
			return version;
		}

		@Override
		protected void onPostExecute(ContentZip contentZip) {
			super.onPostExecute(contentZip);
			progressDialog.dismiss();
			endConnectionAndSentEmail(contentZip);
		}
	}
}
