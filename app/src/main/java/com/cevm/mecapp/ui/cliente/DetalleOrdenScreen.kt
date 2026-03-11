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
