package epsap4.soliton.co.jp;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Binder;
import android.os.IBinder;
import android.os.Parcel;
import android.os.RemoteException;
import android.util.Log;
//import android.util.Log;

/** BlockService class **/
public class SacService extends Service {
	private LogMonitor m_LogMonitor = null;
	
	
	private RestrictionsFlgs m_restriction;	// yoshim add...
	BroadcastReceiver mReceiver; 			// yoshim add...
	
	@Override
	public void onCreate() {
		// Initialize Monitoring log
		m_LogMonitor  = new LogMonitor();
		m_LogMonitor.init(this);
		
		
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
		mReceiver = new BootReceiver();
		IntentFilter filter = new IntentFilter();
		filter.addAction(Intent.ACTION_SCREEN_ON);
		filter.addAction(Intent.ACTION_SCREEN_OFF);
		registerReceiver(mReceiver, filter);
		
		super.onCreate();
		
		
	}
	
	@Override
	public void onStart(Intent intent, int startId) {
		
		Log.i("SacService", "onStart");
		try {
		m_restriction = (RestrictionsFlgs)intent.getSerializableExtra("restrictionlabel");
		m_LogMonitor.init_rest(m_restriction);
		
		// Start Monitoring log
		m_LogMonitor.start();
		
	//	Toast.makeText(this, "Start monitoring ...", Toast.LENGTH_SHORT).show();
		} catch (Exception e) {
			Log.e("SacService::onStart", e.toString());
		}
	}
	
	@Override
	public void onDestroy() {
		// Stop Monitoring log
		m_LogMonitor.stop();
		
	//	Toast.makeText(this, "Stop monitoring.", Toast.LENGTH_SHORT).show();
		this.stopSelf();
		super.onDestroy();
		
		unregisterReceiver(mReceiver);
	}
	
	@Override
	public IBinder onBind(Intent intent) {
		return binder;
	}
	
	private final IBinder binder = new Binder() {  
        @Override  
        protected boolean onTransact(int code, Parcel data, Parcel reply, int flags) throws RemoteException {  
            return super.onTransact(code, data, reply, flags);  
        }  
    }; 
};