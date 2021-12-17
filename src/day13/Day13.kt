package day13

import util.Grid
import util.Point
import util.readInput
import util.shouldBe
import kotlin.math.abs

fun main() {
    val day = 13
    val testInput = readInput(day, testInput = true).parseInput()
    part1(testInput) shouldBe 17

    val input = readInput(day).parseInput()
    println("output for part1: ${part1(input)}")
    println("output for part2:\n${part2(input)}")
}

private class Fold(val isX: Boolean, val pos: Int)

private class Input(
    val points: Set<Point>,
    val folds: List<Fold>,
)

private fun List<String>.parseInput(): Input {
    val points = mutableSetOf<Point>()
    val folds = mutableListOf<Fold>()
    for (line in this) {
        when {
            line.isBlank() -> continue
            line.first().isDigit() -> {
                val (x, y) = line.split(',').map { it.toInt() }
                points += Point(x, y)
            }
            else -> {
                val (_, dir, pos) = Regex("""^fold along (.)=(\d+)$""")
                    .find(line)!!
                    .groupValues
                folds += Fold(dir == "x", pos.toInt())
            }
        }
    }
    return Input(points, folds)
}

private fun part1(input: Input): Int {
    return input.points.fold(input.folds.first()).size
}

private fun part2(input: Input): String {
    val points = input.folds.fold(input.points) { points, fold -> points.fold(fold) }
    val grid = Grid(points.maxOf { it.x } + 1, points.maxOf { it.y } + 1) { it in points }
    return grid.debugString { if (it) "#" else " " }
}

private fun Set<Point>.fold(fold: Fold) = mapTo(mutableSetOf()) { it.fold(fold) }
private fun Point.fold(fold: Fold) = if (fold.isX) Point(x.fold(fold.pos), y) else Point(x, y.fold(fold.pos))
private fun Int.fold(pos: Int) = pos - abs(pos - this)
