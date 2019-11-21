package de.stf.play.ground

import mu.KotlinLogging
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import reactor.core.publisher.Flux
import reactor.core.publisher.Signal
import reactor.test.StepVerifier
import java.awt.image.DataBuffer
import java.security.MessageDigest
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

//    @Test
//    fun testMD5WithSmallBuffer() {
//        val testString = "someRandomContentToTesteee"
//        val contentHashing =
//            ContentHashing.hashing(testString.toDataBufferFlux())
//        StepVerifier.create(contentHashing.first)
//            .expextNextMatches {
//                String(it.asInputStream(true).readBytes()) == "someRandomCo"
//            }
//            .expextNextMatches {
//                String(it.asInputStream(true).readBytes()) == "ntentToTeste"
//            }
//            .expextNextMatches {
//                String(it.asInputStream(true).readBytes()) == "ee"
//            }
//            .verifyComplete()
//        assertEquals(contentHashing.seconf.md5, DigestUtils.md5Hex(testString))
//
//        fun String.toDataBufferFlux() : Flux<DataBuffer> =
//            DataBufferUtils.readInputStream( { byteInputStream() },
//                DefaultDataBufferFactory(), 12)
//    }


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

//class ContentHashing private constructor() {
//    lateinit var md5: String
//    companion object {
//        fun hashing(source: Flux<DataBuffer>) : Pair<Flux<DataBuffer>>, ContentHashing> {
//            val contentHashing = ContentHashing()
//            val digest = MessageDigest.getInstance("MD5")
//            return source.doOnEach { t: Signal<DataBuffer> ->
//                t.get()?.apply { digest.update(asByteBuffer()) }
//            }.doOnComplete {
//                contentHashing.md5 = Hex.encodeHexString(digest.digest())
//            } to contentHashing
//        }
//    }
//}