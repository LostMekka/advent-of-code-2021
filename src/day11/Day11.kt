package day11

import util.Grid
import util.Point
import util.readInput
import util.shouldBe
import util.with8BitColor

fun main() {
    val day = 11
    val testInput = readInput(day, testInput = true)
    part1(testInput) shouldBe 1656
    part2(testInput) shouldBe 195

    val input = readInput(day)
    println(part1(input))
    println(part2(input))
}

private fun part1(input: List<String>): Int {
    val state = State(input)
    var blinkCount = 0
    repeat(100) { blinkCount += state.simulateStep() }
    return blinkCount
}

private fun part2(input: List<String>): Int {
    val state = State(input)
    var stepCount = 0
    while (true) {
        state.simulateStep()
        stepCount++
        if (state.grid.values().all { it == 0 }) return stepCount
    }
}

private class State(input: List<String>) {
    val grid = Grid(input.first().length, input.size) { (x, y) -> input[y][x].digitToInt() }

    fun simulateStep(): Int {
        val toBlink = mutableSetOf<Point>()
        val blinkDone = mutableSetOf<Point>()
        for (p in grid.positions()) {
            val energy = grid[p] + 1
            grid[p] = energy
            if (energy > 9) toBlink += p
        }
        while (toBlink.isNotEmpty()) {
            val p = toBlink.first()
            toBlink -= p
            blinkDone += p
            grid[p] = 0
            for (n in p.neighboursIncludingDiagonals()) {
                if (n !in grid || n in blinkDone) continue
                val energy = grid[n] + 1
                grid[n] = energy
                if (energy > 9) toBlink += n
            }
        }
        return blinkDone.size
    }

    override fun toString() = grid.debugString {
        val color = it.coerceIn(0..10) * 2 + 235
        val string = if (it > 9) "X" else it.toString()
        string.with8BitColor(color, 0)
    }
}
