package org.breizhcamp.kalon.config.log


/**
 * Use this annotation to add value to the MDC logger.
 *
 * The annotation can be used:
 *  - on a function to store the current value of the MDC [keys] and restore it at the end of the function.
 *    You can use [org.slf4j.MDC.put] to set one of a [keys] within the function, the value will be restored at the end of the function.
 *  - on a parameter to store the value of the parameter in the MDC [keys] for the duration of the function.
 */
@Target(AnnotationTarget.FUNCTION, AnnotationTarget.VALUE_PARAMETER)
@Retention(AnnotationRetention.RUNTIME)
annotation class Log(vararg val keys: String)
