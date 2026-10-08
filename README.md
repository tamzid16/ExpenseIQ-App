# ExpenseIQ

<p align="center">
  <img src="docs/screenshots/dashboard.png" alt="ExpenseIQ Banner" width="800"/>
</p>

<p align="center">
  <strong>A modern, offline-first personal finance tracker for Android built with Jetpack Compose.</strong>
</p>

<p align="center">
  <img src="https://img.shields.io/badge/Platform-Android-3DDC84?style=flat-square&logo=android&logoColor=white" alt="Platform" />
  <img src="https://img.shields.io/badge/Kotlin-2.0+-7F52FF?style=flat-square&logo=kotlin&logoColor=white" alt="Kotlin" />
  <img src="https://img.shields.io/badge/UI-Jetpack%20Compose-4285F4?style=flat-square&logo=jetpackcompose&logoColor=white" alt="Compose" />
  <img src="https://img.shields.io/badge/Min%20SDK-24-orange?style=flat-square" alt="Min SDK" />
  <img src="https://img.shields.io/badge/Target%20SDK-36-blue?style=flat-square" alt="Target SDK" />
  <img src="https://img.shields.io/badge/License-MIT-green?style=flat-square" alt="License" />
</p>

---

## Overview

**ExpenseIQ** is designed around a simple premise: financial tracking should be fast, insightful, and strictly private. 

Unlike traditional financial management tools that sync personal records to remote servers, ExpenseIQ operates entirely **offline-first**. All transaction logs, budgets, and historical metrics reside directly on the device—eliminating external dependencies, network latency, and privacy risks.

---

## Key Features

- **Local-First Architecture:** Complete data ownership. No cloud accounts, trackers, or remote dependencies.
- **Keystore-Backed Security:** Local session management secured via hardware-backed Android Keystore encryption and salted password hashing.
- **Dynamic Budgeting:** Configurable monthly limits with automated threshold alerts (80%, 90%, and budget-exceeded warnings).
- **Recurring Obligations:** Automated tracking for scheduled expenses across weekly, monthly, and yearly intervals.
- **Visual Analytics:** Breakdown of spending distribution by category, month-over-month comparisons, and historical trends.
- **CSV Data Portability:** Bulk transaction import engine equipped with pre-flight schema validation.
- **Localization:** Full interface support for English and Bangla (বাংলা), with native Bangladeshi Taka (৳ BDT) currency formatting.
- **Adaptive UI:** Material Design 3 interface with dynamic theming (Light, Dark, and System).

---

## Interface

| Dashboard | Analytics | Financial Setup |
| :---: | :---: | :---: |
| <img src="docs/screenshots/dashboard.png" width="240" alt="Dashboard"/> | <img src="docs/screenshots/analytics.png" width="240" alt="Analytics"/> | <img src="docs/screenshots/setup.png" width="240" alt="Setup"/> |
| Real-time budget monitoring and recent activity | Multi-period visual spending breakdown | Baseline allocation and recurring obligations |

---

## Architecture & Technology Stack

ExpenseIQ follows official Android Architecture Guidelines, enforcing a strict unidirectional data flow (UDF) across separate concerns.

```text
┌──────────────────────────────────────┐
│       Jetpack Compose (UI)           │  Declarative UI & Material 3
└──────────────────┬───────────────────┘
                   │ StateFlow / Events
                   ▼
┌──────────────────────────────────────┐
│       ViewModel (State Layer)        │  Business Logic & State Preservation
└──────────────────┬───────────────────┘
                   │ Coroutines / Flow
                   ▼
┌──────────────────────────────────────┐
│       Repository (Data Layer)        │  Single Source of Truth
└──────────────────┬───────────────────┘
                   │
                   ▼
┌──────────────────────────────────────┐
│       Room Database / Keystore       │  Encrypted SQLite Storage
└──────────────────────────────────────┘
```

### Tech Stack

| Layer | Component |
| --- | --- |
| **Language** | [Kotlin](https://kotlinlang.org/) |
| **User Interface** | [Jetpack Compose](https://developer.android.com/jetpack/compose) & [Material 3](https://m3.material.io/) |
| **Architecture** | MVVM + Repository Pattern |
| **Local Persistence** | [Room Database](https://developer.android.com/training/data-storage/room) |
| **Concurrency** | Kotlin Coroutines & Flow (`StateFlow`, `SharedFlow`) |
| **Navigation** | Navigation Compose |
| **Security** | Android Keystore & Crypto API |
| **Build System** | Gradle (Kotlin DSL) |

---

## Project Structure

```text
app/src/main/java/com/expenseiq/app/
├── data/
│   ├── local/
│   │   ├── database/       # Room database instance & converters
│   │   └── dao/            # Data access objects (Expense, Budget, User)
│   ├── model/              # Domain models and entities
│   └── repository/         # Data layer implementations
├── security/               # Keystore encryption & password hashing
└── ui/
    ├── components/         # Reusable Compose design tokens
    ├── screens/            # Top-level composables (Dashboard, Analytics, etc.)
    ├── theme/              # Color palettes, typography, and shapes
    ├── util/               # Formatting, currency, and date helpers
    └── viewmodel/          # Screen-level state holders
```

---

## Getting Started

### Prerequisites

- **Android Studio:** Ladybug (2024.2.1) or newer
- **JDK:** Version 17+
- **Android SDK:** API Level 36 (Minimum: API 24)

### Installation

1. Clone the repository:
   ```bash
   git clone [https://github.com/tamzid16/ExpenseIQ-App.git](https://github.com/tamzid16/ExpenseIQ-App.git)
   cd ExpenseIQ-App
   ```

2. Open the project in Android Studio.

3. Allow Gradle to sync dependencies:
   ```bash
   ./gradlew build
   ```

4. Run the application on an emulator or a connected physical device:
   ```bash
   ./gradlew installDebug
   ```

---

## Data Specifications

### CSV Import Schema

Expense records can be batch-imported using standard RFC 4180 CSV files:

```csv
title,amount,category,date,notes
Lunch,12.50,Food,2026-10-01,Office lunch
Metro Pass,35.00,Transportation,2026-10-02,Monthly card
```

### Budget Tracking States

| Utilization | State Indicator | Action / System Response |
| :--- | :--- | :--- |
| `< 80%` | **Normal** | Standard progress indicators |
| `80% – 89%` | **Warning** | Amber visual indicator on dashboard |
| `90% – 99%` | **High Usage** | High-priority notice on transaction entry |
| `≥ 100%` | **Exceeded** | Critical alert highlighting excess amount |

---

## Roadmap

- [ ] Encrypted local backup and restore (JSON/SQLite archive)
- [ ] Automated database migrations for Room schema updates
- [ ] Custom CSV export capabilities
- [ ] Biometric authentication (Fingerprint / Face Unlock)
- [ ] Granular sub-category breakdowns

---

## License

This project is licensed under the MIT License — see the [LICENSE](LICENSE) file for details.
