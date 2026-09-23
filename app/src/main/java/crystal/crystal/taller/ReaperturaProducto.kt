package crystal.crystal.taller

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import crystal.crystal.casilla.EdicionProducto
import crystal.crystal.casilla.ProyectoManager

/** Las listas de un producto como las junta Productos: (nombre de lista, [(dato, cantidad)]). */
typealias ListasDeProducto = List<Pair<String, List<Pair<String, String>>>>

/**
 * Volver a abrir un producto archivado (lo que se ve en Productos: Rm4, Vna2, P10…) en su
 * calculadora, para editarlo. Al archivarlo de nuevo reemplaza al que había ([EdicionProducto]).
 *
 * De lo archivado se saca qué calculadora le toca (por el prefijo del id: Rm, Vna, P, MP…),
 * el ancho y el alto, el cliente y, si lo guarda, el diseño completo: el ropero, el drywall y
 * la Nova se abren tal cual; las demás, con sus medidas.
 */
object ReaperturaProducto {

    data class Destino(val clase: Class<out AppCompatActivity>, val nombre: String)

    private val porPrefijo: List<Pair<String, Destino>> = listOf(
        "Rm" to Destino(crystal.crystal.taller.melamina.RoperoActivity::class.java, "Ropero Melamina"),
        "Dw" to Destino(crystal.crystal.taller.drywall.DrywallActivity::class.java, "Drywall"),
        "Vna" to Destino(crystal.crystal.taller.nova.NovaCorrediza::class.java, "Nova Corrediza"),
        "Vni" to Destino(crystal.crystal.taller.nova.NovaCorrediza::class.java, "Nova Corrediza"),
        "Vnp" to Destino(crystal.crystal.taller.nova.NovaCorrediza::class.java, "Nova Corrediza"),
        "Vpd" to Destino(PDuchaActivity::class.java, "Puerta Ducha"),
        "Vit" to Destino(Vitroven::class.java, "Vitroven"),
        "Va" to Destino(crystal.crystal.taller.venAl.VentanaAl::class.java, "Ventana Aluminio"),
        "PV" to Destino(PivotAl::class.java, "Pivot"),
        "P" to Destino(crystal.crystal.taller.puerta.PuertasActivity::class.java, "Puerta"),
        "MP" to Destino(crystal.crystal.taller.mamparas.MamparaPaflon::class.java, "Mampara Paflón"),
        "MF" to Destino(crystal.crystal.taller.mamparas.MamparaFC::class.java, "Mampara FC"),
        "MV" to Destino(crystal.crystal.taller.mamparas.MamparaVidrioActivity::class.java, "Mampara Vidrio"),
        "MC" to Destino(Muro::class.java, "Muro Cortina"),
        "Rj" to Destino(RejasActivity::class.java, "Rejas"),
        "Db" to Destino(DivisionBanoActivity::class.java, "División Baño")
    )

    /** Las letras del id: "Rm" de "Rm4", "P" de "P10, abel". */
    fun prefijoDe(id: String): String = Regex("^([A-Za-z]+)\\d").find(id.trim())?.groupValues?.get(1).orEmpty()

    /** La calculadora de ese id (el prefijo tal cual; mayúsculas y minúsculas cuentan: P no es Pv). */
    fun destinoDe(id: String): Destino? {
        val p = prefijoDe(id)
        return porPrefijo.firstOrNull { it.first == p }?.second
            ?: porPrefijo.firstOrNull { it.first.equals(p, ignoreCase = true) }?.second
    }

    private fun primero(listas: ListasDeProducto, nombre: String): String =
        listas.firstOrNull { it.first == nombre }?.second?.firstOrNull()?.first?.trim().orEmpty()

    /** El diseño completo, si el producto lo guarda y su calculadora sabe abrirlo. */
    fun disenoDe(listas: ListasDeProducto): String =
        primero(listas, crystal.crystal.taller.melamina.RoperoActivity.CLAVE_DISENO)
            .ifBlank { primero(listas, crystal.crystal.taller.drywall.DrywallActivity.CLAVE_DISENO) }
            .ifBlank { primero(listas, "DisenoPaquete") }

    /** El ancho y el alto archivados, de lo más fiable a lo menos: el diseño, el paquete, las referencias. */
    fun medidasDe(listas: ListasDeProducto): Pair<Float, Float>? {
        primero(listas, crystal.crystal.taller.melamina.RoperoActivity.CLAVE_DISENO).takeIf { it.isNotBlank() }
            ?.let { crystal.crystal.taller.melamina.Ropero.desdeJson(it) }?.takeIf { it.anchoCm > 0f }
            ?.let { return it.anchoCm to it.altoCm }
        primero(listas, crystal.crystal.taller.puerta.PuertaDescriptor.CLAVE).takeIf { it.isNotBlank() }
            ?.let { crystal.crystal.taller.puerta.PuertaDescriptor.parsear(it) }?.let { return it.ancho to it.alto }
        primero(listas, VitrovenDescriptor.CLAVE).takeIf { it.isNotBlank() }
            ?.let { VitrovenDescriptor.parsear(it) }?.let { return it.ancho to it.alto }
        primero(listas, "DisenoVentanaAl").takeIf { it.isNotBlank() }
            ?.let { crystal.crystal.taller.venAl.VentanaAlDescriptor.parsear(it) }?.let { return it.ancho to it.alto }
        // El paquete simbólico de casi todos: "C<…>-M<ancho,alto,…>".
        Regex("M<(\\d+(?:\\.\\d+)?),(\\d+(?:\\.\\d+)?)").find(primero(listas, "DisenoSimbolicoV2"))
            ?.let { return it.groupValues[1].toFloat() to it.groupValues[2].toFloat() }
        primero(listas, "DisenoMampara").takeIf { it.startsWith("MPF1:") }?.removePrefix("MPF1:")?.split(";")
            ?.let { p -> val a = p.getOrNull(0)?.toFloatOrNull(); val h = p.getOrNull(1)?.toFloatOrNull(); if (a != null && h != null) return a to h }
        // Las referencias: "anch 69 x alt 194.5", "An: 204.2  x  Al: 51.7", "Ancho 213.8 · Alto 276.5".
        Regex("(\\d+(?:\\.\\d+)?)[^\\d\\n]{1,14}?(\\d+(?:\\.\\d+)?)").find(primero(listas, "Referencias"))
            ?.let { return it.groupValues[1].toFloat() to it.groupValues[2].toFloat() }
        return null
    }

    /** Abre el producto [id] en su calculadora, para editarlo. false si no tiene calculadora conocida. */
    fun abrir(context: Context, id: String, listas: ListasDeProducto): Boolean {
        val destino = destinoDe(id) ?: return false
        val proyecto = ProyectoManager.getProyectoActivo().orEmpty()
        val diseno = disenoDe(listas)
        val intent = Intent(context, destino.clase).apply {
            // Con el diseño entero, las medidas van en él: pasarlas aparte las pisaría (el drywall
            // las escribe después de cargar el suyo).
            if (diseno.isBlank()) medidasDe(listas)?.let { (a, h) -> putExtra("ancho", a); putExtra("alto", h) }
            primero(listas, "Cliente").takeIf { it.isNotBlank() }?.let { putExtra("rcliente", it) }
            putExtra("producto", destino.nombre)
            EdicionProducto.ponerEn(this, id, proyecto, diseno)
        }
        context.startActivity(intent)
        return true
    }
}
