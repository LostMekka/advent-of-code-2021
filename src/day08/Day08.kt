package day08

import util.Grid
import util.readInput
import util.shouldBe

//  aaaa            aaaa    aaaa
// b    c       c       c       c  b    c
// b    c       c       c       c  b    c
//                 dddd    dddd    dddd
// e    f       f  e            f       f
// e    f       f  e            f       f
//  gggg            gggg    gggg
//
//  aaaa    aaaa    aaaa    aaaa    aaaa
// b       b            c  b    c  b    c
// b       b            c  b    c  b    c
//  dddd    dddd            dddd    dddd
//      f  e    f       f  e    f       f
//      f  e    f       f  e    f       f
//  gggg    gggg            gggg    gggg
//
//   a b c d e f g
// 0 x x x   x x x  (6)    (2): 1     - cf      in common
// 1     x     x    (2)    (3): 7     - acf     in common
// 2 x   x x x   x  (5)    (4): 4     - bcdf    in common
// 3 x   x x   x x  (5)    (5): 2,3,5 - adg     in common
// 4   x x x   x    (4)    (6): 0,6,9 - abfg    in common
// 5 x x   x   x x  (5)    (7): 8     - abcdefg in common
// 6 x x   x x x x  (6)
// 7 x   x     x    (3)
// 8 x x x x x x x  (7)
// 9 x x x x   x x  (6)
//   a b c d e f g

fun main() {
    val testInput = readInput(8, testInput = true) { it.parseData() }
    part1(testInput) shouldBe 26
    part2(testInput) shouldBe 61229

    val input = readInput(8) { it.parseData() }
    println(part1(input))
    println(part2(input))
}

private data class Data(val left: List<String>, val right: List<String>)

private fun String.parseData(): Data {
    val (left, right) = split(" | ")
    return Data(
        left = left.split(" ").map { it.sorted() },
        right = right.split(" ").map { it.sorted() },
    )
}

private fun part1(input: List<Data>): Int {
    return input
        .flatMap { it.right }
        .count { it.length in listOf(2, 3, 4, 7) }
}

private fun part2(input: List<Data>): Int {
    return input.sumOf { solveSingleLine(it) }
}

private fun solveSingleLine(data: Data): Int {
    val grid = Grid(7, 7) { true }
    for ((length, inputDigits) in data.left.groupBy { it.length }) {
        val inputLettersInCommon = inputDigits.lettersInCommon
        val outputLettersInCommon = when (length) {
            2 -> "cf"
            3 -> "acf"
            4 -> "bcdf"
            5 -> "adg"
            6 -> "abfg"
            7 -> "abcdefg"
            else -> error("if this error is thrown, i messed up badly")
        }
        grid.deduce(inputLettersInCommon, outputLettersInCommon)
    }
    println(grid.toDebugString() + "\n\n")
    return data.right
        .map { grid.solve(it).parseSevenSegmentDigit() }
        .reduce { a, b -> 10 * a + b }
}

private fun Grid<Boolean>.solve(input: String) =
    buildString {
        for (letter in input) {
            append(getSolvedLetterForInput(letter) ?: error("grid is not solved yet"))
        }
    }

private fun String.parseSevenSegmentDigit(): Int =
    when (val sortedInput = sorted()) {
        "abcefg" -> 0
        "cf" -> 1
        "acdeg" -> 2
        "acdfg" -> 3
        "bcdf" -> 4
        "abdfg" -> 5
        "abdefg" -> 6
        "acf" -> 7
        "abcdefg" -> 8
        "abcdfg" -> 9
        else -> error("'$this' ($sortedInput) is not a seven segment digit")
    }

private fun String.sorted() = toList().sorted().joinToString("")

private const val letters = "abcdefg"
private val List<String>.lettersInCommon get() = letters.filter { letter -> all { letter in it } }
private val Char.index get() = code - 97
private val Int.letter get() = (this + 97).toChar()
private operator fun Grid<Boolean>.get(input: Char, output: Char) = get(input.index, output.index)
private operator fun Grid<Boolean>.set(input: Char, output: Char, value: Boolean) =
    set(input.index, output.index, value)

private fun Grid<Boolean>.deduce(input: String, output: String) {
    require(input.length == output.length)
    for (i in letters) {
        for (inputLetter in input) this[inputLetter, i] = this[inputLetter, i] && i in output
        for (outputLetter in output) this[i, outputLetter] = this[i, outputLetter] && i in input
    }
}

private fun Grid<Boolean>.getSolvedLetterForInput(input: Char): Char? =
    column(input.index).withIndex().singleOrNull { it.value }?.index?.letter

@Suppress("unused")
private fun Grid<Boolean>.toDebugString() =
    letters.toList().joinToString(" ", "  ", "\n") +
        letters.toList().joinToString("\n") { y ->
            y + " " + letters.toList().joinToString(" ") { x -> if (this[x, y]) "X" else "." }
        }
// prints something like this:
//   a b c d e f g
// a . . . . . . X
// b . . X . . . .
// c . . . X . . .
// d . . . . . X .
// e . X . . . . .
// f X . . . . . .
// g . . . . X . .
// shuffled input comes from the top,
// corrected output goes out to the right
