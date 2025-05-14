package com.example.semky.data.converters

import androidx.room.TypeConverter
import com.example.semky.data.model.Deadline
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class Converters {
    private val gson = Gson()

    @TypeConverter
    fun fromLongList(value: List<Long>): String {
        return value.joinToString(",")
    }

    @TypeConverter
    fun toLongList(value: String): List<Long> {
        if (value.isEmpty()) return emptyList()
        return value.split(",").map { it.toLong() }
    }

    @TypeConverter
    fun fromDeadlineList(value: List<Deadline>): String {
        return gson.toJson(value)
    }

    @TypeConverter
    fun toDeadlineList(value: String): List<Deadline> {
        if (value.isEmpty()) return emptyList()
        val listType = object : TypeToken<List<Deadline>>() {}.type
        return gson.fromJson(value, listType)
    }
} 