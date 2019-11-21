package de.stf.play.ground

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import java.util.stream.Stream

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
internal class RecamanTest {
    fun provideTestArguments(): Stream<Arguments> =
        Stream.of(
            Arguments.of(12, TESTCHUNKS[0]),
            Arguments.of(24, TESTCHUNKS[0] + TESTCHUNKS[1]),
            Arguments.of(25, TESTCHUNKS[0] + TESTCHUNKS[1]),
            Arguments.of(26, TESTCHUNKS[0] + TESTCHUNKS[1] + TESTCHUNKS[2]),
            Arguments.of(36, TESTCHUNKS[0] + TESTCHUNKS[1] + TESTCHUNKS[2] + TESTCHUNKS[3]),
            Arguments.of(48, TESTCHUNKS[0] + TESTCHUNKS[1] + TESTCHUNKS[2] + TESTCHUNKS[3] + TESTCHUNKS[4])
        )

    @ParameterizedTest
    @MethodSource("provideTestArguments")
    fun recamanSequential(upTo: Int, expected: String) {
        recamanSeq
            .take(upTo)
            .joinToString(", ")
            .let {
                assertEquals(expected, it)
                println("RecamanSeq: $it")
            }
    }

    @ParameterizedTest
    @MethodSource("provideTestArguments")
    fun recamanRecursive(upTo: Int, expected: String) {
        recamanRec(upTo)
            .joinToString(", ")
            .let {
                assertEquals(expected, it)
                println("RecamanRec: $it")
            }
    }

    @ParameterizedTest
    @MethodSource("provideTestArguments")
    fun recamanImperative(upTo: Int, expected: String) {
        recamanImp(upTo)
            .joinToString(", ")
            .let {
                assertEquals(expected, it)
                println("RecamanImp: $it")
            }
    }

    @ParameterizedTest
    @MethodSource("provideTestArguments")
    fun recamanFold(upTo: Int, expected: String) {
        recamanFold(upTo)
            .joinToString(", ")
            .let {
                assertEquals(expected, it)
                println("RecamanFold: $it")
            }
    }

    companion object {
        val TESTCHUNKS = listOf(
            "0, 1, 3, 6, 2, 7, 13, 20, 12, 21, 11, 22",
            ", 10, 23, 9, 24, 8, 25, 43, 62, 42, 63, 41, 18",
            ", 17", // seq failed first with n=26
            ", 16, 44, 15, 45, 14, 46, 79, 113, 78",
            ", 114, 77, 39, 38, 37, 80, 36, 81, 35, 82")

        val recamanSeq = sequence {
            val sequence = mutableSetOf<Int>()
            var element = 0
            while (true) {
                yield(element)
                sequence.add(element)
                val n = sequence.size
                val maybeNext = element - n
                element =
                    if (maybeNext >= 0 && maybeNext !in sequence)
                        maybeNext
                    else
                        element + n
            }
        }

        fun recamanRec(upTo: Int): Collection<Int> =
            recamanRec(0, 0, upTo, mutableSetOf<Int>())
        tailrec fun recamanRec(element: Int, n: Int, upTo: Int, sequence: MutableSet<Int>): MutableSet<Int> {
            val maybeNext = element - n
            val next =
                if (maybeNext >= 0 && maybeNext !in sequence)
                    maybeNext
                else
                    element + n
            sequence.add(next)
            return if (n + 1 < upTo)
                recamanRec(next, n + 1, upTo, sequence)
            else
                sequence
        }

        fun recamanImp(upTo: Int): Collection<Int> {
            val sequence = mutableSetOf<Int>()
            var element = 0
            for (n in 0 until upTo) {
                val maybeNext = element - n
                val next =
                    if (maybeNext >= 0 && maybeNext !in sequence)
                        maybeNext
                    else
                        element + n
                sequence.add(next)
                element = next
            }
            return sequence
        }

        fun recamanFold(upTo: Int) = (0..upTo)
            .fold(mutableSetOf()) { list: MutableSet<Int>, n: Int ->
                val last =
                    if (n == 0)
                        0
                    else
                        list.last()
                val maybeNext = last - n
                val next =
                    if (maybeNext >= 0 && maybeNext !in list)
                        maybeNext
                    else
                        last + n
                list.add(next)
                list
            }
    }
}