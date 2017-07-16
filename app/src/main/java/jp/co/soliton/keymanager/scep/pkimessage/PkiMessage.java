package jp.co.soliton.keymanager.scep.pkimessage;

import java.io.InputStream;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.cert.CertStore;
import java.security.cert.CertStoreException;
import java.security.cert.Certificate;
import java.security.cert.CertificateExpiredException;
import java.security.cert.CertificateNotYetValidException;
import java.security.cert.X509Certificate;
import java.util.Collection;
import java.util.Iterator;
import java.util.Hashtable;

import org.bouncycastle.asn1.cms.Attribute;
import org.bouncycastle.asn1.cms.AttributeTable;
import org.bouncycastle.cms.CMSException;
import org.bouncycastle.cms.CMSSignedData;
import org.bouncycastle.cms.SignerInformation;
import org.bouncycastle.cms.SignerInformationStore;
import org.bouncycastle.asn1.DERObjectIdentifier;

import jp.co.soliton.keymanager.LogCtrl;

public class PkiMessage extends CMSSignedData {
	private Hashtable<DERObjectIdentifier, Attribute> scepAttributeTable = null;
	
	public PkiMessage(byte[] sigBlock) throws CMSException {
		super(sigBlock);
	}
	
	public PkiMessage(InputStream sigData) throws CMSException {
		super(sigData);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private void getScepAttributeTable() throws CMSException {
		SignerInformationStore signerInfomationStore = getSignerInfos();
		Collection signers = signerInfomationStore.getSigners();
		if (signers.size() != 1) {
			throw new CMSException("Invalid Signers");
		}
		Iterator itrator = signers.iterator();
		SignerInformation signerInfomation = (SignerInformation) itrator.next();

		AttributeTable authenticatedAttributes = signerInfomation.getSignedAttributes();

		scepAttributeTable = authenticatedAttributes.toHashtable();
		
		return;
	}

	@SuppressWarnings({ "rawtypes", "deprecation" })
	public void verify() throws CMSException {
		SignerInformationStore signerInfomationStore = getSignerInfos();
		Collection signers = signerInfomationStore.getSigners();
		if (signers.size() != 1) {
			throw new CMSException("Invalid Signers");
		}
		Iterator itrator = signers.iterator();
		SignerInformation signerInfomation = (SignerInformation) itrator.next();

		// Signer certificate
		// Verify
		try {
			CertStore signerCertificateStore = getCertificatesAndCRLs("Collection", "BC");
			Collection<? extends Certificate> signerCertificateCollection =
					signerCertificateStore.getCertificates(signerInfomation.getSID());
			if (signerCertificateCollection.size() > 0) {
				X509Certificate signerCertificate =
						(X509Certificate) signerCertificateCollection.iterator().next();
				signerInfomation.verify(signerCertificate, "BC");
			} else {
				throw new CMSException("Unable to verify");
			}
			LogCtrl.getInstance().info("PKIMessage: Verify OK");
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			throw new CMSException(e.getMessage());
		} catch (NoSuchProviderException e) {
			e.printStackTrace();
			throw new CMSException(e.getMessage());
		} catch (CertStoreException e) {
			e.printStackTrace();
			throw new CMSException(e.getMessage());
		} catch (CertificateExpiredException e) {
			e.printStackTrace();
			throw new CMSException(e.getMessage());
		} catch (CertificateNotYetValidException e) {
			// TODO Auto-generated catch block
			throw new CMSException(e.getMessage());
		}
	}

	public TransactionId getTransactionId() throws CMSException {
		if (scepAttributeTable == null) {
			getScepAttributeTable();
		}
		Attribute attribute = scepAttributeTable.get(PrivateObjectIdentifier.transId);
		if (attribute == null) {
			return null;
		}
		return new TransactionId(attribute);
	}

	public MessageType getMessageType() throws CMSException {
		if (scepAttributeTable == null) {
			getScepAttributeTable();
		}
		Attribute attribute = scepAttributeTable.get(PrivateObjectIdentifier.messageType);
		if (attribute == null) {
			return null;
		}
		return new MessageType(attribute);
	}

	public SenderNonce getSenderNonce() throws CMSException {
		if (scepAttributeTable == null) {
			getScepAttributeTable();
		}
		Attribute attribute = scepAttributeTable.get(PrivateObjectIdentifier.senderNonce);
		if (attribute == null) {
			return null;
		}
		return new SenderNonce(attribute);
	}

	public RecipientNonce getRecipientNonce() throws CMSException {
		if (scepAttributeTable == null) {
			getScepAttributeTable();
		}
		Attribute attribute = scepAttributeTable.get(PrivateObjectIdentifier.recipientNonce);
		if (attribute == null) {
			return null;
		}
		return new RecipientNonce(attribute);
	}

	public PkiStatus getPkiStatus() throws CMSException {
		if (scepAttributeTable == null) {
			getScepAttributeTable();
		}
		Attribute attribute = scepAttributeTable.get(PrivateObjectIdentifier.pkiStatus);
		if (attribute == null) {
			return null;
		}
		return new PkiStatus(attribute);
	}

	public PkcsPKIEnvelope getPkcsPKIEnvelope() throws CMSException {
		return new PkcsPKIEnvelope((byte[]) getSignedContent().getContent());
	}
}
