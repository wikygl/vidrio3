package crystal.crystal.tienda

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import crystal.crystal.R
import crystal.crystal.red.CrystalMessageNotifier

/**
 * El aviso de un pedido nuevo de un cliente de la web (fase 3). Llega por el tema FCM de la ciudad
 * del taller o, si pregunta por un anuncio, directo al vendedor. Abre el pedido. Canal propio, para
 * que quien no quiera estos avisos los apague sin apagar los del chat.
 */
object AvisoPedidoCliente {

    private const val CANAL = "tienda_pedidos"

    @SuppressLint("MissingPermission") // canNotify revisa el permiso antes.
    fun mostrar(context: Context, pedidoId: String, titulo: String, cuerpo: String) {
        if (pedidoId.isBlank() || !CrystalMessageNotifier.canNotify(context)) return
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.getSystemService(NotificationManager::class.java).createNotificationChannel(
                NotificationChannel(CANAL, "Pedidos de clientes", NotificationManager.IMPORTANCE_HIGH).apply {
                    description = "Clientes de su ciudad que piden cotización desde la web de la Tienda"
                }
            )
        }
        val intent = PedidoClienteActivity.intent(context, pedidoId)
            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val flags = PendingIntent.FLAG_UPDATE_CURRENT or
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) PendingIntent.FLAG_IMMUTABLE else 0
        val aviso = NotificationCompat.Builder(context, CANAL)
            .setSmallIcon(R.drawable.ic_crystal_notification)
            .setContentTitle(titulo.ifBlank { "Pedido de cliente" })
            .setContentText(cuerpo)
            .setStyle(NotificationCompat.BigTextStyle().bigText(cuerpo))
            .setContentIntent(PendingIntent.getActivity(context, pedidoId.hashCode(), intent, flags))
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .build()
        NotificationManagerCompat.from(context).notify(pedidoId.hashCode(), aviso)
    }
}
