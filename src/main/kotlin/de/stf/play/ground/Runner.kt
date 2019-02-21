package de.stf.play.ground

import de.stf.play.ground.data.Type1
import de.stf.play.ground.data.Type2
import de.stf.play.ground.test.UpdateHelper

fun main(args: Array<String>) {
    println("\n\n1 to 1\n----------------------------------------------------")
    UpdateHelper.update(Type1.getOne(), Type1.getOne())
    println("\n\n1 to 2\n----------------------------------------------------")
    UpdateHelper.update(Type1.getOne(), Type2.getOne())
    println("\n\n2 to 1\n----------------------------------------------------")
    UpdateHelper.update(Type2.getOne(), Type1.getOne())
    println("\n\n2 to 2\n----------------------------------------------------")
    UpdateHelper.update(Type2.getOne(), Type2.getOne())
}
