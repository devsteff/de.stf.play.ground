package de.stf.play.ground

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import kotlin.system.measureTimeMillis

class SieveAlgorythmTest {

    @Test
    fun simple() {
        val result = SieveAlgorythm().sieveSimple(SieveAlgorythm.noOfPrimes)
        val primes = result.filter { it }
        assertEquals(primes.size, 17986, "Primzahlen falsch ermittelt")
    }

    @Test
    fun coroutines() {
        val result = SieveAlgorythm().sieveCoroutines(SieveAlgorythm.noOfPrimes)
        val primes = result.filter { it }
        assertEquals(primes.size, 17986, "Primzahlen falsch ermittelt")
    }

    @Test
    fun sharedMutableState() {
        val result = SieveAlgorythm().sieveSharedMutableState(SieveAlgorythm.noOfPrimes)
        val primes = result.filter { it }
        assertEquals(primes.size, 17986, "Primzahlen falsch ermittelt")
    }

    @Test
    fun performnce() {
        val simple = measureTimeMillis { SieveAlgorythm().sieveSimple(SieveAlgorythm.noOfPrimes) }
        val coroutine = measureTimeMillis { SieveAlgorythm().sieveCoroutines(SieveAlgorythm.noOfPrimes) }
        assertTrue(simple < coroutine, "Expected simple is faster than coroutine")
        val shared = measureTimeMillis { SieveAlgorythm().sieveSharedMutableState(SieveAlgorythm.noOfPrimes) }
        assertTrue(shared < coroutine, "Expected shared is faster than coroutine")
        assertTrue(shared < simple, "Expected shared is faster than simple")
    }
}