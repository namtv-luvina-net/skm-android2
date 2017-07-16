package jp.co.soliton.keymanager.scep.response;

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

import org.bouncycastle.cms.CMSException;

import jp.co.soliton.keymanager.LogCtrl;
import jp.co.soliton.keymanager.SKMApplication;
import jp.co.soliton.keymanager.scep.pkimessage.CertRep;
import jp.co.soliton.keymanager.scep.pkimessage.MessageType;
import jp.co.soliton.keymanager.scep.pkimessage.PkiMessage;
import jp.co.soliton.keymanager.scep.pkimessage.PkiStatus;
import jp.co.soliton.keymanager.scep.pkimessage.RecipientNonce;
import jp.co.soliton.keymanager.scep.pkimessage.SenderNonce;
import jp.co.soliton.keymanager.scep.pkimessage.TransactionId;
import jp.co.soliton.keymanager.scep.request.Request;
import jp.co.soliton.keymanager.scep.transaction.Transaction;

public class CertRepReponseHandler implements ResponseHandler<CertRep> {

	@Override
	public CertRep processResponse(HttpURLConnection connection, Request request) {
		CertRep certRep = new CertRep();
		final String contentType = connection.getContentType();

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
			TransactionId requestTransactionId = requestPkiMessage.getTransactionId();
			TransactionId transactionId = pkiMessage.getTransactionId();
			MessageType msgType = pkiMessage.getMessageType();
			SenderNonce senderNonce = requestPkiMessage.getSenderNonce();
			RecipientNonce recipientNonce = pkiMessage.getRecipientNonce();
			LogCtrl.getInstance().info("SCEP: " + pkiMessage.getPkiStatus().toString() + ", " + msgType.toString());
			LogCtrl.getInstance().info("SCEP: REQ:" + requestTransactionId.toString() + ", RES:" + transactionId.toString());
			LogCtrl.getInstance().debug("SCEP: " + senderNonce.toString() + ", " + recipientNonce.toString());

			if (msgType.getMessageType() != MessageType.Type.CertRep) {
				certRep.setPkiStatus(PkiStatus.Status.FAILURE);
				return certRep;
			}
			if (requestPkiMessage.getTransactionId().equals(pkiMessage.getTransactionId()) == false) {
				LogCtrl.getInstance().error("SCEP: TransactionId Mismatch");
				certRep.setPkiStatus(PkiStatus.Status.FAILURE);
				return certRep;
			}
			if (requestPkiMessage.getSenderNonce().equals(pkiMessage.getRecipientNonce()) == false) {
				LogCtrl.getInstance().error("SCEP: Nonce Mismatch");
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
			LogCtrl.getInstance().info("SCEP: Receive X509Certificate");
			LogCtrl.getInstance().debug("SCEP: Subject: " + cert.getSubjectDN().toString() + ", Issuer" + cert.getIssuerDN().toString());
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
