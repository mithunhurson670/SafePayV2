package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.SignalCellular4Bar
import androidx.compose.material.icons.filled.SignalCellularConnectedNoInternet0Bar
import androidx.compose.material.icons.filled.StopCircle
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.WifiOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.FluencyComfortLevel
import com.example.data.model.NetworkState
import com.example.ui.theme.AmberWarning
import com.example.ui.theme.CoralStop
import com.example.ui.theme.EmeraldTrust
import com.example.ui.theme.NavyPrimary
import com.example.util.Formatters

@Composable
fun HelpSafetyScreen(
    networkState: NetworkState,
    comfortLevel: FluencyComfortLevel,
    onSetNetwork: (NetworkState) -> Unit,
    onSetFluencyComfort: (FluencyComfortLevel?) -> Unit,
    onAddFunds: (Double) -> Unit,
    onWhyLimitClicked: () -> Unit,
    onExplainRequested: (String, String) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Column(modifier = Modifier.padding(top = 4.dp)) {
                Text(
                    text = "Safety Guardrails & Help",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = NavyPrimary
                )
                Text(
                    text = "Designed for effortless, worry-free payments",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFF64748B)
                )
            }
        }

        // 1. Current Session Fluency Limit Card (Feature 4)
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFE0F2F1)),
                border = BorderStroke(1.5.dp, EmeraldTrust.copy(alpha = 0.5f)),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("help_fluency_card")
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Security,
                            contentDescription = null,
                            tint = EmeraldTrust,
                            modifier = Modifier.size(24.dp)
                        )
                        Text(
                            text = "Fluency-Based Self-Limit",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF004D40)
                        )
                    }

                    Text(
                        text = comfortLevel.plainReason,
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color(0xFF004D40),
                        lineHeight = 22.sp
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Current Cap: ${Formatters.formatCurrency(comfortLevel.transferLimit)}",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.ExtraBold,
                            color = EmeraldTrust
                        )

                        Text(
                            text = "Learn More →",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = EmeraldTrust,
                            modifier = Modifier
                                .clickable { onWhyLimitClicked() }
                                .padding(4.dp)
                        )
                    }
                }
            }
        }

        // 2. Interactive Safety Simulator (Test Features Live!)
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Text(
                        text = "Safety Simulator (Demo Controls)",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = NavyPrimary
                    )

                    // Network State Simulator
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text(
                            text = "Simulate Network State (Feature 7):",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF475569)
                        )
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            FilterChip(
                                selected = networkState == NetworkState.GOOD,
                                onClick = { onSetNetwork(NetworkState.GOOD) },
                                label = { Text("Good (4G/Wi-Fi)") },
                                leadingIcon = {
                                    Icon(Icons.Default.SignalCellular4Bar, contentDescription = null, modifier = Modifier.size(16.dp))
                                },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = EmeraldTrust,
                                    selectedLabelColor = Color.White,
                                    selectedLeadingIconColor = Color.White
                                )
                            )

                            FilterChip(
                                selected = networkState == NetworkState.POOR,
                                onClick = { onSetNetwork(NetworkState.POOR) },
                                label = { Text("Poor") },
                                leadingIcon = {
                                    Icon(Icons.Default.SignalCellularConnectedNoInternet0Bar, contentDescription = null, modifier = Modifier.size(16.dp))
                                },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = AmberWarning,
                                    selectedLabelColor = Color.White,
                                    selectedLeadingIconColor = Color.White
                                )
                            )

                            FilterChip(
                                selected = networkState == NetworkState.OFFLINE,
                                onClick = { onSetNetwork(NetworkState.OFFLINE) },
                                label = { Text("Offline") },
                                leadingIcon = {
                                    Icon(Icons.Default.WifiOff, contentDescription = null, modifier = Modifier.size(16.dp))
                                },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = CoralStop,
                                    selectedLabelColor = Color.White,
                                    selectedLeadingIconColor = Color.White
                                )
                            )
                        }
                    }

                    // Comfort Level Simulator
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text(
                            text = "Test Fluency Guardrail Cap (Feature 4):",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF475569)
                        )
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            FilterChip(
                                selected = comfortLevel == FluencyComfortLevel.CAUTIOUS,
                                onClick = { onSetFluencyComfort(FluencyComfortLevel.CAUTIOUS) },
                                label = { Text("Cautious (₹5k)") },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = NavyPrimary,
                                    selectedLabelColor = Color.White
                                )
                            )

                            FilterChip(
                                selected = comfortLevel == FluencyComfortLevel.MODERATE,
                                onClick = { onSetFluencyComfort(FluencyComfortLevel.MODERATE) },
                                label = { Text("Moderate (₹15k)") },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = NavyPrimary,
                                    selectedLabelColor = Color.White
                                )
                            )

                            FilterChip(
                                selected = comfortLevel == FluencyComfortLevel.HIGH,
                                onClick = { onSetFluencyComfort(FluencyComfortLevel.HIGH) },
                                label = { Text("High (₹50k)") },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = NavyPrimary,
                                    selectedLabelColor = Color.White
                                )
                            )
                        }
                    }

                    // Add Demo Balance
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text(
                            text = "Add Demo Money:",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF475569)
                        )
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedButton(
                                onClick = { onAddFunds(2000.0) },
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Text("+ ₹2,000")
                            }
                            OutlinedButton(
                                onClick = { onAddFunds(10000.0) },
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Text("+ ₹10,000")
                            }
                        }
                    }
                }
            }
        }

        // 3. Educational Safety Feature Cards
        item {
            Text(
                text = "Key Safety Features",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = NavyPrimary
            )
        }

        item {
            SafetyFeatureCard(
                icon = Icons.Default.Security,
                iconTint = EmeraldTrust,
                title = "1. Visual Trust Rings",
                description = "Every contact photo has a colored ring. New contacts have a thin grey ring. As you pay them safely over time, the ring turns green and thicker so you always recognize genuine payees."
            )
        }

        item {
            SafetyFeatureCard(
                icon = Icons.Default.StopCircle,
                iconTint = CoralStop,
                title = "2. 10-Second STOP Window",
                description = "When you tap 'Confirm & Send', money is never sent immediately. You get 10 seconds with a big STOP button to cancel with zero charge."
            )
        }

        item {
            SafetyFeatureCard(
                icon = Icons.Default.Visibility,
                iconTint = AmberWarning,
                title = "3. Spend-Pattern Mirror",
                description = "If you enter an amount much higher than what you usually pay to that person, Paysure shows a gentle notice before sending so you can double-check."
            )
        }

        item {
            SafetyFeatureCard(
                icon = Icons.Default.CheckCircle,
                iconTint = EmeraldTrust,
                title = "4. Extra-Zero Mistake Shield",
                description = "The amount is spelled out in words in large letters. If the amount is large, you must tap the words card directly to verify before sending is unlocked."
            )
        }

        item {
            SafetyFeatureCard(
                icon = Icons.Default.Phone,
                iconTint = NavyPrimary,
                title = "5. Free Safe Support",
                description = "If you ever feel confused, our friendly team is always available to guide you step-by-step in your local language."
            )
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun SafetyFeatureCard(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    iconTint: Color,
    title: String,
    description: String
) {
    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.Top,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .background(iconTint.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconTint,
                    modifier = Modifier.size(22.dp)
                )
            }

            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = NavyPrimary
                )
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFF475569),
                    lineHeight = 22.sp
                )
            }
        }
    }
}
