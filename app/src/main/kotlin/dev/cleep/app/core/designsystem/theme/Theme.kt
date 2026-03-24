package dev.cleep.app.core.designsystem.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.unit.dp

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
    surfaceBright = SurfaceContainerHighest,
    surfaceDim = Surface,
)

private val CleepShapes = Shapes(
    extraSmall = RoundedCornerShape(0.dp),
    small = RoundedCornerShape(0.dp),
    medium = RoundedCornerShape(0.dp),
    large = RoundedCornerShape(0.dp),
    extraLarge = RoundedCornerShape(0.dp),
)

@Composable
fun CleepTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = CleepColorScheme,
        typography = CleepTypography,
        shapes = CleepShapes,
        content = content,
    )
}
