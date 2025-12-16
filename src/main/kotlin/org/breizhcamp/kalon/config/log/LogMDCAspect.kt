package org.breizhcamp.kalon.config.log

import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.reflect.MethodSignature
import org.slf4j.MDC
import org.springframework.stereotype.Component

@Aspect
@Component
class LogMDCAspect {

    @Around("execution(* *(.., @Log (*), ..)) || @annotation(Log)")
    fun logMDC(pjp: ProceedingJoinPoint): Any? {
        val oldMdc = mutableMapOf<String, String?>()

        //method
        val log = (pjp.signature as MethodSignature).method.getAnnotation(Log::class.java)
        log?.keys?.forEach {
            oldMdc[it] = MDC.get(it)
        }

        //parameters
        val paramAnnotations = (pjp.signature as MethodSignature).method.parameterAnnotations
        paramAnnotations.forEachIndexed { index, annotations ->
            val value = pjp.args[index]
            val logAnnotations = annotations.filterIsInstance<Log>()
            if (logAnnotations.isEmpty()) return@forEachIndexed

            if (value is LogMDC) {
                value.mdc().forEach { (key, value) -> addToMdcAndSaveOld(key, value, oldMdc) }

            } else {
                logAnnotations
                    .filter { it.keys.isNotEmpty() }
                    .flatMap { it.keys.asIterable() }
                    .forEach { addToMdcAndSaveOld(it, value, oldMdc) }
            }

        }

        return try {
            pjp.proceed()
        } finally {
            oldMdc.forEach { (key, value) -> value?.let { MDC.put(key, it) } ?: MDC.remove(key) }
        }
    }

    private fun addToMdcAndSaveOld(string: String, value: Any?, oldMdc: MutableMap<String, String?>) {
        oldMdc[string] = MDC.get(string)
        value?.let { v -> MDC.put(string, v.toString()) } ?: MDC.remove(string)
    }

}
