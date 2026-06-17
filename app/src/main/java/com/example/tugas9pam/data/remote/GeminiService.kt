package com.example.tugas9pam.data.remote

import android.graphics.Bitmap
import com.example.tugas9pam.utils.PromptManager
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content
import com.google.ai.client.generativeai.type.generationConfig
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class GeminiService(apiKey: String) {
    private val config = generationConfig {
        temperature = 0.7f
        topK = 40
        topP = 0.95f
        maxOutputTokens = 1024
    }

    private val generativeModel = GenerativeModel(
        modelName = "gemini-3.5-flash",
        apiKey = apiKey,
        generationConfig = config,
        systemInstruction = content { text(PromptManager.SYSTEM_PROMPT) }
    )

    private val visionModel = GenerativeModel(
        modelName = "gemini-1.5-flash",
        apiKey = apiKey,
        generationConfig = config,
        systemInstruction = content { text(PromptManager.SYSTEM_PROMPT) }
    )

    fun generateContentStream(prompt: String, history: List<com.google.ai.client.generativeai.type.Content>): Flow<String> {
        val chat = generativeModel.startChat(history)
        return chat.sendMessageStream(prompt).map { it.text ?: "" }
    }

    suspend fun generateContentWithImage(prompt: String, image: Bitmap): String {
        val response = visionModel.generateContent(
            content {
                image(image)
                text(prompt)
            }
        )
        return response.text ?: "Tidak ada respon."
    }
}
