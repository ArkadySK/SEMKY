package com.example.semky.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import androidx.room.TypeConverters

@Entity(tableName = "sem_prace")
data class SemPraca(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val nazov: String,
    val informacie: String,
    val terminy: List<String>,
    val prilohy: List<Long>
)
