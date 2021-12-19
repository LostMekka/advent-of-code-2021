package util

fun <T> List<T>.mutate(block: (MutableList<T>) -> Unit): List<T> =
    toMutableList().also(block)

operator fun <T> T.plus(tail: List<T>): List<T> {
    val result = ArrayList<T>(tail.size + 1)
    result.add(this)
    result.addAll(tail)
    return result
}

fun <T> List<T>.split(
    includeSplittingItem: Boolean = false,
    ignoreFirstEmptyChunk: Boolean = true,
    predicate: (T) -> Boolean,
): List<List<T>> {
    if (isEmpty()) return emptyList()
    val outerList = mutableListOf<List<T>>()
    var innerList = mutableListOf<T>()
    outerList += innerList
    for (element in this) {
        if (predicate(element)) {
            if (!ignoreFirstEmptyChunk || innerList.isNotEmpty() || outerList.isNotEmpty()) innerList = mutableListOf()
            if (includeSplittingItem) innerList += element
            outerList += innerList
        } else {
            innerList += element
        }
    }
    return outerList
}
