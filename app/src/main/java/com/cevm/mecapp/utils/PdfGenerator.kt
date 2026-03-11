package com.cevm.mecapp.utils


import android.content.Context
import android.os.Environment
import com.itextpdf.kernel.pdf.PdfDocument
import com.itextpdf.kernel.pdf.PdfWriter
import com.itextpdf.layout.Document
import com.itextpdf.layout.element.Paragraph
import com.itextpdf.layout.element.Table
import com.itextpdf.layout.element.Cell
import com.itextpdf.layout.properties.TextAlignment
import com.itextpdf.layout.properties.UnitValue
import com.cevm.mecapp.data.model.OrdenServicio
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

/**
 * Utilidad encargada de generar documentos PDF (como contratos u órdenes de servicio).
 */
object PdfGenerator {

    /**
     * Genera un archivo PDF con los detalles de una [OrdenServicio].
     * Se crea un diseño básico con encabezados, datos del cliente, del vehículo, y una sección para firmas.
     *
     * @param context El contexto de la aplicación, usado para acceder al directorio de documentos externos.
     * @param orden La orden de servicio a partir de la cual se generará el contrato PDF.
     * @return Un objeto [File] apuntando al archivo PDF recién creado.
     */
    fun generarContrato(context: Context, orden: OrdenServicio): File {
        val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
        val fecha = sdf.format(Date(orden.fechaEntrada))
        val nombre = "contrato_${orden.placa}_${System.currentTimeMillis()}.pdf"
        val file = File(context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), nombre)

        val writer = PdfWriter(file)
        val pdf = PdfDocument(writer)
        val doc = Document(pdf)

        // Encabezado
        doc.add(Paragraph("ORDEN DE SERVICIO — TALLER MECÁNICO")
            .setBold().setFontSize(18f).setTextAlignment(TextAlignment.CENTER))
        doc.add(Paragraph("Fecha: $fecha").setFontSize(11f))
        doc.add(Paragraph("No. de orden: ${orden.id.take(8).uppercase()}").setFontSize(11f))
        doc.add(Paragraph(" "))

        // Datos del cliente
        doc.add(Paragraph("DATOS DEL CLIENTE").setBold().setFontSize(13f))
        doc.add(Paragraph("Nombre: ${orden.clienteNombre}"))
        doc.add(Paragraph(" "))

        // Datos del vehículo
        doc.add(Paragraph("DATOS DEL VEHÍCULO").setBold().setFontSize(13f))
        val tablaVeh = Table(UnitValue.createPercentArray(floatArrayOf(50f, 50f))).useAllAvailableWidth()
        tablaVeh.addCell(Cell().add(Paragraph("Marca: ${orden.marca}")))
        tablaVeh.addCell(Cell().add(Paragraph("Modelo: ${orden.modelo}")))
        tablaVeh.addCell(Cell().add(Paragraph("Año: ${orden.anio}")))
        tablaVeh.addCell(Cell().add(Paragraph("Color: ${orden.color}")))
        tablaVeh.addCell(Cell().add(Paragraph("Placa: ${orden.placa}")))
        tablaVeh.addCell(Cell().add(Paragraph("KM entrada: ${orden.kmEntrada}")))
        doc.add(tablaVeh)
        doc.add(Paragraph(" "))

        // Falla reportada
        doc.add(Paragraph("FALLA REPORTADA").setBold().setFontSize(13f))
        doc.add(Paragraph(orden.descripcionFalla))
        if (orden.notasEntrada.isNotEmpty()) doc.add(Paragraph("Notas: ${orden.notasEntrada}"))
        doc.add(Paragraph(" "))

        // Trabajos
        if (orden.trabajosRealizados.isNotEmpty()) {
            doc.add(Paragraph("TRABAJOS A REALIZAR / REALIZADOS").setBold().setFontSize(13f))
            orden.trabajosRealizados.forEach { doc.add(Paragraph("  • $it")) }
            doc.add(Paragraph(" "))
        }

        // Costo
        if (orden.costoTotal > 0) {
            doc.add(Paragraph("COSTO ESTIMADO: \$${orden.costoTotal}")
                .setBold().setFontSize(13f).setTextAlignment(TextAlignment.RIGHT))
            doc.add(Paragraph(" "))
        }

        // Sección de firmas
        doc.add(Paragraph(" "))
        doc.add(Paragraph(" "))
        doc.add(Paragraph("El cliente declara estar de acuerdo con los trabajos y costos descritos.")
            .setFontSize(11f).setTextAlignment(TextAlignment.CENTER).setItalic())
        doc.add(Paragraph(" "))
        doc.add(Paragraph(" "))

        val tablaFirmas = Table(UnitValue.createPercentArray(floatArrayOf(50f, 50f))).useAllAvailableWidth()
        tablaFirmas.addCell(Cell().add(
            Paragraph("__________________________\nFirma del Cliente\n${orden.clienteNombre}")
                .setTextAlignment(TextAlignment.CENTER)))
        tablaFirmas.addCell(Cell().add(
            Paragraph("__________________________\nFirma del Taller")
                .setTextAlignment(TextAlignment.CENTER)))
        doc.add(tablaFirmas)

        doc.close()
        return file
    }
}
