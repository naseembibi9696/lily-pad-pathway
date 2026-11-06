package com.ripple.lilypath.presentation.game

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.ripple.lilypath.R
import com.ripple.lilypath.core.config.GameConfig
import com.ripple.lilypath.core.theme.LotusAccent
import com.ripple.lilypath.core.theme.LotusAccentLight
import com.ripple.lilypath.core.theme.PondPrimaryDark
import com.ripple.lilypath.core.theme.PondPrimaryLight
import com.ripple.lilypath.core.theme.StoneBlue
import com.ripple.lilypath.core.theme.SurfaceBorder
import com.ripple.lilypath.domain.model.PadCell
import com.ripple.lilypath.domain.model.PadKind
import com.ripple.lilypath.domain.model.Pond
import kotlin.math.floor
import kotlin.math.hypot
import kotlin.math.min

private val BoardPadding = 14.dp
private val BoardBorder = 2.dp
private val BoardFrame = BoardPadding + BoardBorder

@Composable
fun PondBoard(
    pond: Pond,
    visited: List<PadCell>,
    hintCell: PadCell?,
    solved: Boolean,
    boardDescription: String,
    frogDescription: String,
    visitedState: String,
    freeState: String,
    homeState: String,
    stoneState: String,
    modifier: Modifier = Modifier,
    onPadTouched: (Int, Int) -> Unit,
    onDragFinished: () -> Unit
) {
    BoxWithConstraints(modifier = modifier, contentAlignment = Alignment.Center) {
        val widthBudget = (maxWidth - 32.dp).coerceAtMost(380.dp)
        val heightBudget = maxHeight - 16.dp
        val byWidth = floor((widthBudget - BoardFrame * 2).value / pond.cols)
        val byHeight = floor((heightBudget - BoardFrame * 2).value / pond.rows)
        val cell = min(byWidth, byHeight).coerceAtLeast(40f).dp
        val boardWidth = cell * pond.cols + BoardFrame * 2
        val boardHeight = cell * pond.rows + BoardFrame * 2
        val shape = RoundedCornerShape(28.dp)
        val head = visited.lastOrNull() ?: pond.start
        val frogSize = cell * 0.78f

        val frogX by animateDpAsState(
            targetValue = BoardFrame + cell * head.col + (cell - frogSize) / 2,
            animationSpec = spring(dampingRatio = 0.55f, stiffness = Spring.StiffnessMedium),
            label = "frogX"
        )
        val frogY by animateDpAsState(
            targetValue = BoardFrame + cell * head.row + (cell - frogSize) / 2,
            animationSpec = spring(dampingRatio = 0.55f, stiffness = Spring.StiffnessMedium),
            label = "frogY"
        )

        Box(
            modifier = Modifier
                .size(width = boardWidth, height = boardHeight)
                .background(color = Color(0x40FFFFFF), shape = shape)
                .border(width = BoardBorder, color = SurfaceBorder, shape = shape)
                .semantics { contentDescription = boardDescription }
                .pointerInput(pond.index, cell) {
                    val framePx = BoardFrame.toPx()
                    val cellPx = cell.toPx()
                    detectDragGestures(
                        onDragStart = { position ->
                            resolveCell(position, framePx, cellPx, pond.rows, pond.cols)?.let {
                                onPadTouched(it.row, it.col)
                            }
                        },
                        onDragEnd = onDragFinished,
                        onDragCancel = onDragFinished
                    ) { change, _ ->
                        resolveCell(change.position, framePx, cellPx, pond.rows, pond.cols)?.let {
                            onPadTouched(it.row, it.col)
                        }
                    }
                }
                .pointerInput(pond.index, cell) {
                    val framePx = BoardFrame.toPx()
                    val cellPx = cell.toPx()
                    detectTapGestures { position ->
                        resolveCell(position, framePx, cellPx, pond.rows, pond.cols)?.let {
                            onPadTouched(it.row, it.col)
                        }
                        onDragFinished()
                    }
                }
        ) {
            TrailCanvas(pond = pond, visited = visited, cell = cell)

            pond.cells.forEach { cellPosition ->
                key(cellPosition.row, cellPosition.col) {
                    val kind = pond.kindOf(cellPosition)
                    val isVisited = visited.contains(cellPosition)
                    PadTile(
                        cell = cellPosition,
                        size = cell,
                        kind = kind,
                        visited = isVisited,
                        hinted = hintCell == cellPosition,
                        ready = kind == PadKind.HOME && visited.size == pond.pads.size - 1,
                        solved = solved,
                        state = when {
                            kind == PadKind.STONE -> stoneState
                            kind == PadKind.HOME -> homeState
                            isVisited -> visitedState
                            else -> freeState
                        }
                    )
                }
            }

            Image(
                painter = painterResource(R.drawable.sprite_1),
                contentDescription = frogDescription,
                modifier = Modifier
                    .offset(x = frogX, y = frogY)
                    .size(frogSize),
                contentScale = ContentScale.Fit
            )
        }
    }
}

