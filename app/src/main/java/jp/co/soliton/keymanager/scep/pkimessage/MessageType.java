package jp.co.soliton.keymanager.scep.pkimessage;

import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.DERObjectIdentifier;
import org.bouncycastle.asn1.DERPrintableString;
import org.bouncycastle.asn1.DERSet;
import org.bouncycastle.asn1.cms.Attribute;
import org.bouncycastle.cms.CMSException;

public class MessageType implements PrivateObjectIdentifier {
	public enum Type {
		PKCSReq(19),
		CertRep(3),
		GetCertInitial(20),
		GetCert(21),
		GetCRL(22);
		
		private final Integer value;
		Type(Integer value) {
			this.value = value;
		}
		public Integer getValue() {
			return value;
		}
	}
	
	private Type messageType;
	
	public MessageType(Attribute attribute) throws CMSException {
		if (attribute.getAttrType().equals(getDERObjectIdentifier()) == false) {
			throw new CMSException("Invalid Objecet Identifier");
		}
		DERPrintableString messageTypeString = (DERPrintableString) attribute.getAttrValues().getObjectAt(0);
		Integer type = Integer.valueOf(messageTypeString.getString());
		Type[] types = Type.values();
		for (int i = 0;i < types.length;i++) {
			if (types[i].getValue() == type) {
				setMessageType(types[i]);
			}
		}
	}

	public MessageType(Type messageType) {
		setMessageType(messageType);
	}
	
	public Type getMessageType() {
		return this.messageType;
	}

	public DERPrintableString getDERMessageType() {
		return new DERPrintableString(messageType.getValue().toString());
	}
	
	private void setMessageType(Type type) {
		this.messageType = type;
	}
	
	public Attribute getAttribute() {
		return new Attribute(getDERObjectIdentifier(), new DERSet(getDERMessageType()));
	}
	private DERObjectIdentifier getDERObjectIdentifier() {
		return new DERObjectIdentifier(getASN1ObjectIdentifier().getId());
	}
	
	@Override
	public ASN1ObjectIdentifier getASN1ObjectIdentifier() {
		return PrivateObjectIdentifier.messageType;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		return getMessageType().equals(((MessageType) obj).getMessageType());
	}
	
	@Override
	public String toString() {
		return new String("Message Type : " + messageType.name() + "(" + messageType.getValue().toString() + ")");
	}
}
