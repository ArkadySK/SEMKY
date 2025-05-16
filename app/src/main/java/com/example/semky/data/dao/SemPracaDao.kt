package com.example.semky.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.semky.data.model.SemPraca
import kotlinx.coroutines.flow.Flow

@Dao
interface SemPracaDao {
    @Query("SELECT * FROM sem_prace")
    fun getAllPrace(): Flow<List<SemPraca>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPraca(praca: SemPraca): Long

    @Update
    suspend fun updatePraca(praca: SemPraca)

    @Delete
    suspend fun deletePraca(praca: SemPraca)
} 