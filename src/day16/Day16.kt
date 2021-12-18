package day16

import day16.PacketType.EqualTo
import day16.PacketType.GreaterThan
import day16.PacketType.LessThan
import day16.PacketType.Literal
import day16.PacketType.Maximum
import day16.PacketType.Minimum
import day16.PacketType.Product
import day16.PacketType.Sum
import util.readInput
import util.shouldBe

fun main() {
    val day = 16
    val testInput1 = listOf(
        "8A004A801A8002F478",
        "620080001611562C8802118E34",
        "C0015000016115A2E0802F182340",
        "A0016C880162017C3686B18A3D4780",
    )
    part1(testInput1) shouldBe listOf(16, 12, 23, 31)
    val testInput2 = listOf(
        "C200B40A82",
        "04005AC33890",
        "880086C3E88112",
        "CE00C43D881120",
        "D8005AC2A8F0",
        "F600BC2D8F",
        "9C005AC2F8F0",
        "9C0141080250320F1802104A08",
    )
    part2(testInput2) shouldBe listOf(3L, 54L, 7L, 9L, 1L, 0L, 0L, 1L)

    val input = readInput(day)
    println("output for part1: ${part1(input)}")
    println("output for part2: ${part2(input)}")
}

private fun part1(input: List<String>): List<Int> {
    return input.map { Reader(it).readPacket().sumUpVersions() }
}

private fun Packet.sumUpVersions(): Int =
    packetVersion + childPackets.sumOf { it.sumUpVersions() }

private fun part2(input: List<String>): List<Long> {
    return input.map { Reader(it).readPacket().evaluate() }
}

private fun Packet.evaluate(): Long =
    when (packetType) {
        Sum -> childPackets.sumOf { it.evaluate() }
        Product -> childPackets.fold(1L) { acc, packet -> acc * packet.evaluate() }
        Minimum -> childPackets.minOf { it.evaluate() }
        Maximum -> childPackets.maxOf { it.evaluate() }
        Literal -> value
        GreaterThan -> (childPackets.first().evaluate() > childPackets.last().evaluate()).toLong()
        LessThan -> (childPackets.first().evaluate() < childPackets.last().evaluate()).toLong()
        EqualTo -> (childPackets.first().evaluate() == childPackets.last().evaluate()).toLong()
    }

private fun Boolean.toLong() = if (this) 1L else 0L

private class Reader(input: String) {
    private val input = input.iterator()
    private var buffer = ""

    var headPosition = 0
        private set

    private fun read(length: Int): String {
        while (buffer.length < length) buffer += input.nextChar().asBinary()
        val bits = buffer.take(length)
        buffer = buffer.drop(length)
        headPosition += length
        return bits
    }

    fun readRawBinary(bitCount: Int) = read(bitCount)
    fun readBoolean() = read(1) == "1"
    fun readInt(bitCount: Int) = read(bitCount).toInt(2)
}

private fun Char.asBinary() =
    when (this) {
        '0' -> "0000"
        '1' -> "0001"
        '2' -> "0010"
        '3' -> "0011"
        '4' -> "0100"
        '5' -> "0101"
        '6' -> "0110"
        '7' -> "0111"
        '8' -> "1000"
        '9' -> "1001"
        'A' -> "1010"
        'B' -> "1011"
        'C' -> "1100"
        'D' -> "1101"
        'E' -> "1110"
        'F' -> "1111"
        else -> error("'$this' is not a hex char")
    }

private fun Reader.readPacket(): Packet {
    val version = readInt(3)
    val type = readPacketType()
    return if (type == Literal) {
        // literal packet
        var hasNext = true
        var binary = ""
        while (hasNext) {
            hasNext = readBoolean()
            binary += readRawBinary(4)
        }
        val value = binary.toLong(2)
        Packet(version, type, value, listOf())
    } else {
        // operator packet
        val subPackets = when (readBoolean()) {
            true -> {
                // packet count
                val count = readInt(11)
                (1..count).map { readPacket() }
            }
            false -> {
                // bit count
                val length = readInt(15)
                val startPos = headPosition
                buildList {
                    while (headPosition < startPos + length) add(readPacket())
                }
            }
        }
        Packet(version, type, 0L, subPackets)
    }
}

private class Packet(
    val packetVersion: Int,
    val packetType: PacketType,
    val value: Long,
    val childPackets: List<Packet>,
)

private enum class PacketType { Sum, Product, Minimum, Maximum, Literal, GreaterThan, LessThan, EqualTo }

private fun Reader.readPacketType() =
    when (readInt(3)) {
        0 -> Sum
        1 -> Product
        2 -> Minimum
        3 -> Maximum
        4 -> Literal
        5 -> GreaterThan
        6 -> LessThan
        7 -> EqualTo
        else -> error("how did this even happen???")
    }
