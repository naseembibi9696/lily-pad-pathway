package com.ripple.lilypath.domain.usecase

import com.ripple.lilypath.core.config.GameConfig
import com.ripple.lilypath.domain.model.PadCell
import com.ripple.lilypath.domain.model.PadEdge
import com.ripple.lilypath.domain.model.Pond
import com.ripple.lilypath.domain.model.Season
import kotlin.math.roundToInt
import kotlin.random.Random

class GeneratePondUseCase {

    operator fun invoke(index: Int): Pond {
        val safeIndex = index.coerceIn(0, GameConfig.TOTAL_PONDS - 1)
        val random = Random(GameConfig.SEED_BASE + safeIndex * GameConfig.SEED_STEP)
        val season = seasonOf(safeIndex)
        val grid = gridFor(safeIndex)
        val rows = grid.first
        val cols = grid.second
        val target = targetPads(rows, cols)
        val path = walk(rows, cols, target, random) ?: serpentine(rows, cols, target)
        val offPath = allCells(rows, cols).filterNot { path.contains(it) }
        val stones = if (season == Season.WINTER && offPath.isNotEmpty()) {
            offPath.shuffled(random).take(1 + random.nextInt(3))
        } else {
            emptyList()
        }
        val blocked = if (season == Season.AUTUMN) {
            oneWayEdges(rows, cols, path, stones, random)
        } else {
            emptySet()
        }
        return Pond(
            index = safeIndex,
            rows = rows,
            cols = cols,
            season = season,
            pads = path,
            stones = stones,
            start = path.first(),
            home = path.last(),
            solution = path,
            blockedEdges = blocked
        )
    }

    fun seasonOf(index: Int): Season {
        val slot = (index / GameConfig.SEASON_SIZE).coerceIn(0, Season.entries.size - 1)
        return Season.entries[slot]
    }

    private fun gridFor(index: Int): Pair<Int, Int> {
        val number = index + 1
        return when {
            number <= 10 -> 3 to 3
            number <= 25 -> 4 to 4
            number <= 45 -> 5 to 4
            number <= 65 -> 5 to 5
            else -> 6 to 5
        }
    }

    private fun targetPads(rows: Int, cols: Int): Int {
        val total = rows * cols
        val wanted = (total * GameConfig.PAD_DENSITY).roundToInt()
        return wanted.coerceIn(GameConfig.MIN_PADS, GameConfig.MAX_PADS).coerceAtMost(total)
    }

    private fun allCells(rows: Int, cols: Int): List<PadCell> =
        (0 until rows).flatMap { r -> (0 until cols).map { c -> PadCell(r, c) } }

    private fun neighbours(cell: PadCell, rows: Int, cols: Int): List<PadCell> = listOf(
        PadCell(cell.row - 1, cell.col),
        PadCell(cell.row + 1, cell.col),
        PadCell(cell.row, cell.col - 1),
        PadCell(cell.row, cell.col + 1)
    ).filter { it.row in 0 until rows && it.col in 0 until cols }

    private fun walk(rows: Int, cols: Int, target: Int, random: Random): List<PadCell>? {
        val starts = allCells(rows, cols).shuffled(random)
        for (start in starts.take(10)) {
            val path = ArrayList<PadCell>(target)
            val seen = HashSet<PadCell>(target * 2)
            val budget = intArrayOf(40000)
            if (extend(start, rows, cols, target, path, seen, random, budget)) {
                return path.toList()
            }
        }
        return null
    }

    private fun extend(
        cell: PadCell,
        rows: Int,
        cols: Int,
        target: Int,
        path: ArrayList<PadCell>,
        seen: HashSet<PadCell>,
        random: Random,
        budget: IntArray
    ): Boolean {
        if (budget[0] <= 0) return false
        budget[0] = budget[0] - 1
        path.add(cell)
        seen.add(cell)
        if (path.size == target) return true
        val options = neighbours(cell, rows, cols)
            .filterNot { seen.contains(it) }
            .shuffled(random)
            .sortedBy { candidate -> neighbours(candidate, rows, cols).count { !seen.contains(it) } }
        for (option in options) {
            if (extend(option, rows, cols, target, path, seen, random, budget)) return true
        }
        path.removeAt(path.size - 1)
        seen.remove(cell)
        return false
    }

    private fun serpentine(rows: Int, cols: Int, target: Int): List<PadCell> {
        val cells = ArrayList<PadCell>(rows * cols)
        for (r in 0 until rows) {
            val range = if (r % 2 == 0) (0 until cols) else (cols - 1 downTo 0)
            for (c in range) cells.add(PadCell(r, c))
        }
        return cells.take(target)
    }

    private fun oneWayEdges(
        rows: Int,
        cols: Int,
        path: List<PadCell>,
        stones: List<PadCell>,
        random: Random
    ): Set<PadEdge> {
        val onPath = HashSet<PadEdge>()
        for (i in 0 until path.size - 1) {
            onPath.add(PadEdge(path[i], path[i + 1]))
        }
        val usable = path + stones
        val candidates = ArrayList<PadEdge>()
        for (cell in usable) {
            for (other in neighbours(cell, rows, cols)) {
                if (!usable.contains(other)) continue
                val edge = PadEdge(cell, other)
                if (!onPath.contains(edge)) candidates.add(edge)
            }
        }
        if (candidates.isEmpty()) return emptySet()
        return candidates.shuffled(random).take(2 + random.nextInt(3)).toSet()
    }
}
