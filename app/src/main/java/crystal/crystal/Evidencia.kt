package crystal.crystal

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.gson.Gson
import com.google.zxing.BarcodeFormat
import com.google.zxing.qrcode.QRCodeWriter
import java.io.File
import java.io.InputStream
import java.security.MessageDigest
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.random.Random

/**
 * Evidencia de aceptación de un presupuesto/contrato. NO es firma digital legal ni biometría:
 * es un registro verificable (folio + hash del PDF + estado) para respaldar la aceptación.
 */
data class EvidenciaPresupuesto(
    val idPresupuesto: String,
    val cliente: String,
    val telefono: String,
    val fechaGeneracion: String,
    val total: Float,
    val hashDocumento: String,
    var estado: String = "GENERADO",          // GENERADO / ACEPTADO
    var metodoAceptacion: String = "",         // PRESENCIAL / WHATSAPP / LLAMADA / FIRMA
    var mensajeAceptacion: String = "",
    var fechaAceptacion: String = ""
)

object EvidenciaManager {

    private val gson = Gson()

    // Folio único: CRY-yyyyMMddHHmmss-XXXX
    fun generarIdPresupuesto(): String {
        val sello = SimpleDateFormat("yyyyMMddHHmmss", Locale.US).format(Date())
        val aleatorio = Random.nextInt(0, 10000).toString().padStart(4, '0')
        return "CRY-$sello-$aleatorio"
    }

    fun fechaActual(): String =
        SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US).format(Date())

    // SHA-256 de cualquier flujo (archivo, Uri de Storage, etc.).
    fun calcularHashStream(entrada: InputStream): String {
        val digest = MessageDigest.getInstance("SHA-256")
        val buffer = ByteArray(8192)
        var leidos = entrada.read(buffer)
        while (leidos > 0) {
            digest.update(buffer, 0, leidos)
            leidos = entrada.read(buffer)
        }
        return digest.digest().joinToString("") { "%02x".format(it) }
    }

    // SHA-256 del archivo PDF real ya generado.
    fun calcularHashArchivo(archivo: File): String =
        archivo.inputStream().use { calcularHashStream(it) }

    fun crearEvidenciaPresupuesto(
        archivoPdf: File,
        cliente: String,
        telefono: String,
        total: Float,
        idPresupuesto: String = generarIdPresupuesto()
    ): EvidenciaPresupuesto = EvidenciaPresupuesto(
        idPresupuesto = idPresupuesto,
        cliente = cliente,
        telefono = telefono,
        fechaGeneracion = fechaActual(),
        total = total,
        hashDocumento = calcularHashArchivo(archivoPdf)
    )

    // Carpeta "Evidencias" en almacenamiento propio de la app (sin permisos, minSdk 21→actual).
    private fun carpetaEvidencias(context: Context): File =
        File(context.getExternalFilesDir(null), "Evidencias").apply { mkdirs() }

    fun guardarEvidenciaJson(context: Context, evidencia: EvidenciaPresupuesto): File {
        val archivo = File(carpetaEvidencias(context), "${evidencia.idPresupuesto}.json")
        archivo.writeText(gson.toJson(evidencia))
        return archivo
    }

    fun leerEvidenciaJson(archivoJson: File): EvidenciaPresupuesto =
        gson.fromJson(archivoJson.readText(), EvidenciaPresupuesto::class.java)

    // Todas las evidencias guardadas (para verificar/respaldar).
    fun listarEvidencias(context: Context): List<EvidenciaPresupuesto> =
        carpetaEvidencias(context).listFiles { f -> f.extension == "json" }
            ?.mapNotNull { runCatching { leerEvidenciaJson(it) }.getOrNull() }
            ?: emptyList()

    fun archivosEvidencia(context: Context): List<File> =
        carpetaEvidencias(context).listFiles { f -> f.extension == "json" }?.toList() ?: emptyList()

    // Busca una evidencia cuya huella coincida con la del PDF en disputa.
    fun buscarPorHash(context: Context, hash: String): EvidenciaPresupuesto? =
        listarEvidencias(context).firstOrNull { it.hashDocumento.equals(hash, ignoreCase = true) }

    // Uid de la empresa: patrón (patron_uid) o la cuenta del usuario (auth.uid). Mismo patrón que el resto.
    private fun obtenerUidEmpresa(context: Context): String? =
        context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE).getString("patron_uid", null)
            ?: FirebaseAuth.getInstance().currentUser?.uid

    // Respalda la evidencia en el Firebase del usuario: usuarios/{empresaUid}/evidencias/{folio}.
    fun subirEvidenciaFirestore(
        context: Context,
        evidencia: EvidenciaPresupuesto,
        onResultado: (Boolean) -> Unit = {}
    ) {
        val empresaUid = obtenerUidEmpresa(context)
        if (empresaUid.isNullOrBlank()) { onResultado(false); return }
        val datos = mapOf(
            "idPresupuesto" to evidencia.idPresupuesto,
            "cliente" to evidencia.cliente,
            "telefono" to evidencia.telefono,
            "fechaGeneracion" to evidencia.fechaGeneracion,
            "total" to evidencia.total,
            "hashDocumento" to evidencia.hashDocumento,
            "estado" to evidencia.estado,
            "metodoAceptacion" to evidencia.metodoAceptacion,
            "mensajeAceptacion" to evidencia.mensajeAceptacion,
            "fechaAceptacion" to evidencia.fechaAceptacion
        )
        FirebaseFirestore.getInstance()
            .collection("usuarios").document(empresaUid)
            .collection("evidencias").document(evidencia.idPresupuesto)
            .set(datos)
            .addOnSuccessListener { onResultado(true) }
            .addOnFailureListener { onResultado(false) }
    }

    // QR con los datos mínimos para verificar el presupuesto.
    fun generarQrEvidencia(evidencia: EvidenciaPresupuesto, tam: Int = 600): Bitmap {
        val contenido = buildString {
            appendLine("CRYSTAL")
            appendLine("ID: ${evidencia.idPresupuesto}")
            appendLine("Cliente: ${evidencia.cliente}")
            appendLine("Total: S/ ${evidencia.total}")
            appendLine("Fecha: ${evidencia.fechaGeneracion}")
            appendLine("Hash: ${evidencia.hashDocumento}")
            append("Estado: ${evidencia.estado}")
        }
        val matriz = QRCodeWriter().encode(contenido, BarcodeFormat.QR_CODE, tam, tam)
        val bmp = Bitmap.createBitmap(tam, tam, Bitmap.Config.RGB_565)
        for (x in 0 until tam) {
            for (y in 0 until tam) {
                bmp.setPixel(x, y, if (matriz.get(x, y)) Color.BLACK else Color.WHITE)
            }
        }
        return bmp
    }

    // Registro manual de aceptación (presencial / WhatsApp / llamada). Reescribe el JSON.
    fun marcarComoAceptado(
        context: Context,
        evidencia: EvidenciaPresupuesto,
        metodo: String,
        mensajeRespuesta: String = ""
    ): File {
        evidencia.estado = "ACEPTADO"
        evidencia.metodoAceptacion = metodo
        evidencia.mensajeAceptacion = mensajeRespuesta
        evidencia.fechaAceptacion = fechaActual()
        return guardarEvidenciaJson(context, evidencia)
    }
}
