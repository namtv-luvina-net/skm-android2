package jp.co.soliton.keymanager.scep.pkimessage;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.Provider;
import java.security.Security;
import java.security.cert.X509Certificate;

import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.cms.CMSEnvelopedData;
import org.bouncycastle.cms.CMSEnvelopedDataGenerator;
import org.bouncycastle.cms.CMSException;
import org.bouncycastle.cms.CMSProcessable;
import org.bouncycastle.cms.CMSProcessableByteArray;

@SuppressWarnings("deprecation")
public class PkcsPKIEnvelopeGenerator {

	public static PkcsPKIEnvelope generate(
			ASN1Encodable messageData,
			X509Certificate recipientCertificate) {
		
		CMSEnvelopedDataGenerator envelopedDataGenerator =
				new CMSEnvelopedDataGenerator();
		

		try {
			CMSProcessable plainMessageData = new CMSProcessableByteArray(messageData.getEncoded());
			Provider[] providers = Security.getProviders("KeyGenerator.DESEDE");
			envelopedDataGenerator.addKeyTransRecipient(recipientCertificate);
			CMSEnvelopedData envelopedData =
					envelopedDataGenerator.generate(
							plainMessageData,
							CMSEnvelopedDataGenerator.DES_EDE3_CBC,
							providers[0]);
			return new PkcsPKIEnvelope(envelopedData.getEncoded());
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (CMSException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
}
