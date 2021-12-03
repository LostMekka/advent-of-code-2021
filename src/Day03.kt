fun main() {
    val testInput = readInput(3, testInput = true)
    part1(testInput) shouldBe 198
    part2(testInput) shouldBe 230

    val input = readInput(3)
    println(part1(input))
    println(part2(input))
}

private fun part1(input: List<String>): Int {
    val length = input.first().length
    val oneCounts = CharArray(length) { input.mostCommonDigit(it) }
    val gammaRate = oneCounts
        .joinToString("")
        .toInt(radix = 2)
    val epsilonRate = oneCounts
        .joinToString("") { it.flip().toString() }
        .toInt(radix = 2)
    return gammaRate * epsilonRate
}

private fun part2(input: List<String>): Int {
    val oxygenGeneratorRating = input.findValue(takeLeastCommonDigit = false).toInt(radix = 2)
    val co2ScrubberRating = input.findValue(takeLeastCommonDigit = true).toInt(radix = 2)
    return oxygenGeneratorRating * co2ScrubberRating
}

private fun List<String>.findValue(takeLeastCommonDigit: Boolean): String {
    val possibleValues = toMutableList()
    var i = 0
    while (possibleValues.size != 1) {
        val digit = possibleValues
            .mostCommonDigit(i)
            .let { if (takeLeastCommonDigit) it.flip() else it }
        possibleValues.retainAll { it[i] == digit }
        i++
    }
    return possibleValues.single()
}

private fun List<String>.mostCommonDigit(i: Int): Char {
    val doubleOneCount = 2 * count { it[i] == '1' }
    return if (doubleOneCount >= size) '1' else '0'
}

private fun Char.flip() = if (this == '1') '0' else '1'
