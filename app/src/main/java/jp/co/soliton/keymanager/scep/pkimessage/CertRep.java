package jp.co.soliton.keymanager.scep.pkimessage;

import java.security.cert.X509Certificate;

public class CertRep {
	@SuppressWarnings("unused")
	private TransactionId transactionId;
	@SuppressWarnings("unused")
	private Nonce senderNonce;
	@SuppressWarnings("unused")
	private Nonce recipientNonce;
	private X509Certificate certificate;
	
	final private PkiStatus pkiStatus = new PkiStatus();

	public PkiStatus getPkiStatus() {
		return pkiStatus;
	}

	public void setPkiStatus(PkiStatus.Status pkiStatus) {
		this.pkiStatus.setPkiStatus(pkiStatus);
	}

	public X509Certificate getCertificate() {
		return certificate;
	}

	public void setCertificate(X509Certificate certificate) {
		this.certificate = certificate;
	}
}
