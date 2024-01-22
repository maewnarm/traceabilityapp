package com.epddx.traceabilityapp

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Create
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import com.epddx.traceabilityapp.ui.screens.PrinterScreen
import com.epddx.traceabilityapp.ui.screens.ScannerScreen

interface Destination {
    val icon: ImageVector
    val route: String
    val screen: @Composable () -> Unit
}

object Scanner: Destination {
    override val icon = Icons.Filled.Create
    override val route= "Scanner"
    override val screen: @Composable () -> Unit = { ScannerScreen()}
}

object Printer: Destination {
    override val icon = Icons.Filled.Create
    override val route= "Printer"
    override val screen: @Composable () -> Unit = { PrinterScreen()}
}

val tabRowScreens = listOf(Scanner,Printer)