package de.stf.play.ground.util

import org.slf4j.LoggerFactory
import kotlin.reflect.KMutableProperty
import kotlin.reflect.KProperty1
import kotlin.reflect.full.memberProperties

private const val FORCED_UPDATE_METHOD_NAME = "forcedUpdate"
const val WILDCARD_METHOD = "*"

class UpdateHelper {

    companion object {
        private val logger = LoggerFactory.getLogger(UpdateHelper::class.java)

        fun <T: Any>update(updateObject: Any, currentObject: T): T {
            if (logger.isTraceEnabled) logger.trace("UPDATE  $updateObject")
            if (logger.isTraceEnabled) logger.trace("CURRENT $currentObject")

            val fromDeclaredFields: Collection<KProperty1<Any, *>> = updateObject.javaClass.kotlin.memberProperties
            val fromForcedFields = try {
                fromDeclaredFields.first { it.name == FORCED_UPDATE_METHOD_NAME }.get(updateObject)
                } catch (e: NoSuchElementException) { null }
            val toDeclaredFields: Collection<KProperty1<T, *>> = currentObject.javaClass.kotlin.memberProperties
            val toDeclaredFieldNames = currentObject.javaClass.declaredFields.map{ it.name }

            fun processMutable(
                toField: KProperty1<T, *>,
                fromField: KProperty1<Any, *>,
                fromForcedFields: Any?
            ) {
                if (toField is KMutableProperty<*>) {
                    val value = fromField.getter.call(updateObject) //retrieve value from update object
                    if (value != null) { // value is not null -> copy
                        toField.setter.call(currentObject, value)
                        if (logger.isDebugEnabled) logger.debug("copied ${fromField.name} with '$value'")
                    } else if (fromForcedFields != null && // value is null but we have forced fields
                        fromForcedFields is ArrayList<*> && // of the right type
                        (fromForcedFields.contains(fromField.name) || // and field name is listed
                            fromForcedFields.contains(WILDCARD_METHOD)) // or field name is the wildcard matching all fields
                    ) {
                        toField.setter.call(currentObject, value) // --> copy a null value
                        if (logger.isDebugEnabled)
                            logger.debug("copied ${fromField.name} FORCED with null")
                    } else if (logger.isWarnEnabled)
                        logger.warn("skipped ${fromField.name} value is null and not forced")
                } else if (logger.isWarnEnabled)
                    logger.warn("skipped ${fromField.name} not mutable")
            }

            fromDeclaredFields.forEach {
                // process for all declared properties
                if (it.name != FORCED_UPDATE_METHOD_NAME) {
                    when {
                        toDeclaredFieldNames.contains(it.name) -> { // is field declared in target class?
                            val toField = toDeclaredFields.first { tdf -> tdf.name == it.name }
                            // did the types match?
                            if (it.returnType == toField.returnType)
                                processMutable(toField, it, fromForcedFields)
                            else if (logger.isWarnEnabled)
                                logger.warn("skipped ${it.name} different types \n  from $it.returnType\n  to   $toField.returnType")
                        }
                        else -> if (logger.isWarnEnabled)
                            logger.warn("skipped ${it.name} Not in target type")
                    }
                } else if (logger.isWarnEnabled)
                    logger.warn("skipped ${it.name} Reserved field")
            }
            if (logger.isTraceEnabled)
                logger.trace("UPDATED $currentObject")
            return currentObject
        }
    }
}
