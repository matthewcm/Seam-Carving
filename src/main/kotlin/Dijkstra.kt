package seamcarving



class Dijkstra(var grid: Array<Array<Double>>) {

//    var from = mutableMapOf<List<Int>, Double >()
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

    fun fillAdjacentVectors (adjacentVectors: List<List<Int>>): List<List<Int>> {

        return adjacentVectors
            .filter{ adjacentVector ->
                try {
                    grid[adjacentVector[1]][adjacentVector[0]]
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

    fun getAdjacentVectors (vector: String, vectors: MutableMap<String, Double>): List<String> {
        val (x, y) = vector.split(" ").map { it.toInt() }



        val adjacentVectors = gridAdjacency(x,y)

        return adjacentVectors
            .filter{ vectors.keys.contains(it)}
            .filter{ adjacentVector ->
                try {
                    val (vx, vy) = vector.split(" ").map { it.toInt() }

                    grid[vy][vx]
                    true
                }catch(e: Exception){
                    false
                }
            }

    }
    fun getAdjacentVectorsVerticalSeam (vector: String ): List<String> {
        val (x, y) = vector.split(" ").map { it.toInt() }


        val adjacentVectors: List<String>


        if (y == 0 || y == grid.size - 1){
            adjacentVectors = imaginarySeamAdjacency(x,y)
        }else {
            adjacentVectors = seamAdjacency(x,y)
        }

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

    fun updateAdjacentVectors(vector: String, adjacentVectors: List<String> ,shortestPath: MutableSet<String>) {

//        println("Adjacents: $adjacents")
        val vectors  = distanceVectors
        adjacentVectors
            .filter{
                vectors.contains(it)
            }
            .filterNot{
                shortestPath.contains(it)
            }
            .filter{ adjacentVector ->
//                println(adjacentVector.toString())
                val (x, y) = adjacentVector.split(" ").map{it.toInt()}
                val currentMinimumDistanceToAdjacent = vectors[vector]!!.toDouble() + grid[y][x]
                    val currentValueOfAdjacent = vectors[adjacentVector]!!.toDouble()
                println("$adjacentVector distance from source = $currentMinimumDistanceToAdjacent")
                println("$adjacentVector current distance = $currentValueOfAdjacent")
                    currentMinimumDistanceToAdjacent < currentValueOfAdjacent

            }
            .forEach{ adjacentVector ->
//                vectors[adjacentVector] = vectors[vector]!!.toDouble() + grid[adjacentVector[1]][adjacentVector[0]]
                val (x, y) = adjacentVector.split(" ").map{it.toInt()}
                val distance = vectors[vector]!!.toDouble() + grid[y][x]
                vectors[adjacentVector] = distance
                val (vx, vy) = vector.split(" ").map{it.toInt()}
                fromNode[listOf(x,y)] = listOf(vx,vy)
            }


    }
    fun initialiseInfiniteDistanceVectors():MutableMap<String, Double> {

        distanceVectors = mutableMapOf<String, Double>()

        repeat(grid.size) { y ->

            repeat(grid[0].size ) { x ->
                distanceVectors["$x $y"] = Double.POSITIVE_INFINITY
            }
        }

        distanceVectors["0 0"] = 0.0
        fromNode[listOf(0,0)] = listOf()
        return distanceVectors

    }

    fun findMinimumDistanceVector(
        shortestPathSet: MutableSet<String>
    ): String {
        var minDistance = Double.POSITIVE_INFINITY

        var shortestKey = ""

        distanceVectors.filterNot{vector ->
            shortestPathSet.contains(vector.key)
        }.forEach{ vector ->
            if (vector.value < minDistance) {
                minDistance = vector.value
                shortestKey = vector.key
            }
        }

//        distanceVectors = distanceVectors
//            .filter{ vector -> vector.value < minDistance }
//            .filter{vector -> shortestPathSet.contains(vector.key)}
//            .toMutableMap()
//
        return shortestKey
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


        println(grid.size)

        val shortestPathSet = mutableSetOf<String>()
        distanceVectors = initialiseInfiniteDistanceVectors()

        println(distanceVectors)

        var shortestKey: String
        while ( shortestPathSet != distanceVectors.keys.toSet() ){


            shortestKey = findMinimumDistanceVector( shortestPathSet)
            println("shortestkey : $shortestKey")

            shortestPathSet.add(shortestKey)
            val adjacentVectors = getAdjacentVectorsVerticalSeam(shortestKey)

            updateAdjacentVectors(shortestKey, adjacentVectors, shortestPathSet)
//            println(shortestPathSet)
        }

        return shortestPathSet.toList()


    }
    fun shortestPath(): Double? {

        val shortestPathSet = mutableSetOf<String>()
        val distanceVectors = initialiseInfiniteDistanceVectors()

                println(distanceVectors)

        while ( shortestPathSet != distanceVectors.keys.toSet() ){


            val shortestKey = findMinimumDistanceVector( shortestPathSet)
            println("Next shortest = $shortestKey")

            shortestPathSet.add(shortestKey)
            val adjacentVectors = getAdjacentVectors(shortestKey, distanceVectors)

            println("Adjacencies $adjacentVectors")

            updateAdjacentVectors(shortestKey, adjacentVectors, shortestPathSet)
                println(distanceVectors)
//                println(distanceVectors.keys)
//                println(shortestPathSet)
//        distanceVectors.remove(shortestKey)

            println(fromNode)

        }

//        println(shortestPathSet)
//        return distanceVectorsj

        return distanceVectors[listOf(grid.size - 1, grid[0].size - 1).joinToString(" ")]


    }




}


fun main(){

    val grid = Array(1){ arrayOf(1.0, 2.0) }
    val dk = Dijkstra(grid)

    dk.printGrid()

}