package org.breizhcamp.kalon.application.requests

data class ContactCreationReq(
    val platform: String,
    val link: String,
    val public: Boolean
)
