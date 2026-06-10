package com.github.tanmayvaity.bluebytes.core.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.github.tanmayvaity.bluebytes.core.data.local.entity.ConversationEntity
import com.github.tanmayvaity.bluebytes.core.data.local.entity.MessageEntity

@Database(
    entities = [ConversationEntity::class, MessageEntity::class],
    version = 1,
    exportSchema = false
)
abstract class BlueBytesDatabase : RoomDatabase() {
    abstract fun chatDao(): ChatDao
}
