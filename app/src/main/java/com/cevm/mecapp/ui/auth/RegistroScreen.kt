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
