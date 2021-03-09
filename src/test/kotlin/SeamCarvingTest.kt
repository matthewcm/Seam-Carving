import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import seamcarving.SeamCarving
import java.awt.Color
import java.awt.image.BufferedImage
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.PrintStream
import javax.imageio.ImageIO
import kotlin.math.round

internal class SeamCarvingTest {
    private val myOut = ByteArrayOutputStream()

    var sc = SeamCarving()


    @ExperimentalStdlibApi
    @BeforeEach
    fun before() {
        sc = SeamCarving()
    }

    @Test
    fun `Given image arguments, When Start is invoked, Then print Questions for Rectangle size`() {
        System.setOut(PrintStream(myOut))
        val input = "15\n15\n out.png"
        val inp = ByteArrayInputStream(input.toByteArray())
        System.setIn(inp)

        val (width, height, name) = sc.start()

        val result  = myOut.toString().trim().lines()

        // THEN
        assertEquals( "Enter rectangle width:", result[0] )
        assertEquals( "Enter rectangle height:", result[1] )
        assertEquals( "Enter output image name:", result[2] )

        assertEquals( "15", width)
        assertEquals( "15", height)
        assertEquals( "out.png",name )
    }
    @Test
    fun `Given another image arguments, When Start is invoked, Then print Questions for Rectangle size`() {
        System.setOut(PrintStream(myOut))
        val input = "10\n10\n out2.png"
        val inp = ByteArrayInputStream(input.toByteArray())
        System.setIn(inp)

        val (width, height, name) = sc.start()

        val result  = myOut.toString().trim().lines()

        // THEN
        assertEquals( "Enter rectangle width:", result[0] )
        assertEquals( "Enter rectangle height:", result[1] )
        assertEquals( "Enter output image name:", result[2] )

        assertEquals( "10", width)
        assertEquals( "10", height)
        assertEquals( "out2.png",name )
    }

    @Test
    fun `getEnergiseRGBPredicate for center pixel`() {

        val image = ImageIO.read(File("sky.png"))
        // LEFT
        image.setRGB(1,1, Color(255,250,155).rgb)

        // RIGHT
        image.setRGB(3,1, Color(150,150,100).rgb)

        // UP
        image.setRGB(2,0, Color(50,255,255).rgb)

        // DOWN
        image.setRGB(2,2, Color(10,250,40).rgb)

        val energy = sc.energiseRGBPredicate(2,1, image)

        assertEquals(268.14, round(energy * 100) / 100 )

    }

    @Test
    fun `getEnergiseRGBPredicate for border pixel`() {

        val image = ImageIO.read(File("sky.png"))
        // LEFT
        image.setRGB(0,0, Color(0,255,250).rgb)

        // RIGHT
        image.setRGB(2,0, Color(50,255,255).rgb)

        // UP
        image.setRGB(0,0, Color(0,255,250).rgb)

        // DOWN
        image.setRGB(0,2, Color(110 ,250,140).rgb)

        val energy = sc.energiseRGBPredicate(0,0, image)

        assertEquals(163.55, round(energy * 100) / 100 )

    }

    @Test
    fun getInverseRGBPredicate() {
    }
}