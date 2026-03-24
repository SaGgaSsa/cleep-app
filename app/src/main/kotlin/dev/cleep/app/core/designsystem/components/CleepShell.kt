package dev.cleep.app.core.designsystem.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
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
        CleepDestination.Home to "NEW",
        CleepDestination.Feed to "CLEEPS",
        CleepDestination.Settings to "SETTINGS",
    )

    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surfaceContainerLow)
            .padding(
                start = CleepSpacing.space3,
                top = CleepSpacing.space3,
                end = CleepSpacing.space3,
                bottom = CleepSpacing.space4,
            ),
        horizontalArrangement = Arrangement.spacedBy(CleepSpacing.space2),
    ) {
        items.forEach { (destination, label) ->
            val selected = currentDestination.route == destination.route
            Box(
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
                    .padding(
                        top = CleepSpacing.space2,
                        bottom = CleepSpacing.space3,
                    ),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelMedium,
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
