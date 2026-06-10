package com.github.tanmayvaity.bluebytes.feature.feature_chat.presentation

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.github.tanmayvaity.bluebytes.core.domain.model.Conversation
import com.github.tanmayvaity.bluebytes.ui.theme.BlueBytesTheme

@Composable
fun ChatScreen(
    modifier: Modifier = Modifier,
    onOpenConversation: (address: String, name: String?) -> Unit = { _, _ -> },
    viewModel: ChatViewModel = hiltViewModel()
) {
    val conversations by viewModel.conversations.collectAsStateWithLifecycle()
    ChatScreenContent(
        modifier = modifier,
        conversations = conversations,
        onOpenConversation = onOpenConversation
    )
}

@Composable
private fun ChatScreenContent(
    conversations: List<Conversation>,
    modifier: Modifier = Modifier,
    onOpenConversation: (address: String, name: String?) -> Unit = { _, _ -> }
) {
    if (conversations.isEmpty()) {
        Box(
            modifier = modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "No chats yet",
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        return
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(conversations, key = { it.address }) { conversation ->
            ConversationRow(
                conversation = conversation,
                onClick = { onOpenConversation(conversation.address, conversation.name) }
            )
        }
    }
}

@Composable
private fun ConversationRow(
    conversation: Conversation,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.1f)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = conversation.name ?: conversation.address,
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = conversation.lastMessage,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@PreviewLightDark
@Composable
private fun ChatScreenPreview() {
    BlueBytesTheme {
        ChatScreenContent(
            conversations = listOf(
                Conversation("AA:BB:CC:DD:EE:FF", "Pixel 8", "See you then!", 0L),
                Conversation("11:22:33:44:55:66", null, "ok", 0L)
            )
        )
    }
}
