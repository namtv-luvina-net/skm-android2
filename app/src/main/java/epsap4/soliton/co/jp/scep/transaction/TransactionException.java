package epsap4.soliton.co.jp.scep.transaction;

public class TransactionException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1120555858762068037L;

	public TransactionException() {
	}

	public TransactionException(String detailMessage) {
		super(detailMessage);
	}	
}
