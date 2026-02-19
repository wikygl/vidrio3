package crystal.crystal.taller

import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import crystal.crystal.casilla.DialogosProyecto
import crystal.crystal.casilla.MapStorage
import crystal.crystal.casilla.ProyectoManager
import crystal.crystal.databinding.ActivityRejasBinding

class RejasActivity : AppCompatActivity() {
    lateinit var binding: ActivityRejasBinding
    private val mapListas = mutableMapOf<String, MutableList<MutableList<String>>>()
    private var primerClickArchivarRealizado = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRejasBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnCalcularNfcfs.setOnClickListener { res() }

        binding.btArchivar.setOnClickListener {
            if (binding.txTubo.text.isNullOrBlank()) {
                Toast.makeText(this, "Haz nuevo cálculo", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (!primerClickArchivarRealizado) {
                DialogosProyecto.mostrarDialogoSeleccionarParaArchivar(this, object : DialogosProyecto.ProyectoCallback {
                    override fun onProyectoSeleccionado(nombreProyecto: String) {
                        primerClickArchivarRealizado = true
                        val mapExistente = MapStorage.cargarProyecto(this@RejasActivity, nombreProyecto)
                        if (mapExistente != null) { mapListas.clear(); mapListas.putAll(mapExistente) }
                        archivarMapas()
                        Toast.makeText(this@RejasActivity, "Archivado", Toast.LENGTH_SHORT).show()
                        binding.etMed1.setText(""); binding.etMed2.setText("")
                    }
                    override fun onProyectoCreado(nombreProyecto: String) {
                        primerClickArchivarRealizado = true
                        archivarMapas()
                        Toast.makeText(this@RejasActivity, "Archivado", Toast.LENGTH_SHORT).show()
                        binding.etMed1.setText(""); binding.etMed2.setText("")
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
                binding.etMed1.setText(""); binding.etMed2.setText("")
            }
        }
    }

    // ==================== ARCHIVAR ====================
    private fun obtenerPrefijo(): String = "RJ"

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

            archivarConNombre("Tubo", binding.txTubo.text.toString(), paqueteID)

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
    private fun med1(): Float = binding.etMed1.text.toString().toFloat()
    private fun med2(): Float = binding.etMed2.text.toString().toFloat()
    private fun tubo(): Float = binding.etTubo.text.toString().toFloat()
    private fun divisiones(): Int = binding.etDivi.text.toString().toInt()

    private fun mDivisiones(): Float {
        val div = binding.etDivi.text.toString().toFloat()
        return (med1() - (tubo() * (div + 1))) / div
    }

    @SuppressLint("SetTextI18n")
    private fun res() {
        val x = mDivisiones() - 0.1f
        binding.txTubo.text = "${df1(med1())} = 2\n" +
                "${df1(med2())} = 2\n" +
                "${df1(med2() - (tubo() * 2))} = ${divisiones()}\n" +
                "${df1(x)} = ${divisiones()}"
    }

    private fun df1(defo: Float): String {
        return if (defo % 1 == 0f) {
            defo.toInt().toString()
        } else {
            "%.1f".format(defo).replace(",", ".")
        }
    }
}
