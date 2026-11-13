package com.example.proyectointegrador

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

sealed interface CamaraCarnetUiState {
    object SinPermiso : CamaraCarnetUiState
    object Escaneando : CamaraCarnetUiState
//5
    data class CodigoLeido(
        val codigoProfesor: String
    ) : CamaraCarnetUiState

    data class Error(
        val mensaje: String
    ) : CamaraCarnetUiState
}

class CamaraCarnetViewModel(
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _uiState = MutableStateFlow<CamaraCarnetUiState>(
        recuperarEstado()
    )

    val uiState: StateFlow<CamaraCarnetUiState> =
        _uiState.asStateFlow()

    private fun recuperarEstado(): CamaraCarnetUiState {
        val codigo = savedStateHandle.get<String>("codigoProfesor")
        val error = savedStateHandle.get<String>("errorCamara")

        return when {
            !codigo.isNullOrBlank() ->
                CamaraCarnetUiState.CodigoLeido(codigo)

            !error.isNullOrBlank() ->
                CamaraCarnetUiState.Error(error)

            else ->
                CamaraCarnetUiState.SinPermiso
        }
    }

    fun actualizarPermiso(concedido: Boolean) {
        // Mantiene el resultado cuando ya se ha leído un código.
        if (_uiState.value is CamaraCarnetUiState.CodigoLeido) {
            return
        }

        if (!concedido) {
            _uiState.value = CamaraCarnetUiState.SinPermiso
        } else {
            _uiState.value = CamaraCarnetUiState.Escaneando
        }
    }

    fun registrarCodigo(codigo: String): Boolean {
        if (
            codigo.isBlank() ||
            _uiState.value !is CamaraCarnetUiState.Escaneando
        ) {
            return false
        }

        savedStateHandle["codigoProfesor"] = codigo
        savedStateHandle["errorCamara"] = null

        _uiState.value = CamaraCarnetUiState.CodigoLeido(codigo)

        return true
    }

    fun registrarError(mensaje: String) {
        if (_uiState.value !is CamaraCarnetUiState.Escaneando) {
            return
        }

        savedStateHandle["errorCamara"] = mensaje
        _uiState.value = CamaraCarnetUiState.Error(mensaje)
    }

    fun reiniciar(permisoConcedido: Boolean) {
        savedStateHandle["codigoProfesor"] = null
        savedStateHandle["errorCamara"] = null

        _uiState.value = if (permisoConcedido) {
            CamaraCarnetUiState.Escaneando
        } else {
            CamaraCarnetUiState.SinPermiso
        }
    }
}