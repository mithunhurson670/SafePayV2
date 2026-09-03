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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.CallReceived
import androidx.compose.material.icons.filled.CallMade
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Contact
import com.example.data.model.FluencyComfortLevel
import com.example.data.model.NetworkState
import com.example.data.model.PaymentTransaction
import com.example.data.model.TransactionType
import com.example.ui.components.NetworkProtectionBanner
import com.example.ui.components.TrustRingAvatar
import com.example.ui.theme.EmeraldTrust
import com.example.ui.theme.NavyPrimary
import com.example.ui.theme.NavySecondary
import com.example.util.Formatters

@Composable
fun HomeScreen(
    walletBalance: Double,
    isBalanceHidden: Boolean,
    onToggleBalance: () -> Unit,
    contacts: List<Contact>,
    recentTransactions: List<PaymentTransaction>,
    networkState: NetworkState,
    comfortLevel: FluencyComfortLevel,
    onSendMoneyClicked: () -> Unit,
    onContactClicked: (Contact) -> Unit,
    onViewHistoryClicked: () -> Unit,
    onAddFundsClicked: () -> Unit,
    onWhyLimitClicked: () -> Unit,
    onExplainRequested: (String, String) -> Unit,
    onRecheckNetwork: () -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(4.dp))
        }

        // Network Warning Banner if Poor or Offline
        if (!networkState.isSendAllowed) {
            item {
                NetworkProtectionBanner(
                    networkState = networkState,
                    onTryAgain = onRecheckNetwork
                )
            }
        }

        // 1. Wallet Balance Card (with long-press explain)
        item {
            Card(
                shape = RoundedCornerShape(24.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
                colors = CardDefaults.cardColors(containerColor = Color.Transparent),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("home_wallet_card")
                    .combinedClickable(
                        onClick = { },
                        onLongClick = {
                            onExplainRequested(
                                "Safe Wallet Balance",
                                "This is the real, verified money available in your Paysure safe balance. It updates automatically whenever you send or receive funds."
                            )
                        }
                    )
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            brush = Brush.linearGradient(
                                listOf(NavyPrimary, NavySecondary, Color(0xFF004D40))
                            )
                        )
                        .padding(22.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Security,
                                    contentDescription = null,
                                    tint = Color(0xFF69F0AE),
                                    modifier = Modifier.size(18.dp)
                                )
                                Text(
                                    text = "SAFE WALLET BALANCE",
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFFB0BEC5),
                                    letterSpacing = 1.sp
                                )
                            }

                            IconButton(
                                onClick = onToggleBalance,
                                modifier = Modifier
                                    .size(36.dp)
                                    .testTag("toggle_balance_button")
                            ) {
                                Icon(
                                    imageVector = if (isBalanceHidden) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                    contentDescription = "Toggle Balance Privacy",
                                    tint = Color.White.copy(alpha = 0.85f),
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }

                        // Big readable balance amount
                        Text(
                            text = if (isBalanceHidden) "••••••••" else Formatters.formatCurrency(walletBalance),
                            style = MaterialTheme.typography.displayMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )

                        // Fluency Self-Limit pill & Quick Add funds
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Fluency Guardrail Indicator (Feature 4)
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp),
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(Color.White.copy(alpha = 0.15f))
                                    .clickable { onWhyLimitClicked() }
                                    .padding(horizontal = 10.dp, vertical = 6.dp)
                                    .testTag("fluency_limit_chip")
                            ) {
                                Text(
                                    text = "🛡️ ${comfortLevel.badgeLabel}",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFFA7FFEB)
                                )
                                Text(
                                    text = "Why?",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Medium,
                                    color = Color.White.copy(alpha = 0.7f)
                                )
                            }

                            // Quick Add demo funds
                            OutlinedButton(
                                onClick = onAddFundsClicked,
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
                                border = BorderStroke(1.dp, Color.White.copy(alpha = 0.35f)),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier
                                    .height(36.dp)
                                    .testTag("home_add_funds_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Add,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp)
                                )
                                Text(
                                    text = "+ Add Money",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    modifier = Modifier.padding(start = 4.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

        // 2. High-contrast Primary Action Button ("Send Money") in thumb-friendly reach
        item {
            Button(
                onClick = onSendMoneyClicked,
                shape = RoundedCornerShape(18.dp),
                colors = ButtonDefaults.buttonColors(containerColor = EmeraldTrust),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp)
                    .testTag("home_send_money_button")
                    .combinedClickable(
                        onClick = onSendMoneyClicked,
                        onLongClick = {
                            onExplainRequested(
                                "Send Money Button",
                                "Tapping this starts a safe 3-step sequence to send money. You will select who to pay, enter the amount, and review everything before any money moves."
                            )
                        }
                    )
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Send,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "Send Money Safely",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
        }

        // 3. Visual Trust Rings - Recent Contacts Section (Feature 1)
        item {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Trusted Contacts",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = NavyPrimary
                        )
                        Text(
                            text = "Green ring shows past safe payments",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color(0xFF64748B)
                        )
                    }

                    IconButton(
                        onClick = {
                            onExplainRequested(
                                "Visual Trust Rings",
                                "Each person's photo has a colored ring. First-time contacts have a thin grey ring. As you pay someone safely over time, their ring turns green and thicker. This helps you instantly recognize genuine contacts."
                            )
                        },
                        modifier = Modifier.testTag("explain_trust_rings_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.HelpOutline,
                            contentDescription = "Explain Trust Rings",
                            tint = NavyPrimary
                        )
                    }
                }

                // Horizontal Carousel of Trusted Contacts
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp)
                        .testTag("home_contacts_row")
                ) {
                    items(contacts) { contact ->
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier
                                .width(78.dp)
                                .combinedClickable(
                                    onClick = { onContactClicked(contact) },
                                    onLongClick = {
                                        onExplainRequested(
                                            "${contact.name} (${contact.trustLevel.label})",
                                            "${contact.trustLevel.description} You have made ${contact.pastTransactionCount} safe past payments to them."
                                        )
                                    }
                                )
                                .testTag("contact_item_${contact.id}")
                        ) {
                            TrustRingAvatar(
                                contact = contact,
                                sizeDp = 64.dp
                            )
                            Text(
                                text = contact.name.split(" ").firstOrNull() ?: contact.name,
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = NavyPrimary,
                                maxLines = 1
                            )
                            Text(
                                text = contact.roleOrRelation,
                                style = MaterialTheme.typography.labelSmall,
                                color = Color(0xFF64748B),
                                maxLines = 1,
                                fontSize = 11.sp
                            )
                        }
                    }
                }
            }
        }

        // 4. Plain-Language Recent Activity Log
        item {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Recent Activity",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = NavyPrimary
                    )

                    Text(
                        text = "See All",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold,
                        color = EmeraldTrust,
                        modifier = Modifier
                            .clickable { onViewHistoryClicked() }
                            .padding(4.dp)
                            .testTag("home_see_all_history")
                    )
                }

                // List of plain language cards
                recentTransactions.take(3).forEach { tx ->
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .combinedClickable(
                                onClick = { onViewHistoryClicked() },
                                onLongClick = {
                                    onExplainRequested(
                                        "Payment Activity",
                                        "This shows everyday payments written in clear, plain words. We avoid confusing reference codes and banking jargon."
                                    )
                                }
                            )
                            .testTag("recent_tx_${tx.id}")
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(42.dp)
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
                                    modifier = Modifier.size(20.dp)
                                )
                            }

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = tx.plainDescription,
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Color(0xFF1E293B)
                                )
                                Text(
                                    text = Formatters.formatPlainDateTime(tx.timestamp),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Color(0xFF64748B)
                                )
                            }

                            Text(
                                text = (if (tx.type == TransactionType.SENT) "-" else "+") + Formatters.formatCurrency(tx.amount),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = if (tx.type == TransactionType.SENT) Color(0xFF0F172A) else Color(0xFF2E7D32)
                            )
                        }
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}
