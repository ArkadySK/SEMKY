package com.example.semky.data

import android.content.Context
import android.net.Uri
import android.os.Environment
import java.io.File
import java.io.FileOutputStream
import java.util.UUID

/**
 * Správa súborov pre prílohy v aplikácii.
 * Stará sa o ukladanie, získavanie a mazanie príloh v externom úložisku aplikácie.
 *
 * @property context Kontext aplikácie používaný pre operácie so súbormi
 */
class FileManager(private val context: Context) {
    private val attachmentsDir: File by lazy {
        File(context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), "attachments").apply {
            if (!exists()) {
                mkdirs()
            }
        }
    }

    /**
     * Uloží prílohu z daného URI do externého úložiska aplikácie.
     *
     * @param uri URI súboru, ktorý sa má uložiť
     * @return Časová značka poslednej úpravy uloženého súboru, ktorá slúži ako jeho ID
     */
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

    /**
     * Získa objekt súboru pre dané ID prílohy.
     *
     * @param attachmentId ID prílohy (časová značka poslednej úpravy súboru)
     * @return Objekt súboru ak je nájdený, null inak
     */
    fun getAttachmentFile(attachmentId: Long): File? {
        return attachmentsDir.listFiles()?.find { it.lastModified() == attachmentId }
    }

    /**
     * Vymaže prílohu z úložiska.
     *
     * @param attachmentId ID prílohy, ktorá sa má vymazať
     */
    fun deleteAttachment(attachmentId: Long) {
        getAttachmentFile(attachmentId)?.delete()
    }
} 