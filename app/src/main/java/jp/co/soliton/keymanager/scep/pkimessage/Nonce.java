package jp.co.soliton.keymanager.scep.pkimessage;

import org.bouncycastle.asn1.DERObjectIdentifier;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.DERSet;
import org.bouncycastle.asn1.cms.Attribute;
import org.bouncycastle.cms.CMSException;

import java.security.SecureRandom;

public abstract class Nonce {
	final private SecureRandom secureRandom; 
	protected static final int NONCE_SIZE = 16;
	protected byte[] nonce;

	public Nonce() {
		secureRandom = new SecureRandom();
	}

	public Nonce(Attribute attribute) throws CMSException {
		this();
		if (attribute.getAttrType().equals(getDERObjectIdentifier()) == false) {
			throw new CMSException("Invalid Objecet Identifier");
		}
		DEROctetString nonce = (DEROctetString) attribute.getAttrValues().getObjectAt(0);
		this.nonce = new byte[NONCE_SIZE];
		System.arraycopy(nonce.getOctets(), 0, this.nonce, 0, NONCE_SIZE);
	}
	
	public Nonce(byte[] nonce) {
		this();
		this.nonce = new byte[NONCE_SIZE];
		System.arraycopy(nonce, 0, this.nonce, 0, NONCE_SIZE);
	}
	
	public DEROctetString getNonce() {
		return new DEROctetString(nonce);
	}
	private DEROctetString getNewNonce() {
		nonce = new byte[NONCE_SIZE];
		secureRandom.nextBytes(nonce);
		return new DEROctetString(nonce);
	}
	public Attribute getAttribute() {
		return new Attribute(getDERObjectIdentifier(), new DERSet(getNewNonce()));
	}

	public abstract DERObjectIdentifier getDERObjectIdentifier();
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!(obj instanceof Nonce)) {
			return false;
		}

		return getNonce().equals(((Nonce) obj).getNonce());
	}
}
