package seamcarving

import java.util.*
import java.util.PriorityQueue
import javax.print.DocFlavor


class Dijkstra(var grid: Array<Array<Double>>) {

//    var from = mutableMapOf<List<Int>, Double >()

    var distances = PriorityQueue { a: Map.Entry<String, Double>, b:Map.Entry<String,Double> ->
        if (a.value == b.value) b.key.compareTo(
            a.key
        ) else (a.value - b.value).toInt()
    }

    var distanceVectors = mutableMapOf<String, Double>()
    var fromNode = mutableMapOf<List<Int>, List<Int>>()

    fun printGrid() {
        repeat(grid.size) { y ->
            repeat(grid[0].size) { x ->
                print("" +grid[y][x] + ", ")
            }
            println()
        }
    }

    fun fillAdjacentVectors (adjacentVectors: List<String>): List<String> {

        return adjacentVectors
            .filter{ adjacentVector ->
                try {
                    val (vx, vy) = adjacentVector.split(" ").map { it.toInt() }

                    grid[vy][vx]
                    true
                }catch(e: Exception){
                    false
                }
            }
    }
    fun seamAdjacency (x:Int, y:Int): List<String> {
        return listOf(
            listOf(x ,    y + 1).joinToString(" ") ,
            listOf(x - 1 ,    y + 1).joinToString(" ") ,
            listOf(x  + 1 ,    y + 1).joinToString(" ") ,
        )
    }
    fun imaginarySeamAdjacency (x:Int, y:Int): List<String> {
        return listOf(
            listOf(x - 1, y).joinToString(" "),
            listOf(x + 1, y).joinToString(" "),
            listOf(x ,    y + 1).joinToString(" "),
            listOf(x - 1, y + 1).joinToString(" "),
            listOf(x + 1, y + 1).joinToString(" "),
        )
    }
    fun gridAdjacency(x:Int, y:Int):List<String>{
        return listOf(
            listOf(x - 1, y).joinToString(" "),
            listOf(x + 1, y).joinToString(" "),
            listOf(x ,    y + 1).joinToString(" "),
            listOf(x ,    y - 1).joinToString(" "),
            listOf(x - 1, y + 1).joinToString(" "),
            listOf(x + 1, y - 1).joinToString(" "),
            listOf(x - 1, y - 1).joinToString(" "),
            listOf(x + 1, y + 1).joinToString(" "),
        )
    }

    fun getAdjacentVectors (vector: String): List<String> {
        val vectors  = distanceVectors
        val (x, y) = vector.split(" ").map { it.toInt() }



        val adjacentVectors = gridAdjacency(x,y)

        return fillAdjacentVectors(adjacentVectors)

    }
    fun getAdjacentVectorsVerticalSeam (vector: String ): List<String> {
        val (x, y) = vector.split(" ").map { it.toInt() }


        val adjacentVectors: List<String>


        if (y == 0 || y == grid.size - 1){
            adjacentVectors = imaginarySeamAdjacency(x,y)
        }else {
            adjacentVectors = seamAdjacency(x,y)
        }

        return fillAdjacentVectors(adjacentVectors)
    }

