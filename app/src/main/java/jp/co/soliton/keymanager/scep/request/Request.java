package jp.co.soliton.keymanager.scep.request;

import jp.co.soliton.keymanager.scep.pkimessage.PkiMessage;

import java.net.MalformedURLException;
import java.net.URL;

public abstract class Request {
	private URL url = null;
	private Operation operation = null;
	private PkiMessage pkiMessage;

	public Request(URL url, Operation operation) {
		this.setUrl(url);
		this.setOperation(operation);
	}

	public Operation getOperation() {
		return operation;
	}

	public void setOperation(Operation operation) {
		this.operation = operation;
	}
	

	public URL makeUrl() throws MalformedURLException {
		String query = "?operation=" + this.getOperation().name() + "&message=";
		String message = getMessage();
		if (message != null) {
			query += message;
		}
		
		return new URL(getUrl().toExternalForm() + query);
	}

	public URL getUrl() {
		return url;
	}

	public void setUrl(URL url) {
		this.url = url;
	}

	public abstract String getMessage();

	@Override
	public String toString() {
		if (getMessage() != null) {
			return getOperation().name() + "(" + getMessage() + ")";
		}
		
		return getOperation().name();
	}

	public PkiMessage getPkiMessage() {
		return pkiMessage;
	}

	public void setPkiMessage(PkiMessage pkiMessage) {
		this.pkiMessage = pkiMessage;
	}
}
