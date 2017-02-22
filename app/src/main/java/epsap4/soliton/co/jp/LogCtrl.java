package epsap4.soliton.co.jp;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;
import android.util.Log;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.Calendar;
import java.util.Date;

public class LogCtrl {

	public final static String m_strInfo = "Info";
	public final static String m_strVerbose = "Verbose";
	public final static String m_strDebug = "Debug";
	public final static String m_strError = "Error";

	public final static String m_strinfo_txt = "skminfo.txt";
	public final static String m_strlog_csv = "skmLog.csv";
	public final static String m_strlog_zip = "ZskmLog.zip";
	public final static String m_strlog_zippass = "ZskmLogPass.zip";
	public final static String m_str_zippassword = "9dk2@ml";	// ZIPファイル解凍パスワード

	public LogCtrl() {
		// TODO 自動生成されたコンストラクター・スタブ
	}

	public static void Logger(String msgtype, String msg, Context ctx) {

		if(msgtype.equalsIgnoreCase(m_strInfo) == true) {
			Log.i(StringList.m_str_SKMTag, msg);
		} else if(msgtype.equalsIgnoreCase(m_strVerbose) == true) {
			Log.v(StringList.m_str_SKMTag, msg);
		} else if(msgtype.equalsIgnoreCase(m_strDebug) == true) {
			Log.d(StringList.m_str_SKMTag, msg);
		} else if(msgtype.equalsIgnoreCase(m_strError) == true) {
			Log.e(StringList.m_str_SKMTag, msg);
		} else {
			Log.i(StringList.m_str_SKMTag, msg);
		}

		if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED) == false) {
			// SDカード領域が存在しないときは抜ける
			return;
		}

		// ユーザーがアクセスできない内部領域に作成する http://blog.lciel.jp/blog/2014/02/08/android-about-storage/
		// # 26472
		String log_path = ctx./*getExternalFilesDir(null)*/getFilesDir().getPath() + "/" + m_strlog_csv;

	//	String log_path = Environment.getExternalStorageDirectory().getPath() + "/" + m_strlog_csv;
		String log_str = printDate() + "," + msgtype + "," + msg + "\n";

		//Log.i(StringList.m_str_SKMTag, log_path);



		OutputStream out;
	    try {
	        out = new BufferedOutputStream(new FileOutputStream(log_path, true));// ctx.openFileOutput(log_path,Context.MODE_PRIVATE|Context.MODE_APPEND);
	        PrintWriter writer = new PrintWriter(new OutputStreamWriter(out,/*"UTF-8"*/"SJIS"));

	        //追記する
	        writer.append(log_str);
	        writer.close();
	    } catch (IOException e) {
	        // TODO 自動生成された catch ブロック
	        e.printStackTrace();
	    }
	}

	static public String printDate() {
		// Dateクラスによる現在時表示
		 Date date = new Date();

		// デフォルトのCalendarオブジェクト
		 Calendar cal = Calendar.getInstance();

		 String tmp = cal.get(Calendar.YEAR) + "/"
		            + (cal.get(Calendar.MONTH) + 1) + "/" + cal.get(Calendar.DATE)
		            + " " + cal.get(Calendar.HOUR_OF_DAY) + ":"
		            + cal.get(Calendar.MINUTE) + ":" + cal.get(Calendar.SECOND);

		 return tmp;

	 }

	static public void CreateInfoText(Context ctx) {
		String inf_path = ctx./*getExternalFilesDir(null)*/getFilesDir().getPath() + "/" + m_strinfo_txt;	// # 26472
		String log_str = "";

		////////////////////////
		// 情報を形成する
		////////////////////////

		// アプリ名:バージョン
		PackageInfo packageInfo = null;
		try {
			packageInfo = ctx.getPackageManager().getPackageInfo(ctx.getPackageName(), PackageManager.GET_META_DATA);
			log_str = ctx.getText(R.string.ApplicationTitle).toString() + " Ver. " + packageInfo.versionName
					+ " (Build " + Integer.toString(packageInfo.versionCode) + ")" + "\n";
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// タイムスタンプ
		log_str += "Timestamp : " + printDate() + "\n\n";

		// OSVersion
		log_str += "OSVersion : " + Build.VERSION.RELEASE + "\n";

		// Build Number
		log_str += "Build Number : " + Build.VERSION.INCREMENTAL + "\n";

		// MODEL
		log_str += "Model Name : " + Build.MODEL + "\n";

		// 全容量サイズ
		log_str += "Total Size: " + getTotalMem(Environment.getDataDirectory()) + "\n";

		// 空き容量
		log_str += "Useful Size: " + getUsableMem(Environment.getDataDirectory()) + "\n";


		OutputStream out;
	    try {
	        out = new BufferedOutputStream(new FileOutputStream(inf_path, false));// ctx.openFileOutput(log_path,Context.MODE_PRIVATE|Context.MODE_APPEND);
	        PrintWriter writer = new PrintWriter(new OutputStreamWriter(out,"UTF-8"));

	        //追記する
	        writer.append(log_str);
	        writer.close();
	    } catch (IOException e) {
	        // TODO 自動生成された catch ブロック
	        e.printStackTrace();
	    }
	}

	// トータルサイズ
	public static String getTotalMem(File item) {

		double d_size = 0;
	    int size = 0;
	    try {
	        StatFs fs = new StatFs(item.getAbsolutePath());
	        d_size = (fs.getBlockSize() / 1024) * (fs.getBlockCount() / 1024);
	    } catch (IllegalArgumentException e) {

	    }

	    String unit = "";
        if (d_size > 1024) {
        	d_size /= 1024;
            unit = "GB";
        //    if (size > 1024) {
        //    	size /= 1024;
        //        unit = "GB";
        //    }
        }
        String rtn_size = /*Integer.toString(size)*/Double.toString(d_size) + unit;

        Log.i(StringList.m_str_SKMTag, "getTotalMem size = " + rtn_size);

	    return rtn_size;
	}

	//
	public static String getUsableMem(File item) {

		double d_size = 0;
	    int size = 0;
	    try {
	        StatFs fs = new StatFs(item.getAbsolutePath());
	        d_size = (fs.getBlockSize() / 1024) * (fs.getAvailableBlocks() / 1024);
	    } catch (IllegalArgumentException e) {

	    }
	    String unit = "";
        if (d_size > 1024) {
        	d_size /= 1024;
            unit = "GB";
        //    if (size > 1024) {
        //    	size /= 1024;
        //        unit = "GB";
        //    }
        }
        String rtn_size = /*Integer.toString(size)*/Double.toString(d_size) + unit;

        Log.i(StringList.m_str_SKMTag, "getUsableMem size = " + rtn_size);

	    return rtn_size;
	}

}
