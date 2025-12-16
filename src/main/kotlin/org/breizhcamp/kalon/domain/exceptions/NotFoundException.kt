package org.breizhcamp.kalon.domain.exceptions

class NotFoundException(message: String) : Exception(message)

inline fun <reified T : Any> NotFoundException(id: Any) =
    NotFoundException("Entity ${T::class.simpleName} with id [$id] not found")
