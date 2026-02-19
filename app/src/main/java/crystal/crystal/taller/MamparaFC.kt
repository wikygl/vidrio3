package crystal.crystal.taller

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import crystal.crystal.casilla.DialogosProyecto
import crystal.crystal.casilla.MapStorage
import crystal.crystal.casilla.ProyectoManager
import crystal.crystal.casilla.ProyectoUIHelper
import crystal.crystal.databinding.ActivityMamparaFcBinding

class MamparaFC : AppCompatActivity() {

    lateinit var binding: ActivityMamparaFcBinding
    private val mapListas = mutableMapOf<String, MutableList<MutableList<String>>>()
    private var primerClickArchivarRealizado = false

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMamparaFcBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnCalcular.setOnClickListener {
            try {
                val corrediza = binding.marco.text.toString().toFloat()
                val jun = binding.junk.text.toString().toFloat()
                val alto = binding.med2.text.toString().toFloat()

                val bastidor = if (binding.bast.text.toString().toFloat() == 0f) 8f
                else binding.bast.text.toString().toFloat()

                if (corrediza == 0f) {
                    binding.bastitxt.text = "${df1(zocaloTechoFijo())} = 4\n" +
                            "${df1(paranteCorredizo())} = 2\n" +
                            "${df1(paranteFijo())} = 2"
                } else {
                    binding.bastitxt.text = "${df1(zocaloTechoFijo())} = 2\n" +
                            "${df1(zocaloTechoCorre())} = 2\n" +
                            "${df1(paranteCorredizo())} = 2\n" +
                            "${df1(paranteFijo())} = 2"
                }

                if (jun == 0f) {
                    if (corrediza == 0f) {
                        binding.junkitxt.text = "${df1(zocaloTechoFijo())} = 8\n" +
                                "${df1(paranteCorredizo() - (2 * bastidor))} = 4\n" +
                                "${df1(paranteFijo() - (2 * bastidor))} = 4"
                    } else {
                        binding.junkitxt.text = "${df1(zocaloTechoFijo())} = 4\n" +
                                "${df1(zocaloTechoCorre())} = 4\n" +
                                "${df1(paranteCorredizo() - (2 * bastidor))} = 4\n" +
                                "${df1(paranteFijo() - (2 * bastidor))} = 4"
                    }
                } else {
                    if (corrediza == 0f) {
                        binding.junkitxt.text = "${df1(zocaloTechoFijo())} = 8\n" +
                                "${df1(paranteCorredizo() - junquilloAlto())} = 4\n" +
                                "${df1(paranteFijo() - junquilloAlto())} = 4"
                    } else {
                        binding.junkitxt.text = "${df1(zocaloTechoFijo())} = 4\n" +
                                "${df1(zocaloTechoCorre())} = 4\n" +
                                "${df1(paranteCorredizo() - junquilloAlto())} = 4\n" +
                                "${df1(paranteFijo() - junquilloAlto())} = 4"
                    }
                }

                binding.marcotxt.text = "${df1(alto)} = 2\n${df1(marcoSuperior())} = 1"
                binding.angtxt.text = "${df1(marcoSuperior())} = 2\n${df1(paranteFijo() - 1.5f)} = 1"
                binding.rieltxt.text = "${df1(marcoSuperior())} = 1"
                binding.vidriostxt.text = "${vidrioF()} = 1\n${vidrioC()} = 1"
                binding.referencias.text = referen()
                binding.porttxt.text = "${df1(paranteFijo() - 1.5f)} = 1"
                binding.txTe.text = "${angTapa()} = 1"

            } catch (e: Exception) {
                Toast.makeText(this, "Ingrese dato válido", Toast.LENGTH_SHORT).show()
            }
        }

