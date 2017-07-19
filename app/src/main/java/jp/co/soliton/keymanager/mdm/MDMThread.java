package jp.co.soliton.keymanager.mdm;

import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import jp.co.soliton.keymanager.HttpConnectionCtrl;
import jp.co.soliton.keymanager.InformCtrl;
import jp.co.soliton.keymanager.LogCtrl;
import jp.co.soliton.keymanager.StringList;
import jp.co.soliton.keymanager.xmlparser.XmlKeyWord;
import jp.co.soliton.keymanager.xmlparser.XmlPullParserAided;

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
			LogCtrl.getInstance().info("MDMThread: Start");

			while(running == true) {
				LogCtrl.getInstance().debug("MDMThread: 30sec intervals");
				try {
					sleep(30000);

					// EPS-apへ問い合わせ
					InformCtrl inform = new InformCtrl();
					inform.SetURL(m_flgs.GetServerurl());

					LogCtrl.getInstance().debug("MDMThread: Send message");

					String sendmsg = m_flgs.StatusMsg(StringList.m_strIdle);		
					inform.SetMessage(sendmsg);
					
					// Idle送信
					HttpConnectionCtrl conn = new HttpConnectionCtrl(context);
					boolean ret = conn.RunHttpMDMConnection(inform);
					if (ret == false) {
						LogCtrl.getInstance().info("MDMThread: Idle (No commands)");
						continue;
					}
					
					while(true) {

						// 解析して、CommandUUID or RequestTypeがなければcontinueするか？
						XmlPullParserAided aided = new XmlPullParserAided(context, inform.GetRtn(), 2);	// 最上位dictの階層は2になる
						
						aided.TakeApartMdmCommand(inform);
						
						XmlKeyWord xmlKeyword = aided.GetXmlKeyWord();
						String uuid = xmlKeyword.GetCmdUUID();
						if(uuid == null) {
							// コマンドが入っていない場合は抜けて、Idleからやり直す.
							LogCtrl.getInstance().debug("MDMThread: No commands");
							break;
						}

						LogCtrl.getInstance().info("MDMThread: Receive commands");
						
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
							LogCtrl.getInstance().info("MDMThread: Run wipe");
							MDMFlgs.RunWipe(m_DPM);
						}
						
						if (ret == false) {
							break;
						}
					}
					
				} catch (InterruptedException e) {
					LogCtrl.getInstance().error("MDMThread::MDMRunThread:InterruptedException: " + e.toString());
				}
			}

			LogCtrl.getInstance().info("MDMThread: Stop");
		}
	}
}
