package com.example.tugas9pam.presentation.chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import android.graphics.Bitmap
import com.example.tugas9pam.data.repository.AIRepository
import com.example.tugas9pam.domain.model.ChatMessage
import com.example.tugas9pam.domain.model.Participant
import com.example.tugas9pam.utils.AIError
import com.google.ai.client.generativeai.type.content
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ChatViewModel(private val repository: AIRepository) : ViewModel() {

    private val _uiState = MutableStateFlow<List<ChatMessage>>(emptyList())
    val uiState: StateFlow<List<ChatMessage>> = _uiState.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private val history = mutableListOf<com.google.ai.client.generativeai.type.Content>()

    fun sendMessage(userPrompt: String) {
        if (userPrompt.isBlank()) return

        val userMessage = ChatMessage(text = userPrompt, participant = Participant.USER)
        _uiState.update { it + userMessage }
        
        val aiMessagePlaceholder = ChatMessage(participant = Participant.AI, isPending = true)
        _uiState.update { it + aiMessagePlaceholder }

        viewModelScope.launch {
            var fullResponse = ""
            repository.chatStream(userPrompt, history)
                .onStart { 
                    _isLoading.value = true 
                    _error.value = null
                }
                .onCompletion {
                    _isLoading.value = false
                    if (fullResponse.isNotEmpty()) {
                        history.add(content("user") { text(userPrompt) })
                        history.add(content("model") { text(fullResponse) })
                    }
                }
                .catch { e ->
                    _isLoading.value = false
                    val errorMessage = "Error: ${e.localizedMessage}"
                    _error.value = errorMessage
                    _uiState.update { messages ->
                        messages.dropLast(1) + ChatMessage(text = errorMessage, participant = Participant.ERROR)
                    }
                }
                .collect { chunk ->
                    fullResponse += chunk
                    _uiState.update { messages ->
                        val lastMessage = messages.last()
                        if (lastMessage.participant == Participant.AI) {
                            messages.dropLast(1) + lastMessage.copy(text = fullResponse, isPending = false)
                        } else {
                            messages
                        }
                    }
                }
        }
    }
    
    fun clearError() {
        _error.value = null
    }

    suspend fun analyzeImage(prompt: String, image: Bitmap): String {
        return try {
            repository.analyzeImage(prompt, image)
        } catch (e: Exception) {
            "Error: ${e.localizedMessage}"
        }
    }
}
