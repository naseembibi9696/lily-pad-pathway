package com.ripple.lilypath.presentation.game

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ripple.lilypath.R
import com.ripple.lilypath.core.di.AppViewModelFactory
import com.ripple.lilypath.core.theme.GameOverlay
import com.ripple.lilypath.core.theme.LotusGradient
import com.ripple.lilypath.core.theme.PondPrimary
import com.ripple.lilypath.core.theme.PondSecondary
import com.ripple.lilypath.core.theme.SurfaceSheet
import com.ripple.lilypath.core.theme.TextPrimary
import com.ripple.lilypath.core.theme.TextSecondary
import com.ripple.lilypath.core.ui.AppIcons
import com.ripple.lilypath.core.ui.PrimaryButton
import com.ripple.lilypath.core.ui.RippleBackground
import com.ripple.lilypath.core.ui.ScreenBackground
import com.ripple.lilypath.core.ui.SecondaryButton
import com.ripple.lilypath.core.ui.seasonName
import com.ripple.lilypath.presentation.dialog.PauseDialog
import com.ripple.lilypath.presentation.dialog.TutorialOverlay

@Composable
fun GameScreen(
    pondIndex: Int,
    onBack: () -> Unit,
    onMenu: () -> Unit,
    onSolved: (Int, Int, Int, Boolean) -> Unit
) {
    val gameViewModel: GameViewModel = viewModel(factory = AppViewModelFactory)
    val state by gameViewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(pondIndex) {
        gameViewModel.load(pondIndex)
    }
    LaunchedEffect(Unit) {
        gameViewModel.events.collect { event ->
            when (event) {
                is GameEvent.Solved -> onSolved(
                    event.pondIndex,
                    event.pads,
                    event.hintsUsed,
                    event.seasonCompleted
                )
            }
        }
    }
    DisposableEffect(Unit) {
        onDispose { gameViewModel.pause() }
    }

    GameContent(
        state = state,
        onBack = onBack,
        onPause = gameViewModel::openPause,
        onPadTouched = gameViewModel::onPadTouched,
        onDragFinished = gameViewModel::onDragFinished,
        onUndo = gameViewModel::undo,
        onReset = gameViewModel::resetTrail,
        onHint = gameViewModel::useHint
    )

    if (state.tutorialVisible) {
        TutorialOverlay(onDismiss = gameViewModel::dismissTutorial)
    }
    if (state.pauseVisible) {
        PauseDialog(
            onResume = gameViewModel::closePause,
            onRestart = gameViewModel::restartPond,
            onMenu = onMenu
        )
    }
}

@Composable
private fun GameContent(
    state: GameUiState,
    onBack: () -> Unit,
    onPause: () -> Unit,
    onPadTouched: (Int, Int) -> Unit,
    onDragFinished: () -> Unit,
    onUndo: () -> Unit,
    onReset: () -> Unit,
    onHint: () -> Unit
) {
    val transition = rememberInfiniteTransition(label = "gameLoop")
    val drift by transition.animateFloat(
        initialValue = -10f,
        targetValue = 10f,
        animationSpec = infiniteRepeatable(
            animation = tween(4400, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "decorDrift"
    )
    val pond = state.pond

    ScreenBackground(overlay = GameOverlay) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .safeDrawingPadding()
        ) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp)
                    .height(56.dp),
                shape = RoundedCornerShape(28.dp),
                color = SurfaceSheet,
                contentColor = TextPrimary,
                shadowElevation = 4.dp
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier.size(48.dp)
                    ) {
                        Icon(
                            imageVector = AppIcons.ArrowBack,
                            contentDescription = stringResource(R.string.menu),
                            tint = PondPrimary,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    Column(
                        modifier = Modifier.weight(1f),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = stringResource(
                                R.string.pond_header,
                                (pond?.index ?: 0) + 1,
                                if (pond == null) {
                                    stringResource(R.string.season_spring)
                                } else {
                                    seasonName(pond.season)
                                }
                            ),
                            style = MaterialTheme.typography.titleMedium,
                            color = TextPrimary,
                            textAlign = TextAlign.Center
                        )
                        Text(
                            text = stringResource(
                                R.string.pads_counter,
                                state.visitedPads,
                                state.totalPads
                            ),
                            style = MaterialTheme.typography.labelSmall,
                            color = TextSecondary,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.semantics {
                                liveRegion = LiveRegionMode.Polite
                            }
                        )
                    }
                    IconButton(
                        onClick = onPause,
                        modifier = Modifier.size(48.dp)
                    ) {
                        Image(
                            painter = painterResource(R.drawable.icon_pause),
                            contentDescription = stringResource(R.string.paused),
                            modifier = Modifier.size(28.dp),
                            contentScale = ContentScale.Fit
                        )
                    }
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                RippleBackground(
                    modifier = Modifier.fillMaxSize(),
                    color = PondSecondary,
                    rings = 3,
                    minRadius = 40.dp,
                    maxRadius = 240.dp,
                    durationMillis = 5200
                )
                Image(
                    painter = painterResource(R.drawable.sprite_3),
                    contentDescription = null,
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(start = 10.dp, bottom = 10.dp)
                        .offset(x = drift.dp)
                        .size(44.dp)
                        .alpha(0.7f),
                    contentScale = ContentScale.Fit
                )
                Image(
                    painter = painterResource(R.drawable.sprite_5),
                    contentDescription = null,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(end = 10.dp, top = 6.dp)
                        .offset(y = drift.dp)
                        .size(40.dp)
                        .alpha(0.7f),
                    contentScale = ContentScale.Fit
                )
                if (pond != null) {
                    PondBoard(
                        pond = pond,
                        visited = state.visited,
                        hintCell = state.hintCell,
                        solved = state.solved,
                        boardDescription = stringResource(R.string.cd_board, state.totalPads),
                        frogDescription = stringResource(R.string.cd_frog),
                        visitedState = stringResource(R.string.state_visited),
                        freeState = stringResource(R.string.state_free),
                        homeState = stringResource(R.string.state_home_log),
                        stoneState = stringResource(R.string.state_stone),
                        modifier = Modifier.fillMaxSize(),
                        onPadTouched = onPadTouched,
                        onDragFinished = onDragFinished
                    )
                }
            }

            Text(
                text = stringResource(
                    if (state.stuck) R.string.try_another_way else R.string.drag_hint
                ),
                style = MaterialTheme.typography.bodyLarge,
                color = TextSecondary,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
                    .semantics { liveRegion = LiveRegionMode.Polite }
            )
            Spacer(modifier = Modifier.height(10.dp))

            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .padding(bottom = 24.dp)
                    .height(72.dp),
                shape = RoundedCornerShape(28.dp),
                color = SurfaceSheet,
                contentColor = TextPrimary,
                shadowElevation = 8.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    SecondaryButton(
                        text = stringResource(R.string.undo),
                        onClick = onUndo,
                        modifier = Modifier.weight(1f),
                        enabled = state.canUndo && !state.solved
                    )
                    SecondaryButton(
                        text = stringResource(R.string.reset),
                        onClick = onReset,
                        modifier = Modifier.weight(1f),
                        enabled = state.canUndo && !state.solved
                    )
                    PrimaryButton(
                        text = stringResource(R.string.hint),
                        onClick = onHint,
                        modifier = Modifier.weight(1f),
                        enabled = state.hintsEnabled && !state.solved,
                        compact = true,
                        gradient = LotusGradient,
                        contentColor = TextPrimary,
                        shadowColor = androidx.compose.ui.graphics.Color(0x4DF7B2BD)
                    )
                }
            }
        }
    }
}
