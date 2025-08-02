package crystal.crystal.red

import Message
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.text.format.DateFormat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.appcompat.widget.PopupMenu
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import crystal.crystal.R
import crystal.crystal.databinding.ItemMessageBinding

class MessageAdapter(
    private val usuario: String,
    private val onEditar: (Message) -> Unit,
    private val onEliminar: (Message) -> Unit,
    private val onMostrarArchivo: (Message) -> Unit   // ← Nuevo
) : RecyclerView.Adapter<MessageAdapter.MessageViewHolder>() {

    private val mensajes = mutableListOf<Message>()

    @SuppressLint("NotifyDataSetChanged")
    fun setData(lista: List<Message>) {
        mensajes.clear()
        mensajes.addAll(lista)
        notifyDataSetChanged()
    }

    fun addMensajeTemporal(m: Message) {
        mensajes.add(m)
        notifyItemInserted(mensajes.size - 1)
    }

    private fun esFormatoMedidas(mensaje: String): Boolean {
        val lineas = mensaje.trim().split("\n").filter { it.isNotBlank() }

        if (lineas.size < 2) return false

        // Regex para detectar medidas: número x número = número (con decimales)
        val regexMedida = Regex("""^\s*\d+(\.\d+)?\s*[xX]\s*\d+(\.\d+)?\s*=\s*\d+(\.\d+)?\s*$""")

        // Buscar la primera línea que sea una medida
        var primeraMedida = -1
        for (i in lineas.indices) {
            if (regexMedida.matches(lineas[i].trim())) {
                primeraMedida = i
                break
            }
        }

        // Debe haber al menos una línea antes (producto) y debe encontrar medidas
        if (primeraMedida <= 0) return false

        // Desde la primera medida en adelante, TODAS deben ser medidas
        for (i in primeraMedida until lineas.size) {
            if (!regexMedida.matches(lineas[i].trim())) {
                return false
            }
        }

        return true
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        MessageViewHolder(
            ItemMessageBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )

    override fun getItemCount(): Int = mensajes.size

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: MessageViewHolder, position: Int) {
        val mensaje = mensajes[position]
        val b = holder.binding

        val esMio = mensaje.from == usuario

        val contenedor    = if (esMio) b.myMessageLayout else b.otherMessageLayout
        val textView      = if (esMio) b.myMessageTextView else b.othersMessageTextView
        val frameLayout   = if (esMio) b.myFrameLayout else b.otherFrameLayout
        val imageView     = if (esMio) b.myImageView else b.otherImageView
        val playIcon      = if (esMio) b.myPlayIcon else b.otherPlayIcon
        val fileButton    = if (esMio) b.myFileButton else b.otherFileButton
        val hora          = if (esMio) b.tvHorar else b.tvHorad
        val check         = b.tvCheck

        // Mostrar contenedor correcto
        b.myMessageLayout.visibility    = if (esMio) View.VISIBLE else View.GONE
        b.otherMessageLayout.visibility = if (esMio) View.GONE else View.VISIBLE

        // Ocultar todos los elementos
        textView.visibility    = View.GONE
        frameLayout.visibility = View.GONE
        imageView.visibility   = View.GONE
        playIcon.visibility    = View.GONE
        fileButton.visibility  = View.GONE

        // Limpiar fondo de contenedor
        contenedor.background = null
        contenedor.backgroundTintList = null

        // **MODIFICAR ESTA LÍNEA PARA INCLUIR PRESUPUESTOS:**
        // Aplicar fondo si es multimedia
        if (mensaje.tipo in listOf("imagen","video","audio","pdf","presupuesto")) {
            if (esMio) {
                contenedor.setBackgroundResource(R.drawable.corner)
                contenedor.backgroundTintList = ContextCompat.getColorStateList(holder.itemView.context, R.color.colort)
            } else {
                contenedor.setBackgroundResource(R.drawable.corne)
                contenedor.backgroundTintList = ContextCompat.getColorStateList(holder.itemView.context, R.color.rojot)
            }
        }

        if (mensaje.deletedForEveryone) {
            // Mensaje borrado globalmente
            textView.visibility = View.VISIBLE
            textView.text = "mensaje borrado."
        } else {
            when (mensaje.tipo) {
                "texto" -> {
                    textView.visibility = View.VISIBLE

                    // Verificar si es formato de medidas
                    if (esFormatoMedidas(mensaje.message)) {
                        // Mensaje con formato de medidas detectado
                        val lineas = mensaje.message.trim().split("\n").filter { it.isNotBlank() }
                        val producto = lineas[0].trim()
                        val cantidadMedidas = lineas.size - 1

                        // Mostrar texto con indicador especial
                        val textoMostrar = "📐 Lista de medidas detectada\n\n" +
                                "Producto: $producto\n" +
                                "Elementos: $cantidadMedidas\n\n" +
                                "Toca para importar ↗️"

                        textView.text = textoMostrar
                        textView.setTextColor(ContextCompat.getColor(holder.itemView.context, R.color.color))

                        // Cambiar fondo para destacar
                        contenedor.setBackgroundResource(R.drawable.corner)
                        contenedor.backgroundTintList = ContextCompat.getColorStateList(
                            holder.itemView.context,
                            if (esMio) R.color.colort else R.color.verde
                        )

                        // Click para importar medidas
                        textView.setOnClickListener {
                            onMostrarArchivo(mensaje) // Reutilizamos el callback existente
                        }

                    } else {
                        // Mensaje de texto normal
                        textView.text = mensaje.message
                        textView.setTextColor(ContextCompat.getColor(holder.itemView.context, R.color.negro))
                        textView.setOnClickListener(null) // Quitar click listener
                    }
                }
                "imagen" -> {
                    frameLayout.visibility = View.VISIBLE
                    imageView.visibility   = View.VISIBLE

                    // Cargar imagen
                    Glide.with(holder.itemView.context)
                        .load(mensaje.message)
                        .placeholder(R.drawable.play)
                        .into(imageView)

                    // Al pulsar la imagen, abrir el visor
                    imageView.setOnClickListener { onMostrarArchivo(mensaje) }
                }
                "video" -> {
                    frameLayout.visibility = View.VISIBLE
                    imageView.visibility   = View.VISIBLE
                    playIcon.visibility    = View.VISIBLE

                    // Cargar thumbnail de vídeo
                    Glide.with(holder.itemView.context)
                        .asBitmap()
                        .load(mensaje.message)
                        .frame(1_000_000)
                        .placeholder(R.drawable.play)
                        .into(imageView)

                    // Al pulsar el contenedor, abrir el visor
                    frameLayout.setOnClickListener { onMostrarArchivo(mensaje) }
                }
                "audio", "pdf" -> {
                    fileButton.visibility = View.VISIBLE

                    // Nombre del archivo (o extraído de la URL)
                    val nombreMostrar = mensaje.nombreArchivo
                        .takeIf { it.isNotEmpty() }
                        ?: extraerNombreDesdeUrl(mensaje.message)

                    fileButton.text = when (mensaje.tipo) {
                        "audio" -> "🎵 $nombreMostrar"
                        "pdf"   -> "📄 $nombreMostrar"
                        else    -> nombreMostrar
                    }

                    // Al pulsar el botón, abrir el visor
                    fileButton.setOnClickListener { onMostrarArchivo(mensaje) }
                }

                // **AGREGAR ESTE NUEVO CASO PARA PRESUPUESTOS:**
                "presupuesto" -> {
                    fileButton.visibility = View.VISIBLE

                    // Texto personalizado para presupuestos
                    if (mensaje.message.startsWith("ENVIANDO") || mensaje.message.startsWith("CARGANDO")) {
                        fileButton.text = mensaje.message
                        fileButton.setTextColor(Color.GRAY)
                        fileButton.isEnabled = false
                    } else if (mensaje.message.startsWith("Error")) {
                        fileButton.text = "❌ ${mensaje.message}"
                        fileButton.setTextColor(Color.RED)
                        fileButton.isEnabled = false
                    } else {
                        val nombreArchivo = mensaje.nombreArchivo
                        fileButton.text = "📋 Presupuesto\n📄 $nombreArchivo\n💰 Toca para abrir"
                        fileButton.setTextColor(if (esMio) Color.WHITE else Color.BLACK)
                        fileButton.isEnabled = true

                        // Aplicar fondo especial para presupuestos
                        if (esMio) {
                            fileButton.setBackgroundColor(ContextCompat.getColor(holder.itemView.context, R.color.color))
                        } else {
                            fileButton.setBackgroundColor(ContextCompat.getColor(holder.itemView.context, R.color.verde))
                        }

                        // Al pulsar el botón, abrir el presupuesto
                        fileButton.setOnClickListener { onMostrarArchivo(mensaje) }
                    }
                }

                else -> {
                    // Cualquier otro tipo cae aquí
                    textView.visibility = View.VISIBLE
                    textView.text = mensaje.message
                }
            }
        }

        // Mostrar hora
        hora.text = mensaje.dob
            ?.let { DateFormat.format("hh:mm a", it).toString() }
            ?: ""

        // Mostrar estado de envío (solo en mis mensajes)
        if (esMio) {
            check.visibility = View.VISIBLE
            when {
                mensaje.hasPendingWrites -> {
                    check.text = "⌛"
                    check.setTextColor(ContextCompat.getColor(holder.itemView.context, R.color.negro))
                }
                mensaje.leido -> {
                    check.text = "✔✔"
                    check.setTextColor(ContextCompat.getColor(holder.itemView.context, R.color.color))
                }
                mensaje.entregado -> {
                    check.text = "✔✔"
                    check.setTextColor(ContextCompat.getColor(holder.itemView.context, R.color.negro))
                }
                else -> {
                    check.text = "✔"
                    check.setTextColor(ContextCompat.getColor(holder.itemView.context, R.color.negro))
                }
            }
        } else {
            check.visibility = View.GONE
        }

        // Menú de editar/eliminar en mis mensajes
        holder.itemView.setOnLongClickListener { v ->
            if (!esMio) return@setOnLongClickListener false
            val popup = PopupMenu(v.context, v)
            popup.menuInflater.inflate(R.menu.menu_mensaje, popup.menu)

            // **MODIFICAR PARA PRESUPUESTOS - No se pueden editar:**
            popup.menu.findItem(R.id.action_editar).isVisible =
                !mensaje.deletedForEveryone && mensaje.tipo != "presupuesto"

            popup.setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.action_editar -> {
                        if (!mensaje.deletedForEveryone && mensaje.tipo != "presupuesto") {
                            onEditar(mensaje)
                        }
                        true
                    }
                    R.id.action_eliminar -> {
                        onEliminar(mensaje)
                        true
                    }
                    else -> false
                }
            }
            popup.show()
            true
        }
    }

    private fun extraerNombreDesdeUrl(url: String): String {
        return try {
            Uri.parse(url).lastPathSegment?.substringAfterLast("/") ?: "archivo"
        } catch (e: Exception) {
            "archivo"
        }
    }

    private fun abrirArchivo(context: Context, url: String) {
        val intent = Intent(Intent.ACTION_VIEW)
        intent.setDataAndType(Uri.parse(url), "*/*")
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(intent)
    }

    private fun mostrarMiniaturaVideoConPlay(context: Context, videoUrl: String, imageView: ImageView) {
        Glide.with(context)
            .asBitmap()
            .load(videoUrl)
            .frame(1000000) // 1 segundo
            .placeholder(R.drawable.scan)
            .into(imageView)
    }

    class MessageViewHolder(val binding: ItemMessageBinding) :
        RecyclerView.ViewHolder(binding.root)
}






