package com.example.tugas9pam.presentation.image_analysis

import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.tugas9pam.presentation.chat.ChatViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ImageAnalysisScreen(viewModel: ChatViewModel, onBack: () -> Unit) {
    val context = LocalContext.current
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    var bitmap by remember { mutableStateOf<Bitmap?>(null) }
    var analysisResult by remember { mutableStateOf("") }
    var isAnalyzing by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        selectedImageUri = uri
        uri?.let {
            bitmap = if (Build.VERSION.SDK_INT < 28) {
                MediaStore.Images.Media.getBitmap(context.contentResolver, it)
            } else {
                val source = ImageDecoder.createSource(context.contentResolver, it)
                ImageDecoder.decodeBitmap(source)
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Analisis Gambar") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Kembali")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (bitmap != null) {
                Image(
                    bitmap = bitmap!!.asImageBitmap(),
                    contentDescription = "Selected Image",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp)
                )
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Belum ada gambar terpilih")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(onClick = { launcher.launch("image/*") }) {
                Text("Pilih Gambar dari Galeri")
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (bitmap != null) {
                Button(
                    onClick = {
                        coroutineScope.launch {
                            isAnalyzing = true
                            analysisResult = viewModel.analyzeImage("Jelaskan gambar ini secara detail untuk asisten akademik.", bitmap!!)
                            isAnalyzing = false
                        }
                    },
                    enabled = !isAnalyzing
                ) {
                    if (isAnalyzing) {
                        CircularProgressIndicator(modifier = Modifier.size(24.dp))
                    } else {
                        Text("Analisis Gambar")
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (analysisResult.isNotEmpty()) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Hasil Analisis:", fontWeight = androidx.compose.ui.text.font.FontWeight.Bold)
                        Text(analysisResult)
                    }
                }
            }
        }
    }
}
