package epsap4.soliton.co.jp.scep.pkimessage;

import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.DERObjectIdentifier;
import org.bouncycastle.asn1.DERPrintableString;
import org.bouncycastle.asn1.DERSet;
import org.bouncycastle.asn1.cms.Attribute;
import org.bouncycastle.cms.CMSException;


public class PkiStatus implements PrivateObjectIdentifier {
	public enum Status {
		SUCCESS(0),
		FAILURE(2),
		PENDING(3);
		
		private final Integer value;
		Status(Integer value) {
			this.value = value;
		}
		public Integer getValue() {
			return value;
		}
	}
	private Status pkiStatus;
	
	public PkiStatus() {
	}
	
	public PkiStatus(Attribute attribute) throws CMSException {
		if (attribute.getAttrType().equals(getDERObjectIdentifier()) == false) {
			throw new CMSException("Invalid Objecet Identifier");
		}
		DERPrintableString status = (DERPrintableString) attribute.getAttrValues().getObjectAt(0);
		Integer type = Integer.valueOf(status.getString());
		Status[] types = Status.values();
		for (int i = 0;i < types.length;i++) {
			if (types[i].getValue() == type) {
				setPkiStatus(types[i]);
			}
		}
	}
	
	public Status getStatus() {
		return pkiStatus;
	}

	public DERPrintableString getPkiStatus() {
		return new DERPrintableString(pkiStatus.getValue().toString());
	}
	
	public void setPkiStatus(Status status) {
		pkiStatus = status;
	}
	
	public Attribute getAttribute() {
		return new Attribute(getDERObjectIdentifier(), new DERSet(getPkiStatus()));
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
		return getPkiStatus().equals(((PkiStatus) obj).getPkiStatus());
	}
	
	@Override
	public ASN1ObjectIdentifier getASN1ObjectIdentifier() {
		return PrivateObjectIdentifier.pkiStatus;
	}
	
	@Override
	public String toString() {
		return new String("pkiStatus : " + pkiStatus.name() + "(" + pkiStatus.getValue().toString() + ")");
	}
}
