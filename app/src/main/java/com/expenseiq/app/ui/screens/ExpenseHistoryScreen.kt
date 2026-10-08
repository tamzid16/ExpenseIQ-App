package com.expenseiq.app.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Sort
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.expenseiq.app.data.model.Expense
import com.expenseiq.app.data.model.ExpenseCategory
import com.expenseiq.app.data.model.User
import com.expenseiq.app.ui.components.CategoryIcon
import com.expenseiq.app.ui.components.ConfirmationDialog
import com.expenseiq.app.ui.components.EmptyStateView
import com.expenseiq.app.ui.viewmodel.DateFilter
import com.expenseiq.app.ui.viewmodel.ExpenseViewModel
import com.expenseiq.app.ui.viewmodel.SortOrder
import java.util.Locale

@Composable
fun ExpenseHistoryScreen(
    viewModel: ExpenseViewModel,
    currentUser: User?,
    onAddExpenseClick: () -> Unit,
    onEditExpenseClick: (Expense) -> Unit
) {
    val currency = currentUser?.currencySymbol ?: "$"
    val filteredExpenses by viewModel.filteredExpenses.collectAsState()
    val filterState by viewModel.filterState.collectAsState()

    var expenseToDelete by remember { mutableStateOf<Expense?>(null) }
    var sortMenuExpanded by remember { mutableStateOf(false) }
    var dateMenuExpanded by remember { mutableStateOf(false) }

    val totalAmount = filteredExpenses.sumOf { it.amount }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        Spacer(modifier = Modifier.height(8.dp))

        // Title and count
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Expense History",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "${filteredExpenses.size} items • Total: $currency${String.format(Locale.US, "%.2f", totalAmount)}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Row {
                // Date Filter Menu
                Box {
                    IconButton(
                        onClick = { dateMenuExpanded = true },
                        modifier = Modifier.testTag("date_filter_menu_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.FilterList,
                            contentDescription = "Filter Date",
                            tint = if (filterState.dateFilter != DateFilter.ALL) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    DropdownMenu(
                        expanded = dateMenuExpanded,
                        onDismissRequest = { dateMenuExpanded = false }
                    ) {
                        DateFilter.entries.forEach { df ->
                            DropdownMenuItem(
                                text = { Text(df.label, fontWeight = if (filterState.dateFilter == df) FontWeight.Bold else FontWeight.Normal) },
                                onClick = {
                                    viewModel.updateDateFilter(df)
                                    dateMenuExpanded = false
                                }
                            )
                        }
                    }
                }

                // Sort Order Menu
                Box {
                    IconButton(
                        onClick = { sortMenuExpanded = true },
                        modifier = Modifier.testTag("sort_menu_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Sort,
                            contentDescription = "Sort Expenses",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    DropdownMenu(
                        expanded = sortMenuExpanded,
                        onDismissRequest = { sortMenuExpanded = false }
                    ) {
                        SortOrder.entries.forEach { so ->
                            DropdownMenuItem(
                                text = { Text(so.label, fontWeight = if (filterState.sortOrder == so) FontWeight.Bold else FontWeight.Normal) },
                                onClick = {
                                    viewModel.updateSortOrder(so)
                                    sortMenuExpanded = false
                                }
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Search Bar
        OutlinedTextField(
            value = filterState.searchQuery,
            onValueChange = { viewModel.updateSearchQuery(it) },
            placeholder = { Text("Search by title, notes, or category...") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
            trailingIcon = {
                if (filterState.searchQuery.isNotEmpty()) {
                    IconButton(onClick = { viewModel.updateSearchQuery("") }) {
                        Icon(Icons.Default.Clear, contentDescription = "Clear search")
                    }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .testTag("history_search_input"),
            singleLine = true,
            shape = RoundedCornerShape(12.dp)
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Category Filter Chips
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            FilterChip(
                selected = filterState.category == null,
                onClick = { viewModel.updateCategoryFilter(null) },
                label = { Text("All") },
                modifier = Modifier.testTag("filter_category_all")
            )

            ExpenseCategory.entries.forEach { cat ->
                val isSelected = filterState.category == cat
                FilterChip(
                    selected = isSelected,
                    onClick = {
                        if (isSelected) viewModel.updateCategoryFilter(null)
                        else viewModel.updateCategoryFilter(cat)
                    },
                    label = { Text(cat.displayName) },
                    leadingIcon = {
                        Icon(
                            imageVector = cat.icon,
                            contentDescription = null,
                            tint = if (isSelected) MaterialTheme.colorScheme.primary else cat.color,
                            modifier = Modifier.size(16.dp)
                        )
                    },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = cat.color.copy(alpha = 0.2f),
                        selectedLabelColor = cat.color
                    ),
                    modifier = Modifier.testTag("filter_category_${cat.name.lowercase()}")
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Expense List
        if (filteredExpenses.isEmpty()) {
            EmptyStateView(
                icon = Icons.Default.Search,
                title = "No Matching Expenses",
                message = "Try clearing your search or category filters.",
                actionText = "Clear Filters",
                onActionClick = {
                    viewModel.updateSearchQuery("")
                    viewModel.updateCategoryFilter(null)
                    viewModel.updateDateFilter(DateFilter.ALL)
                }
            )
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(filteredExpenses, key = { it.id }) { expense ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onEditExpenseClick(expense) }
                            .testTag("history_expense_item_${expense.id}"),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            CategoryIcon(categoryName = expense.category)

                            Spacer(modifier = Modifier.width(12.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = expense.title,
                                    style = MaterialTheme.typography.bodyLarge,
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "${expense.category} • ${expense.date}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                if (expense.notes.isNotBlank()) {
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = expense.notes,
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f),
                                        maxLines = 1
                                    )
                                }
                            }

                            Column(horizontalAlignment = Alignment.End) {
                                Text(
                                    text = "$currency${String.format(Locale.US, "%.2f", expense.amount)}",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )

                                Row {
                                    IconButton(
                                        onClick = { onEditExpenseClick(expense) },
                                        modifier = Modifier.size(32.dp).testTag("edit_expense_${expense.id}")
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Edit,
                                            contentDescription = "Edit",
                                            tint = MaterialTheme.colorScheme.primary,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                    IconButton(
                                        onClick = { expenseToDelete = expense },
                                        modifier = Modifier.size(32.dp).testTag("delete_expense_${expense.id}")
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Delete,
                                            contentDescription = "Delete",
                                            tint = MaterialTheme.colorScheme.error,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(80.dp))
                }
            }
        }
    }

    // Confirmation dialog before deleting an expense
    if (expenseToDelete != null) {
        ConfirmationDialog(
            title = "Delete Expense?",
            message = "Are you sure you want to delete '${expenseToDelete!!.title}' of $currency${String.format(Locale.US, "%.2f", expenseToDelete!!.amount)}? This action cannot be undone.",
            confirmText = "Delete",
            dismissText = "Cancel",
            isDestructive = true,
            onConfirm = {
                viewModel.deleteExpense(expenseToDelete!!.id)
                expenseToDelete = null
            },
            onDismiss = {
                expenseToDelete = null
            }
        )
    }
}
