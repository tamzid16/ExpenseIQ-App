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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import com.expenseiq.app.ui.util.Localization
import com.expenseiq.app.ui.viewmodel.AuthViewModel
import java.util.Locale

@Composable
fun InitialSetupScreen(
    authViewModel: AuthViewModel,
    onSetupCompleted: () -> Unit
) {
    val currentLang by authViewModel.currentLanguage.collectAsState()

    var allowanceText by remember { mutableStateOf("") }
    var rentText by remember { mutableStateOf("") }
    var billsText by remember { mutableStateOf("") }
    var otherFixedText by remember { mutableStateOf("") }
    var customBudgetText by remember { mutableStateOf("") }
    var selectedCurrency by remember { mutableStateOf("৳") }

    val allowance = allowanceText.toDoubleOrNull() ?: 0.0
    val rent = rentText.toDoubleOrNull() ?: 0.0
    val bills = billsText.toDoubleOrNull() ?: 0.0
    val otherFixed = otherFixedText.toDoubleOrNull() ?: 0.0
    val totalFixed = rent + bills + otherFixed

    // Recommended budget target (either user custom or remaining allowance)
    val defaultBudget = if (allowance > 0) allowance else (totalFixed + 5000.0)

    val currencies = listOf("৳", "$", "€", "£", "¥", "₹")

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState()),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(22.dp)
            ) {
                // Language switcher at top
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primaryContainer),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.AccountBalanceWallet,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = Localization.get("setup_title", currentLang),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Row {
                        FilterChip(
                            selected = currentLang == "en",
                            onClick = { authViewModel.setLanguage("en") },
                            label = { Text("EN") },
                            modifier = Modifier.testTag("setup_lang_en")
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        FilterChip(
                            selected = currentLang == "bn",
                            onClick = { authViewModel.setLanguage("bn") },
                            label = { Text("বাংলা") },
                            modifier = Modifier.testTag("setup_lang_bn")
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = Localization.get("setup_subtitle", currentLang),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Currency selector
                Text(
                    text = Localization.get("currency_symbol", currentLang),
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    currencies.forEach { cur ->
                        FilterChip(
                            selected = selectedCurrency == cur,
                            onClick = { selectedCurrency = cur },
                            label = { Text(if (cur == "৳") "৳ (BDT)" else cur, fontWeight = FontWeight.Bold) },
                            modifier = Modifier.testTag("setup_currency_$cur")
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Monthly allowance / income
                OutlinedTextField(
                    value = allowanceText,
                    onValueChange = { allowanceText = it },
                    label = { Text(Localization.get("monthly_income", currentLang)) },
                    placeholder = { Text("e.g. 50000") },
                    leadingIcon = { Icon(Icons.Default.AttachMoney, contentDescription = null) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("setup_income_input"),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Rent
                OutlinedTextField(
                    value = rentText,
                    onValueChange = { rentText = it },
                    label = { Text(Localization.get("house_rent", currentLang)) },
                    placeholder = { Text("e.g. 15000") },
                    leadingIcon = { Icon(Icons.Default.Home, contentDescription = null) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("setup_rent_input"),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Monthly utility bills
                OutlinedTextField(
                    value = billsText,
                    onValueChange = { billsText = it },
                    label = { Text(Localization.get("utility_bills", currentLang)) },
                    placeholder = { Text("e.g. 4500") },
                    leadingIcon = { Icon(Icons.Default.Receipt, contentDescription = null) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("setup_bills_input"),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Other fixed expenses
                OutlinedTextField(
                    value = otherFixedText,
                    onValueChange = { otherFixedText = it },
                    label = { Text(Localization.get("other_fixed", currentLang)) },
                    placeholder = { Text("e.g. 3000") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("setup_other_input"),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Fixed Obligations Overview Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = Localization.get("fixed_total", currentLang),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "$selectedCurrency${String.format(Locale.US, "%.2f", totalFixed)}",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        if (allowance > 0) {
                            Spacer(modifier = Modifier.height(4.dp))
                            val discretionary = (allowance - totalFixed).coerceAtLeast(0.0)
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = if (currentLang == "bn") "বাকি অবশিষ্ট অর্থ" else "Available Discretionary",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = "$selectedCurrency${String.format(Locale.US, "%.2f", discretionary)}",
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Monthly Spending Budget Target
                OutlinedTextField(
                    value = customBudgetText,
                    onValueChange = { customBudgetText = it },
                    label = { Text(Localization.get("monthly_budget_target", currentLang)) },
                    placeholder = { Text(String.format(Locale.US, "%.2f", defaultBudget)) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("setup_budget_input"),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
                )

                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = {
                        val finalBudget = customBudgetText.toDoubleOrNull() ?: defaultBudget
                        authViewModel.completeInitialFinancialSetup(
                            monthlyAllowance = allowance,
                            rent = rent,
                            bills = bills,
                            otherFixed = otherFixed,
                            budgetAmount = finalBudget,
                            currencySymbol = selectedCurrency,
                            language = currentLang,
                            onComplete = onSetupCompleted
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("setup_submit_button")
                ) {
                    Icon(Icons.Default.Check, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(Localization.get("setup_complete_button", currentLang))
                }

                Spacer(modifier = Modifier.height(8.dp))

                TextButton(
                    onClick = { authViewModel.logout() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("setup_back_to_login")
                ) {
                    Text(Localization.get("back_to_login", currentLang))
                }
            }
        }
    }
}
