package de.stf.play.ground.test

import mu.KotlinLogging
import org.junit.Test
import reactor.core.publisher.Flux
import reactor.test.StepVerifier
import java.util.Arrays
import java.util.function.BiFunction

private val logger = KotlinLogging.logger {}

class ReactiveTest {
    @Test
    fun fewWords() {
        val fewWords = Flux.just("Hello", "World")
        logger.debug { "############ fewWords" }
        StepVerifier.create(fewWords)
            .expectNext("Hello")
            .expectNextCount(1)
            .expectComplete()
            .verify()
    }

    @Test
    fun manyWordsSortedTest() {
        val manyWordsSorted = WORDS
            .sort()
        logger.info { "############ manyWordsSortedTest" }
        StepVerifier.create(manyWordsSorted)
            .expectNext("brown")
            .expectNextCount(7)
            .expectNext("the")
            .expectComplete()
            .verify()
    }

    @Test
    fun manyWordsReversedSortedTest() {
        val manyWordsReversedSorted = WORDS
            .sort { o1, o2 -> o1.compareTo(o2) * -1 }
        logger.warn { "############ manyWordsReversedSortedTest" }
        StepVerifier.create(manyWordsReversedSorted)
            .expectNext("the")
            .expectNextCount(7)
            .expectNext("brown")
            .expectComplete()
            .verify()
    }

    @Test
    fun findingMissingLetter() {
        val manyLetters = WORDS
            //.log()
            .flatMap { word ->
                val x = word.split("")
                Flux.fromIterable(x)
            }
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
            //.log()
        logger.error { "############ findingMissingLetter" }
        StepVerifier.create(manyLetters)
            .expectNext(" 1. ")
            .expectNext(" 2. a")
            .expectNextCount(16)
            .consumeNextWith { println("should be r: '$it'") }
            .consumeNextWith { println("should be t: '$it'") } // s is missing
            .expectNextCount(6)
            .expectComplete()
            .verify()
    }

    @Test
    fun givenFluxes_thenZipWith() {
        val fluxOfIntegers = EVEN_INT_FLUX
            //.log()
            .zipWith(ODD_INT_FLUX, BiFunction<Int, Int, String> { even, odd -> "$even*$odd=${even * odd}" })
            //.log()
        logger.debug { "############ givenFluxes_thenZipWith" }
        StepVerifier.create(fluxOfIntegers)
            .expectNext("2*1=2")  // 2 * 1
            .expectNext("4*3=12") // 4 * 3
            .expectComplete()
            .verify()
    }

    companion object {
        private val WORDS = Flux.fromIterable<String>(
            Arrays.asList(
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
        )

        val EVEN_INT_FLUX = Flux
            .range(1, 5)
            .filter { x -> x!! % 2 == 0 } // i.e. 2, 4

        val ODD_INT_FLUX = Flux
            .range(1, 5)
            .filter { x -> x!! % 2 > 0 }  // ie. 1, 3, 5
    }
}
