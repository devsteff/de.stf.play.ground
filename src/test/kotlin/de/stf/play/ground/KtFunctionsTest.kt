package de.stf.play.ground

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import java.util.Date
import kotlin.random.Random

class KtFunctionsTest {

    @Test
    fun pillepalleEqualsTest() {
        data class A(var i: Int)

        val a1 = A(1)
        val a2 = A(1)
        val a3 = A(2)
        a2.i = 2
        assert(a1 != a2)
        assert(a2 == a3)
    }

    @Test
    fun bufferTrimmingTest() {
        val buf = StringBuilder()
        buf.append("").appendln("\r").append("\n").appendln("\t")
        assertTrue(buf.isNotEmpty())

        val str0 = buf.toString()
        assertEquals("\r\n\n\t\n", str0)
        assertEquals(5, str0.length)

        val str1 = buf.trim()
        assertEquals(0, str1.length)

        val str2 = buf.toString().trim()
        assertEquals(0, str2.length)
    }

    @Test
    fun elvisTest() {
        fun String?.orEmpty() = "=\"${this ?: " "}\""
        val toTest: String? = null
        assertEquals("=\"\"", "".orEmpty(), "oops")
        assertEquals("=\"1\"", "1".orEmpty(), "oops")
        assertEquals("=\" \"", toTest.orEmpty(), "oops")
    }

    @Test
    fun trimmedMiddleTest() {
        fun String.trimmedTo(maxLen: Int): String {
            if (maxLen > 4 && this.length > maxLen) {
                val splitAt: Int = (maxLen - 3) / 2
                var addOn = 0
                if (maxLen > 5) addOn = splitAt % 2
                return this.substring(0, splitAt + addOn) + "..." + this.substring(this.length - splitAt)
            }
            return this
        }

        val testStr = "abcdefghijklmnopqrstuvw"

        assertEquals(testStr, testStr.trimmedTo(1), "under bound 1")
        assertEquals(testStr.length, testStr.trimmedTo(1).length, "under bound 1")

        assertEquals("a...w", testStr.trimmedTo(5), "min bound 5")
        assertEquals(5, testStr.trimmedTo(5).length, "min bound 5")

        assertEquals("ab...w", testStr.trimmedTo(6), "min bound 5+1")
        assertEquals(6, testStr.trimmedTo(6).length, "min bound 5+1")

        assertEquals("ab...vw", testStr.trimmedTo(7), "min bound 5+2")
        assertEquals(7, testStr.trimmedTo(7).length,"min bound 5+2")

        assertEquals("abcd...tuvw", testStr.trimmedTo(11),"middle odd")
        assertEquals(11, testStr.trimmedTo(11).length,"middle odd")

        assertEquals("abcd...uvw", testStr.trimmedTo(10),"middle even")
        assertEquals( 10, testStr.trimmedTo(10).length,"middle even")

        assertEquals(testStr, testStr.trimmedTo(testStr.length),"exact")
        assertEquals( testStr.length, testStr.trimmedTo(testStr.length).length,"exact")

        assertEquals( testStr, testStr.trimmedTo(100),"smaller than max")
        assertEquals( testStr.length, testStr.trimmedTo(100).length,"smaller than max")
    }

    @Test
    fun bigDecimalTest() {
        val amount = BigDecimal(1000.00)
        var period = 33
        assertEquals( 30, amount.div(BigDecimal(period)).toInt(),"rounded")
        period = 10
        assertEquals( 100, amount.div(BigDecimal(period)).toInt(),"rounded")
    }

    @Test
    fun unitTest() {
        val unitList = mutableListOf<() -> Unit>()
        val iterator = 1..3
        var result = ""
        for ((i, j) in iterator.iterator().withIndex()) {
            unitList += { result += "$i $j; " }
        }
        unitList.forEach { it() }
        assertEquals( "0 1; 1 2; 2 3; ", result,"surprise1")

        unitList.clear()
        var i = 0
        result = ""
        iterator.iterator().forEach {
            i++
            unitList += { result += "$i $it; " }
        }
        unitList.forEach { it() }
        assertEquals( "3 1; 3 2; 3 3; ", result,"surprise2")

        unitList.clear()
        i = 0
        result = ""
        for (j in iterator) {
            i++
            unitList += { result += "$i $j; " }
        }
        unitList.forEach { it() }
        assertEquals( "3 1; 3 2; 3 3; ", result, "surprise3")
    }

    @Test
    fun sortTest() {
        val myList = listOf(5, 7, 1, 0, -5, 4, 9)
        val sortedList = myList.sortedBy { it }
        assertEquals( listOf(-5, 0, 1, 4, 5, 7, 9), sortedList,"sorted")
        val sortedDescendingList = myList.sortedByDescending { it }
        assertEquals( listOf(9, 7, 5, 4, 1, 0, -5), sortedDescendingList,"sorted")
        val myStringList = listOf("abc", "zxy", "zab")
        val sortedStringList = myStringList.sortedBy { it[1] }
        assertEquals( listOf("zab", "abc", "zxy"), sortedStringList,"sorted")
    }

