package com.example.semky.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.semky.data.model.SemPracaBody
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object (DAO) pre prácu s bodmi semestrálnych prác v databáze.
 */
@Dao
interface SemPracaPointsDao {
    /**
     * Získa zoznam všetkých bodov semestrálnych prác z databázy.
     *
     * @return Flow zoznamu všetkých bodov semestrálnych prác
     */
    @Query("SELECT * FROM sem_praca_body")
    fun getAllPoints(): Flow<List<SemPracaBody>>

    /**
     * Získa body pre konkrétnu semestrálnu prácu.
     *
     * @param semPracaId ID semestrálnej práce
     * @return Dáta o bodoch ak sú nájdené (ak sa nenašli, tak null)
     */
    @Query("SELECT * FROM sem_praca_body WHERE semPracaId = :semPracaId LIMIT 1")
    suspend fun getPointsBySemPracaId(semPracaId: Long): SemPracaBody?

    /**
     * Vloží nové body do databázy.
     *
     * @param points Dáta o bodoch, ktoré sa majú vložiť
     * @return ID novo vloženého záznamu o bodoch
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPoints(points: SemPracaBody): Long

    /**
     * Vymaže záznam o bodoch z databázy.
     *
     * @param points Dáta o bodoch, ktoré sa majú vymazať
     */
    @Delete
    suspend fun deletePoints(points: SemPracaBody)
} 