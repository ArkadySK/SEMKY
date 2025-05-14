package com.example.semky.data.repository

import com.example.semky.data.dao.SemPracaPointsDao
import com.example.semky.data.model.SemPracaBody
import kotlinx.coroutines.flow.Flow

class SemPracaPointsRepository(private val semPracaPointsDao: SemPracaPointsDao) {
    fun getAllPointsBySemPracaId(semPracaId: Long): Flow<List<SemPracaBody>> = 
        semPracaPointsDao.getAllPointsBySemPracaId(semPracaId)

    suspend fun insertPoints(points: SemPracaBody): Long = 
        semPracaPointsDao.insertPoints(points)

    suspend fun deletePoints(points: SemPracaBody) = 
        semPracaPointsDao.deletePoints(points)
} 