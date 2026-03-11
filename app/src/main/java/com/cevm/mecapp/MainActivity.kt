package com.cevm.mecapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.cevm.mecapp.ui.theme.MecAppTheme


/**
 * Actividad principal de la aplicación.
 * Sirve como punto de entrada y contenedor para la navegación de Jetpack Compose.
 */
class MainActivity : ComponentActivity() {
    /**
     * Se llama cuando la actividad está iniciando.
     * Configura el contenido de Compose y maneja la navegación inicial.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MecAppTheme {
                AppNavigation()            }
        }
    }
}