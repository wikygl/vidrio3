package crystal.crystal.pos

import android.content.SharedPreferences
import crystal.crystal.clientes.Cliente
import crystal.crystal.comprobantes.DatosEmpresa

/**
 * Lo que va en la cabecera de una proforma y detrás de ella: el membrete y el sello de agua.
 *
 * El membrete es quién la hace —la tienda, con su logo, su RUC y su dirección, y el técnico que
 * midió—, y va arriba de todo. El sello de agua es la imagen que se ve al fondo de cada hoja, en
 * claro, para que la proforma se reconozca de un vistazo y no se pueda pasar por otra.
 *
 * Los datos de la tienda NO se apuntan aquí: son los mismos de siempre, los que ya usa el ticket
 * (`empresa_*` en las preferencias), para que el papel diga lo mismo salga por donde salga.
 */
data class MembreteDeProforma(
    val empresa: DatosEmpresa?,
    val tecnico: String,
    val mostrarMembrete: Boolean,
    val selloRuta: String,
    val mostrarSello: Boolean,
    val nota: String
) {
    /** El logo de la tienda, si lo hay: primero el archivo propio, y si no la dirección remota. */
    val logo: String?
        get() = empresa?.logoPath?.takeIf { it.isNotBlank() }
            ?: empresa?.logoUrl?.takeIf { it.isNotBlank() }

    val hayMembrete: Boolean get() = mostrarMembrete && (empresa != null || tecnico.isNotBlank())
    val haySello: Boolean get() = mostrarSello && selloRuta.isNotBlank()
}

/**
 * Los datos del cliente tal como salen impresos. Si se eligió del registro salen completos; si se
 * escribió a mano, sale lo que se escribió.
 */
data class ClienteDeLaProforma(
    val nombre: String,
    val documento: String,
    val direccion: String,
    val telefono: String,
    val correo: String
) {
    val hayDatos: Boolean
        get() = documento.isNotBlank() || direccion.isNotBlank() ||
            telefono.isNotBlank() || correo.isNotBlank()

    /** Las líneas que de verdad tienen algo que decir. */
    fun lineas(): List<String> = listOfNotNull(
        documento.takeIf { it.isNotBlank() },
        direccion.takeIf { it.isNotBlank() },
        telefono.takeIf { it.isNotBlank() }?.let { "Tel. $it" },
        correo.takeIf { it.isNotBlank() }
    )
}

object DatosDeLaProforma {

    private const val SELLO = "proforma_sello_ruta"
    private const val CON_SELLO = "proforma_sello_puesto"
    private const val CON_MEMBRETE = "proforma_membrete_puesto"
    private const val NOTA = "proforma_nota"

    /** Lo que se imprime en la cabecera, reuniendo lo que ya sabe la app. */
    fun membrete(prefs: SharedPreferences, empresa: DatosEmpresa?): MembreteDeProforma =
        MembreteDeProforma(
            empresa = empresa,
            tecnico = prefs.getString("nombre_vendedor", "").orEmpty().trim(),
            mostrarMembrete = prefs.getBoolean(CON_MEMBRETE, true),
            selloRuta = prefs.getString(SELLO, "").orEmpty(),
            mostrarSello = prefs.getBoolean(CON_SELLO, true),
            nota = prefs.getString(NOTA, "").orEmpty()
        )

    fun guardarSello(prefs: SharedPreferences, ruta: String) {
        prefs.edit().putString(SELLO, ruta.trim()).apply()
    }

    fun quitarSello(prefs: SharedPreferences) {
        prefs.edit().remove(SELLO).apply()
    }

    fun guardarNota(prefs: SharedPreferences, nota: String) {
        prefs.edit().putString(NOTA, nota.trim()).apply()
    }

    fun ponerMembrete(prefs: SharedPreferences, puesto: Boolean) {
        prefs.edit().putBoolean(CON_MEMBRETE, puesto).apply()
    }

    fun ponerSello(prefs: SharedPreferences, puesto: Boolean) {
        prefs.edit().putBoolean(CON_SELLO, puesto).apply()
    }

    /**
     * El cliente de la proforma: del registro si se eligió de ahí, y si no, lo escrito a mano.
     *
     * El nombre escrito manda sobre el del registro: si el vidriero lo cambió en la pantalla, es
     * porque esta proforma va a ese nombre.
     */
    fun cliente(escrito: String, elegido: Cliente?): ClienteDeLaProforma {
        val nombre = escrito.trim().ifEmpty { elegido?.nombreCompleto.orEmpty() }
        if (elegido == null) return ClienteDeLaProforma(nombre, "", "", "", "")
        return ClienteDeLaProforma(
            nombre = nombre.ifEmpty { elegido.nombreCompleto },
            documento = "${elegido.tipoDocumento.nombre} ${elegido.numeroDocumento}".trim(),
            direccion = elegido.direccion.orEmpty().trim(),
            telefono = elegido.telefono.orEmpty().trim(),
            correo = elegido.email.orEmpty().trim()
        )
    }
}
