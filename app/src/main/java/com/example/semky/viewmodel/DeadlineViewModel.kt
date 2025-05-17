package com.example.semky.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.semky.R
import com.example.semky.data.model.Deadline
import com.example.semky.data.repository.DeadlineRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class DeadlineViewModel(
    private val repository: DeadlineRepository
) : ViewModel() {

    val deadlines: StateFlow<List<Deadline>> = repository.getAll()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun addDeadline(deadline: Deadline) {
        viewModelScope.launch {
            repository.insertDeadline(deadline)
        }
    }

    fun updateDeadline(deadline: Deadline) {
        viewModelScope.launch {
            repository.updateDeadline(deadline)
        }
    }

    fun deleteDeadline(deadline: Deadline) {
        viewModelScope.launch {
            repository.deleteDeadline(deadline)
        }
    }

    fun deleteByPracaId(pracaId: Long) {
        viewModelScope.launch {
            repository.deleteByPracaId(pracaId)
        }
    }

    fun getAllByPracaId(pracaId: Long?): StateFlow<List<Deadline>> {
        if(pracaId == null) return MutableStateFlow(emptyList())
        return repository.getAllByPracaId(pracaId)
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = emptyList()
            )
    }
}

//src: https://medium.com/@1mailanton/approaches-to-creating-viewmodel-in-android-f9f6f62a155a
class DeadlineViewModelFactory(
    private val repository: DeadlineRepository,
    private val context: Context
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DeadlineViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return DeadlineViewModel(repository) as T
        }
        throw IllegalArgumentException(context.getString(R.string.error_unknown_viewmodel_class))
    }
} 