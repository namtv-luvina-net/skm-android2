package jp.co.soliton.keymanager.common;

import android.content.Context;
import jp.co.soliton.keymanager.ItemChildDetailCertSetting;
import jp.co.soliton.keymanager.R;
import jp.co.soliton.keymanager.dbalias.ElementApply;

import java.util.ArrayList;
import java.util.List;

import static jp.co.soliton.keymanager.manager.APIDManager.PREFIX_APID_WIFI;

/**
 * Created by nguyenducdat on 6/8/2017.
 */

public class InfoDetailCertificateSetting {

	public static List<String>  prepareHeader(Context context) {
		List<String> listDataHeader = new ArrayList<>();
		listDataHeader.add("");
		listDataHeader.add(context.getString(R.string.label_subject_name));
		listDataHeader.add(context.getString(R.string.label_issuer_name));
		listDataHeader.add(context.getString(R.string.validity_period));
		listDataHeader.add(context.getString(R.string.label_signature_algorithm));
		listDataHeader.add(context.getString(R.string.label_public_key_algorithm));
		listDataHeader.add(context.getString(R.string.label_basic_constraints));
		listDataHeader.add(context.getString(R.string.label_key_usage));
		listDataHeader.add(context.getString(R.string.label_subject_key_identifier));
		listDataHeader.add(context.getString(R.string.label_authority_key_identifier));
		listDataHeader.add(context.getString(R.string.label_crl_distribution_points));
		listDataHeader.add(context.getString(R.string.label_certificate_authority_information_access));
		listDataHeader.add(context.getString(R.string.label_extended_key_usage));
		return listDataHeader;
	}

