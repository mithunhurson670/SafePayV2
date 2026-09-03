package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.SignalCellularConnectedNoInternet0Bar
import androidx.compose.material.icons.filled.WifiOff
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
import com.example.data.model.NetworkState
import com.example.ui.theme.CoralStop
import com.example.ui.theme.CoralStopLight

/**
 * Feature 7: Network-Aware Payment Protection
 * Blocks sending when network is poor or offline to prevent stuck or duplicate charges.
 * Provides a plain-language explanation and a "Try Again" action.
 */
@Composable
fun NetworkProtectionBanner(
    networkState: NetworkState,
    onTryAgain: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CoralStopLight),
        border = BorderStroke(1.5.dp, CoralStop.copy(alpha = 0.5f)),
        modifier = modifier
            .fillMaxWidth()
            .testTag("network_protection_banner")
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Icon(
                    imageVector = if (networkState == NetworkState.OFFLINE) Icons.Default.WifiOff else Icons.Default.SignalCellularConnectedNoInternet0Bar,
                    contentDescription = "Connection Alert",
                    tint = CoralStop,
                    modifier = Modifier.size(26.dp)
                )
                Text(
                    text = if (networkState == NetworkState.OFFLINE) "No Internet Connection" else "Weak Internet Connection",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = CoralStop
                )
            }

            Text(
                text = "Your internet connection looks weak right now. Please try again once it's stronger, so your payment isn't stuck or duplicated.",
                style = MaterialTheme.typography.bodyMedium,
                color = Color(0xFF7F1D1D)
            )

            OutlinedButton(
                onClick = onTryAgain,
                colors = ButtonDefaults.outlinedButtonColors(contentColor = CoralStop),
                border = BorderStroke(1.dp, CoralStop),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier
                    .align(Alignment.End)
                    .testTag("network_try_again_button")
            ) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp)
                )
                Text(
                    text = "Try Again",
                    modifier = Modifier.padding(start = 6.dp),
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}
