package com.pocketstudios.app.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.navigation.navDeepLink
import com.pocketstudios.feature.editor.EditorScreen
import com.pocketstudios.feature.gallery.GalleryScreen
import com.pocketstudios.feature.onboarding.SplashScreen
import com.pocketstudios.feature.onboarding.OnboardingScreen
import com.pocketstudios.feature.settings.SettingsScreen
import com.pocketstudios.feature.settings.ProUpgradeScreen

sealed class Screen(val route: String) {
    object Splash : Screen("splash")
    object Onboarding : Screen("onboarding")
    object Gallery : Screen("gallery")
    object Editor : Screen("editor/{projectId}") {
        fun createRoute(projectId: String) = "editor/$projectId"
    }
    object Export : Screen("export/{projectId}") {
        fun createRoute(projectId: String) = "export/$projectId"
    }
    object EffectsStore : Screen("effects_store")
    object Settings : Screen("settings")
    object ProUpgrade : Screen("pro_upgrade")
}

@Composable
fun PocketStudiosNavHost(
    navController: NavHostController = rememberNavController(),
    startDestination: String = Screen.Splash.route
) {
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(Screen.Splash.route) {
            SplashScreen(
                onSplashComplete = { isFirstLaunch ->
                    if (isFirstLaunch) {
                        navController.navigate(Screen.Onboarding.route) {
                            popUpTo(Screen.Splash.route) { inclusive = true }
                        }
                    } else {
                        navController.navigate(Screen.Gallery.route) {
                            popUpTo(Screen.Splash.route) { inclusive = true }
                        }
                    }
                }
            )
        }

        composable(Screen.Onboarding.route) {
            OnboardingScreen(
                onComplete = {
                    navController.navigate(Screen.Gallery.route) {
                        popUpTo(Screen.Onboarding.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.Gallery.route) {
            GalleryScreen(
                onProjectClick = { projectId ->
                    navController.navigate(Screen.Editor.createRoute(projectId))
                },
                onSettingsClick = {
                    navController.navigate(Screen.Settings.route)
                }
            )
        }

        composable(
            route = Screen.Editor.route,
            arguments = listOf(navArgument("projectId") { type = NavType.StringType }),
            deepLinks = listOf(
                navDeepLink { uriPattern = "pocketstudios://editor/{projectId}" },
                navDeepLink { uriPattern = "https://pocketstudios.app/open/{projectId}" }
            )
        ) { backStackEntry ->
            val projectId = backStackEntry.arguments?.getString("projectId") ?: return@composable
            EditorScreen(
                projectId = projectId,
                onBack = { navController.popBackStack() },
                onExport = { navController.navigate(Screen.Export.createRoute(projectId)) },
                onProUpgrade = { navController.navigate(Screen.ProUpgrade.route) }
            )
        }

        composable(Screen.Settings.route) {
            SettingsScreen(
                onBack = { navController.popBackStack() },
                onProUpgrade = { navController.navigate(Screen.ProUpgrade.route) }
            )
        }

        composable(Screen.ProUpgrade.route) {
            ProUpgradeScreen(onBack = { navController.popBackStack() })
        }
    }
}
