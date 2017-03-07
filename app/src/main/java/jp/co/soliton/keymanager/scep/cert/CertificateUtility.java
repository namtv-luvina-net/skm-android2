package jp.co.soliton.keymanager.scep.cert;


import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;
import java.security.SignatureException;
import java.security.cert.CertStore;
import java.security.cert.CertStoreException;
import java.security.cert.Certificate;
import java.security.cert.CertificateEncodingException;
import java.security.cert.X509Certificate;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.Vector;

import org.bouncycastle.asn1.ASN1Set;
import org.bouncycastle.asn1.DEREncodable;
import org.bouncycastle.asn1.DERObjectIdentifier;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.DERPrintableString;
import org.bouncycastle.asn1.DERSet;
import org.bouncycastle.asn1.cms.Attribute;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.x509.BasicConstraints;
import org.bouncycastle.asn1.x509.ExtendedKeyUsage;
import org.bouncycastle.asn1.x509.GeneralName;
import org.bouncycastle.asn1.x509.GeneralNames;
import org.bouncycastle.asn1.x509.KeyPurposeId;
import org.bouncycastle.asn1.x509.KeyUsage;
import org.bouncycastle.asn1.x509.X509Extension;
import org.bouncycastle.asn1.x509.X509Extensions;
import org.bouncycastle.asn1.x509.X509Name;
import org.bouncycastle.jce.PKCS10CertificationRequest;
import org.bouncycastle.x509.X509V3CertificateGenerator;

import android.app.Activity;
import android.content.Intent;
import android.security.KeyChain;
import android.util.Log;
import jp.co.soliton.keymanager.LogCtrl;

@SuppressWarnings("deprecation")
public class CertificateUtility {
	public static void certStoreToKeyChain(
			Activity context,
			CertStore store, String alias) {
		try {
			Collection<? extends Certificate> cACertificateCollection;
			cACertificateCollection = (Collection<? extends Certificate>) store.getCertificates(null);
			Iterator<? extends Certificate> iterator = cACertificateCollection.iterator();
			int i = cACertificateCollection.size();
			while (iterator.hasNext() == true) {
				X509Certificate certificate = (X509Certificate) iterator.next();
				CertificateUtility.certificateToKeyChain(context, certificate, alias, i + 9);
				i--;
			}
		} catch (CertStoreException e) {
			e.printStackTrace();
			LogCtrl.Logger(LogCtrl.m_strError, "CertificateUtility::certStoreToKeyChain CertStoreException::" + e.toString(), context);
		}
	}

	public static void certificateToKeyChain(
			Activity context,
			X509Certificate certificate,
			String alias,
			int requestCode) {
		try {
			Intent intent = KeyChain.createInstallIntent();
			intent.putExtra(KeyChain.EXTRA_CERTIFICATE, certificate.getEncoded());
			intent.putExtra(KeyChain.EXTRA_NAME, alias);
			context.startActivityForResult(intent, requestCode);
		} catch (CertificateEncodingException e) {
			e.printStackTrace();
			LogCtrl.Logger(LogCtrl.m_strError, "CertificateUtility::certificateToKeyChain CertificateEncodingException::" + e.toString(), context);
		}
	}

	public static void keyPairToKeyChain(
			Activity context,
			KeyPair keyPair) {
		Intent intent = KeyChain.createInstallIntent();
		intent.putExtra("PKEY", keyPair.getPrivate().getEncoded());
		intent.putExtra("KEY", keyPair.getPublic().getEncoded());
		context.startActivity(intent);
	}

