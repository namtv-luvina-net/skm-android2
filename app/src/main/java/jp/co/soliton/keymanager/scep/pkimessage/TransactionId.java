package jp.co.soliton.keymanager.scep.pkimessage;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.util.Formatter;

import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.DERObjectIdentifier;
import org.bouncycastle.asn1.DERPrintableString;
import org.bouncycastle.asn1.DERSet;
import org.bouncycastle.asn1.cms.Attribute;
import org.bouncycastle.cms.CMSException;

public class TransactionId implements PrivateObjectIdentifier {
	private String transactionId;

	public TransactionId(Attribute attribute) throws CMSException {
		if (attribute.getAttrType().equals(getDERObjectIdentifier()) == false) {
			throw new CMSException("Invalid Objecet Identifier");
		}
		DERPrintableString transid = (DERPrintableString) attribute.getAttrValues().getObjectAt(0);
		transactionId = transid.getString();
	}

	public TransactionId(PublicKey publicKey, String digestAlgorithm) {
		try {
			MessageDigest messageDigest = MessageDigest.getInstance(digestAlgorithm);
			byte[] id = messageDigest.digest(publicKey.getEncoded());
			setTransactionId(id);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
	}
	
	public DERPrintableString getTransacionId() {
		return new DERPrintableString(transactionId.getBytes());
	}
	
	private void setTransactionId(byte[] id) {
		StringBuilder sb = new StringBuilder(id.length * 2);  
			  
		Formatter formatter = new Formatter(sb);  
		for (byte b : id) {  
			formatter.format("%02x", b);  
		}  
			  
		transactionId = sb.toString();  
	}
	
	public Attribute getAttribute() {
		return new Attribute(getDERObjectIdentifier(), new DERSet(getTransacionId()));
	}
	private DERObjectIdentifier getDERObjectIdentifier() {
		return new DERObjectIdentifier(getASN1ObjectIdentifier().getId());
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (obj.getClass() != getClass()) {
			return false;
		}
		
		return getTransacionId().equals(((TransactionId) obj).getTransacionId());
	}
	
	@Override
	public ASN1ObjectIdentifier getASN1ObjectIdentifier() {
		return PrivateObjectIdentifier.transId;
	}
	
	@Override
	public String toString() {
		return new String("Transaction ID : " + transactionId + "(" + transactionId.length() + ")");
	}
}
