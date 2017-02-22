package epsap4.soliton.co.jp.scep.response;

import java.net.HttpURLConnection;

import epsap4.soliton.co.jp.scep.request.Request;

public interface ResponseHandler<T> {
	public T processResponse(HttpURLConnection connection, Request request);
}
