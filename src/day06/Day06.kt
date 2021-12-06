package day06

import util.readInput
import util.shouldBe

fun main() {
    val testInput = readInput(6, testInput = true).toCountArray()
    part1(testInput) shouldBe 5934L
    part2(testInput) shouldBe 26984457539L

    val input = readInput(6).toCountArray()
    println(part1(input))
    println(part2(input))
}

private fun part1(input: LongArray): Long {
    val counts = input.copyOf()
    repeat(80) { counts.simulateOneDay() }
    return counts.sum()
}

private fun part2(input: LongArray): Long {
    val counts = input.copyOf()
    repeat(256) { counts.simulateOneDay() }
    return counts.sum()
}

private fun List<String>.toCountArray(): LongArray {
    val countsByState = single()
        .split(",")
        .map { it.toInt() }
        .groupingBy { it }
        .eachCount()
    return LongArray(9) { countsByState[it]?.toLong() ?: 0L }
}

private fun LongArray.simulateOneDay() {
    val done = this[0]
    for (i in 0 until size - 1) this[i] = this[i + 1]
    this[6] += done
    this[8] = done
}
