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
