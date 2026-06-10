package com.github.tanmayvaity.bluebytes.core.data.repository

import com.github.tanmayvaity.bluebytes.core.data.local.ChatDao
import com.github.tanmayvaity.bluebytes.core.data.local.entity.ConversationEntity
import com.github.tanmayvaity.bluebytes.core.data.local.entity.MessageEntity
import com.github.tanmayvaity.bluebytes.core.domain.model.BluetoothMessage
import com.github.tanmayvaity.bluebytes.core.domain.model.Conversation
import com.github.tanmayvaity.bluebytes.core.domain.repository.ChatRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class ChatRepositoryImpl @Inject constructor(
    private val chatDao: ChatDao
) : ChatRepository {

    override suspend fun saveMessage(address: String, name: String?, message: BluetoothMessage) {
        chatDao.insertMessage(
            MessageEntity(
                conversationAddress = address,
                content = message.message,
                isFromLocalUser = message.isFromLocalUser,
                timestamp = message.timestamp
            )
        )
        chatDao.upsertConversation(
            ConversationEntity(
                address = address,
                name = name,
                lastMessage = message.message,
                lastTimestamp = message.timestamp
            )
        )
    }

    override fun getMessages(address: String): Flow<List<BluetoothMessage>> {
        return chatDao.getMessages(address).map { messages ->
            messages.map { it.toBluetoothMessage() }
        }
    }

    override fun getConversations(): Flow<List<Conversation>> {
        return chatDao.getConversations().map { conversations ->
            conversations.map { it.toConversation() }
        }
    }

    private fun MessageEntity.toBluetoothMessage() = BluetoothMessage(
        message = content,
        isFromLocalUser = isFromLocalUser,
        timestamp = timestamp
    )

    private fun ConversationEntity.toConversation() = Conversation(
        address = address,
        name = name,
        lastMessage = lastMessage,
        lastTimestamp = lastTimestamp
    )
}
