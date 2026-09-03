package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.SignalCellular4Bar
import androidx.compose.material.icons.filled.SignalCellularConnectedNoInternet0Bar
import androidx.compose.material.icons.filled.WifiOff
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import com.example.data.model.NetworkState
import com.example.ui.theme.CoralStop
import com.example.ui.theme.EmeraldTrust
import com.example.ui.theme.NavyPrimary

@Composable
fun PaysureTopBar(
    networkState: NetworkState,
    currentLanguage: String,
    onToggleNetwork: () -> Unit,
    onToggleLanguage: () -> Unit,
    onExplainNetwork: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // App Identity
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(NavyPrimary),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Shield,
                    contentDescription = null,
                    tint = EmeraldTrust,
                    modifier = Modifier.size(20.dp)
                )
            }

            Text(
                text = "Paysure",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.ExtraBold,
                color = NavyPrimary,
                letterSpacing = (-0.5).sp
            )
        }

        // Action controls (Network pill & Language switcher)
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Interactive Network Pill
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(
                        when (networkState) {
                            NetworkState.GOOD -> Color(0xFFE8F5E9)
                            NetworkState.POOR -> Color(0xFFFFF3E0)
                            NetworkState.OFFLINE -> Color(0xFFFFEBEE)
                        }
                    )
                    .border(
                        width = 1.dp,
                        color = when (networkState) {
                            NetworkState.GOOD -> Color(0xFF81C784)
                            NetworkState.POOR -> Color(0xFFFFB74D)
                            NetworkState.OFFLINE -> Color(0xFFE57373)
                        },
                        shape = RoundedCornerShape(20.dp)
                    )
                    .clickable { onToggleNetwork() }
                    .padding(horizontal = 10.dp, vertical = 6.dp)
                    .testTag("top_bar_network_pill")
            ) {
                Icon(
                    imageVector = when (networkState) {
                        NetworkState.GOOD -> Icons.Default.SignalCellular4Bar
                        NetworkState.POOR -> Icons.Default.SignalCellularConnectedNoInternet0Bar
                        NetworkState.OFFLINE -> Icons.Default.WifiOff
                    },
                    contentDescription = "Network Status",
                    tint = when (networkState) {
                        NetworkState.GOOD -> EmeraldTrust
                        NetworkState.POOR -> Color(0xFFE65100)
                        NetworkState.OFFLINE -> CoralStop
                    },
                    modifier = Modifier.size(16.dp)
                )
                Text(
                    text = when (networkState) {
                        NetworkState.GOOD -> "Online"
                        NetworkState.POOR -> "Weak Net"
                        NetworkState.OFFLINE -> "Offline"
                    },
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = when (networkState) {
                        NetworkState.GOOD -> Color(0xFF1B5E20)
                        NetworkState.POOR -> Color(0xFFBF360C)
                        NetworkState.OFFLINE -> CoralStop
                    }
                )
            }

            // Language switcher button
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(3.dp),
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color(0xFFF1F5F9))
                    .clickable { onToggleLanguage() }
                    .padding(horizontal = 10.dp, vertical = 6.dp)
                    .testTag("top_bar_language_button")
            ) {
                Icon(
                    imageVector = Icons.Default.Language,
                    contentDescription = "Change Language",
                    tint = NavyPrimary,
                    modifier = Modifier.size(15.dp)
                )
                Text(
                    text = currentLanguage,
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = NavyPrimary
                )
            }
        }
    }
}
