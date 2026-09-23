package crystal.crystal.pagos

import android.content.Context
import androidx.appcompat.app.AlertDialog

/**
 * Canal de cobro — variante PLAY.
 *
 * La política de pagos de Google Play exige Play Billing para el software de gestión, así que esta
 * build **no vende nada**: no hay wallet, ni número de Yape, ni pantalla de planes. Es el modelo de
 * "solo consumo" —el mismo con el que se distribuyen Notion, Slack o QuickBooks—: el usuario paga
 * fuera y la app se limita a reconocer lo que ya pagó.
 *
 * El mensaje no enlaza ni nombra un método de pago externo a propósito: dirigir al usuario a pagar
 * fuera desde dentro de la app es justamente lo que la política prohíbe fuera de Estados Unidos.
 */
object CanalPagos {

    /** No hay pantallas de pago en esta build; la UI oculta los accesos que llevarían a ellas. */
    const val DISPONIBLE_EN_LA_APP = false

    /** Existe por simetría con la build directa: aquí no hay pantalla de planes que devolver. */
    const val EXTRA_VOLVER_ATRAS = "volver_atras"

    fun abrirRecarga(ctx: Context, vararg extras: Pair<String, Any?>) = avisar(ctx)

    fun abrirPlanes(ctx: Context, vararg extras: Pair<String, Any?>) = avisar(ctx)

    /** Sin recargas en la app no hay a qué adjuntar un comprobante. La UI ni siquiera lo ofrece. */
    @Suppress("UNUSED_PARAMETER")
    fun usarComprobanteParaRecarga(ctx: Context, voucherUrl: String) = Unit

    private fun avisar(ctx: Context) {
        AlertDialog.Builder(ctx)
            .setTitle("Gestión de tu plan")
            .setMessage(
                "Tu plan y tu saldo se administran desde tu cuenta de Crystal.\n\n" +
                "Los cambios que hagas allí aparecen aquí automáticamente."
            )
            .setPositiveButton("Entendido", null)
            .show()
    }
}
