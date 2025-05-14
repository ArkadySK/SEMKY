package com.example.semky.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "sem_praca_body")
data class SemPracaBody(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val semPracaId: Long = 0,
    val description: String,
    val points: Int = 0
)
