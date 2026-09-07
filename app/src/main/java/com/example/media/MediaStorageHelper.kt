package com.example.media

import android.content.Context
import android.graphics.Bitmap
import android.media.MediaMetadataRetriever
import android.net.Uri
import java.io.File
import java.io.FileOutputStream

object MediaStorageHelper {

    fun copyUriToInternalStorage(
        context: Context,
        uri: Uri,
        subDir: String,
        prefix: String,
        extension: String
    ): String? {
        return try {
            val baseDir = File(context.filesDir, "my_corner_media/$subDir").apply {
                if (!exists()) mkdirs()
            }
            val targetFile = File(baseDir, "${prefix}_${System.currentTimeMillis()}.$extension")
            context.contentResolver.openInputStream(uri)?.use { inputStream ->
                FileOutputStream(targetFile).use { outputStream ->
                    inputStream.copyTo(outputStream)
                }
            }
            targetFile.absolutePath
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun getVideoThumbnail(context: Context, videoPathOrUri: String): Bitmap? {
        if (videoPathOrUri.isBlank()) return null
        val retriever = MediaMetadataRetriever()
        return try {
            if (videoPathOrUri.startsWith("content://")) {
                retriever.setDataSource(context, Uri.parse(videoPathOrUri))
            } else {
                val file = File(videoPathOrUri)
                if (file.exists()) {
                    retriever.setDataSource(file.absolutePath)
                } else {
                    retriever.setDataSource(videoPathOrUri)
                }
            }
            retriever.getFrameAtTime(0, MediaMetadataRetriever.OPTION_CLOSEST_SYNC)
                ?: retriever.frameAtTime
        } catch (e: Exception) {
            e.printStackTrace()
            null
        } finally {
            try {
                retriever.release()
            } catch (e: Exception) {
                // Ignore
            }
        }
    }

    fun deleteInternalFile(path: String?): Boolean {
        if (path.isNullOrBlank()) return false
        return try {
            val file = File(path)
            if (file.exists()) file.delete() else false
        } catch (e: Exception) {
            false
        }
    }
}
