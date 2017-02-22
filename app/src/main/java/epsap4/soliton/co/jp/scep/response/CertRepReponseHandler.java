package epsap4.soliton.co.jp.scep.response;

import org.bouncycastle.cms.CMSException;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.cert.CertStore;
import java.security.cert.CertStoreException;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.util.Collection;
import java.util.Iterator;

import epsap4.soliton.co.jp.scep.pkimessage.CertRep;
import epsap4.soliton.co.jp.scep.pkimessage.MessageType;
import epsap4.soliton.co.jp.scep.pkimessage.PkiMessage;
import epsap4.soliton.co.jp.scep.pkimessage.PkiStatus;
import epsap4.soliton.co.jp.scep.request.Request;

public class CertRepReponseHandler implements ResponseHandler<CertRep> {
	
	@Override
	public CertRep processResponse(HttpURLConnection connection, Request request) {
		CertRep certRep = new CertRep();
		final String contentType = connection.getContentType();

		System.out.println("ContentType : " + contentType);
		if (contentType.equals("application/x-pki-message") == false) {
			certRep.setPkiStatus(PkiStatus.Status.FAILURE);
			return certRep;
		}
		
		PkiMessage requestPkiMessage = request.getPkiMessage();

		InputStream responseStream;
		PkiMessage pkiMessage;
		try {
			responseStream = connection.getInputStream();
			pkiMessage = new PkiMessage(responseStream);
			pkiMessage.verify();
		} catch (IOException e1) {
			e1.printStackTrace();
			certRep.setPkiStatus(PkiStatus.Status.FAILURE);
			return certRep;
		} catch (CMSException e) {
			e.printStackTrace();
			certRep.setPkiStatus(PkiStatus.Status.FAILURE);
			return certRep;
		}
		
		try {
			if (pkiMessage.getMessageType().getMessageType() != MessageType.Type.CertRep) {
				certRep.setPkiStatus(PkiStatus.Status.FAILURE);
				return certRep;
			}
			if (requestPkiMessage.getTransactionId().equals(pkiMessage.getTransactionId()) == false) {
				certRep.setPkiStatus(PkiStatus.Status.FAILURE);
				return certRep;
			}
			if (requestPkiMessage.getSenderNonce().equals(pkiMessage.getRecipientNonce()) == false) {
				certRep.setPkiStatus(PkiStatus.Status.FAILURE);
				return certRep;
			}
			certRep.setPkiStatus(pkiMessage.getPkiStatus().getStatus());
			if (pkiMessage.getPkiStatus().getStatus() != PkiStatus.Status.SUCCESS) {
				return certRep;
			}
		} catch (CMSException e2) {
			e2.printStackTrace();
			certRep.setPkiStatus(PkiStatus.Status.FAILURE);
			return certRep;
		}
		
		
		// pkcsPKIEnvelope
		try {
			@SuppressWarnings("deprecation")
			CertStore certStore = pkiMessage.getCertificatesAndCRLs("Collection", "BC");
			Collection<? extends Certificate> certificateCollection;
			certificateCollection = (Collection<? extends Certificate>) certStore.getCertificates(null);
			Iterator<? extends Certificate> it = certificateCollection.iterator();
			X509Certificate cert = (X509Certificate) it.next();
			System.out.println("Subject : " + cert.getSubjectDN().toString());
			System.out.println("Issure : " + cert.getIssuerDN().toString());
			certRep.setCertificate(cert);
			return certRep;
		} catch (NoSuchAlgorithmException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (NoSuchProviderException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (CMSException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (CertStoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		certRep.setPkiStatus(PkiStatus.Status.FAILURE);
		return certRep;
	}
}
