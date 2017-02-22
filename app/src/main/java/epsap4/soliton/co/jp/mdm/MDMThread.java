package epsap4.soliton.co.jp.mdm;

import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.util.Log;

import epsap4.soliton.co.jp.HttpConnectionCtrl;
import epsap4.soliton.co.jp.InformCtrl;
import epsap4.soliton.co.jp.LogCtrl;
import epsap4.soliton.co.jp.StringList;
import epsap4.soliton.co.jp.xmlparser.XmlKeyWord;
import epsap4.soliton.co.jp.xmlparser.XmlPullParserAided;

public class MDMThread {

	private MDMRunThread m_runthread;
	private boolean running = false;
	private MDMFlgs m_flgs;
	private Context context;
	
	DevicePolicyManager m_DPM;
    ComponentName m_DeviceAdmin;
	
	public MDMThread(Context ctx, DevicePolicyManager dpm, ComponentName admin) {
		// TODO 自動生成されたコンストラクター・スタブ
		m_runthread = new MDMRunThread();
		context = ctx;
		m_DPM = dpm;
		m_DeviceAdmin = admin;
	}
	
	public void init(MDMFlgs flg) { m_flgs = flg; }
	
	public void start() {
		if (m_runthread == null) {
			return;
		}
		running = true;
		m_runthread.start();
	}
	
	public void stop() {
		running = false;
	}

	private class MDMRunThread extends Thread {
		public void run() {
			Log.i("MDMRunThread::run", "Start.");

			while(running == true) {
				Log.i("MDMRunThread::run", "Sleep 30sec.");
				try {
					sleep(30000);
					
					
					// EPS-apへ問い合わせ
					InformCtrl inform = new InformCtrl();
					inform.SetURL(m_flgs.GetServerurl());
						
					String sendmsg = m_flgs.StatusMsg(StringList.m_strIdle);
					Log.i("MDMRunThread RTN MESSAGE", sendmsg);
					inform.SetMessage(sendmsg);
					
					// Idle送信
					HttpConnectionCtrl conn = new HttpConnectionCtrl(context);
					boolean ret = conn.RunHttpMDMConnection(inform);
					if (ret == false) {
						Log.e("MDMThread::MDMRunThread", "IDLE Error.");
						continue;
					}
					
					while(true) {
						Log.i("MDMThread::MDMRunThread:RTN", inform.GetRtn());
						
						// 解析して、CommandUUID or RequestTypeがなければcontinueするか？
						XmlPullParserAided aided = new XmlPullParserAided(context, inform.GetRtn(), 2);	// 最上位dictの階層は2になる
						
						aided.TakeApartMdmCommand(inform);
						
						XmlKeyWord xmlKeyword = aided.GetXmlKeyWord();
						String uuid = xmlKeyword.GetCmdUUID();
						if(uuid == null) {
							// コマンドが入っていない場合は抜けて、Idleからやり直す.
							Log.i("MDMThread::MDMRunThread", "Reply No Packet.");
							break;
						}
						
						// perseしたコマンドを解析して、結果をreplyする
						String str_replycmd = m_flgs.CmdRepliesMsg(aided, context, m_DPM, m_DeviceAdmin);
						boolean b_wipe = m_flgs.GetWipe();		// wipeフラグ
						
						// 結果がないときはbreak
						if(str_replycmd.length() == 0) break;
						
						// 結果を送信
						inform.SetMessage(str_replycmd);
						
						ret = conn.RunHttpMDMConnection(inform);
						
						// 送信後にwipe判定
						if(b_wipe == true) {
							// wipe実行. 
							Log.i("MDMThread::MDMRunThread", "Wipe Run!");
							MDMFlgs.RunWipe(m_DPM);
						}
						
						if (ret == false) {
							LogCtrl.Logger(LogCtrl.m_strError, "MDMThread::MDMRunThread " + "Reply Error.", context);
							break;
						}
					}
					
				} catch (InterruptedException e) {
					// TODO 自動生成された catch ブロック
					e.printStackTrace();
				}
			}

		}
	}
}
