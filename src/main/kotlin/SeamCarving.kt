package seamcarving
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO
import java.awt.Color
import java.awt.Graphics2D




class SeamCarving {

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


}

fun main() {
    var sc = SeamCarving()
    sc.start()

}



