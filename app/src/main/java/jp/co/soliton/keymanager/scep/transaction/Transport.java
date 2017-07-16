package jp.co.soliton.keymanager.scep.transaction;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import android.util.Log;

import javax.net.ssl.HttpsURLConnection;

import jp.co.soliton.keymanager.LogCtrl;
import jp.co.soliton.keymanager.SKMApplication;
import jp.co.soliton.keymanager.scep.request.Request;
import jp.co.soliton.keymanager.scep.response.ResponseHandler;

public class Transport<T> {
	private ResponseHandler<T> responseHandler;
	
	public T sendRequest(Request request) {
		try {
			URL scepUrl = request.makeUrl();

			LogCtrl.getInstance().info("SCEP: Send Request");
			LogCtrl.getInstance().debug(scepUrl.toString());

			HttpURLConnection scepConnection;// = (HttpURLConnection) scepUrl.openConnection();
			if(scepUrl.getProtocol().toLowerCase().equals("https")) {
				scepConnection = (HttpsURLConnection)scepUrl.openConnection();
			} else {
				scepConnection = (HttpURLConnection) scepUrl.openConnection();
			}

			int responseCode = scepConnection.getResponseCode();

			LogCtrl.getInstance().info("SCEP: Receive Response " + Integer.toString(responseCode));
			LogCtrl.getInstance().debug(getAllHeaderFieldValues(scepConnection.getHeaderFields()));

			if (responseCode != HttpURLConnection.HTTP_OK) {
				scepConnection.disconnect();
				return null;
			}
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

	private String getAllHeaderFieldValues(Map headers) {
		Iterator headerIt = headers.keySet().iterator();
		String header = "{\r\n";
		while(headerIt.hasNext()){
			String headerKey = (String)headerIt.next();
			if (headerKey == null) {
				continue;
			}
			final List valueList = (List)headers.get(headerKey);
			final StringBuilder values = new StringBuilder();
			for (Object value : valueList) {
				values.append(value + ", ");
			}
			header += "  " + headerKey + ": " + values + "\r\n";
		}
		header += "}";
		return header;
	}
}
