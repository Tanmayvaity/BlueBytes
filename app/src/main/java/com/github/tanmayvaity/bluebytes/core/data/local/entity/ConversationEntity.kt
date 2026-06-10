package com.github.tanmayvaity.bluebytes.core.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "conversations")
data class ConversationEntity(
    @PrimaryKey val address: String,
    val name: String?,
    val lastMessage: String,
    val lastTimestamp: Long
)
