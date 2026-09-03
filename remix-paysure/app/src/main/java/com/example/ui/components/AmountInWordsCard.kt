package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.AmberBorder
import com.example.ui.theme.AmberWarning
import com.example.ui.theme.AmberWarningLight
import com.example.ui.theme.EmeraldTrust
import com.example.ui.theme.EmeraldTrustLight

/**
 * Feature 6: Extra-Zero Protection and Amount-in-Words display.
 * Prominently renders numeric amount in plain English words.
 * Requires an explicit confirmation tap on the words card if high threshold is crossed.
 */
@Composable
fun AmountInWordsCard(
    amountInWords: String,
    isHighAmountWarning: Boolean,
    isConfirmedByTap: Boolean,
    onConfirmTap: () -> Unit,
    modifier: Modifier = Modifier
) {
    val bgColor by animateColorAsState(
        targetValue = when {
            isHighAmountWarning && isConfirmedByTap -> EmeraldTrustLight
            isHighAmountWarning -> AmberWarningLight
            else -> Color(0xFFF1F5F9)
        },
        label = "words_bg_color"
    )

    val borderColor by animateColorAsState(
        targetValue = when {
            isHighAmountWarning && isConfirmedByTap -> EmeraldTrust
            isHighAmountWarning -> AmberBorder
            else -> Color(0xFFCBD5E1)
        },
        label = "words_border_color"
    )

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = bgColor),
        border = BorderStroke(1.5.dp, borderColor),
        modifier = modifier
            .fillMaxWidth()
            .testTag("amount_in_words_card")
            .then(
                if (isHighAmountWarning) {
                    Modifier.clickable { onConfirmTap() }
                } else Modifier
            )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "AMOUNT IN WORDS",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = if (isHighAmountWarning) AmberWarning else Color(0xFF475569),
                    letterSpacing = 1.sp
                )

                if (isHighAmountWarning) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = if (isConfirmedByTap) Icons.Default.CheckCircle else Icons.Default.Warning,
                            contentDescription = "Extra Zero Check",
                            tint = if (isConfirmedByTap) EmeraldTrust else AmberWarning,
                            modifier = Modifier.size(18.dp)
                        )
                        Text(
                            text = if (isConfirmedByTap) "Verified" else "Extra-Zero Check",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = if (isConfirmedByTap) EmeraldTrust else AmberWarning
                        )
                    }
                }
            }

            // Big, prominent legible words
            Text(
                text = amountInWords,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = if (isHighAmountWarning && !isConfirmedByTap) Color(0xFFBF360C) else Color(0xFF0F172A),
                lineHeight = 28.sp
            )

            // High amount extra-zero confirmation prompt
            AnimatedVisibility(visible = isHighAmountWarning) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 6.dp)
                        .background(
                            if (isConfirmedByTap) EmeraldTrust.copy(alpha = 0.15f) else AmberWarning.copy(alpha = 0.15f),
                            shape = RoundedCornerShape(10.dp)
                        )
                        .padding(horizontal = 12.dp, vertical = 8.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = if (isConfirmedByTap) Icons.Default.CheckCircle else Icons.Default.Warning,
                            contentDescription = null,
                            tint = if (isConfirmedByTap) EmeraldTrust else AmberWarning,
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            text = if (isConfirmedByTap) {
                                "✓ You verified: You intended to enter this large amount."
                            } else {
                                "⚠️ Tap here to confirm you didn't add an extra zero by mistake."
                            },
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Medium,
                            color = if (isConfirmedByTap) EmeraldTrust else Color(0xFF9A3412)
                        )
                    }
                }
            }
        }
    }
}
