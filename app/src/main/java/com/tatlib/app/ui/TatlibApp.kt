package com.tatlib.app.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.tatlib.app.ui.auth.LoginScreen
import com.tatlib.app.ui.auth.RegisterScreen
import com.tatlib.app.ui.book.BookDetailScreen
import com.tatlib.app.ui.book.BookReaderScreen
import com.tatlib.app.ui.book.RecapScreen
import com.tatlib.app.ui.components.BottomNav
import com.tatlib.app.ui.components.MiniReadingBar
import com.tatlib.app.ui.home.LibraryScreen
import com.tatlib.app.ui.leveltest.LevelIntroScreen
import com.tatlib.app.ui.leveltest.LevelQuizScreen
import com.tatlib.app.ui.leveltest.LevelResultScreen
import com.tatlib.app.ui.navigation.Routes
import com.tatlib.app.ui.onboarding.AuthChoiceScreen
import com.tatlib.app.ui.onboarding.SplashScreen
import com.tatlib.app.ui.onboarding.WelcomeScreen
import com.tatlib.app.ui.profile.ProfileScreen
import com.tatlib.app.ui.progress.ProgressScreen
import com.tatlib.app.ui.scanner.ScannerScreen
import com.tatlib.app.ui.search.SearchScreen

@Composable
fun TatlibApp() {
    val navController = rememberNavController()
    val appViewModel: AppViewModel = viewModel()

    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route
    val showBottomBar = currentRoute in Routes.bottomBarRoutes

    // Мини-бар «Хәзер укыла»: книга с прогрессом, иначе первая (00-ux-map п. 7).
    val books by appViewModel.books.collectAsState()
    val currentBook = books.firstOrNull { it.progress > 0f } ?: books.firstOrNull()
    val showMiniBar = currentRoute in Routes.miniBarRoutes && currentBook != null

    // Экраны рисуются edge-to-edge (фото под статус-баром) и сами берут statusBarsPadding();
    // системные отступы снизу здесь получает только нижняя навигация.
    Scaffold(
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        bottomBar = {
            if (showBottomBar) {
                Column(modifier = Modifier.navigationBarsPadding()) {
                    if (showMiniBar && currentBook != null) {
                        MiniReadingBar(
                            book = currentBook,
                            onOpen = { navController.navigate(Routes.bookReader(currentBook.id)) },
                            modifier = Modifier.padding(start = 12.dp, end = 12.dp, bottom = 8.dp)
                        )
                    }
                    BottomNav(
                        selectedRoute = currentRoute.orEmpty(),
                        onSelect = { route ->
                            if (route != currentRoute) {
                                navController.navigate(route) {
                                    popUpTo(Routes.LIBRARY) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        }
                    )
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
                SearchScreen(navController = navController, viewModel = appViewModel)
            }
            composable(Routes.PROGRESS) {
                ProgressScreen(navController = navController, viewModel = appViewModel)
            }
            composable(Routes.PROFILE) {
                ProfileScreen(navController = navController, viewModel = appViewModel)
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
                RecapScreen(navController = navController, viewModel = appViewModel, bookId = bookId)
            }
        }
    }
}
