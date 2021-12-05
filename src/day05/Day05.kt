package day05

import util.Point
import util.readInput
import util.shouldBe
import util.Grid

fun main() {
    val testInput = readInput(5, testInput = true) { it.parseLine() }
    part1(testInput) shouldBe 5
    part2(testInput) shouldBe 12

    val input = readInput(5) { it.parseLine() }
    println(part1(input))
    println(part2(input))
}

private fun part1(input: List<Line>): Int {
    val grid = input.createBoundingGrid()
    for (line in input) {
        if (line.isHorizontalOrVertical) grid.drawLine(line)
    }
    return grid.values().count { it >= 2 }
}

private fun part2(input: List<Line>): Int {
    val grid = input.createBoundingGrid()
    for (line in input) grid.drawLine(line)
    return grid.values().count { it >= 2 }
}

private fun String.parseLine(): Line {
    val (x1, y1, x2, y2) = Regex("""(\d+),(\d+) -> (\d+),(\d+)""")
        .matchEntire(this)!!
        .groupValues
        .drop(1)
        .map { it.toInt() }
    return Line(Point(x1, y1), Point(x2, y2))
}

private class Line(val p1: Point, val p2: Point) {
    val isHorizontalOrVertical get() = p1.x == p2.x || p1.y == p2.y
    val coveredPoints by lazy {
        val direction = Point(
            x = when {
                p1.x < p2.x -> 1
                p1.x > p2.x -> -1
                else -> 0
            },
            y = when {
                p1.y < p2.y -> 1
                p1.y > p2.y -> -1
                else -> 0
            },
        )
        buildList {
            var p = p1
            add(p)
            while (p != p2) {
                p += direction
                add(p)
            }
        }
    }
}

private fun List<Line>.createBoundingGrid() =
    Grid(
        width = maxOf { maxOf(it.p1.x, it.p2.x) } + 1,
        height = maxOf { maxOf(it.p1.y, it.p2.y) } + 1,
        init = { 0 },
    )

private fun Grid<Int>.drawLine(line: Line) {
    for (p in line.coveredPoints) this[p] += 1
}
