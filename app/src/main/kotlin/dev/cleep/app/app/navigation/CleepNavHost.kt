package dev.cleep.app.app.navigation

import android.app.Activity
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import dev.cleep.app.core.designsystem.components.CleepBottomBar
import dev.cleep.app.core.designsystem.components.CleepSnackbarHost
import dev.cleep.app.feature.auth.presentation.AuthUiState
import dev.cleep.app.feature.auth.presentation.LoginScreen
import dev.cleep.app.feature.cleeps.presentation.CleepsListScreen
import dev.cleep.app.feature.cleeps.presentation.CleepsUiState
import dev.cleep.app.feature.cleeps.presentation.HomeScreen
import dev.cleep.app.feature.settings.presentation.SettingsScreen
import kotlinx.coroutines.CoroutineScope

@Composable
fun CleepNavHost(
    navController: NavHostController = rememberNavController(),
    authState: AuthUiState,
    cleepsState: CleepsUiState,
    scope: CoroutineScope,
    onLoginClick: (Activity) -> Unit,
    onLogoutClick: () -> Unit,
    onRefreshCleeps: suspend () -> Unit,
    onCreateCleep: suspend (String) -> Result<Unit>,
    onSelectProject: (String?) -> Unit,
    onDeleteCleep: suspend (String) -> Result<Unit>,
) {
    LaunchedEffect(authState.isAuthenticated) {
        val target = if (authState.isAuthenticated) CleepDestination.Home.route else CleepDestination.Login.route
        navController.navigate(target) {
            popUpTo(navController.graph.findStartDestination().id) {
                inclusive = true
            }
            launchSingleTop = true
        }
    }

    NavHost(
        navController = navController,
        startDestination = CleepDestination.Login.route,
    ) {
        composable(CleepDestination.Login.route) {
            LoginScreen(
                state = authState,
                onContinueClick = onLoginClick,
            )
        }

        composable(CleepDestination.Home.route) {
            AuthenticatedScaffold(navController = navController) { innerModifier, snackbarHostState ->
                HomeScreen(
                    modifier = innerModifier,
                    state = cleepsState,
                    snackbarHostState = snackbarHostState,
                    scope = scope,
                    onCreateCleep = onCreateCleep,
                    onSelectProject = onSelectProject,
                )
            }
        }

        composable(CleepDestination.Feed.route) {
            AuthenticatedScaffold(navController = navController) { innerModifier, snackbarHostState ->
                CleepsListScreen(
                    modifier = innerModifier,
                    state = cleepsState,
                    snackbarHostState = snackbarHostState,
                    scope = scope,
                    onRefresh = onRefreshCleeps,
                    onDelete = onDeleteCleep,
                )
            }
        }

        composable(CleepDestination.Settings.route) {
            AuthenticatedScaffold(navController = navController) { innerModifier, _ ->
                SettingsScreen(
                    user = authState.user,
                    onLogoutClick = onLogoutClick,
                    modifier = innerModifier,
                )
            }
        }
    }
}

@Composable
private fun AuthenticatedScaffold(
    navController: NavHostController,
    content: @Composable (Modifier, SnackbarHostState) -> Unit,
) {
    val currentBackStack = navController.currentBackStackEntryAsState().value
    val currentRoute = currentBackStack?.destination?.route
    val snackbarHostState = remember { SnackbarHostState() }
    val selectedDestination = when (currentRoute) {
        CleepDestination.Feed.route -> CleepDestination.Feed
        CleepDestination.Settings.route -> CleepDestination.Settings
        else -> CleepDestination.Home
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.surface,
        snackbarHost = { CleepSnackbarHost(hostState = snackbarHostState) },
        bottomBar = {
            CleepBottomBar(
                currentDestination = selectedDestination,
                onNavigate = { destination ->
                    navController.navigate(destination.route) {
                        popUpTo(CleepDestination.Home.route) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
            )
        },
    ) { innerPadding ->
        content(Modifier.padding(innerPadding), snackbarHostState)
    }
}
