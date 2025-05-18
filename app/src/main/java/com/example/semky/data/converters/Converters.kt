package com.example.semky.data.converters

import androidx.room.TypeConverter
import com.example.semky.data.model.Deadline
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.util.Date

/**
 * Pomocná trieda pre konverziu medzi dátovými typmi pre Room databázu.
 * Poskytuje konvertory typov pre operácie s Room databázou.
 */
class Converters {
    private val gson = Gson()

    /**
     * Konvertuje List<Long> do Stringu s oddelenými čiarkami pre uloženie do databázy.
     *
     * @param value Zoznam Long hodnôt na konverziu
     * @return Reťazec oddelený čiarkami reprezentujúci zoznam
     */
    @TypeConverter
    fun fromLongList(value: List<Long>): String {
        return value.joinToString(",")
    }

    /**
     * Konvertuje String oddelený čiarkami späť na List<Long> z databázy.
     *
     * @param value Reťazec oddelený čiarkami na konverziu
     * @return Zoznam Long hodnôt
     */
    @TypeConverter
    fun toLongList(value: String): List<Long> {
        if (value.isEmpty()) return emptyList()
        return value.split(",").map { it.toLong() }
    }

    /**
     * Konvertuje Date na Long časovú značku pre uloženie do databázy.
     *
     * @param date Date objekt na konverziu
     * @return Časová značka ako Long hodnota
     */
    @TypeConverter
    fun fromDate(date: Date): Long {
        return date.time
    }

    /**
     * Konvertuje Long z databázy späť na Date.
     *
     * @param value Časová značka ako Long hodnota
     * @return Zodpovedajúci Date objekt
     */
    @TypeConverter
    fun toDate(value: Long): Date {
        return Date(value)
    }
} 