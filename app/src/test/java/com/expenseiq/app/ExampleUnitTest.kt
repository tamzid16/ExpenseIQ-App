package com.expenseiq.app

import com.expenseiq.app.data.model.Expense
import com.expenseiq.app.data.model.ExpenseCategory
import com.expenseiq.app.data.model.User
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)
    }

    @Test
    fun userModel_defaultsAndCustomValues() {
        val user = User(
            id = 1L,
            name = "Test User",
            email = "test@example.com",
            passwordHash = "hash123",
            currencySymbol = "৳",
            monthlyIncome = 50000.0,
            hasCompletedSetup = true,
            language = "bn"
        )
        assertEquals("৳", user.currencySymbol)
        assertEquals(50000.0, user.monthlyIncome, 0.001)
        assertTrue(user.hasCompletedSetup)
        assertEquals("bn", user.language)
    }

    @Test
    fun expenseModel_propertiesMatch() {
        val expense = Expense(
            id = 10L,
            userId = 1L,
            title = "Groceries",
            amount = 1200.0,
            category = ExpenseCategory.FOOD.name,
            date = "2026-10-08",
            notes = "Weekly groceries"
        )
        assertEquals(10L, expense.id)
        assertEquals("Groceries", expense.title)
        assertEquals(1200.0, expense.amount, 0.001)
        assertEquals("FOOD", expense.category)
    }
}
