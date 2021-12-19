package day19

import util.Matrix3
import util.Point3
import util.readInput
import util.shouldBe
import util.split
import java.util.LinkedList

fun main() {
    val day = 19
    val testInput = readInput(day, testInput = true).parseInput()
    part1(testInput) shouldBe 79
    part2(testInput) shouldBe 3621

    val input = readInput(day).parseInput()
    println("output for part1: ${part1(input)}")
    println("output for part2: ${part2(input)}")
}

private class Scanner(
    val id: Int,
    val beacons: List<Point3>,
)

private val inputHeaderPattern = Regex("""^--- scanner (\d+) ---$""")
private val inputLinePattern = Regex("""^(-?\d+),(-?\d+),(-?\d+)$""")

private fun List<String>.parseInput(): List<Scanner> =
    split { it.isBlank() }.map { it.parseScanner() }

private fun List<String>.parseScanner() =
    Scanner(
        id = inputHeaderPattern
            .matchEntire(first())!!
            .groupValues[1]
            .toInt(),
        beacons = drop(1)
            .map { line ->
                inputLinePattern
                    .matchEntire(line)!!
                    .groupValues
                    .let { (_, x, y, z) -> Point3(x.toInt(), y.toInt(), z.toInt()) }
            }
    )

private fun part1(input: List<Scanner>): Int {
    return solveScanners(input)
        .flatMap { it.beaconsSet }
        .toSet()
        .size
}

private fun part2(input: List<Scanner>): Int {
    val positions = solveScanners(input).map { it.position }
    return positions.maxOf { outer -> positions.maxOf { it manhattanDistanceTo outer } }
}

private fun solveScanners(input: List<Scanner>): List<SolvedScanner> {
    val firstSolvedScanner = input.first().let { SolvedScanner(it.id, Point3.Zero, it.beacons) }
    val solvedScanners = mutableListOf(firstSolvedScanner)
    val scannersToSolve = input.drop(1).associateByTo(mutableMapOf()) { it.id }
    val alignmentsToCheck = input.drop(1).mapTo(LinkedList()) { it to firstSolvedScanner }
    while (alignmentsToCheck.isNotEmpty()) {
        val (subject, target) = alignmentsToCheck.removeFirst()
        val solvedSubject = subject.tryAligningTo(target)
        if (solvedSubject != null) {
            solvedScanners += solvedSubject
            scannersToSolve -= solvedSubject.id
            alignmentsToCheck.removeAll { it.first === subject }
            scannersToSolve.values.mapTo(alignmentsToCheck) { it to solvedSubject }
        }
    }
    require(scannersToSolve.isEmpty()) { "not all scanners were solved" }
    return solvedScanners
}

private val rotations = buildList {
    val orientations = listOf(
        Matrix3.Identity,
        Matrix3.Rotate90DegreesAroundZ,
        Matrix3.Rotate90DegreesAroundZ * Matrix3.Rotate90DegreesAroundZ,
        Matrix3.Rotate90DegreesAroundZ * Matrix3.Rotate90DegreesAroundZ * Matrix3.Rotate90DegreesAroundZ,
    )
    val lookingDirections = listOf(
        Matrix3.Identity,
        Matrix3.Rotate90DegreesAroundY,
        Matrix3.Rotate90DegreesAroundY * Matrix3.Rotate90DegreesAroundY,
        Matrix3.Rotate90DegreesAroundY * Matrix3.Rotate90DegreesAroundY * Matrix3.Rotate90DegreesAroundY,
        Matrix3.Rotate90DegreesAroundX,
        Matrix3.Rotate90DegreesAroundX * Matrix3.Rotate90DegreesAroundX * Matrix3.Rotate90DegreesAroundX,
    )
    for (orientation in orientations) {
        for (lookingDirection in lookingDirections) {
            add(orientation * lookingDirection)
        }
    }
}

private class SolvedScanner(
    val id: Int,
    val position: Point3,
    val beaconsList: List<Point3>,
) {
    val beaconsSet = beaconsList.toSet()
}

private const val minOverlap = 12
private fun Scanner.tryAligningTo(target: SolvedScanner): SolvedScanner? {
    for (rotation in rotations) {
        val rotatedBeacons = beacons.map { rotation * it }
        for (targetIndex in 0..(target.beaconsList.size - minOverlap)) {
            val targetBeaconPos = target.beaconsList[targetIndex]
            for (subjectIndex in 0..(rotatedBeacons.size - minOverlap)) {
                val subjectBeaconPos = rotatedBeacons[subjectIndex]
                val subjectScannerPos = targetBeaconPos - subjectBeaconPos
                val translatedBeacons = rotatedBeacons.map { it + subjectScannerPos }
                if (translatedBeacons.count { it in target.beaconsSet } >= minOverlap) {
                    return SolvedScanner(
                        id = id,
                        position = subjectScannerPos,
                        beaconsList = translatedBeacons,
                    )
                }
            }
        }
    }
    return null
}
