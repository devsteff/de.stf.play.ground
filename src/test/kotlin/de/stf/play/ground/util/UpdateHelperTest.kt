package de.stf.play.ground.util

import de.stf.play.ground.data.Type1
import de.stf.play.ground.data.Type2
import jdk.nashorn.internal.objects.NativeArray.forEach
import org.junit.Assert
import org.junit.Test
import java.math.BigDecimal

class UpdateHelperTest {
    @Test
    fun update1to1Test() {
        val source = Type1.getOne()
        val target = Type1.getOne()
        Assert.assertEquals("field1 not equals", source.field1, target.field1)
        Assert.assertEquals("field2 not equals", source.field2, target.field2)
        Assert.assertEquals("field3 not equals", source.field3, target.field3)
        Assert.assertArrayEquals("field4 not equals", source.field4, target.field4)
        Assert.assertEquals("field5 not equals", source.field5, target.field5)
        Assert.assertEquals("field6 not equals", source.field6, target.field6)
        Assert.assertEquals("field7 not equals", source.field7, target.field7)
        Assert.assertEquals("field8 not equals", source.field8.toString(), target.field8.toString())
        Assert.assertEquals("field9 not equals", source.field9, target.field9)

        val updated: Type1 =
            UpdateHelper.update(source, target.copy())
        Assert.assertEquals("field1 not equals", source.field1, updated.field1)
        Assert.assertNull("field2 not null", updated.field2) // forced
        Assert.assertEquals("field3 not equals", source.field3, updated.field3)
        Assert.assertArrayEquals("field4 not equals", source.field4, updated.field4)
        Assert.assertEquals("field5 not equals", source.field5, updated.field5) // different types
        Assert.assertEquals("field6 not equals", source.field6, updated.field6) // different types
        Assert.assertEquals("field7 not equals", source.field7, updated.field7) // unmutable
        Assert.assertEquals("field8 not equals", source.field8.toString().reversed(),
            updated.field8.toString().reversed())
        Assert.assertEquals("field9 not equals", source.field9, updated.field9) // unmutable
    }

    @Test
    fun update1to2Test() {
        val source = Type1.getOne()
        val target = Type2.getOne()
        Assert.assertNotEquals("field1 not equals", source.field1, target.field1)
        Assert.assertNotEquals("field2 not equals", source.field2, target.field2)
        Assert.assertNotEquals("field3 not equals", source.field3, target.field3)
        Assert.assertNotEquals("field4 not equals", source.field4, target.field4)
        Assert.assertNotEquals("field5 not equals", source.field5, target.field5)
        Assert.assertNotEquals("field6 not equals", source.field6, target.field6)
        Assert.assertNotEquals("field7 not equals", source.field7, target.field7)
        Assert.assertNotEquals("field8 not equals", source.field8, target.field8)
        Assert.assertNotEquals("field9 not equals", source.field9, target.field9)

        val updated: Type2 =
            UpdateHelper.update(source, target.copy())
        Assert.assertEquals("field1 not equals", source.field1, updated.field1)
        Assert.assertNull("field2 not null", updated.field2) // forced
        Assert.assertEquals("field3 not equals", source.field3, updated.field3)
        Assert.assertEquals("field4 not equals", source.field4, updated.field4)
        Assert.assertEquals("field5 not equals", target.field5, updated.field5) // different types
        Assert.assertEquals("field6 not equals", target.field6, updated.field6) // different types
        Assert.assertEquals("field7 not equals", source.field7, updated.field7)
        Assert.assertEquals("field8 not equals", source.field8, updated.field8)
        Assert.assertNotEquals("field9 not equals", source.field9, updated.field9) // unmutable
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
        Assert.assertNull("field1 not null", update.field1)
        Assert.assertNull("field2 not null", update.field2)
        Assert.assertNull("field3 not null", update.field3)
        Assert.assertNull("field4 not null", update.field4)
        Assert.assertNull("field5 not null", update.field5)
        Assert.assertNull("field6 not null", update.field6)
        Assert.assertNotNull("field7 null", update.field7) // not mutable
        Assert.assertNull("field8 not null", update.field8)
        Assert.assertNull("field9 not null", update.field9)

        val updated: Type2 =
            UpdateHelper.update(update, current)
        Assert.assertNull("field1 not null", updated.field1)
        Assert.assertNull("field2 not null", updated.field2)
        Assert.assertNull("field3 not null", updated.field3)
        Assert.assertNull("field4 not null", updated.field4)
        Assert.assertNotNull("field5 null", updated.field5) // skipped different type
        Assert.assertNotNull("field6 null", updated.field6) // skipped different type
        Assert.assertEquals("field7 not equals", updated.field7, 47L) // skipped not mutable at source
        Assert.assertNull("field8 not null", updated.field8)
        Assert.assertEquals("field9 not equals", updated.field9,
            "Unable to change me") // skipped not mutable at target
    }
}
