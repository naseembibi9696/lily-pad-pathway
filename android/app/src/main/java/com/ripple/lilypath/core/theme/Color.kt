package com.ripple.lilypath.core.theme

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

const val PRESET_NAME = "OCEAN_FRESH"

val BackgroundTop = Color(0xFFEAF8F3)
val BackgroundMid = Color(0xFFDFF3EE)
val BackgroundDeep = Color(0xFFBFE3DC)
val SplashDeepWater = Color(0xFF0F4A45)

val SurfaceWhite = Color(0xFFFFFFFF)
val SurfaceSheet = Color(0xF2FFFFFF)
val SurfaceAlt = Color(0xFFF2FBF8)
val SurfaceBorder = Color(0x33A8DADC)

val PondPrimary = Color(0xFF2A9D8F)
val PondPrimaryDark = Color(0xFF1F7D72)
val PondPrimaryLight = Color(0xFF4FBFB1)

val LotusAccent = Color(0xFFF7B2BD)
val LotusAccentLight = Color(0xFFFFD6DE)
val LotusAccentDeep = Color(0xFFE8899A)

val PondSecondary = Color(0xFFA8DADC)
val StoneBlue = Color(0xFF9DCEDB)

val TextPrimary = Color(0xFF16423C)
val TextSecondary = Color(0xFF44756D)
val TextMuted = Color(0xFF7FA49C)

val TealGradient = Brush.horizontalGradient(listOf(PondPrimary, PondPrimaryLight))
val LotusGradient = Brush.horizontalGradient(listOf(LotusAccent, LotusAccentLight))

val SplashOverlay = Brush.verticalGradient(
    listOf(Color(0xE61F7D72), Color(0xF21A6B63), Color(0xFA0F4A45))
)

val MenuOverlay = Brush.verticalGradient(
    listOf(Color(0x1AFFFFFF), Color(0x00FFFFFF), Color(0xCCDFF3EE), Color(0xFFDFF3EE))
)

val GameOverlay = Brush.verticalGradient(
    listOf(Color(0x99DFF3EE), Color(0x66DFF3EE), Color(0xB3BFE3DC))
)

val ResultOverlay = Brush.verticalGradient(
    listOf(Color(0xB3F7B2BD), Color(0x80DFF3EE), Color(0xF2DFF3EE))
)

val FriendsOverlay = Brush.verticalGradient(
    listOf(Color(0x99DFF3EE), Color(0xF2DFF3EE))
)

val SpringAccent = LotusAccent
val SummerAccent = Color(0xFFF6C9A0)
val AutumnAccent = Color(0xFFE9A178)
val WinterAccent = Color(0xFFBFD9EE)

val SpringBackground = Color(0xFFDFF3EE)
val SummerBackground = Color(0xFFD7F0E4)
val AutumnBackground = Color(0xFFEFE7D8)
val WinterBackground = Color(0xFFE3EEF4)
