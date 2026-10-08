package com.expenseiq.app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.expenseiq.app.data.model.Budget
import com.expenseiq.app.data.model.ExpenseCategory
import com.expenseiq.app.data.model.RecurringExpense
import com.expenseiq.app.data.model.User
import com.expenseiq.app.data.repository.AuthRepository
import com.expenseiq.app.data.repository.ExpenseRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

data class AuthUiState(
    val currentUser: User? = null,
    val isLoading: Boolean = true,
    val error: String? = null
)

class AuthViewModel(
    private val authRepository: AuthRepository,
    private val expenseRepository: ExpenseRepository? = null
) : ViewModel() {

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    val currentLanguage: StateFlow<String> = authRepository.currentLanguage

    fun getSavedEmail(): String = authRepository.getSavedEmail()

    init {
        viewModelScope.launch {
            authRepository.initialize()
            authRepository.currentUser.collect { user ->
                _uiState.value = _uiState.value.copy(
                    currentUser = user,
                    isLoading = false
                )
            }
        }
    }

    fun login(email: String, password: String, onSuccess: () -> Unit = {}) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            val result = authRepository.login(email, password)
            result.fold(
                onSuccess = {
                    _uiState.value = _uiState.value.copy(isLoading = false, error = null)
                    onSuccess()
                },
                onFailure = { ex ->
                    _uiState.value = _uiState.value.copy(isLoading = false, error = ex.message)
                }
            )
        }
    }

    fun register(name: String, email: String, password: String, onSuccess: () -> Unit = {}) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            val result = authRepository.register(name, email, password)
            result.fold(
                onSuccess = {
                    _uiState.value = _uiState.value.copy(isLoading = false, error = null)
                    onSuccess()
                },
                onFailure = { ex ->
                    _uiState.value = _uiState.value.copy(isLoading = false, error = ex.message)
                }
            )
        }
    }

    fun completeInitialFinancialSetup(
        monthlyAllowance: Double,
        rent: Double,
        bills: Double,
        otherFixed: Double,
        budgetAmount: Double,
        currencySymbol: String,
        language: String,
        onComplete: () -> Unit
    ) {
        val user = _uiState.value.currentUser ?: return
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)

            // 1. Update user setup state
            authRepository.completeInitialSetup(
                monthlyIncome = monthlyAllowance,
                currencySymbol = currencySymbol,
                language = language
            )

            // 2. Save current month budget & recurring obligations if repo available
            expenseRepository?.let { repo ->
                val today = SimpleDateFormat("yyyy-MM-dd", Locale.US).format(System.currentTimeMillis())
                val calendar = Calendar.getInstance()
                val year = calendar.get(Calendar.YEAR)
                val month = calendar.get(Calendar.MONTH) + 1

                // Set initial budget
                if (budgetAmount > 0) {
                    repo.setBudget(
                        Budget(
                            userId = user.id,
                            year = year,
                            month = month,
                            amount = budgetAmount
                        )
                    )
                }

                // Add Rent recurring obligation if provided
                if (rent > 0) {
                    repo.insertRecurringExpense(
                        RecurringExpense(
                            userId = user.id,
                            title = if (language == "bn") "বাড়ি ভাড়া" else "House Rent",
                            amount = rent,
                            category = ExpenseCategory.BILLS.name,
                            frequency = "MONTHLY",
                            startDate = today,
                            notes = "Monthly rent obligation",
                            isActive = true
                        )
                    )
                }

                // Add Monthly Bills recurring obligation if provided
                if (bills > 0) {
                    repo.insertRecurringExpense(
                        RecurringExpense(
                            userId = user.id,
                            title = if (language == "bn") "মাসিক ইউটিলিটি বিল" else "Utility Bills",
                            amount = bills,
                            category = ExpenseCategory.BILLS.name,
                            frequency = "MONTHLY",
                            startDate = today,
                            notes = "Electricity, Gas, Internet",
                            isActive = true
                        )
                    )
                }

                // Add other fixed recurring obligation if provided
                if (otherFixed > 0) {
                    repo.insertRecurringExpense(
                        RecurringExpense(
                            userId = user.id,
                            title = if (language == "bn") "নির্দিষ্ট খরচ" else "Fixed Expenses",
                            amount = otherFixed,
                            category = ExpenseCategory.OTHER.name,
                            frequency = "MONTHLY",
                            startDate = today,
                            notes = "Other fixed monthly obligations",
                            isActive = true
                        )
                    )
                }
            }

            _uiState.value = _uiState.value.copy(isLoading = false)
            onComplete()
        }
    }

    fun logout() {
        authRepository.logout()
    }

    fun updateCurrency(symbol: String) {
        viewModelScope.launch {
            authRepository.updateCurrencySymbol(symbol)
        }
    }

    fun setLanguage(lang: String) {
        viewModelScope.launch {
            authRepository.setLanguage(lang)
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}
