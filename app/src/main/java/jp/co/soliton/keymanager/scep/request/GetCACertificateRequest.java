package jp.co.soliton.keymanager.scep.request;

import java.net.URL;

public class GetCACertificateRequest extends Request {
	private String caIssureIdentifier = null;

	public GetCACertificateRequest(URL url) {
		super(url, Operation.GetCACert);
	}

	public GetCACertificateRequest(URL url, String identifier) {
		super(url, Operation.GetCACert);
		setCaIssureIdentifier(identifier);
	}
	
	public String getCaIssureIdentifier() {
		return caIssureIdentifier;
	}

	public void setCaIssureIdentifier(String caIssureIdentifier) {
		this.caIssureIdentifier = caIssureIdentifier;
	}

	@Override
	public String getMessage() {
		return getCaIssureIdentifier();
	}

}
