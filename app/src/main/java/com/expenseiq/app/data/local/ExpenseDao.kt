package com.expenseiq.app.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.expenseiq.app.data.model.Expense
import kotlinx.coroutines.flow.Flow

@Dao
interface ExpenseDao {
    @Query("SELECT * FROM expenses WHERE userId = :userId ORDER BY date DESC, createdAt DESC")
    fun getAllExpenses(userId: Long): Flow<List<Expense>>

    @Query("SELECT * FROM expenses WHERE userId = :userId AND date LIKE :monthPrefix || '%' ORDER BY date DESC, createdAt DESC")
    fun getExpensesForMonth(userId: Long, monthPrefix: String): Flow<List<Expense>>

    @Query("SELECT * FROM expenses WHERE userId = :userId AND date LIKE :yearPrefix || '%' ORDER BY date DESC, createdAt DESC")
    fun getExpensesForYear(userId: Long, yearPrefix: String): Flow<List<Expense>>

    @Query("SELECT * FROM expenses WHERE id = :id LIMIT 1")
    suspend fun getExpenseById(id: Long): Expense?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExpense(expense: Expense): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExpenses(expenses: List<Expense>): List<Long>

    @Update
    suspend fun updateExpense(expense: Expense)

    @Delete
    suspend fun deleteExpense(expense: Expense)

    @Query("DELETE FROM expenses WHERE id = :id")
    suspend fun deleteExpenseById(id: Long)

    @Query("DELETE FROM expenses WHERE userId = :userId")
    suspend fun clearAllExpensesForUser(userId: Long)

    @Query("SELECT * FROM expenses WHERE userId = :userId")
    suspend fun getAllExpensesList(userId: Long): List<Expense>
}
