package jp.co.soliton.keymanager.scep.pkimessage;

import org.bouncycastle.asn1.DERObjectIdentifier;
import org.bouncycastle.asn1.nist.NISTObjectIdentifiers;
import org.bouncycastle.asn1.oiw.OIWObjectIdentifiers;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.smime.SMIMECapabilities;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x509.X509ObjectIdentifiers;

import java.util.HashMap;
import java.util.Map;

public final class AlgorithmDictionary {
        private static final String PADDING = "PKCS5Padding";

        private static final String MODE = "CBC";
        
        private final static Map<DERObjectIdentifier, String> contents = new HashMap<DERObjectIdentifier, String>();
        static {
                // Asymmetric Ciphers
                contents.put(PKCSObjectIdentifiers.rsaEncryption, "RSA");
                // Digital Signatures
                contents.put(PKCSObjectIdentifiers.sha1WithRSAEncryption, "SHA1withRSA");
                contents.put(new DERObjectIdentifier("1.2.840.113549.1.1.4"), "md5withRSA");
                contents.put(new DERObjectIdentifier("1.2.840.113549.1.1.11"), "sha256withRSA");
                contents.put(new DERObjectIdentifier("1.2.840.113549.1.1.13"), "sha512withRSA");
                // Symmetric Ciphers
                contents.put(SMIMECapabilities.dES_CBC, "DES/CBC/PKCS5Padding"); // DES
                contents.put(SMIMECapabilities.dES_EDE3_CBC, "DESede/CBC/PKCS5Padding"); // DESEDE
                // Message Digests
                contents.put(X509ObjectIdentifiers.id_SHA1, "SHA");
                contents.put(new DERObjectIdentifier("1.2.840.113549.2.5"), "MD5");
                contents.put(new DERObjectIdentifier("2.16.840.1.101.3.4.2.1"), "SHA-256");
                contents.put(new DERObjectIdentifier("2.16.840.1.101.3.4.2.3"), "SHA-512");
        }
        
        private final static Map<String, DERObjectIdentifier> oids = new HashMap<String, DERObjectIdentifier>();
        static {
                // Cipher
                oids.put("DES/CBC/PKCS5Padding", OIWObjectIdentifiers.desCBC);
                oids.put("DESede/CBC/PKCS5Padding", PKCSObjectIdentifiers.des_EDE3_CBC);
                // KeyFactory or KeyPairGenerator
                oids.put("RSA", PKCSObjectIdentifiers.rsaEncryption);
                // KeyGenerator, AlgorithmParameters or SecretKeyFactory
                oids.put("DES", null);
                oids.put("DESede", null);
                // MessageDigest
                oids.put("MD5", PKCSObjectIdentifiers.md5);
                oids.put("SHA-1", X509ObjectIdentifiers.id_SHA1);
                oids.put("SHA-256", NISTObjectIdentifiers.id_sha256);
                oids.put("SHA-512", NISTObjectIdentifiers.id_sha512);
                // Signature
                oids.put("MD5withRSA", PKCSObjectIdentifiers.md5WithRSAEncryption);
                oids.put("SHA1withRSA", PKCSObjectIdentifiers.sha1WithRSAEncryption);
                oids.put("SHA256withRSA", PKCSObjectIdentifiers.sha256WithRSAEncryption);
                oids.put("SHA512withRSA", PKCSObjectIdentifiers.sha512WithRSAEncryption);
        }
        
        private AlgorithmDictionary() {
                // This constructor will never be invoked.
        }
        
        public static DERObjectIdentifier getOid(String algorithm) {
                final DERObjectIdentifier oid = oids.get(algorithm);
                
                return oid;
        }
        
        public static AlgorithmIdentifier getAlgId(String algorithm) {
                DERObjectIdentifier oid = getOid(algorithm);
                if (oid == null) {
                        return null;
                } else {
                        return new AlgorithmIdentifier(oid);
                }
        }
        
        public static String getTransformation(String cipher) throws IllegalArgumentException {
                if (cipher.equalsIgnoreCase("DES") || cipher.equalsIgnoreCase("DESede")) {
                        return cipher + "/" + MODE + "/" + PADDING;
                } else {
                        throw new IllegalArgumentException(cipher + " is not an appropriate cipher name");
                }
        }
        
        public static String fromTransformation(String transformation) {
                return transformation.split("/")[0];
        }
        
        @SuppressWarnings("deprecation")
		public static String lookup(AlgorithmIdentifier alg) {
                return contents.get(alg.getObjectId());
        }
        
        public static String getRSASignatureAlgorithm(String hashAlgorithm) {
                if (hashAlgorithm.equals("SHA")) {
                        return "SHA1withRSA";
                } else if (hashAlgorithm.startsWith("SHA-")) {
                        return hashAlgorithm.replace("-", "") + "withRSA";
                } else {
                        return hashAlgorithm + "withRSA";
                }
        }
}
