package com.example.semky.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Datová trieda reprezentujúca semestrálnu prácu v aplikácii.
 * Používa sa pre ukladanie informácií o semestrálnych prácach a ich prílohách.
 *
 * @property id ID semestrálnej práce
 * @property name Názov semestrálnej práce
 * @property description Popis semestrálnej práce
 * @property isFinished Určuje, či je semestrálna práca dokončená
 * @property attachments Zoznam ID príloh spojených so semestrálnou prácou
 */
@Entity(tableName = "sem_prace")
data class SemPraca(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val description: String,
    val isFinished: Boolean = false,
    val attachments: List<Long> = emptyList()
)
