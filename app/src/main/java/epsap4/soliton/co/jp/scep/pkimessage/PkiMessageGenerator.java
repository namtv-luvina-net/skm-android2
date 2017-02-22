package epsap4.soliton.co.jp.scep.pkimessage;

import org.bouncycastle.asn1.DERObjectIdentifier;
import org.bouncycastle.asn1.cms.Attribute;
import org.bouncycastle.asn1.cms.AttributeTable;
import org.bouncycastle.cms.CMSEnvelopedData;
import org.bouncycastle.cms.CMSException;
import org.bouncycastle.cms.CMSProcessable;
import org.bouncycastle.cms.CMSProcessableByteArray;
import org.bouncycastle.cms.CMSSignedData;
import org.bouncycastle.cms.CMSSignedDataGenerator;

import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.cert.CertStore;
import java.security.cert.CertStoreException;
import java.security.cert.CollectionCertStoreParameters;
import java.security.cert.X509Certificate;
import java.util.Collection;
import java.util.Collections;
import java.util.Hashtable;

public class PkiMessageGenerator {

	@SuppressWarnings("deprecation")
	public static PkiMessage generator(
			MessageType messageType,
			CMSEnvelopedData envelope,
			X509Certificate senderCertificate,
			PrivateKey privateKey) {
		CMSProcessable signData;
		try {
			signData = new CMSProcessableByteArray(envelope.getEncoded());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}

		Hashtable<DERObjectIdentifier, Attribute> table =
				new Hashtable<DERObjectIdentifier, Attribute>();
		// Message Type
		Attribute messageTypeAttribute = messageType.getAttribute();
		table.put(
				messageTypeAttribute.getAttrType(),
				messageTypeAttribute);

		// Transaction ID
		TransactionId transactionId =
				new TransactionId(senderCertificate.getPublicKey(), "SHA-1");
		Attribute transactionIdAttribute = transactionId.getAttribute();
		table.put(
				transactionIdAttribute.getAttrType(),
				transactionIdAttribute);
		
		// Sender Nonce
		Nonce senderNonce = new SenderNonce();
		Attribute senderNonceAttribute = senderNonce.getAttribute();
		table.put(
				senderNonceAttribute.getAttrType(),
				senderNonceAttribute);

		AttributeTable attributeTable = new AttributeTable(table);

		CMSSignedDataGenerator signedDataGenerator = new CMSSignedDataGenerator();
		signedDataGenerator.addSigner(
				privateKey,
				senderCertificate,
				CMSSignedDataGenerator.DIGEST_SHA1,
				attributeTable,
				null);
				
        Collection<X509Certificate> certificateCollection = Collections.singleton(senderCertificate);
        CertStore senderCertificateStore;
        try {
        	senderCertificateStore =
					CertStore.getInstance(
							"Collection",
							new CollectionCertStoreParameters(certificateCollection));
		} catch (InvalidAlgorithmParameterException e) {
			e.printStackTrace();
			return null;
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			return null;
		}

        try {
			signedDataGenerator.addCertificatesAndCRLs(senderCertificateStore);
		} catch (CertStoreException e1) {
			e1.printStackTrace();
			return null;
		} catch (CMSException e1) {
			e1.printStackTrace();
			return null;
		}

        CMSSignedData signedData;
        PkiMessage pkiMessage = null;
        try {
        	signedData =
					signedDataGenerator.generate(signData, true, (String) null);
        	pkiMessage = new PkiMessage(signedData.getEncoded());
            return pkiMessage;
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			return null;
		} catch (NoSuchProviderException e) {
			e.printStackTrace();
			return null;
		} catch (CMSException e) {
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
}
