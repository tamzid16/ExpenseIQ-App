package com.expenseiq.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.expenseiq.app.data.local.AppDatabase
import com.expenseiq.app.data.repository.AuthRepository
import com.expenseiq.app.data.repository.ExpenseRepository
import com.expenseiq.app.ui.screens.AppThemeMode
import com.expenseiq.app.ui.screens.AuthScreen
import com.expenseiq.app.ui.screens.InitialSetupScreen
import com.expenseiq.app.ui.screens.MainScaffold
import com.expenseiq.app.ui.screens.SplashScreen
import com.expenseiq.app.ui.theme.ExpenseIQTheme
import com.expenseiq.app.ui.viewmodel.AuthViewModel
import com.expenseiq.app.ui.viewmodel.ExpenseViewModel

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val database = AppDatabase.getDatabase(applicationContext)
        val authRepository = AuthRepository(database.userDao(), applicationContext)
        val expenseRepository = ExpenseRepository(
            database.expenseDao(),
            database.budgetDao(),
            database.recurringExpenseDao()
        )

        setContent {
            var themeMode by remember { mutableStateOf(AppThemeMode.SYSTEM) }
            val systemDark = isSystemInDarkTheme()
            val isDark = when (themeMode) {
                AppThemeMode.SYSTEM -> systemDark
                AppThemeMode.LIGHT -> false
                AppThemeMode.DARK -> true
            }

            ExpenseIQTheme(darkTheme = isDark) {
                ExpenseIQApp(
                    authRepository = authRepository,
                    expenseRepository = expenseRepository,
                    currentThemeMode = themeMode,
                    onThemeModeChange = { themeMode = it }
                )
            }
        }
    }
}

@Composable
fun ExpenseIQApp(
    authRepository: AuthRepository,
    expenseRepository: ExpenseRepository,
    currentThemeMode: AppThemeMode,
    onThemeModeChange: (AppThemeMode) -> Unit
) {
    var isSplashVisible by remember { mutableStateOf(true) }
    val authViewModel = remember { AuthViewModel(authRepository, expenseRepository) }
    val authState by authViewModel.uiState.collectAsState()
    val currentUser = authState.currentUser

    if (isSplashVisible) {
        SplashScreen(onSplashFinished = { isSplashVisible = false })
    } else if (currentUser == null) {
        // Not logged in: Show Login page on app launch
        AuthScreen(
            authViewModel = authViewModel,
            onAuthSuccess = { /* Transitions reactively via currentUser */ }
        )
    } else if (!currentUser.hasCompletedSetup) {
        // Logged in new user: Require initial financial setup
        InitialSetupScreen(
            authViewModel = authViewModel,
            onSetupCompleted = { /* Transitions reactively via currentUser.hasCompletedSetup */ }
        )
    } else {
        // Logged in with setup completed: Show Main Scaffold with session persistence
        val expenseViewModel = remember(currentUser.id) {
            ExpenseViewModel(expenseRepository, currentUser.id)
        }

        MainScaffold(
            viewModel = expenseViewModel,
            authViewModel = authViewModel,
            currentUser = currentUser,
            currentThemeMode = currentThemeMode,
            onThemeModeChange = onThemeModeChange,
            onLogout = { authViewModel.logout() }
        )
    }
}
