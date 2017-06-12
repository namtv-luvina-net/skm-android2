package jp.co.soliton.keymanager.asynctask;

import android.content.Context;
import android.os.AsyncTask;
import jp.co.soliton.keymanager.BuildConfig;
import jp.co.soliton.keymanager.common.Compress;
import jp.co.soliton.keymanager.common.DateUtils;
import jp.co.soliton.keymanager.common.InfoDevice;
import jp.co.soliton.keymanager.common.LogFileCtrl;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

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
		File zipFile = createFileZip(infoDevice);
		contentZip.file = zipFile;
		return contentZip;
	}

	private File createFileZip(InfoDevice infoDevice) {
		File outputDir = context.getExternalCacheDir();
		clearOldCacheFiles(outputDir);
		String nameFileZip = String.format(patternNameZipFile, getVersionName(), DateUtils.getCurrentDateZip());
		File outputFile = new File(outputDir, nameFileZip);
		try {
			outputFile.createNewFile();
		} catch (IOException e) {
			e.printStackTrace();
		}
		ArrayList<String> listFileToZip = LogFileCtrl.getListLogFile(context);
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
		endProcessInfoTask.endConnection(contentZip);
	}

	public class ContentZip {
		public String contentMail;
		public File file;
	}
}