package com.example.proyectointegrador

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Nfc
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.proyectointegrador.ui.theme.ProyectoIntegradorTheme
import kotlinx.coroutines.launch

private enum class AppScreen {
    LAUNCHER,
    LOGIN,
    NFC
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ProyectoIntegradorTheme {
                MainAppHost(
                    onOpenConsultaLlavesActivity = {
                        startActivity(Intent(this, ConsultaLlaves::class.java))
                    },
                    onOpenIncidenciasActivity = {
                        startActivity(Intent(this, PantallaIncidencias::class.java))
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainAppHost(
    onOpenConsultaLlavesActivity: () -> Unit = {},
    onOpenIncidenciasActivity: () -> Unit = {}
) {
    var currentScreen by remember { mutableStateOf(AppScreen.LAUNCHER) }

    when (currentScreen) {
        AppScreen.LAUNCHER -> {
            LauncherScreen(
                onOpenLogin = { currentScreen = AppScreen.LOGIN },
                onOpenConsultaLlaves = onOpenConsultaLlavesActivity,
                onOpenNfc = { currentScreen = AppScreen.NFC },
                onOpenIncidencias = onOpenIncidenciasActivity
            )
        }
        AppScreen.LOGIN -> {
            BackHandler { currentScreen = AppScreen.LAUNCHER }
            Scaffold(
                topBar = {
                    TopAppBar(
                        title = { Text(stringResource(R.string.launcher_screen1_title), fontWeight = FontWeight.Bold) },
                        navigationIcon = {
                            IconButton(onClick = { currentScreen = AppScreen.LAUNCHER }) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                    contentDescription = stringResource(R.string.cd_back),
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }
                        },
                        colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
                    )
                }
            ) { innerPadding ->
                Box(modifier = Modifier.padding(innerPadding)) {
                    LoginHeaderScreen()
                }
            }
        }
        AppScreen.NFC -> {
            BackHandler { currentScreen = AppScreen.LAUNCHER }
            Scaffold(
                topBar = {
                    TopAppBar(
                        title = { Text(stringResource(R.string.launcher_screen4_title), fontWeight = FontWeight.Bold) },
                        navigationIcon = {
                            IconButton(onClick = { currentScreen = AppScreen.LAUNCHER }) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                    contentDescription = stringResource(R.string.cd_back),
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }
                        },
                        colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
                    )
                }
            ) { innerPadding ->
                Box(modifier = Modifier.padding(innerPadding)) {
                    NfcScreen()
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LauncherScreen(
    onOpenLogin: () -> Unit = {},
    onOpenConsultaLlaves: () -> Unit = {},
    onOpenNfc: () -> Unit = {},
    onOpenIncidencias: () -> Unit = {}
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()
    val comingSoonMsg = stringResource(R.string.launcher_snackbar_pending)

    fun showPendingSnackbar() {
        coroutineScope.launch {
            snackbarHostState.showSnackbar(comingSoonMsg)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.launcher_title),
                        fontWeight = FontWeight.Bold
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Spacer(modifier = Modifier.height(8.dp))

            // Botón 1: Login
            LauncherButton(
                text = stringResource(R.string.launcher_screen1_title),
                icon = Icons.Default.Lock,
                isActive = true,
                onClick = onOpenLogin
            )

            // Botón 2: Panel Principal y Consulta de Llaves
            LauncherButton(
                text = stringResource(R.string.launcher_screen2_title),
                icon = Icons.Default.Key,
                isActive = true,
                onClick = onOpenConsultaLlaves
            )

            // Botón 3: Préstamos Activos y Devolución
            LauncherButton(
                text = stringResource(R.string.launcher_screen3_title),
                icon = Icons.Default.Schedule,
                isActive = false,
                onClick = { showPendingSnackbar() }
            )

            // Botón 4: Búsqueda y Lectura NFC
            LauncherButton(
                text = stringResource(R.string.launcher_screen4_title),
                icon = Icons.Default.Nfc,
                isActive = true,
                onClick = onOpenNfc
            )

            // Botón 5: Reporte de Incidencias
            LauncherButton(
                text = stringResource(R.string.launcher_screen5_title),
                icon = Icons.Default.Build,
                isActive = true,
                onClick = onOpenIncidencias
            )
        }
    }
}

@Composable
private fun LauncherButton(
    text: String,
    icon: ImageVector,
    isActive: Boolean,
    onClick: () -> Unit
) {
    if (isActive) {
        Button(
            onClick = onClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary
            )
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.padding(end = 12.dp)
            )
            Text(
                text = text,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold
            )
        }
    } else {
        OutlinedButton(
            onClick = onClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(12.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.padding(end = 12.dp)
            )
            Text(
                text = text,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun LauncherScreenPreview() {
    ProyectoIntegradorTheme {
        LauncherScreen()
    }
}
