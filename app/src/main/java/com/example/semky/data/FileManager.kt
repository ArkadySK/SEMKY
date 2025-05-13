package com.example.semky.data

import android.content.Context
import android.net.Uri
import android.os.Environment
import java.io.File
import java.io.FileOutputStream
import java.util.UUID

class FileManager(private val context: Context) {
    private val attachmentsDir: File by lazy {
        File(context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), "attachments").apply {
            if (!exists()) {
                mkdirs()
            }
        }
    }

    fun saveAttachment(uri: Uri): Long {
        val inputStream = context.contentResolver.openInputStream(uri)
        val fileName = UUID.randomUUID().toString()
        val file = File(attachmentsDir, fileName)
        
        FileOutputStream(file).use { outputStream ->
            inputStream?.use { input ->
                input.copyTo(outputStream)
            }
        }
        
        return file.lastModified()
    }

    fun getAttachmentFile(attachmentId: Long): File? {
        return attachmentsDir.listFiles()?.find { it.lastModified() == attachmentId }
    }

    fun deleteAttachment(attachmentId: Long) {
        getAttachmentFile(attachmentId)?.delete()
    }
} 