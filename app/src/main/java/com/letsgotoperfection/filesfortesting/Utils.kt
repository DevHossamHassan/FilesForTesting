package com.letsgotoperfection.filesfortesting

import android.content.ContentResolver
import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import java.io.File
import java.io.FilterOutputStream
import java.io.IOException
import java.io.OutputStream
import java.text.SimpleDateFormat
import java.util.*

interface AttachmentStoreHelper {
    fun writeAttachment(output: (OutputStream) -> Unit): Pair<Uri, Long>
}

class Utils(context: Context) : AttachmentStoreHelper {

    private val fileDateFormat = SimpleDateFormat("yyMMdd-HHmmss", Locale.US)
    val SUB_DIR = "Testing Documents"
    val PUBLIC_DIR = Environment.DIRECTORY_DOWNLOADS
    private val contentResolver = context.contentResolver

    private val volumeName: String = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        MediaStore.VOLUME_EXTERNAL_PRIMARY
    } else {
        "external"
    }


    override fun writeAttachment(output: (OutputStream) -> Unit): Pair<Uri, Long> {
        val filename = getFilename()
        return writeToMediaStore(filename, output)
    }

    private fun writeToMediaStore(filename: String, output: (OutputStream) -> Unit): Pair<Uri, Long> {
        val values = ContentValues()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            values.put(MediaStore.MediaColumns.IS_PENDING, 1)
            values.put(MediaStore.MediaColumns.DISPLAY_NAME, filename)
            values.put(MediaStore.MediaColumns.RELATIVE_PATH, PUBLIC_DIR + File.separator + SUB_DIR)
        } else {
            @Suppress("DEPRECATION") // suppressed as deprecated method only accessed on supporting platforms
            val dir = File(Environment.getExternalStoragePublicDirectory(PUBLIC_DIR), SUB_DIR)
            if (!dir.exists()) dir.mkdirs()
            values.put(MediaStore.MediaColumns.DATA, File(dir, filename).path)
        }

        val uri: Uri = contentResolver.insert(getMediaStoreUri(), values)
            ?: throw IOException("Could not insert media")

        try {
            val outputStream = contentResolver.openOutputStream(uri, "w")
                ?.let { CountingOutputStream(it) }
                ?: throw IOException("Could not open output stream")

            outputStream.use { output.invoke(it) }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                values.clear()
                values.put(MediaStore.MediaColumns.IS_PENDING, 0)
                contentResolver.update(uri, values, null, null)
            }

            return uri to outputStream.count
        } catch (e: Exception) {
            contentResolver.safeDelete(uri)
            throw e
        }
    }

    private fun getMediaStoreUri(): Uri {
        return MediaStore.Files.getContentUri(volumeName)
    }

    private fun getFilename(): String {
        val prefix: String = "VCard-"

//        val extension: String = MimeTypeMap.getSingleton().getExtensionFromMimeType(mimeType)
//            ?.let { ".$it" }
//            .orEmpty()
        val extension: String = ".vcf"

        return "$prefix-${fileDateFormat.format(Date())}-$extension"
    }

    private class CountingOutputStream(out: OutputStream) : FilterOutputStream(out) {
        var count = 0L
            private set

        override fun write(b: ByteArray) {
            out.write(b)
            count += b.size
        }

        override fun write(b: Int) {
            out.write(b)
            ++count
        }

        override fun write(b: ByteArray, off: Int, len: Int) {
            out.write(b, off, len)
            count += len
        }
    }

    fun ContentResolver.safeDelete(uri: Uri): Int = try {
        delete(uri, null, null)
    } catch (e: IOException) {
        0
    } catch (e: RuntimeException) {
        0
    }

}