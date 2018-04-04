package jp.co.soliton.keymanager.dbalias;

import jp.co.soliton.keymanager.common.DateUtils;

import java.io.Serializable;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

/**
 * Created by luongdolong on 2/22/2017.
 */

public class ElementApply implements Serializable {
    public static int STATUS_APPLY_CANCEL  = 0;
    public static int STATUS_APPLY_REJECT  = 1;
    public static int STATUS_APPLY_PENDING = 2;
    public static int STATUS_APPLY_FAILURE = 3;
    public static int STATUS_APPLY_APPROVED = 99;
    public static int STATUS_APPLY_CLOSED = 100;

    private int id;
    private String host;
    private String portSSL;
    private String port;
    private String userId;
    private String password;
    private String email;
    private String reason;
    private String targer;
    private String versionEpsAp;
    private int status;
    private boolean challenge;
    private String updateDate;
    private boolean notiEnableFlag;
    private boolean notiEnableBeforeFlag;
    private int notiEnableBefore;
    private String expirationDate;
    private String cNValue;
    private String sNValue;

    private String subjectCountryName;
    private String subjectStateOrProvinceName;
    private String subjectLocalityName;
    private String subjectOrganizationName;
    private String subjectCommonName;
    private String subjectEmailAddress;
    private String issuerCountryName;
    private String issuerStateOrProvinceName;
    private String issuerLocalityName;
    private String issuerOrganizationName;
    private String issuerOrganizationUnitName;
    private String issuerCommonName;
    private String issuerEmailAdress;
    private String version;
    private String serialNumber;
    private String signatureAlogrithm;
    private String notValidBefore;
    private String notValidAfter;
    private String publicKeyAlogrithm;
    private String publicKeyData;
    private String publicSignature;
    private String certificateAuthority;
    private String usage;
    private String subjectKeyIdentifier;
    private String authorityKeyIdentifier;
    private String clrDistributionPointUri;
    private String certificateAuthorityUri;
    private String purpose;
    private String rfc822Name;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getPortSSL() {
        return portSSL;
    }

