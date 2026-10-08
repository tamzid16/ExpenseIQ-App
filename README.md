# ExpenseIQ

> A local-first personal expense tracker for Android built with Kotlin and Jetpack Compose.

ExpenseIQ is a privacy-focused mobile application for managing everyday expenses, monthly budgets and recurring costs in one place. It is designed around a simple idea: personal financial data should remain under the user's control while the app still provides useful spending insights and clear monthly trends.

The app works offline, stores financial records locally and provides a clean interface for tracking where money goes over time.

---

## ✨ Highlights

- 🔐 Local account with protected session storage
- 💰 Expense and budget management
- 📊 Monthly, yearly and category-based spending analytics
- 📈 Monthly spending trend comparison
- 🔁 Recurring expense management
- 🌐 English and Bangla language support
- 🇧🇩 Bangladeshi Taka (৳) support
- 🌙 Light, dark and system themes
- 📱 Offline-first experience
- 🗃️ Local Room database
- 🔎 Expense search, filtering and sorting
- 📥 CSV expense import
- 📅 Monthly financial setup
- ⚠️ Budget usage warnings
- 🧾 Detailed expense history

---

## 📱 Screenshots

### Dashboard

![ExpenseIQ Dashboard](docs/screenshots/dashboard.png)

The dashboard gives a quick overview of the current month's financial activity, including total spending, budget usage, remaining budget and recent expenses.

### Analytics

![ExpenseIQ Analytics](docs/screenshots/analytics.png)

Analytics provides visual spending information across months, years and expense categories so users can understand their spending patterns.

### Financial Setup

![ExpenseIQ Financial Setup](docs/screenshots/setup.png)

During the first-time setup, users can define their monthly allowance and regular financial obligations to establish a useful monthly baseline.

> Screenshots use sample data and do not contain real personal financial information.

---

# 🎯 Why ExpenseIQ?

Many expense trackers focus on collecting financial information in a cloud service. ExpenseIQ takes a different approach.

The core application is **local-first**.

Your expenses, budgets and recurring financial records are stored locally on the Android device instead of depending on a remote application server.

This provides three practical benefits:

- The application can work without an internet connection.
- Personal expense records remain on the user's device.
- The application does not require a cloud account or third-party financial service.

The goal is not to build a complicated financial platform. It is to make everyday expense tracking quick, understandable and private.

---

# 🧩 Core Features

## 👤 Account & Session

ExpenseIQ provides a simple local account system.

Users can:

- Create an account
- Sign in
- Sign out
- Maintain a persistent login session
- Continue using the application after restarting the device

The session is protected using Android Keystore-backed encryption.

---

## 💰 Expense Management

Users can record individual expenses with:

- Title
- Amount
- Category
- Date
- Optional notes

Expenses can be:

- Added
- Edited
- Deleted
- Searched
- Filtered
- Sorted

Deleting an expense requires confirmation to prevent accidental removal.

---

## 🗂️ Expense Categories

ExpenseIQ organizes spending into practical categories such as:

- Food
- Transportation
- Shopping
- Bills
- Entertainment
- Health
- Education
- Travel
- Other

Category-based analysis makes it easier to identify where the majority of monthly spending goes.

---

## 🔁 Recurring Expenses

Regular financial obligations can be recorded as recurring expenses.

Supported frequencies include:

- Weekly
- Monthly
- Yearly

Typical examples include:

- Rent
- Internet
- Subscriptions
- Memberships
- Regular bills

The feature is intentionally kept simple so recurring financial obligations remain easy to manage.

---

# 📊 Budget Management

Users can create a monthly spending budget and monitor it throughout the month.

The budget section displays:

- Total budget
- Total spent
- Remaining budget
- Percentage used
- Progress indicator

ExpenseIQ provides visual warnings as spending approaches the budget limit.

### Budget thresholds

| Usage | Status |
| --- | --- |
| Below 80% | Normal |
| Around 80% | Warning |
| Around 90% | High usage |
| 100% or more | Budget exceeded |

---

# 📅 Monthly Financial Setup

When a user creates an account for the first time, ExpenseIQ collects a basic financial baseline.

The setup includes:

