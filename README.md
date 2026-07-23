# Expense Tracker

A modern Android expense tracking app built with Jetpack Compose and Material 3. Track expenses across multiple accounts, set monthly budgets, and visualize spending with category breakdowns.

## Features

- **Multi-Bank Account Support** — Create checking, savings, and cash accounts; track income/expense transactions per account
- **Expense Management** — Log expenses with title, amount, category, and notes; expenses automatically deduct from selected accounts
- **Monthly Budget** — Set a monthly spending target and track progress with a visual budget card
- **Category Analytics** — 11 default categories (Food, Transportation, Utilities, Entertainment, etc.) with color-coded stacked bar charts and percentage breakdowns
- **Month-over-Month Comparison** — See how this month's spending compares to last month
- **Account Transactions** — View income and expense history per account

## Tech Stack

- **Kotlin** 2.0.0 with Jetpack Compose + Material 3
- **Navigation Compose** 2.8.2 — single-Activity navigation with shared ViewModel
- **Room** 2.6.1 — local SQLite database with reactive Flow queries
- **KSP** annotation processor for Room DAO generation
- **No DI framework** — manual singleton via `DatabaseClient`

## Screens

| Screen | Description |
|---|---|
| Home | Monthly summary, budget card, category breakdown, quick actions |
| Expenses | Full expense list with swipe-to-delete |
| Add Expense | Form with category picker and account selection |
| Accounts | Account list with current balances and recent income |
| Add Account / Add Income | Create accounts or add income to existing ones |
| Analytics | Category breakdown with stacked bar chart and summary cards |

## Build

```powershell
.\gradlew assembleDebug
```

Database version 1 with `fallbackToDestructiveMigration()` — schema changes will clear existing data.
