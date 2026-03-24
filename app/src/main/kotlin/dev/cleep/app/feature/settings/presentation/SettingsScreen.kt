package dev.cleep.app.feature.settings.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import dev.cleep.app.R
import dev.cleep.app.core.designsystem.components.CleepPanel
import dev.cleep.app.core.designsystem.components.CleepPrimaryButton
import dev.cleep.app.core.designsystem.components.CleepScreenScaffold
import dev.cleep.app.core.designsystem.components.CleepSectionLabel
import dev.cleep.app.core.designsystem.components.CleepSecondaryButton
import dev.cleep.app.core.designsystem.theme.CleepSpacing
import dev.cleep.app.feature.auth.domain.AuthUser

@Composable
fun SettingsScreen(
    user: AuthUser?,
    onLogoutClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var showLogoutDialog by remember { mutableStateOf(false) }

    CleepScreenScaffold(
        modifier = modifier,
        verticalSpacing = CleepSpacing.space8,
    ) {
        CleepSectionLabel(text = stringResource(R.string.settings_section_title))
        Text(
            text = stringResource(R.string.settings_section_title),
            style = MaterialTheme.typography.headlineLarge,
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
                        text = user?.displayName ?: stringResource(R.string.settings_user_unknown),
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

        SettingsField(
            label = stringResource(R.string.settings_user_label),
            value = user?.displayName ?: stringResource(R.string.settings_user_unknown),
        )

        SettingsField(
            label = stringResource(R.string.settings_email_label),
            value = user?.email ?: stringResource(R.string.settings_email_unknown),
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
private fun SettingsField(
    label: String,
    value: String,
) {
    CleepPanel(
        modifier = Modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surfaceContainerLow,
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(CleepSpacing.space2),
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.primary,
            )
            Text(
                text = value,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface,
            )
        }
    }
}

@Composable
private fun ProfileAvatar(user: AuthUser?) {
    val fallback = (user?.displayName ?: user?.email ?: "?")
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
