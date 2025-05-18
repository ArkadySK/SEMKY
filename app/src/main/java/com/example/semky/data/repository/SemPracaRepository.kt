package com.example.semky.data.repository

import com.example.semky.data.dao.SemPracaDao
import com.example.semky.data.model.SemPraca
import kotlinx.coroutines.flow.Flow

/**
 * Repository trieda pre správu semestrálnych prác v aplikácii.
 * Poskytuje rozhranie pre prístup k dátam o semestrálnych prácach cez DAO.
 *
 * @property semPracaDao Data Access Object pre prácu so semestrálnymi prácami v databáze
 */
class SemPracaRepository(private val semPracaDao: SemPracaDao) {
    /**
     * Získa zoznam všetkých semestrálnych prác z databázy.
     *
     * @return Flow zoznamu všetkých semestrálnych prác
     */
    fun getAllPrace(): Flow<List<SemPraca>> = semPracaDao.getAllPrace()

    /**
     * Vloží novú semestrálnu prácu do databázy.
     *
     * @param praca Semestrálna práca, ktorá sa má vložiť
     * @return ID novej semestrálnej práce
     */
    suspend fun insertPraca(praca: SemPraca): Long = semPracaDao.insertPraca(praca)

    /**
     * Aktualizuje existujúcu semestrálnu prácu v databáze.
     *
     * @param praca Semestrálna práca, ktorá sa má aktualizovať
     */
    suspend fun updatePraca(praca: SemPraca) = semPracaDao.updatePraca(praca)

    /**
     * Vymaže semestrálnu prácu z databázy.
     *
     * @param praca Semestrálna práca, ktorá sa má vymazať
     */
    suspend fun deletePraca(praca: SemPraca) = semPracaDao.deletePraca(praca)
} 