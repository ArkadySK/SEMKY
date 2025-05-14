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
interface SemPracaBodyDao {
    @Query("SELECT * FROM sem_praca_body WHERE semPracaId = :semPracaId")
    fun getAllBodiesBySemPracaId(semPracaId: Long): Flow<List<SemPracaBody>>

    @Query("SELECT * FROM sem_praca_body WHERE id = :id")
    suspend fun getBodyById(id: Long): SemPracaBody?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBody(body: SemPracaBody): Long

    @Update
    suspend fun updateBody(body: SemPracaBody)

    @Delete
    suspend fun deleteBody(body: SemPracaBody)
} 