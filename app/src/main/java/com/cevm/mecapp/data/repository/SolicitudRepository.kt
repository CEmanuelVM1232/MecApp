package com.cevm.mecapp.data.repository

import com.cevm.mecapp.data.model.SolicitudRevision
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

/**
 * Repositorio de acceso a datos para gestionar las operaciones relacionadas con las
 * solicitudes de revisión ([SolicitudRevision]) en Firestore.
 */
class SolicitudRepository {
    private val db = FirebaseFirestore.getInstance()
    private val col = db.collection("solicitudes")

    /**
     * Crea una nueva solicitud en base de datos.
     *
     * @param solicitud El objeto con la información de la solicitud.
     * @return Result envolviendo el ID asignado por Firestore, o una excepción si falló.
     */
    suspend fun crearSolicitud(solicitud: SolicitudRevision): Result<String> {
        return try {
            val ref = col.document()
            val conId = solicitud.copy(id = ref.id)
            ref.set(conId).await()
            Result.success(ref.id)
        } catch (e: Exception) { Result.failure(e) }
    }

    /**
     * Devuelve un flujo persistente (Flow) de recolección para las solicitudes
     * que están en estado "pendiente" en todo el sistema. 
     * Ideal para mostrar a los mecánicos qué trabajos pueden tomar.
     */
    fun solicitudesPendientes(): Flow<List<SolicitudRevision>> = callbackFlow {
        val listener = col
            .whereEqualTo("estado", "pendiente")
            .addSnapshotListener { snap, _ ->
                val lista = snap?.toObjects(SolicitudRevision::class.java)
                    ?.sortedByDescending { it.fechaSolicitud } ?: emptyList()
                trySend(lista)
            }
        awaitClose { listener.remove() }
    }

    /**
     * Obtiene un flujo de las solicitudes realizadas por un cliente específico
     * sin importar el estado.
     *
     * @param clienteUid Identificador único del cliente.
     */
    fun solicitudesDelCliente(clienteUid: String): Flow<List<SolicitudRevision>> = callbackFlow {
        val listener = col
            .whereEqualTo("clienteUid", clienteUid)
            .addSnapshotListener { snap, _ ->
                val lista = snap?.toObjects(SolicitudRevision::class.java)
                    ?.sortedByDescending { it.fechaSolicitud } ?: emptyList()
                trySend(lista)
            }
        awaitClose { listener.remove() }
    }

    /**
     * Actualiza el campo 'estado' de una solicitud específica.
     * Útil cuando un mecánico acepta o rechaza una solicitud.
     *
     * @param id ID del documento en Firestore.
     * @param nuevoEstado El estado por establecer ("aceptada", "rechazada", etc).
     */
    suspend fun actualizarEstado(id: String, nuevoEstado: String): Result<Unit> {
        return try {
            col.document(id).update("estado", nuevoEstado).await()
            Result.success(Unit)
        } catch (e: Exception) { Result.failure(e) }
    }

    /**
     * Borra permanentemente una solicitud de revisión de la base de datos.
     *
     * @param id Identificador de Firestore de la solicitud.
     */
    suspend fun eliminarSolicitud(id: String): Result<Unit> {
        return try {
            col.document(id).delete().await()
            Result.success(Unit)
        } catch (e: Exception) { Result.failure(e) }
    }
}
