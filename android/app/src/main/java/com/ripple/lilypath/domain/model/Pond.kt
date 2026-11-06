package com.ripple.lilypath.domain.model

data class Pond(
    val index: Int,
    val rows: Int,
    val cols: Int,
    val season: Season,
    val pads: List<PadCell>,
    val stones: List<PadCell>,
    val start: PadCell,
    val home: PadCell,
    val solution: List<PadCell>,
    val blockedEdges: Set<PadEdge>
) {

    val cells: List<PadCell> = pads + stones

    val requiredPads: Int = pads.size

    fun kindOf(cell: PadCell): PadKind = when {
        cell == home -> PadKind.HOME
        stones.contains(cell) -> PadKind.STONE
        else -> PadKind.PAD
    }

    fun contains(cell: PadCell): Boolean = cells.contains(cell)
}
