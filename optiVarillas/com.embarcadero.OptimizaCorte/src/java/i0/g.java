package I0;

import C0.j;
import L0.p;
import android.os.Build;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/5.dex */
public final class g extends c<H0.b> {
    @Override // I0.c
    public final boolean b(p pVar) {
        j jVar = pVar.f1458j.f304a;
        if (jVar != j.f326l && (Build.VERSION.SDK_INT < 30 || jVar != j.f329o)) {
            return false;
        }
        return true;
    }

    @Override // I0.c
    public final boolean c(H0.b bVar) {
        H0.b bVar2 = bVar;
        if (bVar2.f1012a && !bVar2.f1014c) {
            return false;
        }
        return true;
    }
}
