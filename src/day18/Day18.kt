package day18

import day18.Direction.Left
import day18.Direction.Right
import util.plus
import util.readInput
import util.shouldBe

fun main() {
    val day = 18
    val testInput = readInput(day, testInput = true).parseInput()
    part1(testInput) shouldBe 4140
    part2(testInput) shouldBe 3993

    val input = readInput(day).parseInput()
    println("output for part1: ${part1(input)}")
    println("output for part2: ${part2(input)}")
}

private typealias Path = List<Direction>

private enum class Direction { Left, Right }

private val Direction.opposite
    get() = when (this) {
        Left -> Right
        Right -> Left
    }

private sealed class Number {
    var parent: PairedNumber? = null
    abstract val magnitude: Int
    abstract var path: Path
    abstract fun clone(): Number
    operator fun get(path: Path): Number {
        var number = this
        for (direction in path) {
            if (number !is PairedNumber) error("invalid path")
            number = number[direction]
        }
        return number
    }
}

private class LiteralNumber(
    var value: Int,
    override var path: Path,
) : Number() {
    override fun clone() = LiteralNumber(value, path)
    override val magnitude get() = value
    override fun toString() = value.toString()
}

private class PairedNumber(
    var left: Number,
    var right: Number,
    override var path: Path,
) : Number() {
    init {
        left.parent = this
        right.parent = this
    }
    override val magnitude get() = 3 * left.magnitude + 2 * right.magnitude
    override fun clone() = PairedNumber(left.clone(), right.clone(), path)
    operator fun get(direction: Direction) =
        when (direction) {
            Left -> left
            Right -> right
        }

    operator fun set(direction: Direction, value: Number) =
        when (direction) {
            Left -> left = value
            Right -> right = value
        }

    override fun toString() = "[$left,$right]"
}

private fun List<String>.parseInput() = map { it.parseNumber() }
private fun String.parseNumber() = iterator().parseNumber(emptyList())

private fun CharIterator.parseNumber(path: Path): Number {
    val next = nextChar()
    val digit = next.digitToIntOrNull()
    return if (digit != null) {
        LiteralNumber(digit, path)
    } else {
        val left = parseNumber(path + Left)
        nextChar()
        val right = parseNumber(path + Right)
        nextChar()
        PairedNumber(left, right, path)
    }
}

private fun part1(input: List<Number>): Int {
    return input.reduce { a, b -> a + b }.magnitude
}

private fun part2(input: List<Number>): Int {
    var max = 0
    for ((i1, n1) in input.withIndex()) {
        for ((i2, n2) in input.withIndex()) {
            if (i2 == i1) continue
            max = maxOf(max, (n1 + n2).magnitude)
        }
    }
    return max
}

private operator fun Number.plus(other: Number): Number {
    val a = this.clone()
    val b = other.clone()
    val result = PairedNumber(a, b, emptyList())
    a.onSetAsChild(Left)
    b.onSetAsChild(Right)
    result.reduce()
    return result
}

private fun Number.onSetAsChild(direction: Direction) {
    path = direction + path
    if (this is PairedNumber) {
        left.onSetAsChild(direction)
        right.onSetAsChild(direction)
    }
}

private fun Number.reduce() {
    while (true) {
        val toExplode = findPair { it.path.size >= 4 }
        if (toExplode != null) {
            explode(toExplode)
            continue
        }
        val toSplit = findLiteral { it.value >= 10 }
        if (toSplit != null) {
            split(toSplit)
            continue
        }
        return
    }
}

private fun Number.explode(target: PairedNumber) {
    val leftValue = target.left.requireLiteral().value
    val rightValue = target.right.requireLiteral().value
    val newLiteral = LiteralNumber(0, target.path)
    replace(target, newLiteral)
    neighbouringLiteralOf(target.path, Left)?.let { it.value += leftValue }
    neighbouringLiteralOf(target.path, Right)?.let { it.value += rightValue }
}

private fun Number.split(target: LiteralNumber) {
    val targetPath = target.path
    val newPair = PairedNumber(
        left = LiteralNumber(target.value / 2, targetPath + Left),
        right = LiteralNumber((target.value + 1) / 2, targetPath + Right),
        path = targetPath,
    )
    replace(target, newPair)
}

private fun Number.replace(target: Number, replacement: Number) {
    require(target !== this) { "cannot replace root number" }
    val parent = target.parent!!
    parent[target.path.last()] = replacement
    replacement.parent = parent
}

private fun Number.neighbouringLiteralOf(targetPath: Path, direction: Direction): LiteralNumber? {
    val oppositeDirection = direction.opposite
    val depthToFlip = targetPath.lastIndexOf(oppositeDirection)
    if (depthToFlip < 0) return null
    var number = this
    for (i in 0 until depthToFlip) {
        number = number.requirePair()[targetPath[i]]
    }
    number = number.requirePair()[direction]
    while (true) {
        when (number) {
            is LiteralNumber -> return number
            is PairedNumber -> number = number[oppositeDirection]
        }
    }
}

private fun Number.requireLiteral() = this as? LiteralNumber ?: error("this number is required to be a literal")
private fun Number.requirePair() = this as? PairedNumber ?: error("this number is required to be a pair")

private fun Number.findLiteral(predicate: (LiteralNumber) -> Boolean): LiteralNumber? =
    when (this) {
        is LiteralNumber -> takeIf(predicate)
        is PairedNumber -> left.findLiteral(predicate) ?: right.findLiteral(predicate)
    }

private fun Number.findPair(predicate: (PairedNumber) -> Boolean): PairedNumber? =
    when (this) {
        is LiteralNumber -> null
        is PairedNumber -> takeIf(predicate) ?: left.findPair(predicate) ?: right.findPair(predicate)
    }
