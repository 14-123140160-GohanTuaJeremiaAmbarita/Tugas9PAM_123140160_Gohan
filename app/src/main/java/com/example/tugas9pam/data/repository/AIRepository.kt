package com.example.tugas9pam.data.repository

import android.graphics.Bitmap
import com.example.tugas9pam.data.remote.GeminiService
import com.google.ai.client.generativeai.type.Content
import kotlinx.coroutines.flow.Flow

class AIRepository(private val geminiService: GeminiService) {
    fun chatStream(prompt: String, history: List<Content>): Flow<String> {
        return geminiService.generateContentStream(prompt, history)
    }

    suspend fun analyzeImage(prompt: String, image: Bitmap): String {
        return geminiService.generateContentWithImage(prompt, image)
    }
}
