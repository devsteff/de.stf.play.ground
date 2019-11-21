package de.stf.play.ground

import de.stf.play.ground.data.Type1
import de.stf.play.ground.data.Type2
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertArrayEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.Test

class UpdateHelperTest {
    @Test
    fun update1to1Test() {
        val source = Type1.getOne()
        val target = Type1.getOne()
        assertEquals(source.field1, target.field1, "field1 not equals")
        assertEquals(source.field2, target.field2, "field2 not equals")
        assertEquals(source.field3, target.field3, "field3 not equals")
        assertArrayEquals(source.field4, target.field4, "field4 not equals")
        assertEquals(source.field5, target.field5, "field5 not equals")
        assertEquals(source.field6, target.field6, "field6 not equals")
        assertEquals(source.field7, target.field7, "field7 not equals")
        assertEquals(source.field8.toString(), target.field8.toString(), "field8 not equals")
        assertEquals(source.field9, target.field9, "field9 not equals")

        val updated: Type1 = UpdateHelper.update(source, target.copy())
        assertEquals(source.field1, updated.field1, "field1 not equals")
        assertNull(updated.field2, "field2 not null") // forced
        assertEquals(source.field3, updated.field3, "field3 not equals")
        assertArrayEquals(source.field4, updated.field4, "field4 not equals")
        assertEquals(source.field5, updated.field5, "field5 not equals") // different types
        assertEquals(source.field6, updated.field6, "field6 not equals") // different types
        assertEquals(source.field7, updated.field7, "field7 not equals") // unmutable
        assertEquals(source.field8.toString().reversed(),
            updated.field8.toString().reversed(), "field8 not equals")
        assertEquals(source.field9, updated.field9, "field9 not equals") // unmutable
    }

    @Test
    fun update1to2Test() {
        val source = Type1.getOne()
        val target = Type2.getOne()
        assertNotEquals(source.field1, target.field1, "field1 not equals")
        assertNotEquals(source.field2, target.field2, "field2 not equals")
        assertNotEquals(source.field3, target.field3, "field3 not equals")
        assertNotEquals(source.field4, target.field4, "field4 not equals")
        assertNotEquals(source.field5, target.field5, "field5 not equals")
        assertNotEquals(source.field6, target.field6, "field6 not equals")
        assertNotEquals(source.field7, target.field7, "field7 not equals")
        assertNotEquals(source.field8, target.field8, "field8 not equals")
        assertNotEquals(source.field9, target.field9, "field9 not equals")

        val updated: Type2 =
            UpdateHelper.update(source, target.copy())
        assertEquals(source.field1, updated.field1, "field1 not equals")
        assertNull(updated.field2, "field2 not null") // forced
        assertEquals(source.field3, updated.field3, "field3 not equals")
        assertEquals(source.field4, updated.field4, "field4 not equals")
        assertEquals(target.field5, updated.field5, "field5 not equals") // different types
        assertEquals(target.field6, updated.field6, "field6 not equals") // different types
        assertEquals(source.field7, updated.field7, "field7 not equals")
        assertEquals(source.field8, updated.field8, "field8 not equals")
        assertNotEquals(source.field9, updated.field9,"field9 not equals") // unmutable
    }

    @Test
    fun update1toAllNull() {
        val update = Type1.getOne()
        update.field1 = null
        update.field2 = null
        update.field3 = null
        update.field4 = null
        update.field5 = null
        update.field6 = null
        // update.field7 = null
        update.field8 = null
        update.field9 = null
        update.forcedUpdate = arrayListOf("*")

        val current = Type2.getOne()
        assertNull(update.field1, "field1 not null")
        assertNull(update.field2, "field1 not null")
        assertNull(update.field3, "field3 not null")
        assertNull(update.field4, "field4 not null")
        assertNull(update.field5, "field5 not null")
        assertNull(update.field6, "field6 not null")
        assertNotNull(update.field7, "field7 null") // not mutable
        assertNull(update.field8, "field8 not null")
        assertNull(update.field9, "field9 not null")

        val updated: Type2 = UpdateHelper.update(update, current)
        assertNull(updated.field1, "field1 not null")
        assertNull(updated.field2, "field2 not null")
        assertNull(updated.field3, "field3 not null")
        assertNull(updated.field4, "field4 not null")
        assertNotNull(updated.field5, "field5 null") // skipped different type
        assertNotNull(updated.field6, "field6 null") // skipped different type
        assertEquals(updated.field7, 47L, "field7 not equals") // skipped not mutable at source
        assertNull(updated.field8, "field8 not null")
        assertEquals(updated.field9,
            "Unable to change me",
            "field9 not equals") // skipped not mutable at target
    }
}
