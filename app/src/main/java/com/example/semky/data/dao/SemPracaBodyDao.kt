package com.example.semky.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.semky.data.model.SemPracaBody
import kotlinx.coroutines.flow.Flow

@Dao
interface SemPracaPointsDao {
    @Query("SELECT * FROM sem_praca_body WHERE semPracaId = :semPracaId")
    fun getAllPointsBySemPracaId(semPracaId: Long): Flow<List<SemPracaBody>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPoints(points: SemPracaBody): Long

    @Delete
    suspend fun deletePoints(points: SemPracaBody)
} 