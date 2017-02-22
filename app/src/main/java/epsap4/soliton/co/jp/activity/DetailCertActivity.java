package epsap4.soliton.co.jp.activity;

import android.app.AlarmManager;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;


import org.bouncycastle.asn1.ASN1InputStream;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DERIA5String;
import org.bouncycastle.asn1.DERObject;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.x509.AccessDescription;
import org.bouncycastle.asn1.x509.AuthorityInformationAccess;
import org.bouncycastle.asn1.x509.AuthorityKeyIdentifier;
import org.bouncycastle.asn1.x509.BasicConstraints;
import org.bouncycastle.asn1.x509.CRLDistPoint;
import org.bouncycastle.asn1.x509.DistributionPoint;
import org.bouncycastle.asn1.x509.DistributionPointName;
import org.bouncycastle.asn1.x509.ExtendedKeyUsage;
import org.bouncycastle.asn1.x509.GeneralName;
import org.bouncycastle.asn1.x509.GeneralNames;
import org.bouncycastle.asn1.x509.SubjectKeyIdentifier;
import org.bouncycastle.asn1.x509.X509Extensions;
import org.bouncycastle.jce.PrincipalUtil;
import org.bouncycastle.x509.extension.X509ExtensionUtil;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.net.URL;
import java.security.PublicKey;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateParsingException;
import java.security.cert.X509Certificate;
import java.security.interfaces.RSAPublicKey;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import epsap4.soliton.co.jp.LogCtrl;
import epsap4.soliton.co.jp.R;
import epsap4.soliton.co.jp.StringList;
import epsap4.soliton.co.jp.CommonCertificate;
import epsap4.soliton.co.jp.adapter.AdapterDetailCertificate;
import epsap4.soliton.co.jp.adapter.ItemDetalCert;
import epsap4.soliton.co.jp.dbalias.DatabaseHandler;
import epsap4.soliton.co.jp.dbalias.ItemAlias;
import epsap4.soliton.co.jp.notification.AlarmReceiver;
import epsap4.soliton.co.jp.notification.SKMNotification;


/**
 * Created by daoanhtung on 12/27/2016.
 */

