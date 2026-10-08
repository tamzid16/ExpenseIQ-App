package com.expenseiq.app.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.expenseiq.app.data.model.Budget
import kotlinx.coroutines.flow.Flow

@Dao
interface BudgetDao {
    @Query("SELECT * FROM budgets WHERE userId = :userId AND year = :year AND month = :month LIMIT 1")
    fun getBudget(userId: Long, year: Int, month: Int): Flow<Budget?>

    @Query("SELECT * FROM budgets WHERE userId = :userId AND year = :year AND month = :month LIMIT 1")
    suspend fun getBudgetSync(userId: Long, year: Int, month: Int): Budget?

    @Query("SELECT * FROM budgets WHERE userId = :userId AND year = :year ORDER BY month ASC")
    fun getBudgetsForYear(userId: Long, year: Int): Flow<List<Budget>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateBudget(budget: Budget): Long

    @Query("DELETE FROM budgets WHERE id = :id")
    suspend fun deleteBudgetById(id: Long)
}
