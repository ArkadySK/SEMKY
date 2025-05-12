package com.example.semky.data.repository

import com.example.semky.data.dao.SemPracaDao
import com.example.semky.data.model.SemPraca
import kotlinx.coroutines.flow.Flow

class SemPracaRepository(private val semPracaDao: SemPracaDao) {
    fun getAllPrace(): Flow<List<SemPraca>> = semPracaDao.getAllPrace()

    suspend fun getPracaById(id: Long): SemPraca? = semPracaDao.getPracaById(id)

    suspend fun insertPraca(praca: SemPraca): Long = semPracaDao.insertPraca(praca)

    suspend fun updatePraca(praca: SemPraca) = semPracaDao.updatePraca(praca)

    suspend fun deletePraca(praca: SemPraca) = semPracaDao.deletePraca(praca)
} 