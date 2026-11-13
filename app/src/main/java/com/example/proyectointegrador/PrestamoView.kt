package com.example.proyectointegrador.ui.viewmodels

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

sealed interface PrestamoUiState {
    object Inicio : PrestamoUiState
    object EscaneandoLlaveNfc : PrestamoUiState
    object EscaneandoProfesor : PrestamoUiState
    data class Confirmacion(
        val codigoProfesor: String,
        val nfcLlave: String,
        val aula: String
    ) : PrestamoUiState
    data class Exito(val mensaje: String) : PrestamoUiState
    data class Error(val error: String) : PrestamoUiState
}

class PrestamoView : ViewModel() {

    private val _uiState = MutableStateFlow<PrestamoUiState>(PrestamoUiState.Inicio)
    val uiState: StateFlow<PrestamoUiState> = _uiState.asStateFlow()

    var codigoProfesorCapturado: String = ""
        private set
    var nfcLlaveCapturada: String = ""
        private set

    fun onNfcTagDetected(nfcUid: String) {
        if (nfcUid.isNotBlank()) {
            nfcLlaveCapturada = nfcUid
            if (codigoProfesorCapturado.isNotEmpty()) {
                _uiState.update {
                    PrestamoUiState.Confirmacion(
                        codigoProfesor = codigoProfesorCapturado,
                        nfcLlave = nfcLlaveCapturada,
                        aula = "BE205 - Berlín Planta 2"
                    )
                }
            } else {
                _uiState.update { PrestamoUiState.EscaneandoProfesor }
            }
        }
    }

    fun onBarcodeScanned(codigo: String) {
        if (codigo.isNotBlank()) {
            codigoProfesorCapturado = codigo
            if (nfcLlaveCapturada.isNotEmpty()) {
                _uiState.update {
                    PrestamoUiState.Confirmacion(
                        codigoProfesor = codigoProfesorCapturado,
                        nfcLlave = nfcLlaveCapturada,
                        aula = "BE205 - Berlín Planta 2"
                    )
                }
            } else {
                _uiState.update { PrestamoUiState.EscaneandoLlaveNfc }
            }
        }
    }

    fun confirmarPrestamo() {
        _uiState.update {
            PrestamoUiState.Exito("Préstamo registrado correctamente para la llave $nfcLlaveCapturada")
        }
    }

    fun reiniciarFlujo() {
        codigoProfesorCapturado = ""
        nfcLlaveCapturada = ""
        _uiState.update { PrestamoUiState.Inicio }
    }
}