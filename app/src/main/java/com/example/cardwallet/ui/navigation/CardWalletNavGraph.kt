package com.example.cardwallet.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.cardwallet.CardWalletApplication
import com.example.cardwallet.ui.about.AboutScreen
import com.example.cardwallet.ui.about.LicenseScreen
import com.example.cardwallet.ui.detail.CardDetailScreen
import com.example.cardwallet.ui.edit.EditCardScreen
import com.example.cardwallet.ui.home.HomeScreen
import com.example.cardwallet.ui.pass.PassDetailScreen
import com.example.cardwallet.ui.payment.TapToPayScreen
import com.example.cardwallet.ui.settings.SettingsScreen
import com.example.cardwallet.ui.sort.SortCardsScreen

@Composable
fun CardWalletNavGraph(navController: NavHostController = rememberNavController()) {
    val context = LocalContext.current
    val settings = (context.applicationContext as CardWalletApplication).settingsRepository
    val transitionKey by settings.transition.collectAsStateWithLifecycle(initialValue = "fade")
    val ts = navTransitionSet(transitionKey)

    NavHost(
        navController = navController,
        startDestination = "home",
        enterTransition = ts.enter,
        exitTransition = ts.exit,
        popEnterTransition = ts.popEnter,
        popExitTransition = ts.popExit,
    ) {
        composable("home") {
            HomeScreen(
                onAddCard = { navController.navigate("edit") },
                onCardClick = { id -> navController.navigate("detail/$id") },
                onPassClick = { id -> navController.navigate("pass/$id") },
                onOpenSettings = { navController.navigate("settings") }
            )
        }

        composable("settings") {
            SettingsScreen(
                onBack = { navController.popBackStack() },
                onOpenTapToPay = { navController.navigate("tappay") },
                onSortCards = { navController.navigate("sort") },
                onAbout = { navController.navigate("about") },
                onOpenLicenses = { navController.navigate("licenses") }
            )
        }

        composable("about") {
            AboutScreen(onBack = { navController.popBackStack() })
        }

        composable("licenses") {
            LicenseScreen(onBack = { navController.popBackStack() })
        }

        composable("sort") {
            SortCardsScreen(onBack = { navController.popBackStack() })
        }

        composable("tappay") {
            TapToPayScreen(onClose = { navController.popBackStack() })
        }

        composable(
            route = "pass/{passId}",
            arguments = listOf(navArgument("passId") { type = NavType.LongType })
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getLong("passId") ?: -1L
            PassDetailScreen(passId = id, onBack = { navController.popBackStack() })
        }

        composable(
            route = "detail/{cardId}",
            arguments = listOf(navArgument("cardId") { type = NavType.LongType })
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getLong("cardId") ?: -1L
            CardDetailScreen(
                cardId = id,
                onBack = { navController.popBackStack() },
                onEdit = { navController.navigate("edit?cardId=$id") }
            )
        }

        composable(
            route = "edit?cardId={cardId}",
            arguments = listOf(
                navArgument("cardId") {
                    type = NavType.LongType
                    defaultValue = -1L
                }
            )
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getLong("cardId") ?: -1L
            EditCardScreen(
                cardId = id,
                onDone = { navController.popBackStack() },
                onBack = { navController.popBackStack() }
            )
        }
    }
}