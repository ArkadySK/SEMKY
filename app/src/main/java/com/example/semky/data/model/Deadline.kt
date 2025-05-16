package com.example.semky.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "deadlines")
data class Deadline(
    @PrimaryKey(autoGenerate = true)
    val id: Long,
    val semPracaId: Long,
    val date: Date,
    val name: String
) {
}