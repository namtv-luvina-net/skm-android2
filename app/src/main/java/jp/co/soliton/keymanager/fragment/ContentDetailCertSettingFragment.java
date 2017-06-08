package jp.co.soliton.keymanager.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import jp.co.soliton.keymanager.ItemChildDetailCertSetting;
import jp.co.soliton.keymanager.R;
import jp.co.soliton.keymanager.adapter.AdapterSettingDetailCertificate;
import jp.co.soliton.keymanager.dbalias.ElementApply;
import jp.co.soliton.keymanager.dbalias.ElementApplyManager;

import java.util.ArrayList;
import java.util.List;

import static jp.co.soliton.keymanager.activity.SettingTabletActivity.RATIO_SCALE_WIDTH;

/**
 * Created by nguyenducdat on 4/25/2017.
 */

public class ContentDetailCertSettingFragment extends Fragment {

	private View viewFragment;
	private TextView tvTitleHeader;
	private TextView textViewBack;
	private LinearLayout moreOption;
	private ExpandableListView expandableListView;
	private List<String> listDataHeader;
	private List<List<ItemChildDetailCertSetting>> listDataChild;
	private AdapterSettingDetailCertificate adapterSettingDetailCertificate;
	private ElementApplyManager elementMgr;
	private String id;
	ElementApply elementApply;

