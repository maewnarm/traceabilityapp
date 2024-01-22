package com.epddx.traceabilityapp.ui.screens

import android.content.Context
import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.epddx.traceabilityapp.ui.vm.PrinterViewModel

var printerViewModel: PrinterViewModel = PrinterViewModel()

@Composable
fun PrinterScreen(
    modifier: Modifier = Modifier,
    viewModel: PrinterViewModel = viewModel(),
) {
    printerViewModel = viewModel
    val localContext:Context = LocalContext.current
    val printerStatus by printerViewModel.printerStatus.collectAsState()
    val printerName by printerViewModel.printerName.collectAsState()
    val printerType by printerViewModel.printerType.collectAsState()
    val printerPaper by printerViewModel.printerPaper.collectAsState()
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(onClick = { initPrinter(localContext) }) {
            Text("Initiate Printer")
        }
        Button(onClick = { getPrinter() }) {
            Text("Get Printer")
        }
        Button(onClick = { changeText() }) {
            Text("Change Text")
        }
        Text(text = "printer name : $printerStatus")
        Text(text = "printer name : $printerName")
        Text(text = "printer name : $printerType")
        Text(text = "printer name : $printerPaper")
        Button(onClick = { printerViewModel.testPrint() }) {
            Text("Test Print")
        }
    }
}

fun initPrinter(context: Context) {
    printerViewModel.initPrinter(context)
}

fun getPrinter() {
    printerViewModel.showPrinter()
}

fun changeText() {
    Log.d("ACTIVITY", "change text")
    printerViewModel.changeText()
}