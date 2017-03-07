package jp.co.soliton.keymanager.scep.pkimessage;

import java.util.Formatter;

import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.DERObjectIdentifier;
import org.bouncycastle.asn1.cms.Attribute;
import org.bouncycastle.cms.CMSException;

public class RecipientNonce extends Nonce implements PrivateObjectIdentifier {

	public RecipientNonce() {
		super();
	}
	public RecipientNonce(Attribute recipientNonce) throws CMSException {
		super(recipientNonce);
	}

	public DERObjectIdentifier getDERObjectIdentifier() {
		return new DERObjectIdentifier(getASN1ObjectIdentifier().getId());
	}

	@Override
	public ASN1ObjectIdentifier getASN1ObjectIdentifier() {
		return PrivateObjectIdentifier.recipientNonce;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder(NONCE_SIZE * 2);  
		  
		Formatter formatter = new Formatter(sb);  
		for (byte b : this.nonce) {  
			formatter.format("%02x", b);  
		}  

		return new String("Recipient Nonce : " + sb.toString());
	}
}
