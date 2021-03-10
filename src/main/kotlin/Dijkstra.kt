package seamcarving

import java.util.PriorityQueue


class Dijkstra(var grid: Array<Array<Double>>) {

    private var distances = PriorityQueue(grid[0].size * grid.size){
        a:Map.Entry<String, Double> , b:Map.Entry<String,Double> ->
        if(b.value > a.value){
            - 1
        }else {
            + 1
        }

    }

    var distanceVectors = mutableMapOf<String, Double>()
    private var fromNode = mutableMapOf<List<Int>, List<Int>>()

    fun printGrid() {
        repeat(grid.size) { y ->
            repeat(grid[0].size) { x ->
                print("" +grid[y][x] + ", ")
            }
            println()
        }
    }

    private fun validAdjacencyVectors (adjacentVectors: List<String>): List<String> {

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
    private fun seamVerticalAdjacency (x:Int, y:Int): List<String> {
        return listOf(
            listOf(x ,    y + 1).joinToString(" ") ,
            listOf(x - 1 ,    y + 1).joinToString(" ") ,
            listOf(x  + 1 ,    y + 1).joinToString(" ") ,
        )
    }
    private fun imaginaryVerticalSeamAdjacency (x:Int, y:Int): List<String> {
        return listOf(
            listOf(x - 1, y).joinToString(" "),
            listOf(x + 1, y).joinToString(" "),
            listOf(x ,    y + 1).joinToString(" "),
            listOf(x - 1, y + 1).joinToString(" "),
            listOf(x + 1, y + 1).joinToString(" "),
        )
    }
    private fun gridAdjacency(x:Int, y:Int):List<String>{
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
        val (x, y) = vector.split(" ").map { it.toInt() }

        val adjacentVectors = gridAdjacency(x,y)

        return validAdjacencyVectors(adjacentVectors)

    }
    private fun getAdjacentVectorsVerticalSeam (vector: String ): List<String> {
        val (x, y) = vector.split(" ").map { it.toInt() }

        val adjacentVectors: List<String> = if (y == 0 || y == grid.size - 1){
            imaginaryVerticalSeamAdjacency(x,y)
        }else {
            seamVerticalAdjacency(x,y)
        }

        return validAdjacencyVectors(adjacentVectors)
    }

    private fun updateAdjacentVectors(vector: String, previousDistance: Double, adjacentVectors: List<String>, shortestPath: MutableSet<String>) {

        adjacentVectors
            .forEach{ adjacentVector ->
                if (!shortestPath.contains(adjacentVector)){
                    val (x, y) = adjacentVector.split(" ").map{it.toInt()}
                    val currentMinimumDistanceToAdjacent = previousDistance + grid[y][x]
                    var currentValueOfAdjacent = Double.POSITIVE_INFINITY

                    try {
                        currentValueOfAdjacent = distanceVectors[adjacentVector]!!.toDouble()
                    }catch (e:Exception){
//                        distanceVectors[adjacentVector] = Double.POSITIVE_INFINITY
//                        distances.add(mapOf(adjacentVector to Double.POSITIVE_INFINITY).entries.first())
                    }

                    if (currentMinimumDistanceToAdjacent < currentValueOfAdjacent){
                        distanceVectors[adjacentVector] = currentMinimumDistanceToAdjacent

                        distances.add(mapOf(adjacentVector to currentMinimumDistanceToAdjacent).entries.first())

                        val (vx, vy) = vector.split(" ").map{it.toInt()}
                        fromNode[listOf(x,y)] = listOf(vx,vy)

                    }
                }

            }


    }
    private fun initialiseInfiniteDistanceVectors():MutableMap<String, Double> {

        distances.add(mapOf("0 0" to 0.0).entries.first())

        distanceVectors["0 0"] = 0.0
        fromNode[listOf(0,0)] = listOf()
        return distanceVectors

    }

    private fun findMinimumDistanceVector(
    ): Map.Entry<String, Double> {
        return distances.remove()
    }

    fun getShortestPathSequence( toV: List<Int>): Array<List<Int>>{
        if (toV.isEmpty() ){
            return arrayOf()
        }
            return arrayOf( toV, *getShortestPathSequence( fromNode[toV]!!.toList()))

    }

    fun shortestPathVerticalSeam(): Double{

        grid = arrayOf(
            Array(grid[0].size){0.0},
            *grid,
            Array(grid[0].size){0.0}
        )
        val finish: String = "" + (grid[0].size - 1) + " " + (grid.size - 1)
        println("GRID: $grid")
        println("FINISH: $finish")


        val shortestPathSet = mutableSetOf<String>()

        initialiseInfiniteDistanceVectors()

        println(distanceVectors)

        do {
            val shortest = findMinimumDistanceVector()

            val shortestKey = shortest.key
            val shortestValue = shortest.value

            if (shortestKey == finish){
                return distanceVectors[finish]!!.toDouble()
            }

            distanceVectors.remove(shortestKey)

//            println("shortest key : $shortestKey")

            shortestPathSet.add(shortestKey)
            val adjacentVectors = getAdjacentVectorsVerticalSeam(shortestKey)

            updateAdjacentVectors(shortestKey, shortestValue, adjacentVectors, shortestPathSet)


        } while ( shortest.key != finish )




        return Double.POSITIVE_INFINITY


    }
    fun shortestPath(): Double {

        val shortestPathSet = mutableSetOf<String>()
        val distanceVectors = initialiseInfiniteDistanceVectors()
        val finish: String = "" + (grid[0].size - 1) + " " + (grid.size - 1)

                println(distanceVectors)

            do {
                val shortest = findMinimumDistanceVector()


                val shortestKey = shortest.key

                if (shortestKey == finish){
                    return distanceVectors[finish]!!.toDouble()
                }
                distanceVectors.remove(shortestKey)

//                if (shortestPathSet.contains(shortestKey)) {
//                    distanceVectors.remove(shortestKey)
//                }

                val shortestValue = shortest.value
//                println("shortest key : $shortestKey")

                shortestPathSet.add(shortestKey)
                val adjacentVectors = getAdjacentVectors(shortestKey)

                updateAdjacentVectors(shortestKey, shortestValue, adjacentVectors, shortestPathSet)


//            println(shortestPathSet)

            } while ( shortest.key != finish )

            return Double.POSITIVE_INFINITY




    }




}


fun main(){

    val grid = Array(1){ arrayOf(1.0, 2.0) }
    val dk = Dijkstra(grid)

    dk.printGrid()

}