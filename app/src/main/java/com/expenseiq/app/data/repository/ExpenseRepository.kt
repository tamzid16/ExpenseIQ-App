package com.expenseiq.app.data.repository

import com.expenseiq.app.data.local.BudgetDao
import com.expenseiq.app.data.local.ExpenseDao
import com.expenseiq.app.data.local.RecurringExpenseDao
import com.expenseiq.app.data.model.Budget
import com.expenseiq.app.data.model.Expense
import com.expenseiq.app.data.model.RecurringExpense
import kotlinx.coroutines.flow.Flow
import java.util.Locale

class ExpenseRepository(
    private val expenseDao: ExpenseDao,
    private val budgetDao: BudgetDao,
    private val recurringDao: RecurringExpenseDao
) {
    fun getAllExpenses(userId: Long): Flow<List<Expense>> {
        return expenseDao.getAllExpenses(userId)
    }

    fun getExpensesForMonth(userId: Long, year: Int, month: Int): Flow<List<Expense>> {
        val monthPrefix = String.format(Locale.US, "%04d-%02d", year, month)
        return expenseDao.getExpensesForMonth(userId, monthPrefix)
    }

    fun getExpensesForYear(userId: Long, year: Int): Flow<List<Expense>> {
        val yearPrefix = String.format(Locale.US, "%04d", year)
        return expenseDao.getExpensesForYear(userId, yearPrefix)
    }

    suspend fun getExpenseById(id: Long): Expense? {
        return expenseDao.getExpenseById(id)
    }

    suspend fun insertExpense(expense: Expense): Long {
        return expenseDao.insertExpense(expense)
    }

    suspend fun insertExpenses(expenses: List<Expense>): List<Long> {
        return expenseDao.insertExpenses(expenses)
    }

    suspend fun updateExpense(expense: Expense) {
        expenseDao.updateExpense(expense)
    }

    suspend fun deleteExpense(id: Long) {
        expenseDao.deleteExpenseById(id)
    }

    suspend fun clearExpenses(userId: Long) {
        expenseDao.clearAllExpensesForUser(userId)
    }

    suspend fun getAllExpensesList(userId: Long): List<Expense> {
        return expenseDao.getAllExpensesList(userId)
    }

    fun getBudget(userId: Long, year: Int, month: Int): Flow<Budget?> {
        return budgetDao.getBudget(userId, year, month)
    }

    suspend fun setBudget(budget: Budget): Long {
        val existing = budgetDao.getBudgetSync(budget.userId, budget.year, budget.month)
        val toSave = if (existing != null) budget.copy(id = existing.id) else budget
        return budgetDao.insertOrUpdateBudget(toSave)
    }

    fun getBudgetsForYear(userId: Long, year: Int): Flow<List<Budget>> {
        return budgetDao.getBudgetsForYear(userId, year)
    }

    fun getRecurringExpenses(userId: Long): Flow<List<RecurringExpense>> {
        return recurringDao.getRecurringExpenses(userId)
    }

    suspend fun insertRecurringExpense(recurring: RecurringExpense): Long {
        return recurringDao.insertRecurringExpense(recurring)
    }

    suspend fun updateRecurringExpense(recurring: RecurringExpense) {
        recurringDao.updateRecurringExpense(recurring)
    }

    suspend fun deleteRecurringExpense(id: Long) {
        recurringDao.deleteRecurringExpenseById(id)
    }
}
