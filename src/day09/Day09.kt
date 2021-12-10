package day09

import util.Grid
import util.Point
import util.readInput
import util.shouldBe
import java.util.LinkedList

fun main() {
    val testInput = readInput(9, testInput = true).parseHeightMap()
    part1(testInput) shouldBe 15
    part2(testInput) shouldBe 1134

    val input = readInput(9).parseHeightMap()
    println(part1(input))
    println(part2(input))
}

private fun List<String>.parseHeightMap() =
    Grid(first().length, size) { (x, y) -> this[y][x].digitToInt() }

private fun part1(input: Grid<Int>): Int {
    return input.lowPoints().sumOf { input[it] + 1 }
}

private fun part2(input: Grid<Int>): Int {
    return input.lowPoints()
        .map { input.expandBasin(it).size }
        .sortedDescending()
        .let { (a, b, c) -> a * b * c }
}

private fun Grid<Int>.lowPoints(): List<Point> =
    positions().filter { isLowPoint(it) }

private fun Grid<Int>.isLowPoint(p: Point): Boolean {
    val height = this[p]
    return height < 9 && p.neighbours().all { it !in this || this[it] > height }
}

private fun Grid<Int>.expandBasin(lowPoint: Point): Set<Point> {
    val toExpand = LinkedList(listOf(lowPoint))
    val basin = mutableSetOf<Point>()
    while (toExpand.isNotEmpty()) {
        val p = toExpand.removeFirst()
        basin += p
        toExpand += p.neighbours().filter { it in this && it !in basin && this[it] < 9 }
    }
    return basin
}
