package com.anta40.capuploader.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.Security;
import java.security.spec.AlgorithmParameterSpec;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;

import org.spongycastle.jce.provider.BouncyCastleProvider;

public class CryptoUtil {
	private static Cipher decrypt;
	private static final byte[] initialization_vector = { 22, 33, 11, 44, 55, 99, 66, 77 };
	
	static {
		Security.insertProviderAt(new BouncyCastleProvider(), 1);
	}
	
	public static String decryptFile (String filePath){
		String decryptedText = "";
		
		try {
			SecretKey secret_key = KeyGenerator.getInstance("DESede").generateKey();
			AlgorithmParameterSpec alogrithm_specs = new IvParameterSpec(initialization_vector);
			
			decrypt = Cipher.getInstance("DESede/CBC/PKCS5Padding");
			decrypt.init(Cipher.DECRYPT_MODE, secret_key, alogrithm_specs);
			
			File inputFile = new File(filePath);
			
			decryptedText = decrypt_wrapper(new FileInputStream(inputFile));
			
		}
		catch (NoSuchAlgorithmException nsae){
			
		}
		catch (NoSuchPaddingException nspe){
			
		}
		catch (InvalidKeyException ike){
			
		}
		catch (InvalidAlgorithmParameterException iape){
			
		}
		catch (IOException ioe) {
			
		}
		return decryptedText;
	}
	
	private static String decrypt_wrapper(InputStream input) throws IOException {

		input = new CipherInputStream(input, decrypt);
        return getStringFromInputStream(input);
	}
	
	private static String getStringFromInputStream(InputStream is) {
		 
		BufferedReader br = null;
		StringBuilder sb = new StringBuilder();
 
		String line;
		try {
 
			br = new BufferedReader(new InputStreamReader(is));
			while ((line = br.readLine()) != null) {
				sb.append(line);
                sb.append(System.getProperty("line.separator"));
			}
 
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
 
		return sb.toString();
 
	}
}
