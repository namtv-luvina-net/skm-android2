package jp.co.soliton.keymanager.scep.response;

import jp.co.soliton.keymanager.scep.request.Request;

import java.net.HttpURLConnection;

public interface ResponseHandler<T> {
	T processResponse(HttpURLConnection connection, Request request);
}
