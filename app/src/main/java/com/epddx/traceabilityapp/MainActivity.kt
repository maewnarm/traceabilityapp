package com.epddx.traceabilityapp

import android.content.Context
import android.content.DialogInterface
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.epddx.traceabilityapp.ui.screens.HomeScreen
import com.epddx.traceabilityapp.ui.screens.RepackScreen
import com.epddx.traceabilityapp.ui.screens.ScannerScreen
import com.epddx.traceabilityapp.ui.theme.TraceabilityAppTheme
import com.epddx.traceabilityapp.ui.vm.PrinterViewModel
import kotlinx.coroutines.launch


class MainActivity : AppCompatActivity() {
    companion object {
        private const val TAG = "CameraX-MLKit"
        private const val REQUEST_CODE_PERMISSIONS = 10
        private val REQUIRED_PERMISSIONS =
            mutableListOf(
                android.Manifest.permission.CAMERA
            ).toTypedArray()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        checkPermission()
        setContent {
            TraceabilityAppTheme {
                if (allPermissionsGranted()) {
                    MyApp(modifier = Modifier.fillMaxSize(), context = this)
                } else {
                    requestPermission()
                }
            }
        }
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(
            baseContext, it
        ) == PackageManager.PERMISSION_GRANTED
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (allPermissionsGranted()) {
                recreate()
            } else {
                Toast.makeText(
                    this,
                    "Permissions not granted by the user.",
                    Toast.LENGTH_SHORT
                ).show()
                finish()
            }
        }
    }

    private fun requestPermission() {
        ActivityCompat.requestPermissions(
            this, mutableListOf(android.Manifest.permission.CAMERA).toTypedArray(),
            REQUEST_CODE_PERMISSIONS
        )
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyApp(modifier: Modifier = Modifier, context: Context) {
    val navController = rememberNavController()
    val currentBackStack by navController.currentBackStackEntryAsState()
    val currentDestination = currentBackStack?.destination
    val currentScreen = tabRowScreens.find { it.route == currentDestination?.route } ?: Home
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    val printerViewModel: PrinterViewModel = viewModel()

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(modifier = Modifier.width(200.dp)) {
                Row(
                    modifier = Modifier
                        .background(MaterialTheme.colorScheme.secondary)
                        .padding(16.dp, 8.dp)
                        .fillMaxWidth()
                ) {
                    Text(
                        text = "Menu",
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.surface,

                        )
                }
                Spacer(Modifier.height(12.dp))
                tabRowScreens.forEach { screen ->
                    NavigationDrawerItem(
                        label = {
                            Row {
                                Icon(imageVector = screen.icon, contentDescription = screen.route)
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(text = screen.route)
                            }
                        },
                        selected = currentScreen == screen,
                        onClick = {
                            navController.navigateSingleTopTo(screen.route)
                            scope.launch {
                                drawerState.apply {
                                    close()
                                }
                            }
                        },
                        modifier = Modifier.height(40.dp)
                    )
                }
            }
        },
    ) {
        Scaffold(
            topBar = {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .animateContentSize()
                            .height(56.dp)
                            .background(color = MaterialTheme.colorScheme.primary),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        TextButton(onClick = {
                            scope.launch {
                                drawerState.apply {
                                    if (isClosed) open() else close()
                                }
                            }
                        }) {
                            Icon(
                                imageVector = Icons.Filled.Menu,
                                contentDescription = "",
                                tint = MaterialTheme.colorScheme.surface
                            )
                        }
                        TextButton(onClick = {
                            scope.launch {
                                drawerState.apply {
                                    if (isClosed) open() else close()
                                }
                            }
                        }) {
                            Icon(
                                imageVector = Icons.Filled.Settings,
                                contentDescription = "",
                                tint = MaterialTheme.colorScheme.surface
                            )
                        }
                    }
                }
            }
        ) { innerPadding ->
            NavHost(
                navController = navController,
                startDestination = Home.route,
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxWidth()
                    .fillMaxHeight()
            ) {
                composable(route = Home.route) {
                    HomeScreen()
                }
                composable(route = Repack.route) {
                    RepackScreen(
                        onCallScanner = { navController.navigate(Scanner.route) },
                        printerViewModel = printerViewModel,
                    )
                }
                composable(route = Scanner.route) {
                    ScannerScreen(onBack = {
                        navController.popBackStack()
                    }, printerViewModel = printerViewModel, startScan = true)
                }
            }
        }
    }
}

fun NavHostController.navigateSingleTopTo(route: String) =
    this.navigate(route) {
        popUpTo(
            this@navigateSingleTopTo.graph.findStartDestination().id
        ) {
            saveState = true
        }
        launchSingleTop = true
        restoreState = true
    }


@Preview(showBackground = true, name = "Light Mode")
//@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true, name = "Dark Mode")
@Composable
fun GreetingPreview() {
    TraceabilityAppTheme {
        MyApp(modifier = Modifier.fillMaxSize(), context = LocalContext.current)
    }
}