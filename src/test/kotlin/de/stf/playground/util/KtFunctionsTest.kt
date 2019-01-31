package de.stf.playground.util

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.math.BigDecimal

class KtFunctionsTest {

    @Test
    fun pillepalleEqualsTest() {
        data class A (var i: Int)
        val a1 = A(1)
        val a2 = A(1)
        val a3 = A(2)
        a2.i = 2
        assert(a1!=a2)
        assert(a2==a3)
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
        assertEquals("oops", "=\"\"", "".orEmpty())
        assertEquals("oops", "=\"1\"", "1".orEmpty())
        assertEquals("oops", "=\" \"", toTest.orEmpty() )
    }

    @Test
    fun trimmedMiddleTest() {
        fun String.trimmedTo(maxLen: Int): String {
            if (maxLen > 4 && this.length > maxLen) {
                val splitAt: Int =  (maxLen-3) / 2
                var addOn = 0
                if(maxLen > 5) addOn = splitAt % 2
                return this.substring(0, splitAt + addOn) + "..." + this.substring(this.length - splitAt)
            }
            return this
        }
        val testStr = "abcdefghijklmnopqrstuvw"

        assertEquals("under bound 1", testStr, testStr.trimmedTo(1))
        assertEquals("under bound 1",testStr.length, testStr.trimmedTo(1).length)

        assertEquals("min bound 5","a...w", testStr.trimmedTo(5))
        assertEquals("min bound 5",5, testStr.trimmedTo(5).length)

        assertEquals("min bound 5+1","ab...w", testStr.trimmedTo(6))
        assertEquals("min bound 5+1",6, testStr.trimmedTo(6).length)

        assertEquals("min bound 5+2","ab...vw", testStr.trimmedTo(7))
        assertEquals("min bound 5+2",7, testStr.trimmedTo(7).length)

        assertEquals("middle odd","abcd...tuvw", testStr.trimmedTo(11))
        assertEquals("middle odd",11, testStr.trimmedTo(11).length)

        assertEquals("middle even","abcd...uvw", testStr.trimmedTo(10))
        assertEquals("middle even",10, testStr.trimmedTo(10).length)

        assertEquals("exact", testStr, testStr.trimmedTo(testStr.length))
        assertEquals("exact",testStr.length, testStr.trimmedTo(testStr.length).length)

        assertEquals("smaller than max",testStr, testStr.trimmedTo(100))
        assertEquals("smaller than max",testStr.length, testStr.trimmedTo(100).length)
    }

    @Test
    fun bigDecimalTest() {
        val amount = BigDecimal(1000.00)
        var period = 33
        assertEquals("rounded", 30, amount.div( BigDecimal(period) ).toInt())
        period = 10
        assertEquals("rounded", 100, amount.div( BigDecimal(period) ).toInt())
    }

    @Test
    fun unitTest() {
        val unitList = mutableListOf<() -> Unit>()
        val iterator = 1..3
        var result = ""
        for((i,j) in iterator.iterator().withIndex()) {
            unitList += { result += "$i $j; " }
        }
        unitList.forEach { it() }
        assertEquals("surprise1", "0 1; 1 2; 2 3; ", result)

        unitList.clear()
        var i = 0
        result = ""
        iterator.iterator().forEach {
            i++
            unitList += { result += "$i $it; " }
        }
        unitList.forEach { it() }
        assertEquals("surprise2", "3 1; 3 2; 3 3; ", result)

        unitList.clear()
        i = 0
        result = ""
        for (j in iterator) {
            i++
            unitList += { result += "$i $j; " }
        }
        unitList.forEach { it() }
        assertEquals("surprise3", "3 1; 3 2; 3 3; ", result)
    }

    @Test
    fun sortTest() {
        val myList = listOf(5, 7, 1, 0, -5, 4, 9)
        val sortedList = myList.sortedBy { it }
        assertEquals("sorted", listOf(-5,0, 1, 4, 5, 7, 9), sortedList)
        val sortedDescendingList = myList.sortedByDescending { it }
        assertEquals("sorted", listOf(9, 7, 5, 4, 1, 0, -5), sortedDescendingList)
        val myStringList = listOf("abc", "zxy", "zab")
        val sortedStringList = myStringList.sortedBy { it[1] }
        assertEquals("sorted", listOf("zab", "abc", "zxy"), sortedStringList)
    }

    @Test
    fun companionTest() {
        val xxx = listOf("aa", "ba", "ca").joinToString(",", "", "", 100, "")
        assertEquals("local var", "aa,ba,ca", xxx)
        assertEquals("companion const", "aa,ba,ca", KtFunctionsTest.xxx)
        assertEquals("companion val", xxx, KtFunctionsTest.yyy)
        assertEquals("companion val", xxx, KtFunctionsTest.zzz)
        assertTrue("in", "ba" in xxx)
        assertTrue("!in", "ab" !in xxx)
    }

    @Test
    fun ifEmptyTest() {
        var in1 = ""
        assertEquals("replacement expected","abc", in1.ifEmpty { "abc" })
        in1 = "cba"
        assertEquals("original value expected","cba", in1.ifEmpty { "abc" })
    }

    @Test
    fun xxx() {
        val (a, b) = "a" to "b"
        if (listOf(a, b, "c").all { value -> value in listOf(a, b) })
            println("ok")
         else
            println("not ok")
    }

    companion object {
        val zzz = listOf("aa","ba","ca").joinToString(",","","",100,"" )
        val yyy = zzz
        const val xxx = "aa,ba,ca"
    }
}
