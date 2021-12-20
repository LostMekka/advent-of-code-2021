package util

@Suppress("unused", "MemberVisibilityCanBePrivate")
class Rect(x1: Int, y1: Int, x2: Int, y2: Int) : Iterable<Point> {
    constructor(p1: Point, p2: Point) : this(p1.x, p1.y, p2.x, p2.y)

    val minX = minOf(x1, x2)
    val maxX = maxOf(x1, x2)
    val minY = minOf(y1, y2)
    val maxY = maxOf(y1, y2)

    val width = maxX - minX
    val height = maxY - minY

    val minPos = Point(minX, minY)
    val maxPos = Point(maxX, maxY)

    val xRange get() = minX..maxX
    val yRange get() = minY..maxY

    operator fun contains(p: Point) = p.x in xRange && p.y in yRange
    override fun iterator() = iterator { for (y in yRange) for (x in xRange) yield(Point(x, y)) }

    fun expandedBy(size: Int) = Rect(minX - size, minY - size, maxX + size, maxY + size)
}

fun Iterable<Point>.boundingRect(): Rect {
    val first = first()
    var minX = first.x
    var maxX = first.x
    var minY = first.y
    var maxY = first.y
    for (p in this) {
        if (p.x < minX) minX = p.x
        if (p.x > maxX) maxX = p.x
        if (p.y < minY) minY = p.y
        if (p.y > maxY) maxY = p.y
    }
    return Rect(minX, minY, maxX, maxY)
}