    @Test
    fun companionTest() {
        val xxx = listOf("aa", "ba", "ca").joinToString(",", "", "", 100, "")
        assertEquals( "aa,ba,ca", xxx,"local var")
        assertEquals( "aa,ba,ca", XXX,"companion const")
        assertEquals( xxx, yyy,"companion val")
        assertEquals( xxx, zzz,"companion val")
        assertTrue( "ba" in xxx,"in")
        assertTrue( "ab" !in xxx,"!in")
    }

    @Test
    fun ifEmptyTest() {
        var in1 = ""
        assertEquals("abc", in1.ifEmpty { "abc" },"replacement expected")
        in1 = "cba"
        assertEquals( "cba", in1.ifEmpty { "abc" },"original value expected")
    }

    @Test
    fun foldTest() {
        val total = NUMBER_LIST.fold(0, { total, next ->
            println("total=$total, next=$next")
            total + next
        })
        assertEquals(15, total)

        val mul = NUMBER_LIST.fold(1, { mul, next -> mul * next })
        assertEquals(120, mul)
    }

    @Test
    fun foldRightTest() {
        val total = NUMBER_LIST.foldRight(0, { next, total ->
            println("total=$total, next=$next")
            total + next
        })
        assertEquals(15, total)
    }

    @Test
    fun foldObjectListTest() {
        val quantity = PRODUCT_LIST.map { it.quantity }.fold(0, { total, next -> total + next })
        assertEquals(1500, quantity)
    }

    @Test
    fun foldObjectMapTest() {
        val quantity = PRODUCT_MAP.map { it.value.quantity }.fold(0, { total, next -> total + next })
        assertEquals(1500, quantity)
    }

    @Test
    fun partitionObjectListTest() {
        val (from250, less250) = PRODUCT_LIST.partition { it.quantity >= 250 }
        assertEquals(3, from250.size)
        assertEquals(2, less250.size)
    }

    @Test
    fun listTo() {
        val x = listOf("a", "b") to listOf(1, 2, 3)
        val y = mapOf<List<String>, List<Int>>(x)
        println(y)
    }

    @Test
    fun pairDeconstructing() {
        val (a, b) = testPair
        assertEquals("a", a)
        assertEquals(4711, b)
    }

    @Test
    fun tripleDeconstructing() {
        val (a, _, b) = testTriple
        assertEquals("a", a)
        assertEquals(true, b)
    }

    @Test
    fun functionPassiningTest() {
        val result = fooFunction(
            "hi there",
            Companion::buzFunction
        )
        assertEquals("ereht ih", result)

        val result1 = fooFunction("hi there") { m ->
            println("this is the second way of calling: $m")
            m.reversed()
        }
        assertEquals("ereht ih", result1)

        val result2 = fooFunction("hi there") {
            println("this is the third way of calling: $it")
            it.reversed()
        }
        assertEquals("ereht ih", result2)
    }

    @Test
    fun shuffleTest() {
        val words = mutableListOf<String>()
        words.addAll(SENTENCE.split(' '))
        val shuffeldWords = mutableListOf<String>()
        shuffeldWords.addAll(SENTENCE.split(' '))
        shuffeldWords.shuffle(Random(Date().time))
        println("$words shuffeled to $shuffeldWords")
        assertNotEquals(words, shuffeldWords)
    }

    @Test
    fun stillReadableTest() {
        val words = mutableListOf<String>()
        val newWords = mutableListOf<String>()
        words.addAll(SENTENCE.split(' '))
        words.forEach { newWords.add(it.stillReadable()) }
        println("$words\nstill readable as:\n$newWords")
        assertNotEquals(words, newWords)
    }

    private fun String.stillReadable(): String {
        if (this.length < 4) return this
        else {
            val first = this.first()
            val last = this.last()
            val shuffled = mutableListOf<Char>()
            val chars = this.substring(1, this.length - 1).toList()
            shuffled.addAll(chars)
            shuffled.shuffle(Random(Date().time))
            val newShuffled = shuffled.joinToString { "$it" }
            return first + newShuffled + last
        }
    }

    @Test
    fun enclosingMethodTest() {
        val apiName = "${javaClass.name}#${object {}.javaClass.enclosingMethod.name}"
        assertEquals("de.stf.play.ground.KtFunctionsTest#enclosingMethodTest", apiName)
        println("enclosing method is $apiName")
    }

    companion object {
        fun fooFunction(p1: String, anyFunction: (m: String) -> String): String {
            return anyFunction(p1)
        }

        // my function to pass into the other
        fun buzFunction(m: String): String {
            println("this is the first way of calling: $m")
            return m.reversed()
        }

        const val XXX = "aa,ba,ca"
        const val SENTENCE = "The quick brown fox jumps over the lazy dog"
        val zzz = listOf("aa", "ba", "ca").joinToString(",", "", "", 100, "")
        val yyy = zzz
        val NUMBER_LIST = listOf(1, 2, 3, 4, 5)
        val PRODUCT_LIST = listOf(
            Product("A", 100),
            Product("B", 200),
            Product("C", 300),
            Product("D", 400),
            Product("E", 500)
        )
        val PRODUCT_MAP = mapOf(
            "a" to Product("A", 100),
            "b" to Product("B", 200),
            "c" to Product("C", 300),
            "d" to Product("C", 400),
            "e" to Product("C", 500)
        )
        val testPair = "a" to 4711
        val testTriple = Triple("a", 4711, true)
    }
}

data class Product(val name: String, val quantity: Int)