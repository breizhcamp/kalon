package org.breizhcamp.kalon

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class KalonApplication

fun main(args: Array<String>) {
	runApplication<KalonApplication>(*args)
}
