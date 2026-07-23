package com.expensetracker.viewmodel

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.expensetracker.model.*
import com.expensetracker.repository.DatabaseClient
import com.expensetracker.repository.ExpenseRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Calendar

class MainViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: ExpenseRepository = ExpenseRepository(
        DatabaseClient.getDatabase(application.applicationContext)
    )

    private val _expenseList = MutableStateFlow<List<Expense>>(emptyList())
    val expenseList: StateFlow<List<Expense>> = _expenseList

    private val _accountList = MutableStateFlow<List<Account>>(emptyList())
    val accountList: StateFlow<List<Account>> = _accountList

    private val _monthlySummary = MutableStateFlow<MonthlySummary>(MonthlySummary())
    val monthlySummary: StateFlow<MonthlySummary> = _monthlySummary

    private val _categories = MutableStateFlow<List<CategoryEntity>>(emptyList())
    val categories: StateFlow<List<CategoryEntity>> = _categories

    private val _incomeTransactions = MutableStateFlow<List<AccountTransaction>>(emptyList())
    val incomeTransactions: StateFlow<List<AccountTransaction>> = _incomeTransactions

    private val _accountTransactions = MutableStateFlow<List<AccountTransaction>>(emptyList())
    val accountTransactions: StateFlow<List<AccountTransaction>> = _accountTransactions

    private val _monthlyBudget = MutableStateFlow(loadBudget())
    val monthlyBudget: StateFlow<Double> = _monthlyBudget

    private val _lastMonthExpenses = MutableStateFlow(0.0)
    val lastMonthExpenses: StateFlow<Double> = _lastMonthExpenses

    init {
        seedCategories()
        collectFlows()
    }

    private fun loadBudget(): Double {
        val prefs = getApplication<Application>().getSharedPreferences("budget_prefs", Context.MODE_PRIVATE)
        return prefs.getFloat("monthly_budget", 0f).toDouble()
    }

    private fun saveBudget(amount: Double) {
        val prefs = getApplication<Application>().getSharedPreferences("budget_prefs", Context.MODE_PRIVATE)
        prefs.edit().putFloat("monthly_budget", amount.toFloat()).apply()
    }

    fun setMonthlyBudget(amount: Double) {
        _monthlyBudget.value = amount
        saveBudget(amount)
    }

    private fun seedCategories() {
        viewModelScope.launch(Dispatchers.IO) {
            repository.seedDefaultCategories()
        }
    }

    private fun startOfMonthMillis(): Long {
        return Calendar.getInstance().apply {
            set(Calendar.DAY_OF_MONTH, 1)
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis
    }

    private fun endOfMonthMillis(): Long {
        return Calendar.getInstance().apply {
            set(Calendar.DAY_OF_MONTH, 1)
            add(Calendar.MONTH, 1)
            add(Calendar.DAY_OF_MONTH, -1)
            set(Calendar.HOUR_OF_DAY, 23)
            set(Calendar.MINUTE, 59)
            set(Calendar.SECOND, 59)
            set(Calendar.MILLISECOND, 999)
        }.timeInMillis
    }

    private fun startOfLastMonthMillis(): Long {
        return Calendar.getInstance().apply {
            add(Calendar.MONTH, -1)
            set(Calendar.DAY_OF_MONTH, 1)
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis
    }

    private fun endOfLastMonthMillis(): Long {
        return Calendar.getInstance().apply {
            add(Calendar.MONTH, -1)
            set(Calendar.DAY_OF_MONTH, 1)
            add(Calendar.MONTH, 1)
            add(Calendar.DAY_OF_MONTH, -1)
            set(Calendar.HOUR_OF_DAY, 23)
            set(Calendar.MINUTE, 59)
            set(Calendar.SECOND, 59)
            set(Calendar.MILLISECOND, 999)
        }.timeInMillis
    }

    private fun collectFlows() {
        viewModelScope.launch {
            repository.getAllExpensesFlow().collect { _expenseList.value = it }
        }
        viewModelScope.launch {
            repository.getAllAccountsFlow().collect { _accountList.value = it }
        }
        viewModelScope.launch {
            repository.getAllCategoriesFlow().collect { _categories.value = it }
        }
        viewModelScope.launch {
            repository.getIncomeTransactionsFlow().collect { _incomeTransactions.value = it }
        }
        viewModelScope.launch {
            repository.getExpenseTransactionsFlow().collect { _accountTransactions.value = it }
        }
        viewModelScope.launch {
            combine(
                repository.getAllExpensesFlow(),
                repository.getIncomeTransactionsFlow()
            ) { allExpenses, incomeTxns ->
                val start = startOfMonthMillis()
                val end = endOfMonthMillis()

                val monthExpenses = allExpenses.filter { it.date in start..end }
                val monthIncome = incomeTxns.filter { it.date in start..end }.sumOf { it.amount }
                val totalExpenses = monthExpenses.sumOf { it.amount }
                val expenseByCategory = monthExpenses.groupBy { it.categoryId }
                    .map { (categoryId, expenseList) ->
                        val category = CategoryEnum.fromId(categoryId)
                        val totalAmount = expenseList.sumOf { it.amount }
                        val percentage = if (totalExpenses > 0) (totalAmount / totalExpenses) * 100 else 0.0
                        ExpenseByCategory(category, totalAmount, percentage)
                    }
                    .sortedByDescending { it.totalAmount }

                MonthlySummary(
                    totalIncome = monthIncome,
                    totalExpenses = totalExpenses,
                    savings = monthIncome - totalExpenses,
                    expenseByCategory = expenseByCategory
                )
            }.collect { _monthlySummary.value = it }
        }

        viewModelScope.launch {
            combine(
                _expenseList,
                _incomeTransactions,
                _accountTransactions
            ) { allExpenses, incomeTxns, expenseTxns ->
                val start = startOfLastMonthMillis()
                val end = endOfLastMonthMillis()
                expenseTxns.filter { it.date in start..end }.sumOf { it.amount }
            }.collect { _lastMonthExpenses.value = it }
        }
    }

    suspend fun insertExpense(
        title: String,
        amount: Double,
        categoryId: Long,
        notes: String = "",
        accountId: Long? = null
    ) = withContext(Dispatchers.IO) {
        val calendar = Calendar.getInstance()
        val expense = Expense(
            title = title,
            amount = amount,
            categoryId = categoryId,
            date = calendar.timeInMillis,
            notes = notes
        )
        repository.insertExpense(expense)
        if (accountId != null) {
            repository.deductFromAccount(accountId, amount, "Expense: $title")
        }
    }

    fun deleteExpense(expenseId: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteExpense(expenseId)
        }
    }

    suspend fun addIncomeToAccount(accountId: Long, amount: Double, notes: String = "") = withContext(Dispatchers.IO) {
        repository.addIncomeToAccount(accountId, amount, notes)
    }

    fun deductFromAccount(accountId: Long, amount: Double, notes: String = "") {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deductFromAccount(accountId, amount, notes)
        }
    }

    suspend fun createAccount(
        name: String,
        initialBalance: Double = 0.0,
        accountType: String = "checking"
    ) = withContext(Dispatchers.IO) {
        val account = Account(
            name = name,
            initialBalance = initialBalance,
            currentBalance = initialBalance,
            type = accountType
        )
        repository.insertAccount(account)
    }

    fun getExpenseById(expenseId: Long): Expense? {
        return _expenseList.value.find { it.id == expenseId }
    }

    fun getExpenseByCategoryId(categoryId: Long): List<Expense> {
        return _expenseList.value.filter { it.categoryId == categoryId }
    }

    fun getCategoryById(categoryId: Long): Category? {
        return _categories.value.find { it.id == categoryId }?.let {
            Category(id = it.id, name = it.name, icon = it.icon, color = it.color)
        }
    }

    fun getAccountDaoGetIncomeTransactions() = _incomeTransactions
}
