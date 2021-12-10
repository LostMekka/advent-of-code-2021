package day10

import util.readInput
import util.shouldBe

fun main() {
    val day = 10
    val testInput = readInput(day, testInput = true)
    part1(testInput) shouldBe 26397
    part2(testInput) shouldBe 288957

    val input = readInput(day)
    println(part1(input))
    println(part2(input))
}

private fun part1(input: List<String>): Int {
    return input
        .map { it.validate() }
        .sumOf { (it as? LineCorruptError)?.corruptChunkType?.errorScore() ?: 0 }
}

private fun part2(input: List<String>): Long {
    val errorScores = input
        .mapNotNull { (it.validate() as? LineIncompleteError)?.stack?.errorScore() }
        .sorted()
    return errorScores[errorScores.size / 2]
}

private enum class ChunkOperationType { Open, Close }
private enum class ChunkType(val open: Char, val close: Char) {
    Parenthesis('(', ')'),
    Brackets('[', ']'),
    Braces('{', '}'),
    Sharps('<', '>'),
}

private fun Char.parseOperation(): Pair<ChunkOperationType, ChunkType> {
    for (type in ChunkType.values()) {
        if (this == type.open) return ChunkOperationType.Open to type
        if (this == type.close) return ChunkOperationType.Close to type
    }
    error("unknown char '$this'")
}

private fun ChunkType.errorScore() =
    when (this) {
        ChunkType.Parenthesis -> 3
        ChunkType.Brackets -> 57
        ChunkType.Braces -> 1197
        ChunkType.Sharps -> 25137
    }

private fun List<ChunkType>.errorScore(): Long {
    var score = 0L
    for (type in this.asReversed()) {
        score *= 5
        score += when (type) {
            ChunkType.Parenthesis -> 1
            ChunkType.Brackets -> 2
            ChunkType.Braces -> 3
            ChunkType.Sharps -> 4
        }
    }
    return score
}

private sealed class LineError
private data class LineCorruptError(val corruptChunkType: ChunkType): LineError()
private data class LineIncompleteError(val stack: List<ChunkType>): LineError()

private fun String.validate(): LineError? {
    val stack = mutableListOf<ChunkType>()
    for (char in this) {
        val (op, type) = char.parseOperation()
        when (op) {
            ChunkOperationType.Open -> stack += type
            ChunkOperationType.Close -> {
                if (stack.lastOrNull() != type) return LineCorruptError(type)
                stack.removeLast()
            }
        }
    }
    if (stack.isNotEmpty()) return LineIncompleteError(stack)
    return null
}
