package jp.co.soliton.keymanager.scep.transaction;

import jp.co.soliton.keymanager.scep.request.Request;
import jp.co.soliton.keymanager.scep.response.ResponseHandler;

public class Transaction<T> {
	public T start(Request request, ResponseHandler<T> responseHandler) throws TransactionException {

		Transport<T> transport = new Transport<T>();
		transport.setResponseHandler(responseHandler);

		return transport.sendRequest(request);
	}
}
