package com.cevm.mecapp.ui.auth


import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.*
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

import com.cevm.mecapp.ui.AuthState
import com.cevm.mecapp.ui.AuthViewModel
import com.cevm.mecapp.ui.theme.Azul700

/**
 * Componente visual de la pantalla de inicio de sesión.
 * Permite a los usuarios ingresar su correo y contraseña para autenticarse,
 * u ofrece un enlace en caso de que quieran registrar una nueva cuenta.
 *
 * @param viewModel El [AuthViewModel] que gestiona la lógica de autenticación.
 * @param onLoginExitoso Acción ejecutada cuando el login se aprueba, recibe el `tipo` de usuario.
 * @param onIrARegistro Acción ejecutada cuando el usuario presiona el botón de registro.
 */
@Composable
fun LoginScreen(
    viewModel: AuthViewModel,
    onLoginExitoso: (String) -> Unit,
    onIrARegistro: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var verPassword by remember { mutableStateOf(false) }
    val state by viewModel.state.collectAsState()

    LaunchedEffect(state) {
        if (state is AuthState.Success) onLoginExitoso((state as AuthState.Success).tipo)
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("🔧", fontSize = 64.sp)
        Spacer(Modifier.height(8.dp))
        Text("Taller Mecánico", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = Azul700)
        Text("Inicia sesión para continuar", fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(Modifier.height(32.dp))

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Correo electrónico") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
        )
        Spacer(Modifier.height(12.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Contraseña") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            visualTransformation = if (verPassword) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                IconButton(onClick = { verPassword = !verPassword }) {
                    Icon(if (verPassword) Icons.Default.VisibilityOff else Icons.Default.Visibility, null)
                }
            }
        )
        Spacer(Modifier.height(24.dp))

        if (state is AuthState.Error) {
            Text((state as AuthState.Error).mensaje, color = MaterialTheme.colorScheme.error, fontSize = 14.sp)
            Spacer(Modifier.height(8.dp))
        }

        Button(
            onClick = { viewModel.login(email, password) },
            modifier = Modifier.fillMaxWidth().height(50.dp),
            enabled = state !is AuthState.Loading
        ) {
            if (state is AuthState.Loading) CircularProgressIndicator(Modifier.size(20.dp), color = MaterialTheme.colorScheme.onPrimary)
            else Text("Iniciar Sesión", fontSize = 16.sp)
        }
        Spacer(Modifier.height(16.dp))

        TextButton(onClick = onIrARegistro) {
            Text("¿No tienes cuenta? Regístrate")
        }
    }
}
