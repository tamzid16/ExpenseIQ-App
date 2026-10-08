package com.expenseiq.app.data.model

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "expenses",
    indices = [
        Index(value = ["userId"]),
        Index(value = ["date"]),
        Index(value = ["category"])
    ]
)
data class Expense(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val userId: Long,
    val title: String,
    val amount: Double,
    val category: String, // from ExpenseCategory
    val date: String,     // ISO format YYYY-MM-DD (e.g. 2026-01-01)
    val notes: String = "",
    val isRecurring: Boolean = false,
    val recurrenceFrequency: String = "MONTHLY", // WEEKLY, MONTHLY, YEARLY
    val createdAt: Long = System.currentTimeMillis()
)