	public static List<List<ItemChildDetailCertSetting>> prepareChild(Context context, ElementApply elementApply) {
		List<List<ItemChildDetailCertSetting>> listDataChild = new ArrayList<>();
		//Add child STORAGE DESTINATION
		List<ItemChildDetailCertSetting> storageDestination = new ArrayList<>();
		String strTarget = "";
		if (elementApply.getTarget() != null) {
			if (elementApply.getTarget().startsWith(PREFIX_APID_WIFI)) {
				strTarget = context.getString(R.string.main_apid_wifi);
			} else {
				strTarget = context.getString(R.string.main_apid_vpn);
			}
		}
		ItemChildDetailCertSetting storage = new ItemChildDetailCertSetting(context.getString(R.string.place), strTarget);
		storageDestination.add(storage);

		//Add child SUBJECT NAME
		List<ItemChildDetailCertSetting> subjectName = new ArrayList<>();
		ItemChildDetailCertSetting countryName = new ItemChildDetailCertSetting(context.getString(R.string.label_country_name),
				elementApply.getSubjectCountryName());
		ItemChildDetailCertSetting stateOrProvinceName = new ItemChildDetailCertSetting(context.getString(R.string
				.label_state_or_province_name), elementApply.getSubjectStateOrProvinceName());
		ItemChildDetailCertSetting localityName = new ItemChildDetailCertSetting(context.getString(R.string
				.label_locality_name), elementApply.getSubjectLocalityName());
		ItemChildDetailCertSetting organizationName = new ItemChildDetailCertSetting(context.getString(R.string
				.label_organization_name), elementApply.getSubjectOrganizationName());
		ItemChildDetailCertSetting commonName = new ItemChildDetailCertSetting(context.getString(R.string
				.label_common_name), elementApply.getSubjectCommonName());
		ItemChildDetailCertSetting emailAddress = new ItemChildDetailCertSetting(context.getString(R.string
				.label_email_address), elementApply.getSubjectEmailAddress());
		subjectName.add(countryName);
		subjectName.add(stateOrProvinceName);
		subjectName.add(localityName);
		subjectName.add(organizationName);
		subjectName.add(commonName);
		subjectName.add(emailAddress);

		// Add child ISSUER NAME
		List<ItemChildDetailCertSetting> issuerName = new ArrayList<>();
		ItemChildDetailCertSetting issuerCountryName = new ItemChildDetailCertSetting(
				context.getString(R.string.label_country_name), elementApply.getIssuerCountryName());
		ItemChildDetailCertSetting issuerStateOrProvinceName = new ItemChildDetailCertSetting(
				context.getString(R.string.label_state_or_province_name), elementApply.getIssuerStateOrProvinceName());
		ItemChildDetailCertSetting issuerLocalityName = new ItemChildDetailCertSetting(
				context.getString(R.string.label_locality_name), elementApply.getIssuerLocalityName());
		ItemChildDetailCertSetting issuerOrganizationName = new ItemChildDetailCertSetting(
				context.getString(R.string.label_organization_name), elementApply.getIssuerOrganizationName());
		ItemChildDetailCertSetting issuerOrganizationUnitName = new ItemChildDetailCertSetting(
				context.getString(R.string.label_organizational_unit_name), elementApply.getIssuerOrganizationUnitName());
		ItemChildDetailCertSetting issuerCommonName = new ItemChildDetailCertSetting(
				context.getString(R.string.label_common_name), elementApply.getIssuerCommonName());
		ItemChildDetailCertSetting issuerEmailAdress = new ItemChildDetailCertSetting(
				context.getString(R.string.label_email_address), elementApply.getIssuerEmailAdress());
		ItemChildDetailCertSetting version = new ItemChildDetailCertSetting(
				context.getString(R.string.label_issuer_version), elementApply.getVersion());
		ItemChildDetailCertSetting serialNumber = new ItemChildDetailCertSetting(
				context.getString(R.string.label_issuer_serial_number), elementApply.getSerialNumber());
		issuerName.add(issuerCountryName);
		issuerName.add(issuerStateOrProvinceName);
		issuerName.add(issuerLocalityName);
		issuerName.add(issuerOrganizationName);
		issuerName.add(issuerOrganizationUnitName);
		issuerName.add(issuerCommonName);
		issuerName.add(issuerEmailAdress);
		issuerName.add(version);
		issuerName.add(serialNumber);

		// Add child VALIDITY PERIOD
		List<ItemChildDetailCertSetting> validityPeriod = new ArrayList<>();
		ItemChildDetailCertSetting notValidBefore = new ItemChildDetailCertSetting(
				context.getString(R.string.label_signature_not_valid_before), elementApply.getNotValidBefore());
		ItemChildDetailCertSetting notValidAfter = new ItemChildDetailCertSetting(
				context.getString(R.string.label_signature_not_valid_after), elementApply.getNotValidAfter());
		validityPeriod.add(notValidBefore);
		validityPeriod.add(notValidAfter);

		// Add child SIGNATURE
		List<ItemChildDetailCertSetting> signature = new ArrayList<>();
		ItemChildDetailCertSetting signatureAlogrithm = new ItemChildDetailCertSetting(
				context.getString(R.string.label_algorithm_label), elementApply.getSignatureAlogrithm());
		signature.add(signatureAlogrithm);

		// Add child PUBLIC KEY ALGORITHM
		List<ItemChildDetailCertSetting> publicKeyAlgorithm = new ArrayList<>();
		ItemChildDetailCertSetting algorithm = new ItemChildDetailCertSetting(
				context.getString(R.string.label_algorithm_label), elementApply.getPublicKeyAlogrithm());
		String strPublicData = elementApply.getPublicKeyData().toUpperCase().replace(":", " ");
		ItemChildDetailCertSetting publicKeyData = new ItemChildDetailCertSetting(
				context.getString(R.string.label_public_key_algorithm_data), strPublicData, false);
		String strSignature = elementApply.getPublicSignature().toUpperCase().replace(":", " ");
		ItemChildDetailCertSetting signatureData = new ItemChildDetailCertSetting(
				context.getString(R.string.label_public_key_algorithm_signature), strSignature, false);
		publicKeyAlgorithm.add(algorithm);
		publicKeyAlgorithm.add(publicKeyData);
		publicKeyAlgorithm.add(signatureData);

		// Add child BASIC CONTRAINTS
		List<ItemChildDetailCertSetting> basicContraints = new ArrayList<>();
		String strCertificateAuthority;
		if (Integer.parseInt(elementApply.getCertificateAuthority()) >= 0) {
			strCertificateAuthority = "TRUE";
		} else {
			strCertificateAuthority = "FALSE";
		}
		basicContraints.add(new ItemChildDetailCertSetting(
				context.getString(R.string.label_certificate_authority), strCertificateAuthority));

		// Add child KEY USAGE
		List<ItemChildDetailCertSetting> keyUsage = new ArrayList<>();
		keyUsage.add(new ItemChildDetailCertSetting(context.getString(R.string.label_usage), elementApply.getUsage()));

		// Add child SUBJECT KEY IDENTIFIER
		List<ItemChildDetailCertSetting> subjectkeyIdentifier = new ArrayList<>();
		String detailKeyIdentifier = elementApply.getSubjectKeyIdentifier().toUpperCase().replace(":", " ");
		subjectkeyIdentifier.add(new ItemChildDetailCertSetting(context.getString(R.string.label_subject_key_identifier),
				detailKeyIdentifier, false));

		// Add child AUTHORITY KEY IDENTIFIER
		List<ItemChildDetailCertSetting> authorityKeyIdentifier = new ArrayList<>();
		String detailAuthorKeyIdentifier = elementApply.getAuthorityKeyIdentifier().toUpperCase().replace(":", " ");
		authorityKeyIdentifier.add(new ItemChildDetailCertSetting(context.getString(R.string.label_authority_key_identifier),
				detailAuthorKeyIdentifier, false));

		// Add child CRL DISTRIBUTION POINTS
		List<ItemChildDetailCertSetting> crlDistributionPoints = new ArrayList<>();
		crlDistributionPoints.add(new ItemChildDetailCertSetting(context.getString(R.string.label_uri),
				elementApply.getClrDistributionPointUri()));

		// Add child CERTIFICATE AUTHORITY INFORMATION ACCESS
		List<ItemChildDetailCertSetting> certificateAuthority = new ArrayList<>();
		certificateAuthority.add(new ItemChildDetailCertSetting(context.getString(R.string.label_uri),
				elementApply.getCertificateAuthorityUri()));

		// Add child EXTENDED KEY USAGE
		List<ItemChildDetailCertSetting> extendedKeyUsage = new ArrayList<>();
		extendedKeyUsage.add(new ItemChildDetailCertSetting(context.getString(R.string.label_purpose),
				elementApply.getPurpose()));

		listDataChild.add(storageDestination);
		listDataChild.add(subjectName);
		listDataChild.add(issuerName);
		listDataChild.add(validityPeriod);
		listDataChild.add(signature);
		listDataChild.add(publicKeyAlgorithm);
		listDataChild.add(basicContraints);
		listDataChild.add(keyUsage);
		listDataChild.add(subjectkeyIdentifier);
		listDataChild.add(authorityKeyIdentifier);
		listDataChild.add(crlDistributionPoints);
		listDataChild.add(certificateAuthority);
		listDataChild.add(extendedKeyUsage);
		return listDataChild;
	}
}
