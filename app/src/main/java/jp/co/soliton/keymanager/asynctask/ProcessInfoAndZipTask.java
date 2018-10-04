package jp.co.soliton.keymanager.asynctask;

import android.content.Context;
import android.os.AsyncTask;
import jp.co.soliton.keymanager.BuildConfig;
import jp.co.soliton.keymanager.NotificationLogCtrl;
import jp.co.soliton.keymanager.SKMApplication;
import jp.co.soliton.keymanager.StringList;
import jp.co.soliton.keymanager.common.*;
import jp.co.soliton.keymanager.dbalias.ElementApply;
import jp.co.soliton.keymanager.dbalias.ElementApplyManager;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static jp.co.soliton.keymanager.common.DateUtils.STRING_DATE_FORMAT_SYSTEM_TIME;
import static jp.co.soliton.keymanager.common.DateUtils.STRING_DATE_FORMAT_SYSTEM_TIME1;
import static jp.co.soliton.keymanager.dbalias.DatabaseHandler.DATABASE_NAME;

/**
 * Created by nguyenducdat on 6/6/2017.
 */

public class ProcessInfoAndZipTask extends AsyncTask<Void, Void, ProcessInfoAndZipTask.ContentZip> {

	public interface EndProcessInfoTask{
		void endConnection(ContentZip contentZip);
	}
	String patternNameZipFile = "skm_and%1s_diag_%2s.zip";
	Context context;
	EndProcessInfoTask endProcessInfoTask;
	public ProcessInfoAndZipTask(Context context, EndProcessInfoTask endProcessInfoTask) {
		this.context = context;
		this.endProcessInfoTask = endProcessInfoTask;
	}

	@Override
	protected ContentZip doInBackground(Void... params) {
		ProcessInfoAndZipTask.ContentZip contentZip = new ContentZip();
		InfoDevice infoDevice = InfoDevice.getInstance(context);
		contentZip.contentMail = infoDevice.createFileInfo();
		ArrayList<String> listFileToZip = getListFileToZip(infoDevice);
		File zipFile = createFileZip(listFileToZip);
		contentZip.file = zipFile;
		return contentZip;
	}

	private ArrayList<String> getListFileToZip(InfoDevice infoDevice) {
		ArrayList<String> listFileToZip = LogFileCtrl.getListLogFile(context);
		listFileToZip.add(infoDevice.getPathFileInfo());
		String notificationFilePath = NotificationLogCtrl.getInstance().getNotificationLogFile();
		if (notificationFilePath != null) {
			listFileToZip.add(notificationFilePath);
		}

		ElementApplyManager elementMgr = ElementApplyManager.getInstance(context);
		List<ElementApply> lsElement = elementMgr.getAllCertificate();
		listFileToZip.add(createCertificateFile(lsElement));
		listFileToZip.add(createNotificationFile(lsElement));

		if (SKMApplication.SKM_DEBUG || SKMApplication.SKM_TRACE) {
			addFileMdmIfExists(listFileToZip);
			addFileDatabaseIfOk(listFileToZip);
		}
		return listFileToZip;
	}

