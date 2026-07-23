package com.expensetracker.room

import androidx.room.*
import com.expensetracker.model.*
import kotlinx.coroutines.flow.Flow
import java.util.Calendar

@Database(
    entities = [Expense::class, Account::class, AccountTransaction::class, CategoryEntity::class],
    version = 1,
    exportSchema = false
)
abstract class ExpenseTrackerDatabase : RoomDatabase() {
    abstract fun expenseDao(): ExpenseDao
    abstract fun accountDao(): AccountDao
    abstract fun categoryDao(): CategoryDao
    abstract fun accountTransactionDao(): AccountTransactionDao
}

@Dao
interface ExpenseDao {
    @Query("SELECT * FROM expenses ORDER BY date DESC")
    fun getAllExpenses(): List<Expense>

    @Query("SELECT * FROM expenses ORDER BY date DESC")
    fun getAllExpensesFlow(): Flow<List<Expense>>

    @Query("SELECT * FROM expenses WHERE date >= :startDate AND date <= :endDate ORDER BY date DESC")
    fun getExpensesByDateRange(startDate: Long, endDate: Long): List<Expense>

    @Query("SELECT * FROM expenses WHERE categoryId = :categoryId ORDER BY date DESC")
    fun getExpensesByCategory(categoryId: Long): List<Expense>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertExpense(expense: Expense): Long

    @Query("SELECT SUM(amount) FROM expenses WHERE date >= :startDate AND date <= :endDate")
    fun getTotalExpensesByDateRange(startDate: Long, endDate: Long): Double?

    @Query("SELECT amount FROM expenses WHERE id = :expenseId")
    fun getExpenseAmount(expenseId: Long): Double?

    @Query("DELETE FROM expenses WHERE id = :expenseId")
    fun deleteExpense(expenseId: Long)

    @Query("SELECT DISTINCT categoryId FROM expenses")
    fun getUniqueCategoryIds(): List<Long>
}

@Dao
interface AccountDao {
    @Query("SELECT * FROM accounts ORDER BY createdAt DESC")
    fun getAllAccounts(): List<Account>

    @Query("SELECT * FROM accounts ORDER BY createdAt DESC")
    fun getAllAccountsFlow(): Flow<List<Account>>

    @Query("SELECT * FROM accounts WHERE id = :accountId")
    fun getAccount(accountId: Long): Account?

    @Query("SELECT * FROM accounts WHERE id = :accountId ORDER BY createdAt DESC LIMIT 1")
    fun getLatestAccount(accountId: Long): Account?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAccount(account: Account): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAccounts(accounts: List<Account>): List<Long>

    @Update
    fun updateAccount(account: Account)

    @Query("SELECT * FROM account_transactions ORDER BY timestamp DESC")
    fun getAllAccountTransactions(): List<AccountTransaction>

    @Query("SELECT * FROM account_transactions WHERE timestamp >= :startDate AND timestamp <= :endDate ORDER BY timestamp DESC")
    fun getTransactionsByDateRange(startDate: Long, endDate: Long): List<AccountTransaction>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAccountTransaction(transaction: AccountTransaction): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAccountTransactions(transactions: List<AccountTransaction>): List<Long>

    @Query("SELECT SUM(amount) FROM account_transactions WHERE type = 'income'")
    fun getTotalIncome(): Double?

    @Query("SELECT SUM(amount) FROM account_transactions WHERE type = 'expense'")
    fun getTotalAccountExpenses(): Double?

    @Query("DELETE FROM account_transactions WHERE id = :transactionId")
    fun deleteAccountTransaction(transactionId: Long)

    @Query("SELECT * FROM account_transactions WHERE accountId = :accountId ORDER BY timestamp DESC")
    fun getAccountTransactions(accountId: Long): List<AccountTransaction>

    @Query("SELECT * FROM account_transactions WHERE type = 'income' ORDER BY timestamp DESC")
    fun getIncomeTransactions(): List<AccountTransaction>

    @Query("SELECT * FROM account_transactions WHERE type = 'expense' ORDER BY timestamp DESC")
    fun getAccountExpenseTransactions(): List<AccountTransaction>

    @Query("SELECT * FROM account_transactions WHERE timestamp >= :startDate AND timestamp <= :endDate ORDER BY timestamp DESC")
    fun getAccountTransactionsByDateRange(startDate: Long, endDate: Long): List<AccountTransaction>
}

@Dao
interface AccountTransactionDao {
    @Query("SELECT * FROM account_transactions ORDER BY timestamp DESC")
    fun getAllAccountTransactions(): List<AccountTransaction>

    @Query("SELECT * FROM account_transactions WHERE accountId = :accountId ORDER BY timestamp DESC")
    fun getTransactionsByAccountId(accountId: Long): List<AccountTransaction>

    @Query("SELECT * FROM account_transactions WHERE timestamp >= :startDate AND timestamp <= :endDate ORDER BY timestamp DESC")
    fun getTransactionsByDateRange(startDate: Long, endDate: Long): List<AccountTransaction>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertTransaction(transaction: AccountTransaction): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertTransactions(transactions: List<AccountTransaction>): List<Long>

    @Delete
    fun deleteTransaction(transaction: AccountTransaction)

    @Query("DELETE FROM account_transactions WHERE id = :transactionId")
    fun deleteTransactionById(transactionId: Long)

    @Query("SELECT * FROM account_transactions WHERE type = 'income' ORDER BY timestamp DESC")
    fun getIncomeTransactions(): List<AccountTransaction>

    @Query("SELECT * FROM account_transactions WHERE type = 'income' ORDER BY timestamp DESC")
    fun getIncomeTransactionsFlow(): Flow<List<AccountTransaction>>

    @Query("SELECT * FROM account_transactions WHERE type = 'expense' ORDER BY timestamp DESC")
    fun getExpenseTransactions(): List<AccountTransaction>

    @Query("SELECT * FROM account_transactions WHERE type = 'expense' ORDER BY timestamp DESC")
    fun getExpenseTransactionsFlow(): Flow<List<AccountTransaction>>

    @Query("SELECT * FROM account_transactions WHERE accountId = :accountId AND type = 'income' ORDER BY timestamp DESC")
    fun getIncomeTransactionsByAccountId(accountId: Long): List<AccountTransaction>
}

@Dao
interface CategoryDao {
    @Query("SELECT * FROM categories")
    fun getAllCategories(): List<CategoryEntity>

    @Query("SELECT * FROM categories")
    fun getAllCategoriesFlow(): Flow<List<CategoryEntity>>

    @Query("SELECT * FROM categories WHERE id = :categoryId")
    fun getCategory(categoryId: Long): CategoryEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertCategory(category: CategoryEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertCategories(categories: List<CategoryEntity>): List<Long>

    @Query("SELECT * FROM categories WHERE isDefault = 1")
    fun getDefaultCategories(): List<CategoryEntity>
}
