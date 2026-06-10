package com.github.tanmayvaity.bluebytes.core.domain.model

data class Conversation(
    val address: String,
    val name: String?,
    val lastMessage: String,
    val lastTimestamp: Long
)
