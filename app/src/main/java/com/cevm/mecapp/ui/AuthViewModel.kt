package com.cevm.mecapp.ui


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cevm.mecapp.data.model.Usuario
import com.cevm.mecapp.data.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/** Representa los posibles estados del flujo de autenticación de la aplicación. */
sealed class AuthState {
    object Idle : AuthState()
    object Loading : AuthState()
    data class Success(val tipo: String) : AuthState()
    data class Error(val mensaje: String) : AuthState()
}

/**
 * ViewModel principal encargado de gestionar la sesión del usuario,
 * el flujo de inicio de sesión/registro, y las actualizaciones del perfil.
 */
class AuthViewModel : ViewModel() {
    private val repo = AuthRepository()

    private val _state = MutableStateFlow<AuthState>(AuthState.Idle)
    val state: StateFlow<AuthState> = _state

    private val _usuario = MutableStateFlow<Usuario?>(null)
    val usuario: StateFlow<Usuario?> = _usuario

    private val _mecanicos = MutableStateFlow<List<Usuario>>(emptyList())
    val mecanicos: StateFlow<List<Usuario>> = _mecanicos

    /**
     * Comprueba asíncronamente si ya existe una sesión activa guardada.
     * De ser así, carga el usuario desde Firestore y actualiza el estado a [AuthState.Success].
     */
    fun verificarSesion() {
        val user = repo.currentUser
        if (user != null) {
            viewModelScope.launch {
                val usuario = repo.obtenerUsuario(user.uid)
                _state.value = AuthState.Success(usuario?.tipo ?: "cliente")
                _usuario.value = usuario
            }
        }
    }

    /**
     * Obtiene el perfil de un usuario específico por su identificador.
     *
     * @param uid El ID de usuario en Firebase.
     */
    fun cargarPerfil(uid: String) {
        viewModelScope.launch {
            _usuario.value = repo.obtenerUsuario(uid)
        }
    }

    /**
     * Carga todos los usuarios con el rol "mecanico" y los asigna al flujo [mecanicos].
     * Útil para mostrarlos en el mapa o en listados para los clientes.
     */
    fun cargarMecanicos() {
        viewModelScope.launch {
            _mecanicos.value = repo.obtenerMecanicos()
        }
    }

    /**
     * Actualiza la información básica del perfil del usuario (nombre y teléfono).
     *
     * @param uid Identificador del usuario.
     * @param nombre Nuevo nombre a guardar.
     * @param telefono Nuevo teléfono a guardar.
     */
    fun actualizarPerfil(uid: String, nombre: String, telefono: String) {
        viewModelScope.launch {
            val result = repo.actualizarPerfil(uid, nombre, telefono)
            if (result.isSuccess) {
                // Actualizar el estado local sin recargar de Firebase
                _usuario.value = _usuario.value?.copy(nombre = nombre, telefono = telefono)
            }
        }
    }

    /**
     * Actualiza la información completa del perfil para un usuario que es mecánico.
     *
     * @param uid Identificador del usuario.
     * @param nombre Nuevo nombre a guardar.
     * @param telefono Nuevo teléfono.
     * @param nombreTaller Nombre del negocio/taller.
     * @param descripcionTaller Breve descripción del taller y horarios.
     * @param especialidad La especialidad principal de reparación.
     */
    fun actualizarPerfilMecanico(
        uid: String, nombre: String, telefono: String,
        nombreTaller: String, descripcionTaller: String, especialidad: String
    ) {
        viewModelScope.launch {
            val result = repo.actualizarPerfilMecanico(uid, nombre, telefono, nombreTaller, descripcionTaller, especialidad)
            if (result.isSuccess) {
                _usuario.value = _usuario.value?.copy(
                    nombre = nombre,
                    telefono = telefono,
                    nombreTaller = nombreTaller,
                    descripcionTaller = descripcionTaller,
                    especialidad = especialidad
                )
            }
        }
    }

    /**
     * Guarda la nueva posición geográfica (latitud/longitud) del usuario en Firestore.
     * 
     * @param uid Identificador del usuario.
     * @param latitud Coordenada de latitud.
     * @param longitud Coordenada de longitud.
     */
    fun actualizarUbicacion(uid: String, latitud: Double, longitud: Double) {
        viewModelScope.launch {
            val result = repo.actualizarUbicacion(uid, latitud, longitud)
            if (result.isSuccess) {
                _usuario.value = _usuario.value?.copy(latitud = latitud, longitud = longitud)
            }
        }
    }

    /**
     * Inicia sesión del usuario utilizando su correo y contraseña.
     * El estado ([state]) cambiará a [AuthState.Loading] y luego a Success o Error dependiendo del resultado.
     *
     * @param email Correo electrónico registrado.
     * @param password Contraseña de la cuenta.
     */
    fun login(email: String, password: String) {
        viewModelScope.launch {
            _state.value = AuthState.Loading
            val result = repo.login(email, password)
            _state.value = result.fold(
                onSuccess = { tipo -> AuthState.Success(tipo) },
                onFailure = { e -> AuthState.Error(e.message ?: "Error al iniciar sesión") }
            )
        }
    }

    /**
     * Registra un nuevo usuario en la plataforma.
     * Transiciona el estado ([state]) a lo largo del proceso.
     *
     * @param email Correo para la nueva cuenta.
     * @param password Contraseña.
     * @param nombre Nombre completo real.
     * @param telefono Número de contacto.
     * @param tipo "cliente" o "mecanico" según se elija.
     */
    fun registrar(email: String, password: String, nombre: String, telefono: String, tipo: String) {
        viewModelScope.launch {
            _state.value = AuthState.Loading
            val result = repo.registrar(email, password, nombre, telefono, tipo)
            _state.value = result.fold(
                onSuccess = { usuario -> AuthState.Success(usuario.tipo) },
                onFailure = { e -> AuthState.Error(e.message ?: "Error al registrarse") }
            )
        }
    }

    /**
     * Cierra la sesión activa del usuario y resetea los estados del ViewModel 
     * a valores por defecto ([AuthState.Idle]).
     */
    fun logout() {
        repo.logout()
        _state.value = AuthState.Idle
        _usuario.value = null
    }
}
