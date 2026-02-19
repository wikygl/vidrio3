package crystal.crystal.taller

import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import crystal.crystal.casilla.DialogosProyecto
import crystal.crystal.casilla.MapStorage
import crystal.crystal.casilla.ProyectoManager
import crystal.crystal.databinding.ActivityPivotAlBinding

class PivotAl : AppCompatActivity() {

    private lateinit var binding: ActivityPivotAlBinding
    private val mapListas = mutableMapOf<String, MutableList<MutableList<String>>>()
    private var primerClickArchivarRealizado = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPivotAlBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnCalcularP.setOnClickListener {
            marco()
            parante()
            zocalo()
            vidrio()
            portafelpa()
        }

        binding.btnArchivarFccfss.setOnClickListener {
            if (binding.tvMarcoResult.text.isNullOrBlank()) {
                Toast.makeText(this, "Haz nuevo cálculo", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (!primerClickArchivarRealizado) {
                DialogosProyecto.mostrarDialogoSeleccionarParaArchivar(this, object : DialogosProyecto.ProyectoCallback {
                    override fun onProyectoSeleccionado(nombreProyecto: String) {
                        primerClickArchivarRealizado = true
                        val mapExistente = MapStorage.cargarProyecto(this@PivotAl, nombreProyecto)
                        if (mapExistente != null) { mapListas.clear(); mapListas.putAll(mapExistente) }
                        archivarMapas()
                        Toast.makeText(this@PivotAl, "Archivado", Toast.LENGTH_SHORT).show()
                        binding.etAnchoP.setText(""); binding.etAltoP.setText("")
                    }
                    override fun onProyectoCreado(nombreProyecto: String) {
                        primerClickArchivarRealizado = true
                        archivarMapas()
                        Toast.makeText(this@PivotAl, "Archivado", Toast.LENGTH_SHORT).show()
                        binding.etAnchoP.setText(""); binding.etAltoP.setText("")
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
                binding.etAnchoP.setText(""); binding.etAltoP.setText("")
            }
        }

        // Pre-carga desde presupuesto
        intent.getFloatExtra("ancho", -1f).let { if (it > 0) binding.etAnchoP.setText(df1(it)) }
        intent.getFloatExtra("alto", -1f).let { if (it > 0) binding.etAltoP.setText(df1(it)) }
    }

    // ==================== ARCHIVAR ====================
    private fun obtenerPrefijo(): String = "PV"

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

            archivarConNombre("Marco", binding.tvMarcoResult.text.toString(), paqueteID)
            archivarConNombre("Parante", binding.tvParante.text.toString(), paqueteID)
            archivarConNombre("Zócalo", binding.tvZocaloE.text.toString(), paqueteID)
            archivarConNombre("Portafelpa", binding.tvPortaP.text.toString(), paqueteID)
            archivarConNombre("Vidrios", binding.tvVidriosP.text.toString(), paqueteID)

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
        val resultado = if ("$defo".endsWith(".0")) {
            "$defo".replace(".0", "")
        } else {
            "%.1f".format(defo)
        }
        return resultado.replace(",", ".")
    }

    @SuppressLint("SetTextI18n")
    private fun marco() {
        val alto = binding.etAltoP.text.toString().toFloat()
        binding.tvMarcoResult.text = "${df1(alto)} = 2\n${df1(anchUtil())} = 2"
    }

    @SuppressLint("SetTextI18n")
    private fun parante() {
        val p = paran()
        binding.tvParante.text = "${df1(p)} = 2"
    }

    @SuppressLint("SetTextI18n")
    private fun zocalo() {
        val z = zoc()
        binding.tvZocaloE.text = "${df1(z)} = 2"
    }

    @SuppressLint("SetTextI18n")
    private fun portafelpa() {
        val alt = altoUtil()
        val anc = anchUtil() - 0.6f
        binding.tvPortaP.text = "${df1(alt)} = 2\n${df1(anc)} = 2"
    }

    @SuppressLint("SetTextI18n")
    private fun vidrio() {
        val an = paran() - 7f
        val al = zoc() + 1.5f
        binding.tvVidriosP.text = "${df1(an)} x ${df1(al)} =1"
    }

    // FUNCIONES GENERALES
    private fun anchUtil(): Float {
        val ancho = binding.etAnchoP.text.toString().toFloat()
        val marco = binding.etMarcoP.text.toString().toFloat()
        return ancho - (2 * marco)
    }

    private fun altoUtil(): Float {
        val alto = binding.etAltoP.text.toString().toFloat()
        val marco = binding.etMarcoP.text.toString().toFloat()
        return alto - (2 * marco)
    }

    private fun paran(): Float {
        return anchUtil() - 1.4f
    }

    private fun zoc(): Float {
        return (altoUtil() - 1.4f) - 5.8f
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        if (ModoMasivoHelper.esModoMasivo(this)) {
            val perfiles = mapOf(
                "Marco" to ModoMasivoHelper.texto(binding.tvMarcoResult),
                "Parante" to ModoMasivoHelper.texto(binding.tvParante),
                "Zócalo" to ModoMasivoHelper.texto(binding.tvZocaloE),
                "Portafelpa" to ModoMasivoHelper.texto(binding.tvPortaP)
            ).filter { it.value.isNotBlank() }

            ModoMasivoHelper.devolverResultado(
                activity = this,
                calculadora = "Pivot",
                perfiles = perfiles,
                vidrios = ModoMasivoHelper.texto(binding.tvVidriosP),
                accesorios = emptyMap(),
                referencias = ""
            )
            return
        }
        @Suppress("DEPRECATION")
        super.onBackPressed()
    }
}
