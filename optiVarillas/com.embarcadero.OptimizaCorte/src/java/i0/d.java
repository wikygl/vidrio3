package I0;

import C0.j;
import L0.p;
import android.os.Build;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/5.dex */
public final class d extends c<H0.b> {
    @Override // I0.c
    public final boolean b(p pVar) {
        if (pVar.f1458j.f304a == j.f325k) {
            return true;
        }
        return false;
    }

    @Override // I0.c
    public final boolean c(H0.b bVar) {
        H0.b bVar2 = bVar;
        if (Build.VERSION.SDK_INT >= 26) {
            if (!bVar2.f1012a || !bVar2.f1013b) {
                return true;
            }
            return false;
        }
        return true ^ bVar2.f1012a;
    }
}
