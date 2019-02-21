package de.stf.play.ground.test

import org.junit.Assert.*
import org.junit.Test
import reactor.core.publisher.Flux
import reactor.test.StepVerifier
import java.util.Arrays
import java.util.function.BiFunction

class ReactiveTest {
    @Test
    fun aTest() {
        val fewWords = Flux.just("Hello", "World")

        StepVerifier.create(fewWords)
            .expectNext("Hello")
            .expectNextCount(1)
            .expectComplete()
            .verify()
    }

    @Test
    fun bTest() {
        val manyWordsSorted = Flux.fromIterable<String>(words)
            .sort()

        StepVerifier.create(manyWordsSorted)
            .expectNext("brown")
            .expectNextCount(7)
            .expectNext("the")
            .expectComplete()
            .verify()

        val manyWordsReversedSorted = Flux.fromIterable<String>(words)
            .sort { o1, o2 -> o1.compareTo(o2) * -1 }

        StepVerifier.create(manyWordsReversedSorted)
            .expectNext("the")
            .expectNextCount(7)
            .expectNext("brown")
            .expectComplete()
            .verify()
    }

    @Test
    fun findingMissingLetter() {
        val manyLetters = Flux
            .fromIterable(words)
            //.log()
            .flatMap { word ->
                val x = word.split("")
                Flux.fromIterable(x) }
            //.log()
            .distinct()
            //.log()
            .sort()
            //.log()
            .zipWith(
               Flux.range(1, Integer.MAX_VALUE),
               BiFunction<String, Int, String> { s, c -> String.format("%2d. %s", c, s) }
            )
            .map { it }
            .log()

        StepVerifier.create(manyLetters)
            .expectNext(" 1. ")
            .expectNext(" 2. a")
            .expectNextCount(23)
            .expectNext("26. z")
            .expectComplete()
            .verify()

        //manyLetters.subscribe { println("*** $it") }
    }

    @Test
    fun givenFluxes_whenZipWithIsInvoked_thenZipWith() {
        val fluxOfIntegers = evenNumbers
            .log()
            .zipWith( oddNumbers, BiFunction<Int, Int, Int> { even, odd -> even * odd } )
            .log()

        StepVerifier.create(fluxOfIntegers)
            .expectNext(2)  // 2 * 1
            .expectNext(12) // 4 * 3
            .expectComplete()
            .verify()
    }

    companion object {
        private val words = Arrays.asList(
            "the",
            "quick",
            "brown",
            "fox",
            "jumped",
            "over",
            "the",
            "lazy",
            "dog"
        )

        val evenNumbers = Flux
            .range(1, 5)
            .filter { x -> x!! % 2 == 0 } // i.e. 2, 4

        val oddNumbers = Flux
            .range(1, 5)
            .filter { x -> x!! % 2 > 0 }  // ie. 1, 3, 5
    }
}
