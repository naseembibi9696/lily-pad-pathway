package com.ripple.lilypath.domain.usecase

import com.ripple.lilypath.domain.model.PadCell
import com.ripple.lilypath.domain.model.PadEdge
import com.ripple.lilypath.domain.model.Pond

class ValidateMoveUseCase {

    fun canAdvance(pond: Pond, visited: List<PadCell>, target: PadCell): Boolean {
        if (visited.isEmpty()) return false
        if (!pond.contains(target)) return false
        if (visited.contains(target)) return false
        val head = visited.last()
        if (!target.isNeighbourOf(head)) return false
        if (pond.blockedEdges.contains(PadEdge(head, target))) return false
        return true
    }

    fun isRetrace(visited: List<PadCell>, target: PadCell): Boolean =
        visited.size > 1 && visited[visited.size - 2] == target

    fun isSolved(pond: Pond, visited: List<PadCell>): Boolean {
        if (visited.isEmpty()) return false
        if (visited.last() != pond.home) return false
        return pond.pads.all { visited.contains(it) }
    }

    fun isStuck(pond: Pond, visited: List<PadCell>): Boolean {
        if (isSolved(pond, visited)) return false
        return pond.cells.none { canAdvance(pond, visited, it) }
    }
}
