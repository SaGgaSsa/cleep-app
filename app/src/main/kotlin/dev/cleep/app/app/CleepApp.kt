package dev.cleep.app.app

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
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
    var warmupAttempt by remember { mutableIntStateOf(0) }
    var warmupState by remember { androidx.compose.runtime.mutableStateOf(BackendWarmupUiState()) }
    val authViewModel: AuthViewModel = viewModel(
        factory = AuthViewModelFactory(appContainer.authRepository),
    )
    val cleepsViewModel: CleepsViewModel = viewModel(
        factory = CleepsViewModelFactory(
            repository = appContainer.cleepsRepository,
            projectsRepository = appContainer.projectsRepository,
        ),
    )
    val authState by authViewModel.state.collectAsStateWithLifecycle()
    val cleepsState by cleepsViewModel.state.collectAsStateWithLifecycle()
    val navController = rememberNavController()
    val scope = rememberCoroutineScope()

    LaunchedEffect(appContainer, warmupAttempt) {
        warmupState = BackendWarmupUiState(isLoading = true)
        warmupState = appContainer.awaitBackendWarmup().fold(
            onSuccess = { BackendWarmupUiState(isLoading = false, isReady = true) },
            onFailure = { error ->
                BackendWarmupUiState(
                    isLoading = false,
                    isReady = false,
                    errorMessage = error.message ?: "Backend warmup failed",
                )
            },
        )
    }

    LaunchedEffect(authState.isAuthenticated, warmupState.isReady) {
        if (warmupState.isReady && authState.isAuthenticated) {
            cleepsViewModel.refresh()
        } else {
            cleepsViewModel.clear()
        }
    }

    CleepTheme {
        if (!warmupState.isReady) {
            BackendWarmupScreen(
                isLoading = warmupState.isLoading,
                errorMessage = warmupState.errorMessage,
                onRetry = { warmupAttempt += 1 },
            )
            return@CleepTheme
        }

        CleepNavHost(
            navController = navController,
            authState = authState,
            cleepsState = cleepsState,
            scope = scope,
            onLoginClick = authViewModel::signIn,
            onLogoutClick = authViewModel::signOut,
            onRefreshCleeps = cleepsViewModel::refresh,
            onCreateCleep = { content -> cleepsViewModel.createCleep(content).map { Unit } },
            onSelectProject = cleepsViewModel::selectProject,
            onDeleteCleep = cleepsViewModel::deleteCleep,
        )
    }
}

private data class BackendWarmupUiState(
    val isLoading: Boolean = true,
    val isReady: Boolean = false,
    val errorMessage: String? = null,
)