    fun updateAdjacentVectors(vector: String, vectorValue: Double,  adjacentVectors: List<String> ,shortestPath: MutableSet<String>) {

//        println("Adjacents: $adjacents")
        val vectors  = distanceVectors

        val previousDistance = vectorValue
        adjacentVectors
            .filterNot{
                shortestPath.contains(it)
            }
            .filter{ adjacentVector ->
//                println(adjacentVector.toString())
                val (x, y) = adjacentVector.split(" ").map{it.toInt()}
                val currentMinimumDistanceToAdjacent = previousDistance + grid[y][x]
                    var currentValueOfAdjacent = Double.POSITIVE_INFINITY
                    try {
                        currentValueOfAdjacent = vectors[adjacentVector]!!.toDouble()
                    }catch (e:Exception){
                        vectors[adjacentVector] = Double.POSITIVE_INFINITY
                    }
//                println("$adjacentVector distance from source = $currentMinimumDistanceToAdjacent")
//                println("$adjacentVector current distance = $currentValueOfAdjacent")
                    currentMinimumDistanceToAdjacent < currentValueOfAdjacent

            }
            .forEach{ adjacentVector ->
//                vectors[adjacentVector] = vectors[vector]!!.toDouble() + grid[adjacentVector[1]][adjacentVector[0]]
                val (x, y) = adjacentVector.split(" ").map{it.toInt()}
                val distance = previousDistance + grid[y][x]
                vectors[adjacentVector] = distance
                val (vx, vy) = vector.split(" ").map{it.toInt()}
                fromNode[listOf(x,y)] = listOf(vx,vy)
            }


    }
    fun initialiseInfiniteDistanceVectors():MutableMap<String, Double> {

//        repeat(grid.size) { y ->
//
//            repeat(grid[0].size ) { x ->
//                distanceVectors["$x $y"] = Double.POSITIVE_INFINITY
//            }
//        }
        distances.add(mapOf("0 0" to 0.0).entries.first())

        distanceVectors["0 0"] = 0.0
        fromNode[listOf(0,0)] = listOf()
        return distanceVectors

    }

    fun findMinimumDistanceVector(
        shortestPathSet: MutableSet<String>
    ): Map.Entry<String, Double> {
        var minDistance = Double.POSITIVE_INFINITY

        var shortestKey = ""


        distanceVectors
        .forEach{ vector ->
            if (shortestPathSet.contains(vector.key)){

            }else {
//                println("wowza")
//                println(vector)
                if (vector.value < minDistance) {
                    minDistance = vector.value
                    shortestKey = vector.key
                }
            }
        }

        return mapOf(shortestKey to minDistance ).entries.first()
    }

    fun getShortestPathSequence( toV: List<Int>): Array<List<Int>>{
        if (toV.isEmpty() ){
            return arrayOf()
        }
            return arrayOf( toV, *getShortestPathSequence( fromNode[toV]!!.toList()))

    }

    fun shortestPathSeam(): List<String> {

        grid = arrayOf(
            Array(grid[0].size){0.0},
            *grid,
            Array(grid[0].size){0.0}
        )
        val start: String = "0 0"
        val finish: String = "" + (grid[0].size - 1) + " " + (grid.size - 1)
        println("GRID: $grid")
        println("FINISH: $finish")


        val shortestPathSet = mutableSetOf<String>()

        initialiseInfiniteDistanceVectors()

        println(distanceVectors)

        do {
            var shortest = findMinimumDistanceVector( shortestPathSet)

            var shortestKey = shortest.key
            var shortestValue = shortest.value
            println("shortestkey : $shortestKey")

            shortestPathSet.add(shortestKey)
            val adjacentVectors = getAdjacentVectorsVerticalSeam(shortestKey)

            updateAdjacentVectors(shortestKey, shortestValue, adjacentVectors, shortestPathSet)

            distanceVectors.remove(shortestKey)

//            println(shortestPathSet)

        } while ( shortest.key != finish )




        return shortestPathSet.toList()


    }
    fun shortestPath(): Double? {

        val shortestPathSet = mutableSetOf<String>()
        val distanceVectors = initialiseInfiniteDistanceVectors()
        val finish: String = "" + (grid[0].size - 1) + " " + (grid.size - 1)

                println(distanceVectors)

            do {
                var shortest = findMinimumDistanceVector( shortestPathSet)

                var shortestKey = shortest.key
                var shortestValue = shortest.value
                println("shortestkey : $shortestKey")

                shortestPathSet.add(shortestKey)
                val adjacentVectors = getAdjacentVectors(shortestKey)

                updateAdjacentVectors(shortestKey, shortestValue, adjacentVectors, shortestPathSet)


//            println(shortestPathSet)

            } while ( shortest.key != finish )


        return distanceVectors[listOf(grid.size - 1, grid[0].size - 1).joinToString(" ")]


    }




}


fun main(){

    val grid = Array(1){ arrayOf(1.0, 2.0) }
    val dk = Dijkstra(grid)

    dk.printGrid()

}