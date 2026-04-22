package V1;

import U1.a;
import W1.InterfaceC0320h;
import android.util.Log;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/1.dex */
public final class w implements Runnable {

    /* renamed from: j  reason: collision with root package name */
    public final /* synthetic */ T1.b f2619j;

    /* renamed from: k  reason: collision with root package name */
    public final /* synthetic */ x f2620k;

    public w(x xVar, T1.b bVar) {
        this.f2620k = xVar;
        this.f2619j = bVar;
    }

    @Override // java.lang.Runnable
    public final void run() {
        boolean z4;
        InterfaceC0320h interfaceC0320h;
        x xVar = this.f2620k;
        u uVar = (u) xVar.f.f2584s.get(xVar.f2622b);
        if (uVar == null) {
            return;
        }
        T1.b bVar = this.f2619j;
        if (bVar.f2342k == 0) {
            z4 = true;
        } else {
            z4 = false;
        }
        if (z4) {
            xVar.f2625e = true;
            a.e eVar = xVar.f2621a;
            if (eVar.o()) {
                if (xVar.f2625e && (interfaceC0320h = xVar.f2623c) != null) {
                    eVar.d(interfaceC0320h, xVar.f2624d);
                    return;
                }
                return;
            }
            try {
                eVar.d(null, eVar.b());
                return;
            } catch (SecurityException e4) {
                Log.e("GoogleApiManager", "Failed to get service from broker. ", e4);
                eVar.c("Failed to get service from broker.");
                uVar.m(new T1.b(10), null);
                return;
            }
        }
        uVar.m(bVar, null);
    }
}
