# Taller Mecánico App

Aplicación Android para la gestión de órdenes de servicio en talleres mecánicos.

Permite:

Registro e inicio de sesión

Gestión de clientes y mecánicos

Registro de vehículos

Seguimiento del estado de reparación

Subida de fotos y firmas

Sincronización en tiempo real con Firebase

📦 Arquitectura del Proyecto
<img width="461" height="297" alt="image" src="https://github.com/user-attachments/assets/8c363d44-abf2-42a0-81ed-752a0a7f72a8" />


Crear la carpeta:

data/model/
📄 Usuario.kt
package com.tuempresa.tallermecanicoapp.data.model

data class Usuario(
    val uid: String = "",
    val nombre: String = "",
    val email: String = "",
    val telefono: String = "",
    val tipo: String = "cliente", // cliente o mecanico
    val createdAt: Long = System.currentTimeMillis()
)
📄 OrdenServicio.kt
package com.tuempresa.tallermecanicoapp.data.model

data class OrdenServicio(
    val id: String = "",
    val clienteUid: String = "",
    val clienteNombre: String = "",
    val mecanicoUid: String = "",

    // Vehículo
    val marca: String = "",
    val modelo: String = "",
    val anio: Int = 0,
    val placa: String = "",
    val color: String = "",
    val kmEntrada: Int = 0,

    // Entrada
    val fechaEntrada: Long = System.currentTimeMillis(),
    val descripcionFalla: String = "",
    val notasEntrada: String = "",
    val fotosEntrada: List<String> = emptyList(),

    // Servicio
    val trabajosRealizados: List<String> = emptyList(),
    val refacciones: List<String> = emptyList(),
    val costoTotal: Double = 0.0,

    // Estados
    // recibido | revision | reparacion | listo | entregado
    val estado: String = "recibido",

    // Documentos
    val urlPdfContrato: String = "",
    val urlFirmaFisica: String = "",

    // Salida
    val fechaSalida: Long? = null,
    val kmSalida: Int? = null,
    val notasSalida: String = ""
)
🔐 PARTE 2 — Repositorios (Firebase)

Crear la carpeta:

data/repository/
📄 AuthRepository.kt

Encargado de manejar autenticación y usuarios usando Firebase Authentication y Cloud Firestore.

package com.tuempresa.tallermecanicoapp.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.tuempresa.tallermecanicoapp.data.model.Usuario
import kotlinx.coroutines.tasks.await

class AuthRepository {

    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    val currentUser get() = auth.currentUser

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

            db.collection("usuarios")
                .document(usuario.uid)
                .set(usuario)
                .await()

            Result.success(usuario)

        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun login(email: String, password: String): Result<String> {

        return try {

            auth.signInWithEmailAndPassword(email, password).await()

            val uid = auth.currentUser!!.uid

            val snap = db.collection("usuarios")
                .document(uid)
                .get()
                .await()

            val tipo = snap.getString("tipo") ?: "cliente"

            Result.success(tipo)

        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun obtenerUsuario(uid: String): Usuario? {
        return try {
            val snap = db.collection("usuarios").document(uid).get().await()
            snap.toObject(Usuario::class.java)
        } catch (e: Exception) {
            null
        }
    }

    fun logout() = auth.signOut()
}
📄 OrdenRepository.kt

Encargado de manejar las órdenes de servicio.

Utiliza:

Cloud Firestore

Firebase Storage

class OrdenRepository {

    private val db = FirebaseFirestore.getInstance()
    private val storage = FirebaseStorage.getInstance()
    private val col = db.collection("ordenes")

    suspend fun crearOrden(orden: OrdenServicio): Result<String> {
        return try {

            val ref = col.document()

            val conId = orden.copy(id = ref.id)

            ref.set(conId).await()

            Result.success(ref.id)

        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
🧠 PARTE 3 — ViewModels

Los ViewModels conectan la UI con los repositorios.

Se ubican en:

ui/
📄 AuthViewModel.kt

Gestiona:

login

registro

sesión activa

logout

Utiliza StateFlow para comunicar estados a la interfaz.

sealed class AuthState {

    object Idle : AuthState()

    object Loading : AuthState()

    data class Success(val tipo: String) : AuthState()

    data class Error(val mensaje: String) : AuthState()
}
🧭 PARTE 4 — Navegación

Se utiliza Jetpack Navigation Compose.

Archivo:

Navigation.kt

Define todas las pantallas del sistema:

Login
Registro
ClienteHome
ClienteDetalle
MecanicoHome
NuevaOrden
EditarOrden
🎨 PARTE 5 — Tema y Componentes

Ubicación:

ui/theme

Define colores globales para la app.

Ejemplo:

val Azul700 = Color(0xFF1565C0)
val Verde700 = Color(0xFF2E7D32)
val Naranja700 = Color(0xFFE65100)
🧱 Componentes Reutilizables

Carpeta:

ui/components/
EstadoBadge.kt

Componente visual que muestra el estado del vehículo.

Estados disponibles:

recibido
revision
reparacion
listo
entregado

Ejemplo:

[Recibido]
[En revisión]
[Reparación]
[Listo]
[Entregado]
🔑 PARTE 6 — Pantallas de Autenticación

Carpeta:

ui/auth/

Pantallas incluidas:

LoginScreen

RegistroScreen

Funciones:

iniciar sesión

crear cuenta

seleccionar tipo de usuario

cliente

mecánico

🚗 PARTE 7 — Pantallas del Cliente

Carpeta:

ui/cliente/

Pantallas:

ClienteHomeScreen

Lista de vehículos registrados por el cliente.

ClienteDetalleScreen

Muestra:

estado del servicio

información del vehículo

falla reportada

trabajos realizados

firma del documento

notas de entrega

🔧 PARTE 8 — Pantallas del Mecánico

Carpeta:

ui/mecanico/

Pantallas:

MecanicoHomeScreen

Panel principal del taller.

Permite:

ver órdenes

editar órdenes

crear nuevas órdenes

NuevaOrdenScreen

Registro de vehículo:

datos del cliente

datos del vehículo

falla reportada

EditarOrdenScreen

Permite:

actualizar estado

agregar trabajos realizados

subir fotos

capturar firma

🚀 Tecnologías utilizadas

Kotlin

Android Studio

Jetpack Compose

Firebase

Cloud Firestore

Firebase Authentication

Firebase Storage
