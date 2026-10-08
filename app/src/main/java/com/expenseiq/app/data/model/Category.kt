package com.expenseiq.app.data.model

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Flight
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector

enum class ExpenseCategory(
    val displayName: String,
    val hexColor: Long
) {
    FOOD("Food", 0xFFE65100),
    TRANSPORTATION("Transportation", 0xFF0277BD),
    SHOPPING("Shopping", 0xFF8E24AA),
    BILLS("Bills", 0xFFD32F2F),
    ENTERTAINMENT("Entertainment", 0xFFF57C00),
    HEALTH("Health", 0xFF2E7D32),
    EDUCATION("Education", 0xFF3949AB),
    TRAVEL("Travel", 0xFF00897B),
    OTHER("Other", 0xFF546E7A);

    val color: Color
        get() = Color(hexColor)

    val icon: ImageVector
        get() = when (this) {
            FOOD -> Icons.Default.Restaurant
            TRANSPORTATION -> Icons.Default.DirectionsCar
            SHOPPING -> Icons.Default.ShoppingBag
            BILLS -> Icons.Default.Receipt
            ENTERTAINMENT -> Icons.Default.Movie
            HEALTH -> Icons.Default.LocalHospital
            EDUCATION -> Icons.Default.School
            TRAVEL -> Icons.Default.Flight
            OTHER -> Icons.Default.Category
        }

    companion object {
        fun fromString(name: String?): ExpenseCategory {
            if (name == null) return OTHER
            val normalized = name.trim().uppercase()
            return entries.firstOrNull { it.name == normalized || it.displayName.equals(name.trim(), ignoreCase = true) }
                ?: OTHER
        }
    }
}
