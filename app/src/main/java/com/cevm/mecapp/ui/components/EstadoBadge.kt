package com.cevm.mecapp.ui.components


import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cevm.mecapp.ui.theme.*

/**
 * Un componente visual (etiqueta o "badge") que muestra el estado actual de una orden
 * con colores específicos según el progreso (ej. Revisión, Reparación, Listo).
 *
 * @param estado String que representa el estado actual de la orden.
 */
@Composable
fun EstadoBadge(estado: String) {
    val (label, bg, fg) = when (estado) {
        "recibido"   -> Triple("Recibido",   ColorRecibido,   Color.White)
        "revision"   -> Triple("En Revisión", ColorRevision,  Color.White)
        "reparacion" -> Triple("Reparación",  ColorReparacion, Color.White)
        "listo"      -> Triple("Listo",       ColorListo,      Color.White)
        "entregado"  -> Triple("Entregado",   ColorEntregado,  Color.White)
        else         -> Triple(estado,           Gris400,          Color.White)
    }
    Text(
        text = label,
        color = fg,
        fontSize = 12.sp,
        fontWeight = FontWeight.Bold,
        modifier = Modifier
            .background(bg, RoundedCornerShape(50))
            .padding(horizontal = 12.dp, vertical = 4.dp)
    )
}