    public void setPortSSL(String portSSL) {
        this.portSSL = portSSL;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getTarget() {
        return targer;
    }

    public void setTarger(String targer) {
        this.targer = targer;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public boolean isChallenge() {
        return challenge;
    }

    public void setChallenge(boolean challenge) {
        this.challenge = challenge;
    }

    public String getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(String updateDate) {
        this.updateDate = updateDate;
    }

    public boolean isNotiEnableFlag() {
        return notiEnableFlag;
    }

    public void setNotiEnableFlag(boolean notiEnableFlag) {
        this.notiEnableFlag = notiEnableFlag;
    }

    public boolean isNotiEnableBeforeFlag() {
        return notiEnableBeforeFlag;
    }

    public void setNotiEnableBeforeFlag(boolean notiEnableBeforeFlag) {
        this.notiEnableBeforeFlag = notiEnableBeforeFlag;
    }

    public int getNotiEnableBefore() {
        return notiEnableBefore;
    }

    public void setNotiEnableBefore(int notiEnableBefore) {
        this.notiEnableBefore = notiEnableBefore;
    }

    public String getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(String expirationDate) {
        this.expirationDate = expirationDate;
    }

    public String getcNValue() {
        return cNValue;
    }

    public void setcNValue(String cNValue) {
        this.cNValue = cNValue;
    }

    public String getsNValue() {
        return sNValue;
    }

    public void setsNValue(String sNValue) {
        this.sNValue = sNValue;
    }

    public String getSubjectCountryName() {
        return subjectCountryName;
    }

    public void setSubjectCountryName(String subjectCountryName) {
        this.subjectCountryName = subjectCountryName;
    }

    public String getSubjectStateOrProvinceName() {
        return subjectStateOrProvinceName;
    }

    public void setSubjectStateOrProvinceName(String subjectStateOrProvinceName) {
        this.subjectStateOrProvinceName = subjectStateOrProvinceName;
    }

    public String getSubjectLocalityName() {
        return subjectLocalityName;
    }

    public void setSubjectLocalityName(String subjectLocalityName) {
        this.subjectLocalityName = subjectLocalityName;
    }

    public String getSubjectOrganizationName() {
        return subjectOrganizationName;
    }

    public void setSubjectOrganizationName(String subjectOrganizationName) {
        this.subjectOrganizationName = subjectOrganizationName;
    }

    public String getSubjectCommonName() {
        return subjectCommonName;
    }

    public void setSubjectCommonName(String subjectCommonName) {
        this.subjectCommonName = subjectCommonName;
    }

    public String getSubjectEmailAddress() {
        return subjectEmailAddress;
    }

    public void setSubjectEmailAddress(String subjectEmailAddress) {
        this.subjectEmailAddress = subjectEmailAddress;
    }

    public String getIssuerCountryName() {
        return issuerCountryName;
    }

    public void setIssuerCountryName(String issuerCountryName) {
        this.issuerCountryName = issuerCountryName;
    }

    public String getIssuerStateOrProvinceName() {
        return issuerStateOrProvinceName;
    }

    public void setIssuerStateOrProvinceName(String issuerStateOrProvinceName) {
        this.issuerStateOrProvinceName = issuerStateOrProvinceName;
    }

    public String getIssuerLocalityName() {
        return issuerLocalityName;
    }

    public void setIssuerLocalityName(String issuerLocalityName) {
        this.issuerLocalityName = issuerLocalityName;
    }

    public String getIssuerOrganizationName() {
        return issuerOrganizationName;
    }

    public void setIssuerOrganizationName(String issuerOrganizationName) {
        this.issuerOrganizationName = issuerOrganizationName;
    }

    public String getIssuerOrganizationUnitName() {
        return issuerOrganizationUnitName;
    }

    public void setIssuerOrganizationUnitName(String issuerOrganizationUnitName) {
        this.issuerOrganizationUnitName = issuerOrganizationUnitName;
    }

    public String getIssuerCommonName() {
        return issuerCommonName;
    }

    public void setIssuerCommonName(String issuerCommonName) {
        this.issuerCommonName = issuerCommonName;
    }

    public String getIssuerEmailAdress() {
        return issuerEmailAdress;
    }

    public void setIssuerEmailAdress(String issuerEmailAdress) {
        this.issuerEmailAdress = issuerEmailAdress;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    public String getSignatureAlogrithm() {
        return signatureAlogrithm;
    }

    public void setSignatureAlogrithm(String signatureAlogrithm) {
        this.signatureAlogrithm = signatureAlogrithm;
    }

    public String getNotValidBefore() {
        return notValidBefore;
    }

    public void setNotValidBefore(String notValidBefore) {
        this.notValidBefore = notValidBefore;
    }

    public String getNotValidAfter() {
        return notValidAfter;
    }

    public void setNotValidAfter(String notValidAfter) {
        this.notValidAfter = notValidAfter;
    }

    public String getPublicKeyAlogrithm() {
        return publicKeyAlogrithm;
    }

    public void setPublicKeyAlogrithm(String publicKeyAlogrithm) {
        this.publicKeyAlogrithm = publicKeyAlogrithm;
    }

    public String getPublicKeyData() {
        return publicKeyData;
    }

    public void setPublicKeyData(String publicKeyData) {
        this.publicKeyData = publicKeyData;
    }

    public String getPublicSignature() {
        return publicSignature;
    }

    public void setPublicSignature(String publicSignature) {
        this.publicSignature = publicSignature;
    }

    public String getCertificateAuthority() {
        return certificateAuthority;
    }

    public void setCertificateAuthority(String certificateAuthority) {
        this.certificateAuthority = certificateAuthority;
    }

    public String getUsage() {
        return usage;
    }

    public void setUsage(String usage) {
        this.usage = usage;
    }

    public String getSubjectKeyIdentifier() {
        return subjectKeyIdentifier;
    }

    public void setSubjectKeyIdentifier(String subjectKeyIdentifier) {
        this.subjectKeyIdentifier = subjectKeyIdentifier;
    }

    public String getAuthorityKeyIdentifier() {
        return authorityKeyIdentifier;
    }

    public void setAuthorityKeyIdentifier(String authorityKeyIdentifier) {
        this.authorityKeyIdentifier = authorityKeyIdentifier;
    }

    public String getClrDistributionPointUri() {
        return clrDistributionPointUri;
    }

    public void setClrDistributionPointUri(String clrDistributionPointUri) {
        this.clrDistributionPointUri = clrDistributionPointUri;
    }

    public String getCertificateAuthorityUri() {
        return certificateAuthorityUri;
    }

    public void setCertificateAuthorityUri(String certificateAuthorityUri) {
        this.certificateAuthorityUri = certificateAuthorityUri;
    }

	public String getVersionEpsAp() {
		return versionEpsAp;
	}

	public void setVersionEpsAp(String versionEpsAp) {
		this.versionEpsAp = versionEpsAp;
	}

	public String getPurpose() {
        return purpose;
    }

    public void setPurpose(String purpose) {
        this.purpose = purpose;
    }

	public String getRfc822Name() {
		return rfc822Name;
	}

	public void setRfc822Name(String rfc822Name) {
		this.rfc822Name = rfc822Name;
	}

	public static void sortListConfirmApply(List<ElementApply> listElementApply) {
		Collections.sort(listElementApply, new Comparator<ElementApply>() {
			@Override
			public int compare(ElementApply o1, ElementApply o2) {
				Date date1 = DateUtils.convertSringToDate("yyyy-MM-dd HH:mm:ss", o1.getUpdateDate().replace("/", "-"));
				Date date2 = DateUtils.convertSringToDate("yyyy-MM-dd HH:mm:ss", o2.getUpdateDate().replace("/", "-"));
				return date1.before(date2) ? 1 : -1;
			}
		});
	}

	public static void sortListApplyUpdate(List<ElementApply> listElementApply) {
		Collections.sort(listElementApply, new Comparator<ElementApply>() {
			@Override
			public int compare(ElementApply o1, ElementApply o2) {
				Date date1 = DateUtils.convertSringToDate("yyyy-MM-dd HH:mm:ss", o1.getExpirationDate().replace("/", "-"));
				Date date2 = DateUtils.convertSringToDate("yyyy-MM-dd HH:mm:ss", o2.getExpirationDate().replace("/", "-"));
				return date1.before(date2) ? -1 : 1;
			}
		});
	}
}
