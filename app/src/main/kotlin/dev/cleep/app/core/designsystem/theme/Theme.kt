package dev.cleep.app.core.designsystem.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val CleepColorScheme: ColorScheme = darkColorScheme(
    primary = Primary,
    onPrimary = OnPrimary,
    primaryContainer = PrimaryContainer,
    onPrimaryContainer = OnPrimary,
    secondary = Primary,
    onSecondary = OnPrimary,
    surface = Surface,
    onSurface = OnSurface,
    background = Surface,
    onBackground = OnSurface,
    error = Error,
    onError = Surface,
    outline = Outline,
    outlineVariant = OutlineVariant,
    surfaceContainerLowest = Surface,
    surfaceContainerLow = SurfaceContainerLow,
    surfaceContainer = SurfaceContainer,
    surfaceContainerHigh = SurfaceContainerHigh,
    surfaceContainerHighest = SurfaceContainerHighest,
)

@Composable
fun CleepTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = CleepColorScheme,
        typography = CleepTypography,
        content = content,
    )
}
