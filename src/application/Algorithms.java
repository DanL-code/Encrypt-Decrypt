package application;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Properties;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;

public class Algorithms {

	private String codebook = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789abcdefghijklmnopqrstuvwxyz !/\"#$%&'()*+,-./:;<=>?@[]^_`{|}~";

	public String caesarCipherEncrypt(String plainText, int keyE) {
		String cipherText = "";
		for (int i = 0; i < plainText.length(); i++) {
			char plainCharacter = plainText.charAt(i);
			int position = codebook.indexOf(plainCharacter);
			int newPosition = Math.floorMod((position + keyE), codebook.length());
			char cipherCharacter = codebook.charAt(newPosition);
			cipherText += cipherCharacter;

		}
		return cipherText;
	}

	public String caesarCipherDecrypt(String plainText, int keyD) {
		String cipherText = "";
		for (int i = 0; i < plainText.length(); i++) {
			char plainCharacter = plainText.charAt(i);
			int position = codebook.indexOf(plainCharacter);
			int newPosition = Math.floorMod((position - keyD), codebook.length());
			char cipherCharacter = codebook.charAt(newPosition);
			cipherText += cipherCharacter;

		}
		return cipherText;
	}

	public String DESEncrypt(DESSimple des1, String msg) {
		String result = null;
		try {
			System.out.println("The plain text: " + msg);
			byte[] encText = des1.encrypt(msg);
			result = Base64.getEncoder().encodeToString(encText);
			System.out.println(result);
			System.out.println(encText);

		} catch (Exception e) {
			System.out.println("Error in DES: " + e);
			e.printStackTrace();
		}
		return result;
	}

	public String DESDecrypt(String keyDES, String str) {
		String result = null;

		try {
			DESSimple des2 = new DESSimple();
			byte[] keyBytes = Base64.getDecoder().decode(keyDES);
			SecretKey secretKey = new SecretKeySpec(keyBytes, "DES");
			byte[] b = Base64.getDecoder().decode(str);
			des2.setSecretkey(secretKey);
//			System.out.println(b);
			result = des2.decrypt(b);
//			System.out.println(result);

		} catch (Exception e) {
			System.out.println("Error in DES: " + e);
			e.printStackTrace();
		}
		return result;
	}

	public String AESEncrypt(AESSimple aes1, String msg) {
		String result = null;
		try {
			System.out.println("The plain text: " + msg);
			byte[] encText = aes1.encrypt(msg);
			result = Base64.getEncoder().encodeToString(encText);
			System.out.println(result);
			System.out.println(encText);

		} catch (Exception e) {
			System.out.println("Error in DES: " + e);
			e.printStackTrace();
		}
		return result;
	}

	public String AESEncrypt(String masterKey, String msg) {
		String result = null;
		try {
			AESSimple aes = new AESSimple();
			byte[] keyBytes = Base64.getDecoder().decode(masterKey);
			SecretKey aesMasterKey = new SecretKeySpec(keyBytes, "AES");
			aes.setSecretKey(aesMasterKey);
			System.out.println("The plain text: " + msg);
			byte[] encText = aes.encrypt(msg);
			result = Base64.getEncoder().encodeToString(encText);
			System.out.println(result);
			System.out.println(encText);

		} catch (Exception e) {
			System.out.println("Error in AES: " + e);
			e.printStackTrace();
		}
		return result;
	}

	public String AESDecrypt(String keyAES, String str) {
		String result = null;

		try {
			AESSimple aes2 = new AESSimple();
			
			//testing masterKey
//			System.out.println("ldld1111_keyAES:" + keyAES);
//			System.out.println("Key Length: " + keyAES.length());
//			String master = "KWQfiAHrxoDYlTKd2yVELA==";
//			System.out.println("Key Length: " + master.length());
			
			byte[] keyBytes = Base64.getDecoder().decode(keyAES);
			SecretKey secretKey = new SecretKeySpec(keyBytes, "AES");
			byte[] b = Base64.getDecoder().decode(str);
			aes2.setSecretKey(secretKey);
//			System.out.println(b);
			result = aes2.decrypt(b);
//			System.out.println(result);

		} catch (Exception e) {
			System.out.println("Error in AES: " + e);
			e.printStackTrace();
		}
		return result;
	}

	// Source: https://www.geeksforgeeks.org/md5-hash-in-java/
	public String hashPassword(String input) {
		try {

			// Static getInstance method is called with hashing MD5
			MessageDigest md = MessageDigest.getInstance("MD5");

			// digest() method is called to calculate message digest
			// of an input digest() return array of byte
			byte[] messageDigest = md.digest(input.getBytes());

			// Convert byte array into signum representation
			BigInteger no = new BigInteger(1, messageDigest);

			// Convert message digest into hex value
			String hashtext = no.toString(16);
			while (hashtext.length() < 32) {
				hashtext = "0" + hashtext;
			}
			return hashtext;
		}

		// For specifying wrong message digest algorithms
		catch (NoSuchAlgorithmException e) {
			throw new RuntimeException(e);
		}
	}
	public void saveKeyFile(String fileName, String savedKey) throws Exception {
		ObjectOutputStream outputStr = new ObjectOutputStream(new FileOutputStream(fileName));
		byte[] savedKeyByte = Base64.getDecoder().decode(savedKey);
		SecretKey secretKey = new SecretKeySpec(savedKeyByte, "AES");
		// Write the SecretKey object to the file
		outputStr.writeObject(secretKey);
		outputStr.close();
	}

	public String loadKeyFile(String fileName) throws Exception {
		ObjectInputStream inputStr = new ObjectInputStream(new FileInputStream(fileName));
		SecretKey fileKey = (SecretKey) inputStr.readObject();
		byte[] fileKeyByte = fileKey.getEncoded();
		String fileKeyStr = Base64.getEncoder().encodeToString(fileKeyByte);
		// Write the SecretKey object to the file
		inputStr.close();
		return fileKeyStr;
	}
	
	public void showErrorInfo(Stage primaryStage, String errorStr) {
		Alert alert = new Alert(Alert.AlertType.ERROR, errorStr, ButtonType.OK);
		alert.initOwner(primaryStage);
		alert.showAndWait();
	}

	public void showConfirmation(Stage primaryStage, String errorStr) {
		Alert alert = new Alert(Alert.AlertType.INFORMATION, errorStr, ButtonType.OK);
		alert.initOwner(primaryStage);
		alert.showAndWait();
	}
	
	public String getMasterKey() {
		Properties properties = new Properties();
		try (InputStream input = getClass().getClassLoader().getResourceAsStream("config.properties")) {
		    properties.load(input);
		} catch (IOException e) {
		    e.printStackTrace();
		}
		String masterK = properties.getProperty("masterKey");
		return masterK;
	}



}
