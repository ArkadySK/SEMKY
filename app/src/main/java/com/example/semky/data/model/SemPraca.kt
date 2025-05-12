package com.example.semky.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "sem_prace")
data class SemPraca(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val description: String,
    val isFinished: Boolean = false,
    val deadlines: List<Long> = emptyList(),
    val attachments: List<Long> = emptyList()
)
