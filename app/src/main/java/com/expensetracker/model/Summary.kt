package com.expensetracker.model

data class MonthlySummary(
    val totalIncome: Double = 0.0,
    val totalExpenses: Double = 0.0,
    val savings: Double = 0.0,
    val expenseByCategory: List<ExpenseByCategory> = emptyList()
)

fun calculateMonthlySummary(
    expenses: List<Expense>,
    incomeTransactions: List<AccountTransaction>,
    expenseTransactions: List<AccountTransaction>,
    allCategories: List<CategoryEnum> = emptyList()
): MonthlySummary {
    val totalIncome = incomeTransactions.sumOf { it.amount }
    val totalExpenses = expenseTransactions.sumOf { it.amount }
    
    val categoryMap = allCategories.associateBy { it.id }
    
    val expenseByCategory = expenses.groupBy { it.categoryId }
        .map { (categoryId, categoryExpenses) ->
            val total = categoryExpenses.sumOf { it.amount }
            val category = categoryMap[categoryId] ?: CategoryEnum.fromId(categoryId)
            ExpenseByCategory(
                category = category,
                totalAmount = total,
                percentage = if (totalExpenses > 0) (total / totalExpenses * 100) else 0.0
            )
        }
        .sortedByDescending { it.totalAmount }
    
    return MonthlySummary(
        totalIncome = totalIncome,
        totalExpenses = totalExpenses,
        savings = totalIncome - totalExpenses,
        expenseByCategory = expenseByCategory
    )
}
