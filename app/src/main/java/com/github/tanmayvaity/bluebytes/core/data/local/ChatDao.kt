package com.github.tanmayvaity.bluebytes.core.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Upsert
import com.github.tanmayvaity.bluebytes.core.data.local.entity.ConversationEntity
import com.github.tanmayvaity.bluebytes.core.data.local.entity.MessageEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ChatDao {

    @Upsert
    suspend fun upsertConversation(conversation: ConversationEntity)

    @Insert
    suspend fun insertMessage(message: MessageEntity)

    @Query("SELECT * FROM conversations ORDER BY lastTimestamp DESC")
    fun getConversations(): Flow<List<ConversationEntity>>

    @Query("SELECT * FROM messages WHERE conversationAddress = :address ORDER BY timestamp ASC")
    fun getMessages(address: String): Flow<List<MessageEntity>>
}
