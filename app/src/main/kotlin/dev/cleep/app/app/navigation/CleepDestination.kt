package dev.cleep.app.app.navigation

sealed class CleepDestination(val route: String) {
    data object Login : CleepDestination("login")
    data object Home : CleepDestination("home")
    data object Feed : CleepDestination("feed")
    data object Projects : CleepDestination("projects")
    data object Settings : CleepDestination("settings")
}
