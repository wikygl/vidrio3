package crystal.crystal.pos

import crystal.crystal.clientes.Cliente
import crystal.crystal.clientes.TipoDocumento
import crystal.crystal.comprobantes.DatosEmpresa
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Lo que la proforma dice de quién la hace y a quién va.
 *
 * Son los datos que salen impresos en la cabecera, así que lo que se comprueba es que no se
 * inventen líneas vacías ni se pierda lo que el vidriero escribió a mano.
 */
class DatosDeLaProformaTest {

    private val clinica = Cliente(
        tipoDocumento = TipoDocumento.RUC,
        numeroDocumento = "20512345678",
        nombreCompleto = "Clínica Bilbao S.A.C.",
        direccion = "Av. Arequipa 1234",
        telefono = "987654321",
        email = "compras@bilbao.pe"
    )

    /** Elegido del registro, la proforma sale con todo lo suyo. */
    @Test
    fun el_cliente_del_registro_sale_completo() {
        val c = DatosDeLaProforma.cliente("", clinica)
        assertEquals("Clínica Bilbao S.A.C.", c.nombre)
        assertEquals("RUC 20512345678", c.documento)
        assertTrue(c.hayDatos)
        assertEquals(
            listOf("RUC 20512345678", "Av. Arequipa 1234", "Tel. 987654321", "compras@bilbao.pe"),
            c.lineas()
        )
    }

    /** Lo escrito a mano manda sobre el nombre del registro: esta proforma va a ese nombre. */
    @Test
    fun lo_escrito_a_mano_manda_sobre_el_nombre() {
        val c = DatosDeLaProforma.cliente("Clínica Bilbao - sede 2", clinica)
        assertEquals("Clínica Bilbao - sede 2", c.nombre)
        assertEquals("el resto de sus datos se perdió", "RUC 20512345678", c.documento)
    }

    /** Sin cliente del registro, solo hay nombre: y no se inventan líneas. */
    @Test
    fun sin_registro_solo_el_nombre() {
        val c = DatosDeLaProforma.cliente("Doña Rosa", null)
        assertEquals("Doña Rosa", c.nombre)
        assertTrue(!c.hayDatos)
        assertTrue(c.lineas().isEmpty())
    }

    /** Los datos que faltan no dejan líneas en blanco en el papel. */
    @Test
    fun lo_que_falta_no_deja_hueco() {
        val suelto = clinica.copy(direccion = null, email = "   ")
        val c = DatosDeLaProforma.cliente("", suelto)
        assertEquals(listOf("RUC 20512345678", "Tel. 987654321"), c.lineas())
    }

    /** El membrete necesita o la tienda o el técnico: sin ninguno de los dos no hay cabecera. */
    @Test
    fun sin_tienda_ni_tecnico_no_hay_membrete() {
        val vacio = MembreteDeProforma(null, "", true, "", true, "")
        assertTrue(!vacio.hayMembrete)
        val conTecnico = vacio.copy(tecnico = "Gonzalo")
        assertTrue(conTecnico.hayMembrete)
    }

    /** Y el sello solo se pone si hay imagen Y está puesto. */
    @Test
    fun el_sello_necesita_imagen_y_estar_puesto() {
        val base = MembreteDeProforma(null, "Gonzalo", true, "", true, "")
        assertTrue("sin imagen no hay sello", !base.haySello)
        assertTrue(base.copy(selloRuta = "/fotos/sello.png").haySello)
        assertTrue(
            "apagado no se pone aunque haya imagen",
            !base.copy(selloRuta = "/fotos/sello.png", mostrarSello = false).haySello
        )
    }

    /** El logo sale del archivo propio antes que de la dirección remota: es el que siempre se lee. */
    @Test
    fun el_logo_prefiere_el_archivo_propio() {
        val empresa = DatosEmpresa(
            ruc = "20512345678",
            razonSocial = "Vidriería Crystal",
            direccion = "Jr. Lima 100",
            telefono = "999888777",
            logoUrl = "https://algo/logo.png",
            logoPath = "/data/logo.png"
        )
        val m = MembreteDeProforma(empresa, "Gonzalo", true, "", false, "")
        assertEquals("/data/logo.png", m.logo)
        assertEquals("https://algo/logo.png", m.copy(empresa = empresa.copy(logoPath = null)).logo)
        assertNull(m.copy(empresa = empresa.copy(logoPath = null, logoUrl = null)).logo)
    }
}
