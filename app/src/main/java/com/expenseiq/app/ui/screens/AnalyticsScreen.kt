package com.expenseiq.app.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.ShowChart
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.expenseiq.app.data.model.User
import com.expenseiq.app.ui.components.CategoryDonutChart
import com.expenseiq.app.ui.components.MetricCard
import com.expenseiq.app.ui.components.MonthlyBarChart
import com.expenseiq.app.ui.util.Localization
import com.expenseiq.app.ui.viewmodel.ExpenseViewModel
import java.util.Locale

@Composable
fun AnalyticsScreen(
    viewModel: ExpenseViewModel,
    currentUser: User?
) {
    val lang = currentUser?.language ?: "en"
    val currency = currentUser?.currencySymbol ?: "৳"
    val allExpenses by viewModel.allExpenses.collectAsState()
    val monthlyChartData by viewModel.monthlyChartData.collectAsState()
    val categoryShares by viewModel.categoryShares.collectAsState()
    val weeklyChartData by viewModel.weeklyChartData.collectAsState()
    val currentMonthTotal by viewModel.currentMonthTotal.collectAsState()
    val selectedMonth by viewModel.selectedMonth.collectAsState()
    val selectedYear by viewModel.selectedYear.collectAsState()

    val monthName = Localization.getMonthName(selectedMonth, lang)

    // Metric calculations across months
    val totalAllTimeSpending = allExpenses.sumOf { it.amount }

    // Average monthly spending (based on unique months recorded)
    val monthsSet = allExpenses.mapNotNull {
        if (it.date.length >= 7) it.date.substring(0, 7) else null
    }.toSet()
    val avgMonthly = if (monthsSet.isNotEmpty()) totalAllTimeSpending / monthsSet.size else totalAllTimeSpending

    // Highest spending category in current month
    val topCategory = categoryShares.maxByOrNull { it.amount }

    // Highest spending month
    val highestMonth = monthlyChartData.maxByOrNull { it.value }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(8.dp))
            Column {
                Text(
                    text = Localization.get("spending_analytics", lang),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = Localization.get("analytics_subtitle", lang),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // Metrics 2x2 Grid
        item {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    MetricCard(
                        title = Localization.get("total_spending", lang),
                        value = "$currency${String.format(Locale.US, "%.2f", totalAllTimeSpending)}",
                        subtitle = "${allExpenses.size} ${if (lang == "bn") "টি রেকর্ড" else "total entries"}",
                        icon = Icons.Default.ShowChart,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("metric_total_spending")
                    )

                    MetricCard(
                        title = Localization.get("avg_monthly", lang),
                        value = "$currency${String.format(Locale.US, "%.2f", avgMonthly)}",
                        subtitle = "${monthsSet.size.coerceAtLeast(1)} ${if (lang == "bn") "মাসের ভিত্তিতে" else "months tracked"}",
                        icon = Icons.Default.TrendingUp,
                        iconTint = MaterialTheme.colorScheme.secondary,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("metric_avg_monthly")
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    MetricCard(
                        title = Localization.get("top_category", lang),
                        value = topCategory?.category?.displayName ?: (if (lang == "bn") "নেই" else "None"),
                        subtitle = if (topCategory != null) "$currency${String.format(Locale.US, "%.2f", topCategory.amount)} (${topCategory.percentage}%)" else "$currency 0.00",
                        icon = Icons.Default.Category,
                        iconTint = topCategory?.category?.color ?: MaterialTheme.colorScheme.tertiary,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("metric_top_category")
                    )

                    MetricCard(
                        title = Localization.get("highest_month", lang),
                        value = highestMonth?.label ?: monthName,
                        subtitle = if (highestMonth != null) "$currency${String.format(Locale.US, "%.2f", highestMonth.value)}" else "$currency 0.00",
                        icon = Icons.Default.CalendarMonth,
                        iconTint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("metric_highest_month")
                    )
                }
            }
        }

        // Chart 1: Monthly Spending Bar Chart Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("monthly_spending_chart_card"),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text(
                        text = Localization.get("monthly_trend", lang),
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    MonthlyBarChart(
                        items = monthlyChartData,
                        currencySymbol = currency
                    )
                }
            }
        }

        // Chart 2: Category-wise Spending Donut Chart Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("category_spending_chart_card"),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text(
                        text = "${Localization.get("category_breakdown", lang)} ($monthName $selectedYear)",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    CategoryDonutChart(
                        shares = categoryShares,
                        totalAmount = currentMonthTotal,
                        currencySymbol = currency
                    )
                }
            }
        }

        // Chart 3: Weekly Spending Distribution of Current Month
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("weekly_spending_chart_card"),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text(
                        text = if (lang == "bn") "সাপ্তাহিক ব্যয়ের বিভাজন ($monthName $selectedYear)" else "WEEKLY DISTRIBUTION ($monthName $selectedYear)",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    MonthlyBarChart(
                        items = weeklyChartData,
                        currencySymbol = currency
                    )
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}
