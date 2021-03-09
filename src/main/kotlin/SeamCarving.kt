package seamcarving
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO
import java.awt.Color
import java.awt.Graphics2D
import kotlin.math.*


class SeamCarving {

    var maxEnergyValue = 0.0

    private fun createImage(width:Int, height:Int, name:String):BufferedImage {

        val bufferedImage = BufferedImage(width,height, BufferedImage.TYPE_INT_RGB);

        val g2d: Graphics2D = bufferedImage.createGraphics()

        g2d.color = Color.red
        g2d.drawLine(0,0, width - 1, height - 1)
        g2d.drawLine(width - 1, 0, 0, height - 1)
        g2d.dispose()

        val file = ImageIO.write(bufferedImage, "png", File(name) )


        return bufferedImage;
    }
    fun start(): Array<String> {
        println("Enter rectangle width:")
        val width = readLine().toString().trim()
        println("Enter rectangle height:")
        val height = readLine().toString().trim()
        println("Enter output image name:")
        val  name = readLine().toString().trim()

        createImage(width.toInt(), height.toInt(), name)
        return arrayOf(width,height, name)

    }

    fun createEnergyGrid (image: BufferedImage): Array<Array<Double>> {

        val width = 100
        val height = 100
        val energyGrid = Array(width) {Array(height) {0.0} }

//        repeat(image.height){ y ->
            repeat(height){ y ->

            val rowGrid = Array(width) {0.0}
//            repeat(image.width){ x ->
                repeat(width){ x ->
                val energy = energiseRGBPredicate(x,y,image)
                rowGrid[x] = energy

                if (energy > maxEnergyValue) maxEnergyValue = energy
            }

            energyGrid[y] = rowGrid
        }

        return energyGrid

    }

    fun shortestEnergySeam (inputName: String, outputName: String): List<List<Int>>? {

        val image = ImageIO.read(File(inputName))

        val grid = createEnergyGrid(image)

        println(grid.size)
        val dk = Dijkstra(grid)

        val nodes = dk.shortestPathSeam()

        val g2d: Graphics2D = image.createGraphics()

        g2d.color = Color.red

        nodes?.forEach{ (x,y) ->
            g2d.drawLine(x,y,x,y)
        }

        g2d.dispose()

        ImageIO.write(image, "png", File("SEAM.png") )
        return listOf(listOf(2))
    }


    val energiseRGBPredicate = {x: Int, y: Int, image: BufferedImage ->

        var rgbLeft: Color
        var rgbRight:Color
        var rgbUp:Color
        var rgbDown:Color

        when (x) {
            0 -> {
                rgbLeft = Color(image.getRGB(x , y))
                rgbRight = Color(image.getRGB(x + 2, y))
            }
            image.width - 1 -> {
                rgbLeft = Color(image.getRGB(x - 2, y))
                rgbRight = Color(image.getRGB(x , y))
            }
            else -> {
                rgbLeft = Color(image.getRGB(x - 1, y))
                rgbRight = Color(image.getRGB(x + 1 , y))
            }
        }
        when (y) {
            0 -> {
                rgbUp = Color(image.getRGB(x , y))
                rgbDown= Color(image.getRGB(x , y + 2))
            }
            image.height - 1 -> {
                rgbUp = Color(image.getRGB(x , y - 2))
                rgbDown= Color(image.getRGB(x , y))
            }
            else -> {
                rgbUp = Color(image.getRGB(x , y - 1))
                rgbDown= Color(image.getRGB(x  , y + 1))
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

    val inverseRGBPredicate = { x:Int, y:Int, image: BufferedImage ->
        val rgb = Color(image.getRGB(x, y))
        image.setRGB(x, y, Color(255 - rgb.red, 255 - rgb.green, 255 - rgb.blue).rgb)
    }

    fun energiseImage(inputName: String, outputName: String){

        val image = ImageIO.read(File(inputName))
        val imageToMutate = ImageIO.read(File(inputName))

        createEnergyGrid(image)

        println("max energy $maxEnergyValue" )

        repeat(image.height){ y ->
            repeat(image.width){ x ->
                val energy =energiseRGBPredicate(x,y,image)
                val intensity = (255.0 * energy / maxEnergyValue)
                imageToMutate.setRGB(x, y, Color(intensity.toInt(), intensity.toInt(), intensity.toInt()).rgb)
            }
        }
        ImageIO.write(imageToMutate, "png", File(outputName) )

    }

    fun inverseImage(inputName: String, outputName:String) {

        val image = ImageIO.read(File(inputName))

        repeat(image.height){ y ->
            repeat(image.width){ x ->
                inverseRGBPredicate(x,y,image)
            }
        }

        ImageIO.write(image, "png", File(outputName) )

    }


}

fun main(args: Array<String>) {
    args.forEach(::println)

    var inputName = args[args.indexOfFirst{it == "-in"} + 1]
    var outputName = args[args.indexOfFirst{it == "-out"} + 1]
    println(inputName)
    println(outputName)

    var sc = SeamCarving()
//    sc.start()

//    sc.inverseImage(inputName, outputName)

//    sc.energiseImage(inputName, outputName)

     sc.shortestEnergySeam(inputName, outputName)

}



