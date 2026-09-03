@file:OptIn(androidx.compose.foundation.ExperimentalFoundationApi::class)

package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CallMade
import androidx.compose.material.icons.filled.CallReceived
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.model.PaymentTransaction
import com.example.data.model.TransactionType
import com.example.ui.theme.EmeraldTrust
import com.example.ui.theme.NavyPrimary
import com.example.util.Formatters

enum class HistoryFilter {
    ALL,
    SENT,
    RECEIVED
}

@Composable
fun HistoryScreen(
    transactions: List<PaymentTransaction>,
    onExplainRequested: (String, String) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedFilter by remember { mutableStateOf(HistoryFilter.ALL) }
    var selectedTransactionForReceipt by remember { mutableStateOf<PaymentTransaction?>(null) }

    // Dialog showing clean receipt without technical jargon
    selectedTransactionForReceipt?.let { tx ->
        TransactionReceiptDialog(
            transaction = tx,
            onDismiss = { selectedTransactionForReceipt = null }
        )
    }

    val filteredList = remember(transactions, selectedFilter) {
        when (selectedFilter) {
            HistoryFilter.ALL -> transactions
            HistoryFilter.SENT -> transactions.filter { it.type == TransactionType.SENT }
            HistoryFilter.RECEIVED -> transactions.filter { it.type == TransactionType.RECEIVED }
        }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Payment History",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = NavyPrimary
                    )
                    Text(
                        text = "Clear, jargon-free log of your transactions",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color(0xFF64748B)
                    )
                }

                IconButton(
                    onClick = {
                        onExplainRequested(
                            "Plain Language History",
                            "All transactions here are written in clear, everyday sentences. We hide complex transaction IDs and banking hashes so you can scan your spending effortlessly."
                        )
                    },
                    modifier = Modifier.testTag("explain_history_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.HelpOutline,
                        contentDescription = "Explain History",
                        tint = NavyPrimary
                    )
                }
            }
        }

        // Filter Tabs (All, Sent, Received)
        item {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.padding(vertical = 2.dp)
            ) {
                FilterChip(
                    selected = selectedFilter == HistoryFilter.ALL,
                    onClick = { selectedFilter = HistoryFilter.ALL },
                    label = { Text("All Activities") },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = NavyPrimary,
                        selectedLabelColor = Color.White
                    ),
                    modifier = Modifier.testTag("history_filter_all")
                )

                FilterChip(
                    selected = selectedFilter == HistoryFilter.SENT,
                    onClick = { selectedFilter = HistoryFilter.SENT },
                    label = { Text("Money Sent") },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = NavyPrimary,
                        selectedLabelColor = Color.White
                    ),
                    modifier = Modifier.testTag("history_filter_sent")
                )

                FilterChip(
                    selected = selectedFilter == HistoryFilter.RECEIVED,
                    onClick = { selectedFilter = HistoryFilter.RECEIVED },
                    label = { Text("Money Received") },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = NavyPrimary,
                        selectedLabelColor = Color.White
                    ),
                    modifier = Modifier.testTag("history_filter_received")
                )
            }
        }

        if (filteredList.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 40.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Receipt,
                            contentDescription = null,
                            tint = Color(0xFF94A3B8),
                            modifier = Modifier.size(48.dp)
                        )
                        Text(
                            text = "No payments found in this filter",
                            style = MaterialTheme.typography.bodyLarge,
                            color = Color(0xFF64748B)
                        )
                    }
                }
            }
        } else {
            items(filteredList) { tx ->
                Card(
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .combinedClickable(
                            onClick = { selectedTransactionForReceipt = tx },
                            onLongClick = {
                                onExplainRequested(
                                    "Payment to ${tx.recipientName}",
                                    "Sent ${Formatters.formatCurrency(tx.amount)} on ${Formatters.formatPlainDateTime(tx.timestamp)}. Tap to open the friendly receipt."
                                )
                            }
                        )
                        .testTag("history_item_${tx.id}")
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(46.dp)
                                .clip(CircleShape)
                                .background(
                                    if (tx.type == TransactionType.SENT) Color(0xFFFFEBEE) else Color(0xFFE8F5E9)
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = if (tx.type == TransactionType.SENT) Icons.Default.CallMade else Icons.Default.CallReceived,
                                contentDescription = null,
                                tint = if (tx.type == TransactionType.SENT) Color(0xFFD32F2F) else Color(0xFF2E7D32),
                                modifier = Modifier.size(22.dp)
                            )
                        }

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = tx.plainDescription,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.SemiBold,
                                color = NavyPrimary
                            )
                            Text(
                                text = Formatters.formatPlainDateTime(tx.timestamp),
                                style = MaterialTheme.typography.bodySmall,
                                color = Color(0xFF64748B)
                            )
                        }

                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = (if (tx.type == TransactionType.SENT) "-" else "+") + Formatters.formatCurrency(tx.amount),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = if (tx.type == TransactionType.SENT) Color(0xFF0F172A) else Color(0xFF2E7D32)
                            )
                            Text(
                                text = "Completed",
                                style = MaterialTheme.typography.labelSmall,
                                color = EmeraldTrust,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

/**
 * Clean plain-language receipt modal without technical jargon
 */
@Composable
private fun TransactionReceiptDialog(
    transaction: PaymentTransaction,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
                .testTag("transaction_receipt_dialog")
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(18.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFE8F5E9)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = Color(0xFF2E7D32),
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    Column {
                        Text(
                            text = "SAFE RECEIPT",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = EmeraldTrust,
                            letterSpacing = 1.sp
                        )
                        Text(
                            text = "Payment Verified",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = NavyPrimary
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(Color(0xFFF8FAFC))
                        .padding(16.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        ReceiptRow("Amount", Formatters.formatCurrency(transaction.amount))
                        ReceiptRow("Recipient", transaction.recipientName)
                        ReceiptRow("Relation / Role", transaction.roleOrRelation)
                        ReceiptRow("When", Formatters.formatPlainDateTime(transaction.timestamp))
                        ReceiptRow("Status", "✓ Successfully Transferred")
                    }
                }

                Text(
                    text = "Tip: Need help or have questions about this payment? You can call Paysure safe support anytime.",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFF64748B)
                )

                androidx.compose.material3.Button(
                    onClick = onDismiss,
                    shape = RoundedCornerShape(12.dp),
                    colors = androidx.compose.material3.ButtonDefaults.buttonColors(containerColor = NavyPrimary),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("receipt_close_button")
                ) {
                    Text("Close Receipt", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
private fun ReceiptRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, style = MaterialTheme.typography.bodyMedium, color = Color(0xFF64748B))
        Text(text = value, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold, color = NavyPrimary)
    }
}