	public static Fragment newInstance(String id) {
		ContentDetailCertSettingFragment f = new ContentDetailCertSettingFragment();
		f.id = id;
		return f;
	}

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		elementMgr = new ElementApplyManager(getActivity());
		elementApply = elementMgr.getElementApply(id);
		viewFragment = inflater.inflate(R.layout.fragment_setting_detail_certificate, container, false);
		tvTitleHeader = (TextView) viewFragment.findViewById(R.id.tvTitleHeader);
		tvTitleHeader.setText(elementApply.getcNValue());
		textViewBack = (TextView) viewFragment.findViewById(R.id.textViewBack);
		moreOption = (LinearLayout) viewFragment.findViewById(R.id.more_option);
		expandableListView = (ExpandableListView) viewFragment.findViewById(R.id.expand_detail_cert);
		prepareData();
		adapterSettingDetailCertificate = new AdapterSettingDetailCertificate(getActivity(), listDataHeader, listDataChild);
		expandableListView.setAdapter(adapterSettingDetailCertificate);
		expandableListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
			@Override
			public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
				return true;
			}
		});
		expandableListView.setClickable(false);
		return viewFragment;
	}

	@Override
	public void onResume() {
		super.onResume();
		updateTitle();
		textViewBack.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				getActivity().onBackPressed();
			}
		});
		moreOption.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {

			}
		});
	}

	private void prepareData() {
		prepareHeader();
		prepareChild();
	}

	private void prepareHeader() {
		listDataHeader = new ArrayList<>();
		listDataHeader.add("");
		listDataHeader.add(getString(R.string.label_subject_name));
		listDataHeader.add(getString(R.string.label_issuer_name));
		listDataHeader.add(getString(R.string.validity_period));
		listDataHeader.add(getString(R.string.label_signature_algorithm));
		listDataHeader.add(getString(R.string.label_public_key_algorithm));
		listDataHeader.add(getString(R.string.label_basic_constraints));
		listDataHeader.add(getString(R.string.label_key_usage));
		listDataHeader.add(getString(R.string.label_subject_key_identifier));
		listDataHeader.add(getString(R.string.label_authority_key_identifier));
		listDataHeader.add(getString(R.string.label_crl_distribution_points));
		listDataHeader.add(getString(R.string.label_certificate_authority_information_access));
		listDataHeader.add(getString(R.string.label_extended_key_usage));
	}

	private void prepareChild() {
		listDataChild = new ArrayList<>();
		//Add child STORAGE DESTINATION
		List<ItemChildDetailCertSetting> storageDestination = new ArrayList<>();
		String strTarget;
		if (elementApply.getTarger() != null) {
			if (elementApply.getTarger().startsWith("WIFI")) {
				strTarget = getContext().getString(R.string.main_apid_wifi);
			} else {
				strTarget = getContext().getString(R.string.main_apid_vpn);
			}
		}else {
			strTarget = "";
		}
		ItemChildDetailCertSetting storage = new ItemChildDetailCertSetting(getString(R.string.place), strTarget);
		storageDestination.add(storage);

		//Add child SUBJECT NAME
		List<ItemChildDetailCertSetting> subjectName = new ArrayList<>();
		ItemChildDetailCertSetting countryName = new ItemChildDetailCertSetting(getString(R.string.label_country_name),
				elementApply.getSubjectCountryName());
		ItemChildDetailCertSetting stateOrProvinceName = new ItemChildDetailCertSetting(getString(R.string
				.label_state_or_province_name), elementApply.getSubjectStateOrProvinceName());
		ItemChildDetailCertSetting localityName = new ItemChildDetailCertSetting(getString(R.string
				.label_locality_name), elementApply.getSubjectLocalityName());
		ItemChildDetailCertSetting organizationName = new ItemChildDetailCertSetting(getString(R.string
				.label_organization_name), elementApply.getSubjectOrganizationName());
		ItemChildDetailCertSetting commonName = new ItemChildDetailCertSetting(getString(R.string
				.label_common_name), elementApply.getSubjectCommonName());
		ItemChildDetailCertSetting emailAddress = new ItemChildDetailCertSetting(getString(R.string
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
				getString(R.string.label_country_name), elementApply.getIssuerCountryName());
		ItemChildDetailCertSetting issuerStateOrProvinceName = new ItemChildDetailCertSetting(
				getString(R.string.label_state_or_province_name), elementApply.getIssuerStateOrProvinceName());
		ItemChildDetailCertSetting issuerLocalityName = new ItemChildDetailCertSetting(
				getString(R.string.label_locality_name), elementApply.getIssuerLocalityName());
		ItemChildDetailCertSetting issuerOrganizationName = new ItemChildDetailCertSetting(
				getString(R.string.label_organization_name), elementApply.getIssuerOrganizationName());
		ItemChildDetailCertSetting issuerOrganizationUnitName = new ItemChildDetailCertSetting(
				getString(R.string.label_organizational_unit_name), elementApply.getIssuerOrganizationUnitName());
		ItemChildDetailCertSetting issuerCommonName = new ItemChildDetailCertSetting(
				getString(R.string.label_common_name), elementApply.getIssuerCommonName());
		ItemChildDetailCertSetting issuerEmailAdress = new ItemChildDetailCertSetting(
				getString(R.string.label_email_address), elementApply.getIssuerEmailAdress());
		ItemChildDetailCertSetting version = new ItemChildDetailCertSetting(
				getString(R.string.label_issuer_version), elementApply.getVersion());
		ItemChildDetailCertSetting serialNumber = new ItemChildDetailCertSetting(
				getString(R.string.label_issuer_serial_number), elementApply.getSerialNumber());
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
				getString(R.string.label_signature_not_valid_before), elementApply.getNotValidBefore());
		ItemChildDetailCertSetting notValidAfter = new ItemChildDetailCertSetting(
				getString(R.string.label_signature_not_valid_after), elementApply.getNotValidAfter());
		validityPeriod.add(notValidBefore);
		validityPeriod.add(notValidAfter);

		// Add child SIGNATURE
		List<ItemChildDetailCertSetting> signature = new ArrayList<>();
		ItemChildDetailCertSetting signatureAlogrithm = new ItemChildDetailCertSetting(
				getString(R.string.label_algorithm_label), elementApply.getSignatureAlogrithm());
		signature.add(signatureAlogrithm);

		// Add child PUBLIC KEY ALGORITHM
		List<ItemChildDetailCertSetting> publicKeyAlgorithm = new ArrayList<>();
		ItemChildDetailCertSetting algorithm = new ItemChildDetailCertSetting(
				getString(R.string.label_algorithm_label), elementApply.getPublicKeyAlogrithm());
		String strPublicData = elementApply.getPublicKeyData().toUpperCase().replace(":", " ");
		ItemChildDetailCertSetting publicKeyData = new ItemChildDetailCertSetting(
				getString(R.string.label_public_key_algorithm_data), strPublicData, false);
		String strSignature = elementApply.getPublicSignature().toUpperCase().replace(":", " ");
		ItemChildDetailCertSetting signatureData = new ItemChildDetailCertSetting(
				getString(R.string.label_public_key_algorithm_signature), strSignature, false);
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
				getString(R.string.label_certificate_authority), strCertificateAuthority));

		// Add child KEY USAGE
		List<ItemChildDetailCertSetting> keyUsage = new ArrayList<>();
		keyUsage.add(new ItemChildDetailCertSetting(getString(R.string.label_usage), elementApply.getUsage()));

		// Add child SUBJECT KEY IDENTIFIER
		List<ItemChildDetailCertSetting> subjectkeyIdentifier = new ArrayList<>();
		String detailKeyIdentifier = elementApply.getSubjectKeyIdentifier().toUpperCase().replace(":", " ");
		subjectkeyIdentifier.add(new ItemChildDetailCertSetting(getString(R.string.label_subject_key_identifier),
				detailKeyIdentifier, false));

		// Add child AUTHORITY KEY IDENTIFIER
		List<ItemChildDetailCertSetting> authorityKeyIdentifier = new ArrayList<>();
		String detailAuthorKeyIdentifier = elementApply.getAuthorityKeyIdentifier().toUpperCase().replace(":", " ");
		authorityKeyIdentifier.add(new ItemChildDetailCertSetting(getString(R.string.label_authority_key_identifier),
				detailAuthorKeyIdentifier, false));

		// Add child CRL DISTRIBUTION POINTS
		List<ItemChildDetailCertSetting> crlDistributionPoints = new ArrayList<>();
		crlDistributionPoints.add(new ItemChildDetailCertSetting(getString(R.string.label_uri),
				elementApply.getClrDistributionPointUri()));

		// Add child CERTIFICATE AUTHORITY INFORMATION ACCESS
		List<ItemChildDetailCertSetting> certificateAuthority = new ArrayList<>();
		certificateAuthority.add(new ItemChildDetailCertSetting(getString(R.string.label_uri),
				elementApply.getCertificateAuthorityUri()));

		// Add child EXTENDED KEY USAGE
		List<ItemChildDetailCertSetting> extendedKeyUsage = new ArrayList<>();
		extendedKeyUsage.add(new ItemChildDetailCertSetting(getString(R.string.label_purpose),
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
	}

	private void updateTitle() {
		tvTitleHeader.measure(0, 0);
		textViewBack.measure(0, 0);
		viewFragment.measure(0, 0);
		DisplayMetrics displayMetrics = new DisplayMetrics();
		getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
		int width = (int) (displayMetrics.widthPixels * RATIO_SCALE_WIDTH);

		if (tvTitleHeader.getMeasuredWidth() > width - (textViewBack.getMeasuredWidth() * 2)) {
			textViewBack.setText("");
			RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
					RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
			params.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
			params.addRule(RelativeLayout.RIGHT_OF, textViewBack.getId());
			params.addRule(RelativeLayout.LEFT_OF, moreOption.getId());
			tvTitleHeader.setLayoutParams(params);
		}
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		viewFragment = null;
	}
}
