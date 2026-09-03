@file:OptIn(androidx.compose.foundation.ExperimentalFoundationApi::class)

package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Contact
import com.example.data.model.TrustLevel

/**
 * Feature 1: Visual Trust Rings
 * Each recipient photo has a colored ring around it.
 * New recipients show a thin grey ring.
 * The ring fills in with vibrant green color and thicker stroke as past transactions increase.
 */
@Composable
fun TrustRingAvatar(
    contact: Contact,
    sizeDp: Dp = 64.dp,
    showVerifiedBadge: Boolean = true,
    onClick: (() -> Unit)? = null,
    onLongClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val trust = contact.trustLevel
    val ringColor = Color(trust.ringColorHex)
    val strokeWidth = trust.ringStrokeWidthDp.dp

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .size(sizeDp)
            .testTag("trust_ring_avatar_${contact.id}")
            .then(
                if (onClick != null || onLongClick != null) {
                    Modifier.combinedClickable(
                        onClick = { onClick?.invoke() },
                        onLongClick = { onLongClick?.invoke() }
                    )
                } else Modifier
            )
    ) {
        // The Visual Trust Ring Canvas
        Canvas(modifier = Modifier.size(sizeDp)) {
            val strokePx = strokeWidth.toPx()
            val radius = (size.minDimension - strokePx) / 2f
            drawCircle(
                color = ringColor,
                radius = radius,
                style = Stroke(width = strokePx)
            )
        }

        // Inner Avatar Circle with Initials
        val innerPadding = (strokeWidth + 3.dp) * 2
        val innerSize = sizeDp - innerPadding

        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(innerSize)
                .clip(CircleShape)
                .background(Color(contact.avatarBgColorHex))
        ) {
            Text(
                text = contact.initials,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = (innerSize.value * 0.38f).sp
            )
        }

        // Verified Shield Badge for Highly Trusted and Frequent contacts
        if (showVerifiedBadge && trust.isVerified) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .offset(x = 2.dp, y = 2.dp)
                    .size((sizeDp.value * 0.36f).dp)
                    .clip(CircleShape)
                    .background(Color(0xFF00C853))
            ) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = "Verified Contact",
                    tint = Color.White,
                    modifier = Modifier.size((sizeDp.value * 0.22f).dp)
                )
            }
        }
    }
}
