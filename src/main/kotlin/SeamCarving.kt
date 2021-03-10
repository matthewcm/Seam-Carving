package seamcarving

import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO
import java.awt.Color
import java.awt.Graphics2D
import kotlin.math.*


class SeamCarving {

    var maxEnergyValue = 0.0
    lateinit var preservedRGBGrid: Array<Array<Int>>

    private fun createImage(width: Int, height: Int, name: String): BufferedImage {

        val bufferedImage = BufferedImage(width, height, BufferedImage.TYPE_INT_RGB)

        val g2d: Graphics2D = bufferedImage.createGraphics()

        g2d.color = Color.red
        g2d.drawLine(0, 0, width - 1, height - 1)
        g2d.drawLine(width - 1, 0, 0, height - 1)
        g2d.dispose()

        ImageIO.write(bufferedImage, "png", File(name))

        return bufferedImage
    }

    fun start(): Array<String> {

        println("Enter rectangle width:")
        val width = readLine().toString().trim()
        println("Enter rectangle height:")
        val height = readLine().toString().trim()
        println("Enter output image name:")
        val name = readLine().toString().trim()

        createImage(width.toInt(), height.toInt(), name)
        return arrayOf(width, height, name)
    }

    private fun createEnergyGrid(image: BufferedImage): Array<Array<Double>> {

        val width = image.width
        val height = image.height

        preservedRGBGrid = Array(height) { Array(width) { 0 } }
        val energyGrid = Array(height) { Array(width) { 0.0 } }

        repeat(height) { y ->

            val rowGrid = Array(width) { 0.0 }
            val rgbRow = Array(width) { 0 }
            repeat(width) { x ->
                val energy = energiseRGBPredicate(x, y, image)
                rowGrid[x] = energy

                rgbRow[x] = image.getRGB(x, y)

                if (energy > maxEnergyValue) maxEnergyValue = energy
            }
            preservedRGBGrid[y] = (rgbRow)
            energyGrid[y] = rowGrid
        }

        return energyGrid

    }

    private fun drawSeam(nodes: List<List<Int>>, image: BufferedImage) {

        val g2d: Graphics2D = image.createGraphics()

        g2d.color = Color.red

        nodes.forEach { (x, y) ->
            g2d.drawLine(x, y, x, y)
        }

        g2d.dispose()

    }

    var transposePredicate = { matrix: Array<Array<Double>> ->
        val transposedGrid = Array(matrix[0].size) { Array(matrix.size) { 0.0 } }

        for (i in transposedGrid.indices) {
            for (j in transposedGrid[i].indices) {
                transposedGrid[i][j] = matrix[j][i]
            }
        }
        transposedGrid
    }
    var transposeNodes = { nodes: List<List<Int>> ->
        nodes.map { (x, y) ->
            listOf(y, x)
        }
    }


    fun shortestEnergyHorizontalSeam(grid: Array<Array<Double>>): List<List<Int>> {

        val transposedGrid = transposePredicate(grid)

        val dk = Dijkstra(transposedGrid)

        dk.shortestPathVerticalSeam()

        val nodes: List<List<Int>> = dk.getShortestPathSequence(listOf(dk.grid[0].size - 1, dk.grid.size - 1)).toList()


        return transposeNodes(nodes.filter { (_, y) ->
            !(y == 0 || y == dk.grid.size - 1)
        }.map { (x, y) ->
            listOf(x, y - 1)
        })
    }

    fun drawRedSeam(inputName: String, seamNodes: List<List<Int>>, outputName: String) {
        val image = ImageIO.read(File(inputName))

        drawSeam(seamNodes, image)

        ImageIO.write(image, "png", File(outputName))
    }

    fun shortestEnergyVerticalSeam(grid: Array<Array<Double>>): List<List<Int>> {
        val dk = Dijkstra(grid)

        dk.shortestPathVerticalSeam()

        val nodes: List<List<Int>> = dk.getShortestPathSequence(listOf(dk.grid[0].size - 1, dk.grid.size - 1)).toList()

        val filteredNodes = nodes.filter { (_, y) ->
            !(y == 0 || y == dk.grid.size - 1)
        }.map { (x, y) ->
            listOf(x, y - 1)
        }

        return filteredNodes
    }


