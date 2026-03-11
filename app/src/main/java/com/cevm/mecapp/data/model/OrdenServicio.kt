package com.cevm.mecapp.data.model

import java.util.UUID

/**
 * Representa un ítem dentro del presupuesto de una orden.
 */
data class PresupuestoItem(
    val id: String = UUID.randomUUID().toString(),
    val descripcion: String = "",
    val cantidad: Int = 1,
    val precioUnitario: Double = 0.0
)
/**
 * Representa una orden de servicio (reparación o mantenimiento) en el taller.
 * Contiene información sobre el cliente, el mecánico, los datos del vehículo,
 * el estado actual del servicio, y los costos y detalles asociados.
 */
data class OrdenServicio(
    val id: String = "",
    val clienteUid: String = "",
    val clienteNombre: String = "",
    val mecanicoUid: String = "",
    // Vehículo
    val marca: String = "",
    val modelo: String = "",
    val anio: Int = 0,
    val placa: String = "",
    val color: String = "",
    val kmEntrada: Int = 0,
    // Entrada
    val fechaEntrada: Long = System.currentTimeMillis(),
    val descripcionFalla: String = "",
    val notasEntrada: String = "",
    val fotosEntrada: List<String> = emptyList(),
    // Servicio
    val trabajosRealizados: List<String> = emptyList(),
    val refacciones: List<String> = emptyList(),
    // Presupuesto
    val presupuestoItems: List<PresupuestoItem> = emptyList(),
    val costoManoObra: Double = 0.0,
    val costoRepuestos: Double = 0.0,
    val notasPresupuesto: String = "",
    val costoTotal: Double = 0.0,
    // Estado: recibido | revision | reparacion | listo | entregado
    val estado: String = "recibido",
    // Documentos
    val urlPdfContrato: String = "",
    val urlFirmaFisica: String = "",
    // Salida
    val fechaSalida: Long? = null,
    val kmSalida: Int? = null,
    val notasSalida: String = ""
)
