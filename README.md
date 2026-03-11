# MecApp - Taller Mecánico App

MecApp es una aplicación nativa para Android desarrollada con Kotlin y Jetpack Compose. Está diseñada para digitalizar la gestión de un taller mecánico, ofreciendo dos roles principales: **Cliente** y **Mecánico**.

## 🚀 Características Principales

*   **Autenticación y Roles:** Registro e inicio de sesión seguro usando Firebase Authentication, diferenciando entre cuentas de clientes y mecánicos.
*   **Gestión de Órdenes de Servicio:** Los mecánicos pueden registrar la entrada de vehículos, detallar fallas iniciales, y actualizar el estado de la reparación (Recibido, Revisión, Reparación, Listo, Entregado).
*   **Calculadora de Presupuestos:** Permite desglosar el costo de reparaciones incluyendo ítems (servicios/repuestos), mano de obra e impuestos, calculando el gran total automáticamente.
*   **Generación de Contratos PDF:** Genera automáticamente un documento PDF con los detalles del servicio y el presupuesto para el acuerdo físico.
*   **Captura de Firma Física:** Integración con la cámara para tomar una fotografía del contrato físico firmado y subirlo a la nube.
*   **Solicitudes de Revisión:** Los clientes pueden enviar solicitudes de diagnóstico a los mecánicos, indicando el nivel de urgencia.
*   **Geolocalización (Google Maps):** Los mecánicos pueden establecer la ubicación de su taller en el mapa, y los clientes pueden usar un mapa interactivo para encontrar talleres cercanos.
*   **Interfaz Moderna:** Desarrollada 100% con Jetpack Compose y Material Design 3.

---

## 🛠️ Tecnologías Utilizadas