        binding.btnArchivar.setOnClickListener {
            if (binding.marcotxt.text.isNullOrBlank()) {
                Toast.makeText(this, "Haz nuevo cálculo", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (!primerClickArchivarRealizado) {
                DialogosProyecto.mostrarDialogoSeleccionarParaArchivar(this, object : DialogosProyecto.ProyectoCallback {
                    override fun onProyectoSeleccionado(nombreProyecto: String) {
                        primerClickArchivarRealizado = true
                        val mapExistente = MapStorage.cargarProyecto(this@MamparaFC, nombreProyecto)
                        if (mapExistente != null) { mapListas.clear(); mapListas.putAll(mapExistente) }
                        archivarMapas()
                        Toast.makeText(this@MamparaFC, "Archivado", Toast.LENGTH_SHORT).show()
                        binding.med1.setText(""); binding.med2.setText("")
                    }
                    override fun onProyectoCreado(nombreProyecto: String) {
                        primerClickArchivarRealizado = true
                        archivarMapas()
                        Toast.makeText(this@MamparaFC, "Archivado", Toast.LENGTH_SHORT).show()
                        binding.med1.setText(""); binding.med2.setText("")
                    }
                    override fun onProyectoEliminado(nombreProyecto: String) {}
                })
            } else {
                if (!ProyectoManager.hayProyectoActivo()) {
                    primerClickArchivarRealizado = false
                    Toast.makeText(this, "No hay proyecto activo. Selecciona uno.", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
                archivarMapas()
                Toast.makeText(this, "Archivado", Toast.LENGTH_SHORT).show()
                binding.med1.setText(""); binding.med2.setText("")
            }
        }

        // Pre-carga desde presupuesto
        intent.getFloatExtra("ancho", -1f).let { if (it > 0) binding.med1.setText(df1(it)) }
        intent.getFloatExtra("alto", -1f).let { if (it > 0) binding.med2.setText(df1(it)) }
    }

    // ==================== ARCHIVAR ====================
    private fun obtenerPrefijo(): String = "MF"

    private fun archivarMapas() {
        val proyectoActivo = ProyectoManager.getProyectoActivo()
        if (proyectoActivo != null) {
            val mapExistente = MapStorage.cargarProyecto(this, proyectoActivo)
            mapListas.clear()
            if (mapExistente != null) mapListas.putAll(mapExistente)
        }

        val prefijo = obtenerPrefijo()
        val cant = intent.getFloatExtra("cantidad", 1f).toInt().coerceAtLeast(1)

        for (u in 1..cant) {
            val siguienteNumero = ProyectoManager.obtenerSiguienteContadorPorPrefijo(this, prefijo)
            val paqueteID = "v${siguienteNumero}${prefijo}"

            archivarConNombre("Marco", binding.marcotxt.text.toString(), paqueteID)
            archivarConNombre("Bastidor", binding.bastitxt.text.toString(), paqueteID)
            archivarConNombre("Riel", binding.rieltxt.text.toString(), paqueteID)
            archivarConNombre("Junquillo", binding.junkitxt.text.toString(), paqueteID)
            archivarConNombre("Ángulo", binding.angtxt.text.toString(), paqueteID)
            archivarConNombre("Portafelpa", binding.porttxt.text.toString(), paqueteID)
            archivarConNombre("Tee", binding.txTe.text.toString(), paqueteID)
            archivarConNombre("Vidrios", binding.vidriostxt.text.toString(), paqueteID)
            archivarReferencias(binding.referencias.text.toString(), paqueteID)

            ProyectoManager.actualizarContadorPorPrefijo(this, prefijo, siguienteNumero)
        }

        MapStorage.guardarMap(this, mapListas)
    }

    private fun archivarConNombre(nombre: String, texto: String, paqueteID: String) {
        if (texto.isBlank()) return
        val entradas = parsearTexto(texto, paqueteID)
        if (entradas.isNotEmpty()) {
            mapListas.getOrPut(nombre) { mutableListOf() }.addAll(entradas)
        }
    }

    private fun archivarReferencias(texto: String, paqueteID: String) {
        if (texto.isBlank()) return
        val entrada = mutableListOf(texto, "", paqueteID)
        mapListas.getOrPut("Referencias") { mutableListOf() }.add(entrada)
    }

    private fun parsearTexto(texto: String, paqueteID: String): MutableList<MutableList<String>> {
        val resultado = mutableListOf<MutableList<String>>()
        for (linea in texto.split("\n")) {
            val l = linea.trim()
            if (l.isEmpty()) continue
            val partes = l.split("=")
            if (partes.size == 2) {
                val valor = partes[0].trim()
                val cantidad = partes[1].trim()
                if (cantidad.isBlank()) continue
                val cantidadNum = cantidad.toIntOrNull()
                if (cantidadNum == null || cantidadNum == 0) continue
                val valorNum = valor.toFloatOrNull()
                if (valorNum != null && valorNum == 0f) continue
                if (valor.isBlank()) continue
                resultado.add(mutableListOf(valor, cantidad, paqueteID))
            } else {
                resultado.add(mutableListOf(l, "", paqueteID))
            }
        }
        return resultado
    }

    // ==================== CÁLCULOS ====================
    private fun df1(defo: Float): String {
        val resultado = if ("$defo".endsWith(".0")) "$defo".replace(".0", "")
        else "%.1f".format(defo)
        return resultado.replace(",", ".")
    }

    private fun zocaloTechoFijo(): Float {
        val ancho = binding.med1.text.toString().toFloat()
        val corrediza = binding.marco.text.toString().toFloat()
        val marco = 2.5f
        val bastidor = if (binding.bast.text.toString().toFloat() == 0f) 8f
        else binding.bast.text.toString().toFloat()
        val espesor = 0.3f
        val anchoHoja = (ancho - (2 * marco)) - espesor
        return when (corrediza) {
            0F -> ((anchoHoja + bastidor) / 2) - (2 * bastidor)
            else -> (anchoHoja - corrediza) - (2 * bastidor)
        }
    }

    private fun zocaloTechoCorre(): Float {
        val ancho = binding.med1.text.toString().toFloat()
        val corrediza = binding.marco.text.toString().toFloat()
        val marco = 2.5f
        val bastidor = if (binding.bast.text.toString().toFloat() == 0f) 8f
        else binding.bast.text.toString().toFloat()
        val espesor = 0.3f
        val anchoHoja = (ancho - (2 * marco)) - espesor
        return when (corrediza) {
            0F -> ((anchoHoja + bastidor) / 2) - (2 * bastidor)
            else -> corrediza - bastidor
        }
    }

    private fun paranteFijo(): Float {
        val alto = binding.med2.text.toString().toFloat()
        val marco = 2.5f
        return alto - marco
    }

    private fun paranteCorredizo(): Float {
        val alto = binding.med2.text.toString().toFloat()
        val descuento = 4.5f
        return alto - descuento
    }

    private fun junquilloAlto(): Float {
        val jun = binding.junk.text.toString().toFloat()
        val bastidor = if (binding.bast.text.toString().toFloat() == 0f) 8f
        else binding.bast.text.toString().toFloat()
        return (2 * bastidor) + (2 * jun)
    }

    private fun marcoSuperior(): Float {
        val ancho = binding.med1.text.toString().toFloat()
        val marco = 2.5f
        return ancho - (2 * marco)
    }

    private fun vidrioF(): String {
        val jun = binding.junk.text.toString().toFloat()
        val holgura = if (jun == 0f) 0.4f else 0.6f
        val anchfij = df1(zocaloTechoFijo() - holgura)
        val altfij = df1((paranteFijo() - 16.5f) - holgura)
        return "$anchfij x $altfij"
    }

    private fun vidrioC(): String {
        val jun = binding.junk.text.toString().toFloat()
        val holgura = if (jun == 0f) 0.4f else 0.6f
        val anchcorre = df1(zocaloTechoCorre() - holgura)
        val altcorr = df1((paranteCorredizo() - 16.5f) - holgura)
        return "$anchcorre x $altcorr"
    }

    private fun referen(): String {
        val ancho = binding.med1.text.toString()
        val alto = binding.med2.text.toString()
        val corrediza = binding.marco.text.toString()
        return "anch:$ancho x alt:$alto -> Cdza:$corrediza"
    }

    private fun angTapa(): String {
        val bastidor = if (binding.bast.text.toString().toFloat() == 0f) 8f
        else binding.bast.text.toString().toFloat()
        return df1(zocaloTechoCorre() + (2 * bastidor))
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        if (ModoMasivoHelper.esModoMasivo(this)) {
            // Solo incluir elementos con contenido
            val perfiles = mapOf(
                "Marco" to ModoMasivoHelper.texto(binding.marcotxt),
                "Bastidor" to ModoMasivoHelper.texto(binding.bastitxt),
                "Riel" to ModoMasivoHelper.texto(binding.rieltxt)
            ).filter { it.value.isNotBlank() }

            val accesorios = mapOf(
                "Junquillo" to ModoMasivoHelper.texto(binding.junkitxt),
                "Ángulo" to ModoMasivoHelper.texto(binding.angtxt),
                "Porta" to ModoMasivoHelper.texto(binding.porttxt),
                "Tee" to ModoMasivoHelper.texto(binding.txTe)
            ).filter { it.value.isNotBlank() }

            ModoMasivoHelper.devolverResultado(
                activity = this,
                calculadora = "Mampara FC",
                perfiles = perfiles,
                vidrios = ModoMasivoHelper.texto(binding.vidriostxt),
                accesorios = accesorios,
                referencias = ModoMasivoHelper.texto(binding.referencias)
            )
            return
        }
        @Suppress("DEPRECATION")
        super.onBackPressed()
    }
}
