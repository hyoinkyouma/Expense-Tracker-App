package com.expensetracker.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Calendar

@Entity(tableName = "expenses")
data class Expense(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val amount: Double,
    val categoryId: Long,
    val date: Long,
    val notes: String = ""
)

enum class CategoryEnum(val id: Long, val categoryName: String, val icon: String, val color: Int) {
    FOOD(1, "Food", "🍽️", 0xFFE57373.toInt()),
    TRANSPORTATION(2, "Transportation", "🚗", 0xFF64B5F6.toInt()),
    UTILITIES(3, "Utilities", "💡", 0xFF81C784.toInt()),
    ENTERTAINMENT(4, "Entertainment", "🎬", 0xFF9FA8DA.toInt()),
    SHOPPING(5, "Shopping", "🛍️", 0xFFFFAB91.toInt()),
    HEALTHCARE(6, "Healthcare", "🩺", 0xFFB0BEC5.toInt()),
    INSURANCE(7, "Insurance", "🛡️", 0xFFA5D6A7.toInt()),
    HOUSING(8, "Housing", "🏠", 0xFF4DD0E1.toInt()),
    EDUCATION(9, "Education", "📚", 0xFFFFF59D.toInt()),
    PERSONAL(10, "Personal", "✨", 0xFFE1BEE7.toInt()),
    OTHER(11, "Other", "📦", 0xFFBCAAC4.toInt());

    companion object {
        fun fromId(id: Long): CategoryEnum {
            return values().find { it.id == id } ?: OTHER
        }
    }
}
