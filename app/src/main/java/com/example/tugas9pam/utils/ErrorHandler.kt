package com.example.tugas9pam.utils

sealed class AIError {
    object NoInternet : AIError()
    object InvalidApiKey : AIError()
    object RateLimited : AIError()
    object Timeout : AIError()
    data class Unknown(val message: String) : AIError()
}

fun AIError.toMessage(): String {
    return when (this) {
        AIError.NoInternet -> "Tidak ada koneksi internet."
        AIError.InvalidApiKey -> "API Key tidak valid."
        AIError.RateLimited -> "Batas permintaan tercapai. Silakan coba lagi nanti."
        AIError.Timeout -> "Permintaan waktu habis."
        is AIError.Unknown -> "Terjadi kesalahan: ${this.message}"
    }
}
