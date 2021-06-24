package com.brainwellnessspa.utility

import android.util.Base64
import java.io.UnsupportedEncodingException
import java.nio.charset.StandardCharsets
import java.security.*
import javax.crypto.BadPaddingException
import javax.crypto.Cipher
import javax.crypto.IllegalBlockSizeException
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

class CryptLib {
    private enum class EncryptMode {
        ENCRYPT, DECRYPT
    }

    private val _cx: Cipher = Cipher.getInstance("AES/CBC/PKCS5Padding")
    private val _key: ByteArray = ByteArray(32)
    private val _iv: ByteArray = ByteArray(16)

    @Throws(UnsupportedEncodingException::class,
        InvalidKeyException::class,
        InvalidAlgorithmParameterException::class,
        IllegalBlockSizeException::class,
        BadPaddingException::class)
    private fun encryptDecrypt(inputText: String,
        encryptionKey: String,
        mode: EncryptMode,
        initVector: String): ByteArray {
        var len =
            encryptionKey.toByteArray(StandardCharsets.UTF_8).size // length of the key	provided
        if (encryptionKey.toByteArray(StandardCharsets.UTF_8).size > _key.size) len = _key.size
        var ivlength = initVector.toByteArray(StandardCharsets.UTF_8).size
        if (initVector.toByteArray(StandardCharsets.UTF_8).size > _iv.size) ivlength = _iv.size
        System.arraycopy(encryptionKey.toByteArray(StandardCharsets.UTF_8), 0, _key, 0, len)
        System.arraycopy(initVector.toByteArray(StandardCharsets.UTF_8), 0, _iv, 0, ivlength)
        val keySpec = SecretKeySpec(_key,
            "AES") // Create a new SecretKeySpec for the specified key data and algorithm name.
        val ivSpec =
            IvParameterSpec(_iv) // Create a new IvParameterSpec instance with the bytes from the specified buffer iv used as initialization vector.

        // encryption
        return if (mode == EncryptMode.ENCRYPT) { // Potentially insecure random numbers on Android 4.3 and older. Read for more info.
            // https://android-developers.blogspot.com/2013/08/some-securerandom-thoughts.html
            _cx.init(Cipher.ENCRYPT_MODE, keySpec, ivSpec) // Initialize this cipher instance
            _cx.doFinal(inputText.toByteArray(StandardCharsets.UTF_8)) // Finish multi-part transformation (encryption)
        } else {
            _cx.init(Cipher.DECRYPT_MODE, keySpec, ivSpec) // Initialize this cipher instance
            val decodedValue = Base64.decode(inputText.toByteArray(), Base64.NO_WRAP)
            _cx.doFinal(decodedValue) // Finish multi-part transformation (decryption)
        }
    }

    @Throws(Exception::class)
    fun encryptPlainText(plainText: String, key: String, iv: String): String {
        val bytes = encryptDecrypt(plainText, SHA256(key, 32), EncryptMode.ENCRYPT, iv)
        return Base64.encodeToString(bytes, Base64.NO_WRAP)
    }

    @Throws(Exception::class)
    fun decryptCipherText(cipherText: String, key: String, iv: String): String {
        val bytes = encryptDecrypt(cipherText, SHA256(key, 32), EncryptMode.DECRYPT, iv)
        return String(bytes)
    }

    @Throws(Exception::class)
    fun encryptPlainTextWithRandomIV(plainText: String, key: String): String {
        val bytes = encryptDecrypt(generateRandomIV16() + plainText,
            SHA256(key, 32),
            EncryptMode.ENCRYPT,
            generateRandomIV16())
        return Base64.encodeToString(bytes, Base64.NO_WRAP)
    }

    @Throws(Exception::class)
    fun decryptCipherTextWithRandomIV(cipherText: String, key: String): String {
        val bytes =
            encryptDecrypt(cipherText, SHA256(key, 32), EncryptMode.DECRYPT, generateRandomIV16())
        val out = String(bytes)
        return out.substring(16)
    }

    fun generateRandomIV16(): String {
        val ranGen = SecureRandom()
        val aesKey = ByteArray(16)
        ranGen.nextBytes(aesKey)
        val result = StringBuilder()
        for (b in aesKey) {
            result.append(String.format("%02x", b)) //convert to hex
        }
        return if (16 > result.toString().length) {
            result.toString()
        } else {
            result.toString().substring(0, 16)
        }
    }

    companion object {
        @Throws(NoSuchAlgorithmException::class, UnsupportedEncodingException::class)
        private fun SHA256(text: String, length: Int): String {
            val resultString: String
            val md = MessageDigest.getInstance("SHA-256")
            md.update(text.toByteArray(StandardCharsets.UTF_8))
            val digest = md.digest()
            val result = StringBuilder()
            for (b in digest) {
                result.append(String.format("%02x", b)) //convert to hex
            }
            resultString = if (length > result.toString().length) {
                result.toString()
            } else {
                result.toString().substring(0, length)
            }
            return resultString
        }
    }

    init { //256 bit key space
        //128 bit IV
    }
}