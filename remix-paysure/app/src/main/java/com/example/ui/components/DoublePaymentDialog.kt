package com.example.ui.components

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
import androidx.compose.material.icons.filled.WarningAmber
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.model.PaymentTransaction
import com.example.ui.theme.AmberWarning
import com.example.ui.theme.CoralStop
import com.example.util.Formatters

/**
 * Feature 8: Double-Payment Protection Dialog
 * Warns the user if a duplicate or similar amount was recently sent to the same recipient.
 */
@Composable
fun DoublePaymentDialog(
    previousTransaction: PaymentTransaction,
    currentAmount: Double,
    recipientName: String,
    onConfirmSendAgain: () -> Unit,
    onCancelMistake: () -> Unit
) {
    Dialog(onDismissRequest = onCancelMistake) {
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 10.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp)
                .testTag("double_payment_dialog")
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .background(AmberWarning.copy(alpha = 0.15f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.WarningAmber,
                            contentDescription = null,
                            tint = AmberWarning,
                            modifier = Modifier.size(28.dp)
                        )
                    }

                    Column {
                        Text(
                            text = "POSSIBLE DUPLICATE",
                            style = MaterialTheme.typography.labelSmall,
                            color = AmberWarning,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                        Text(
                            text = "Double-Payment Check",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF0F172A)
                        )
                    }
                }

                Text(
                    text = "You already sent ${Formatters.formatCurrency(previousTransaction.amount)} to $recipientName ${Formatters.formatPlainDateTime(previousTransaction.timestamp).lowercase()}.\n\nDo you want to send ${Formatters.formatCurrency(currentAmount)} again, or was this a mistake?",
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color(0xFF334155),
                    lineHeight = 22.sp
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Primary safety choice: Cancel (Cancel was a mistake)
                Button(
                    onClick = onCancelMistake,
                    colors = ButtonDefaults.buttonColors(containerColor = CoralStop),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .testTag("cancel_duplicate_mistake_button")
                ) {
                    Text(
                        text = "Cancel, this was a mistake",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                }

                // Secondary choice: Yes, send again
                OutlinedButton(
                    onClick = onConfirmSendAgain,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("confirm_send_again_button")
                ) {
                    Text(
                        text = "Yes, send again",
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF334155),
                        fontSize = 15.sp
                    )
                }
            }
        }
    }
}
