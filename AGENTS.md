# ExpenseTracker — AGENTS.md

## Stack
- **Language:** Kotlin 2.0.0, jvmTarget=17
- **UI:** Jetpack Compose (BOM 2024.12.01) + Material 3
- **Navigation:** Navigation Compose 2.8.2
- **Database:** Room 2.6.1 with KSP annotation processor
- **Architecture:** Single-Activity, single `MainViewModel` shared across all screens (created at `AppNavigation` level — do NOT use `viewModel()` defaults per screen)
- **No DI framework** — manual singleton via `DatabaseClient` object

## Build & run
```powershell
.\gradlew assembleDebug
```
Kotlin compilation only (skips Java): `.\gradlew :app:compileDebugKotlin`

Room generates DAO implementations via KSP — run `compileDebugKotlin` (or `kspDebugKotlin`) to regenerate after changing DAOs or entities.

Database version is 1 with `fallbackToDestructiveMigration()` — schema changes destroy existing data.

## Architecture quirks
- All screens share one `MainViewModel` scoped to the Activity. ViewModel state (`expenseList`, `accountList`, `categories`, `monthlySummary`, etc.) is kept in `MutableStateFlow`s that are populated by collecting Room `Flow<List<T>>` DAO queries.
- **Mutation pattern:** ViewModel methods that write to the DB are `suspend` functions using `withContext(Dispatchers.IO)`. UI screens await them with `scope.launch { viewModel.addX(...); onBack() }` so navigation only fires after the DB write completes.
- Room Flow queries auto-refresh all screens when the DB changes — no manual `loadAllData()` calls.

## Key navigation routes (Navigation.kt)
| Route | Screen | Notes |
|---|---|---|
| `home` | `HomeScreen` | Start destination |
| `expenses` | `ExpensesScreen` | Expense list, FAB → add_expense |
| `add_expense` | `AddExpenseScreen` | Has account dropdown, deducts from account |
| `accounts` | `AccountsScreen` | Account list, has Add Income button |
| `add_account` | `AddAccountScreen` | |
| `add_income` | `AddIncomeScreen` | |
| `analytics` | `AnalyticsScreen` | Category breakdown with stacked bar |

## Data model
- **`Expense`**: id, title, amount, categoryId, date, notes (no FK to Account — deduction is a separate AccountTransaction)
- **`Account`**: id, name, initialBalance, currentBalance, type, createdAt
- **`AccountTransaction`**: id, accountId (FK), amount, type("income"/"expense"), date, notes
- **`CategoryEntity`**: id, name, icon, color, isDefault — seeded on first launch via `repository.seedDefaultCategories()` (11 hardcoded defaults)

## Currency & locale
All currency display uses `java.text.NumberFormat.getCurrencyInstance()` — never hardcode `"$"`.

## Budget
- Monthly budget stored in SharedPreferences (`budget_prefs` / `monthly_budget` as Float)
- Accessible via `viewModel.monthlyBudget` (StateFlow) and `viewModel.setMonthlyBudget(amount)`
- HomeScreen shows a `BudgetCard` (budget / spent / remaining)

## DAO patterns
- Each DAO has both blocking `List<T>` methods (for mutation flows) and `Flow<List<T>>` methods (for reactive UI state). Add both when adding new queries.
- When adding a column to an entity, bump `version` in `@Database` annotation.
