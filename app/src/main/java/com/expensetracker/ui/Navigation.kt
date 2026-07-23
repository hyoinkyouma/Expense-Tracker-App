package com.expensetracker.ui

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.expensetracker.viewmodel.MainViewModel

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val viewModel: MainViewModel = viewModel()

    NavHost(
        navController = navController,
        startDestination = "home"
    ) {
        composable("home") {
            HomeScreen(
                viewModel = viewModel,
                onNavigateToExpenses = { navController.navigate("expenses") },
                onNavigateToAddExpense = { navController.navigate("add_expense") },
                onNavigateToAccounts = { navController.navigate("accounts") },
                onNavigateToAnalytics = { navController.navigate("analytics") }
            )
        }

        composable("expenses") {
            ExpensesScreen(
                viewModel = viewModel,
                onNavigateToAddExpense = { navController.navigate("add_expense") },
                onBack = { navController.popBackStack() }
            )
        }

        composable("add_expense") {
            AddExpenseScreen(
                viewModel = viewModel,
                onExpenseAdded = { navController.popBackStack() },
                onBack = { navController.popBackStack() }
            )
        }

        composable("accounts") {
            AccountsScreen(
                viewModel = viewModel,
                onNavigateToAddAccount = { navController.navigate("add_account") },
                onNavigateToAddIncome = { navController.navigate("add_income") },
                onBack = { navController.popBackStack() }
            )
        }

        composable("add_account") {
            AddAccountScreen(
                viewModel = viewModel,
                onAccountAdded = { navController.popBackStack() },
                onBack = { navController.popBackStack() }
            )
        }

        composable("analytics") {
            AnalyticsScreen(
                viewModel = viewModel,
                onBack = { navController.popBackStack() }
            )
        }

        composable("add_income") {
            AddIncomeScreen(
                viewModel = viewModel,
                onIncomeAdded = { navController.popBackStack() },
                onBack = { navController.popBackStack() }
            )
        }
    }
}
