package com.epddx.traceabilityapp

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.Home
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import com.epddx.traceabilityapp.ui.screens.HomeScreen
import com.epddx.traceabilityapp.ui.screens.RepackScreen
import com.epddx.traceabilityapp.ui.screens.ScannerScreen

interface Destination {
    val icon: ImageVector
    val route: String
    val screen: @Composable () -> Unit
}
object Home: Destination {
    override val icon = Icons.Filled.Home
    override val route= "Home"
    override val screen: @Composable () -> Unit = { HomeScreen() }
}

object Scanner: Destination {
    override val icon = Icons.Filled.Create
    override val route= "Scanner"
    override val screen: @Composable () -> Unit = { ScannerScreen()}
}

object Repack: Destination {
    override val icon = Icons.Filled.Create
    override val route= "Repack"
    override val screen: @Composable () -> Unit = { RepackScreen()}
}

val tabRowScreens = listOf(Home,Repack,Scanner)