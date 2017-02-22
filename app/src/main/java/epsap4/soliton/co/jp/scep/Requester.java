package epsap4.soliton.co.jp.scep;

import android.util.Log;

import org.bouncycastle.jce.PKCS10CertificationRequest;

import java.net.MalformedURLException;
import java.net.URL;
import java.security.PrivateKey;
import java.security.cert.CertStore;
import java.security.cert.X509Certificate;

import epsap4.soliton.co.jp.scep.pkimessage.CertRep;
import epsap4.soliton.co.jp.scep.request.CertificateEnrollmentRequest;
import epsap4.soliton.co.jp.scep.request.GetCACertificateRequest;
import epsap4.soliton.co.jp.scep.response.CertRepReponseHandler;
import epsap4.soliton.co.jp.scep.response.GetCACertificateResponseHandler;
import epsap4.soliton.co.jp.scep.transaction.Transaction;
import epsap4.soliton.co.jp.scep.transaction.TransactionException;

//import jp.co.soliton.android.scep.RequesterException;

public class Requester {
	public Requester() {
	}

	public CertStore getCACertificate(String url) throws RequesterException {
		try {
			return getCACertificate(new URL(url));
		} catch (MalformedURLException e) {
			throw new RequesterException(e.getMessage());
		}
	}
	
	public CertStore getCACertificate(URL url) throws RequesterException {
		Log.d("Requester", "getCACertificate start ");
		try {
			Transaction<CertStore> transaction = new Transaction<CertStore>();
			GetCACertificateRequest request = new GetCACertificateRequest(url);
			CertStore cACertificateStore = transaction.start(request, new GetCACertificateResponseHandler());
			if (cACertificateStore == null) {
				throw new RequesterException("No CA Certificcate");
			}
			return cACertificateStore;
		} catch (TransactionException e) {
			throw new RequesterException(e.getMessage());
		}
	}
	
	public CertRep certificateEnrollment(
			String url,
			PKCS10CertificationRequest certificateSigningRequest,
			X509Certificate myCertificate,
			PrivateKey privateKey,
			CertStore cACertificateStore) throws RequesterException {
		
		try {
			return certificateEnrollment(
					new URL(url),
					certificateSigningRequest,
					myCertificate,
					privateKey,
					cACertificateStore);
		} catch (MalformedURLException e) {
			throw new RequesterException(e.getMessage());
		}
	}
	
	public CertRep certificateEnrollment(
			URL url,
			PKCS10CertificationRequest certificateSigningRequest,
			X509Certificate myCertificate,
			PrivateKey privateKey,
			CertStore cACertificateStore) throws RequesterException {
		
		CertificateEnrollmentRequest request = new CertificateEnrollmentRequest(url);
		request.setCertificateSigningRequest(certificateSigningRequest);
		request.setMyCertificate(myCertificate);
		request.setPrivateKey(privateKey);
		request.setCACertificateStore(cACertificateStore);
		try {
			Transaction<CertRep> transaction = new Transaction<CertRep>();
			CertRepReponseHandler certRepResponseHandler = new CertRepReponseHandler();
			return transaction.start(request, certRepResponseHandler);
		} catch (TransactionException e) {
			// TODO Auto-generated catch block
			throw new RequesterException(e.getMessage());
		}
	}
}
