package epsap4.soliton.co.jp;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class LogMonitor {
	private LogMonitorThread m_LogMonitorThread = null;
	private Context serviceContext = null;
	private boolean running = false;
	
	// cmp
	private static final String LOG_GMP = "cmp=";
	// act
	private static final String Log_ACT = "act=";
	
	// Log
	private static final String LOG_TAG = "MDM LogMonitor";
	private static final String LOG_CAT_COMMAND = "logcat";
	private static final String LOG_MONITOR_FILTER = "ActivityManager:I *:S";
	
	// target application list
	private static final String LOG_APP_TARGET_ACTION = "act=android.intent.action.MAIN";//"Starting activity: Intent";
	private static final String LOG_APP_TARGET_APP1 = "cmp=com.alphonso.pulse";
	private static final String LOG_APP_TARGET_YOUTUBE = "com.google.android.youtube";
	private static final String LOG_APP_TARGET_APP3 = "cmp=com.twitter.android";
	
	// Android Market
	private static final String LOG_APP_TARGET_MARKET = "com.android.vending";
	private static final String LOG_APP_TARGET_MARKET2 = "com.google.android.finsky";
	
	// target action for camera
	private static final String LOG_CAMERA_TARGET_ACTION = "act=android.intent.action.MAIN";//"Starting activity: Intent";
	private static final String LOG_CAMERA_TARGET_APP = "act=android.media.action.IMAGE_CAPTURE";
	private static final String LOG_CAMERA_TARGET_CMP = "cmp=com.sec.android.app.camera";
	private static final String LOG_CAMERA_TARGET_CMP2 = "cmp=com.google.android.camera";
	private static final String LOG_CAMERA_TARGET_CMP3= "cmp=com.android.camera";
	private static final String LOG_CAMERA_TARGET_CMP4= "android.camera";
	
	// target action url filter
	private static final String LOG_URI_FILTER_TARGET_ACTION = "act=android.intent.action.VIEW";//"Starting activity: Intent";
	private static final String LOG_URI_FILTER_TARGET_APP = "dat=http://www.soliton.co.jp";
	private static final String LOG_URI_FILTER_TARGET_APP2 = "dat=http://www.yahoo.com";
	
	private RestrictionsFlgs m_restriction;	// yoshim add

	public void init(Context context) {
		m_LogMonitorThread = new LogMonitorThread();
		serviceContext = context;
	}
	
	public void init_rest(RestrictionsFlgs rest) {m_restriction = rest;}
	
	public void start() {
		if (m_LogMonitorThread == null) {
			return;
		}
		LogClear();
		running = true;
		m_LogMonitorThread.start();		
		Log.i(LOG_TAG, "***start monitoring***");
	}
	
	public void stop() {
		///// ★★★ /////
		///// サービスを終了すると、ここで受け取る /////
		///// ★★★ /////
		running = false;
		Log.i(LOG_TAG, "***stop monitoring***");
	}
	
	// Session
	public boolean isUnlocked() {
		SessionInfo session = SessionInfo.getInstance();
		boolean unlock = session.isUnlock();
		if (unlock) {
			session.clear();
		}
		return unlock;
	}
	
	public void setExtra(String pkg, String cls, String extraType, String extraString) {
		SessionInfo session = SessionInfo.getInstance();
		session.setBlock(true);
		session.setExtra(pkg, cls, extraType, extraString);
	}
	
	// URI filter
	public String getUri(String line) {
		String uri = null;
		
		int nCmpIndex = line.indexOf("dat=");
		int nSlaIndex = line.indexOf(" ", nCmpIndex);
		if ((nCmpIndex == -1) || (nCmpIndex >= nSlaIndex)) {
			uri = null;
		}
		else {
			uri = line.substring(nCmpIndex+4, nSlaIndex);
		}

		return uri;
	}
	
	// Camera
	public String getPackageName(String line) {
		String packageName = null;
		int nCmpIndex = line.indexOf("cmp=");
		int nSlaIndex = line.indexOf("/", nCmpIndex);
		if (nCmpIndex < nSlaIndex) {
			packageName = line.substring(nCmpIndex+4, nSlaIndex);
			//Log.w(LOG_TAG, "********** PkgName:" + packageName);
		}
		return packageName;
	}
	
	public String getClassName(String packageName, String line) {
		String className = null;
		if (packageName == null) {
			return null;
		}
		int nPkgIndex = line.indexOf(packageName);
		int nSlaIndex = line.indexOf(" ", nPkgIndex+packageName.length());
		int nClsIndex = nPkgIndex + packageName.length();
		if (nClsIndex < nSlaIndex) {
			className = packageName + line.substring(nClsIndex+1, nSlaIndex);
			//Log.w(LOG_TAG, "********** ClsName:" + className);
		}
		return className;
	}
	
	public boolean isBlockApp(String line) {
		boolean block = false;
		Log.i("====LogMonitor isBlockApp====", "start");
		Log.i("====LogMonitor isBlockApp====", line);
		
		////////////////
		// widget対応 //
		////////////////
		// youtube
		if((line.indexOf(Log_ACT + LOG_APP_TARGET_YOUTUBE) != -1) &&
			(m_restriction.GetYouTube() == false)) {
			block = true;
		}
		// Android Market
		if((line.indexOf(Log_ACT + LOG_APP_TARGET_MARKET) != -1) &&
			(m_restriction.GetiTunes() == false)) {
			block = true;
		} else if((line.indexOf(Log_ACT + LOG_APP_TARGET_MARKET2) != -1) &&
			(m_restriction.GetiTunes() == false)) {
			block = true;
		}
		
		// アプリ起動
		if (line.indexOf(LOG_APP_TARGET_ACTION) != -1) {
			/*if ((line.indexOf(LOG_APP_TARGET_APP1) != -1) ||
					(line.indexOf(LOG_APP_TARGET_APP2) != -1) ||
					(line.indexOf(LOG_APP_TARGET_APP3) != -1)){
				// unlocked?
				block = true;
			}*/
			
			// youtubeフラグがないときは、起動しないようにする
			if((line.indexOf(LOG_GMP + LOG_APP_TARGET_YOUTUBE) != -1) && 
					(m_restriction.GetYouTube() == false)) {
				block = true;
			}
			
			// Android Market
			if((line.indexOf(LOG_GMP + LOG_APP_TARGET_MARKET) != -1) &&
					(m_restriction.GetiTunes() == false)) {
				block = true;
			} else if((line.indexOf(LOG_GMP + LOG_APP_TARGET_MARKET2) != -1) &&
					(m_restriction.GetiTunes() == false)) {
				block = true;
			}
		}
		return block;
	}
	
	public boolean isBlockUri(String line) {
		boolean block = false;
		if (line.indexOf(LOG_URI_FILTER_TARGET_ACTION) != -1) {
			if ((line.indexOf(LOG_URI_FILTER_TARGET_APP) != -1) ||
					(line.indexOf(LOG_URI_FILTER_TARGET_APP2) != -1)){
				// unlocked?
				block = true;
			}
		}
		return block;
	}
	
	public boolean isBlockCamera(String line) {
		boolean block = false;
		// yoshim add...
		// Cameraフラグがfalseかtrueか
		if(m_restriction.GetCamera() == true) {
			// cameraがtrueのときは正常に起動させるため、すぐにreturnする.
			Log.d(LOG_TAG, "isBlockCamera false");
			return block;
		}
		
		if (line.indexOf(LOG_CAMERA_TARGET_ACTION) != -1) {
			// found start activity
			String packageName = getPackageName(line);
			Log.d(LOG_TAG, line);
			
			// target application?
			if (line.indexOf(LOG_CAMERA_TARGET_APP) != -1) {
				// start interrupt activity!
				Log.d(LOG_TAG, "Camera00001");
				block = true;
			}
			else if (line.indexOf(LOG_CAMERA_TARGET_CMP) != -1) {
				// start interrupt activity!
				Log.d(LOG_TAG, "Camera00002");
				block = true;
			}
			else if (line.indexOf(LOG_CAMERA_TARGET_CMP2) != -1) {
				// start interrupt activity!
				Log.d(LOG_TAG, "Camera00003");
				block = true;
			} else if(line.indexOf(LOG_CAMERA_TARGET_CMP3) != -1) {
				Log.d(LOG_TAG, "Camera00004");
				block = true;
			} else if(line.indexOf(LOG_CAMERA_TARGET_CMP4) != -1) {
				Log.d(LOG_TAG, "Camera00005");
				block = true;
			}
			//else {
				// check permission. take picture
			//	PackageManager pm = serviceContext.getPackageManager();
			//	if (pm.checkPermission(android.Manifest.permission.CAMERA, packageName) == 0) {
			//		Log.d(LOG_TAG, "Camera00002");
			//		block = true;
			//	}
			//}
		}
		return block;
	}
	
	public void LogClear() {
		ArrayList<String> command = new ArrayList<String>();
		command.add(LOG_CAT_COMMAND);
		command.add("-c");
		try {
			Process process = Runtime.getRuntime().exec(command.toArray(new String[command.size()]));
			BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()), 1024);
			
			String line = bufferedReader.readLine();
			if (line != null) {
				Log.i(LOG_TAG, "LogClear:" + line);
			}
		}
		catch (IOException ex) {
			// exception
			Log.i(LOG_TAG, "IOException:" + ex.getMessage());
		}
	}
	
	private class LogMonitorThread extends Thread {
		@Override
		public void run() {
			Log.d(LOG_TAG, "LogMonitorThread *=*=*=*= Start =*=*=*=*");
			
			ArrayList<String> command = new ArrayList<String>();
			command.add(LOG_CAT_COMMAND);
			command.add(LOG_MONITOR_FILTER);	// "ActivityManager"tagメンバを取得する.
			
			try {
				Process process = Runtime.getRuntime().exec(command.toArray(new String[command.size()]));
				BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()), 1024);
				
				Log.d(LOG_TAG, "LogMonitorThread *=*=*=*= TRACE 01 =*=*=*=*");
				
				String line = "***read start***";
				while ((running == true) /*&& (line != null)*/) {
					
					try {
					//////////////////////////////////
					// スレッド起動後はwhileループ内を動き続ける.
					// スレッドが止まってしまうのは、何らかが原因でループを抜けてしまうためだと思われる.
					// lineがnullになってしまうことがあるのかも...
					/////////////////////////////////
					Log.d(LOG_TAG, "LogMonitorThread *=*=*=*= TRACE 02 =*=*=*=*");
					
					// listen log
					line = bufferedReader.readLine();
					Log.d(LOG_TAG, "LogMonitorThread *=*=*=*= TRACE 03 =*=*=*=*");
					
					if(line == null) continue;		// lineがnullだったら,ループを抜けずに再度readLineを実行する

					boolean block = false;
					String extraType = null;
					String extraString = null;
					
					if (isBlockApp(line)) {
						// Application
						if (!isUnlocked()) {
							extraType = "TYPE_APP";
							extraString = getPackageName(line);
							block = true;
							Log.i(LOG_TAG, "***isBlockApp block true***");
						}
					}
				/*	else if (isBlockUri(line)) {
						// URI
						if (!isUnlocked()) {						
							extraType = "TYPE_URI";
							extraString = getUri(line);
							block = true;
							
							// none page activity
					    	Intent noneView = new Intent(Intent.ACTION_VIEW);
					    	noneView.setData(Uri.parse("about:blank"));
					    	noneView.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					    	serviceContext.startActivity(noneView);
						}
					}*/
			/*		else if (isBlockCamera(line)) {
						// CAMERA
						if (!isUnlocked()) {
							extraType = "TYPE_CAMERA";
							extraString = getPackageName(line);
							block = true;
							Log.i(LOG_TAG, "***isBlockCamera block true***");
						}
					}
			    */	
					// Block!!!
					if (block) {
						// set common data
						String pkg = getPackageName(line);
						String cls = getClassName(pkg, line);
						Log.e(LOG_TAG, line);
						Log.e(LOG_TAG, "PackageName=" + pkg + ", ClassName=" + cls + ", Type=" + extraType + ", String=" + extraString);
						
						// set extra .. ここでblockを設定する
						setExtra(pkg, cls, extraType, extraString);
						
						// 外部アプリを終了する(2.2以降は動かないようだ...)
						// 参照：http://www.bpsinc.jp/blog/archives/1707
						/*ActivityManager am = 
							 (ActivityManager)serviceContext.getSystemService(serviceContext.ACTIVITY_SERVICE); 
						am.restartPackage(pkg); */
							                                
						// send
						Intent Views = new Intent();
						Views.setAction("epsap3.soliton.co.jp.block.ViewAction.VIEW");
						serviceContext.sendBroadcast(Views);
					}
					else {
						//Log.i(LOG_TAG, line);
					}
					} catch (IOException ex) {
						// exception
						Log.e(LOG_TAG, "IOException:" + ex.getMessage());
					}
				}
			}
			catch (IOException ex) {
				// exception
				Log.e(LOG_TAG, "IOException:" + ex.getMessage());
			}
		}
	};
};