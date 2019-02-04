package de.stf.play.ground.data

import java.time.Instant
import java.util.Date

data class Type1(
    var field1: String?, // copied
    var field3: Type2?, // copied
    var field4: IntArray? // copied
) {
    var forcedUpdate: List<String>? = null
    var field2: String? = null // copied
    var field5: String? = "Field5" // not copied -> same name but different type
    var field6: Long? = 0 // not copied -> not nullable
    val field7: Long? = companionField8 // copied
    var field8: Date? = Date.from(Instant.now().minusSeconds(600000))
    var field9: String? = "You can change me"

    companion object {
        const val companionField8: Long = 47 // not copied --> static
        fun getOne(): Type1 {
            val updateObject = Type1(
                "Type1 aaa",
                Type2("from Type1 aaa", "from Type1 bbb", null, null, null, null),
                intArrayOf(1, 2, 3)
            )
            updateObject.field2 = null // will be copied because of the _forcedUpdate entry
            updateObject.forcedUpdate = arrayListOf("field2", "field3")
            return updateObject
        }
    }
}