    val energiseRGBPredicate = { x: Int, y: Int, image: BufferedImage ->

        val rgbLeft: Color
        val rgbRight: Color
        val rgbUp: Color
        val rgbDown: Color

        when (x) {
            0 -> {
                rgbLeft = Color(image.getRGB(x, y))
                rgbRight = Color(image.getRGB(x + 2, y))
            }
            image.width - 1 -> {
                rgbLeft = Color(image.getRGB(x - 2, y))
                rgbRight = Color(image.getRGB(x, y))
            }
            else -> {
                rgbLeft = Color(image.getRGB(x - 1, y))
                rgbRight = Color(image.getRGB(x + 1, y))
            }
        }
        when (y) {
            0 -> {
                rgbUp = Color(image.getRGB(x, y))
                rgbDown = Color(image.getRGB(x, y + 2))
            }
            image.height - 1 -> {
                rgbUp = Color(image.getRGB(x, y - 2))
                rgbDown = Color(image.getRGB(x, y))
            }
            else -> {
                rgbUp = Color(image.getRGB(x, y - 1))
                rgbDown = Color(image.getRGB(x, y + 1))
            }
        }

        val redX = abs(rgbLeft.red - rgbRight.red)
        val redY = abs(rgbUp.red - rgbDown.red)

        val greenX = abs(rgbLeft.green - rgbRight.green)
        val greenY = abs(rgbUp.green - rgbDown.green)

        val blueX = abs(rgbLeft.blue - rgbRight.blue)
        val blueY = abs(rgbUp.blue - rgbDown.blue)


        val xGradient = redX.toDouble().pow(2) + greenX.toDouble().pow(2) + blueX.toDouble().pow(2)
        val yGradient = redY.toDouble().pow(2) + greenY.toDouble().pow(2) + blueY.toDouble().pow(2)

        val energy = sqrt(xGradient + yGradient)

        energy

    }

    val inverseRGBPredicate = { x: Int, y: Int, image: BufferedImage ->
        val rgb = Color(image.getRGB(x, y))
        image.setRGB(x, y, Color(255 - rgb.red, 255 - rgb.green, 255 - rgb.blue).rgb)
    }

    fun energiseImage(inputName: String, outputName: String) {

        val image = ImageIO.read(File(inputName))
        val imageToMutate = ImageIO.read(File(inputName))

        createEnergyGrid(image)

        println("max energy $maxEnergyValue")

        repeat(image.height) { y ->
            repeat(image.width) { x ->
                val energy = energiseRGBPredicate(x, y, image)
                val intensity = (255.0 * energy / maxEnergyValue)
                imageToMutate.setRGB(x, y, Color(intensity.toInt(), intensity.toInt(), intensity.toInt()).rgb)
            }
        }
        ImageIO.write(imageToMutate, "png", File(outputName))

    }

    fun inverseImage(inputName: String, outputName: String) {

        val image = ImageIO.read(File(inputName))

        repeat(image.height) { y ->
            repeat(image.width) { x ->
                inverseRGBPredicate(x, y, image)
            }
        }

        ImageIO.write(image, "png", File(outputName))

    }

    fun removeVerticalSeam(grid: Array<Array<Double>>): Array<Array<Double>> {
        val newGrid = Array(grid.size) { Array(grid[0].size - 1) { 0.0 } }

        val seamNodes = shortestEnergyVerticalSeam(grid).toSet()

        var newx = 0
        var newy = 0
        repeat(grid.size) { y ->
            repeat(grid[0].size) { x ->

                if (!seamNodes.contains(listOf(x, y))) {
                    newGrid[newy][newx] = grid[y][x]
                    preservedRGBGrid[newy][newx] = preservedRGBGrid[y][x]

                    newx++
                }
            }
            newx = 0
            newy++
        }
        return newGrid

    }

    private fun removeHorizontalSeam(grid: Array<Array<Double>>): Array<Array<Double>> {

        val newGrid = Array(grid.size - 1) { Array(grid[0].size) { 0.0 } }


        val seamNodes = shortestEnergyHorizontalSeam(grid).toList().toSet()

        var newx = 0
        var newy = 0
        repeat(grid[0].size) { x ->
            repeat(grid.size) { y ->
                if (!seamNodes.contains(listOf(x, y))) {
                    newGrid[newy][newx] = grid[y][x]
                    preservedRGBGrid[newy][newx] = preservedRGBGrid[y][x]
                    newy++
                }
            }
            newy = 0
            newx++
        }
        return newGrid

    }

    fun removeSeams(inputName: String, outputName: String, width: Int, height: Int) {

        val image = ImageIO.read(File(inputName))

        var grid = createEnergyGrid(image)


        repeat(width) {
            grid = removeVerticalSeam(grid)
        }
        repeat(height) {
            grid = removeHorizontalSeam(grid)
        }

        val newImage = BufferedImage(grid[0].size, grid.size, BufferedImage.TYPE_INT_ARGB)
        repeat(grid.size) { y ->
            repeat(grid[0].size) { x ->
                newImage.setRGB(x, y, preservedRGBGrid[y][x])
            }
        }

        ImageIO.write(newImage, "png", File(outputName))

    }

}

fun main(args: Array<String>) {
    args.forEach(::println)

    val inputName = args[args.indexOfFirst { it == "-in" } + 1]
    val outputName = args[args.indexOfFirst { it == "-out" } + 1]
    val width = args[args.indexOfFirst { it == "-width" } + 1].toInt()
    val height = args[args.indexOfFirst { it == "-height" } + 1].toInt()
    println(inputName)
    println(outputName)
    println(width)
    println(height)

    val sc = SeamCarving()
//    sc.start()

//    sc.inverseImage(inputName, outputName)

//    sc.energiseImage(inputName, outputName)

//     sc.shortestEnergyVerticalSeam(inputName, "SEAM.png")
    sc.removeSeams(inputName, outputName, width, height)

}


