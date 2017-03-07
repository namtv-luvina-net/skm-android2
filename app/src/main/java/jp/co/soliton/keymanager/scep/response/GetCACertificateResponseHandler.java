package jp.co.soliton.keymanager.scep.response;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.cert.CertStore;
import java.security.cert.CertStoreParameters;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.CollectionCertStoreParameters;
import java.security.cert.X509Certificate;
import java.util.Collection;
import java.util.Collections;

import jp.co.soliton.keymanager.scep.request.GetCACertificateRequest;
import jp.co.soliton.keymanager.scep.request.Request;

import org.bouncycastle.cms.CMSException;
import org.bouncycastle.cms.CMSSignedData;

public class GetCACertificateResponseHandler implements ResponseHandler<CertStore> {

	@SuppressWarnings("deprecation")
	@Override
	public CertStore processResponse(HttpURLConnection connection, Request request) {
		final String contentType = connection.getContentType();
		@SuppressWarnings("unused")
		final GetCACertificateRequest getCACertificateRequest = (GetCACertificateRequest) request;

		InputStream responseStream;
		try {
			responseStream = connection.getInputStream();
		} catch (IOException e1) {
			e1.printStackTrace();
			return null;
		}
		
		if (contentType.equals("application/x-x509-ca-cert") == true) {
			// Message is CA Certificate
			try {
				CertificateFactory certificateFactory = 
						CertificateFactory.getInstance("X.509", "BC");
				X509Certificate cACertificate =
						(X509Certificate) certificateFactory.generateCertificate(responseStream);
				Collection<X509Certificate> cACertificateCollection =
						Collections.singleton(cACertificate);
				CertStoreParameters certStoreParameter =
						new CollectionCertStoreParameters(cACertificateCollection);
				return CertStore.getInstance("Collection", certStoreParameter);
			} catch (CertificateException e) {
				e.printStackTrace();
			} catch (InvalidAlgorithmParameterException e) {
				e.printStackTrace();
			} catch (NoSuchAlgorithmException e) {
				e.printStackTrace();
			} catch (NoSuchProviderException e) {
				e.printStackTrace();
			}
		} else if (contentType.equals("application/x-x509-ca-ra-cert") == true) {
			try {
				CMSSignedData signedData = new CMSSignedData(responseStream);
				return signedData.getCertificatesAndCRLs("Collection", "BC");
			} catch (NoSuchAlgorithmException e) {
				e.printStackTrace();
			} catch (NoSuchProviderException e) {
				e.printStackTrace();
			} catch (CMSException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return null;
	}
}
