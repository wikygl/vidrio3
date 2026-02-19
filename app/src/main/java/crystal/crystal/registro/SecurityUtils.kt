package crystal.crystal.registro

import android.util.Base64
import java.security.SecureRandom
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec
import kotlin.math.min

object SecurityUtils {

    fun generateSalt(len: Int = 16): ByteArray {
        val r = SecureRandom()
        val salt = ByteArray(len)
        r.nextBytes(salt)
        return salt
    }

    fun pbkdf2(pin: String, salt: ByteArray, iterations: Int, keyLenBytes: Int = 32): ByteArray {
        val spec = PBEKeySpec(pin.toCharArray(), salt, iterations, keyLenBytes * 8)
        val skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256")
        return skf.generateSecret(spec).encoded
    }

    fun toBase64(bytes: ByteArray): String =
        Base64.encodeToString(bytes, Base64.NO_WRAP)

    fun fromBase64(s: String): ByteArray =
        Base64.decode(s, Base64.NO_WRAP)

    // Comparación en tiempo constante para evitar ataques de timing
    fun constantTimeEquals(a: ByteArray, b: ByteArray): Boolean {
        val max = min(a.size, b.size)
        var result = a.size xor b.size
        for (i in 0 until max) {
            result = result or (a[i].toInt() xor b[i].toInt())
        }
        return result == 0
    }
}
