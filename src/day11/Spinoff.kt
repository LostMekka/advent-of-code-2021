package day11

import util.Grid
import util.Point
import java.awt.Color
import java.awt.image.BufferedImage
import java.awt.image.RenderedImage
import java.io.File
import javax.imageio.IIOImage
import javax.imageio.ImageIO
import javax.imageio.ImageTypeSpecifier
import javax.imageio.ImageWriteParam
import javax.imageio.ImageWriter
import javax.imageio.metadata.IIOMetadata
import javax.imageio.metadata.IIOMetadataNode
import javax.imageio.stream.FileImageOutputStream
import javax.imageio.stream.ImageOutputStream
import kotlin.math.roundToInt
import kotlin.random.Random

private const val width = 50
private const val height = 50
private const val pixelSize = 16
private const val maxEnergy = 25
private const val delay = 150
private val gifTargets = listOf(0, 500, 1000, 2000, 5000, 10000, 20000, 50000, 100000)
private const val maxRenderedSteps = 100
private val colorGradient = mapOf(
    0.0 to Color(0, 0, 0),
    0.75 to Color(0, 0, 168),
    1.0 to Color(170, 170, 0),
)
private val blinkColor = Color(200, 200, 255)

private fun main() {
    renderTimelapse()
}

private fun renderTimelapse() {
    val recordedFrames = 500

    val state = Grid(width, height) { Random.nextInt(maxEnergy) + 1 }
    val fileName = "out_timelapse.gif"
    val outStream = FileImageOutputStream(File(fileName))
    val writer = GifSequenceWriter(outStream, BufferedImage.TYPE_INT_RGB, delay, true)
    var stepCount = 0
    var frameCount = 0
    writer.writeToSequence(state.toBufferedImage())
    while (frameCount < recordedFrames) {
        state.simulateStep()
        stepCount++
        if (state[width / 2, height / 2] == 0) {
            writer.writeToSequence(state.toBufferedImage())
            frameCount++
        }
    }
    writer.close()
}

private fun renderTimedSlices() {
    val state = Grid(width, height) { Random.nextInt(maxEnergy) + 1 }
    var stepCount = 0
    for (target in gifTargets) {
        while (stepCount < target) {
            state.simulateStep()
            stepCount++
        }
        println("simulated fast forward until step $stepCount")
        val fileName = "out_$target-${target + maxRenderedSteps - 1}.gif"
        val outStream = FileImageOutputStream(File(fileName))
        val writer = GifSequenceWriter(outStream, BufferedImage.TYPE_INT_RGB, delay, true)
        repeat(maxRenderedSteps) {
            writer.writeToSequence(state.toBufferedImage())
            state.simulateStep()
            stepCount++
        }
        writer.close()
        println("simulated until step $stepCount and wrote data to $fileName")
    }
}

private fun Grid<Int>.simulateStep(): Int {
    val toBlink = mutableSetOf<Point>()
    val blinkDone = mutableSetOf<Point>()
    for (p in positions()) {
        val energy = this[p] + 1
        this[p] = energy
        if (energy > maxEnergy) toBlink += p
    }
    while (toBlink.isNotEmpty()) {
        val p = toBlink.first()
        toBlink -= p
        blinkDone += p
        this[p] = 0
        for (n in p.neighboursIncludingDiagonals()) {
            if (n !in this || n in blinkDone) continue
            val energy = this[n] + 1
            this[n] = energy
            if (energy > maxEnergy) toBlink += n
        }
    }
    return blinkDone.size
}

private fun Grid<Int>.toBufferedImage(): BufferedImage {
    val image = BufferedImage(width * pixelSize, height * pixelSize, BufferedImage.TYPE_INT_RGB)
    val g = image.createGraphics()
    for (p in positions()) {
        g.color = colorForEnergyLevel(this[p])
        g.fillRect(p.x * pixelSize, p.y * pixelSize, pixelSize, pixelSize)
    }
    g.dispose()
    return image
}

private fun colorForEnergyLevel(energy: Int): Color {
    if (energy == 0) return blinkColor

    val r = energy.toDouble() / maxEnergy
    val (r1, c2) = colorGradient.entries.filter { it.key <= r }.maxByOrNull { it.key }!!
    val (r2, c1) = colorGradient.entries.filter { it.key >= r }.minByOrNull { it.key }!!
    if (r1 == r2) return c1

    val a1 = (r - r1) / (r2 - r1)
    val a2 = 1 - a1
    return Color(
        (c1.red * a1 + c2.red * a2).roundToInt(),
        (c1.green * a1 + c2.green * a2).roundToInt(),
        (c1.blue * a1 + c2.blue * a2).roundToInt(),
    )
}

class GifSequenceWriter(out: ImageOutputStream, imageType: Int, delay: Int, loop: Boolean) {
    protected var writer: ImageWriter = ImageIO.getImageWritersBySuffix("gif").next()
    protected var params: ImageWriteParam = writer.defaultWriteParam
    protected var metadata: IIOMetadata =
        writer.getDefaultImageMetadata(ImageTypeSpecifier.createFromBufferedImageType(imageType), params)

    init {
        configureRootMetadata(delay, loop)
        writer.output = out
        writer.prepareWriteSequence(null)
    }

    private fun configureRootMetadata(delay: Int, loop: Boolean) {
        val metaFormatName = metadata.nativeMetadataFormatName
        val root = metadata.getAsTree(metaFormatName) as IIOMetadataNode
        val graphicsControlExtensionNode = getNode(root, "GraphicControlExtension")
        graphicsControlExtensionNode.setAttribute("disposalMethod", "none")
        graphicsControlExtensionNode.setAttribute("userInputFlag", "FALSE")
        graphicsControlExtensionNode.setAttribute("transparentColorFlag", "FALSE")
        graphicsControlExtensionNode.setAttribute("delayTime", (delay / 10).toString())
        graphicsControlExtensionNode.setAttribute("transparentColorIndex", "0")
        val commentsNode = getNode(root, "CommentExtensions")
        commentsNode.setAttribute("CommentExtension", "hi.")
        val appExtensionsNode = getNode(root, "ApplicationExtensions")
        val child = IIOMetadataNode("ApplicationExtension")
        child.setAttribute("applicationID", "NETSCAPE")
        child.setAttribute("authenticationCode", "2.0")
        val loopContinuously = if (loop) 0 else 1
        child.userObject =
            byteArrayOf(0x1, (loopContinuously and 0xFF).toByte(), (loopContinuously shr 8 and 0xFF).toByte())
        appExtensionsNode.appendChild(child)
        metadata.setFromTree(metaFormatName, root)
    }

    fun writeToSequence(img: RenderedImage) {
        writer.writeToSequence(IIOImage(img, null, metadata), params)
    }

    fun close() {
        writer.endWriteSequence()
    }

    private fun getNode(rootNode: IIOMetadataNode, nodeName: String): IIOMetadataNode {
        val nNodes = rootNode.length
        for (i in 0 until nNodes) {
            if (rootNode.item(i).nodeName.equals(nodeName, ignoreCase = true)) {
                return rootNode.item(i) as IIOMetadataNode
            }
        }
        val node = IIOMetadataNode(nodeName)
        rootNode.appendChild(node)
        return node
    }
}
