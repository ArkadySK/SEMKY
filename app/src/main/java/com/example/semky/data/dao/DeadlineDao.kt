package com.example.semky.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.semky.data.model.Deadline
import com.example.semky.data.model.SemPraca
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object (DAO) pre prácu s termínmi v databáze.
 */
@Dao
interface DeadlineDao {
    /**
     * Získa zoznam všetkých termínov z databázy.
     *
     * @return Flow zoznamu všetkých termínov
     */
    @Query("SELECT * FROM deadlines")
    fun getAll(): Flow<List<Deadline>>

    /**
     * Vloží nový termín do databázy.
     *
     * @param deadline Termín, ktorý sa má vložiť
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDeadline(deadline: Deadline)

    /**
     * Aktualizuje existujúci termín v databáze.
     *
     * @param deadline Termín, ktorý sa má aktualizovať
     */
    @Update
    suspend fun updateDeadline(deadline: Deadline)

    /**
     * Vymaže termín z databázy.
     *
     * @param deadline Termín, ktorý sa má vymazať
     */
    @Delete
    suspend fun deleteDeadline(deadline: Deadline)

    /**
     * Získa zoznam všetkých termínov pre konkrétnu semestrálnu prácu.
     *
     * @param pracaId ID semestrálnej práce
     * @return Flow zoznamu termínov pre danú semestrálnu prácu
     */
    @Query("SELECT * FROM deadlines WHERE semPracaId = :pracaId")
    fun getAllByPracaId(pracaId: Long): Flow<List<Deadline>>
} 