package com.ripple.lilypath.core.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import com.ripple.lilypath.R
import com.ripple.lilypath.core.theme.AutumnAccent
import com.ripple.lilypath.core.theme.SpringAccent
import com.ripple.lilypath.core.theme.SummerAccent
import com.ripple.lilypath.core.theme.WinterAccent
import com.ripple.lilypath.domain.model.Season

@Composable
fun seasonName(season: Season): String = stringResource(
    when (season) {
        Season.SPRING -> R.string.season_spring
        Season.SUMMER -> R.string.season_summer
        Season.AUTUMN -> R.string.season_autumn
        Season.WINTER -> R.string.season_winter
    }
)

fun seasonAccent(season: Season): Color = when (season) {
    Season.SPRING -> SpringAccent
    Season.SUMMER -> SummerAccent
    Season.AUTUMN -> AutumnAccent
    Season.WINTER -> WinterAccent
}
