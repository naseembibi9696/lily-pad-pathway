package com.ripple.lilypath.presentation.gameover

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ripple.lilypath.R
import com.ripple.lilypath.core.config.GameConfig
import com.ripple.lilypath.core.di.AppViewModelFactory
import com.ripple.lilypath.core.theme.LotusAccent
import com.ripple.lilypath.core.theme.LotusAccentLight
import com.ripple.lilypath.core.theme.PondPrimary
import com.ripple.lilypath.core.theme.ResultOverlay
import com.ripple.lilypath.core.theme.TextPrimary
import com.ripple.lilypath.core.theme.TextSecondary
import com.ripple.lilypath.core.ui.GlassCard
import com.ripple.lilypath.core.ui.PrimaryButton
import com.ripple.lilypath.core.ui.ScreenBackground
import com.ripple.lilypath.core.ui.SecondaryButton
import com.ripple.lilypath.core.ui.StatCard
import com.ripple.lilypath.core.ui.seasonAccent
import com.ripple.lilypath.domain.model.PadCell
import com.ripple.lilypath.domain.model.Pond
import kotlin.math.floor
import kotlin.math.min

@Composable
fun GameOverScreen(
    pondIndex: Int,
    pads: Int,
    hintsUsed: Int,
    seasonCompleted: Boolean,
    onNextPond: (Int) -> Unit,
    onPlayAgain: (Int) -> Unit,
    onMenu: () -> Unit
) {
    val gameOverViewModel: GameOverViewModel = viewModel(factory = AppViewModelFactory)
    val state by gameOverViewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(pondIndex, pads, hintsUsed) {
        gameOverViewModel.load(pondIndex, pads, hintsUsed, seasonCompleted)
    }
    DisposableEffect(Unit) {
        onDispose { gameOverViewModel.pause() }
    }

    GameOverContent(
        state = state,
        onNextPond = { onNextPond(state.nextPondIndex) },
        onPlayAgain = { onPlayAgain(pondIndex) },
        onMenu = onMenu
    )
}

@Composable
private fun GameOverContent(
    state: GameOverUiState,
    onNextPond: () -> Unit,
    onPlayAgain: () -> Unit,
    onMenu: () -> Unit
) {
    ScreenBackground(overlay = ResultOverlay) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .safeDrawingPadding()
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(18.dp))
            Text(
                text = stringResource(R.string.pond_complete, state.pondNumber),
                style = MaterialTheme.typography.displaySmall,
                color = TextPrimary,
                textAlign = TextAlign.Center,
                modifier = Modifier.semantics { liveRegion = LiveRegionMode.Polite }
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = stringResource(R.string.frog_hops_home),
                style = MaterialTheme.typography.bodyLarge,
                color = TextSecondary,
                textAlign = TextAlign.Center
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                val pond = state.pond
                if (pond != null) {
                    ParadeBoard(
                        pond = pond,
                        step = state.paradeStep,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
            val frog = state.newFrog
            if (frog != null) {
                GlassCard(modifier = Modifier.fillMaxWidth()) {
                    Image(
                        painter = painterResource(R.drawable.sprite_1),
                        contentDescription = stringResource(R.string.cd_frog),
                        modifier = Modifier.size(88.dp),
                        contentScale = ContentScale.Fit
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = stringResource(R.string.new_frog_friend),
                        style = MaterialTheme.typography.titleMedium,
                        color = seasonAccent(frog.season),
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text = frog.name,
                        style = MaterialTheme.typography.headlineMedium,
                        color = TextPrimary,
                        textAlign = TextAlign.Center
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))
            }
            if (state.statsVisible) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    StatCard(
                        value = stringResource(R.string.value_plain, state.pads),
                        label = stringResource(R.string.stat_pads),
                        valueColor = PondPrimary,
                        modifier = Modifier.weight(1f)
                    )
                    if (state.hintCardVisible) {
                        StatCard(
                            value = stringResource(R.string.value_plain, state.hintsUsed),
                            label = stringResource(R.string.stat_hints),
                            valueColor = LotusAccent,
                            modifier = Modifier.weight(1f)
                        )
                    } else {
                        StatCard(
                            value = stringResource(
                                R.string.value_fraction,
                                state.solvedCount,
                                GameConfig.TOTAL_PONDS
                            ),
                            label = stringResource(R.string.stat_ponds),
                            valueColor = LotusAccent,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(14.dp))
            }
            PrimaryButton(
                text = stringResource(R.string.next_pond),
                onClick = onNextPond,
                modifier = Modifier.fillMaxWidth(),
                enabled = state.hasNextPond
            )
            Spacer(modifier = Modifier.height(14.dp))
            SecondaryButton(
                text = stringResource(R.string.play_again),
                onClick = onPlayAgain,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(4.dp))
            TextButton(
                onClick = onMenu,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
            ) {
                Text(
                    text = stringResource(R.string.menu),
                    style = MaterialTheme.typography.titleMedium
                )
            }
            Spacer(modifier = Modifier.height(10.dp))
        }
    }
}

