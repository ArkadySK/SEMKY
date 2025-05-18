package com.example.semky.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.semky.data.converters.Converters
import com.example.semky.data.dao.DeadlineDao
import com.example.semky.data.dao.SemPracaDao
import com.example.semky.data.dao.SemPracaPointsDao
import com.example.semky.data.model.Deadline
import com.example.semky.data.model.SemPraca
import com.example.semky.data.model.SemPracaBody

/**
 * Hlavná databázová trieda aplikácie.
 * Používa Room databázu pre ukladanie dát o semestrálnych prácach, termínoch a bodoch.
 * Implementuje singleton pattern pre prístup k databáze.
 */
@Database(
    entities = [SemPraca::class, SemPracaBody::class, Deadline::class],
    version = 11, exportSchema = false
)
@TypeConverters(Converters::class)
abstract class SemkyDatabase : RoomDatabase() {
    /** Vráti DAO pre prácu so semestrálnymi prácami.*/
    abstract fun semPracaDao(): SemPracaDao

    /** Vráti DAO pre prácu s bodmi semestrálnych prác.*/
    abstract fun semPracaPointsDao(): SemPracaPointsDao

    /** Vráti DAO pre prácu s termínmi. */
    abstract fun deadlineDao(): DeadlineDao

    companion object {
        @Volatile
        private var Instance: SemkyDatabase? = null

        /**
         * Vráti inštanciu databázy. Ak databáza ešte neexistuje, vytvorí ju.
         *
         * @param context Kontext aplikácie
         * @return Inštancia databázy
         */
        fun getDatabase(context: Context): SemkyDatabase {
            return Instance ?: synchronized(this) {
                Room.databaseBuilder(
                    context,
                    SemkyDatabase::class.java,
                    "semky_database"
                )
                    .fallbackToDestructiveMigration(false)
                    .build()
                    .also { Instance = it }
            }
        }
    }
} 