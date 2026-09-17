package com.example.ui.components

import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.AppTab
import com.example.model.Language
import com.example.model.Localization
import com.example.theme.LineBorder
import com.example.theme.Muted
import com.example.theme.Navy
import com.example.theme.TealAccent

data class NavItem(
    val tab: AppTab,
    val titleKey: String,
    val icon: ImageVector
)

@Composable
fun BottomNavBar(
    selectedTab: AppTab,
    onTabSelected: (AppTab) -> Unit,
    lang: Language,
    modifier: Modifier = Modifier
) {
    val items = listOf(
        NavItem(AppTab.HOME, "nav_home", Icons.Default.Home),
        NavItem(AppTab.PREPARE, "nav_prepare", Icons.Default.Inventory2),
        NavItem(AppTab.PROTECT, "nav_protect", Icons.Default.Security),
        NavItem(AppTab.RECOVER, "nav_recover", Icons.Default.Place),
        NavItem(AppTab.CONNECT, "nav_connect", Icons.Default.People)
    )

    NavigationBar(
        modifier = modifier
            .height(68.dp)
            .testTag("bottom_nav_bar"),
        containerColor = Color.White,
        tonalElevation = 8.dp
    ) {
        items.forEach { item ->
            val isSelected = item.tab == selectedTab
            val label = Localization.t(item.titleKey, lang)

            NavigationBarItem(
                selected = isSelected,
                onClick = { onTabSelected(item.tab) },
                icon = {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = label
                    )
                },
                label = {
                    Text(
                        text = label,
                        fontSize = 10.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                        maxLines = 1
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = Navy,
                    selectedTextColor = Navy,
                    indicatorColor = LineBorder.copy(alpha = 0.6f),
                    unselectedIconColor = Muted,
                    unselectedTextColor = Muted
                ),
                modifier = Modifier.testTag("nav_item_${item.tab.id}")
            )
        }
    }
}
