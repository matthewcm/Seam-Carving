import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import seamcarving.SeamCarving
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.PrintStream

internal class SeamCarvingTest {
    private val myOut = ByteArrayOutputStream()

    var sc = SeamCarving()


    @ExperimentalStdlibApi
    @BeforeEach
    fun before() {
        System.setOut(PrintStream(myOut))
        sc = SeamCarving()
    }

    @Test
    fun `Given image arguments, When Start is invoked, Then print Questions for Rectangle size`() {
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
}