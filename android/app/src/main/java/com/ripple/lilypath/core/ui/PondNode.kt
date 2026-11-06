package com.ripple.lilypath.core.ui

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.unit.dp
import com.ripple.lilypath.core.theme.LotusAccent
import com.ripple.lilypath.core.theme.PondPrimary
import com.ripple.lilypath.core.theme.SurfaceBorder
import com.ripple.lilypath.core.theme.TextMuted
import com.ripple.lilypath.core.theme.TextPrimary
import com.ripple.lilypath.domain.model.PondNodeKind

@Composable
fun PondNode(
    label: String,
    kind: PondNodeKind,
    state: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val transition = rememberInfiniteTransition(label = "pondNode")
    val pulse by transition.animateFloat(
        initialValue = 1f,
        targetValue = if (kind == PondNodeKind.CURRENT) 1.08f else 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1400),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pondNodePulse"
    )
    val fill = when (kind) {
        PondNodeKind.SOLVED -> PondPrimary
        PondNodeKind.CURRENT -> LotusAccent
        PondNodeKind.LOCKED -> Color(0xFFE6F2EF)
    }
    val textColor = when (kind) {
        PondNodeKind.SOLVED -> Color.White
        PondNodeKind.CURRENT -> TextPrimary
        PondNodeKind.LOCKED -> TextMuted
    }
    val border = when (kind) {
        PondNodeKind.LOCKED -> BorderStroke(1.dp, SurfaceBorder)
        else -> BorderStroke(1.dp, Color.White.copy(alpha = 0.6f))
    }
    Box(
        modifier = modifier
            .size(56.dp)
            .scale(pulse)
            .clip(CircleShape)
            .background(color = fill, shape = CircleShape)
            .border(border = border, shape = CircleShape)
            .clickable(enabled = kind != PondNodeKind.LOCKED, onClick = onClick)
            .semantics { stateDescription = state },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.titleMedium,
            color = textColor
        )
    }
}
