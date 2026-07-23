# Release Notes — v1.0.0

## Initial Release

### Features

- **Expense tracking** — Log expenses with title, amount, category, notes, and optional account deduction
- **Multi-account management** — Create checking, savings, and cash accounts with real-time balance updates
- **Income recording** — Add income to any account with automatic balance recalculation
- **Monthly budget** — Set a monthly spending target; track progress with a visual progress bar (spent vs. remaining)
- **Category analytics** — 11 default categories (Food, Transportation, Utilities, Entertainment, Shopping, Healthcare, Insurance, Housing, Education, Personal, Other) with percentage breakdowns and a color-coded stacked bar chart
- **Month-over-month comparison** — See how current spending trends compare to the previous month
- **Home dashboard** — Monthly summary, budget card, top spending categories, and quick-action buttons
- **Full expense list** — Scrollable list with category icons, amounts, dates, and delete support
- **Budget persistence** — Monthly budget survives app restarts via `SharedPreferences`

### Technical

- Kotlin 2.0.0 with Jetpack Compose + Material 3
- Room 2.6.1 local SQLite database with reactive `Flow`-based UI updates
- Navigation Compose 2.8.2 — single-Activity architecture with a shared `MainViewModel`
- KSP annotation processor for Room DAO generation
- No DI framework — lightweight manual singleton via `DatabaseClient`
- `fallbackToDestructiveMigration()` — schema changes clear existing data
- `minSdk 26`, `targetSdk 34`, `jvmTarget 17`
