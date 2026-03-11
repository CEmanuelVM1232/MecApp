package com.cevm.mecapp.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cevm.mecapp.data.model.SolicitudRevision
import com.cevm.mecapp.data.repository.SolicitudRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class SolicitudViewModel : ViewModel() {
    private val repo = SolicitudRepository()

    private val _solicitudes = MutableStateFlow<List<SolicitudRevision>>(emptyList())
    val solicitudes: StateFlow<List<SolicitudRevision>> = _solicitudes

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading

    private val _exito = MutableStateFlow<String?>(null)
    val exito: StateFlow<String?> = _exito

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    /**
     * Carga todas las solicitudes de revisión que están en estado pendiente.
     * Actualiza el flujo [solicitudes] con la lista obtenida del repositorio.
     */
    fun cargarPendientes() {
        viewModelScope.launch {
            repo.solicitudesPendientes().collect { lista ->
                _solicitudes.value = lista
            }
        }
    }

    /**
     * Carga las solicitudes de revisión correspondientes a un cliente específico.
     * 
     * @param clienteUid El identificador único del cliente.
     */
    fun cargarDelCliente(clienteUid: String) {
        viewModelScope.launch {
            repo.solicitudesDelCliente(clienteUid).collect { lista ->
                _solicitudes.value = lista
            }
        }
    }

    /**
     * Crea una nueva solicitud de revisión en la base de datos.
     * Actualiza los estados de carga y muestra un mensaje de éxito o error al finalizar.
     *
     * @param solicitud El objeto [SolicitudRevision] que contiene los datos de la solicitud.
     */
    fun crearSolicitud(solicitud: SolicitudRevision) {
        viewModelScope.launch {
            _loading.value = true
            repo.crearSolicitud(solicitud).fold(
                onSuccess = { _exito.value = "Solicitud enviada correctamente" },
                onFailure = { e -> _error.value = e.message }
            )
            _loading.value = false
        }
    }

    /**
     * Cambia el estado de una solicitud existente a "aceptada".
     *
     * @param id El identificador de la solicitud a actualizar.
     */
    fun aceptar(id: String) {
        viewModelScope.launch {
            repo.actualizarEstado(id, "aceptada")
        }
    }

    /**
     * Cambia el estado de una solicitud existente a "rechazada".
     *
     * @param id El identificador de la solicitud a actualizar.
     */
    fun rechazar(id: String) {
        viewModelScope.launch {
            repo.actualizarEstado(id, "rechazada")
        }
    }

    /**
     * Elimina una solicitud de revisión de la base de datos.
     * Actualiza el flujo de estado con un mensaje de éxito o el error resultante.
     *
     * @param id El identificador de la solicitud que se desea eliminar.
     */
    fun eliminarSolicitud(id: String) {
        viewModelScope.launch {
            repo.eliminarSolicitud(id).fold(
                onSuccess = { _exito.value = "Solicitud eliminada correctamente" },
                onFailure = { e -> _error.value = e.message }
            )
        }
    }

    /**
     * Limpia los mensajes de éxito y error, restableciéndolos a nulo.
     * Útil para resetear el estado de la UI después de mostrar un SnackBar o un Toast.
     */
    fun limpiarMensajes() {
        _exito.value = null
        _error.value = null
    }
}
