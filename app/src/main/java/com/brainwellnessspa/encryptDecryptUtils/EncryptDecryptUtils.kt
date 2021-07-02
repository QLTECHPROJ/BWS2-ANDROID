package com.brainwellnessspa.encryptDecryptUtils

import android.content.Context
import android.util.Base64
import com.brainwellnessspa.utility.CONSTANTS.CIPHER_ALGORITHM
import com.brainwellnessspa.utility.CONSTANTS.KEY_SPEC_ALGORITHM
import com.brainwellnessspa.utility.CONSTANTS.OUTPUT_KEY_LENGTH
import com.brainwellnessspa.utility.CONSTANTS.PROVIDER
import java.security.NoSuchAlgorithmException
import java.security.SecureRandom
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

class EncryptDecryptUtils {
    fun saveSecretKey(secretKey: SecretKey) {
        val encodedKey = Base64.encodeToString(secretKey.encoded, Base64.NO_WRAP)
        prefUtils!!.saveSecretKey(encodedKey)
    }

    val secretKey: SecretKey
        get() {
            val encodedKey = prefUtils!!.secretKey
            if (null == encodedKey || encodedKey.isEmpty()) {
                val secureRandom = SecureRandom()
                var keyGenerator: KeyGenerator? = null
                try {
                    keyGenerator = KeyGenerator.getInstance(KEY_SPEC_ALGORITHM)
                } catch (e: NoSuchAlgorithmException) {
                    e.printStackTrace()
                }
                if (keyGenerator != null) {
                    keyGenerator.init(OUTPUT_KEY_LENGTH, secureRandom)
                }
                val secretKey = keyGenerator!!.generateKey()
                saveSecretKey(secretKey)
                return secretKey
            }
            val decodedKey = Base64.decode(encodedKey, Base64.NO_WRAP)
            return SecretKeySpec(decodedKey, 0, decodedKey.size, KEY_SPEC_ALGORITHM)
        }

    companion object {
        var instance: EncryptDecryptUtils? = null
        private var prefUtils: PrefUtils? = null
        @JvmStatic
        fun getInstance(context: Context?): EncryptDecryptUtils? {
            if (null == instance) instance = EncryptDecryptUtils()
            if (null == prefUtils) prefUtils = PrefUtils.getInstance(context)
            return instance
        }

        @Throws(Exception::class)
        fun encode(yourKey: SecretKey, fileData: ByteArray?): ByteArray {
            val data = yourKey.encoded
            val skeySpec = SecretKeySpec(data, 0, data.size, KEY_SPEC_ALGORITHM)
            val cipher: Cipher = Cipher.getInstance(CIPHER_ALGORITHM, PROVIDER)
            cipher.init(Cipher.ENCRYPT_MODE, skeySpec, IvParameterSpec(ByteArray(cipher.blockSize)))
            return cipher.doFinal(fileData)
        }

        @JvmStatic
        @Throws(Exception::class)
        fun decode(yourKey: SecretKey?, fileData: ByteArray?): ByteArray {
            val decrypted: ByteArray? = null
            val cipher: Cipher = Cipher.getInstance(CIPHER_ALGORITHM, PROVIDER)
            cipher.init(Cipher.DECRYPT_MODE, yourKey, IvParameterSpec(ByteArray(cipher.blockSize)))
            //        decrypted = cipher.doFinal(Arrays.toString(fileData).getBytes("utf-8"));
            return cipher.doFinal(fileData)
        }
    }
}