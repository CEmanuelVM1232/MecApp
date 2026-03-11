package com.cevm.mecapp.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = Azul500,
    secondary = Verde700,
    tertiary = Naranja700
)

private val LightColorScheme = lightColorScheme(
    primary = Azul700,
    secondary = Verde700,
    tertiary = Naranja700

    /* Other default colors to override
    background = Color(0xFFFFFBFE),
    surface = Color(0xFFFFFBFE),
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = Color(0xFF1C1B1F),
    onSurface = Color(0xFF1C1B1F),
    */
)

/**
 * Tema principal de Jetpack Compose para la aplicación MecApp.
 * Define la paleta de colores (claros u oscuros) y la tipografía base
 * para mantener un diseño coherente en toda la UI.
 *
 * @param darkTheme Determina si se fuerza el tema oscuro o claro (por defecto toma el del sistema).
 * @param dynamicColor Habilita colores dinámicos en dispositivos con Android 12+.
 * @param content El bloque Composable que representa la interfaz de usuario de la App.
 */
@Composable
fun MecAppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}