package com.example.tugas9pam

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.tugas9pam.data.remote.GeminiService
import com.example.tugas9pam.data.repository.AIRepository
import com.example.tugas9pam.presentation.chat.ChatScreen
import com.example.tugas9pam.presentation.chat.ChatViewModel
import com.example.tugas9pam.presentation.image_analysis.ImageAnalysisScreen
import com.example.tugas9pam.ui.theme.Tugas9pamTheme
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        // TODO: Ganti dengan API Key Gemini Anda
        val apiKey = "AQ.Ab8RN6J-rDy-LU-V8QCvOPEDNajyyGfbPGBu3cs2plMJzTfiKA"
        
        val geminiService = GeminiService(apiKey)
        val repository = AIRepository(geminiService)
        
        setContent {
            Tugas9pamTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val viewModel: ChatViewModel = viewModel(
                        factory = object : ViewModelProvider.Factory {
                            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                                return ChatViewModel(repository) as T
                            }
                        }
                    )
                    AppNavigation(viewModel)
                }
            }
        }
    }
}

@Composable
fun AppNavigation(viewModel: ChatViewModel) {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "chat") {
        composable("chat") {
            ChatScreen(
                viewModel = viewModel,
                onNavigateToImage = { navController.navigate("image_analysis") }
            )
        }
        composable("image_analysis") {
            ImageAnalysisScreen(
                viewModel = viewModel,
                onBack = { navController.popBackStack() }
            )
        }
    }
}
