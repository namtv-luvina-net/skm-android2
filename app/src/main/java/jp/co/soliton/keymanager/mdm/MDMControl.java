package jp.co.soliton.keymanager.mdm;

import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import jp.co.soliton.keymanager.HttpConnectionCtrl;
import jp.co.soliton.keymanager.InformCtrl;
import jp.co.soliton.keymanager.LogCtrl;
import jp.co.soliton.keymanager.ServiceInfo;
import jp.co.soliton.keymanager.StringList;
import jp.co.soliton.keymanager.xmlparser.XmlDictionary;
import jp.co.soliton.keymanager.xmlparser.XmlStringData;

public class MDMControl {

	private Context context;
	private MDMFlgs m_flgs;
	
	public MDMControl(Context con, String strapid) {
		LogCtrl.getInstance().info("MDMControl: Initialize");
    	context = con;
    	m_flgs = new MDMFlgs();
    	m_flgs.SetUDID(/*XmlPullParserAided.GetUDID(context)*/strapid);
     	
    	// SacServiceが起動していたら一旦止める.
		boolean running = ServiceInfo.IsRunning(context, ".mdm.MDMService");
		if(running) {
			LogCtrl.getInstance().info("MDMControl: Stop service");
			Intent intent = new Intent(context, MDMService.class);
	    	
			context.stopService(intent);
		}
    }
	
	// BootReceiver経由で実行.
	// 保存ファイルからMDMFlgsメンバに値を割り当てて、SrartServiceに渡す。
	// コンストラクタで作成したMDMFlgsインスタンスやUDIDは上書きされる
	public void startService(MDMFlgs flgs) {
		// MDMServiceのIntentを作成してサービス開始.

		LogCtrl.getInstance().info("MDMControl: Start service");
		m_flgs = flgs;
		
		Intent intent = new Intent(context, MDMService.class);
		
		intent.putExtra(StringList.m_str_MdmFlgs, /*this.getClass()*/m_flgs);
		
		if (context.startService(intent) != null) {
        	//bRet = true;
        }
		
	}
	
	// チェックイン後に実行
	// MDMControl::SetMDMmemberでプロファイルで取得したMDM情報をMDMFlagsに設定している
	// UDID(APID)はコンストラクタで設定
	public void startService() {
		// MDMServiceのIntentを作成してサービス開始.
		
		LogCtrl.getInstance().info("MDMControl: Start service2");
		
		Intent intent = new Intent(context, MDMService.class);
		
		intent.putExtra(StringList.m_str_MdmFlgs, m_flgs);
		
		if (context.startService(intent) != null) {
        	//bRet = true;
        }
		
	}
	
	public boolean CheckIn(boolean chkin) {
		LogCtrl.getInstance().info("MDMControl: Check-in");

		// チェックアウトのときはm_flgsに値が入っていないので別に設定しないと...
		boolean rtn = true;
		if(chkin == true) m_flgs.WriteScepMdmInfo(context);
		
		InformCtrl inform = new InformCtrl();

		String sendmsg = m_flgs.CheckinoutMsg(chkin);		
		inform.SetMessage(sendmsg);
		
		inform.SetURL(m_flgs.GetCheckin());
		
		HttpConnectionCtrl conn = new HttpConnectionCtrl(context);	
		rtn = conn.RunHttpMDMConnection(inform);

		// チェックイン/アウトの場合、
		int ret_code = inform.GetResponseCode();
		if(ret_code == StringList.RES_200_OK) {
			LogCtrl.getInstance().info("MDMControl: Check-in successful");
			rtn = true;
		}
		else {
			LogCtrl.getInstance().error("MDMControl: Check-in failed");
		}
		
		return rtn;
	}

