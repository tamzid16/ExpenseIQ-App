package com.expenseiq.app.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.expenseiq.app.data.model.ExpenseCategory
import com.expenseiq.app.data.model.RecurrenceFrequency
import com.expenseiq.app.data.model.RecurringExpense
import com.expenseiq.app.data.model.User
import com.expenseiq.app.ui.components.BudgetProgressBar
import com.expenseiq.app.ui.components.CategoryIcon
import com.expenseiq.app.ui.components.ConfirmationDialog
import com.expenseiq.app.ui.util.Localization
import com.expenseiq.app.ui.viewmodel.ExpenseViewModel
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BudgetScreen(
    viewModel: ExpenseViewModel,
    currentUser: User?
) {
    val lang = currentUser?.language ?: "en"
    val currency = currentUser?.currencySymbol ?: "৳"

    val selectedYear by viewModel.selectedYear.collectAsState()
    val selectedMonth by viewModel.selectedMonth.collectAsState()

    val currentMonthSpent by viewModel.currentMonthTotal.collectAsState()
    val currentBudget by viewModel.currentBudget.collectAsState()
    val recurringExpenses by viewModel.recurringExpenses.collectAsState()

    var showEditBudgetDialog by remember { mutableStateOf(false) }
    var showAddRecurringDialog by remember { mutableStateOf(false) }
    var recurringToDelete by remember { mutableStateOf<RecurringExpense?>(null) }

    val budgetAmount = currentBudget?.amount ?: if (currentUser?.monthlyIncome != null && currentUser.monthlyIncome > 0) currentUser.monthlyIncome else 1200.0
    val monthName = Localization.getMonthName(selectedMonth, lang)
    val periodLabel = "$monthName $selectedYear"

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = Localization.get("budget_and_recurring", lang),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "$periodLabel ${Localization.get("budget", lang)}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                OutlinedButton(
                    onClick = { showEditBudgetDialog = true },
                    modifier = Modifier.testTag("edit_budget_button"),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(Localization.get("set_budget", lang))
                }
            }
        }

        // Month Selector Bar (Allows navigating between separate months)
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 6.dp, vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = { viewModel.previousMonth() },
                        modifier = Modifier.testTag("budget_prev_month")
                    ) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Previous Month")
                    }

                    Text(
                        text = periodLabel,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    IconButton(
                        onClick = { viewModel.nextMonth() },
                        modifier = Modifier.testTag("budget_next_month")
                    ) {
                        Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = "Next Month")
                    }
                }
            }
        }

        // Monthly Budget Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("budget_management_card"),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = Localization.get("budget_status", lang),
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "$currency${String.format(Locale.US, "%.2f", budgetAmount)}",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    BudgetProgressBar(
                        spent = currentMonthSpent,
                        budget = budgetAmount,
                        currencySymbol = currency
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                text = Localization.get("budget", lang),
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "$currency${String.format(Locale.US, "%.2f", budgetAmount)}",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Column {
                            Text(
                                text = Localization.get("spent", lang),
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "$currency${String.format(Locale.US, "%.2f", currentMonthSpent)}",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Column {
                            Text(
                                text = Localization.get("remaining", lang),
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            val rem = (budgetAmount - currentMonthSpent).coerceAtLeast(0.0)
                            Text(
                                text = "$currency${String.format(Locale.US, "%.2f", rem)}",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = if (currentMonthSpent > budgetAmount) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
            }
        }

        // Recurring Expenses Header
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Repeat,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = Localization.get("recurring_expenses", lang),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                Button(
                    onClick = { showAddRecurringDialog = true },
                    modifier = Modifier.testTag("add_recurring_expense_button"),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(Localization.get("add_recurring", lang))
                }
            }
        }

        if (recurringExpenses.isEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = if (lang == "bn") "কোনো নির্দিষ্ট খরচ নেই" else "No Recurring Expenses",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = if (lang == "bn") "নিয়মিত খরচ যেমন বাসা ভাড়া বা বিল যুক্ত করুন।" else "Add regular commitments like rent, internet, or memberships.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        } else {
            items(recurringExpenses, key = { it.id }) { item ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("recurring_item_${item.id}"),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        CategoryIcon(categoryName = item.category)

                        Spacer(modifier = Modifier.width(12.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = item.title,
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(MaterialTheme.colorScheme.primaryContainer)
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = item.frequency,
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                                if (item.notes.isNotBlank()) {
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = item.notes,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        maxLines = 1
                                    )
                                }
                            }
                        }

                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = "$currency${String.format(Locale.US, "%.2f", item.amount)}",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )

                            IconButton(
                                onClick = { recurringToDelete = item },
                                modifier = Modifier
                                    .size(32.dp)
                                    .testTag("delete_recurring_${item.id}")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = "Delete",
                                    tint = MaterialTheme.colorScheme.error,
                                    modifier = Modifier.size(18.dp)
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

    // Edit Budget Dialog
    if (showEditBudgetDialog) {
        var newBudgetInput by remember {
            mutableStateOf(String.format(Locale.US, "%.2f", budgetAmount))
        }
        var budgetError by remember { mutableStateOf<String?>(null) }

        AlertDialog(
            onDismissRequest = { showEditBudgetDialog = false },
            title = { Text("$periodLabel ${Localization.get("budget", lang)}") },
            text = {
                Column {
                    Text(
                        text = if (lang == "bn") "এই মাসের ব্যয়ের জন্য বাজেট সীমা নির্ধারণ করুন।" else "Set spending limit for this month.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    OutlinedTextField(
                        value = newBudgetInput,
                        onValueChange = {
                            newBudgetInput = it
                            budgetError = null
                        },
                        label = { Text("${Localization.get("budget", lang)} ($currency)") },
                        leadingIcon = { Icon(Icons.Default.AttachMoney, contentDescription = null) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("budget_amount_input"),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
                    )
                    if (budgetError != null) {
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = budgetError ?: "",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val amount = newBudgetInput.toDoubleOrNull()
                        if (amount == null || amount <= 0) {
                            budgetError = if (lang == "bn") "সঠিক সংখ্যা দিন।" else "Please enter a valid amount."
                            return@Button
                        }
                        viewModel.setBudget(amount)
                        showEditBudgetDialog = false
                    },
                    modifier = Modifier.testTag("save_budget_button")
                ) {
                    Text(if (lang == "bn") "সংরক্ষণ করুন" else "Save Budget")
                }
            },
            dismissButton = {
                TextButton(onClick = { showEditBudgetDialog = false }) {
                    Text(Localization.get("cancel", lang))
                }
            }
        )
    }

    // Add Recurring Expense Dialog
    if (showAddRecurringDialog) {
        var title by remember { mutableStateOf("") }
        var amountText by remember { mutableStateOf("") }
        var selectedCat by remember { mutableStateOf(ExpenseCategory.BILLS) }
        var selectedFreq by remember { mutableStateOf(RecurrenceFrequency.MONTHLY) }
        var notes by remember { mutableStateOf("") }
        var freqExpanded by remember { mutableStateOf(false) }

        Dialog(onDismissRequest = { showAddRecurringDialog = false }) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = Localization.get("add_recurring", lang),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(14.dp))

                    OutlinedTextField(
                        value = title,
                        onValueChange = { title = it },
                        label = { Text(if (lang == "bn") "শিরোনাম (যেমন: বাসা ভাড়া, বিল)" else "Title (e.g. Rent, Internet, Gym)") },
                        modifier = Modifier.fillMaxWidth().testTag("recurring_title_input"),
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = amountText,
                        onValueChange = { amountText = it },
                        label = { Text("${if (lang == "bn") "পরিমাণ" else "Amount"} ($currency)") },
                        modifier = Modifier.fillMaxWidth().testTag("recurring_amount_input"),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    // Frequency selector
                    ExposedDropdownMenuBox(
                        expanded = freqExpanded,
                        onExpandedChange = { freqExpanded = it }
                    ) {
                        OutlinedTextField(
                            value = selectedFreq.displayName,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text(if (lang == "bn") "পুনরাবৃত্তির সময়" else "Frequency") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = freqExpanded) },
                            modifier = Modifier
                                .menuAnchor(MenuAnchorType.PrimaryNotEditable)
                                .fillMaxWidth()
                        )
                        ExposedDropdownMenu(
                            expanded = freqExpanded,
                            onDismissRequest = { freqExpanded = false }
                        ) {
                            RecurrenceFrequency.entries.forEach { freq ->
                                DropdownMenuItem(
                                    text = { Text(freq.displayName) },
                                    onClick = {
                                        selectedFreq = freq
                                        freqExpanded = false
                                    }
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = notes,
                        onValueChange = { notes = it },
                        label = { Text(if (lang == "bn") "নোট (ঐচ্ছিক)" else "Notes (Optional)") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        TextButton(onClick = { showAddRecurringDialog = false }) {
                            Text(Localization.get("cancel", lang))
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(
                            onClick = {
                                val amount = amountText.toDoubleOrNull()
                                if (title.isNotBlank() && amount != null && amount > 0) {
                                    viewModel.addRecurringExpense(
                                        title = title.trim(),
                                        amount = amount,
                                        category = selectedCat,
                                        frequency = selectedFreq.name,
                                        startDate = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.US)
                                            .format(System.currentTimeMillis()),
                                        notes = notes.trim()
                                    )
                                    showAddRecurringDialog = false
                                }
                            },
                            modifier = Modifier.testTag("save_recurring_button")
                        ) {
                            Text(if (lang == "bn") "সংরক্ষণ করুন" else "Save")
                        }
                    }
                }
            }
        }
    }

    // Confirmation dialog before deleting recurring expense
    if (recurringToDelete != null) {
        ConfirmationDialog(
            title = Localization.get("delete_confirm_title", lang),
            message = "${Localization.get("delete_confirm_msg", lang)} (${recurringToDelete!!.title}: $currency${String.format(Locale.US, "%.2f", recurringToDelete!!.amount)})",
            confirmText = Localization.get("delete", lang),
            dismissText = Localization.get("cancel", lang),
            isDestructive = true,
            onConfirm = {
                viewModel.deleteRecurringExpense(recurringToDelete!!.id)
                recurringToDelete = null
            },
            onDismiss = {
                recurringToDelete = null
            }
        )
    }
}
