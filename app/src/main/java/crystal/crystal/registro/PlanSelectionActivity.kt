// ==================================================================================
// ARCHIVO: PlanSelectionActivity.kt
// CAMBIOS COMPLETOS PARA PLAN VENTAS
// ==================================================================================

package crystal.crystal.registro

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import crystal.crystal.MainActivity
import crystal.crystal.databinding.ActivityPlanSelectionBinding
import java.util.Date
import java.util.concurrent.TimeUnit

class PlanSelectionActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPlanSelectionBinding
    private val auth by lazy { FirebaseAuth.getInstance() }
    private val db by lazy { FirebaseFirestore.getInstance() }

    // Precios en céntimos
    private val precioMensualCent = 1500L   // S/ 15.00
    private val precioAnualCent = 15000L    // S/ 150.00
    private val precioPrepagoDiaCent = 100L // S/ 1.00 por día
    private val precioVentasBaseCent = 3000L // S/ 30.00 base (1 terminal)
    private val precioVentasTerminalAdicionalCent = 1000L // S/ 10.00 por terminal adicional

    private var esAmpliacion = false
    private var terminalesActuales = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlanSelectionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val usuario = auth.currentUser
        if (usuario == null) {
            Toast.makeText(this, "Inicia sesión primero.", Toast.LENGTH_LONG).show()
            finish()
            return
        }

        // ⭐ NUEVO: Detectar si viene desde ampliación
        esAmpliacion = intent.getBooleanExtra("ES_AMPLIACION", false)
        terminalesActuales = intent.getIntExtra("TERMINALES_ACTUALES", 0)

        // Si es ampliación, cargar terminales actuales desde Firestore
        if (esAmpliacion && terminalesActuales == 0) {
            cargarTerminalesActuales()
        }

        // Botón PREPAGO
        binding.btnPrepago.setOnClickListener {
            activarPlan(
                tipo = "PREPAGO",
                precioCent = precioPrepagoDiaCent,
                duracionDias = 1
            )
        }

        // Botón MENSUAL
        binding.btnMensual.setOnClickListener {
            activarPlan(
                tipo = "MENSUAL",
                precioCent = precioMensualCent,
                duracionDias = 30
            )
        }

        // Botón ANUAL
        binding.btnAnual.setOnClickListener {
            activarPlan(
                tipo = "ANUAL",
                precioCent = precioAnualCent,
                duracionDias = 365
            )
        }

        // Botón VENTAS
        binding.btnVentas.setOnClickListener {
            mostrarDialogoTerminales()
        }
    }

    /**
     * Muestra diálogo para seleccionar número de terminales
     */

    private fun cargarTerminalesActuales() {
        val uid = auth.currentUser?.uid ?: return

        db.collection("usuarios")
            .document(uid)
            .collection("plan_ventas")
            .document("config")
            .get()
            .addOnSuccessListener { doc ->
                terminalesActuales = doc.getLong("terminales_contratadas")?.toInt() ?: 0
            }
            .addOnFailureListener {
                Toast.makeText(this, "Error cargando datos", Toast.LENGTH_SHORT).show()
            }
    }

    private fun mostrarDialogoTerminales() {
        // ⭐ Si es ampliación, solo mostrar opciones mayores a las actuales
        val opcionesBase = if (esAmpliacion) {
            // Empezar desde terminales actuales + 1
            (terminalesActuales + 1..10).map { num ->
                val precio = calcularPrecioVentas(num)
                val precioSoles = precio / 100.0
                "$num terminales (S/ $precioSoles)"
            }.toTypedArray()
        } else {
            // Opciones normales para compra inicial
            arrayOf(
                "1 terminal (S/ 30)",
                "2 terminales (S/ 40)",
                "3 terminales (S/ 50)",
                "4 terminales (S/ 60)",
                "5 terminales (S/ 70)"
            )
        }

        val titulo = if (esAmpliacion) {
            "Ampliar Plan VENTAS - Selecciona nuevo total"
        } else {
            "Plan VENTAS - Elige terminales"
        }

        AlertDialog.Builder(this)
            .setTitle(titulo)
            .setItems(opcionesBase) { _, which ->
                val numTerminales = if (esAmpliacion) {
                    terminalesActuales + which + 1
                } else {
                    which + 1
                }
                val costoTotal = calcularPrecioVentas(numTerminales)

                // ⭐ Si es ampliación, calcular solo la diferencia
                val costoFinal = if (esAmpliacion) {
                    calcularCostoAmpliacion(numTerminales)
                } else {
                    costoTotal
                }

                confirmarActivacionVentas(numTerminales, costoFinal)
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }
    private fun calcularCostoAmpliacion(nuevoTotal: Int): Long {
        // Solo cobra por las terminales adicionales
        val terminalesNuevas = nuevoTotal - terminalesActuales
        return terminalesNuevas * precioVentasTerminalAdicionalCent
    }

    /**
     * Calcula el precio del plan VENTAS según número de terminales
     */
    private fun calcularPrecioVentas(numTerminales: Int): Long {
        val base = precioVentasBaseCent
        val adicionales = if (numTerminales > 1) {
            (numTerminales - 1) * precioVentasTerminalAdicionalCent
        } else {
            0L
        }
        return base + adicionales
    }
    private fun ampliarPlanVentas(nuevoTotal: Int, precioCent: Long) {
        val usuario = auth.currentUser ?: run {
            Toast.makeText(this, "Inicia sesión primero.", Toast.LENGTH_LONG).show()
            return
        }

        val uid = usuario.uid
        val userRef = db.collection("usuarios").document(uid)

        db.runTransaction { tx ->
            val snap = tx.get(userRef)

            // Saldo actual en céntimos
            val saldoActual = snap.getLong("wallet_saldo_cent") ?: 0L

            if (saldoActual < precioCent) {
                throw Exception("SALDO_INSUFICIENTE")
            }

            val nuevoSaldo = saldoActual - precioCent

            // ⭐ Solo actualizar wallet y terminales_contratadas
            // NO tocar estado_servicio (ya está en VENTAS)
            tx.update(userRef, mapOf(
                "wallet_saldo_cent" to nuevoSaldo
            ))

            // Actualizar terminales contratadas
            val planVentasRef = userRef.collection("plan_ventas").document("config")
            tx.update(planVentasRef, mapOf(
                "terminales_contratadas" to nuevoTotal,
                "ultima_ampliacion" to FieldValue.serverTimestamp()
            ))

            // Registrar movimiento en wallet_movs
            val terminalesNuevas = nuevoTotal - terminalesActuales
            val movRef = userRef.collection("wallet_movs").document()
            tx.set(movRef, mapOf(
                "tipo" to "AMPLIACION_PLAN_VENTAS",
                "monto_cent" to -precioCent,
                "terminales_anteriores" to terminalesActuales,
                "terminales_nuevas" to nuevoTotal,
                "terminales_adicionales" to terminalesNuevas,
                "created_at" to FieldValue.serverTimestamp()
            ))

            null
        }.addOnSuccessListener {
            Toast.makeText(
                this,
                "✅ Plan ampliado a $nuevoTotal terminales",
                Toast.LENGTH_LONG
            ).show()

            // Volver a GestionDispositivosActivity
            finish()

        }.addOnFailureListener { e ->
            if (e.message?.contains("SALDO_INSUFICIENTE") == true) {
                Toast.makeText(
                    this,
                    "Saldo insuficiente. Recarga con Yape.",
                    Toast.LENGTH_LONG
                ).show()
                startActivity(Intent(this, WalletActivity::class.java))
            } else {
                Toast.makeText(
                    this,
                    "Error al ampliar plan: ${e.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    /**
     * Confirma la activación del plan VENTAS
     */
    private fun confirmarActivacionVentas(numTerminales: Int, precioCent: Long) {
        val precioSoles = precioCent / 100.0

        val mensaje = if (esAmpliacion) {
            // Mensaje para ampliación
            val terminalesNuevas = numTerminales - terminalesActuales
            """
            Ampliar Plan VENTAS
            
            Terminales actuales: $terminalesActuales
            Nuevo total: $numTerminales terminales
            Terminales adicionales: $terminalesNuevas
            
            Precio: S/ $precioSoles
            (S/ 10.00 × $terminalesNuevas terminales adicionales)
            
            ¿Confirmar ampliación?
        """.trimIndent()
        } else {
            // Mensaje para compra inicial
            """
            Plan VENTAS - $numTerminales terminal${if (numTerminales > 1) "es" else ""}
            
            Precio: S/ $precioSoles/mes
            - Base: S/ 30.00 (1 terminal)
            ${if (numTerminales > 1) "- Adicionales: ${numTerminales - 1} × S/ 10.00" else ""}
            
            ¿Confirmar activación?
        """.trimIndent()
        }

        AlertDialog.Builder(this)
            .setTitle(if (esAmpliacion) "Confirmar Ampliación" else "Confirmar Plan VENTAS")
            .setMessage(mensaje)
            .setPositiveButton("Activar") { _, _ ->
                if (esAmpliacion) {
                    ampliarPlanVentas(numTerminales, precioCent)
                } else {
                    activarPlanVentas(numTerminales, precioCent)
                }
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }
    /**
     * Activa el plan VENTAS con el número de terminales seleccionado
     */
    private fun activarPlanVentas(numTerminales: Int, precioCent: Long) {
        val usuario = auth.currentUser ?: run {
            Toast.makeText(this, "Inicia sesión primero.", Toast.LENGTH_LONG).show()
            return
        }

        val uid = usuario.uid
        val userRef = db.collection("usuarios").document(uid)

        db.runTransaction { tx ->
            val snap = tx.get(userRef)

            // Saldo actual en céntimos
            val saldoActual = snap.getLong("wallet_saldo_cent") ?: 0L

            if (saldoActual < precioCent) {
                throw Exception("SALDO_INSUFICIENTE")
            }

            val nuevoSaldo = saldoActual - precioCent

            // Construir nuevo estado_servicio
            val ahora = Timestamp.now()
            val millis = ahora.toDate().time + TimeUnit.DAYS.toMillis(30)
            val validoHasta = Timestamp(Date(millis))

            val estadoServicio = mutableMapOf<String, Any>(
                "mode" to "VENTAS",
                "source" to "PLAN_VENTAS",
                "full_until" to validoHasta,
                // ⭐ Ya NO guardamos numTerminales aquí (solo en plan_ventas/config)
                "updatedAt" to FieldValue.serverTimestamp()
            )

            // Actualizar usuario
            tx.update(userRef, mapOf(
                "wallet_saldo_cent" to nuevoSaldo,
                "estado_servicio" to estadoServicio
            ))

            // Crear estructura de control de dispositivos
            val planVentasRef = userRef.collection("plan_ventas").document("config")
            tx.set(planVentasRef, mapOf(
                "terminales_contratadas" to numTerminales,
                "terminales_autorizadas" to 0,
                "fecha_activacion" to FieldValue.serverTimestamp(),
                "activo" to true
            ))

            // Registrar movimiento en wallet_movs
            val movRef = userRef.collection("wallet_movs").document()
            tx.set(movRef, mapOf(
                "tipo" to "PAGO_PLAN_VENTAS",
                "monto_cent" to -precioCent,
                "numTerminales" to numTerminales,
                "created_at" to FieldValue.serverTimestamp()
            ))

            null
        }.addOnSuccessListener {
            Toast.makeText(
                this,
                "Plan VENTAS activado: $numTerminales terminal${if (numTerminales > 1) "es" else ""}",
                Toast.LENGTH_LONG
            ).show()
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }.addOnFailureListener { e ->
            if (e.message?.contains("SALDO_INSUFICIENTE") == true) {
                Toast.makeText(
                    this,
                    "Saldo insuficiente. Recarga con Yape.",
                    Toast.LENGTH_LONG
                ).show()
                startActivity(Intent(this, WalletActivity::class.java))
            } else {
                Toast.makeText(
                    this,
                    "Error al activar plan: ${e.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    private fun activarPlan(
        tipo: String,
        precioCent: Long,
        duracionDias: Long?
    ) {
        val usuario = auth.currentUser ?: run {
            Toast.makeText(this, "Inicia sesión primero.", Toast.LENGTH_LONG).show()
            return
        }

        val uid = usuario.uid
        val userRef = db.collection("usuarios").document(uid)

        db.runTransaction { tx ->
            val snap = tx.get(userRef)

            // Saldo actual en céntimos
            val saldoActual = snap.getLong("wallet_saldo_cent") ?: 0L

            if (saldoActual < precioCent) {
                throw Exception("SALDO_INSUFICIENTE")
            }

            val nuevoSaldo = saldoActual - precioCent

            // Construir nuevo estado_servicio
            val ahora = Timestamp.now()
            val estadoServicio = mutableMapOf<String, Any>(
                "mode" to "FULL",
                "source" to tipo,
                "updatedAt" to FieldValue.serverTimestamp()
            )

            if (duracionDias != null) {
                val millis = ahora.toDate().time + TimeUnit.DAYS.toMillis(duracionDias)
                estadoServicio["full_until"] = Timestamp(Date(millis))
            }

            // Actualizar usuario
            tx.update(userRef, mapOf(
                "wallet_saldo_cent" to nuevoSaldo,
                "estado_servicio" to estadoServicio
            ))

            // Registrar movimiento en wallet_movs
            val movRef = userRef.collection("wallet_movs").document()
            tx.set(movRef, mapOf(
                "tipo" to "PAGO_PLAN_$tipo",
                "monto_cent" to -precioCent,
                "created_at" to FieldValue.serverTimestamp()
            ))

            null
        }.addOnSuccessListener {
            Toast.makeText(this, "Plan $tipo activado con saldo de wallet.", Toast.LENGTH_LONG).show()
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }.addOnFailureListener { e ->
            if (e.message?.contains("SALDO_INSUFICIENTE") == true) {
                Toast.makeText(this, "Saldo insuficiente. Recarga con Yape.", Toast.LENGTH_LONG).show()
                startActivity(Intent(this, WalletActivity::class.java))
            } else {
                Toast.makeText(this, "Error al activar plan: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }
}