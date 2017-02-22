package epsap4.soliton.co.jp.scep.request;

import org.bouncycastle.jce.PKCS10CertificationRequest;
import org.bouncycastle.util.encoders.Base64;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLEncoder;
import java.security.PrivateKey;
import java.security.cert.CertStore;
import java.security.cert.CertStoreException;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.util.Collection;

import epsap4.soliton.co.jp.scep.pkimessage.MessageType;
import epsap4.soliton.co.jp.scep.pkimessage.PkcsPKIEnvelope;
import epsap4.soliton.co.jp.scep.pkimessage.PkcsPKIEnvelopeGenerator;
import epsap4.soliton.co.jp.scep.pkimessage.PkiMessageGenerator;

public class CertificateEnrollmentRequest extends Request {
	private PKCS10CertificationRequest certificateSigningRequest;
	private X509Certificate myCertificate;
	private PrivateKey privateKey;
	private CertStore cACertificateStore;
	
	public CertificateEnrollmentRequest(URL url) {
		super(url, Operation.PKIOperation);
	}

	@Override
	public String getMessage() {
		Collection<? extends Certificate> cACertificateCollection;
		try {
			cACertificateCollection = cACertificateStore.getCertificates(null);
		} catch (CertStoreException e) {
			e.printStackTrace();
			return null;
		}
		X509Certificate[] cACertificates =
				cACertificateCollection.toArray(new X509Certificate[cACertificateCollection.size()]);
		// Make pkcsPKIEnvelope
		PkcsPKIEnvelope pkcsPKIEnvelope =
				PkcsPKIEnvelopeGenerator.generate(
						certificateSigningRequest,
						cACertificates[0]);

		// Make pkiMessage
		setPkiMessage(PkiMessageGenerator.generator(
				new MessageType(MessageType.Type.PKCSReq),
				pkcsPKIEnvelope,
				myCertificate,
				privateKey));

		String message;
		try {
			message = URLEncoder.encode(
					new String(Base64.encode(getPkiMessage().getEncoded())), "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
		return message;
	}

	public PKCS10CertificationRequest getCertificateSigningRequest() {
		return certificateSigningRequest;
	}

	public void setCertificateSigningRequest(PKCS10CertificationRequest certificateSigningRequest) {
		this.certificateSigningRequest = certificateSigningRequest;
	}

	public X509Certificate getMyCertificate() {
		return myCertificate;
	}

	public void setMyCertificate(X509Certificate myCertificate) {
		this.myCertificate = myCertificate;
	}

	public PrivateKey getPrivateKey() {
		return privateKey;
	}

	public void setPrivateKey(PrivateKey privateKey) {
		this.privateKey = privateKey;
	}

	public CertStore getCACertificateStore() {
		return cACertificateStore;
	}

	public void setCACertificateStore(CertStore cACertificateStore) {
		this.cACertificateStore = cACertificateStore;
	}

}
