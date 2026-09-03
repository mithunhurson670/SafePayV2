package com.example.ui.components

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.EmeraldTrust
import com.example.ui.theme.NavyPrimary
import com.example.ui.viewmodel.AppTab

@Composable
fun BottomNavBar(
    selectedTab: AppTab,
    onTabSelected: (AppTab) -> Unit,
    modifier: Modifier = Modifier
) {
    NavigationBar(
        containerColor = Color.White,
        tonalElevation = 8.dp,
        modifier = modifier
            .windowInsetsPadding(WindowInsets.navigationBars)
            .testTag("paysure_bottom_nav")
    ) {
        NavigationBarItem(
            selected = selectedTab == AppTab.HOME,
            onClick = { onTabSelected(AppTab.HOME) },
            icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
            label = {
                Text(
                    text = "Home",
                    fontWeight = if (selectedTab == AppTab.HOME) FontWeight.Bold else FontWeight.Medium,
                    fontSize = 12.sp
                )
            },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = NavyPrimary,
                selectedTextColor = NavyPrimary,
                indicatorColor = Color(0xFFE2E8F0),
                unselectedIconColor = Color(0xFF64748B),
                unselectedTextColor = Color(0xFF64748B)
            ),
            modifier = Modifier.testTag("nav_item_home")
        )

        NavigationBarItem(
            selected = selectedTab == AppTab.SEND_MONEY,
            onClick = { onTabSelected(AppTab.SEND_MONEY) },
            icon = { Icon(Icons.Default.Send, contentDescription = "Send Money") },
            label = {
                Text(
                    text = "Send Money",
                    fontWeight = if (selectedTab == AppTab.SEND_MONEY) FontWeight.Bold else FontWeight.Medium,
                    fontSize = 12.sp
                )
            },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = EmeraldTrust,
                selectedTextColor = EmeraldTrust,
                indicatorColor = Color(0xFFE0F2F1),
                unselectedIconColor = Color(0xFF64748B),
                unselectedTextColor = Color(0xFF64748B)
            ),
            modifier = Modifier.testTag("nav_item_send")
        )

        NavigationBarItem(
            selected = selectedTab == AppTab.HISTORY,
            onClick = { onTabSelected(AppTab.HISTORY) },
            icon = { Icon(Icons.Default.History, contentDescription = "History") },
            label = {
                Text(
                    text = "History",
                    fontWeight = if (selectedTab == AppTab.HISTORY) FontWeight.Bold else FontWeight.Medium,
                    fontSize = 12.sp
                )
            },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = NavyPrimary,
                selectedTextColor = NavyPrimary,
                indicatorColor = Color(0xFFE2E8F0),
                unselectedIconColor = Color(0xFF64748B),
                unselectedTextColor = Color(0xFF64748B)
            ),
            modifier = Modifier.testTag("nav_item_history")
        )

        NavigationBarItem(
            selected = selectedTab == AppTab.HELP,
            onClick = { onTabSelected(AppTab.HELP) },
            icon = { Icon(Icons.Default.Shield, contentDescription = "Safety & Help") },
            label = {
                Text(
                    text = "Safety & Help",
                    fontWeight = if (selectedTab == AppTab.HELP) FontWeight.Bold else FontWeight.Medium,
                    fontSize = 12.sp
                )
            },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = NavyPrimary,
                selectedTextColor = NavyPrimary,
                indicatorColor = Color(0xFFE2E8F0),
                unselectedIconColor = Color(0xFF64748B),
                unselectedTextColor = Color(0xFF64748B)
            ),
            modifier = Modifier.testTag("nav_item_help")
        )
    }
}