public class DetailCertActivity extends AppCompatActivity {
    //UI param
    private TextView textViewNameCertificate;
    private TextView textViewCertificates;
    private TextView textViewMenu;
    private ListView listviewInforCert;
    private AdapterDetailCertificate adapterDetailCertificate;
    private Dialog dialogChooseCertOperation = null;
    //Certificate
    private X509Certificate x509Certificate = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detailcert);
        textViewNameCertificate = (TextView) findViewById(R.id.textViewNameCertificate);
        textViewCertificates = (TextView) findViewById(R.id.textViewCertificates);
        textViewMenu = (TextView) findViewById(R.id.textViewMenu);
        listviewInforCert = (ListView) findViewById(R.id.listviewInforCert);
        Intent intent = getIntent();
        if (intent != null) {
            setDataViewCertificate(intent);
        }
        setOnClickListener();
    }


    @Override
    protected void onStop() {
        if (dialogChooseCertOperation != null)
            dialogChooseCertOperation.dismiss();
        super.onStop();
    }

    /**
     * This method returns onclick textViewCertificates, textViewMenu
     */
    private void setOnClickListener() {
        textViewCertificates.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DetailCertActivity.this, ListCertActivity.class);
                startActivity(intent);
                finish();
            }
        });
        textViewMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // custom dialog
                dialogChooseCertOperation = new Dialog(DetailCertActivity.this);
                dialogChooseCertOperation.setContentView(R.layout.dialog_menu_detail_cert);
                TextView textviewDeleteCert = (TextView) dialogChooseCertOperation.findViewById(R.id.textviewDeleteCert);
                TextView textviewNotificationOne = (TextView) dialogChooseCertOperation.findViewById(R.id.textviewNotificationOne);
                TextView textviewCancleDialog = (TextView) dialogChooseCertOperation.findViewById(R.id.textviewCancleDialog);
                textviewDeleteCert.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialogChooseCertOperation.dismiss();
                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(DetailCertActivity.this);
                        alertDialogBuilder.setMessage(getString(R.string.msg_dialog_delete_cert));
                        alertDialogBuilder.setPositiveButton(getString(R.string.label_yes),
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface arg0, int arg1) {
                                        deleteAlias();
                                    }
                                });

                        alertDialogBuilder.setNegativeButton(getString(R.string.label_no), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                        AlertDialog alertDialog = alertDialogBuilder.create();
                        alertDialog.show();
                    }
                });
                textviewNotificationOne.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialogChooseCertOperation.dismiss();
                        DatabaseHandler databaseHandler = new DatabaseHandler(DetailCertActivity.this);
                        Intent intent = new Intent(DetailCertActivity.this, SettingNotificationOneCertActivity.class);
                        ItemAlias itemAlias = databaseHandler.getAlias(x509Certificate.getNotAfter().getTime());
                        intent.putExtra(StringList.m_str_alias_skm, itemAlias);
                        startActivity(intent);
                    }
                });
                textviewCancleDialog.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialogChooseCertOperation.dismiss();
                    }
                });

                dialogChooseCertOperation.show();

            }
        });
    }

    /**
     * This method delete alias in DB
     */
    private void deleteAlias() {
        DatabaseHandler databaseHandler = new DatabaseHandler(DetailCertActivity.this);
        if (x509Certificate != null) {
            ItemAlias itemAlias = databaseHandler.getAlias(x509Certificate.getNotAfter().getTime());
            AlarmManager alarmMgr = (AlarmManager) getSystemService(ALARM_SERVICE);
            Intent intent = new Intent(DetailCertActivity.this, AlarmReceiver.class);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(DetailCertActivity.this, itemAlias.getiD(), intent, 0);
            alarmMgr.cancel(pendingIntent);
            databaseHandler.deleteAlias(itemAlias);
            finish();
        }
    }

    /**
     * @param intent
     */
    private void setDataViewCertificate(Intent intent) {
        x509Certificate = (X509Certificate) intent.getSerializableExtra(StringList.m_str_InformCtrl);
        if (x509Certificate != null) {
            textViewNameCertificate.setText(CommonCertificate.getPartName(x509Certificate.getSubjectDN().getName(), StringList.m_str_common_name_certificate));
            LogCtrl.Logger(LogCtrl.m_strInfo, x509Certificate.toString(), DetailCertActivity.this);
            List<ItemDetalCert> itemDetalCertList = new ArrayList<ItemDetalCert>();
            getDataX509Certificate(itemDetalCertList);
            getDataX509CertificateExtension(itemDetalCertList);
            adapterDetailCertificate = new AdapterDetailCertificate(this, itemDetalCertList);
            listviewInforCert.setAdapter(adapterDetailCertificate);
        }
    }

    /**
     * This method get data of certificate for X509Certificate
     *
     * @param itemDetalCertList
     */
    private void getDataX509Certificate(List<ItemDetalCert> itemDetalCertList) {
        itemDetalCertList.add(new ItemDetalCert(getString(R.string.label_subject_name).toUpperCase(), 0));
        String subjectName = "";
        String issuerName = "";
        try {
            subjectName = PrincipalUtil.getSubjectX509Principal(x509Certificate).toString();
            issuerName = PrincipalUtil.getIssuerX509Principal(x509Certificate).toString();
        } catch (CertificateEncodingException e) {
            LogCtrl.Logger(LogCtrl.m_strError, e.toString(),DetailCertActivity.this);
        }

        itemDetalCertList.add(new ItemDetalCert(getString(R.string.label_country_name), CommonCertificate.getPartName(subjectName, StringList.m_str_country_name_certificate), 1));
        itemDetalCertList.add(new ItemDetalCert(getString(R.string.label_state_or_province_name), CommonCertificate.getPartName(subjectName, StringList.m_str_street_name_certificate), 1));
        itemDetalCertList.add(new ItemDetalCert(getString(R.string.label_locality_name), CommonCertificate.getPartName(subjectName, StringList.m_str_locality_name_certificate), 1));
        itemDetalCertList.add(new ItemDetalCert(getString(R.string.label_organization_name), CommonCertificate.getPartName(subjectName, StringList.m_str_organization_name_certificate), 1));
        itemDetalCertList.add(new ItemDetalCert(getString(R.string.label_common_name), CommonCertificate.getPartName(subjectName, StringList.m_str_common_name_certificate), 1));

        itemDetalCertList.add(new ItemDetalCert(getString(R.string.label_issuer_name).toUpperCase(), 0));
        try {
            issuerName = PrincipalUtil.getIssuerX509Principal(x509Certificate).toString();
        } catch (CertificateEncodingException e) {
            LogCtrl.Logger(LogCtrl.m_strError, e.toString(),DetailCertActivity.this);
        }
        itemDetalCertList.add(new ItemDetalCert(getString(R.string.label_country_name), CommonCertificate.getPartName(issuerName, StringList.m_str_country_name_certificate), 1));
        itemDetalCertList.add(new ItemDetalCert(getString(R.string.label_state_or_province_name), CommonCertificate.getPartName(issuerName, StringList.m_str_street_name_certificate), 1));
        itemDetalCertList.add(new ItemDetalCert(getString(R.string.label_locality_name), CommonCertificate.getPartName(issuerName, StringList.m_str_locality_name_certificate), 1));
        itemDetalCertList.add(new ItemDetalCert(getString(R.string.label_organization_name), CommonCertificate.getPartName(issuerName, StringList.m_str_organization_name_certificate), 1));
        itemDetalCertList.add(new ItemDetalCert(getString(R.string.label_organizational_unit_name), CommonCertificate.getPartName(issuerName, StringList.m_str_organization_unit_name_certificate), 1));
        itemDetalCertList.add(new ItemDetalCert(getString(R.string.label_common_name), CommonCertificate.getPartName(issuerName, StringList.m_str_common_name_certificate), 1));
        itemDetalCertList.add(new ItemDetalCert(getString(R.string.label_email_address), CommonCertificate.getPartName(issuerName, StringList.m_str_email_name_certificate), 1));
        itemDetalCertList.add(new ItemDetalCert(getString(R.string.label_issuer_version), String.valueOf(x509Certificate.getVersion()), 1));
        itemDetalCertList.add(new ItemDetalCert(getString(R.string.label_issuer_serial_number), String.valueOf(x509Certificate.getSerialNumber()), 1));
        itemDetalCertList.add(new ItemDetalCert(getString(R.string.label_signature_algorithm).toUpperCase(), 0));
        itemDetalCertList.add(new ItemDetalCert(getString(R.string.label_algorithm), x509Certificate.getSigAlgName(), 1));
        if (x509Certificate.getNotBefore() != null) {
            String dateBefore = SKMNotification.format.format(x509Certificate.getNotBefore());
            itemDetalCertList.add(new ItemDetalCert(getString(R.string.label_signature_not_valid_before), dateBefore.toString(), 1));

        }
        if (x509Certificate.getNotAfter() != null) {
            String dateAfter = SKMNotification.format.format(x509Certificate.getNotAfter());
            itemDetalCertList.add(new ItemDetalCert(getString(R.string.label_signature_not_valid_after), dateAfter.toString(), 1));
        }
        itemDetalCertList.add(new ItemDetalCert(getString(R.string.label_public_key_algorithm).toUpperCase(), 0));
        PublicKey pk = x509Certificate.getPublicKey();
        RSAPublicKey rsaPub = (RSAPublicKey) (pk);
        BigInteger modulus = rsaPub.getModulus();
        itemDetalCertList.add(new ItemDetalCert(getString(R.string.label_algorithm), pk.getAlgorithm().toString(), 1));
        itemDetalCertList.add(new ItemDetalCert(getString(R.string.label_public_key_algorithm_data), addSignModulus(modulus.toString(16)), 1));
        itemDetalCertList.add(new ItemDetalCert(getString(R.string.label_public_key_algorithm_signature), byteArrayToHex(x509Certificate.getSignature()).toString(), 1));
        itemDetalCertList.add(new ItemDetalCert(getString(R.string.label_basic_constraints).toUpperCase(), 0));
        BasicConstraints bc = getBasicConstraints(x509Certificate);
        if (bc.isCA()) {
            itemDetalCertList.add(new ItemDetalCert(getString(R.string.label_certificate_authority), "TRUE", 1));
        } else {
            itemDetalCertList.add(new ItemDetalCert(getString(R.string.label_certificate_authority), "FALSE", 1));
        }
    }

    /**
     * This method get data of certificate for X509CertificateExtension
     *
     * @param itemDetalCertList
     */
    private void getDataX509CertificateExtension(List<ItemDetalCert> itemDetalCertList) {
        itemDetalCertList.add(new ItemDetalCert(getString(R.string.label_key_usage).toUpperCase(), 0));
        itemDetalCertList.add(new ItemDetalCert(getString(R.string.label_usage), getKeyUser(x509Certificate.getKeyUsage()), 1));
        itemDetalCertList.add(new ItemDetalCert(getString(R.string.label_subject_key_identifier).toUpperCase(), 0));
        itemDetalCertList.add(new ItemDetalCert(getString(R.string.label_subject_key_identifier), byteArrayToHex(getSKID(x509Certificate).getKeyIdentifier()).toUpperCase(), 1));
        itemDetalCertList.add(new ItemDetalCert(getString(R.string.label_authority_key_identifier).toUpperCase(), 0));
        itemDetalCertList.add(new ItemDetalCert(getString(R.string.label_authority_key_identifier), byteArrayToHex(getAKID(x509Certificate).getKeyIdentifier()).toUpperCase(), 1));
        itemDetalCertList.add(new ItemDetalCert(getString(R.string.label_crl_distribution_points), 0));
        try {
            if (getCrlDistributionPoints(x509Certificate) != null) {
                itemDetalCertList.add(new ItemDetalCert(getString(R.string.label_uri), getCrlDistributionPoints(x509Certificate).get(0), 1));
            } else {
                itemDetalCertList.add(new ItemDetalCert(getString(R.string.label_uri), "", 1));
            }
        } catch (Exception e) {
            itemDetalCertList.add(new ItemDetalCert(getString(R.string.label_uri), "", 1));
        }
        itemDetalCertList.add(new ItemDetalCert(getString(R.string.label_certificate_authority_information_access).toUpperCase(), 0));
        if (getOcspUrlFromCertificate(x509Certificate) != null) {
            itemDetalCertList.add(new ItemDetalCert(getString(R.string.label_uri), getOcspUrlFromCertificate(x509Certificate).toString(), 1));
        } else {
            itemDetalCertList.add(new ItemDetalCert(getString(R.string.label_uri), "", 1));
        }
        itemDetalCertList.add(new ItemDetalCert(getString(R.string.label_extended_key_usage), 0));
        if (getextendedKeyUsage(x509Certificate) != null) {
            itemDetalCertList.add(new ItemDetalCert(getString(R.string.label_purpose), getPurposeExtendedKeyUsage(getextendedKeyUsage(x509Certificate)), 1));
        } else {
            itemDetalCertList.add(new ItemDetalCert(getString(R.string.label_purpose), "", 1));
        }
    }


    /**
     * @param keyUser
     * @return
     */
    private String getKeyUser(boolean[] keyUser) {
        String usage = "";
        if (keyUser != null) {
            if (keyUser.length == 9) {
                if (keyUser[0]) {
                    usage = usage + getString(R.string.label_digital_signature) + ", ";
                }
                if (keyUser[1]) {
                    usage = usage + getString(R.string.label_non_repudiation) + ", ";
                }
                if (keyUser[2]) {
                    usage = usage + getString(R.string.label_key_encipherment) + ", ";
                }

                if (keyUser[3]) {
                    usage = usage + getString(R.string.label_data_encipherment) + ", ";
                }
                if (keyUser[4]) {
                    usage = usage + getString(R.string.label_key_agreement) + ", ";
                }
                if (keyUser[5]) {
                    usage = usage + getString(R.string.label_key_cert_sign) + ", ";
                }
                if (keyUser[6]) {
                    usage = usage + getString(R.string.label_crl_sign) + ", ";
                }
                if (keyUser[7]) {
                    usage = usage + getString(R.string.label_encipher_only) + ", ";
                }
                if (keyUser[8]) {
                    usage = usage + getString(R.string.label_decipher_only) + ", ";
                }
                if (usage.length() > 2) {
                    if (usage.substring(usage.length() - 2, usage.length()).compareTo(", ") == 0)
                        usage = usage.substring(0, usage.length() - 2);
                }
                return usage;
            }
        }
        return "";
    }

    /**
     * @param module
     * @return
     */
    private String addSignModulus(String module) {
        if (module != null) {
            int size = module.length() / 2;
            String temp = "";
            for (int i = 0; i < size; i++) {
                temp = temp + module.substring(2 * i, 2 * i + 2) + ":";
            }
            if (temp.length() > 0) {
                return "00:" + temp.substring(0, temp.length() - 1);
            }
            return temp;
        }
        return "";
    }

    /**
     * This method get BasicConstraints of Certificate
     *
     * @param cert
     * @return BasicConstraints
     */
    private BasicConstraints getBasicConstraints(X509Certificate cert) {
        if (cert != null) {
            byte[] akid = cert.getExtensionValue("2.5.29.19");
            if (akid != null) {
                DERObject dobj = null;
                try {
                    dobj = new ASN1InputStream(new ByteArrayInputStream(akid)).readObject();
                    dobj = new ASN1InputStream(new ByteArrayInputStream(((DEROctetString) dobj).getOctets())).readObject();
                } catch (Exception e) {
                    LogCtrl.Logger(LogCtrl.m_strError, e.toString(), DetailCertActivity.this);
                }

                return BasicConstraints.getInstance(ASN1Sequence.getInstance(dobj));
            }
        }
        return null;
    }

    /**
     * This method get SubjectKeyIdentifier of Certificate
     *
     * @param cert
     * @return SubjectKeyIdentifier
     */
    private SubjectKeyIdentifier getSKID(X509Certificate cert) {
        if (cert != null) {
            byte[] akid = cert.getExtensionValue("2.5.29.14");
            if (akid != null) {
                DERObject dobj = null;
                try {
                    dobj = new ASN1InputStream(new ByteArrayInputStream(akid)).readObject();
                    dobj = new ASN1InputStream(new ByteArrayInputStream(((DEROctetString) dobj).getOctets())).readObject();
                } catch (Exception e) {
                    LogCtrl.Logger(LogCtrl.m_strError, "SubjectKeyIdentifier: " + e.toString(), DetailCertActivity.this);
                }
                return SubjectKeyIdentifier.getInstance(dobj);
            }
        }
        return null;
    }

    /**
     * This method get AuthorityKeyIdentifier of Certificate
     *
     * @param cert
     * @return AuthorityKeyIdentifier
     */
    private AuthorityKeyIdentifier getAKID(X509Certificate cert) {
        if (cert != null) {
            byte[] akid = cert.getExtensionValue("2.5.29.35");
            if (akid != null) {
                DERObject dobj = null;
                try {
                    dobj = new ASN1InputStream(new ByteArrayInputStream(akid)).readObject();
                    dobj = new ASN1InputStream(new ByteArrayInputStream(((DEROctetString) dobj).getOctets())).readObject();
                } catch (Exception e) {
                    LogCtrl.Logger(LogCtrl.m_strError, "AuthorityKeyIdentifier: " + e.toString(), DetailCertActivity.this);
                }
                return AuthorityKeyIdentifier.getInstance(dobj);
            }
        }
        return null;
    }

    /**
     * This method convert byte[] to Hex
     *
     * @param source
     * @return String
     */
    private String byteArrayToHex(byte[] source) {
        StringBuilder sb = new StringBuilder(source.length * 2);
        for (byte temp : source) {
            sb.append(String.format("%02x", temp & 0xff) + ":");
        }
        if (sb.length() > 0) {
            return sb.substring(0, sb.length() - 1);
        }
        return sb.toString();
    }

    /**
     * This method get distribution point URLs from the "CRL Distribution Point"
     *
     * @param cert
     * @return
     * @throws CertificateParsingException
     * @throws IOException
     */
    private List<String> getCrlDistributionPoints(
            X509Certificate cert) throws CertificateParsingException, IOException {
        byte[] crldpExt = null;
        crldpExt = cert.getExtensionValue(
                X509Extensions.CRLDistributionPoints.getId());
        if (crldpExt != null) {
            ASN1InputStream oAsnInStream = new ASN1InputStream(
                    new ByteArrayInputStream(crldpExt));
            DERObject derObjCrlDP = oAsnInStream.readObject();
            DEROctetString dosCrlDP = (DEROctetString) derObjCrlDP;
            byte[] crldpExtOctets = dosCrlDP.getOctets();
            ASN1InputStream oAsnInStream2 = new ASN1InputStream(
                    new ByteArrayInputStream(crldpExtOctets));
            DERObject derObj2 = oAsnInStream2.readObject();
            CRLDistPoint distPoint = CRLDistPoint.getInstance(derObj2);
            List<String> crlUrls = new ArrayList<String>();
            for (DistributionPoint dp : distPoint.getDistributionPoints()) {
                DistributionPointName dpn = dp.getDistributionPoint();
                // Look for URIs in fullName
                if (dpn != null) {
                    if (dpn.getType() == DistributionPointName.FULL_NAME) {
                        GeneralName[] genNames = GeneralNames.getInstance(
                                dpn.getName()).getNames();
                        // Look for an URI
                        for (int j = 0; j < genNames.length; j++) {
                            if (genNames[j].getTagNo() == GeneralName.uniformResourceIdentifier) {
                                String url = DERIA5String.getInstance(
                                        genNames[j].getName()).getString();
                                crlUrls.add(url);
                            }
                        }
                    }
                }
            }
            return crlUrls;
        }
        return null;
    }

    /**
     * This method get url AuthorityInfoAccess of X509Extensions
     *
     * @param certificate
     * @return
     */
    private URL getOcspUrlFromCertificate(X509Certificate certificate) {
        byte[] octetBytes = certificate.getExtensionValue(X509Extensions.AuthorityInfoAccess.getId());
        URL url = null;
        if (null != octetBytes) {
            try {
                byte[] encoded = X509ExtensionUtil.fromExtensionValue(octetBytes).getEncoded();
                ASN1InputStream oAsnInStream = new ASN1InputStream(
                        new ByteArrayInputStream(encoded));
                DERObject derObjCrlDP = oAsnInStream.readObject();
                ASN1Sequence seq = ASN1Sequence.getInstance(derObjCrlDP);
                AuthorityInformationAccess access = AuthorityInformationAccess.getInstance(seq);
                for (AccessDescription accessDescription : access.getAccessDescriptions()) {
                    if (accessDescription.getAccessMethod().equals(AccessDescription.id_ad_ocsp)) {
                        url = new URL(accessDescription.getAccessLocation().getName().toString());
                        break;
                    }
                }
            } catch (IOException ignore) {
                LogCtrl.Logger(LogCtrl.m_strError, "AuthorityInfoAccess: " + ignore.toString(), DetailCertActivity.this);
            }
        }

        return url;
    }

    /**
     * This method get ExtendedKeyUsage of X509Extensions
     *
     * @param certificate
     * @return ExtendedKeyUsage
     */
    private ExtendedKeyUsage getextendedKeyUsage(X509Certificate certificate) {
        byte[] octetBytes = certificate.getExtensionValue(X509Extensions.ExtendedKeyUsage.getId());

        DERObject derObjCrlDP = null;
        try {
            if (octetBytes != null) {
                byte[] encoded = X509ExtensionUtil.fromExtensionValue(octetBytes).getEncoded();
                ASN1InputStream oAsnInStream = new ASN1InputStream(
                        new ByteArrayInputStream(encoded));
                derObjCrlDP = oAsnInStream.readObject();
                ASN1Sequence asn1Sequence = ASN1Sequence.getInstance(derObjCrlDP);
                ExtendedKeyUsage extendedKeyUsage = new ExtendedKeyUsage(asn1Sequence);
                return extendedKeyUsage;
            }
        } catch (IOException e) {
            LogCtrl.Logger(LogCtrl.m_strError, "ExtendedKeyUsage: " + e.toString(), DetailCertActivity.this);
        }
        return null;
    }

    /**
     * This method get Purpose of ExtendedKeyUsage
     *
     * @param extendedKeyUsage
     * @return
     */
    private String getPurposeExtendedKeyUsage(ExtendedKeyUsage extendedKeyUsage) {
        if (extendedKeyUsage != null) {
            Vector vector = extendedKeyUsage.getUsages();
            if (vector != null) {
                String purpose = "";
                for (int i = 0; i < vector.size(); i++) {
                    if (vector.get(i).toString().compareTo("1.3.6.1.5.5.7.3.1") == 0)
                        purpose = purpose + getString(R.string.label_server_auth) + ", ";
                    if (vector.get(i).toString().compareTo("1.3.6.1.5.5.7.3.2") == 0)
                        purpose = purpose + getString(R.string.label_client_auth) + ", ";
                    if (vector.get(i).toString().compareTo("1.3.6.1.5.5.7.3.3") == 0)
                        purpose = purpose + getString(R.string.label_code_signing) + ", ";
                    if (vector.get(i).toString().compareTo("1.3.6.1.5.5.7.3.4") == 0)
                        purpose = purpose + getString(R.string.label_email_protection) + ", ";
                    if (vector.get(i).toString().compareTo("1.3.6.1.5.5.7.3.5") == 0)
                        purpose = purpose + getString(R.string.label_ipsec_end_system) + ", ";
                    if (vector.get(i).toString().compareTo("1.3.6.1.5.5.7.3.6") == 0)
                        purpose = purpose + getString(R.string.label_ipsec_tunnel) + ", ";
                    if (vector.get(i).toString().compareTo("1.3.6.1.5.5.7.3.7") == 0)
                        purpose = purpose + getString(R.string.label_ipsec_user) + ", ";
                    if (vector.get(i).toString().compareTo("1.3.6.1.5.5.7.3.8") == 0)
                        purpose = purpose + getString(R.string.label_time_stamping) + ", ";
                    if (vector.get(i).toString().compareTo("1.3.6.1.5.5.7.3.9") == 0)
                        purpose = purpose + getString(R.string.label_ocsp_signing) + ", ";
                }
                if (purpose.length() > 1) {
                    return purpose.substring(0, purpose.length() - 2);
                }
            }
        }
        return "";
    }

}
