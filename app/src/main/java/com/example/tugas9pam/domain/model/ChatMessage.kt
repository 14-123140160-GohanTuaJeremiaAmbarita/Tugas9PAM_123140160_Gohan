package com.example.tugas9pam.domain.model

import android.graphics.Bitmap

enum class Participant {
    USER, AI, ERROR
}

data class ChatMessage(
    val text: String = "",
    val participant: Participant = Participant.USER,
    val isPending: Boolean = false,
    val image: Bitmap? = null
)
