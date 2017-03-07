package jp.co.soliton.keymanager.scep.response;

import java.net.HttpURLConnection;

import jp.co.soliton.keymanager.scep.request.Request;

public interface ResponseHandler<T> {
	public T processResponse(HttpURLConnection connection, Request request);
}
