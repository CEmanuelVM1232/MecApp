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
