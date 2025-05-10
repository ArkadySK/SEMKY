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

    @Query("SELECT * FROM sem_prace WHERE id = :id")
    fun getPracaById(id: Long): SemPraca?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertPraca(praca: SemPraca): Long

    @Update
    fun updatePraca(praca: SemPraca)

    @Delete
    fun deletePraca(praca: SemPraca)
} 