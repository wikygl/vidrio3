package crystal.crystal.ocr

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import crystal.crystal.Listado
import crystal.crystal.R
import crystal.crystal.databinding.ActivityOcrBinding
import java.io.IOException
import java.util.regex.Pattern

class OcrActivity : AppCompatActivity() {

    private lateinit var binding: ActivityOcrBinding
    private var uriImagenActual: Uri? = null
    private var bitmapImagenActual: Bitmap? = null

    // Lista de elementos extraídos
    private val elementosExtraidos = ArrayList<Listado>()

    // Constantes
    private companion object {
        const val SOLICITUD_CAPTURAR_IMAGEN = 101
        const val SOLICITUD_GALERIA_IMAGEN = 102
        const val SOLICITUD_PERMISO_CAMARA = 103
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOcrBinding.inflate(layoutInflater)
        setContentView(binding.root)

        configurarInterfaz()
        verificarPermisos()
    }

    private fun configurarInterfaz() {
        // Botón volver
        binding.btnVolver.setOnClickListener {
            finish()
        }

        // Botones de cámara y galería
        binding.btnCamara.setOnClickListener {
            mostrarOpcionesFoto()
        }

        // Botón procesar imagen
        binding.btnProcesar.setOnClickListener {
            procesarImagen()
        }

        // Botón limpiar
        binding.btnLimpiar.setOnClickListener {
            limpiarTodo()
        }

        // Botón finalizar
        binding.btnFinalizar.setOnClickListener {
            finalizarOcr()
        }

        // Botón cancelar
        binding.btnCancelar.setOnClickListener {
            setResult(RESULT_CANCELED)
            finish()
        }

        actualizarInterfaz()
    }

