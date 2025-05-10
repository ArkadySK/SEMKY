package com.example.semky.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.semky.data.model.SemPraca
import com.example.semky.data.repository.SemPracaRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class SemPracaViewModel(private val repository: SemPracaRepository) : ViewModel() {
    val allPrace: Flow<List<SemPraca>> = repository.getAllPrace()

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