package day14

import util.readInput
import util.shouldBe

fun main() {
    val day = 14
    val testInput = readInput(day, testInput = true).parseInput()
    part1(testInput) shouldBe 1588
    part2(testInput) shouldBe 2188189693529

    val input = readInput(day).parseInput()
    println("output for part1: ${part1(input)}")
    println("output for part2: ${part2(input)}")
}

private class Rule(val key: Pair<Char, Char>, val insert: Char)
private class Input(val start: String, val rules: List<Rule>)

private fun List<String>.parseInput(): Input {
    return Input(
        start = first(),
        rules = drop(2).map { line ->
            val (a, b, c) = Regex("""^(.)(.) -> (.)$""")
                .matchEntire(line)!!.groupValues
                .drop(1)
                .map { it.single() }
            Rule(a to b, c)
        }
    )
}

private fun part1(input: Input): Long {
    return simulate(input, 10)
}

private fun part2(input: Input): Long {
    return simulate(input, 40)
}

private class Pairs(val lastChar: Char) : Iterable<Pair<Pair<Char, Char>, Long>> {
    private val map = mutableMapOf<Pair<Char, Char>, Long>()

    operator fun get(pair: Pair<Char, Char>) = map[pair] ?: 0L
    operator fun set(pair: Pair<Char, Char>, count: Long) {
        map[pair] = count
    }

    override fun iterator(): Iterator<Pair<Pair<Char, Char>, Long>> = iterator {
        for ((key, count) in map) yield(key to count)
    }

    fun counts() = map.entries
        .map { (pair, count) -> pair.first to count }
        .let { it + (lastChar to 1L) }
        .groupingBy { it.first }
        .fold(0L) { acc, (_, count) -> acc + count }
        .values
}

private fun simulate(input: Input, iterations: Int): Long {
    var state = input.start.toPairs()
    repeat(iterations) { state = state.insert(input.rules) }
    return state
        .counts()
        .sorted()
        .let { it.last() - it.first() }
}

private fun String.toPairs(): Pairs {
    val pairs = Pairs(last())
    windowed(2)
        .groupingBy { it }
        .eachCount()
        .forEach { (pair, count) -> pairs[pair.first() to pair.last()] = count.toLong() }
    return pairs
}

private fun Pairs.insert(rules: List<Rule>): Pairs {
    val pairs = Pairs(lastChar)
    for ((key, count) in this) {
        val insert = rules.find { it.key == key }?.insert
        if (insert == null) {
            pairs[key] += count
        } else {
            val (a, b) = key
            pairs[a to insert] += count
            pairs[insert to b] += count
        }
    }
    return pairs
}
