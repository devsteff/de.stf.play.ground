package de.stf.play.ground.util

import de.stf.play.ground.SieveAlgorythm
import org.junit.Assert
import org.junit.Test
import kotlin.system.measureTimeMillis

class SieveAlgorythmTest {

    @Test
    fun simple() {
        val result = SieveAlgorythm().sieveSimple(SieveAlgorythm.noOfPrimes)
        val primes = result.filter { it }
        Assert.assertEquals("Primzahlen falsch ermittelt", primes.size, 17986)
    }

    @Test
    fun coroutines() {
        val result = SieveAlgorythm().sieveCoroutines(SieveAlgorythm.noOfPrimes)
        val primes = result.filter { it }
        Assert.assertEquals("Primzahlen falsch ermittelt", primes.size, 17986)
    }

    @Test
    fun sharedMutableState() {
        val result = SieveAlgorythm().sieveSharedMutableState(SieveAlgorythm.noOfPrimes)
        val primes = result.filter { it }
        Assert.assertEquals("Primzahlen falsch ermittelt", primes.size, 17986)
    }

    @Test
    fun performnce() {
        val simple = measureTimeMillis { SieveAlgorythm().sieveSimple(SieveAlgorythm.noOfPrimes) }
        val coroutine = measureTimeMillis { SieveAlgorythm().sieveCoroutines(SieveAlgorythm.noOfPrimes) }
        Assert.assertTrue("Expected simple is faster than coroutine", simple < coroutine)
        val shared = measureTimeMillis { SieveAlgorythm().sieveSharedMutableState(SieveAlgorythm.noOfPrimes) }
        Assert.assertTrue("Expected shared is faster than coroutine", shared < coroutine)
        Assert.assertTrue("Expected shared is faster than simple", shared < simple)
    }
}