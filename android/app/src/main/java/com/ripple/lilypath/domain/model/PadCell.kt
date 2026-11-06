package com.ripple.lilypath.domain.model

data class PadCell(val row: Int, val col: Int) {

    fun isNeighbourOf(other: PadCell): Boolean {
        val dr = row - other.row
        val dc = col - other.col
        return (dr == 0 && (dc == 1 || dc == -1)) || (dc == 0 && (dr == 1 || dr == -1))
    }
}

data class PadEdge(val from: PadCell, val to: PadCell)

enum class PadKind {
    PAD,
    HOME,
    STONE
}
