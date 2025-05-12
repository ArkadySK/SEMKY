package com.example.semky.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.semky.data.model.SemPraca
import com.example.semky.data.repository.SemPracaRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SemPracaViewModel(private val repository: SemPracaRepository) : ViewModel() {

    private val _semPrace = MutableStateFlow<List<SemPraca>>(emptyList())
    val semPrace: StateFlow<List<SemPraca>> = _semPrace.asStateFlow()

    init {
        viewModelScope.launch {
            _semPrace.value = repository.getAllPrace()
        }
    }

    fun addPraca(praca: SemPraca) {
        viewModelScope.launch {
            repository.insertPraca(praca)
        }
    }

    fun updatePraca(praca: SemPraca) {
        viewModelScope.launch {
            repository.updatePraca(praca)
        }
    }

    fun deletePraca(praca: SemPraca) {
        viewModelScope.launch {
            repository.deletePraca(praca)
        }
    }

    suspend fun getPracaById(id: Long): SemPraca? {
        return repository.getPracaById(id)
    }
}

class SemPracaViewModelFactory(private val repository: SemPracaRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SemPracaViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return SemPracaViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
} 