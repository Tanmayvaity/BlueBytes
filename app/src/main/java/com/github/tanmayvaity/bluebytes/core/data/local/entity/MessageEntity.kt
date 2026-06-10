package com.github.tanmayvaity.bluebytes.core.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "messages",
    indices = [Index(value = ["conversationAddress"])]
)
data class MessageEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val conversationAddress: String,
    val content: String,
    val isFromLocalUser: Boolean,
    val timestamp: Long
)
