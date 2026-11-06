package com.ripple.lilypath.core.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val PondColorScheme = lightColorScheme(
    primary = PondPrimary,
    onPrimary = SurfaceWhite,
    primaryContainer = PondPrimaryLight,
    onPrimaryContainer = TextPrimary,
    secondary = PondSecondary,
    onSecondary = TextPrimary,
    secondaryContainer = SurfaceAlt,
    onSecondaryContainer = TextSecondary,
    tertiary = LotusAccent,
    onTertiary = TextPrimary,
    tertiaryContainer = LotusAccentLight,
    onTertiaryContainer = TextPrimary,
    background = BackgroundMid,
    onBackground = TextPrimary,
    surface = SurfaceWhite,
    onSurface = TextPrimary,
    surfaceVariant = SurfaceAlt,
    onSurfaceVariant = TextSecondary,
    outline = PondSecondary,
    outlineVariant = SurfaceBorder,
    error = LotusAccentDeep,
    onError = SurfaceWhite
)

@Composable
fun AppTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = PondColorScheme,
        typography = AppTypography,
        content = content
    )
}
