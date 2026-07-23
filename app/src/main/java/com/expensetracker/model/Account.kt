package com.expensetracker.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "accounts")
data class Account(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val initialBalance: Double = 0.0,
    val currentBalance: Double,
    val type: String = "checking",
    val createdAt: Long = System.currentTimeMillis()
)
