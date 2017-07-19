package jp.co.soliton.keymanager.mdm;

import android.app.Service;
import android.app.admin.DevicePolicyManager;
import android.content.*;
import android.os.Binder;
import android.os.IBinder;
import android.os.Parcel;
import android.os.RemoteException;
import jp.co.soliton.keymanager.EpsapAdminReceiver;
import jp.co.soliton.keymanager.LogCtrl;

public class MDMService extends Service {

	private MDMThread m_ndmthread;
	private MDMFlgs m_mdmflgs;	
	BroadcastReceiver mReceiver; 
	
	DevicePolicyManager m_DPM;
    ComponentName m_DeviceAdmin;
	
	public MDMService() {
		// TODO 自動生成されたコンストラクター・スタブ
	}
	
	public void onCreate() {
		LogCtrl.getInstance().info("MDMService: onCreate");
		m_DPM = (DevicePolicyManager)getSystemService(Context.DEVICE_POLICY_SERVICE);
		m_DeviceAdmin = new ComponentName(MDMService.this, EpsapAdminReceiver.class);
		// Initialize Monitoring log
		m_ndmthread  = new MDMThread(this, m_DPM, m_DeviceAdmin);
	//	m_LogMonitor.init(this);
		
		
		// BootReceiverをSCREEN_ONに対応させるために動的に登録.
		// BroadcastReceiverの静的、動的設定の違いは 
		// ■静的 
		//・AndroidManifest.xmlに定義する事で利用出来る。 (ACTION_BOOT_COMPLETED)
		//・受信出来ないActionがある。(ACTION_BATTERY_CHANGEDとか) 
		//・onReceive時にインスタンスが生成され、実行後に破棄される。つまり関連するメンバ変数、staticな変数等は毎回クリアされる。 
		//■動的 
		//・Activity,Serviceの中でContext#registerReceiverして登録する事で利用出来る。 
		//・多分全部のActionを受信できる 
		//・Context#registerReceiverかそれ以前にインスタンスが生成され、Context#registerReceiverしたActivi tyやServiceが生きている内はインスタンスが存在する。
	/*	mReceiver = new MDMBootReceiver();
		IntentFilter filter = new IntentFilter();
		filter.addAction(Intent.ACTION_SCREEN_ON);
		filter.addAction(Intent.ACTION_SCREEN_OFF);
		registerReceiver(mReceiver, filter);
		*/
		super.onCreate();
		
		
	}

	@Override
	public void onStart(Intent intent, int startId) {
		LogCtrl.getInstance().info("MDMService: onStart");
		try {
		//	m_mdmflgs = (MDMFlgs)intent.getSerializableExtra(StringList.m_str_MdmFlgs);
			// サービスが実行される頃にはすでにチェックインを済ませて情報を保存しているため,
			// put渡しではなく出力ファイルから読み込んでFlagパラメータを設定しなおすほうが間違いがない
			MDMFlgs mdm = new MDMFlgs();
        	boolean bRet = mdm.ReadAndSetScepMdmInfo(this);
        	
        	if (bRet == false) {
				LogCtrl.getInstance().error("MDMService: No MDM info");
        		return;
        	}
			m_ndmthread.init(/*m_mdmflgs*/mdm);
		
		// Start Monitoring log
			m_ndmthread.start();
			
			// BootReceiver起動. DevicePolicyManagerを参照する必要があるため、onCreateからこちらに移行した
			mReceiver = new MDMBootReceiver(/*m_mdmflgs.GetDPM()m_DPM, /*m_mdmflgs.GetComponent()m_DeviceAdmin*/);
			IntentFilter filter = new IntentFilter();
			filter.addAction(Intent.ACTION_SCREEN_ON);
			filter.addAction(Intent.ACTION_SCREEN_OFF);
			registerReceiver(mReceiver, filter);
		
	//	Toast.makeText(this, "Start monitoring ...", Toast.LENGTH_SHORT).show();
		} catch (Exception e) {
			// 20131107
			// ログを見るとなぜかServiceがAndroidのシステム？から起動されることがあり、
			// MDMFlgsが無いため、NullPointerExceptionが発生してここに来ることがある。
			// その場合、BootReceiverも止まってしまうようなので、ここでMDMFlgsを作成して再実行が必要かも
			LogCtrl.getInstance().error("MDMService::onStart:Exception" + e.toString());
			
//			MDMFlgs mdm = new MDMFlgs();
//        	boolean bRet = mdm.ReadAndSetScepMdmInfo(this);
        	
//        	if (bRet == false) {
//        		return;
//        	}
        	
        	// 読み込んだ情報をMDM制御クラスに引き渡して、SacServiceを実行
//        	MDMControl MDM_ctrl = new MDMControl(this);
//        	MDM_ctrl.startService(mdm);
		}
	}
	
	@Override
	public void onDestroy() {
		LogCtrl.getInstance().info("MDMService: onDestory");

		// Stop Monitoring log
		m_ndmthread.stop();
		
	//	Toast.makeText(this, "Stop monitoring.", Toast.LENGTH_SHORT).show();
		this.stopSelf();
		super.onDestroy();
		try {
			unregisterReceiver(mReceiver);
		}catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Override
//	public IBinder onBind(Intent intent) {
		// TODO 自動生成されたメソッド・スタブ
//		return null;
//	}
	
	public IBinder onBind(Intent intent) {
		return binder;
	}
	
	private final IBinder binder = new Binder() {  
        @Override  
        protected boolean onTransact(int code, Parcel data, Parcel reply, int flags) throws RemoteException {  
            return super.onTransact(code, data, reply, flags);  
        }  
    }; 

}
