package day07

import util.readInput
import util.shouldBe
import kotlin.math.abs
import kotlin.math.roundToInt

fun main() {
    val testInput = readInput(7, testInput = true).parsePositions()
    part1(testInput) shouldBe 37
    part2(testInput) shouldBe 168

    val input = readInput(7).parsePositions()
    println(part1(input))
    println(part2(input))
}

private fun List<String>.parsePositions() = first().split(",").map { it.toInt() }

private fun part1(input: List<Int>): Int {
    val sortedList = input.sorted()
    val median = when {
        input.size % 2 == 0 -> ((sortedList[input.size / 2 - 1] + sortedList[input.size / 2]) / 2.0).roundToInt()
        else -> sortedList[input.size / 2]
    }
    return input.sumOf { abs(it - median) }
}

private fun part2(input: List<Int>): Int {
    var lastPos = 0
    var lastCost = adjustedFuelCost(0, input)
    while (true) {
        val currCost = adjustedFuelCost(lastPos++, input)
        if (currCost > lastCost) return lastCost
        lastCost = currCost
    }
}

private fun adjustedFuelCost(pos: Int, input: List<Int>) = input.sumOf { adjustedFuelCost(abs(it - pos)) }
private fun adjustedFuelCost(distance: Int) = distance * (distance + 1) / 2
