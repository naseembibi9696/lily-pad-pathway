package com.ripple.lilypath.presentation.dialog

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.unit.dp
import com.ripple.lilypath.R
import androidx.compose.ui.text.style.TextAlign
import com.ripple.lilypath.core.theme.LotusAccentDeep
import com.ripple.lilypath.core.theme.PondPrimary
import com.ripple.lilypath.core.theme.PondSecondary
import com.ripple.lilypath.core.theme.SurfaceAlt
import com.ripple.lilypath.core.theme.SurfaceBorder
import com.ripple.lilypath.core.theme.SurfaceWhite
import com.ripple.lilypath.core.theme.TextPrimary
import com.ripple.lilypath.core.theme.TextSecondary
import com.ripple.lilypath.domain.model.HintFrequency

@Composable
fun SettingsDialog(
    volume: Int,
    haptics: Boolean,
    hintFrequency: HintFrequency,
    resetConfirmVisible: Boolean,
    onVolumeChange: (Int) -> Unit,
    onHapticsChange: (Boolean) -> Unit,
    onHintFrequencyChange: (HintFrequency) -> Unit,
    onAskReset: () -> Unit,
    onCancelReset: () -> Unit,
    onConfirmReset: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(28.dp),
        containerColor = SurfaceWhite,
        titleContentColor = TextPrimary,
        textContentColor = TextSecondary,
        title = {
            Text(
                text = stringResource(R.string.settings),
                style = MaterialTheme.typography.headlineMedium,
                color = TextPrimary
            )
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = stringResource(R.string.volume_label, volume),
                    style = MaterialTheme.typography.labelSmall,
                    color = TextSecondary
                )
                Slider(
                    value = volume.toFloat(),
                    onValueChange = { onVolumeChange(it.toInt()) },
                    valueRange = 0f..100f,
                    modifier = Modifier
                        .fillMaxWidth()
                        .semantics { stateDescription = volume.toString() }
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(R.string.haptics),
                        style = MaterialTheme.typography.labelSmall,
                        color = TextSecondary
                    )
                    Switch(
                        checked = haptics,
                        onCheckedChange = onHapticsChange
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = stringResource(R.string.hints),
                    style = MaterialTheme.typography.labelSmall,
                    color = TextSecondary
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    HintFrequency.entries.forEach { option ->
                        HintOption(
                            label = stringResource(
                                when (option) {
                                    HintFrequency.OFTEN -> R.string.hint_often
                                    HintFrequency.SOMETIMES -> R.string.hint_sometimes
                                    HintFrequency.NEVER -> R.string.hint_never
                                }
                            ),
                            selected = option == hintFrequency,
                            onClick = { onHintFrequencyChange(option) },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
                TextButton(onClick = onAskReset) {
                    Text(
                        text = stringResource(R.string.reset_progress),
                        style = MaterialTheme.typography.titleMedium,
                        color = LotusAccentDeep
                    )
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text(
                    text = stringResource(R.string.got_it),
                    style = MaterialTheme.typography.titleMedium
                )
            }
        }
    )

    if (resetConfirmVisible) {
        AlertDialog(
            onDismissRequest = onCancelReset,
            shape = RoundedCornerShape(28.dp),
            containerColor = SurfaceWhite,
            titleContentColor = TextPrimary,
            textContentColor = TextSecondary,
            title = {
                Text(
                    text = stringResource(R.string.reset_progress),
                    style = MaterialTheme.typography.headlineMedium,
                    color = TextPrimary
                )
            },
            text = {
                Text(
                    text = stringResource(R.string.reset_progress_question),
                    style = MaterialTheme.typography.bodyLarge,
                    color = TextSecondary
                )
            },
            confirmButton = {
                TextButton(onClick = onConfirmReset) {
                    Text(
                        text = stringResource(R.string.reset),
                        style = MaterialTheme.typography.titleMedium,
                        color = LotusAccentDeep
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = onCancelReset) {
                    Text(
                        text = stringResource(R.string.cancel),
                        style = MaterialTheme.typography.titleMedium
                    )
                }
            }
        )
    }
}

@Composable
private fun HintOption(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .heightIn(min = 48.dp)
            .clip(RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
            .semantics { stateDescription = label },
        shape = RoundedCornerShape(16.dp),
        color = if (selected) PondPrimary.copy(alpha = 0.14f) else SurfaceAlt,
        contentColor = if (selected) TextPrimary else TextSecondary,
        border = BorderStroke(
            width = if (selected) 1.5.dp else 1.dp,
            color = if (selected) PondSecondary else SurfaceBorder
        )
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 4.dp, vertical = 14.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                textAlign = TextAlign.Center,
                maxLines = 1
            )
        }
    }
}
