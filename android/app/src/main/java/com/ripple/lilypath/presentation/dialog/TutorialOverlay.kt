package com.ripple.lilypath.presentation.dialog

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.ripple.lilypath.R
import com.ripple.lilypath.core.theme.TextPrimary
import com.ripple.lilypath.core.ui.GlassCard

@Composable
fun TutorialOverlay(onDismiss: () -> Unit) {
    val transition = rememberInfiniteTransition(label = "tutorial")
    val handDrift by transition.animateFloat(
        initialValue = -30f,
        targetValue = 30f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "handDrift"
    )
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0x6616423C)),
        contentAlignment = Alignment.Center
    ) {
        GlassCard(
            modifier = Modifier
                .safeDrawingPadding()
                .padding(horizontal = 36.dp)
        ) {
            Image(
                painter = painterResource(R.drawable.sprite_5),
                contentDescription = null,
                modifier = Modifier
                    .size(72.dp)
                    .offset(x = handDrift.dp),
                contentScale = ContentScale.Fit
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = stringResource(R.string.drag_hint),
                style = MaterialTheme.typography.titleMedium,
                color = TextPrimary,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(8.dp))
            TextButton(onClick = onDismiss) {
                Text(
                    text = stringResource(R.string.got_it),
                    style = MaterialTheme.typography.titleMedium
                )
            }
        }
    }
}
