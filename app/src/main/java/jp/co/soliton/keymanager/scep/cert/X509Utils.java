package jp.co.soliton.keymanager.scep.cert;

import org.bouncycastle.asn1.*;
import org.bouncycastle.asn1.x509.*;
import org.bouncycastle.x509.extension.AuthorityKeyIdentifierStructure;
import org.bouncycastle.x509.extension.SubjectKeyIdentifierStructure;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateParsingException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by luongdolong on 4/4/2017.
 */

public class X509Utils {
    static final Map<String, String> PURPOSE = new HashMap<String , String>() {{
        put("1.3.6.1.5.5.7.3.0", "Any extended key usage");
        put("1.3.6.1.5.5.7.3.1", "TLS Web server authentication");
        put("1.3.6.1.5.5.7.3.2", "TLS Web client authentication");
        put("1.3.6.1.5.5.7.3.3", "Code Signing");
        put("1.3.6.1.5.5.7.3.4", "E-mail protection");
        put("1.3.6.1.5.5.7.3.5", "IP security end system");
        put("1.3.6.1.5.5.7.3.6", "IP security tunnel termination");
        put("1.3.6.1.5.5.7.3.7", "IP security user");
        put("1.3.6.1.5.5.7.3.8", "Timestamping");
        put("1.3.6.1.5.5.7.3.9", "OCSP signing");
        put("1.3.6.1.5.5.7.3.10", "Data validation");
        put("1.3.6.1.5.5.7.3.11", "");
        put("1.3.6.1.5.5.7.3.12", "Server based certification validation protocol responder");
        put("1.3.6.1.5.5.7.3.13", "EAP over PPP");
        put("1.3.6.1.5.5.7.3.14", "EAP over LAM");
        put("1.3.6.1.5.5.7.3.15", "Server based certification validation protocol responder");
        put("1.3.6.1.5.5.7.3.16", "Server based certification validation protocol client");
        put("1.3.6.1.5.5.7.3.17", "Internet key exchange");
        put("1.3.6.1.5.5.7.3.18", "Control And Provisioning of Wireless Access Points, Access Controller");
        put("1.3.6.1.5.5.7.3.19", "Control And Provisioning of Wireless Access Points, Wireless Termination Points");
        put("1.3.6.1.4.1.311.20.2.2", "Microsoft-specific usage for smart-card-based authentication");
    }};

    public static byte[] getAuthorityKeyIdentifier(X509Certificate certificate) throws CertificateException {
        byte[] result = null;
        try {
            byte[] extvalue = certificate.getExtensionValue(X509Extensions.AuthorityKeyIdentifier.getId());
            if (extvalue != null) {
                AuthorityKeyIdentifier keyId = new AuthorityKeyIdentifierStructure(extvalue);
                result = keyId.getKeyIdentifier();
            }
        } catch (IOException e) {
            throw new CertificateException("Error retrieving certificate authority key identifier for subject "
                    +certificate.getSubjectX500Principal().getName(), e);
        }
        return result;
    }
    public static byte[] getSubjectKeyIdentifier(X509Certificate certificate) throws CertificateException {
        byte[] result = null;
        try {
            byte[] extvalue = certificate.getExtensionValue(X509Extensions.SubjectKeyIdentifier.getId());
            if (extvalue != null) {
                SubjectKeyIdentifier keyId = new SubjectKeyIdentifierStructure(extvalue);
                result = keyId.getKeyIdentifier();
            }
        } catch (IOException e) {
            throw new CertificateException("Error retrieving certificate authority key identifier for subject "
                    +certificate.getSubjectX500Principal().getName(), e);
        }
        return result;
    }

    /**
     * Gets the URL of the Certificate Revocation List for a Certificate
     * @param certificate	the Certificate
     * @return	the String where you can check if the certificate was revoked
     * @throws CertificateParsingException
     * @throws IOException
     */
    public static String getCRLURL(X509Certificate certificate) throws CertificateParsingException {
        DERObject obj;
        try {
            obj = getExtensionValue(certificate, X509Extensions.CRLDistributionPoints.getId());
        } catch (IOException e) {
            obj = null;
        }
        if (obj == null) {
            return null;
        }
        CRLDistPoint dist = CRLDistPoint.getInstance(obj);
        DistributionPoint[] dists = dist.getDistributionPoints();
        for (DistributionPoint p : dists) {
            DistributionPointName distributionPointName = p.getDistributionPoint();
            if (DistributionPointName.FULL_NAME != distributionPointName.getType()) {
                continue;
            }
            GeneralNames generalNames = (GeneralNames)distributionPointName.getName();
            GeneralName[] names = generalNames.getNames();
            for (GeneralName name : names) {
                if (name.getTagNo() != GeneralName.uniformResourceIdentifier) {
                    continue;
                }
                DERIA5String derStr = DERIA5String.getInstance((ASN1TaggedObject)name.toASN1Object(), false);
                return derStr.getString();
            }
        }
        return null;
    }

    public static String getPurpose(X509Certificate certificate) throws CertificateParsingException {
        StringBuilder builder = new StringBuilder();
        List<String> lsPurpose = certificate.getExtendedKeyUsage();
        for (String e : lsPurpose) {
            if (PURPOSE.containsKey(e)) {
                if (builder.length() > 0) {
                    builder.append(PURPOSE.get(e) + " ");
                } else {
                    builder.append(PURPOSE.get(e));
                }
            }
        }
        return builder.toString();
    }

    /**
     * Authority Information Access (AIA) is a non-critical extension in an X509 Certificate. This contains the
     * URL of the OCSP endpoint if one is available.
     * TODO: This might contain non OCSP urls as well. Handle this.
     *
     * @param cert is the certificate
     * @return a lit of URLs in AIA extension of the certificate which will hopefully contain an OCSP endpoint.
     *
     */
    public static List<String> getAIALocations(X509Certificate cert) {

        //Gets the DER-encoded OCTET string for the extension value for Authority information access Points
        byte[] aiaExtensionValue = cert.getExtensionValue(X509Extensions.AuthorityInfoAccess.getId());
        if (aiaExtensionValue == null) {
            return null;
        }
        AuthorityInformationAccess authorityInformationAccess = null;

        try {
            DEROctetString oct = (DEROctetString) (new ASN1InputStream(new ByteArrayInputStream(aiaExtensionValue)).readObject());
            authorityInformationAccess = AuthorityInformationAccess.getInstance(new ASN1InputStream(oct.getOctets()).readObject());
        } catch (IOException e) {
            return null;
        }

        List<String> ocspUrlList = new ArrayList<>();
        AccessDescription[] accessDescriptions = authorityInformationAccess.getAccessDescriptions();
        for (AccessDescription accessDescription : accessDescriptions) {
            GeneralName gn = accessDescription.getAccessLocation();
            if (gn.getTagNo() == GeneralName.uniformResourceIdentifier) {
                DERIA5String str = DERIA5String.getInstance(gn.getName());
                String accessLocation = str.getString();
                ocspUrlList.add(accessLocation);
            }
        }
        return ocspUrlList;
    }

    private static DERObject getExtensionValue(X509Certificate certificate, String oid) throws IOException {
        byte[] bytes = certificate.getExtensionValue(oid);
        if (bytes == null) {
            return null;
        }
        ASN1InputStream aIn = new ASN1InputStream(new ByteArrayInputStream(bytes));
        ASN1OctetString octs = (ASN1OctetString) aIn.readObject();
        aIn = new ASN1InputStream(new ByteArrayInputStream(octs.getOctets()));
        return aIn.readObject();
    }
}
