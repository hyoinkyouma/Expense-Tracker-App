package com.expensetracker.room

import androidx.room.TypeConverter
import androidx.room.TypeConverters

class Converters {
    @TypeConverter
    fun fromString(value: String?): List<String>? {
        return value?.let {
            it.split(";")
        }
    }

    @TypeConverter
    fun toString(list: List<String>?): String? {
        return list?.joinToString(separator = ";")
    }
}
