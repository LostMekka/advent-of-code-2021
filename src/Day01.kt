fun main() {
    fun part1(input: List<String>): Int =
        input.asSequence()
            .map { it.toInt() }
            .zipWithNext()
            .count { (a, b) -> a < b }

    fun part2(input: List<String>): Int =
        input.asSequence()
            .map { it.toInt() }
            .windowed(3)
            .map { it.sum() }
            .zipWithNext()
            .count { (a, b) -> a < b }

    val testInput = readInput(1, testInput = true)
    part1(testInput) shouldBe 7
    part2(testInput) shouldBe 5

    val input = readInput(1)
    println(part1(input))
    println(part2(input))
}