	private String createCertificateFile(List<ElementApply> lsElement) {
		File certificateFile = new File(SKMApplication.getAppContext().getFilesDir().getPath() + File.separator +
				"certificates.txt");
		if (certificateFile.exists()) {
			certificateFile.delete();
		}
		JSONArray jsonArray = new JSONArray();
		for (ElementApply el : lsElement) {
			JSONObject jsonObject = new JSONObject();
			try {
				jsonObject.put("CN", el.getcNValue());
				jsonObject.put("SN", el.getSerialNumber());
				Date date = DateUtils.convertSringToDate(STRING_DATE_FORMAT_SYSTEM_TIME, el.getExpirationDate());
				String strDate = DateUtils.convertDateToString(STRING_DATE_FORMAT_SYSTEM_TIME1, date);
				jsonObject.put("Expiration", strDate);
			} catch (JSONException e) {
				e.printStackTrace();
			}
			jsonArray.put(jsonObject);
		}
		try {
			certificateFile.createNewFile();
			FileUtils.saveFileInfo(certificateFile, jsonArray.toString(4));
		} catch (IOException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return certificateFile.getAbsolutePath();
	}

	private String createNotificationFile(List<ElementApply> lsElement) {
		File notificationFile = new File(SKMApplication.getAppContext().getFilesDir().getPath() + File.separator +
				"notification.txt");
		if (notificationFile.exists()) {
			notificationFile.delete();
		}
		JSONArray jsonArray = new JSONArray();
		for (ElementApply el : lsElement) {
			if (el.isNotiEnableFlag()) {
				JSONObject jsonObject = new JSONObject();
				try {
					jsonObject.put("Type", "Expired");
					Date date = DateUtils.convertSringToDate(STRING_DATE_FORMAT_SYSTEM_TIME, el.getExpirationDate());
					String strDate = DateUtils.convertDateToString(STRING_DATE_FORMAT_SYSTEM_TIME1, date);
					jsonObject.put("DeliveryDate", strDate);
					jsonObject.put("DateInterval", 0);
					jsonObject.put("SN", el.getSerialNumber());
					jsonObject.put("CN", el.getcNValue());
				} catch (Exception e) {
					e.printStackTrace();
				}
				jsonArray.put(jsonObject);
			}
			if (el.isNotiEnableBeforeFlag()) {
				JSONObject jsonObject = new JSONObject();
				try {
					jsonObject.put("Type", "DaysBefore");
					Date date = DateUtils.convertSringToDate(STRING_DATE_FORMAT_SYSTEM_TIME, el.getExpirationDate());
					String strDate = DateUtils.convertDateToString(STRING_DATE_FORMAT_SYSTEM_TIME1, date);
					jsonObject.put("DeliveryDate", strDate);
					jsonObject.put("DateInterval", el.getNotiEnableBefore());
					jsonObject.put("SN", el.getSerialNumber());
					jsonObject.put("CN", el.getcNValue());
				} catch (Exception e) {
					e.printStackTrace();
				}
				jsonArray.put(jsonObject);
			}
		}
		try {
			notificationFile.createNewFile();
			FileUtils.saveFileInfo(notificationFile, jsonArray.toString(4));
		} catch (IOException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return notificationFile.getAbsolutePath();
	}

	private void addFileMdmIfExists(ArrayList<String> listFileToZip) {
		String filedir = "/data/data/" + context.getPackageName() + "/files/";
		File filename_mdm = new File(filedir + StringList.m_strMdmOutputFile);
		if(filename_mdm.exists()) {
			listFileToZip.add(filename_mdm.getAbsolutePath());
		}
	}

	private void addFileDatabaseIfOk(ArrayList<String> listFileToZip) {
		try {
			String pathDatabase = backupDatabase();
			if (pathDatabase.length() != 0) {
				listFileToZip.add(pathDatabase);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private File createFileZip(ArrayList<String> listFileToZip) {
		File outputDir = context.getExternalCacheDir();
		clearOldCacheFiles(outputDir);
		String nameFileZip = String.format(patternNameZipFile, getVersionName(), DateUtils.getCurrentDateZip());
		File outputFile = new File(outputDir, nameFileZip);
		try {
			outputFile.createNewFile();
		} catch (IOException e) {
			e.printStackTrace();
		}
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

	public String backupDatabase() throws IOException {
		String inFileName = "/data/data/" + context.getPackageName() + File.separator + "databases" + File.separator +
				DATABASE_NAME;
		File dbFile = new File(inFileName);
		FileInputStream fis = new FileInputStream(dbFile);

		String outFileName = context.getFilesDir().getPath() + File.separator + DATABASE_NAME;
		//Open the empty db as the output stream
		OutputStream output = new FileOutputStream(outFileName, false);
		//transfer bytes from the inputfile to the outputfile
		byte[] buffer = new byte[1024];
		int length;
		while ((length = fis.read(buffer)) > 0) {
			output.write(buffer, 0, length);
		}
		//Close the streams
		output.flush();
		output.close();
		fis.close();
		return outFileName;
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
		endProcessInfoTask.endConnection(contentZip);
	}

	public class ContentZip {
		public String contentMail;
		public File file;
	}
}