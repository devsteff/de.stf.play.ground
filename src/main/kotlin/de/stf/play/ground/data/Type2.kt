package de.stf.play.ground.data

import java.util.Date

data class Type2(
    var field1: String?,
    var field2: String?,
    var field3: Type2?,
    var field4: IntArray?,
    var field5: Int?,
    val field6: Long?
) {
    var field7: Long? = 0
    var field8: Date? = Date()
    val field9: String? = "Unable to change me"

    companion object {
        fun getOne(): Type2 {
            val result =
                Type2("Type2 AAA", "Type2 BBB", null, null, 123, 456)
            result.field7 = 789
            result.field3 = result.copy()
            result.field4 = intArrayOf(100,110,111,1,11,101)
            return result
        }
    }
}
