package com.ripple.lilypath.presentation.menu

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ripple.lilypath.R
import com.ripple.lilypath.core.di.AppViewModelFactory
import com.ripple.lilypath.core.theme.LotusAccent
import com.ripple.lilypath.core.theme.MenuOverlay
import com.ripple.lilypath.core.theme.PondPrimary
import com.ripple.lilypath.core.theme.PondSecondary
import com.ripple.lilypath.core.theme.SurfaceSheet
import com.ripple.lilypath.core.theme.TextPrimary
import com.ripple.lilypath.core.theme.TextSecondary
import com.ripple.lilypath.core.ui.AppIcons
import com.ripple.lilypath.core.ui.PondNode
import com.ripple.lilypath.core.ui.PrimaryButton
import com.ripple.lilypath.core.ui.RippleBackground
import com.ripple.lilypath.core.ui.ScreenBackground
import com.ripple.lilypath.core.ui.SeasonChip
import com.ripple.lilypath.core.ui.SecondaryButton
import com.ripple.lilypath.core.ui.StatCard
import com.ripple.lilypath.core.ui.seasonName
import com.ripple.lilypath.domain.model.PondNodeKind
import com.ripple.lilypath.domain.model.Season
import com.ripple.lilypath.presentation.dialog.SettingsDialog

@Composable
fun MenuScreen(
    onPlay: (Int) -> Unit,
    onOpenFriends: () -> Unit
) {
    val menuViewModel: MenuViewModel = viewModel(factory = AppViewModelFactory)
    val state by menuViewModel.state.collectAsStateWithLifecycle()
    val listState = rememberLazyListState()

    LaunchedEffect(Unit) {
        menuViewModel.refresh()
    }
    LaunchedEffect(state.scrollRequest) {
        if (state.scrollRequest > 0) listState.animateScrollToItem(state.scrollTarget)
    }

    MenuContent(
        state = state,
        listState = listState,
        onPlay = { onPlay(state.nextPond) },
        onPondSelected = onPlay,
        onSeasonSelected = menuViewModel::selectSeason,
        onTrail = menuViewModel::scrollToCurrent,
        onOpenFriends = onOpenFriends,
        onOpenSettings = menuViewModel::openSettings
    )

    if (state.settingsVisible) {
        SettingsDialog(
            volume = state.volume,
            haptics = state.haptics,
            hintFrequency = state.hintFrequency,
            resetConfirmVisible = state.resetConfirmVisible,
            onVolumeChange = menuViewModel::setVolume,
            onHapticsChange = menuViewModel::setHaptics,
            onHintFrequencyChange = menuViewModel::setHintFrequency,
            onAskReset = menuViewModel::askResetConfirm,
            onCancelReset = menuViewModel::cancelReset,
            onConfirmReset = menuViewModel::confirmReset,
            onDismiss = menuViewModel::closeSettings
        )
    }
}

