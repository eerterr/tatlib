package com.tatlib.app.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoStories
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.tatlib.app.ui.auth.LoginScreen
import com.tatlib.app.ui.auth.RegisterScreen
import com.tatlib.app.ui.book.BookDetailScreen
import com.tatlib.app.ui.book.BookReaderScreen
import com.tatlib.app.ui.book.RecapScreen
import com.tatlib.app.ui.home.LibraryScreen
import com.tatlib.app.ui.leveltest.LevelIntroScreen
import com.tatlib.app.ui.leveltest.LevelQuizScreen
import com.tatlib.app.ui.leveltest.LevelResultScreen
import com.tatlib.app.ui.navigation.Routes
import com.tatlib.app.ui.onboarding.AuthChoiceScreen
import com.tatlib.app.ui.onboarding.SplashScreen
import com.tatlib.app.ui.onboarding.WelcomeScreen
import com.tatlib.app.ui.progress.ProgressScreen
import com.tatlib.app.ui.scanner.ScannerScreen
import com.tatlib.app.ui.search.SearchScreen

private data class BottomNavItem(
    val route: String,
    val label: String,
    val icon: ImageVector
)

private val bottomNavItems = listOf(
    BottomNavItem(Routes.LIBRARY, "Китапханә", Icons.Filled.AutoStories),
    BottomNavItem(Routes.SEARCH, "Эзләү", Icons.Filled.Search),
    BottomNavItem(Routes.SCANNER, "Скан", Icons.Filled.PhotoCamera),
    BottomNavItem(Routes.PROGRESS, "Алгарыш", Icons.Filled.BarChart)
)

@Composable
fun TatlibApp() {
    val navController = rememberNavController()
    val appViewModel: AppViewModel = viewModel()

    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route
    val showBottomBar = Routes.bottomBarRoutes.any { it == currentRoute }

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                NavigationBar {
                    bottomNavItems.forEach { item ->
                        val selected = backStackEntry?.destination?.hierarchy
                            ?.any { it.route == item.route } == true
                        NavigationBarItem(
                            selected = selected,
                            onClick = {
                                if (!selected) {
                                    navController.navigate(item.route) {
                                        popUpTo(navController.graph.findStartDestination().id) {
                                            saveState = true
                                        }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                }
                            },
                            icon = { Icon(item.icon, contentDescription = item.label) },
                            label = { Text(item.label) }
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Routes.SPLASH,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Routes.SPLASH) {
                SplashScreen(navController = navController, viewModel = appViewModel)
            }
            composable(Routes.ONBOARDING_WELCOME) {
                WelcomeScreen(navController = navController, viewModel = appViewModel)
            }
            composable(Routes.AUTH_CHOICE) {
                AuthChoiceScreen(navController = navController)
            }
            composable(Routes.REGISTER) {
                RegisterScreen(navController = navController, viewModel = appViewModel)
            }
            composable(Routes.LOGIN) {
                LoginScreen(navController = navController, viewModel = appViewModel)
            }
            composable(Routes.LEVEL_INTRO) {
                LevelIntroScreen(navController = navController)
            }
            composable(Routes.LEVEL_QUIZ) {
                LevelQuizScreen(navController = navController, viewModel = appViewModel)
            }
            composable(Routes.LEVEL_RESULT) {
                LevelResultScreen(navController = navController, viewModel = appViewModel)
            }
            composable(Routes.LIBRARY) {
                LibraryScreen(navController = navController, viewModel = appViewModel)
            }
            composable(Routes.SEARCH) {
                SearchScreen(navController = navController)
            }
            composable(Routes.PROGRESS) {
                ProgressScreen(navController = navController, viewModel = appViewModel)
            }
            composable(Routes.SCANNER) {
                ScannerScreen(navController = navController)
            }
            composable(Routes.BOOK_DETAIL) { backStack ->
                val bookId = backStack.arguments?.getString("bookId").orEmpty()
                BookDetailScreen(navController = navController, viewModel = appViewModel, bookId = bookId)
            }
            composable(Routes.BOOK_READER) { backStack ->
                val bookId = backStack.arguments?.getString("bookId").orEmpty()
                BookReaderScreen(navController = navController, viewModel = appViewModel, bookId = bookId)
            }
            composable(Routes.RECAP) { backStack ->
                val bookId = backStack.arguments?.getString("bookId").orEmpty()
                RecapScreen(navController = navController, bookId = bookId)
            }
        }
    }
}
