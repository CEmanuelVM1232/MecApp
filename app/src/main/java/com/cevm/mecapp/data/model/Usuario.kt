package com.cevm.mecapp.data.model


/**
 * Representa un usuario en el sistema.
 * Puede ser de tipo "cliente" o "mecanico". Los mecánicos tienen campos extra para
 * detalles de taller y ubicación.
 */
data class Usuario(
    val uid: String = "",
    val nombre: String = "",
    val email: String = "",
    val telefono: String = "",
    val tipo: String = "cliente",  // "cliente" o "mecanico"
    // Campos específicos para mecánicos (Google Maps y Perfil de Taller)
    val nombreTaller: String = "",
    val latitud: Double? = null,
    val longitud: Double? = null,
    val descripcionTaller: String = "",
    val especialidad: String = "",
    val puntuacion: Double = 0.0,
    val createdAt: Long = System.currentTimeMillis()
)