@Composable
private fun PadTile(
    cell: PadCell,
    size: Dp,
    kind: PadKind,
    visited: Boolean,
    hinted: Boolean,
    ready: Boolean,
    solved: Boolean,
    state: String
) {
    val sink by animateDpAsState(
        targetValue = if (visited) 4.dp else 0.dp,
        animationSpec = tween(durationMillis = GameConfig.SINK_MS, easing = FastOutSlowInEasing),
        label = "padSink"
    )
    val padScale by animateFloatAsState(
        targetValue = if (visited) 0.96f else 1f,
        animationSpec = tween(durationMillis = GameConfig.SINK_MS, easing = FastOutSlowInEasing),
        label = "padScale"
    )
    val bloom by animateFloatAsState(
        targetValue = if (solved && visited) 0.9f else 0f,
        animationSpec = tween(durationMillis = GameConfig.BLOOM_MS),
        label = "padBloom"
    )
    val transition = rememberInfiniteTransition(label = "padPulse")
    val pulse by transition.animateFloat(
        initialValue = 1f,
        targetValue = if (hinted || ready) 1.06f else 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(if (hinted) 1200 else 900),
            repeatMode = RepeatMode.Reverse
        ),
        label = "padPulseValue"
    )

    val baseOffsetX = BoardFrame + size * cell.col
    val baseOffsetY = BoardFrame + size * cell.row
    val tileSize = size - 8.dp
    val homeSize = size * 0.95f

    Box(
        modifier = Modifier
            .offset(x = baseOffsetX, y = baseOffsetY + sink)
            .size(size)
            .semantics { stateDescription = state },
        contentAlignment = Alignment.Center
    ) {
        if (hinted || ready) {
            Box(
                modifier = Modifier
                    .size(size)
                    .scale(pulse)
                    .background(
                        brush = Brush.radialGradient(
                            listOf(LotusAccent.copy(alpha = 0.5f), Color.Transparent)
                        ),
                        shape = RoundedCornerShape(size / 2)
                    )
            )
        }
        Image(
            painter = painterResource(
                if (kind == PadKind.HOME) R.drawable.sprite_4 else R.drawable.sprite_2
            ),
            contentDescription = null,
            modifier = Modifier
                .size(if (kind == PadKind.HOME) homeSize else tileSize)
                .scale(if (kind == PadKind.HOME) pulse else padScale),
            contentScale = ContentScale.Fit,
            colorFilter = when {
                kind == PadKind.STONE -> ColorFilter.tint(StoneBlue, androidx.compose.ui.graphics.BlendMode.Modulate)
                visited && kind == PadKind.PAD -> ColorFilter.tint(
                    PondPrimaryLight,
                    androidx.compose.ui.graphics.BlendMode.Modulate
                )
                else -> null
            }
        )
        if (bloom > 0f) {
            Image(
                painter = painterResource(R.drawable.sprite_3),
                contentDescription = null,
                modifier = Modifier
                    .size(tileSize * 0.6f)
                    .alpha(bloom),
                contentScale = ContentScale.Fit
            )
        }
    }
}

@Composable
private fun TrailCanvas(pond: Pond, visited: List<PadCell>, cell: Dp) {
    Canvas(modifier = Modifier.size(cell * pond.cols + BoardFrame * 2, cell * pond.rows + BoardFrame * 2)) {
        val framePx = BoardFrame.toPx()
        val cellPx = cell.toPx()
        fun centerX(target: PadCell) = framePx + (target.col + 0.5f) * cellPx
        fun centerY(target: PadCell) = framePx + (target.row + 0.5f) * cellPx

        pond.blockedEdges.forEach { edge ->
            val x1 = centerX(edge.from)
            val y1 = centerY(edge.from)
            val x2 = centerX(edge.to)
            val y2 = centerY(edge.to)
            val midX = (x1 + x2) / 2f
            val midY = (y1 + y2) / 2f
            val length = hypot(x2 - x1, y2 - y1)
            if (length <= 0f) return@forEach
            val ux = (x2 - x1) / length
            val uy = (y2 - y1) / length
            val tip = 7.dp.toPx()
            val marker = Path()
            marker.moveTo(midX - ux * tip, midY - uy * tip)
            marker.lineTo(midX + ux * tip - uy * tip * 0.8f, midY + uy * tip + ux * tip * 0.8f)
            marker.lineTo(midX + ux * tip + uy * tip * 0.8f, midY + uy * tip - ux * tip * 0.8f)
            marker.close()
            drawPath(path = marker, color = PondPrimaryDark.copy(alpha = 0.45f))
        }

        if (visited.size > 1) {
            val trail = Path()
            visited.forEachIndexed { position, target ->
                if (position == 0) {
                    trail.moveTo(centerX(target), centerY(target))
                } else {
                    trail.lineTo(centerX(target), centerY(target))
                }
            }
            drawPath(
                path = trail,
                color = LotusAccent.copy(alpha = 0.33f),
                style = Stroke(
                    width = 26.dp.toPx(),
                    cap = StrokeCap.Round,
                    join = StrokeJoin.Round
                )
            )
            drawPath(
                path = trail,
                brush = Brush.linearGradient(listOf(LotusAccent, LotusAccentLight)),
                style = Stroke(
                    width = 14.dp.toPx(),
                    cap = StrokeCap.Round,
                    join = StrokeJoin.Round
                )
            )
        }
    }
}

private fun resolveCell(
    position: Offset,
    framePx: Float,
    cellPx: Float,
    rows: Int,
    cols: Int
): PadCell? {
    if (cellPx <= 0f) return null
    val col = floor((position.x - framePx) / cellPx).toInt()
    val row = floor((position.y - framePx) / cellPx).toInt()
    if (row < 0 || row >= rows || col < 0 || col >= cols) return null
    val centerX = framePx + (col + 0.5f) * cellPx
    val centerY = framePx + (row + 0.5f) * cellPx
    if (hypot(position.x - centerX, position.y - centerY) > cellPx * GameConfig.SNAP_FACTOR) return null
    return PadCell(row, col)
}