	public static X509Certificate generateSelfSignedCertificate(
			String subject,
			BigInteger serial,
			Date notBefore,
			Date notAfter,
			KeyPair keyPair,
			String signatureAlgorithm) {

		X509Name subjectDn = new X509Name(subject/*"C=JP, O=Soliton Systems Co.LTD, CN=medamaoyaji, 1.2.840.113549.1.9.1=test@soliton.jp, EmailAddress=test@soliton.jp"*/);
		X509Name issureDn = new X509Name(subject/*"C=JP, O=Soliton Systems Co.LTD, CN=\"medam+&aoyaji\",  1.2.840.113549.1.9.1=test@soliton.jp, EmailAddress=test@soliton.jp"*/);

		X509V3CertificateGenerator certificateGenerator = new X509V3CertificateGenerator();
		certificateGenerator.setSerialNumber(serial);
		certificateGenerator.setIssuerDN(issureDn);
		certificateGenerator.setSubjectDN(subjectDn);
		certificateGenerator.setNotBefore(notBefore);
		certificateGenerator.setNotAfter(notAfter);
		certificateGenerator.setSignatureAlgorithm(signatureAlgorithm);
		certificateGenerator.setPublicKey(keyPair.getPublic());
		certificateGenerator.addExtension(
				X509Extensions.BasicConstraints,
				true,
				new BasicConstraints(false));
		certificateGenerator.addExtension(
				X509Extensions.KeyUsage,
				true,
				new KeyUsage(KeyUsage.digitalSignature | KeyUsage.keyEncipherment));
		certificateGenerator.addExtension(
				X509Extensions.ExtendedKeyUsage,
				true,
				new ExtendedKeyUsage(KeyPurposeId.id_kp_clientAuth));
		try {
			X509Certificate certificate;
			certificate = certificateGenerator.generateX509Certificate(keyPair.getPrivate(), new SecureRandom());
			return certificate;
		} catch (InvalidKeyException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (SignatureException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static PKCS10CertificationRequest generateCertificateSigningRequest(
			String subject,
			String challenge,
			KeyPair keyPair,
			String signatureAlgorithm,
			String sbjectAltName) {
		PKCS10CertificationRequest certificateSigningRequest;
		X509Name subjectDn = new X509Name(subject);

		ASN1Set attributes = null;		
		if (challenge != null) {
			// <== debug
			// create the extension value
			Log.d("PKCS10CertificationRequest::generateCertificateSigningRequest", "trace01. subject alt name:" + sbjectAltName);
			GeneralNames subjectAltName = new GeneralNames( 
					new GeneralName(GeneralName.rfc822Name, sbjectAltName/*"soliton@example.local"*/));
			// create the extensions object and add it as an attribute
			Vector oids = new Vector();
			Vector values = new Vector();

			oids.add(X509Extensions.SubjectAlternativeName);
			values.add(new X509Extension(false, new DEROctetString(subjectAltName)));
			values.add(new DERPrintableString(challenge));

			X509Extensions extensions = new X509Extensions(oids, values);
			ASN1Set attrValues1 = new DERSet(extensions);

			/*Attribute*/DEREncodable attribute = new Attribute(
			                           PKCSObjectIdentifiers.pkcs_9_at_extensionRequest,
			                           attrValues1/*new DERSet(extensions)*/);

			Log.d("PKCS10CertificationRequest::generateCertificateSigningRequest", "trace02");
			// debug ==>
			DERObjectIdentifier attrType = PKCSObjectIdentifiers.pkcs_9_at_challengePassword;
			ASN1Set attrValues = new DERSet(new DERPrintableString(challenge));
			DEREncodable password = new Attribute(attrType, attrValues);
			attributes = new DERSet(password/*attribute*/);
		}

		try {
			certificateSigningRequest =
					new PKCS10CertificationRequest(
							signatureAlgorithm,
							subjectDn,
							keyPair.getPublic(),
							attributes,
							keyPair.getPrivate(),
							"BC");
			Log.d("PKCS10CertificationRequest::generateCertificateSigningRequest", "trace03");
			return certificateSigningRequest;
		} catch (InvalidKeyException e) {
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (NoSuchProviderException e) {
			e.printStackTrace();
		} catch (SignatureException e) {
			e.printStackTrace();
		}
		return null;

	}
}
