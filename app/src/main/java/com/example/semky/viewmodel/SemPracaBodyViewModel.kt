package com.example.semky.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.semky.data.model.SemPraca
import com.example.semky.data.model.SemPracaBody
import com.example.semky.data.repository.SemPracaPointsRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

class SemPracaPointsViewModel(
    private val repository: SemPracaPointsRepository
) : ViewModel() {

    private var currentSemPracaId: Long = 0

    fun deletePoints(praca: SemPraca) {
        viewModelScope.launch {
            repository.deleteBySemPracaId(praca.id)
        }
    }

    fun calculateSubmissionPoints(praca: SemPraca) {
        // Očakavame ze posledný deadline bude aj termín odovzdania / dokončenia
        val earliestDeadline = praca.deadlines.maxOfOrNull { it.date }
        if (earliestDeadline == null) return

        // daysDifference: záporné - skôr, kladné neskôr
        val daysDifference =
            ((System.currentTimeMillis() - earliestDeadline) / (24 * 60 * 60 * 1000)).toInt()

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

        viewModelScope.launch {
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

class SemPracaPointsViewModelFactory(
    private val repository: SemPracaPointsRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SemPracaPointsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return SemPracaPointsViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
} 