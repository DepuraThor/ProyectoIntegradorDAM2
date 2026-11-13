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
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
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
import com.google.mlkit.vision.common.InputImage

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
        when (uiState) {
            is CamaraCarnetUiState.CodigoLeido -> {
                Text("Código leído correctamente")

                Spacer(Modifier.height(8.dp))

                Text((uiState as CamaraCarnetUiState.CodigoLeido).codigoProfesor)
            }

            is CamaraCarnetUiState.Escaneando -> {
                Text("Enfoca el código del carné del alumno")

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
                Text(
                    text = (uiState as CamaraCarnetUiState.Error).mensaje,
                    color = androidx.compose.ui.graphics.Color.Red
                )

                Spacer(Modifier.height(16.dp))

                Button(
                    onClick = {
                        viewModel.reiniciar(permisoConcedido)
                    }
                ) {
                    Text("Reintentar")
                }
            }

            is CamaraCarnetUiState.SinPermiso -> {
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
                if (uiState is CamaraCarnetUiState.CodigoLeido) "Volver" else "Cancelar"
            )
        }
    }
}

// VisorCarnet basado en el código de referencia oficial de la Entrega 2 (ProcessCameraProvider)
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

    val cameraProviderFuture = remember {
        ProcessCameraProvider.getInstance(context)
    }

    AndroidView(
        factory = { ctx ->
            val previewView = PreviewView(ctx).apply {
                scaleType = PreviewView.ScaleType.FILL_CENTER
            }

            val scanner = BarcodeScanning.getClient()
            val executor = ContextCompat.getMainExecutor(ctx)

            cameraProviderFuture.addListener({
                try {
                    val cameraProvider = cameraProviderFuture.get()
                    val preview = Preview.Builder().build().also {
                        it.setSurfaceProvider(previewView.surfaceProvider)
                    }

                    val imageAnalysis = ImageAnalysis.Builder()
                        .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                        .build()

                    var resultadoEntregado = false

                    imageAnalysis.setAnalyzer(executor) { imageProxy ->
                        val mediaImage = imageProxy.image
                        if (mediaImage != null && !resultadoEntregado) {
                            val image = InputImage.fromMediaImage(
                                mediaImage,
                                imageProxy.imageInfo.rotationDegrees
                            )
                            scanner.process(image)
                                .addOnSuccessListener { barcodes ->
                                    val codigo = barcodes.firstOrNull { !it.rawValue.isNullOrBlank() }?.rawValue
                                    if (codigo != null && !resultadoEntregado) {
                                        resultadoEntregado = true
                                        onDetectadoActual(codigo)
                                    }
                                }
                                .addOnFailureListener {}
                                .addOnCompleteListener {
                                    imageProxy.close()
                                }
                        } else {
                            imageProxy.close()
                        }
                    }

                    val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
                    cameraProvider.unbindAll()
                    cameraProvider.bindToLifecycle(
                        lifecycleOwner,
                        cameraSelector,
                        preview,
                        imageAnalysis
                    )
                } catch (e: Exception) {
                    e.printStackTrace()
                    onErrorActual("Error cámara: ${e.javaClass.simpleName} - ${e.localizedMessage}")
                }
            }, executor)

            previewView
        },
        modifier = modifier
    )
}
