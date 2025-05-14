package com.example.semky.data.repository

import com.example.semky.data.dao.SemPracaPointsDao
import com.example.semky.data.model.SemPraca
import com.example.semky.data.model.SemPracaBody
import kotlinx.coroutines.flow.Flow

class SemPracaPointsRepository(private val semPracaPointsDao: SemPracaPointsDao) {
    suspend fun getPointsBySemPracaId(semPracaId: Long): SemPracaBody =
        semPracaPointsDao.getPointsBySemPracaId(semPracaId)

    suspend fun insertPoints(points: SemPracaBody): Long = 
        semPracaPointsDao.insertPoints(points)

    suspend fun deleteBySemPracaId(pracaId: Long) {
        val body: SemPracaBody = semPracaPointsDao.getPointsBySemPracaId(pracaId)
        semPracaPointsDao.deletePoints(body)
    }
}