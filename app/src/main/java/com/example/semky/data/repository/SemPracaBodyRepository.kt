package com.example.semky.data.repository

import com.example.semky.data.dao.SemPracaBodyDao
import com.example.semky.data.model.SemPracaBody
import kotlinx.coroutines.flow.Flow

class SemPracaBodyRepository(private val semPracaBodyDao: SemPracaBodyDao) {
    fun getAllBodiesBySemPracaId(semPracaId: Long): Flow<List<SemPracaBody>> = 
        semPracaBodyDao.getAllBodiesBySemPracaId(semPracaId)

    suspend fun getBodyById(id: Long): SemPracaBody? = 
        semPracaBodyDao.getBodyById(id)

    suspend fun insertBody(body: SemPracaBody): Long = 
        semPracaBodyDao.insertBody(body)

    suspend fun updateBody(body: SemPracaBody) = 
        semPracaBodyDao.updateBody(body)

    suspend fun deleteBody(body: SemPracaBody) = 
        semPracaBodyDao.deleteBody(body)
} 