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
