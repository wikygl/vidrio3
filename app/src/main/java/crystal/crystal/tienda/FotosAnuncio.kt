package crystal.crystal.tienda

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import android.net.Uri
import java.io.ByteArrayOutputStream

/**
 * Las fotos de un anuncio, achicadas antes de subirlas.
 *
 * Lo único que cuesta de verdad en la Tienda es la SALIDA de las fotos cada vez que alguien mira
 * (ver docs/TIENDA_ANUNCIOS_PLAN.md, "Costos"). Una foto de celular pesa 3–5 MB; aquí se sube en dos
 * tamaños: la grande de 1280 px (~150 KB), que se ve al abrir el anuncio, y la miniatura de 400 px
 * (~25 KB), que es la única que baja la lista.
 */
object FotosAnuncio {

    const val LADO_GRANDE = 1280
    const val LADO_MINI = 400

    /** La foto lista para subir: (grande, miniatura) en JPEG. Null si no se pudo leer. */
    fun preparar(context: Context, uri: Uri): Pair<ByteArray, ByteArray>? = runCatching {
        val grande = leerReducida(context, uri, LADO_GRANDE) ?: return null
        val mini = escalar(grande, LADO_MINI)
        val a = jpeg(grande, 78)
        val b = jpeg(mini, 70)
        if (mini !== grande) mini.recycle()
        grande.recycle()
        a to b
    }.getOrNull()

    private fun jpeg(bmp: Bitmap, calidad: Int): ByteArray =
        ByteArrayOutputStream().also { bmp.compress(Bitmap.CompressFormat.JPEG, calidad, it) }.toByteArray()

    /** Lee la imagen ya reducida (sin cargar los 12 MP en memoria) y derecha según su EXIF. */
    private fun leerReducida(context: Context, uri: Uri, lado: Int): Bitmap? {
        val cr = context.contentResolver
        val limites = BitmapFactory.Options().apply { inJustDecodeBounds = true }
        cr.openInputStream(uri)?.use { BitmapFactory.decodeStream(it, null, limites) }
        if (limites.outWidth <= 0 || limites.outHeight <= 0) return null
        var muestra = 1
        while (maxOf(limites.outWidth, limites.outHeight) / (muestra * 2) >= lado) muestra *= 2
        val opciones = BitmapFactory.Options().apply { inSampleSize = muestra }
        val bmp = cr.openInputStream(uri)?.use { BitmapFactory.decodeStream(it, null, opciones) } ?: return null
        val girada = enderezar(bmp, rotacion(context, uri))
        return escalar(girada, lado).also { if (it !== girada) girada.recycle() }
    }

    /** Achica para que el lado mayor sea [lado]; si ya es menor, la deja como está. */
    private fun escalar(bmp: Bitmap, lado: Int): Bitmap {
        val mayor = maxOf(bmp.width, bmp.height)
        if (mayor <= lado) return bmp
        val f = lado.toFloat() / mayor
        return Bitmap.createScaledBitmap(bmp, (bmp.width * f).toInt().coerceAtLeast(1), (bmp.height * f).toInt().coerceAtLeast(1), true)
    }

    /** Las fotos del celular vienen acostadas y el EXIF dice cuánto girarlas. */
    private fun rotacion(context: Context, uri: Uri): Int = runCatching {
        context.contentResolver.openInputStream(uri)?.use { s ->
            when (ExifInterface(s).getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL)) {
                ExifInterface.ORIENTATION_ROTATE_90 -> 90
                ExifInterface.ORIENTATION_ROTATE_180 -> 180
                ExifInterface.ORIENTATION_ROTATE_270 -> 270
                else -> 0
            }
        } ?: 0
    }.getOrDefault(0)

    private fun enderezar(bmp: Bitmap, grados: Int): Bitmap {
        if (grados == 0) return bmp
        val m = Matrix().apply { postRotate(grados.toFloat()) }
        return Bitmap.createBitmap(bmp, 0, 0, bmp.width, bmp.height, m, true).also { if (it !== bmp) bmp.recycle() }
    }
}
