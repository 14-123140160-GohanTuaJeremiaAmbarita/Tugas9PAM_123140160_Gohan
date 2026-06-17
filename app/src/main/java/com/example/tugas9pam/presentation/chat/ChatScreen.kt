package com.example.tugas9pam.presentation.chat

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.tugas9pam.domain.model.ChatMessage
import com.example.tugas9pam.domain.model.Participant
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(viewModel: ChatViewModel, onNavigateToImage: () -> Unit) {
    val messages by viewModel.uiState.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()
    var textInput by remember { mutableStateOf("") }

    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("AI Study Assistant", style = MaterialTheme.typography.titleLarge)
                        Text(
                            "Gohan Tua Jeremia Ambarita - 123140160",
                            style = MaterialTheme.typography.labelSmall
                        )
                    }
                },
                actions = {
                    Button(onClick = onNavigateToImage) {
                        Text("Analisis Gambar")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(messages) { message ->
                    ChatBubble(message)
                }
                if (isLoading) {
                    item {
                        TypingIndicator()
                    }
                }
            }

            MessageInput(
                value = textInput,
                onValueChange = { textInput = it },
                onSend = {
                    viewModel.sendMessage(textInput)
                    textInput = ""
                },
                enabled = !isLoading
            )
        }
    }
}

@Composable
fun ChatBubble(message: ChatMessage) {
    val isUser = message.participant == Participant.USER
    val isError = message.participant == Participant.ERROR
    
    val alignment = if (isUser) Alignment.End else Alignment.Start
    val bubbleColor = when {
        isUser -> MaterialTheme.colorScheme.primaryContainer
        isError -> MaterialTheme.colorScheme.errorContainer
        else -> MaterialTheme.colorScheme.secondaryContainer
    }
    val textColor = when {
        isUser -> MaterialTheme.colorScheme.onPrimaryContainer
        isError -> MaterialTheme.colorScheme.onErrorContainer
        else -> MaterialTheme.colorScheme.onSecondaryContainer
    }

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = alignment
    ) {
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(
                    topStart = 16.dp,
                    topEnd = 16.dp,
                    bottomStart = if (isUser) 16.dp else 0.dp,
                    bottomEnd = if (isUser) 0.dp else 16.dp
                ))
                .background(bubbleColor)
                .padding(12.dp)
        ) {
            Text(
                text = message.text,
                color = textColor,
                fontSize = 14.sp
            )
        }
        Text(
            text = if (isUser) "Anda" else "AI Assistant",
            style = MaterialTheme.typography.labelSmall,
            modifier = Modifier.padding(top = 4.dp, start = 4.dp, end = 4.dp)
        )
    }
}

@Composable
fun MessageInput(
    value: String,
    onValueChange: (String) -> Unit,
    onSend: () -> Unit,
    enabled: Boolean
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        TextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.weight(1f),
            placeholder = { Text("Tanya asisten...") },
            enabled = enabled,
            shape = RoundedCornerShape(24.dp),
            colors = TextFieldDefaults.colors(
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent
            )
        )
        Spacer(modifier = Modifier.width(8.dp))
        IconButton(
            onClick = onSend,
            enabled = enabled && value.isNotBlank(),
            modifier = Modifier
                .clip(RoundedCornerShape(24.dp))
                .background(MaterialTheme.colorScheme.primary)
        ) {
            Icon(
                Icons.AutoMirrored.Filled.Send,
                contentDescription = "Kirim",
                tint = MaterialTheme.colorScheme.onPrimary
            )
        }
    }
}

@Composable
fun TypingIndicator() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        CircularProgressIndicator(
            modifier = Modifier.size(16.dp),
            strokeWidth = 2.dp
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            "AI sedang berpikir...",
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.outline
        )
    }
}
