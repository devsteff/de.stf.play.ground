package de.stf.play.ground

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class KtStreamTest {

    @Test
    fun mapAndFilterTest() {
        val collect = STRING_LIST // Stream of String
            .filter { it.length == 1 }
            .map { it.toUpperCase() }
            .sorted()
        assertEquals(listOf("A", "B", "C", "D", "G"), collect)
    }

    @Test
    fun flatMapAndFilterTest() {
        var i = 1
        val x = HashMap<Int, String>()
        val collect = CUSTOMER_MAP
            .filter { (customer, _) -> customer.name.contains("a") }
            .flatMap { (customer, _) ->
                x.put(i*i++, customer.name)
                listOf(customer.name)
            }
            .sortedBy { it.reversed() }
        assertEquals(listOf("Craig", "Jack", "Mary"), collect) // --> gairC, kcaJ, yraM
        assertEquals("Mary", x[4])
    }

    @Test
    fun toSortedMap() {
        val sortedListById = CUSTOMER_MAP
            // .toSortedMap(object: Comparator<Customer>{
            //    override fun compare(c1: Customer, c2: Customer): Int = c1.id.compareTo(c2.id)
            // })
            // .toSortedMap(Comparator { c1, c2 -> c2.id.compareTo(c1.id) })
            .toSortedMap(compareBy { it.id })
            .map { (customer, _) -> customer.name }
        assertEquals(listOf("Amos", "Jack", "Craig", "Mary", "Peter"), sortedListById)
    }

    @Test
    fun flatMapTest() {
        val flattenMapList = CUSTOMER_MAP
            .flatMap { (customer, address) -> listOf(customer.name, address.street) }
            .filter { item -> item.toUpperCase().contains("A") }
            .sortedDescending()
        assertEquals(
            listOf("W NORMA ST", "S NUGENT AVE", "NANTERRE CT", "Mary", "Jack", "E NAVAHO TRL",
                "Craig", "Amos", "AVE N"),
            flattenMapList)
    }

    companion object {
        val STRING_LIST = listOf("xyz", "a", "b", "ef", "g", "hijk", "d", "hello", "C")
        val CUSTOMER_MAP = mapOf(
            Pair(
                Customer(3, "Jack", 25),
                Address("NANTERRE CT", "77471")
            ),
            Pair(
                Customer(8, "Mary", 37),
                Address("W NORMA ST", "77009")
            ),
            Pair(
                Customer(11, "Peter", 18),
                Address("S NUGENT AVE", "77571")
            ),
            Pair(
                Customer(1, "Amos", 23),
                Address("E NAVAHO TRL", "77449")
            ),
            Pair(
                Customer(4, "Craig", 45),
                Address("AVE N", "77587")
            )
        )
    }
}

data class Address(
    val street: String,
    val postcode: String) {
}

data class Customer(
    val id: Int,
    val name: String,
    val age: Int
)
