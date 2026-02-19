package crystal.crystal.taller

import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.core.content.FileProvider
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Exporta resultados de cálculos masivos a archivos TXT y CSV.
 */
object ExportadorResultados {

    private fun timestamp(): String =
        SimpleDateFormat("yyyyMMdd_HHmm", Locale.getDefault()).format(Date())

    /**
     * Exporta como TXT agrupado por material y comparte.
     */
    fun exportarTxt(context: Context, resultados: List<ResultadoCalculo>, cliente: String) {
        val texto = buildString {
            append("RESUMEN TALLER - $cliente\n")
            append("Fecha: ${SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(Date())}\n")
            append("Items: ${resultados.size}\n")
            append("─".repeat(40) + "\n\n")

            append("Organización: por material\n")
            append(AgrupadorResultados.generarTextoAgrupado(resultados))
            append("\n" + "─".repeat(40) + "\n\n")

            append("Organización: por cliente\n")
            append(AgrupadorResultados.generarTextoPorCliente(resultados))
            append("\n" + "─".repeat(40) + "\n\n")

            append("Organización: por producto\n")
            append(AgrupadorResultados.generarTextoPorProducto(resultados))
        }

        val archivo = guardarArchivo(context, "Taller_${cliente}_${timestamp()}.txt", texto)
        if (archivo != null) {
            compartirArchivo(context, archivo, "text/plain")
        }
    }

    /**
     * Exporta como CSV con columnas: Calculadora, Tipo, Nombre, Medida, Cantidad.
     * Parsea el formato "medida = cantidad" de los TextViews.
     */
    fun exportarCsv(context: Context, resultados: List<ResultadoCalculo>, cliente: String) {
        val csv = buildString {
            append("Calculadora,Tipo,Nombre,Texto\n")
            for (r in resultados) {
                val calc = r.calculadora.replace(",", ";")
                for ((nombre, texto) in r.perfiles) {
                    if (texto.isNotBlank()) {
                        texto.lines().filter { it.isNotBlank() }.forEach { linea ->
                            append("$calc,Perfil,$nombre,\"${linea.trim()}\"\n")
                        }
                    }
                }
                if (r.vidrios.isNotBlank()) {
                    r.vidrios.lines().filter { it.isNotBlank() }.forEach { linea ->
                        append("$calc,Vidrio,Vidrio,\"${linea.trim()}\"\n")
                    }
                }
                for ((nombre, texto) in r.accesorios) {
                    if (texto.isNotBlank()) {
                        texto.lines().filter { it.isNotBlank() }.forEach { linea ->
                            append("$calc,Accesorio,$nombre,\"${linea.trim()}\"\n")
                        }
                    }
                }
            }
        }

        val archivo = guardarArchivo(context, "Taller_${cliente}_${timestamp()}.csv", csv)
        if (archivo != null) {
            compartirArchivo(context, archivo, "text/csv")
        }
    }

    private fun guardarArchivo(context: Context, nombre: String, contenido: String): File? {
        return try {
            val dir = File(context.filesDir, "exportaciones")
            if (!dir.exists()) dir.mkdirs()
            val archivo = File(dir, nombre)
            archivo.writeText(contenido)
            Toast.makeText(context, "Archivo: $nombre", Toast.LENGTH_SHORT).show()
            archivo
        } catch (e: Exception) {
            Toast.makeText(context, "Error al guardar: ${e.message}", Toast.LENGTH_SHORT).show()
            null
        }
    }

    private fun compartirArchivo(context: Context, archivo: File, mimeType: String) {
        try {
            val uri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                archivo
            )
            val intent = Intent(Intent.ACTION_SEND).apply {
                type = mimeType
                putExtra(Intent.EXTRA_STREAM, uri)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            context.startActivity(Intent.createChooser(intent, "Exportar"))
        } catch (e: Exception) {
            // Fallback: compartir como texto
            val intent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_TEXT, archivo.readText())
            }
            context.startActivity(Intent.createChooser(intent, "Compartir"))
        }
    }
}