- Monthly allowance or income
- Rent
- Monthly bills
- Other fixed expenses
- Monthly spending budget
- Preferred language
- Preferred currency

This information helps establish a realistic starting point for monthly financial tracking.

---

# 📈 Analytics

ExpenseIQ turns recorded expenses into simple visual insights.

### Monthly spending

View total spending for individual months and compare financial activity over time.

### Yearly spending

Understand how spending changes across an entire year.

### Category analysis

Identify the categories responsible for the largest portion of spending.

### Key metrics

The analytics section includes:

- Total spending
- Average monthly spending
- Highest spending category
- Highest spending month
- Monthly spending comparison
- Category-wise spending

The charts are designed specifically for mobile screens so the information remains readable without overwhelming the user.

---

# 📉 Monthly Trends

ExpenseIQ keeps monthly financial records so users can compare different periods.

For example:

```text
January    ৳18,500
February   ৳21,200
March      ৳19,750
April      ৳23,100
```

This makes it easier to recognize whether spending is increasing, decreasing or remaining relatively stable.

Monthly records are associated with their respective year and month rather than being treated as one continuously changing value.

---

# 📥 CSV Import

Existing expense records can be imported using a CSV file.

### Supported format

```csv
title,amount,category,date,notes
Lunch,12.50,Food,2026-10-01,Office lunch
Uber,18.00,Transportation,2026-10-02,Airport
```

The import process validates records before saving them and provides a result showing:

- Successfully imported records
- Failed records
- Invalid data

This makes it possible to move existing expense records into ExpenseIQ without entering everything manually.

---

# 🌐 Language & Currency

### Languages

ExpenseIQ supports:

- English
- বাংলা

The language can be selected according to the user's preference.

### Currency

Bangladeshi Taka is supported as the primary currency:

- ৳ BDT

The application also supports additional currency symbols for users who prefer another display currency.

---

# 🎨 User Experience

ExpenseIQ uses a clean Material 3 interface designed for frequent everyday use.

The interface includes:

- Rounded cards
- Clear typography
- Category icons
- Progress indicators
- Mobile-friendly charts
- Empty states
- Loading states
- Error feedback
- Confirmation dialogs
- Responsive layouts
- Light theme
- Dark theme
- System theme

The navigation is organized around the most frequently used areas:

- Home
- Expenses
- Analytics
- Budget

The goal is to keep important financial information accessible within a few taps.

---

# 🔒 Privacy & Security

Privacy is a core part of the application's design.

### Local data storage

ExpenseIQ stores application data in a local Room database on the Android device.

The application does not require:

- A cloud database
- A financial institution connection
- A remote account service
- A third-party analytics platform

### Password protection

Account passwords are not stored as plain text.

Passwords are processed using a salted password-hashing mechanism before being stored locally.

### Session protection

Persistent session information is encrypted before being stored.

The encryption key is protected through the Android Keystore system rather than being embedded directly in the application source.

### No embedded secrets

The application does not require an API key or cloud service credential to operate.

No private credentials should be committed to the repository.

---

# 🏗️ Architecture

ExpenseIQ follows a layered Android architecture designed to keep the user interface separate from data access and application logic.

```text
┌─────────────────────────────┐
│       Jetpack Compose       │
│          UI Layer           │
└──────────────┬──────────────┘
               │
               ▼
┌─────────────────────────────┐
│          ViewModel          │
│        UI State / Flow      │
└──────────────┬──────────────┘
               │
               ▼
┌─────────────────────────────┐
│         Repository          │
│   Application Data Access   │
└──────────────┬──────────────┘
               │
               ▼
┌─────────────────────────────┐
│            Room             │
│       Local Database        │
└─────────────────────────────┘
```

This separation keeps database operations away from Compose screens and makes the application easier to maintain and test.

---

# 🗃️ Data Model

The application uses several main local entities.

### User

Stores local account and preference information.

```text
User
├── account information
├── language
├── currency
└── monthly allowance
```

### Expense

Represents an individual spending record.

```text
Expense
├── title
├── amount
├── category
├── date
├── notes
└── recurring information
```

### Budget

Stores monthly budget information.

```text
Budget
├── year
├── month
└── amount
```

