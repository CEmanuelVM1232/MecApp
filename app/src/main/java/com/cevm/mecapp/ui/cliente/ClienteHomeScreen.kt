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
