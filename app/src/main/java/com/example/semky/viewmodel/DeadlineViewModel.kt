package com.example.semky.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.semky.R
import com.example.semky.data.model.Deadline
import com.example.semky.data.repository.DeadlineRepository
import com.example.semky.notifications.NotificationManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/**
 * ViewModel pre správu termínov v aplikácii.
 */
class DeadlineViewModel(
    private val repository: DeadlineRepository,
    private val context: Context
) : ViewModel() {

    /**
     * Flow obsahujúci zoznam všetkých termínov.
     */
    val deadlines: StateFlow<List<Deadline>> = repository.getAll()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    /**
     * Pridá nový termín do databázy a naplánuje notifikáciu.
     *
     * @param deadline Termín, ktorý sa má pridať
     */
    fun addDeadline(deadline: Deadline) {
        viewModelScope.launch {
            repository.insertDeadline(deadline)
            NotificationManager.scheduleDeadlineNotification(context, deadline)
        }
    }

    /**
     * Aktualizuje existujúci termín v databáze a aktualizuje jeho notifikáciu.
     *
     * @param deadline Termín, ktorý sa má aktualizovať
     */
    fun updateDeadline(deadline: Deadline) {
        viewModelScope.launch {
            repository.updateDeadline(deadline)
            NotificationManager.scheduleDeadlineNotification(context, deadline)
        }
    }

    /**
     * Vymaže termín z databázy a zruší jeho notifikáciu.
     *
     * @param deadline Termín, ktorý sa má vymazať
     */
    fun deleteDeadline(deadline: Deadline) {
        viewModelScope.launch {
            repository.deleteDeadline(deadline)
            NotificationManager.cancelDeadlineNotification(context, deadline.id)
        }
    }

    /**
     * Vymaže všetky termíny spojené s konkrétnou semestrálnou prácou.
     *
     * @param pracaId ID semestrálnej práce
     */
    fun deleteByPracaId(pracaId: Long) {
        viewModelScope.launch {
            val deadlines = repository.getAllByPracaId(pracaId).first()
            deadlines.forEach { deadline ->
                repository.deleteDeadline(deadline)
                NotificationManager.cancelDeadlineNotification(context, deadline.id)
            }
        }
    }

    /**
     * Získa zoznam všetkých termínov pre konkrétnu semestrálnu prácu.
     *
     * @param pracaId ID semestrálnej práce
     * @return Flow obsahujúci zoznam termínov pre danú semestrálnu prácu
     */
    fun getAllByPracaId(pracaId: Long?): StateFlow<List<Deadline>> {
        if (pracaId == null) return MutableStateFlow(emptyList())
        return repository.getAllByPracaId(pracaId)
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = emptyList()
            )
    }
}

/**
 * Factory trieda pre vytváranie inštancií DeadlineViewModel.
 * Implementuje ViewModelProvider.Factory pre dependency injection.
 * 
 * Zdroj: https://medium.com/@1mailanton/approaches-to-creating-viewmodel-in-android-f9f6f62a155a
 */
class DeadlineViewModelFactory(
    private val repository: DeadlineRepository,
    private val context: Context
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DeadlineViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return DeadlineViewModel(repository, context) as T
        }
        throw IllegalArgumentException(context.getString(R.string.error_unknown_viewmodel_class))
    }
} 