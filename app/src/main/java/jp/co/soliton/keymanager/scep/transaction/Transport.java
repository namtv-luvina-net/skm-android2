package jp.co.soliton.keymanager.scep.transaction;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import android.util.Log;

import javax.net.ssl.HttpsURLConnection;

import jp.co.soliton.keymanager.scep.request.Request;
import jp.co.soliton.keymanager.scep.response.ResponseHandler;

public class Transport<T> {
	private ResponseHandler<T> responseHandler;
	
	public T sendRequest(Request request) {
		Log.d("Transport", "sendRequest start ");
		try {
			URL scepUrl = request.makeUrl();
			HttpURLConnection scepConnection;// = (HttpURLConnection) scepUrl.openConnection();
			if(scepUrl.getProtocol().toLowerCase().equals("https")) {
				scepConnection = (HttpsURLConnection)scepUrl.openConnection();
			} else {
				scepConnection = (HttpURLConnection) scepUrl.openConnection();
			}
			if ((scepConnection.getResponseCode() != HttpURLConnection.HTTP_OK) &&
					(scepConnection.getResponseCode() != HttpsURLConnection.HTTP_OK)) {
				System.out.println(scepConnection.getResponseCode());
				scepConnection.disconnect();
				Log.d("Transport", "sendRequest Error 01. ");
				return null;
			}
			Log.d("Transport", "sendRequest TRACE1 ");
			return getResponseHandler().processResponse(scepConnection, request);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	public ResponseHandler<T> getResponseHandler() {
		return responseHandler;
	}

	public  void setResponseHandler(ResponseHandler<T> responseHandler) {
		this.responseHandler = responseHandler;
	}

}
