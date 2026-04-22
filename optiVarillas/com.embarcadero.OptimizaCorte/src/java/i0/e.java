package I0;

import C0.i;
import C0.j;
import L0.p;
import android.os.Build;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/5.dex */
public final class e extends c<H0.b> {

    /* renamed from: e  reason: collision with root package name */
    public static final String f1146e = i.e("NetworkMeteredCtrlr");

    @Override // I0.c
    public final boolean b(p pVar) {
        if (pVar.f1458j.f304a == j.f328n) {
            return true;
        }
        return false;
    }

    @Override // I0.c
    public final boolean c(H0.b bVar) {
        H0.b bVar2 = bVar;
        boolean z4 = true;
        if (Build.VERSION.SDK_INT < 26) {
            i.c().a(f1146e, "Metered network constraint is not supported before API 26, only checking for connected state.", new Throwable[0]);
            return !bVar2.f1012a;
        }
        if (bVar2.f1012a && bVar2.f1014c) {
            z4 = false;
        }
        return z4;
    }
}
