package seamcarving

import java.util.*


class Vertex(val name: List<Int>, val energy: Double)

class Dijkstra(var grid: Array<Array<Double>>) {

    private lateinit var distances: PriorityQueue<Vertex>

    lateinit var distanceVectors:  Array<Array<Double>>

    private var fromNode = hashMapOf<String, List<Int>>()


    fun printGrid() {
        repeat(grid.size) { y ->
            repeat(grid[0].size) { x ->
                print("" +grid[y][x] + ", ")
            }
            println()
        }
    }

    private fun validAdjacencyVectors (adjacentVectors: Set<List<Int>>): Set<List<Int>> {

        return adjacentVectors
            .filter{ adjacentVector ->
                try {
                    val (vx, vy) = adjacentVector

                    grid[vy][vx]
                    true
                }catch(e: Exception){
                    false
                }
            }.toSet()
    }
    private fun seamVerticalAdjacency (x:Int, y:Int): Set<List<Int>> {
        return setOf(
            listOf(x ,    y + 1),
            listOf(x - 1 ,    y + 1),
            listOf(x  + 1 ,    y + 1),
        )
    }
    private fun imaginaryVerticalSeamAdjacency (x:Int, y:Int):Set<List<Int>> {
        return setOf(
            listOf(x - 1, y),
            listOf(x + 1, y),
            listOf(x ,    y + 1),
            listOf(x - 1, y + 1),
            listOf(x + 1, y + 1),
        )
    }
    private fun gridAdjacency(x:Int, y:Int): Set<List<Int>>{
        return setOf(
            listOf(x - 1, y)    ,
            listOf(x + 1, y)    ,
            listOf(x ,    y + 1),
            listOf(x ,    y - 1),
            listOf(x - 1, y + 1),
            listOf(x + 1, y - 1),
            listOf(x - 1, y - 1),
            listOf(x + 1, y + 1),
        )
    }

    fun getAdjacentVectors (vector: List<Int>): Set<List<Int>> {
        val adjacentVectors = gridAdjacency(vector[0], vector[1])

        return validAdjacencyVectors(adjacentVectors)

    }
    private fun getAdjacentVectorsVerticalSeam (vector: List<Int>): Set<List<Int>> {
        val (x, y) = vector

        val adjacentVectors: Set<List<Int>> = if (y == 0 || y == grid.size - 1){
            imaginaryVerticalSeamAdjacency(x,y)
        }else {
            seamVerticalAdjacency(x,y)
        }

        if((x > 0 && x < grid[0].size - 1) && (y < grid.size - 1 && y > 0)  ){
            return adjacentVectors
        }

        return validAdjacencyVectors(adjacentVectors)
    }


    private fun updateAdjacentVectors(vector: Vertex, adjacentVectors: Set<List<Int>>) {

        val previousKey = vector.name
        val previousDistance = vector.energy

        adjacentVectors
            .forEach{ adjacentVector ->
                    val (x, y ) = adjacentVector
                    val currentMinimumDistanceToAdjacent = previousDistance + grid[y][x]
                    val currentValueOfAdjacent = distanceVectors[y][x]

                    if (currentMinimumDistanceToAdjacent < currentValueOfAdjacent){
                        distanceVectors[y][x] = currentMinimumDistanceToAdjacent

                        distances.add(Vertex(adjacentVector ,currentMinimumDistanceToAdjacent))

//                        println(adjacentVector.toString())
                        fromNode["$x $y"] = previousKey

                    }

            }


    }
    private fun initialiseInfiniteDistanceVectors(): Array<Array<Double>> {


        distances = PriorityQueue( grid.size){ a:Vertex , b:Vertex ->
            if(b.energy > a.energy) - 1
            else + 1
        }

        distances.add(Vertex(listOf(0,0), 0.0))

        distanceVectors = Array(grid.size){ Array(grid[0].size ){ Double.POSITIVE_INFINITY} }
        distanceVectors[0][0] = 0.0


        fromNode["0 0"] = listOf()
        return distanceVectors

    }

    private fun findMinimumDistanceVector(
    ): Vertex {
        return distances.poll()
    }

    fun getShortestPathSequence( toV: List<Int>): Array<List<Int>>{
        if (toV.isEmpty() ){
            return arrayOf()
        }
            return arrayOf( toV, *getShortestPathSequence( fromNode["" + toV[0] + " " + toV[1]]!!.toList()))

    }

    fun shortestPathVerticalSeam(): Double{

        grid = arrayOf(
            Array(grid[0].size){0.0},
            *grid,
            Array(grid[0].size){0.0}
        )

        initialiseInfiniteDistanceVectors()

        while (distances.isNotEmpty()){
            val shortest = findMinimumDistanceVector()

            val adjacentVectors = getAdjacentVectorsVerticalSeam(shortest.name)

            updateAdjacentVectors(shortest, adjacentVectors)

        }


        return distanceVectors[grid.size - 1][grid[0].size - 1]


    }
    fun shortestPath(): Double {

        initialiseInfiniteDistanceVectors()

        while (distances.isNotEmpty()){
            val shortest = findMinimumDistanceVector()

            val adjacentVectors = getAdjacentVectors(shortest.name)

            updateAdjacentVectors(shortest, adjacentVectors)

        }


        return distanceVectors[grid[0].size - 1][grid.size - 1]


    }




}


fun main(){

    val grid = Array(1){ arrayOf(1.0, 2.0) }
    val dk = Dijkstra(grid)

    dk.printGrid()

}