@Composable
private fun ParadeBoard(
    pond: Pond,
    step: Int,
    modifier: Modifier = Modifier
) {
    BoxWithConstraints(modifier = modifier, contentAlignment = Alignment.Center) {
        val frame = 10.dp
        val widthBudget = (maxWidth - 16.dp).coerceAtMost(320.dp)
        val heightBudget = maxHeight - 8.dp
        val byWidth = floor((widthBudget - frame * 2).value / pond.cols)
        val byHeight = floor((heightBudget - frame * 2).value / pond.rows)
        val cell = min(byWidth, byHeight).coerceIn(24f, 44f).dp
        val boardWidth = cell * pond.cols + frame * 2
        val boardHeight = cell * pond.rows + frame * 2
        val solution = pond.solution
        val safeStep = step.coerceIn(0, solution.size - 1)
        val head = solution[safeStep]
        val frogSize = cell * 0.8f

        val frogX by animateDpAsState(
            targetValue = frame + cell * head.col + (cell - frogSize) / 2,
            animationSpec = spring(dampingRatio = 0.55f, stiffness = Spring.StiffnessMedium),
            label = "paradeX"
        )
        val frogY by animateDpAsState(
            targetValue = frame + cell * head.row + (cell - frogSize) / 2,
            animationSpec = spring(dampingRatio = 0.55f, stiffness = Spring.StiffnessMedium),
            label = "paradeY"
        )
        val squash by animateFloatAsState(
            targetValue = if (safeStep % 2 == 0) 1.14f else 0.86f,
            animationSpec = tween(durationMillis = (GameConfig.HOP_MS / 2).toInt()),
            label = "paradeSquash"
        )

        Box(modifier = Modifier.size(width = boardWidth, height = boardHeight)) {
            ParadeTrail(pond = pond, cell = cell, frame = frame, step = safeStep)
            pond.cells.forEach { target ->
                val bloom = solution.indexOf(target) in 0..safeStep
                ParadeTile(
                    cell = target,
                    size = cell,
                    frame = frame,
                    home = target == pond.home,
                    bloomed = bloom
                )
            }
            Image(
                painter = painterResource(R.drawable.sprite_1),
                contentDescription = stringResource(R.string.cd_frog),
                modifier = Modifier
                    .offset(x = frogX, y = frogY)
                    .size(frogSize)
                    .scale(scaleX = 2f - squash, scaleY = squash),
                contentScale = ContentScale.Fit
            )
        }
    }
}

@Composable
private fun ParadeTile(
    cell: PadCell,
    size: Dp,
    frame: Dp,
    home: Boolean,
    bloomed: Boolean
) {
    val bloom by animateFloatAsState(
        targetValue = if (bloomed) 0.9f else 0f,
        animationSpec = tween(durationMillis = GameConfig.BLOOM_MS),
        label = "paradeBloom"
    )
    Box(
        modifier = Modifier
            .offset(x = frame + size * cell.col, y = frame + size * cell.row)
            .size(size),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(if (home) R.drawable.sprite_4 else R.drawable.sprite_2),
            contentDescription = null,
            modifier = Modifier.size(size - 6.dp),
            contentScale = ContentScale.Fit
        )
        if (bloom > 0f) {
            Image(
                painter = painterResource(R.drawable.sprite_3),
                contentDescription = null,
                modifier = Modifier
                    .size(size * 0.55f)
                    .alpha(bloom),
                contentScale = ContentScale.Fit
            )
        }
    }
}

@Composable
private fun ParadeTrail(pond: Pond, cell: Dp, frame: Dp, step: Int) {
    Canvas(
        modifier = Modifier.size(
            width = cell * pond.cols + frame * 2,
            height = cell * pond.rows + frame * 2
        )
    ) {
        val framePx = frame.toPx()
        val cellPx = cell.toPx()
        val solution = pond.solution
        if (solution.size < 2) return@Canvas
        val path = Path()
        solution.forEachIndexed { position, target ->
            val x = framePx + (target.col + 0.5f) * cellPx
            val y = framePx + (target.row + 0.5f) * cellPx
            if (position == 0) path.moveTo(x, y) else path.lineTo(x, y)
        }
        drawPath(
            path = path,
            color = LotusAccent.copy(alpha = 0.3f),
            style = Stroke(width = 18.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round)
        )
        val walked = Path()
        solution.take(step + 1).forEachIndexed { position, target ->
            val x = framePx + (target.col + 0.5f) * cellPx
            val y = framePx + (target.row + 0.5f) * cellPx
            if (position == 0) walked.moveTo(x, y) else walked.lineTo(x, y)
        }
        drawPath(
            path = walked,
            brush = Brush.linearGradient(listOf(LotusAccentLight, LotusAccent)),
            style = Stroke(width = 10.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round)
        )
    }
}
