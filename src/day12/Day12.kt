package day12

import util.readInput
import util.shouldBe

fun main() {
    val day = 12
    val testInput = readInput(day, testInput = true).parseInput()
    part1(testInput) shouldBe 10
    part2(testInput) shouldBe 36

    val input = readInput(day).parseInput()
    println("output for part1: ${part1(input)}")
    println("output for part2: ${part2(input)}")
}

private class Node(val name: String) {
    val isStart = name == "start"
    val isEnd = name == "end"
    val canVisitInfiniteTimes = name.all { it.isUpperCase() }
    val canVisitTwice = !canVisitInfiniteTimes && !isStart && !isEnd
    override fun hashCode(): Int = name.hashCode()
    override fun equals(other: Any?): Boolean = this === other || (other is Node && name == other.name)
}

private class Input(
    nodes: Set<Node>,
    val links: Map<Node, Set<Node>>,
) {
    val startNode = nodes.single { it.isStart }
}

private fun List<String>.parseInput(): Input {
    val nodes = mutableSetOf<Node>()
    val links = mutableMapOf<Node, MutableSet<Node>>()
    for (line in this) {
        val (a, b) = line.split('-').map { Node(it) }
        nodes += a
        nodes += b
        links.getOrPut(a) { mutableSetOf() } += b
        links.getOrPut(b) { mutableSetOf() } += a
    }
    return Input(nodes, links)
}

private fun part1(input: Input): Int {
    return input.countPathsToEnd(input.startNode)
}

private fun part2(input: Input): Int {
    return input.countPathsToEnd(input.startNode, doubleVisitAllowed = true)
}

private fun Input.countPathsToEnd(
    currentNode: Node,
    visitedNodes: Set<Node> = emptySet(),
    doubleVisitAllowed: Boolean = false,
): Int {
    val newVisitedNodes = visitedNodes + currentNode
    return links
        .getValue(currentNode)
        .sumOf {
            when {
                it in visitedNodes -> when {
                    it.canVisitInfiniteTimes -> countPathsToEnd(it, newVisitedNodes, doubleVisitAllowed)
                    it.canVisitTwice && doubleVisitAllowed -> countPathsToEnd(it, newVisitedNodes, false)
                    else -> 0
                }
                it.isEnd -> 1
                else -> countPathsToEnd(it, newVisitedNodes, doubleVisitAllowed)
            }
        }
}
