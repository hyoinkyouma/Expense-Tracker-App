package com.expensetracker.repository

import android.content.Context
import com.expensetracker.model.*
import com.expensetracker.room.ExpenseTrackerDatabase
import kotlinx.coroutines.flow.Flow
import java.util.Calendar

class ExpenseRepository(private val database: ExpenseTrackerDatabase) {
    private val expenseDao = database.expenseDao()
    private val accountDao = database.accountDao()
    private val categoryDao = database.categoryDao()
    private val transactionDao = database.accountTransactionDao()

    fun getAllExpenses(): List<Expense> {
        return expenseDao.getAllExpenses()
    }

    fun getAllExpensesFlow(): Flow<List<Expense>> {
        return expenseDao.getAllExpensesFlow()
    }

    fun getExpensesByDateRange(startDate: Long, endDate: Long): List<Expense> {
        return expenseDao.getExpensesByDateRange(startDate, endDate)
    }

    fun insertExpense(expense: Expense): Long {
        return expenseDao.insertExpense(expense)
    }

    fun deleteExpense(expenseId: Long) {
        expenseDao.deleteExpense(expenseId)
    }

    fun getAllAccounts(): List<Account> {
        return accountDao.getAllAccounts()
    }

    fun getAllAccountsFlow(): Flow<List<Account>> {
        return accountDao.getAllAccountsFlow()
    }

    fun getAccount(accountId: Long): Account? {
        return accountDao.getAccount(accountId)
    }

    fun insertAccount(account: Account): Long {
        return accountDao.insertAccount(account)
    }

    fun updateAccount(account: Account) {
        accountDao.updateAccount(account)
    }

    fun addIncomeToAccount(accountId: Long, amount: Double, notes: String = ""): AccountTransaction? {
        val account = accountDao.getAccount(accountId) ?: return null
        val transaction = AccountTransaction(
            accountId = accountId,
            amount = amount,
            type = "income",
            date = System.currentTimeMillis(),
            notes = notes
        )
        val transactionId = accountDao.insertAccountTransaction(transaction)
        val newBalance = account.currentBalance + amount
        accountDao.updateAccount(account.copy(currentBalance = newBalance))
        return transaction.copy(id = transactionId)
    }

    fun deductFromAccount(accountId: Long, amount: Double, notes: String = ""): AccountTransaction? {
        val account = accountDao.getAccount(accountId) ?: return null
        if (account.currentBalance < amount) return null
        
        val transaction = AccountTransaction(
            accountId = accountId,
            amount = amount,
            type = "expense",
            date = System.currentTimeMillis(),
            notes = notes
        )
        val transactionId = accountDao.insertAccountTransaction(transaction)
        val newBalance = account.currentBalance - amount
        accountDao.updateAccount(account.copy(currentBalance = newBalance))
        return transaction.copy(id = transactionId)
    }

    fun getAllAccountTransactions(): List<AccountTransaction> {
        return transactionDao.getAllAccountTransactions()
    }

    fun getIncomeTransactions(): List<AccountTransaction> {
        return transactionDao.getIncomeTransactions()
    }

    fun getExpenseTransactions(): List<AccountTransaction> {
        return transactionDao.getExpenseTransactions()
    }

    fun getTransactionsByAccountId(accountId: Long): List<AccountTransaction> {
        return transactionDao.getTransactionsByAccountId(accountId)
    }

    fun getTransactionsByDateRange(startDate: Long, endDate: Long): List<AccountTransaction> {
        return transactionDao.getTransactionsByDateRange(startDate, endDate)
    }

    fun insertTransaction(transaction: AccountTransaction): Long {
        val transactionId = transactionDao.insertTransaction(transaction)
        updateAccountBalance(transaction.accountId, transaction.amount, transaction.type)
        return transactionId
    }

    fun deleteTransaction(transaction: AccountTransaction) {
        transactionDao.deleteTransaction(transaction)
        updateAccountBalance(transaction.accountId, transaction.amount, transaction.type)
    }

    fun deleteTransactionById(transactionId: Long) {
        val transaction = transactionDao.getAllAccountTransactions()
            .find { it.id == transactionId } ?: return
        transactionDao.deleteTransactionById(transactionId)
        updateAccountBalance(transaction.accountId, transaction.amount, transaction.type)
    }

    fun insertAccountTransaction(accountId: Long, amount: Double, type: String, notes: String = ""): Long? {
        val account = accountDao.getAccount(accountId) ?: return null
        val transaction = AccountTransaction(
            accountId = accountId,
            amount = amount,
            type = type,
            date = System.currentTimeMillis(),
            notes = notes
        )
        val transactionId = transactionDao.insertTransaction(transaction)
        updateAccountBalance(accountId, amount, type)
        return transactionId
    }

    fun getTransactionById(transactionId: Long): AccountTransaction? {
        return transactionDao.getAllAccountTransactions()
            .find { it.id == transactionId }
    }



    fun getAllCategories(): List<CategoryEntity> {
        return categoryDao.getAllCategories()
    }

    fun getAllCategoriesFlow(): Flow<List<CategoryEntity>> {
        return categoryDao.getAllCategoriesFlow()
    }

    fun seedDefaultCategories() {
        if (categoryDao.getAllCategories().isEmpty()) {
            categoryDao.insertCategories(getCategoryList())
        }
    }

    fun getIncomeTransactionsFlow(): Flow<List<AccountTransaction>> {
        return transactionDao.getIncomeTransactionsFlow()
    }

