package com.example.semky.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "sem_prace")
data class SemPraca(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val nazov: String,
    val informacie: String,
    val isFinished: Boolean = false
    //val terminy: List<Long> = emptyList(),
    //val prilohy: List<Long> = emptyList()
)
