package ru.xoxmuk.votifierfork.crypto;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

public class RSAIO {

	public static void save(File directory, KeyPair keyPair) throws Exception {
		PrivateKey privateKey = keyPair.getPrivate();
		PublicKey publicKey = keyPair.getPublic();

		X509EncodedKeySpec publicSpec = new X509EncodedKeySpec(publicKey.getEncoded());
		FileOutputStream out = null;
		try {
			out = new FileOutputStream(directory + "/public.key");
			out.write(Base64.getEncoder().encodeToString(publicSpec.getEncoded()).getBytes());
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (out != null) {
				out.close();
			}
		}

		PKCS8EncodedKeySpec privateSpec = new PKCS8EncodedKeySpec(privateKey.getEncoded());
		try {
			out = new FileOutputStream(directory + "/private.key");
			out.write(Base64.getEncoder().encodeToString(privateSpec.getEncoded()).getBytes());
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (out != null) {
				out.close();
			}
		}
	}

	public static KeyPair load(File directory) throws Exception {

		File publicKeyFile = new File(directory + "/public.key");
		byte[] encodedPublicKey = null;
		try (FileInputStream in = new FileInputStream(publicKeyFile)) {
			encodedPublicKey = new byte[(int) publicKeyFile.length()];
			in.read(encodedPublicKey);
			encodedPublicKey = Base64.getDecoder().decode(encodedPublicKey);
		} catch (Exception e) {
			e.printStackTrace();
		}

		File privateKeyFile = new File(directory + "/private.key");
		byte[] encodedPrivateKey = null;
		try (FileInputStream in = new FileInputStream(privateKeyFile)) {
			encodedPrivateKey = new byte[(int) privateKeyFile.length()];
			in.read(encodedPrivateKey);
			encodedPrivateKey = Base64.getDecoder().decode(encodedPrivateKey);
		} catch (Exception e) {
			e.printStackTrace();
		}

		KeyFactory keyFactory = KeyFactory.getInstance("RSA");
		X509EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(encodedPublicKey);
		PublicKey publicKey = keyFactory.generatePublic(publicKeySpec);
		PKCS8EncodedKeySpec privateKeySpec = new PKCS8EncodedKeySpec(encodedPrivateKey);
		PrivateKey privateKey = keyFactory.generatePrivate(privateKeySpec);
		return new KeyPair(publicKey, privateKey);
	}

}
