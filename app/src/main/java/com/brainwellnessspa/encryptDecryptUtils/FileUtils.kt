package com.brainwellnessspa.encryptDecryptUtils

import android.content.Context
import android.util.Log
import com.brainwellnessspa.utility.CONSTANTS
import com.brainwellnessspa.utility.CONSTANTS.TEMP_FILE_NAME
import java.io.*

object FileUtils {
    fun saveFile(encodedBytes: ByteArray?, path: String?) {
        try {
            val file = File(path)
            val bos = BufferedOutputStream(FileOutputStream(file))
            bos.write(encodedBytes)
            bos.flush()
            bos.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    @JvmStatic
    fun readFile(filePath: String?): ByteArray {
        val contents: ByteArray
        val file = File(filePath)
        val size = file.length().toInt()
        contents = ByteArray(size)
        try {
            val buf = BufferedInputStream(FileInputStream(file))
            try {
                buf.read(contents)
                buf.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        }
        return contents
    }

    fun readFile1(filePath: String?): File {
        return File(filePath)
    }

    @Throws(IOException::class)
    fun createTempFile(context: Context, decrypted: ByteArray?): File {
        val tempFile = File.createTempFile(TEMP_FILE_NAME, CONSTANTS.FILE_EXT, context.cacheDir)
        //        tempFile.deleteOnExit();
        val fos = FileOutputStream(tempFile)
        fos.write(decrypted)
        fos.close()
        return tempFile
    }

    @Throws(IOException::class)
    fun getTempFileDescriptor(context: Context, decrypted: ByteArray?): FileDescriptor {
        val tempFile = createTempFile(context, decrypted)
        val fis = FileInputStream(tempFile)
        return fis.fd
    }

    @Throws(IOException::class)
    fun getTempFileDescriptor1(context: Context, decrypted: ByteArray?): File {
        val tempFile = createTempFile(context, decrypted)
        val fis = FileInputStream(tempFile)
        return tempFile
    }

    @JvmStatic
    fun getDirPath(context: Context): String {
        return context.getDir("Audio", Context.MODE_PRIVATE).absolutePath
    }

    fun getDirPath1(context: Context): String {
        return context.getDir("PDF", Context.MODE_PRIVATE).absolutePath
    }

    @JvmStatic
    fun getFilePath(context: Context, FILE_NAME: String): String {
        return getDirPath(context) + File.separator + FILE_NAME + CONSTANTS.FILE_EXT
    }

    fun getFilePath1(context: Context, FILE_NAME: String): String {
        return getDirPath1(context) + File.separator + FILE_NAME + ".pdf"
    }

    @JvmStatic
    fun deleteDownloadedFile(context: Context, FILE_NAME: String) {
        val file = File(getFilePath(context, FILE_NAME + CONSTANTS.FILE_EXT))
        if (null != file && file.exists()) {
            if (file.delete()) Log.i("FileUtils", "File Deleted.")
        }
    }

    fun deleteDownloadedFile1(context: Context, FILE_NAME: String) {
        val file = File(getFilePath(context, FILE_NAME))
        if (null != file && file.exists()) {
            if (file.delete()) Log.i("FileUtils", "File Deleted.")
        }
    }
}