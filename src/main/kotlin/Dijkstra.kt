package seamcarving

import java.util.PriorityQueue


class Dijkstra(var grid: Array<Array<Double>>) {

    private var distances = PriorityQueue(grid[0].size * grid.size){
        a:Map.Entry<List<Int>, Double> , b:Map.Entry<List<Int>,Double> ->
        if(b.value > a.value){
            - 1
        }else {
            + 1
        }

    }

    var distanceVectors = hashMapOf<List<Int>, Double>()
    private var fromNode = hashMapOf<List<Int>, List<Int>>()

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

        return validAdjacencyVectors(adjacentVectors)
    }


    private fun updateAdjacentVectors(vector: Map.Entry<List<Int>, Double>, adjacentVectors: Set<List<Int>>, shortestPath: HashSet<List<Int>>) {

        val ( previousKey ,previousDistance) = vector

        adjacentVectors
            .stream()
            .forEach{ adjacentVector ->
                if (!shortestPath.contains(adjacentVector)){
                    val currentMinimumDistanceToAdjacent = previousDistance + grid[adjacentVector[1]][adjacentVector[0]]
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

                        fromNode[adjacentVector] = previousKey

                    }
                }

            }


    }
    private fun initialiseInfiniteDistanceVectors():MutableMap<List<Int>, Double> {

        distances.add(mapOf(listOf(0,0) to 0.0).entries.first())

        distanceVectors[listOf(0,0)] = 0.0
        fromNode[listOf(0,0)] = listOf()
        return distanceVectors

    }

    private fun findMinimumDistanceVector(
    ): Map.Entry<List<Int>, Double> {
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
        val finish = listOf(grid[0].size - 1 , grid.size - 1)

        val shortestPathSet = HashSet<List<Int>>()

        initialiseInfiniteDistanceVectors()

//        println(distanceVectors)

        do {
            val shortest = findMinimumDistanceVector()

            val shortestKey = shortest.key

            if (shortestKey == finish){
                return distanceVectors[finish]!!.toDouble()
            }

            distanceVectors.remove(shortestKey)

//            println("shortest key : $shortestKey")

            shortestPathSet.add(shortestKey)
            val adjacentVectors = getAdjacentVectorsVerticalSeam(shortestKey)

            updateAdjacentVectors(shortest, adjacentVectors, shortestPathSet)


        } while ( shortest.key != finish )




        return Double.POSITIVE_INFINITY


    }
    fun shortestPath(): Double {

        val distanceVectors = initialiseInfiniteDistanceVectors()

        val finish = listOf(grid[0].size - 1 , grid.size - 1)

        val shortestPathSet = HashSet<List<Int>>()


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

//                println("shortest key : $shortestKey")

                shortestPathSet.add(shortestKey)
                val adjacentVectors = getAdjacentVectors(shortestKey)

                updateAdjacentVectors(shortest, adjacentVectors, shortestPathSet)


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