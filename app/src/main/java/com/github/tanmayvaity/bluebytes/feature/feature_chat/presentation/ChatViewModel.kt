package com.github.tanmayvaity.bluebytes.feature.feature_chat.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.tanmayvaity.bluebytes.core.domain.model.Conversation
import com.github.tanmayvaity.bluebytes.core.domain.repository.ChatRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(
    chatRepository: ChatRepository
) : ViewModel() {

    val conversations: StateFlow<List<Conversation>> = chatRepository
        .getConversations()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
}
