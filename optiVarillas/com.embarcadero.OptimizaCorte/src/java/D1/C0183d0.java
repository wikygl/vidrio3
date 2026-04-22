package D1;

import android.util.Log;
import com.google.android.gms.internal.ads.dL;
import com.google.android.gms.internal.ads.tc;
import java.util.Iterator;

/* renamed from: D1.d0  reason: case insensitive filesystem */
/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/3.dex */
public final class C0183d0 extends E1.m {
    public static void k(String str) {
        if (m()) {
            if (str != null && str.length() > 4000) {
                Iterator it = new dL(E1.m.f875a, str).iterator();
                boolean z4 = true;
                while (it.hasNext()) {
                    String str2 = (String) it.next();
                    if (z4) {
                        Log.v("Ads", str2);
                    } else {
                        Log.v("Ads-cont", str2);
                    }
                    z4 = false;
                }
                return;
            }
            Log.v("Ads", str);
        }
    }

    public static void l(String str, Throwable th) {
        if (m()) {
            Log.v("Ads", str, th);
        }
    }

    public static boolean m() {
        if (E1.m.j(2) && ((Boolean) tc.a.e()).booleanValue()) {
            return true;
        }
        return false;
    }
}
