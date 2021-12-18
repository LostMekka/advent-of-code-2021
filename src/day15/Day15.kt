package day15

import util.Grid
import util.Point
import util.readInput
import util.shouldBe
import util.toGrid
import java.util.PriorityQueue
import kotlin.math.abs
import kotlin.math.sign

fun main() {
    val day = 15
    val testInput = readInput(day, testInput = true).parseInput()
    part1(testInput) shouldBe 40
    part2(testInput) shouldBe 315

    val input = readInput(day).parseInput()
    println("output for part1: ${part1(input)}")
    println("output for part2: ${part2(input)}")
}

private class Input(
    val grid: Grid<Int>,
)

private fun List<String>.parseInput(): Input {
    return Input(toGrid { it.digitToInt() })
}

private class SearchNode(
    val pos: Point,
    val costUntilNow: Int,
    val estimatedCostToGo: Int,
) : Comparable<SearchNode> {
    val totalEstimatedCost = costUntilNow + estimatedCostToGo
    override fun equals(other: Any?) = other is SearchNode && pos == other.pos
    override fun hashCode() = pos.hashCode()
    override fun compareTo(other: SearchNode) = (totalEstimatedCost - other.totalEstimatedCost).sign
}

private fun part1(input: Input): Int {
    return findLowestPathCost(input.grid)
}

private fun part2(input: Input): Int {
    val w = input.grid.width
    val h = input.grid.height
    val grid = Grid(w * 5, h * 5) { (x, y) ->
        val originalCost = input.grid[x % w, y % h]
        val additionalCost = x / w + y / h
        (originalCost + additionalCost - 1) % 9 + 1
    }
    return findLowestPathCost(grid)
}

private fun findLowestPathCost(grid: Grid<Int>): Int {
    val start = Point(0, 0)
    val end = Point(grid.width - 1, grid.height - 1)
    val toExpand = PriorityQueue<SearchNode>(grid.width * grid.height)
    toExpand += SearchNode(start, 0, start estimatedCostTo end)
    val expanded = mutableSetOf<Point>()
    while (toExpand.isNotEmpty()) {
        val node = toExpand.remove()
        expanded += node.pos
        for (p in node.pos.neighbours()) {
            if (p in expanded || p !in grid) continue
            val costUntilNow = node.costUntilNow + grid[p]
            if (p == end) return costUntilNow
            toExpand += SearchNode(
                pos = p,
                costUntilNow = costUntilNow,
                estimatedCostToGo = p estimatedCostTo end,
            )
        }
    }
    error("no valid path found")
}

private infix fun Point.estimatedCostTo(other: Point) = abs(x - other.x) + abs(y - other.y)
