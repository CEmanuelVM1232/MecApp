package com.cevm.mecapp.ui


import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cevm.mecapp.data.model.OrdenServicio
import com.cevm.mecapp.data.repository.OrdenRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class OrdenViewModel : ViewModel() {
    private val repo = OrdenRepository()

    private val _ordenes = MutableStateFlow<List<OrdenServicio>>(emptyList())
    val ordenes: StateFlow<List<OrdenServicio>> = _ordenes

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    private val _exito = MutableStateFlow<String?>(null)
    val exito: StateFlow<String?> = _exito

    /**
     * Carga todas las órdenes de servicio asignadas a un mecánico específico.
     *
     * @param uid El identificador (UID) del mecánico.
     */
    fun cargarOrdenesDelMecanico(uid: String) {
        viewModelScope.launch {
            repo.ordenesDelMecanico(uid).collect { lista ->
                _ordenes.value = lista
            }
        }
    }

    /**
     * Carga todas las órdenes de servicio que pertenecen a un cliente específico.
     *
     * @param uid El identificador (UID) del cliente.
     */
    fun cargarOrdenesDelCliente(uid: String) {
        viewModelScope.launch {
            repo.ordenesDelCliente(uid).collect { lista ->
                _ordenes.value = lista
            }
        }
    }

    /**
     * Crea una nueva orden de servicio en la base de datos.
     * Actualiza el estado de carga y emite un mensaje de éxito o de error según el resultado.
     *
     * @param orden La orden de servicio a crear.
     */
    fun crearOrden(orden: OrdenServicio) {
        viewModelScope.launch {
            _loading.value = true
            repo.crearOrden(orden).fold(
                onSuccess = { _exito.value = "Orden creada correctamente" },
                onFailure = { e -> _error.value = e.message }
            )
            _loading.value = false
        }
    }

    /**
     * Actualiza el estado de una orden específica (ej. de "pendiente" a "en progreso").
     *
     * @param id El identificador único de la orden.
     * @param nuevoEstado El estado al cual se actualizará la orden.
     */
    fun actualizarEstado(id: String, nuevoEstado: String) {
        viewModelScope.launch {
            repo.actualizar(id, mapOf("estado" to nuevoEstado))
        }
    }

    /**
     * Elimina una orden de servicio de la base de datos.
     * Notifica el resultado a través de los flujos de éxito o error.
     *
     * @param id El identificador de la orden que se desea eliminar.
     */
    fun eliminarOrden(id: String) {
        viewModelScope.launch {
            _loading.value = true
            repo.eliminarOrden(id).fold(
                onSuccess = { _exito.value = "Orden eliminada correctamente" },
                onFailure = { e -> _error.value = e.message }
            )
            _loading.value = false
        }
    }

    /**
     * Sube una imagen de una firma física a Storage y enlaza la URL generada a la orden correspondiente.
     *
     * @param uri La URI local del archivo de la imagen de la firma.
     * @param ordenId El identificador de la orden donde se adjuntará la firma.
     */
    fun subirFirmaFisica(uri: Uri, ordenId: String) {
        viewModelScope.launch {
            _loading.value = true
            val path = "firmas/$ordenId/firma_fisica.jpg"
            repo.subirFoto(uri, path).fold(
                onSuccess = { url ->
                    viewModelScope.launch {
                        repo.actualizar(ordenId, mapOf("urlFirmaFisica" to url))
                    }
                    _exito.value = "Firma adjuntada correctamente"
                },
                onFailure = { e -> _error.value = e.message }
            )
            _loading.value = false
        }
    }

    /**
     * Restablece los mensajes de error y de éxito a nulo, limpiando el estado de la interfaz.
     */
    fun limpiarMensajes() {
        _error.value = null
        _exito.value = null
    }
}
