package dev.cleep.app.app

import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import dev.cleep.app.app.navigation.CleepNavHost
import dev.cleep.app.core.designsystem.theme.CleepTheme
import dev.cleep.app.feature.auth.presentation.AuthViewModel
import dev.cleep.app.feature.auth.presentation.AuthViewModelFactory
import dev.cleep.app.feature.cleeps.presentation.CleepsViewModel
import dev.cleep.app.feature.cleeps.presentation.CleepsViewModelFactory

@Composable
fun CleepApp() {
    val appContext = LocalContext.current.applicationContext
    val appContainer = remember(appContext) { AppContainer(appContext) }
    val authViewModel: AuthViewModel = viewModel(
        factory = AuthViewModelFactory(appContainer.authRepository),
    )
    val cleepsViewModel: CleepsViewModel = viewModel(
        factory = CleepsViewModelFactory(appContainer.cleepsRepository),
    )
    val authState by authViewModel.state.collectAsStateWithLifecycle()
    val cleepsState by cleepsViewModel.state.collectAsStateWithLifecycle()
    val navController = rememberNavController()
    val scope = rememberCoroutineScope()

    LaunchedEffect(authState.isAuthenticated) {
        if (authState.isAuthenticated) {
            cleepsViewModel.refresh()
        } else {
            cleepsViewModel.clear()
        }
    }

    CleepTheme {
        CleepNavHost(
            navController = navController,
            authState = authState,
            cleepsState = cleepsState,
            scope = scope,
            onLoginClick = authViewModel::signIn,
            onLogoutClick = authViewModel::signOut,
            onRefreshCleeps = cleepsViewModel::refresh,
            onCreateCleep = { content ->
                cleepsViewModel.createCleep(content).map { Unit }
            },
            onDeleteCleep = cleepsViewModel::deleteCleep,
        )
    }
}
