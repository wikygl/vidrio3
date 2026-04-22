package i2;

import android.util.Log;
import java.io.IOException;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/2.dex */
public final class b0 extends Exception {
    public b0(String str, int i4) {
        super(str);
    }

    public final X2.c a() {
        if (getCause() == null) {
            Log.w("UserMessagingPlatform", getMessage());
        } else {
            Log.w("UserMessagingPlatform", getMessage(), getCause());
        }
        return new X2.c(getMessage());
    }

    public b0(int i4, String str, IOException iOException) {
        super(str, iOException);
    }
}
