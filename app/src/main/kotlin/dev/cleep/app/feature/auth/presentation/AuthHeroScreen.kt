package dev.cleep.app.feature.auth.presentation

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.cleep.app.core.designsystem.theme.CleepSpacing

@Composable
fun AuthHeroScreen(
    statusText: String,
    taglineText: String,
    primaryActionLabel: String? = null,
    onPrimaryAction: (() -> Unit)? = null,
    primaryActionEnabled: Boolean = true,
    primaryActionLoading: Boolean = false,
    secondaryActionLabel: String? = null,
    onSecondaryAction: (() -> Unit)? = null,
    secondaryActionEnabled: Boolean = true,
    loading: Boolean = false,
    errorText: String? = null,
    footerContent: @Composable (() -> Unit)? = null,
) {
    BoxWithConstraints(
        modifier = Modifier.fillMaxSize(),
    ) {
        val contentWidth = maxWidth.coerceAtMost(420.dp)
        val titleSize = if (maxWidth < 360.dp) 72.sp else 96.sp

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = CleepSpacing.space6, vertical = 28.dp),
        ) {
            Column(
                modifier = Modifier
                    .align(Alignment.Center)
                    .widthIn(max = contentWidth),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    text = statusText,
                    style = MaterialTheme.typography.labelLarge.copy(
                        letterSpacing = 3.sp,
                        fontWeight = FontWeight.Bold,
                    ),
                    color = MaterialTheme.colorScheme.primary,
                    textAlign = TextAlign.Center,
                )
                Spacer(modifier = Modifier.height(30.dp))
                Text(
                    text = "CLEEP",
                    color = Color.White,
                    fontSize = titleSize,
                    lineHeight = titleSize,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = (-3).sp,
                    textAlign = TextAlign.Center,
                )
                Spacer(modifier = Modifier.height(28.dp))
                Text(
                    text = taglineText,
                    style = MaterialTheme.typography.labelLarge.copy(letterSpacing = 4.sp),
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.78f),
                    textAlign = TextAlign.Center,
                )
                Spacer(modifier = Modifier.height(64.dp))

                if (primaryActionLabel != null && onPrimaryAction != null) {
                    HeroPrimaryButton(
                        text = primaryActionLabel,
                        enabled = primaryActionEnabled,
                        loading = primaryActionLoading,
                        onClick = onPrimaryAction,
                    )
                }

                if (secondaryActionLabel != null && onSecondaryAction != null) {
                    Spacer(modifier = Modifier.height(CleepSpacing.space5))
                    Text(
                        text = "OR",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    Spacer(modifier = Modifier.height(CleepSpacing.space5))
                    HeroSecondaryButton(
                        text = secondaryActionLabel,
                        enabled = secondaryActionEnabled,
                        onClick = onSecondaryAction,
                    )
                }

                if (loading) {
                    Spacer(modifier = Modifier.height(CleepSpacing.space6))
                    CircularProgressIndicator(
                        modifier = Modifier.size(28.dp),
                        color = MaterialTheme.colorScheme.primary,
                        strokeWidth = 2.5.dp,
                    )
                }

                if (!errorText.isNullOrBlank()) {
                    Spacer(modifier = Modifier.height(CleepSpacing.space6))
                    Text(
                        text = errorText,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.error,
                        textAlign = TextAlign.Center,
                    )
                }

                footerContent?.let {
                    Spacer(modifier = Modifier.height(CleepSpacing.space6))
                    it()
                }
            }
        }
    }
}

@Composable
private fun HeroPrimaryButton(
    text: String,
    enabled: Boolean,
    loading: Boolean,
    onClick: () -> Unit,
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(52.dp),
        color = MaterialTheme.colorScheme.primaryContainer,
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clickable(
                    enabled = enabled && !loading,
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = onClick,
                )
                .padding(horizontal = CleepSpacing.space4),
            contentAlignment = Alignment.Center,
        ) {
            if (loading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    color = MaterialTheme.colorScheme.onPrimary,
                    strokeWidth = 2.dp,
                )
            } else {
                Text(
                    text = text,
                    style = MaterialTheme.typography.labelLarge.copy(letterSpacing = 2.sp),
                    color = MaterialTheme.colorScheme.onPrimary,
                    textAlign = TextAlign.Center,
                )
            }
        }
    }
}

@Composable
private fun HeroSecondaryButton(
    text: String,
    enabled: Boolean,
    onClick: () -> Unit,
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(52.dp),
        color = Color.Transparent,
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.7f),
        ),
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clickable(
                    enabled = enabled,
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = onClick,
                )
                .padding(horizontal = CleepSpacing.space4),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = "${text}_",
                style = MaterialTheme.typography.labelLarge.copy(letterSpacing = 2.sp),
                color = if (enabled) {
                    MaterialTheme.colorScheme.onSurface
                } else {
                    MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                },
                textAlign = TextAlign.Center,
            )
        }
    }
}
