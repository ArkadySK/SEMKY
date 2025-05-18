package com.example.semky.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.semky.R
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

/**
 * ViewModel pre správu bodov semestrálnych prác.
 */
class SemPracaPointsViewModel(
    private val repository: SemPracaPointsRepository,
    private val deadlineRepository: DeadlineRepository,
    private val context: Context
) : ViewModel() {

    /**
     * Flow obsahujúci zoznam všetkých bodov semestrálnych prác.
     */
    val allPoints: StateFlow<List<SemPracaBody>> = repository.getAllPoints()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    /**
     * Vypočíta a uloží body pre semestrálnu prácu na základe dátumu odovzdania.
     * Body sa vypočítajú nasledovne:
     * - Základných 10 bodov
     * - Za každý deň predčasného odovzdania +2 body (max 20 extra bodov)
     * - Za každý deň oneskorenia -1 bod (max -9 bodov)
     * - Minimálne 1 bod
     *
     * @param praca Semestrálna práca, pre ktorú sa majú vypočítať body
     */
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
                    daysDifference > 0 -> context.getString(R.string.submit_semPraca_later_with_days, daysDifference.toString())
                    daysDifference < 0 -> context.getString(R.string.submit_semPraca_earlier_with_days, abs(daysDifference).toString())
                    else -> context.getString(R.string.submit_semPraca_on_time)
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

    /**
     * Vymaže body a informácie k nim, spojené s konkrétnou semestrálnou prácou.
     *
     * @param praca Semestrálna práca, ktorej body sa majú vymazať
     */
    fun deletePoints(praca: SemPraca) {
        viewModelScope.launch {
            repository.deleteBySemPracaId(praca.id)
        }
    }
}

/**
 * Factory trieda pre vytváranie inštancií SemPracaPointsViewModel.
 * Implementuje ViewModelProvider.Factory pre dependency injection.
 * 
 * Zdroj: https://medium.com/@1mailanton/approaches-to-creating-viewmodel-in-android-f9f6f62a155a
 */
class SemPracaPointsViewModelFactory(
    private val repository: SemPracaPointsRepository,
    private val deadlineRepository: DeadlineRepository,
    private val context: Context
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SemPracaPointsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return SemPracaPointsViewModel(repository, deadlineRepository, context) as T
        }
        throw IllegalArgumentException(context.getString(R.string.error_unknown_viewmodel_class))
    }
} 