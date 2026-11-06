package com.ripple.lilypath.domain.usecase

import com.ripple.lilypath.domain.model.PadCell
import com.ripple.lilypath.domain.model.Pond

class NextHintUseCase(private val validateMove: ValidateMoveUseCase) {

    operator fun invoke(pond: Pond, visited: List<PadCell>): PadCell? {
        if (visited.isEmpty()) return pond.start
        val solution = pond.solution
        val followsSolution = visited.size < solution.size &&
            visited.indices.all { solution[it] == visited[it] }
        if (followsSolution) return solution[visited.size]
        return pond.cells.firstOrNull { validateMove.canAdvance(pond, visited, it) }
    }
}