    fun getExpenseTransactionsFlow(): Flow<List<AccountTransaction>> {
        return transactionDao.getExpenseTransactionsFlow()
    }

    private fun updateAccountBalance(accountId: Long, amount: Double, type: String) {
        val account = accountDao.getAccount(accountId) ?: return
        val newBalance = if (type == "income") {
            account.currentBalance + amount
        } else {
            account.currentBalance - amount
        }
        accountDao.updateAccount(account.copy(currentBalance = newBalance))
    }

    fun calculateMonthlySummary(
        expenses: List<Expense>,
        incomeTransactions: List<AccountTransaction>,
        expenseTransactions: List<AccountTransaction>,
        allCategories: List<CategoryEntity>
    ): MonthlySummary {
        val totalIncome = incomeTransactions.sumOf { it.amount }
        val totalExpenses = expenseTransactions.sumOf { it.amount }
        val savings = totalIncome - totalExpenses

        val expenseByCategory = expenses.groupBy { it.categoryId }
            .map { (categoryId, expenseList) ->
                val category = CategoryEnum.fromId(categoryId)
                val totalAmount = expenseList.sumOf { it.amount }
                val percentage = if (totalIncome > 0) {
                    (totalAmount / totalIncome) * 100
                } else 0.0
                ExpenseByCategory(category, totalAmount, percentage)
            }
            .sortedByDescending { it.totalAmount }

        return MonthlySummary(
            totalIncome = totalIncome,
            totalExpenses = totalExpenses,
            savings = savings,
            expenseByCategory = expenseByCategory
        )
    }

    fun getMonthlySummary(): MonthlySummary {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = System.currentTimeMillis()
        val startOfMonth = Calendar.getInstance().apply {
            set(Calendar.DAY_OF_MONTH, 1)
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis
        val endOfMonth = Calendar.getInstance().apply {
            set(Calendar.DAY_OF_MONTH, 1)
            set(Calendar.MONTH, Calendar.getInstance().get(Calendar.MONTH) + 1)
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis - 1

        val expenses = expenseDao.getExpensesByDateRange(startOfMonth, endOfMonth)
        val incomeTransactions = accountDao.getIncomeTransactions()
        val expenseTransactions = accountDao.getAccountExpenseTransactions()
        val allCategories = getCategoryList()
        val expenseByCategory = expenses.groupBy { it.categoryId }
            .map { (categoryId, expenseList) ->
                val category = CategoryEnum.fromId(categoryId)
                val totalAmount = expenseList.sumOf { it.amount }
                val percentage = if (incomeTransactions.sumOf { it.amount } > 0) {
                    (totalAmount / incomeTransactions.sumOf { it.amount }) * 100
                } else 0.0
                ExpenseByCategory(category, totalAmount, percentage)
            }

        return MonthlySummary(
            totalIncome = incomeTransactions.sumOf { it.amount },
            totalExpenses = expenseTransactions.sumOf { it.amount },
            savings = incomeTransactions.sumOf { it.amount } - expenseTransactions.sumOf { it.amount },
            expenseByCategory = expenseByCategory
        )
    }

    fun getWeeklySummary(): MonthlySummary {
        val calendar = Calendar.getInstance()
        val calendarTime = calendar.timeInMillis
        
        val calendarStart = Calendar.getInstance().apply {
            timeInMillis = calendarTime
            set(Calendar.DAY_OF_WEEK, getFirstDayOfWeek())
        }
        
        val startOfDay = Calendar.getInstance().apply {
            timeInMillis = calendarStart.timeInMillis
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis

        val endOfDay = Calendar.getInstance().apply {
            timeInMillis = startOfDay + 7 * 24 * 60 * 60 * 1000
            set(Calendar.HOUR_OF_DAY, 23)
            set(Calendar.MINUTE, 59)
            set(Calendar.SECOND, 59)
            set(Calendar.MILLISECOND, 999)
        }.timeInMillis

        val expenses = expenseDao.getExpensesByDateRange(startOfDay, endOfDay)
        val incomeTransactions = accountDao.getIncomeTransactions()
        val expenseTransactions = accountDao.getAccountExpenseTransactions()
        val allCategories = getCategoryList()
        val expenseByCategory = expenses.groupBy { it.categoryId }
            .map { (categoryId, expenseList) ->
                val category = CategoryEnum.fromId(categoryId)
                val totalAmount = expenseList.sumOf { it.amount }
                val percentage = if (incomeTransactions.sumOf { it.amount } > 0) {
                    (totalAmount / incomeTransactions.sumOf { it.amount }) * 100
                } else 0.0
                ExpenseByCategory(category, totalAmount, percentage)
            }

        return MonthlySummary(
            totalIncome = incomeTransactions.sumOf { it.amount },
            totalExpenses = expenseTransactions.sumOf { it.amount },
            savings = incomeTransactions.sumOf { it.amount } - expenseTransactions.sumOf { it.amount },
            expenseByCategory = expenseByCategory
        )
    }

    private fun getFirstDayOfWeek(): Int {
        return Calendar.MONDAY
    }
}

object DatabaseClient {
    @Volatile
    private var INSTANCE: ExpenseTrackerDatabase? = null

    fun getDatabase(context: Context): ExpenseTrackerDatabase {
        return INSTANCE ?: synchronized(this) {
            INSTANCE ?: androidx.room.Room.databaseBuilder(
                context.applicationContext,
                ExpenseTrackerDatabase::class.java,
                "expense-tracker.db"
            ).fallbackToDestructiveMigration().build()
        }
    }
}
