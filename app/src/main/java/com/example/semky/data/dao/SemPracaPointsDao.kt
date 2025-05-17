package com.example.semky.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.semky.data.model.SemPracaBody
import kotlinx.coroutines.flow.Flow

@Dao
interface SemPracaPointsDao {
    @Query("SELECT * FROM sem_praca_body WHERE semPracaId = :semPracaId LIMIT 1")
    suspend fun getPointsBySemPracaId(semPracaId: Long): SemPracaBody?

    @Query("SELECT * FROM sem_praca_body")
    fun getAllPoints(): Flow<List<SemPracaBody>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPoints(points: SemPracaBody): Long

    @Delete
    suspend fun deletePoints(points: SemPracaBody)
} 