### Recurring Expense

Stores regular financial obligations.

```text
RecurringExpense
├── title
├── amount
├── category
├── frequency
└── next occurrence
```

---

# 🛠️ Technology Stack

| Layer | Technology |
| --- | --- |
| Language | Kotlin |
| UI | Jetpack Compose |
| Design System | Material 3 |
| Architecture | ViewModel + Repository |
| Database | Room |
| Async Operations | Kotlin Coroutines |
| State Management | StateFlow |
| Navigation | Navigation Compose |
| Charts | Custom Compose Components |
| Build System | Gradle Kotlin DSL |
| Minimum SDK | Android API 24 |
| Target SDK | Android API 36 |

---

# 📂 Project Structure

```text
app/
└── src/
    └── main/
        └── java/
            └── com/
                └── expenseiq/
                    └── app/
                        ├── data/
                        │   ├── local/
                        │   │   ├── database/
                        │   │   └── dao/
                        │   ├── model/
                        │   └── repository/
                        │
                        ├── security/
                        │
                        └── ui/
                            ├── components/
                            ├── screens/
                            ├── theme/
                            ├── util/
                            └── viewmodel/
```

The exact package structure may evolve as the application develops but the main separation remains between data, security and UI responsibilities.

---

# 🧪 Testing

The project includes unit and Android instrumentation testing support.

Important application flows should be verified before release:

- Account registration
- Login
- Logout
- Persistent session
- Financial setup
- Expense creation
- Expense editing
- Expense deletion
- Recurring expenses
- Budget calculations
- Budget warnings
- CSV import validation
- Monthly analytics
- Yearly analytics
- Category analysis
- Monthly trend comparison
- Language switching
- Currency selection
- Theme switching
- Local data isolation

---

# 🚀 Getting Started

### Requirements

Before running the project, install:

- Android Studio
- Android SDK
- JDK 17
- Android SDK Platform API 36
- Android emulator or compatible Android device

### Installation

Clone the repository:

```bash
git clone [https://github.com/your-username/expenseiq.git](https://github.com/your-username/expenseiq.git)
```

Open the project in Android Studio and allow Gradle to synchronize the project.

Then select an emulator or connected Android device and run the app configuration.

No external API key is required for the core application.

---

# 📱 Typical User Flow

```text
Launch
   │
   ▼
Login / Registration
   │
   ▼
First-time Financial Setup
   │
   ├── Monthly Allowance
   ├── Fixed Expenses
   ├── Budget
   ├── Language
   └── Currency
   │
   ▼
Dashboard
   │
   ├── Add Expense
   ├── Expense History
   ├── Analytics
   └── Budget
   │
   ▼
Monthly Tracking
   │
   ▼
Historical Comparison
```

---

# 🧠 Design Principles

### Keep financial tracking simple
Users should be able to record an expense quickly without navigating through complicated forms.

### Make important information visible
Budget usage, recent expenses and monthly spending should be understandable at a glance.

### Keep data close to the user
The application uses local storage for its core financial records rather than requiring a remote service.

### Separate data from the interface
Repositories and ViewModels keep storage and application logic separate from Compose screens.

### Design for everyday use
Search, filtering, recurring expenses, monthly budgets and trend analysis are included because they solve common problems during regular expense tracking.

---

# 🔮 Future Improvements

Potential improvements for future versions include:

- Encrypted user-controlled backup and restore
- More detailed financial reports
- Advanced recurring expense scheduling
- Additional accessibility improvements
- More powerful monthly comparisons
- Optional synchronization across devices
- Additional export formats
- Automated database migration support for future releases

These features are intentionally kept separate from the current core application so the existing experience remains simple.

---

# 📌 Current Scope

ExpenseIQ is currently an Android-first local expense management application.

It focuses on:

- Personal expense tracking
- Monthly budgeting
- Recurring financial obligations
- Spending analytics
- Monthly comparisons
- Local privacy
- Simple everyday usability

It is not intended to replace banking applications, accounting software or professional financial management systems.

---

# 📄 License

A license should be selected and added to the repository before public distribution.

Until a license is included, the source code remains under the rights of the repository owner and should not be assumed to be freely reusable.
