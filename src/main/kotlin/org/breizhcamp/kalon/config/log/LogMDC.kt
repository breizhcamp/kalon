package org.breizhcamp.kalon.config.log

interface LogMDC {
    fun mdc(): Map<String, String>
}
