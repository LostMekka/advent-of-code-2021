package day04

import util.readInput
import util.shouldBe

fun main() {
    val testInput = readInput(4, testInput = true)
    part1(testInput) shouldBe 4512
    part2(testInput) shouldBe 1924

    val input = readInput(4)
    println(part1(input))
    println(part2(input))
}

private fun part1(input: List<String>): Int {
    val (numbers, boards) = input.parseInput()
    for (number in numbers) {
        for (board in boards) {
            board.markAll(number)
            if (board.isDone) return number * board.unmarkedSum
        }
    }
    throw Exception("oops, no board won!")
}

private fun part2(input: List<String>): Int {
    val (numbers, allBoards) = input.parseInput()
    val boardsLeft = allBoards.toMutableList()
    for (number in numbers) {
        val currentBoards = boardsLeft.toList()
        for (board in boardsLeft) board.markAll(number)
        boardsLeft.removeAll { it.isDone }
        if (boardsLeft.isEmpty()) return number * currentBoards.random().unmarkedSum
    }
    throw Exception("oops, not all boards won!")
}

private fun List<String>.parseInput(): Input {
    val numbers = first().split(",").map { it.toInt() }
    val boards = drop(1).chunked(6) { lines ->
        lines.joinToString(" ")
            .trim()
            .split(Regex("""\s+"""))
            .map { it.toInt() }
            .toIntArray()
            .let { Board(it) }
    }
    return Input(numbers, boards)
}

private data class Input(val numbers: List<Int>, val boards: List<Board>)

private class Board(val data: IntArray) {
    val marks = BooleanArray(25)
    init { require(data.size == 25) }
    private fun index(x: Int, y: Int) = x + 5 * y
    operator fun get(x: Int, y: Int) = data[index(x, y)]
    fun markAll(number: Int) {
        for (i in marks.indices) {
            if (data[i] == number) marks[i] = true
        }
    }
    val isDone: Boolean get() {
        for (i in 0..4) {
            if (isRowMarked(i)) return true
            if (isColumnMarked(i)) return true
        }
        return false
    }
    fun isRowMarked(y: Int) = (0..4).all { marks[index(it, y)] }
    fun isColumnMarked(x: Int) = (0..4).all { marks[index(x, it)] }
    val unmarkedSum get() = marks.indices.sumOf { if (marks[it]) 0 else data[it] }
}
