package m1;

import android.os.Build;
import android.util.Log;

/* renamed from: m1.a  reason: case insensitive filesystem */
/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/2.dex */
public final class C0736a {
    public static void a(Object obj, String str, String str2) {
        String c4 = c(str);
        if (Log.isLoggable(c4, 3)) {
            Log.d(c4, String.format(str2, obj));
        }
    }

    public static void b(String str, String str2, Exception exc) {
        String c4 = c(str);
        if (Log.isLoggable(c4, 6)) {
            Log.e(c4, str2, exc);
        }
    }

    public static String c(String str) {
        if (Build.VERSION.SDK_INT < 26) {
            String concat = "TRuntime.".concat(str);
            if (concat.length() > 23) {
                return concat.substring(0, 23);
            }
            return concat;
        }
        return "TRuntime.".concat(str);
    }
}
