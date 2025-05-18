package com.example.semky.data.repository

import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Update
import com.example.semky.data.dao.DeadlineDao
import com.example.semky.data.dao.SemPracaDao
import com.example.semky.data.model.Deadline
import com.example.semky.data.model.SemPraca
import kotlinx.coroutines.flow.Flow

/**
 * Repository trieda pre správu termínov v aplikácii.
 *
 * @property deadlineDao Data Access Object pre prácu s termínmi v databáze
 */
class DeadlineRepository(private val deadlineDao: DeadlineDao) {
    /**
     * Získa zoznam všetkých termínov z databázy.
     *
     * @return Flow zoznamu všetkých termínov
     */
    fun getAll(): Flow<List<Deadline>> = deadlineDao.getAll()

    /**
     * Vloží nový termín do databázy.
     *
     * @param deadline Termín, ktorý sa má vložiť
     */
    suspend fun insertDeadline(deadline: Deadline) = deadlineDao.insertDeadline(deadline)

    /**
     * Aktualizuje existujúci termín v databáze.
     *
     * @param deadline Termín, ktorý sa má aktualizovať
     */
    suspend fun updateDeadline(deadline: Deadline) = deadlineDao.updateDeadline(deadline)

    /**
     * Vymaže termín z databázy.
     *
     * @param deadline Termín, ktorý sa má vymazať
     */
    suspend fun deleteDeadline(deadline: Deadline) = deadlineDao.deleteDeadline(deadline)

    /**
     * Získa zoznam všetkých termínov pre konkrétnu semestrálnu prácu.
     *
     * @param pracaId ID semestrálnej práce
     * @return Flow zoznamu termínov pre danú semestrálnu prácu
     */
    fun getAllByPracaId(pracaId: Long): Flow<List<Deadline>> = deadlineDao.getAllByPracaId(pracaId)
}