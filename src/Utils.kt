import java.io.File
import java.math.BigInteger
import java.security.MessageDigest

fun readInput(name: String) = File("src/$name.txt").readLines()
fun readInput(dayNumber: Int, testInput: Boolean = false) =
    dayNumber.toString()
        .padStart(2,'0')
        .let { if (testInput) "day${it}/test" else "day$it/input" }
        .let { readInput(it) }
fun <T> readInput(dayNumber: Int, testInput: Boolean = false, transform: (String) -> T) =
    readInput(dayNumber, testInput).map(transform)


@Suppress("unused") // probably used later on??
fun String.md5(): String = BigInteger(1, MessageDigest.getInstance("MD5").digest(toByteArray())).toString(16)
