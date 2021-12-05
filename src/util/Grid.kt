package util

class Grid<T>(val width: Int, val height: Int, init: (Point) -> T) {
    private val items = MutableList(width * height) { init(point(it)) }
    private fun index(x: Int, y: Int) = x + y * width
    private fun index(p: Point) = p.x + p.y * width
    private fun point(i: Int) = Point(i % width, i / width)
    operator fun get(p: Point) = items[index(p)]
    operator fun set(p: Point, value: T) {
        items[index(p)] = value
    }

    fun values() = Iterable {
        iterator {
            for (x in 0 until width) {
                for (y in 0 until height) {
                    yield(items[index(x, y)])
                }
            }
        }
    }
}
