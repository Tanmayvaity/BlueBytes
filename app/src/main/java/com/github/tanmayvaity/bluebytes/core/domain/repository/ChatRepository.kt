package com.github.tanmayvaity.bluebytes.core.domain.repository

import com.github.tanmayvaity.bluebytes.core.domain.model.BluetoothMessage
import com.github.tanmayvaity.bluebytes.core.domain.model.Conversation
import kotlinx.coroutines.flow.Flow

interface ChatRepository {
    suspend fun saveMessage(address: String, name: String?, message: BluetoothMessage)
    fun getMessages(address: String): Flow<List<BluetoothMessage>>
    fun getConversations(): Flow<List<Conversation>>
}
