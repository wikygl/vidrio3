package D1;

import android.annotation.TargetApi;
import android.os.Process;
import android.webkit.CookieManager;

@TargetApi(21)
/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/3.dex */
public class u0 extends C0178b {
    public final CookieManager i() {
        boolean z4;
        t0 t0Var = z1.p.f6575A.f6578c;
        int myUid = Process.myUid();
        if (myUid != 0 && myUid != 1000) {
            z4 = false;
        } else {
            z4 = true;
        }
        if (z4) {
            return null;
        }
        try {
            return CookieManager.getInstance();
        } catch (Throwable th) {
            E1.m.e("Failed to obtain CookieManager.", th);
            z1.p.f6575A.f6581g.g("ApiLevelUtil.getCookieManager", th);
            return null;
        }
    }
}
