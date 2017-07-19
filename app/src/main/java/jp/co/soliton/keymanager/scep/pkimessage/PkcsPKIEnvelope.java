package jp.co.soliton.keymanager.scep.pkimessage;

import org.bouncycastle.asn1.ASN1Set;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.cms.EncryptedContentInfo;
import org.bouncycastle.asn1.cms.EnvelopedData;
import org.bouncycastle.asn1.cms.KeyTransRecipientInfo;
import org.bouncycastle.asn1.cms.RecipientInfo;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.cms.CMSEnvelopedData;
import org.bouncycastle.cms.CMSException;

import javax.crypto.*;
import java.io.IOException;
import java.security.*;

public class PkcsPKIEnvelope extends CMSEnvelopedData {

	public PkcsPKIEnvelope(byte[] envelopedData) throws CMSException {
		super(envelopedData);
		// TODO Auto-generated constructor stub
	}

	public byte[] getContents(PrivateKey privateKey) {
		final EnvelopedData envData = EnvelopedData.getInstance(getContentInfo().getContent());
        final EncryptedContentInfo contentInfo = envData.getEncryptedContentInfo();
        final AlgorithmIdentifier contentAlg = contentInfo.getContentEncryptionAlgorithm();
        final String transformationName = getCipherName(contentAlg);
        final String cipherName = AlgorithmDictionary.fromTransformation(transformationName);

        Cipher decryptingCipher;
        AlgorithmParameters params;
        try {
        	decryptingCipher = Cipher.getInstance(transformationName);
        	params = AlgorithmParameters.getInstance(cipherName);
        	DEROctetString paramsString = (DEROctetString) contentAlg.getParameters();
        	params.init(paramsString.getEncoded());
        } catch (NoSuchAlgorithmException e) {
        	e.printStackTrace();
        	return null;
        } catch (NoSuchPaddingException e) {
        	e.printStackTrace();
        	return null;
        } catch (IOException e) {
        	e.printStackTrace();
        	return null;
        }
        
		final ASN1Set recipientInfos = envData.getRecipientInfos();
        if (recipientInfos.size() == 0) {
        	return null;
        }
        byte[] encryptedContentBytes = contentInfo.getEncryptedContent().getOctets();
        RecipientInfo encodable = RecipientInfo.getInstance(recipientInfos.getObjectAt(0));
        KeyTransRecipientInfo keyTrans = KeyTransRecipientInfo.getInstance(encodable.getInfo());
        byte[] wrappedKey = keyTrans.getEncryptedKey().getOctets();
        // Decrypt the secret key
        Cipher cipher;
		try {
			
			String keyEncAld = AlgorithmDictionary.lookup(keyTrans.getKeyEncryptionAlgorithm());
			cipher = Cipher.getInstance(keyEncAld);
	        cipher.init(Cipher.UNWRAP_MODE, privateKey);
	        SecretKey secretKey = (SecretKey) cipher.unwrap(wrappedKey, cipherName, Cipher.SECRET_KEY);
	        decryptingCipher.init(Cipher.DECRYPT_MODE, secretKey, params);
	        byte[] contentBytes = decryptingCipher.doFinal(encryptedContentBytes);
	        return contentBytes;
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			return null;
		} catch (NoSuchPaddingException e) {
			e.printStackTrace();
			return null;
		} catch (InvalidKeyException e) {
			e.printStackTrace();
			return null;
		} catch (InvalidAlgorithmParameterException e) {
			e.printStackTrace();
			return null;
		} catch (IllegalBlockSizeException e) {
			e.printStackTrace();
			return null;
		} catch (BadPaddingException e) {
			e.printStackTrace();
			return null;
		}
	}

	private String getCipherName(AlgorithmIdentifier algId) {
        return AlgorithmDictionary.lookup(algId);
	}
}
