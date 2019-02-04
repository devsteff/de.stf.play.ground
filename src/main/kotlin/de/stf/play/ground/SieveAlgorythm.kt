package de.stf.play.ground

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import kotlin.math.pow
import kotlin.math.sqrt
import kotlin.system.measureTimeMillis

class SieveAlgorythm() {

    companion object {
        const val noOfPrimes = 200000
    }

    fun main(args: Array<String>) {
        val sieves: Array<Array<Boolean>> = arrayOf(arrayOf(), arrayOf(), arrayOf())
        val simple = measureTimeMillis {
            sieves[0] = sieveSimple(noOfPrimes)
        }
        val coroutine = measureTimeMillis {
            sieves[1] = sieveCoroutines(noOfPrimes)
        }
        val state = measureTimeMillis {
            sieves[2] = sieveSharedMutableState(noOfPrimes)
        }
        println("Simple sieve (#prims=$noOfPrimes) computed in $simple ms")
        //printPrimes(sieves[0])
        println("\nCoroutines sieve (#prims=$noOfPrimes) computed in $coroutine ms")
        //printPrimes(sieves[1])
        println("\nShared mutable state sieve (#prims=$noOfPrimes) computed in $state ms")
        //printPrimes(sieves[2])
    }

    fun sieveSimple(n: Int): Array<Boolean> {
        val indices = Array(n) { true }
        val limit = sqrt(n.toDouble()).toInt()
        val range = 2..limit
        for (i in range) {
            if (indices[i]) {
                var j = i.toDouble().pow(2).toInt()
                while (j < n) {
                    indices[j] = false
                    j += i
                }
            }
        }
        return indices
    }

    fun sieveCoroutines(n: Int): Array<Boolean> {
        val indices = Array(n) { true }
        val limit = sqrt(n.toDouble()).toInt()
        val range = 2..limit
        runBlocking {
            withContext(Dispatchers.Default) {
                for (i in range) {
                    val result = computeForSingleNumber(i, n)
                    mergeBooleanArrays(indices, result)
                }
            }
        }
        return indices
    }

    fun sieveSharedMutableState(n: Int): Array<Boolean> {
        val indices = Array(n) { true }
        val limit = sqrt(n.toDouble()).toInt()
        val range = 2..limit
        runBlocking {
            withContext(Dispatchers.Default) {
                for (i in range) {
                    computeForSingleNumber(i, indices)
                }
            }
        }
        return indices
    }

    fun printPrimes(indices: Array<Boolean>?) {
        indices?.forEachIndexed { index, value -> if (value && index > 1) print("$index ") }
        println()
    }

    fun mergeBooleanArrays(a1: Array<Boolean>, a2: Array<Boolean>) {
        val range = 0 until a1.size
        for (i in range) a1[i] = a1[i] && a2[i]
    }

    fun computeForSingleNumber(i: Int, n: Int): Array<Boolean> {
        val indices = Array(n) { true }
        if (indices[i]) {
            var j = i.toDouble().pow(2).toInt()
            while (j < n) {
                indices[j] = false
                j += i
            }
        }
        return indices
    }

    suspend fun computeForSingleNumber(i: Int, indices: Array<Boolean>) {
        val n = indices.size
        if (indices[i]) {
            var j = i.toDouble().pow(2).toInt()
            while (j < n) {
                indices[j] = false
                j += i
            }
        }
    }
}
