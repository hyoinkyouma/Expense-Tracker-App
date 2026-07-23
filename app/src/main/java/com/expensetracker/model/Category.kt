package com.expensetracker.model

import java.util.Calendar

fun getCategoryList(): List<CategoryEntity> {
    return listOf(
        CategoryEntity(1, "Food", "🍽️", 0xFFE57373.toInt(), true),
        CategoryEntity(2, "Transportation", "🚗", 0xFF64B5F6.toInt(), true),
        CategoryEntity(3, "Utilities", "💡", 0xFF81C784.toInt(), true),
        CategoryEntity(4, "Entertainment", "🎬", 0xFF9FA8DA.toInt(), true),
        CategoryEntity(5, "Shopping", "🛍️", 0xFFFFAB91.toInt(), true),
        CategoryEntity(6, "Healthcare", "🩺", 0xFFB0BEC5.toInt(), true),
        CategoryEntity(7, "Insurance", "🛡️", 0xFFA5D6A7.toInt(), true),
        CategoryEntity(8, "Housing", "🏠", 0xFF4DD0E1.toInt(), true),
        CategoryEntity(9, "Education", "📚", 0xFFFFF59D.toInt(), true),
        CategoryEntity(10, "Personal", "✨", 0xFFE1BEE7.toInt(), true),
        CategoryEntity(11, "Other", "📦", 0xFFBCAAC4.toInt(), true)
    )
}

val allCategories by lazy {
    getCategoryList()
}

data class Category(
    val id: Long = 0,
    val name: String,
    val icon: String,
    val color: Int,
    val totalAmount: Double = 0.0
) {
    companion object {
        fun fromId(id: Long): Category {
            val categories = getCategoryList()
            return categories.find { it.id == id }?.let {
                Category(
                    id = it.id,
                    name = it.name,
                    icon = it.icon,
                    color = it.color
                )
            } ?: Category(
                id = id,
                name = "Other",
                icon = "📦",
                color = 0xFFBCAAC4.toInt()
            )
        }
    }
}
