package com.example.semky.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.semky.data.converters.Converters
import com.example.semky.data.dao.SemPracaDao
import com.example.semky.data.model.SemPraca

@Database(
    entities = [SemPraca::class],
    version = 3, exportSchema = false
)
@TypeConverters(Converters::class)
abstract class SemkyDatabase : RoomDatabase() {
    abstract fun semPracaDao(): SemPracaDao

    companion object {
        @Volatile
        private var Instance: SemkyDatabase? = null

        fun getDatabase(context: Context): SemkyDatabase {
            return Instance ?: synchronized(this) {
                Room.databaseBuilder(
                    context,
                    SemkyDatabase::class.java,
                    "semky_database"
                )
                .fallbackToDestructiveMigration()
                .build()
                .also { Instance = it }
            }
        }
    }
} 