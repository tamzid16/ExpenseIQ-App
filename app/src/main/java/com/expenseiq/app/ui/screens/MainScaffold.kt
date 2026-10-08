package com.expenseiq.app.ui.screens

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.outlined.AccountBalanceWallet
import androidx.compose.material.icons.outlined.BarChart
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Receipt
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.expenseiq.app.data.model.Expense
import com.expenseiq.app.data.model.User
import com.expenseiq.app.ui.util.Localization
import com.expenseiq.app.ui.viewmodel.AuthViewModel
import com.expenseiq.app.ui.viewmodel.ExpenseViewModel

enum class NavigationTab(
    val key: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val testTag: String
) {
    HOME("nav_home", Icons.Filled.Home, Icons.Outlined.Home, "nav_home"),
    EXPENSES("nav_expenses", Icons.Filled.Receipt, Icons.Outlined.Receipt, "nav_expenses"),
    ANALYTICS("nav_analytics", Icons.Filled.BarChart, Icons.Outlined.BarChart, "nav_analytics"),
    BUDGET("nav_budget", Icons.Filled.AccountBalanceWallet, Icons.Outlined.AccountBalanceWallet, "nav_budget")
}

@Composable
fun MainScaffold(
    viewModel: ExpenseViewModel,
    authViewModel: AuthViewModel,
    currentUser: User?,
    currentThemeMode: AppThemeMode,
    onThemeModeChange: (AppThemeMode) -> Unit,
    onLogout: () -> Unit
) {
    val lang = currentUser?.language ?: "en"
    val currency = currentUser?.currencySymbol ?: "৳"

    var selectedTab by remember { mutableIntStateOf(0) }

    // Dialog state holders
    var showAddExpenseDialog by remember { mutableStateOf(false) }
    var expenseToEdit by remember { mutableStateOf<Expense?>(null) }
    var showSettingsDialog by remember { mutableStateOf(false) }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface,
                tonalElevation = 8.dp
            ) {
                NavigationTab.entries.forEachIndexed { index, tab ->
                    NavigationBarItem(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        icon = {
                            Icon(
                                imageVector = if (selectedTab == index) tab.selectedIcon else tab.unselectedIcon,
                                contentDescription = Localization.get(tab.key, lang)
                            )
                        },
                        label = { Text(Localization.get(tab.key, lang)) },
                        modifier = Modifier.testTag(tab.testTag)
                    )
                }
            }
        },
        floatingActionButton = {
            if (selectedTab != 3) {
                FloatingActionButton(
                    onClick = { showAddExpenseDialog = true },
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.testTag("main_fab_add_expense")
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Add Expense")
                }
            }
        }
    ) { innerPadding ->
        androidx.compose.foundation.layout.Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (selectedTab) {
                0 -> DashboardScreen(
                    viewModel = viewModel,
                    currentUser = currentUser,
                    onAddExpenseClick = { showAddExpenseDialog = true },
                    onEditExpenseClick = { expenseToEdit = it },
                    onNavigateToHistory = { selectedTab = 1 },
                    onOpenSettings = { showSettingsDialog = true }
                )
                1 -> ExpenseHistoryScreen(
                    viewModel = viewModel,
                    currentUser = currentUser,
                    onAddExpenseClick = { showAddExpenseDialog = true },
                    onEditExpenseClick = { expenseToEdit = it }
                )
                2 -> AnalyticsScreen(
                    viewModel = viewModel,
                    currentUser = currentUser
                )
                3 -> BudgetScreen(
                    viewModel = viewModel,
                    currentUser = currentUser
                )
            }
        }
    }

    // Add Expense Dialog
    if (showAddExpenseDialog) {
        AddEditExpenseDialog(
            currencySymbol = currency,
            onDismiss = { showAddExpenseDialog = false },
            onSave = { title, amount, category, date, notes, isRecurring, recurrenceFreq ->
                viewModel.addExpense(
                    title = title,
                    amount = amount,
                    category = category,
                    date = date,
                    notes = notes,
                    isRecurring = isRecurring,
                    recurrenceFreq = recurrenceFreq
                )
                showAddExpenseDialog = false
            }
        )
    }

    // Edit Expense Dialog
    if (expenseToEdit != null) {
        AddEditExpenseDialog(
            initialExpense = expenseToEdit,
            currencySymbol = currency,
            onDismiss = { expenseToEdit = null },
            onSave = { title, amount, category, date, notes, isRecurring, recurrenceFreq ->
                viewModel.updateExpense(
                    id = expenseToEdit!!.id,
                    title = title,
                    amount = amount,
                    category = category,
                    date = date,
                    notes = notes,
                    isRecurring = isRecurring,
                    recurrenceFreq = recurrenceFreq
                )
                expenseToEdit = null
            },
            onDelete = { id ->
                viewModel.deleteExpense(id)
                expenseToEdit = null
            }
        )
    }

    // Settings Dialog
    if (showSettingsDialog) {
        SettingsDialog(
            currentUser = currentUser,
            authViewModel = authViewModel,
            currentThemeMode = currentThemeMode,
            onThemeModeChange = onThemeModeChange,
            onDismiss = { showSettingsDialog = false },
            onLogout = onLogout
        )
    }
}
