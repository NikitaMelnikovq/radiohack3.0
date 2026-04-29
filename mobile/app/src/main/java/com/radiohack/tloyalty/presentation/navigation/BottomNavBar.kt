package com.radiohack.tloyalty.presentation.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material.icons.rounded.ThumbUp
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import com.radiohack.tloyalty.core.ui.theme.TBlack
import com.radiohack.tloyalty.core.ui.theme.TGray
import com.radiohack.tloyalty.core.ui.theme.TSurface
import com.radiohack.tloyalty.core.ui.theme.TWhite
import com.radiohack.tloyalty.core.ui.theme.TYellow

@Composable
fun BottomNavBar(
    userId: Int,
    currentRoute: String?,
    onNavigate: (String) -> Unit,
) {
    val items = listOf(
        BottomNavItem("Главная", Icons.Rounded.Home, Routes.dashboard(userId), "dashboard"),
        BottomNavItem("Аналитика", Icons.Rounded.Star, Routes.analytics(userId), "analytics"),
        BottomNavItem("Офферы", Icons.Rounded.ThumbUp, Routes.offers(userId), "offers"),
        BottomNavItem("Ассистент", Icons.Rounded.Person, Routes.assistant(userId), "assistant"),
        BottomNavItem("Путь", Icons.Rounded.Settings, Routes.gamification(userId), "gamification"),
    )

    NavigationBar(
        containerColor = TSurface,
        contentColor = TWhite,
    ) {
        items.forEach { item ->
            val selected = currentRoute?.startsWith(item.routePrefix) == true
            NavigationBarItem(
                selected = selected,
                onClick = { onNavigate(item.route) },
                icon = { Icon(imageVector = item.icon, contentDescription = item.label) },
                label = { Text(text = item.label) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = TBlack,
                    selectedTextColor = TYellow,
                    indicatorColor = TYellow,
                    unselectedIconColor = TGray,
                    unselectedTextColor = TGray,
                ),
            )
        }
    }
}

private data class BottomNavItem(
    val label: String,
    val icon: ImageVector,
    val route: String,
    val routePrefix: String,
)
