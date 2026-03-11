package com.cevm.mecapp.data.model

/**
 * Representa una solicitud de revisión de un vehículo creada por un cliente.
 * El mecánico puede aceptar o rechazar esta solicitud antes de generar una orden de servicio.
 */
data class SolicitudRevision(
    val id: String = "",
    val clienteUid: String = "",
    val clienteNombre: String = "",
    val clienteEmail: String = "",
    // Vehículo
    val marcaVehiculo: String = "",
    val modeloVehiculo: String = "",
    val anioVehiculo: String = "",
    val placaVehiculo: String = "",
    // Solicitud
    val descripcionProblema: String = "",
    val urgencia: String = "normal",   // "normal" | "urgente"
    // Estado: "pendiente" | "aceptada" | "rechazada"
    val estado: String = "pendiente",
    val fechaSolicitud: Long = System.currentTimeMillis()
)
