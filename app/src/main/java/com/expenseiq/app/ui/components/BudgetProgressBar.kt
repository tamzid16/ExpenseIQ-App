package com.expenseiq.app.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.expenseiq.app.ui.theme.Danger100
import com.expenseiq.app.ui.theme.SuccessGreen
import com.expenseiq.app.ui.theme.Warning80
import com.expenseiq.app.ui.theme.Warning90

@Composable
fun BudgetProgressBar(
    spent: Double,
    budget: Double,
    currencySymbol: String,
    modifier: Modifier = Modifier
) {
    if (budget <= 0) return

    val percentage = ((spent / budget) * 100).toInt()
    val progressRatio = (spent / budget).toFloat().coerceIn(0f, 1f)
    val animatedProgress by animateFloatAsState(
        targetValue = progressRatio,
        label = "budget_progress"
    )

    val (barColor, warningText, warningIcon) = when {
        percentage >= 100 -> Triple(Danger100, "100%+ Budget Exceeded!", Icons.Default.Error)
        percentage >= 90 -> Triple(Warning90, "90% Alert: Approaching Limit", Icons.Default.Warning)
        percentage >= 80 -> Triple(Warning80, "80% Warning: High Usage", Icons.Default.Warning)
        else -> Triple(SuccessGreen, "On Track ($percentage% used)", Icons.Default.CheckCircle)
    }

    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Budget Used: $percentage%",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = barColor
            )

            // Warning pill badge
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(barColor.copy(alpha = 0.15f))
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Icon(
                    imageVector = warningIcon,
                    contentDescription = null,
                    tint = barColor,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.size(4.dp))
                Text(
                    text = warningText,
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = barColor
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Custom rounded linear progress bar
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(10.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(animatedProgress)
                    .height(10.dp)
                    .clip(CircleShape)
                    .background(barColor)
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            val remaining = (budget - spent).coerceAtLeast(0.0)
            Text(
                text = "Spent: $currencySymbol${String.format(java.util.Locale.US, "%.2f", spent)}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = if (spent > budget) {
                    "Over by: $currencySymbol${String.format(java.util.Locale.US, "%.2f", spent - budget)}"
                } else {
                    "Remaining: $currencySymbol${String.format(java.util.Locale.US, "%.2f", remaining)}"
                },
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.Medium,
                color = if (spent > budget) Danger100 else MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
