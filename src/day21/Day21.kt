package day21

import util.crossProductOf
import util.readInput
import util.shouldBe

fun main() {
    val day = 21
    val testInput = readInput(day, testInput = true).parseInput()
    part1(testInput) shouldBe 739785
    part2(testInput) shouldBe 444356092776315L

    val input = readInput(day).parseInput()
    println("output for part1: ${part1(input)}")
    println("output for part2: ${part2(input)}")
}

private class Board(
    val firstPlayer: PlayerData,
    val secondPlayer: PlayerData,
)

private class PlayerData(val index: Int, val pos: Int, val score: Int)

private fun List<String>.parseInput(): Board {
    return Board(
        firstPlayer = PlayerData(0, first().takeLastWhile { it.isDigit() }.toInt() - 1, 0),
        secondPlayer = PlayerData(1, last().takeLastWhile { it.isDigit() }.toInt() - 1, 0),
    )
}

private fun part1(input: Board): Int {
    val die = PracticeDie()
    val scoreThreshold = 1000
    var board = input
    while (!board.hasWinner(scoreThreshold)) board = board.playTurn(die)
    return board.loser(scoreThreshold).score * die.rollCount()
}

private class PracticeDie {
    private var rollCount = 0
    private var state = 0
    fun roll(): Int {
        rollCount++
        val result = state + 1
        state = result % 100
        return result
    }

    fun rollCount() = rollCount
}

private fun Board.playTurn(rolledSum: Int): Board {
    val newPos = (firstPlayer.pos + rolledSum) % 10
    val newScore = firstPlayer.score + newPos + 1
    return Board(
        firstPlayer = secondPlayer,
        secondPlayer = PlayerData(firstPlayer.index, newPos, newScore),
    )
}

private fun Board.playTurn(die: PracticeDie) = playTurn((1..3).sumOf { die.roll() })
private fun Board.hasWinner(scoreThreshold: Int) = secondPlayer.score >= scoreThreshold
private fun Board.winner(scoreThreshold: Int) = secondPlayer.takeIf { hasWinner(scoreThreshold) } ?: error("game has not ended")
private fun Board.loser(scoreThreshold: Int) = firstPlayer.takeIf { hasWinner(scoreThreshold) } ?: error("game has not ended")

private fun part2(input: Board): Long {
    val tally = LongArray(2)
    input.playWithDiracDie(tally)
    return tally.maxOrNull()!!
}

private val possibleDiracDieSums =
    crossProductOf(1..3, 1..3, 1..3) { a, b, c -> a + b + c }
        .groupingBy { it }
        .eachCount()
private const val fullGameScoreThreshold = 21
private fun Board.playWithDiracDie(tally: LongArray, gameCount: Long = 1L) {
    for ((rolledSum, absoluteFrequency) in possibleDiracDieSums) {
        val newGameCount = gameCount * absoluteFrequency
        val newBoard = playTurn(rolledSum)
        if (newBoard.hasWinner(fullGameScoreThreshold)) {
            val winner = newBoard.winner(fullGameScoreThreshold)
            tally[winner.index] += newGameCount
        } else {
            newBoard.playWithDiracDie(tally, newGameCount)
        }
    }
}
