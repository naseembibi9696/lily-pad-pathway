package com.ripple.lilypath.presentation.dialog

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.ripple.lilypath.R
import com.ripple.lilypath.core.theme.SurfaceWhite
import com.ripple.lilypath.core.theme.TextPrimary
import com.ripple.lilypath.core.theme.TextSecondary
import com.ripple.lilypath.core.ui.PrimaryButton
import com.ripple.lilypath.core.ui.SecondaryButton

@Composable
fun PauseDialog(
    onResume: () -> Unit,
    onRestart: () -> Unit,
    onMenu: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onResume,
        shape = RoundedCornerShape(28.dp),
        containerColor = SurfaceWhite,
        titleContentColor = TextPrimary,
        textContentColor = TextSecondary,
        title = {
            Text(
                text = stringResource(R.string.paused),
                style = MaterialTheme.typography.headlineMedium,
                color = TextPrimary
            )
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                PrimaryButton(
                    text = stringResource(R.string.resume),
                    onClick = onResume,
                    modifier = Modifier.fillMaxWidth(),
                    compact = true
                )
                Spacer(modifier = Modifier.height(10.dp))
                SecondaryButton(
                    text = stringResource(R.string.restart_pond),
                    onClick = onRestart,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            TextButton(onClick = onMenu) {
                Text(
                    text = stringResource(R.string.menu),
                    style = MaterialTheme.typography.titleMedium
                )
            }
        }
    )
}
