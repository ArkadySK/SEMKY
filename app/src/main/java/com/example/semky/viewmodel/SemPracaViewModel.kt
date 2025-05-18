package com.example.semky.viewmodel

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.semky.R
import com.example.semky.data.FileManager
import com.example.semky.data.model.SemPraca
import com.example.semky.data.repository.SemPracaRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/**
 * ViewModel trieda pre správu dát a operácií semestrálnych prác.
 *
 * @property repository Repository pre operácie s dátami semestrálnych prác
 * @property fileManager Správca pre prácu s prílohami
 */
class SemPracaViewModel(
    private val repository: SemPracaRepository,
    private val fileManager: FileManager
) : ViewModel() {

    /**
     * Zoznam všetkých semestrálnych prác (ako flow), automaticky aktualizovaný pri zmene v databáze.
     */
    val semPrace: StateFlow<List<SemPraca>> = repository.getAllPrace()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    /**
     * Pridá novú semestrálnu prácu do databázy.
     *
     * @param praca Semestrálna práca, ktorá sa má pridať
     * @return ID novej semestrálnej práce
     */
    suspend fun addPraca(praca: SemPraca): Long {
        return repository.insertPraca(praca)
    }

    /**
     * Aktualizuje existujúcu semestrálnu prácu.
     *
     * @param praca Semestrálna práca, ktorá sa má aktualizovať
     */
    fun updatePraca(praca: SemPraca) {
        viewModelScope.launch {
            repository.updatePraca(praca)
        }
    }

    /**
     * Vymaže semestrálnu prácu a všetky jej prílohy.
     *
     * @param praca Semestrálna práca, ktorá sa má vymazať
     */
    fun deletePraca(praca: SemPraca) {
        viewModelScope.launch {
            praca.attachments.forEach { attachmentId ->
                fileManager.deleteAttachment(attachmentId)
            }
            repository.deletePraca(praca)
        }
    }

    /**
     * Pridá novú prílohu do lokálneho úložiska.
     *
     * @param uri URI súboru, ktorý sa má priložiť
     * @return ID novej prílohy
     */
    fun addAttachment(uri: Uri): Long {
        return fileManager.saveAttachment(uri)
    }

    /**
     * Získa objekt súboru pre dané ID prílohy.
     *
     * @param attachmentId ID prílohy, ktorá sa má získať
     * @return Objekt súboru ak je nájdený, null inak
     */
    fun getAttachmentFile(attachmentId: Long) = fileManager.getAttachmentFile(attachmentId)
}

/**
 * Factory trieda pre vytváranie inštancií SemPracaViewModel.
 * 
 * Zdroj: https://medium.com/@1mailanton/approaches-to-creating-viewmodel-in-android-f9f6f62a155a
 *
 * @property repository Repository pre operácie s dátami semestrálnych prác
 * @property context Kontext aplikácie
 */
class SemPracaViewModelFactory(
    private val repository: SemPracaRepository,
    private val context: Context
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SemPracaViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return SemPracaViewModel(repository, FileManager(context)) as T
        }
        throw IllegalArgumentException(context.getString(R.string.error_unknown_viewmodel_class))
    }
} 