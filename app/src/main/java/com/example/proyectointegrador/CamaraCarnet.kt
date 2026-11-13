package com.example.proyectointegrador

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.mlkit.vision.MlKitAnalyzer
import androidx.camera.view.CameraController
import androidx.camera.view.LifecycleCameraController
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.mlkit.vision.barcode.BarcodeScanning

@Composable
fun CamaraCarnet(
    onCodigoLeido: (String) -> Unit,
    onCancelar: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: CamaraCarnetViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    fun tienePermiso(): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED
    }

    var permisoConcedido by remember {
        mutableStateOf(tienePermiso())
    }

    var permisoSolicitado by rememberSaveable {
        mutableStateOf(false)
    }

    val onCodigoActual by rememberUpdatedState(onCodigoLeido)

    val solicitarPermiso = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { concedido ->
        permisoConcedido = concedido
        viewModel.actualizarPermiso(concedido)
    }

    LaunchedEffect(Unit) {
        viewModel.reiniciar(permisoConcedido)
        if (!permisoConcedido && !permisoSolicitado) {
            permisoSolicitado = true
            solicitarPermiso.launch(Manifest.permission.CAMERA)
        }
    }

    DisposableEffect(lifecycleOwner, context) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                val concedido = tienePermiso()
                permisoConcedido = concedido
                viewModel.actualizarPermiso(concedido)
            }
        }

        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    LaunchedEffect(uiState) {
        if (uiState is CamaraCarnetUiState.CodigoLeido) {
            val codigo = (uiState as CamaraCarnetUiState.CodigoLeido).codigoProfesor
            onCodigoActual(codigo)
        }
    }

    BackHandler {
        onCancelar()
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        when (val state = uiState) {
            is CamaraCarnetUiState.CodigoLeido -> {
                Text("Código leído correctamente")
                Spacer(Modifier.height(8.dp))
                Text(state.codigoProfesor)
            }

            is CamaraCarnetUiState.Escaneando -> {
                Text("Enfoca el código del carné del alumno/profesor")
                Spacer(Modifier.height(16.dp))

                VisorCarnet(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    onError = { error ->
                        viewModel.registrarError(error)
                    },
                    onDetectado = { codigo ->
                        viewModel.registrarCodigo(codigo)
                    }
                )
            }

            is CamaraCarnetUiState.Error -> {
                Text("Error: ${state.mensaje}")
                Spacer(Modifier.height(16.dp))
                Button(onClick = { viewModel.reiniciar(permisoConcedido) }) {
                    Text("Reintentar")
                }
            }

            is CamaraCarnetUiState.SinPermiso -> {
                Text("Necesitamos permiso de cámara para escanear el carné.")
                Spacer(Modifier.height(16.dp))

                Button(
                    onClick = {
                        solicitarPermiso.launch(Manifest.permission.CAMERA)
                    }
                ) {
                    Text("Permitir cámara")
                }

                TextButton(
                    onClick = {
                        val intent = Intent(
                            Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                            Uri.parse("package:${context.packageName}")
                        )
                        context.startActivity(intent)
                    }
                ) {
                    Text("Abrir ajustes")
                }
            }
        }

        Spacer(Modifier.height(16.dp))

        TextButton(onClick = onCancelar) {
            Text(if (uiState is CamaraCarnetUiState.CodigoLeido) "Volver" else "Cancelar")
        }
    }
}

// Esta función está fuera de CamaraCarnet.
@Composable
private fun VisorCarnet(
    modifier: Modifier,
    onDetectado: (String) -> Unit,
    onError: (String) -> Unit
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    // si
    val onDetectadoActual by rememberUpdatedState(onDetectado)
    val onErrorActual by rememberUpdatedState(onError)

    val previewView = remember(context) {
        PreviewView(context).apply {
            scaleType = PreviewView.ScaleType.FILL_CENTER
        }
    }

    AndroidView(
        factory = { previewView },
        modifier = modifier
    )

    DisposableEffect(previewView, lifecycleOwner) {
        val executor = ContextCompat.getMainExecutor(context)
        val controller = LifecycleCameraController(context)

        // Lee QR y códigos de barras.
        val scanner = BarcodeScanning.getClient()

        var vigente = true
        var resultadoEntregado = false

        try {
            val cameraSelector = if (controller.hasCamera(CameraSelector.DEFAULT_BACK_CAMERA)) {
                CameraSelector.DEFAULT_BACK_CAMERA
            } else if (controller.hasCamera(CameraSelector.DEFAULT_FRONT_CAMERA)) {
                CameraSelector.DEFAULT_FRONT_CAMERA
            } else {
                CameraSelector.DEFAULT_BACK_CAMERA
            }
            controller.cameraSelector = cameraSelector

            val analyzer = MlKitAnalyzer(
                listOf(scanner),
                ImageAnalysis.COORDINATE_SYSTEM_VIEW_REFERENCED,
                executor
            ) { resultado ->

                val pantallaActiva =
                    lifecycleOwner.lifecycle.currentState.isAtLeast(
                        Lifecycle.State.RESUMED
                    )

                if (
                    vigente &&
                    pantallaActiva &&
                    !resultadoEntregado
                ) {
                    val codigos = resultado.getValue(scanner)

                    val codigo = codigos
                        ?.firstOrNull {
                            !it.rawValue.isNullOrBlank()
                        }
                        ?.rawValue

                    if (codigo != null) {
                        // Evita entregar el mismo código varias veces.
                        resultadoEntregado = true

                        controller.clearImageAnalysisAnalyzer()
                        controller.unbind()

                        onDetectadoActual(codigo)
                    }
                }
            }

            controller.setImageAnalysisAnalyzer(
                executor,
                analyzer
            )

            previewView.controller = controller
            controller.bindToLifecycle(lifecycleOwner)
        } catch (_: Exception) {
            controller.clearImageAnalysisAnalyzer()
            controller.unbind()

            onErrorActual(
                "No se ha podido abrir la cámara."
            )
        }

        // Se ejecuta al abandonar el visor.
        onDispose {
            vigente = false

            controller.clearImageAnalysisAnalyzer()
            controller.unbind()

            previewView.controller = null
            scanner.close()
        }
    }
}