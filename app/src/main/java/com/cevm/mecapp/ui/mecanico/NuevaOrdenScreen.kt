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

