package dev.cleep.app.core.designsystem.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ListAlt
import androidx.compose.material.icons.outlined.AddBox
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import dev.cleep.app.app.navigation.CleepDestination
import dev.cleep.app.core.designsystem.theme.CleepSpacing

@Composable
fun CleepBottomBar(
    currentDestination: CleepDestination,
    onNavigate: (CleepDestination) -> Unit,
    modifier: Modifier = Modifier,
) {
    val items = listOf(
        Triple(CleepDestination.Home, "NEW", Icons.Outlined.AddBox),
        Triple(CleepDestination.Feed, "CLEEPS", Icons.AutoMirrored.Outlined.ListAlt),
        Triple(CleepDestination.Settings, "SETTINGS", Icons.Outlined.Settings),
    )

    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surfaceContainerLow)
            .padding(horizontal = CleepSpacing.space3, vertical = CleepSpacing.space3),
        horizontalArrangement = Arrangement.spacedBy(CleepSpacing.space2),
    ) {
        items.forEach { (destination, label, icon) ->
            val selected = currentDestination.route == destination.route
            Column(
                modifier = Modifier
                    .weight(1f)
                    .background(
                        if (selected) {
                            MaterialTheme.colorScheme.surfaceContainerHighest
                        } else {
                            MaterialTheme.colorScheme.surfaceContainerLow
                        },
                    )
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = { onNavigate(destination) },
                    )
                    .padding(vertical = CleepSpacing.space3),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(CleepSpacing.space1),
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = label,
                    tint = if (selected) {
                        MaterialTheme.colorScheme.primary
                    } else {
                        MaterialTheme.colorScheme.onSurfaceVariant
                    },
                )
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelLarge,
                    color = if (selected) {
                        MaterialTheme.colorScheme.primary
                    } else {
                        MaterialTheme.colorScheme.onSurfaceVariant
                    },
                    textAlign = TextAlign.Center,
                )
            }
        }
    }
}

@Composable
fun CleepSnackbarHost(
    hostState: SnackbarHostState,
    modifier: Modifier = Modifier,
) {
    SnackbarHost(
        hostState = hostState,
        modifier = modifier.padding(horizontal = CleepSpacing.space6, vertical = CleepSpacing.space4),
    )
}