	public boolean TokenUpdate() {
		LogCtrl.getInstance().info("MDMControl: Token update");
		boolean rtn;
		
		InformCtrl inform = new InformCtrl();

		String sendmsg = m_flgs.TokenUpdateMsg();
		
		inform.SetMessage(sendmsg);
		
		inform.SetURL(m_flgs.GetCheckin());
		
		HttpConnectionCtrl conn = new HttpConnectionCtrl(context);	
		rtn = conn.RunHttpMDMConnection(inform);
		
		// 
		int ret_code = inform.GetResponseCode();
		if(ret_code == StringList.RES_200_OK) {
			LogCtrl.getInstance().info("MDMControl: Token update successful");
			rtn = true;
		}
		else {
			LogCtrl.getInstance().error("MDMControl: Token update failed");
		}
		
		return rtn;
	}
	
	
	public void SetMDMmember(XmlDictionary dict) {
		List<XmlStringData> str_list;
		str_list = dict.GetArrayString();
		for(int i = 0; str_list.size() > i; i++){
			// config情報に従って、処理を行う.
			XmlStringData p_data = str_list.get(i);
			SetConfigrationChild(p_data);
		}
	}
	
	private void SetConfigrationChild(XmlStringData p_data) {
		String strKeyName = p_data.GetKeyName();	// キー名
		int    i_type = p_data.GetType();		// 要素タイプ(string:1, data=2, date=3, real=4, integer=5, true=6, false=7)
		String strData = p_data.GetData();		// 要素

		boolean b_type = true;
		if(i_type == 7) b_type = false;
		
		if(strKeyName.equalsIgnoreCase(StringList.m_str_mdm_server)) {	// ServerURL
			m_flgs.SetServerurl(strData);
			LogCtrl.getInstance().debug("MDMControl: Server=" + strData);
		} else if(strKeyName.equalsIgnoreCase(StringList.m_str_mdm_checkin)) {	// チェックインURL
			m_flgs.SetCheckin(strData);
			LogCtrl.getInstance().debug("MDMControl: Check-in URL=" + strData);
		} else if(strKeyName.equalsIgnoreCase(StringList.m_str_topic)) {
			m_flgs.SetTopic(strData);
			LogCtrl.getInstance().debug("MDMControl: Topic=" + strData);
		} else if(strKeyName.equalsIgnoreCase(StringList.m_str_AccessRights)) {	//
			m_flgs.SetAccessRight(Integer.parseInt(strData));
			LogCtrl.getInstance().info("MDMControl: AccessRights=" + strData);
		} else if(strKeyName.equalsIgnoreCase(StringList.m_str_CheckOutRemoved)) {
			m_flgs.SetCheckOut(b_type);
		}
	}

	public static class CheckOutMdmTask extends AsyncTask<Object, Object, Void> {
		private Context context;
		private CheckOutListener checkOutListener;

		public CheckOutMdmTask(Context context, CheckOutListener checkOutListener) {
			this.context = context;
			this.checkOutListener = checkOutListener;
		}

		@Override
		protected Void doInBackground(Object... params) {
			String filedir = "/data/data/" + context.getPackageName() + "/files/";

			java.io.File filename_mdm = new java.io.File(filedir + StringList.m_strMdmOutputFile);
			if(filename_mdm.exists()) {
				MDMFlgs mdm = new MDMFlgs();
				boolean bRet = mdm.ReadAndSetScepMdmInfo(context);
				if(mdm.GetCheckOut()) {
					CheckOut(mdm, context);
				}
				MDMControl mdmctrl = new MDMControl(context, mdm.GetUDID());	// この時点でサービスを止める
				filename_mdm.delete();
			}
			return null;
		}

		private boolean CheckOut(MDMFlgs flgs, Context cont) {
			LogCtrl.getInstance().info("MDMControl: Check-out");
			boolean rtn;

			InformCtrl inform = new InformCtrl();

			String sendmsg = flgs.CheckinoutMsg(false);
			inform.SetMessage(sendmsg);

			inform.SetURL(flgs.GetCheckin());

			HttpConnectionCtrl conn = new HttpConnectionCtrl(cont);
			rtn = conn.RunHttpMDMConnection(inform);

			int ret_code = inform.GetResponseCode();
			if(ret_code == StringList.RES_200_OK) {
				LogCtrl.getInstance().info("MDMControl: Check-out successful");
				rtn = true;
			}
			else {
				LogCtrl.getInstance().error("MDMControl: Check-out failed");
			}

			return rtn;
		}
		@Override
		protected void onPostExecute(Void aVoid) {
			super.onPostExecute(aVoid);
			checkOutListener.checkOutComplete();
		}
	}

	public interface CheckOutListener{
		void checkOutComplete();
	}
}
