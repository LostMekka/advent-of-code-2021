package day25

import util.Grid
import util.Point
import util.readInput
import util.shouldBe

fun main() {
    val day = 25
    val testInput = readInput(day, testInput = true)
    part1(testInput) shouldBe 58

    val input = readInput(day)
    println("output for part1: ${part1(input)}")
    // no part two here...
}

private enum class Direction { Right, Down }
private typealias Map = Grid<Direction?>

private fun List<String>.parseInput(): Grid<Direction?> {
    return Grid(first().length, size) { (x, y) ->
        when (this[y][x]) {
            '>' -> Direction.Right
            'v' -> Direction.Down
            '.' -> null
            else -> error("unknown char found!")
        }
    }
}

private fun part1(input: List<String>): Int {
    val map = input.parseInput()
    var i = 0
    while (true) {
        i++
        if (!map.simulateStep()) break
    }
    return i
}

private fun Map.getOffset(p: Point, direction: Direction) =
    when (direction) {
        Direction.Right -> Point((p.x + 1) % width, p.y)
        Direction.Down -> Point(p.x, (p.y + 1) % height)
    }

private fun Map.simulateStep(): Boolean {
    var somethingMoved = false
    for (direction in Direction.values()) {
        val freePositions = positions().filter { this[it] == null }.toSet()
        val sourcePositions = positions().filter { this[it] == direction }.toSet()
        for (source in sourcePositions) {
            val target = getOffset(source, direction)
            if (target in freePositions) {
                this[source] = null
                this[target] = direction
                somethingMoved = true
            }
        }
    }
    return somethingMoved
}

@Suppress("unused")
private fun Map.toDebugString() =
    debugString { dir ->
        when (dir) {
            Direction.Right -> ">"
            Direction.Down -> "v"
            null -> "."
        }
    }
