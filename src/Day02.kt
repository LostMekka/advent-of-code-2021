fun main() {
    val testInput = readInput(2, testInput = true) { parseMove(it) }
    part1(testInput) shouldBe 150
    part2(testInput) shouldBe 900

    val input = readInput(2) { parseMove(it) }
    println(part1(input))
    println(part2(input))
}

private fun part1(input: List<Move>): Int {
    var dx = 0
    var dy = 0
    for ((direction, amount) in input) {
        when (direction) {
            Direction.Forward -> dx += amount
            Direction.Down -> dy += amount
            Direction.Up -> dy -= amount
        }
    }
    return dx * dy
}

private fun part2(input: List<Move>): Int {
    var aim = 0
    var dx = 0
    var dy = 0
    for ((direction, amount) in input) {
        when (direction) {
            Direction.Forward -> {
                dx += amount
                dy += aim * amount
            }
            Direction.Down -> aim += amount
            Direction.Up -> aim -= amount
        }
    }
    return dx * dy
}

private enum class Direction { Forward, Down, Up }
private data class Move(val direction: Direction, val amount: Int)

private fun parseMove(it: String): Move {
    val (first, second) = it.split(' ')
    return Move(
        direction = Direction.values().first { it.name.equals(first, ignoreCase = true) },
        amount = second.toInt(),
    )
}
