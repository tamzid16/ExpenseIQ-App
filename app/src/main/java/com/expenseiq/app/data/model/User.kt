package com.expenseiq.app.data.model

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "users",
    indices = [Index(value = ["email"], unique = true)]
)
data class User(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val email: String,
    val passwordHash: String,
    val currencySymbol: String = "৳",
    val monthlyIncome: Double = 0.0,
    val hasCompletedSetup: Boolean = false,
    val language: String = "en",
    val createdAt: Long = System.currentTimeMillis()
)
