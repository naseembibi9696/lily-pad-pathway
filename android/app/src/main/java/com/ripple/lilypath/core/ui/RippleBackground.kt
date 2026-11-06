package com.ripple.lilypath.core.ui

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun RippleBackground(
    modifier: Modifier = Modifier,
    color: Color,
    rings: Int = 3,
    minRadius: Dp = 40.dp,
    maxRadius: Dp = 170.dp,
    durationMillis: Int = 5200
) {
    val transition = rememberInfiniteTransition(label = "ripples")
    val progress = transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rippleProgress"
    )
    Canvas(modifier = modifier.fillMaxSize()) {
        val minPx = minRadius.toPx()
        val maxPx = maxRadius.toPx()
        val strokePx = 2.dp.toPx()
        for (ring in 0 until rings) {
            val phase = (progress.value + ring.toFloat() / rings) % 1f
            val radius = minPx + (maxPx - minPx) * phase
            val alpha = (1f - phase) * 0.35f
            drawCircle(
                color = color.copy(alpha = alpha),
                radius = radius,
                style = Stroke(width = strokePx)
            )
        }
    }
}
