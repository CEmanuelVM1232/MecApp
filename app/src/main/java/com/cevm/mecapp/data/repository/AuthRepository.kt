package com.cevm.mecapp.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.cevm.mecapp.data.model.Usuario
import kotlinx.coroutines.tasks.await

/**
 * Repositorio encargado de gestionar la autenticación y los perfiles de usuario
 * utilizando Firebase Authentication y Firestore.
 */
class AuthRepository {
    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    /** Obtiene el usuario actualmente autenticado, si lo hay. */
    val currentUser get() = auth.currentUser

    /**
     * Registra un nuevo usuario en Firebase Auth y guarda sus datos en Firestore.
     * 
     * @param email Correo electrónico del usuario.
     * @param password Contraseña del usuario.
     * @param nombre Nombre completo del usuario.
     * @param telefono Número de teléfono.
     * @param tipo Tipo de usuario ("cliente" o "mecanico").
     * @return Result que contiene el objeto Usuario si es exitoso, o el error en caso contrario.
     */
    suspend fun registrar(
        email: String,
        password: String,
        nombre: String,
        telefono: String,
        tipo: String
    ): Result<Usuario> {
        return try {
            val result = auth.createUserWithEmailAndPassword(email, password).await()
            val usuario = Usuario(
                uid = result.user!!.uid,
                nombre = nombre,
                email = email,
                telefono = telefono,
                tipo = tipo
            )
            db.collection("usuarios").document(usuario.uid).set(usuario).await()
            Result.success(usuario)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Inicia sesión con credenciales existentes en Firebase Auth.
     * 
     * @param email Correo electrónico.
     * @param password Contraseña.
     * @return Result con el tipo de usuario ("cliente" o "mecanico") si fue exitoso, o un error.
     */
    suspend fun login(email: String, password: String): Result<String> {
        return try {
            auth.signInWithEmailAndPassword(email, password).await()
            val uid = auth.currentUser!!.uid
            val snap = db.collection("usuarios").document(uid).get().await()
            val tipo = snap.getString("tipo") ?: "cliente"
            Result.success(tipo)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Obtiene el perfil completo de un usuario desde Firestore dado su UID.
     *
     * @param uid Identificador único del usuario.
     * @return El objeto [Usuario] si se encuentra y mapea correctamente, null en caso de error o no encontrado.
     */
    suspend fun obtenerUsuario(uid: String): Usuario? {
        return try {
            val snap = db.collection("usuarios").document(uid).get().await()
            snap.toObject(Usuario::class.java)
        } catch (e: Exception) { null }
    }

    /**
     * Obtiene una lista de todos los usuarios registrados con el rol "mecanico".
     *
     * @return Lista de [Usuario] que son mecánicos. Lista vacía si hay un error.
     */
    suspend fun obtenerMecanicos(): List<Usuario> {
        return try {
            val snapshot = db.collection("usuarios")
                .whereEqualTo("tipo", "mecanico")
                .get()
                .await()
            snapshot.toObjects(Usuario::class.java)
        } catch (e: Exception) { emptyList() }
    }

    /** Cierra la sesión activa en el dispositivo. */
    fun logout() = auth.signOut()

    /**
     * Actualiza el perfil básico de un usuario (generalmente cliente) en Firestore.
     *
     * @param uid Identificador del usuario.
     * @param nombre Nuevo nombre.
     * @param telefono Nuevo teléfono.
     */
    suspend fun actualizarPerfil(uid: String, nombre: String, telefono: String): Result<Unit> {
        return try {
            val campos = mapOf("nombre" to nombre, "telefono" to telefono)
            db.collection("usuarios").document(uid).update(campos).await()
            Result.success(Unit)
        } catch (e: Exception) { Result.failure(e) }
    }

    /**
     * Actualiza el perfil de un mecánico, incluyendo la información ampliada de su taller.
     *
     * @param uid Identificador único del mecánico.
     * @param nombre Nombre del mecánico.
     * @param telefono Teléfono de contacto.
     * @param nombreTaller Nombre de su taller.
     * @param descripcionTaller Breve descripción de los servicios.
     * @param especialidad Especialidad principal del taller.
     */
    suspend fun actualizarPerfilMecanico(
        uid: String, nombre: String, telefono: String,
        nombreTaller: String, descripcionTaller: String, especialidad: String
    ): Result<Unit> {
        return try {
            val campos = mapOf(
                "nombre" to nombre,
                "telefono" to telefono,
                "nombreTaller" to nombreTaller,
                "descripcionTaller" to descripcionTaller,
                "especialidad" to especialidad
            )
            db.collection("usuarios").document(uid).update(campos).await()
            Result.success(Unit)
        } catch (e: Exception) { Result.failure(e) }
    }

    /**
     * Actualiza las coordenadas geográficas de un mecánico (o taller) en Firestore.
     *
     * @param uid Identificador único del usuario.
     * @param latitud Nueva latitud.
     * @param longitud Nueva longitud.
     */
    suspend fun actualizarUbicacion(uid: String, latitud: Double, longitud: Double): Result<Unit> {
        return try {
            val campos = mapOf("latitud" to latitud, "longitud" to longitud)
            db.collection("usuarios").document(uid).update(campos).await()
            Result.success(Unit)
        } catch (e: Exception) { Result.failure(e) }
    }
}
