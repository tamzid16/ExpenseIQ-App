package com.expenseiq.app.data.model

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

enum class RecurrenceFrequency(val displayName: String) {
    WEEKLY("Weekly"),
    MONTHLY("Monthly"),
    YEARLY("Yearly");

    companion object {
        fun fromString(value: String?): RecurrenceFrequency {
            return entries.firstOrNull { it.name.equals(value, ignoreCase = true) } ?: MONTHLY
        }
    }
}

@Entity(
    tableName = "recurring_expenses",
    indices = [
        Index(value = ["userId"])
    ]
)
data class RecurringExpense(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val userId: Long,
    val title: String,
    val amount: Double,
    val category: String,
    val frequency: String = "MONTHLY",
    val startDate: String, // YYYY-MM-DD
    val notes: String = "",
    val isActive: Boolean = true
)
