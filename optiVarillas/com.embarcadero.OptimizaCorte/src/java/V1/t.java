package V1;

import U1.a;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/1.dex */
public final class t implements Runnable {

    /* renamed from: j  reason: collision with root package name */
    public final /* synthetic */ D1.E f2603j;

    public t(D1.E e4) {
        this.f2603j = e4;
    }

    @Override // java.lang.Runnable
    public final void run() {
        a.e eVar = ((u) this.f2603j.f633j).f2605k;
        eVar.c(eVar.getClass().getName().concat(" disconnecting because it was signed out."));
    }
}
