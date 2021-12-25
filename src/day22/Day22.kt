package day22

import util.Cuboid
import util.Point3
import util.component6
import util.readInput
import util.shouldBe

fun main() {
    val day = 22
    val testInput = readInput(day, testInput = true).parseInput()
    part1(testInput) shouldBe 474140
    part2(testInput) shouldBe 2758514936282235L

    val input = readInput(day).parseInput()
    println("output for part1: ${part1(input)}")
    println("output for part2: ${part2(input)}")
}

private data class Instruction(
    val on: Boolean,
    val area: Cuboid,
)

private val inputRegex = Regex("""^(on|off) x=(-?\d+)\.\.(-?\d+),y=(-?\d+)\.\.(-?\d+),z=(-?\d+)\.\.(-?\d+)$""")
private fun List<String>.parseInput(): List<Instruction> =
    map { line ->
        val values = inputRegex.matchEntire(line)!!.groupValues
        val (x1,x2,y1,y2,z1,z2) = values.drop(2).map { it.toInt() }
        Instruction(
            on = values[1] == "on",
            area = Cuboid(x1, y1, z1, x2, y2, z2),
        )
    }

private fun part1(instructions: List<Instruction>): Int {
    val reactorArea = Cuboid(-50, -50, -50, 50, 50, 50)
    val points = mutableSetOf<Point3>()
    for ((on, area) in instructions) {
        val set = area.intersect(reactorArea) ?: continue
        when (on) {
            true -> points += set
            false -> points -= set
        }
    }
    return points.size
}

private fun part2(instructions: List<Instruction>): Long {
    var existingAreas = listOf<Node>()
    for ((on, newArea) in instructions) {
        val adjustedAreas = existingAreas.mapNotNull { it.without(newArea) }
        existingAreas = if (on) adjustedAreas + Node(newArea) else adjustedAreas
    }
    return existingAreas.sumOf { it.size() }
}

private class Node(
    val area: Cuboid,
    val exceptions: List<Node> = emptyList(),
) {
    fun size(): Long = area.size() - exceptions.sumOf { it.size() }
    fun without(cutout: Cuboid): Node? {
        if (area in cutout) return null
        val intersection = area.intersect(cutout) ?: return this
        return Node(area, exceptions.mapNotNull { it.without(intersection) } + Node(intersection))
    }
}
