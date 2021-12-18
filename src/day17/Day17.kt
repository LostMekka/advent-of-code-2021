package day17

import util.Point
import util.Rect
import util.readInput
import util.shouldBe
import kotlin.math.sign

fun main() {
    val day = 17
    val testInput = readInput(day, testInput = true).parseInput()
    part1(testInput) shouldBe 45
    part2(testInput) shouldBe 112

    val input = readInput(day).parseInput()
    println("output for part1: ${part1(input)}")
    println("output for part2: ${part2(input)}")
}

private fun List<String>.parseInput(): Rect {
    val (x1, x2, y1, y2) = Regex("""target area: x=(-?\d+)\.\.(-?\d+), y=(-?\d+)\.\.(-?\d+)""")
        .matchEntire(first())!!
        .groupValues
        .drop(1)
        .map { it.toInt() }
    return Rect(x1, y1, x2, y2)
}

private fun part1(target: Rect): Int {
    return (1 until -target.minY).sum()
}

private fun part2(target: Rect): Int {
    val maxVx = target.maxX     // one step already reaches the right end of the target area
    val maxVy = 1 - target.minY // after reaching y==0 again, one step further reaches the bottom end of the target area
    val minVy = target.minY     // one step already reaches the bottom end of the target area
    var count = 0
    for (vx in 0..maxVx) {
        for (vy in minVy..maxVy) {
            var state = State(vx, vy)
            while (state.position.x <= target.maxX && state.position.y >= target.minY) {
                if (state.position in target) {
                    count++
                    break
                }
                state = state.nextStep()
            }
        }
    }
    return count
}

private class State(val position: Point, val velocity: Point) {
    constructor(vx: Int, vy: Int) : this(Point.Zero, Point(vx, vy))

    fun nextStep() = State(
        position = position + velocity,
        velocity = Point(velocity.x - velocity.x.sign, velocity.y - 1),
    )
}
