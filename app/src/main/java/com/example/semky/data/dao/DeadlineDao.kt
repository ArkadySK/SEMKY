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

@Dao
interface DeadlineDao {
    @Query("SELECT * FROM deadlines")
    fun getAll(): Flow<List<Deadline>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDeadline(deadline: Deadline)

    @Update
    suspend fun updateDeadline(deadline: Deadline)

    @Delete
    suspend fun deleteDeadline(deadline: Deadline)

    @Query("SELECT * FROM deadlines WHERE semPracaId = :pracaId")
    fun getAllByPracaId(pracaId: Long): Flow<List<Deadline>>
} 