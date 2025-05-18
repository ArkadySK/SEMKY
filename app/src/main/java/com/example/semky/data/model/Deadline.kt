package com.example.semky.data.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize
import java.util.Date

/**
 * Datová trieda reprezentujúca termín v aplikácii.
 * Používa sa pre ukladanie termínov spojených so semestrálnymi prácami.
 *
 * @property id ID termínu
 * @property semPracaId ID semestrálnej práce, ku ktorej termín patrí
 * @property date Dátum termínu
 * @property name Názov termínu
 */
@Parcelize
@Entity(tableName = "deadlines")
data class Deadline(
    @PrimaryKey(autoGenerate = true)
    val id: Long,
    val semPracaId: Long,
    val date: Date,
    val name: String
) : Parcelable {
}