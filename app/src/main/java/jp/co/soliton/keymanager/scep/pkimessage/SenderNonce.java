package jp.co.soliton.keymanager.scep.pkimessage;

import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.DERObjectIdentifier;
import org.bouncycastle.asn1.cms.Attribute;
import org.bouncycastle.cms.CMSException;

import java.util.Formatter;

public class SenderNonce extends Nonce implements PrivateObjectIdentifier {

	public SenderNonce() {
		super();
	}
	
	public SenderNonce(Attribute senderNonce) throws CMSException {
		super(senderNonce);
	}

	public DERObjectIdentifier getDERObjectIdentifier() {
		return new DERObjectIdentifier(getASN1ObjectIdentifier().getId());
	}
	
	@Override
	public ASN1ObjectIdentifier getASN1ObjectIdentifier() {
		return PrivateObjectIdentifier.senderNonce;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder(NONCE_SIZE * 2);  
		  
		Formatter formatter = new Formatter(sb);  
		for (byte b : this.nonce) {  
			formatter.format("%02x", b);  
		}  

		return new String("senderNonce=" + sb.toString());
	}
}
