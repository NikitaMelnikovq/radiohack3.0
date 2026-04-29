package com.radiohack.tloyalty.presentation.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.radiohack.tloyalty.core.ui.components.SegmentBadge
import com.radiohack.tloyalty.core.ui.theme.TBlack
import com.radiohack.tloyalty.core.ui.theme.TGray
import com.radiohack.tloyalty.core.ui.theme.TWhite
import com.radiohack.tloyalty.core.ui.theme.TYellow
import com.radiohack.tloyalty.presentation.analytics.AnalyticsScreen
import com.radiohack.tloyalty.presentation.assistant.AssistantScreen
import com.radiohack.tloyalty.presentation.common.appContainer
import com.radiohack.tloyalty.presentation.dashboard.DashboardScreen
import com.radiohack.tloyalty.presentation.demo.DemoProfilesScreen
import com.radiohack.tloyalty.presentation.gamification.GamificationScreen
import com.radiohack.tloyalty.presentation.offers.OffersScreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppNavGraph(
    navController: NavHostController = rememberNavController(),
) {
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route
    val userId = backStackEntry?.arguments?.getInt("userId")
    val showShell = currentRoute != Routes.DEMO && userId != null
    val context = LocalContext.current
    val selectedUser = remember(currentRoute, userId) {
        context.appContainer.userPreferences.selectedUser()
    }

    Scaffold(
        containerColor = TBlack,
        topBar = {
            if (showShell) {
                LoyaltyTopBar(
                    title = titleForRoute(currentRoute),
                    userLabel = selectedUser.label ?: "Пользователь #$userId",
                    segment = selectedUser.segment.orEmpty(),
                    onChangeUser = {
                        navController.navigate(Routes.DEMO) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                inclusive = true
                            }
                        }
                    },
                )
            }
        },
        bottomBar = {
            if (showShell) {
                BottomNavBar(
                    userId = userId,
                    currentRoute = currentRoute,
                    onNavigate = { route ->
                        navController.navigate(route) {
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                )
            }
        },
    ) { padding ->
        LoyaltyNavHost(navController = navController, padding = padding)
    }
}

@Composable
private fun LoyaltyNavHost(
    navController: NavHostController,
    padding: PaddingValues,
) {
    NavHost(
        navController = navController,
        startDestination = Routes.DEMO,
        modifier = Modifier.padding(padding),
    ) {
        composable(Routes.DEMO) {
            DemoProfilesScreen(
                onOpenDashboard = { userId ->
                    navController.navigate(Routes.dashboard(userId)) {
                        popUpTo(Routes.DEMO)
                    }
                },
            )
        }
        composable(
            route = Routes.DASHBOARD,
            arguments = listOf(navArgument("userId") { type = NavType.IntType }),
        ) { entry ->
            DashboardScreen(
                userId = entry.arguments?.getInt("userId") ?: 0,
                onOpenAnalytics = { navController.navigate(Routes.analytics(it)) },
                onOpenOffers = { navController.navigate(Routes.offers(it)) },
                onOpenAssistant = { navController.navigate(Routes.assistant(it)) },
                onOpenGamification = { navController.navigate(Routes.gamification(it)) },
            )
        }
        composable(
            route = Routes.ANALYTICS,
            arguments = listOf(navArgument("userId") { type = NavType.IntType }),
        ) { entry ->
            AnalyticsScreen(userId = entry.arguments?.getInt("userId") ?: 0)
        }
        composable(
            route = Routes.OFFERS,
            arguments = listOf(navArgument("userId") { type = NavType.IntType }),
        ) { entry ->
            OffersScreen(userId = entry.arguments?.getInt("userId") ?: 0)
        }
        composable(
            route = Routes.ASSISTANT,
            arguments = listOf(navArgument("userId") { type = NavType.IntType }),
        ) { entry ->
            AssistantScreen(userId = entry.arguments?.getInt("userId") ?: 0)
        }
        composable(
            route = Routes.GAMIFICATION,
            arguments = listOf(navArgument("userId") { type = NavType.IntType }),
        ) { entry ->
            GamificationScreen(userId = entry.arguments?.getInt("userId") ?: 0)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun LoyaltyTopBar(
    title: String,
    userLabel: String,
    segment: String,
    onChangeUser: () -> Unit,
) {
    TopAppBar(
        title = {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = title,
                        color = TWhite,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    if (segment.isNotBlank()) {
                        SegmentBadge(segment = segment)
                    }
                }
                Text(
                    text = userLabel,
                    color = TGray,
                    style = MaterialTheme.typography.bodyMedium,
                )
            }
        },
        actions = {
            TextButton(onClick = onChangeUser) {
                Text(text = "Сменить", color = TYellow, fontWeight = FontWeight.Bold)
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(containerColor = TBlack),
    )
}

private fun titleForRoute(route: String?): String {
    return when {
        route?.startsWith("analytics") == true -> "Аналитика"
        route?.startsWith("offers") == true -> "Офферы"
        route?.startsWith("assistant") == true -> "Ассистент"
        route?.startsWith("gamification") == true -> "Путь выгоды"
        else -> "Моя выгода"
    }
}
