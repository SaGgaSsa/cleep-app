package dev.cleep.app.feature.settings.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.*
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import dev.cleep.app.R
import dev.cleep.app.core.designsystem.components.CleepPanel
import dev.cleep.app.core.designsystem.components.CleepPrimaryButton
import dev.cleep.app.core.designsystem.components.CleepScreenScaffold
import dev.cleep.app.core.designsystem.components.CleepSecondaryButton
import dev.cleep.app.core.designsystem.theme.CleepSpacing
import dev.cleep.app.feature.auth.domain.AuthUser

@Composable
fun SettingsScreen(
    user: AuthUser?,
    state: SettingsUiState,
    onRefreshUsage: suspend () -> Unit,
    onLogoutClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var showLogoutDialog by remember { mutableStateOf(false) }
    val uriHandler = LocalUriHandler.current
    val documentationUrl = stringResource(R.string.settings_documentation_url)

    LaunchedEffect(Unit) {
        onRefreshUsage()
    }

    CleepScreenScaffold(
        modifier = modifier,
        verticalSpacing = CleepSpacing.space8,
    ) {
        Text(
            text = stringResource(R.string.settings_section_title),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface,
        )

        CleepPanel(
            modifier = Modifier.fillMaxWidth(),
            color = MaterialTheme.colorScheme.surfaceContainer,
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(CleepSpacing.space4),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                ProfileAvatar(user = user)
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(CleepSpacing.space2),
                ) {
                    Text(
                        text = user.displayNameForSettings()
                            ?: stringResource(R.string.settings_user_unknown),
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                    Text(
                        text = user?.email ?: stringResource(R.string.settings_email_unknown),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
            }
        }

        CleepPanel(
            modifier = Modifier.fillMaxWidth(),
            color = MaterialTheme.colorScheme.surfaceContainer,
        ) {
            if (state.isLoading && state.usage == null) {
                Text(
                    text = stringResource(R.string.settings_usage_loading),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            } else {
                state.usage?.let { usage ->
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(CleepSpacing.space3),
                    ) {
                        UsageRow(
                            label = stringResource(R.string.settings_usage_active_cleeps),
                            value = usage.activeCleepsValue(),
                        )
                        UsageRow(
                            label = stringResource(R.string.settings_usage_cleeps_today),
                            value = usage.dailyCleepsValue(),
                        )
                        UsageRow(
                            label = stringResource(R.string.settings_usage_active_projects),
                            value = usage.activeProjectsValue(),
                        )
                        UsageRow(
                            label = stringResource(R.string.settings_usage_archived_history),
                            value = usage.archivedHistoryValue(),
                        )
                    }
                }
            }
        }

        state.errorMessage?.let { errorMessage ->
            Text(
                text = stringResource(R.string.settings_usage_error, errorMessage),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.error,
            )
        }

        CleepSecondaryButton(
            text = stringResource(R.string.settings_documentation_cta),
            onClick = {
                uriHandler.openUri(documentationUrl)
            },
        )

        CleepPrimaryButton(
            text = stringResource(R.string.settings_sign_out),
            onClick = { showLogoutDialog = true },
        )
    }

    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            title = { Text(stringResource(R.string.settings_sign_out_title)) },
            text = { Text(stringResource(R.string.settings_sign_out_confirmation)) },
            confirmButton = {
                CleepPrimaryButton(
                    text = stringResource(R.string.settings_sign_out),
                    onClick = {
                        showLogoutDialog = false
                        onLogoutClick()
                    },
                )
            },
            dismissButton = {
                CleepSecondaryButton(
                    text = stringResource(R.string.common_cancel),
                    onClick = { showLogoutDialog = false },
                )
            },
        )
    }
}

@Composable
private fun UsageRow(
    label: String,
    value: String,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface,
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.End,
            modifier = Modifier.widthIn(min = 72.dp),
        )
    }
}

@Composable
private fun ProfileAvatar(user: AuthUser?) {
    val fallback = (user.displayNameForSettings() ?: user?.email ?: "?")
        .trim()
        .firstOrNull()
        ?.uppercase()
        ?: "?"

    if (!user?.photoUrl.isNullOrBlank()) {
        AsyncImage(
            model = user?.photoUrl,
            contentDescription = stringResource(R.string.settings_avatar_description),
            modifier = Modifier
                .size(64.dp)
                .background(MaterialTheme.colorScheme.surfaceContainerHighest),
            contentScale = ContentScale.Crop,
        )
    } else {
        Box(
            modifier = Modifier
                .size(64.dp)
                .background(MaterialTheme.colorScheme.primaryContainer),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = fallback,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onPrimary,
            )
        }
    }
}

private fun AuthUser?.displayNameForSettings(): String? {
    val displayName = this?.displayName?.trim()
    if (!displayName.isNullOrEmpty()) {
        return displayName
    }

    val emailName = this?.email
        ?.substringBefore('@')
        ?.replace('.', ' ')
        ?.replace('_', ' ')
        ?.trim()
        ?.takeIf { it.isNotEmpty() }

    return emailName
}

private fun dev.cleep.app.feature.settings.domain.SettingsUsage.activeCleepsValue(): String =
    "${activeCleepsUsed.toAlignedCounter()}/${activeCleepsLimit}"

private fun dev.cleep.app.feature.settings.domain.SettingsUsage.dailyCleepsValue(): String =
    "${dailyCleepsUsed.toAlignedCounter()}/${dailyCleepsLimit}"

private fun dev.cleep.app.feature.settings.domain.SettingsUsage.activeProjectsValue(): String =
    "${activeProjectsUsed.toAlignedCounter()}/${activeProjectsLimit?.toString() ?: "\u221e"}"

private fun dev.cleep.app.feature.settings.domain.SettingsUsage.archivedHistoryValue(): String =
    "${historyUsed.toAlignedCounter()}/${historyLimit}"

private fun Int.toAlignedCounter(): String = toString().padStart(2, '0')
