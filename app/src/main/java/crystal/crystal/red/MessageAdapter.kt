package crystal.crystal.red

import android.annotation.SuppressLint
import android.graphics.Color
import android.net.Uri
import android.text.format.DateFormat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.PopupMenu
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import crystal.crystal.R
import crystal.crystal.databinding.ItemMessageBinding
import crystal.crystal.red.interop.MeasuresMessageCodec

class MessageAdapter(
    private val usuario: String,
    private val onEditar: (Message) -> Unit,
    private val onEliminar: (Message) -> Unit,
    private val onMostrarArchivo: (Message) -> Unit,
    private val onOpciones: (Message, Boolean) -> Unit = { _, _ -> }
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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        MessageViewHolder(
            ItemMessageBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )

    override fun getItemCount(): Int = mensajes.size

    /**
     * Tipo con el que se pinta el mensaje. Los archivos Crystal enviados antes de que su tipo se
     * detectara llegaron marcados como "texto": se mostraban como el enlace pelado de Firebase y no
     * se podían abrir. Se reconocen por la extensión del archivo para que sigan sirviendo.
     */
    private fun tipoEfectivo(mensaje: Message): String {
        if (mensaje.tipo != "texto") return mensaje.tipo
        val nombre = mensaje.nombreArchivo
        if (nombre.isBlank()) return mensaje.tipo
        return when {
            nombre.endsWith(".crystalproyecto", ignoreCase = true) -> "proyecto_crystal"
            nombre.endsWith(".crystalmedidas", ignoreCase = true) -> "medidas_crystal"
            nombre.endsWith(".crystalcorte", ignoreCase = true) -> "corte_crystal"
            nombre.endsWith(".crystalplancha", ignoreCase = true) -> "plancha_crystal"
            else -> mensaje.tipo
        }
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: MessageViewHolder, position: Int) {
        val mensaje = mensajes[position]
        // No se copia el mensaje (perdería hasPendingWrites, que no viaja en el constructor): el tipo
        // corregido se usa como variable local para pintar.
        val tipo = tipoEfectivo(mensaje)
        val b = holder.binding
        val esMio = mensaje.from == usuario

        val contenedor = if (esMio) b.myMessageLayout else b.otherMessageLayout
        val textView = if (esMio) b.myMessageTextView else b.othersMessageTextView
        val frameLayout = if (esMio) b.myFrameLayout else b.otherFrameLayout
        val imageView = if (esMio) b.myImageView else b.otherImageView
        val playIcon = if (esMio) b.myPlayIcon else b.otherPlayIcon
        val fileButton = if (esMio) b.myFileButton else b.otherFileButton
        val hora = if (esMio) b.tvHorar else b.tvHorad
        val check = b.tvCheck

        b.myMessageLayout.visibility = if (esMio) View.VISIBLE else View.GONE
        b.otherMessageLayout.visibility = if (esMio) View.GONE else View.VISIBLE

        textView.visibility = View.GONE
        frameLayout.visibility = View.GONE
        imageView.visibility = View.GONE
        playIcon.visibility = View.GONE
        fileButton.visibility = View.GONE
        contenedor.background = null
        contenedor.backgroundTintList = null

        if (tipo in listOf("imagen", "video", "audio", "pdf", "presupuesto", "archivo", "medidas_crystal", "corte_crystal", "plancha_crystal", "proyecto_crystal")) {
            if (esMio) {
                contenedor.setBackgroundResource(R.drawable.corner)
                contenedor.backgroundTintList =
                    ContextCompat.getColorStateList(holder.itemView.context, R.color.colort)
            } else {
                contenedor.setBackgroundResource(R.drawable.corne)
                contenedor.backgroundTintList =
                    ContextCompat.getColorStateList(holder.itemView.context, R.color.rojot)
            }
        }

        if (mensaje.deletedForEveryone) {
            textView.visibility = View.VISIBLE
            textView.text = "mensaje borrado."
        } else {
            when (tipo) {
                "texto", "medidas" -> {
                    textView.visibility = View.VISIBLE
                    val parsedMeasures = MeasuresMessageCodec.parse(mensaje.message)
                     if (parsedMeasures != null) {
                         textView.text = "Lista de medidas detectada\n\n" +
                             "Producto: ${parsedMeasures.productName}\n" +
                             "Elementos: ${parsedMeasures.items.size}\n\n" +
                             "Toca para importar"
                         textView.setTextColor(ContextCompat.getColor(holder.itemView.context, R.color.negro))
                         contenedor.setBackgroundResource(if (esMio) R.drawable.corner else R.drawable.corne)
                         contenedor.backgroundTintList = ContextCompat.getColorStateList(
                             holder.itemView.context,
                             if (esMio) R.color.colort else R.color.verdet
                         )
                         textView.setOnClickListener { onMostrarArchivo(mensaje) }
                         contenedor.setOnClickListener { onMostrarArchivo(mensaje) }
                     } else {
                        textView.text = mensaje.message
                        textView.setTextColor(ContextCompat.getColor(holder.itemView.context, R.color.negro))
                        textView.setOnClickListener(null)
                        contenedor.setOnClickListener(null)
                    }
                }
                "imagen" -> {
                    frameLayout.visibility = View.VISIBLE
                    imageView.visibility = View.VISIBLE
                    Glide.with(holder.itemView.context)
                        .load(mensaje.message)
                        .placeholder(R.drawable.play)
                        .into(imageView)
                    imageView.setOnClickListener { onMostrarArchivo(mensaje) }
                }
                "video" -> {
                    frameLayout.visibility = View.VISIBLE
                    imageView.visibility = View.VISIBLE
                    playIcon.visibility = View.VISIBLE
                    Glide.with(holder.itemView.context)
                        .asBitmap()
                        .load(mensaje.message)
                        .frame(1_000_000)
                        .placeholder(R.drawable.play)
                        .into(imageView)
                    frameLayout.setOnClickListener { onMostrarArchivo(mensaje) }
                }
                "audio", "pdf", "archivo", "medidas_crystal", "corte_crystal", "plancha_crystal", "proyecto_crystal" -> {
                    fileButton.visibility = View.VISIBLE
                    val nombreMostrar = mensaje.nombreArchivo.takeIf { it.isNotEmpty() }
                        ?: extraerNombreDesdeUrl(mensaje.message)
                    fileButton.text = when (tipo) {
                        "audio" -> "Musica $nombreMostrar"
                        "pdf" -> "PDF $nombreMostrar"
                        "medidas_crystal" -> "Medidas Crystal\n$nombreMostrar\nToca para abrir"
                        "corte_crystal" -> "Corte Crystal\n$nombreMostrar\nToca para abrir"
                        "plancha_crystal" -> "Corte Plancha Crystal\n$nombreMostrar\nToca para abrir"
                        "proyecto_crystal" -> "Proyecto Crystal\n$nombreMostrar\nToca para abrir"
                        // Los mensajes viejos llegaron como "archivo" (o incluso como texto) porque el
                        // tipo no se detectaba; se reconocen por la extensión para que sigan abriendo.
                        "archivo" -> if (nombreMostrar.endsWith(".crystalmedidas", ignoreCase = true)) {
                            "Medidas Crystal\n$nombreMostrar\nToca para abrir"
                        } else if (nombreMostrar.endsWith(".crystalcorte", ignoreCase = true)) {
                            "Corte Crystal\n$nombreMostrar\nToca para abrir"
                        } else if (nombreMostrar.endsWith(".crystalplancha", ignoreCase = true)) {
                            "Corte Plancha Crystal\n$nombreMostrar\nToca para abrir"
                        } else if (nombreMostrar.endsWith(".crystalproyecto", ignoreCase = true)) {
                            "Proyecto Crystal\n$nombreMostrar\nToca para abrir"
                        } else {
                            "Archivo $nombreMostrar"
                        }
                        else -> nombreMostrar
                    }
                    fileButton.setOnClickListener { onMostrarArchivo(mensaje) }
                }
                "presupuesto" -> {
                    fileButton.visibility = View.VISIBLE
                    if (mensaje.message.startsWith("ENVIANDO") || mensaje.message.startsWith("CARGANDO")) {
                        fileButton.text = mensaje.message
                        fileButton.setTextColor(Color.GRAY)
                        fileButton.isEnabled = false
                    } else if (mensaje.message.startsWith("Error")) {
                        fileButton.text = "Error ${mensaje.message}"
                        fileButton.setTextColor(Color.RED)
                        fileButton.isEnabled = false
                     } else {
                         fileButton.text = "Presupuesto\nArchivo ${mensaje.nombreArchivo}\nToca para abrir"
                         fileButton.setTextColor(ContextCompat.getColor(holder.itemView.context, R.color.negro))
                         fileButton.isEnabled = true
                         fileButton.backgroundTintList = ContextCompat.getColorStateList(
                             holder.itemView.context,
                             if (esMio) R.color.colort else R.color.verdet
                         )
                         fileButton.setOnClickListener { onMostrarArchivo(mensaje) }
                     }
                 }
                else -> {
                    textView.visibility = View.VISIBLE
                    textView.text = mensaje.message
                }
            }
        }

        hora.text = mensaje.dob?.let { DateFormat.format("hh:mm a", it).toString() } ?: ""

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

        // Indicador de pedido en línea (en espera / atendido).
        if (mensaje.esPedido && !mensaje.deletedForEveryone) {
            val tag = when (mensaje.estadoPedido) {
                "en_espera" -> "🛒 PEDIDO · EN ESPERA"
                "cogido" -> "🛒 PEDIDO · Atendido por ${mensaje.atendidoNombre.ifBlank { "alguien" }}"
                else -> "🛒 PEDIDO"
            }
            textView.visibility = View.VISIBLE
            val base = textView.text?.toString().orEmpty()
            textView.text = if (base.isBlank()) tag else "$tag\n$base"
        }

        attachLongPressMenu(holder.itemView, mensaje, esMio)
        attachLongPressMenu(contenedor, mensaje, esMio)
        attachLongPressMenu(textView, mensaje, esMio)
        attachLongPressMenu(frameLayout, mensaje, esMio)
        attachLongPressMenu(fileButton, mensaje, esMio)
    }

    private fun attachLongPressMenu(target: View, mensaje: Message, esMio: Boolean) {
        target.setOnLongClickListener {
            if (mensaje.deletedForEveryone) return@setOnLongClickListener false
            onOpciones(mensaje, esMio)
            true
        }
    }

    private fun extraerNombreDesdeUrl(url: String): String {
        return try {
            Uri.parse(url).lastPathSegment?.substringAfterLast("/") ?: "archivo"
        } catch (_: Exception) {
            "archivo"
        }
    }

    class MessageViewHolder(val binding: ItemMessageBinding) :
        RecyclerView.ViewHolder(binding.root)
}
