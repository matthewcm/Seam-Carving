import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import seamcarving.Dijkstra

internal class DijkstraTest {

    private lateinit var dk:Dijkstra

    @BeforeEach
    fun before() {
        val grid = arrayOf(
            arrayOf(0.0, 10.0, 25.0),
            arrayOf(2.0, 18.0, 3.0),
            arrayOf(1.0, 3.0, 4.0)
        )
        dk = Dijkstra(grid)
    }


    @Test
    fun printGrid() {

        dk.printGrid()
    }

    @Test
    fun ShortestPath() {

        val sum = dk.shortestPath()

        assertEquals(9.0, sum)
    }

    @Test
    fun findAnotherShortestPathSum() {

        val grid = arrayOf(
            arrayOf(0.0, 10.0, 25.0),
            arrayOf(3.0, 18.0, 3.0),
            arrayOf(2.0, 4.0, 4.0)
        )

        dk = Dijkstra(grid)

        val sum = dk.shortestPath()

        assertEquals(11.0, sum)
    }

    @Test
    fun getAdjacentVectors() {

        val grid = arrayOf(
            arrayOf(0.0, 10.0, 25.0),
            arrayOf(3.0, 18.0, 3.0),
            arrayOf(2.0, 4.0, 4.0)
        )

        var distanceVectors = mutableMapOf<String, Double>()
        repeat(grid.size) { y ->
            repeat(grid[0].size) { x ->
                distanceVectors[listOf(x,y).joinToString(" ") ] = Double.POSITIVE_INFINITY
            }
        }
        dk.distanceVectors = distanceVectors

        var neighbors = dk.getAdjacentVectors("0 0" )

        assertEquals(
            listOf(
               listOf (1,0).joinToString(" "),
               listOf (0,1).joinToString(" "),
               listOf (1,1).joinToString(" ")
        ), neighbors)

        neighbors = dk.getAdjacentVectors("1 1" )

        assertEquals(
            listOf(
                listOf (0,0).joinToString(" "),
                listOf (0,1).joinToString(" "),
                listOf (0,2).joinToString(" "),
                listOf (1,0).joinToString(" "),
                listOf (1,2).joinToString(" "),
                listOf (2,0).joinToString(" "),
                listOf (2,1).joinToString(" "),
                listOf (2,2).joinToString(" "),
            ).toSet(), neighbors.toSet())
    }

    @Test
    fun shortestPathSeam() {

        val grid = arrayOf(
            arrayOf(0.0, 10.0, 25.0),
            arrayOf(3.0, 18.0, 3.0),
            arrayOf(2.0, 4.0, 4.0)
        )

        dk = Dijkstra(grid)

        val sum = dk.shortestPathVerticalSeam()


    }
    @Test
    fun shortestPathSequence(){
        val grid = arrayOf(
            arrayOf(0.0, 10.0, 25.0),
            arrayOf(3.0, 18.0, 3.0),
            arrayOf(2.0, 4.0, 4.0)
        )

        dk = Dijkstra(grid)

        println(dk.shortestPathVerticalSeam())

        val sequence = dk.getShortestPathSequence(listOf(2,4))

        println(sequence)


    }
}