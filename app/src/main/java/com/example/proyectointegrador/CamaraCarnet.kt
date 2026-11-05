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
import com.google.mlkit.vision.barcode.BarcodeScanning

@Composable
fun CamaraCarnet(
    onCodigoLeido: (String) -> Unit,
    onCancelar: () -> Unit,
    modifier: Modifier = Modifier
) {
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

    var codigoLeido by rememberSaveable {
        mutableStateOf<String?>(null)
    }

    var mensaje by remember {
        mutableStateOf("Enfoca el código del carné del alumno")
    }

    val onCodigoActual by rememberUpdatedState(onCodigoLeido)

    // Solicita el permiso y recibe la respuesta de Android.
    val solicitarPermiso = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { concedido ->
        permisoConcedido = concedido
    }

    // Se ejecuta al entrar en la pantalla.
    LaunchedEffect(Unit) {
        if (!permisoConcedido && !permisoSolicitado) {
            permisoSolicitado = true
            solicitarPermiso.launch(Manifest.permission.CAMERA)
        }
    }

    // Actualiza el permiso al regresar desde los ajustes.
    DisposableEffect(lifecycleOwner, context) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                permisoConcedido = tienePermiso()
            }
        }

        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
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
        when {
            codigoLeido != null -> {
                Text("Código leído correctamente")

                Spacer(Modifier.height(8.dp))

                Text(codigoLeido.orEmpty())
            }

            permisoConcedido -> {
                Text(mensaje)

                Spacer(Modifier.height(16.dp))

                VisorCarnet(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    onError = { error ->
                        mensaje = error
                    },
                    onDetectado = { codigo ->
                        codigoLeido = codigo
                        onCodigoActual(codigo)
                    }
                )
            }

            else -> {
                Text(
                    "Necesitamos permiso de cámara para escanear el carné."
                )

                Spacer(Modifier.height(16.dp))

                Button(
                    onClick = {
                        solicitarPermiso.launch(
                            Manifest.permission.CAMERA
                        )
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
            Text(
                if (codigoLeido == null) "Cancelar" else "Volver"
            )
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
            controller.cameraSelector =
                CameraSelector.DEFAULT_BACK_CAMERA

            controller.setEnabledUseCases(
                CameraController.IMAGE_ANALYSIS
            )

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
                    } else if (
                        resultado.getThrowable(scanner) != null
                    ) {
                        onErrorActual(
                            "No se ha podido analizar la imagen. " +
                                    "Vuelve a enfocar el código."
                        )
                    }
                }
            }

            controller.setImageAnalysisAnalyzer(
                executor,
                analyzer
            )

            previewView.controller = controller
            controller.bindToLifecycle(lifecycleOwner)

            val inicializacion = controller.initializationFuture

            inicializacion.addListener(
                {
                    if (vigente && !resultadoEntregado) {
                        try {
                            inicializacion.get()
                        } catch (_: Exception) {
                            onErrorActual(
                                "No se ha podido iniciar la cámara."
                            )
                        }
                    }
                },
                executor
            )
        } catch (_: Exception) {
            controller.clearImageAnalysisAnalyzer()
            controller.unbind()

            onErrorActual(
                "No se ha podido abrir la cámara trasera."
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