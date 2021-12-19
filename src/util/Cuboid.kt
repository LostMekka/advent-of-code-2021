package util

@Suppress("unused", "MemberVisibilityCanBePrivate")
class Cuboid(x1: Int, y1: Int, z1: Int, x2: Int, y2: Int, z2: Int) {
    constructor(p1: Point3, p2: Point3) : this(p1.x, p1.y, p1.z, p2.x, p2.y, p2.z)

    val minX = minOf(x1, x2)
    val maxX = maxOf(x1, x2)
    val minY = minOf(y1, y2)
    val maxY = maxOf(y1, y2)
    val minZ = minOf(z1, z2)
    val maxZ = maxOf(z1, z2)

    val width = maxX - minX
    val height = maxY - minY
    val depth = maxZ - minZ

    val minPos = Point3(minX, minY, minZ)
    val maxPos = Point3(maxX, maxY, maxZ)

    val xRange get() = minX..maxX
    val yRange get() = minY..maxY
    val zRange get() = minZ..maxZ

    operator fun contains(p: Point3) = p.x in xRange && p.y in yRange && p.z in zRange
}

fun Iterable<Point3>.boundingCuboid(): Cuboid {
    val first = first()
    var minX = first.x
    var maxX = first.x
    var minY = first.y
    var maxY = first.y
    var minZ = first.z
    var maxZ = first.z
    for (p in this) {
        if (p.x < minX) minX = p.x
        if (p.x > maxX) maxX = p.x
        if (p.y < minY) minY = p.y
        if (p.y > maxY) maxY = p.y
        if (p.z < minZ) minZ = p.z
        if (p.z > maxZ) maxZ = p.z
    }
    return Cuboid(minX, minY, minZ, maxX, maxY, maxZ)
}
