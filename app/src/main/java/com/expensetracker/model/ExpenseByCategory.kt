package com.expensetracker.model

data class ExpenseByCategory(
    val category: CategoryEnum,
    val totalAmount: Double,
    val percentage: Double
)
