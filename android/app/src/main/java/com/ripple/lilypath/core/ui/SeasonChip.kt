package com.ripple.lilypath.core.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.unit.dp
import com.ripple.lilypath.core.theme.PondPrimary
import com.ripple.lilypath.core.theme.SurfaceAlt
import com.ripple.lilypath.core.theme.SurfaceBorder
import com.ripple.lilypath.core.theme.TextMuted
import com.ripple.lilypath.core.theme.TextPrimary

@Composable
fun SeasonChip(
    label: String,
    selected: Boolean,
    locked: Boolean,
    state: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val background = when {
        locked -> SurfaceAlt
        selected -> PondPrimary.copy(alpha = 0.12f)
        else -> SurfaceAlt
    }
    val border = when {
        locked -> BorderStroke(1.dp, SurfaceBorder)
        selected -> BorderStroke(1.5.dp, PondPrimary)
        else -> BorderStroke(1.dp, SurfaceBorder)
    }
    val labelColor = when {
        locked -> TextMuted
        selected -> TextPrimary
        else -> PondPrimary
    }
    Surface(
        modifier = modifier
            .heightIn(min = 48.dp)
            .clip(RoundedCornerShape(20.dp))
            .clickable(enabled = !locked, onClick = onClick)
            .semantics { stateDescription = state },
        shape = RoundedCornerShape(20.dp),
        color = background,
        contentColor = labelColor,
        border = border
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (locked) {
                Icon(
                    imageVector = AppIcons.Lock,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
            }
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                color = labelColor,
                maxLines = 1
            )
        }
    }
}
