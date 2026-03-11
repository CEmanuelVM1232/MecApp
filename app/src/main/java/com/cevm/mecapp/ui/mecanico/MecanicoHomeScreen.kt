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
