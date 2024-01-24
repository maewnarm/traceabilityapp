package com.epddx.traceabilityapp.ui.screens

import android.annotation.SuppressLint
import androidx.camera.core.CameraSelector
import androidx.camera.view.LifecycleCameraController
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import com.epddx.traceabilityapp.ui.vm.ScannerViewModel
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import java.util.concurrent.Executors


@SuppressLint("UnsafeOptInUsageError")
@Composable
fun ScannerScreen(viewModel: ScannerViewModel = viewModel()) {
    val context = LocalContext.current
    val previewView: PreviewView = remember { PreviewView(context) }

    val cameraController = remember { LifecycleCameraController(context) }
    val lifecycleOwner = LocalLifecycleOwner.current

    cameraController.bindToLifecycle(lifecycleOwner)
    cameraController.cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
    previewView.controller = cameraController

    val executor = remember { Executors.newSingleThreadExecutor() }


    val textRecognizer =
        remember { TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS) }
    var textResult by rememberSaveable { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }



    Column(
        modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        AndroidView(factory = { previewView }, modifier = Modifier.height(240.dp).width(320.dp))
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier
                    .size(50.dp)
                    .align(Alignment.CenterHorizontally)
            )
        } else {
            Button(
                modifier = Modifier.padding(16.dp),
                onClick = {
                isLoading = true
                cameraController.setImageAnalysisAnalyzer(executor) { imageProxy ->
                    imageProxy.image?.let { image ->
                        val img =
                            InputImage.fromMediaImage(image, imageProxy.imageInfo.rotationDegrees)

                        textRecognizer.process(img).addOnCompleteListener { task ->
                            textResult = if (!task.isSuccessful) {
                                task.exception!!.localizedMessage.toString()
                            } else {
                                task.result.text
                            }


                            cameraController.clearImageAnalysisAnalyzer()
                            imageProxy.close()
                        }
                    }
                }
            }) {
                Text(text = "initiate")
            }
        }
        if (textResult.isNotEmpty()) {
            Dialog(onDismissRequest = { textResult = "" }) {
                Card {
                    Text(
                        text = textResult,
                        modifier = Modifier.padding(16.dp),
                        style = MaterialTheme.typography.bodySmall
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(onClick = { textResult = "" }) {
                        Text(text = "Done")


                    }
                }
            }
        }
    }

}
