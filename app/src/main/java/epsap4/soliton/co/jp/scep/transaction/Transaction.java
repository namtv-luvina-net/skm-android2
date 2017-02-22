package epsap4.soliton.co.jp.scep.transaction;

import epsap4.soliton.co.jp.scep.request.Request;
import epsap4.soliton.co.jp.scep.response.ResponseHandler;

public class Transaction<T> {
	public T start(Request request, ResponseHandler<T> responseHandler) throws TransactionException {

		Transport<T> transport = new Transport<T>();
		transport.setResponseHandler(responseHandler);

		return transport.sendRequest(request);
	}
}
