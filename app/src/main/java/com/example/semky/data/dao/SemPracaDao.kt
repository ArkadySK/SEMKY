package com.example.semky.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.semky.data.model.SemPraca
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object (DAO) pre prácu so semestrálnymi prácami v databáze.
 */
@Dao
interface SemPracaDao {
    /**
     * Získa zoznam všetkých semestrálnych prác z databázy.
     *
     * @return Flow zoznamu všetkých semestrálnych prác
     */
    @Query("SELECT * FROM sem_prace")
    fun getAllPrace(): Flow<List<SemPraca>>

    /**
     * Vloží novú semestrálnu prácu do databázy.
     *
     * @param praca Semestrálna práca, ktorá sa má vložiť
     * @return ID novej semestrálnej práce
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPraca(praca: SemPraca): Long

    /**
     * Aktualizuje existujúcu semestrálnu prácu v databáze.
     *
     * @param praca Semestrálna práca, ktorá sa má aktualizovať
     */
    @Update
    suspend fun updatePraca(praca: SemPraca)

    /**
     * Vymaže semestrálnu prácu z databázy.
     *
     * @param praca Semestrálna práca, ktorá sa má vymazať
     */
    @Delete
    suspend fun deletePraca(praca: SemPraca)
} 