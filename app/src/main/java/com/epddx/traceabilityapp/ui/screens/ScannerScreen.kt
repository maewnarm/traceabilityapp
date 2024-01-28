package com.epddx.traceabilityapp.ui.screens

import android.annotation.SuppressLint
import android.util.Log
import androidx.camera.core.CameraSelector
import androidx.camera.mlkit.vision.MlKitAnalyzer
import androidx.camera.view.CameraController
import androidx.camera.view.LifecycleCameraController
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.epddx.traceabilityapp.ui.utils.OnLifecycleEvent
import com.epddx.traceabilityapp.ui.vm.PrinterViewModel
import com.epddx.traceabilityapp.ui.vm.ScannerViewModel
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import java.util.concurrent.Executors


@SuppressLint("UnsafeOptInUsageError")
@Composable
fun ScannerScreen(
    onBack: () -> Unit = {},
    startScan: Boolean = false,
    scannerVM: ScannerViewModel = viewModel(),
    printerViewModel: PrinterViewModel = viewModel()
) {
    val context = LocalContext.current

    val cameraController = remember { LifecycleCameraController(context) }
    val previewView: PreviewView = remember { PreviewView(context) }

    val options = BarcodeScannerOptions.Builder().setBarcodeFormats(Barcode.FORMAT_QR_CODE).build()

    val lifecycleOwner = LocalLifecycleOwner.current

    var barcodeScanner = BarcodeScanning.getClient(options)
    var executor = remember { Executors.newSingleThreadExecutor() }


    val textRecognizer =
        remember { TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS) }
    var textResult by rememberSaveable { mutableStateOf("") }
    var isScanning by remember { mutableStateOf(false) }
    var msgResult by rememberSaveable {
        mutableStateOf("")
    }

    fun onCancel() {
        isScanning = false
        cameraController.unbind()
        executor.shutdown()
        barcodeScanner.close()
        onBack()
    }

    fun onFoundResult() {
        printerViewModel.setText(textResult)
        Log.d("SCANNER", printerViewModel.getText())
        textResult = ""
        onBack()
    }

    fun startScanner() {
        isScanning = true

        executor = Executors.newSingleThreadExecutor()
        barcodeScanner = BarcodeScanning.getClient(options)
        cameraController.bindToLifecycle(lifecycleOwner)
        cameraController.cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
        previewView.controller = cameraController

        cameraController.setImageAnalysisAnalyzer(
            executor,
            MlKitAnalyzer(
                listOf(barcodeScanner),
                CameraController.COORDINATE_SYSTEM_VIEW_REFERENCED,
                executor
            ) { result: MlKitAnalyzer.Result? ->
                val qrCodeResults = result?.getValue(barcodeScanner)
                if ((qrCodeResults == null) ||
                    (qrCodeResults.size == 0) ||
                    (qrCodeResults.first() == null)
                ) {
//                    previewView.overlay.clear()
                    return@MlKitAnalyzer
                } else {
                    // found code
                    isScanning = false
                    textResult = qrCodeResults[0].displayValue ?: ""
                    onFoundResult()
                }
            }
        )
    }

    OnLifecycleEvent { _, event ->
        when (event) {
            Lifecycle.Event.ON_RESUME -> {
                if (startScan) {
                    startScanner()
                }
            }

            else -> {}
        }
    }

    Column(
        modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (isScanning) {
            Box(
                modifier = Modifier.fillMaxSize()
            ) {
                AndroidView(
                    factory = { previewView }, modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight()
                )
                Button(
                    onClick = { onCancel() },
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(text = "CANCEL")
                }
            }
        }

        Row(modifier = Modifier) {
            if (!isScanning) {
                Button(
                    onClick = { startScanner() }) {
                    Text(text = "SCAN")
                }
            }
        }

//        if (textResult.isNotEmpty()) {
//            Dialog(onDismissRequest = { textResult = "" }) {
//                Card {
//                    Text(
//                        text = textResult,
//                        modifier = Modifier.padding(16.dp),
//                        style = MaterialTheme.typography.bodySmall
//                    )
//                    Spacer(modifier = Modifier.height(16.dp))
//                    Row {
//                        Button(onClick = { textResult = "" }) {
//                            Text(text = "Done")
//                        }
//                        Button(onClick = { onPrint() }) {
//                            Text(text = "Print QR")
//                        }
//                    }
//                }
//            }
//        }

//        if (msgResult.isNotEmpty()) {
//            Dialog(onDismissRequest = { msgResult = "" }) {
//                Card {
//                    Text(
//                        text = msgResult,
//                        modifier = Modifier.padding(16.dp),
//                        style = MaterialTheme.typography.bodySmall
//                    )
//                    Spacer(modifier = Modifier.height(16.dp))
//                    Row {
//                        Button(onClick = { textResult = "" }) {
//                            Text(text = "OK")
//                        }
//                    }
//                }
//            }
//        }
    }
}
