package com.episen.eshop_ms_membership.setting;


import java.io.FileInputStream;
import java.security.Key;
import java.security.KeyPair;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.cert.Certificate;

public class InfraSettings {

	public static KeyPair keypairLoader() {
		

		try (FileInputStream is = new FileInputStream("C:\\personnel\\Hajar_EPISEN\\ING3\\S5\\Orchestration_Données\\hajar-episen-dm\\src\\main\\resources\\Keys\\server.p12")) {
			KeyStore kstore = KeyStore.getInstance("PKCS12");
			kstore.load(is, "hajar".toCharArray());
			
			Key key = kstore.getKey("episen", "hajar".toCharArray());
			
			Certificate certificate = kstore.getCertificate("episen");
			
			return new KeyPair(certificate.getPublicKey(), (PrivateKey) key);
			
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
