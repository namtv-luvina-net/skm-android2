package jp.co.soliton.keymanager.scep;

public class RequesterException extends Exception {
	private static final long serialVersionUID = 2751394910143005476L;

	public RequesterException() {
	}

	public RequesterException(String detailMessage) {
		super(detailMessage);
	}	
}