*   **Lenguaje:** [Kotlin](https://kotlinlang.org/)
*   **UI Toolkit:** [Jetpack Compose](https://developer.android.com/jetpack/compose)
*   **Arquitectura:** MVVM (Model-View-ViewModel) con StateFlow.
*   **Navegación:** Compose Navigation (`androidx.navigation.compose`).
*   **Backend como Servicio (BaaS):**
    *   **Firebase Authentication:** Login por Email/Contraseña.
    *   **Cloud Firestore:** Base de datos NoSQL en tiempo real para usuarios, órdenes y solicitudes.
    *   **Firebase Cloud Storage:** Almacenamiento seguro de fotografías de firmas de contratos.
*   **Mapas:** Google Maps SDK para Android (`com.google.maps.android:maps-compose`).
*   **PDF:** iText7 (Core y Layout) para la generación dinámica de documentos.

---

## 📋 Guía Paso a Paso para Construir este Proyecto

Si deseas replicar este proyecto desde cero o entender cómo está estructurado, sigue estos pasos lógicos:

### Paso 1: Configuración Inicial del Proyecto
1.  Abre Android Studio y crea un nuevo proyecto seleccionando **"Empty Activity"** (que por defecto usa Jetpack Compose en versiones recientes).
2.  Nombra el proyecto como `MecApp`, usa Kotlin como lenguaje.
3.  Configura el `build.gradle.kts` (app) para incluir las dependencias necesarias:
    *   Navegación (`androidx.navigation:navigation-compose`)
    *   Corrutinas y ViewModel (`androidx.lifecycle:lifecycle-viewmodel-compose`)
    *   Material Design 3 (viene por defecto).

### Paso 2: Integración con Firebase
1.  Ve a la [Consola de Firebase](https://console.firebase.google.com/) y crea un nuevo proyecto.
2.  Añade una aplicación Android registrando el ID de paquete (ej. `com.cevm.mecapp`).
3.  Descarga el archivo `google-services.json` y colócalo dentro de la carpeta `app/` de tu proyecto.
4.  Agrega los plugins de Google Services en tus archivos `build.gradle.kts`.
5.  Añade las dependencias de Firebase (BoM, Auth, Firestore, Storage) al `build.gradle.kts` (app).
6.  En la consola de Firebase, activa: Auth (Email/Contraseña), Firestore Database (Configura las reglas de seguridad apropiadas) y Storage.

### Paso 3: Capa de Datos (Modelos y Repositorios)
1.  **Modelos de Datos (`data/model/`):**
    *   Crea `Usuario.kt` (para guardar tipo de usuario, datos personales y del taller).
    *   Crea `OrdenServicio.kt` y `PresupuestoItem.kt` (info del vehículo, estados, fallas y lista de cobranza).
    *   Crea `SolicitudRevision.kt` (peticiones de los clientes).
2.  **Repositorios (`data/repository/`):**
    *   `AuthRepository.kt`: Lógica para interactuar con Firebase Auth y guardar el `Usuario` en la colección "usuarios" de Firestore.
    *   `OrdenRepository.kt`: Operaciones CRUD sobre la colección "ordenes" y subida de imágenes a Firebase Storage.
    *   `SolicitudRepository.kt`: Operaciones CRUD sobre la colección "solicitudes".

### Paso 4: Capa de Lógica de Presentación (ViewModels)
1.  Crea `AuthViewModel.kt` para manejar el estado de la sesión, login, registro y gestionar perfiles de usuarios.
2.  Crea `OrdenViewModel.kt` para orquestar la creación de órdenes, edición, cambio de estados, subida de firmas y carga de listas (para cliente y mecánico).
3.  Crea `SolicitudViewModel.kt` para administrar los flujos de enviar y responder (Aceptar/Rechazar) solicitudes de revisión.

### Paso 5: Construcción de la Interfaz de Usuario (UI con Compose)
1.  **Navegación Core:** Define las rutas en `Navigation.kt` y configura el `NavHost`.
2.  **Módulo de Autenticación (`ui/auth/`):** Crea `LoginScreen.kt` y `RegistroScreen.kt`.
3.  **Módulo del Mecánico (`ui/mecanico/`):**
    *   `MecanicoHomeScreen.kt`: Pestañas inferiores, vista rápida de órdenes en curso y solicitudes pendientes.
    *   `NuevaOrdenScreen.kt`: Formulario para registrar la entrada física de un vehículo al taller.
    *   `EditarOrdenScreen.kt`: Panel de control de la orden. Aquí está el núcleo de la App: Cambio de estados, Calculadora de Subtotal/Impuestos, Generación de PDF y Cámara para firmas.
    *   `UbicacionTallerScreen.kt`: Mapa interactivo para que el mecánico guarde sus coordenadas exactas en Firestore.
4.  **Módulo del Cliente (`ui/cliente/`):**
    *   `ClienteHomeScreen.kt`: Vista general del histórico de sus vehículos y opción para enviar nuevas solicitudes.
    *   `DetalleOrdenScreen.kt`: Visión de "solo lectura" del presupuesto, trabajos realizados y estado del coche.
    *   `NuevaSolicitudScreen.kt`: Formulario para reportar averías al mecánico de forma remota.
    *   `MapaTalleresScreen.kt`: Pantalla con Google Maps (usando permisos de ubicación del dispositivo) para cargar los talleres marcados en Firestore.

### Paso 6: Utilidades (PDF y Permisos)
1.  **Google Maps:** Obtén una API Key desde Google Cloud Console y añádela a tu `AndroidManifest.xml` como meta-data (`com.google.android.geo.API_KEY`).
2.  **iText PDF:** Crea un objeto `PdfGenerator.kt` en el paquete `utils/`. Utiliza las dependencias de iText7 para diseñar un layout de tabla dinámica en PDF, extrayendo datos del objeto `OrdenServicio`.
3.  **Cámara y Storage:** Configura un `FileProvider` en el `AndroidManifest.xml` y la carpeta `xml/file_paths.xml` para poder tomar fotos de alta resolución con la cámara del dispositivo y pasárselas a Firebase Storage.

---

## ⚙️ Cómo ejecutar este proyecto

1.  Clona el repositorio: `git clone <URL_DEL_REPO>`
2.  Abre el proyecto en **Android Studio** (Koala o superior recomendado).
3.  Asegúrate de agregar tu propio archivo `google-services.json` (descargado desde tu cuenta de Firebase) en la carpeta `app/`.
4.  Agrega tu API Key de Google Maps en el `AndroidManifest.xml`.
5.  Sincroniza los archivos de Gradle.
6.  Ejecuta la app en un emulador o dispositivo físico con Android 8.0 (API 26) o superior.

## 👥 Contribuciones

[Elaborar políticas de contribución si se desea hacer el proyecto de código abierto]

## 📜 Licencia

[Especificar licencia del proyecto, por ejemplo: MIT License]


## ðŸ’» CÃ³digo Fuente de la AplicaciÃ³n

A continuaciÃ³n se presenta el cÃ³digo de todos los archivos .kt del proyecto ordenados:

### MainActivity.kt
*(UbicaciÃ³n: \$relativePath\)*

```kotlin
package com.cevm.mecapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.cevm.mecapp.ui.theme.MecAppTheme


/**
 * Actividad principal de la aplicación.
 * Sirve como punto de entrada y contenedor para la navegación de Jetpack Compose.
 */
class MainActivity : ComponentActivity() {
    /**
     * Se llama cuando la actividad está iniciando.
     * Configura el contenido de Compose y maneja la navegación inicial.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MecAppTheme {
                AppNavigation()            }
        }
    }
}
```

### Navigation.kt
*(UbicaciÃ³n: \$relativePath\)*

```kotlin
package com.cevm.mecapp


import androidx.compose.runtime.*
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import com.cevm.mecapp.ui.AuthViewModel
import com.cevm.mecapp.ui.OrdenViewModel
import com.cevm.mecapp.ui.SolicitudViewModel
import com.cevm.mecapp.ui.auth.LoginScreen
import com.cevm.mecapp.ui.auth.RegistroScreen
import com.cevm.mecapp.ui.cliente.ClienteHomeScreen
import com.cevm.mecapp.ui.cliente.ClienteDetalleScreen
import com.cevm.mecapp.ui.cliente.NuevaSolicitudScreen
import com.cevm.mecapp.ui.cliente.MapaTalleresScreen
import com.cevm.mecapp.ui.mecanico.MecanicoHomeScreen
import com.cevm.mecapp.ui.mecanico.NuevaOrdenScreen
import com.cevm.mecapp.ui.mecanico.EditarOrdenScreen
import com.cevm.mecapp.ui.mecanico.UbicacionTallerScreen

/**
 * Define las rutas de navegación disponibles en la aplicación.
 *
 * @property ruta La cadena de texto que representa la ruta de destino.
 */
sealed class Pantalla(val ruta: String) {
    object Login : Pantalla("login")
    object Registro : Pantalla("registro")
    object ClienteHome : Pantalla("cliente_home")
    object ClienteDetalle : Pantalla("cliente_detalle/{ordenId}") {
        /** Construye la ruta para un detalle específico de cliente. */
        fun conId(id: String) = "cliente_detalle/$id"
    }
    object MapaTalleres : Pantalla("mapa_talleres")
    object MecanicoHome : Pantalla("mecanico_home")
    object UbicacionTaller : Pantalla("ubicacion_taller")
    object NuevaOrden : Pantalla("nueva_orden")
    object NuevaSolicitud : Pantalla("nueva_solicitud")
    object EditarOrden : Pantalla("editar_orden/{ordenId}") {
        /** Construye la ruta para editar una orden específica de mecánico. */
        fun conId(id: String) = "editar_orden/$id"
    }
}

/**
 * Componente principal responsable de gestionar la navegación en la aplicación.
 * Configura el NavHost y define qué Composable mostrar para cada ruta de [Pantalla].
 */
@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val authViewModel: AuthViewModel = viewModel()
    val ordenViewModel: OrdenViewModel = viewModel()
    val solicitudViewModel: SolicitudViewModel = viewModel()

    // Verificar sesión al iniciar
    LaunchedEffect(Unit) { authViewModel.verificarSesion() }

    NavHost(navController = navController, startDestination = Pantalla.Login.ruta) {

        composable(Pantalla.Login.ruta) {
            LoginScreen(
                viewModel = authViewModel,
                onLoginExitoso = { tipo ->
                    val destino = if (tipo == "mecanico") Pantalla.MecanicoHome.ruta
                    else Pantalla.ClienteHome.ruta
                    navController.navigate(destino) {
                        popUpTo(Pantalla.Login.ruta) { inclusive = true }
                    }
                },
                onIrARegistro = { navController.navigate(Pantalla.Registro.ruta) }
            )
        }

        composable(Pantalla.Registro.ruta) {
            RegistroScreen(
                viewModel = authViewModel,
                onRegistroExitoso = { tipo ->
                    val destino = if (tipo == "mecanico") Pantalla.MecanicoHome.ruta
                    else Pantalla.ClienteHome.ruta
                    navController.navigate(destino) {
                        popUpTo(Pantalla.Login.ruta) { inclusive = true }
                    }
                },
                onVolver = { navController.popBackStack() }
            )
        }

        composable(Pantalla.ClienteHome.ruta) {
            ClienteHomeScreen(
                viewModel = ordenViewModel,
                authViewModel = authViewModel,
                solicitudViewModel = solicitudViewModel,
                onVerDetalle = { id -> navController.navigate(Pantalla.ClienteDetalle.conId(id)) },
                onNuevaSolicitud = { navController.navigate(Pantalla.NuevaSolicitud.ruta) },
                onBuscarTalleres = { navController.navigate(Pantalla.MapaTalleres.ruta) },
                onLogout = { authViewModel.logout(); navController.navigate(Pantalla.Login.ruta) { popUpTo(0) } }
            )
        }

        composable(
            Pantalla.ClienteDetalle.ruta,
            arguments = listOf(navArgument("ordenId") { type = NavType.StringType })
        ) { backStack ->
            val id = backStack.arguments?.getString("ordenId") ?: ""
            ClienteDetalleScreen(
                ordenId = id,
                viewModel = ordenViewModel,
                onVolver = { navController.popBackStack() }
            )
        }

        composable(Pantalla.NuevaSolicitud.ruta) {
            NuevaSolicitudScreen(
                solicitudViewModel = solicitudViewModel,
                authViewModel = authViewModel,
                onVolver = { navController.popBackStack() }
            )
        }

        composable(Pantalla.MapaTalleres.ruta) {
            MapaTalleresScreen(
                authViewModel = authViewModel,
                onVolver = { navController.popBackStack() }
            )
        }

        composable(Pantalla.MecanicoHome.ruta) {
            MecanicoHomeScreen(
                viewModel = ordenViewModel,
                authViewModel = authViewModel,
                solicitudViewModel = solicitudViewModel,
                onNuevaOrden = { navController.navigate(Pantalla.NuevaOrden.ruta) },
                onEditarOrden = { id -> navController.navigate(Pantalla.EditarOrden.conId(id)) },
                onConfigurarTaller = { navController.navigate(Pantalla.UbicacionTaller.ruta) },
                onLogout = { authViewModel.logout(); navController.navigate(Pantalla.Login.ruta) { popUpTo(0) } }
            )
        }

        composable(Pantalla.UbicacionTaller.ruta) {
            UbicacionTallerScreen(
                authViewModel = authViewModel,
                onVolver = { navController.popBackStack() }
            )
        }

        composable(Pantalla.NuevaOrden.ruta) {
            NuevaOrdenScreen(
                viewModel = ordenViewModel,
                onVolver = { navController.popBackStack() }
            )
        }

        composable(
            Pantalla.EditarOrden.ruta,
            arguments = listOf(navArgument("ordenId") { type = NavType.StringType })
        ) { backStack ->
            val id = backStack.arguments?.getString("ordenId") ?: ""
            EditarOrdenScreen(
                ordenId = id,
                viewModel = ordenViewModel,
                authViewModel = authViewModel,
                onVolver = { navController.popBackStack() }
            )
        }
    }
}

```

### OrdenServicio.kt
*(UbicaciÃ³n: \$relativePath\)*

```kotlin
package com.cevm.mecapp.data.model

import java.util.UUID

/**
 * Representa un ítem dentro del presupuesto de una orden.
 */
data class PresupuestoItem(
    val id: String = UUID.randomUUID().toString(),
    val descripcion: String = "",
    val cantidad: Int = 1,
    val precioUnitario: Double = 0.0
)
/**
 * Representa una orden de servicio (reparación o mantenimiento) en el taller.
 * Contiene información sobre el cliente, el mecánico, los datos del vehículo,
 * el estado actual del servicio, y los costos y detalles asociados.
 */
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
    // Presupuesto
    val presupuestoItems: List<PresupuestoItem> = emptyList(),
    val costoManoObra: Double = 0.0,
    val costoRepuestos: Double = 0.0,
    val notasPresupuesto: String = "",
    val costoTotal: Double = 0.0,
    // Estado: recibido | revision | reparacion | listo | entregado
    val estado: String = "recibido",
    // Documentos
    val urlPdfContrato: String = "",
    val urlFirmaFisica: String = "",
    // Salida
    val fechaSalida: Long? = null,
    val kmSalida: Int? = null,
    val notasSalida: String = ""
)

```

### SolicitudRevision.kt
*(UbicaciÃ³n: \$relativePath\)*

```kotlin
package com.cevm.mecapp.data.model

/**
 * Representa una solicitud de revisión de un vehículo creada por un cliente.
 * El mecánico puede aceptar o rechazar esta solicitud antes de generar una orden de servicio.
 */
data class SolicitudRevision(
    val id: String = "",
    val clienteUid: String = "",
    val clienteNombre: String = "",
    val clienteEmail: String = "",
    // Vehículo
    val marcaVehiculo: String = "",
    val modeloVehiculo: String = "",
    val anioVehiculo: String = "",
    val placaVehiculo: String = "",
    // Solicitud
    val descripcionProblema: String = "",
    val urgencia: String = "normal",   // "normal" | "urgente"
    // Estado: "pendiente" | "aceptada" | "rechazada"
    val estado: String = "pendiente",
    val fechaSolicitud: Long = System.currentTimeMillis()
)

```

### Usuario.kt
*(UbicaciÃ³n: \$relativePath\)*

```kotlin
package com.cevm.mecapp.data.model


/**
 * Representa un usuario en el sistema.
 * Puede ser de tipo "cliente" o "mecanico". Los mecánicos tienen campos extra para
 * detalles de taller y ubicación.
 */
data class Usuario(
    val uid: String = "",
    val nombre: String = "",
    val email: String = "",
    val telefono: String = "",
    val tipo: String = "cliente",  // "cliente" o "mecanico"
    // Campos específicos para mecánicos (Google Maps y Perfil de Taller)
    val nombreTaller: String = "",
    val latitud: Double? = null,
    val longitud: Double? = null,
    val descripcionTaller: String = "",
    val especialidad: String = "",
    val puntuacion: Double = 0.0,
    val createdAt: Long = System.currentTimeMillis()
)

```

### AuthRepository.kt
*(UbicaciÃ³n: \$relativePath\)*

```kotlin
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

```

### OrdenRepository.kt
*(UbicaciÃ³n: \$relativePath\)*

```kotlin
package com.cevm.mecapp.data.repository

import android.net.Uri
import com.cevm.mecapp.data.model.OrdenServicio
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

/**
 * Repositorio que gestiona las operaciones CRUD para [OrdenServicio] en Firestore y
 * el almacenamiento de archivos (como firmas) en Firebase Storage.
 */
class OrdenRepository {
    private val db = FirebaseFirestore.getInstance()
    private val storage = FirebaseStorage.getInstance()
    private val col = db.collection("ordenes")

    /**
     * Crea una nueva orden de servicio en Firestore con un ID generado automáticamente.
     *
     * @param orden La [OrdenServicio] a guardar.
     * @return Result que contiene el ID generado para la nueva orden o el error respectivo.
     */
    suspend fun crearOrden(orden: OrdenServicio): Result<String> {
        return try {
            val ref = col.document()
            val conId = orden.copy(id = ref.id)
            ref.set(conId).await()
            Result.success(ref.id)
        } catch (e: Exception) { Result.failure(e) }
    }

    /**
     * Actualiza campos específicos de una orden de servicio existente.
     *
     * @param id ID de la orden en Firestore.
     * @param campos Mapa con los nombres de los campos a actualizar y sus nuevos valores.
     */
    suspend fun actualizar(id: String, campos: Map<String, Any?>): Result<Unit> {
        return try {
            col.document(id).update(campos).await()
            Result.success(Unit)
        } catch (e: Exception) { Result.failure(e) }
    }

    /**
     * Elimina definitivamente una orden de servicio de Firestore.
     *
     * @param id ID de la orden a eliminar.
     */
    suspend fun eliminarOrden(id: String): Result<Unit> {
        return try {
            col.document(id).delete().await()
            Result.success(Unit)
        } catch (e: Exception) { Result.failure(e) }
    }

    /**
     * Retorna un flujo (Flow) con la lista de órdenes asociadas a un mecánico,
     * escuchando actualizaciones en tiempo real desde Firestore.
     *
     * @param mecanicoUid Identificador único del mecánico.
     */
    fun ordenesDelMecanico(mecanicoUid: String): Flow<List<OrdenServicio>> =
        callbackFlow {
            val listener = col
                .whereEqualTo("mecanicoUid", mecanicoUid)
                .addSnapshotListener { snap, _ ->
                    val lista = snap?.toObjects(OrdenServicio::class.java)
                        ?.sortedByDescending { it.fechaEntrada } ?: emptyList()
                    trySend(lista)
                }
            awaitClose { listener.remove() }
        }

    /**
     * Retorna un flujo (Flow) con la lista de órdenes pertenecientes a un cliente dado,
     * escuchando actualizaciones en tiempo real desde Firestore.
     *
     * @param clienteUid Identificador único del cliente.
     */
    fun ordenesDelCliente(clienteUid: String): Flow<List<OrdenServicio>> =
        callbackFlow {
            val listener = col
                .whereEqualTo("clienteUid", clienteUid)
                .addSnapshotListener { snap, _ ->
                    val lista = snap?.toObjects(OrdenServicio::class.java)
                        ?.sortedByDescending { it.fechaEntrada } ?: emptyList()
                    trySend(lista)
                }
            awaitClose { listener.remove() }
        }

    /**
     * Sube un archivo de imagen (ej. foto, firma) a Firebase Storage.
     *
     * @param uri URI local del archivo.
     * @param path Ruta en Firebase Storage donde se guardará (ej. "firmas/123/firma.jpg").
     * @return Result que contiene la URL de descarga pública si tiene éxito, o error.
     */
    suspend fun subirFoto(uri: Uri, path: String): Result<String> {
        return try {
            val ref = storage.reference.child(path)
            ref.putFile(uri).await()
            val url = ref.downloadUrl.await().toString()
            Result.success(url)
        } catch (e: Exception) { Result.failure(e) }
    }
}

```

### SolicitudRepository.kt
*(UbicaciÃ³n: \$relativePath\)*

```kotlin
package com.cevm.mecapp.data.repository

import com.cevm.mecapp.data.model.SolicitudRevision
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

/**
 * Repositorio de acceso a datos para gestionar las operaciones relacionadas con las
 * solicitudes de revisión ([SolicitudRevision]) en Firestore.
 */
class SolicitudRepository {
    private val db = FirebaseFirestore.getInstance()
    private val col = db.collection("solicitudes")

    /**
     * Crea una nueva solicitud en base de datos.
     *
     * @param solicitud El objeto con la información de la solicitud.
     * @return Result envolviendo el ID asignado por Firestore, o una excepción si falló.
     */
    suspend fun crearSolicitud(solicitud: SolicitudRevision): Result<String> {
        return try {
            val ref = col.document()
            val conId = solicitud.copy(id = ref.id)
            ref.set(conId).await()
            Result.success(ref.id)
        } catch (e: Exception) { Result.failure(e) }
    }

    /**
     * Devuelve un flujo persistente (Flow) de recolección para las solicitudes
     * que están en estado "pendiente" en todo el sistema. 
     * Ideal para mostrar a los mecánicos qué trabajos pueden tomar.
     */
    fun solicitudesPendientes(): Flow<List<SolicitudRevision>> = callbackFlow {
        val listener = col
            .whereEqualTo("estado", "pendiente")
            .addSnapshotListener { snap, _ ->
                val lista = snap?.toObjects(SolicitudRevision::class.java)
                    ?.sortedByDescending { it.fechaSolicitud } ?: emptyList()
                trySend(lista)
            }
        awaitClose { listener.remove() }
    }

    /**
     * Obtiene un flujo de las solicitudes realizadas por un cliente específico
     * sin importar el estado.
     *
     * @param clienteUid Identificador único del cliente.
     */
    fun solicitudesDelCliente(clienteUid: String): Flow<List<SolicitudRevision>> = callbackFlow {
        val listener = col
            .whereEqualTo("clienteUid", clienteUid)
            .addSnapshotListener { snap, _ ->
                val lista = snap?.toObjects(SolicitudRevision::class.java)
                    ?.sortedByDescending { it.fechaSolicitud } ?: emptyList()
                trySend(lista)
            }
        awaitClose { listener.remove() }
    }

    /**
     * Actualiza el campo 'estado' de una solicitud específica.
     * Útil cuando un mecánico acepta o rechaza una solicitud.
     *
     * @param id ID del documento en Firestore.
     * @param nuevoEstado El estado por establecer ("aceptada", "rechazada", etc).
     */
    suspend fun actualizarEstado(id: String, nuevoEstado: String): Result<Unit> {
        return try {
            col.document(id).update("estado", nuevoEstado).await()
            Result.success(Unit)
        } catch (e: Exception) { Result.failure(e) }
    }

    /**
     * Borra permanentemente una solicitud de revisión de la base de datos.
     *
     * @param id Identificador de Firestore de la solicitud.
     */
    suspend fun eliminarSolicitud(id: String): Result<Unit> {
        return try {
            col.document(id).delete().await()
            Result.success(Unit)
        } catch (e: Exception) { Result.failure(e) }
    }
}

```

### AuthViewModel.kt
*(UbicaciÃ³n: \$relativePath\)*

```kotlin
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

```

### OrdenViewModel.kt
*(UbicaciÃ³n: \$relativePath\)*

```kotlin
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

```

### SolicitudViewModel.kt
*(UbicaciÃ³n: \$relativePath\)*

```kotlin
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

```

### LoginScreen.kt
*(UbicaciÃ³n: \$relativePath\)*

```kotlin
package com.cevm.mecapp.ui.auth


import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.*
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

import com.cevm.mecapp.ui.AuthState
import com.cevm.mecapp.ui.AuthViewModel
import com.cevm.mecapp.ui.theme.Azul700

/**
 * Componente visual de la pantalla de inicio de sesión.
 * Permite a los usuarios ingresar su correo y contraseña para autenticarse,
 * u ofrece un enlace en caso de que quieran registrar una nueva cuenta.
 *
 * @param viewModel El [AuthViewModel] que gestiona la lógica de autenticación.
 * @param onLoginExitoso Acción ejecutada cuando el login se aprueba, recibe el `tipo` de usuario.
 * @param onIrARegistro Acción ejecutada cuando el usuario presiona el botón de registro.
 */
@Composable
fun LoginScreen(
    viewModel: AuthViewModel,
    onLoginExitoso: (String) -> Unit,
    onIrARegistro: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var verPassword by remember { mutableStateOf(false) }
    val state by viewModel.state.collectAsState()

    LaunchedEffect(state) {
        if (state is AuthState.Success) onLoginExitoso((state as AuthState.Success).tipo)
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("🔧", fontSize = 64.sp)
        Spacer(Modifier.height(8.dp))
        Text("Taller Mecánico", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = Azul700)
        Text("Inicia sesión para continuar", fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(Modifier.height(32.dp))

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Correo electrónico") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
        )
        Spacer(Modifier.height(12.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Contraseña") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            visualTransformation = if (verPassword) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                IconButton(onClick = { verPassword = !verPassword }) {
                    Icon(if (verPassword) Icons.Default.VisibilityOff else Icons.Default.Visibility, null)
                }
            }
        )
        Spacer(Modifier.height(24.dp))

        if (state is AuthState.Error) {
            Text((state as AuthState.Error).mensaje, color = MaterialTheme.colorScheme.error, fontSize = 14.sp)
            Spacer(Modifier.height(8.dp))
        }

        Button(
            onClick = { viewModel.login(email, password) },
            modifier = Modifier.fillMaxWidth().height(50.dp),
            enabled = state !is AuthState.Loading
        ) {
            if (state is AuthState.Loading) CircularProgressIndicator(Modifier.size(20.dp), color = MaterialTheme.colorScheme.onPrimary)
            else Text("Iniciar Sesión", fontSize = 16.sp)
        }
        Spacer(Modifier.height(16.dp))

        TextButton(onClick = onIrARegistro) {
            Text("¿No tienes cuenta? Regístrate")
        }
    }
}

```

### RegistroScreen.kt
*(UbicaciÃ³n: \$relativePath\)*

```kotlin
package com.cevm.mecapp.ui.auth


import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cevm.mecapp.ui.AuthState
import com.cevm.mecapp.ui.AuthViewModel

/**
 * Componente visual para la pantalla de registro.
 * Permite capturar los datos personales de un usuario nuevo y le permite elegir 
 * si su cuenta será de tipo "cliente" o "mecanico".
 *
 * @param viewModel El [AuthViewModel] que contiene la lógica para la creación de cuentas.
 * @param onRegistroExitoso Lambda que se llama cuando el usuario se crea correctamente, pasando su `tipo`.
 * @param onVolver Lambda para retroceder en la pila de navegación.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegistroScreen(
    viewModel: AuthViewModel,
    onRegistroExitoso: (String) -> Unit,
    onVolver: () -> Unit
) {
    var nombre by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var telefono by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var tipo by remember { mutableStateOf("cliente") }
    val state by viewModel.state.collectAsState()

    LaunchedEffect(state) {
        if (state is AuthState.Success) onRegistroExitoso((state as AuthState.Success).tipo)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Crear cuenta") },
                navigationIcon = { IconButton(onClick = onVolver) { Icon(Icons.Default.ArrowBack, null) } }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding).padding(horizontal = 24.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Spacer(Modifier.height(8.dp))
            Text("Datos personales", fontWeight = FontWeight.Bold, fontSize = 16.sp)

            OutlinedTextField(value = nombre, onValueChange = { nombre = it },
                label = { Text("Nombre completo") }, modifier = Modifier.fillMaxWidth(), singleLine = true)

            OutlinedTextField(value = email, onValueChange = { email = it },
                label = { Text("Correo electrónico") }, modifier = Modifier.fillMaxWidth(), singleLine = true)

            OutlinedTextField(value = telefono, onValueChange = { telefono = it },
                label = { Text("Teléfono") }, modifier = Modifier.fillMaxWidth(), singleLine = true)

            OutlinedTextField(value = password, onValueChange = { password = it },
                label = { Text("Contraseña") }, modifier = Modifier.fillMaxWidth(), singleLine = true)

            Text("Tipo de cuenta", fontWeight = FontWeight.Bold, fontSize = 16.sp)

            Row(verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.selectable(tipo == "cliente") { tipo = "cliente" }) {
                RadioButton(selected = tipo == "cliente", onClick = { tipo = "cliente" })
                Text("Cliente", modifier = Modifier.padding(start = 8.dp))
            }
            Row(verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.selectable(tipo == "mecanico") { tipo = "mecanico" }) {
                RadioButton(selected = tipo == "mecanico", onClick = { tipo = "mecanico" })
                Text("Mecánico / Taller", modifier = Modifier.padding(start = 8.dp))
            }

            if (state is AuthState.Error) {
                Text((state as AuthState.Error).mensaje, color = MaterialTheme.colorScheme.error)
            }

            Button(
                onClick = { viewModel.registrar(email, password, nombre, telefono, tipo) },
                modifier = Modifier.fillMaxWidth().height(50.dp),
                enabled = state !is AuthState.Loading
            ) {
                if (state is AuthState.Loading) CircularProgressIndicator(Modifier.size(20.dp))
                else Text("Crear cuenta", fontSize = 16.sp)
            }
            Spacer(Modifier.height(16.dp))
        }
    }
}

```

### ClienteHomeScreen.kt
*(UbicaciÃ³n: \$relativePath\)*

```kotlin
package com.cevm.mecapp.ui.cliente


import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.auth.FirebaseAuth
import com.cevm.mecapp.data.model.OrdenServicio
import com.cevm.mecapp.data.model.SolicitudRevision
import com.cevm.mecapp.ui.AuthViewModel
import com.cevm.mecapp.ui.OrdenViewModel
import com.cevm.mecapp.ui.SolicitudViewModel
import com.cevm.mecapp.ui.components.EstadoBadge
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Pantalla principal para los usuarios con rol "cliente".
 * Muestra pestañas para ver el estado de sus órdenes de servicio,
 * consultar/eliminar solicitudes de revisión pendientes, y ver/copiar su perfil y UID.
 *
 * @param viewModel ViewModel encargado de cargar y gestionar las [OrdenServicio].
 * @param authViewModel ViewModel que maneja la sesión y el perfil del usuario activo.
 * @param solicitudViewModel ViewModel que gestiona las [SolicitudRevision].
 * @param onVerDetalle Lambda para navegar al detalle de una orden específica.
 * @param onNuevaSolicitud Lambda para ir a la pantalla de crear una nueva solicitud o revisión.
 * @param onBuscarTalleres Lambda para abrir el mapa interactivo de talleres.
 * @param onLogout Lambda para finalizar la sesión del usuario.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClienteHomeScreen(
    viewModel: OrdenViewModel,
    authViewModel: AuthViewModel,
    solicitudViewModel: SolicitudViewModel,
    onVerDetalle: (String) -> Unit,
    onNuevaSolicitud: () -> Unit,
    onBuscarTalleres: () -> Unit,
    onLogout: () -> Unit
) {
    val uid = FirebaseAuth.getInstance().currentUser?.uid ?: ""
    val ordenes by viewModel.ordenes.collectAsState()
    val usuario by authViewModel.usuario.collectAsState()
    val solicitudes by solicitudViewModel.solicitudes.collectAsState()
    val context = LocalContext.current

    var ordenAEliminar by remember { mutableStateOf<String?>(null) }
    var solicitudAEliminar by remember { mutableStateOf<String?>(null) }

    var tabSeleccionado by remember { mutableIntStateOf(0) }
    val tabs = listOf("Mis Órdenes", "Solicitudes", "Mi Perfil")

    LaunchedEffect(uid) {
        if (uid.isNotEmpty()) {
            viewModel.cargarOrdenesDelCliente(uid)
            authViewModel.cargarPerfil(uid)
            solicitudViewModel.cargarDelCliente(uid)
        }
    }

    if (ordenAEliminar != null) {
        AlertDialog(
            onDismissRequest = { ordenAEliminar = null },
            title = { Text("Eliminar Orden") },
            text = { Text("¿Deseas eliminar u ocultar esta orden de servicio? Esta acción no se puede deshacer.") },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.eliminarOrden(ordenAEliminar!!)
                    ordenAEliminar = null
                }) { Text("Eliminar", color = MaterialTheme.colorScheme.error) }
            },
            dismissButton = {
                TextButton(onClick = { ordenAEliminar = null }) { Text("Cancelar") }
            }
        )
    }

    if (solicitudAEliminar != null) {
        AlertDialog(
            onDismissRequest = { solicitudAEliminar = null },
            title = { Text("Cancelar Solicitud") },
            text = { Text("¿Estás seguro de que deseas cancelar y eliminar esta solicitud?") },
            confirmButton = {
                TextButton(onClick = {
                    solicitudViewModel.eliminarSolicitud(solicitudAEliminar!!)
                    solicitudAEliminar = null
                }) { Text("Cancelar Solicitud", color = MaterialTheme.colorScheme.error) }
            },
            dismissButton = {
                TextButton(onClick = { solicitudAEliminar = null }) { Text("Cerrar") }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Taller Mecánico") },
                actions = {
                    IconButton(onClick = onLogout) {
                        Icon(Icons.AutoMirrored.Filled.ExitToApp, "Salir")
                    }
                }
            )
        },
        floatingActionButton = {
            if (tabSeleccionado == 1) {
                ExtendedFloatingActionButton(
                    onClick = onNuevaSolicitud,
                    icon = { Icon(Icons.Default.Add, null) },
                    text = { Text("Nueva solicitud") }
                )
            }
        }
    ) { padding ->
        Column(Modifier.fillMaxSize().padding(padding)) {

            // ── Pestañas ──────────────────────────────────────────────────
            TabRow(selectedTabIndex = tabSeleccionado) {
                tabs.forEachIndexed { index, titulo ->
                    Tab(
                        selected = tabSeleccionado == index,
                        onClick = { tabSeleccionado = index },
                        text = { Text(titulo, maxLines = 1) }
                    )
                }
            }

            when (tabSeleccionado) {

                // ── 0: Mis Órdenes ────────────────────────────────────────
                0 -> {
                    if (ordenes.isEmpty()) {
                        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("🚗", fontSize = 64.sp)
                                Spacer(Modifier.height(8.dp))
                                Text("Sin órdenes activas", fontSize = 18.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                    } else {
                        LazyColumn(
                            Modifier.fillMaxSize().padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(ordenes) { orden -> 
                                OrdenCardCliente(
                                    orden = orden, 
                                    onVerDetalle = onVerDetalle,
                                    onEliminar = { ordenAEliminar = it }
                                ) 
                            }
                        }
                    }
                }

                // ── 1: Solicitudes ────────────────────────────────────────
                1 -> {
                    if (solicitudes.isEmpty()) {
                        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("🔧", fontSize = 64.sp)
                                Spacer(Modifier.height(8.dp))
                                Text("Sin solicitudes", fontSize = 18.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text("Toca + para enviar una solicitud al taller",
                                    fontSize = 14.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                    } else {
                        LazyColumn(
                            Modifier.fillMaxSize().padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            item {
                                OutlinedButton(
                                    onClick = onBuscarTalleres,
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = ButtonDefaults.outlinedButtonColors(
                                        contentColor = MaterialTheme.colorScheme.primary
                                    )
                                ) {
                                    Text("📍 Buscar Talleres Cercanos en el Mapa")
                                }
                            }
                            items(solicitudes) { sol -> 
                                SolicitudCardCliente(
                                    solicitud = sol,
                                    onEliminar = { solicitudAEliminar = it }
                                ) 
                            }
                        }
                    }
                }

                // ── 2: Mi Perfil ──────────────────────────────────────────
                2 -> {
                    if (usuario == null) {
                        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator()
                        }
                    } else {
                        LazyColumn(
                            Modifier.fillMaxSize().padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            item {
                                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                                    Card(
                                        shape = MaterialTheme.shapes.extraLarge,
                                        colors = CardDefaults.cardColors(
                                            containerColor = MaterialTheme.colorScheme.primaryContainer)
                                    ) {
                                        Icon(Icons.Default.Person, null,
                                            Modifier.padding(24.dp).size(56.dp),
                                            tint = MaterialTheme.colorScheme.onPrimaryContainer)
                                    }
                                }
                            }
                            item {
                                Card(Modifier.fillMaxWidth(), elevation = CardDefaults.cardElevation(2.dp)) {
                                    Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                        Text("Datos del perfil", fontWeight = FontWeight.Bold, fontSize = 16.sp,
                                            color = MaterialTheme.colorScheme.primary)
                                        HorizontalDivider()
                                        PerfilFilaCliente("Nombre", usuario!!.nombre)
                                        PerfilFilaCliente("Email", usuario!!.email)
                                        PerfilFilaCliente("Teléfono", usuario!!.telefono.ifEmpty { "—" })
                                        PerfilFilaCliente("Tipo de cuenta",
                                            usuario!!.tipo.replaceFirstChar { it.uppercase() })
                                    }
                                }
                            }
                            item {
                                Card(
                                    Modifier.fillMaxWidth(), elevation = CardDefaults.cardElevation(2.dp),
                                    colors = CardDefaults.cardColors(
                                        containerColor = MaterialTheme.colorScheme.secondaryContainer)
                                ) {
                                    Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                        Text("🔑 Mi UID de Usuario", fontWeight = FontWeight.Bold, fontSize = 15.sp,
                                            color = MaterialTheme.colorScheme.onSecondaryContainer)
                                        Text("Comparte este código con el mecánico para vincular tu vehículo.",
                                            fontSize = 12.sp, color = MaterialTheme.colorScheme.onSecondaryContainer)
                                        Spacer(Modifier.height(4.dp))
                                        Surface(shape = MaterialTheme.shapes.medium,
                                            color = MaterialTheme.colorScheme.surface, modifier = Modifier.fillMaxWidth()) {
                                            Row(Modifier.fillMaxWidth().padding(12.dp),
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                verticalAlignment = Alignment.CenterVertically) {
                                                Text(uid, fontSize = 12.sp, fontWeight = FontWeight.Medium,
                                                    modifier = Modifier.weight(1f))
                                                IconButton(onClick = {
                                                    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                                    clipboard.setPrimaryClip(ClipData.newPlainText("UID", uid))
                                                    Toast.makeText(context, "UID copiado al portapapeles", Toast.LENGTH_SHORT).show()
                                                }) {
                                                    Icon(Icons.Default.ContentCopy, "Copiar UID",
                                                        tint = MaterialTheme.colorScheme.primary)
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

/**
 * Componente reutilizable para mostrar un dato del perfil (ej. Nombre: Juan).
 *
 * @param etiqueta Texto descriptivo alineado a la izquierda.
 * @param valor Texto de la información, en negrita y alineado a la derecha.
 */
@Composable
private fun PerfilFilaCliente(etiqueta: String, valor: String) {
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(etiqueta, fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(valor, fontSize = 14.sp, fontWeight = FontWeight.Medium)
    }
}

/**
 * Tarjeta visual para representar una [SolicitudRevision] dentro de la lista de solicitudes.
 *
 * @param solicitud El objeto con los datos de la solicitud (vehículo, estado, fechas, etc).
 * @param onEliminar Lambda que se invoca si el cliente decide borrar su solicitud pendiente.
 */
@Composable
fun SolicitudCardCliente(solicitud: SolicitudRevision, onEliminar: (String) -> Unit) {
    val fecha = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
        .format(Date(solicitud.fechaSolicitud))
    val (estadoColor, estadoLabel) = when (solicitud.estado) {
        "aceptada"  -> Color(0xFF2E7D32) to "✅ Aceptada"
        "rechazada" -> Color(0xFFC62828) to "❌ Rechazada"
        else        -> Color(0xFFF57C00) to "⏳ Pendiente"
    }
    Card(Modifier.fillMaxWidth(), elevation = CardDefaults.cardElevation(2.dp)) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Row(Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically) {
                Text("${solicitud.marcaVehiculo} ${solicitud.modeloVehiculo}",
                    fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Surface(shape = MaterialTheme.shapes.small,
                    color = estadoColor.copy(alpha = 0.12f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(estadoLabel, fontSize = 12.sp, color = estadoColor,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp))
                        if (solicitud.estado == "pendiente") {
                            IconButton(
                                onClick = { onEliminar(solicitud.id) },
                                modifier = Modifier.size(24.dp).padding(end = 4.dp)
                            ) {
                                Icon(Icons.Default.Delete, contentDescription = "Eliminar", tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(16.dp))
                            }
                        }
                    }
                }
            }
            if (solicitud.placaVehiculo.isNotBlank())
                Text("Placa: ${solicitud.placaVehiculo}  •  ${solicitud.anioVehiculo}",
                    fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(solicitud.descripcionProblema, fontSize = 14.sp, maxLines = 2)
            if (solicitud.urgencia == "urgente")
                Text("🚨 Urgente", fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.error, fontWeight = FontWeight.Bold)
            Text("Enviada: $fecha", fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

/**
 * Tarjeta visual para representar una [OrdenServicio] con la información de un trabajo de taller.
 * Al hacer clic, abre los detalles completos de esa orden.
 *
 * @param orden Datos de la reparación o mantenimiento.
 * @param onVerDetalle Lambda invocada al tocar la tarjeta, pasando el ID de la orden.
 * @param onEliminar Lambda invocada as tocar el ícono de eliminar.
 */
@Composable
fun OrdenCardCliente(orden: OrdenServicio, onVerDetalle: (String) -> Unit, onEliminar: (String) -> Unit) {
    val fecha = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date(orden.fechaEntrada))
    Card(
        modifier = Modifier.fillMaxWidth().clickable { onVerDetalle(orden.id) },
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically) {
                Text("${orden.marca} ${orden.modelo}", fontWeight = FontWeight.Bold, fontSize = 17.sp)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    EstadoBadge(orden.estado)
                    IconButton(
                        onClick = { onEliminar(orden.id) },
                        modifier = Modifier.size(32.dp).padding(start = 8.dp)
                    ) {
                        Icon(Icons.Default.Delete, contentDescription = "Eliminar", tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(18.dp))
                    }
                }
            }
            Text("Placa: ${orden.placa}  •  ${orden.anio}", fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text("Ingresó: $fecha", fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant)
            if (orden.descripcionFalla.isNotEmpty())
                Text(orden.descripcionFalla, fontSize = 14.sp, maxLines = 2)
        }
    }
}

```

### DetalleOrdenScreen.kt
*(UbicaciÃ³n: \$relativePath\)*

```kotlin
package com.cevm.mecapp.ui.cliente


import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cevm.mecapp.ui.OrdenViewModel
import com.cevm.mecapp.ui.components.EstadoBadge

/**
 * Pantalla que muestra al cliente el historial y estado detallado de una de sus [OrdenServicio].
 * Visualiza diagnóstico, trabajos realizados, costo y notas de entrega del mecánico.
 *
 * @param ordenId El ID de Firestore correspondiente a la orden a visualizar.
 * @param viewModel ViewModel con la lista de órdenes cargada en memoria.
 * @param onVolver Lambda para retroceder a la pantalla anterior.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClienteDetalleScreen(
    ordenId: String,
    viewModel: OrdenViewModel,
    onVolver: () -> Unit
) {
    val ordenes by viewModel.ordenes.collectAsState()
    val orden = ordenes.find { it.id == ordenId }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detalle del servicio") },
                navigationIcon = { IconButton(onClick = onVolver) { Icon(Icons.Default.ArrowBack, null) } }
            )
        }
    ) { padding ->
        if (orden == null) {
            Box(Modifier.fillMaxSize(), contentAlignment = androidx.compose.ui.Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            Column(Modifier.fillMaxSize().padding(padding).padding(16.dp).verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)) {

                // Estado
                Row { EstadoBadge(orden.estado) }

                // Datos del vehículo
                Card(Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text("🚗 Vehículo", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        Text("${orden.marca} ${orden.modelo} ${orden.anio}")
                        Text("Placa: ${orden.placa}  •  Color: ${orden.color}")
                        Text("Km entrada: ${orden.kmEntrada}")
                        if (orden.kmSalida != null) Text("Km salida: ${orden.kmSalida}")
                    }
                }

                // Falla reportada
                Card(Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text("🔍 Falla reportada", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        Text(orden.descripcionFalla)
                        if (orden.notasEntrada.isNotEmpty()) Text("Notas: ${orden.notasEntrada}")
                    }
                }

                // Presupuesto y Trabajos
                if (orden.trabajosRealizados.isNotEmpty() || orden.presupuestoItems.isNotEmpty() || orden.costoManoObra > 0 || orden.costoRepuestos > 0) {
                    Card(Modifier.fillMaxWidth()) {
                        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text("📋 Trabajos y Presupuesto", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                            
                            // Mostrar trabajos antiguos si los hay
                            if (orden.trabajosRealizados.isNotEmpty()) {
                                orden.trabajosRealizados.forEach { Text("• $it") }
                                Spacer(Modifier.height(8.dp))
                            }
                            
                            if (orden.presupuestoItems.isNotEmpty()) {
                                orden.presupuestoItems.forEach { item ->
                                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                        Text("${item.cantidad}x ${item.descripcion}", modifier = Modifier.weight(1f), fontSize = 14.sp)
                                        Text("$${"%.2f".format(item.cantidad * item.precioUnitario)}", fontSize = 14.sp)
                                    }
                                }
                                HorizontalDivider(Modifier.padding(vertical = 4.dp))
                            }
                            
                            val subtotal = orden.presupuestoItems.sumOf { it.cantidad * it.precioUnitario }
                            val impuestos = (subtotal + orden.costoManoObra + orden.costoRepuestos) * 0.16
                            if (subtotal > 0 || orden.costoManoObra > 0 || orden.costoRepuestos > 0) {
                                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) { Text("Subtotal", fontSize = 14.sp); Text("$${"%.2f".format(subtotal)}", fontSize = 14.sp) }
                                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) { Text("Mano de Obra", fontSize = 14.sp); Text("$${"%.2f".format(orden.costoManoObra)}", fontSize = 14.sp) }
                                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) { Text("Repuestos", fontSize = 14.sp); Text("$${"%.2f".format(orden.costoRepuestos)}", fontSize = 14.sp) }
                                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) { Text("Impuestos (16%)", fontSize = 14.sp); Text("$${"%.2f".format(impuestos)}", fontSize = 14.sp) }
                                HorizontalDivider(Modifier.padding(vertical = 4.dp))
                            }
                            if (orden.costoTotal > 0) {
                                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) { Text("Total", fontWeight = FontWeight.Bold, fontSize = 16.sp); Text("$${"%.2f".format(orden.costoTotal)}", fontWeight = FontWeight.Bold, fontSize = 16.sp) }
                            }
                            
                            if (orden.notasPresupuesto.isNotBlank()) {
                                Spacer(Modifier.height(4.dp))
                                Text("Notas: ${orden.notasPresupuesto}", fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                    }
                }

                // Firma física
                if (orden.urlFirmaFisica.isNotEmpty()) {
                    Card(Modifier.fillMaxWidth()) {
                        Column(Modifier.padding(16.dp)) {
                            Text("📄 Documento firmado", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                            Spacer(Modifier.height(8.dp))
                            Text("El documento de acuerdo ha sido firmado y guardado.", fontSize = 14.sp)
                        }
                    }
                }

                // Notas de salida
                if (orden.notasSalida.isNotEmpty()) {
                    Card(Modifier.fillMaxWidth()) {
                        Column(Modifier.padding(16.dp)) {
                            Text("✅ Notas de entrega", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                            Spacer(Modifier.height(4.dp))
                            Text(orden.notasSalida)
                        }
                    }
                }
            }
        }
    }
}

```

### MapaTalleresScreen.kt
*(UbicaciÃ³n: \$relativePath\)*

```kotlin
package com.cevm.mecapp.ui.cliente

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*
import com.cevm.mecapp.data.model.Usuario
import com.cevm.mecapp.ui.AuthViewModel

/**
 * Pantalla interactiva con Google Maps que permite a los clientes buscar talleres cercanos.
 * Solicita permisos de ubicación para centrar el mapa y dibuja marcadores con las
 * posiciones de los diferentes mecánicos registrados en el sistema.
 *
 * @param authViewModel Para cargar la lista pública de mecánicos y sus coordenadas.
 * @param onVolver Lambda para regresar a la vista anterior.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapaTalleresScreen(
    authViewModel: AuthViewModel,
    onVolver: () -> Unit
) {
    val context = LocalContext.current
    val mecanicos by authViewModel.mecanicos.collectAsState()

    var locationPermissionGranted by remember { mutableStateOf(false) }
    var locationInicialObtenida by remember { mutableStateOf(false) }
    
    var mecanicoSeleccionado by remember { mutableStateOf<Usuario?>(null) }

    val defaultLocation = LatLng(19.4326, -99.1332)
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(defaultLocation, 10f)
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        locationPermissionGranted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
                permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
    }

    LaunchedEffect(Unit) {
        authViewModel.cargarMecanicos()
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            locationPermissionGranted = true
        } else {
            permissionLauncher.launch(arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ))
        }
    }

    // Centrar mapa en ubicación del cliente
    LaunchedEffect(locationPermissionGranted) {
        if (locationPermissionGranted && !locationInicialObtenida) {
            val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
            @SuppressLint("MissingPermission")
            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                if (location != null) {
                    val loc = LatLng(location.latitude, location.longitude)
                    cameraPositionState.position = CameraPosition.fromLatLngZoom(loc, 14f)
                    locationInicialObtenida = true
                }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Talleres Cercanos") },
                navigationIcon = {
                    IconButton(onClick = onVolver) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Volver")
                    }
                }
            )
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            GoogleMap(
                modifier = Modifier.fillMaxSize(),
                cameraPositionState = cameraPositionState,
                properties = MapProperties(isMyLocationEnabled = locationPermissionGranted),
                uiSettings = MapUiSettings(myLocationButtonEnabled = true)
            ) {
                mecanicos.forEach { mecanico ->
                    if (mecanico.latitud != null && mecanico.longitud != null) {
                        val loc = LatLng(mecanico.latitud, mecanico.longitud)
                        val titulo = mecanico.nombreTaller.ifEmpty { mecanico.nombre }
                        Marker(
                            state = MarkerState(position = loc),
                            title = titulo,
                            snippet = mecanico.especialidad.ifEmpty { "Taller Mecánico" },
                            onClick = {
                                mecanicoSeleccionado = mecanico
                                true // Consumir evento
                            }
                        )
                    }
                }
            }

            // Mostrar información del mecánico seleccionado en la parte inferior
            if (mecanicoSeleccionado != null) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.BottomCenter)
                        .padding(16.dp),
                    elevation = CardDefaults.cardElevation(8.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Row(
                            Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            val tituloSecundario = if (mecanicoSeleccionado!!.nombreTaller.isNotEmpty()) mecanicoSeleccionado!!.nombre else ""
                            Column {
                                Text(
                                    mecanicoSeleccionado!!.nombreTaller.ifEmpty { mecanicoSeleccionado!!.nombre },
                                    style = MaterialTheme.typography.titleMedium,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                if (tituloSecundario.isNotEmpty()) {
                                    Text(
                                        "Mecánico: $tituloSecundario",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                            IconButton(onClick = { mecanicoSeleccionado = null }) {
                                Text("✕")
                            }
                        }

                        if (mecanicoSeleccionado!!.especialidad.isNotEmpty()) {
                            Text("Especialidad: ${mecanicoSeleccionado!!.especialidad}", style = MaterialTheme.typography.bodyMedium)
                        }
                        if (mecanicoSeleccionado!!.descripcionTaller.isNotEmpty()) {
                            Text(mecanicoSeleccionado!!.descripcionTaller, style = MaterialTheme.typography.bodySmall)
                        }
                        
                        // Aquí en el futuro se podría agregar la puntuación o botón para enviar solicitud directamente
                    }
                }
            }
        }
    }
}

```

### NuevaSolicitudScreen.kt
*(UbicaciÃ³n: \$relativePath\)*

```kotlin
package com.cevm.mecapp.ui.cliente

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.auth.FirebaseAuth
import com.cevm.mecapp.data.model.SolicitudRevision
import com.cevm.mecapp.ui.AuthViewModel
import com.cevm.mecapp.ui.SolicitudViewModel

/**
 * Formulario donde el cliente puede detallar el problema de su vehículo y enviar una 
 * [SolicitudRevision] a la plataforma. Puede especificar si el requerimiento es urgente.
 *
 * @param solicitudViewModel Encargado de mandar la solicitud hacia Firestore.
 * @param authViewModel Requerido para precargar el nombre e email del usuario actual.
 * @param onVolver Lambda para cerrar la pantalla al completar el envío.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NuevaSolicitudScreen(
    solicitudViewModel: SolicitudViewModel,
    authViewModel: AuthViewModel,
    onVolver: () -> Unit
) {
    val user = FirebaseAuth.getInstance().currentUser
    val uid = user?.uid ?: ""
    val usuario by authViewModel.usuario.collectAsState()

    val loading by solicitudViewModel.loading.collectAsState()
    val exito by solicitudViewModel.exito.collectAsState()

    var marca by remember { mutableStateOf("") }
    var modelo by remember { mutableStateOf("") }
    var anio by remember { mutableStateOf("") }
    var placa by remember { mutableStateOf("") }
    var descripcion by remember { mutableStateOf("") }
    var urgenciaSeleccionada by remember { mutableStateOf("normal") }

    LaunchedEffect(exito) {
        if (exito != null) {
            solicitudViewModel.limpiarMensajes()
            onVolver()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Nueva solicitud") },
                navigationIcon = {
                    IconButton(onClick = onVolver) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, null)
                    }
                }
            )
        }
    ) { padding ->
        Column(
            Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            // ── Datos del vehículo ────────────────────────────────────────
            Text("Datos del vehículo", fontWeight = FontWeight.Bold, fontSize = 16.sp)

            OutlinedTextField(
                value = marca, onValueChange = { marca = it },
                label = { Text("Marca") },
                modifier = Modifier.fillMaxWidth(), singleLine = true
            )
            OutlinedTextField(
                value = modelo, onValueChange = { modelo = it },
                label = { Text("Modelo") },
                modifier = Modifier.fillMaxWidth(), singleLine = true
            )
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = anio, onValueChange = { anio = it },
                    label = { Text("Año") },
                    modifier = Modifier.weight(1f), singleLine = true
                )
                OutlinedTextField(
                    value = placa, onValueChange = { placa = it },
                    label = { Text("Placa") },
                    modifier = Modifier.weight(1f), singleLine = true
                )
            }

            HorizontalDivider()

            // ── Problema y urgencia ───────────────────────────────────────
            Text("Descripción del problema", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            OutlinedTextField(
                value = descripcion,
                onValueChange = { descripcion = it },
                label = { Text("¿Qué falla o revisión necesita?") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3
            )

            Text("Urgencia", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                listOf("normal" to "Normal", "urgente" to "🚨 Urgente").forEach { (valor, etiqueta) ->
                    FilterChip(
                        selected = urgenciaSeleccionada == valor,
                        onClick = { urgenciaSeleccionada = valor },
                        label = { Text(etiqueta) }
                    )
                }
            }

            Spacer(Modifier.height(8.dp))

            Button(
                onClick = {
                    val solicitud = SolicitudRevision(
                        clienteUid = uid,
                        clienteNombre = usuario?.nombre ?: user?.displayName ?: "",
                        clienteEmail = user?.email ?: "",
                        marcaVehiculo = marca.trim(),
                        modeloVehiculo = modelo.trim(),
                        anioVehiculo = anio.trim(),
                        placaVehiculo = placa.trim(),
                        descripcionProblema = descripcion.trim(),
                        urgencia = urgenciaSeleccionada
                    )
                    solicitudViewModel.crearSolicitud(solicitud)
                },
                modifier = Modifier.fillMaxWidth().height(50.dp),
                enabled = !loading && descripcion.isNotBlank()
            ) {
                if (loading) CircularProgressIndicator(Modifier.size(20.dp))
                else Text("Enviar solicitud", fontSize = 16.sp)
            }

            Spacer(Modifier.height(16.dp))
        }
    }
}

```

### EstadoBadge.kt
*(UbicaciÃ³n: \$relativePath\)*

```kotlin
package com.cevm.mecapp.ui.components


import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cevm.mecapp.ui.theme.*

/**
 * Un componente visual (etiqueta o "badge") que muestra el estado actual de una orden
 * con colores específicos según el progreso (ej. Revisión, Reparación, Listo).
 *
 * @param estado String que representa el estado actual de la orden.
 */
@Composable
fun EstadoBadge(estado: String) {
    val (label, bg, fg) = when (estado) {
        "recibido"   -> Triple("Recibido",   ColorRecibido,   Color.White)
        "revision"   -> Triple("En Revisión", ColorRevision,  Color.White)
        "reparacion" -> Triple("Reparación",  ColorReparacion, Color.White)
        "listo"      -> Triple("Listo",       ColorListo,      Color.White)
        "entregado"  -> Triple("Entregado",   ColorEntregado,  Color.White)
        else         -> Triple(estado,           Gris400,          Color.White)
    }
    Text(
        text = label,
        color = fg,
        fontSize = 12.sp,
        fontWeight = FontWeight.Bold,
        modifier = Modifier
            .background(bg, RoundedCornerShape(50))
            .padding(horizontal = 12.dp, vertical = 4.dp)
    )
}

```

### EditarOrdenScreen.kt
*(UbicaciÃ³n: \$relativePath\)*

```kotlin
package com.cevm.mecapp.ui.mecanico

import android.content.Intent
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import com.cevm.mecapp.data.model.PresupuestoItem
import com.cevm.mecapp.ui.AuthViewModel
import com.cevm.mecapp.ui.OrdenViewModel
import com.cevm.mecapp.ui.components.EstadoBadge
import com.cevm.mecapp.utils.PdfGenerator
import kotlinx.coroutines.launch
import java.io.File

// IMPORTANTE: Asegúrate de tener estos delegados para que el "by" funcione
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue

val ESTADOS = listOf("recibido", "revision", "reparacion", "listo", "entregado")

/**
 * Pantalla que permite al mecánico gestionar una orden de servicio existente.
 * Permite cambiar el estado del servicio, añadir descripciones de trabajos realizados,
 * establecer el costo total, capturar la firma del cliente físicamente y generar el PDF.
 *
 * @param ordenId ID único de la orden a editar.
 * @param viewModel ViewModel con los datos y reglas de negocio para órdenes.
 * @param authViewModel ViewModel para consultar información del mecánico.
 * @param onVolver Lambda para regresar a la pantalla anterior.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditarOrdenScreen(
    ordenId: String,
    viewModel: OrdenViewModel,
    authViewModel: AuthViewModel,
    onVolver: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val ordenes by viewModel.ordenes.collectAsState()
    val orden = ordenes.find { it.id == ordenId }
    val exito by viewModel.exito.collectAsState()

    val presupuestoItems = remember { mutableStateListOf<PresupuestoItem>() }
    var manoObra by remember { mutableStateOf(orden?.costoManoObra?.toString() ?: "") }
    var costoRepuestos by remember { mutableStateOf(orden?.costoRepuestos?.toString() ?: "") }
    var notasPresupuesto by remember { mutableStateOf(orden?.notasPresupuesto ?: "") }
    var notasSalida by remember { mutableStateOf(orden?.notasSalida ?: "") }

    LaunchedEffect(orden) {
        if (orden != null && presupuestoItems.isEmpty()) {
            presupuestoItems.addAll(orden.presupuestoItems)
            if (manoObra.isEmpty()) manoObra = orden.costoManoObra.toString()
            if (costoRepuestos.isEmpty()) costoRepuestos = orden.costoRepuestos.toString()
        }
    }

    val subtotal = presupuestoItems.sumOf { it.cantidad * it.precioUnitario }
    val mo = manoObra.toDoubleOrNull() ?: 0.0
    val rep = costoRepuestos.toDoubleOrNull() ?: 0.0
    val impuestos = (subtotal + mo + rep) * 0.16
    val granTotal = subtotal + mo + rep + impuestos

    var nuevoItemDesc by remember { mutableStateOf("") }
    var nuevoItemCant by remember { mutableStateOf("1") }
    var nuevoItemPrecio by remember { mutableStateOf("") }

    var fotoUri by remember { mutableStateOf<Uri?>(null) }
    var estadoExpandido by remember { mutableStateOf(false) }

    val cameraLauncher = rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) { ok ->
        if (ok && fotoUri != null) viewModel.subirFirmaFisica(fotoUri!!, ordenId)
    }

    LaunchedEffect(exito) { if (exito != null) viewModel.limpiarMensajes() }

    if (orden == null) return

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("${orden.marca} ${orden.modelo}") },
                navigationIcon = { IconButton(onClick = onVolver) { Icon(Icons.Default.ArrowBack, null) } }
            )
        }
    ) { padding ->
        Column(Modifier.fillMaxSize().padding(padding).padding(16.dp)
            .verticalScroll(rememberScrollState()), verticalArrangement = Arrangement.spacedBy(16.dp)) {

            // Estado actual
            Card(Modifier.fillMaxWidth()) {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Estado actual", fontWeight = FontWeight.Bold)
                    Row(verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        EstadoBadge(orden.estado)
                        ExposedDropdownMenuBox(expanded = estadoExpandido,
                            onExpandedChange = { estadoExpandido = it }) {
                            OutlinedButton(onClick = { estadoExpandido = true },
                                modifier = Modifier.menuAnchor()) {
                                Text("Cambiar estado")
                            }
                            ExposedDropdownMenu(expanded = estadoExpandido,
                                onDismissRequest = { estadoExpandido = false }) {
                                ESTADOS.forEach { estado ->
                                    DropdownMenuItem(
                                        text = { Text(estado) },
                                        onClick = {
                                            viewModel.actualizarEstado(ordenId, estado)
                                            estadoExpandido = false
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Presupuesto: Lista de Servicios y Repuestos
            Card(Modifier.fillMaxWidth()) {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Lista de Servicios y Repuestos", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    
                    if (presupuestoItems.isNotEmpty()) {
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Servicio/Repuesto", modifier = Modifier.weight(2f), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            Text("Cant.", modifier = Modifier.weight(1f), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            Text("Precio U.", modifier = Modifier.weight(1f), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            Text("Sub.", modifier = Modifier.weight(1f), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            Spacer(Modifier.width(32.dp))
                        }
                    }
                    
                    presupuestoItems.forEach { item ->
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                            Text(item.descripcion, modifier = Modifier.weight(2f), fontSize = 14.sp)
                            Text(item.cantidad.toString(), modifier = Modifier.weight(1f), fontSize = 14.sp)
                            Text("$${"%.2f".format(item.precioUnitario)}", modifier = Modifier.weight(1f), fontSize = 14.sp)
                            Text("$${"%.2f".format(item.cantidad * item.precioUnitario)}", modifier = Modifier.weight(1f), fontSize = 14.sp)
                            IconButton(
                                onClick = { presupuestoItems.remove(item) },
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(Icons.Default.Delete, "Eliminar", tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(20.dp))
                            }
                        }
                    }
                    
                    HorizontalDivider(Modifier.padding(vertical = 8.dp))
                    Text("+ Agregar Servicio / Repuesto", fontSize = 14.sp, fontWeight = FontWeight.Medium)
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(value = nuevoItemDesc, onValueChange = { nuevoItemDesc = it }, label = { Text("Desc.") }, modifier = Modifier.weight(2f), singleLine = true)
                        OutlinedTextField(value = nuevoItemCant, onValueChange = { nuevoItemCant = it }, label = { Text("Cant.") }, modifier = Modifier.weight(1f), singleLine = true, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number))
                        OutlinedTextField(value = nuevoItemPrecio, onValueChange = { nuevoItemPrecio = it }, label = { Text("Precio") }, modifier = Modifier.weight(1f), singleLine = true, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number))
                        IconButton(onClick = {
                            if (nuevoItemDesc.isNotBlank()) {
                                presupuestoItems.add(PresupuestoItem(descripcion = nuevoItemDesc, cantidad = nuevoItemCant.toIntOrNull() ?: 1, precioUnitario = nuevoItemPrecio.toDoubleOrNull() ?: 0.0))
                                nuevoItemDesc = ""
                                nuevoItemCant = "1"
                                nuevoItemPrecio = ""
                            }
                        }) { Icon(Icons.Default.Add, "Agregar") }
                    }
                }
            }

            // Resumen del Presupuesto
            Card(Modifier.fillMaxWidth()) {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Resumen del Presupuesto", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) { Text("Subtotal"); Text("$${"%.2f".format(subtotal)}") }
                    OutlinedTextField(value = manoObra, onValueChange = { manoObra = it }, label = { Text("Mano de Obra") }, modifier = Modifier.fillMaxWidth(), singleLine = true, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number))
                    OutlinedTextField(value = costoRepuestos, onValueChange = { costoRepuestos = it }, label = { Text("Repuestos") }, modifier = Modifier.fillMaxWidth(), singleLine = true, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number))
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) { Text("Impuestos (16%)"); Text("$${"%.2f".format(impuestos)}") }
                    HorizontalDivider(Modifier.padding(vertical = 8.dp))
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) { Text("Total", fontWeight = FontWeight.Bold, fontSize = 18.sp); Text("$${"%.2f".format(granTotal)}", fontWeight = FontWeight.Bold, fontSize = 18.sp) }
                    
                    OutlinedTextField(value = notasPresupuesto, onValueChange = { notasPresupuesto = it }, label = { Text("Notas Adicionales") }, modifier = Modifier.fillMaxWidth(), minLines = 2)
                }
            }

            OutlinedButton(
                onClick = {
                    val file = PdfGenerator.generarContrato(context, orden)
                    val uri = FileProvider.getUriForFile(context, "${context.packageName}.provider", file)
                    val intent = Intent(Intent.ACTION_VIEW).apply {
                        setDataAndType(uri, "application/pdf")
                        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                    }
                    context.startActivity(Intent.createChooser(intent, "Abrir contrato"))
                },
                modifier = Modifier.fillMaxWidth()
            ) { Text("📄 Generar PDF para firma física") }

            OutlinedButton(
                onClick = {
                    val file = File.createTempFile("firma_", ".jpg", context.cacheDir)
                    fotoUri = FileProvider.getUriForFile(context, "${context.packageName}.provider", file)
                    cameraLauncher.launch(fotoUri!!)
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Default.CameraAlt, null, Modifier.size(18.dp))
                Spacer(Modifier.width(8.dp))
                Text("📸 Fotografiar documento firmado")
            }

            if (orden.urlFirmaFisica.isNotEmpty()) {
                Text("✅ Firma física adjuntada", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
            }

            OutlinedTextField(value = notasSalida, onValueChange = { notasSalida = it },
                label = { Text("Notas de salida / entrega") }, modifier = Modifier.fillMaxWidth(), minLines = 3)

            Button(
                onClick = {
                    scope.launch {
                        com.cevm.mecapp.data.repository.OrdenRepository().actualizar(
                            ordenId, mapOf(
                                "presupuestoItems" to presupuestoItems.toList(),
                                "costoManoObra" to mo,
                                "costoRepuestos" to rep,
                                "notasPresupuesto" to notasPresupuesto,
                                "costoTotal" to granTotal,
                                "notasSalida" to notasSalida
                            )
                        )
                    }
                },
                modifier = Modifier.fillMaxWidth().height(50.dp)
            ) { Text("Guardar cambios", fontSize = 16.sp) }

            Spacer(Modifier.height(16.dp))
        }
    }
}
```

### MecanicoHomeScreen.kt
*(UbicaciÃ³n: \$relativePath\)*

```kotlin
package com.cevm.mecapp.ui.mecanico


import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.auth.FirebaseAuth
import com.cevm.mecapp.data.model.OrdenServicio
import com.cevm.mecapp.data.model.SolicitudRevision
import com.cevm.mecapp.ui.AuthViewModel
import com.cevm.mecapp.ui.OrdenViewModel
import com.cevm.mecapp.ui.SolicitudViewModel
import com.cevm.mecapp.ui.components.EstadoBadge
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Pantalla principal para los usuarios con rol "mecanico".
 * Cuenta con tres pestañas: "Mis Órdenes" para ver los servicios en curso,
 * "Solicitudes" para aceptar o rechazar revisiones pedidas por clientes,
 * y "Mi Perfil" para gestionar los datos y la ubicación del taller.
 *
 * @param viewModel ViewModel para operaciones CRUD de las órdenes.
 * @param authViewModel ViewModel de sesión para manejar perfil y cierre de sesión.
 * @param solicitudViewModel ViewModel para carga y actualización de solicitudes pendientes.
 * @param onNuevaOrden Lambda para navegar a la pantalla de crear una orden manual.
 * @param onEditarOrden Lambda para abrir el modo edición de una orden en particular.
 * @param onConfigurarTaller Lambda para abrir el mapa y asentar la ubicación del negocio.
 * @param onLogout Lambda para salir de la cuenta de usuario.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MecanicoHomeScreen(
    viewModel: OrdenViewModel,
    authViewModel: AuthViewModel,
    solicitudViewModel: SolicitudViewModel,
    onNuevaOrden: () -> Unit,
    onEditarOrden: (String) -> Unit,
    onConfigurarTaller: () -> Unit,
    onLogout: () -> Unit
) {
    val uid = FirebaseAuth.getInstance().currentUser?.uid ?: ""
    val ordenes by viewModel.ordenes.collectAsState()
    val usuario by authViewModel.usuario.collectAsState()
    val solicitudes by solicitudViewModel.solicitudes.collectAsState()
    val context = LocalContext.current

    var tabSeleccionado by remember { mutableIntStateOf(0) }
    val tabs = listOf("Mis Órdenes", "Solicitudes", "Mi Perfil")

    var ordenAEliminar by remember { mutableStateOf<String?>(null) }

    // Campos editables del perfil
    var editando by remember { mutableStateOf(false) }
    var nombreEdit by remember { mutableStateOf("") }
    var telefonoEdit by remember { mutableStateOf("") }

    LaunchedEffect(uid) {
        if (uid.isNotEmpty()) {
            viewModel.cargarOrdenesDelMecanico(uid)
            authViewModel.cargarPerfil(uid)
            solicitudViewModel.cargarPendientes()
        }
    }

    // Cuando se carga el usuario, pre-llenar los campos de edición
    LaunchedEffect(usuario) {
        usuario?.let {
            nombreEdit = it.nombre
            telefonoEdit = it.telefono
        }
    }

    if (ordenAEliminar != null) {
        AlertDialog(
            onDismissRequest = { ordenAEliminar = null },
            title = { Text("Eliminar Orden") },
            text = { Text("¿Deseas eliminar permanentemente esta orden de servicio? Esta acción no se puede deshacer.") },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.eliminarOrden(ordenAEliminar!!)
                    ordenAEliminar = null
                }) { Text("Eliminar", color = MaterialTheme.colorScheme.error) }
            },
            dismissButton = {
                TextButton(onClick = { ordenAEliminar = null }) { Text("Cancelar") }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Taller Mecánico") },
                actions = {
                    if (tabSeleccionado == 1 && !editando) {
                        IconButton(onClick = { editando = true }) {
                            Icon(Icons.Default.Edit, contentDescription = "Editar perfil")
                        }
                    }
                    IconButton(onClick = onLogout) {
                        Icon(Icons.AutoMirrored.Filled.ExitToApp, "Salir")
                    }
                }
            )
        },
        floatingActionButton = {
            if (tabSeleccionado == 0) {
                ExtendedFloatingActionButton(
                    onClick = onNuevaOrden,
                    icon = { Icon(Icons.Default.Add, null) },
                    text = { Text("Nueva orden") }
                )
            }
        }
    ) { padding ->
        Column(Modifier.fillMaxSize().padding(padding)) {

            // ── Pestañas ─────────────────────────────────────────────────────
            TabRow(selectedTabIndex = tabSeleccionado) {
                tabs.forEachIndexed { index, titulo ->
                    Tab(
                        selected = tabSeleccionado == index,
                        onClick = { tabSeleccionado = index },
                        text = { Text(titulo) }
                    )
                }
            }

            when (tabSeleccionado) {

                // ── 0: Mis Órdenes ────────────────────────────────────
                0 -> {
                    if (ordenes.isEmpty()) {
                        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("\uD83D\uDD27", fontSize = 64.sp)
                                Spacer(Modifier.height(8.dp))
                                Text("Sin órdenes activas", fontSize = 18.sp)
                                Text(
                                    "Toca + para registrar un vehículo",
                                    fontSize = 14.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    } else {
                        LazyColumn(
                            Modifier.fillMaxSize().padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(ordenes) { orden -> 
                                OrdenCardMecanico(
                                    orden = orden, 
                                    onEditar = onEditarOrden,
                                    onEliminar = { ordenAEliminar = it }
                                ) 
                            }
                        }
                    }
                }

                // ── 1: Solicitudes de Clientes ────────────────────────
                1 -> {
                    if (solicitudes.isEmpty()) {
                        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("📋", fontSize = 64.sp)
                                Spacer(Modifier.height(8.dp))
                                Text("Sin solicitudes pendientes", fontSize = 18.sp)
                            }
                        }
                    } else {
                        LazyColumn(
                            Modifier.fillMaxSize().padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(solicitudes) { sol ->
                                SolicitudCardMecanico(
                                    solicitud = sol,
                                    onAceptar = { solicitudViewModel.aceptar(sol.id) },
                                    onRechazar = { solicitudViewModel.rechazar(sol.id) }
                                )
                            }
                        }
                    }
                }

                // ── 2: Mi Perfil ─────────────────────────────────────
                2 -> {
                    if (usuario == null) {
                        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator()
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize().padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            item {
                                // Avatar
                                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                                    Card(
                                        shape = MaterialTheme.shapes.extraLarge,
                                        colors = CardDefaults.cardColors(
                                            containerColor = MaterialTheme.colorScheme.primaryContainer
                                        )
                                    ) {
                                        Icon(
                                            Icons.Default.Person,
                                            contentDescription = null,
                                            modifier = Modifier.padding(24.dp).size(56.dp),
                                            tint = MaterialTheme.colorScheme.onPrimaryContainer
                                        )
                                    }
                                }
                            }

                            item {
                                // Datos del perfil (editables o solo lectura)
                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    elevation = CardDefaults.cardElevation(2.dp)
                                ) {
                                    Column(
                                        Modifier.padding(16.dp),
                                        verticalArrangement = Arrangement.spacedBy(12.dp)
                                    ) {
                                        Text(
                                            if (editando) "Editando perfil" else "Datos del perfil",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 16.sp,
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                        HorizontalDivider()

                                        if (editando) {
                                            OutlinedTextField(
                                                value = nombreEdit,
                                                onValueChange = { nombreEdit = it },
                                                label = { Text("Nombre") },
                                                modifier = Modifier.fillMaxWidth(),
                                                singleLine = true
                                            )
                                            OutlinedTextField(
                                                value = telefonoEdit,
                                                onValueChange = { telefonoEdit = it },
                                                label = { Text("Teléfono") },
                                                modifier = Modifier.fillMaxWidth(),
                                                singleLine = true
                                            )
                                            // Email (no editable)
                                            Text("Email", fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                            Text(usuario!!.email, fontSize = 14.sp, fontWeight = FontWeight.Medium)

                                            Row(
                                                Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                                            ) {
                                                OutlinedButton(
                                                    onClick = {
                                                        // Restaurar valores originales y cancelar
                                                        nombreEdit = usuario!!.nombre
                                                        telefonoEdit = usuario!!.telefono
                                                        editando = false
                                                    },
                                                    modifier = Modifier.weight(1f)
                                                ) { Text("Cancelar") }
                                                Button(
                                                    onClick = {
                                                        authViewModel.actualizarPerfil(uid, nombreEdit.trim(), telefonoEdit.trim())
                                                        editando = false
                                                        Toast.makeText(context, "Perfil actualizado", Toast.LENGTH_SHORT).show()
                                                    },
                                                    modifier = Modifier.weight(1f)
                                                ) {
                                                    Icon(Icons.Default.Save, null, modifier = Modifier.size(18.dp))
                                                    Spacer(Modifier.width(4.dp))
                                                    Text("Guardar")
                                                }
                                            }
                                        } else {
                                            // Modo solo lectura
                                            PerfilFilaMecanico("Nombre", usuario!!.nombre)
                                            PerfilFilaMecanico("Email", usuario!!.email)
                                            PerfilFilaMecanico("Teléfono", usuario!!.telefono.ifEmpty { "—" })
                                            PerfilFilaMecanico("Tipo", "Mecánico")
                                        }
                                    }
                                }
                            }

                            item {
                                // Taller y Ubicación
                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    elevation = CardDefaults.cardElevation(2.dp)
                                ) {
                                    Column(
                                        Modifier.padding(16.dp),
                                        verticalArrangement = Arrangement.spacedBy(12.dp)
                                    ) {
                                        Text(
                                            "🛠️ Mi Taller",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 16.sp,
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                        HorizontalDivider()

                                        PerfilFilaMecanico("Nombre", usuario!!.nombreTaller.ifEmpty { "No definido" })
                                        PerfilFilaMecanico("Especialidad", usuario!!.especialidad.ifEmpty { "No definida" })
                                        PerfilFilaMecanico("Descripción", usuario!!.descripcionTaller.ifEmpty { "No definida" })
                                        
                                        val ubicacionTxt = if (usuario!!.latitud != null && usuario!!.longitud != null) "Configurada en mapa" else "No configurada"
                                        PerfilFilaMecanico("Ubicación", ubicacionTxt)

                                        Spacer(Modifier.height(8.dp))
                                        OutlinedButton(
                                            onClick = onConfigurarTaller,
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            Text("Configurar Ubicación y Detalles")
                                        }
                                    }
                                }
                            }

                            item {
                                // UID del mecánico
                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    elevation = CardDefaults.cardElevation(2.dp),
                                    colors = CardDefaults.cardColors(
                                        containerColor = MaterialTheme.colorScheme.secondaryContainer
                                    )
                                ) {
                                    Column(
                                        Modifier.padding(16.dp),
                                        verticalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Text(
                                            "🔑 Mi UID de Mecánico",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 15.sp,
                                            color = MaterialTheme.colorScheme.onSecondaryContainer
                                        )
                                        Text(
                                            "Este es tu identificador único en el sistema.",
                                            fontSize = 12.sp,
                                            color = MaterialTheme.colorScheme.onSecondaryContainer
                                        )
                                        Spacer(Modifier.height(4.dp))
                                        Surface(
                                            shape = MaterialTheme.shapes.medium,
                                            color = MaterialTheme.colorScheme.surface,
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            Row(
                                                Modifier.fillMaxWidth().padding(12.dp),
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Text(
                                                    text = uid,
                                                    fontSize = 12.sp,
                                                    fontWeight = FontWeight.Medium,
                                                    modifier = Modifier.weight(1f)
                                                )
                                                IconButton(onClick = {
                                                    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                                    clipboard.setPrimaryClip(ClipData.newPlainText("UID", uid))
                                                    Toast.makeText(context, "UID copiado", Toast.LENGTH_SHORT).show()
                                                }) {
                                                    Icon(
                                                        Icons.Default.ContentCopy,
                                                        contentDescription = "Copiar UID",
                                                        tint = MaterialTheme.colorScheme.primary
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

/**
 * Componente interno para mostrar un par etiqueta-valor dentro del perfil del mecánico.
 *
 * @param etiqueta Título de la fila a la izquierda.
 * @param valor Contenido asociado a la derecha en negrita.
 */
@Composable
private fun PerfilFilaMecanico(etiqueta: String, valor: String) {
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(etiqueta, fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(valor, fontSize = 14.sp, fontWeight = FontWeight.Medium)
    }
}

/**
 * Tarjeta visual para representar una orden de servicio en la lista del mecánico.
 *
 * @param orden Datos de la orden en curso.
 * @param onEditar Lambda invocada al tocar la tarjeta, pasando el ID de la orden para ir a su detalle.
 * @param onEliminar Lambda invocada para borrar permanentemente esa orden.
 */
@Composable
fun OrdenCardMecanico(orden: OrdenServicio, onEditar: (String) -> Unit, onEliminar: (String) -> Unit) {
    val fecha = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(Date(orden.fechaEntrada))
    var mostrarMenu by remember { mutableStateOf(false) }
    Card(
        Modifier.fillMaxWidth().clickable { onEditar(orden.id) },
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("${orden.marca} ${orden.modelo}", fontWeight = FontWeight.Bold, fontSize = 17.sp)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    EstadoBadge(orden.estado)
                    Box {
                        IconButton(onClick = { mostrarMenu = true }) {
                            Icon(Icons.Default.MoreVert, "Opciones")
                        }
                        DropdownMenu(
                            expanded = mostrarMenu,
                            onDismissRequest = { mostrarMenu = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("Eliminar Orden", color = MaterialTheme.colorScheme.error) },
                                onClick = { 
                                    mostrarMenu = false
                                    onEliminar(orden.id) 
                                }
                            )
                        }
                    }
                }
            }
            Text("Cliente: ${orden.clienteNombre}", fontSize = 14.sp)
            Text(
                "Placa: ${orden.placa}  •  ${orden.anio}",
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                "Ingresó: $fecha",
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

/**
 * Tarjeta visual para que el mecánico evalúe una [SolicitudRevision] entrante.
 *
 * @param solicitud Información de la solicitud hecha por el cliente.
 * @param onAceptar Acción para marcar que el mecánico acepta revisar el vehículo.
 * @param onRechazar Acción para declinar la solicitud de revisión.
 */
@Composable
fun SolicitudCardMecanico(
    solicitud: SolicitudRevision,
    onAceptar: () -> Unit,
    onRechazar: () -> Unit
) {
    val fecha = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
        .format(Date(solicitud.fechaSolicitud))
    Card(Modifier.fillMaxWidth(), elevation = CardDefaults.cardElevation(3.dp)) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {

            // Encabezado: vehículo y chip de urgencia
            Row(Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically) {
                Column {
                    Text("${solicitud.marcaVehiculo} ${solicitud.modeloVehiculo}",
                        fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    if (solicitud.placaVehiculo.isNotBlank())
                        Text("Placa: ${solicitud.placaVehiculo}  •  ${solicitud.anioVehiculo}",
                            fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                if (solicitud.urgencia == "urgente") {
                    Surface(shape = MaterialTheme.shapes.small,
                        color = MaterialTheme.colorScheme.errorContainer) {
                        Text("🚨 Urgente", fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onErrorContainer,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp))
                    }
                }
            }

            // Cliente
            Text("👤 ${solicitud.clienteNombre}", fontSize = 14.sp)

            // Problema
            HorizontalDivider()
            Text(solicitud.descripcionProblema, fontSize = 14.sp, maxLines = 3)
            Text("Recibida: $fecha", fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant)

            // Botones
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedButton(
                    onClick = onRechazar,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = MaterialTheme.colorScheme.error)
                ) { Text("✗ Rechazar") }
                Button(
                    onClick = onAceptar,
                    modifier = Modifier.weight(1f)
                ) { Text("✓ Aceptar") }
            }
        }
    }
}

```

### NuevaOrdenScreen.kt
*(UbicaciÃ³n: \$relativePath\)*

```kotlin
package com.cevm.mecapp.ui.mecanico


import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.auth.FirebaseAuth
import com.cevm.mecapp.data.model.OrdenServicio
import com.cevm.mecapp.ui.OrdenViewModel

// Lista local de marcas populares (sin necesidad de API)
private val MARCAS_AUTO = listOf(
    "Acura", "Alfa Romeo", "Audi", "BMW", "Buick", "Cadillac", "Chevrolet",
    "Chrysler", "Citroën", "Dodge", "Ferrari", "Fiat", "Ford", "Genesis",
    "GMC", "Honda", "Hyundai", "Infiniti", "Jaguar", "Jeep", "Kia",
    "Lamborghini", "Land Rover", "Lexus", "Lincoln", "Maserati", "Mazda",
    "Mercedes-Benz", "Mini", "Mitsubishi", "Nissan", "Peugeot", "Porsche",
    "RAM", "Renault", "Seat", "Skoda", "Subaru", "Suzuki", "Tesla",
    "Toyota", "Volkswagen", "Volvo"
)

/**
 * Pantalla donde el mecánico registra manualmente la entrada de un vehículo al taller,
 * llenando los datos del cliente, vehículo (marca, modelo, año, placa, color),
 * y los detalles de la falla inicial, para crear una nueva orden de servicio.
 *
 * @param viewModel ViewModel para enviar la orden recién creada a Firestore.
 * @param onVolver Lambda para cancelar y retroceder o regresar tras guardar con éxito.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NuevaOrdenScreen(
    viewModel: OrdenViewModel,
    onVolver: () -> Unit
) {
    val mecanicoUid = FirebaseAuth.getInstance().currentUser?.uid ?: ""
    val exito by viewModel.exito.collectAsState()
    val loading by viewModel.loading.collectAsState()
    val focusManager = LocalFocusManager.current

    // Campos del vehículo
    var clienteNombre by remember { mutableStateOf("") }
    var clienteUid by remember { mutableStateOf("") }

    // Autocompletado de marca
    var marca by remember { mutableStateOf("") }
    var marcaMenuExpanded by remember { mutableStateOf(false) }
    val marcasFiltradas = remember(marca) {
        if (marca.length >= 1)
            MARCAS_AUTO.filter { it.contains(marca, ignoreCase = true) }
        else emptyList()
    }

    var modelo by remember { mutableStateOf("") }
    var anio by remember { mutableStateOf("") }
    var placa by remember { mutableStateOf("") }
    var color by remember { mutableStateOf("") }
    var km by remember { mutableStateOf("") }
    var falla by remember { mutableStateOf("") }
    var notas by remember { mutableStateOf("") }

    LaunchedEffect(exito) {
        if (exito != null) { viewModel.limpiarMensajes(); onVolver() }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Registrar vehículo") },
                navigationIcon = {
                    IconButton(onClick = onVolver) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, null)
                    }
                }
            )
        }
    ) { padding ->
        Column(
            Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            // ── Datos del cliente ───────────────────────────────────────────
            Text("Datos del cliente", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            OutlinedTextField(
                value = clienteNombre, onValueChange = { clienteNombre = it },
                label = { Text("Nombre del cliente") },
                modifier = Modifier.fillMaxWidth(), singleLine = true
            )
            OutlinedTextField(
                value = clienteUid, onValueChange = { clienteUid = it },
                label = { Text("UID del cliente (de Firebase)") },
                modifier = Modifier.fillMaxWidth(), singleLine = true
            )

            HorizontalDivider()

            // ── Datos del vehículo ──────────────────────────────────────────
            Text("Datos del vehículo", fontWeight = FontWeight.Bold, fontSize = 16.sp)

            // Marca con autocompletado
            ExposedDropdownMenuBox(
                expanded = marcaMenuExpanded && marcasFiltradas.isNotEmpty(),
                onExpandedChange = { marcaMenuExpanded = it }
            ) {
                OutlinedTextField(
                    value = marca,
                    onValueChange = { marca = it; marcaMenuExpanded = true },
                    label = { Text("Marca") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor(MenuAnchorType.PrimaryEditable)
                        .onFocusChanged { if (!it.isFocused) marcaMenuExpanded = false },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                    keyboardActions = KeyboardActions(onNext = {
                        // Si hay sugerencias, completar con la primera al presionar Next/Enter
                        if (marcasFiltradas.isNotEmpty()) {
                            marca = marcasFiltradas.first()
                            marcaMenuExpanded = false
                        }
                        focusManager.clearFocus()
                    })
                )
                ExposedDropdownMenu(
                    expanded = marcaMenuExpanded && marcasFiltradas.isNotEmpty(),
                    onDismissRequest = { marcaMenuExpanded = false }
                ) {
                    marcasFiltradas.forEach { sugerencia ->
                        DropdownMenuItem(
                            text = { Text(sugerencia) },
                            onClick = {
                                marca = sugerencia
                                marcaMenuExpanded = false
                                focusManager.clearFocus()
                            }
                        )
                    }
                }
            }

            // Modelo
            OutlinedTextField(
                value = modelo, onValueChange = { modelo = it },
                label = { Text("Modelo") },
                modifier = Modifier.fillMaxWidth(), singleLine = true
            )

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = anio, onValueChange = { anio = it },
                    label = { Text("Año") }, modifier = Modifier.weight(1f), singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
                OutlinedTextField(
                    value = color, onValueChange = { color = it },
                    label = { Text("Color") }, modifier = Modifier.weight(1f), singleLine = true
                )
            }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = placa, onValueChange = { placa = it },
                    label = { Text("Placa") }, modifier = Modifier.weight(1f), singleLine = true
                )
                OutlinedTextField(
                    value = km, onValueChange = { km = it },
                    label = { Text("KM entrada") }, modifier = Modifier.weight(1f), singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
            }

            HorizontalDivider()

            // ── Falla reportada ─────────────────────────────────────────────
            Text("Falla reportada", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            OutlinedTextField(
                value = falla, onValueChange = { falla = it },
                label = { Text("Descripción de la falla") },
                modifier = Modifier.fillMaxWidth(), minLines = 3
            )
            OutlinedTextField(
                value = notas, onValueChange = { notas = it },
                label = { Text("Notas adicionales de entrada") },
                modifier = Modifier.fillMaxWidth(), minLines = 2
            )

            Spacer(Modifier.height(8.dp))
            Button(
                onClick = {
                    val orden = OrdenServicio(
                        clienteUid = clienteUid,
                        clienteNombre = clienteNombre,
                        mecanicoUid = mecanicoUid,
                        marca = marca, modelo = modelo,
                        anio = anio.toIntOrNull() ?: 0,
                        placa = placa, color = color,
                        kmEntrada = km.toIntOrNull() ?: 0,
                        descripcionFalla = falla,
                        notasEntrada = notas
                    )
                    viewModel.crearOrden(orden)
                },
                modifier = Modifier.fillMaxWidth().height(50.dp),
                enabled = !loading
            ) {
                if (loading) CircularProgressIndicator(Modifier.size(20.dp))
                else Text("Registrar vehículo", fontSize = 16.sp)
            }
            Spacer(Modifier.height(16.dp))
        }
    }
}


```

### UbicacionTallerScreen.kt
*(UbicaciÃ³n: \$relativePath\)*

```kotlin
package com.cevm.mecapp.ui.mecanico

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*
import com.cevm.mecapp.ui.AuthViewModel

/**
 * Pantalla donde el mecánico puede especificar la ubicación exacta de su taller en un mapa,
 * además de editar el nombre, descripción y especialidad de su negocio.
 * Esta información será pública para que los clientes puedan encontrarlo en el mapa.
 *
 * @param authViewModel ViewModel de sesión para subir los datos e ubicación actualizados.
 * @param onVolver Lambda para regresar a la vista de perfil u otra pantalla.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UbicacionTallerScreen(
    authViewModel: AuthViewModel,
    onVolver: () -> Unit
) {
    val context = LocalContext.current
    val usuario by authViewModel.usuario.collectAsState()

    var nombreTaller by remember { mutableStateOf("") }
    var descripcion by remember { mutableStateOf("") }
    var especialidad by remember { mutableStateOf("") }
    var ubicacionMarcada by remember { mutableStateOf<LatLng?>(null) }
    
    val defaultLocation = LatLng(19.4326, -99.1332) // CDMX default
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(defaultLocation, 10f)
    }

    // Inicializar con valores existentes
    LaunchedEffect(usuario) {
        usuario?.let {
            nombreTaller = it.nombreTaller
            descripcion = it.descripcionTaller
            especialidad = it.especialidad
            if (it.latitud != null && it.longitud != null) {
                val loc = LatLng(it.latitud, it.longitud)
                ubicacionMarcada = loc
                cameraPositionState.position = CameraPosition.fromLatLngZoom(loc, 15f)
            }
        }
    }

    var locationPermissionGranted by remember { mutableStateOf(false) }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        locationPermissionGranted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
                permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
    }

    LaunchedEffect(Unit) {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            locationPermissionGranted = true
        } else {
            permissionLauncher.launch(arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ))
        }
    }

    // Obtener ubicación actual si no hay ninguna marcada
    LaunchedEffect(locationPermissionGranted) {
        if (locationPermissionGranted && ubicacionMarcada == null) {
            val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
            @SuppressLint("MissingPermission")
            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                if (location != null) {
                    val loc = LatLng(location.latitude, location.longitude)
                    ubicacionMarcada = loc
                    cameraPositionState.position = CameraPosition.fromLatLngZoom(loc, 15f)
                }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Configurar Taller") },
                navigationIcon = {
                    IconButton(onClick = onVolver) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Volver")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text("Detalles del Taller", style = MaterialTheme.typography.titleMedium)

            OutlinedTextField(
                value = nombreTaller,
                onValueChange = { nombreTaller = it },
                label = { Text("Nombre del Taller") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            OutlinedTextField(
                value = descripcion,
                onValueChange = { descripcion = it },
                label = { Text("Descripción del taller") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 2
            )

            OutlinedTextField(
                value = especialidad,
                onValueChange = { especialidad = it },
                label = { Text("Especialidad (ej. Frenos, Motor, General)") },
                modifier = Modifier.fillMaxWidth()
            )

            Text("Ubicación en el Mapa", style = MaterialTheme.typography.titleMedium)
            Text("Toca el mapa para establecer o corregir la ubicación de tu taller.", style = MaterialTheme.typography.bodySmall)

            Box(
                modifier = Modifier.fillMaxWidth().weight(1f)
            ) {
                GoogleMap(
                    modifier = Modifier.fillMaxSize(),
                    cameraPositionState = cameraPositionState,
                    properties = MapProperties(isMyLocationEnabled = locationPermissionGranted),
                    uiSettings = MapUiSettings(myLocationButtonEnabled = true),
                    onMapClick = { latLng -> ubicacionMarcada = latLng }
                ) {
                    ubicacionMarcada?.let { loc ->
                        Marker(
                            state = MarkerState(position = loc),
                            title = "Mi Taller",
                            snippet = "Ubicación seleccionada"
                        )
                    }
                }
            }

            Button(
                onClick = {
                    val uid = usuario?.uid
                    if (uid != null && ubicacionMarcada != null) {
                        authViewModel.actualizarPerfilMecanico(
                            uid = uid,
                            nombre = usuario!!.nombre,
                            telefono = usuario!!.telefono,
                            nombreTaller = nombreTaller.trim(),
                            descripcionTaller = descripcion.trim(),
                            especialidad = especialidad.trim()
                        )
                        authViewModel.actualizarUbicacion(
                            uid = uid,
                            latitud = ubicacionMarcada!!.latitude,
                            longitud = ubicacionMarcada!!.longitude
                        )
                        Toast.makeText(context, "Taller guardado", Toast.LENGTH_SHORT).show()
                        onVolver()
                    } else {
                        Toast.makeText(context, "Falta establecer ubicación en el mapa", Toast.LENGTH_SHORT).show()
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Default.Save, null)
                Spacer(Modifier.width(8.dp))
                Text("Guardar Configuración")
            }
        }
    }
}

```

### Color.kt
*(UbicaciÃ³n: \$relativePath\)*

```kotlin
package com.cevm.mecapp.ui.theme


import androidx.compose.ui.graphics.Color

val Azul900    = Color(0xFF0D3B66)
val Azul700    = Color(0xFF1565C0)
val Azul500    = Color(0xFF1E88E5)
val AzulClaro  = Color(0xFFE3F2FD)
val Verde700   = Color(0xFF2E7D32)
val Verde100   = Color(0xFFE8F5E9)
val Naranja700 = Color(0xFFE65100)
val Naranja100 = Color(0xFFFFF3E0)
val Gris100    = Color(0xFFF5F5F5)
val Gris400    = Color(0xFFBDBDBD)
val Gris700    = Color(0xFF616161)

// Estados del vehículo
val ColorRecibido   = Color(0xFF78909C)
val ColorRevision   = Color(0xFF1976D2)
val ColorReparacion = Color(0xFFE65100)
val ColorListo      = Color(0xFF2E7D32)
val ColorEntregado  = Color(0xFF6A1B9A)

```

### Theme.kt
*(UbicaciÃ³n: \$relativePath\)*

```kotlin
package com.cevm.mecapp.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = Azul500,
    secondary = Verde700,
    tertiary = Naranja700
)

private val LightColorScheme = lightColorScheme(
    primary = Azul700,
    secondary = Verde700,
    tertiary = Naranja700

    /* Other default colors to override
    background = Color(0xFFFFFBFE),
    surface = Color(0xFFFFFBFE),
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = Color(0xFF1C1B1F),
    onSurface = Color(0xFF1C1B1F),
    */
)

/**
 * Tema principal de Jetpack Compose para la aplicación MecApp.
 * Define la paleta de colores (claros u oscuros) y la tipografía base
 * para mantener un diseño coherente en toda la UI.
 *
 * @param darkTheme Determina si se fuerza el tema oscuro o claro (por defecto toma el del sistema).
 * @param dynamicColor Habilita colores dinámicos en dispositivos con Android 12+.
 * @param content El bloque Composable que representa la interfaz de usuario de la App.
 */
@Composable
fun MecAppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
```

### Type.kt
*(UbicaciÃ³n: \$relativePath\)*

```kotlin
package com.cevm.mecapp.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

// Set of Material typography styles to start with
val Typography = Typography(
    bodyLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
    )
    /* Other default text styles to override
    titleLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 22.sp,
        lineHeight = 28.sp,
        letterSpacing = 0.sp
    ),
    labelSmall = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,
        fontSize = 11.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp
    )
    */
)
```

### PdfGenerator.kt
*(UbicaciÃ³n: \$relativePath\)*

```kotlin
package com.cevm.mecapp.utils


import android.content.Context
import android.os.Environment
import com.itextpdf.kernel.pdf.PdfDocument
import com.itextpdf.kernel.pdf.PdfWriter
import com.itextpdf.layout.Document
import com.itextpdf.layout.element.Paragraph
import com.itextpdf.layout.element.Table
import com.itextpdf.layout.element.Cell
import com.itextpdf.layout.properties.TextAlignment
import com.itextpdf.layout.properties.UnitValue
import com.cevm.mecapp.data.model.OrdenServicio
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

/**
 * Utilidad encargada de generar documentos PDF (como contratos u órdenes de servicio).
 */
object PdfGenerator {

    /**
     * Genera un archivo PDF con los detalles de una [OrdenServicio].
     * Se crea un diseño básico con encabezados, datos del cliente, del vehículo, y una sección para firmas.
     *
     * @param context El contexto de la aplicación, usado para acceder al directorio de documentos externos.
     * @param orden La orden de servicio a partir de la cual se generará el contrato PDF.
     * @return Un objeto [File] apuntando al archivo PDF recién creado.
     */
    fun generarContrato(context: Context, orden: OrdenServicio): File {
        val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
        val fecha = sdf.format(Date(orden.fechaEntrada))
        val nombre = "contrato_${orden.placa}_${System.currentTimeMillis()}.pdf"
        val file = File(context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), nombre)

        val writer = PdfWriter(file)
        val pdf = PdfDocument(writer)
        val doc = Document(pdf)

        // Encabezado
        doc.add(Paragraph("ORDEN DE SERVICIO — TALLER MECÁNICO")
            .setBold().setFontSize(18f).setTextAlignment(TextAlignment.CENTER))
        doc.add(Paragraph("Fecha: $fecha").setFontSize(11f))
        doc.add(Paragraph("No. de orden: ${orden.id.take(8).uppercase()}").setFontSize(11f))
        doc.add(Paragraph(" "))

        // Datos del cliente
        doc.add(Paragraph("DATOS DEL CLIENTE").setBold().setFontSize(13f))
        doc.add(Paragraph("Nombre: ${orden.clienteNombre}"))
        doc.add(Paragraph(" "))

        // Datos del vehículo
        doc.add(Paragraph("DATOS DEL VEHÍCULO").setBold().setFontSize(13f))
        val tablaVeh = Table(UnitValue.createPercentArray(floatArrayOf(50f, 50f))).useAllAvailableWidth()
        tablaVeh.addCell(Cell().add(Paragraph("Marca: ${orden.marca}")))
        tablaVeh.addCell(Cell().add(Paragraph("Modelo: ${orden.modelo}")))
        tablaVeh.addCell(Cell().add(Paragraph("Año: ${orden.anio}")))
        tablaVeh.addCell(Cell().add(Paragraph("Color: ${orden.color}")))
        tablaVeh.addCell(Cell().add(Paragraph("Placa: ${orden.placa}")))
        tablaVeh.addCell(Cell().add(Paragraph("KM entrada: ${orden.kmEntrada}")))
        doc.add(tablaVeh)
        doc.add(Paragraph(" "))

        // Falla reportada
        doc.add(Paragraph("FALLA REPORTADA").setBold().setFontSize(13f))
        doc.add(Paragraph(orden.descripcionFalla))
        if (orden.notasEntrada.isNotEmpty()) doc.add(Paragraph("Notas: ${orden.notasEntrada}"))
        doc.add(Paragraph(" "))

        // Trabajos
        if (orden.trabajosRealizados.isNotEmpty()) {
            doc.add(Paragraph("TRABAJOS A REALIZAR / REALIZADOS").setBold().setFontSize(13f))
            orden.trabajosRealizados.forEach { doc.add(Paragraph("  • $it")) }
            doc.add(Paragraph(" "))
        }

        // Costo
        if (orden.costoTotal > 0) {
            doc.add(Paragraph("COSTO ESTIMADO: \$${orden.costoTotal}")
                .setBold().setFontSize(13f).setTextAlignment(TextAlignment.RIGHT))
            doc.add(Paragraph(" "))
        }

        // Sección de firmas
        doc.add(Paragraph(" "))
        doc.add(Paragraph(" "))
        doc.add(Paragraph("El cliente declara estar de acuerdo con los trabajos y costos descritos.")
            .setFontSize(11f).setTextAlignment(TextAlignment.CENTER).setItalic())
        doc.add(Paragraph(" "))
        doc.add(Paragraph(" "))

        val tablaFirmas = Table(UnitValue.createPercentArray(floatArrayOf(50f, 50f))).useAllAvailableWidth()
        tablaFirmas.addCell(Cell().add(
            Paragraph("__________________________\nFirma del Cliente\n${orden.clienteNombre}")
                .setTextAlignment(TextAlignment.CENTER)))
        tablaFirmas.addCell(Cell().add(
            Paragraph("__________________________\nFirma del Taller")
                .setTextAlignment(TextAlignment.CENTER)))
        doc.add(tablaFirmas)

        doc.close()
        return file
    }
}

```
