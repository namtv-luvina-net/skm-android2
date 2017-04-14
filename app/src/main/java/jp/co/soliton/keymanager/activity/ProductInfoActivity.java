package jp.co.soliton.keymanager.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.view.View;
import android.widget.Button;
import jp.co.soliton.keymanager.LogCtrl;
import jp.co.soliton.keymanager.R;
import jp.co.soliton.keymanager.common.CommonUtils;
import jp.co.soliton.keymanager.common.InfoDevice;
import jp.co.soliton.keymanager.customview.DialogApplyProgressBar;
import jp.co.soliton.keymanager.xmlparser.XmlPullParserAided;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by luongdolong on 3/31/2017.
 */

public class ProductInfoActivity extends Activity {
    private Button btnLogSendMail;
	DialogApplyProgressBar progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_info);
        btnLogSendMail = (Button) findViewById(R.id.btnLogSendMail);
	    progressDialog = new DialogApplyProgressBar(this);
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
        btnLogSendMail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
	            new ProcessInfoAndZipTask().execute();
            }
        });
    }

	private void endConnectionAndSentEmail(ContentZip contentZip) {
		Intent intent = new Intent(Intent.ACTION_SENDTO);
		intent.setType("*/*");
		intent.setData(new Uri.Builder().scheme("mailto").build());
		intent.putExtra(Intent.EXTRA_SUBJECT, getResources().getString(R.string.main_log_mailtitle));
		intent.putExtra(Intent.EXTRA_TEXT, contentZip.contentMail);
		// the attachment
		intent .putExtra(Intent.EXTRA_STREAM, contentZip.uriFileAttach);
		startActivityForResult(Intent.createChooser(intent, "Send via email"), 100);
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
		public Uri uriFileAttach;
	}

	private class ProcessInfoAndZipTask extends AsyncTask<Void, Void, ContentZip> {

		@Override
		protected void onPreExecute() {
			progressDialog.show();
		}

		@Override
		protected ContentZip doInBackground(Void... params) {
			ContentZip contentZip = new ContentZip();
			InfoDevice infoDevice = InfoDevice.getInstance(ProductInfoActivity.this);
			contentZip.contentMail = infoDevice.createFileInfo();
			File zipFile = infoDevice.createFileZip();
			contentZip.uriFileAttach = Uri.fromFile(zipFile);
			return contentZip;
		}

		@Override
		protected void onPostExecute(ContentZip contentZip) {
			super.onPostExecute(contentZip);
			progressDialog.dismiss();
			endConnectionAndSentEmail(contentZip);
		}
	}
}
