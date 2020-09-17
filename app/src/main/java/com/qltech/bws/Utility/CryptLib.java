package com.qltech.bws.Utility;import android.util.Base64;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class CryptLib {

    private enum EncryptMode {
        ENCRYPT, DECRYPT
    }

    private Cipher _cx;
    private byte[] _key, _iv;

    public CryptLib() throws NoSuchAlgorithmException, NoSuchPaddingException {
        _cx = Cipher.getInstance("AES/CBC/PKCS5Padding");
        _key = new byte[32]; //256 bit key space
        _iv = new byte[16]; //128 bit IV
    }

    private byte[] encryptDecrypt(String inputText, String encryptionKey,
                                  EncryptMode mode, String initVector) throws UnsupportedEncodingException,
            InvalidKeyException, InvalidAlgorithmParameterException,
            IllegalBlockSizeException, BadPaddingException {

        int len = encryptionKey.getBytes(StandardCharsets.UTF_8).length; // length of the key	provided

        if (encryptionKey.getBytes(StandardCharsets.UTF_8).length > _key.length)
            len = _key.length;

        int ivlength = initVector.getBytes(StandardCharsets.UTF_8).length;

        if(initVector.getBytes(StandardCharsets.UTF_8).length > _iv.length)
            ivlength = _iv.length;

        System.arraycopy(encryptionKey.getBytes(StandardCharsets.UTF_8), 0, _key, 0, len);
        System.arraycopy(initVector.getBytes(StandardCharsets.UTF_8), 0, _iv, 0, ivlength);


        SecretKeySpec keySpec = new SecretKeySpec(_key, "AES"); // Create a new SecretKeySpec for the specified key data and algorithm name.

        IvParameterSpec ivSpec = new IvParameterSpec(_iv); // Create a new IvParameterSpec instance with the bytes from the specified buffer iv used as initialization vector.

        // encryption
        if (mode.equals(EncryptMode.ENCRYPT)) {
            // Potentially insecure random numbers on Android 4.3 and older. Read for more info.
            // https://android-developers.blogspot.com/2013/08/some-securerandom-thoughts.html
            _cx.init(Cipher.ENCRYPT_MODE, keySpec, ivSpec);// Initialize this cipher instance
            return _cx.doFinal(inputText.getBytes(StandardCharsets.UTF_8)); // Finish multi-part transformation (encryption)
        } else {
            _cx.init(Cipher.DECRYPT_MODE, keySpec, ivSpec);// Initialize this cipher instance

            byte[] decodedValue = Base64.decode(inputText.getBytes(), Base64.NO_WRAP);
            return _cx.doFinal(decodedValue); // Finish multi-part transformation (decryption)
        }
    }

    private static String SHA256 (String text, int length) throws NoSuchAlgorithmException, UnsupportedEncodingException {

        String resultString;
        MessageDigest md = MessageDigest.getInstance("SHA-256");

        md.update(text.getBytes(StandardCharsets.UTF_8));
        byte[] digest = md.digest();

        StringBuilder result = new StringBuilder();
        for (byte b : digest) {
            result.append(String.format("%02x", b)); //convert to hex
        }

        if(length > result.toString().length()) {
            resultString = result.toString();
        } else {
            resultString = result.toString().substring(0, length);
        }

        return resultString;

    }


    public String encryptPlainText(String plainText, String key, String iv) throws Exception {
        byte[] bytes = encryptDecrypt(plainText, CryptLib.SHA256(key, 32), EncryptMode.ENCRYPT, iv);
        return Base64.encodeToString(bytes, Base64.NO_WRAP);
    }

    public String decryptCipherText(String cipherText, String key, String iv) throws Exception {
        byte[] bytes = encryptDecrypt(cipherText, CryptLib.SHA256(key, 32), EncryptMode.DECRYPT, iv);
        return new String(bytes);
    }


    public String encryptPlainTextWithRandomIV(String plainText, String key) throws Exception {
        byte[] bytes = encryptDecrypt(generateRandomIV16() + plainText, CryptLib.SHA256(key, 32), EncryptMode.ENCRYPT, generateRandomIV16());
        return Base64.encodeToString(bytes, Base64.NO_WRAP);
    }

    public String decryptCipherTextWithRandomIV(String cipherText, String key) throws Exception {
        byte[] bytes = encryptDecrypt(cipherText, CryptLib.SHA256(key, 32), EncryptMode.DECRYPT, generateRandomIV16());
        String out = new String(bytes);
        return out.substring(16);
    }

    public String generateRandomIV16() {
        SecureRandom ranGen = new SecureRandom();
        byte[] aesKey = new byte[16];
        ranGen.nextBytes(aesKey);
        StringBuilder result = new StringBuilder();
        for (byte b : aesKey) {
            result.append(String.format("%02x", b)); //convert to hex
        }
        if (16 > result.toString().length()) {
            return result.toString();
        } else {
            return result.toString().substring(0, 16);
        }
    }

}