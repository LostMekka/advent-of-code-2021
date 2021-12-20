package day20

import util.Point
import util.Rect
import util.boundingRect
import util.readInput
import util.shouldBe

fun main() {
    val day = 20
    val testInput = readInput(day, testInput = true).parseInput()
    part1(testInput) shouldBe 35
    part2(testInput) shouldBe 3351

    val input = readInput(day).parseInput()
    println("output for part1: ${part1(input)}")
    println("output for part2: ${part2(input)}")
}

private class Input(
    val rules: BooleanArray,
    val image: Set<Point>,
)

private fun List<String>.parseInput(): Input {
    return Input(
        rules = first().map { it == '#' }.toBooleanArray(),
        image = buildSet {
            for ((y, line) in this@parseInput.drop(2).withIndex()) {
                for ((x, char) in line.withIndex()) {
                    if (char == '#') add(Point(x, y))
                }
            }
        }
    )
}

private fun part1(input: Input): Int {
    var state = State(input.image)
    repeat(2) { state = state.iterate(input.rules) }
    return state.image.size
}

private fun part2(input: Input): Int {
    var state = State(input.image)
    repeat(50) { state = state.iterate(input.rules) }
    return state.image.size
}

private class State(
    val image: Set<Point>,
    val bounds: Rect = image.boundingRect(),
    val defaultPixelValue: Boolean = false,
) {
    fun pixelValueAt(pos: Point) = if (pos in bounds) pos in image else defaultPixelValue
    fun codeAt(pos: Point): Int {
        var code = 0
        for (y in -1..1) {
            for (x in -1..1) {
                code *= 2
                if (pixelValueAt(Point(pos.x + x, pos.y + y))) code++
            }
        }
        return code
    }
}

private fun State.iterate(rules: BooleanArray): State {
    val newBounds = bounds.expandedBy(1)
    return State(
        image = buildSet {
            for (p in newBounds) {
                if (rules[codeAt(p)]) add(p)
            }
        },
        bounds = newBounds,
        defaultPixelValue = if (defaultPixelValue) rules[511] else rules[0],
    )
}
