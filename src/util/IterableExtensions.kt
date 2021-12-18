package util

fun <T> List<T>.mutate(block: (MutableList<T>) -> Unit): List<T> =
    toMutableList().also(block)

operator fun <T> T.plus(tail: List<T>): List<T> {
    val result = ArrayList<T>(tail.size + 1)
    result.add(this)
    result.addAll(tail)
    return result
}