@Composable
private fun MenuContent(
    state: MenuUiState,
    listState: androidx.compose.foundation.lazy.LazyListState,
    onPlay: () -> Unit,
    onPondSelected: (Int) -> Unit,
    onSeasonSelected: (Season) -> Unit,
    onTrail: () -> Unit,
    onOpenFriends: () -> Unit,
    onOpenSettings: () -> Unit
) {
    val transition = rememberInfiniteTransition(label = "menuLoop")
    val frogBob by transition.animateFloat(
        initialValue = -5f,
        targetValue = 5f,
        animationSpec = infiniteRepeatable(
            animation = tween(2400, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "frogBob"
    )
    val padBreath by transition.animateFloat(
        initialValue = 1f,
        targetValue = 1.03f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "padBreath"
    )
    val flyDrift by transition.animateFloat(
        initialValue = -14f,
        targetValue = 14f,
        animationSpec = infiniteRepeatable(
            animation = tween(4400, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "flyDrift"
    )

    ScreenBackground(overlay = MenuOverlay) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .safeDrawingPadding()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                RippleBackground(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .size(320.dp),
                    color = PondSecondary,
                    rings = 3,
                    minRadius = 50.dp,
                    maxRadius = 150.dp,
                    durationMillis = 5200
                )
                Image(
                    painter = painterResource(R.drawable.sprite_5),
                    contentDescription = null,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(end = 28.dp, top = 72.dp)
                        .offset(x = flyDrift.dp, y = (-flyDrift).dp)
                        .size(48.dp)
                        .alpha(0.85f),
                    contentScale = ContentScale.Fit
                )
                Image(
                    painter = painterResource(R.drawable.sprite_2),
                    contentDescription = null,
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 6.dp)
                        .size(148.dp)
                        .scale(padBreath),
                    contentScale = ContentScale.Fit
                )
                Image(
                    painter = painterResource(R.drawable.sprite_1),
                    contentDescription = stringResource(R.string.cd_frog),
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 56.dp)
                        .offset(y = frogBob.dp)
                        .size(120.dp),
                    contentScale = ContentScale.Fit
                )
                IconButton(
                    onClick = onOpenSettings,
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(start = 12.dp, top = 8.dp)
                        .size(48.dp)
                ) {
                    Icon(
                        imageVector = AppIcons.Settings,
                        contentDescription = stringResource(R.string.settings),
                        modifier = Modifier.size(26.dp),
                        tint = PondPrimary
                    )
                }
                IconButton(
                    onClick = onOpenFriends,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(end = 12.dp, top = 8.dp)
                        .size(48.dp)
                ) {
                    Image(
                        painter = painterResource(R.drawable.icon_home),
                        contentDescription = stringResource(R.string.frog_friends),
                        modifier = Modifier.size(40.dp),
                        contentScale = ContentScale.Fit
                    )
                }
            }

            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp),
                color = SurfaceSheet,
                contentColor = TextPrimary,
                shadowElevation = 6.dp
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp)
                        .padding(top = 12.dp, bottom = 18.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .width(44.dp)
                            .height(4.dp)
                            .background(color = PondSecondary, shape = CircleShape)
                    )
                    Spacer(modifier = Modifier.height(14.dp))
                    Text(
                        text = stringResource(R.string.menu_title),
                        style = MaterialTheme.typography.headlineMedium,
                        color = TextPrimary,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = stringResource(R.string.tagline),
                        style = MaterialTheme.typography.bodyLarge,
                        color = TextSecondary,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Season.entries.forEach { season ->
                            val locked = !state.unlockedSeasons.contains(season)
                            SeasonChip(
                                label = seasonName(season),
                                selected = state.selectedSeason == season,
                                locked = locked,
                                state = stringResource(
                                    if (locked) R.string.state_locked else R.string.state_open
                                ),
                                onClick = { onSeasonSelected(season) },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                    if (state.statsVisible) {
                        Spacer(modifier = Modifier.height(14.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            StatCard(
                                value = stringResource(
                                    R.string.value_fraction,
                                    state.solvedCount,
                                    state.totalPonds
                                ),
                                label = stringResource(R.string.stat_ponds),
                                valueColor = PondPrimary,
                                modifier = Modifier.weight(1f)
                            )
                            StatCard(
                                value = stringResource(
                                    R.string.value_fraction,
                                    state.frogCount,
                                    state.totalFrogs
                                ),
                                label = stringResource(R.string.stat_frogs),
                                valueColor = LotusAccent,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    PrimaryButton(
                        text = stringResource(R.string.play),
                        onClick = onPlay,
                        modifier = Modifier.fillMaxWidth(),
                        icon = AppIcons.PlayArrow
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    SecondaryButton(
                        text = stringResource(R.string.pond_trail),
                        onClick = onTrail,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(14.dp))
                    LazyRow(
                        state = listState,
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        items(state.trail, key = { it.index }) { node ->
                            PondNode(
                                label = if (node.kind == PondNodeKind.SOLVED) {
                                    stringResource(R.string.solved_mark)
                                } else {
                                    stringResource(R.string.pond_number, node.index + 1)
                                },
                                kind = node.kind,
                                state = stringResource(
                                    when (node.kind) {
                                        PondNodeKind.SOLVED -> R.string.state_solved
                                        PondNodeKind.CURRENT -> R.string.state_current
                                        PondNodeKind.LOCKED -> R.string.state_locked
                                    }
                                ),
                                onClick = { onPondSelected(node.index) }
                            )
                        }
                    }
                }
            }
        }
    }
}
