package C3;

import C3.InterfaceC0172w;

/* renamed from: C3.x  reason: case insensitive filesystem */
/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/0.dex */
public final class C0173x {
    public static final void a(n3.f fVar, Throwable th) {
        try {
            InterfaceC0172w interfaceC0172w = (InterfaceC0172w) fVar.E(InterfaceC0172w.a.f503j);
            if (interfaceC0172w != null) {
                interfaceC0172w.l(fVar, th);
            } else {
                A3.d.e(fVar, th);
            }
        } catch (Throwable th2) {
            if (th != th2) {
                RuntimeException runtimeException = new RuntimeException("Exception while trying to handle coroutine exception", th2);
                A3.d.a(runtimeException, th);
                th = runtimeException;
            }
            A3.d.e(fVar, th);
        }
    }
}
