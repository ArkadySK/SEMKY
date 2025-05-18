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

class DeadlineRepository(private val deadlineDao: DeadlineDao) {
    fun getAll(): Flow<List<Deadline>> = deadlineDao.getAll()

    suspend fun insertDeadline(deadline: Deadline) = deadlineDao.insertDeadline(deadline)

    suspend fun updateDeadline(deadline: Deadline) = deadlineDao.updateDeadline(deadline)

    suspend fun deleteDeadline(deadline: Deadline) = deadlineDao.deleteDeadline(deadline)

    fun getAllByPracaId(pracaId: Long): Flow<List<Deadline>> = deadlineDao.getAllByPracaId(pracaId)
}