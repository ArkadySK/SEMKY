package com.example.semky.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Datová trieda reprezentujúca body získané za semestrálnu prácu.
 * Používa sa pre ukladanie bodov a ich popisu pre každú semestrálnu prácu.
 * Počet bodov sa odvíja od dátumu dokončenia práce v porovnaní s posledným termínom.
 *
 * @property id Unikátne ID záznamu o bodoch
 * @property semPracaId ID semestrálnej práce, ku ktorej body patria
 * @property description Popis, prečo boli body udelené (napr. "Odovzdané o 2 dni skôr")
 * @property points Počet získaných bodov
 */
@Entity(tableName = "sem_praca_body")
data class SemPracaBody(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val semPracaId: Long = 0,
    val description: String,
    val points: Int = 0
)
