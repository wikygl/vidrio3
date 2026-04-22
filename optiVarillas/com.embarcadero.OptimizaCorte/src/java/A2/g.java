package a2;

import android.os.Build;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/2.dex */
public final class g {
    public static boolean a() {
        if (Build.VERSION.SDK_INT >= 26) {
            return true;
        }
        return false;
    }

    public static boolean b() {
        if (Build.VERSION.SDK_INT >= 30) {
            return true;
        }
        return false;
    }
}
