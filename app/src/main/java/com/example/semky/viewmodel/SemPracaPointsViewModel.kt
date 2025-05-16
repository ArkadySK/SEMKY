package com.example.semky.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.semky.data.model.SemPraca
import com.example.semky.data.model.SemPracaBody
import com.example.semky.data.repository.DeadlineRepository
import com.example.semky.data.repository.SemPracaPointsRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Date
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

class SemPracaPointsViewModel(
    private val repository: SemPracaPointsRepository,
    private val deadlineRepository: DeadlineRepository
) : ViewModel() {

    val allPoints: StateFlow<List<SemPracaBody>> = repository.getAllPoints()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun calculateSubmissionPoints(praca: SemPraca) {
        // Očakavame ze posledný deadline bude aj termín odovzdania / dokončenia
        viewModelScope.launch {
            deadlineRepository.getAll().collect { deadlines ->
                val earliestDeadline = deadlines.maxOfOrNull { it.date }
                if (earliestDeadline == null) return@collect

                // daysDifference: záporné - skôr, kladné neskôr
                val now = Date()
                val daysDifference = ((now.time - earliestDeadline.time) / (24 * 60 * 60 * 1000)).toInt()

                // Za každý deň oneskorenia sa mu strhne jeden bod
                // Za každý deň, v prípade že odovzdal skôr, dostane 2 extra body
                val pointsAdjustment = when {
                    daysDifference > 0 -> {
                        -min(9, daysDifference)
                    }
                    daysDifference <= 0 -> {
                        min(20, abs(daysDifference) * 2)
                    }
                    else -> 0
                }

                val totalPoints = max(1, 10 + pointsAdjustment)

                val description = when {
                    daysDifference > 0 -> "Odovzdanie práce (oneskorenie ${daysDifference} dní)"
                    daysDifference < 0 -> "Odovzdanie práce (predčasne ${abs(daysDifference)} dní)"
                    else -> "Odovzdanie práce (v termíne)"
                }

                repository.insertPoints(
                    SemPracaBody(
                        semPracaId = praca.id,
                        description = description,
                        points = totalPoints
                    )
                )
            }
        }
    }

    fun deletePoints(praca: SemPraca) {
        viewModelScope.launch {
            repository.deleteBySemPracaId(praca.id)
        }
    }
}

class SemPracaPointsViewModelFactory(
    private val repository: SemPracaPointsRepository,
    private val deadlineRepository: DeadlineRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SemPracaPointsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return SemPracaPointsViewModel(repository, deadlineRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
} 