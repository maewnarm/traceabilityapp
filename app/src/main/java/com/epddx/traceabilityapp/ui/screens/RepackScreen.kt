package com.epddx.traceabilityapp.ui.screens

import android.content.Context
import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.epddx.traceabilityapp.ui.utils.OnLifecycleEvent
import com.epddx.traceabilityapp.ui.vm.PrinterViewModel
import kotlinx.coroutines.delay

var printerViewModel: PrinterViewModel = PrinterViewModel()

@Composable
fun RepackScreen(
    modifier: Modifier = Modifier,
    viewModel: PrinterViewModel = viewModel(),
) {

    printerViewModel = viewModel
    val localContext: Context = LocalContext.current
    val printerStatus by printerViewModel.printerStatus.collectAsState()

    OnLifecycleEvent { _, event ->
        when (event) {
            Lifecycle.Event.ON_RESUME -> {
                initPrinter(localContext)
            }

            else -> {}
        }
    }

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(modifier = Modifier, verticalAlignment = Alignment.CenterVertically) {
            Button(onClick = { getPrinter() }, enabled = printerStatus !== "READY") {
                Text("Get Printer")
            }
            Spacer(modifier = Modifier.width(4.dp))
            Text(text = if (printerStatus != "") printerStatus else "NOT READY")
        }
        Button(onClick = { printerViewModel.testPrint() }) {
            Text("Test Print")
        }
    }
}

fun initPrinter(context: Context) {
    Log.d("REPACK", "initPrinter start")
    printerViewModel.initPrinter(context, ::getPrinter)
}

fun getPrinter() {
    printerViewModel.showPrinter()
}
