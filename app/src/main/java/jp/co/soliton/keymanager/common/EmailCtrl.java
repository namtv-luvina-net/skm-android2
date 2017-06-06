package jp.co.soliton.keymanager.common;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.content.FileProvider;
import jp.co.soliton.keymanager.R;
import jp.co.soliton.keymanager.asynctask.ProcessInfoAndZipTask;

/**
 * Created by nguyenducdat on 6/6/2017.
 */

public class EmailCtrl {

	public static void sentEmailInfo(Context context, ProcessInfoAndZipTask.ContentZip contentZip) {
		Uri contentUri = FileProvider.getUriForFile(context, "jp.co.soliton.keymanager", contentZip.file);
		Intent intent = new Intent(Intent.ACTION_SEND, contentUri);
		intent.setType("application/octet-stream");
		intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
		intent.putExtra(Intent.EXTRA_SUBJECT, context.getResources().getString(R.string.main_log_mailtitle) + " - " +
				contentZip.file
				.getName());
		intent.putExtra(Intent.EXTRA_TEXT, contentZip.contentMail);
		intent.putExtra(Intent.EXTRA_STREAM, contentUri);
		context.startActivity(Intent.createChooser(intent, null));
	}
}
