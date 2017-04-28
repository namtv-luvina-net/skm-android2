package jp.co.soliton.keymanager.asynctask;

import android.app.Activity;
import android.os.AsyncTask;
import jp.co.soliton.keymanager.HttpConnectionCtrl;
import jp.co.soliton.keymanager.InformCtrl;
import jp.co.soliton.keymanager.LogCtrl;
import jp.co.soliton.keymanager.R;

import static jp.co.soliton.keymanager.fragment.InputBasePageFragment.*;

/**
 * Created by nguyenducdat on 4/28/2017.
 */

public class ConnectApplyTask extends AsyncTask<Void, Void, Boolean> {

	public interface EndConnection{
		void endConnect(Boolean result, InformCtrl m_InformCtrl, int m_nErroType);
	}

	Activity activity;
	InformCtrl m_InformCtrl;
	int m_nErroType;
	EndConnection endConnection;
	public ConnectApplyTask(Activity activity, InformCtrl m_InformCtrl, int m_nErroType, EndConnection endConnection) {
		this.activity = activity;
		this.m_InformCtrl = m_InformCtrl;
		this.m_nErroType = m_nErroType;
		this.endConnection = endConnection;
	}

	@Override
	protected Boolean doInBackground(Void... params) {
		LogCtrl logCtrlAsyncTask = LogCtrl.getInstance(activity);
		HttpConnectionCtrl conn = new HttpConnectionCtrl(activity);
		//execute send request to server
		boolean ret = conn.RunHttpProbeHostCerConnection(m_InformCtrl);
		//check result return from server
		if (ret == false) {
			logCtrlAsyncTask.loggerError("ConnectApplyTask " + "Network error");
			m_nErroType = ERR_NETWORK;
			return false;
		}
		// ログイン結果
		if (m_InformCtrl.GetRtn().startsWith(activity.getText(R.string.Forbidden).toString())) {
			logCtrlAsyncTask.loggerError("ConnectApplyTask  " + " Forbidden.");
			m_nErroType = ERR_FORBIDDEN;
			return false;
		} else if (m_InformCtrl.GetRtn().startsWith(activity.getText(R.string.Unauthorized).toString())) {
			logCtrlAsyncTask.loggerError("ConnectApplyTask  " + "Unauthorized.");
			m_nErroType = ERR_UNAUTHORIZED;
			return false;
		} else if (m_InformCtrl.GetRtn().startsWith(activity.getText(R.string.ERR).toString())) {
			logCtrlAsyncTask.loggerError("ConnectApplyTask  " + "ERR:");
			m_nErroType = ERR_COLON;
			return false;
		}
		if (m_InformCtrl.GetRtn().startsWith(activity.getText(R.string.not_installed_ca).toString())) {
			logCtrlAsyncTask.loggerError("ConnectApplyTask  " + activity.getText(R.string.not_installed_ca));
			m_nErroType = NOT_INSTALL_CA;
		} else {
			m_nErroType = SUCCESSFUL;
		}
		return ret;
	}

	@Override
	protected void onPostExecute(Boolean result) {
		super.onPostExecute(result);
		this.endConnection.endConnect(result, m_InformCtrl, m_nErroType);
	}
}