    private fun verificarPermisos() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
            != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.CAMERA),
                SOLICITUD_PERMISO_CAMARA
            )
        }
    }

    private fun mostrarOpcionesFoto() {
        val opciones = arrayOf("📷 Tomar foto", "🖼️ Seleccionar de galería")

        val constructor = AlertDialog.Builder(this)
        constructor.setTitle("Seleccionar imagen")
        constructor.setItems(opciones) { _, cual ->
            when (cual) {
                0 -> abrirCamara()
                1 -> abrirGaleria()
            }
        }
        constructor.show()
    }

    @SuppressLint("QueryPermissionsNeeded")
    private fun abrirCamara() {
        val intentTomarFoto = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (intentTomarFoto.resolveActivity(packageManager) != null) {
            startActivityForResult(intentTomarFoto, SOLICITUD_CAPTURAR_IMAGEN)
        } else {
            Toast.makeText(this, "No se encontró una aplicación de cámara", Toast.LENGTH_SHORT).show()
        }
    }

    @SuppressLint("IntentReset")
    private fun abrirGaleria() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        intent.type = "image/*"
        startActivityForResult(intent, SOLICITUD_GALERIA_IMAGEN)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when (requestCode) {
            SOLICITUD_CAPTURAR_IMAGEN -> {
                if (resultCode == RESULT_OK) {
                    val imagenBitmap = data?.extras?.get("data") as? Bitmap
                    if (imagenBitmap != null) {
                        bitmapImagenActual = imagenBitmap
                        uriImagenActual = null
                        mostrarImagen(imagenBitmap)
                    }
                }
            }
            SOLICITUD_GALERIA_IMAGEN -> {
                if (resultCode == RESULT_OK && data != null) {
                    val uriImagen = data.data
                    if (uriImagen != null) {
                        uriImagenActual = uriImagen
                        bitmapImagenActual = null
                        mostrarImagen(uriImagen)
                    }
                }
            }
        }
    }

    private fun mostrarImagen(bitmap: Bitmap) {
        binding.ivVistaPrevia.setImageBitmap(bitmap)
        binding.tvEstado.text = "📷 Imagen cargada - Presiona 'Procesar' para extraer texto"
        binding.btnProcesar.isEnabled = true
        actualizarInterfaz()
    }

    private fun mostrarImagen(uri: Uri) {
        Glide.with(this)
            .load(uri)
            .into(binding.ivVistaPrevia)

        binding.tvEstado.text = "📷 Imagen cargada - Presiona 'Procesar' para extraer texto"
        binding.btnProcesar.isEnabled = true
        actualizarInterfaz()
    }

    private fun procesarImagen() {
        binding.tvEstado.text = "🔄 Procesando imagen..."
        binding.btnProcesar.isEnabled = false

        val imagen = try {
            when {
                bitmapImagenActual != null -> InputImage.fromBitmap(bitmapImagenActual!!, 0)
                uriImagenActual != null -> InputImage.fromFilePath(this, uriImagenActual!!)
                else -> {
                    Toast.makeText(this, "No hay imagen para procesar", Toast.LENGTH_SHORT).show()
                    return
                }
            }
        } catch (e: IOException) {
            Toast.makeText(this, "Error al cargar imagen: ${e.message}", Toast.LENGTH_SHORT).show()
            return
        }

        // Usar ML Kit para reconocimiento de texto
        val reconocedor = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)

        reconocedor.process(imagen)
            .addOnSuccessListener { textoVision ->
                val textoExtraido = textoVision.text
                Log.d("OCR", "Texto extraído: $textoExtraido")

                binding.tvTextoExtraido.text = textoExtraido
                binding.tvEstado.text = "✅ Texto extraído - Procesando medidas..."

                // Procesar el texto para extraer medidas
                procesarTextoAMedidas(textoExtraido)
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error en OCR: ${e.message}", Toast.LENGTH_SHORT).show()
                binding.tvEstado.text = "❌ Error al procesar imagen"
                binding.btnProcesar.isEnabled = true
            }
    }

    private fun procesarTextoAMedidas(texto: String) {
        try {
            val medidasEncontradas = extraerMedidasDeTexto(texto)

            if (medidasEncontradas.isNotEmpty()) {
                elementosExtraidos.clear()

                medidasEncontradas.forEach { (producto, medidas) ->
                    medidas.forEach { (med1, med2, cantidad) ->
                        val listado = crearListadoDesdeOcr(med1, med2, cantidad, producto)
                        elementosExtraidos.add(listado)
                    }
                }

                binding.tvEstado.text = "🎯 ${elementosExtraidos.size} elementos encontrados"
                actualizarListaElementos()
            } else {
                binding.tvEstado.text = "⚠️ No se encontraron medidas válidas en el texto"
            }

        } catch (e: Exception) {
            Toast.makeText(this, "Error procesando medidas: ${e.message}", Toast.LENGTH_SHORT).show()
            binding.tvEstado.text = "❌ Error al procesar medidas"
        }

        binding.btnProcesar.isEnabled = true
        actualizarInterfaz()
    }

    private fun extraerMedidasDeTexto(texto: String): Map<String, List<Triple<Float, Float, Float>>> {
        val resultado = mutableMapOf<String, List<Triple<Float, Float, Float>>>()

        // Dividir texto en líneas
        val lineas = texto.split("\n").map { it.trim() }.filter { it.isNotEmpty() }

        // Patrones para reconocer medidas
        val patronesMedidas = listOf(
            // Patrón: número x número = número
            Pattern.compile("""(\d+(?:[.,]\d+)?)\s*[xX×]\s*(\d+(?:[.,]\d+)?)\s*[=]\s*(\d+(?:[.,]\d+)?)"""),
            // Patrón: número por número igual número
            Pattern.compile("""(\d+(?:[.,]\d+)?)\s*por\s*(\d+(?:[.,]\d+)?)\s*igual\s*(\d+(?:[.,]\d+)?)"""),
            // Patrón: número × número - número (sin igual)
            Pattern.compile("""(\d+(?:[.,]\d+)?)\s*[xX×]\s*(\d+(?:[.,]\d+)?)\s*[-–]\s*(\d+(?:[.,]\d+)?)"""),
            // Patrón: número x número : número
            Pattern.compile("""(\d+(?:[.,]\d+)?)\s*[xX×]\s*(\d+(?:[.,]\d+)?)\s*[:]\s*(\d+(?:[.,]\d+)?)""")
        )

        var productoActual = "Elemento OCR"
        val medidasTemporales = mutableListOf<Triple<Float, Float, Float>>()

        for (linea in lineas) {
            var medidaEncontrada = false

            // Buscar patrones de medidas en la línea
            for (patron in patronesMedidas) {
                val buscador = patron.matcher(linea)
                if (buscador.find()) {
                    try {
                        val med1 = buscador.group(1)?.replace(",", ".")?.toFloatOrNull()
                        val med2 = buscador.group(2)?.replace(",", ".")?.toFloatOrNull()
                        val cantidad = buscador.group(3)?.replace(",", ".")?.toFloatOrNull()

                        if (med1 != null && med2 != null && cantidad != null) {
                            medidasTemporales.add(Triple(med1, med2, cantidad))
                            medidaEncontrada = true
                            Log.d("OCR", "Medida encontrada: $med1 x $med2 = $cantidad")
                        }
                    } catch (e: Exception) {
                        Log.w("OCR", "Error procesando medida en línea: $linea")
                    }
                }
            }

            // Si la línea no contiene medidas, podría ser un producto
            if (!medidaEncontrada && linea.length > 3) {
                // Filtrar líneas que parezcan productos
                val lineaLimpia = linea.replace(Regex("[^a-zA-ZáéíóúÁÉÍÓÚñÑ\\s]"), "").trim()
                if (lineaLimpia.length >= 3) {
                    // Si ya tenemos medidas acumuladas, guardarlas con el producto anterior
                    if (medidasTemporales.isNotEmpty()) {
                        resultado[productoActual] = medidasTemporales.toList()
                        medidasTemporales.clear()
                    }
                    productoActual = lineaLimpia.take(50) // Limitar longitud
                }
            }
        }

        // Guardar las últimas medidas si las hay
        if (medidasTemporales.isNotEmpty()) {
            resultado[productoActual] = medidasTemporales.toList()
        }

        return resultado
    }

    private fun crearListadoDesdeOcr(med1: Float, med2: Float, cantidad: Float, producto: String): Listado {
        val precio = 0f

        // Calcular valores usando conversión a metros (asumiendo centímetros)
        val piescua = (med1 / 100) * (med2 / 100) * 11.1f * cantidad
        val metcua = (med1 / 100) * (med2 / 100)
        val metli = (med1 / 100) * cantidad
        val metcub = (med1 / 100) * (med2 / 100) * 1f
        val peri = (((med1 * 2) + (med2 * 2)) / 100)
        val costo = piescua * precio

        return Listado(
            escala = "p2",
            uni = "Centímetros",
            medi1 = med1,
            medi2 = med2,
            medi3 = 1f,
            canti = cantidad,
            piescua = piescua,
            precio = precio,
            costo = costo,
            producto = producto,
            peri = peri,
            metcua = metcua,
            metli = metli,
            metcub = metcub,
            color = android.graphics.Color.GREEN, // Verde para OCR
            uri = ""
        )
    }

    @SuppressLint("SetTextI18n")
    private fun actualizarListaElementos() {
        if (elementosExtraidos.isEmpty()) {
            binding.tvElementosEncontrados.text = "No se han encontrado elementos aún"
        } else {
            val texto = StringBuilder()
            elementosExtraidos.forEachIndexed { indice, elemento ->
                texto.append("${indice + 1}. ${elemento.medi1} x ${elemento.medi2} = ${elemento.canti}, ${elemento.producto}\n")
            }
            binding.tvElementosEncontrados.text = texto.toString()
        }
    }

    @SuppressLint("SetTextI18n")
    private fun actualizarInterfaz() {
        binding.btnFinalizar.isEnabled = elementosExtraidos.isNotEmpty()

        // Actualizar contador
        if (elementosExtraidos.isNotEmpty()) {
            binding.tvContador.text = "📊 ${elementosExtraidos.size} elementos listos"
        } else {
            binding.tvContador.text = "📊 0 elementos"
        }
    }

    private fun limpiarTodo() {
        uriImagenActual = null
        bitmapImagenActual = null
        elementosExtraidos.clear()

        binding.ivVistaPrevia.setImageResource(R.drawable.ic_placeholder_camara)
        binding.tvTextoExtraido.text = "El texto extraído aparecerá aquí..."
        binding.tvElementosEncontrados.text = "No se han encontrado elementos aún"
        binding.tvEstado.text = "📷 Selecciona una imagen para comenzar el análisis OCR"
        binding.btnProcesar.isEnabled = false

        actualizarInterfaz()

        Toast.makeText(this, "✅ Todo limpiado", Toast.LENGTH_SHORT).show()
    }

    private fun finalizarOcr() {
        if (elementosExtraidos.isNotEmpty()) {
            val intent = Intent()
            intent.putExtra("elementos_ocr", elementosExtraidos)
            setResult(RESULT_OK, intent)
            finish()
        } else {
            Toast.makeText(this, "No hay elementos para enviar", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onRequestPermissionsResult(codigoSolicitud: Int, permisos: Array<out String>, resultadosPermisos: IntArray) {
        super.onRequestPermissionsResult(codigoSolicitud, permisos, resultadosPermisos)
        when (codigoSolicitud) {
            SOLICITUD_PERMISO_CAMARA -> {
                if (resultadosPermisos.isNotEmpty() && resultadosPermisos[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "Permiso de cámara concedido", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "Permiso de cámara denegado", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}