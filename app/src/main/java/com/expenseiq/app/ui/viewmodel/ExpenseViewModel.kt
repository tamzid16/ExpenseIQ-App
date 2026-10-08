package com.expenseiq.app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.expenseiq.app.data.model.Budget
import com.expenseiq.app.data.model.Expense
import com.expenseiq.app.data.model.ExpenseCategory
import com.expenseiq.app.data.model.RecurringExpense
import com.expenseiq.app.data.repository.ExpenseRepository
import com.expenseiq.app.ui.components.BarChartItem
import com.expenseiq.app.ui.components.CategoryShare
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

enum class SortOrder(val label: String) {
    NEWEST("Newest"),
    OLDEST("Oldest"),
    HIGHEST_AMOUNT("Highest Amount"),
    LOWEST_AMOUNT("Lowest Amount")
}

enum class DateFilter(val label: String) {
    ALL("All Time"),
    THIS_MONTH("This Month"),
    LAST_30_DAYS("Last 30 Days")
}

data class ExpenseFilterState(
    val searchQuery: String = "",
    val category: ExpenseCategory? = null,
    val dateFilter: DateFilter = DateFilter.ALL,
    val sortOrder: SortOrder = SortOrder.NEWEST
)

@OptIn(ExperimentalCoroutinesApi::class)
class ExpenseViewModel(
    private val repository: ExpenseRepository,
    private val userId: Long
) : ViewModel() {

    // Start from the device's current month.
    private val initialCalendar = Calendar.getInstance()
    val selectedYear = MutableStateFlow(initialCalendar.get(Calendar.YEAR))
    val selectedMonth = MutableStateFlow(initialCalendar.get(Calendar.MONTH) + 1) // 1..12

    private val _filterState = MutableStateFlow(ExpenseFilterState())
    val filterState: StateFlow<ExpenseFilterState> = _filterState.asStateFlow()

    val allExpenses: StateFlow<List<Expense>> = repository.getAllExpenses(userId)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Dynamically observe budget for whichever month is currently selected
    val currentBudget: StateFlow<Budget?> = combine(selectedYear, selectedMonth) { y, m ->
        Pair(y, m)
    }.flatMapLatest { (y, m) ->
        repository.getBudget(userId, y, m)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val recurringExpenses: StateFlow<List<RecurringExpense>> = repository.getRecurringExpenses(userId)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun previousMonth() {
        val currentM = selectedMonth.value
        val currentY = selectedYear.value
        if (currentM == 1) {
            selectedMonth.value = 12
            selectedYear.value = currentY - 1
        } else {
            selectedMonth.value = currentM - 1
        }
    }

    fun nextMonth() {
        val currentM = selectedMonth.value
        val currentY = selectedYear.value
        if (currentM == 12) {
            selectedMonth.value = 1
            selectedYear.value = currentY + 1
        } else {
            selectedMonth.value = currentM + 1
        }
    }

    // Filtered and Sorted Expenses for History screen
    val filteredExpenses: StateFlow<List<Expense>> = combine(
        allExpenses,
        _filterState,
        selectedYear,
        selectedMonth
    ) { expenses, filter, year, month ->
        var list = expenses

        // 1. Text Search
        if (filter.searchQuery.isNotBlank()) {
            val q = filter.searchQuery.trim().lowercase()
            list = list.filter {
                it.title.lowercase().contains(q) ||
                it.notes.lowercase().contains(q) ||
                it.category.lowercase().contains(q)
            }
        }

        // 2. Category Filter
        if (filter.category != null) {
            list = list.filter {
                it.category.equals(filter.category.name, ignoreCase = true)
            }
        }

        // 3. Date Filter
        when (filter.dateFilter) {
            DateFilter.THIS_MONTH -> {
                val prefix = String.format(Locale.US, "%04d-%02d", year, month)
                list = list.filter { it.date.startsWith(prefix) }
            }
            DateFilter.LAST_30_DAYS -> {
                val thirtyDaysAgo = Calendar.getInstance().apply {
                    add(Calendar.DAY_OF_YEAR, -30)
                }.time
                val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.US)
                val cutoff = sdf.format(thirtyDaysAgo)
                list = list.filter { it.date >= cutoff }
            }
            DateFilter.ALL -> { /* no filter */ }
        }

        // 4. Sort
        when (filter.sortOrder) {
            SortOrder.NEWEST -> list.sortedWith(compareByDescending<Expense> { it.date }.thenByDescending { it.createdAt })
            SortOrder.OLDEST -> list.sortedWith(compareBy<Expense> { it.date }.thenBy { it.createdAt })
            SortOrder.HIGHEST_AMOUNT -> list.sortedByDescending { it.amount }
            SortOrder.LOWEST_AMOUNT -> list.sortedBy { it.amount }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Calculations for Dashboard
    val currentMonthTotal: StateFlow<Double> = combine(
        allExpenses,
        selectedYear,
        selectedMonth
    ) { expenses, year, month ->
        val prefix = String.format(Locale.US, "%04d-%02d", year, month)
        expenses.filter { it.date.startsWith(prefix) }.sumOf { it.amount }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    val todaySpending: StateFlow<Double> = allExpenses.combine(selectedYear) { expenses, _ ->
        val todayStr = SimpleDateFormat("yyyy-MM-dd", Locale.US).format(System.currentTimeMillis())
        expenses.filter { it.date == todayStr }.sumOf { it.amount }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    // Chart 1: Month-over-month chart data
    val monthlyChartData: StateFlow<List<BarChartItem>> = combine(allExpenses, selectedYear, selectedMonth) { expenses, year, activeMonth ->
        val calendar = Calendar.getInstance().apply {
            set(Calendar.YEAR, year)
            set(Calendar.MONTH, activeMonth - 1)
            set(Calendar.DAY_OF_MONTH, 1)
            add(Calendar.MONTH, -5)
        }
        val labelFormat = SimpleDateFormat("MMM", Locale.US)

        (0..5).map {
            val chartYear = calendar.get(Calendar.YEAR)
            val chartMonth = calendar.get(Calendar.MONTH) + 1
            val prefix = String.format(Locale.US, "%04d-%02d", chartYear, chartMonth)
            val total = expenses.filter { it.date.startsWith(prefix) }.sumOf { it.amount }
            val isHighlighted = chartYear == year && chartMonth == activeMonth
            val item = BarChartItem(
                label = labelFormat.format(calendar.time),
                value = total,
                isHighlighted = isHighlighted
            )
            calendar.add(Calendar.MONTH, 1)
            item
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Chart 2: Category Breakdown
    val categoryShares: StateFlow<List<CategoryShare>> = combine(
        allExpenses,
        selectedYear,
        selectedMonth
    ) { expenses, year, month ->
        val prefix = String.format(Locale.US, "%04d-%02d", year, month)
        val monthExpenses = expenses.filter { it.date.startsWith(prefix) }
        val total = monthExpenses.sumOf { it.amount }

        if (total <= 0.0) emptyList()
        else {
            val map = mutableMapOf<ExpenseCategory, Double>()
            monthExpenses.forEach { exp ->
                val cat = ExpenseCategory.fromString(exp.category)
                map[cat] = (map[cat] ?: 0.0) + exp.amount
            }
            map.entries.map { (cat, amount) ->
                val pct = ((amount / total) * 100).toInt()
                CategoryShare(cat, amount, pct)
            }.sortedByDescending { it.amount }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Chart 3: Weekly Spending Distribution of Current Month
    val weeklyChartData: StateFlow<List<BarChartItem>> = combine(
        allExpenses,
        selectedYear,
        selectedMonth
    ) { expenses, year, month ->
        val prefix = String.format(Locale.US, "%04d-%02d", year, month)
        val monthExpenses = expenses.filter { it.date.startsWith(prefix) }

        var w1 = 0.0
        var w2 = 0.0
        var w3 = 0.0
        var w4 = 0.0
        var w5 = 0.0

        for (exp in monthExpenses) {
            val day = exp.date.substringAfterLast("-").toIntOrNull() ?: 1
            when {
                day in 1..7 -> w1 += exp.amount
                day in 8..14 -> w2 += exp.amount
                day in 15..21 -> w3 += exp.amount
                day in 22..28 -> w4 += exp.amount
                else -> w5 += exp.amount
            }
        }

        listOf(
            BarChartItem("W1 (1-7)", w1),
            BarChartItem("W2 (8-14)", w2),
            BarChartItem("W3 (15-21)", w3),
            BarChartItem("W4 (22-28)", w4),
            BarChartItem("W5 (29+)", w5)
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun updateSearchQuery(query: String) {
        _filterState.value = _filterState.value.copy(searchQuery = query)
    }

    fun updateCategoryFilter(cat: ExpenseCategory?) {
        _filterState.value = _filterState.value.copy(category = cat)
    }

    fun updateDateFilter(dateFilter: DateFilter) {
        _filterState.value = _filterState.value.copy(dateFilter = dateFilter)
    }

    fun updateSortOrder(order: SortOrder) {
        _filterState.value = _filterState.value.copy(sortOrder = order)
    }

    fun addExpense(
        title: String,
        amount: Double,
        category: ExpenseCategory,
        date: String,
        notes: String,
        isRecurring: Boolean = false,
        recurrenceFreq: String = "MONTHLY"
    ) {
        viewModelScope.launch {
            val expense = Expense(
                userId = userId,
                title = title.trim(),
                amount = amount,
                category = category.name,
                date = date,
                notes = notes.trim(),
                isRecurring = isRecurring,
                recurrenceFrequency = recurrenceFreq
            )
            repository.insertExpense(expense)
        }
    }

    fun updateExpense(
        id: Long,
        title: String,
        amount: Double,
        category: ExpenseCategory,
        date: String,
        notes: String,
        isRecurring: Boolean,
        recurrenceFreq: String
    ) {
        viewModelScope.launch {
            val expense = Expense(
                id = id,
                userId = userId,
                title = title.trim(),
                amount = amount,
                category = category.name,
                date = date,
                notes = notes.trim(),
                isRecurring = isRecurring,
                recurrenceFrequency = recurrenceFreq
            )
            repository.updateExpense(expense)
        }
    }

    fun deleteExpense(id: Long) {
        viewModelScope.launch {
            repository.deleteExpense(id)
        }
    }

    fun setBudget(amount: Double) {
        viewModelScope.launch {
            repository.setBudget(
                Budget(
                    userId = userId,
                    year = selectedYear.value,
                    month = selectedMonth.value,
                    amount = amount
                )
            )
        }
    }

    fun addRecurringExpense(
        title: String,
        amount: Double,
        category: ExpenseCategory,
        frequency: String,
        startDate: String,
        notes: String
    ) {
        viewModelScope.launch {
            val item = RecurringExpense(
                userId = userId,
                title = title.trim(),
                amount = amount,
                category = category.name,
                frequency = frequency,
                startDate = startDate,
                notes = notes.trim(),
                isActive = true
            )
            repository.insertRecurringExpense(item)
        }
    }

    fun deleteRecurringExpense(id: Long) {
        viewModelScope.launch {
            repository.deleteRecurringExpense(id)
        }
    }
}
