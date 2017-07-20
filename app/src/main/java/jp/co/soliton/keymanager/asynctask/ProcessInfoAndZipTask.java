package jp.co.soliton.keymanager.asynctask;

import android.content.Context;
import android.os.AsyncTask;
import jp.co.soliton.keymanager.BuildConfig;
import jp.co.soliton.keymanager.SKMApplication;
import jp.co.soliton.keymanager.StringList;
import jp.co.soliton.keymanager.common.Compress;
import jp.co.soliton.keymanager.common.DateUtils;
import jp.co.soliton.keymanager.common.InfoDevice;
import jp.co.soliton.keymanager.common.LogFileCtrl;

import java.io.*;
import java.util.ArrayList;

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
		if (SKMApplication.SKM_DEBUG || SKMApplication.SKM_TRACE) {
			addFileMdmIfExists(listFileToZip);
			addFileDatabaseIfOk(listFileToZip);
		}
		return listFileToZip;
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