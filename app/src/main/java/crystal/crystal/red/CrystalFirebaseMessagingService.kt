package crystal.crystal.red

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class CrystalFirebaseMessagingService : FirebaseMessagingService() {

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        CrystalFcmTokenManager.saveToken(applicationContext, token)
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        val data = message.data
        val chatId = data["chatId"].orEmpty()
        if (chatId.isBlank()) return

        val currentUserId = data["recipientUid"]
            ?: FirebaseAuth.getInstance().currentUser?.uid.orEmpty()
        val senderName = data["senderName"]
            ?: message.notification?.title
            ?: "Mensaje de Crystal"
        val preview = data["preview"]
            ?: message.notification?.body
            ?: "Nuevo mensaje"
        val unreadCount = data["unreadCount"]?.toIntOrNull() ?: 1

        CrystalMessageNotifier.notifyMessage(
            context = applicationContext,
            chatId = chatId,
            currentUserId = currentUserId,
            senderName = senderName,
            preview = preview,
            unreadCount = unreadCount
        )
    }
}
