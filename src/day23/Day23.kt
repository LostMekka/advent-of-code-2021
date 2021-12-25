package day23

import util.PathFindingMove
import util.PathFindingState
import util.findPath
import util.mutate
import util.readInput
import util.shouldBe
import java.util.Objects
import kotlin.math.abs

fun main() {
    val day = 23
    val testInput = readInput(day, testInput = true)
    part1(testInput) shouldBe 12521
    part2(testInput) shouldBe 44169

    val input = readInput(day)
    println("output for part1: ${part1(input)}")
    println("output for part2: ${part2(input)}")
}

private val allStoragePositions = (0..10).toSet()
private val forbiddenStoragePositions = Type.values().map { it.roomPosition }.toSet()
private val allowedStoragePositions = allStoragePositions - forbiddenStoragePositions
private class State(val roomLength: Int, val storage: List<Type?>, val rooms: List<Room>): PathFindingState<State> {
    override fun nextMoves() = sequence {
        for (storagePos in allowedStoragePositions) {
            for (roomIndex in 0..3) {
                moveIn(storagePos, roomIndex)?.also { yield(it) }
                moveOut(roomIndex, storagePos)?.also { yield(it) }
            }
        }
    }

    override fun estimatedCostToGo(): Long {
        var sum = 0L
        for (i in allowedStoragePositions) {
            val type = storage[i] ?: continue
            sum += abs(i - type.roomPosition) * type.costMultiplier
        }
        for (room in rooms) {
            val first = room[0]
            if (first != room.desiredType){
                sum += room.desiredType.costMultiplier
                if (first != null) sum += (abs(room.position - first.roomPosition) + 1) * first.costMultiplier
            }
            val second = room[1]
            if (second != room.desiredType) {
                sum += room.desiredType.costMultiplier * 2
                if (second != null) sum += (abs(room.position - second.roomPosition) + 2) * second.costMultiplier
            }
        }
        return sum
    }

    override fun isGoal() = rooms.all { it.isSolved() }
    override fun equals(other: Any?) = other is State && storage == other.storage && rooms == other.rooms
    override fun hashCode() = Objects.hash(storage, rooms)
}
private class Room(val desiredType: Type, val content: List<Type?>) {
    override fun equals(other: Any?) = other is Room && content == other.content && desiredType == other.desiredType
    override fun hashCode() = Objects.hash(content, desiredType)
}
private enum class Type { A, B, C, D }

private fun List<String>.parseInput(): State {
    val roomLines = drop(2).dropLast(1)
    fun String.at(pos: Int) = this[2 * pos + 3].toString().let { name -> Type.values().single { it.name == name } }
    return State(
        roomLength = roomLines.size,
        storage = allStoragePositions.map { null },
        rooms = Type.values().mapIndexed { i, type ->
            Room(
                desiredType = type,
                content = roomLines.map { it.at(i) }
            )
         },
    )
}

private fun part1(input: List<String>): Long {
    return findPath(input.mutate { it.removeAt(3); it.removeAt(3) }.parseInput())
        ?.totalCost
        ?: error("could not find solution")
}

private fun part2(input: List<String>): Long {
    return findPath(input.parseInput())
        ?.totalCost
        ?: error("could not find solution")
}

private val Type.roomPosition get() = ordinal * 2 + 2
private val Type.costMultiplier get() =
    when (this) {
        Type.A -> 1
        Type.B -> 10
        Type.C -> 100
        Type.D -> 1000
    }
private val Room.position get() = desiredType.roomPosition
private fun Room.isFull() = content.first() != null
private fun Room.isSolved() = content.all { it == desiredType }
private fun Room.isPartiallySolved() = content.all { it == null || it == desiredType }
private operator fun Room.get(i: Int) = content[i]
private fun Room.mutate(i: Int, value: Type?) = Room(desiredType, content.mutate { it[i] = value })

private fun State.moveOut(roomIndex: Int, desiredPosition: Int): PathFindingMove<State>? {
    val room = rooms[roomIndex]
    val index = when {
        desiredPosition in forbiddenStoragePositions -> return null
        room.isPartiallySolved() -> return null
        else -> room.content.indexOfFirst { it != null }
    }
    val type = room.content[index]!!
    val roomPos = room.position
    val range = if (roomPos < desiredPosition) roomPos..desiredPosition else desiredPosition..roomPos
    if (range.any { storage[it] != null }) return null

    return PathFindingMove(
        cost = (range.last - range.first + index + 1) * type.costMultiplier.toLong(),
        state = State(
            roomLength = roomLength,
            storage = storage.mutate { it[desiredPosition] = type },
            rooms = rooms.map { if (it == room) room.mutate(index, null) else it },
        ),
    )
}
private fun State.moveIn(startPosition: Int, roomIndex: Int): PathFindingMove<State>? {
    val room = rooms[roomIndex]
    val type = storage.getOrNull(startPosition) ?: return null
    val index = when {
        room.desiredType != type || room.isFull() -> return null
        !room.isPartiallySolved() -> return null
        else -> room.content.indexOfLast { it == null }
    }
    val roomPos = room.position
    val range = if (roomPos < startPosition) roomPos until startPosition else (startPosition + 1)..roomPos
    if (range.any { storage[it] != null }) return null

    return PathFindingMove(
        cost = (range.last - range.first + index + 2) * type.costMultiplier.toLong(),
        state = State(
            roomLength = roomLength,
            storage = storage.mutate { it[startPosition] = null },
            rooms = rooms.map { if (it == room) room.mutate(index, type) else it },
        ),
    )
}

@Suppress("unused")
private fun State.toDebugString() =
    """
    #############
    #${storage.joinToString("") {it?.name ?: "."}}#
    ###${rooms.joinToString("#") { it.content[0]?.name ?: "." }}###
      #${rooms.joinToString("#") { it.content[1]?.name ?: "." }}#
      #########
    """.trimIndent()
