package com.example.semky.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.semky.data.model.SemPracaBody
import com.example.semky.data.repository.SemPracaBodyRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SemPracaBodyViewModel(
    private val repository: SemPracaBodyRepository
) : ViewModel() {

    private var currentSemPracaId: Long = 0

    fun setBodies(semPracaId: Long): StateFlow<List<SemPracaBody>> {
        currentSemPracaId = semPracaId
        return repository.getAllBodiesBySemPracaId(semPracaId)
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = emptyList()
            )
    }

    fun addBody(body: SemPracaBody) {
        viewModelScope.launch {
            repository.insertBody(body.copy(semPracaId = currentSemPracaId))
        }
    }

    fun updateBody(body: SemPracaBody) {
        viewModelScope.launch {
            repository.updateBody(body)
        }
    }

    fun deleteBody(body: SemPracaBody) {
        viewModelScope.launch {
            repository.deleteBody(body)
        }
    }

    suspend fun getBodyById(id: Long): SemPracaBody? {
        return repository.getBodyById(id)
    }
}

class SemPracaBodyViewModelFactory(
    private val repository: SemPracaBodyRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SemPracaBodyViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return SemPracaBodyViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
} 