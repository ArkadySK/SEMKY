package com.example.semky.data.repository

import com.example.semky.data.dao.SemPracaPointsDao
import com.example.semky.data.model.SemPracaBody
import kotlinx.coroutines.flow.Flow

/**
 * Repository trieda pre správu bodov semestrálnych prác v aplikácii.
 * Poskytuje rozhranie pre prístup k dátam o bodoch cez DAO.
 *
 * @property semPracaPointsDao Data Access Object pre prácu s bodmi semestrálnych prác v databáze
 */
class SemPracaPointsRepository(private val semPracaPointsDao: SemPracaPointsDao) {
    /**
     * Získa zoznam všetkých bodov semestrálnych prác z databázy.
     * Získa body pre konkrétnu semestrálnu prácu.
     *
     * @return Flow zoznamu všetkých bodov semestrálnych prác
     */
    fun getAllPoints(): Flow<List<SemPracaBody>> = semPracaPointsDao.getAllPoints()

    /**
     * Získa body pre konkrétnu semestrálnu prácu.
     *
     * @param semPracaId ID semestrálnej práce
     * @return Dáta o bodoch ak sú nájdené, null inak
     */
    suspend fun getPointsBySemPracaId(semPracaId: Long): SemPracaBody? =
        semPracaPointsDao.getPointsBySemPracaId(semPracaId)

    /**
     * Vloží nové body do databázy.
     *
     * @param points Dáta o bodoch, ktoré sa majú vložiť
     * @return ID novo vloženého záznamu o bodoch
     */
    suspend fun insertPoints(points: SemPracaBody): Long = 
        semPracaPointsDao.insertPoints(points)

    /**
     * Vymaže body spojené s konkrétnou semestrálnou prácou.
     *
     * @param pracaId ID semestrálnej práce, ktorej body sa majú vymazať
     */
    suspend fun deleteBySemPracaId(pracaId: Long) {
        val body: SemPracaBody? = semPracaPointsDao.getPointsBySemPracaId(pracaId)
        if(body != null)
            semPracaPointsDao.deletePoints(body)
    }
}