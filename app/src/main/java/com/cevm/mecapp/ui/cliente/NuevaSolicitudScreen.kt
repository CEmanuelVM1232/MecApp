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
