package com.cevm.mecapp.data.repository

import android.net.Uri
import com.cevm.mecapp.data.model.OrdenServicio
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

/**
 * Repositorio que gestiona las operaciones CRUD para [OrdenServicio] en Firestore y
 * el almacenamiento de archivos (como firmas) en Firebase Storage.
 */
class OrdenRepository {
    private val db = FirebaseFirestore.getInstance()
    private val storage = FirebaseStorage.getInstance()
    private val col = db.collection("ordenes")

    /**
     * Crea una nueva orden de servicio en Firestore con un ID generado automáticamente.
     *
     * @param orden La [OrdenServicio] a guardar.
     * @return Result que contiene el ID generado para la nueva orden o el error respectivo.
     */
    suspend fun crearOrden(orden: OrdenServicio): Result<String> {
        return try {
            val ref = col.document()
            val conId = orden.copy(id = ref.id)
            ref.set(conId).await()
            Result.success(ref.id)
        } catch (e: Exception) { Result.failure(e) }
    }

    /**
     * Actualiza campos específicos de una orden de servicio existente.
     *
     * @param id ID de la orden en Firestore.
     * @param campos Mapa con los nombres de los campos a actualizar y sus nuevos valores.
     */
    suspend fun actualizar(id: String, campos: Map<String, Any?>): Result<Unit> {
        return try {
            col.document(id).update(campos).await()
            Result.success(Unit)
        } catch (e: Exception) { Result.failure(e) }
    }

    /**
     * Elimina definitivamente una orden de servicio de Firestore.
     *
     * @param id ID de la orden a eliminar.
     */
    suspend fun eliminarOrden(id: String): Result<Unit> {
        return try {
            col.document(id).delete().await()
            Result.success(Unit)
        } catch (e: Exception) { Result.failure(e) }
    }

    /**
     * Retorna un flujo (Flow) con la lista de órdenes asociadas a un mecánico,
     * escuchando actualizaciones en tiempo real desde Firestore.
     *
     * @param mecanicoUid Identificador único del mecánico.
     */
    fun ordenesDelMecanico(mecanicoUid: String): Flow<List<OrdenServicio>> =
        callbackFlow {
            val listener = col
                .whereEqualTo("mecanicoUid", mecanicoUid)
                .addSnapshotListener { snap, _ ->
                    val lista = snap?.toObjects(OrdenServicio::class.java)
                        ?.sortedByDescending { it.fechaEntrada } ?: emptyList()
                    trySend(lista)
                }
            awaitClose { listener.remove() }
        }

    /**
     * Retorna un flujo (Flow) con la lista de órdenes pertenecientes a un cliente dado,
     * escuchando actualizaciones en tiempo real desde Firestore.
     *
     * @param clienteUid Identificador único del cliente.
     */
    fun ordenesDelCliente(clienteUid: String): Flow<List<OrdenServicio>> =
        callbackFlow {
            val listener = col
                .whereEqualTo("clienteUid", clienteUid)
                .addSnapshotListener { snap, _ ->
                    val lista = snap?.toObjects(OrdenServicio::class.java)
                        ?.sortedByDescending { it.fechaEntrada } ?: emptyList()
                    trySend(lista)
                }
            awaitClose { listener.remove() }
        }

    /**
     * Sube un archivo de imagen (ej. foto, firma) a Firebase Storage.
     *
     * @param uri URI local del archivo.
     * @param path Ruta en Firebase Storage donde se guardará (ej. "firmas/123/firma.jpg").
     * @return Result que contiene la URL de descarga pública si tiene éxito, o error.
     */
    suspend fun subirFoto(uri: Uri, path: String): Result<String> {
        return try {
            val ref = storage.reference.child(path)
            ref.putFile(uri).await()
            val url = ref.downloadUrl.await().toString()
            Result.success(url)
        } catch (e: Exception) { Result.failure(e) }
    }
}
