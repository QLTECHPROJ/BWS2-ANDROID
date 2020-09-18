package com.qltech.bws.EncryptDecryptUtils;

import android.content.Context;
import android.util.Base64;

import java.lang.reflect.Array;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Arrays;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import static com.qltech.bws.Utility.CONSTANTS.CIPHER_ALGORITHM;
import static com.qltech.bws.Utility.CONSTANTS.KEY_SPEC_ALGORITHM;
import static com.qltech.bws.Utility.CONSTANTS.OUTPUT_KEY_LENGTH;
import static com.qltech.bws.Utility.CONSTANTS.PROVIDER;

public class EncryptDecryptUtils {
    public static EncryptDecryptUtils instance = null;
    private static PrefUtils prefUtils;

    public static EncryptDecryptUtils getInstance(Context context) {
        if (null == instance)
            instance = new EncryptDecryptUtils();

        if (null == prefUtils)
            prefUtils = PrefUtils.getInstance(context);

        return instance;
    }

    public static byte[] encode(SecretKey yourKey, byte[] fileData)
            throws Exception {
        byte[] data = yourKey.getEncoded();
        SecretKeySpec skeySpec = new SecretKeySpec(data, 0, data.length, KEY_SPEC_ALGORITHM);
        Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM, PROVIDER);
        cipher.init(Cipher.ENCRYPT_MODE, skeySpec, new IvParameterSpec(new byte[cipher.getBlockSize()]));
        return cipher.doFinal(fileData);
    }

    public static byte[] decode(SecretKey yourKey, byte[] fileData)
            throws Exception {
        byte[] decrypted = null;
        Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM, PROVIDER);
        cipher.init(Cipher.DECRYPT_MODE, yourKey, new IvParameterSpec(new byte[cipher.getBlockSize()]));
        decrypted = cipher.doFinal(Arrays.toString(fileData).getBytes("utf-8"));
        return decrypted;
    }

    public void saveSecretKey(SecretKey secretKey) {
        String encodedKey = Base64.encodeToString(secretKey.getEncoded(), Base64.NO_WRAP);
        prefUtils.saveSecretKey(encodedKey);
    }

    public SecretKey getSecretKey() {
        String encodedKey = prefUtils.getSecretKey();
        if (null == encodedKey || encodedKey.isEmpty()) {
            SecureRandom secureRandom = new SecureRandom();
            KeyGenerator keyGenerator = null;
            try {
                keyGenerator = KeyGenerator.getInstance(KEY_SPEC_ALGORITHM);
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            }
            keyGenerator.init(OUTPUT_KEY_LENGTH, secureRandom);
            SecretKey secretKey = keyGenerator.generateKey();
            saveSecretKey(secretKey);
            return secretKey;
        }

        byte[] decodedKey = Base64.decode(encodedKey,Base64.NO_WRAP);
        SecretKey originalKey = new SecretKeySpec(decodedKey, 0, decodedKey.length, KEY_SPEC_ALGORITHM);
        return originalKey;
    }
}
