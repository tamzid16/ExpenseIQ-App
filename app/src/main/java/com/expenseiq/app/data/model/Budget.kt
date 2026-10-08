package com.expenseiq.app.data.model

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "budgets",
    indices = [
        Index(value = ["userId", "year", "month"], unique = true)
    ]
)
data class Budget(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val userId: Long,
    val year: Int,
    val month: Int, // 1 to 12
    val amount: Double,
    val updatedAt: Long = System.currentTimeMillis()
)
