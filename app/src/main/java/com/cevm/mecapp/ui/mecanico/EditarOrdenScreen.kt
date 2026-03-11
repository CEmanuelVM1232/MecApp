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